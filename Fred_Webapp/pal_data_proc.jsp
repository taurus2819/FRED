<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*"
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
%><%!	class BadRightsException extends Exception {}
%><%!	class DataInputException extends Exception {
			private String field;
			DataInputException() { }
			DataInputException(String field, String msg) { super(msg); this.field = field; }
			public String getField() { return field; }
		}
%><%!	private int checkStage (String stageStart, String stageStop, Statement statement) throws java.sql.SQLException {
			if (!stageStop.equals("-")) {
				ResultSet rs = statement.executeQuery("SELECT Ta_Age_Start, Ta_Age_Stop FROM Age_View WHERE Ag_ID = " + stageStart);
				rs.next();
				double startStart = rs.getDouble(1);
				double startStop = rs.getDouble(2);
				rs = statement.executeQuery("SELECT Ta_Age_Start, Ta_Age_Stop FROM Age_View WHERE Ag_ID = " + stageStop);
				rs.next();
				double stopStart = rs.getDouble(1);
				double stopStop = rs.getDouble(2);
				if (startStart < stopStart || startStop < stopStop) { return 0; }
			}
			return 1;
		}
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	ResultSet rs;
	int userRights = 0, execUp, i = -1;
	String foldID, sampID, recID, auditID = "", featStatus, identifierID = "", palDate = "", dateRnd = "", stageID = "";
	User user = getUser(session);
	int userID = user.getPersonId();
	long[] identID = new long[20];

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	if (request.getParameter("FoldID") != null && request.getParameter("SaveType") != null && request.getParameter("RecID") != null && request.getParameter("SampID") != null) {

		foldID = request.getParameter("FoldID");
		sampID = request.getParameter("SampID");
		recID = request.getParameter("RecID");

		//set user rights
		rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID);
		if (rs.next()) { userRights = rs.getInt(1); }

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/pal.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='heading'>Paleontology Record</td></tr>");
		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("<tr><td><a href='javascript:history.back();' title='Back to Data Entry'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='javascript:history.back();' class='heading'>Back to Data Entry</a></td></tr>");
		out.println("<tr><td><a href='folder_detail.jsp?ID=" + foldID + "' title='Quit Without Saving'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_detail.jsp?ID=" + foldID + "' class='heading'>Quit</a></td></tr>");
		out.println("</table>");

		drawEndNavigation(out);

		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		try { //Surround with exception testing so can throw an exception if data problem

			//Parse tricky fields
			//Status
			if (request.getParameter("SaveType").equals("Submit")) {
				if ((userRights & 16) == 0) { throw new BadRightsException(); }
				featStatus = "approved";
			} else {
				featStatus = "working";
			}
			//Dates
			if (!request.getParameter("PalDate").equals("")) {
				dateRnd = request.getParameter("DateRnd");
				if (dateRnd.equals("Year")) {
					palDate = "1/1/" + request.getParameter("PalDate");
				} else if (dateRnd.equals("Month")) {
					palDate = "1/" + request.getParameter("PalDate");
				} else {
					palDate = request.getParameter("PalDate");
				}
			}
			//Identifier_ID
			if (!request.getParameter("Identifier").equals("")) {
				String identifier = request.getParameter("Identifier");
				i = 0;
				while (identifier.length() > 0) {
					if (identifier.indexOf("\n") == -1) { identifier = identifier + "\n"; }
					rs = statement.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = '" + identifier.substring(0, identifier.indexOf("\n")).trim() + "'");
					if (rs.next()) {
						identID[++i] = rs.getLong(1);
					} else {  //Identifier not in database so throw exception
						throw new DataInputException("Identifier", identifier.substring(0, identifier.indexOf("\n")).trim() + " not in database - add through builder");
					}
					identifier = identifier.substring(identifier.indexOf("\n") + 1, identifier.length());
				}
			}
			//Stage ages
			if (checkStage(request.getParameter("StageStart"), request.getParameter("StageStop"), statement) == 0) { throw new DataInputException("Stage", "Stop age greater than Start age"); }
			if (recID.equals("0")) {
				//create new audit, record data
				if ((userRights & 4) == 0) { throw new BadRightsException(); }
				rs = statement.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
				rs.next();
				auditID = rs.getString(1);
				execUp = statement.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Comments, Working_Folder_ID) VALUES (" + auditID + ", 'working', " + userID + ", SYSDATE, " + JspUtils.sqlEscape(request.getParameter("WorkComm")) + ", " + foldID + ")");
				rs = statement.executeQuery("SELECT Record_Seq.NEXTVAL FROM DUAL");
				rs.next();
				recID = rs.getString(1);
				execUp = statement.executeUpdate("INSERT INTO Record (Record_ID, Sample_ID, Audit_ID) VALUES (" + recID + ", " + sampID + ", " + auditID + ")");
			} else { // edit
				//Update edited by fields and delete PALEONTOLOGY (ready for adding new data from form)
				if ((userRights & 2) == 0) { throw new BadRightsException(); }
				rs = statement.executeQuery("SELECT Audit_ID FROM Record WHERE Record_ID = " + recID);
				rs.next();
				auditID = rs.getString(1);
				execUp = statement.executeUpdate("UPDATE Audit_Table SET Modified_By_ID = " + userID + ", Modified_Date = SYSDATE, Working_Comments = " + JspUtils.sqlEscape(request.getParameter("WorkComm")) + " WHERE Audit_ID = " + auditID);
				execUp = statement.executeUpdate("DELETE FROM Paleontology WHERE Record_ID = " + recID);
			}

			//Create STAGE entry
			if (!request.getParameter("StageStart").equals("-")) {
				rs = statement.executeQuery("SELECT Get_Stage_ID(" + request.getParameter("StageStart") + ", " + makeDropDownNulls(request.getParameter("SartMod")) + ", " + makeDropDownNulls(request.getParameter("StageStop")) + ", " + makeDropDownNulls(request.getParameter("StopMod")) + ") FROM DUAL");
				rs.next();
				if (rs.getString(1) != null) {
					stageID = rs.getString(1);
				} else {
					rs = statement.executeQuery("SELECT Stage_Seq.NEXTVAL FROM DUAL");
					rs.next();
					stageID = rs.getString(1);
					execUp = statement.executeUpdate("INSERT INTO Stage (Stage_ID, Stage_Lower_ID, Stage_Lower_Mod, Stage_Upper_ID, Stage_Upper_Mod) VALUES (" + stageID + ", " + request.getParameter("StageStart") + ", " + makeDropDownNulls(request.getParameter("StartMod")) + ", " + makeDropDownNulls(request.getParameter("StageStop")) + ", " + makeDropDownNulls(request.getParameter("StopMod")) + ")");
				}
			}

			//Create PALEONTOLOGY entry
			execUp = statement.executeUpdate("INSERT INTO Paleontology (Record_ID, Identification_Date, Date_Rounding, Stage_ID, Stage_Comments, Lab_Section_ID, Lab_Number, Collection_Comments) VALUES (" + recID + ", TO_DATE('" + palDate + "'), " + JspUtils.sqlEscape(dateRnd) + ", " + JspUtils.sqlEscape(stageID) + ", " + JspUtils.sqlEscape(request.getParameter("StComm")) + ", " + makeDropDownNulls(request.getParameter("SectID")) + ", " + JspUtils.sqlEscape(request.getParameter("LabNum")) + ", " + JspUtils.sqlEscape(request.getParameter("CollComm")) + ")");

			//Create IDENTIFIERS entries
			for (int j = 0; j < identID.length; j++) {
				if (identID[j] > 0) { execUp = statement.executeUpdate("INSERT INTO Identifier (Record_ID, Person_ID) VALUES (" + recID + ", " + identID[j] + ")"); }
			}

			statement2.close();

			//Move to next page (for taxanomic parsing)
			session.setAttribute("Taxa",request.getParameter("Taxa"));
			response.sendRedirect("pal_data_proc2.jsp?FoldID=" + foldID + "&SampID=" + sampID + "&RecID=" + recID + "&Status=" + featStatus + "&AuditID=" + auditID);
			//response.sendRedirect("pal_data_proc2.jsp?FoldID=" + foldID + "&SampID=" + sampID + "&RecID=" + recID + "&Status=" + featStatus + "&AuditID=" + auditID + "&Taxa=" + URLEncoder.encode(request.getParameter("Taxa")));

		} catch (DataInputException e) {
			out.println("<p><div class='bigheading'>Data Error</div></p>");
			out.println("<table border='0' cellspacing='0'>");
			out.println("<tr><td class='heading'>Problem Field<img src='images/blank.gif' width='20' height='1' /></td><td>" + e.getField() + "</td></tr>");
			out.println("<tr><td class='heading'>Error</td><td>"+ e.getMessage() + "</td></tr>");
			out.println("</table>");
		} catch (BadRightsException e) {
			out.println("<p><div class='bigheading'>Access Denied</div></p>");
			out.println("<p>You do not have sufficient rights to save this record</p>");
		} catch (NullPointerException e) {
			out.println("<p><div class='bigheading'>Error</div></p>");
			out.println("<p>Not all fields recieved</p>");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
