<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*"
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
	DBConnection connection = FREDUtils.getFREDConnection(state);
	Statement statement = connection.statement;
	ResultSet rs;
	User user = (User)getUser(session);
	String groupID;
	int userID = user.getPersonId(), execUp;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	if (request.getParameter("ID") != null) {
		groupID = request.getParameter("ID");

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		rs = statement.executeQuery("SELECT Group_Name FROM Taxa_Panel_View WHERE Group_ID = " + groupID);
		rs.next();
		out.println("<tr><td colspan='2' align='center' class='bigheading'>" + rs.getString(1) + "</td></tr>");
		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
		out.println("</table>");

		drawEndNavigation(out);

		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		//check user a panelist
		rs = statement.executeQuery("SELECT * FROM Taxa_Panel WHERE Group_ID = " + groupID + " AND Panelist_ID = " + userID);
		if (rs.next()) { //OK


			if (request.getParameter("ActionType") != null && request.getParameter("TaxaID") != null) { //do something
				String actionType = request.getParameter("ActionType");
				if (actionType.equals("Approve")) { //approve taxa
					execUp = statement.executeUpdate("UPDATE Taxonomic_Lookup SET Status = 'approved', Approved_By_ID = " + userID + ", Approved_Date = SYSDATE WHERE Taxa_ID = " + request.getParameter("TaxaID"));
				}
				else if (actionType.equals("Reject")) { //reject taxa
					execUp = statement.executeUpdate("UPDATE Taxonomic_Lookup SET Status = 'rejected', Approved_By_ID = " + userID + ", Approved_Date = SYSDATE WHERE Taxa_ID = " + request.getParameter("TaxaID"));
				}
				response.sendRedirect("taxa_group_detail.jsp?ID=" + groupID);
			}

			out.println("<p>Listed below are the Taxonomic names in the above group.  Any provisional entries are listed first and need to be either approved or rejected</p>");

			//List theasurus - provisional entries at top
			out.println("<table border='0' cellspacing='0' cellpadding='2' width='550'>");

			rs = statement.executeQuery("SELECT * FROM Taxonomic_Lookup WHERE Status = 'provisional' AND Group_ID = " + groupID);
			if (rs.next()) {
				out.println("<tr><th colspan='5'>Provisional Entries</th></tr>");
				out.println("<tr><th>Name<img src='blank.gif' width='10' height='1' /></th><th>Author<img src='blank.gif' width='10' height='1' /></th><th>Submitted By</th></tr>");
				rs = statement.executeQuery("SELECT TL.Taxa_ID, TL.Taxonomic_Name, TL.Author, FU.Full_Name FROM Taxonomic_Lookup TL, FR_User_View FU WHERE TL.Submitted_By_ID = FU.PE_ID AND TL.Group_ID = " + groupID + " AND TL.Status = 'provisional' ORDER BY Taxonomic_Name");
				while (rs.next()) {
					out.println("<tr><td class='heading'>" + rs.getString(2) + "<img src='images/blank.gif' width='20' height='1' /></td><td>" +FREDUtils.noNulls(rs.getString(3)) + "<img src='images/blank.gif' width='20' height='1' /></td><td>" + rs.getString(4) + "<img src='images/blank.gif' width='20' height='1' /></td><td><a href='taxa_group_detail.jsp?ID=" + groupID + "&ActionType=Approve&TaxaID=" + rs.getString(1) + "' title='Approve'><img src='images/ok.gif' border='0' height='20' width='20' /></td><td><a href='taxa_group_detail.jsp?ID=" + groupID + "&ActionType=Reject&TaxaID=" + rs.getString(1) + "' title='Reject'><img src='images/cancel.gif' border='0' height='20' width='20' /></a></td></tr>");
				}
			}

			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");

			out.println("<tr><th colspan='5'>Approved Entries</th></tr>");
			out.println("<tr><th>Name<img src='blank.gif' width='10' height='1' /></th><th>Author<img src='blank.gif' width='10' height='1' /></th><th>Approved By</th></tr>");
			rs = statement.executeQuery("SELECT TL.Taxonomic_Name, TL.Author, FU.Full_Name FROM Taxonomic_Lookup TL, FR_User_View FU WHERE TL.Approved_By_ID = FU.PE_ID(+) AND TL.Group_ID = " + groupID + " AND TL.Status = 'approved' ORDER BY Taxonomic_Name");
			while (rs.next()) {
				out.println("<tr><td class='heading'>" + rs.getString(1) + "<img src='images/blank.gif' width='20' height='1' /></td><td>" +FREDUtils.noNulls(rs.getString(2)) + "<img src='images/blank.gif' width='20' height='1' /></td><td>" +FREDUtils.noNulls(rs.getString(3)) + "</td></tr>");
			}

			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");

			out.println("<tr><th colspan='5'>Obsolete Entries</th></tr>");
			out.println("<tr><th>Name<img src='blank.gif' width='10' height='1' /></th><th>Author</th></tr>");
			rs = statement.executeQuery("SELECT Taxonomic_Name, Author FROM Taxonomic_Lookup WHERE Group_ID = " + groupID + " AND Status = 'obsolete' ORDER BY Taxonomic_Name");
			while (rs.next()) {
				out.println("<tr><td class='heading'>" + rs.getString(1) + "<img src='images/blank.gif' width='20' height='1' /></td><td>" +FREDUtils.noNulls(rs.getString(2)) + "</td></tr>");
			}

			out.println("</table>");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);

%>


