-- Migración manual: propiedades físicas con QR (plh-backend/migrations/)
-- Ejecutar DESPUÉS de arrancar el backend nuevo (Hibernate crea la tabla `properties`):
--   psql -U plh_app -d plh_condominio -f migrations/2026-09-24-properties-qr.sql

insert into properties (id, qr_code, lot, owner_id, created_at)
select gen_random_uuid(), gen_random_uuid(), btrim(u.property), u.id, now()
from app_users u
where u.role = 'OWNER'
  and u.property is not null
  and btrim(u.property) <> ''
  and not exists (select 1 from properties p where p.owner_id = u.id);
