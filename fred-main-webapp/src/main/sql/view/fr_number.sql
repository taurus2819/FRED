-- a view of fr_number and its related data
CREATE OR REPLACE VIEW 
AS
  SELECT fr.fr_number,
    fr.fr_id,
    fe.feature_id,
    sa.sample_id,
    r.relationship_id,
    fc.FOLDER_ID,
    fm.meta_id fm_meta_id,
    st.sent_to_id,
    re.record_id,
    sf.sed_feature_id,
    co.person_id,
    co.sample_id co_sample_id,
    sm.meta_id sm_meta_id,
    ad.record_id ad_record_id,
    ao.person_id ao_person_id,
    ao.record_id ao_record_id,
    pa.record_id pa_record_id,
    rm.meta_id rm_meta_id
  FROM fr_number fr,
    feature fe,
    sample sa,
    relationship r,
    folder_content fc,
    feature_meta fm,
    sent_to st,
    record re,
    sedimentary_feature sf,
    collector co,
    sample_meta sm,
    adoption ad,
    adoptor ao,
    paleontology pa,
    record_meta rm
  WHERE fr.fr_id    = fe.fr_id
  AND fe.feature_id = sa.feature_id (+)
  AND fe.feature_id = r.related_feature_id (+)
  AND fe.feature_id = fc.feature_id (+)
  AND fe.feature_id = fm.feature_id (+)
  AND sa.sample_id  = st.sample_id (+)
  AND sa.sample_id  = re.sample_id (+)
  AND sa.sample_id  = sf.sample_id (+)
  AND sa.sample_id  = co.sample_id (+)
  AND sa.sample_id  = sm.sample_id (+)
  AND re.record_id  = ad.record_id (+)
  AND re.record_id  = pa.record_id(+)
  AND re.record_id  =rm.record_id(+)
  AND ad.record_id  = ao.record_id(+);