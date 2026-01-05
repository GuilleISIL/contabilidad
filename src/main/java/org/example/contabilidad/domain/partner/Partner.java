package org.example.contabilidad.domain.partner;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "partners")
public class Partner {
  public enum PartnerType { CUSTOMER, SUPPLIER }

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String name;

  private String taxId; // RUC u otro

  @Enumerated(EnumType.STRING)
  private PartnerType type;

}
