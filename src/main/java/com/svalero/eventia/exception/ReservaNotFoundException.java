package com.svalero.eventia.exception;

public class ReservaNotFoundException extends Exception {
    public ReservaNotFoundException() {
        super("Reserva no encontrado");
    }
}
