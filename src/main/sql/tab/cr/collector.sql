CREATE TABLE Collector
(
Sample_ID	NUMBER(7),
Person_ID	NUMBER(5),
PRIMARY KEY (Sample_ID, Person_ID),
CONSTRAINT Collector_FK1 FOREIGN KEY (Sample_ID) REFERENCES Sample (Sample_ID) ON DELETE CASCADE,
CONSTRAINT Collector_FK2 FOREIGN KEY (Person_ID) REFERENCES Person (Person_ID)
);

CREATE INDEX Collector_IDX1 ON Collector (Sample_ID) TABLESPACE Indx;
CREATE INDEX Collector_IDX2 ON Collector (Person_ID) TABLESPACE Indx;