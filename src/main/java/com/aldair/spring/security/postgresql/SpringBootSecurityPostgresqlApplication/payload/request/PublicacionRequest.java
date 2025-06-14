package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PublicacionRequest {

    @NotBlank(message = "El contenido no puede estar vacío")
    @Size(max = 280, message = "El contenido no puede superar los 280 caracteres")
    private String contenido;

    public PublicacionRequest() {
    }

    public PublicacionRequest(String contenido) {
        this.contenido = contenido;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
