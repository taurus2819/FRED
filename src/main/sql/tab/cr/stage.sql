CREATE TABLE Stage
(
Stage_ID	NUMBER(8),
age_Lower_ID	NUMBER(5),
Stage_Lower_Mod	VARCHAR2(1) CHECK (Stage_Lower_Mod = '?'),
age_Upper_ID	NUMBER(5),
Stage_Upper_Mod	VARCHAR2(1) CHECK (Stage_Upper_Mod = '?'),
base_age number(7,3),
top_age number(7,3),
PRIMARY KEY (Stage_ID),
CONSTRAINT Stage_FK1 FOREIGN KEY (age_Lower_ID) REFERENCES age (age_id),
CONSTRAINT Stage_FK2 FOREIGN KEY (age_Upper_ID) REFERENCES age (age_id)
);

CREATE INDEX Stage_IDX1 ON Stage (age_Lower_ID) TABLESPACE Indx;
CREATE INDEX Stage_IDX2 ON Stage (age_Upper_ID) TABLESPACE Indx;