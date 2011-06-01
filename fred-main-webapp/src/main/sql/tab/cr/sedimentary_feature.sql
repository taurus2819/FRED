CREATE TABLE Sedimentary_Feature
(
Sample_ID	NUMBER(7),
Sed_Feature_ID	NUMBER(4),
Abundant	VARCHAR2(1) CHECK (Abundant IN ('Y', 'N')),
PRIMARY KEY (Sample_ID, Sed_Feature_ID),
CONSTRAINT Sedimentary_Feature_FK1 FOREIGN KEY (Sample_ID) REFERENCES Sample (Sample_ID) ON DELETE CASCADE,
CONSTRAINT Sedimentary_Feature_FK2 FOREIGN KEY (Sed_Feature_ID) REFERENCES Lookup (Lookup_ID)
);

CREATE INDEX Sedimentary_Feature_IDX1 ON Sedimentary_Feature (Sample_ID) TABLESPACE Indx;
CREATE INDEX Sedimentary_Feature_IDX2 ON Sedimentary_Feature (Sed_Feature_ID) TABLESPACE Indx;