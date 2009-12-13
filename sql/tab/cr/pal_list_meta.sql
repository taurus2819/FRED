CREATE TABLE Pal_List_Meta
(
Pal_List_ID	NUMBER(8),
Meta_ID	NUMBER(10),
PRIMARY KEY (Pal_List_ID, Meta_ID),
CONSTRAINT Pal_List_Meta_Record_FK FOREIGN KEY (Pal_List_ID) REFERENCES Pal_List (Pal_List_ID) ON DELETE CASCADE,
CONSTRAINT Pal_List_Meta_Meta_FK FOREIGN KEY (Meta_ID) REFERENCES Metacat.Meta_Cat (Meta_ID)
);

CREATE INDEX Pal_List_Meta_IDX1 ON Pal_List_Meta (Pal_List_ID) TABLESPACE Indx;
CREATE INDEX Pal_List_Meta_IDX2 ON Pal_List_Meta (Meta_ID) TABLESPACE Indx;