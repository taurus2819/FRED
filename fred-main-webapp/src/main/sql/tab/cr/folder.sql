CREATE TABLE Folder
(
Folder_ID	NUMBER(4),
Name		VARCHAR2(32) NOT NULL,
Folder_Type	VARCHAR2(8) DEFAULT 'personal' CHECK (Folder_Type IN ('personal', 'admin')),
Owner_ID	NUMBER(5),,
PRIMARY KEY (Folder_ID),
CONSTRAINT Folder_FK1 FOREIGN KEY (Owner_ID) REFERENCES IP.Person (PE_ID)
);

CREATE INDEX Folder_IDX1 ON Folder (Owner_ID) TABLESPACE Indx;