
whenever oserror exit SQL.SQLCODE rollback;
whenever sqlerror exit SQL.SQLCODE rollback;

insert into audit_edit (audit_id, comments, edited_by_id, edited_date)
values (254603, 'Deleted as per J Simes request', 2432, sysdate);

-- Remove the last sample
delete from sample where sample_id = 149682;

-- Remove the old feature
delete from feature where feature_id = 92094;

-- For re-inserting the feature
/*
insert into feature(audit_id, comments, coord_comments, datum_elevation, datum_type, depth_unit, drillhole_licence_name, feature_id,
  feature_name, feature_type,field_number, finish_date,finish_date_rounding, finish_depth, fr_id, locality,
  map_year, masterfile_id, orig_coord, orig_system_id, person_id, reg_area_id, site_id, start_date, start_date_rounding, start_depth, yard_fr_id) 
values('254602','','','','','m','PEP 38114','92094',
'Moki-1','Drillhole','','01/12/83','','2620','154991',
'Final Location Approximately 15 metres ESE From Intended Location-39 58 9.96 S 173 18 42.40 E 
Moki-1 was the first well drilled under New Zealand Petroleum Prospecting Licence 38114. 
Moki-1 was spudded on 24 October 1983 at SP1540 on seismic line TNZ81-618A, using Atlantic Drilling''s semi-submersible - Benreoch ',
'','11','-39.96943|173.31177','29','10536','419','265949','01/10/83','','0','');
--*/
-- For re-inserting the sample
/*
insert into sample(sample_id, feature_id, audit_id, collection_date, date_rounding, strat_unit, in_place, not_collected, significance, depth_unit)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
values('149682','92094','254603','01/01/00','Year','','Almost','','','m');   
--*/

--rollback;
commit;



