package org.example.contabilidad.domain.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Account {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false, length = 20)
  private String code;   // p.ej. 10, 12, 70...

  @NotBlank
  @Column(nullable = false, length = 120)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private AccountType type;

  private boolean active = true;

}
