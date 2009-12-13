CREATE TABLE Taxa_Panel
(
Group_ID	NUMBER(4),
Panelist_ID	NUMBER(7),
PRIMARY KEY (Group_ID, Panelist_ID),
CONSTRAINT Taxa_Panel_FK1 FOREIGN KEY (Group_ID) REFERENCES Lookup (Lookup_ID),
CONSTRAINT Taxa_Panel_FK2 FOREIGN KEY (Panelist_ID) REFERENCES IP.Person (PE_ID)
);

CREATE INDEX Taxa_Panel_IDX1 ON Taxa_Panel (Group_ID) TABLESPACE Indx;
CREATE INDEX Taxa_Panel_IDX2 ON Taxa_Panel (Panelist_ID) TABLESPACE Indx;