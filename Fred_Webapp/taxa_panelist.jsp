<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, nz.cri.gns.auth.*"
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
	User user = getUser(session);
	int userID = user.getPersonId(), execUp;
	String groupID;
	ComboDescriptor cd;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
	out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
	out.println("</table>");

	drawEndNavigation(out);

	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");
	
	if (request.getParameter("GroupID") != null) {
		groupID = request.getParameter("GroupID");
		rs =statement.executeQuery("SELECT Group_Name FROM Taxa_Panel_View WHERE Panelist_ID = " + userID + " AND Group_ID = " + groupID);
		if (rs.next()) { //to get past this if statement user must be a panelist
			out.println("<p><span class='bigheading'>" + rs.getString(1) + "</span></p>");

			//process any changes
			if (request.getParameter("ActionType") != null) {
				String actionType = request.getParameter("ActionType");
				if (actionType.equals("Add")) {
					execUp = statement.executeUpdate("INSERT INTO Taxa_Panel (Group_ID, Panelist_ID) VALUES (" + groupID + ", " + request.getParameter("UserID") + ")");
					response.sendRedirect("taxa_panelist.jsp?GroupID=" + groupID);
				}
				else if (actionType.equals("Delete")) {
					execUp = statement.executeUpdate("DELETE FROM Taxa_Panel WHERE Group_ID = " + groupID + " AND Panelist_ID = " + request.getParameter("UserID"));
					response.sendRedirect("taxa_panelist.jsp?GroupID=" + groupID);
				}
			}

			out.println("<p>The users listed below are on the panel for this taxanomic group and may accept or reject new entries to the theasurus.<br>Users can be added or deleted from this list by clicking on the <img src='images/ok.gif' width='20' height='20' border='0' /> or <img src='images/cancel.gif' width='20' height='20' border='0' /> icons.</p>");

			out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
			out.println("<tr class='heading'><td>User&nbsp&nbsp</td><td width='60' align='center'>Member</td></tr>");
			out.println("<tr><td><img src='images/blank.gif width='1' height='5' /></td></tr>");
			out.println("<form name='AddForm' method='post' action='taxa_panelist.jsp'>");
			out.println("<input type='hidden' name='GroupID' value='" + groupID + "'>");
			out.println("<input type='hidden' name='ActionType' value='Add'>");
			out.print("<tr><td>");
			cd = new ComboDescriptor("FR_User_View", "PE_ID", "Full_Name");
			cd.name = "UserID";
			cd.orderBy = "Family_Name";
			cd.join = "NOT PE_ID IN (SELECT Panelist_ID FROM Taxa_Panel WHERE Group_ID = " + groupID + ")";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("<img src='images/blank.gif' width='20' height='1' /></td><td align='center'><a href='#' onClick='AddForm.submit();' title='Add User'><img src='images/cancel.gif' border='0' height='20' width='20' /></a></td></tr>");
			out.println("</form>");

			rs = statement.executeQuery("SELECT Panelist_ID, Panelist_Name FROM Taxa_Panel_View WHERE Group_ID = " + groupID);
			while (rs.next()) {
				out.print("<tr><td>" + rs.getString(2) + "<img src='images/blank.gif' width='20' height='1' /></td><td align='center'><a href='taxa_panelist.jsp?GroupID=" + groupID + "&ActionType=Delete&UserID=" + rs.getString(1) + "' title='Delete User'><img src='images/ok.gif' border='0' height='20' width='20' /></a></td></tr>");
			}

			out.println("</table></p>");
		}
		else { //no rights
			out.println("<p><span class='subhead'>Access denied</span></p>Either there is no folder matching the ID you entered or you have insufficient rights to edit the folder.  Click <a href='index.jsp' class='fname'>here</a> to return to the FRED home page.");
		}
	}

	out.println("</table>");

	out.println("</td></tr></table>");
	drawBottom(out, et);

%>
