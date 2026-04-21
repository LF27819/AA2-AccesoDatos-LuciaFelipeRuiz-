package com.svalero.eventia.service;

import com.svalero.eventia.domain.Usuario;
import com.svalero.eventia.exception.UsuarioNotFoundException;
import com.svalero.eventia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;


    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(long id) throws UsuarioNotFoundException {
        return usuarioRepository.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);
    }

    public Usuario add(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void delete(long id) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);

        usuarioRepository.delete(usuario);
    }

    public Usuario modify(long id, Usuario nuevoUsuario) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);

        usuario.setNombre(nuevoUsuario.getNombre());
        usuario.setApellidos(nuevoUsuario.getApellidos());
        usuario.setEmail(nuevoUsuario.getEmail());
        usuario.setPassword(nuevoUsuario.getPassword());
        usuario.setTelefono(nuevoUsuario.getTelefono());
        usuario.setActivo(nuevoUsuario.isActivo());
        usuario.setFechaNacimiento(nuevoUsuario.getFechaNacimiento());
        usuario.setEventosAsistidos(nuevoUsuario.getEventosAsistidos());
        usuario.setRol(nuevoUsuario.getRol());
        usuario.setSaldoCuenta(nuevoUsuario.getSaldoCuenta());

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> findAll(String nombre, String email, String rol) {
        return usuarioRepository.findByFilters(nombre, email, rol);
    }

    public Usuario patch(long id, Map<String, Object> updates) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);

        updates.forEach((key, value) -> {
            if (key.equals("id")) {
                return;
            }

            Field field = ReflectionUtils.findField(Usuario.class, key);
            if (field != null) {
                field.setAccessible(true);
                Object convertedValue = objectMapper.convertValue(value, field.getType());
                ReflectionUtils.setField(field, usuario, convertedValue);
            }
        });

        return usuarioRepository.save(usuario);
    }
}