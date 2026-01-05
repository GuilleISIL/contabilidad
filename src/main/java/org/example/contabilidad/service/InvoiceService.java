package org.example.contabilidad.service;

import jakarta.transaction.Transactional;
import org.example.contabilidad.domain.account.AccountRepository;
import org.example.contabilidad.domain.invoice.Invoice;
import org.example.contabilidad.domain.invoice.InvoiceLine;
import org.example.contabilidad.domain.invoice.InvoiceRepository;
import org.example.contabilidad.domain.journal.JournalEntry;
import org.example.contabilidad.domain.journal.JournalLine;
import org.example.contabilidad.domain.tax.Tax;
import org.example.contabilidad.domain.journal.JournalRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class InvoiceService {

  private final InvoiceRepository invoiceRepo;
  private final AccountRepository accountRepo;
  private final JournalRepository journalRepo;

  // IDs de cuentas principales (configurables en DB o properties)
  private final Long arAccountId;       // CxC
  private final Long revenueAccountId;  // Ingresos

  public InvoiceService(InvoiceRepository invoiceRepo,
                        AccountRepository accountRepo,
                        JournalRepository journalRepo) {
    this.invoiceRepo = invoiceRepo;
    this.accountRepo = accountRepo;
    this.journalRepo = journalRepo;

    // En producción: traer desde configuración/tabla de parámetros
    this.arAccountId = 100L;
    this.revenueAccountId = 700L;
  }

  @Transactional
  public Invoice post(Long invoiceId) {
    Invoice inv = invoiceRepo.findById(invoiceId)
      .orElseThrow(() -> new IllegalArgumentException("Factura no existe"));

    // 1) Calcular totales
    calcTotals(inv);

    // 2) Crear asiento
    JournalEntry je = new JournalEntry();
    je.setDate(inv.getDate());
    je.setDescription("Posteo factura " + inv.getId());
    je.setReferenceType("INVOICE");
    je.setReferenceId(inv.getId());

    // Debe: AR por el total
    JournalLine l1 = new JournalLine();
    l1.setEntry(je);
    l1.setAccountId(arAccountId);
    l1.setDebit(inv.getTotalAmount());
    l1.setPartnerId(inv.getPartner().getId());

    // Haber: Revenue por el neto
    JournalLine l2 = new JournalLine();
    l2.setEntry(je);
    l2.setAccountId(revenueAccountId);
    l2.setCredit(inv.getNetAmount());

    // Haber: Impuesto por pagar (por cada impuesto diferente podrías sumar)
    BigDecimal tax = inv.getTaxAmount();
    if (tax.compareTo(BigDecimal.ZERO) > 0) {
      Long taxAccountId = resolveTaxAccount(inv);
      JournalLine l3 = new JournalLine();
      l3.setEntry(je);
      l3.setAccountId(taxAccountId);
      l3.setCredit(tax);
      je.getLines().add(l3);
    }

    je.getLines().addAll(List.of(l1, l2));
    journalRepo.save(je);

    inv.setStatus(Invoice.Status.POSTED);
    return invoiceRepo.save(inv);
  }

  private void calcTotals(Invoice inv) {
    BigDecimal net = BigDecimal.ZERO, tax = BigDecimal.ZERO, total = BigDecimal.ZERO;
    for (InvoiceLine line : inv.getLines()) {
      BigDecimal lnNet = line.getQuantity().multiply(line.getUnitPrice());
      BigDecimal lnTax = line.getTax() != null ? lnNet.multiply(line.getTax().getRate()) : BigDecimal.ZERO;
      BigDecimal lnTotal = lnNet.add(lnTax);

      line.setLineNet(lnNet);
      line.setLineTax(lnTax);
      line.setLineTotal(lnTotal);

      net = net.add(lnNet); tax = tax.add(lnTax); total = total.add(lnTotal);
    }
    inv.setNetAmount(net); inv.setTaxAmount(tax); inv.setTotalAmount(total);
  }

  private Long resolveTaxAccount(Invoice inv) {
    // en MVP, tomar la cuenta del primer impuesto no nulo
    return inv.getLines().stream()
      .map(InvoiceLine::getTax).filter(Objects::nonNull)
      .findFirst().map(Tax::getTaxAccountId)
      .orElseThrow(() -> new IllegalStateException("Factura con impuesto pero sin cuenta de impuesto"));
  }
}
