-- INSERT INTO account (
--     first_name,
--     last_name,
--     email,
--     date_of_birth,
--     country_of_residence,
--     postal_code,
--     contact_number,
--     account_type
--     ) VALUES (
--     'James',
--     'Gosling',
--     'j.gosling@gmail.com',
--     '1980-01-01',
--     'Denmark',
--     '1111AA',
--     '+31612345678',
--     'REGISTERED_USER'
-- );

INSERT INTO country (name, multiplier) VALUES ('Netherlands', 4);
INSERT INTO country (name, multiplier) VALUES ('Germany', 5);
INSERT INTO country (name, multiplier) VALUES ('Belgium', 6);
INSERT INTO country (name, multiplier) VALUES ('France', 7);
INSERT INTO country (name, multiplier) VALUES ('Italy', 8);
INSERT INTO country (name, multiplier) VALUES ('America', 10);
INSERT INTO country (name, multiplier) VALUES ('Japan', 10);

INSERT INTO shipment(
    receiver_name,
    weight_option,
    box_color,
    shipment_status
    ) VALUES (
    'Guido van Rossum',
    5,
    '#000000',
    'CREATED'
);