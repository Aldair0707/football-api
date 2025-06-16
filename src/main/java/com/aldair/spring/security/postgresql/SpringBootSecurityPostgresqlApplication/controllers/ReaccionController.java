package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.controllers;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.*;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.request.ReaccionRequest;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.response.ReaccionContadorResponse;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.response.ReaccionResponse;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository.*;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reacciones")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReaccionController {

    @Autowired
    private ReaccionRepository reaccionRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UserRepository userRepository;

    // Crear o actualizar una reacción
    @PostMapping("/crear")
    public ResponseEntity<?> reaccionar(@Valid @RequestBody ReaccionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<Publicacion> pubOpt = publicacionRepository.findById(request.getPublicacionId());

        if (userOpt.isEmpty() || pubOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario o publicación no encontrada");
        }

        // Verifica si ya existe una reacción de este usuario a esta publicación
        Optional<Reaccion> existente = reaccionRepository.findByPublicacionAndUsuario(pubOpt.get(), userOpt.get());
        Reaccion reaccion = existente.orElse(new Reaccion());
        reaccion.setTipo(request.getTipo());
        reaccion.setUsuario(userOpt.get());
        reaccion.setPublicacion(pubOpt.get());

        // Guardar o actualizar la reacción
        reaccionRepository.save(reaccion);

        return ResponseEntity.ok(new ReaccionResponse(request.getTipo(), username));
    }

    // Obtener las reacciones de una publicación
    @GetMapping("/publicacion/{id}")
    public ResponseEntity<List<ReaccionResponse>> obtenerReacciones(@PathVariable Long id) {
        Optional<Publicacion> pubOpt = publicacionRepository.findById(id);
        if (pubOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Reaccion> reacciones = reaccionRepository.findByPublicacion(pubOpt.get());

        List<ReaccionResponse> response = reacciones.stream().map(r ->
            new ReaccionResponse(r.getTipo(), r.getUsuario().getUsername())
        ).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Contar reacciones por tipo para una publicación
    @GetMapping("/contador/{publicacionId}")
    public ResponseEntity<ReaccionContadorResponse> contarReacciones(@PathVariable Long publicacionId) {
        Optional<Publicacion> pubOpt = publicacionRepository.findById(publicacionId);
        if (pubOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Publicacion publicacion = pubOpt.get();

        Map<String, Long> conteo = new HashMap<>();
        for (EReaction tipo : EReaction.values()) {
            Long cantidad = reaccionRepository.countByPublicacionAndTipo(publicacion, tipo);
            conteo.put(tipo.name(), cantidad);
        }

        return ResponseEntity.ok(new ReaccionContadorResponse(conteo));
    }

    @DeleteMapping("/eliminar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> eliminarReaccion(@RequestParam Long publicacionId, @RequestParam String tipo) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    // Verificar si la publicación existe
    Optional<Publicacion> publicacionOpt = publicacionRepository.findById(publicacionId);
    if (publicacionOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Publicacion publicacion = publicacionOpt.get();

    // Verificar si el usuario ya reaccionó
    Optional<Reaccion> reaccionOpt = reaccionRepository.findByPublicacionAndUsuario(publicacion, userRepository.findByUsername(username).get());
    if (reaccionOpt.isEmpty()) {
        return ResponseEntity.badRequest().body("Reacción no encontrada");
    }

    // Verificar si la reacción coincide con el tipo enviado (usando el enum EReaction)
    try {
        EReaction reaccionTipo = EReaction.valueOf(tipo);  // Convierte el tipo a enum
        Reaccion reaccion = reaccionOpt.get();
        if (!reaccion.getTipo().equals(reaccionTipo)) {
            return ResponseEntity.badRequest().body("Tipo de reacción no coincide");
        }

        // Eliminar la reacción
        reaccionRepository.delete(reaccion);
        return ResponseEntity.ok("Reacción eliminada con éxito");

    } catch (IllegalArgumentException e) {
        // Si el tipo de reacción no es válido
        return ResponseEntity.badRequest().body("Tipo de reacción no válido");
    }
}
}

