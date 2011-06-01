CREATE TABLE Audit_Table
(
Audit_ID	NUMBER(8),
Status		VARCHAR2(16) DEFAULT 'working' CHECK (Status IN ('working', 'waiting', 'approved', 'rejected')) NOT NULL,
Data_Origin_ID	NUMBER(4) DEFAULT 908,
Created_By_ID	NUMBER(5),
Created_Date	DATE,
Submitted_By_ID	NUMBER(5),
Submitted_Date	DATE,
Approved_By_ID	NUMBER(5),
Approved_Date	DATE,
Send_Message	VARCHAR2(1),
Working_Comments	VARCHAR2(255),
Working_Folder_ID	NUMBER(4),
Curator_Comments	VARCHAR2(255),
Confidential_Flag NUMBER(1),
Confid_Period NUMBER(2,1),
Confid_Lapse_Date DATE,
Confid_Email_Flag NUMBER(1),
Confid_Lapse_Email	VARCHAR2(32),
PRIMARY KEY (Audit_ID),
CONSTRAINT Audit_Table_FK1 FOREIGN KEY (Data_Origin_ID) REFERENCES Lookup (Lookup_ID),
CONSTRAINT Audit_Table_FK2 FOREIGN KEY (Created_By_ID) REFERENCES IP.Person (PE_ID),
CONSTRAINT Audit_Table_FK4 FOREIGN KEY (Submitted_By_ID) REFERENCES IP.Person (PE_ID),
CONSTRAINT Audit_Table_FK5 FOREIGN KEY (Approved_By_ID) REFERENCES IP.Person (PE_ID),
CONSTRAINT Audit_Table_FK6 FOREIGN KEY (Working_Folder_ID) REFERENCES Folder (Folder_ID)
);

CREATE INDEX Audit_Table_IDX1 ON Audit_Table (Status) TABLESPACE Indx;
CREATE INDEX Audit_Table_IDX2 ON Audit_Table (Data_Origin_ID) TABLESPACE Indx;
CREATE INDEX Audit_Table_IDX3 ON Audit_Table (Created_By_ID) TABLESPACE Indx;
CREATE INDEX Audit_Table_IDX5 ON Audit_Table (Submitted_By_ID) TABLESPACE Indx;
CREATE INDEX Audit_Table_IDX6 ON Audit_Table (Approved_By_ID) TABLESPACE Indx;
CREATE INDEX Audit_Table_IDX7 ON Audit_Table (Working_Folder_ID) TABLESPACE Indx;
CREATE INDEX Audit_Table_IDX8 ON Audit_Table (Send_Message) TABLESPACE Indx;
CREATE INDEX Audit_Table_IDX9 ON Audit_Table (Approved_Date) TABLESPACE Indx;
CREATE INDEX Audit_Table_IDX11 ON Audit_Table (Confidential_Flag) TABLESPACE Indx;