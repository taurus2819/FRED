CREATE OR REPLACE TRIGGER Relationship_Seq_Trg
BEFORE INSERT ON Relationship
REFERENCING NEW AS new
FOR EACH ROW
WHEN (new.Relationship_ID IS NULL)
DECLARE
  ok INTEGER;
  newID NUMBER;
BEGIN
  ok := 1;
  WHILE ok > 0 LOOP
    SELECT Relationship_Seq.NEXTVAL INTO newID FROM DUAL;
    SELECT COUNT(*) INTO ok FROM Relationship WHERE Relationship_ID = newID;
  END LOOP;
  :NEW.Relationship_ID := newID;
END;
/