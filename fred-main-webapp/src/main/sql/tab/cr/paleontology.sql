CREATE TABLE Paleontology
(
Record_ID	NUMBER(8),
Identification_Date	DATE,
Date_Rounding	VARCHAR2(5) CHECK (Date_Rounding IN ('Year', 'Month')),
Stage_ID	NUMBER(8),
Stage_Comments	VARCHAR2(1024),
Lab_Section_ID	NUMBER(4),
Lab_Number	VARCHAR2(16),
List_Security_Class_ID	NUMBER(5),
Collection_Comments	VARCHAR2(1536),
PRIMARY KEY (Record_ID),
CONSTRAINT Paleontology_FK1 FOREIGN KEY (Record_ID) REFERENCES Record (Record_ID) ON DELETE CASCADE,
CONSTRAINT Paleontology_FK3 FOREIGN KEY (Stage_ID) REFERENCES Stage (Stage_ID),
CONSTRAINT Paleontology_FK4 FOREIGN KEY (Lab_Section_ID) REFERENCES Lab_Section (Lab_Section_ID),
CONSTRAINT Paleontology_FK5 FOREIGN KEY (List_Security_Class_ID) REFERNECES IP.Security_Class (SC_ID)
);

CREATE INDEX Paleontology_IDX2 ON Paleontology (Stage_ID) TABLESPACE Indx;
CREATE INDEX Paleontology_IDX3 ON Paleontology (Lab_Section_ID) TABLESPACE Indx;
CREATE INDEX Paleontology_IDX4 ON Paleontology (List_Security_Class_ID) TABLESPACE Indx;