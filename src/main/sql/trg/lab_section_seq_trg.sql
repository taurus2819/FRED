CREATE OR REPLACE TRIGGER Lab_Section_Seq_Trg
BEFORE INSERT ON Lab_Section
REFERENCING NEW AS new
FOR EACH ROW
WHEN (new.Lab_Section_ID IS NULL)
DECLARE
  ok INTEGER;
  newID NUMBER;
BEGIN
  ok := 1;
  WHILE ok > 0 LOOP
    SELECT Lab_Section_Seq.NEXTVAL INTO newID FROM DUAL;
    SELECT COUNT(*) INTO ok FROM Lab_Section WHERE Lab_Section_ID = newID;
  END LOOP;
  :NEW.Lab_Section_ID := newID;
END;
/