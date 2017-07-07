--ALTER TABLE FR.SAMPLE ADD (AUTO_AGE_WIDE NUMBER(7) references stage(stage_id));
--ALTER TABLE FR.SAMPLE ADD (AUTO_AGE_NARROW NUMBER(7) references stage(stage_id));

-- This is how you define constants in Oracle
CREATE OR REPLACE FUNCTION "FR"."CONST_MAX_BASE_AGE" 
-- Part of squirrel_age_view
        return number 
        is
        
        begin
                return (999.9);
        end;

CREATE OR REPLACE FUNCTION "FR"."CONST_MIN_TOP_AGE"
-- Part of squirrel_age_view
RETURN number
AS
BEGIN
  RETURN 0.0;
END;


-- Arbitrary, guessed, default identification dates.
create table default_identification_date (person_id number(6), identification_date date);
ALTER TABLE FR.DEFAULT_IDENTIFICATION_DATE ADD PRIMARY KEY (PERSON_ID, IDENTIFICATION_DATE);
comment on table default_identification_date is 'Used by squirrel_age_view.';
insert into default_identification_date (person_id, identification_date) values (91, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (804, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (9977, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (8830, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (103, to_date('2000-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (139, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (7936, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (8755, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (8816, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (737, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (179, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (341, to_date('1950-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (186, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (332, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (3129, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (2723, to_date('1950-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (269, to_date('1950-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (3790, to_date('1960-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (354, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (982, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (386, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (3291, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (389, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (4108, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (2447, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (1824, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (500, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (2428, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (721, to_date('1940-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (2886, to_date('1940-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (447, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (4696, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (8289, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (8288, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (3896, to_date('1960-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (2454, to_date('1960-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (12676, to_date('1960-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (1092, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (1165, to_date('2010-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (9316, to_date('2010-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (550, to_date('2010-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (52, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (64, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (4974, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (11436, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (3352, to_date('2010-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (12660, to_date('2010-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (10417, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (1097, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (198, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (4629, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (217, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (237, to_date('1960-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (2337, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (10556, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (3409, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (3359, to_date('1980-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (921, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (2431, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (3501, to_date('1960-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (3407, to_date('1970-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (399, to_date('1990-01-01', 'YYYY-MM-DD'));
insert into default_identification_date (person_id, identification_date) values (12661, to_date('1990-01-01', 'YYYY-MM-DD'));


-- Testing how to get the const value:
--select const_max_base_age from dual;
        
create or replace view stage_fixed_view as
-- Part of squirrel_age_view
select stage_id, stage_lower_mod, stage_upper_mod, stage_mod, age_lower_id, nvl(age_upper_id, age_lower_id) as age_upper_id
from stage;   




create or replace view latest_adoption_view as
-- Part of squirrel_age_view
select r.sample_id, s.age_lower_id as adopted_age_lower, s.age_upper_id as adopted_age_upper
from record r
join adoption ad on ad.record_id=r.record_id
join stage_fixed_view s on s.stage_id=ad.adopted_stage_id
where ad.adoption_date in (
        select max(ad2.adoption_date)
        from adoption ad2
        join record r2 on r2.record_id=ad2.record_id
        where r2.sample_id=r.sample_id
);
comment on table latest_adoption_view is 'List the latest adoption, if there is one, for each sample, where the adoption_date is used to determine its newness.';


create or replace view paleontology_fixed_view as
-- Part of squirrel_age_view
select p.RECORD_ID,
    nvl(p.identification_date, nvl(did.identification_date, to_date('1850-01-01', 'YYYY-MM-DD'))) as identification_date,
    p.DATE_ROUNDING,
    p.STAGE_ID,
    p.STAGE_COMMENTS,
    p.LAB_SECTION_ID,
    p.LAB_NUMBER,
    p.COLLECTION_COMMENTS,
    p.PAL_ID
from paleontology p
left outer join identifier i on i.record_id=p.record_id
left outer join default_identification_date did on did.person_id=i.person_id;



-- auto age wide 
create or replace view auto_age_wide_view as
-- Part of squirrel_age_view
-- Calculate the squirrel wide ages for each sample.
select sample_id, min(top_age) as wide_age_upper, max(base_age) as wide_age_lower
from (
select sample_id, top_age, base_age
from (
        -- determined ages
        select r.sample_id, a_upper.top_age as top_age, a_lower.base_age as base_age
        from record r
        join paleontology_fixed_view p on p.record_id=r.record_id
        join pal_list pl on pl.record_id=p.record_id
        join stage_fixed_view s on s.stage_id=p.stage_id
        left join age a_lower on (a_lower.age_id=s.age_lower_id and a_lower.base_age <> CONST_MAX_BASE_AGE)
        left join age a_upper on (a_upper.age_id=s.age_upper_id and a_upper.top_age <> CONST_MIN_TOP_AGE)
        where pl.group_id in (328, 301, 323, 322, 306, 348, 303, 314, 324, 342, 327, 313, 312, 326, 316, 325, 311, 334)
) union (
        -- include inferred age in the min/max
        select sa.sample_id, a2_upper.top_age as top_age, a2_lower.base_age as base_age
        from sample sa 
        join stage_fixed_view s2 on s2.stage_id=sa.inferred_stage_id
        left join age a2_lower on (a2_lower.age_id=s2.age_lower_id and a2_lower.base_age <> CONST_MAX_BASE_AGE)
        join age a2_upper on (a2_upper.age_id=s2.age_upper_id and a2_upper.top_age <> CONST_MIN_TOP_AGE)
) union (
        -- include known age in the min/max
        select sa.sample_id, a3_upper.top_age as top_age, a3_lower.base_age as base_age
        from sample sa 
        join stage_fixed_view s3 on s3.stage_id=sa.known_stage_id
        left join age a3_lower on (a3_lower.age_id=s3.age_lower_id and a3_lower.base_age <> CONST_MAX_BASE_AGE)
        left join age a3_upper on (a3_upper.age_id=s3.age_upper_id and a3_upper.top_age <> CONST_MIN_TOP_AGE)
))
group by sample_id;


create or replace view auto_age_wide_dtrmnd as
-- Part of squirrel_age_view
-- I'm only used by auto_age_narrow_view; I do a subset of auto_age_wide_view.
select sample_id, min(top_age) as wide_age_upper, max(base_age) as wide_age_lower
from (
select sample_id, top_age, base_age
from (
        -- determined ages
        select r.sample_id, a_upper.top_age as top_age, a_lower.base_age as base_age
        from record r
        join paleontology_fixed_view p on p.record_id=r.record_id
        join pal_list pl on pl.record_id=p.record_id
        join stage_fixed_view s on s.stage_id=p.stage_id
        left join age a_lower on (a_lower.age_id=s.age_lower_id and a_lower.base_age <> CONST_MAX_BASE_AGE)
        left join age a_upper on (a_upper.age_id=s.age_upper_id and a_upper.top_age <> CONST_MIN_TOP_AGE)
        where pl.group_id in (328, 301, 323, 322, 306, 348, 303, 314, 324, 342, 327, 313, 312, 326, 316, 325, 311, 334)
) )
group by sample_id;


-- Auto age narrow. 
create or replace view auto_age_narrow_view as
-- Part of squirrel_age_view
-- Calculate the narrow ages for each sample.
select sample_id, min_lower_base_age as narrow_age_lower, max_upper_top_age as narrow_age_upper
from (
select sample_id, min(lower_base_age) as min_lower_base_age, max(upper_top_age) as max_upper_top_age
from (
        -- Only include entries with the latest identification_date/record_id per group.
        -- TODO: fix the identification dates.
        select r.sample_id, 
                a_lower.base_age as lower_base_age, 
                a_upper.top_age as upper_top_age,
                row_number() over (partition by r.sample_id, pl.group_id order by p.identification_date desc, r.record_id desc ) as dest_rank
        from record r
        join paleontology_fixed_view p on p.record_id=r.record_id
        join pal_list pl on pl.record_id=p.record_id
        join stage_fixed_view s on s.stage_id=p.stage_id
        left join age a_lower on (a_lower.age_id = s.age_lower_id and a_lower.base_age <> CONST_MAX_BASE_AGE)
        left join age a_upper on (a_upper.age_id = s.age_upper_id and a_upper.top_age <> CONST_MIN_TOP_AGE)
        where pl.group_id in (328, 301, 323, 322, 306, 348, 303, 314, 324, 342, 327, 313, 312, 326, 316, 325, 311, 334)
)
where dest_rank=1 -- exclude rows with old identification_date or record_id
group by sample_id
)
where min_lower_base_age > max_upper_top_age; -- exclude samples with no age overlap.


-- The main view of all of this.
create or replace view squirrel_age_view as 
-- Implementation of "squirrel ages" (mikevdg's name) or "auto-consensus ages" (official name).
-- For each sample, calculate a 'narrow' and 'wide' age range by gathering ages from various places.
-- The logic here is non-trivial. Refer to the documentation for more information.
-- Jira PDB-140.
select s.sample_id,
        nvl(vlaal.base_age, nvl(vaan.narrow_age_lower, nvl(aawd.wide_age_lower, nvl(ksal.base_age, nvl(isl.base_age, const_max_base_age))))) as NARROW_BASE_AGE,
        nvl(vlaau.top_age, nvl(vaan.narrow_age_upper, nvl(aawd.wide_age_upper, nvl(ksau.top_age, nvl(isu.top_age, const_min_top_age))))) as NARROW_TOP_AGE,
        nvl(vlaal.base_age, nvl(vaaw.wide_age_lower, const_max_base_age)) as WIDE_BASE_AGE,
        nvl(vlaau.top_age, nvl(vaaw.wide_age_upper, const_min_top_age)) as WIDE_TOP_AGE
from sample s
-- adoption
left outer join latest_adoption_view vla on vla.sample_id=s.sample_id
left outer join age vlaal on vlaal.age_id=vla.adopted_age_lower
left outer join age vlaau on vlaau.age_id=vla.adopted_age_upper
-- determined
left outer join auto_age_wide_view vaaw on vaaw.sample_id=s.sample_id
left outer join auto_age_narrow_view vaan on vaan.sample_id=s.sample_id
left outer join auto_age_wide_dtrmnd aawd on aawd.sample_id=s.sample_id
-- known 
left outer join stage_fixed_view known_stage on known_stage.stage_id=s.known_stage_id
left outer join age ksal on ksal.age_id=known_stage.age_lower_id
left outer join age ksau on ksau.age_id=known_stage.age_upper_id
-- inferred 
left outer join stage_fixed_view inferred_stage on inferred_stage.stage_id=s.inferred_stage_id
left outer join age isl on isl.age_id=inferred_stage.age_lower_id
left outer join age isu on isu.age_id=inferred_stage.age_upper_id;

create view squirrel_sample_view as
select s.*, a.narrow_base_age, a.narrow_top_age, a.wide_base_age, a.wide_top_age from sample s
join squirrel_age_view a on a.sample_id=s.sample_id;



-- An existing view that we're updating with squirrel ages.
CREATE or replace VIEW SAMPLE_STAGE_VIEW
 AS
SELECT s.sample_id,
    'inferred' AS type,
    st.stage_id,
    st.base_age,
    st.top_age
FROM sample s
JOIN stage st ON s.inferred_stage_id = st.stage_id
UNION
SELECT s.sample_id,
    'known' AS type,
    st.stage_id,
    st.base_age,
    st.top_age
FROM sample s
JOIN stage st ON s.known_stage_id = st.stage_id
UNION
SELECT s.sample_id,
    'adoption' AS type,
    st.stage_id,
    st.base_age,
    st.top_age
FROM sample s
JOIN record r   ON s.sample_id = r.sample_id
JOIN adoption a ON r.record_id = a.record_id
JOIN stage st   ON a.adopted_stage_id = st.stage_id
UNION
SELECT s.sample_id,
    'paleontology' AS type,
    st.stage_id,
    st.base_age,
    st.top_age
FROM sample s
JOIN record r       ON s.sample_id = r.sample_id
JOIN paleontology p ON r.record_id = p.record_id
JOIN stage st       ON p.stage_id = st.stage_id
UNION
-- See PDB-140.sql
SELECT sample_id,
    'squirrelWide' AS type,
    null as stage_id,
     wide_base_age as base_age,
     wide_top_age as top_age
FROM squirrel_age_view
UNION
-- See PDB-140.sql.
SELECT sample_id,
    'squirrelNarrow' AS type,
    null as stage_id,
    narrow_base_age as base_age,
    narrow_top_age as top_age
FROM squirrel_age_view;
