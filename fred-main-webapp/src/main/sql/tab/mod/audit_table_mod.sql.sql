--Amended 28 march 2012
alter table audit_table 
modify working_folder_id number(7,0); -- references FOLDER.FOLDER_ID
commit;