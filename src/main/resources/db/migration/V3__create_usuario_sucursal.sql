-- Crear tabla para manejar usuarios en múltiples sucursales
-- Esto permite que un usuario pueda cambiar de gimnasio/sucursal manteniendo su historial
CREATE TABLE IF NOT EXISTS usuario_sucursal (
    id_usuario_sucursal BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    id_sucursal BIGINT NOT NULL REFERENCES sucursal(id_sucursal) ON DELETE CASCADE,
    fecha_vinculacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    notas TEXT,
    
    -- Un usuario no puede estar vinculado dos veces a la misma sucursal
    UNIQUE(id_usuario, id_sucursal)
);

-- Índices para mejorar performance en consultas
CREATE INDEX IF NOT EXISTS idx_usuario_sucursal_usuario ON usuario_sucursal(id_usuario);
CREATE INDEX IF NOT EXISTS idx_usuario_sucursal_sucursal ON usuario_sucursal(id_sucursal);
CREATE INDEX IF NOT EXISTS idx_usuario_sucursal_activo ON usuario_sucursal(activo);

-- Comentarios para documentación
COMMENT ON TABLE usuario_sucursal IS 'Relación entre usuarios y sucursales para permitir migración entre gimnasios';
COMMENT ON COLUMN usuario_sucursal.fecha_vinculacion IS 'Fecha en que el usuario se vinculó a esta sucursal';
COMMENT ON COLUMN usuario_sucursal.activo IS 'Indica si la vinculación está activa (útil para historial)';
COMMENT ON COLUMN usuario_sucursal.notas IS 'Notas sobre la vinculación (ej: motivo de cambio de sucursal)';
