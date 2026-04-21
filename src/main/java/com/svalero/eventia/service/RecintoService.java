package com.svalero.eventia.service;

import com.svalero.eventia.domain.Recinto;
import com.svalero.eventia.exception.RecintoNotFoundException;
import com.svalero.eventia.repository.RecintoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

import java.util.List;

@Service
public class RecintoService {

    @Autowired
    private RecintoRepository recintoRepository;

    @Autowired
    private ObjectMapper objectMapper;



    public List<Recinto> findAll() {
        return recintoRepository.findAll();
    }

    public Recinto findById(Long id) throws RecintoNotFoundException {
        return recintoRepository.findById(id)
                .orElseThrow(RecintoNotFoundException::new);
    }

    public Recinto add(Recinto recinto) {
        return recintoRepository.save(recinto);
    }

    public void delete(Long id) throws RecintoNotFoundException {
        Recinto recinto = recintoRepository.findById(id)
                .orElseThrow(RecintoNotFoundException::new);

        recintoRepository.delete(recinto);
    }

    public Recinto modify(Long id, Recinto nuevoRecinto) throws RecintoNotFoundException {
        Recinto recinto = recintoRepository.findById(id)
                .orElseThrow(RecintoNotFoundException::new);

        recinto.setNombre(nuevoRecinto.getNombre());
        recinto.setDireccion(nuevoRecinto.getDireccion());
        recinto.setCiudad(nuevoRecinto.getCiudad());
        recinto.setCapacidad(nuevoRecinto.getCapacidad());
        recinto.setCubierto(nuevoRecinto.isCubierto());
        recinto.setPrecioAlquiler(nuevoRecinto.getPrecioAlquiler());
        recinto.setEventosCelebrados(nuevoRecinto.getEventosCelebrados());
        recinto.setFechaInauguracion(nuevoRecinto.getFechaInauguracion());

        return recintoRepository.save(recinto);
    }

    public List<Recinto> findAll(String nombre, String ciudad, Boolean cubierto) {
        return recintoRepository.findByFilters(nombre, ciudad, cubierto);
    }

    public Recinto patch(long id, Map<String, Object> updates) throws RecintoNotFoundException {
        Recinto recinto = recintoRepository.findById(id)
                .orElseThrow(RecintoNotFoundException::new);

        updates.forEach((key, value) -> {
            if (key.equals("id")) {
                return;
            }

            Field field = ReflectionUtils.findField(Recinto.class, key);
            if (field != null) {
                field.setAccessible(true);
                Object convertedValue = objectMapper.convertValue(value, field.getType());
                ReflectionUtils.setField(field, recinto, convertedValue);
            }
        });

        return recintoRepository.save(recinto);
    }
}