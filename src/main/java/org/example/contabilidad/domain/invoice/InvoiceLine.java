package org.example.contabilidad.domain.invoice;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.contabilidad.domain.tax.Tax;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "invoice_lines")
public class InvoiceLine {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "invoice_id")
  private Invoice invoice;

  @NotBlank
  private String description;
  @DecimalMin("0.00") private BigDecimal quantity;
  @DecimalMin("0.00") private BigDecimal unitPrice;

  @ManyToOne private Tax tax; // puede ser null si exento

  @Column(precision = 19, scale = 2)
  private BigDecimal lineNet;

  @Column(precision = 19, scale = 2)
  private BigDecimal lineTax;

  @Column(precision = 19, scale = 2)
  private BigDecimal lineTotal;

}