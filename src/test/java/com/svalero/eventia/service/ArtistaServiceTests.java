package com.svalero.eventia.service;

import com.svalero.eventia.domain.Artista;
import com.svalero.eventia.exception.ArtistaNotFoundException;
import com.svalero.eventia.repository.ArtistaRepository;
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
public class ArtistaServiceTests {

    @InjectMocks
    private ArtistaService artistaService;

    @Mock
    private ArtistaRepository artistaRepository;

    @Mock
    private ObjectMapper objectMapper;


    // findAll
    @Test
    public void testFindAll() {
        List<Artista> mockList = List.of(
                new Artista(1L, "Bad Bunny", "Benito Martínez", "reggaeton", LocalDate.of(1994, 3, 10), true, 50000f, 120),
                new Artista(2L, "Rosalía", "Rosalía Vila", "flamenco-pop", LocalDate.of(1993, 9, 25), true, 60000f, 95)
        );
        when(artistaRepository.findAll()).thenReturn(mockList);

        List<Artista> result = artistaService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Bad Bunny", result.get(0).getNombreArtistico());
        assertEquals("Rosalía", result.get(1).getNombreArtistico());
        verify(artistaRepository, times(1)).findAll();
    }


    // findAll + filtros
    @Test
    public void testFindAllWithFilters() {
        List<Artista> mockList = List.of(
                new Artista(1L, "Bad Bunny", "Benito Martínez", "reggaeton", LocalDate.of(1994, 3, 10), true, 50000f, 120)
        );
        when(artistaRepository.findByFilters("Bad Bunny", "reggaeton", true)).thenReturn(mockList);

        List<Artista> result = artistaService.findAll("Bad Bunny", "reggaeton", true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bad Bunny", result.get(0).getNombreArtistico());
        verify(artistaRepository, times(1)).findByFilters("Bad Bunny", "reggaeton", true);
    }

    @Test
    public void testFindAllWithFiltersNoResults() {
        when(artistaRepository.findByFilters("Inexistente", null, null)).thenReturn(List.of());

        List<Artista> result = artistaService.findAll("Inexistente", null, null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // findById
    @Test
    public void testFindById() throws ArtistaNotFoundException {
        Artista mockArtista = new Artista(1L, "Bad Bunny", "Benito Martínez", "reggaeton", LocalDate.of(1994, 3, 10), true, 50000f, 120);
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(mockArtista));

        Artista result = artistaService.findById(1L);

        assertNotNull(result);
        assertEquals("Bad Bunny", result.getNombreArtistico());
        verify(artistaRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(artistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ArtistaNotFoundException.class, () -> artistaService.findById(99L));
        verify(artistaRepository, times(1)).findById(99L);
    }


    // add
    @Test
    public void testAdd() {
        Artista artista = new Artista(0L, "Karol G", "Carolina Giraldo", "reggaeton", LocalDate.of(1991, 2, 14), true, 45000f, 80);
        Artista saved = new Artista(3L, "Karol G", "Carolina Giraldo", "reggaeton", LocalDate.of(1991, 2, 14), true, 45000f, 80);
        when(artistaRepository.save(artista)).thenReturn(saved);

        Artista result = artistaService.add(artista);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Karol G", result.getNombreArtistico());
        verify(artistaRepository, times(1)).save(artista);
    }


    // delete
    @Test
    public void testDelete() throws ArtistaNotFoundException {
        Artista artista = new Artista(1L, "Bad Bunny", "Benito Martínez", "reggaeton", LocalDate.of(1994, 3, 10), true, 50000f, 120);
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));

        artistaService.delete(1L);

        verify(artistaRepository, times(1)).findById(1L);
        verify(artistaRepository, times(1)).delete(artista);
    }

    @Test
    public void testDeleteNotFound() {
        when(artistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ArtistaNotFoundException.class, () -> artistaService.delete(99L));
        verify(artistaRepository, never()).delete(any());
    }


    // modify
    @Test
    public void testModify() throws ArtistaNotFoundException {
        Artista existing = new Artista(1L, "Bad Bunny", "Benito Martínez", "reggaeton", LocalDate.of(1994, 3, 10), true, 50000f, 120);
        Artista updated = new Artista(1L, "Bad Bunny Updated", "Benito Martínez", "reggaeton", LocalDate.of(1994, 3, 10), true, 55000f, 125);
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(artistaRepository.save(existing)).thenReturn(updated);

        Artista result = artistaService.modify(1L, updated);

        assertNotNull(result);
        assertEquals("Bad Bunny Updated", result.getNombreArtistico());
        assertEquals(55000f, result.getCache());
        verify(artistaRepository, times(1)).save(existing);
    }

    @Test
    public void testModifyNotFound() {
        when(artistaRepository.findById(99L)).thenReturn(Optional.empty());
        Artista artista = new Artista(99L, "X", "X", "pop", LocalDate.now(), true, 1000f, 1);

        assertThrows(ArtistaNotFoundException.class, () -> artistaService.modify(99L, artista));
        verify(artistaRepository, never()).save(any());
    }


    // Artistas activos
    @Test
    public void testFindActiveArtistas() {
        List<Artista> mockList = List.of(
                new Artista(1L, "Bad Bunny", "Benito Martínez", "reggaeton", LocalDate.of(1994, 3, 10), true, 50000f, 120)
        );
        when(artistaRepository.findActiveArtistas()).thenReturn(mockList);

        List<Artista> result = artistaService.findActiveArtistas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActivo());
        verify(artistaRepository, times(1)).findActiveArtistas();
    }


    // patch
    @Test
    public void testPatch() throws ArtistaNotFoundException {
        Artista existing = new Artista(1L, "Bad Bunny", "Benito Martínez", "reggaeton", LocalDate.of(1994, 3, 10), true, 50000f, 120);
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(artistaRepository.save(existing)).thenReturn(existing);
        when(objectMapper.convertValue("trap", String.class)).thenReturn("trap");

        Map<String, Object> updates = Map.of("generoMusical", "trap");
        Artista result = artistaService.patch(1L, updates);

        assertNotNull(result);
        verify(artistaRepository, times(1)).save(existing);
    }

    @Test
    public void testPatchNotFound() {
        when(artistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ArtistaNotFoundException.class, () -> artistaService.patch(99L, Map.of("generoMusical", "pop")));
        verify(artistaRepository, never()).save(any());
    }
}
