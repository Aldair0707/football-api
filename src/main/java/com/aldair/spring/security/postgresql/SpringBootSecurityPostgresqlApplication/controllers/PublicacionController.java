package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.controllers;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Publicacion;
 import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.User;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.request.PublicacionRequest;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.payload.response.PublicacionResponse;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository.PublicacionRepository;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository.ReaccionRepository;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;  // Asegúrate de que esta importación sea correcta
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/publicaciones")
public class PublicacionController {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReaccionRepository reaccionRepository;

    // Obtener todas las publicaciones paginadas
    @GetMapping("/all")
public ResponseEntity<Page<PublicacionResponse>> obtenerTodasPublicaciones(Pageable pageable) {
    Page<Publicacion> publicaciones = publicacionRepository.findAll(
        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("fechaCreacion")))); // Modificado para ordenar por fechaCreacion

    Page<PublicacionResponse> respuesta = publicaciones.map(p -> new PublicacionResponse(
        p.getId(),
        p.getContenido(),
        p.getPostedBy().getUsername(),
        p.getFechaCreacion()
    ));

    return ResponseEntity.ok(respuesta);
}

    // Crear una nueva publicación
    @PostMapping("/crear")
    public ResponseEntity<?> crearPublicacion(@Valid @RequestBody PublicacionRequest publicacionRequest) {
        System.out.println("Contenido recibido: " + publicacionRequest.getContenido());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        Publicacion nuevaPublicacion = new Publicacion(publicacionRequest.getContenido());
        nuevaPublicacion.setPostedBy(userOpt.get());

        publicacionRepository.save(nuevaPublicacion);

        PublicacionResponse response = new PublicacionResponse(
                nuevaPublicacion.getId(),
                nuevaPublicacion.getContenido(),
                userOpt.get().getUsername(),
                nuevaPublicacion.getFechaCreacion()
        );

        return ResponseEntity.ok(response);
    }

    // Obtener publicaciones del usuario autenticado
    @GetMapping("/mis-posts")
    public ResponseEntity<List<PublicacionResponse>> obtenerMisPublicaciones() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Publicacion> publicaciones = publicacionRepository.findByPostedBy(userOpt.get());

        List<PublicacionResponse> respuesta = publicaciones.stream()
                .map(p -> new PublicacionResponse(
                        p.getId(),
                        p.getContenido(),
                        p.getPostedBy().getUsername(),
                        p.getFechaCreacion()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Publicacion> obtenerPublicacionPorId(@PathVariable Long id) {
     Optional<Publicacion> publicacion = publicacionRepository.findById(id);
     return publicacion.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> actualizarPublicacion(@PathVariable Long id, @RequestBody Publicacion nuevaData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Publicacion> pubOpt = publicacionRepository.findById(id);
        if (pubOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
        }

        Publicacion publicacion = pubOpt.get();

        if (!publicacion.getPostedBy().getUsername().equals(username)) {
        return ResponseEntity.status(403).body("Solo el autor puede editar esta publicación");
        }

        publicacion.setContenido(nuevaData.getContenido());
        publicacionRepository.save(publicacion);
        return ResponseEntity.ok("Publicación actualizada correctamente");
    }


@DeleteMapping("/{id}")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@Transactional  // Asegura que esta operación esté en una transacción
public ResponseEntity<?> eliminarPublicacion(@PathVariable Long id) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    // Verificar si la publicación existe
    Optional<Publicacion> publicacionOpt = publicacionRepository.findById(id);
    if (publicacionOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Publicacion publicacion = publicacionOpt.get();

    // Verificar si el usuario tiene permiso para eliminar la publicación
    if (!publicacion.getPostedBy().getUsername().equals(username) &&
        auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        return ResponseEntity.status(403).body("No tienes permiso para eliminar esta publicación");
    }

    // Eliminar todas las reacciones asociadas a la publicación/tweet
    reaccionRepository.deleteByPublicacionId(id);  // Método en el repositorio para eliminar las reacciones

    // Eliminar la publicación
    publicacionRepository.delete(publicacion);

    return ResponseEntity.ok("Publicación eliminada con éxito");
}


 
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@GetMapping("/publicaciones/{id}/owner")
public ResponseEntity<Boolean> checkPublicacionOwner(@PathVariable Long id) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName(); // Obtener el nombre de usuario autenticado

    Optional<Publicacion> publicacionOpt = publicacionRepository.findById(id);
    if (publicacionOpt.isEmpty()) {
        return ResponseEntity.notFound().build(); // Si no existe el tweet, retornamos 404
    }

    Publicacion publicacion = publicacionOpt.get();

        // Verificar si el usuario es el propietario o tiene permisos de admin
        boolean isOwner = publicacion.getPostedBy().getUsername().equals(username) ||
                          auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(isOwner);
}




}
