-- Amended 28 March 2012
alter table feature  
modify masterfile_id number(7,0); -- references FOLDER.FOLDER_ID
commit;