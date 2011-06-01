create table log_table
(
log_id number(8),
log_type varchar2(16),
log_date date,
user_id number(5),
locality_count number(5),
primary key (log_id),
constraint log_table_fk1 foreign key (user_id) references ip.person (pe_id)
);