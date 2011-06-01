 CREATE OR REPLACE VIEW sample_stage_view
 AS
 SELECT s.sample_id, 'inferred' AS type, st.stage_id, st.base_age, st.top_age FROM sample s JOIN stage st ON s.inferred_stage_id = st.stage_id
 UNION
 SELECT s.sample_id, 'known' AS type, st.stage_id, st.base_age, st.top_age FROM sample s JOIN stage st ON s.known_stage_id = st.stage_id
 UNION
 SELECT s.sample_id, 'adoption' AS type, st.stage_id, st.base_age, st.top_age FROM sample s JOIN record r ON s.sample_id = r.sample_id JOIN adoption a ON r.record_id = a.record_id JOIN stage st ON a.adopted_stage_id = st.stage_id
 UNION
 SELECT s.sample_id, 'paleontology' AS type, st.stage_id, st.base_age, st.top_age FROM sample s JOIN record r ON s.sample_id = r.sample_id JOIN paleontology p ON r.record_id = p.record_id JOIN stage st ON p.stage_id = st.stage_id;