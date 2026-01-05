package org.example.contabilidad.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.contabilidad.api.dto.JournalEntryListDTO;
import org.example.contabilidad.api.dto.JournalQueryParams;
import org.example.contabilidad.service.JournalQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Journal", description = "Consulta de asientos contables")
@RestController
@RequestMapping("/api/v1/journal")
public class JournalController {

    private final JournalQueryService service;

    public JournalController(JournalQueryService service) {
        this.service = service;
    }

    @Operation(
            summary = "Listar asientos contables",
            description = """
      Devuelve una página de asientos con filtros opcionales:
      - dateFrom/dateTo (rango de fechas)
      - posted
      - accountId / partnerId
      Usa paginación nativa (page, size) y orden con sort (ej: sort=date,desc&sort=id,asc).
      """
    )
    @GetMapping
    public ResponseEntity<Page<JournalEntryListDTO>> listJournal(
            @Valid JournalQueryParams params,

            // Paginación y orden profesional (Spring Data)
            @Parameter(description = "Paginación y orden. Ej: ?page=0&size=20&sort=date,desc&sort=id,asc")
            @PageableDefault(page = 0, size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "date", direction = org.springframework.data.domain.Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC)
            })
            Pageable pageable
    ) {
        // Si el cliente envía page/size en params, puedes combinarlos con Pageable (opcional):
        Pageable effective = pageable;
        if (params.getPage() != null || params.getSize() != null) {
            int p = params.getPage() != null ? params.getPage() : pageable.getPageNumber();
            int s = params.getSize() != null ? params.getSize() : pageable.getPageSize();
            effective = org.springframework.data.domain.PageRequest.of(p, s, pageable.getSort());
        }

        Page<JournalEntryListDTO> result = service.list(
                params.getDateFrom(),
                params.getDateTo(),
                params.getPosted(),
                params.getAccountId(),
                params.getPartnerId(),
                effective
        );
        return ResponseEntity.ok(result);
    }
}
