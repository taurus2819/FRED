--ALTER TABLE FR.SAMPLE ADD (AUTO_AGE_WIDE NUMBER(7) references stage(stage_id));
--ALTER TABLE FR.SAMPLE ADD (AUTO_AGE_NARROW NUMBER(7) references stage(stage_id));

-- This is how you define constants in Oracle
CREATE OR REPLACE FUNCTION "FR"."CONST_MAX_BASE_AGE" 
        return number 
        is
        
        begin
                return (999.9);
        end;

CREATE OR REPLACE FUNCTION "FR"."CONST_MIN_TOP_AGE"
RETURN number
AS
BEGIN
  RETURN 0.0;
END;

-- Testing how to get the const value:
select const_max_base_age from dual;
        
create or replace view squirrel_age_view as 
select s.sample_id,
        nvl(vlaal.base_age, nvl(vaan.narrow_age_lower, nvl(ksal.base_age, nvl(isl.base_age, const_max_base_age)))) as NARROW_BASE_AGE,
        nvl(vlaau.top_age, nvl(vaan.narrow_age_upper, nvl(ksau.top_age, nvl(isu.top_age, const_min_top_age)))) as NARROW_TOP_AGE,
        nvl(vlaal.base_age, nvl(vaaw.wide_age_lower, nvl(ksau.base_age, nvl(isl.base_age, const_max_base_age)))) as WIDE_BASE_AGE,
        nvl(vlaau.top_age, nvl(vaaw.wide_age_upper, nvl(ksau.top_age, nvl(isl.top_age, const_min_top_age)))) as WIDE_TOP_AGE
from sample s
-- adoption
left outer join latest_adoption_view vla on vla.sample_id=s.sample_id
left outer join age vlaal on vlaal.age_id=vla.adopted_age_lower
left outer join age vlaau on vlaau.age_id=vla.adopted_age_upper
-- determined
left outer join auto_age_wide_view vaaw on vaaw.sample_id=s.sample_id
left outer join auto_age_narrow_view vaan on vaan.sample_id=s.sample_id
-- known 
left outer join stage known_stage on known_stage.stage_id=s.known_stage_id
left outer join age ksal on ksal.age_id=known_stage.age_lower_id
left outer join age ksau on ksau.age_id=known_stage.age_upper_id
-- inferred 
left outer join stage inferred_stage on inferred_stage.stage_id=s.inferred_stage_id
left outer join age isl on isl.age_id=inferred_stage.age_lower_id
left outer join age isu on isu.age_id=inferred_stage.age_upper_id;


create or replace view latest_adoption_view as
select r.sample_id, s.age_lower_id as adopted_age_lower, s.age_upper_id as adopted_age_upper
from record r
join adoption ad on ad.record_id=r.record_id
join stage s on s.stage_id=ad.adopted_stage_id
where ad.adoption_date in (
        select max(ad2.adoption_date)
        from adoption ad2
        join record r2 on r2.record_id=ad2.record_id
        where r2.sample_id=r.sample_id
);
comment on table latest_adoption_view is 'List the latest adoption, if there is one, for each sample, where the adoption_date is used to determine its newness.';


create or replace view paleontology_fixed_view as
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
select sample_id, min(top_age) as wide_age_lower, max(base_age) as wide_age_upper
from (
select sample_id, top_age, base_age
from (
        -- determined ages
        select r.sample_id, a_upper.top_age as top_age, a_lower.base_age as base_age
        from record r
        join paleontology_fixed_view p on p.record_id=r.record_id
        join stage s on s.stage_id=p.stage_id
        join age a_lower on (a_lower.age_id=s.age_lower_id and a_lower.code<>'nd')
        join age a_upper on (a_upper.age_id=s.age_upper_id and a_upper.code<>'nd')
        -- TODO: but only for certain groups? How would this be done?
) union (
        -- include inferred age in the min/max
        select sa.sample_id, a2_upper.top_age as top_age, a2_lower.base_age as base_age
        from sample sa 
        join stage s2 on s2.stage_id=sa.inferred_stage_id
        join age a2_lower on (a2_lower.age_id=s2.age_lower_id and a2_lower.code<>'nd')
        join age a2_upper on (a2_upper.age_id=s2.age_upper_id and a2_upper.code<>'nd')
) union (
        -- include known age in the min/max
        select sa.sample_id, a3_upper.top_age as top_age, a3_lower.base_age as base_age
        from sample sa 
        join stage s3 on s3.stage_id=sa.known_stage_id
        join age a3_lower on (a3_lower.age_id=s3.age_lower_id and a3_lower.code<>'nd')
        join age a3_upper on (a3_upper.age_id=s3.age_upper_id and a3_upper.code<>'nd')
))
group by sample_id;


-- Auto age narrow. 
create view auto_age_narrow_view as
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
        join stage s on s.stage_id=p.stage_id
        join age a_lower on (a_lower.age_id = s.age_lower_id and a_lower.code<>'nd')
        join age a_upper on (a_upper.age_id = s.age_upper_id and a_upper.code<>'nd')
        where pl.group_id in (328, 301, 323, 322, 306, 348, 303, 314, 324, 342, 327, 313, 312, 326, 316, 325, 311, 334)
)
where dest_rank=1 -- exclude rows with old identification_date or record_id
group by sample_id
)
where min_lower_base_age > max_upper_top_age; -- exclude samples with no age overlap.


-- Arbitrary, guessed, default identification dates.
create table default_identification_date (person_id number(6), identification_date date);
ALTER TABLE FR.DEFAULT_IDENTIFICATION_DATE ADD PRIMARY KEY (PERSON_ID, IDENTIFICATION_DATE);
insert into default_identification_date (person_id, identification_date) values (1, to_date('2000-01-01', 'YYYY-MM-DD');
-- TODO: more entries.



-------------------------------------------------

Questions for Chris:
* I'm only meant to use a list of approved groups. What parts of the query is this for? Only the narrow ages calculation? Should I ignore whole paleontology entries if they don't have a corrosponding pal_list entry in an approved group?

TODO: b ii and convert "999" into "nd".

-- find some demo data
select r2.sample_id, r2.record_id, p2.identification_date
from paleontology p2
join record r2 on r2.record_id=p2.record_id
where r2.sample_id in (
        select r.sample_id
        from paleontology p
        join record r on r.record_id=p.record_id
        group by r.sample_id
        having count(r.sample_id) > 3 
        and count(r.sample_id)<10
)



