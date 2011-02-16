create table audit_confid
(
audit_id	number(8),
group_id	number(2),
primary key (audit_id, group_id),
constraint audit_confid_fk1 foreign key (audit_id) references audit_table (audit_id) on delete cascade,
constraint audit_confid_fk2 foreign key (group_id) references confidential_group (group_id)
);

create index audit_confid_idx1 on audit_confid (audit_id) tablespace indx;
create index audit_confid_idx2 on audit_confid (group_id) tablespace indx;