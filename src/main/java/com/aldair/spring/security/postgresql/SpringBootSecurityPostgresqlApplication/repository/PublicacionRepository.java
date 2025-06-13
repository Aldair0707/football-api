package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Publicacion;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findByPostedBy(User user);
}
