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

-- =========================
-- OPPORTUNITIES (dates relative to today so the dashboard has data)
-- =========================
INSERT INTO OPPORTUNITIES (name, business_name, opportunity_value, status, close_date, contact_id)
VALUES ('Website redesign', 'Google', 12000, 'WON', DATEADD('DAY', -3, CURRENT_DATE), 1);

INSERT INTO OPPORTUNITIES (name, business_name, opportunity_value, status, close_date, contact_id)
VALUES ('SEO audit', 'Microsoft', 4500, 'WON', DATEADD('DAY', -20, CURRENT_DATE), 2);

INSERT INTO OPPORTUNITIES (name, business_name, opportunity_value, status, close_date, contact_id)
VALUES ('Mobile app', 'Apple', 30000, 'WON', DATEADD('DAY', -75, CURRENT_DATE), 3);

INSERT INTO OPPORTUNITIES (name, business_name, opportunity_value, status, close_date, contact_id)
VALUES ('Brand workshop', 'Google', 2500, 'LOST', DATEADD('DAY', -40, CURRENT_DATE), 1);

INSERT INTO OPPORTUNITIES (name, business_name, opportunity_value, status, close_date, contact_id)
VALUES ('Maintenance contract', 'Microsoft', 8000, 'OPEN', NULL, 2);

-- =========================
-- INTERACTIONS
-- =========================
INSERT INTO INTERACTIONS (type, occurred_on, note, contact_id, opportunity_id, user_id)
VALUES ('CALL', DATEADD('DAY', -1, CURRENT_DATE), 'Kick-off call for the redesign', 1, 1, 1);

INSERT INTO INTERACTIONS (type, occurred_on, note, contact_id, opportunity_id, user_id)
VALUES ('EMAIL', DATEADD('DAY', -4, CURRENT_DATE), 'Sent the signed quote', 1, 1, 1);

INSERT INTO INTERACTIONS (type, occurred_on, note, contact_id, opportunity_id, user_id)
VALUES ('MEETING', DATEADD('DAY', -9, CURRENT_DATE), NULL, 2, 5, 1);

INSERT INTO INTERACTIONS (type, occurred_on, note, contact_id, opportunity_id, user_id)
VALUES ('MESSAGE', DATEADD('DAY', -22, CURRENT_DATE), NULL, 3, NULL, 1);

INSERT INTO INTERACTIONS (type, occurred_on, note, contact_id, opportunity_id, user_id)
VALUES ('CALL', DATEADD('DAY', -50, CURRENT_DATE), NULL, 2, NULL, 1);
