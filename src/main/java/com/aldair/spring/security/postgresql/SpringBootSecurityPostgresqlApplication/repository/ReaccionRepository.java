package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Reaccion;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Publicacion;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.User;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.EReaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReaccionRepository extends JpaRepository<Reaccion, Long> {

    // Obtener reacciones por publicación
    List<Reaccion> findByPublicacion(Publicacion publicacion);

    // Ver si un usuario ya reaccionó a una publicación
    Optional<Reaccion> findByPublicacionAndUsuario(Publicacion publicacion, User usuario);

    // Contar reacciones por tipo para una publicación
    Long countByPublicacionAndDescription(Publicacion publicacion, EReaction description);

 
}
