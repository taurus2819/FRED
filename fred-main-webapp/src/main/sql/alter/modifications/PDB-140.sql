--ALTER TABLE FR.SAMPLE ADD (AUTO_AGE_WIDE NUMBER(7) references stage(stage_id));
--ALTER TABLE FR.SAMPLE ADD (AUTO_AGE_NARROW NUMBER(7) references stage(stage_id));

create or replace view v_sample_auto_age as 
select s.*,
        nvl(adopted_age_lower, nvl(age_narrow_lower.age_id, nvl(known_stage.age_lower_id, nvl(inferred_stage.age_lower_id, 15374)))) as age_narrow_lower_id,
        nvl(adopted_age_upper, nvl(age_narrow_upper.age_id, nvl(known_stage.age_upper_id, nvl(inferred_stage.age_upper_id, 15374)))) as age_narrow_upper_id,
        nvl(adopted_age_lower, nvl(age_wide_lower.age_id, nvl(known_stage.age_lower_id, nvl(inferred_stage.age_lower_id, 15374)))) as age_wide_lower_id,
        nvl(adopted_age_upper, nvl(age_wide_upper.age_id, nvl(known_stage.age_upper_id, nvl(inferred_stage.age_upper_id, 15374)))) as age_wide_upper_id
from sample s
-- adoption
left outer join v_latest_adoption vla on vla.sample_id=s.sample_id
-- determined
left outer join v_auto_age_wide vaaw on vaaw.sample_id=s.sample_id
left outer join v_auto_age_narrow vaan on vaan.sample_id=s.sample_id
-- Convert age bounds of both the above back to age IDs.
left outer join age age_narrow_lower on age_narrow_lower.base_age=vaan.narrow_age_lower
left outer join age age_narrow_upper on age_narrow_upper.top_age=vaan.narrow_age_upper
left outer join age age_wide_lower on age_wide_lower.base_age=vaaw.wide_age_lower
left outer join age age_wide_upper on age_wide_upper.top_age=wide_age_upper
-- known 
left outer join stage known_stage on known_stage.stage_id=s.known_stage_id
-- inferred 
left outer join stage inferred_stage on inferred_stage.stage_id=s.inferred_stage_id;


create or replace view v_latest_adoption as
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
comment on table v_latest_adoption is 'List the latest adoption, if there is one, for each sample, where the adoption_date is used to determine its newness.';


-- auto age wide 
create or replace view v_auto_age_wide as
select sample_id, min(top_age) as wide_age_lower, max(base_age) as wide_age_upper
from (
select sample_id, top_age, base_age
from (
        -- determined ages
        select r.sample_id, a_upper.top_age as top_age, a_lower.base_age as base_age
        from record r
        join paleontology p on p.record_id=r.record_id
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
create view v_auto_age_narrow as
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
        join v_paleontology_fixed p on p.record_id=r.record_id
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


create or replace view v_paleontology_fixed as
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


CREATE or replace VIEW SAMPLE_STAGE_VIEW
    (
        SAMPLE_ID,
        TYPE,
        STAGE_ID,
        BASE_AGE,
        TOP_AGE
    ) AS
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
union
SELECT vs.sample_id,
    'monkeyNarrow' AS type,
    null as stage_id,
    vs.age_narrow_lower_id as base_age,
    vs.age_narrow_upper_id as top_age
from v_sample_auto_age vs
union
SELECT vs.sample_id,
    'monkeyWide' AS type,
    null as stage_id,
    vs.age_wide_lower_id as base_age,
    vs.age_wide_upper_id as top_age
from v_sample_auto_age vs;


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



