package com.svalero.eventia.controller;

import com.svalero.eventia.domain.Reserva;
import com.svalero.eventia.exception.ErrorResponse;
import com.svalero.eventia.exception.ReservaNotFoundException;
import com.svalero.eventia.service.ReservaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    private final Logger logger = LoggerFactory.getLogger(ReservaController.class);

    @GetMapping("/reservas")
    public ResponseEntity<List<Reserva>> getAllReservas(
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) String codigoReserva,
            @RequestParam(required = false) Boolean confirmada) {
        logger.info("GET /rseervas - filtros nombre={}, categoria={}, cancelado={}", metodoPago, codigoReserva, confirmada);
        List<Reserva> reservas = reservaService.findAll(metodoPago, codigoReserva, confirmada);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<Reserva> getReserva(@PathVariable long id) throws ReservaNotFoundException {
        logger.info("GET/reservas/{}", id);
        Reserva reserva = reservaService.findById(id);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/reservas/confirmadas")
    public ResponseEntity<List<Reserva>> getConfirmedReservas() {
        logger.info("GET/reservas/confirmadas");
        List<Reserva> reservas = reservaService.findConfirmedReservas();
        return ResponseEntity.ok(reservas);
    }

    @PostMapping("/reservas")
    public ResponseEntity<Reserva> addReserva(@Valid  @RequestBody Reserva reserva) {
        logger.info("POST/reservas");
        Reserva nuevaReserva = reservaService.add(reserva);
        return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
    }

    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable long id) throws ReservaNotFoundException {
        logger.info("DELETE/reservas/{}",id);
        reservaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reservas/{id}")
    public ResponseEntity<Reserva> modifyReserva(@PathVariable long id, @Valid @RequestBody Reserva reserva) throws ReservaNotFoundException {
        logger.info("PUT/reservas/{}",id);
        Reserva reservaModificada = reservaService.modify(id, reserva);
        return ResponseEntity.ok(reservaModificada);
    }

    @PatchMapping("/reservas/{id}")
    public ResponseEntity<Reserva> patchReserva(@PathVariable long id,
                                                @RequestBody Map<String, Object> updates) throws ReservaNotFoundException {
        logger.info("PATCH/reservas/{}",id);
        Reserva reservaActualizada = reservaService.patch(id, updates);
        return ResponseEntity.ok(reservaActualizada);
    }



    @ExceptionHandler(ReservaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ReservaNotFoundException rnfe) {
        logger.error("Error 404 - rserva no encontrada: {}", rnfe.getMessage());
        return new ResponseEntity<>(ErrorResponse.notFound(rnfe.getMessage()), HttpStatus.NOT_FOUND);
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