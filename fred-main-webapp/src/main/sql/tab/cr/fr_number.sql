CREATE TABLE FR_Number
(
FR_ID		NUMBER(7),
Map_Sheet	VARCHAR2(7) NOT NULL,
Serial_Number	NUMBER(5) NOT NULL,
Recollection_Number	VARCHAR2(2),
FR_Number	VARCHAR2(15),
FRNum_Comments	VARCHAR2(255),
Obsolete	VARCHAR2(1),
PRIMARY KEY (FR_ID)
);

CREATE INDEX FR_Number_IDX1 ON FR_Number (Map_Sheet) TABLESPACE Indx;
CREATE INDEX FR_Number_IDX2 ON FR_Number (FR_Number) TABLESPACE Indx;

GRANT REFERENCES ON fr_number TO paleo;