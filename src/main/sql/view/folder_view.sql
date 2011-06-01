CREATE OR REPLACE VIEW Folder_View AS
SELECT 
	Fd.Folder_ID, 
	Fd.Name AS Folder_Name, 
	Fd.Owner_ID, 
	S1.Full_Name AS Folder_Owner, 
	FU.User_ID, 
	FU.User_Rights,
 	S2.Full_Name AS Folder_User, 
 	Fd.Folder_Type
FROM 
	Folder Fd, Folder_User FU, FR_User_View S1, FR_User_View S2
WHERE 
	Fd.Owner_ID = S1.PE_ID(+) 
AND Fd.Folder_ID = FU.Folder_ID 
AND FU.User_ID = S2.PE_ID
UNION ALL
SELECT 
	Fd.Folder_ID, 
	Fd.Name AS Folder_Name, 
	Fd.Owner_ID, 
	S1.Full_Name AS Folder_Owner, 
	nvl(Fd.Owner_ID, -1) AS User_ID,
 	63 AS User_Rights, 
 	S1.Full_Name AS Folder_User, 
 	Fd.Folder_Type
FROM 
	Folder Fd, FR_User_View S1
WHERE 
	Fd.Owner_ID = S1.PE_ID 
AND Fd.Folder_Type IN (2, 3);