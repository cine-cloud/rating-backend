package com.unrn.services;

import com.unrn.controller.DTO.RatingDTO;
import com.unrn.model.Rating;
import com.unrn.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public List<RatingDTO> obtenerPorPelicula(Integer peliculaId) {
        return ratingRepository.findByPeliculaIdOrderByFechaDesc(peliculaId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RatingDTO votar(RatingDTO dto) {
        // Validar si el usuario ya votó esta película
        ratingRepository.findByUsuarioIdAndPeliculaId(dto.getUsuarioId(), dto.getPeliculaId())
                .ifPresent(r -> {
                    throw new RuntimeException("El cliente ya ha votado esta película");
                });

        Rating rating = new Rating();
        rating.setUsuarioId(dto.getUsuarioId());
        rating.setPeliculaId(dto.getPeliculaId());
        rating.setEstrellas(dto.getEstrellas());
        rating.setComentario(dto.getComentario());
        rating.setFecha(dto.getFecha());
        
        return mapToDTO(ratingRepository.save(rating));
    }

    private RatingDTO mapToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setUsuarioId(rating.getUsuarioId());
        dto.setPeliculaId(rating.getPeliculaId());
        dto.setEstrellas(rating.getEstrellas());
        dto.setComentario(rating.getComentario());
        dto.setFecha(rating.getFecha());
        return dto;
    }
}
