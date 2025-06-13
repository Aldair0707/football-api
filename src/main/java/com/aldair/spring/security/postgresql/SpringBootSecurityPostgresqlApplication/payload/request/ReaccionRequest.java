package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.request;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.EReaction;

public class ReaccionRequest {

    private Long publicacionId;
    private EReaction tipo;

    public Long getPublicacionId() {
        return publicacionId;
    }

    public void setPublicacionId(Long publicacionId) {
        this.publicacionId = publicacionId;
    }

    public EReaction getTipo() {
        return tipo;
    }

    public void setTipo(EReaction tipo) {
        this.tipo = tipo;
    }
}
