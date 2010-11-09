ALTER TABLE confidential_group ADD (
	owner_id NUMBER(5,0)
);

ALTER TABLE confidential_group ADD CONSTRAINT 
	confid_group_fk2 FOREIGN KEY (owner_id) REFERENCES ip.person(pe_id);