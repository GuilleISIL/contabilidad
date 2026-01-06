package org.example.contabilidad.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;
import org.example.contabilidad.api.controller.JournalController;
import org.example.contabilidad.api.dto.JournalEntryListDTO;
import org.example.contabilidad.service.JournalQueryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = JournalController.class)
class JournalControllerWebMvcTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private JournalQueryService service;

    @Test
    void GET_listJournal_aplicaDefaultsDePaginacionYOrden() throws Exception {
        // Arrange
        Page<JournalEntryListDTO> empty = new PageImpl<>(List.of());
        when(service.list(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(empty);

        // Act
        mvc.perform(
                get("/api/v1/journal") // ruta real del controlador
                        .param("dateFrom", "2026-01-01")
                        .param("dateTo", "2026-01-31")
                        .param("posted", "true")
                        .param("accountId", "100")
                        .param("partnerId", "1")
        ).andExpect(status().isOk());

        // Assert
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).list(
                eq(LocalDate.parse("2026-01-01")),
                eq(LocalDate.parse("2026-01-31")),
                eq(true),
                eq(100L),
                eq(1L),
                captor.capture()
        );

        Pageable pageable = captor.getValue();
        assertEquals(0, pageable.getPageNumber(), "page por defecto debe ser 0");
        assertEquals(20, pageable.getPageSize(), "size por defecto debe ser 20");

        Sort sort = pageable.getSort();
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("date").getDirection());
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("id").getDirection());
    }

    @Test
    void GET_listJournal_sobrescribePageYSizeDesdeParams_y_conservaSort() throws Exception {
        // Arrange
        Page<JournalEntryListDTO> empty = new PageImpl<>(List.of());
        when(service.list(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(empty);

        // Act
        mvc.perform(
                get("/api/v1/journal")
                        // Estos van a JournalQueryParams y el método los prioriza
                        .param("page", "3")
                        .param("size", "50")
                        // sort explícito (entra por Pageable) para comprobar que se preserva
                        .param("sort", "date,desc")
                        .param("sort", "id,desc")
        ).andExpect(status().isOk());

        // Assert
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).list(any(), any(), any(), any(), any(), captor.capture());

        Pageable effective = captor.getValue();
        assertEquals(3, effective.getPageNumber());
        assertEquals(50, effective.getPageSize());

        Sort sort = effective.getSort();
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("date").getDirection());
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("id").getDirection());
    }

    @Test
    void GET_listJournal_sizeInvalido_enParams_devuelve400() throws Exception {
        // Arrange
        when(service.list(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        // Act + Assert
        // size=0 viola @Min(1) en JournalQueryParams -> 400 Bad Request
        mvc.perform(get("/api/v1/journal")
                .param("size", "0")) // inválido para JournalQueryParams
            .andExpect(status().isBadRequest());
    }
}
