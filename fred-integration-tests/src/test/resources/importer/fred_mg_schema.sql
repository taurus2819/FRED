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
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 6, 'FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 6, 'YARD_FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE'), 3, 'DEPTH_UNIT');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE_META'), 6, 'FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FEATURE_META'), 1, 'META_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER'), 1, 'FOLDER_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FOLDER'), 3, 'NAME');
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
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'MAP_SHEET');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'SERIAL_NUMBER');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'RECOLLECTION_NUMBER');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'FRNUM_COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'FR_NUMBER');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'OBSOLETE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='FR_NUMBER'), 3, 'DELETE_FLAG');
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
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PERSON'), 1, 'PERSON_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PERSON'), 3, 'ST_CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='PERSON'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD'), 1, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD'), 6, 'SAMPLE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD'), 6, 'AUDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD'), 6, 'PAL_LIST_AUDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD_META'), 6, 'RECORD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RECORD_META'), 1, 'META_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='REGISTRATION_AREA'), 1, 'REG_AREA_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='REGISTRATION_AREA'), 3, 'NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='REGISTRATION_AREA'), 3, 'CODE');

insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 1, 'RELATIONSHIP_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 6, 'RELATIONSHIP_TYPE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 6, 'RELATED_FEATURE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 3, 'STRAT_UNIT');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 3, 'DISTANCE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 3, 'DISTANCE_MOD');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 6, 'RELATION_TYPE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='RELATIONSHIP'), 3, 'DISTANCE_RANGE');
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
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'TOP_DEPTH');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'BOTTOM_DEPTH');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'DRILL_TYPE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 3, 'COMMENTS');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'DRILL_TYPE_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 6, 'YARD_FR_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 7, 'AUDIT_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SAMPLE'), 4, 'COLLECTION_DATE');
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
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SEDIMENTARY_FEATURE'), 3, 'ABUNDANT');
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
insert into mg_column (id, mg_table, mg_column_type, code, sequence) values (mg_column_id_seq.nextval, (select id from mg_table where code='STAGE'), 9, 'STAGE_ID', 'STAGE_SEQ');
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
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.LAB'), 3, 'LAB_NAME');


CREATE TABLE LU_COORD_SYSTEM (
        ORIG_SYSTEM_ID NUMBER(3) PRIMARY KEY REFERENCES SC.ORIG_SYSTEM(SYSTEM_ID),
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

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SC.METHOD', 'Coordinate System');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.METHOD'), 1, 'METHOD_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SC.METHOD'), 3, 'METHOD');

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'MIS.COUNTRY', 'Country');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='MIS.COUNTRY'), 3, 'COUNTRY_CODE');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='MIS.COUNTRY'), 3, 'COUNTRY_NAME');

insert into mg_table (id, code, name) values (mg_table_id_seq.nextval, 'SL.STRAT_UNIT', 'Strat unit');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SL.STRAT_UNIT'), 1, 'SU_ID');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SL.STRAT_UNIT'), 3, 'SU_NAME');
insert into mg_column (id, mg_table, mg_column_type, code) values (mg_column_id_seq.nextval, (select id from mg_table where code='SL.STRAT_UNIT'), 3, 'SU_NAME_STANDARD');

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


INSERT INTO MG_IMPORT_SPREADSHEET_TYPE(ID, CODE, NAME)
VALUES (MG_IMPORT_SS_TYPE_ID_SEQ.NEXTVAL, 'FRED_OUTCROP', 'FRED Outcrop');

insert into MG_IMPORT_SHEET_TYPE (ID, CODE,  NAME, MG_TABLE, ROW_DATA_START, MG_IMPORT_SPREADSHEET_TYPE) 
values (mg_import_s_type_id_seq.nextval, 'FRED_OUTCROP', 'Outcrop', (SELECT ID FROM MG_TABLE WHERE CODE='SAMPLE'), 1, (SELECT ID FROM MG_IMPORT_SPREADSHEET_TYPE WHERE CODE='FRED_OUTCROP'));

INSERT INTO MG_IMPORT_SPREADSHEET_TYPE(ID, CODE, NAME)
VALUES (MG_IMPORT_SS_TYPE_ID_SEQ.NEXTVAL, 'FRED_VERTICAL_SECTION', 'FRED Vertical Section');

insert into MG_IMPORT_SHEET_TYPE (ID, CODE,  NAME, MG_TABLE, ROW_DATA_START, MG_IMPORT_SPREADSHEET_TYPE) 
values (mg_import_s_type_id_seq.nextval, 'VERTICAL_SECTION', 'Vertical Section', (SELECT ID FROM MG_TABLE WHERE CODE='SAMPLE'), 1, (SELECT ID FROM MG_IMPORT_SPREADSHEET_TYPE WHERE CODE='FRED_VERTICAL_SECTION'));

INSERT INTO MG_IMPORT_SPREADSHEET_TYPE(ID, CODE, NAME)
VALUES (MG_IMPORT_SS_TYPE_ID_SEQ.NEXTVAL, 'FRED_DRILL_HOLE', 'FRED Drill Hole');

insert into MG_IMPORT_SHEET_TYPE (ID, CODE,  NAME, MG_TABLE, ROW_DATA_START, MG_IMPORT_SPREADSHEET_TYPE) 
values (mg_import_s_type_id_seq.nextval, 'DRILL_HOLE', 'Drill Hole', (SELECT ID FROM MG_TABLE WHERE CODE='SAMPLE'), 1, (SELECT ID FROM MG_IMPORT_SPREADSHEET_TYPE WHERE CODE='FRED_DRILL_HOLE'));

INSERT INTO MG_IMPORT_SPREADSHEET_TYPE(ID, CODE, NAME)
VALUES (MG_IMPORT_SS_TYPE_ID_SEQ.NEXTVAL, 'FRED_PALEO', 'FRED Paleontological Analysis');

insert into MG_IMPORT_SHEET_TYPE (ID, CODE,  NAME, MG_TABLE, ROW_DATA_START, MG_IMPORT_SPREADSHEET_TYPE) 
values (mg_import_s_type_id_seq.nextval, 'PALEO', 'Paleontological Analysis', (SELECT ID FROM MG_TABLE WHERE CODE='SAMPLE'), 1, (SELECT ID FROM MG_IMPORT_SPREADSHEET_TYPE WHERE CODE='FRED_PALEO'));

