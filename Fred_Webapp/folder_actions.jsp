<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.fred.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, nz.cri.gns.auth.*"
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
	PageState state = new PageState(request, response, getServletContext());
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	Statement statement3 = connection.getExtraStatement();
	ResultSet rs, rs2, rs3;
	User user = getUser(session);
	String featID, recID, mfID, recType, sampID, auditID, drillSampName, errMessage = "";
	int execUp, recCount, i;

	if (request.getParameter("ID") != null && request.getParameter("ActionType") != null) {
		String foldID = request.getParameter("ID");
		String actionType = request.getParameter("ActionType");
		int userRights = FREDUtils.getUserFolderRights(user, foldID, state);

		try {

		 //Delete working records
		if (actionType.equals("DeleteRec") && (userRights & 8) != 0) {
			FolderUtils.deleteRecord(request.getParameter("RecID"), state);
		}

		//Delete working feature
		else if (actionType.equals("DeleteFeat") && (userRights & 8) != 0) {
			FolderUtils.deleteFeature(request.getParameter("FeatID"), state);
		}

		// submit working locality
		else if (actionType.equals("Submit") && (userRights & 16) != 0) {
			FolderUtils.submitLocality(request.getParameter("FeatID"), foldID, user, state);
		}

		// submit working record
		else if (actionType.equals("SubmitRec") && (userRights & 16) != 0) {
			FolderUtils.submitRecord(request.getParameter("RecID"), request.getParameter("RecType"), foldID, user, state);
		}

		//Revoke waiting records
		else if (actionType.equals("Revoke") && (userRights & 16) != 0) {
			FolderUtils.revokeRecord(request.getParameter("FeatID"), foldID, state);
		}

		//Copy locality
		else if (actionType.equals("CopyFeat") && (userRights & 4) != 0) {
			FolderUtils.copyLocality(request.getParameter("FeatID"), request.getParameter("NewFeatName"), foldID, user, state)
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
*/

		} catch (FolderUtilException e) {
			errMessage = "&ErrMsg=" + URLEncoder.encode("<script language='JavaScript'>alert(\"" + e.getMessage() + "\");</script>", "UTF-8");
		} catch (Exception e) {
			errMessage = "&ErrMsg=" + URLEncoder.encode("<script language='JavaScript'>alert(\"Unspecified Error - action not processed\");</script>", "UTF-8");
		}
		response.sendRedirect("folder_detail.jsp?ID=" + foldID + errMessage);
	}
%>
