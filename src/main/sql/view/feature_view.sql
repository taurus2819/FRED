CREATE OR REPLACE VIEW feature_view
AS
SELECT f.*, decode(fr.fr_number, NULL, decode(f.feature_name, NULL, 'unnamed locality', f.feature_name), fr.fr_number) as feature_identifying_name,
 fr.fr_number, fr.map_sheet, fr.serial_number, fr.recollection_number, fr2.fr_number AS yard_fr_number,  
 fr2.map_sheet AS yard_map_sheet, fr2.serial_number AS yard_serial_number, fr2.recollection_number AS yard_recollection_number,
 s.sample_id, f.feature_name as sample_name,
 s.fr_id as sample_fr_id, fr3.fr_number as sample_fr_number, fr3.map_sheet as sample_map_sheet, fr3.serial_number as sample_serial_number,
 fr3.recollection_number as sample_recollection_number, s.yard_fr_id as s_yard_fr_id, fr4.fr_number AS s_yard_fr_number,  
 fr4.map_sheet AS s_yard_map_sheet, fr4.serial_number AS s_yard_serial_number, fr4.recollection_number AS s_yard_recollection_number,
 a.status AS feature_status, a.working_folder_id as feature_working_folder_id, fd.name AS masterfile_name,
 a2.status as sample_status, a2.working_folder_id as sample_working_folder_id, a2.security_class_id as sample_security_class_id
FROM feature f, sample s, fr_number fr, fr_number fr2, sc.site_view st, folder fd, audit_table a, fr_number fr3, fr_number fr4, audit_table a2
WHERE f.feature_id = s.feature_id AND f.fr_id = fr.fr_id(+) AND f.yard_fr_id = fr2.fr_id(+) AND f.site_id = st.site_id(+) AND f.audit_id = a.audit_id
 AND s.audit_id = a2.audit_id AND f.masterfile_id = fd.folder_id(+) AND s.fr_id = fr3.fr_id(+) AND s.yard_fr_id = fr4.fr_id(+);