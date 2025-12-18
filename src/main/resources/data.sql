INSERT INTO role (name)
VALUES ('RECEPTIONIST'),
       ('CLINIC_MANAGER');

INSERT INTO person (username, name, last_name, phone_number, email, address, password)
VALUES  ('rec', 'Alice', 'Smith', 123456789, 'alice@rec.com', '123 Main St', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
        ('vet','Bob', 'Johnson', 987654321, 'bob@vet.com', '456 Elm St', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
        ('manager','Manager', 'Manager', 987654321, 'manager@manager.com', '456 Elm St', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
        ('petowner', 'Nilga', 'Brown', 555666777, 'nilga@petowner.com', '6789 Oak St', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
        ('petowner2', 'Pere', 'Sisset', 676767676, 'peresisset@petowner.com', '101 Pine St', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2');

INSERT INTO administrator (person_id) VALUES (1), (3);

INSERT INTO administrator_roles(administrator_id, role_id)
VALUES (1, 1),
       (3, 2);

INSERT INTO veterinarian (person_id, license_number, years_of_experience)
VALUES (2, 67890, 10);

INSERT INTO medication (name, active_ingredient, dosage_unit, unit_price, reorder_threshold)
VALUES ('GLEKGLEK', 'Formol', 250, 25.99, 50),
       ('JosephJoseph', 'Cloro', 150, 10.99, 100);

INSERT INTO pet (name, date_of_birth, gender, breed, weight, microchip_number)
VALUES ('Ngong', '2005-01-03', 'MALE', 'PitbullMal', 30.5, 123456789),
       ('Ptusi', '2020-09-10', 'MALE', 'German Shepperd', 20, 987654321);

INSERT INTO pet_owner (person_id)
VALUES (4),
       (5);

insert into pet_owner_pet (pet_owner_id, pet_id) VALUES
(5, 1),
(4, 2);

INSERT INTO availability (day_of_week, start_time, end_time, period_start, period_end, veterinarian_id)
VALUES (1, '09:00:00', '17:00:00', '2025-01-01', '2025-12-31', 2),
       (2, '09:00:00', '17:00:00', '2025-01-01', '2025-12-31', 2),
       (3, '09:00:00', '17:00:00', '2025-01-01', '2025-12-31', 2),
       (4, '09:00:00', '17:00:00', '2025-01-01', '2025-12-31', 2),
       (5, '09:00:00', '17:00:00', '2025-01-01', '2025-12-31', 2);
