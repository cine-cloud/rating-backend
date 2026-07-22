package com.unrn.repository;

import com.unrn.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByPeliculaIdOrderByFechaDesc(Integer peliculaId);
    Optional<Rating> findByUsuarioIdAndPeliculaId(String usuarioId, Integer peliculaId);

    @Query("SELECT AVG(r.estrellas) FROM Rating r WHERE r.peliculaId = :peliculaId")
    Double findAverageRatingByPeliculaId(@Param("peliculaId") Integer peliculaId);

    Long countByPeliculaId(Integer peliculaId);
}
