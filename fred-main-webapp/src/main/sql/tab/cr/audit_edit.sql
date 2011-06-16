CREATE TABLE Audit_Edit
(
Audit_Edit_ID	NUMBER(9),
Audit_ID	NUMBER(8),
Edited_By_ID	NUMBER(5),
Edited_Date	DATE,
Comments	VARCHAR2(255),
PRIMARY KEY (Audit_Edit_ID),
CONSTRAINT Audit_Edit_FK1 FOREIGN KEY (Audit_ID) REFERENCES Audit_Table (Audit_ID) ON DELETE CASCADE,
CONSTRAINT Audit_Edit_FK2 FOREIGN KEY (Edited_By_ID) REFERENCES IP.Person (PE_ID)
);

CREATE INDEX Audit_Edit_IDX1 ON Audit_Edit (Audit_ID) TABLESPACE Indx;
CREATE INDEX Audit_Edit_IDX2 ON Audit_Edit (Edited_By_ID) TABLESPACE Indx;