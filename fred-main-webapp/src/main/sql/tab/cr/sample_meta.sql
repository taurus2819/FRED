CREATE TABLE Sample_Meta
(
Sample_ID	NUMBER(7),
Meta_ID	NUMBER(10),
PRIMARY KEY (Sample_ID, Meta_ID),
CONSTRAINT Sample_Meta_Record_FK FOREIGN KEY (Sample_ID) REFERENCES Sample (Sample_ID) ON DELETE CASCADE,
CONSTRAINT Sample_Meta_Meta_FK FOREIGN KEY (Meta_ID) REFERENCES Metacat.Meta_Cat (Meta_ID)
);

CREATE INDEX Sample_Meta_IDX1 ON Sample_Meta (Sample_ID) TABLESPACE Indx;
CREATE INDEX Sample_Meta_IDX2 ON Sample_Meta (Meta_ID) TABLESPACE Indx;