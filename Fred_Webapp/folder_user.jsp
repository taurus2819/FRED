<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, nz.cri.gns.auth.*"
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
	int userID = user.getPersonId(), execUp, i, userRightValue[], maxRights, rightCode = 0;
	String foldType, userRight[], foldID;
	userRightValue = new int[10];
	userRight = new String[10];
	ComboDescriptor cd;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
	out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
	out.println("</table>");

	drawEndNavigation(out);

	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");
	
	if (request.getParameter("FoldID") != null) {
		foldID = request.getParameter("FoldID");
		rs =statement.executeQuery("SELECT Folder_Name, Folder_Owner, User_Rights, Folder_Type FROM Folder_View WHERE User_ID = " + userID + " AND Folder_ID = " + foldID);
		if (rs.next() && (rs.getInt(3) & 32) == 32) { //to get past this if statement user must either be the owner of the folder or have admin rights
			out.println("<p><span class='bigheading'>Folder: " + rs.getString(1) + "</span><br>");
			out.println("<span class='heading'>Owner: " + rs.getString(2) + "</span></p>");
						foldType = rs.getString(4);

			//build array of rights
			if (foldType.equals("personal")) {
				rs = statement.executeQuery("SELECT Name, Code FROM Lookup WHERE FieldName = 'FolderRight' AND Code NOT IN ('1', '64') ORDER BY Lookup_ID");
			} else {
				rs = statement.executeQuery("SELECT Name, Code FROM Lookup WHERE FieldName = 'FolderRight' AND Code IN ('32', '64') ORDER BY Lookup_ID");
			}
			i = -1;
			while (rs.next()) {
				userRight[++i] = rs.getString(1);
				userRightValue[i] = rs.getInt(2);
			}
			maxRights = ++i;

			//process any changes
			if (request.getParameter("ActionType") != null) {
				String actionType = request.getParameter("ActionType");
				if (actionType.equals("AddUser")) {
					execUp = statement.executeUpdate("INSERT INTO Folder_User (Folder_ID, User_ID, User_Rights) VALUES (" + foldID + ", " + request.getParameter("UserID") + ", 1)");
				}
				if (actionType.equals("DeleteUser")) {
					execUp = statement.executeUpdate("DELETE FROM Folder_User WHERE User_ID = " + request.getParameter("UserID") + " AND Folder_ID = " + foldID);
				}
				else if (actionType.equals("ChangeRight")) {
					execUp = statement.executeUpdate("UPDATE Folder_User SET User_Rights = User_Rights + " + request.getParameter("Right") + " WHERE User_ID = " + request.getParameter("UserID") + " AND Folder_ID = " + foldID);
				}
				response.sendRedirect("folder_user.jsp?FoldID=" + foldID);
			}

			out.println("<p>The users listed below have rights to this folder.<br>Users can be added or deleted from this list and their rights altered by clicking on the <img src='images/ok.gif' width='20' height='20' border='0' /> or <img src='images/cancel.gif' width='20' height='20' border='0' /> icons.</p>");

			out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
			out.print("<tr class='heading' align='center'><td align='left'>User&nbsp&nbsp</td><td width='60'>Read</td>");
			for (int x = 0; x < maxRights; x++) { out.print("<td width='60'>" + userRight[x] + "</td>"); }
			out.println("<tr><td><img src='images/blank.gif width='1' height='5' /></td></tr>");

			out.println("<form name='UserForm' method='post' action='folder_user.jsp'>");
			out.println("<input type='hidden' name='FoldID' value='" + foldID + "'>");
			out.println("<input type='hidden' name='ActionType' value='AddUser'>");
			out.print("<tr><td>");
			cd = new ComboDescriptor("FR_User_View", "PE_ID", "Full_Name");
			cd.name = "UserID";
			cd.orderBy = "Family_Name";
			cd.join = "NOT PE_ID IN (SELECT User_ID FROM Folder_View WHERE Folder_ID = " + foldID + ")";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("&nbsp&nbsp</td><td align='center'><a href='#' onClick='UserForm.submit();' title='Add User'><img src='images/cancel.gif' width='20' height='20' border='0' /></a></td></tr>");
			out.println("</form>");

			rs = statement.executeQuery("SELECT User_ID, Folder_User, User_Rights FROM Folder_View WHERE User_ID <> Owner_ID AND Folder_ID = " + foldID);
			i = 0;
			while (rs.next()) {
				out.print("<tr><td>" + rs.getString(2) + "&nbsp&nbsp</td><td align='center'><a href='folder_user.jsp?FoldID=" + foldID + "&ActionType=DeleteUser&UserID=" + rs.getString(1) + "' title='Delete User'><img src='images/ok.gif' width='20' height='20' border='0' /></a></td>");
				for (int x = 0; x < maxRights; x++) {
					out.print("<td align='center'><a href='folder_user.jsp?FoldID=" + foldID + "&ActionType=ChangeRight&UserID=" + rs.getString(1) + "&Right=");
					if ((rs.getInt(3) & userRightValue[x]) != 0) {
						out.print((userRightValue[x] * -1) + "' title='Remove Right'><img src='images/ok.gif'");
					} else {
						out.print(userRightValue[x] + "' title='Add Right'><img src='images/cancel.gif'");
					}
					out.print(" width='20' height='20' border='0' /></a></td>");
				}
				out.println("</tr>");
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
