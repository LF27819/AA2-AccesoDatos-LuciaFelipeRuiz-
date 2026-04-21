package com.svalero.eventia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.eventia.domain.Recinto;
import com.svalero.eventia.exception.RecintoNotFoundException;
import com.svalero.eventia.service.RecintoService;
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

@WebMvcTest(RecintoController.class)
public class RecintoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecintoService recintoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Recinto buildRecinto(long id) {
        return new Recinto(id, "Estadio Romareda", "Calle Real 1", "Zaragoza",
                30000, false, 5000f, 50, LocalDate.of(1957, 1, 1));
    }

    // GET 200
    @Test
    public void testGetAllRecintos200() throws Exception {
        when(recintoService.findAll(null, null, null)).thenReturn(List.of(buildRecinto(1L), buildRecinto(2L)));

        mockMvc.perform(get("/recintos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // GET recintos/id 200
    @Test
    public void testGetRecinto200() throws Exception {
        when(recintoService.findById(1L)).thenReturn(buildRecinto(1L));

        mockMvc.perform(get("/recintos/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Estadio Romareda"));
    }

    // GET recintos/id 404
    @Test
    public void testGetRecinto404() throws Exception {
        when(recintoService.findById(99L)).thenThrow(new RecintoNotFoundException());

        mockMvc.perform(get("/recintos/99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // POST 201
    @Test
    public void testAddRecinto201() throws Exception {
        Recinto recinto = buildRecinto(0L);
        when(recintoService.add(any(Recinto.class))).thenReturn(buildRecinto(1L));

        mockMvc.perform(post("/recintos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recinto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }


    // POST 400 (nombre, dirección, ciudad @NotNull)
    @Test
    public void testAddRecinto400() throws Exception {
        // Sin nombre, dirección ni ciudad (campos @NotNull)
        String invalidJson = "{\"capacidad\":1000,\"cubierto\":true,\"precioAlquiler\":100,\"eventosCelebrados\":0}";

        mockMvc.perform(post("/recintos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // DELETE recintos/id 204
    @Test
    public void testDeleteRecinto204() throws Exception {
        doNothing().when(recintoService).delete(1L);

        mockMvc.perform(delete("/recintos/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE recintos/id 404
    @Test
    public void testDeleteRecinto404() throws Exception {
        doThrow(new RecintoNotFoundException()).when(recintoService).delete(99L);

        mockMvc.perform(delete("/recintos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PUT recintos/id 200
    @Test
    public void testModifyRecinto200() throws Exception {
        Recinto recinto = buildRecinto(1L);
        when(recintoService.modify(eq(1L), any(Recinto.class))).thenReturn(recinto);

        mockMvc.perform(put("/recintos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recinto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PUT recintos/id  400
    @Test
    public void testModifyRecinto400() throws Exception {
        String invalidJson = "{\"capacidad\":-1,\"precioAlquiler\":-100,\"eventosCelebrados\":-1}";

        mockMvc.perform(put("/recintos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // PUT recintos/id  404
    @Test
    public void testModifyRecinto404() throws Exception {
        Recinto recinto = buildRecinto(99L);
        when(recintoService.modify(eq(99L), any(Recinto.class))).thenThrow(new RecintoNotFoundException());

        mockMvc.perform(put("/recintos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recinto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PATCH recintos/id 200
    @Test
    public void testPatchRecinto200() throws Exception {
        when(recintoService.patch(eq(1L), any())).thenReturn(buildRecinto(1L));

        mockMvc.perform(patch("/recintos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ciudad\": \"Madrid\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PATCH recintos/id 404
    @Test
    public void testPatchRecinto404() throws Exception {
        when(recintoService.patch(eq(99L), any())).thenThrow(new RecintoNotFoundException());

        mockMvc.perform(patch("/recintos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ciudad\": \"Madrid\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
