CREATE OR REPLACE TRIGGER Submit_Feature_Trg
BEFORE UPDATE OF Status ON Audit_Table
FOR EACH ROW
DECLARE
BEGIN
  IF :NEW.Status = 'waiting' AND :OLD.Status = 'working' THEN
    :NEW.Send_Message := 'sub';
  ELSIF :NEW.Status = 'waiting' AND :OLD.Status = 'rejected' THEN
    :NEW.Send_Message := 're-sub';
  ELSIF :NEW.Status = 'working' AND :OLD.Status = 'waiting' THEN
    :NEW.Send_Message := 'rev';    
  ELSIF :NEW.Status = 'approved' AND :OLD.Status = 'waiting' THEN
    :NEW.Send_Message := 'app';
  ELSIF :NEW.Status = 'rejected' AND :OLD.Status = 'waiting' THEN
    :NEW.Send_Message := 'rej';
  END IF;
END;
/