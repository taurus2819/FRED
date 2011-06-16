CREATE TABLE Adoption
(
Record_ID	NUMBER(8),
Adoption_Date	DATE,
Date_Rounding	VARCHAR2(5) CHECK (Date_Rounding IN ('Year', 'Month')),
Adopted_Stage_ID	NUMBER(8),
Comments	VARCHAR2(512),
PRIMARY KEY (Record_ID),
CONSTRAINT Adoption_FK1 FOREIGN KEY (Record_ID) REFERENCES Record (Record_ID) ON DELETE CASCADE,
CONSTRAINT Adoption_FK3 FOREIGN KEY (Adopted_Stage_ID) REFERENCES Stage (Stage_ID)
);

CREATE INDEX Adoption_IDX2 ON Adoption (Adopted_Stage_ID) TABLESPACE Indx;