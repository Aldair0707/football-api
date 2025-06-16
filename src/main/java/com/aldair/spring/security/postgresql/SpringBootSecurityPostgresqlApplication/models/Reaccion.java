package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models;

import jakarta.persistence.*;

@Entity
@Table(name = "reacciones")  // Define el nombre de la tabla en la base de datos
public class Reaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EReaction tipo; // Puede ser LIKE, LOVE, SAD, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User usuario; // Relación con el usuario que reaccionó

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicacion_id")
    private Publicacion publicacion; // Relación con la publicación a la que se reaccionó

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EReaction getTipo() {
        return tipo;
    }

    public void setTipo(EReaction tipo) {
        this.tipo = tipo;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    // Getters y setters

    
}

