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
	Statement statement2 = connection.getExtraStatement();
	Statement statement3 = connection.getExtraStatement();
	ResultSet rs, rs2, rs3;
	User user = getUser(session);
	String foldID, featID, sampID, drillSampName;
	int userID = user.getPersonId(), userRights;
	boolean sampPropFlag;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	if (request.getParameter("ID") != null) {
		foldID = request.getParameter("ID");

		//get user rights
		rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID + " AND Folder_Type = 'admin'");
		if (rs.next()) {
			userRights = rs.getInt(1);
		} else { //no record
			userRights = 0;
		}

		rs = statement.executeQuery("SELECT Name FROM Folder WHERE Folder_ID = " + foldID);
		if ((userRights & 1) != 0 && rs.next()) {
			
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/folder.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' class='bigheading' align='center'>Masterfile: " + rs.getString(1) + "</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			//List records

			//Table header
			out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
			out.println("<tr><th colspan='2'>Name<img src='blank.gif' width='10' height='1' /></th><th>Field No/<br>Drillhole<img src='blank.gif' width='10' height='1' /></th><th>Submitted Date<img src='blank.gif' width='10' height='1' /></th><th>Options</th></tr>");

			//Feature
			rs3 = statement3.executeQuery("SELECT DISTINCT Feature_ID, Sample_Name FROM Masterfile_Content_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID + " ORDER BY Sample_Name");
			featID = "";
			while (rs3.next()) {
				if (rs3.getString(1).equals(featID)) { continue; }
				featID = rs3.getString(1);
				rs = statement.executeQuery("SELECT S.Sample_ID, S.Sample_Name, S.Drillhole_Name, S.Field_Number, A.Submitted_Date FROM Sample_All_View S, Audit_View A WHERE S.Audit_ID = A.Audit_ID AND S.Feature_ID = " + featID);
				rs.next();
				if (rs.getString(3) != null) { //drillhole so loop through individual sample names
					drillSampName = "";
					rs2 = statement2.executeQuery("SELECT DISTINCT Sample_Name FROM Sample_All_View WHERE Feature_ID = " + featID + " ORDER BY Sample_Name");
					while (rs2.next()) { drillSampName = drillSampName + rs2.getString(1) + ", "; }
					drillSampName = drillSampName.substring(0, drillSampName.length() - 2);
					out.print("<tr><td><img src='images/loc.gif' height='20' width='20' /><img src='images/blank.gif' width='5' height='20' /></td><td class='heading'><a href='drillhole_detail.jsp?ID=" + featID + "'>" + drillSampName + "</a></td><td class='heading'><a href='drillhole_detail.jsp?ID=" + featID + "'>" + rs.getString(3) +"</a></td><td>");
				} else {
					out.print("<tr><td class='heading'><img src='images/loc.gif' height='20' width='20' /><img src='images/blank.gif' width='5' height='20' /></td><td class='heading'><a href='detail.jsp?ID=" + rs.getString(1) + "'>" + rs.getString(2) + "</a></td><td>" + noNulls(rs.getString(4)) + "</td><td>");
				}
				if (rs.getString(5) != null) { 
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(5)));
				}
				out.print("</td><td class='smallheading'>");
				if ((userRights & 64) != 0) {
					out.print("<a href='review.jsp?ID=" + featID + "' title='Review'><img src='images/review.gif' width='20' height='20' border='0' /></a>");
					out.print("<img src='images/blank.gif' height='20' width='20' /><a href='feat_data_entry.jsp?Type=Outcrop&FeatID=" + featID + "&FoldID=" + foldID + " 'title='Edit Locality'><img src='images/edit.gif' border='0' height='20' width='20'></a>");
				}
				out.println("&nbsp;</td></tr>");
			}
			out.println("</table></p>");
		}
		else { //no record found
			out.println("No folder found");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);

	statement2.close();
	statement3.close();
%>