 CREATE OR REPLACE VIEW record_stage_view
 AS
 SELECT r.record_id, 'inferred' AS type, st.stage_id, st.base_age, st.top_age FROM record r JOIN sample s ON r.sample_id = s.sample_id JOIN stage st ON s.inferred_stage_id = st.stage_id
 UNION
 SELECT r.record_id, 'known' AS type, st.stage_id, st.base_age, st.top_age FROM record r JOIN sample s ON r.sample_id = s.sample_id JOIN stage st ON s.known_stage_id = st.stage_id
 UNION
 SELECT r.record_id, 'adoption' AS type, st.stage_id, st.base_age, st.top_age FROM record r JOIN adoption a ON r.record_id = a.record_id JOIN stage st ON a.adopted_stage_id = st.stage_id
 UNION
 SELECT r.record_id, 'paleontology' AS type, st.stage_id, st.base_age, st.top_age FROM record r JOIN paleontology p ON r.record_id = p.record_id JOIN stage st ON p.stage_id = st.stage_id;