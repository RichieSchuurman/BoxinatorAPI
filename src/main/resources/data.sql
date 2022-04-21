INSERT INTO account (
    id,
    first_name,
    last_name,
    email,
    date_of_birth,
    country_of_residence,
    postal_code,
    contact_number,
    account_type
    ) VALUES (
    'a881cded-b432-4f34-8e0c-8a2444a4c839',
    'James',
    'Gosling',
    'j.gosling@gmail.com',
    '1980-01-01',
    'Denmark',
    '1111AA',
    '+31612345678',
    'REGISTERED_USER'
);
INSERT INTO account (
    id,
    first_name,
    last_name,
    email,
    date_of_birth,
    country_of_residence,
    postal_code,
    contact_number,
    account_type
) VALUES (
    '9b13edfa-bc24-4303-9e79-953f22b65a30',
    'Boris',
    'Johnson',
    'gebruiker@gmail.com',
    '1940-12-12',
    'France',
    '3333CC',
    '+31687654321',
    'REGISTERED_USER'
);
INSERT INTO account (
    id,
    first_name,
    last_name,
    email,
    date_of_birth,
    country_of_residence,
    postal_code,
    contact_number,
    account_type
) VALUES (
    'a27fe992-d207-4a9c-a758-975df883c3fb',
    'Box',
    'Admin',
    'b.admin@gmail.com',
    '1991-11-06',
    'Norway',
    '2222BB',
    '+31611223344',
    'ADMINISTRATOR'
);

INSERT INTO country (name, multiplier) VALUES ('Netherlands', 4);
INSERT INTO country (name, multiplier) VALUES ('Germany', 5);
INSERT INTO country (name, multiplier) VALUES ('Belgium', 6);
INSERT INTO country (name, multiplier) VALUES ('France', 7);
INSERT INTO country (name, multiplier) VALUES ('Italy', 8);
INSERT INTO country (name, multiplier) VALUES ('America', 10);
INSERT INTO country (name, multiplier) VALUES ('Japan', 10);

INSERT INTO shipment(receiver_name, weight_option, box_color, shipment_status) VALUES (
    'Guido van Rossum',
    5,
    '#000000',
    'CREATED'
);
INSERT INTO shipment(receiver_name, weight_option, box_color, shipment_status) VALUES (
    'Bjarne Stroustrup',
    15,
    '#FFFFFF',
    'CREATED'
);
INSERT INTO shipment(receiver_name, weight_option, box_color, shipment_status) VALUES (
    'Brendan Eich',
    10,
    '#FFF4D2',
    'RECEIVED'
);
INSERT INTO shipment(receiver_name, weight_option, box_color, shipment_status) VALUES (
    'Linus Torvalds',
    8,
    '#0AF4E3',
    'COMPLETED'
);
INSERT INTO shipment(receiver_name, weight_option, box_color, shipment_status) VALUES (
    'Dennis Ritchie',
    3,
    '#C564B3',
    'COMPLETED'
);
INSERT INTO shipment(receiver_name, weight_option, box_color, shipment_status) VALUES (
    'Jordan Walke',
    13,
    '#C564B3',
    'CANCELED'
);

UPDATE shipment SET account_id = 'a881cded-b432-4f34-8e0c-8a2444a4c839', country_id = 1 WHERE id = 1;
UPDATE shipment SET account_id = 'a881cded-b432-4f34-8e0c-8a2444a4c839', country_id = 2 WHERE id = 2;
UPDATE shipment SET account_id = '9b13edfa-bc24-4303-9e79-953f22b65a30', country_id = 6 WHERE id = 3;
UPDATE shipment SET account_id = '9b13edfa-bc24-4303-9e79-953f22b65a30', country_id = 5 WHERE id = 4;
UPDATE shipment SET account_id = 'a881cded-b432-4f34-8e0c-8a2444a4c839', country_id = 2 WHERE id = 5;
UPDATE shipment SET account_id = '9b13edfa-bc24-4303-9e79-953f22b65a30', country_id = 4 WHERE id = 6;