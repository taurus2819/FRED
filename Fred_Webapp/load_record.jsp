<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.text.*, nz.cri.gns.auth.*"
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
	ResultSet rs;
	User user = getUser(session);
	String foldID, recID, featID, recType, returnURL = null;
	int userID = user.getPersonId(), userRights;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
	out.println("<tr><td><a href='javascript:history.back();' title='Quit'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='javascript:history.back();' class='heading'>Quit</a></td></tr>");
	out.println("</table>");

	drawEndNavigation(out);

	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");

	if (request.getParameter("FoldID") != null && ((request.getParameter("SampID") != null && request.getParameter("RecID") != null) || request.getParameter("FeatID") != null) && request.getParameter("RecType") != null) {
		foldID = request.getParameter("FoldID");
		recID = request.getParameter("RecID");
		featID = request.getParameter("FeatID");
		recType = request.getParameter("RecType");

		if (recType.equals("SMP")) {
			returnURL = "samp_prop_data_entry.jsp";
		}
		else if (recType.equals("ADO")) {
			returnURL = "ado_data_entry.jsp";
		}
		else if (recType.equals("PAL")) {
			returnURL = "pal_data_entry.jsp";
		}
		else if (recType.equals("LOC")) {
			returnURL = "feat_data_entry.jsp";
		}

		//get user rights
		rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID + " AND Folder_Type = 'personal'");
		if (rs.next()) {
			userRights = rs.getInt(1);
		} else { //no record
			userRights = 0;
		}

		if (recType.equals("LOC")) {
			out.println("<p>Choose the locality to copy from the list below by clicking on the <img src='images/load.gif' width='20' height='20' /> icon</p>");

			//List records
			if ((userRights & 1) != 0 && returnURL != null) {
				rs = statement.executeQuery("SELECT DISTINCT Feature_ID, Sample_Name FROM Folder_Content_View WHERE Folder_ID = " + foldID + " AND Feature_ID <> " + featID + " ORDER BY Sample_Name");
				out.println("<table border='0' cellspacing='0' cellpadding='2'>");
				out.print("<tr class='heading'><td></td><td>Sample</td></tr>");
				while (rs.next()) {
					out.print("<tr><td><a href='" + returnURL + "?FoldID=" + foldID + "&FeatID=" + featID + "&LoadFeatID=" + rs.getString(1) + "' title='Copy Locality'><img src='images/load.gif' width='20' height='20' border='0' /></a><img src='images/blank.gif' width='10' height='1' /></td><td>" + rs.getString(2) + "</td></tr>");
				}
			}
		}
		else {
			out.println("<p>Choose the record to copy from the list below by clicking on the <img src='images/load.gif' width='20' height='20' /> icon</p>");

			//List records
			if ((userRights & 1) != 0 && returnURL != null) {
				rs = statement.executeQuery("SELECT DISTINCT R.Record_ID, R.Sample_Name, R.Drillhole_Depth, R.Record_Name FROM Record_View R, Folder_Content_View F WHERE F.Sample_ID = R.Sample_ID AND F.Folder_ID = " + foldID + " AND R.Record_ID <> " + recID + " AND R.Record_Type = " + JspUtils.sqlEscape(recType) + " ORDER BY R.Sample_Name, R.Drillhole_Depth, R.Record_Name");
				out.println("<table border='0' cellspacing='0' cellpadding='2'>");
				out.print("<tr class='heading'><td></td><td>Sample<img src='images/blank.gif' width='10' height='1' /></td><td>Depth");
				if (!recType.equals("SMP")) {
					out.print("<img src='images/blank.gif' width='10' height='1' /></td><td>Record");
				}
				out.println("</td></tr>");
				while (rs.next()) {
					out.print("<tr><td><a href='" + returnURL + "?FoldID=" + foldID + "&SampID=" + request.getParameter("SampID") + "&RecID=" + recID + "&LoadRecID=" + rs.getString(1) + "' title='Copy Record'><img src='images/load.gif' width='20' height='20' border='0' /></a><img src='images/blank.gif' width='10' height='1' /></td><td>" + rs.getString(2) + "<img src='images/blank.gif' width='10' height='1' /></td><td>" + noNulls(rs.getString(3)));
					if (!recType.equals("SMP")) {
					 out.print("<img src='images/blank.gif' width='10' height='1' /></td><td>" + rs.getString(4));
					}
					out.println("</td></tr>");
				}
			}
		}

		out.println("</table>");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
