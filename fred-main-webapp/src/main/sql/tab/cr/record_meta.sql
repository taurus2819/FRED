CREATE TABLE Record_Meta
(
Record_ID	NUMBER(8),
Meta_ID	NUMBER(10),
PRIMARY KEY (Record_ID, Meta_ID),
CONSTRAINT Record_Meta_Record_FK FOREIGN KEY (Record_ID) REFERENCES Record (Record_ID) ON DELETE CASCADE,
CONSTRAINT Record_Meta_Meta_FK FOREIGN KEY (Meta_ID) REFERENCES Metacat.Meta_Cat (Meta_ID)
);

CREATE INDEX Record_Meta_IDX1 ON Record_Meta (Record_ID) TABLESPACE Indx;
CREATE INDEX Record_Meta_IDX2 ON Record_Meta (Meta_ID) TABLESPACE Indx;