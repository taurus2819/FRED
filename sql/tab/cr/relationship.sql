CREATE TABLE Relationship
(
Relationship_ID	NUMBER(8),
Record_ID	NUMBER(8) NOT NULL,
Relationship_Type	VARCHAR2(6) NOT NULL CHECK (Relationship_Type IN ('Sample', 'Strat')),
Related_Feature_ID	NUMBER(7),
Strat_Unit	VARCHAR2(255),
STRAT_UNIT_ID number(5) references SL.STRAT_UNIT.SU_ID 
Distance	NUMBER(8,3),
Distance_Range	NUMBER(8,3),
Distance_Mod	VARCHAR2(2) CHECK (Distance_Mod IN 'c.', '?'),
Relation_Type_ID	NUMBER(4) NOT NULL,
PRIMARY KEY (Relationship_ID),
CONSTRAINT Relationship_FK1 FOREIGN KEY (Sample_ID) REFERENCES Sample (Sample_ID) ON DELETE CASCADE,
CONSTRAINT Relationship_FK2 FOREIGN KEY (Related_Feature_ID) REFERENCES Feature (Feature_ID),
CONSTRAINT Relationship_FK3 FOREIGN KEY (Relation_Type_ID) REFERENCES Lookup (Lookup_ID),
CONSTRAINT Relationship_CK1 CHECK (Related_Feature_ID IS NOT NULL OR Strat_Unit IS NOT NULL)
);

CREATE INDEX Relationship_IDX1 ON Relationship (Sample_ID) TABLESPACE Indx;
CREATE INDEX Relationship_IDX2 ON Relationship (Related_Feature_ID) TABLESPACE Indx;
CREATE INDEX Relationship_IDX3 ON Relationship (Relation_Type_ID) TABLESPACE Indx;