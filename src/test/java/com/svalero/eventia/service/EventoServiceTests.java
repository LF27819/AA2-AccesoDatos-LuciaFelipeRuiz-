package com.svalero.eventia.service;

import com.svalero.eventia.domain.Evento;
import com.svalero.eventia.exception.EventoNotFoundException;
import com.svalero.eventia.repository.EventoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventoServiceTests {

    @InjectMocks
    private EventoService eventoService;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private ObjectMapper objectMapper;

    private Evento buildEvento(long id, String nombre, String categoria, boolean cancelado) {
        return new Evento(id, nombre, "Descripción de " + nombre,
                LocalDate.of(2025, 6, 15), LocalTime.of(20, 0),
                30f, 5000, 4000, cancelado, true, categoria, null, null, null);
    }

    // findAll
    @Test
    public void testFindAll() {
        List<Evento> mockList = List.of(
                buildEvento(1L, "Festival Rock", "musica", false),
                buildEvento(2L, "Feria de Arte", "arte", false)
        );
        when(eventoRepository.findAll()).thenReturn(mockList);

        List<Evento> result = eventoService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Festival Rock", result.get(0).getNombre());
        verify(eventoRepository, times(1)).findAll();
    }

    // findAll + filtros
    @Test
    public void testFindAllWithFilters() {
        List<Evento> mockList = List.of(buildEvento(1L, "Festival Rock", "musica", false));
        when(eventoRepository.findByFilters("Festival Rock", "musica", false)).thenReturn(mockList);

        List<Evento> result = eventoService.findAll("Festival Rock", "musica", false);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventoRepository, times(1)).findByFilters("Festival Rock", "musica", false);
    }

    @Test
    public void testFindAllWithFiltersNoResults() {
        when(eventoRepository.findByFilters("Inexistente", null, null)).thenReturn(List.of());

        List<Evento> result = eventoService.findAll("Inexistente", null, null);

        assertEquals(0, result.size());
    }


    // findById
    @Test
    public void testFindById() throws EventoNotFoundException {
        Evento mock = buildEvento(1L, "Festival Rock", "musica", false);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(mock));

        Evento result = eventoService.findById(1L);

        assertNotNull(result);
        assertEquals("Festival Rock", result.getNombre());
        verify(eventoRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EventoNotFoundException.class, () -> eventoService.findById(99L));
    }


    // add
    @Test
    public void testAdd() {
        Evento nuevo = buildEvento(0L, "Concierto Jazz", "musica", false);
        Evento saved = buildEvento(3L, "Concierto Jazz", "musica", false);
        when(eventoRepository.save(nuevo)).thenReturn(saved);

        Evento result = eventoService.add(nuevo);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Concierto Jazz", result.getNombre());
        verify(eventoRepository, times(1)).save(nuevo);
    }


    // delete
    @Test
    public void testDelete() throws EventoNotFoundException {
        Evento mock = buildEvento(1L, "Festival Rock", "musica", false);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(mock));

        eventoService.delete(1L);

        verify(eventoRepository, times(1)).delete(mock);
    }

    @Test
    public void testDeleteNotFound() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EventoNotFoundException.class, () -> eventoService.delete(99L));
        verify(eventoRepository, never()).delete(any());
    }


    // modify
    @Test
    public void testModify() throws EventoNotFoundException {
        Evento existing = buildEvento(1L, "Festival Rock", "musica", false);
        Evento updated = buildEvento(1L, "Festival Rock Actualizado", "musica", false);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(eventoRepository.save(existing)).thenReturn(updated);

        Evento result = eventoService.modify(1L, updated);

        assertNotNull(result);
        assertEquals("Festival Rock Actualizado", result.getNombre());
    }

    @Test
    public void testModifyNotFound() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());
        Evento evento = buildEvento(99L, "X", "pop", false);

        assertThrows(EventoNotFoundException.class, () -> eventoService.modify(99L, evento));
        verify(eventoRepository, never()).save(any());
    }

    // Eventos candcelados
    @Test
    public void testFindCancelledEventos() {
        List<Evento> mockList = List.of(buildEvento(1L, "Evento Cancelado", "musica", true));
        when(eventoRepository.findCancelledEventos()).thenReturn(mockList);

        List<Evento> result = eventoService.findCancelledEventos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCancelado());
        verify(eventoRepository, times(1)).findCancelledEventos();
    }


    // patch
    @Test
    public void testPatch() throws EventoNotFoundException {
        Evento existing = buildEvento(1L, "Festival Rock", "musica", false);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(eventoRepository.save(existing)).thenReturn(existing);
        when(objectMapper.convertValue("teatro", String.class)).thenReturn("teatro");

        Evento result = eventoService.patch(1L, Map.of("categoria", "teatro"));

        assertNotNull(result);
        verify(eventoRepository, times(1)).save(existing);
    }

    @Test
    public void testPatchNotFound() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EventoNotFoundException.class, () -> eventoService.patch(99L, Map.of("nombre", "X")));
        verify(eventoRepository, never()).save(any());
    }
}
