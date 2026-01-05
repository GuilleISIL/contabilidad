package org.example.contabilidad.domain.payment;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.example.contabilidad.domain.partner.Partner;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private LocalDate date = LocalDate.now();
  @ManyToOne(optional = false) private Partner partner;

  @DecimalMin("0.01") @Column(precision = 19, scale = 2)
  private BigDecimal amount;

  private String method;  // TRANSFER, CASH, CARD...

  private Long bankAccountId; // cuenta contable de banco/caja

  private Long invoiceId; // el documento al que aplica (simple)

}
