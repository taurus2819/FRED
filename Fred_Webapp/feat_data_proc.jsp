<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, nz.cri.gns.util.map.*"
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
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	ResultSet rs;
	int userRights = 0, execUp;
	double latitude = 0, longitude = 0;
	String foldID, featID, auditID, siteID, featStatus, recoll = "", origCoord = "";
	User user = getUser(session);
	int userID = user.getPersonId();

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	if (request.getParameter("FoldID") != null && request.getParameter("SaveType") != null && request.getParameter("FeatID") != null) {

		foldID = request.getParameter("FoldID");
		featID = request.getParameter("FeatID");

		//set user rights
		rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID);
		if (rs.next()) { userRights = rs.getInt(1); }

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='heading'>Locality</td></tr>");
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
				featStatus = "waiting";
			} else {
				featStatus = "working";
			}

			//Field Number
			if (request.getParameter("FieldNum") != null) {
				rs = statement.executeQuery("SELECT * FROM Feature_Security_View WHERE User_ID = " + JspUtils.getUser(session) + " AND Feature_ID <> " + featID + " AND Sample_Name = '" + request.getParameter("FieldNum") + "'");
				if (rs.next()) {
					throw new DataInputException("Field Number", request.getParameter("FieldNum") + " already being used by you.  Please select a unique field number");
				}
			}

			//Coords
			if (request.getParameter("OrigCoord").equals("29")) { //LatLong
				latitude = Double.parseDouble(request.getParameter("North"));
				longitude = Double.parseDouble(request.getParameter("East"));
				origCoord = latitude + "|" + longitude;
			} else if (request.getParameter("OrigCoord").equals("38")) { //NZMG Full
				NorthingEasting nzmgCoord = new NorthingEasting(Double.parseDouble(request.getParameter("North")), Double.parseDouble(request.getParameter("East")));
				NZMG nzmg = new NZMG();
				Datum.LatLong latLong = nzmg.convertToNZGD49(nzmgCoord);
				latitude = latLong.getNorthSouth();
				longitude = latLong.getEastWest();
				origCoord = request.getParameter("East") + "|" + request.getParameter("North");
			} else if (request.getParameter("OrigCoord").equals("16")) { //NZMG trunc
				TruncNorthingEasting truncNzmgCoord = new TruncNorthingEasting(Double.parseDouble(request.getParameter("North")), Double.parseDouble(request.getParameter("East")), request.getParameter("NZMGSheet"), request.getParameter("East").length());
				NZMS260 nzms260 = new NZMS260();
				Datum.LatLong latLong = nzms260.convertToNZGD49(truncNzmgCoord);
				latitude = latLong.getNorthSouth();
				longitude = latLong.getEastWest();
				origCoord = request.getParameter("NZMGSheet") + "|" + request.getParameter("East") + "|" + request.getParameter("North");
			}

			//Recollection Number
			if (!request.getParameter("Recoll").equals("")) {
				recoll = "*Recoll:" + request.getParameter("Recoll") + "*";
				rs = statement.executeQuery("SELECT * FROM Feature_Security_View WHERE Sample_Name = " + JspUtils.sqlEscape(request.getParameter("Recoll")) + " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = " + userID + "))");
				if (!rs.next()) {
					throw new DataInputException("Recollection", request.getParameter("Recoll") + " is not an existing FR Number or temporary name.  Please use the builder to select.");
				}
			}


			//create SITE entry (if coords entered)
			//check for existing site (and create new one of necessary)
			if (!request.getParameter("Coord").equals("")) {
				rs = statement.executeQuery("SELECT SC.Site_Check(" + latitude + ", " + longitude + ", " + makeDropDownNulls(request.getParameter("LocMethodID")) + ", " + makeNulls(request.getParameter("Accuracy")) + ") FROM DUAL");
				rs.next();
				if (rs.getString(1) != null) {
					siteID = rs.getString(1);
				} else {
					rs = statement.executeQuery("SELECT SC.Site_Seq.NEXTVAL FROM DUAL");
					rs.next();
					siteID = rs.getString(1);
					execUp = statement.executeUpdate("INSERT INTO SC.Site (Site_ID, Site_Name, Latitude, Longitude, Method_ID, Accuracy, Directions, Orig_system_ID, Orig_Coord, Country_Code) VALUES (" + siteID + ", " + JspUtils.sqlEscape(request.getParameter("FieldNum")) + ", " + latitude + ", " + longitude + ", " + makeDropDownNulls(request.getParameter("LocMethodID")) + ", " + JspUtils.sqlEscape(request.getParameter("Accuracy")) + ", " + JspUtils.sqlEscape(request.getParameter("Loc")) + ", " + request.getParameter("OrigCoord") + ", '" + origCoord + "', '" + request.getParameter("Country") + "')");
				}
			} else {
				siteID = "NULL";
			}

			if (featID.equals("0")) {
				//create new audit, feature and sample data
				if ((userRights & 4) == 0) { throw new BadRightsException(); }
				rs = statement.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
				rs.next();
				auditID = rs.getString(1);
				execUp = statement.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Comments, Working_Folder_ID) VALUES (" + auditID + ", 'working', " + userID + ", SYSDATE, " + JspUtils.sqlEscape(recoll + request.getParameter("WorkComm")) + ", " + foldID + ")");
				rs = statement.executeQuery("SELECT Feature_Seq.NEXTVAL FROM DUAL");
				rs.next();
				featID = rs.getString(1);
				execUp = statement.executeUpdate("INSERT INTO Feature (Feature_ID, Site_ID, Audit_ID, Locality, Field_Number, Drillhole_Name, Reg_Area_ID) VALUES (" + featID + ", " + siteID + ", " + auditID + ", " + JspUtils.sqlEscape(request.getParameter("Loc")) + ", " + JspUtils.sqlEscape(request.getParameter("FieldNum")) + ", " + JspUtils.sqlEscape(request.getParameter("DrillName")) + ", " + makeDropDownNulls(request.getParameter("RegAreaID")) + ")");
				execUp = statement.executeUpdate("INSERT INTO Sample (Feature_ID) VALUES (" + featID + ")");
			} else { // edit
				//Update edited by fields
				if ((userRights & 2) == 0) { throw new BadRightsException(); }
				rs = statement.executeQuery("SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featID);
				rs.next();
				auditID = rs.getString(1);
				execUp = statement.executeUpdate("UPDATE Audit_Table SET Modified_By_ID = " + userID + ", Modified_Date = SYSDATE, Working_Comments = " + JspUtils.sqlEscape(recoll + request.getParameter("WorkComm")) + " WHERE Audit_ID = " + auditID);
				execUp = statement.executeUpdate("UPDATE Feature SET Site_ID = " + siteID + ", Locality = " + JspUtils.sqlEscape(request.getParameter("Loc")) + ", Field_Number = " + JspUtils.sqlEscape(request.getParameter("FieldNum")) + ", Drillhole_Name = " + JspUtils.sqlEscape(request.getParameter("DrillName")) + ", Reg_Area_ID = " + makeDropDownNulls(request.getParameter("RegAreaID")) + " WHERE Feature_ID = " + featID);
			}

			if (featStatus.equals("waiting")) { //submitted
				//change status, check MF & add saved record to folder
				rs =statement.executeQuery("SELECT Code FROM Lookup WHERE FieldName = 'RegArea' AND Lookup_ID = " + makeDropDownNulls(request.getParameter("RegAreaID")));
				if (rs.next()) {
					rs = statement.executeQuery("SELECT Which_Masterfile('" + rs.getString(1) + "', " + latitude + ", " + longitude + ") FROM DUAL");
					rs.next();
					String mfID = rs.getString(1);
					execUp = statement.executeUpdate("UPDATE Feature SET Masterfile_ID = " + mfID + " WHERE Feature_ID = " + featID);
					execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'waiting', Submitted_By_ID = " + userID + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL, Working_Comments = NULL WHERE Audit_ID = " + auditID);
					rs = statement.executeQuery("SELECT * FROM Folder_Content_View WHERE Feature_ID = " + featID + " AND Folder_ID = " + foldID);
					if (!rs.next()) {
						execUp = statement.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) VALUES (" + foldID + ", " + featID + ")");
					}
				}
				else { //Reg Area code not valid
					throw new DataInputException("Registration Area", "You have entered an invalid Registration Area and the record has not been submitted");
				}
			}

			statement2.close();

			response.sendRedirect("folder_detail.jsp?ID=" + foldID);

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