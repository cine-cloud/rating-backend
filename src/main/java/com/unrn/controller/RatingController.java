package com.unrn.controller;

import com.unrn.controller.DTO.RatingDTO;
import com.unrn.services.RatingService;
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

    @PostMapping
    public ResponseEntity<RatingDTO> votar(@RequestBody RatingDTO ratingDTO) {
        try {
            RatingDTO creado = ratingService.votar(ratingDTO);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
