CREATE OR REPLACE PACKAGE "FR"."FRED_MAINTAIN"
/*
Mod 04Jul16 mqe add exception trapping in Generate_MF_Email procedure
Mod 19Mar19 robinp added the optional From parameter
Mod 6Jun19 robinp stop dev & tst from spamming FRED people 
*/
AUTHID CURRENT_USER
AS
  PROCEDURE Go;
  PROCEDURE Check_Masterfiles;
  PROCEDURE Check_Taxa;
  PROCEDURE Check_Confidentiality;
  PROCEDURE Generate_MF_Email(MFID IN NUMBER);
  PROCEDURE Generate_Submit_Email (UserID IN NUMBER);
  PROCEDURE Generate_Taxa_Email (PanelID IN NUMBER);
  PROCEDURE Generate_TaxaSub_Email (UserID IN NUMBER);
  PROCEDURE Generate_Confidentiality_EMail(UserID IN NUMBER);
  PROCEDURE Update_Backlog_Status;
END FRED_Maintain;
/
CREATE OR REPLACE PACKAGE BODY "FR"."FRED_MAINTAIN"
AS
  intval BINARY_INTEGER;
  sidval VARCHAR2(256); 
  PROCEDURE Go
  AS
  BEGIN
    -- Get SID of database: 5 June 2019 
    -- if sidval is 'DEV' or 'TST', send to developer@gns.cri.nz
    -- Prevents spamming real users for testing
    intval := dbms_utility.get_parameter_value('instance_name',intval, sidval);
    Check_Masterfiles;
    Check_Taxa;
    Check_Confidentiality;
    Update_Backlog_Status;
  END Go;

  PROCEDURE Check_Masterfiles
  AS
    CURSOR MF_Cur IS
      SELECT DISTINCT F.Masterfile_ID
      FROM Feature F, Audit_Table A
      WHERE F.Audit_ID = A.Audit_ID AND A.Send_Message IN ('sub', 're-sub', 'rev')
      AND F.Masterfile_ID IS NOT NULL
      ORDER BY F.Masterfile_ID;
    CURSOR User_Cur IS
      SELECT DISTINCT Submitted_By_ID FROM Audit_Table WHERE Submitted_By_ID IS NOT NULL AND Send_Message IN ('app', 'rej');
  BEGIN
    FOR CRow IN MF_Cur LOOP
      Generate_MF_Email(CRow.Masterfile_ID);
    END LOOP;
    FOR CRow IN User_Cur LOOP
      Generate_Submit_Email(CRow.Submitted_By_ID);
    END LOOP;
    UPDATE Audit_Table SET Send_Message = NULL WHERE Send_Message IS NOT NULL;
    COMMIT;
  END Check_Masterfiles;

  PROCEDURE Check_Taxa
  AS
    CURSOR TP_Cur IS
      SELECT DISTINCT Panelist_ID FROM Taxa_Panel TP, Taxonomic_Lookup TL
      WHERE TP.Group_ID = TL.Group_ID AND (sysdate - tl.submitted_date) < 2 AND TL.Status = 'provisional' AND TL.Send_Message IS NOT NULL;
    CURSOR TPSub_Cur IS
      SELECT DISTINCT Submitted_By_ID FROM Taxonomic_Lookup WHERE (sysdate - approved_date) < 2 AND Status IN ('approved', 'rejected') AND Submitted_By_ID IS NOT NULL AND Send_Message IS NOT NULL;
  BEGIN
    FOR CRow IN TP_Cur LOOP
      Generate_Taxa_Email(CRow.Panelist_ID);
    END LOOP;
    FOR CRow IN TPSub_Cur LOOP
      Generate_TaxaSub_Email(CRow.Submitted_By_ID);
    END LOOP;
    UPDATE Taxonomic_Lookup SET Send_Message = NULL WHERE Send_Message IS NOT NULL;
    COMMIT;
  END Check_Taxa;

  PROCEDURE Check_Confidentiality
  AS
    CURSOR Confid_Cur IS
      SELECT DISTINCT Submitted_By_ID
      FROM Audit_Table
      WHERE Status = 'approved' AND (Confid_Email_Flag IS NULL OR Confid_Email_Flag = 0)
      AND Submitted_By_ID IS NOT NULL AND Confid_Lapse_Date - SYSDATE < 91;
  BEGIN
    FOR CRow IN Confid_Cur LOOP
      Generate_Confidentiality_Email(CRow.Submitted_By_ID);
    END LOOP;
    UPDATE Audit_Table SET Confid_Email_Flag = 1 WHERE Status = 'approved' AND (Confid_Email_Flag IS NULL OR Confid_Email_Flag = 0)
      AND Confid_Lapse_Date - SYSDATE < 91;
    COMMIT;
  END Check_Confidentiality;

  PROCEDURE Generate_MF_Email(MFID IN NUMBER)
  AS
   err_num number ;
   err_msg varchar2(100) ;
    MFName VARCHAR2(32);
    EmailAdd VARCHAR2(64);
    EmailString VARCHAR2(20000);
    CURSOR Feat_Cur IS
      SELECT DISTINCT A.Submitted_By, F.Feature_Name,
       DECODE(A.Send_Message, 'sub', 'Submitted', DECODE(A.Send_Message, 're-sub', 'Re-Submitted', 'Revoke')) AS Type, Send_Message
       FROM Feature F, Audit_View A WHERE F.Audit_ID = A.Audit_ID AND Send_Message IN ('sub', 're-sub', 'rev')
       AND F.Masterfile_ID = MFID
      ORDER BY A.Send_Message;
    CURSOR Email_Cur IS
      SELECT user_id FROM folder_view WHERE folder_id = MFID;
  BEGIN
    SELECT name INTO MFName FROM folder WHERE folder_id = MFID;
    EmailString := 'A change has occured in the ' || MFName || ' masterfile as listed below:' || CHR(10) || CHR(10) ||
     'User' || CHR(9) || CHR(9) || 'Feature Name' || CHR(9) || 'Action' || CHR(10);
    FOR CRow IN Feat_Cur LOOP
      EmailString := EmailString || CRow.Submitted_By || CHR(9) || CRow.Feature_Name || CHR(9) || CHR(9) || CRow.Type || CHR(10);
    END LOOP;
    EmailString := EmailString || CHR(10) || 'You may view the folder by clicking here http://www.fred.org.nz/admin_folder_detail.jsp?ID='
     || MFID;
    FOR CRow IN Email_Cur LOOP
      IF CRow.user_id > 0 THEN
        SELECT ST_Email INTO EmailAdd FROM mis.all_staff WHERE ST_Code = CRow.user_id;
      ELSE
        SELECT CP_Email_Address INTO EmailAdd FROM MIS.Contact_Person WHERE CP_Code = (CRow.user_id * -1);
      END IF;
      IF (upper(sidval) = 'DEV' OR upper(sidval) = 'TST') then
          EmailAdd := 'developer@gns.cri.nz' ;
      END IF;
      SYS.SEND_MAIL(EmailAdd, 'FRED Database - localities to approve/reject (' || sidval || ')', EmailString, 'Fossil_Record_Administrator');
    END LOOP;
    COMMIT;
exception
   when others then
      err_num := SQLCODE ;
      dbms_output.put_line( 'Error number is: ' || err_num ) ;
      err_msg := substr ( SQLERRM, 1, 100) ;
      dbms_output.put_line( 'Error message is: ' || err_msg ) ;
		dbms_output.put_line( DBMS_UTILITY.format_error_backtrace ) ;
		raise ;
  END Generate_MF_Email;

  PROCEDURE Generate_Submit_Email(UserID IN NUMBER)
/* 
mod mqe 20Sep13 debug; limit size of emails produced
*/
  AS
    UserEmail VARCHAR2(64);
    EmailString VARCHAR2(32767);
	 kount integer := 0 ;
    CURSOR Feat_Cur IS
      SELECT F.Feature_Name, Fd.Name, FR.FR_Number, DECODE(A.Send_Message, 'app', 'Approved', 'Rejected') AS Type, A.Curator_Comments
       FROM Feature F, Audit_Table A, Folder Fd, FR_Number FR
       WHERE F.Audit_ID = A.Audit_ID AND F.Masterfile_ID = Fd.Folder_ID AND F.FR_ID = FR.FR_ID(+)
        AND A.Send_Message IN ('app', 'rej') AND A.Submitted_By_ID = UserID
       ORDER BY A.Send_Message;
  BEGIN
    IF UserID > 0 THEN
      SELECT ST_Email INTO UserEmail FROM mis.all_staff WHERE ST_Code = UserID;
    ELSE
      SELECT CP_Email_Address INTO UserEmail FROM MIS.Contact_Person WHERE CP_Code = (UserID * -1);
    END IF;
    FOR CRow IN Feat_Cur LOOP
	 	begin
			kount := kount + 1 ;
			if kount = 1 then
		   	EmailString := 'Your samples have been approved or rejected '
					|| 'as listed below:' || CHR(10) || CHR(10) 
					||	'Feature Name' || CHR(9) || 'Masterfile' || CHR(9) 
					|| 'FR Number' || CHR(9) || 'Action' || CHR(9) 
					|| 'Comments' || CHR(10);
			end if ;
			EmailString := EmailString || CRow.Feature_Name || CHR(9) 
				|| CRow.Name || CHR(9) || CRow.FR_Number || CHR(9) 
				|| CRow.Type || CHR(9) || CRow.Curator_Comments || CHR(10);
			if kount > 49 then
				/* Limit email size to 50 line-items */
				EmailString := EmailString || CHR(10) 
					|| 'Click here to view your folders '
					|| ' http://www.fred.org.nz/folder_list.jsp';
			if (upper(sidval) = 'DEV' OR upper(sidval) = 'TST') then
				UserEmail := 'developer@gns.cri.nz' ;
          		end if;
		    	SYS.SEND_MAIL(UserEmail, 'FRED Database - localities approved/rejected (' || sidval || ')', EmailString, 'Fossil_Record_Administrator');
				kount := 0 ;
	   	end if ;
	  exception
			when others then
				raise_application_error(-20001, 'procedure fred_maintain.generate_submit_mail failed building EmailString length ' || length(EmailString) || ' on row ' || kount ) ;
		end ;	
    END LOOP;
    EmailString := EmailString || CHR(10) 
		|| 'Click here to view your folders '
		|| '  http://www.fred.org.nz/folder_list.jsp';
    if (upper(sidval) = 'DEV' OR upper(sidval) = 'TST') then
            UserEmail := 'developer@gns.cri.nz' ;
    end if ;
    SYS.SEND_MAIL(UserEmail, 'FRED Database - localities approved/rejected (' || sidval || ')', EmailString, 'Fossil_Record_Administrator');
  END Generate_Submit_Email;

  PROCEDURE Generate_Taxa_Email(PanelID IN NUMBER)
  AS
    PanelistEmail VARCHAR2(64);
    EmailString VARCHAR2(10000);
    CURSOR Taxa_Cur IS
      SELECT L.Name AS Group_Name, TL.Taxa_ID, TL.Taxonomic_Name, TL.Author, FU.Full_Name AS Sub_Name
       FROM Taxonomic_Lookup TL, Taxa_Panel TP, taxonomic_group L, FR_User_View FU
       WHERE TL.Group_ID = TP.Group_ID AND TL.Group_ID = L.group_ID AND TL.Submitted_By_ID = FU.PE_ID
       AND TL.Status = 'provisional' AND TL.Send_Message IS NOT NULL AND TP.Panelist_ID = PanelID;
  BEGIN
    IF PanelID > 0 THEN
      SELECT ST_Email INTO PanelistEmail FROM mis.all_staff WHERE ST_Code = PanelID;
    ELSE
      SELECT CP_Email_Address INTO PanelistEmail FROM MIS.Contact_Person WHERE CP_Code = (PanelID * -1);
    END IF;
    EmailString := 'New taxonomic name(s) submitted to the thesaurus:' || CHR(10) || CHR(10) ||
     'Submitted By' || CHR(9) || 'Group' || CHR(9) || 'Taxonomic Name' || CHR(9) || 'Author' || CHR(10);
    FOR CRow IN Taxa_Cur LOOP
      EmailString := EmailString || CRow.Sub_Name || CHR(9) || CRow.Group_Name || CHR(9) || CRow.Taxonomic_Name ||
       CHR(9) || CRow.Author || CHR(10);
    END LOOP;
    EmailString := EmailString || CHR(10) || 'Click here to view http://www.fred.org.nz/folder_list.jsp';
    if (upper(sidval) = 'DEV' OR upper(sidval) = 'TST') then
            PanelistEmail := 'developer@gns.cri.nz' ;
    end if ;
    SYS.SEND_MAIL(PanelistEmail, 'FRED Database - new taxonomic names (' || sidval || ')', EmailString, 'Fossil_Record_Administrator');
  END Generate_Taxa_Email;

  PROCEDURE Generate_TaxaSub_Email(UserID IN NUMBER)
  AS
    UserEmail VARCHAR2(64);
    FeatureID NUMBER(7);
    RecordID NUMBER(8);
    FrID NUMBER(7);
    FeatureName VARCHAR2(64);
    FolderName VARCHAR2(64);
    EmailString VARCHAR2(10000);
    CURSOR Taxa_Cur IS
      SELECT TL.Taxa_ID, TL.Taxonomic_Name, TL.Status, TL.Panelist_Comments, FU.Full_Name
       FROM Taxonomic_Lookup TL, FR_User_View FU
       WHERE TL.Status <> 'provisional' AND TL.Send_Message IS NOT NULL
        AND TL.Approved_By_ID = FU.PE_ID AND TL.Submitted_By_ID = UserID
       ORDER BY TL.Status;
  BEGIN
    IF UserID > 0 THEN
      SELECT ST_Email INTO UserEmail FROM mis.all_staff WHERE ST_Code = UserID;
    ELSE
      SELECT CP_Email_Address INTO UserEmail FROM MIS.Contact_Person WHERE CP_Code = (UserID * -1);
    END IF;
    
    EmailString := 'Your taxonomic name(s) have been considered.' || CHR(10) ||
     'Records with approved names can now be submitted, records with rejected names will have to be editied' ||
      CHR(10) || CHR(10) || 'Taxonomic Name' || CHR(9) || 'Feature Name'  || CHR(9) || 'Folder Name'  || CHR(9) || 'Status' || CHR(9) || 'Actioned By' || CHR(9) || 'Comments' || CHR(10);
    FOR CRow IN Taxa_Cur LOOP
      SELECT MIN(F.Feature_ID), MIN(P.Record_ID) INTO FeatureID, RecordID FROM Feature F, Sample S, Record R, Pal_List P
      	WHERE F.Feature_ID = S.Feature_ID AND S.Sample_ID = R.Sample_ID AND R.Record_ID = P.Record_ID AND P.Taxa_ID = CRow.Taxa_ID;
      IF FeatureID IS NOT NULL THEN
        SELECT fr_id, feature_name INTO FrID, FeatureName FROM feature WHERE feature_id = FeatureID;
        IF FrID IS NULL THEN
          IF FeatureName IS NULL THEN
            FeatureName := 'unnamed locality';
          END IF;
        ELSE
          SELECT fr_number INTO FeatureName FROM fr_number WHERE fr_id = FrID;
        END IF;
        SELECT Fd.Name INTO FolderName FROM Record R, Audit_Table A, Folder Fd
            WHERE R.Audit_ID = A.Audit_ID AND A.Working_Folder_ID = Fd.Folder_ID(+) AND R.Record_ID = RecordID;
        EmailString := EmailString || CRow.Taxonomic_Name || CHR(9) || FeatureName || CHR(9) || FolderName || CHR(9) || CRow.Status ||
         	CHR(9) || CRow.Full_Name || CHR(9) || CRow.Panelist_Comments  || CHR(10);
      ELSE
        EmailString := EmailString || CRow.Taxonomic_Name || CHR(9) || 'n/a' || CHR(9) || 'n/a' || CHR(9) || CRow.Status ||
         	CHR(9) || CRow.Full_Name || CHR(9) || CRow.Panelist_Comments  || CHR(10);
      END IF;
    END LOOP;
    if (upper(sidval) = 'DEV' OR upper(sidval) = 'TST') then
            UserEmail := 'developer@gns.cri.nz' ;
    end if ;
    SYS.SEND_MAIL(UserEmail, 'FRED Database - taxonomic names approved/rejected (' || sidval || ')', EmailString, 'Fossil_Record_Administrator');
  END Generate_TaxaSub_Email;

  PROCEDURE Generate_Confidentiality_Email(UserID IN NUMBER)
  AS
    UserEmail VARCHAR2(64);
    EmailString VARCHAR2(20000);
    CURSOR Feat_Cur IS
      SELECT DISTINCT FR.FR_Number
      FROM Audit_Table A, Feature F, FR_Number FR
      WHERE A.Audit_ID = F.Audit_ID AND F.FR_ID = FR.FR_ID
      AND A.Status = 'approved' AND (A.Confid_Email_Flag IS NULL OR A.Confid_Email_Flag = 0)
      AND A.Submitted_By_ID = UserID AND A.Confid_Lapse_Date - SYSDATE < 91;
    CURSOR Samp_Cur IS
      SELECT DISTINCT FR.FR_Number
      FROM Audit_Table A, Sample S, Feature F, FR_Number FR
      WHERE A.Audit_ID = S.Audit_ID AND S.Feature_ID = F.Feature_ID AND F.FR_ID = FR.FR_ID
      AND F.Feature_Type <> 'Outcrop' AND A.Status = 'approved' AND (A.Confid_Email_Flag IS NULL OR A.Confid_Email_Flag = 0)
      AND A.Submitted_By_ID = UserID AND A.Confid_Lapse_Date - SYSDATE < 91;
    CURSOR Rec_Cur IS
      SELECT DISTINCT FR.FR_Number
      FROM Audit_Table A, Record R, Sample S, Feature F, FR_Number FR
      WHERE A.Audit_ID = R.Audit_ID AND R.Sample_ID = S.Sample_ID AND S.Feature_ID = F.Feature_ID AND F.FR_ID = FR.FR_ID
      AND A.Status = 'approved' AND (A.Confid_Email_Flag IS NULL OR A.Confid_Email_Flag = 0)
      AND A.Submitted_By_ID = UserID AND A.Confid_Lapse_Date - SYSDATE < 91;
    CURSOR Pal_Cur IS
      SELECT DISTINCT FR.FR_Number
      FROM Audit_Table A, Record R, Sample S, Feature F, FR_Number FR
      WHERE A.Audit_ID = R.Pal_List_Audit_ID AND R.Sample_ID = S.Sample_ID AND S.Feature_ID = F.Feature_ID AND F.FR_ID = FR.FR_ID
      AND A.Status = 'approved' AND (A.Confid_Email_Flag IS NULL OR A.Confid_Email_Flag = 0)
      AND A.Submitted_By_ID = UserID AND A.Confid_Lapse_Date - SYSDATE < 91;
  BEGIN
    IF UserID > 0 THEN
      SELECT ST_Email INTO UserEmail FROM mis.all_staff WHERE ST_Code = UserID;
    ELSE
      SELECT CP_Email_Address INTO UserEmail FROM MIS.Contact_Person WHERE CP_Code = (UserID * -1);
    END IF;
    EmailString := 'Some data you have set as confidential in FRED is about to become open. You have three months to extend the confidentiality period or the data will automatically become visible to all registered FRED users' || CHR(10) || CHR(10) ||
     'Localities' || CHR(10);
    FOR CRow IN Feat_Cur LOOP
      EmailString := EmailString || CRow.FR_Number || CHR(10);
    END LOOP;
    EmailString := EmailString || 'Samples' || CHR(10);
    FOR CRow IN Samp_Cur LOOP
      EmailString := EmailString || CRow.FR_Number || CHR(10);
    END LOOP;
    EmailString := EmailString || 'Records' || CHR(10);
    FOR CRow IN Rec_Cur LOOP
      EmailString := EmailString || CRow.FR_Number || CHR(10);
    END LOOP;
    EmailString := EmailString || 'Taxonomic Lists' || CHR(10);
    FOR CRow IN Pal_Cur LOOP
      EmailString := EmailString || CRow.FR_Number || CHR(10);
    END LOOP;
    if (upper(sidval) = 'DEV' OR upper(sidval) = 'TST') then
            UserEmail := 'developer@gns.cri.nz' ;
    end if ;
    SYS.SEND_MAIL(UserEmail, 'FRED Database - confidentiality about to lapse (' || sidval || ')', EmailString, 'Fossil_Record_Administrator');
  END Generate_Confidentiality_Email;

  PROCEDURE Update_Backlog_Status
  AS
  BEGIN
	insert into backlog_status_temp (map_no, masterfile_id)
	select distinct fr.map_sheet, f.masterfile_id
	  from backlog_status b, fr_number fr, feature f
	  where f.fr_id = fr.fr_id and fr.map_sheet = b.map_no(+)
	  and (b.map_no is null or b.masterfile_id is null);

    insert into backlog_status (objectid, map_no, masterfile_id, status)
    select i508.nextval, map_no, masterfile_id, 'not started'
    from backlog_status_temp;

    delete from backlog_status_temp;

    update backlog_status b
	set b.locality_count =
	(
	  select count(*)
      from fr_number fr, feature f
	  where fr.fr_id = f.fr_id
	  and fr.map_sheet = b.map_no
	);

	update backlog_status b
	set b.processing_count =
	(
	  select count(*)
	  from fr_number fr, feature f, audit_table a, audit_edit e
	  where fr.fr_id = f.fr_id and f.audit_id = a.audit_id and a.audit_id = e.audit_id
	  and a.status <> 'approved' and e.comments = 'Locality prepared for backlog editing'
	  and fr.map_sheet = b.map_no
	);

	update backlog_status b
	set b.completed_count =
	(
	  select count(*)
	  from fr_number fr, feature f, audit_table a
	  where fr.fr_id = f.fr_id and f.audit_id = a.audit_id
	  and a.status = 'approved' and a.curator_comments like '%Approved after backlog editing%'
	  and fr.map_sheet = b.map_no
	);

	update backlog_status b
	set b.new_count =
	(
	  select count(*)
	  from fr_number fr, feature f, audit_table a
	  where fr.fr_id = f.fr_id and f.audit_id = a.audit_id
	  and a.status = 'approved' and (a.curator_comments is null or a.curator_comments not like '%Approved after backlog editing%') and a.created_date >= '01-OCT-05'
	  and fr.map_sheet = b.map_no
	);

	update backlog_status set not_started_count = locality_count - processing_count - completed_count - new_count;

	update backlog_status
	set status = 'not started';

	update backlog_status
	set status = 'no locality'
	where locality_count - new_count = 0;

	update backlog_status
	set status = 'processing'
	where processing_count > 0 or completed_count > 0;

	update backlog_status
	set status = 'complete'
	where locality_count - new_count > 0 and completed_count = locality_count - new_count;

	commit;
  END Update_Backlog_Status;

END;
/
