<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.text.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	ResultSet rs;
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");
	SimpleDateFormat yearFormatter = new SimpleDateFormat ("yyyy");
	SimpleDateFormat monthFormatter = new SimpleDateFormat ("MMM yyyy");
	User user = getUser(session);
	String featType = "", featID, palID, status = "";
	int i = 1, userRights = 0, execUp, userID = 0;

	if (user != null) { userID = user.getPersonId(); }

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("ID") != null) {
		featID = request.getParameter("ID");

		//check if user can view this record and that record exists
		rs = statement.executeQuery("SELECT Status, User_Rights FROM Feature_Security_View WHERE Feature_ID = " + featID + " AND (User_ID IS NULL OR User_ID = " + userID + ")");
		while (rs.next()) { //accumulate rights over multiple folders
			status = rs.getString(1);
			userRights = (userRights | rs.getInt(2));
		}

		rs = statement.executeQuery("SELECT Feature_Name FROM Sample_All_View WHERE Feature_Type <> 'Outcrop' AND Feature_ID = " + featID);
		
		if ((userRights & 1) != 0 && rs.next()) { //allowed to view this record and the record is not an outcrop

			out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
			rs = statement.executeQuery("SELECT S.Feature_Name, S.Feature_Type, S.Masterfile_Name, S.Status, A.Created_By, A.Created_Date, A.Modified_By, A.Modified_Date, A.Submitted_By, A.Submitted_Date, A.Approved_By, A.Approved_Date FROM Sample_All_View S, Audit_View A WHERE S.Audit_ID = A.Audit_ID AND S.Feature_ID = " + featID);
			rs.next();
			featType = rs.getString(2);
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='bigheading' >" + rs.getString(1) + "</td></tr>");
			out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
			if (rs.getString(3) != null) {
				out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + rs.getString(3) + "</td></tr>");
			}
			if (!rs.getString(4).equals("approved")) {
				out.println("<tr><td class='smallheading'>Status:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + rs.getString(4) + "</td></tr>");
			}
			if (rs.getString(5) != null || rs.getString(6) != null) {
				out.println("<tr><td class='smallheading'>Created:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (rs.getString(5) != null) { out.print(rs.getString(5) + "<br />"); }
				if (rs.getString(6) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(6))); }
				out.println("</td></tr>");
			}
			if (rs.getString(7) != null || rs.getString(8) != null) {
				out.println("<tr><td class='smallheading'>Edited:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (rs.getString(7) != null) { out.print(rs.getString(7) + "<br />"); }
				if (rs.getString(8) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(8))); }
				out.println("</td></tr>");
			}
			if (rs.getString(9) != null || rs.getString(10) != null) {
				out.println("<tr><td class='smallheading'>Submitted:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (rs.getString(9) != null) { out.print(rs.getString(9) + "<br />"); }
				if (rs.getString(10) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(10))); }
				out.println("</td></tr>");
			}
			if (rs.getString(11) != null || rs.getString(12) != null) {
				out.println("<tr><td class='smallheading'>Approved:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (rs.getString(11) != null) { out.print(rs.getString(11) + "<br />"); }
				if (rs.getString(12) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(12))); }
				out.println("</td></tr>");
			}
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			rs = statement.executeQuery("SELECT NZMG_Sheet, NZMG_East, NZMG_North, Latitude, Longitude, Accuracy, Method, Locality, Person, Start_Date, Start_Date_Rounding, Finish_Date, Finish_Date_Rounding, Drillhole_Licence_Name, Datum_Type, Datum_Elevation, Start_Depth, Finish_Depth FROM Sample_All_View WHERE Feature_ID = " + featID);
			rs.next();

			out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
			if (rs.getString(4) != null) {
				out.print("<tr><td class='heading'>Grid Ref</td><td>");
				if (rs.getString(1) != null) {
					out.print(rs.getString(1) + ": " + nzmg.format(rs.getDouble(2)) + ", " + nzmg.format(rs.getDouble(3)));
					out.print("<img src='images/blank.gif' width='20' height='1' />|<img src='images/blank.gif' width='20' height='1' />");
				}
				if (rs.getDouble(4) > 0) {
					out.print(latlong.format(rs.getDouble(4)) + "&#176N");
				} else {
					out.print(latlong.format(Math.abs(rs.getDouble(4))) + "&#176S");
				}
				out.println("/");
				if (rs.getDouble(5) > 0) {
					out.print(latlong.format(rs.getDouble(5)) + "&#176E");
				} else {
					out.print(latlong.format(Math.abs(rs.getDouble(5))) + "&#176W");
				}
				if (rs.getString(6) != null) { out.print(" (&#177 " + rs.getString(6) + "m)"); }
				out.println("</td></tr>");
			}
			if (userID != 0) {
				if (rs.getString(7) != null) { out.println("<tr><td class='heading' width='135'>Method</td><td>" + rs.getString(7) + "</td></tr>"); }
				if (rs.getString(8) != null) { out.println("<tr><td class='heading' width='135'>Locality</td><td>" + rs.getString(8) + "</td></tr>"); }
				if (rs.getString(9) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Operating Company");
					} else {
						out.print("Section Collector");
					}
					out.println("</td><td>" + rs.getString(9) + "</td></tr>");
				}
				if (rs.getString(10) != null) {
					out.print("<tr><td class='heading'>");
					if (featType.equals("Drillhole")) {
						out.print("Spud Date");
					} else {
						out.print("Sampling Start Date");
					}
					out.print("</td><td>");
					if (rs.getString(11) == null) {
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(10)));
					} else if (rs.getString(11).equals("Year")) {
						out.print(yearFormatter.format(rs.getDate(10)));
					} else if (rs.getString(11).equals("Month")) {
						out.print(monthFormatter.format(rs.getDate(10)));
					}
					out.println("</td></tr>");
				}
				if (rs.getString(12) != null) {
					out.print("<tr><td class='heading'>	Completion Date</td><td>");
					if (rs.getString(13) == null) {
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(12)));
					} else if (rs.getString(13).equals("Year")) {
						out.print(yearFormatter.format(rs.getDate(12)));
					} else if (rs.getString(13).equals("Month")) {
						out.print(monthFormatter.format(rs.getDate(12)));
					}
					out.println("</td></tr>");
				}
				if (featType.equals("Drillhole") && rs.getString(14) != null) { out.println("<tr><td class='heading' width='135'>Licence Area</td><td>" + rs.getString(14) + "</td></tr>"); }
				if (rs.getString(15) != null) { out.println("<tr><td class='heading' width='135'>Datum Type</td><td>" + rs.getString(15) + "</td></tr>"); }
				if (rs.getString(16) != null) { out.println("<tr><td class='heading' width='135'>Datum Elevation</td><td>" + rs.getString(16) + " m asl</td></tr>"); }
				if (rs.getString(17) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Kick-off Depth");
					} else {
						out.print("Top Horizon");
					}
					out.println("</td><td>" + rs.getString(17) + " m</td></tr>");
				}
				if (rs.getString(18) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Termination Depth");
					} else {
						out.print("Base Horizon");
					}
					out.println("</td><td>" + rs.getString(18) + " m</td></tr>");
				}
			}
			out.println("</table></p>");

			if (userID != 0) {
				rs = statement.executeQuery("SELECT Sample_ID, Sample_Name, Drillhole_Depth FROM Sample_All_View WHERE Feature_ID = " + featID + " ORDER BY Top_Depth");
				out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
				out.println("<tr><th>Locality Name<img src='images/blank.gif' height='1' width='20' /></th><th colspan='2'>Sample Depth</th></tr>");
				while (rs.next()) {
					out.println("<tr><td class='heading'>" + rs.getString(2) + "<img src='images/blank.gif' height='1' width='20' /></td><td width='25'><img src='images/drill.gif' height='20' width='20' /></td><td><a href='detail.jsp?ID=" + rs.getString(1) + "' class='heading'>" + rs.getString(3) + "</a></td></tr>");
				}
			} else {
				out.println("<tr><td></td></tr><tr><td colspan='2'>More data is available for this locality for logged in users</td></tr>");
			}
			out.println("</table></p>");
		}

		// not allowed to view or not a drillhole
		else {
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='heading'>Error</span></p><p>You do not have rights to view this drillhole, or the drillhole does not exist</p>");
		}
	}

	//ID not specified
	else {
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");
		out.println("<p><span class='heading'>Error</span></p><p>Drillhole not specified</p>");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>