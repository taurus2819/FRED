CREATE TABLE age
(
age_id	number(3),
name	varchar2(64) NOT NULL,
code	varchar2(16) NOT NULL,
period	varchar2(16),
base_age	number(7,3) NOT NULL,
top_age		number(7,3) NOT NULL,
obsolete_flag	number(1),
PRIMARY KEY (age_id)
);

CREATE INDEX age_idx1 ON age (code) TABLESPACE indx;
CREATE INDEX age_idx2 ON age (base_age) TABLESPACE indx;
CREATE INDEX age_idx3 ON age (top_age) TABLESPACE indx;