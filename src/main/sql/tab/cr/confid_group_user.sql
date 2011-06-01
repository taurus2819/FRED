create table confid_group_user
(
group_id	number(8),
user_id	number(7),
primary key (group_id, user_id),
constraint confid_group_user_fk1 foreign key (group_id) references confidential_group (group_id),
constraint confid_group_user_fk2 foreign key (user_id) references ip.person (pe_id)
);

create index confid_group_user_idx1 on confid_group_user (group_id) tablespace indx;
create index confid_group_user_idx2 on confid_group_user (user_id) tablespace indx;