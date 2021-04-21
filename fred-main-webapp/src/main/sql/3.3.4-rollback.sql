
  CREATE OR REPLACE FORCE VIEW "FR"."AUTO_AGE_NARROW_VIEW" ("SAMPLE_ID", "NARROW_AGE_LOWER", "NARROW_AGE_UPPER") AS 
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
where min_lower_base_age > max_upper_top_age -- exclude samples with no age overlap.

 ;


  CREATE OR REPLACE FORCE VIEW "FR"."AUTO_AGE_WIDE_DTRMND" ("SAMPLE_ID", "WIDE_AGE_UPPER", "WIDE_AGE_LOWER") AS 
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
group by sample_id
 ;


  CREATE OR REPLACE FORCE VIEW "FR"."AUTO_AGE_WIDE_VIEW" ("SAMPLE_ID", "WIDE_AGE_UPPER", "WIDE_AGE_LOWER") AS 
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
group by sample_id
 ;
