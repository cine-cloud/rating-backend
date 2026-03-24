package com.unrn.controller.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RatingDTO {
    private Long id;
    private String usuarioId;
    private Integer peliculaId;
    private Integer estrellas;
    private String comentario;
    private LocalDateTime fecha;
}
