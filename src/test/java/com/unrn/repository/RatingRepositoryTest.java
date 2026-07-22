package com.unrn.repository;

import com.unrn.model.Rating;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;

    @Test
    @DisplayName("Guardar y buscar ratings por película ordenados por fecha descendente")
    void testGuardarYBuscarPorPelicula() {
        // Arrange
        Rating r1 = new Rating();
        r1.setUsuarioId("user1");
        r1.setPeliculaId(100);
        r1.setEstrellas(4);
        r1.setComentario("Buena");
        r1.setFecha(LocalDateTime.now().minusDays(1));
        ratingRepository.save(r1);

        Rating r2 = new Rating();
        r2.setUsuarioId("user2");
        r2.setPeliculaId(100);
        r2.setEstrellas(5);
        r2.setComentario("Obra maestra");
        r2.setFecha(LocalDateTime.now());
        ratingRepository.save(r2);

        // Act
        List<Rating> ratings = ratingRepository.findByPeliculaIdOrderByFechaDesc(100);

        // Assert
        assertEquals(2, ratings.size());
        assertEquals("user2", ratings.get(0).getUsuarioId(), "El rating más reciente debe ser el primero");
        assertEquals("user1", ratings.get(1).getUsuarioId());
    }

    @Test
    @DisplayName("Buscar voto por usuarioId y peliculaId")
    void testFindByUsuarioIdAndPeliculaId() {
        // Arrange
        Rating rating = new Rating();
        rating.setUsuarioId("user_test");
        rating.setPeliculaId(200);
        rating.setEstrellas(3);
        ratingRepository.save(rating);

        // Act
        Optional<Rating> encontrado = ratingRepository.findByUsuarioIdAndPeliculaId("user_test", 200);

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals(3, encontrado.get().getEstrellas());

        Optional<Rating> noExiste = ratingRepository.findByUsuarioIdAndPeliculaId("user_test", 999);
        assertFalse(noExiste.isPresent());
    }

    @Test
    @DisplayName("Calcular rating promedio y conteo de votos")
    void testCalculoPromedioYConteo() {
        // Arrange
        Rating r1 = new Rating();
        r1.setUsuarioId("u1");
        r1.setPeliculaId(300);
        r1.setEstrellas(5);
        ratingRepository.save(r1);

        Rating r2 = new Rating();
        r2.setUsuarioId("u2");
        r2.setPeliculaId(300);
        r2.setEstrellas(3);
        ratingRepository.save(r2);

        // Act
        Double promedio = ratingRepository.findAverageRatingByPeliculaId(300);
        Long total = ratingRepository.countByPeliculaId(300);

        // Assert
        assertEquals(4.0, promedio);
        assertEquals(2L, total);
    }
}
