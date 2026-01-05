package org.example.contabilidad.domain.invoice;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.example.contabilidad.domain.partner.Partner;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice {
  public enum Status { DRAFT, POSTED, PAID }

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private LocalDate date = LocalDate.now();

  @ManyToOne(optional = false)
  private Partner partner;

  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InvoiceLine> lines = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  private Status status = Status.DRAFT;

  @Column(precision = 19, scale = 2)
  private BigDecimal netAmount = BigDecimal.ZERO;

  @Column(precision = 19, scale = 2)
  private BigDecimal taxAmount = BigDecimal.ZERO;

  @Column(precision = 19, scale = 2)
  private BigDecimal totalAmount = BigDecimal.ZERO;

}
