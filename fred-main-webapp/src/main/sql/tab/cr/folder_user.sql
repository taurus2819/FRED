CREATE TABLE Folder_User
(
fu_id NUMBER(5),
Folder_ID	NUMBER(4),
User_ID		NUMBER(5),
User_Rights	NUMBER(3) DEFAULT 1,
PRIMARY KEY (fu_id),
CONSTRAINT Folder_User_FK1 FOREIGN KEY (Folder_ID) REFERENCES Folder (Folder_ID) ON DELETE CASCADE,
CONSTRAINT Folder_User_FK2 FOREIGN KEY (User_ID) REFERENCES IP.Person (PE_ID)
);

CREATE INDEX Folder_User_IDX1 ON Folder_User (Folder_ID) TABLESPACE Indx;
CREATE INDEX Folder_User_IDX2 ON Folder_User (User_ID) TABLESPACE Indx;