CREATE OR REPLACE TRIGGER Audit_Edit_Seq_Trg
BEFORE INSERT ON Audit_Edit
REFERENCING NEW AS new
FOR EACH ROW
WHEN (new.Audit_Edit_ID IS NULL)
DECLARE
  ok INTEGER;
  newID NUMBER;
BEGIN
  ok := 1;
  WHILE ok > 0 LOOP
    SELECT Audit_Edit_Seq.NEXTVAL INTO newID FROM DUAL;
    SELECT COUNT(*) INTO ok FROM Audit_Edit WHERE Audit_Edit_ID = newID;
  END LOOP;
  :NEW.Audit_Edit_ID := newID;
END;
/