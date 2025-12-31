-- Agregar campo cedula a la tabla usuario
ALTER TABLE usuario 
ADD COLUMN IF NOT EXISTS cedula VARCHAR(20) NULL,
ADD COLUMN IF NOT EXISTS cedula_tipo VARCHAR(20) NULL DEFAULT 'NACIONAL';

-- Crear índice para búsquedas rápidas por cédula
CREATE INDEX IF NOT EXISTS idx_usuario_cedula ON usuario(cedula);

-- Hacer la cédula única cuando no sea NULL
CREATE UNIQUE INDEX IF NOT EXISTS idx_usuario_cedula_unique 
ON usuario(cedula) WHERE cedula IS NOT NULL;

-- Comentarios para documentación
COMMENT ON COLUMN usuario.cedula IS 'Número de cédula/documento de identidad del usuario';
COMMENT ON COLUMN usuario.cedula_tipo IS 'Tipo de documento: NACIONAL, PASAPORTE, EXTRANJERO, etc';
