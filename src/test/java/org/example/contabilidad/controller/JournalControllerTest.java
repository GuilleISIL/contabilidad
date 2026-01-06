package org.example.contabilidad.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class JournalControllerTest {

    @Mock
    private JournalQueryService service; // TODO: tu interfaz/servicio real

    @InjectMocks
    private JournalController controller; // TODO: tu controlador real

    @Test
    void listJournal_usaPageableCuandoParamsNoTienePageNiSize() {
        // Arrange
        JournalQueryParams params = new JournalQueryParams(); // asegúrate de tener setters
        params.setDateFrom(LocalDate.of(2024, 1, 1));
        params.setDateTo(LocalDate.of(2024, 12, 31));
        params.setPosted(true);
        params.setAccountId(123L);
        params.setPartnerId(456L);
        // No seteamos page/size => quedan null

        Pageable incoming = PageRequest.of(
                0, 20, Sort.by(
                        Sort.Order.desc("date"),
                        Sort.Order.desc("id")
                ));

        Page<JournalEntryListDTO> fakePage = new PageImpl<>(
                java.util.List.of(), incoming, 0);
        when(service.list(
                any(), any(), any(), any(), any(), any(Pageable.class))
        ).thenReturn(fakePage);

        // Act
        ResponseEntity<Page<JournalEntryListDTO>> resp = controller.listJournal(params, incoming);

        // Assert
        assertEquals(200, resp.getStatusCode().value());
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).list(
                eq(params.getDateFrom()),
                eq(params.getDateTo()),
                eq(params.getPosted()),
                eq(params.getAccountId()),
                eq(params.getPartnerId()),
                pageableCaptor.capture()
        );

        Pageable effectiveUsed = pageableCaptor.getValue();
        // Debe ser igual al incoming ya que params no sobreescribe page/size
        assertEquals(incoming.getPageNumber(), effectiveUsed.getPageNumber());
        assertEquals(incoming.getPageSize(), effectiveUsed.getPageSize());
        assertEquals(incoming.getSort(), effectiveUsed.getSort());

        // Verificamos órdenes de sort
        Sort sort = effectiveUsed.getSort();
        Sort.Order dateOrder = sort.getOrderFor("date");
        Sort.Order idOrder = sort.getOrderFor("id");
        assertNotNull(dateOrder);
        assertNotNull(idOrder);
        assertEquals(Sort.Direction.DESC, dateOrder.getDirection());
        assertEquals(Sort.Direction.DESC, idOrder.getDirection());
    }

    @Test
    void listJournal_sobrescribePageYSizeDesdeParamsConservandoSort() {
        // Arrange
        JournalQueryParams params = new JournalQueryParams();
        params.setPage(3);  // el cliente envía page/size vía params
        params.setSize(50);

        // Mantendremos un sort entrante que debe preservarse
        Pageable incoming = PageRequest.of(
                0, 20, Sort.by(
                        Sort.Order.desc("date"),
                        Sort.Order.desc("id")
                ));

        Page<JournalEntryListDTO> fakePage = new PageImpl<>(
                java.util.List.of(), incoming, 0);
        when(service.list(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(fakePage);

        // Act
        ResponseEntity<Page<JournalEntryListDTO>> resp = controller.listJournal(params, incoming);

        // Assert
        assertEquals(200, resp.getStatusCode().value());
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).list(
                eq(params.getDateFrom()),
                eq(params.getDateTo()),
                eq(params.getPosted()),
                eq(params.getAccountId()),
                eq(params.getPartnerId()),
                pageableCaptor.capture()
        );

        Pageable effectiveUsed = pageableCaptor.getValue();
        assertEquals(3, effectiveUsed.getPageNumber());
        assertEquals(50, effectiveUsed.getPageSize());
        // Debe conservar el sort del pageable original
        assertEquals(incoming.getSort(), effectiveUsed.getSort());
    }
}
