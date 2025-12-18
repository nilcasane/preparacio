insert into person (username, name, last_name, phone_number, email, address, password)
values ('alice', 'Alice', 'Smith', 123456789, 'alice@vet.com', '123 Main St', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('bob', 'Bob', 'Johnson', 987654321, 'bob@vet.com', '456 Elm St', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('carol', 'Carol', 'Jones', 111222333, 'carol@vet.com', '789 Pine St', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('dave', 'Dave', 'Miller', 222333444, 'dave@owner.com', '10 Owner St', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('eva', 'Eva', 'Davis', 333444555, 'eva@owner.com', '20 Owner Ave', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('frank', 'Frank', 'Wilson', 444555666, 'frank@owner.com', '30 Owner Blvd', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2');

insert into veterinarian (person_id, license_number, years_of_experience)
values (1, 12345, 5),
       (2, 67890, 10),
       (3, 11111, 2);

insert into availability (day_of_week, start_time, end_time, period_start, period_end, veterinarian_id)
values (1, '09:00', '12:00', '2025-01-01', '2025-12-31', 1),
       (2, '10:00', '14:00', '2025-02-01', '2025-11-30', 1),
       (3, '08:00', '11:00', '2025-03-01', '2025-10-31', 2);

-- Insert pet_owner rows (PrimaryKeyJoinColumn uses person_id)
insert into pet_owner (person_id)
values (4), (5), (6);

-- Test data for pets
insert into pet (name, date_of_birth, gender, breed, weight, microchip_number)
values ('Max', '2020-05-15', 'Male', 'Golden Retriever', 30.5, 123456789012345),
       ('Luna', '2021-03-20', 'Female', 'Siamese Cat', 4.2, 987654321098765),
       ('Rocky', '2019-11-10', 'Male', 'German Shepherd', 35.0, 555666777888999);

-- Link pets to owners via join table
insert into pet_owner_pet (pet_owner_id, pet_id)
values (4, 1), (5, 2), (6, 3);

-- Test data for medications
insert into medication (name, active_ingredient, dosage_unit, unit_price, reorder_threshold)
values ('Amoxicillin', 'Amoxicillin', 500, 5.50, 100),
       ('Carprofen', 'Carprofen', 100, 8.75, 50),
       ('Prednisolone', 'Prednisolone', 5, 3.25, 75),
       ('MedA', 'IngA', 500, 1.50, 50),
       ('MedB', 'IngB', 250, 2.00, 10);

-- Test data for medication batches
insert into medication_batch (medication_id, lot_number, received_date, expiry_date, initial_quantity, current_quantity, purchase_price_per_unit)
values (1, 123456789, '2025-01-15', '2026-12-31', 200, 200, 5.00),
       (2, 987654321, '2025-02-20', '2026-06-30', 150, 150, 8.00),
       (3, 555666777, '2025-03-10', '2025-12-31', 100, 100, 3.00),
       (4, 1001, '2025-01-01', '2026-01-01', 100, 5, 10.00),
       (5, 2001, '2025-02-01', '2026-02-01', 100, 20, 15.00);

-- Test data for visits (use pet_owner ids 4..6)
insert into visit (visit_date, visit_time, duration, reason, pricer_per_fifteen, status, veterinarian_id, pet_id, pet_owner_id)
values ('2025-10-27', '09:30', 30, 'Annual checkup', 25.00, 'SCHEDULED', 1, 1, 4),
       ('2025-10-28', '10:00', 15, 'Vaccination', 20.00, 'COMPLETED', 1, 2, 5),
       ('2025-10-29', '08:30', 45, 'Surgery consultation', 30.00, 'IN_PROGRESS', 2, 3, 6);
