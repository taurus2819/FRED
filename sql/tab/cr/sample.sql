CREATE TABLE Sample
(
Sample_ID	NUMBER(7),
Feature_ID	NUMBER(7),
FR_ID		NUMBER(7),
Yard_FR_ID	NUMBER(7),
Top_Depth	NUMBER(7, 3),
Bottom_Depth	NUMBER(7, 3),
Depth_Unit VARCHAR2(2) CHECK (Depth_Unit IN ('m', 'ft')),
Drill_Type_ID	NUMBER(4),
Comments	VARCHAR2(255),
Audit_ID	NUMBER(8),
Collection_Date	DATE,
Date_Rounding	VARCHAR2(5) CHECK (Date_Rounding IN ('Year', 'Month')),
Strat_Unit	VARCHAR2(255),
In_Place	VARCHAR2(7) CHECK (In_Place IN ('Yes', 'No', 'Almost', 'Unknown')),
Not_Collected	VARCHAR2(128),
Significance	VARCHAR2(1024),
Inferred_Stage_ID	NUMBER(8),
Known_Stage_ID	NUMBER(8),
Column_Map	VARCHAR2(255),
Dip		NUMBER(2) CHECK (Dip <= 90),
Dip_Direction	VARCHAR2(2) CHECK (Dip_Direction IN ('N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW')),
Strike		NUMBER(3) CHECK (Strike <= 360),
Facing		VARCHAR2(10) CHECK (Facing IN ('Normal', 'Overturned')),
Strat_Comments VARCHAR2(512),
Primary_Grainsize_ID	NUMBER(4),
Secondary_Grainsize_ID	NUMBER(4),
Comparator_Used	VARCHAR2(1) CHECK (Comparator_Used IN ('Y', 'N')),
Bed_Thick_ID	NUMBER(4),
Primary_Bedding_ID	NUMBER(4),
Secondary_Bedding_ID	NUMBER(4),
Weathering_ID	NUMBER(4),
Hardness_ID	NUMBER(4),
Carbonate_ID	NUMBER(4),
Colour_Modifier_ID	NUMBER(4),
Primary_Colour_ID	NUMBER(4),
Secondary_Colour_ID	NUMBER(4),
Wet		VARCHAR2(3) CHECK (Wet IN ('Wet', 'Dry')),
Rock_Nature	VARCHAR2(512),
Deposition_Env	VARCHAR2(128),
Correspondence	VARCHAR2(512),
PRIMARY KEY (Sample_ID)
CONSTRAINT Sample_FK1 FOREIGN KEY (Feature_ID) REFERENCES Feature (Feature_ID) ON DELETE CASCADE,
CONSTRAINT Sample_FK2 FOREIGN KEY (FR_ID) REFERENCES FR_Number (FR_ID),
CONSTRAINT Sample_FK3 FOREIGN KEY (Yard_FR_ID) REFERENCES FR_Number (FR_ID),
CONSTRAINT Sample_FK4 FOREIGN KEY (Drill_Type_ID) REFERENCES Lookup (Lookup_ID),
CONSTRAINT Sample_FK5 FOREIGN KEY (Audit_ID) REFERENCES Audit_Table (Audit_ID),
CONSTRAINT Sample_FK6 FOREIGN KEY (Inferred_Stage_ID) REFERENCES Stage (Stage_ID),
CONSTRAINT Sample_FK7 FOREIGN KEY (Known_Stage_ID) REFERENCES Stage (Stage_ID),
CONSTRAINT Sample_FK8 FOREIGN KEY (Primary_Grainsize_ID) REFERENCES Lookup (Lookup_ID),
CONSTRAINT Sample_FK9 FOREIGN KEY (Secondary_Grainsize_ID) REFERENCES Lookup (lookup_ID),
CONSTRAINT Sample_FK10 FOREIGN KEY (Bed_Thick_ID) REFERENCES Lookup (lookup_ID),
CONSTRAINT Sample_FK11 FOREIGN KEY (Primary_Bedding_ID) REFERENCES Lookup (lookup_ID),
CONSTRAINT Sample_FK12 FOREIGN KEY (Secondary_Bedding_ID) REFERENCES Lookup (lookup_ID),
CONSTRAINT Sample_FK13 FOREIGN KEY (Weathering_ID) REFERENCES Lookup (lookup_ID),
CONSTRAINT Sample_FK14 FOREIGN KEY (Hardness_ID) REFERENCES Lookup (lookup_ID),
CONSTRAINT Sample_FK15 FOREIGN KEY (Carbonate_ID) REFERENCES Lookup (lookup_ID),
CONSTRAINT Sample_FK16 FOREIGN KEY (Colour_Modifier_ID) REFERENCES Lookup (lookup_ID),
CONSTRAINT Sample_FK17 FOREIGN KEY (Primary_Colour_ID) REFERENCES Lookup (lookup_ID),
CONSTRAINT Sample_FK18 FOREIGN KEY (Secondary_Colour_ID) REFERENCES Lookup (lookup_ID)
);

CREATE INDEX Sample_IDX1 ON Sample (Feature_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX2 ON Sample (FR_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX3 ON Sample (Yard_FR_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX4 ON Sample (Drill_Type_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX5 ON Sample (Audit_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX6 ON Sample (Inferred_Stage_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX7 ON Sample (Known_Stage_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX8 ON Sample (Primary_Grainsize_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX9 ON Sample (Secondary_Grainsize_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX10 ON Sample (Bed_Thick_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX11 ON Sample (Primary_Bedding_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX12 ON Sample (Secondary_Bedding_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX13 ON Sample (Weathering_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX14 ON Sample (Hardness_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX15 ON Sample (Carbonate_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX16 ON Sample (Colour_Modifier_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX17 ON Sample (Primary_Colour_ID) TABLESPACE Indx;
CREATE INDEX Sample_IDX18 ON Sample (Secondary_Colour_ID) TABLESPACE Indx;