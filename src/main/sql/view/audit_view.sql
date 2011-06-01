CREATE OR REPLACE VIEW Audit_View AS
SELECT 
	A.Audit_ID, 
	A.Status, 
	A.Data_Origin_ID, 
	L.Name AS Data_Origin, 
	A.Created_By_ID, 
	S1.Full_Name AS Created_By, 
	A.Created_Date,
 	AE.Edited_By_ID, 
 	S2.Full_Name AS Edited_By, 
 	AE.Edited_Date, 
 	AE.Comments AS Edit_Comments, 
 	A.Submitted_By_ID, 
 	S3.Full_Name AS Submitted_By,
 	A.Submitted_Date, 
 	A.Approved_By_ID, 
 	S4.Full_Name AS Approved_By, 
 	A.Approved_Date, 
 	A.Working_Comments, 
 	A.Working_Folder_ID,
 	A.Curator_Comments,  
 	A.Send_Message
FROM 
	Audit_Table A, 
	Audit_Edit AE, 
	Data_Origin L, 
	FR_User_View S1, 
	FR_User_View S2, 
	FR_User_View S3, FR_User_View S4
WHERE 
	A.Audit_ID = AE.Audit_ID(+) 
AND A.Data_Origin_ID = L.origin_ID(+) 
AND A.Created_By_ID = S1.PE_ID(+) 
AND AE.Edited_By_ID = S2.PE_ID(+)
AND A.Submitted_By_ID = S3.PE_ID(+) 
AND A.Approved_By_ID = S4.PE_ID(+);