package com.unrn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotoUsuarioStatusDTO {
    private Integer peliculaId;
    private String usuarioId;
    private boolean haVotado;
}
