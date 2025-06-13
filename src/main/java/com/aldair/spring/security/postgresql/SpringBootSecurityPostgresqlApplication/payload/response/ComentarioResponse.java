package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.response;

import java.time.LocalDateTime;

public class ComentarioResponse {

    private Long id;
    private String texto;
    private String autor;
    private LocalDateTime fechaCreacion;

    public ComentarioResponse(Long id, String texto, String autor, LocalDateTime fechaCreacion) {
        this.id = id;
        this.texto = texto;
        this.autor = autor;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    
}
