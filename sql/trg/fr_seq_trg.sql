CREATE OR REPLACE TRIGGER FR_Seq_Trg
BEFORE INSERT OR UPDATE ON FR_Number
REFERENCING NEW AS new
FOR EACH ROW
DECLARE
  ok INTEGER;
  newID NUMBER;
BEGIN
  IF INSERTING AND :NEW.FR_ID IS NULL THEN
    ok := 1;
    WHILE ok > 0 LOOP
      SELECT FR_Seq.NEXTVAL INTO newID FROM DUAL;
      SELECT COUNT(*) INTO ok FROM FR_Number WHERE FR_ID = newID;
    END LOOP;
    :NEW.FR_ID := newID;
  END IF;
  :NEW.FR_Number := :NEW.Map_Sheet || '/f' || TO_CHAR(:NEW.Serial_Number, 'FM0000') || :NEW.Recollection_Number;
END;
/