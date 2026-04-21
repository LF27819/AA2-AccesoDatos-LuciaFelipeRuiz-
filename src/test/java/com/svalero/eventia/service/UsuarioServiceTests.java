package com.svalero.eventia.service;

import com.svalero.eventia.domain.Usuario;
import com.svalero.eventia.exception.UsuarioNotFoundException;
import com.svalero.eventia.repository.UsuarioRepository;
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
public class UsuarioServiceTests {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ObjectMapper objectMapper;

    private Usuario buildUsuario(long id, String nombre, String email, String rol) {
        return new Usuario(id, nombre, "Apellido", email, "password123", "600000000",
                true, LocalDate.of(1990, 1, 1), 5, rol, 100f);
    }

    // findAll
    @Test
    public void testFindAll() {
        List<Usuario> mockList = List.of(
                buildUsuario(1L, "Ana", "ana@email.com", "admin"),
                buildUsuario(2L, "Luis", "luis@email.com", "user")
        );
        when(usuarioRepository.findAll()).thenReturn(mockList);

        List<Usuario> result = usuarioService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ana", result.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    // findAll + filtros
    @Test
    public void testFindAllWithFilters() {
        List<Usuario> mockList = List.of(buildUsuario(1L, "Ana", "ana@email.com", "admin"));
        when(usuarioRepository.findByFilters("Ana", null, "admin")).thenReturn(mockList);

        List<Usuario> result = usuarioService.findAll("Ana", null, "admin");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getRol());
        verify(usuarioRepository, times(1)).findByFilters("Ana", null, "admin");
    }

    @Test
    public void testFindAllWithFiltersNoResults() {
        when(usuarioRepository.findByFilters("Inexistente", null, null)).thenReturn(List.of());

        List<Usuario> result = usuarioService.findAll("Inexistente", null, null);

        assertEquals(0, result.size());
    }


    // findById
    @Test
    public void testFindById() throws UsuarioNotFoundException {
        Usuario mock = buildUsuario(1L, "Ana", "ana@email.com", "admin");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mock));

        Usuario result = usuarioService.findById(1L);

        assertNotNull(result);
        assertEquals("Ana", result.getNombre());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.findById(99L));
    }


    // add
        @Test
    public void testAdd() {
        Usuario nuevo = buildUsuario(0L, "Marta", "marta@email.com", "user");
        Usuario saved = buildUsuario(3L, "Marta", "marta@email.com", "user");
        when(usuarioRepository.save(nuevo)).thenReturn(saved);

        Usuario result = usuarioService.add(nuevo);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Marta", result.getNombre());
        verify(usuarioRepository, times(1)).save(nuevo);
    }


    // delete
        @Test
    public void testDelete() throws UsuarioNotFoundException {
        Usuario mock = buildUsuario(1L, "Ana", "ana@email.com", "admin");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mock));

        usuarioService.delete(1L);

        verify(usuarioRepository, times(1)).delete(mock);
    }

    @Test
    public void testDeleteNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.delete(99L));
        verify(usuarioRepository, never()).delete(any());
    }


    // modify
        @Test
    public void testModify() throws UsuarioNotFoundException {
        Usuario existing = buildUsuario(1L, "Ana", "ana@email.com", "user");
        Usuario updated = buildUsuario(1L, "Ana García", "ana@email.com", "admin");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.save(existing)).thenReturn(updated);

        Usuario result = usuarioService.modify(1L, updated);

        assertNotNull(result);
        assertEquals("Ana García", result.getNombre());
        assertEquals("admin", result.getRol());
    }

    @Test
    public void testModifyNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        Usuario usuario = buildUsuario(99L, "X", "x@x.com", "user");

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.modify(99L, usuario));
        verify(usuarioRepository, never()).save(any());
    }

    // patch
    @Test
    public void testPatch() throws UsuarioNotFoundException {
        Usuario existing = buildUsuario(1L, "Ana", "ana@email.com", "user");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.save(existing)).thenReturn(existing);
        when(objectMapper.convertValue("admin", String.class)).thenReturn("admin");

        Usuario result = usuarioService.patch(1L, Map.of("rol", "admin"));

        assertNotNull(result);
        verify(usuarioRepository, times(1)).save(existing);
    }

    @Test
    public void testPatchNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.patch(99L, Map.of("rol", "admin")));
        verify(usuarioRepository, never()).save(any());
    }
}
