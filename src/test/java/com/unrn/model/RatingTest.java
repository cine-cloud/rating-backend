package com.unrn.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    @DisplayName("Crear instancia de Rating y verificar asignación de campos")
    void crearRating_asignarValoresExitoso() {
        // Arrange
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setUsuarioId("user123");
        rating.setPeliculaId(10);
        rating.setEstrellas(5);
        rating.setComentario("Excelente película");

        LocalDateTime fechaActual = LocalDateTime.now();
        rating.setFecha(fechaActual);

        // Assert
        assertEquals(1L, rating.getId(), "El ID debe coincidir");
        assertEquals("user123", rating.getUsuarioId(), "El ID de usuario debe coincidir");
        assertEquals(10, rating.getPeliculaId(), "El ID de película debe coincidir");
        assertEquals(5, rating.getEstrellas(), "Las estrellas deben coincidir");
        assertEquals("Excelente película", rating.getComentario(), "El comentario debe coincidir");
        assertEquals(fechaActual, rating.getFecha(), "La fecha debe coincidir");
    }

    @Test
    @DisplayName("Verificar callbacks @PrePersist y @PreUpdate")
    void testLifeCycleCallbacks() {
        // Arrange
        Rating rating = new Rating();
        rating.setUsuarioId("user456");
        rating.setPeliculaId(20);
        rating.setEstrellas(4);

        // Act
        rating.onCreate();

        // Assert
        assertNotNull(rating.getCreadoEn(), "creadoEn no debe ser nulo tras onCreate");
        assertNotNull(rating.getActualizadoEn(), "actualizadoEn no debe ser nulo tras onCreate");
        assertNotNull(rating.getFecha(), "fecha debe establecerse por defecto si es nula");

        LocalDateTime prevUpdate = rating.getActualizadoEn();
        rating.onUpdate();
        assertNotNull(rating.getActualizadoEn());
    }
}
