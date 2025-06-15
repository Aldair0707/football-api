package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.controllers;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Comentario;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Publicacion;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.User;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.request.ComentarioRequest;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.response.ComentarioResponse;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository.ComentarioRepository;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository.PublicacionRepository;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UserRepository userRepository;

    // Crear comentario
    @PostMapping("/crear")
    public ResponseEntity<?> crearComentario(@Valid @RequestBody ComentarioRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        Optional<Publicacion> pubOpt = publicacionRepository.findById(request.getPublicacionId());
        if (pubOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Publicación no encontrada");
        }

        Comentario comentario = new Comentario();
        comentario.setTexto(request.getTexto());
        comentario.setAutor(userOpt.get());
        comentario.setPublicacion(pubOpt.get());

        comentarioRepository.save(comentario);

        ComentarioResponse response = new ComentarioResponse(
                comentario.getId(),
                comentario.getTexto(),
                username,
                comentario.getFechaCreacion()
        );

        return ResponseEntity.ok(response);
    }

    // Obtener comentarios por publicación
    @GetMapping("/por-publicacion/{id}")
    public ResponseEntity<List<ComentarioResponse>> obtenerComentariosPorPublicacion(@PathVariable Long id) {
        Optional<Publicacion> pubOpt = publicacionRepository.findById(id);
        if (pubOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Comentario> comentarios = comentarioRepository.findByPublicacion(pubOpt.get());

        List<ComentarioResponse> respuestas = comentarios.stream()
                .map(c -> new ComentarioResponse(
                        c.getId(),
                        c.getTexto(),
                        c.getAutor().getUsername(),
                        c.getFechaCreacion()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuestas);
    }

    // Eliminar comentario (solo autor y admin puede)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> eliminarComentario(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Comentario> comentarioOpt = comentarioRepository.findById(id);
        if (comentarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comentario comentario = comentarioOpt.get();

        if (!comentario.getAutor().getUsername().equals(username) &&
            auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar este comentario");
        }

        comentarioRepository.delete(comentario);
        return ResponseEntity.ok("Comentario eliminado con éxito");
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> actualizarComentario(@PathVariable Long id, @RequestBody ComentarioRequest nuevaData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Comentario> comentarioOpt = comentarioRepository.findById(id);
        if (comentarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comentario comentario = comentarioOpt.get();

        if (!comentario.getAutor().getUsername().equals(username)) {
            return ResponseEntity.status(403).body("Solo el autor puede editar este comentario");
        }

        comentario.setTexto(nuevaData.getTexto());
        comentarioRepository.save(comentario);
        return ResponseEntity.ok("Comentario actualizado correctamente");
    }

}
