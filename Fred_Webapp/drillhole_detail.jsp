<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.fred.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	nz.cri.gns.intranet.DBConnection frConn = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	nz.cri.gns.intranet.DBConnection connection;
	User user = getUser(session);
	ResultSet rs;
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");
	SimpleDateFormat yearFormatter = new SimpleDateFormat ("yyyy");
	SimpleDateFormat monthFormatter = new SimpleDateFormat ("MMM yyyy");
	String featType, featID, palID, status = "", query;
	int[] types = {Types.NUMERIC};
	Object data[];
	data = new Object[1];
	int[] doubleTypes = {Types.NUMERIC, Types.NUMERIC};
	Object doubleData[];
	doubleData = new Object[2];

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	if (request.getParameter("ID") != null) {
		featID = request.getParameter("ID");
		session.setAttribute("FeatureID", featID);
	} else {
		featID = (String) session.getAttribute("FeatureID");
	}

	drawTop(out, et, request, response);

	if (featID != null) {

		//create connection:  userConnection if logged in, otherwise FR
		if (user !=  null) {
			connection = user.getUsersConnection(new PageState(request, response, application), frConn);
		} else {
			connection = frConn;
		}

		Feature feature = Feature.getFeature(Integer.parseInt(featID), user, state);
		Audit audit = Audit.getAudit(feature.getAsInt(Feature.AUDIT_ID), state);
		featType = feature.getAsString(Feature.FEATURE_TYPE);

		if (!featType.equals("Outcrop")) {
/*			out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
			query = "SELECT S.Feature_Name, S.Feature_Type, S.Masterfile_Name, S.Status, A.Created_By, A.Created_Date, A.Modified_By, A.Modified_Date, A.Submitted_By, A.Submitted_Date, A.Approved_By, A.Approved_Date FROM FR.Sample_View S, FR.Audit_View A WHERE S.Audit_ID = A.Audit_ID AND S.Feature_ID = ?";
			data[0] = new Integer(Integer.parseInt(featID));
			rs = frConn.executeQuery(query, types, data);
			rs.next();
			featType = rs.getString(2);
*/			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='bigheading' >" + feature.getAsString(Feature.FEATURE_NAME) + "</td></tr>");
			out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
			if (fullSample.get(fullSample.MASTERFILE_NAME) != null) {
				out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + fullSample.getAsString(FullSample.MASTERFILE_NAME) + "</td></tr>");
			}
			if (!audit.getAsString(Audit.STATUS).equals("approved")) {
				out.println("<tr><td class='smallheading'>Status:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + audit.getAsString(Audit.STATUS) + "</td></tr>");
			}
			if (audit.get(Audit.CREATED_BY) != null || audit.get(Audit.CREATED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Created:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.CREATED_BY) != null) { out.print(audit.getAsString(Audit.CREATED_BY) + "<br />"); }
				if (audit.get(Audit.CREATED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.CREATED_DATE))); }
				out.println("</td></tr>");
			}
			if (audit.get(Audit.MODIFIED_BY) != null || audit.get(Audit.MODIFIED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Edited:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.MODIFIED_BY) != null) { out.print(audit.getAsString(Audit.MODIFIED_BY) + "<br />"); }
				if (audit.get(Audit.MODIFIED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.MODIFIED_DATE))); }
				out.println("</td></tr>");
			}
			if (audit.get(Audit.SUBMITTED_BY) != null || audit.get(Audit.SUBMITTED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Submitted:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.SUBMITTED_BY) != null) { out.print(audit.getAsString(Audit.SUBMITTED_BY) + "<br />"); }
				if (audit.get(Audit.SUBMITTED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.SUBMITTED_DATE))); }
				out.println("</td></tr>");
			}
			if (audit.get(Audit.APPROVED_BY) != null || audit.get(Audit.APPROVED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Approved:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.APPROVED_BY) != null) { out.print(audit.getAsString(Audit.APPROVED_BY) + "<br />"); }
				if (audit.get(Audit.APPROVED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.APPROVED_DATE))); }
				out.println("</td></tr>");
			}
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			query = "SELECT NZMG_Sheet, NZMG_East, NZMG_North, Latitude, Longitude, Accuracy, Method, Locality, Person, Start_Date, Start_Date_Rounding, Finish_Date, Finish_Date_Rounding, Drillhole_Licence_Name, Datum_Type, Datum_Elevation, Start_Depth, Finish_Depth FROM FR.Sample_View WHERE Feature_ID = ?";
			data[0] = new Integer(Integer.parseInt(featID));
			rs = connection.executeQuery(query, types, data);
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
			out.println("</table></p>");

			if (user != null) {
				query = "SELECT Sample_ID, Sample_Name, Drillhole_Depth FROM FR.Sample_View WHERE Feature_ID = ? ORDER BY Top_Depth";
				data[0] = new Integer(Integer.parseInt(featID));
				rs = connection.executeQuery(query, types, data);
				out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
				out.println("<tr><th>Locality Name<img src='images/blank.gif' height='1' width='20' /></th><th colspan='2'>Sample Depth</th></tr>");
				while (rs.next()) {
					out.println("<tr><td class='heading'>" + rs.getString(2) + "<img src='images/blank.gif' height='1' width='20' /></td><td width='25'><img src='images/drill.gif' height='20' width='20' /></td><td><a href='detail.jsp?ID=" + rs.getString(1) + "' class='heading'>" + rs.getString(3) + "</a></td></tr>");
				}
			} else {
				out.println("<tr><td></td></tr><tr><td colspan='2'>More data is available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/drillhole_detail.jsp") + "' class='boldlink'>logged</a> in users</td></tr>");
			}
			out.println("</table></p>");
		}

		// not allowed to view or not a drillhole
		else {
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p>Either there is no record matching the ID you entered or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
		}
	}

	//ID not specified
	else {
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");
		out.println("<p>Either there is no record matching the ID you entered or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>