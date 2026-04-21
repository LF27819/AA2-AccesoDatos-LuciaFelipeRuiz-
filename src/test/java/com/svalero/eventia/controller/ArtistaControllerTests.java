package com.svalero.eventia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.eventia.domain.Artista;
import com.svalero.eventia.exception.ArtistaNotFoundException;
import com.svalero.eventia.service.ArtistaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistaController.class)
public class ArtistaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistaService artistaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Artista buildArtista(long id) {
        return new Artista(id, "Bad Bunny", "Benito Martínez", "reggaeton",
                LocalDate.of(1994, 3, 10), true, 50000f, 120);
    }

    // GET 200
    @Test
    public void testGetAllArtistas200() throws Exception {
        List<Artista> mockList = List.of(buildArtista(1L), buildArtista(2L));
        when(artistaService.findAll(null, null, null)).thenReturn(mockList);

        mockMvc.perform(get("/artistas").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombreArtistico").value("Bad Bunny"));
    }

    // GET artistas/id 200
    @Test
    public void testGetArtista200() throws Exception {
        when(artistaService.findById(1L)).thenReturn(buildArtista(1L));

        mockMvc.perform(get("/artistas/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreArtistico").value("Bad Bunny"));
    }

    // GET artistas/id 404
    @Test
    public void testGetArtista404() throws Exception {
        when(artistaService.findById(99L)).thenThrow(new ArtistaNotFoundException());

        mockMvc.perform(get("/artistas/99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // POST 201
    @Test
    public void testAddArtista201() throws Exception {
        Artista artista = buildArtista(0L);
        Artista saved = buildArtista(1L);
        when(artistaService.add(any(Artista.class))).thenReturn(saved);

        mockMvc.perform(post("/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artista)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // POST 400 (validación: nombreArtistico vacío)
    @Test
    public void testAddArtista400() throws Exception {
        // nombreArtistico y nombreReal son @NotBlank, los dejamos vacíos
        Artista invalid = new Artista(0L, "", "", "reggaeton",
                LocalDate.of(1994, 3, 10), true, 50000f, 120);

        mockMvc.perform(post("/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // DELETE artistas/id 204
    @Test
    public void testDeleteArtista204() throws Exception {
        doNothing().when(artistaService).delete(1L);

        mockMvc.perform(delete("/artistas/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE artistas/id 404
    @Test
    public void testDeleteArtista404() throws Exception {
        doThrow(new ArtistaNotFoundException()).when(artistaService).delete(99L);

        mockMvc.perform(delete("/artistas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PUT artistas/id 200
    @Test
    public void testModifyArtista200() throws Exception {
        Artista artista = buildArtista(1L);
        when(artistaService.modify(eq(1L), any(Artista.class))).thenReturn(artista);

        mockMvc.perform(put("/artistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artista)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PUT artistas/id 400
    @Test
    public void testModifyArtista400() throws Exception {
        Artista invalid = new Artista(1L, "", "", "reggaeton",
                LocalDate.of(1994, 3, 10), true, 50000f, 120);

        mockMvc.perform(put("/artistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // PUT artistas/id  404
    @Test
    public void testModifyArtista404() throws Exception {
        Artista artista = buildArtista(99L);
        when(artistaService.modify(eq(99L), any(Artista.class))).thenThrow(new ArtistaNotFoundException());

        mockMvc.perform(put("/artistas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artista)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PATCH artistas/id 200
    @Test
    public void testPatchArtista200() throws Exception {
        when(artistaService.patch(eq(1L), any())).thenReturn(buildArtista(1L));

        mockMvc.perform(patch("/artistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"generoMusical\": \"trap\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PATCH artistas/id 404
    @Test
    public void testPatchArtista404() throws Exception {
        when(artistaService.patch(eq(99L), any())).thenThrow(new ArtistaNotFoundException());

        mockMvc.perform(patch("/artistas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"generoMusical\": \"trap\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
