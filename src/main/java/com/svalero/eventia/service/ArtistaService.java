package com.svalero.eventia.service;

import com.svalero.eventia.domain.Artista;
import com.svalero.eventia.exception.ArtistaNotFoundException;
import com.svalero.eventia.repository.ArtistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.List;

@Service
public class ArtistaService {

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private ObjectMapper objectMapper;


    public List<Artista> findAll() {
        return artistaRepository.findAll();
    }

    public Artista findById(Long id) throws ArtistaNotFoundException {
        return artistaRepository.findById(id)
                .orElseThrow(ArtistaNotFoundException::new);
    }

    public Artista add(Artista artista) {
        return artistaRepository.save(artista);
    }

    public void delete(Long id) throws ArtistaNotFoundException {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(ArtistaNotFoundException::new);

        artistaRepository.delete(artista);
    }

    public Artista modify(Long id, Artista nuevoArtista) throws ArtistaNotFoundException {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(ArtistaNotFoundException::new);

        artista.setNombreArtistico(nuevoArtista.getNombreArtistico());
        artista.setNombreReal(nuevoArtista.getNombreReal());
        artista.setGeneroMusical(nuevoArtista.getGeneroMusical());
        artista.setFechaNacimiento(nuevoArtista.getFechaNacimiento());
        artista.setActivo(nuevoArtista.isActivo());
        artista.setCache(nuevoArtista.getCache());
        artista.setEventosRealizados(nuevoArtista.getEventosRealizados());

        return artistaRepository.save(artista);
    }

    public List<Artista> findAll(String nombreArtistico, String generoMusical, Boolean activo) {
        return artistaRepository.findByFilters(nombreArtistico, generoMusical, activo);
    }

    public Artista patch(long id, Map<String, Object> updates) throws ArtistaNotFoundException {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(ArtistaNotFoundException::new);

        updates.forEach((key, value) -> {
            if (key.equals("id")) {
                return;
            }

            Field field = ReflectionUtils.findField(Artista.class, key);
            if (field != null) {
                field.setAccessible(true);
                Object convertedValue = objectMapper.convertValue(value, field.getType());
                ReflectionUtils.setField(field, artista, convertedValue);
            }
        });

        return artistaRepository.save(artista);
    }

    public List<Artista> findActiveArtistas() {
        return artistaRepository.findActiveArtistas();
    }
}