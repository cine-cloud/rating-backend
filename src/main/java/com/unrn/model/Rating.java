package com.unrn.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "pelicula_id"})
})
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false, length = 100)
    private String usuarioId;

    @Column(name = "pelicula_id", nullable = false)
    private Integer peliculaId;

    @Column(nullable = false)
    private Integer estrellas;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
