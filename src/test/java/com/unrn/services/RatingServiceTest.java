package com.unrn.services;

import com.unrn.dto.RatingDTO;
import com.unrn.dto.RatingPromedioDTO;
import com.unrn.dto.VotoUsuarioStatusDTO;
import com.unrn.exception.ResourceNotFoundException;
import com.unrn.model.Rating;
import com.unrn.repository.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    @DisplayName("Obtener valoraciones por película exitosamente")
    void testObtenerPorPelicula() {
        // Arrange
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setUsuarioId("user1");
        rating.setPeliculaId(10);
        rating.setEstrellas(5);

        when(ratingRepository.findByPeliculaIdOrderByFechaDesc(10))
                .thenReturn(List.of(rating));

        // Act
        List<RatingDTO> resultado = ratingService.obtenerPorPelicula(10);

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("user1", resultado.get(0).getUsuarioId());
        verify(ratingRepository, times(1)).findByPeliculaIdOrderByFechaDesc(10);
    }

    @Test
    @DisplayName("Obtener promedio de calificaciones redondeado a 2 decimales")
    void testObtenerPromedioPorPelicula() {
        // Arrange
        when(ratingRepository.findAverageRatingByPeliculaId(10)).thenReturn(4.6666667);
        when(ratingRepository.countByPeliculaId(10)).thenReturn(3L);

        // Act
        RatingPromedioDTO promedioDTO = ratingService.obtenerPromedioPorPelicula(10);

        // Assert
        assertEquals(10, promedioDTO.getPeliculaId());
        assertEquals(4.67, promedioDTO.getPromedio());
        assertEquals(3L, promedioDTO.getTotalVotos());
    }

    @Test
    @DisplayName("Obtener promedio de película sin votos debe retornar 0.0")
    void testObtenerPromedioSinVotos() {
        // Arrange
        when(ratingRepository.findAverageRatingByPeliculaId(99)).thenReturn(null);
        when(ratingRepository.countByPeliculaId(99)).thenReturn(0L);

        // Act
        RatingPromedioDTO promedioDTO = ratingService.obtenerPromedioPorPelicula(99);

        // Assert
        assertEquals(99, promedioDTO.getPeliculaId());
        assertEquals(0.0, promedioDTO.getPromedio());
        assertEquals(0L, promedioDTO.getTotalVotos());
    }

    @Test
    @DisplayName("Votar película exitosamente cuando no existía voto previo")
    void testVotarExitoso() {
        // Arrange
        RatingDTO dto = new RatingDTO();
        dto.setUsuarioId("user123");
        dto.setPeliculaId(5);
        dto.setEstrellas(5);
        dto.setComentario("Excelente");

        Rating savedEntity = new Rating();
        savedEntity.setId(10L);
        savedEntity.setUsuarioId("user123");
        savedEntity.setPeliculaId(5);
        savedEntity.setEstrellas(5);
        savedEntity.setComentario("Excelente");

        when(ratingRepository.findByUsuarioIdAndPeliculaId("user123", 5))
                .thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(savedEntity);

        // Act
        RatingDTO resultado = ratingService.votar(dto);

        // Assert
        assertNotNull(resultado.getId());
        assertEquals(10L, resultado.getId());
        assertEquals("user123", resultado.getUsuarioId());
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    @DisplayName("Lanzar excepción cuando el cliente intenta votar la misma película más de una vez")
    void testVotarDuplicadoLanzaExcepcion() {
        // Arrange
        RatingDTO dto = new RatingDTO();
        dto.setUsuarioId("user123");
        dto.setPeliculaId(5);

        when(ratingRepository.findByUsuarioIdAndPeliculaId("user123", 5))
                .thenReturn(Optional.of(new Rating()));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> ratingService.votar(dto)
        );

        assertEquals("El cliente ya ha votado esta película", exception.getMessage());
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Verificar si usuario ha votado")
    void testHaVotadoUsuario() {
        // Arrange
        when(ratingRepository.findByUsuarioIdAndPeliculaId("u1", 1))
                .thenReturn(Optional.of(new Rating()));

        // Act
        VotoUsuarioStatusDTO status = ratingService.haVotadoUsuario(1, "u1");

        // Assert
        assertTrue(status.isHaVotado());
    }

    @Test
    @DisplayName("Obtener voto usuario existente")
    void testObtenerVotoUsuarioExitoso() {
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setUsuarioId("user1");
        rating.setPeliculaId(10);
        rating.setEstrellas(5);

        when(ratingRepository.findByUsuarioIdAndPeliculaId("user1", 10))
                .thenReturn(Optional.of(rating));

        RatingDTO result = ratingService.obtenerVotoUsuario(10, "user1");
        assertNotNull(result);
        assertEquals("user1", result.getUsuarioId());
    }

    @Test
    @DisplayName("Obtener voto usuario inexistente lanza ResourceNotFoundException")
    void testObtenerVotoUsuarioInexistente() {
        when(ratingRepository.findByUsuarioIdAndPeliculaId("user1", 10))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ratingService.obtenerVotoUsuario(10, "user1"));
    }

    @Test
    @DisplayName("Obtener rating por ID exitoso")
    void testObtenerPorIdExitoso() {
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setUsuarioId("user1");
        rating.setPeliculaId(10);
        rating.setEstrellas(5);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        RatingDTO result = ratingService.obtenerPorId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Obtener rating por ID inexistente lanza ResourceNotFoundException")
    void testObtenerPorIdInexistente() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ratingService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Eliminar rating existente")
    void testEliminarExitoso() {
        // Arrange
        when(ratingRepository.existsById(1L)).thenReturn(true);

        // Act
        ratingService.eliminar(1L);

        // Assert
        verify(ratingRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar rating inexistente lanza ResourceNotFoundException")
    void testEliminarInexistenteLanzaExcepcion() {
        // Arrange
        when(ratingRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ratingService.eliminar(99L));
    }
}
