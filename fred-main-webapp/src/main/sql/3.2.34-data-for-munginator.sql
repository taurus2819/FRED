-- temp_fk holds foreign keys until the end of this script. Then it's dropped.

create table temp_fk (from_table varchar(400), from_column varchar(400), to_table varchar(400), to_column varchar(400), unique (from_table, from_column, to_table, to_column));

/*
drop table lu_cardinal;
drop table lu_coord_system;
drop table lu_date_rounding;
drop table lu_depth_unit;
drop table lu_facing;
drop table lu_in_place;
drop table lu_wet;
drop table lu_yesno;
*/

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'ADOPTION', 'ADOPTION');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'ADOPTOR', 'ADOPTOR');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'AGE', 'AGE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'AUDIT_CONFID', 'AUDIT_CONFID');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'AUDIT_EDIT', 'AUDIT_EDIT');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'AUDIT_TABLE', 'AUDIT_TABLE');

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'BAD_RECORDS', 'BAD_RECORDS');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'BASIS_PAL_LIST', 'BASIS_PAL_LIST');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'BEDDING', 'BEDDING');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'BED_THICKNESS', 'BED_THICKNESS');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'BEGG_FEATURE', 'BEGG_FEATURE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'BORE_FRED', 'BORE_FRED');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'CARBONATE', 'CARBONATE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'COLLECTOR', 'COLLECTOR');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'COLOUR_MODIFIER', 'COLOUR_MODIFIER');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'CONFIDENTIAL_GROUP', 'CONFIDENTIAL_GROUP');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'CONFID_GROUP_OWNERS', 'CONFID_GROUP_OWNERS');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'CONFID_GROUP_USER', 'CONFID_GROUP_USER');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'DATA_ORIGIN', 'DATA_ORIGIN');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'DEFAULT_IDENTIFICATION_DATE', 'DEFAULT_IDENTIFICATION_DATE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'DOUBLE_TAXA', 'DOUBLE_TAXA');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'DRILL_TYPE', 'DRILL_TYPE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'DUPS', 'DUPS');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FEATURE', 'FEATURE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FEATURE_META', 'FEATURE_META');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FOLDER', 'FOLDER');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FOLDER_CONTENT', 'FOLDER_CONTENT');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FOLDER_RIGHT', 'FOLDER_RIGHT');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FOLDER_TYPE', 'FOLDER_TYPE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FOLDER_USER', 'FOLDER_USER');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FOSSIL_GROUP', 'FOSSIL_GROUP');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FR', 'FR');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FR_NUMBER', 'FR_NUMBER');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'FR_USER', 'FR_USER');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'GRAIN_SIZE', 'GRAIN_SIZE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'HARDNESS', 'HARDNESS');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'IDENTIFIER', 'IDENTIFIER');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LAB_SECTION', 'LAB_SECTION');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LOG_TABLE', 'LOG_TABLE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LOOKUP', 'LOOKUP');

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'PALEONTOLOGY', 'PALEONTOLOGY');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'PAL_LIST', 'PAL_LIST');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'PAL_LIST_META', 'PAL_LIST_META');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'PERSON', 'PERSON');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'RECORD', 'RECORD');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'RECORD_META', 'RECORD_META');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'REGISTRATION_AREA', 'REGISTRATION_AREA');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'RELATIONSHIP', 'RELATIONSHIP');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'RELATIONSHIP_TYPE', 'RELATIONSHIP_TYPE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'RELATION_TYPE', 'RELATION_TYPE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'ROCK_COLOUR', 'ROCK_COLOUR');

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SAMPLE', 'SAMPLE');

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SAMPLE_META', 'SAMPLE_META');

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SECURITY_CLASS', 'SECURITY_CLASS');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SEDIMENTARY_FEATURE', 'SEDIMENTARY_FEATURE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SEDIMENTARY_FEATURE_TYPE', 'SEDIMENTARY_FEATURE_TYPE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SENT_TO', 'SENT_TO');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SIMEDELS', 'SIMEDELS');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'STAGE', 'STAGE');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'TAXA_PANEL', 'TAXA_PANEL');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'TAXONOMIC_GROUP', 'TAXONOMIC_GROUP');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'TAXONOMIC_LOOKUP', 'TAXONOMIC_LOOKUP');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'TAXONOMIC_SYNONYM', 'TAXONOMIC_SYNONYM');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'TEMP_LOB', 'TEMP_LOB');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'TEMP_YARD_REFS', 'TEMP_YARD_REFS');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'USER_LIST', 'USER_LIST');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'WEATHERING', 'WEATHERING');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_COORD_SYSTEM', 'Coordinate System');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SC.ORIG_SYSTEM', 'Original Coordinate System');
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SC.LAB', 'Lab');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ADOPTION'), 6, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ADOPTION'), 3, 'ADOPTION_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ADOPTION'), 3, 'DATE_ROUNDING');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ADOPTION'), 6, 'ADOPTED_STAGE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ADOPTION'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ADOPTOR'), 6, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ADOPTOR'), 6, 'PERSON_ID');
--insert into mg_column (id, mg_table, mg_column_type, code, SEQUENCE) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 9, 'AGE_ID', 'AGE_SEQ');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 1, 'AGE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 3, 'PERIOD');
insert into mg_column (id, mg_table, mg_column_type, code, order_by_rank) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 2, 'BASE_AGE', 1);
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 2, 'TOP_AGE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 1, 'OBSOLETE_FLAG');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AGE'), 1, 'DUPLICATE_FLAG');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_CONFID'), 6, 'AUDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_CONFID'), 6, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_EDIT'), 1, 'AUDIT_EDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_EDIT'), 6, 'AUDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_EDIT'), 3, 'EDITED_BY_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_EDIT'), 3, 'EDITED_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_EDIT'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code, sequence, is_primary_key) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 9, 'AUDIT_ID', 'AUDIT_SEQ', 1);
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 3, 'STATUS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 6, 'DATA_ORIGIN_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 1, 'CREATED_BY_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 4, 'CREATED_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 1, 'SUBMITTED_BY_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 4, 'SUBMITTED_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 1, 'APPROVED_BY_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 4, 'APPROVED_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 3, 'SEND_MESSAGE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 3, 'WORKING_COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 6, 'WORKING_FOLDER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 3, 'CURATOR_COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 1, 'SECURITY_CLASS_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 5, 'CONFIDENTIAL_FLAG');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 1, 'CONFID_PERIOD');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 4, 'CONFID_LAPSE_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 3, 'CONFID_LAPSE_EMAIL');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 5, 'CONFID_EMAIL_FLAG');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='AUDIT_TABLE'), 5, 'DONT_DELETE_FLAG');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BAD_RECORDS'), 1, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BASIS_PAL_LIST'), 1, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BASIS_PAL_LIST'), 1, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BASIS_PAL_LIST'), 3, 'TAXONOMIC_NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BASIS_PAL_LIST'), 3, 'PKEY');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BASIS_PAL_LIST'), 3, 'AUTHOR');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BASIS_PAL_LIST'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BASIS_PAL_LIST'), 1, 'TAXA_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BEDDING'), 1, 'BEDDING_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BEDDING'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BEDDING'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BED_THICKNESS'), 1, 'THICKNESS_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BED_THICKNESS'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BED_THICKNESS'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BEGG_FEATURE'), 3, 'FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BORE_FRED'), 6, 'FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='BORE_FRED'), 3, 'BH_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CARBONATE'), 3, 'CARBONATE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CARBONATE'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CARBONATE'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='COLLECTOR'), 6, 'PERSON_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='COLLECTOR'), 6, 'SAMPLE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='COLOUR_MODIFIER'), 1, 'MODIFIER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='COLOUR_MODIFIER'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='COLOUR_MODIFIER'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CONFIDENTIAL_GROUP'), 1, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CONFIDENTIAL_GROUP'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CONFIDENTIAL_GROUP'), 1, 'ORG_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CONFID_GROUP_OWNERS'), 6, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CONFID_GROUP_OWNERS'), 1, 'USER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CONFID_GROUP_USER'), 6, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='CONFID_GROUP_USER'), 3, 'USER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DATA_ORIGIN'), 1, 'ORIGIN_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DATA_ORIGIN'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DATA_ORIGIN'), 3, 'DESCRIPTION');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DEFAULT_IDENTIFICATION_DATE'), 1, 'PERSON_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DEFAULT_IDENTIFICATION_DATE'), 4, 'IDENTIFICATION_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DOUBLE_TAXA'), 1, 'TAXA_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DOUBLE_TAXA'), 3, 'TAXONOMIC_NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DOUBLE_TAXA'), 1, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DRILL_TYPE'), 1, 'DRILL_TYPE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DRILL_TYPE'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DUPS'), 3, 'OLD_STAGE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DUPS'), 3, 'NEW_STAGE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='DUPS'), 3, 'NAME');

insert into mg_column (id, mg_table, mg_column_type, code, is_primary_key, sequence) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 9, 'FEATURE_ID', 1, 'FEATURE_SEQ');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 1, 'SITE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 6, 'AUDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 6, 'MASTERFILE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'FIELD_NUMBER');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'LOCALITY');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 6, 'REG_AREA_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'FEATURE_TYPE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'FEATURE_NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'DRILLHOLE_LICENCE_NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 4, 'START_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'START_DATE_ROUNDING');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 4, 'FINISH_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'FINISH_DATE_ROUNDING');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 6, 'PERSON_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'DATUM_TYPE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 2, 'DATUM_ELEVATION');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 2, 'START_DEPTH');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 2, 'FINISH_DEPTH');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 6, 'ORIG_SYSTEM_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'ORIG_COORD');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'MAP_YEAR');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'COORD_COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 7, 'FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 6, 'YARD_FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'DEPTH_UNIT');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE_META'), 6, 'FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE_META'), 1, 'META_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER'), 1, 'FOLDER_ID');
insert into mg_column (id, mg_table, mg_column_type, code, order_by_rank) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER'), 3, 'NAME', 1);
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER'), 1, 'OWNER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER'), 6, 'FOLDER_TYPE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_CONTENT'), 6, 'FOLDER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_CONTENT'), 6, 'FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_RIGHT'), 1, 'RIGHT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_RIGHT'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_RIGHT'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_TYPE'), 1, 'FOLDER_TYPE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_TYPE'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_USER'), 6, 'FOLDER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_USER'), 1, 'USER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_USER'), 3, 'USER_RIGHTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER_USER'), 1, 'FU_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOSSIL_GROUP'), 1, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOSSIL_GROUP'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR'), 3, 'FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code, is_primary_key, sequence) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 9, 'FR_ID', 1, 'FR_SEQ');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'MAP_SHEET');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 1, 'SERIAL_NUMBER');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'RECOLLECTION_NUMBER');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'FRNUM_COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'FR_NUMBER');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'OBSOLETE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 1, 'DELETE_FLAG');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_USER'), 1, 'PE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_USER'), 3, 'LAST_LOGIN');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='GRAIN_SIZE'), 1, 'GRAIN_SIZE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='GRAIN_SIZE'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='GRAIN_SIZE'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='HARDNESS'), 1, 'HARDNESS_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='HARDNESS'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='HARDNESS'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='IDENTIFIER'), 6, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='IDENTIFIER'), 6, 'PERSON_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LAB_SECTION'), 1, 'LAB_SECTION_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LAB_SECTION'), 1, 'LAB_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LAB_SECTION'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LAB_SECTION'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LAB_SECTION'), 3, 'CLOSED');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOG_TABLE'), 1, 'LOG_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOG_TABLE'), 3, 'LOG_TYPE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOG_TABLE'), 4, 'LOG_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOG_TABLE'), 3, 'USER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOG_TABLE'), 3, 'LOCALITY_COUNT');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOOKUP'), 1, 'LOOKUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOOKUP'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOOKUP'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOOKUP'), 3, 'DESCRIPTION');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LOOKUP'), 3, 'FIELDNAME');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PALEONTOLOGY'), 6, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PALEONTOLOGY'), 3, 'IDENTIFICATION_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PALEONTOLOGY'), 3, 'DATE_ROUNDING');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PALEONTOLOGY'), 6, 'STAGE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PALEONTOLOGY'), 3, 'STAGE_COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PALEONTOLOGY'), 6, 'LAB_SECTION_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PALEONTOLOGY'), 3, 'LAB_NUMBER');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PALEONTOLOGY'), 3, 'COLLECTION_COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PALEONTOLOGY'), 3, 'PAL_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST'), 1, 'PAL_LIST_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST'), 6, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST'), 6, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST'), 6, 'TAXA_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST'), 3, 'TAXONOMIC_NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST'), 3, 'SPECIMEN_COUNT');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST'), 3, 'SPECIMEN_COORDS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST_META'), 6, 'PAL_LIST_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PAL_LIST_META'), 3, 'META_ID');
insert into mg_column (id, mg_table, mg_column_type, code, is_primary_key, sequence) values (mg_column_id_seq.nextval, (select id from mg_table where code='PERSON'), 1, 'PERSON_ID', 1, 'PERSON_SEQ');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PERSON'), 3, 'ST_CODE');
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='PERSON'), 3, 'NAME', 1);
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD'), 1, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD'), 6, 'SAMPLE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD'), 6, 'AUDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD'), 6, 'PAL_LIST_AUDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD_META'), 6, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD_META'), 1, 'META_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='REGISTRATION_AREA'), 1, 'REG_AREA_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='REGISTRATION_AREA'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='REGISTRATION_AREA'), 3, 'CODE');


insert into mg_column (id, mg_table, mg_column_type, code, is_primary_key, sequence) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 9, 'RELATIONSHIP_ID', 1, 'RELATIONSHIP_SEQ');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 3, 'RELATIONSHIP_TYPE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 6, 'RELATED_FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 3, 'STRAT_UNIT');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 2, 'DISTANCE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 3, 'DISTANCE_MOD');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 6, 'RELATION_TYPE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 2, 'DISTANCE_RANGE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 6, 'SAMPLE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 6, 'STRAT_UNIT_ID');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP_TYPE'), 1, 'RELTYPE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP_TYPE'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP_TYPE'), 3, 'RELATION_TYPE');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATION_TYPE'), 3, 'RELATION_TYPE');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ROCK_COLOUR'), 1, 'COLOUR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ROCK_COLOUR'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='ROCK_COLOUR'), 3, 'CODE');

insert into mg_column (id, mg_table, mg_column_type, code, is_primary_key, sequence) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 9, 'SAMPLE_ID', 1, 'SAMPLE_SEQ');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 7, 'FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 7, 'FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'TOP_DEPTH');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'BOTTOM_DEPTH');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'DRILL_TYPE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'DRILL_TYPE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'YARD_FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 7, 'AUDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 10, 'COLLECTION_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'DATE_ROUNDING');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'STRAT_UNIT');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'IN_PLACE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'NOT_COLLECTED');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'SIGNIFICANCE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 7, 'INFERRED_STAGE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 7, 'KNOWN_STAGE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'COLUMN_MAP');
insert into mg_column (id, mg_table, mg_column_type, code, min_value, max_value) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 1, 'DIP', 0, 90);
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'DIP_DIRECTION');
insert into mg_column (id, mg_table, mg_column_type, code, Min_value, max_value) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 1, 'STRIKE', 0, 360);
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'FACING');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'PRIMARY_GRAINSIZE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'SECONDARY_GRAINSIZE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'COMPARATOR_USED');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'BED_THICK_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'PRIMARY_BEDDING_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'SECONDARY_BEDDING_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'WEATHERING_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'HARDNESS_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'CARBONATE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'COLOUR_MODIFIER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'PRIMARY_COLOUR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'SECONDARY_COLOUR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'WET');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'ROCK_NATURE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'DEPOSITION_ENV');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'CORRESPONDENCE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'DEPTH_UNIT');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'STRAT_COMMENTS');


insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE_META'), 6, 'SAMPLE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE_META'), 1, 'META_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SECURITY_CLASS'), 1, 'CLASS_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SECURITY_CLASS'), 3, 'NAME');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SEDIMENTARY_FEATURE'), 6, 'SED_FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SEDIMENTARY_FEATURE'), 3, 'ABUNDANT'); -- Y or N.
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SEDIMENTARY_FEATURE'), 6, 'SAMPLE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SEDIMENTARY_FEATURE_TYPE'), 1, 'SEDFEATURE_TYPE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SEDIMENTARY_FEATURE_TYPE'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SEDIMENTARY_FEATURE_TYPE'), 3, 'CODE');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SENT_TO'), 6, 'FOSSIL_GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SENT_TO'), 4, 'SENT_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SENT_TO'), 3, 'DATE_ROUNDING');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SENT_TO'), 6, 'PERSON_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SENT_TO'), 6, 'LAB_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SENT_TO'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SENT_TO'), 6, 'SAMPLE_ID');
insert into mg_column (id, mg_table, mg_column_type, code, is_primary_key, sequence) values (mg_column_id_seq.nextval, (select id from mg_table where code='SENT_TO'), 9, 'SENT_TO_ID', 1, 'SENT_TO_SEQ');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SIMEDELS'), 1, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code, is_primary_key, sequence) values (mg_column_id_seq.nextval, (select id from mg_table where code='STAGE'), 9, 'STAGE_ID', 1, 'STAGE_SEQ');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='STAGE'), 3, 'STAGE_LOWER_MOD');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='STAGE'), 3, 'STAGE_UPPER_MOD');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='STAGE'), 3, 'STAGE_MOD');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='STAGE'), 6, 'AGE_LOWER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='STAGE'), 6, 'AGE_UPPER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='STAGE'), 2, 'BASE_AGE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='STAGE'), 2, 'TOP_AGE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXA_PANEL'), 6, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXA_PANEL'), 1, 'PANELIST_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_GROUP'), 1, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_GROUP'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 1, 'TAXA_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 6, 'GROUP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 3, 'TAXONOMIC_NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 3, 'AUTHOR');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 3, 'STATUS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 1, 'SUBMITTED_BY_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 4, 'SUBMITTED_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 1, 'APPROVED_BY_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 4, 'APPROVED_DATE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 3, 'SEND_MESSAGE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_LOOKUP'), 3, 'PANELIST_COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_SYNONYM'), 6, 'TAXA_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TAXONOMIC_SYNONYM'), 6, 'SYNONYM_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TEMP_LOB'), 3, 'WHATEVER');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TEMP_YARD_REFS'), 3, 'FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TEMP_YARD_REFS'), 3, 'SHEET');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TEMP_YARD_REFS'), 3, 'EASTING');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TEMP_YARD_REFS'), 3, 'NORTHING');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='TEMP_YARD_REFS'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='USER_LIST'), 1, 'ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='USER_LIST'), 1, 'SAMPLEID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='WEATHERING'), 1, 'WEATHERING_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='WEATHERING'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='WEATHERING'), 3, 'CODE');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.LAB'), 1, 'LAB_ID');
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.LAB'), 3, 'LAB_NAME', 1);

CREATE OR REPLACE FORCE VIEW "FR"."LU_COUNTRY" ("COUNTRY_CODE", "COUNTRY_NAME", "COUNTRY_DIAL_CODE") AS 
  SELECT "COUNTRY_CODE","COUNTRY_NAME","COUNTRY_DIAL_CODE" FROM MIS.COUNTRY
  ORDER BY decode (country_code, 'AQ', 'AA', 'NZ', 'AB', country_name);

CREATE TABLE LU_COORD_SYSTEM (
        ORIG_SYSTEM_ID NUMBER(3) PRIMARY KEY,
        CODE VARCHAR(40),
        NAME VARCHAR(100),
        DISP_ORDER NUMBER(3) DEFAULT 0
);
COMMENT ON TABLE LU_COORD_SYSTEM IS 'Smaller lookup for SC.ORIG_SYSTEM.';
COMMENT ON COLUMN LU_COORD_SYSTEM.CODE IS 'The same code that is in the nz.cri.gns.util.map.tm datum classes. Not equal to the code in ORIG_SYSTEM. Yea. I didn''t design that.';
INSERT INTO LU_COORD_SYSTEM (ORIG_SYSTEM_ID, CODE, NAME, DISP_ORDER) VALUES (71, 'NZTM', 'NZTM (NZ Transverse Mercator)', 1);
INSERT INTO LU_COORD_SYSTEM (ORIG_SYSTEM_ID, CODE, NAME, DISP_ORDER) VALUES (28, 'NZGD2000', 'Lat/long NZGD 2000', 2);
INSERT INTO LU_COORD_SYSTEM (ORIG_SYSTEM_ID, CODE, NAME, DISP_ORDER) VALUES (73, 'WGS84', 'Lat/long WGS 84', 3);
INSERT INTO LU_COORD_SYSTEM (ORIG_SYSTEM_ID, CODE, NAME, DISP_ORDER) VALUES (72, 'NZTopo50', 'NZTopo50', 4);
insert into mg_column (id, mg_table, mg_column_type, code, is_primary_key) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_COORD_SYSTEM'), 1, 'ORIG_SYSTEM_ID', 1);
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_COORD_SYSTEM'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_COORD_SYSTEM'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_COORD_SYSTEM'), 1, 'DISP_ORDER');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FEATURE','ORIG_SYSTEM_ID','LU_COORD_SYSTEM','ORIG_SYSTEM_ID');

insert into mg_column (id, mg_table, mg_column_type, code, is_primary_key) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.ORIG_SYSTEM'), 1, 'SYSTEM_ID', 1);
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.ORIG_SYSTEM'), 3, 'SYSTEM_CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.ORIG_SYSTEM'), 3, 'HUMAN_NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.ORIG_SYSTEM'), 3, 'COORD_SYSTEM');


insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SC.METHOD', 'Coordinate System');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.METHOD'), 1, 'METHOD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.METHOD'), 3, 'METHOD');
insert into mg_column (id, mg_table, mg_column_type, code, order_by_rank) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.METHOD'), 3, 'DISP_ORDER', 1);

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_COUNTRY', 'Country');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_COUNTRY'), 3, 'COUNTRY_CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_COUNTRY'), 3, 'COUNTRY_NAME');

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SL.STRAT_UNIT', 'Strat unit');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SL.STRAT_UNIT'), 1, 'SU_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SL.STRAT_UNIT'), 3, 'SU_NAME');
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='SL.STRAT_UNIT'), 3, 'SU_NAME_STANDARD', 1);

create table lu_cardinal (
        code varchar(4) not null primary key,
        name varchar(20),
        disp_order number(2)
);
comment on table lu_cardinal is 'Cardinal directions - North, Northeast etc, for sample.dip_direction.';
insert into lu_cardinal(code, name, disp_order) values ('N', 'North', 1);
insert into lu_cardinal(code, name, disp_order) values ('NE', 'Northeast', 2);
insert into lu_cardinal(code, name, disp_order) values ('E', 'East', 3);
insert into lu_cardinal(code, name, disp_order) values ('SE', 'Southeast', 4);
insert into lu_cardinal(code, name, disp_order) values ('S', 'South', 5);
insert into lu_cardinal(code, name, disp_order) values ('SW', 'Southwest', 6);
insert into lu_cardinal(code, name, disp_order) values ('W', 'West', 7);
insert into lu_cardinal(code, name, disp_order) values ('NW', 'Northwest', 8);
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_CARDINAL', 'Direction');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_CARDINAL'), 3, 'CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_CARDINAL'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code, order_by_rank) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_CARDINAL'), 1, 'DISP_ORDER', 1);
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','DIP_DIRECTION','LU_CARDINAL','CODE');


create table lu_date_rounding (code varchar(8) primary key, disp_order number(2));
insert into lu_date_rounding(code, disp_order) values ('Year', 1);
insert into lu_date_rounding(code, disp_order) values ('Month', 2);
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_DATE_ROUNDING', 'Date Rounding');
insert into mg_column (id, mg_table, mg_column_type, code, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_DATE_ROUNDING'), 3, 'CODE', 1);
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_DATE_ROUNDING'), 1, 'DISP_ORDER', 1);
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','DATE_ROUNDING','LU_DATE_ROUNDING','CODE');

create table lu_in_place (code varchar(8) primary key, disp_order number(2));
insert into lu_in_place (code, disp_order) values ('Yes', 1);
insert into lu_in_place (code, disp_order) values ('No', 2);
insert into lu_in_place (code, disp_order) values ('Almost', 3);
insert into lu_in_place (code, disp_order) values ('Unknown', 4);
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_IN_PLACE', 'In Place');
insert into mg_column (id, mg_table, mg_column_type, code, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_IN_PLACE'), 3, 'CODE', 1);
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_IN_PLACE'), 1, 'DISP_ORDER', 1);
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','IN_PLACE','LU_IN_PLACE','CODE');

create table lu_wet (code varchar(8) primary key, DISP_ORDER number(2));
insert into lu_wet (code, DISP_ORDER) values ('Wet', 1);
insert into lu_wet (code, DISP_ORDER) values ('Dry', 2);
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_WET', 'Wet');
insert into mg_column (id, mg_table, mg_column_type, code, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_WET'), 3, 'CODE', 1);
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_WET'), 1, 'DISP_ORDER', 1);
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','WET','LU_WET','CODE');

create table lu_yesno (code varchar(8) primary key, DISP_ORDER number(2));
insert into lu_yesno (code, DISP_ORDER) values ('Yes', 1);
insert into lu_yesno (code, DISP_ORDER) values ('No', 2);
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_YESNO', 'Yes/No');
insert into mg_column (id, mg_table, mg_column_type, code, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_YESNO'), 3, 'CODE', 1);
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_YESNO'), 1, 'DISP_ORDER', 1);
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','COMPARATOR_USED','LU_YESNO','CODE');
      
create table lu_depth_unit (code varchar(8) primary key, name varchar(20), DISP_ORDER number(2));
insert into lu_depth_unit (code, name, DISP_ORDER) values ('m', 'Metres', 1);
insert into lu_depth_unit (code, name, DISP_ORDER) values ('ft', 'Feet', 2);
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_DEPTH_UNIT', 'Depth Unit');
insert into mg_column (id, mg_table, mg_column_type, code, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_DEPTH_UNIT'), 3, 'CODE', 1);
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_DEPTH_UNIT'), 1, 'DISP_ORDER', 1);
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','DEPTH_UNIT','LU_DEPTH_UNIT','CODE');

create table lu_facing (code varchar(16) primary key, DISP_ORDER number(2));
insert into lu_facing (code, DISP_ORDER) values ('Overturned', 1);
insert into lu_facing (code, DISP_ORDER) values ('Normal', 2);
insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_FACING', 'Facing');
insert into mg_column (id, mg_table, mg_column_type, code, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_FACING'), 3, 'CODE', 1);
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_FACING'), 1, 'DISP_ORDER', 1);
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','FACING','LU_FACING','CODE');

create view lu_strat_relationship_view as (
select reltype_id, name
from relationship_type
where relation_type = 'Stratigraphic' );

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_STRAT_RELATIONSHIP_VIEW', 'Stratigraphic Relationship');
insert into mg_column (id, mg_table, mg_column_type, code, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_STRAT_RELATIONSHIP_VIEW'), 1, 'RELTYPE_ID', 1);
insert into mg_column (id, mg_table, mg_column_type, code, ORDER_BY_RANK) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_STRAT_RELATIONSHIP_VIEW'), 1, 'NAME', 1);

create view lu_age_view as 
select age_id, name, code, period, base_age, top_age
from age
where obsolete_flag=0
order by base_age;

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'LU_AGE_VIEW', 'Age list');
insert into mg_column (id, mg_table, mg_column_type, code, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_AGE_VIEW'), 1, 'AGE_ID', 1);
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_AGE_VIEW'), 3, 'NAME' );
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_AGE_VIEW'), 3, 'CODE' );
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_AGE_VIEW'), 3, 'PERIOD' );
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_AGE_VIEW'), 2, 'BASE_AGE' );
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='LU_AGE_VIEW'), 2, 'TOP_AGE' );


insert into temp_fk (from_table, from_column, to_table, to_column) values ('RECORD','AUDIT_ID','AUDIT_TABLE','AUDIT_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('RECORD','SAMPLE_ID','SAMPLE','SAMPLE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('RECORD','PAL_LIST_AUDIT_ID','AUDIT_TABLE','AUDIT_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('RELATIONSHIP','RELATED_FEATURE_ID','FEATURE','FEATURE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('RELATIONSHIP','RELATIONSHIP_TYPE','RELATION_TYPE','RELATION_TYPE');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('RELATIONSHIP','RELATION_TYPE_ID','RELATIONSHIP_TYPE','RELTYPE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('RELATIONSHIP','SAMPLE_ID','SAMPLE','SAMPLE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('RELATIONSHIP','STRAT_UNIT_ID','SL.STRAT_UNIT','SU_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SEDIMENTARY_FEATURE','SED_FEATURE_ID','SEDIMENTARY_FEATURE_TYPE','SEDFEATURE_TYPE_ID');
--insert into temp_fk (from_table, from_column, to_table, to_column) values ('SEDIMENTARY_FEATURE','ABUNDANT','LU_YESNO','CODE');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SEDIMENTARY_FEATURE','SAMPLE_ID','SAMPLE','SAMPLE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('STAGE','AGE_LOWER_ID','LU_AGE_VIEW','AGE_ID'); -- This would mean that entries with obsolete ages will have problems.
insert into temp_fk (from_table, from_column, to_table, to_column) values ('STAGE','AGE_UPPER_ID','LU_AGE_VIEW','AGE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('TAXA_PANEL','GROUP_ID','TAXONOMIC_GROUP','GROUP_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('TAXONOMIC_LOOKUP','GROUP_ID','TAXONOMIC_GROUP','GROUP_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('ADOPTION','RECORD_ID','RECORD','RECORD_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('ADOPTION','ADOPTED_STAGE_ID','STAGE','STAGE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('ADOPTOR','RECORD_ID','ADOPTION','RECORD_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('ADOPTOR','PERSON_ID','PERSON','PERSON_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('AUDIT_TABLE','WORKING_FOLDER_ID','FOLDER','FOLDER_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('AUDIT_TABLE','DATA_ORIGIN_ID','DATA_ORIGIN','ORIGIN_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('COLLECTOR','PERSON_ID','PERSON','PERSON_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('COLLECTOR','SAMPLE_ID','SAMPLE','SAMPLE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FEATURE','AUDIT_ID','AUDIT_TABLE','AUDIT_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FEATURE','MASTERFILE_ID','FOLDER','FOLDER_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FEATURE','PERSON_ID','PERSON','PERSON_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FEATURE','REG_AREA_ID','REGISTRATION_AREA','REG_AREA_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FEATURE','YARD_FR_ID','FR_NUMBER','FR_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FEATURE','FR_ID','FR_NUMBER','FR_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FOLDER','FOLDER_TYPE','FOLDER_TYPE','FOLDER_TYPE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FOLDER_CONTENT','FOLDER_ID','FOLDER','FOLDER_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FOLDER_CONTENT','FEATURE_ID','FEATURE','FEATURE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FOLDER_USER','FOLDER_ID','FOLDER','FOLDER_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('IDENTIFIER','RECORD_ID','PALEONTOLOGY','RECORD_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('IDENTIFIER','PERSON_ID','PERSON','PERSON_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('PALEONTOLOGY','RECORD_ID','RECORD','RECORD_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('PALEONTOLOGY','STAGE_ID','STAGE','STAGE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('PALEONTOLOGY','LAB_SECTION_ID','LAB_SECTION','LAB_SECTION_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('PAL_LIST','RECORD_ID','PALEONTOLOGY','RECORD_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('PAL_LIST','TAXA_ID','TAXONOMIC_LOOKUP','TAXA_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('PAL_LIST','GROUP_ID','TAXONOMIC_GROUP','GROUP_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('FEATURE_META','FEATURE_ID','FEATURE','FEATURE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE_META','SAMPLE_ID','SAMPLE','SAMPLE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('RECORD_META','RECORD_ID','RECORD','RECORD_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('AUDIT_EDIT','AUDIT_ID','AUDIT_TABLE','AUDIT_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('PAL_LIST_META','PAL_LIST_ID','PAL_LIST','PAL_LIST_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','FEATURE_ID','FEATURE','FEATURE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','BED_THICK_ID','BED_THICKNESS','THICKNESS_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','CARBONATE_ID','CARBONATE','CARBONATE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','COLOUR_MODIFIER_ID','COLOUR_MODIFIER','MODIFIER_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','DRILL_TYPE_ID','DRILL_TYPE','DRILL_TYPE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','HARDNESS_ID','HARDNESS','HARDNESS_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','PRIMARY_BEDDING_ID','BEDDING','BEDDING_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','PRIMARY_COLOUR_ID','ROCK_COLOUR','COLOUR_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','PRIMARY_GRAINSIZE_ID','GRAIN_SIZE','GRAIN_SIZE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','SECONDARY_BEDDING_ID','BEDDING','BEDDING_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','SECONDARY_COLOUR_ID','ROCK_COLOUR','COLOUR_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','SECONDARY_GRAINSIZE_ID','GRAIN_SIZE','GRAIN_SIZE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','WEATHERING_ID','WEATHERING','WEATHERING_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','FR_ID','FR_NUMBER','FR_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','YARD_FR_ID','FR_NUMBER','FR_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','AUDIT_ID','AUDIT_TABLE','AUDIT_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','INFERRED_STAGE_ID','STAGE','STAGE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SAMPLE','KNOWN_STAGE_ID','STAGE','STAGE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('BACKLOG_STATUS','MASTERFILE_ID','FOLDER','FOLDER_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('CONFID_GROUP_USER','GROUP_ID','CONFIDENTIAL_GROUP','GROUP_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('AUDIT_CONFID','GROUP_ID','CONFIDENTIAL_GROUP','GROUP_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('AUDIT_CONFID','AUDIT_ID','AUDIT_TABLE','AUDIT_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('CONFID_GROUP_OWNERS','GROUP_ID','CONFIDENTIAL_GROUP','GROUP_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('BORE_FRED','FEATURE_ID','FEATURE','FEATURE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('TAXONOMIC_SYNONYM','TAXA_ID','TAXONOMIC_LOOKUP','TAXA_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('TAXONOMIC_SYNONYM','SYNONYM_ID','TAXONOMIC_LOOKUP','TAXA_ID');


insert into temp_fk (from_table, from_column, to_table, to_column) values ('SENT_TO','PERSON_ID','PERSON','PERSON_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SENT_TO','FOSSIL_GROUP_ID','FOSSIL_GROUP','GROUP_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SENT_TO','SAMPLE_ID','SAMPLE','SAMPLE_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SENT_TO','LAB_ID','SC.LAB','LAB_ID');
insert into temp_fk (from_table, from_column, to_table, to_column) values ('SENT_TO','DATE_ROUNDING','LU_DATE_ROUNDING','CODE');

insert into mg_column_foreign_key (from_mg_column, to_mg_column)
select fc.id, tc.id --, ft.code, fc.code, tt.code, tc.code
from mg_column fc
join mg_table ft on ft.id=fc.mg_table
join temp_fk tfk on tfk.from_table=ft.code and tfk.from_column=fc.code
join mg_column tc on tfk.to_column=tc.code
join mg_table tt on tt.id=tc.mg_table and tt.code=tfk.to_table;

DROP TABLE TEMP_FK;


INSERT INTO MG_IMPORT_SPREADSHEET_TYPE(ID, CODE, NAME, FILENAME_TEMPLATE)
VALUES (MG_IMPORT_SS_TYPE_ID_SEQ.NEXTVAL, 'FRED_OUTCROP', 'FRED Outcrop', 'fred_template.xlsm');

insert into MG_IMPORT_SHEET_TYPE (ID, CODE,  NAME, MG_TABLE, ROW_DATA_START, MG_IMPORT_SPREADSHEET_TYPE) 
values (mg_import_s_type_id_seq.nextval, 'FRED_OUTCROP', 'Outcrop', (SELECT ID FROM MG_TABLE WHERE CODE='SAMPLE'), 1, (SELECT ID FROM MG_IMPORT_SPREADSHEET_TYPE WHERE CODE='FRED_OUTCROP'));

INSERT INTO MG_IMPORT_SPREADSHEET_TYPE(ID, CODE, NAME, FILENAME_TEMPLATE)
VALUES (MG_IMPORT_SS_TYPE_ID_SEQ.NEXTVAL, 'FRED_VERTICAL_SECTION', 'FRED Vertical Section', 'fred_template.xlsm');

insert into MG_IMPORT_SHEET_TYPE (ID, CODE,  NAME, MG_TABLE, ROW_DATA_START, MG_IMPORT_SPREADSHEET_TYPE) 
values (mg_import_s_type_id_seq.nextval, 'VERTICAL_SECTION', 'Vertical Section', (SELECT ID FROM MG_TABLE WHERE CODE='SAMPLE'), 1, (SELECT ID FROM MG_IMPORT_SPREADSHEET_TYPE WHERE CODE='FRED_VERTICAL_SECTION'));

INSERT INTO MG_IMPORT_SPREADSHEET_TYPE(ID, CODE, NAME, FILENAME_TEMPLATE)
VALUES (MG_IMPORT_SS_TYPE_ID_SEQ.NEXTVAL, 'FRED_DRILL_HOLE', 'FRED Drill Hole', 'fred_template.xlsm');

insert into MG_IMPORT_SHEET_TYPE (ID, CODE,  NAME, MG_TABLE, ROW_DATA_START, MG_IMPORT_SPREADSHEET_TYPE) 
values (mg_import_s_type_id_seq.nextval, 'DRILL_HOLE', 'Drill Hole', (SELECT ID FROM MG_TABLE WHERE CODE='SAMPLE'), 1, (SELECT ID FROM MG_IMPORT_SPREADSHEET_TYPE WHERE CODE='FRED_DRILL_HOLE'));

INSERT INTO MG_IMPORT_SPREADSHEET_TYPE(ID, CODE, NAME, FILENAME_TEMPLATE)
VALUES (MG_IMPORT_SS_TYPE_ID_SEQ.NEXTVAL, 'FRED_PALEO', 'FRED Paleontological Analysis', 'fred_template_paleo.xlsm');

insert into MG_IMPORT_SHEET_TYPE (ID, CODE,  NAME, MG_TABLE, ROW_DATA_START, MG_IMPORT_SPREADSHEET_TYPE) 
values (mg_import_s_type_id_seq.nextval, 'PALEO', 'Paleo', (SELECT ID FROM MG_TABLE WHERE CODE='SAMPLE'), 1, (SELECT ID FROM MG_IMPORT_SPREADSHEET_TYPE WHERE CODE='FRED_PALEO'));

/*INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        DISP_ORDER,
        MG_COLUMN_TYPE,
        heading
    )
    VALUES
    (
        MG_IMPORT_COLUMN_TYPE_ID_SEQ.NEXTVAL,
        'TAXON_GROUP',
        (select id from mg_import_sheet_type where code='PALEO'),
        0,
        3,
        'Taxon group'
    );
    
INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        DISP_ORDER,
        MG_COLUMN_TYPE,
        heading
    )
    VALUES
    (
        MG_IMPORT_COLUMN_TYPE_ID_SEQ.NEXTVAL,
        'TAXON',
        (select id from mg_import_sheet_type where code='PALEO'),
        1,
        3,
        'Taxon'
    );*/

Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1528,'ACCURACY',2,12,null,null,'Accuracy (m)','Enter the accuracy of the coordinate measurement, in metres. If blank, 10m is used.
',1,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1576,'ADDITIONAL_FEATURES',2,60,null,'#SEDIMENTARY_FEATURE$SED_FEATURE_ID$NAME','Additional Features','Enter any other comments about the lithology.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1577,'ABUNDANT',2,61,null,'#SEDIMENTARY_FEATURE$ABUNDANT','Additional Features – Abundant?','Whether the lithology is abundant',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1571,null,2,55,null,'CARBONATE_ID$NAME','Carbonate','Note whether the rock unit is calcareous.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1533,null,2,17,null,'COLLECTION_DATE','Collection Date','Enter the date that the collection was made. A full date is required even if only the month or the year are known (see next column).
',10,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1535,'COLLECTOR_NAME',2,19,null,'#COLLECTOR$PERSON_ID$NAME','Collectors','Enter the name(s) of the collector(s), one name per row. Use the format surname-comma-space-initials. Place periods after each initial.
',6,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1573,null,2,57,null,'PRIMARY_COLOUR_ID$NAME','Colour (Primary)','Enter the primary colour of the rock unit.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1574,null,2,58,null,'SECONDARY_COLOUR_ID$NAME','Colour (Secondary)','Enter the secondary colour of the rock unit.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1557,'MAP_SHEET',2,41,null,'COLUMN_MAP','Column / Map','If available, enter a reference where a map or column of the collection site may be found. References to entries in the New Zealand Measured Section database are ideal.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1565,'COMPARATOR_USED',2,49,null,'COMPARATOR_USED','Comparator Used','Specify whether a grain-size comparator was used (Y or N).
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1529,null,2,13,null,'FEATURE_ID$COORD_COMMENTS','Coordinate Comments','Enter any comments relating to the coordinates.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1523,'ORIG_SYSTEM_ID',2,6,null,'FEATURE_ID$ORIG_SYSTEM_ID$NAME','Coordinate System','Choose the coordinate system for the coordinates in the following columns.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1580,null,2,64,null,'CORRESPONDENCE','Correspondence, Cross-References','Enter any other comments about the rock unit as a whole.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1531,'COUNTRY',2,15,null,null,'Country','Enter the country of the locality.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1534,null,2,18,null,'DATE_ROUNDING$CODE','Date Rounding','If the collection date is known exactly, leave this column blank. If only the month (or year) is known, enter ''Month'' (or ''Year'') in this column.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1558,null,2,42,null,'DIP','Dip','Enter the dip, in degrees (0 for horizontal strata).
',2,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1559,null,2,43,null,'DIP_DIRECTION$CODE','Dip Direction','Enter the cardinal direction of the dip.
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1524,'EASTING',2,7,null,null,'Easting','Enter the easting or longitude coordinate. Do not append a letter ''E''.
',2,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1561,null,2,45,null,'FACING$CODE','Facing','Enter normal or overturned.
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1519,'FEATURE_NAME',2,2,null,null,'Field number','Enter the geologist''s field number for the sample. If not known, another distinctive identifier can be substituted.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1518,'FOLDER',2,1,null,null,'Folder','Folders are your groupings of imported data within FRED. Just choose one.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1536,null,2,20,null,'IN_PLACE$CODE','Fossils in Place','Specify whether the fossils collected were in situ, almost, or not in situ.
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1563,null,2,47,null,'PRIMARY_GRAINSIZE_ID$NAME','Grain Size (primary)','Enter the primary grain size.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1564,null,2,48,null,'SECONDARY_GRAINSIZE_ID$NAME','Grain Size (secondary)','Enter the secondary grain size.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1570,null,2,54,null,'HARDNESS_ID$NAME','Hardness','Specify the hardness of the rock unit.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1578,'INFERRED_ENVIRONMENT',2,62,null,'DEPOSITION_ENV','Inferred Environment','Enter one of “Marine” or “Non Marine”.
',3,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1546,'INFERRED_STAGE_LOWER',2,30,null,null,'Inferred Stage Limits – From','Enter the lower bound for the inferred stage interval.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1547,'INFERRED_STAGE_UPPER',2,31,null,null,'Inferred Stage Limits – To','Enter the upper bound for the inferred stage interval.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1544,'KNOWN_STAGE_LOWER',2,28,null,null,'Known Stage Limits – From','Enter the lower bound for the known stage interval.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1545,'KNOWN_STAGE_UPPER',2,29,null,null,'Known Stage Limits – To','Enter the upper bound for the known stage interval.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1532,null,2,16,null,'FEATURE_ID$COMMENTS','Locality Comments','Enter any supplementary comments relating to the locality.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1530,'LOCALITY',2,14,null,'FEATURE_ID$LOCALITY','Locality Description','Describe the fossil locality in words. Try to avoid reference to emphemeral landmarks.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1527,null,2,11,null,'FEATURE_ID$MAP_YEAR','Map Year','If the coordinates were read from a map, enter the map year.
',1,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1526,'LOCATION_METHOD',2,9,null,null,'Method','Enter the means by which the coordinates were determined.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1579,null,2,63,null,'ROCK_NATURE','Nature of Rock Unit','Enter any additional notes on the nature of the rock unit.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1525,'NORTHING',2,8,null,null,'Northing','Enter the northing or latitude coordinate. Use a negative number for Southern Hemisphere coordinates; do not append a letter ''S''.
',2,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1537,null,2,21,null,'NOT_COLLECTED','Not Collected','List any fossils seen but which were not collected. This is a free text field and no taxonomic thesaurus checking will be applied.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1521,'RECOLLECTION_OF',2,4,null,null,'Recollection of','If this is a recollection of a previous collection, enter the Fossil Record Number of the earlier collection here. This must match an existing FRN in FRED.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1520,null,2,3,null,'FEATURE_ID$REG_AREA_ID$NAME','Registration Area','Enter the Fossil Record File registration area for the sample. This is used to identify which curator will approve your data into the database.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1550,'SAMPLE_RELATIONSHIP_DISTANCE',2,34,null,null,'Sample Relationship Distance','The distance or distance range of the sample relationship, in metres. Use decimal numbers; for a range use a dash, e.g. “1.0m – 1.1m”.
',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1549,'SAMPLE_RELATIONSHIP_MOD',2,33,null,null,'Sample Relationship Mod','Choose one of “c.” or “?”, or leave blank.
',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1551,'SAMPLE_RELATIONSHIP_PREP',2,35,null,null,'Sample Relationship Prep','Specify whether the current sample is above, below or nearby the "Nearby" sample.',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1552,'SAMPLE_RELATIONSHIP_REFERENCE',2,36,null,null,'Sample Relationship Reference','Enter the FRNs of Features or Samples that this Sample Relationship relates to.',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1548,'SAMPLES_NEARBY',2,32,null,null,'Samples Nearby','If there are other samples collected nearby, record them here using either their Fossil Record Number (if previously entered into FRED) of the field number from column B (for other samples in this spreadsheet). This is a multi-value column; enter multiple field numbers, one per cell, in a vertical list under this row.
',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1541,null,2,25,null,'#SENT_TO$COMMENTS','Sent To – Comment','Optional comment regarding the disposition of the collection.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1540,null,2,24,null,'#SENT_TO$LAB_ID$LAB_NAME','Sent To – Organisation','Enter the name of the research facility that this fossil collection was/will be sent to.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1539,null,2,23,null,'#SENT_TO$PERSON_ID$NAME','Sent To – Person','Enter the name of the person that this fossil collection was/will be sent to. Use the format surname-comma-space-initials. Place periods after each initial.
',6,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1538,null,2,22,null,'#SENT_TO$FOSSIL_GROUP_ID$NAME','Sent To – Study Area','Enter one of macro/micro flora/fauna, radiocarbon, or other, to describe the study to be performed on the collection.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1572,null,2,56,null,'COLOUR_MODIFIER_ID$NAME','Shade','This and the next three columns work together to specify the colour of the rock unit. Enter one of light, medium or dark.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1542,null,2,26,null,'SIGNIFICANCE','Significance / Comments','If there is any particular significance to the locality or the collection, record it here.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1554,'STRAT_RELATIONSHIP_DISTANCE',2,38,null,null,'Strat. Relationship Distance','The distance or distance range of the sample relationship, in metres. Use decimal numbers; for a range use a dash, e.g. “1.0m – 1.1m”.
',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1553,'STRAT_RELATIONSHIP_MOD',2,37,null,null,'Strat. Relationship Mod','Choose one of “c.” or “?”, or leave blank.
',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1555,'STRAT_RELATIONSHIP_PREP',2,39,null,null,'Strat. Relationship Prep','Specify whether the current sample is above/below the top/bottom of the stratigraphic unit named in the next field.
',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1556,'STRAT_RELATIONSHIP_STRAT_UNIT',2,40,null,'#RELATIONSHIP$STRAT_UNIT_ID$SU_NAME_STANDARD','Strat. Relationship Unit','Enter the name of the stratigraphic unit. Published formal names (as recorded in the NZ Stratigraphic Lexicon) are strongly preferred, but manuscript names are permissable if no other is available.
',6,2,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1568,null,2,52,null,'SECONDARY_BEDDING_ID$NAME','Stratification - Internal Features Secondary','Specify whether the bed is secondarily graded, slump-folded or cross-bedded.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1566,null,2,50,null,'BED_THICK_ID$NAME','Stratification – Bed Thickness','Use this field to record the scale of stratification.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1567,null,2,51,null,'PRIMARY_BEDDING_ID$NAME','Stratification – Internal Features Primary','Specify whether the bed is primarily graded, slump-folded or cross-bedded.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1562,null,2,46,null,'STRAT_COMMENTS','Stratigraphic Comments','Enter any other brief observations of the stratigraphy.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1543,'STRAT_UNIT',2,27,null,'STRAT_UNIT','Stratigraphic Name','Enter the name of the stratigraphic unit. Published formal names (as recorded in the NZ Stratigraphic Lexicon) are strongly preferred, but manuscript names are permissable if no other is available.
',3,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1560,null,2,44,null,'STRIKE','Strike','Enter the strike direction, in degrees true (leave blank for horizontal strata).
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1569,null,2,53,null,'WEATHERING_ID$NAME','Weathering','Specify the intensity of weathering.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1575,null,2,59,null,'WET$CODE','Wet / Dry','Specify whether the rock unit colour was determined wet or dry.
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1522,'WORKING_COMMENTS',2,5,null,null,'Working comments','Room for any temporary working comments here. If left in the spreadsheet when it is submitted, they will not be displayed in FRED.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1630,'ADDITIONAL_FEATURES',3,50,null,'#SEDIMENTARY_FEATURE$SED_FEATURE_ID$NAME','Additional Features',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1631,'ABUNDANT',3,51,null,'#SEDIMENTARY_FEATURE$ABUNDANT','Additional Features – Abundant?','Whether the lithology is abundant',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1584,null,3,4,null,'BOTTOM_DEPTH','Bottom Depth','Enter the bottom depth of the sample.',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1625,null,3,45,null,'CARBONATE_ID$NAME','Carbonate',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1587,null,3,7,null,'COLLECTION_DATE','Collection Date','When was the collection collected?',10,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1589,'COLLECTOR_NAME',3,9,null,'#COLLECTOR$PERSON_ID$NAME','Collectors','Enter the collector. If not in the list, enter a new name. Names must match the exact spelling as the entry in FRED.',6,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1627,null,3,47,null,'PRIMARY_COLOUR_ID$NAME','Colour (Primary)',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1628,null,3,48,null,'SECONDARY_COLOUR_ID$NAME','Colour (Secondary)',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1611,'MAP_SHEET',3,31,null,'COLUMN_MAP','Column / Map','If available, enter a reference where a map or column of the collection site may be found, ideally reference to an entry in the New Zealand Measured Section database.',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1619,'COMPARATOR_USED',3,39,null,'COMPARATOR_USED','Comparator Used','Was a comparator used?',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1634,null,3,54,null,'CORRESPONDENCE','Correspondence, Cross-References','Use this field to reference any correspondence, publications or other information source.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1588,null,3,8,null,'DATE_ROUNDING$CODE','Date Rounding','What is the accuracy of the date?',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1586,null,3,6,null,'DEPTH_UNIT$CODE','Depth Unit','Enter the unit used in measuring the top and bottom depths.',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1612,null,3,32,null,'DIP','Dip','The dip, in degrees (0 for horizontal strata)',2,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1613,null,3,33,null,'DIP_DIRECTION$CODE','Dip Direction','The cardinal direction of the dip.',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1615,null,3,35,null,'FACING$CODE','Facing',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1581,'FOLDER',3,1,null,null,'Folder','Reference any correspondence, publications or other information source.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1590,null,3,10,null,'IN_PLACE$CODE','Fossils in Place','Is the fossil “in place”?',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1617,null,3,37,null,'PRIMARY_GRAINSIZE_ID$NAME','Grain Size (primary)',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1618,null,3,38,null,'SECONDARY_GRAINSIZE_ID$NAME','Grain Size (secondary)',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1624,null,3,44,null,'HARDNESS_ID$NAME','Hardness',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1632,'INFERRED_ENVIRONMENT',3,52,null,'DEPOSITION_ENV','Inferred Environment','Enter one of “Marine” or “Non Marine”',3,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1600,'INFERRED_STAGE_LOWER',3,20,null,null,'Inferred Stage Limits – From','Enter the lower bound for the inferred stage.',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1601,'INFERRED_STAGE_UPPER',3,21,null,null,'Inferred Stage Limits – To','Enter the upper bound for the inferred stage.',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1598,'KNOWN_STAGE_LOWER',3,18,null,null,'Known Stage Limits – From','Enter the lower bound for the known stage.',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1599,'KNOWN_STAGE_UPPER',3,19,null,null,'Known Stage Limits – To','Enter the upport bound for the known stage.',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1633,null,3,53,null,'ROCK_NATURE','Nature of Rock Unit','Enter any additional notes on the nature of the rock.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1591,null,3,11,null,'NOT_COLLECTED','Not Collected','List the fossils not collected. This is free text.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1604,'SAMPLE_RELATIONSHIP_DISTANCE',3,24,null,null,'Sample Relationship Distance','The distance or distance range of the sample relationship, in metres. Use decimal numbers; for a range use a dash, e.g. “1.0m – 1.1m”',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1603,'SAMPLE_RELATIONSHIP_MOD',3,23,null,null,'Sample Relationship Mod','Choose one of “c.” or “?”, or leave blank.
',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1605,'SAMPLE_RELATIONSHIP_PREP',3,25,null,null,'Sample Relationship Prep','For a Sample Relationship, whether the sample is “above” or “below” the next related field.',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1606,'SAMPLE_RELATIONSHIP_REFERENCE',3,26,null,null,'Sample Relationship Reference','Another sample, either already in FRED or in this spreadsheet.',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1602,'SAMPLES_NEARBY',3,22,null,null,'Samples Nearby','List the field numbers (as per the “Field number”) column of nearby samples. This is a multi-value column; enter multiple field numbers, one per cell, in a vertical list under this row.',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1582,'FEATURE_NAME',3,2,null,null,'Section Name','Enter the name of a previously entered and approved vertical section.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1595,null,3,15,null,'#SENT_TO$COMMENTS','Sent To – Comment',null,3,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1594,null,3,14,null,'#SENT_TO$LAB_ID$LAB_NAME','Sent To – Organisation','Which lab was this fossil sent to?',6,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1593,null,3,13,null,'#SENT_TO$PERSON_ID$NAME','Sent To – Person','Enter the person that this fossil was sent to. If the person is not found, a new person will be created in FRED; the name must match exactly.',6,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1592,null,3,12,null,'#SENT_TO$FOSSIL_GROUP_ID$NAME','Sent To – Study Area','Which study area was this fossil sent to?',6,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1626,null,3,46,null,'COLOUR_MODIFIER_ID$NAME','Shade',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1596,null,3,16,null,'SIGNIFICANCE','Significance / Comments','Enter how this collection is significant.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1608,'STRAT_RELATIONSHIP_DISTANCE',3,28,null,null,'Strat. Relationship Distance','The distance or distance range of the stratigraphic relationship, in metres. Use decimal numbers; for a range use a dash, e.g. “1.0m – 1.1m”',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1607,'STRAT_RELATIONSHIP_MOD',3,27,null,null,'Strat. Relationship Mod','Choose one of “c.” or “?”, or leave blank.
',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1609,'STRAT_RELATIONSHIP_PREP',3,29,null,null,'Strat. Relationship Prep','For a Stratigraphic Relationship, a relation to a strategraphic reference horizon.',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1610,'STRAT_RELATIONSHIP_STRAT_UNIT',3,30,null,'#RELATIONSHIP$STRAT_UNIT_ID$SU_NAME_STANDARD','Strat. Relationship Unit','The stratigraphic unit in the Stratigraphic Relationship.',6,2,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1622,null,3,42,null,'SECONDARY_BEDDING_ID$NAME','Stratification - Internal Features Secondary',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1620,null,3,40,null,'BED_THICK_ID$NAME','Stratification – Bed Thickness',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1621,null,3,41,null,'PRIMARY_BEDDING_ID$NAME','Stratification – Internal Features Primary',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1616,null,3,36,null,'STRAT_COMMENTS','Stratigraphic Comments','Enter any other brief observations of the stratigraphy.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1597,'STRAT_UNIT',3,17,null,'STRAT_UNIT','Stratigraphic Name','Enter the stratigraphic unit. This must match exactly an entry from StratLex.',3,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1614,null,3,34,null,'STRIKE','Strike','The strike direction, in degrees true (leave blank for horizontal strata)',2,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1585,null,3,5,null,'TOP_DEPTH','Top Depth','Enter the top depth of the sample.',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1623,null,3,43,null,'WEATHERING_ID$NAME','Weathering',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1629,null,3,49,null,'WET$CODE','Wet / Dry',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1583,'WORKING_COMMENTS',3,3,null,null,'Working comments','This gets added to the audit trail for this entry.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1684,'ADDITIONAL_FEATURES',4,50,null,'#SEDIMENTARY_FEATURE$SED_FEATURE_ID$NAME','Additional Features',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1685,'ABUNDANT',4,51,null,'#SEDIMENTARY_FEATURE$ABUNDANT','Additional Features – Abundant?','Whether the lithology is abundant',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1637,null,4,3,null,'BOTTOM_DEPTH','Bottom Depth','This gets added to the audit trail for this entry.',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1679,null,4,45,null,'CARBONATE_ID$NAME','Carbonate',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1641,null,4,7,null,'COLLECTION_DATE','Collection Date','When was the collection collected?',10,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1643,'COLLECTOR_NAME',4,9,null,'#COLLECTOR$PERSON_ID$NAME','Collectors','Enter the collector. If not in the list, enter a new name. Names must match the exact spelling as the entry in FRED.',null,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1681,null,4,47,null,'PRIMARY_COLOUR_ID$NAME','Colour (Primary)',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1682,null,4,48,null,'SECONDARY_COLOUR_ID$NAME','Colour (Secondary)',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1665,'MAP_SHEET',4,31,null,'COLUMN_MAP','Column / Map','If available, enter a reference where a map or column of the collection site may be found, ideally reference to an entry in the New Zealand Measured Section database.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1673,'COMPARATOR_USED',4,39,null,'COMPARATOR_USED','Comparator Used','Specify whether a grain-size comparator was used.
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1688,null,4,54,null,'CORRESPONDENCE','Correspondence, Cross-References','Use this field to reference any correspondence, publications or other information source.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1642,null,4,8,null,'DATE_ROUNDING$CODE','Date Rounding','What is the accuracy of the date?',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1639,null,4,5,null,'DEPTH_UNIT$CODE','Depth Unit','Enter the top depth of the sample.',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1666,null,4,32,null,'DIP','Dip','Enter the dip, in degrees (0 for horizontal strata).
',2,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1667,null,4,33,null,'DIP_DIRECTION$CODE','Dip Direction','Enter the cardinal direction of the dip.
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1636,'FEATURE_NAME',4,2,null,null,'Drill Hole FRN','Enter the Fossil Record Number for a previously entered and approved drill hole.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1669,null,4,35,null,'FACING$CODE','Facing','Enter normal or overturned.
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1635,'FOLDER',4,1,null,null,'Folder','Folders are your groupings of imported data within FRED. Just choose one.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1644,null,4,10,null,'IN_PLACE$CODE','Fossils in Place',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1671,null,4,37,null,'PRIMARY_GRAINSIZE_ID$NAME','Grain Size (primary)','Enter the primary grain size.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1672,null,4,38,null,'SECONDARY_GRAINSIZE_ID$NAME','Grain Size (secondary)','Enter the secondary grain size.
',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1678,null,4,44,null,'HARDNESS_ID$NAME','Hardness',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1686,'INFERRED_ENVIRONMENT',4,52,null,'DEPOSITION_ENV','Inferred Environment','Enter one of “Marine” or “Non Marine”',3,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1654,'INFERRED_STAGE_LOWER',4,20,null,null,'Inferred Stage Limits – From','Enter the lower bound for the inferred stage interval.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1655,'INFERRED_STAGE_UPPER',4,21,null,null,'Inferred Stage Limits – To','Enter the upper bound for the inferred stage interval.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1652,'KNOWN_STAGE_LOWER',4,18,null,null,'Known Stage Limits – From','Enter the lower bound for the known stage interval.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1653,'KNOWN_STAGE_UPPER',4,19,null,null,'Known Stage Limits – To','Enter the upper bound for the known stage interval.
',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1687,null,4,53,null,'ROCK_NATURE','Nature of Rock Unit','Enter any additional notes on the nature of the rock.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1645,null,4,11,null,'NOT_COLLECTED','Not Collected','List the fossils not collected. This is free text.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1658,'SAMPLE_RELATIONSHIP_DISTANCE',4,24,null,null,'Sample Relationship Distance','The distance or distance range of the sample relationship, in metres. Use decimal numbers; for a range use a dash, e.g. “1.0m – 1.1m”',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1657,'SAMPLE_RELATIONSHIP_MOD',4,23,null,null,'Sample Relationship Mod','Choose one of “c.” or “?”, or leave blank.
',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1659,'SAMPLE_RELATIONSHIP_PREP',4,25,null,null,'Sample Relationship Prep','For a Sample Relationship, whether the sample is “above” or “below” the next related field.',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1660,'SAMPLE_RELATIONSHIP_REFERENCE',4,26,null,null,'Sample Relationship Reference','Another sample, either already in FRED or in this spreadsheet.',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1640,null,4,6,null,'DRILL_TYPE_ID$NAME','Sample Type','Enter the unit used in measuring the top and bottom depths.',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1656,'SAMPLES_NEARBY',4,22,null,null,'Samples Nearby','List the field numbers (as per the “Field number”) column of nearby samples. This is a multi-value column; enter multiple field numbers, one per cell, in a vertical list under this row.',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1649,null,4,15,null,'#SENT_TO$COMMENTS','Sent To – Comment',null,3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1648,null,4,14,null,'#SENT_TO$LAB_ID$LAB_NAME','Sent To – Organisation','Which lab was this fossil sent to?',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1647,null,4,13,null,'#SENT_TO$PERSON_ID$NAME','Sent To – Person','Enter the person that this fossil was sent to. If the person is not found, a new person will be created in FRED; the name must match exactly.',6,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1646,null,4,12,null,'#SENT_TO$FOSSIL_GROUP_ID$NAME','Sent To – Study Area','Which study area was this fossil sent to?',6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1680,null,4,46,null,'COLOUR_MODIFIER_ID$NAME','Shade',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1650,null,4,16,null,'SIGNIFICANCE','Significance / Comments','Enter how this collection is significant.',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1662,'STRAT_RELATIONSHIP_DISTANCE',4,28,null,null,'Strat. Relationship Distance','The distance or distance range of the stratigraphic relationship, in metres. Use decimal numbers; for a range use a dash, e.g. “1.0m – 1.1m”',3,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1661,'STRAT_RELATIONSHIP_MOD',4,27,null,null,'Strat. Relationship Mod','Choose one of “c.” or “?”, or leave blank.
',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1663,'STRAT_RELATIONSHIP_PREP',4,29,null,null,'Strat. Relationship Prep','For a Stratigraphic Relationship, a relation to a strategraphic reference horizon.',6,null,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1664,'STRAT_RELATIONSHIP_STRAT_UNIT',4,30,null,'#RELATIONSHIP$STRAT_UNIT_ID$SU_NAME_STANDARD','Strat. Relationship Unit','The stratigraphic unit in the Stratigraphic Relationship.',6,2,1,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1676,null,4,42,null,'SECONDARY_BEDDING_ID$NAME','Stratification - Internal Features Secondary',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1674,null,4,40,null,'BED_THICK_ID$NAME','Stratification – Bed Thickness',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1675,null,4,41,null,'PRIMARY_BEDDING_ID$NAME','Stratification – Internal Features Primary',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1670,null,4,36,null,'STRAT_COMMENTS','Stratigraphic Comments','Enter any other brief observations of the stratigraphy.
',3,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1651,'STRAT_UNIT',4,17,null,'STRAT_UNIT','Stratigraphic Name','Enter the stratigraphic unit. This must match exactly an entry from StratLex.',3,2,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1668,null,4,34,null,'STRIKE','Strike','Enter the strike direction, in degrees true (leave blank for horizontal strata).
',2,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1638,null,4,4,null,'TOP_DEPTH','Top Depth','Enter the bottom depth of the sample.',null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1677,null,4,43,null,'WEATHERING_ID$NAME','Weathering',null,6,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1683,null,4,49,null,'WET$CODE','Wet / Dry',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1691,null,5,3,null,'BOTTOM_DEPTH','Bottom Depth',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1703,null,5,15,null,null,'Collection Comments',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1695,null,5,7,null,'COLLECTION_DATE','Collection Date',null,10,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1693,null,5,5,null,'DEPTH_UNIT$CODE','Depth Unit',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1689,'FOLDER',5,1,null,null,'Folder',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1690,null,5,2,null,null,'Fossil Record Number',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1696,null,5,8,null,null,'Identifiers',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1702,null,5,14,null,null,'Lab Number',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1701,null,5,13,null,null,'Laboratory and Series',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1700,null,5,12,null,null,'Stage Comments',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1697,null,5,9,null,null,'Stage – Start',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1698,null,5,10,null,null,'Stage – Start Modifier',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1699,null,5,11,null,null,'Stage – Stop',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1705,null,5,17,null,null,'TODO Custom taxons and stuff',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1704,null,5,16,null,null,'Taxonomic Headings',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1692,null,5,4,null,'TOP_DEPTH','Top Depth',null,null,null,0,null,null);
Insert into MG_IMPORT_COLUMN_TYPE (ID,CODE,MG_IMPORT_SHEET_TYPE,DISP_ORDER,MG_COLUMN,MG_COLUMN_CODE,HEADING,DESCRIPTION,MG_COLUMN_TYPE,MG_IMPORT_COLUMN_BEHAVIOUR,IS_MULTIVALUE,PARAM_NUMBER,PARAM_STRING) values (1694,'WORKING_COMMENTS',5,6,null,null,'Working comments',null,null,null,0,null,null);

    
alter table registration_area add disp_order number(4) default 1;
insert into mg_column (id, mg_table, mg_column_type, code, order_by_rank) values (mg_column_id_seq.nextval, (select id from mg_table where code='REGISTRATION_AREA'), 1, 'DISP_ORDER', 1);
update registration_area set disp_order = 	2	 where code = 'AP';
update registration_area set disp_order = 	3	 where code = 'AK';
update registration_area set disp_order = 	4	 where code = 'BT';
update registration_area set disp_order = 	5	 where code = 'CA';
update registration_area set disp_order = 	6	 where code = 'CH';
update registration_area set disp_order = 	7	 where code = 'CK';
update registration_area set disp_order = 	8	 where code = 'FJ';
update registration_area set disp_order = 	9	 where code = 'KE';
update registration_area set disp_order = 	10	 where code = 'LH';
update registration_area set disp_order = 	11	 where code = 'MQ';
update registration_area set disp_order = 	1	 where code = 'NZ';
update registration_area set disp_order = 	13	 where code = 'NC';
update registration_area set disp_order = 	14	 where code = 'NU';
update registration_area set disp_order = 	15	 where code = 'NR';
update registration_area set disp_order = 	16	 where code = 'OT';
update registration_area set disp_order = 	17	 where code = 'PG';
update registration_area set disp_order = 	18	 where code = 'RS';
update registration_area set disp_order = 	19	 where code = 'SA';
update registration_area set disp_order = 	20	 where code = 'SN';
update registration_area set disp_order = 	21	 where code = 'TL';
update registration_area set disp_order = 	22	 where code = 'TG';
update registration_area set disp_order = 	23	 where code = 'VA';

insert into data_origin (origin_id, name, description) values (910, '>=2019 Excel template', 'Entry using Excel spreadsheet template.');

-------------------------------------
--In the SC database:
--alter table sc.method add disp_order number(4) default 1;
--update method set disp_order = 1 where method='GPS - Field';
--update method set disp_order = 2 where method='GPS - Differential';
--update method set disp_order = 3 where method='Map - 1:50,000 scale';
--update method set disp_order = 4 where method='Air Photo';
--update method set disp_order = 5 where method='Altimeter';
--update method set disp_order = 6 where method='Country known only';
--update method set disp_order = 7 where method='Google Elevation';
--update method set disp_order = 8 where method='Location name';
--update method set disp_order = 9 where method='Map - 1:10,000 scale';
--update method set disp_order = 10 where method='Map - <=1:25,000 scale';
--update method set disp_order = 11 where method='Map - 1:63,360 scale';
--update method set disp_order = 12 where method='Map - 1:100,000 scale';
--update method set disp_order = 13 where method='Map - >=1:250,000 scale';
--update method set disp_order = 14 where method='Map - scale not specified';
--update method set disp_order = 15 where method='Map sheet known only';
--update method set disp_order = 16 where method='Remote site';
--update method set disp_order = 17 where method='Surveyed';
--update method set disp_order = 18 where method='Verbal description';

