CREATE OR REPLACE TRIGGER Stage_Seq_Trg
BEFORE INSERT OR UPDATE ON Stage
REFERENCING NEW AS new
FOR EACH ROW
WHEN (new.Stage_ID IS NULL)
DECLARE
  ok INTEGER;
  newID NUMBER;
  baseAge NUMBER;
  topAge NUMBER;
BEGIN
  IF INSERTING AND :NEW.stage_id IS NULL THEN
    ok := 1;
    WHILE ok > 0 LOOP
      SELECT Stage_Seq.NEXTVAL INTO newID FROM DUAL;
      SELECT COUNT(*) INTO ok FROM Stage WHERE Stage_ID = newID;
    END LOOP;
    :NEW.Stage_ID := newID;
  END IF;
  SELECT base_age, top_age INTO baseAge, topAge FROM age WHERE age_id = :NEW.age_lower_id;
  IF :NEW.age_upper_id IS NOT NULL THEN
    SELECT top_age INTO topAge FROM age WHERE age_id = :NEW.age_upper_id;
  END IF;
  :NEW.base_age := baseAge;
  :NEW.top_age := topAge;
END;
/