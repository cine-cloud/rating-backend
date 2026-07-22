package com.unrn.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private Long id;

    @NotBlank(message = "El usuarioId no puede estar vacío")
    private String usuarioId;

    @NotNull(message = "El peliculaId no puede ser nulo")
    private Integer peliculaId;

    @NotNull(message = "La cantidad de estrellas es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1 estrella")
    @Max(value = 5, message = "La calificación máxima es 5 estrellas")
    private Integer estrellas;

    private String comentario;

    private LocalDateTime fecha;
}
