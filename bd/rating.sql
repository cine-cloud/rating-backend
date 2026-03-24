-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS rating_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE rating_db;

-- Tabla RATINGS (votos y comentarios)
CREATE TABLE IF NOT EXISTS ratings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id VARCHAR(100) NOT NULL,
  pelicula_id INT NOT NULL,
  estrellas INT NOT NULL CHECK (estrellas BETWEEN 1 AND 5),
  comentario TEXT,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE(usuario_id, pelicula_id) -- Un cliente no puede votar dos veces la misma película
);

-- Índices
CREATE INDEX idx_ratings_pelicula ON ratings(pelicula_id);
CREATE INDEX idx_ratings_usuario ON ratings(usuario_id);
