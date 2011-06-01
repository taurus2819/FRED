CREATE TABLE Lookup
(
Lookup_ID	NUMBER(4),
Name		VARCHAR2(32) NOT NULL,
Code		VARCHAR2(8),
Description	VARCHAR2(255),
FieldName	VARCHAR2(64),
PRIMARY KEY (Lookup_ID)
);

CREATE INDEX Lookup_IDX1 ON Lookup (FieldName) TABLESPACE Indx;