-- TODO: on delete cascade.
-- TODO: more uniqueness constraints on table names, column names, etc.

/*
DROP TABLE MG_IMPORT_ERROR;
DROP TABLE MG_IMPORT_VALUE;
DROP TABLE MG_IMPORT_COLUMN;
DROP TABLE MG_IMPORT_SHEET;
DROP TABLE MG_IMPORT_COLUMN_TYPE;
DROP TABLE MG_IMPORT_SHEET_TYPE;
DROP TABLE MG_COLUMN_FOREIGN_KEY;
DROP TABLE MG_COLUMN;
DROP TABLE MG_COLUMN_TYPE;
drop table mg_import_spreadsheet_type;
DROP TABLE MG_IMPORT_COLUMN_BEHAVIOUR;
DROP TABLE MG_TABLE;

DROP sequence mg_table_id_seq;
DROP sequence mg_column_type_id_seq;
DROP sequence mg_column_id_seq;
DROP sequence mg_column_foreign_key_id_seq;
DROP sequence mg_import_column_id_seq;
DROP sequence mg_import_column_type_id_seq;
DROP sequence MG_IMPORT_SHEET_id_seq;
DROP sequence mg_import_ss_type_id_seq;
drop sequence mg_import_s_type_id_seq;
DROP sequence mg_import_c_behaviour_id_seq;
DROP sequence mg_import_value_seq;

*/

create sequence mg_table_id_seq;
create sequence mg_column_type_id_seq;
create sequence mg_column_id_seq;
create sequence mg_column_foreign_key_id_seq;
create sequence mg_import_column_id_seq;
create sequence mg_import_column_type_id_seq;
create sequence MG_IMPORT_SHEET_id_seq;
create sequence mg_import_ss_type_id_seq;
create sequence mg_import_s_type_id_seq;
create sequence mg_import_c_behaviour_id_seq;
create sequence mg_import_value_seq;


create table mg_table (
        id number(10) primary key,
        code varchar(40) not null unique,
        name varchar(80) not null ,
        description varchar(4000),
        is_readonly number(1) default 0 
);
        
create table mg_column_type (
        id number(10) primary key,
        name varchar(40),
        code varchar(40) not null unique,
        description varchar(4000)
);

insert into mg_column_type (id, code, name, description) values (1, 'INTEGER', 'Integer', 'A negative or positive integer');
insert into mg_column_type (id, code, name, description) values (2, 'FLOAT', 'Decimal', 'A floating point number');
insert into mg_column_type (id, code, name, description) values (3, 'TEXT', 'Text', 'A varchar in UTF-8.');
--insert into mg_column_type (id, code, name, description) values (mg_column_type_seq.nextval, 'DATE', 'A date, rounded to a whole day.');
insert into mg_column_type (id, code, name, description) values (4, 'TIMESTAMP', 'Timestamp', 'A date and time, at least to the nearest second.');
insert into mg_column_type (id, code, name, description) values (5, 'BOOLEAN', 'Yes/No', 'A number being either 1 (true) or 0 (false)');
insert into mg_column_type (id, code, name, description) values (6, 'SELECTION', 'Selection', 'A foreign key representing a selection from a list.');
insert into mg_column_type (id, code, name, description) values (7, 'COMPOSITION', 'Composition', 'A foreign key representing more of this object.');
insert into mg_column_type (id, code, name, description) values (8, 'REMOTE_COMPOSITION', 'Remote composition', 'An inwards foreign key to my table''s primary key, representing more of this object and assuming a 1:1 relationship is maintained.');
insert into mg_column_type (id, code, name, description) values (9, 'SERIAL', 'Auto-increment ID', 'Automatic incrementing ID column.');
        
        
create table mg_column (
        id number(10) primary key,
        mg_table number(10) references mg_table(id) on delete cascade,
        mg_column_type number(10) references mg_column_type(id),
        code varchar(40) not null,
        name varchar(80),
        description varchar(4000),
        is_primary_key number(1) default 0,
        sequence varchar(4000),
        is_readonly number(1) default 0,
        is_not_null number(1) default 0,
        include_in_grids number(1) default 1,
        order_by_rank number(2),
        text_max_length number(4),
        min_value number(38,10),
        max_value number(38,10),
        CONSTRAINT MG_COLUMN_t_c UNIQUE (MG_TABLE, CODE)
);

create table mg_column_foreign_key (
        from_mg_column number(10) primary key references mg_column(id) on delete cascade,
        to_mg_column number(10) references mg_column(id) on delete cascade,
        constraint mg_column_foreign_key_f_t unique(from_mg_column, to_mg_column)
);
     
create TABLE MG_IMPORT_SPREADSHEET_TYPE (
        ID NUMBER(10) PRIMARY KEY,
        CODE VARCHAR2(4000),
        NAME VARCHAR2(4000)
);

create table MG_IMPORT_SHEET_TYPE
    (
        id number(10) primary key,
        MG_IMPORT_SPREADSHEET_TYPE NUMBER(10) REFERENCES MG_IMPORT_SPREADSHEET_TYPE(ID) on delete cascade,
        code varchar2(4000),
        name varchar2(4000),
        description varchar2(4000),
        mg_table number(10) references mg_table(id),
        row_data_start number(10) default 1,
        row_data_end number(10),
        column_data_start number(10),
        column_data_end number(10),
        row_header number(10),
        disp_order number(4)
    );
comment on table MG_IMPORT_SHEET_TYPE
is
    'This table contains one entry for each sheet template within a spreadsheet.';





create table mg_import_column_behaviour (
    id number(10) primary key,
    name varchar(80),
    code varchar(80) not null,
    description varchar(4000)
);

insert into mg_import_column_behaviour (id, name, code, description)
values (5, 'Find in selection, else create', 'DDSERT', 'Find the value for this drop-down. If it doesn''t exist, add it to the drop-down list.');
insert into mg_import_column_behaviour (id, name, code, description)
values (7, 'Find value, else create.', 'UPDATE_IF_SET', 'If this column has a value, use it to find the row. Otherwise create a new one.');


create table mg_import_column_type
    (
        id number(10) primary key,
        code varchar2(4000),
        MG_IMPORT_SHEET_TYPE number(10) not null REFERENCES mg_import_sheet_type(id) on delete cascade,
        disp_order number(10) default 0,
        mg_column number(10) references mg_column(id),
        mg_column_code varchar(4000),
        heading varchar2(4000) not null,
        description varchar2(4000),
        mg_column_type number(10) references mg_column_type(id) ,
        mg_import_column_behaviour number(10) references mg_import_column_behaviour(id),
        param_number number(10),
        param_string varchar2(4000)
    );
comment on table mg_import_column_type
is
    'Mapping of column headings to codes. The codes are used to determine which (hard-coded) behaviour to do for each column.'
    ;
comment on column mg_import_column_type.heading is 'The column heading. This is matched to incoming spreadsheet headings, so I need a value even if duplicated with MG_COLUMN.NAME.';
comment on column mg_import_column_type.param_number is 'I''m here for your convenience and I can be anything you want me to be. Use me.';
comment on column mg_import_column_type.param_string is 'I''m here for your convenience and I can be anything you want me to be. Use me.';
comment on column mg_import_column_type.mg_column_type is 'If mg_column is not used, the type of this column.';
comment on column mg_import_column_type.mg_column is 'If mg_column_type is not used, a database table and column I pull my stuff from.';
comment on column mg_import_column_type.mg_column is 'For joins, the columns split by $.';


create table mg_import_error
    (
        message varchar2(4000),
        mg_import_column number(10),
        row_num number(10)
    );
comment on table mg_import_error
is
    'Errors that have occurred during a previous import attempt. Data in me is safe to delete.';


create table MG_IMPORT_SHEET
    (
        id number(10) primary key,
        filename varchar2(4000) not null,
        name varchar2(4000),
        created_date date default sysdate,
        user_ number(10),
        row_data_start number(10),
        row_data_end number(10),
        column_data_start number(10),
        column_data_end number(10),
        row_header number(10),
        MG_IMPORT_SHEET_TYPE number(10)
    );
comment on table MG_IMPORT_SHEET
is
    'Spreadsheets that have been imported into this holding area, but have not been processed into the database yet.'
    ;

-- TODO: remove this table?

create table mg_import_column
    (
        id number(10) primary key,
        MG_IMPORT_SHEET number(10) references MG_IMPORT_SHEET(ID) on delete cascade,
        column_num number(10),
        column_heading varchar2(4000)
    );
comment on table mg_import_column
is
    'I contain actual spreadsheet column information.';


create table mg_import_value
    (
        row_num number(10) not null,
        mg_import_column number(10) not null,
        value_string varchar2(4000),
        value_number number(38, 18),
        value_timestamp date,
        value_bool number(1),
        is_imported number(1) not null,
        deleted_date date default null,
        primary key (row_num, mg_import_column)
    );
comment on table mg_import_value
is
    'I contain uploaded spreadsheets.';
comment on column mg_import_value.deleted_date is 
'I am usually null.
 Set me to sysdate to delete a cell. I exist because updating something 
 in a database is far faster than deleting something. Eventually an actual
 delete will be required to clean out old data.';



alter table MG_IMPORT_SHEET add foreign key (MG_IMPORT_SHEET_TYPE) references
    MG_IMPORT_SHEET_TYPE (id);

alter table mg_import_value add foreign key (mg_import_column) references
    mg_import_column (id)
on
delete cascade;

alter table mg_import_value add constraint mg_import_value_is_imported check
    (is_imported in (0, 1));


insert into MG_TABLE (ID, CODE, NAME, DESCRIPTION, IS_READONLY) values (mg_table_id_seq.nextval, 'MG_COLUMN_TYPE', 'Column Type', 'Types that a column may have', 0);

insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY, SEQUENCE) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_COLUMN_TYPE'), 9, 'ID', null, null, 1, 'MG_COLUMN_TYPE_ID_SEQ');
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_COLUMN_TYPE'), 3, 'CODE', 'Type code', null, 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_COLUMN_TYPE'), 3, 'NAME', 'Type', null, 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_COLUMN_TYPE'), 3, 'DESCRIPTION', 'Type description', null, 0);


insert into MG_TABLE (ID, CODE, NAME, DESCRIPTION, IS_READONLY) values (mg_table_id_seq.nextval, 'MG_IMPORT_SHEET_TYPE', 'Sheet', 'Spreadsheet', 0);

insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY, SEQUENCE) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 9, 'ID', 'ID', 'a', 1, 'MG_IMPORT_S_TYPE_ID_SEQ');
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 1, 'ROW_DATA_START', 'First row', 'a', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 1, 'ROW_DATA_END', 'Last row', 'a', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 1, 'COLUMN_DATA_START', 'First column', 'a', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 1, 'COLUMN_DATA_END', 'Last column', 'a', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 3, 'CODE', 'Code', 'a', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 3, 'DESCRIPTION', 'Description', 'a', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 3, 'ROW_HEADER', 'Header', 'a', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 3, 'NAME', 'Sheet name', 'a', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 6, 'MG_IMPORT_SPREADSHEET_TYPE', 'Spreadsheet', 'a', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 1, 'DISP_ORDER', 'Display Order', 'a', 0);
-- TODO: make this a SFK:
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SHEET_TYPE'), 1, 'MG_TABLE', 'Table', 'a', 0);

insert into MG_TABLE (ID, CODE, NAME, DESCRIPTION, IS_READONLY) values (mg_table_id_seq.nextval, 'MG_IMPORT_COLUMN_BEHAVIOUR', 'Behaviour', null, 0);

insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY, SEQUENCE) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_BEHAVIOUR'), 9, 'ID', 'id', 'A', 1, 'MG_IMPORT_C_BEHAVIOUR_ID_SEQ');
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_BEHAVIOUR'), 3, 'NAME', 'Name', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_BEHAVIOUR'), 3, 'CODE', 'Code', 'A', 0);


insert into MG_TABLE (ID, CODE, NAME, DESCRIPTION, IS_READONLY) values (mg_table_id_seq.nextval, 'MG_IMPORT_COLUMN_TYPE', 'Column', 'Spreadsheet columns', 0);

insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY, SEQUENCE) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 9, 'ID', 'id', 'A', 1, 'MG_IMPORT_COLUMN_TYPE_ID_SEQ');
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 3, 'CODE', 'Code', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 6, 'MG_IMPORT_SHEET_TYPE', 'Spreadsheet sheet', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 1, 'DISP_ORDER', 'Display Order', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 3, 'HEADING', 'Heading', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 3, 'DESCRIPTION', 'Description', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 6, 'MG_COLUMN_TYPE', 'Column type', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 1, 'MG_COLUMN', 'Column', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 3, 'MG_COLUMN_CODE', 'Destination code', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 6, 'MG_IMPORT_COLUMN_BEHAVIOUR', 'Column', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 1, 'PARAM_NUMBER', 'Numeric parameter', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_COLUMN_TYPE'), 3, 'PARAM_STRING', 'Text parameter', 'A', 0);

insert into MG_TABLE (ID, CODE, NAME, DESCRIPTION, IS_READONLY) values (mg_table_id_seq.nextval, 'MG_IMPORT_SPREADSHEET_TYPE', 'Spreadsheet', 'Spreadsheet definition', 0);

insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY, SEQUENCE) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SPREADSHEET_TYPE'), 9, 'ID', 'id', 'A', 1, 'MG_IMPORT_SS_TYPE_ID_SEQ');
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SPREADSHEET_TYPE'), 3, 'CODE', 'Code', 'A', 0);
insert into MG_COLUMN (ID, MG_TABLE, MG_COLUMN_TYPE, CODE, NAME, DESCRIPTION, IS_PRIMARY_KEY) values (mg_column_id_seq.nextval, (select id from mg_table where code='MG_IMPORT_SPREADSHEET_TYPE'), 3, 'NAME', 'Spreadsheet name', 'A', 0);


INSERT INTO MG_COLUMN_FOREIGN_KEY (FROM_MG_COLUMN, TO_MG_COLUMN) values (
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_IMPORT_COLUMN_TYPE'
    and c.code='MG_IMPORT_SHEET_TYPE'),
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_IMPORT_SHEET_TYPE'
    and c.code='ID')
);


INSERT INTO MG_COLUMN_FOREIGN_KEY (FROM_MG_COLUMN, TO_MG_COLUMN) values (
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_IMPORT_COLUMN_TYPE'
    and c.code='MG_COLUMN_TYPE'),
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_COLUMN_TYPE'
    and c.code='ID')
);

INSERT INTO MG_COLUMN_FOREIGN_KEY (FROM_MG_COLUMN, TO_MG_COLUMN) values (
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_IMPORT_COLUMN_TYPE'
    and c.code='MG_IMPORT_COLUMN_BEHAVIOUR'),
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_IMPORT_COLUMN_BEHAVIOUR'
    and c.code='ID')
);

/* TODO
INSERT INTO MG_COLUMN_FOREIGN_KEY (FROM_MG_COLUMN, TO_MG_COLUMN) values (
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_IMPORT_SHEET_TYPE'
    and c.code='MG_TABLE'),
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_TABLE'
    and c.code='ID')
);*/


INSERT INTO MG_COLUMN_FOREIGN_KEY (FROM_MG_COLUMN, TO_MG_COLUMN) values (
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_IMPORT_SHEET_TYPE'
    and c.code='MG_IMPORT_SPREADSHEET_TYPE'),
(select c.id from mg_column c
    join mg_table t on t.id=c.mg_table
    where t.code='MG_IMPORT_SPREADSHEET_TYPE'
    and c.code='ID')
);


INSERT INTO MG_IMPORT_SPREADSHEET_TYPE(ID, CODE, NAME)
VALUES (MG_IMPORT_SS_TYPE_ID_SEQ.NEXTVAL, 'METAIMPORTER', 'Metaimporter');

insert into MG_IMPORT_SHEET_TYPE (ID, CODE,  NAME, MG_TABLE, ROW_DATA_START, MG_IMPORT_SPREADSHEET_TYPE) 
values (mg_import_s_type_id_seq.nextval, 'MG_IMPORT_SHEET_TYPE', 'Metaimporter', (SELECT ID FROM MG_TABLE WHERE CODE='MG_IMPORT_COLUMN_TYPE'), 1, (SELECT ID FROM MG_IMPORT_SPREADSHEET_TYPE WHERE CODE='METAIMPORTER'));

INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        HEADING,
        MG_COLUMN_TYPE,
        MG_COLUMN_CODE,
        DISP_ORDER
    )
    VALUES
    (
        mg_import_column_type_id_seq.nextval,
        null,
        (SELECT id
            FROM MG_IMPORT_SHEET_TYPE
            WHERE code='MG_IMPORT_SHEET_TYPE'),
        'Sheet template',
        6,
        'MG_IMPORT_SHEET_TYPE$NAME',
        1
    );


INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        HEADING,
        MG_COLUMN_TYPE,
        MG_COLUMN_CODE,
        DISP_ORDER
    )
    VALUES
    (
        mg_import_column_type_id_seq.nextval,
        null,
        (SELECT id
            FROM MG_IMPORT_SHEET_TYPE
            WHERE code='MG_IMPORT_SHEET_TYPE'),
        'Column name',
        3,
        'HEADING',
        2
    );

INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        HEADING,
        MG_COLUMN_TYPE,
        MG_COLUMN_CODE,
        DISP_ORDER
    )
    VALUES
    (
        mg_import_column_type_id_seq.nextval,
        null,
        (SELECT id
            FROM MG_IMPORT_SHEET_TYPE
            WHERE code='MG_IMPORT_SHEET_TYPE'),
        'Type',
        6,
        'MG_COLUMN_TYPE$NAME',
        3
    );

INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        HEADING,
        MG_COLUMN_TYPE,
        MG_COLUMN_CODE,
        DISP_ORDER
    )
    VALUES
    (
        mg_import_column_type_id_seq.nextval,
        null,
        (SELECT id
            FROM MG_IMPORT_SHEET_TYPE
            WHERE code='MG_IMPORT_SHEET_TYPE'),
        'Display Order',
        1,
        'DISP_ORDER',
        4
    );



INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        HEADING,
        MG_COLUMN_TYPE,
        MG_COLUMN_CODE,
        DISP_ORDER
    )
    VALUES
    (
        mg_import_column_type_id_seq.nextval,
        null,
        (SELECT id
            FROM MG_IMPORT_SHEET_TYPE
            WHERE code='MG_IMPORT_SHEET_TYPE'),
        'Code',
        3,
        'CODE',
        5
    );

INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        HEADING,
        MG_COLUMN_TYPE,
        MG_COLUMN_CODE,
        DISP_ORDER
    )
    VALUES
    (
        mg_import_column_type_id_seq.nextval,
        null,
        (SELECT id
            FROM MG_IMPORT_SHEET_TYPE
            WHERE code='MG_IMPORT_SHEET_TYPE'),
        'Column Code',
        3,
        'MG_COLUMN_CODE',
        6
    );

INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        HEADING,
        MG_COLUMN_TYPE,
        MG_COLUMN_CODE,
        DISP_ORDER
    )
    VALUES
    (
        mg_import_column_type_id_seq.nextval,
        null,
        (SELECT id
            FROM MG_IMPORT_SHEET_TYPE
            WHERE code='MG_IMPORT_SHEET_TYPE'),
        'Behaviour',
        6,
        'MG_IMPORT_COLUMN_BEHAVIOUR$NAME',
        7
    );
    
INSERT
INTO MG_IMPORT_COLUMN_TYPE
    (
        ID,
        CODE,
        MG_IMPORT_SHEET_TYPE,
        HEADING,
        MG_COLUMN_TYPE,
        MG_COLUMN_CODE,
        DISP_ORDER
    )
    VALUES
    (
        mg_import_column_type_id_seq.nextval,
        null,
        (SELECT id
            FROM MG_IMPORT_SHEET_TYPE
            WHERE code='MG_IMPORT_SHEET_TYPE'),
        'Description',
        3,
        'DESCRIPTION',
        8
);
