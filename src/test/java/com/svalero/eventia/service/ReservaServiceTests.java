package com.svalero.eventia.service;

import com.svalero.eventia.domain.Reserva;
import com.svalero.eventia.exception.ReservaNotFoundException;
import com.svalero.eventia.repository.ReservaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTests {

    @InjectMocks
    private ReservaService reservaService;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ObjectMapper objectMapper;

    private Reserva buildReserva(long id, String codigo, String metodoPago, boolean confirmada) {
        return new Reserva(id, LocalDateTime.of(2025, 5, 1, 10, 0), 2, 60f, metodoPago, codigo, confirmada, null, null);
    }


    // findAll
    @Test
    public void testFindAll() {
        List<Reserva> mockList = List.of(
                buildReserva(1L, "RES-001", "tarjeta", true),
                buildReserva(2L, "RES-002", "efectivo", false)
        );
        when(reservaRepository.findAll()).thenReturn(mockList);

        List<Reserva> result = reservaService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("RES-001", result.get(0).getCodigoReserva());
        verify(reservaRepository, times(1)).findAll();
    }


    // findAll + filtros
    @Test
    public void testFindAllWithFilters() {
        List<Reserva> mockList = List.of(buildReserva(1L, "RES-001", "tarjeta", true));
        when(reservaRepository.findByFilters("tarjeta", null, true)).thenReturn(mockList);

        List<Reserva> result = reservaService.findAll("tarjeta", null, true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("tarjeta", result.get(0).getMetodoPago());
        verify(reservaRepository, times(1)).findByFilters("tarjeta", null, true);
    }

    @Test
    public void testFindAllWithFiltersNoResults() {
        when(reservaRepository.findByFilters("bitcoin", null, null)).thenReturn(List.of());

        List<Reserva> result = reservaService.findAll("bitcoin", null, null);

        assertEquals(0, result.size());
    }


    // findById
    @Test
    public void testFindById() throws ReservaNotFoundException {
        Reserva mock = buildReserva(1L, "RES-001", "tarjeta", true);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(mock));

        Reserva result = reservaService.findById(1L);

        assertNotNull(result);
        assertEquals("RES-001", result.getCodigoReserva());
        verify(reservaRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReservaNotFoundException.class, () -> reservaService.findById(99L));
    }


    // add
       @Test
    public void testAdd() {
        Reserva nueva = buildReserva(0L, "RES-003", "paypal", false);
        Reserva saved = buildReserva(3L, "RES-003", "paypal", false);
        when(reservaRepository.save(nueva)).thenReturn(saved);

        Reserva result = reservaService.add(nueva);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("RES-003", result.getCodigoReserva());
        verify(reservaRepository, times(1)).save(nueva);
    }


    // delete
        @Test
    public void testDelete() throws ReservaNotFoundException {
        Reserva mock = buildReserva(1L, "RES-001", "tarjeta", true);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(mock));

        reservaService.delete(1L);

        verify(reservaRepository, times(1)).delete(mock);
    }

    @Test
    public void testDeleteNotFound() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReservaNotFoundException.class, () -> reservaService.delete(99L));
        verify(reservaRepository, never()).delete(any());
    }


    // modify
        @Test
    public void testModify() throws ReservaNotFoundException {
        Reserva existing = buildReserva(1L, "RES-001", "tarjeta", false);
        Reserva updated = buildReserva(1L, "RES-001", "tarjeta", true);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reservaRepository.save(existing)).thenReturn(updated);

        Reserva result = reservaService.modify(1L, updated);

        assertNotNull(result);
        assertTrue(result.isConfirmada());
    }

    @Test
    public void testModifyNotFound() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());
        Reserva reserva = buildReserva(99L, "X", "X", false);

        assertThrows(ReservaNotFoundException.class, () -> reservaService.modify(99L, reserva));
        verify(reservaRepository, never()).save(any());
    }


    // Reservas confirmadas
      @Test
    public void testFindConfirmedReservas() {
        List<Reserva> mockList = List.of(buildReserva(1L, "RES-001", "tarjeta", true));
        when(reservaRepository.findConfirmedReservas()).thenReturn(mockList);

        List<Reserva> result = reservaService.findConfirmedReservas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isConfirmada());
        verify(reservaRepository, times(1)).findConfirmedReservas();
    }


    // patch
    @Test
    public void testPatch() throws ReservaNotFoundException {
        Reserva existing = buildReserva(1L, "RES-001", "tarjeta", false);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reservaRepository.save(existing)).thenReturn(existing);
        when(objectMapper.convertValue("efectivo", String.class)).thenReturn("efectivo");

        Reserva result = reservaService.patch(1L, Map.of("metodoPago", "efectivo"));

        assertNotNull(result);
        verify(reservaRepository, times(1)).save(existing);
    }

    @Test
    public void testPatchNotFound() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReservaNotFoundException.class, () -> reservaService.patch(99L, Map.of("metodoPago", "X")));
        verify(reservaRepository, never()).save(any());
    }
}
