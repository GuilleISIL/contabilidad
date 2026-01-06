package org.example.contabilidad.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.List;
import org.example.contabilidad.api.dto.JournalEntryListDTO;
import org.example.contabilidad.domain.journal.JournalEntry;
import org.example.contabilidad.domain.journal.JournalEntryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

class JournalQueryServiceTest {

    @Test
    void list_invocaRepoConSpecificationYRespetaPageable() {
        // Arrange
        JournalEntryRepository repo = mock(JournalEntryRepository.class);
        JournalQueryService service = new JournalQueryService(repo);

        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        Boolean posted = true;
        Long accountId = 100L;
        Long partnerId = 1L;

        Pageable pageable = PageRequest.of(2, 50, Sort.by("date").descending());

        Page<JournalEntry> entities = new PageImpl<>(List.of(), pageable, 0);
        when(repo.findAll(any(Specification.class), eq(pageable))).thenReturn(entities);

        // Act
        Page<JournalEntryListDTO> dtoPage = service.list(from, to, posted, accountId, partnerId, pageable);

        // Assert
        assertNotNull(dtoPage);
        assertEquals(2, dtoPage.getNumber());
        assertEquals(50, dtoPage.getSize());
        assertEquals(Sort.Direction.DESC, dtoPage.getSort().getOrderFor("date").getDirection());

        ArgumentCaptor<Specification<JournalEntry>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(repo).findAll(specCaptor.capture(), eq(pageable));
        assertNotNull(specCaptor.getValue(), "Se debe construir un Specification con los filtros");
    }
}
