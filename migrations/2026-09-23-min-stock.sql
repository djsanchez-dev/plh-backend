-- Migración manual (ejecutar UNA sola vez sobre la base existente).
-- Hibernate con ddl-auto=update agrega columnas nuevas, pero este script deja
-- la columna con un valor por defecto correcto para las filas actuales.
--
--   psql -U plh_app -d plh_condominio -f migrations/2026-09-23-min-stock.sql

ALTER TABLE inventory_items
    ADD COLUMN IF NOT EXISTS min_stock INTEGER;

UPDATE inventory_items
SET min_stock = 0
WHERE min_stock IS NULL;

-- Opcional (cuando quieras exigir el valor):
-- ALTER TABLE inventory_items ALTER COLUMN min_stock SET NOT NULL;
