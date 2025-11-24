-- Script para corrigir a coluna 'ativo' na tabela app_user
-- Execute este script no seu banco de dados MySQL

-- Verifica se a coluna 'is_active' existe e remove
SET @exist := (SELECT COUNT(*)
               FROM information_schema.COLUMNS
               WHERE TABLE_SCHEMA = 'mydatabase'
               AND TABLE_NAME = 'app_user'
               AND COLUMN_NAME = 'is_active');

SET @sqlstmt := IF(@exist > 0,
                   'ALTER TABLE app_user DROP COLUMN is_active',
                   'SELECT ''Column is_active does not exist'' AS message');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verifica se a coluna 'ativo' existe, se não, cria
SET @exist := (SELECT COUNT(*)
               FROM information_schema.COLUMNS
               WHERE TABLE_SCHEMA = 'mydatabase'
               AND TABLE_NAME = 'app_user'
               AND COLUMN_NAME = 'ativo');

SET @sqlstmt := IF(@exist = 0,
                   'ALTER TABLE app_user ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE',
                   'SELECT ''Column ativo already exists'' AS message');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Atualiza registros existentes para ter ativo = true (caso a coluna tenha sido criada)
UPDATE app_user SET ativo = TRUE WHERE ativo IS NULL OR ativo = FALSE;

SELECT 'Migration completed successfully!' AS status;
