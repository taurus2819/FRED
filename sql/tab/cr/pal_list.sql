CREATE TABLE Pal_List
(
Pal_List_ID	NUMBER(8),
Record_ID	NUMBER(8),
Group_ID	NUMBER(4),
Taxa_ID		NUMBER(7),
Taxonomic_Name	VARCHAR2(128),
Specimen_Count	NUMBER(5),
Specimen_Coords	VARCHAR2(255),
Comments	VARCHAR2(1024),
PRIMARY KEY (Pal_List_ID),
CONSTRAINT Pal_List_FK1 FOREIGN KEY (Record_ID) REFERENCES Paleontology (Record_ID) ON DELETE CASCADE,
CONSTRAINT Pal_List_FK2 FOREIGN KEY (Group_ID) REFERENCES Lookup (Lookup_ID),
CONSTRAINT Pal_List_FK3 FOREIGN KEY (Taxa_ID) REFERENCES Taxonomic_Lookup (Taxa_ID)
);

CREATE INDEX Pal_List_IDX1 ON Pal_List (Record_ID) TABLESPACE Indx;
CREATE INDEX Pal_List_IDX2 ON Pal_List (Group_ID) TABLESPACE Indx;
CREATE INDEX Pal_List_IDX3 ON Pal_List (Taxa_ID) TABLESPACE Indx;