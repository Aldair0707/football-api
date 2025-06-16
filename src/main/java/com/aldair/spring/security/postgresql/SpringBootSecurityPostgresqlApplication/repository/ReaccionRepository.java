package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Reaccion;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.User;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Publicacion;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.EReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaccionRepository extends JpaRepository<Reaccion, Long> {

    // Obtener reacciones por publicación
    List<Reaccion> findByPublicacion(Publicacion publicacion);

    // Contar las reacciones de un tipo específico para una publicación
    Long countByPublicacionAndTipo(Publicacion publicacion, EReaction tipo);

    // Verificar si un usuario ya reaccionó a una publicación
    Optional<Reaccion> findByPublicacionAndUsuario(Publicacion publicacion, User usuario);
    
    void deleteByPublicacionId(Long publicacionId);

 
}
