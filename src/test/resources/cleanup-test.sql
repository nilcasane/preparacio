-- Clean up all tables in correct order (child tables first, parent tables last)
DELETE FROM medication_prescription;
DELETE FROM visit;
DELETE FROM treatment;

-- Admin-role join table and administrators (depend on person)
DELETE FROM administrator_roles;
DELETE FROM administrator;

-- Delete join table linking pet owners and pets before deleting pets/owners
DELETE FROM pet_owner_pet;
DELETE FROM pet_owner;

DELETE FROM loyalty_tier;
DELETE FROM discount;
DELETE FROM promotion;
DELETE FROM low_stock_alert;
DELETE FROM medication_batch;
DELETE FROM medication_incompatibility;
DELETE FROM medication;
DELETE FROM pet;
DELETE FROM availability_exception;
DELETE FROM availability;
DELETE FROM veterinarian;
DELETE FROM role;
DELETE FROM person;

-- Reset all auto-increment sequences (identity columns)
ALTER TABLE person ALTER COLUMN id RESTART WITH 1;
ALTER TABLE role ALTER COLUMN id RESTART WITH 1;
ALTER TABLE availability ALTER COLUMN id RESTART WITH 1;
ALTER TABLE pet ALTER COLUMN id RESTART WITH 1;
ALTER TABLE medication ALTER COLUMN id RESTART WITH 1;
ALTER TABLE medication_batch ALTER COLUMN id RESTART WITH 1;
ALTER TABLE visit ALTER COLUMN id RESTART WITH 1;
ALTER TABLE medication_prescription ALTER COLUMN id RESTART WITH 1;
ALTER TABLE low_stock_alert ALTER COLUMN id RESTART WITH 1;
ALTER TABLE availability_exception ALTER COLUMN id RESTART WITH 1;
ALTER TABLE promotion ALTER COLUMN id RESTART WITH 1;
ALTER TABLE discount ALTER COLUMN id RESTART WITH 1;
ALTER TABLE loyalty_tier ALTER COLUMN id RESTART WITH 1;
