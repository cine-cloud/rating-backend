package com.unrn.controller;

import com.unrn.dto.RatingDTO;
import com.unrn.dto.RatingPromedioDTO;
import com.unrn.dto.VotoUsuarioStatusDTO;
import com.unrn.services.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/pelicula/{peliculaId}")
    public ResponseEntity<List<RatingDTO>> obtenerPorPelicula(@PathVariable Integer peliculaId) {
        return ResponseEntity.ok(ratingService.obtenerPorPelicula(peliculaId));
    }

    @GetMapping("/pelicula/{peliculaId}/promedio")
    public ResponseEntity<RatingPromedioDTO> obtenerPromedioPorPelicula(@PathVariable Integer peliculaId) {
        return ResponseEntity.ok(ratingService.obtenerPromedioPorPelicula(peliculaId));
    }

    @GetMapping("/pelicula/{peliculaId}/usuario/{usuarioId}/voto")
    public ResponseEntity<RatingDTO> obtenerVotoUsuario(
            @PathVariable Integer peliculaId,
            @PathVariable String usuarioId) {
        return ResponseEntity.ok(ratingService.obtenerVotoUsuario(peliculaId, usuarioId));
    }

    @GetMapping("/pelicula/{peliculaId}/usuario/{usuarioId}/ha-votado")
    public ResponseEntity<VotoUsuarioStatusDTO> haVotadoUsuario(
            @PathVariable Integer peliculaId,
            @PathVariable String usuarioId) {
        return ResponseEntity.ok(ratingService.haVotadoUsuario(peliculaId, usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<RatingDTO> votar(@Valid @RequestBody RatingDTO ratingDTO) {
        RatingDTO creado = ratingService.votar(ratingDTO);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ratingService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
