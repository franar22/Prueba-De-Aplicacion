-- =============================================
-- Script de inicialización de la base de datos
-- Se ejecuta automáticamente la primera vez que
-- el contenedor MySQL arranca.
-- =============================================

CREATE DATABASE IF NOT EXISTS tasksdb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tasksdb;

-- Tabla principal de tareas
CREATE TABLE IF NOT EXISTS tasks (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    status      ENUM('PENDIENTE', 'EN_PROGRESO', 'COMPLETADA') NOT NULL DEFAULT 'PENDIENTE',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Datos de ejemplo para probar la API
INSERT INTO tasks (title, description, status) VALUES
    ('Configurar VPC en AWS',       'Crear la VPC 10.0.0.0/16 con subredes pública y privada', 'EN_PROGRESO'),
    ('Crear Security Groups',       'Configurar reglas de entrada/salida para Front, Back y Data', 'PENDIENTE'),
    ('Desplegar instancias EC2',    'Lanzar las 3 instancias con sus respectivos roles', 'PENDIENTE'),
    ('Instalar Docker en EC2',      'sudo yum install docker -y && sudo systemctl start docker', 'PENDIENTE'),
    ('Configurar NAT Gateway',      'Asociar Elastic IP y configurar route table privada', 'PENDIENTE');

-- Usuario de aplicación con solo los permisos necesarios
-- (el usuario se crea desde las variables de entorno MYSQL_USER/MYSQL_PASSWORD)
-- Si quieres crear un usuario adicional de solo lectura:
-- CREATE USER 'readonly'@'%' IDENTIFIED BY 'readpass';
-- GRANT SELECT ON tasksdb.* TO 'readonly'@'%';
-- FLUSH PRIVILEGES;
