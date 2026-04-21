package com.svalero.eventia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.eventia.domain.Evento;
import com.svalero.eventia.exception.EventoNotFoundException;
import com.svalero.eventia.service.EventoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoController.class)
public class EventoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventoService eventoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Evento buildEvento(long id) {
        return new Evento(id, "Festival Rock", "Descripción", LocalDate.of(2025, 8, 10),
                LocalTime.of(20, 0), 30f, 5000, 4000, false, true, "musica", null, null, null);
    }


    // GET 200
    @Test
    public void testGetAllEventos200() throws Exception {
        when(eventoService.findAll(null, null, null)).thenReturn(List.of(buildEvento(1L), buildEvento(2L)));

        mockMvc.perform(get("/eventos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // GET events/id 200
    @Test
    public void testGetEvento200() throws Exception {
        when(eventoService.findById(1L)).thenReturn(buildEvento(1L));

        mockMvc.perform(get("/eventos/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Festival Rock"));
    }

    // GET eventos/id 404
    @Test
    public void testGetEvento404() throws Exception {
        when(eventoService.findById(99L)).thenThrow(new EventoNotFoundException());

        mockMvc.perform(get("/eventos/99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // POST 201
    @Test
    public void testAddEvento201() throws Exception {
        Evento evento = buildEvento(0L);
        when(eventoService.add(any(Evento.class))).thenReturn(buildEvento(1L));

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }


    // POST 400 (fechaEvento y horaEvento son @NotNull)
    @Test
    public void testAddEvento400() throws Exception {
        // Enviamos JSON sin fechaEvento ni horaEvento
        String invalidJson = "{\"nombre\":\"Test\",\"precioEntrada\":10,\"aforoMaximo\":100,\"entradasDisponibles\":100}";

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // DELETE eventos/id 204
    @Test
    public void testDeleteEvento204() throws Exception {
        doNothing().when(eventoService).delete(1L);

        mockMvc.perform(delete("/eventos/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE eventos/id 404
    @Test
    public void testDeleteEvento404() throws Exception {
        doThrow(new EventoNotFoundException()).when(eventoService).delete(99L);

        mockMvc.perform(delete("/eventos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PUT eventos/id 200
    @Test
    public void testModifyEvento200() throws Exception {
        Evento evento = buildEvento(1L);
        when(eventoService.modify(eq(1L), any(Evento.class))).thenReturn(evento);

        mockMvc.perform(put("/eventos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PUT eventos/id 400
    @Test
    public void testModifyEvento400() throws Exception {
        String invalidJson = "{\"nombre\":\"Test\",\"precioEntrada\":-5,\"aforoMaximo\":0,\"entradasDisponibles\":0}";

        mockMvc.perform(put("/eventos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // PUT eventos/id  404
    @Test
    public void testModifyEvento404() throws Exception {
        Evento evento = buildEvento(99L);
        when(eventoService.modify(eq(99L), any(Evento.class))).thenThrow(new EventoNotFoundException());

        mockMvc.perform(put("/eventos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PATCH eventos/id 200
    @Test
    public void testPatchEvento200() throws Exception {
        when(eventoService.patch(eq(1L), any())).thenReturn(buildEvento(1L));

        mockMvc.perform(patch("/eventos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoria\": \"teatro\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PATCH eventos/id 404
    @Test
    public void testPatchEvento404() throws Exception {
        when(eventoService.patch(eq(99L), any())).thenThrow(new EventoNotFoundException());

        mockMvc.perform(patch("/eventos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoria\": \"teatro\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
