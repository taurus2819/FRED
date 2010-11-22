CREATE TABLE confid_group_owners (
	group_id NUMBER(8,0)
,	user_id NUMBER(7,0)
,	PRIMARY KEY (group_id, user_id)
,	CONSTRAINT fk_confid_group_owners_group FOREIGN KEY (group_id) REFERENCES confidential_group(group_id)
,	CONSTRAINT fk_confid_group_owners_user FOREIGN KEY (user_id) REFERENCES ip.person(pe_id)
);