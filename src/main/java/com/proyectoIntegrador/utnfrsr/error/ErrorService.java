package com.proyectoIntegrador.utnfrsr.error;

public class ErrorService extends RuntimeException {
    public ErrorService(String message) {
        super(message);
    }
}
