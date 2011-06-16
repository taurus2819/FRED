create table confidential_group
(
group_id	number(2),
name	varchar2(32),
org_id	number(8),
owner_id	number(5),
primary key (group_id),
constraint confid_group_fk1 foreign key (org_id) references mis.client (cl_client_code),
constraint config_group_fk2 foreign key (owner_id) references ip.person (pe_id)
);

create index confid_group_idx1 on confidential_group (org_id) tablespace indx;
create index confid_group_idx2 on confidential_group (owner_id) tablespace indx;