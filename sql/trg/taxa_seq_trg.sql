CREATE OR REPLACE TRIGGER Taxa_Seq_Trg
BEFORE INSERT ON Taxonomic_Lookup
REFERENCING NEW AS new
FOR EACH ROW
WHEN (new.Taxa_ID IS NULL)
DECLARE
  ok INTEGER;
  newID NUMBER;
BEGIN
  ok := 1;
  WHILE ok > 0 LOOP
    SELECT Taxa_Seq.NEXTVAL INTO newID FROM DUAL;
    SELECT COUNT(*) INTO ok FROM Taxonomic_Lookup WHERE Taxa_ID = newID;
  END LOOP;
  :NEW.Taxa_ID := newID;
END;
/