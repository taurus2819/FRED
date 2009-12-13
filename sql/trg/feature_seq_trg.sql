CREATE OR REPLACE TRIGGER Feature_Seq_Trg
BEFORE INSERT ON Feature
REFERENCING NEW AS new
FOR EACH ROW
WHEN (new.Feature_ID IS NULL)
DECLARE
  ok INTEGER;
  newID NUMBER;
BEGIN
  ok := 1;
  WHILE ok > 0 LOOP
    SELECT Feature_Seq.NEXTVAL INTO newID FROM DUAL;
    SELECT COUNT(*) INTO ok FROM Feature WHERE Feature_ID = newID;
  END LOOP;
  :NEW.Feature_ID := newID;
END;
/
