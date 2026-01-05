package org.example.contabilidad.domain.tax;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "taxes")
public class Tax {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String name; // IGV, Exento, etc.

  @DecimalMin("0.00") @DecimalMax("1.00")
  @Column(nullable = false, precision = 10, scale = 4)
  private java.math.BigDecimal rate; // 0.18 = 18%

  @NotNull
  private Long taxAccountId; // cuenta contable para el pasivo de impuestos

}
