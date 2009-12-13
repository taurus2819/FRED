CREATE TABLE Feature_Meta
(
Feature_ID	NUMBER(7),
Meta_ID	NUMBER(10),
PRIMARY KEY (Feature_ID, Meta_ID),
CONSTRAINT Feature_Meta_Record_FK FOREIGN KEY (Feature_ID) REFERENCES Feature (Feature_ID) ON DELETE CASCADE,
CONSTRAINT Feature_Meta_Meta_FK FOREIGN KEY (Meta_ID) REFERENCES Metacat.Meta_Cat (Meta_ID)
);

CREATE INDEX Feature_Meta_IDX1 ON Feature_Meta (Feature_ID) TABLESPACE Indx;
CREATE INDEX Feature_Meta_IDX2 ON Feature_Meta (Meta_ID) TABLESPACE Indx;