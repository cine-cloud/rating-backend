package com.unrn.services;

import com.unrn.dto.RatingDTO;
import com.unrn.dto.RatingPromedioDTO;
import com.unrn.dto.VotoUsuarioStatusDTO;
import com.unrn.exception.ResourceNotFoundException;
import com.unrn.model.Rating;
import com.unrn.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Transactional(readOnly = true)
    public List<RatingDTO> obtenerPorPelicula(Integer peliculaId) {
        return ratingRepository.findByPeliculaIdOrderByFechaDesc(peliculaId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RatingPromedioDTO obtenerPromedioPorPelicula(Integer peliculaId) {
        Double rawAvg = ratingRepository.findAverageRatingByPeliculaId(peliculaId);
        Long count = ratingRepository.countByPeliculaId(peliculaId);

        double promedio = 0.0;
        if (rawAvg != null) {
            promedio = BigDecimal.valueOf(rawAvg)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new RatingPromedioDTO(peliculaId, promedio, count != null ? count : 0L);
    }

    @Transactional(readOnly = true)
    public RatingDTO obtenerVotoUsuario(Integer peliculaId, String usuarioId) {
        return ratingRepository.findByUsuarioIdAndPeliculaId(usuarioId, peliculaId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El usuario " + usuarioId + " no ha votado la película " + peliculaId));
    }

    @Transactional(readOnly = true)
    public VotoUsuarioStatusDTO haVotadoUsuario(Integer peliculaId, String usuarioId) {
        boolean haVotado = ratingRepository.findByUsuarioIdAndPeliculaId(usuarioId, peliculaId).isPresent();
        return new VotoUsuarioStatusDTO(peliculaId, usuarioId, haVotado);
    }

    @Transactional(readOnly = true)
    public RatingDTO obtenerPorId(Long id) {
        return ratingRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el rating con id " + id));
    }

    @Transactional
    public RatingDTO votar(RatingDTO dto) {
        // Validar si el usuario ya votó esta película
        ratingRepository.findByUsuarioIdAndPeliculaId(dto.getUsuarioId(), dto.getPeliculaId())
                .ifPresent(r -> {
                    throw new IllegalStateException("El cliente ya ha votado esta película");
                });

        Rating rating = new Rating();
        rating.setUsuarioId(dto.getUsuarioId());
        rating.setPeliculaId(dto.getPeliculaId());
        rating.setEstrellas(dto.getEstrellas());
        rating.setComentario(dto.getComentario());
        rating.setFecha(dto.getFecha());

        return mapToDTO(ratingRepository.save(rating));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!ratingRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró la valoración con ID: " + id);
        }
        ratingRepository.deleteById(id);
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
