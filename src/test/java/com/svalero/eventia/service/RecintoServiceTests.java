package com.svalero.eventia.service;

import com.svalero.eventia.domain.Recinto;
import com.svalero.eventia.exception.RecintoNotFoundException;
import com.svalero.eventia.repository.RecintoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecintoServiceTests {

    @InjectMocks
    private RecintoService recintoService;

    @Mock
    private RecintoRepository recintoRepository;

    @Mock
    private ObjectMapper objectMapper;

    private Recinto buildRecinto(long id, String nombre, String ciudad, boolean cubierto) {
        return new Recinto(id, nombre, "Calle Mayor 1", ciudad, 10000, cubierto, 5000f, 30, LocalDate.of(2000, 1, 1));
    }


    // findAll
    @Test
    public void testFindAll() {
        List<Recinto> mockList = List.of(
                buildRecinto(1L, "Palacio de Deportes", "Madrid", true),
                buildRecinto(2L, "Estadio Romareda", "Zaragoza", false)
        );
        when(recintoRepository.findAll()).thenReturn(mockList);

        List<Recinto> result = recintoService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Palacio de Deportes", result.get(0).getNombre());
        verify(recintoRepository, times(1)).findAll();
    }


    // findAll + filtros
    @Test
    public void testFindAllWithFilters() {
        List<Recinto> mockList = List.of(buildRecinto(2L, "Estadio Romareda", "Zaragoza", false));
        when(recintoRepository.findByFilters(null, "Zaragoza", false)).thenReturn(mockList);

        List<Recinto> result = recintoService.findAll(null, "Zaragoza", false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Zaragoza", result.get(0).getCiudad());
        verify(recintoRepository, times(1)).findByFilters(null, "Zaragoza", false);
    }

    @Test
    public void testFindAllWithFiltersNoResults() {
        when(recintoRepository.findByFilters("Inexistente", null, null)).thenReturn(List.of());

        List<Recinto> result = recintoService.findAll("Inexistente", null, null);

        assertEquals(0, result.size());
    }


    // findById
    @Test
    public void testFindById() throws RecintoNotFoundException {
        Recinto mock = buildRecinto(1L, "Palacio de Deportes", "Madrid", true);
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(mock));

        Recinto result = recintoService.findById(1L);

        assertNotNull(result);
        assertEquals("Palacio de Deportes", result.getNombre());
        verify(recintoRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(recintoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecintoNotFoundException.class, () -> recintoService.findById(99L));
    }


    // add
    @Test
    public void testAdd() {
        Recinto nuevo = buildRecinto(0L, "Auditorio", "Barcelona", true);
        Recinto saved = buildRecinto(3L, "Auditorio", "Barcelona", true);
        when(recintoRepository.save(nuevo)).thenReturn(saved);

        Recinto result = recintoService.add(nuevo);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Auditorio", result.getNombre());
        verify(recintoRepository, times(1)).save(nuevo);
    }


    // delete
    @Test
    public void testDelete() throws RecintoNotFoundException {
        Recinto mock = buildRecinto(1L, "Palacio de Deportes", "Madrid", true);
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(mock));

        recintoService.delete(1L);

        verify(recintoRepository, times(1)).delete(mock);
    }

    @Test
    public void testDeleteNotFound() {
        when(recintoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecintoNotFoundException.class, () -> recintoService.delete(99L));
        verify(recintoRepository, never()).delete(any());
    }


    // modify
    @Test
    public void testModify() throws RecintoNotFoundException {
        Recinto existing = buildRecinto(1L, "Palacio de Deportes", "Madrid", true);
        Recinto updated = buildRecinto(1L, "Palacio de Deportes Actualizado", "Madrid", true);
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(recintoRepository.save(existing)).thenReturn(updated);

        Recinto result = recintoService.modify(1L, updated);

        assertNotNull(result);
        assertEquals("Palacio de Deportes Actualizado", result.getNombre());
    }

    @Test
    public void testModifyNotFound() {
        when(recintoRepository.findById(99L)).thenReturn(Optional.empty());
        Recinto recinto = buildRecinto(99L, "X", "X", false);

        assertThrows(RecintoNotFoundException.class, () -> recintoService.modify(99L, recinto));
        verify(recintoRepository, never()).save(any());
    }


    // patch
    @Test
    public void testPatch() throws RecintoNotFoundException {
        Recinto existing = buildRecinto(1L, "Palacio de Deportes", "Madrid", true);
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(recintoRepository.save(existing)).thenReturn(existing);
        when(objectMapper.convertValue("Sevilla", String.class)).thenReturn("Sevilla");

        Recinto result = recintoService.patch(1L, Map.of("ciudad", "Sevilla"));

        assertNotNull(result);
        verify(recintoRepository, times(1)).save(existing);
    }

    @Test
    public void testPatchNotFound() {
        when(recintoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecintoNotFoundException.class, () -> recintoService.patch(99L, Map.of("ciudad", "X")));
        verify(recintoRepository, never()).save(any());
    }
}
