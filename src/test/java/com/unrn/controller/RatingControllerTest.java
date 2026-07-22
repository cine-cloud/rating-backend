package com.unrn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unrn.dto.RatingDTO;
import com.unrn.dto.RatingPromedioDTO;
import com.unrn.dto.VotoUsuarioStatusDTO;
import com.unrn.exception.GlobalExceptionHandler;
import com.unrn.exception.ResourceNotFoundException;
import com.unrn.services.RatingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@Import(GlobalExceptionHandler.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/ratings/pelicula/{peliculaId} - Retorna lista de valoraciones")
    void testObtenerPorPelicula() throws Exception {
        // Arrange
        RatingDTO dto = new RatingDTO();
        dto.setId(1L);
        dto.setUsuarioId("user1");
        dto.setPeliculaId(10);
        dto.setEstrellas(5);
        dto.setComentario("Muy buena");

        when(ratingService.obtenerPorPelicula(10)).thenReturn(List.of(dto));

        // Act & Assert
        mockMvc.perform(get("/api/ratings/pelicula/{peliculaId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value("user1"))
                .andExpect(jsonPath("$[0].estrellas").value(5));
    }

    @Test
    @DisplayName("GET /api/ratings/pelicula/{peliculaId}/promedio - Retorna rating promedio y conteo")
    void testObtenerPromedioPorPelicula() throws Exception {
        // Arrange
        RatingPromedioDTO promedioDTO = new RatingPromedioDTO(10, 4.5, 8L);
        when(ratingService.obtenerPromedioPorPelicula(10)).thenReturn(promedioDTO);

        // Act & Assert
        mockMvc.perform(get("/api/ratings/pelicula/{peliculaId}/promedio", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peliculaId").value(10))
                .andExpect(jsonPath("$.promedio").value(4.5))
                .andExpect(jsonPath("$.totalVotos").value(8));
    }

    @Test
    @DisplayName("GET /api/ratings/pelicula/{peliculaId}/usuario/{usuarioId}/voto - Retorna voto del usuario")
    void testObtenerVotoUsuario() throws Exception {
        // Arrange
        RatingDTO dto = new RatingDTO();
        dto.setId(5L);
        dto.setUsuarioId("user_abc");
        dto.setPeliculaId(10);
        dto.setEstrellas(4);

        when(ratingService.obtenerVotoUsuario(10, "user_abc")).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/api/ratings/pelicula/10/usuario/user_abc/voto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.usuarioId").value("user_abc"))
                .andExpect(jsonPath("$.estrellas").value(4));
    }

    @Test
    @DisplayName("GET /api/ratings/pelicula/{peliculaId}/usuario/{usuarioId}/ha-votado - Retorna estado de voto")
    void testHaVotadoUsuario() throws Exception {
        // Arrange
        VotoUsuarioStatusDTO statusDTO = new VotoUsuarioStatusDTO(10, "user_abc", true);
        when(ratingService.haVotadoUsuario(10, "user_abc")).thenReturn(statusDTO);

        // Act & Assert
        mockMvc.perform(get("/api/ratings/pelicula/10/usuario/user_abc/ha-votado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.haVotado").value(true));
    }

    @Test
    @DisplayName("POST /api/ratings - Registra un voto válido correctamente (201 CREATED)")
    void testVotarExitoso() throws Exception {
        // Arrange
        RatingDTO inputDto = new RatingDTO();
        inputDto.setUsuarioId("user1");
        inputDto.setPeliculaId(10);
        inputDto.setEstrellas(5);
        inputDto.setComentario("Excelente");

        RatingDTO outputDto = new RatingDTO();
        outputDto.setId(1L);
        outputDto.setUsuarioId("user1");
        outputDto.setPeliculaId(10);
        outputDto.setEstrellas(5);
        outputDto.setComentario("Excelente");

        when(ratingService.votar(any(RatingDTO.class))).thenReturn(outputDto);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value("user1"))
                .andExpect(jsonPath("$.estrellas").value(5));
    }

    @Test
    @DisplayName("POST /api/ratings - Lanza 400 Bad Request al enviar datos inválidos (Bean Validation)")
    void testVotarInvalidoLanzaBadRequest() throws Exception {
        // Arrange: estrellas fuera de rango (6) y usuarioId vacío
        RatingDTO inputDto = new RatingDTO();
        inputDto.setUsuarioId("");
        inputDto.setPeliculaId(10);
        inputDto.setEstrellas(6);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    @DisplayName("POST /api/ratings - Lanza 409 Conflict si el usuario ya votó")
    void testVotarDuplicadoLanzaConflict() throws Exception {
        // Arrange
        RatingDTO inputDto = new RatingDTO();
        inputDto.setUsuarioId("user1");
        inputDto.setPeliculaId(10);
        inputDto.setEstrellas(4);

        when(ratingService.votar(any(RatingDTO.class)))
                .thenThrow(new IllegalStateException("El cliente ya ha votado esta película"));

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("El cliente ya ha votado esta película"));
    }

    @Test
    @DisplayName("DELETE /api/ratings/{id} - Elimina correctamente (204 NO_CONTENT)")
    void testEliminarExitoso() throws Exception {
        // Arrange
        doNothing().when(ratingService).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/ratings/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(ratingService, times(1)).eliminar(1L);
    }
}
