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

    @PostMapping("/crear")
    public ResponseEntity<?> reaccionar(@Valid @RequestBody ReaccionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<Publicacion> pubOpt = publicacionRepository.findById(request.getPublicacionId());

        if (userOpt.isEmpty() || pubOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario o publicación no encontrada");
        }

        // Si ya reaccionó, actualiza
        Optional<Reaccion> existente = reaccionRepository.findByPublicacionAndUsuario(pubOpt.get(), userOpt.get());
        Reaccion reaccion = existente.orElse(new Reaccion());
        reaccion.setDescription(request.getTipo());
        reaccion.setUsuario(userOpt.get());
        reaccion.setPublicacion(pubOpt.get());

        reaccionRepository.save(reaccion);

        return ResponseEntity.ok(new ReaccionResponse(request.getTipo(), username));
    }

    @GetMapping("/publicacion/{id}")
    public ResponseEntity<List<ReaccionResponse>> obtenerReacciones(@PathVariable Long id) {
        Optional<Publicacion> pubOpt = publicacionRepository.findById(id);
        if (pubOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Reaccion> reacciones = reaccionRepository.findByPublicacion(pubOpt.get());

        List<ReaccionResponse> response = reacciones.stream().map(r ->
            new ReaccionResponse(r.getDescription(), r.getUsuario().getUsername())
        ).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/contador/{publicacionId}")
    public ResponseEntity<ReaccionContadorResponse> contarPorTipo(@PathVariable Long publicacionId) {
        Optional<Publicacion> pubOpt = publicacionRepository.findById(publicacionId);
        if (pubOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
        }

        Publicacion publicacion = pubOpt.get();

        // Crear mapa para guardar conteo por tipo
        Map<String, Long> conteo = new HashMap<>();
        for (EReaction tipo : EReaction.values()) {
            Long cantidad = reaccionRepository.countByPublicacionAndDescription(publicacion, tipo);
            conteo.put(tipo.name(), cantidad);
        }

        return ResponseEntity.ok(new ReaccionContadorResponse(conteo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> eliminarReaccion(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Reaccion> reaccionOpt = reaccionRepository.findById(id);
        if (reaccionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reaccion reaccion = reaccionOpt.get();

        if (!reaccion.getUsuario().getUsername().equals(username) &&
            auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar esta reacción");
        }

        reaccionRepository.delete(reaccion);
        return ResponseEntity.ok("Reacción eliminada con éxito");
    }

}
