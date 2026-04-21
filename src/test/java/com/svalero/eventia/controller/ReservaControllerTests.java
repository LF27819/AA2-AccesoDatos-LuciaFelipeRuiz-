package com.svalero.eventia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.eventia.domain.Reserva;
import com.svalero.eventia.exception.ReservaNotFoundException;
import com.svalero.eventia.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
public class ReservaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Reserva buildReserva(long id) {
        return new Reserva(id, LocalDateTime.of(2025, 5, 1, 10, 0),
                2, 60f, "tarjeta", "RES-00" + id, true, null, null);
    }

    // GET 200
    @Test
    public void testGetAllReservas200() throws Exception {
        when(reservaService.findAll(null, null, null)).thenReturn(List.of(buildReserva(1L), buildReserva(2L)));

        mockMvc.perform(get("/reservas").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // GET reservas/id 200
    @Test
    public void testGetReserva200() throws Exception {
        when(reservaService.findById(1L)).thenReturn(buildReserva(1L));

        mockMvc.perform(get("/reservas/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigoReserva").value("RES-001"));
    }

    // GET reservas/id 404
    @Test
    public void testGetReserva404() throws Exception {
        when(reservaService.findById(99L)).thenThrow(new ReservaNotFoundException());

        mockMvc.perform(get("/reservas/99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // POST 201
    @Test
    public void testAddReserva201() throws Exception {
        Reserva reserva = buildReserva(0L);
        when(reservaService.add(any(Reserva.class))).thenReturn(buildReserva(1L));

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // POST 400 (fechaReserva, precioTotal, codigoReserva son @NotNull)
    @Test
    public void testAddReserva400() throws Exception {
        // Sin fechaReserva, precioTotal ni codigoReserva
        String invalidJson = "{\"cantidadEntradas\":2,\"metodoPago\":\"tarjeta\",\"confirmada\":false}";

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // DELETE reservas/id 204
    @Test
    public void testDeleteReserva204() throws Exception {
        doNothing().when(reservaService).delete(1L);

        mockMvc.perform(delete("/reservas/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE reservas/id 404
    @Test
    public void testDeleteReserva404() throws Exception {
        doThrow(new ReservaNotFoundException()).when(reservaService).delete(99L);

        mockMvc.perform(delete("/reservas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PUT reservas/id 200
    @Test
    public void testModifyReserva200() throws Exception {
        Reserva reserva = buildReserva(1L);
        when(reservaService.modify(eq(1L), any(Reserva.class))).thenReturn(reserva);

        mockMvc.perform(put("/reservas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PUT reservas/id 400
    @Test
    public void testModifyReserva400() throws Exception {
        String invalidJson = "{\"cantidadEntradas\":-1,\"metodoPago\":\"tarjeta\",\"confirmada\":false}";

        mockMvc.perform(put("/reservas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // PUT reservas/id  404
    @Test
    public void testModifyReserva404() throws Exception {
        Reserva reserva = buildReserva(99L);
        when(reservaService.modify(eq(99L), any(Reserva.class))).thenThrow(new ReservaNotFoundException());

        mockMvc.perform(put("/reservas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // PATCH reservas/id 200
    @Test
    public void testPatchReserva200() throws Exception {
        when(reservaService.patch(eq(1L), any())).thenReturn(buildReserva(1L));

        mockMvc.perform(patch("/reservas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"metodoPago\": \"efectivo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // PATCH reservas/id 404
    @Test
    public void testPatchReserva404() throws Exception {
        when(reservaService.patch(eq(99L), any())).thenThrow(new ReservaNotFoundException());

        mockMvc.perform(patch("/reservas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"metodoPago\": \"efectivo\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
