package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.response;

import java.util.Map;

public class ReaccionContadorResponse {

    private Map<String, Long> conteo;

    public ReaccionContadorResponse(Map<String, Long> conteo) {
        this.conteo = conteo;
    }

    public Map<String, Long> getConteo() {
        return conteo;
    }

    public void setConteo(Map<String, Long> conteo) {
        this.conteo = conteo;
    }
}
