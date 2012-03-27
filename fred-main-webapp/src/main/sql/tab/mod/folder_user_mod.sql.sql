-- Amended 28 March 2012
alter table folder_user  
modify folder_id number(7,0);  -- references FOLDER.FOLDER_ID
commit;
