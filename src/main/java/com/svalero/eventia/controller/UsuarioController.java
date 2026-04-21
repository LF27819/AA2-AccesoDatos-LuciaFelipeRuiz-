package com.svalero.eventia.controller;

import com.svalero.eventia.domain.Usuario;
import com.svalero.eventia.exception.ErrorResponse;
import com.svalero.eventia.exception.UsuarioNotFoundException;
import com.svalero.eventia.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.svalero.eventia.domain.UsuarioV2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @GetMapping("/v1/usuarios")
    public ResponseEntity<List<Usuario>> getAllUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String rol) {
        logger.info("GET /api/v1/usuarios - filtros nombre={}, categoria={}, cancelado={}", nombre, email, rol);
        List<Usuario> usuarios = usuarioService.findAll(nombre, email, rol);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/v1/usuarios/{id}")
    public ResponseEntity<Usuario> getUsuarioV1(@PathVariable long id) throws UsuarioNotFoundException {
        logger.info("GET /api/v1/usuarios/{}", id);
        Usuario usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/v2/usuarios/{id}")
    public ResponseEntity<UsuarioV2> getUsuarioV2(@PathVariable long id) throws UsuarioNotFoundException {
        logger.info("GET /api/v2/usuarios/{}", id);
        UsuarioV2 usuario = usuarioService.findByIdV2(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/v1/usuarios")
    public ResponseEntity<Usuario> addUsuarioV1(@Valid @RequestBody Usuario usuario) {
        logger.info("POST /api/v1/usuarios");
        Usuario nuevoUsuario = usuarioService.add(usuario);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PostMapping("/v2/usuarios")
    public ResponseEntity<UsuarioV2> addUsuarioV2(@Valid @RequestBody UsuarioV2 usuario) {
        logger.info("POST /api/v2/usuarios");
        UsuarioV2 nuevoUsuario = usuarioService.addV2(usuario);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @DeleteMapping("/v1/usuarios/{id}")
    public ResponseEntity<Void> deleteUsuarioV1(@PathVariable long id) throws UsuarioNotFoundException {
        logger.info("DELETE /api/v1/usuarios/{}", id);
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v2/usuarios/{id}")
    public ResponseEntity<Map<String, String>> deleteUsuarioV2(@PathVariable long id) throws UsuarioNotFoundException {
        logger.info("DELETE /api/v2/usuarios/{}", id);
        String mensaje = usuarioService.deleteV2(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", mensaje);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/v1/usuarios/{id}")
    public ResponseEntity<Usuario> modifyUsuarioV1(@PathVariable long id, @Valid @RequestBody Usuario usuario) throws UsuarioNotFoundException {
        logger.info("PUT /api/v1/usuarios/{}", id);
        Usuario usuarioModificado = usuarioService.modify(id, usuario);
        return ResponseEntity.ok(usuarioModificado);
    }

    @PutMapping("/v2/usuarios/{id}")
    public ResponseEntity<UsuarioV2> modifyUsuarioV2(@PathVariable long id, @Valid @RequestBody UsuarioV2 usuario) throws UsuarioNotFoundException {
        logger.info("PUT /api/v2/usuarios/{}", id);
        UsuarioV2 usuarioModificado = usuarioService.modifyV2(id, usuario);
        return ResponseEntity.ok(usuarioModificado);
    }

    @PatchMapping("/v1/usuarios/{id}")
    public ResponseEntity<Usuario> patchUsuario(@PathVariable long id,
                                                @RequestBody Map<String, Object> updates) throws UsuarioNotFoundException {
        logger.info("PATCH/api/v1/usuarios/{}",id);
        Usuario usuarioActualizado = usuarioService.patch(id, updates);
        return ResponseEntity.ok(usuarioActualizado);
    }



    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UsuarioNotFoundException unfe) {
        logger.error("Error 404 - usuario no encontrado: {}", unfe.getMessage());
        return new ResponseEntity<>(ErrorResponse.notFound(unfe.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        logger.error("Error 400 - Error de validación", manve);
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Error 500 - error interno del servidor", e);
        return new ResponseEntity<>(
                ErrorResponse.generalError(500, "internal-server-error", "Error interno del servidor"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
