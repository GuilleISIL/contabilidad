package org.example.contabilidad.service;

import jakarta.transaction.Transactional;
import org.example.contabilidad.domain.invoice.Invoice;
import org.example.contabilidad.domain.invoice.InvoiceRepository;
import org.example.contabilidad.domain.journal.JournalEntry;
import org.example.contabilidad.domain.journal.JournalLine;
import org.example.contabilidad.domain.journal.JournalRepository;
import org.example.contabilidad.domain.payment.Payment;
import org.example.contabilidad.domain.payment.PaymentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {

  private final PaymentRepository paymentRepo;
  private final InvoiceRepository invoiceRepo;
  private final JournalRepository journalRepo;

  private final Long arAccountId = 100L; // CxC

  public PaymentService(PaymentRepository paymentRepo,
                        InvoiceRepository invoiceRepo,
                        JournalRepository journalRepo) {
    this.paymentRepo = paymentRepo;
    this.invoiceRepo = invoiceRepo;
    this.journalRepo = journalRepo;
  }

  @Transactional
  public Payment register(Payment p) {
    paymentRepo.save(p);

    JournalEntry je = new JournalEntry();
    je.setDate(p.getDate());
    je.setDescription("Cobro " + p.getId());
    je.setReferenceType("PAYMENT");
    je.setReferenceId(p.getId());

    // Debe: Banco/Caja
    JournalLine l1 = new JournalLine();
    l1.setEntry(je);
    l1.setAccountId(p.getBankAccountId());
    l1.setDebit(p.getAmount());
    l1.setPartnerId(p.getPartner().getId());

    // Haber: CxC
    JournalLine l2 = new JournalLine();
    l2.setEntry(je);
    l2.setAccountId(arAccountId);
    l2.setCredit(p.getAmount());
    l2.setPartnerId(p.getPartner().getId());

    je.getLines().addAll(List.of(l1, l2));
    journalRepo.save(je);

    // marcar factura como paga si corresponde (simple):
    if (p.getInvoiceId() != null) {
      invoiceRepo.findById(p.getInvoiceId()).ifPresent(inv -> inv.setStatus(Invoice.Status.PAID));
    }
    return p;
  }
}
