package org.example.contabilidad.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.List;
import org.example.contabilidad.api.controller.JournalController;
import org.example.contabilidad.api.dto.JournalEntryListDTO;
import org.example.contabilidad.api.dto.JournalQueryParams;
import org.example.contabilidad.service.JournalQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class JournalControllerUnitTest {

    @Mock
    private JournalQueryService service;

    @InjectMocks
    private JournalController controller;

    @Test
    void listJournal_usaPageableCuandoParamsNoTienePageNiSize() {
        // Arrange
        JournalQueryParams params = new JournalQueryParams();
        params.setDateFrom(LocalDate.of(2026, 1, 1));
        params.setDateTo(LocalDate.of(2026, 1, 31));
        params.setPosted(true);
        params.setAccountId(100L);
        params.setPartnerId(1L);

        Pageable incoming = PageRequest.of(
                0, 20, Sort.by(Sort.Order.desc("date"), Sort.Order.desc("id"))
        );

        Page<JournalEntryListDTO> fakePage = new PageImpl<>(List.of(), incoming, 0);
        when(service.list(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(fakePage);

        // Act
        ResponseEntity<Page<JournalEntryListDTO>> resp = controller.listJournal(params, incoming);

        // Assert
        assertEquals(200, resp.getStatusCodeValue());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).list(
                eq(params.getDateFrom()),
                eq(params.getDateTo()),
                eq(params.getPosted()),
                eq(params.getAccountId()),
                eq(params.getPartnerId()),
                pageableCaptor.capture()
        );

        Pageable effective = pageableCaptor.getValue();
        assertEquals(incoming.getPageNumber(), effective.getPageNumber());
        assertEquals(incoming.getPageSize(), effective.getPageSize());
        assertEquals(incoming.getSort(), effective.getSort());
    }

    @Test
    void listJournal_sobrescribePageYSizeDesdeParams_conservandoSort() {
        // Arrange
        JournalQueryParams params = new JournalQueryParams();
        params.setPage(3);
        params.setSize(50);

        Pageable incoming = PageRequest.of(
                0, 20, Sort.by(Sort.Order.desc("date"), Sort.Order.desc("id"))
        );

        Page<JournalEntryListDTO> fakePage = new PageImpl<>(List.of(), incoming, 0);
        when(service.list(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(fakePage);

        // Act
        ResponseEntity<Page<JournalEntryListDTO>> resp = controller.listJournal(params, incoming);

        // Assert
        assertEquals(200, resp.getStatusCodeValue());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).list(
                any(), any(), any(), any(), any(), pageableCaptor.capture()
        );

        Pageable effective = pageableCaptor.getValue();
        assertEquals(3, effective.getPageNumber());
        assertEquals(50, effective.getPageSize());
        assertEquals(incoming.getSort(), effective.getSort(), "Debe conservar el sort entrante");
    }

    @Test
    void listJournal_soloPageDesdeParams_conservaSizeYSort() {
        // Arrange
        JournalQueryParams params = new JournalQueryParams();
        params.setPage(2); // solo page

        Pageable incoming = PageRequest.of(0, 20, Sort.by("date").descending());

        Page<JournalEntryListDTO> fakePage = new PageImpl<>(List.of(), incoming, 0);
        when(service.list(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(fakePage);

        // Act
        controller.listJournal(params, incoming);

        // Assert
        ArgumentCaptor<Pageable> cap = ArgumentCaptor.forClass(Pageable.class);
        verify(service).list(any(), any(), any(), any(), any(), cap.capture());
        Pageable effective = cap.getValue();
        assertEquals(2, effective.getPageNumber());
        assertEquals(20, effective.getPageSize());
        assertEquals(incoming.getSort(), effective.getSort());
    }

    @Test
    void listJournal_soloSizeDesdeParams_conservaPageYSort() {
        // Arrange
        JournalQueryParams params = new JournalQueryParams();
        params.setSize(100); // solo size

        Pageable incoming = PageRequest.of(5, 20, Sort.by("id").descending());

        Page<JournalEntryListDTO> fakePage = new PageImpl<>(List.of(), incoming, 0);
        when(service.list(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(fakePage);

        // Act
        controller.listJournal(params, incoming);

        // Assert
        ArgumentCaptor<Pageable> cap = ArgumentCaptor.forClass(Pageable.class);
        verify(service).list(any(), any(), any(), any(), any(), cap.capture());
        Pageable effective = cap.getValue();
        assertEquals(5, effective.getPageNumber());
        assertEquals(100, effective.getPageSize());
        assertEquals(incoming.getSort(), effective.getSort());
    }
}
