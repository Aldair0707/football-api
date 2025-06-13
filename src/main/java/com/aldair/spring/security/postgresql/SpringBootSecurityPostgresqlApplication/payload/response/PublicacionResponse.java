package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.response;

import java.time.LocalDateTime;

public class PublicacionResponse {

    private Long id;
    private String contenido;
    private String username;
    private LocalDateTime fechaCreacion;

    public PublicacionResponse(Long id, String contenido, String username, LocalDateTime fechaCreacion) {
        this.id = id;
        this.contenido = contenido;
        this.username = username;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
