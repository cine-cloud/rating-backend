package com.unrn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingPromedioDTO {
    private Integer peliculaId;
    private Double promedio;
    private Long totalVotos;
}
