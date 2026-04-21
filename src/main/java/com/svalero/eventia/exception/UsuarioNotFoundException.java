package com.svalero.eventia.exception;

public class UsuarioNotFoundException extends Exception{
    public UsuarioNotFoundException() {
        super("Usuario no encontrado");
    }
}
