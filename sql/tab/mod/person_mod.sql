ALTER TABLE person ADD (
	name VARCHAR2(64)
);

UPDATE person SET name = family_name || decode(given_name, null, '', ', ' || given_name);


ALTER TABLE person DROP (
	given_name,
	family_name
);

ALTER TABLE person MODIFY name NOT NULL;
CREATE INDEX person_IDX1 ON PERSON (name) TABLESPACE INDX;

CREATE OR REPLACE VIEW Person_View AS
SELECT Person_ID, '' AS Given_Name, Name AS Family_Name, Name AS Name
FROM Person;