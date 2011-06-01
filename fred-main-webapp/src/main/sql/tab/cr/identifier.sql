CREATE TABLE Identifier
(
Record_ID	NUMBER(7),
Person_ID	NUMBER(5),
PRIMARY KEY (Record_ID, Person_ID),
CONSTRAINT Identifier_FK1 FOREIGN KEY (Record_ID) REFERENCES Paleontology (Record_ID) ON DELETE CASCADE,
CONSTRAINT Identifier_FK2 FOREIGN KEY (Person_ID) REFERENCES Person (Person_ID)
);

CREATE INDEX Identifier_IDX1 ON Identifier (Record_ID) TABLESPACE Indx;
CREATE INDEX Identifier_IDX2 ON Identifier (Person_ID) TABLESPACE Indx;