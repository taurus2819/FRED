CREATE or replace VIEW SQUIRREL_AGE_VIEW
AS
SELECT s.sample_id,
    NVL(vlaal.base_age, NVL(vaan.narrow_age_lower, NVL(aawd.wide_age_lower, NVL
    (ksal.base_age, isl.base_age)))) AS NARROW_BASE_AGE,
    NVL(vlaau.top_age, NVL(vaan.narrow_age_upper, NVL(aawd.wide_age_upper, NVL
    (ksau.top_age, isu.top_age))))   AS NARROW_TOP_AGE,
    NVL(vlaal.base_age, vaaw.wide_age_lower) AS
    WIDE_BASE_AGE,
    NVL(vlaau.top_age, vaaw.wide_age_upper) AS
    WIDE_TOP_AGE
FROM sample s
LEFT OUTER JOIN latest_adoption_view vla     ON vla.sample_id=s.sample_id
LEFT OUTER JOIN age vlaal                  ON vlaal.age_id=vla.adopted_age_lower
LEFT OUTER JOIN age vlaau                  ON vlaau.age_id=vla.adopted_age_upper
LEFT OUTER JOIN auto_age_wide_view vaaw      ON vaaw.sample_id=s.sample_id
LEFT OUTER JOIN auto_age_narrow_view vaan    ON vaan.sample_id=s.sample_id
LEFT OUTER JOIN auto_age_wide_dtrmnd aawd    ON aawd.sample_id=s.sample_id
LEFT OUTER JOIN stage_fixed_view known_stage ON known_stage.stage_id=
    s.known_stage_id
LEFT OUTER JOIN age ksal                 ON ksal.age_id=known_stage.age_lower_id
LEFT OUTER JOIN age ksau                 ON ksau.age_id=known_stage.age_upper_id
LEFT OUTER JOIN stage_fixed_view inferred_stage ON inferred_stage.stage_id=
    s.inferred_stage_id
LEFT OUTER JOIN age isl ON isl.age_id=inferred_stage.age_lower_id
LEFT OUTER JOIN age isu ON isu.age_id=inferred_stage.age_upper_id;

