UPDATE confidential_group SET owner_id=NULL;

ALTER TABLE confidential_group DROP CONSTRAINT confid_group_fk2;
ALTER TABLE confidential_group DROP COLUMN owner_id;