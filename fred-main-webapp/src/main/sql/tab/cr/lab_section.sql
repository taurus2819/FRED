CREATE TABLE Lab_Section
(
Lab_Section_ID	NUMBER(3),
Lab_ID		NUMBER,
Name		VARCHAR2(64),
Code		VARCHAR2(8),
Closed		VARCHAR2(1),
PRIMARY KEY (Lab_Section_ID),
CONSTRAINT Lab_Section_FK1 FOREIGN KEY (Lab_ID) REFERENCES SC.Lab (Lab_ID)
);

CREATE INDEX Lab_Section_IDX1 ON Lab_Section (Lab_ID) TABLESPACE Indx;