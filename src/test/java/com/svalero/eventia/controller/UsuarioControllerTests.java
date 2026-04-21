package com.svalero.eventia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.eventia.domain.Usuario;
import com.svalero.eventia.exception.UsuarioNotFoundException;
import com.svalero.eventia.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario buildUsuario(long id) {
        return new Usuario(id, "Ana", "García López", "ana@email.com",
                "password123", "600000000", true, LocalDate.of(1990, 1, 1), 5, "user", 100f);
    }

    // GET 200
    @Test
    public void testGetAllUsuarios200() throws Exception {
        when(usuarioService.findAll(null, null, null)).thenReturn(List.of(buildUsuario(1L), buildUsuario(2L)));

        mockMvc.perform(get("/usuarios").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // GET usuarios/id 200
    @Test
    public void testGetUsuario200() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(buildUsuario(1L));

        mockMvc.perform(get("/usuarios/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    // GET usuarios/id 404
    @Test
    public void testGetUsuario404() throws Exception {
        when(usuarioService.findById(99L)).thenThrow(new UsuarioNotFoundException());

        mockMvc.perform(get("/usuarios/99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // POST 201
    @Test
    public void testAddUsuario201() throws Exception {
        Usuario usuario = buildUsuario(0L);
        when(usuarioService.add(any(Usuario.class))).thenReturn(buildUsuario(1L));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // POST 400 (nombre, apellidos, email, password @NotNull; password @Size(min=6))
    @Test
    public void testAddUsuario400MissingFields() throws Exception {
        // Sin nombre, apellidos, email ni password
        String invalidJson = "{\"telefono\":\"600000000\",\"activo\":true}";

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    public void testAddUsuario400InvalidEmail() throws Exception {
        // Email inválido
        Usuario usuario = new Usuario(0L, "Ana", "García", "no-es-un-email",
                "password123", "600000000", true, LocalDate.of(1990, 1, 1), 0, "user", 0f);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    public void testAddUsuario400ShortPassword() throws Exception {
        // Password demasiado corto (min 6)
        Usuario usuario = new Usuario(0L, "Ana", "García", "ana@email.com",
                "abc", "600000000", true, LocalDate.of(1990, 1, 1), 0, "user", 0f);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // DELETE usuarios/id 204
    @Test
    public void testDeleteUsuario204() throws Exception {
        doNothing().when(usuarioService).delete(1L);

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE usuarios/id 404
    @Test
    public void testDeleteUsuario404() throws Exception {
        doThrow(new UsuarioNotFoundException()).when(usuarioService).delete(99L);

        mockMvc.perform(delete("/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PUT usuarios/id 200
    @Test
    public void testModifyUsuario200() throws Exception {
        Usuario usuario = buildUsuario(1L);
        when(usuarioService.modify(eq(1L), any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PUT usuarios/id 400
    @Test
    public void testModifyUsuario400() throws Exception {
        // saldoCuenta negativo y password demasiado corto
        Usuario invalid = new Usuario(1L, "Ana", "García", "ana@email.com",
                "ab", "600000000", true, LocalDate.of(1990, 1, 1), 0, "user", -10f);

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // PUT usuarios/id  404
    @Test
    public void testModifyUsuario404() throws Exception {
        Usuario usuario = buildUsuario(99L);
        when(usuarioService.modify(eq(99L), any(Usuario.class))).thenThrow(new UsuarioNotFoundException());

        mockMvc.perform(put("/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PATCH usuarios/id 200
    @Test
    public void testPatchUsuario200() throws Exception {
        when(usuarioService.patch(eq(1L), any())).thenReturn(buildUsuario(1L));

        mockMvc.perform(patch("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rol\": \"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PATCH usuarios/id 404
    @Test
    public void testPatchUsuario404() throws Exception {
        when(usuarioService.patch(eq(99L), any())).thenThrow(new UsuarioNotFoundException());

        mockMvc.perform(patch("/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rol\": \"admin\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
