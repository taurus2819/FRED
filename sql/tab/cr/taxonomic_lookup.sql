CREATE TABLE Taxonomic_Lookup
(
Taxa_ID	NUMBER(7) NOT NULL,
Group_ID	NUMBER(4) NOT NULL,
Taxonomic_Name	VARCHAR2(64) NOT NULL,
Author	VARCHAR2(64),
Status	VARCHAR2(1) NOT NULL,
Submitted_By_ID	NUMBER(7),
Submitted_Date	DATE,
Approved_By_ID	NUMBER(7),
Approved_Date	DATE,
Panelist_Comments VARCHAR2(255),
Send_Message	VARCHAR2(1),
PRIMARY KEY (Taxa_ID),
CONSTRAINT Taxonomic_Lookup_FK1 FOREIGN KEY (Group_ID) REFERENCES Lookup (Lookup_ID),
CONSTRAINT Taxonomic_Lookup_FK2 FOREIGN KEY (Submitted_By_ID) REFERENCES IP.Person (PE_ID),
CONSTRAINT Taxonomic_Lookup_FK3 FOREIGN KEY (Approved_By_ID) REFERENCES IP.Person (PE_ID)
);

CREATE INDEX Taxa_Lookup_IDX1 ON Taxonomic_Lookup (Group_ID) TABLESPACE Indx;
CREATE INDEX Taxa_Lookup_IDX2 ON Taxonomic_Lookup (Status) TABLESPACE Indx;
CREATE INDEX Taxa_Lookup_IDX3 ON Taxonomic_Lookup (Submitted_By_ID) TABLESPACE Indx;
CREATE INDEX Taxa_Lookup_IDX4 ON Taxonomic_Lookup (Approved_By_ID) TABLESPACE Indx;
CREATE INDEX Taxa_Lookup_IDX5 ON Taxonomic_Lookup (Send_Message) TABLESPACE Indx;
CREATE INDEX Taxa_Lookup_IDX6 ON Taxonomic_Lookup (Taxonomic_Name) TABLESPACE Indx;
