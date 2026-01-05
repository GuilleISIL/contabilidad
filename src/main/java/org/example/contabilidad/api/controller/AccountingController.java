package org.example.contabilidad.api.controller;

import jakarta.validation.Valid;
import org.example.contabilidad.api.dto.*;
import org.example.contabilidad.domain.account.Account;
import org.example.contabilidad.domain.account.AccountRepository;
import org.example.contabilidad.domain.account.AccountType;
import org.example.contabilidad.domain.invoice.Invoice;
import org.example.contabilidad.domain.invoice.InvoiceLine;
import org.example.contabilidad.domain.invoice.InvoiceRepository;
import org.example.contabilidad.domain.partner.Partner;
import org.example.contabilidad.domain.partner.PartnerRepository;
import org.example.contabilidad.domain.payment.Payment;
import org.example.contabilidad.domain.tax.Tax;
import org.example.contabilidad.domain.tax.TaxRepository;
import org.example.contabilidad.service.InvoiceService;
import org.example.contabilidad.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AccountingController {

  private final AccountRepository accountRepo;
  private final PartnerRepository partnerRepo;
  private final TaxRepository taxRepo;
  private final InvoiceRepository invoiceRepo;
  private final PaymentService paymentService;
  private final InvoiceService invoiceService;

  public AccountingController(AccountRepository accountRepo,
                              PartnerRepository partnerRepo,
                              TaxRepository taxRepo,
                              InvoiceRepository invoiceRepo,
                              PaymentService paymentService,
                              InvoiceService invoiceService) {
    this.accountRepo = accountRepo;
    this.partnerRepo = partnerRepo;
    this.taxRepo = taxRepo;
    this.invoiceRepo = invoiceRepo;
    this.paymentService = paymentService;
    this.invoiceService = invoiceService;
  }

  // --- Plan de cuentas ---
  @PostMapping("/accounts")
  public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountDTO dto) {
    Account acc = new Account();
    acc.setCode(dto.code()); acc.setName(dto.name());
    acc.setType(AccountType.valueOf(dto.type()));
    return ResponseEntity.ok(accountRepo.save(acc));
  }

  @GetMapping("/accounts")
  public List<Account> listAccounts() { return accountRepo.findAll(); }

  // --- Terceros ---
  @PostMapping("/partners")
  public ResponseEntity<Partner> createPartner(@Valid @RequestBody PartnerDTO dto) {
    Partner p = new Partner();
    p.setName(dto.name()); p.setTaxId(dto.taxId());
    p.setType(Partner.PartnerType.valueOf(dto.type()));
    return ResponseEntity.ok(partnerRepo.save(p));
  }

  @GetMapping("/partners")
  public List<Partner> listPartners() { return partnerRepo.findAll(); }

  // --- Impuestos ---
  @PostMapping("/taxes")
  public ResponseEntity<Tax> createTax(@Valid @RequestBody TaxDTO dto) {
    Tax t = new Tax();
    t.setName(dto.name()); t.setRate(dto.rate()); t.setTaxAccountId(dto.taxAccountId());
    return ResponseEntity.ok(taxRepo.save(t));
  }

  @GetMapping("/taxes")
  public List<Tax> listTaxes() { return taxRepo.findAll(); }

  // --- Facturas ---
  @PostMapping("/invoices")
  public ResponseEntity<InvoiceSummaryDTO> createInvoice(@Valid @RequestBody CreateInvoiceDTO dto) {
    Partner partner = partnerRepo.findById(dto.partnerId()).orElseThrow();
    Invoice inv = new Invoice();
    inv.setDate(dto.date()); inv.setPartner(partner);

    for (InvoiceLineDTO l : dto.lines()) {
      InvoiceLine line = new InvoiceLine();
      line.setInvoice(inv);
      line.setDescription(l.description());
      line.setQuantity(l.quantity());
      line.setUnitPrice(l.unitPrice());
      if (l.taxId() != null) {
        Tax tax = taxRepo.findById(l.taxId()).orElseThrow();
        line.setTax(tax);
      }
      inv.getLines().add(line);
    }

    invoiceRepo.save(inv);
    // no posteamos aún; estado DRAFT
    return ResponseEntity.ok(new InvoiceSummaryDTO(inv.getId(), inv.getStatus().name(),
      inv.getNetAmount(), inv.getTaxAmount(), inv.getTotalAmount()));
  }

  @PostMapping("/invoices/{id}/post")
  public ResponseEntity<InvoiceSummaryDTO> postInvoice(@PathVariable Long id) {
    Invoice inv = invoiceService.post(id);
    return ResponseEntity.ok(new InvoiceSummaryDTO(inv.getId(), inv.getStatus().name(),
      inv.getNetAmount(), inv.getTaxAmount(), inv.getTotalAmount()));
  }

  // --- Pagos ---
  @PostMapping("/payments")
  public ResponseEntity<Payment> registerPayment(@Valid @RequestBody PaymentDTO dto) {
    Partner partner = partnerRepo.findById(dto.partnerId()).orElseThrow();
    Payment p = new Payment();
    p.setDate(dto.date()); p.setPartner(partner);
    p.setAmount(dto.amount()); p.setMethod(dto.method());
    p.setBankAccountId(dto.bankAccountId()); p.setInvoiceId(dto.invoiceId());
    return ResponseEntity.ok(paymentService.register(p));
  }
}
