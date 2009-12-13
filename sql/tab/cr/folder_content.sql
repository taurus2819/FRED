CREATE TABLE Folder_Content
(
Folder_ID	NUMBER(4),
Feature_ID	NUMBER(7),
PRIMARY KEY (Folder_ID, Feature_ID),
CONSTRAINT Folder_Content_FK1 FOREIGN KEY (Folder_ID) REFERENCES Folder (Folder_ID),
CONSTRAINT Folder_Content_FK2 FOREIGN KEY (Feature_ID) REFERENCES Feature (Feature_ID) ON DELETE CASCADE
);

CREATE INDEX Folder_Content_IDX1 ON Folder_Content (Folder_ID) TABLESPACE Indx;
CREATE INDEX Folder_Content_IDX2 ON Folder_Content (Feature_ID) TABLESPACE Indx;