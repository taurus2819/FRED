<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, nz.cri.gns.auth.*"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		try {
			return new Authenticable[] {
				 new IPRightAccess(
					new IPRight(
						"FRED data entry",
						getIPApp(
							request.getSession(),
							getServletConfig().getServletContext())),
					Right.ANY_RIGHT)};
		} catch (Exception e) {
			//Database error, so just block them
			return new Authenticable[] {
				 new IPRightAccess(
					IPRight.BLOCKED_IP_RIGHT,
					Right.BLOCKED_RIGHT)};
		}
	}
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	Statement statement3 = connection.getExtraStatement();
	ResultSet rs, rs2, rs3;
	User user = getUser(session);
	String foldID, featID, recID, mfID, recType, sampID, auditID, drillSampName, actionType, errMessage = "";
	int userID = user.getPersonId(), execUp, userRights, recCount, i;

	if (request.getParameter("ID") != null && request.getParameter("ActionType") != null) {
		foldID = request.getParameter("ID");
		actionType = request.getParameter("ActionType");

		//get user rights
		rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID + " AND Folder_Type = 'personal'");
		if (rs.next()) {
			userRights = rs.getInt(1);
		} else { //no record
			userRights = 0;
		}

		 //Delete working records
		if (actionType.equals("DeleteRec") && (userRights & 8) != 0) {
			recID = request.getParameter("RecID");
			rs = statement.executeQuery("SELECT Audit_ID FROM Record WHERE Record_ID = " + recID);
			if (rs.next()) {
				auditID = rs.getString(1);
				execUp = statement.executeUpdate("DELETE FROM Record WHERE Record_ID = " + recID);
				execUp = statement.executeUpdate("DELETE FROM Audit_Table WHERE Audit_ID = " + auditID);
			}
		}

		//Delete working feature
		else if (actionType.equals("DeleteFeat") && (userRights & 8) != 0) {
			featID = request.getParameter("FeatID");
			auditID = "";
			rs = statement.executeQuery("SELECT Audit_ID FROM Record WHERE Sample_ID IN (SELECT Sample_ID FROM Sample WHERE Feature_ID = " + featID + ")");
			while (rs.next()) {
				auditID = auditID + rs.getString(1) + ", ";
			}
			rs = statement.executeQuery("SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featID);
			rs.next();
			auditID = rs.getString(1);
			execUp = statement.executeUpdate("DELETE FROM Record WHERE Sample_ID IN (SELECT Sample_ID FROM Sample WHERE Feature_ID = " + featID + ")");
			execUp = statement.executeUpdate("DELETE FROM Feature WHERE Feature_ID = " + featID);
			execUp = statement.executeUpdate("DELETE FROM Audit_Table WHERE Audit_ID IN (" + auditID + ")");
		}

		// submit working locality
		else if (actionType.equals("Submit") && (userRights & 16) != 0) {
			featID = request.getParameter("FeatID");
			rs = statement.executeQuery("SELECT Audit_ID FROM Feature WHERE Site_ID IS NOT NULL AND Locality IS NOT NULL AND (Field_Number IS NOT NULL OR Drillhole_Name IS NOT NULL) AND Feature_ID = " + featID);
			if (rs.next()) {
				auditID = rs.getString(1);
				//decide whether drillhole or outcrop
				rs = statement.executeQuery("SELECT Drillhole_Name FROM Feature WHERE Feature_ID = " + featID);
				rs.next();
				if (rs.getString(1) == null) { //outcrop so also check sample property record
					rs = statement.executeQuery("SELECT Audit_ID FROM Sample_Property_All_View WHERE Collection_Date IS NOT NULL AND Collector IS NOT NULL AND Strat_Unit IS NOT NULL AND In_Place IS NOT NULL AND Feature_ID = " + featID);
					if (rs.next()) {
						//OK so update sample property audit table
						execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'approved', Submitted_By_ID = " + userID + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL, Working_Comments = NULL WHERE Audit_ID = " + rs.getString(1));
					} else {
						//not OK, so flag by setting AuditID = -1
						auditID = "-1";
						errMessage =  "&ErrMsg=" + URLEncoder.encode("<script language='JavaScript'>alert(\"Cannot submit locality as not all mandatory fields in sample property record have been completed\");</script>");
					}
				}
				if (!auditID.equals("-1")) { //check that auditId hasn't changed it -1 to indicate bad SampProp record
					//Update Masterfile region
					rs = statement.executeQuery("SELECT Which_Masterfile('NZ', S.Latitude, S.Longitude) FROM Feature F, SC.Site S WHERE F.Site_ID = S.Site_ID AND F.Feature_ID = " + featID);
					rs.next();
					mfID = rs.getString(1);
					execUp = statement.executeUpdate("UPDATE Feature SET Masterfile_ID = " + mfID + " WHERE Feature_ID = " + featID);
					//Update AUDIT_TABLE
					execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'waiting', Submitted_By_ID = " + userID + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL WHERE Audit_ID = " + auditID);
					//Check if need to add to FOLDER_CONTENT
					rs = statement.executeQuery("SELECT * FROM Folder_Content_View WHERE Feature_ID = " + featID + " AND Folder_ID = " + foldID);
					if (!rs.next()) {
						execUp = statement.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) VALUES (" + foldID + ", " + featID + ")");
					}
				}
			} else {
				errMessage =  "&ErrMsg=" + URLEncoder.encode("<script language='JavaScript'>alert(\"Cannot submit locality as not all mandatory fields have been completed\");</script>");
			}
		}

		// submit working record
		else if (actionType.equals("SubmitRec") && (userRights & 16) != 0) {
			recID = request.getParameter("RecID");
			recType = request.getParameter("RecType");
			//check mandatory fields
			if (recType.equals("SMP")) {
				rs = statement.executeQuery("SELECT Audit_ID FROM Sample_Property_All_View WHERE Collection_Date IS NOT NULL AND Collector IS NOT NULL AND Strat_Unit IS NOT NULL AND In_Place IS NOT NULL AND Record_ID = " + recID);
			} else if (recType.equals("ADO")) {
				rs = statement.executeQuery("SELECT Audit_ID FROM Adoption WHERE Adoptor_ID IS NOT NULL AND Adoption_Date IS NOT NULL");
			} else if (recType.equals("PAL")) {
				rs = statement.executeQuery("SELECT Audit_ID FROM Paleontology WHERE Identifier_ID IS NOT NULL AND Identification_Date IS NOT NULL");
			}
			if (rs.next()) {
				//update audit table
				execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'approved', Submitted_By_ID = " + userID + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL, Working_Comments = NULL WHERE Audit_ID = " + rs.getString(1));
				//add feature to FOLDER_CONTENT if not already there (as no longer listed as a working record
				rs = statement.executeQuery("SELECT S.Feature_ID FROM Sample S, Record R WHERE S.Sample_ID = R.Sample_ID AND R.Record_ID = " + recID);
				rs.next();
				featID = rs.getString(1);
				rs = statement.executeQuery("SELECT * FROM Folder_Content WHERE Folder_ID = " + foldID + " AND Feature_ID = " + featID);
				if (!rs.next()) {
					execUp = statement.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) VALUES (" + foldID + ", " + featID + ")");
				}
			} else {
				errMessage = "&ErrMsg=" + URLEncoder.encode("<script language='JavaScript'>alert(\"Cannot submit record as not all mandatory fields have been completed\");</script>");
			}
		}

		//Revoke waiting records
		else if (actionType.equals("Revoke") && (userRights & 16) != 0) {
			featID = request.getParameter("FeatID");
			execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'working', Working_Folder_ID = " + foldID + " WHERE Audit_ID IN (SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featID + ")");
			//decide whether drillhole or outcrop
			rs = statement.executeQuery("SELECT Drillhole_Name FROM Feature WHERE Feature_ID = " + featID);
			rs.next();
			if (rs.getString(1) == null) { //outcrop so also revoke sample property record
				execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'working', Working_Folder_ID = " + foldID + " WHERE Audit_ID IN (SELECT DISTINCT Audit_ID FROM Sample_Property_All_View WHERE Feature_ID = " + featID + ")");
			}
		}

		//Copy locality
		else if ((actionType.equals("CopyDrill") || actionType.equals("CopyFeat")) && (userRights & 4) != 0) {
			String oldFeatID, oldSampID, oldRecID;
			oldFeatID = request.getParameter("FeatID");
			rs = statement.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
			rs.next();
			auditID = rs.getString(1);
			execUp = statement.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Folder_ID) VALUES (" + auditID + ", 'working', " + userID + ", SYSDATE, " + foldID + ")");
			rs = statement.executeQuery("SELECT Feature_Seq.NEXTVAL FROM DUAL");
			rs.next();
			featID = rs.getString(1);
			execUp = statement.executeUpdate("INSERT INTO Feature (Feature_ID, Site_ID, Audit_ID, Masterfile_ID, Locality, Reg_Area_ID, Comments) SELECT " + featID + " AS FeatID, Site_ID, " + auditID + " AS AuditID, Masterfile_ID, Locality, Reg_Area_ID, Comments FROM Feature WHERE Feature_ID = " + oldFeatID);
			if (actionType.equals("CopyDrill")) {
				execUp = statement.executeUpdate("UPDATE Feature SET Drillhole_Name = " + JspUtils.sqlEscape(request.getParameter("NewFeatName")) + " WHERE Feature_ID = " + featID);
			} else {
				execUp = statement.executeUpdate("UPDATE Feature SET Field_Number = " + JspUtils.sqlEscape(request.getParameter("NewFeatName")) + " WHERE Feature_ID = " + featID);
			}
			rs = statement.executeQuery("SELECT Sample_ID FROM Sample WHERE Feature_ID = " + oldFeatID);
			while (rs.next()) {
				oldSampID = rs.getString(1);
				rs2 = statement2.executeQuery("SELECT Sample_Seq.NEXTVAL FROM DUAL");
				rs2.next();
				sampID = rs2.getString(1);
				execUp = statement2.executeUpdate("INSERT INTO Sample (Sample_ID, Feature_ID, FR_ID, Top_Depth, Bottom_Depth, Drill_Type, Comments) SELECT " + sampID + " AS SampID, " + featID + " AS FeatID, NULL AS FRID, Top_Depth, Bottom_Depth, Drill_Type, Comments FROM Sample WHERE Sample_ID = " + oldSampID);
				rs2 = statement2.executeQuery("SELECT Record_ID, Record_Type FROM Record_All_View WHERE Sample_ID = " + oldSampID);
				while (rs2.next()) {
					oldRecID = rs2.getString(1);
					rs3 = statement3.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
					rs3.next();
					auditID = rs3.getString(1);
					execUp = statement3.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Folder_ID) VALUES (" + auditID + ", 'working', " + userID + ", SYSDATE, " + foldID + ")");
					rs3 = statement3.executeQuery("SELECT Record_Seq.NEXTVAL FROM DUAL");
					rs3.next();
					recID = rs3.getString(1);
					execUp = statement3.executeUpdate("INSERT INTO Record (Record_ID, Sample_ID, Audit_ID) VALUES (" + recID + ", " + sampID + ", " + auditID + ")");
					if (rs2.getString(2).equals("SMP")) {
						execUp = statement3.executeUpdate("INSERT INTO Sample_Property (Record_ID, Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage_ID, Known_Stage_ID, Column_Map, Dip, Dip_Direction, Strike, Facing, Primary_Grainsize_ID, Secondary_Grainsize_ID, Comparator_Used, Bed_Thick_ID, Primary_Bedding_ID, Secondary_Bedding_ID, Weathering_ID, Hardness_ID, Carbonate_ID, Colour_Modifier_ID, Primary_Colour_ID, Secondary_Colour_ID, Wet, Rock_Nature, Deposition_Env, Correspondence) SELECT " + recID + " AS RecID, Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage_ID, Known_Stage_ID, Column_Map, Dip, Dip_Direction, Strike, Facing, Primary_Grainsize_ID, Secondary_Grainsize_ID, Comparator_Used, Bed_Thick_ID, Primary_Bedding_ID, Secondary_Bedding_ID, Weathering_ID, Hardness_ID, Carbonate_ID, Colour_Modifier_ID, Primary_Colour_ID, Secondary_Colour_ID, Wet, Rock_Nature, Deposition_Env, Correspondence FROM Sample_Property WHERE Record_ID = " + oldRecID);
						execUp = statement3.executeUpdate("INSERT INTO Collector (Record_ID, Person_ID) SELECT " + recID + " AS RecID, Person_ID FROM Collector WHERE Record_ID = " + oldRecID);
						execUp = statement3.executeUpdate("INSERT INTO Relationship (Relationship_ID, Record_ID, Relationship_Type, Related_Feature_ID, Strat_Unit, Distance, Distance_Range, Distance_Mod, Relation_Type_ID) SELECT Relationship_Seq.NEXTVAL, " + recID + " AS RecID, Relationship_Type, Related_Feature_ID, Strat_Unit, Distance, Distance_Range, Distance_Mod, Relation_Type_ID FROM Relationship WHERE Record_ID = " + oldRecID);
						execUp = statement3.executeUpdate("INSERT INTO Sedimentary_Feature (Record_ID, Sed_Feature_ID, Abundant) SELECT " + recID + " AS RecID, Sed_Feature_ID, Abundant FROM Sedimentary_Feature WHERE Record_ID = " + oldRecID);
						execUp = statement3.executeUpdate("INSERT INTO Sent_To (Record_ID, Fossil_Group_ID, Person_ID, Lab_ID, Comments) SELECT " + recID + " AS RecID, Fossil_Group_ID, Person_ID, Lab_ID, Comments FROM Sent_To WHERE Record_ID = " + oldRecID);
					} else if (rs2.getString(2).equals("ADO")) {
						execUp = statement3.executeUpdate("INSERT INTO Adoption (Record_ID, Adoptor_ID, Adoption_Date, Date_Rounding, Adopted_Stage_ID, Comments) SELECT " + recID + " AS RecID, Adoptor_ID, Adoption_Date, Date_Rounding, Adopted_Stage_ID, Comments FROM Adoption WHERE Record_ID = " + oldRecID);
					} else if (rs2.getString(2).equals("PAL")) {
						execUp = statement3.executeUpdate("INSERT INTO Paleontology (Record_ID, Identifier_ID, Identification_Date, Date_Rounding, Stage_ID, Stage_Comments, Lab_Section_ID, Lab_Number, Collection_Comments) SELECT " + recID + " AS RecID, Identifier_ID, Identification_Date, Date_Rounding, Stage_ID, Stage_Comments, Lab_Section_ID, Lab_Number, Collection_Comments FROM Paleontology WHERE Record_ID = " + oldRecID);
						execUp = statement3.executeUpdate("INSERT INTO Pal_List (Pal_List_ID, Record_ID, Group_ID, Taxa_ID, Taxonomic_Name, Specimen_Count, Specimen_Coords, Comments) SELECT Pal_List_Seq.NEXTVAL, " + recID + " AS RecID, Group_ID, Taxa_ID, Taxonomic_Name, Specimen_Count, Specimen_Coords, Comments FROM Pal_List WHERE Record_ID = " + oldRecID);

					}
				}
			}
		}

		//Move working records
		else if (actionType.equals("MoveFold") && (userRights & 8) != 0) {
			//check that user has rights to destination folder
			rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + request.getParameter("NewFoldID") + " AND User_ID = " + userID);
			if (rs.next() && (rs.getInt(1) & 4) != 0) {
				//Sufficient rights so go ahead
				rs = statement.executeQuery("SELECT COUNT(*) FROM Folder_Content_View WHERE Folder_ID = " + foldID);
				rs.next();
				recCount = rs.getInt(1);
				for (i = 0; i < recCount; i++) {
					if (request.getParameter("Check" + i) != null) {
						featID = request.getParameter("Check" + i);
						//update FOLDER_CONTENT (if not already there
						rs = statement.executeQuery("SELECT * FROM Folder_Content WHERE Folder_ID = " + request.getParameter("NewFoldID") + " AND Feature_ID = " + featID);
						if (!rs.next()) {
							execUp = statement.executeUpdate("UPDATE Folder_Content SET Folder_ID = " + request.getParameter("NewFoldID") + " WHERE Folder_ID = " + foldID + " AND Feature_ID = " + featID);
						} else { //reature already in move folder so just delete from this folder
							execUp = statement.executeUpdate("DELETE FROM Folder_Content WHERE Folder_ID = " + foldID + " AND Feature_ID = " + featID);
						}
						//move working features
						execUp = statement.executeUpdate("UPDATE Audit_Table SET Working_Folder_ID = " + request.getParameter("NewFoldID") + " WHERE Working_Folder_ID = " + foldID + " AND Audit_ID IN (SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featID + ")");
						//move working records
						execUp = statement.executeUpdate("UPDATE Audit_Table SET Working_Folder_ID = " + request.getParameter("NewFoldID") + " WHERE Working_Folder_ID = " + foldID + " AND Audit_ID IN (SELECT R.Audit_ID FROM Sample S, Record R WHERE S.Sample_ID = R.Sample_ID AND S.Feature_ID = " + featID + ")");
					}
				}
			}
		}

		//Copy working records
		else if (actionType.equals("CopyFold")) {
			//check that user has rights to destination folder
			rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + request.getParameter("NewFoldID") + " AND User_ID = " + userID);
			if (rs.next() && (rs.getInt(1) & 4) != 0) {
				//Sufficient rights so go ahead
				rs = statement.executeQuery("SELECT COUNT(*) FROM Folder_Content_View WHERE Folder_ID = " + foldID);
				rs.next();
				recCount = rs.getInt(1);
				for (i = 0; i < recCount; i++) {
					if (request.getParameter("Check" + i) != null) {
						featID = request.getParameter("Check" + i);
						//check that not a working feature
						rs = statement.executeQuery("SELECT Status FROM Sample_All_View WHERE Feature_ID = " + featID);
						if (rs.next() && rs.getString(1).equals("approved")) {
						//insert into FOLDER_CONTENT (if not a working feature)
							rs = statement.executeQuery("SELECT * FROM Folder_Content WHERE Folder_ID = " + request.getParameter("NewFoldID") + " AND Feature_ID = " + featID);
							if (!rs.next()) {
								execUp = statement.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) VALUES (" + request.getParameter("NewFoldID") + ", " + featID + ")");
							}
						}
					}
				}
			}
		}
	
		//remove from folder
		else if (actionType.equals("Remove") && (userRights & 8) != 32) {
			rs = statement.executeQuery("SELECT COUNT(*) FROM Folder_Content_View WHERE Folder_ID = " + foldID);
			rs.next();
			recCount = rs.getInt(1);
			for (i = 0; i < recCount; i++) {
				if (request.getParameter("Check" + i) != null) {
					execUp = statement.executeUpdate("DELETE FROM Folder_Content WHERE Feature_ID = " + request.getParameter("Check" + i));
				}
			}
		}

	statement2.close();
	statement3.close();

		response.sendRedirect("folder_detail.jsp?ID=" + foldID + errMessage);
	}
%>
