package com.svalero.eventia.service;

import com.svalero.eventia.domain.Reserva;
import com.svalero.eventia.exception.ReservaNotFoundException;
import com.svalero.eventia.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ObjectMapper objectMapper;


    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Reserva findById(Long id) throws ReservaNotFoundException {
        return reservaRepository.findById(id)
                .orElseThrow(ReservaNotFoundException::new);
    }

    public Reserva add(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public void delete(Long id) throws ReservaNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(ReservaNotFoundException::new);

        reservaRepository.delete(reserva);
    }

    public Reserva modify(Long id, Reserva nuevaReserva) throws ReservaNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(ReservaNotFoundException::new);

        reserva.setFechaReserva(nuevaReserva.getFechaReserva());
        reserva.setCantidadEntradas(nuevaReserva.getCantidadEntradas());
        reserva.setPrecioTotal(nuevaReserva.getPrecioTotal());
        reserva.setMetodoPago(nuevaReserva.getMetodoPago());
        reserva.setCodigoReserva(nuevaReserva.getCodigoReserva());
        reserva.setConfirmada(nuevaReserva.isConfirmada());
        reserva.setUsuario(nuevaReserva.getUsuario());
        reserva.setEvento(nuevaReserva.getEvento());

        return reservaRepository.save(reserva);
    }

    public List<Reserva> findAll(String metodoPago, String codigoReserva, Boolean confirmada) {
        return reservaRepository.findByFilters(metodoPago, codigoReserva, confirmada);
    }

    public Reserva patch(long id, Map<String, Object> updates) throws ReservaNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(ReservaNotFoundException::new);

        updates.forEach((key, value) -> {
            if (key.equals("id")) {
                return;
            }

            Field field = ReflectionUtils.findField(Reserva.class, key);
            if (field != null) {
                field.setAccessible(true);
                Object convertedValue = objectMapper.convertValue(value, field.getType());
                ReflectionUtils.setField(field, reserva, convertedValue);
            }
        });

        return reservaRepository.save(reserva);
    }

    public List<Reserva> findConfirmedReservas() {
        return reservaRepository.findConfirmedReservas();
    }
}