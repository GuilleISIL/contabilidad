package org.example.contabilidad.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

@Schema(description = "Parámetros de consulta para listar asientos contables")
public class JournalQueryParams {

  @Schema(description = "Fecha desde (inclusive)", example = "2026-01-01")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateFrom;

  @Schema(description = "Fecha hasta (inclusive)", example = "2026-01-31")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateTo;

  @Schema(description = "Solo asientos posteados", example = "true")
  private Boolean posted;

  @Schema(description = "Filtrar por cuenta contable asignada en alguna línea", example = "100")
  private Long accountId;

  @Schema(description = "Filtrar por partner en alguna línea", example = "1")
  private Long partnerId;

  // Opcional: límites sugeridos si quieres forzar en controller (Pageable controla, pero puedes validar)
  @Min(0) @Schema(description = "Número de página (0-index)", example = "0")
  private Integer page;

  @Min(1) @Max(200) @Schema(description = "Tamaño de página", example = "20")
  private Integer size;

  // getters/setters…

  public LocalDate getDateFrom() { return dateFrom; }
  public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }

  public LocalDate getDateTo() { return dateTo; }
  public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }

  public Boolean getPosted() { return posted; }
  public void setPosted(Boolean posted) { this.posted = posted; }

  public Long getAccountId() { return accountId; }
  public void setAccountId(Long accountId) { this.accountId = accountId; }

  public Long getPartnerId() { return partnerId; }
  public void setPartnerId(Long partnerId) { this.partnerId = partnerId; }

  public Integer getPage() { return page; }
  public void setPage(Integer page) { this.page = page; }

  public Integer getSize() { return size; }
  public void setSize(Integer size) { this.size = size; }
}
