-- =========================
-- USERS (EN PREMIER)
-- =========================
INSERT INTO USERS (email, password, first_name, last_name) 
VALUES ('test5@test.com', '$2a$12$7tWPGXjfWSRCj7fmKAcT0enQp7mDcLcTSg5rqf2NxxOjqVrL6yBhS', 'John', 'Smith');

INSERT INTO USERS (email, password, first_name, last_name) 
VALUES ('test6@test.com', '$2a$12$7tWPGXjfWSRCj7fmKAcT0enQp7mDcLcTSg5rqf2NxxOjqVrL6yBhS', 'Jane', 'Doe');

-- =========================
-- COMPANIES
-- =========================
INSERT INTO COMPANIES (name, country, city, address, industry, user_id) 
VALUES ('Google', 'Suisse', 'Genève', '1 California Way', 'Tech', 1);

INSERT INTO COMPANIES (name, country, city, address, industry, user_id) 
VALUES ('Microsoft', 'USA', 'Seattle', '1 Microsoft Way', 'Tech', 1);

INSERT INTO COMPANIES (name, country, city, address, industry, user_id) 
VALUES ('Apple', 'USA', 'Cupertino', '1 Apple Park Way', 'Tech', 1);

-- =========================
-- CONTACTS
-- =========================
INSERT INTO CONTACTS ( country, first_name, last_name, city, address, user_id, company_id) 
VALUES ( 'Suisse', 'John1', 'Doe1', 'Genève', 'Rue de Genève', 1, 1);

INSERT INTO CONTACTS ( country, first_name, last_name, city, address, user_id, company_id) 
VALUES ( 'Suisse', 'John2', 'Doe2', 'Genève', 'Rue de Genève', 1, 1); 

INSERT INTO CONTACTS ( country, first_name, last_name, city, address, user_id, company_id) 
VALUES ( 'France', 'John3', 'Doe3', 'Paris', 'Rue de France', 1, 1);

INSERT INTO CONTACTS ( country, first_name, last_name, city, address, user_id, company_id) 
VALUES ( 'USA', 'John4', 'Doe4', 'New York', 'Rue de New York', 2, 2);

-- =========================
-- CONTACT EMAILS
-- =========================
INSERT INTO CONTACT_EMAILS (email, type, is_primary, contact_id) 
VALUES ('test@live.fr', 'WORK', TRUE, 1);

INSERT INTO CONTACT_EMAILS (email, type, is_primary, contact_id) 
VALUES ('test2@live.fr', 'WORK', TRUE, 2);

INSERT INTO CONTACT_EMAILS (email, type, is_primary, contact_id) 
VALUES ('test3@live.fr', 'WORK', TRUE, 3);

INSERT INTO CONTACT_EMAILS (email, type, is_primary, contact_id) 
VALUES ('test4@live.fr', 'WORK', TRUE, 4);

-- =========================
-- CONTACT PHONES
-- =========================
INSERT INTO CONTACT_PHONES (phone, type, is_primary, contact_id) 
VALUES ('0117684965', 'WORK', TRUE, 1);

INSERT INTO CONTACT_PHONES (phone, type, is_primary, contact_id) 
VALUES ('0217684965', 'WORK', TRUE, 2);

INSERT INTO CONTACT_PHONES (phone, type, is_primary, contact_id) 
VALUES ('0317684965', 'WORK', TRUE, 3);

INSERT INTO CONTACT_PHONES (phone, type, is_primary, contact_id) 
VALUES ('0417684965', 'WORK', TRUE, 4);