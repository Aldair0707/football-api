package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Comentario;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Publicacion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByPublicacion(Publicacion publicacion);
}
