package com.proyectoIntegrador.utnfrsr.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiResponse {

    private String mensaje;
    private int status;

    public ApiResponse(String mensaje, int status){
        this.mensaje = mensaje;
        this.status = status;
    }
}
