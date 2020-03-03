-- We don't have access to the MIS schema in an embedded database.
create table lu_country (country_code varchar2(10), country_name varchar2(80));

insert into lu_country (country_code, country_name) values ('NZ', 'New Zealand');
insert into lu_country (country_code, country_name) values ('AQ', 'Antarctica');
