-- ─────────────────────────────────────────────────────────────────────────────
-- Script de inicialización de datos base para Oracle DB — Minimarket
-- Sistema Minimarket | Desarrollo Backend II (PBY2202) | Semana 1
--
-- IMPORTANTE: Los hashes BCrypt se generaron con BCryptPasswordEncoder(12).
-- Para generar nuevos hashes en Java:
--   String hash = new BCryptPasswordEncoder(12).encode("tuPassword");
-- ─────────────────────────────────────────────────────────────────────────────

-- ── Crear tabla ROL (si no existe) ───────────────────────────────────────────
-- Nota: Hibernate crea las tablas con ddl-auto=update.
-- Este script solo inserta datos iniciales.

-- ── Insertar roles base ───────────────────────────────────────────────────────
-- Spring Security requiere el prefijo ROLE_ en los nombres de rol
MERGE INTO ROL (ID, NOMBRE) KEY (ID)
    VALUES (1, 'ROLE_GERENTE');
MERGE INTO ROL (ID, NOMBRE) KEY (ID)
    VALUES (2, 'ROLE_EMPLEADO');
MERGE INTO ROL (ID, NOMBRE) KEY (ID)
    VALUES (3, 'ROLE_CLIENTE');

-- ── Insertar usuarios de prueba ───────────────────────────────────────────────
-- Contraseña: gerente123  → BCrypt strength=12
MERGE INTO USUARIO (ID, USERNAME, PASSWORD) KEY (ID)
    VALUES (1, 'gerente',
    '$2a$12$5K.lXe0vB4gPTKB9b9XB..w1yHaXpFMfEaKy5DJwnfBUXXrXb5iFa');

-- Contraseña: empleado123 → BCrypt strength=12
MERGE INTO USUARIO (ID, USERNAME, PASSWORD) KEY (ID)
    VALUES (2, 'empleado1',
    '$2a$12$Xq9mZdL2KwbT3yF7nR0ceu1EbBnPhCtDL5DJtnqDLf1kWhUjLR1S6');

-- Contraseña: cliente123  → BCrypt strength=12
MERGE INTO USUARIO (ID, USERNAME, PASSWORD) KEY (ID)
    VALUES (3, 'cliente1',
    '$2a$12$DPQvBFnNXr1aH7sF4kL9duOmKvTP3B9lGRJMaKqB0bNZHZ8fmPsTe');

-- ── Asignar roles a usuarios ──────────────────────────────────────────────────
MERGE INTO USUARIO_ROLES (USUARIO_ID, ROL_ID) KEY (USUARIO_ID, ROL_ID)
    VALUES (1, 1);  -- gerente   → ROLE_GERENTE
MERGE INTO USUARIO_ROLES (USUARIO_ID, ROL_ID) KEY (USUARIO_ID, ROL_ID)
    VALUES (2, 2);  -- empleado1 → ROLE_EMPLEADO
MERGE INTO USUARIO_ROLES (USUARIO_ID, ROL_ID) KEY (USUARIO_ID, ROL_ID)
    VALUES (3, 3);  -- cliente1  → ROLE_CLIENTE
