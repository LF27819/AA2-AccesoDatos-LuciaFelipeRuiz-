package com.svalero.eventia.service;

import com.svalero.eventia.domain.Evento;
import com.svalero.eventia.exception.EventoNotFoundException;
import com.svalero.eventia.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ObjectMapper objectMapper;


    public List<Evento> findAll() {
        return eventoRepository.findAll();
    }

    public Evento findById(Long id) throws EventoNotFoundException {
        return eventoRepository.findById(id)
                .orElseThrow(EventoNotFoundException::new);
    }

    public Evento add(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void delete(Long id) throws EventoNotFoundException {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(EventoNotFoundException::new);

        eventoRepository.delete(evento);
    }

    public Evento modify(Long id, Evento nuevoEvento) throws EventoNotFoundException {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(EventoNotFoundException::new);

        evento.setNombre(nuevoEvento.getNombre());
        evento.setDescripcion(nuevoEvento.getDescripcion());
        evento.setFechaEvento(nuevoEvento.getFechaEvento());
        evento.setHoraEvento(nuevoEvento.getHoraEvento());
        evento.setPrecioEntrada(nuevoEvento.getPrecioEntrada());
        evento.setAforoMaximo(nuevoEvento.getAforoMaximo());
        evento.setEntradasDisponibles(nuevoEvento.getEntradasDisponibles());
        evento.setCancelado(nuevoEvento.isCancelado());
        evento.setPresencial(nuevoEvento.isPresencial());
        evento.setCategoria(nuevoEvento.getCategoria());
        evento.setUsuario(nuevoEvento.getUsuario());
        evento.setArtista(nuevoEvento.getArtista());
        evento.setRecinto(nuevoEvento.getRecinto());

        return eventoRepository.save(evento);
    }

    public List<Evento> findAll(String nombre, String categoria, Boolean cancelado) {
        return eventoRepository.findByFilters(nombre, categoria, cancelado);
    }

    public Evento patch(long id, Map<String, Object> updates) throws EventoNotFoundException {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(EventoNotFoundException::new);

        updates.forEach((key, value) -> {
            if (key.equals("id")) {
                return;
            }

            Field field = ReflectionUtils.findField(Evento.class, key);
            if (field != null) {
                field.setAccessible(true);
                Object convertedValue = objectMapper.convertValue(value, field.getType());
                ReflectionUtils.setField(field, evento, convertedValue);
            }
        });

        return eventoRepository.save(evento);
    }

    public List<Evento> findCancelledEventos() {
        return eventoRepository.findCancelledEventos();
    }
}