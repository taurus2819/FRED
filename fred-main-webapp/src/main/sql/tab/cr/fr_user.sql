create table fr_user
(
pe_id number(7),
last_login date,
primary key (pe_id),
constraint fr_user_fk1 foreign key (pe_id) references ip.person (pe_id) on delete cascade
);