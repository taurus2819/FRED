CREATE TABLE Feature
(
Feature_ID	NUMBER(7),
Site_ID		NUMBER(7),
Audit_ID	NUMBER(8) NOT NULL,
Masterfile_ID	NUMBER(3),
Fr_ID	NUMBER(7),
Yard_Fr_ID	NUMBER(7),
Feature_Type	VARCHAR2(26) NOT NULL CHECK (Feature_Type IN ('Outcrop', 'Drillhole', 'Vertical Section')),
Feature_Name	VARCHAR2(64),
Locality	VARCHAR2(1024),
Orig_System_ID	NUMBER(3),
Orig_Coord	VARCHAR2(32),
Map_Year	NUMBER(4),
Coord_Comments VARCHAR2(255),
Drillhole_Licence_Name	VARCHAR2(64),
Start_Date	DATE,
Start_Date_Rounding	VARCHAR2(5) CHECK (Start_Date_Rounding IN ('Year', 'Month')),
Finish_Date	DATE,
Finish_Date_Rounding	VARCHAR2(5) CHECK (Finish_Date_Rounding IN ('Year', 'Month')),
Person_ID	NUMBER(5),
Datum_Type	VARCHAR2(8),
Datum_Elevation	NUMBER(6,2),
Start_Depth	NUMBER(5,2),
Finish_Depth	NUMBER(5,2),
Depth_Unit VARCHAR2(2) CHECK (Depth_Unit IN ('m', 'ft')),
Reg_Area_ID	NUMBER(4),
Comments	VARCHAR2(255),
PRIMARY KEY (Feature_ID),
CONSTRAINT Feature_FK1 FOREIGN KEY (Site_ID) REFERENCES SC.Site (Site_ID),
CONSTRAINT Feature_FK2 FOREIGN KEY (Audit_ID) REFERENCES Audit_Table (Audit_ID),
CONSTRAINT Feature_FK3 FOREIGN KEY (Masterfile_ID) REFERENCES Folder (Folder_ID),
CONSTRAINT Feature_FK4 FOREIGN KEY (Reg_Area_ID) REFERENCES Lookup (Lookup_ID),
CONSTRAINT Feature_FK5 FOREIGN KEY (Person_ID) REFERENCES Person (Person_ID),
CONSTRAINT Feature_FK6 FOREIGN KEY (Orig_System_ID) REFERENCES SC.Orig_system (System_ID),
CONSTRAINT Feature_FK7 FOREIGN KEY (Fr_ID) REFERENCES FR_Number (Fr_ID),
CONSTRAINT Feature_FK8 FOREIGN KEY (Yard_Fr_ID) REFERENCES FR_Number (Fr_ID),
CONSTRAINT Feature_Datum_Chk CHECK (Datum_Type IN ('RT', 'KB', 'Seafloor', 'Top' ,'Bottom'))
);

CREATE INDEX Feature_IDX1 ON Feature (Site_ID) TABLESPACE Indx;
CREATE INDEX Feature_IDX2 ON Feature (Audit_ID) TABLESPACE Indx;
CREATE INDEX Feature_IDX3 ON Feature (Masterfile_ID) TABLESPACE Indx;
CREATE INDEX Feature_IDX4 ON Feature (Feature_Name) TABLESPACE Indx;
CREATE INDEX Feature_IDX5 ON Feature (Reg_Area_ID) TABLESPACE Indx;
CREATE INDEX Feature_IDX6 ON Feature (Person_ID) TABLESPACE Indx;
CREATE INDEX Feature_IDX7 ON Feature (Orig_System_ID) TABLESPACE Indx;
CREATE INDEX Feature_IDX8 ON Feature (Fr_ID) TABLESPACE Indx;
CREATE INDEX Feature_IDX9 ON Feature (Yard_Fr_ID) TABLESPACE Indx;