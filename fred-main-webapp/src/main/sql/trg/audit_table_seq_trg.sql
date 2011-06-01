CREATE OR REPLACE TRIGGER Audit_Table_Seq_Trg
BEFORE INSERT ON Audit_Table
REFERENCING NEW AS new
FOR EACH ROW
WHEN (new.Audit_ID IS NULL)
DECLARE
  ok INTEGER;
  newID NUMBER;
BEGIN
  ok := 1;
  WHILE ok > 0 LOOP
    SELECT Audit_Seq.NEXTVAL INTO newID FROM DUAL;
    SELECT COUNT(*) INTO ok FROM Audit_Table WHERE Audit_ID = newID;
  END LOOP;
  :NEW.Audit_ID := newID;
END;
/