package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.request;

import jakarta.validation.constraints.NotBlank;

public class ComentarioRequest {

    private Long publicacionId;

    @NotBlank
    private String texto;

    public Long getPublicacionId() {
        return publicacionId;
    }

    public void setPublicacionId(Long publicacionId) {
        this.publicacionId = publicacionId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
