<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*"
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
	ResultSet rs, rs2;
	User user = getUser(session);
	String numRecords, foldID;
	int userID = user.getPersonId(), i, execUp, recCount;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	rs = statement.executeQuery("SELECT Full_Name FROM FR_User_View WHERE PE_ID = " + userID);
	if (rs.next()) {

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' class='bigheading' align='center'>" + rs.getString(1) + "'s<br />Folders</td></tr>");
		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("<form name='NewFoldForm' method='post' action='folder_list.jsp'>");
		out.println("<tr><td><a href='#' onClick='document.NewFoldForm.FoldName.value=prompt(\"Please enter the folder name\", \"New Working Folder\");document.NewFoldForm.submit();' title='Add New Folder'><img src='images/folder.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='#' onClick='document.NewFoldForm.FoldName.value=prompt(\"Please enter the folder name\", \"New Folder\");document.NewFoldForm.submit();' class='heading'>New Folder</a></td></tr>");
		out.println("<input type='hidden' name='ActionType' value='Add'>");
		out.println("<input type='hidden' name='FoldName' value=''>");
		out.println("</form>");
		out.println("</table>");

		drawEndNavigation(out);

		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		if (request.getParameter("ActionType") != null) { //do something
			String actionType = request.getParameter("ActionType");
			if (actionType.equals("Add")) { //add folder
				String folderName = request.getParameter("FoldName");
				if (folderName.length() > 32) { folderName = folderName.substring(0, 31); }
				execUp = statement.executeUpdate("INSERT INTO Folder (Name, Owner_ID, Folder_Type) VALUES (" + JspUtils.sqlEscape(folderName) + ", " + userID + ", 'personal')");
			}

			if (actionType.equals("Delete")) { //Delete folder
				foldID = request.getParameter("FoldID");
				//check that folder is empty
				rs = statement.executeQuery("SELECT Folder_Name FROM Folder_Content_View WHERE Feature_ID IS NOT NULL AND Folder_ID = " + foldID);
				if (!rs.next()) {
					execUp = statement.executeUpdate("DELETE FROM Folder WHERE Folder_ID = " + foldID);
				}
			}
			response.sendRedirect("folder_list.jsp");
		}

		out.println("<table border='0' cellspacing='0' cellpadding='2' width='550'>");

		//List Working folders
		rs = statement.executeQuery("SELECT * FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type = 'personal'");
		if (rs.next()) {
			i = 0;
			rs = statement.executeQuery("SELECT DISTINCT Folder_ID, Folder_Name, Owner_ID, Folder_Owner, User_Rights FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type = 'personal' ORDER BY Folder_Name");
			out.println("<tr><th>Working Folder<img src='images/blank.gif' width='20' height='1' /></th><td></td><th>Owner<img src='images/blank.gif' width='20' height='1' /></th><th>Options</th></tr>");
			out.println("<tr><td><img src='images/blank.gif' height='5' width='1' /></td></tr>");
			out.println("<form name='PersForm' method='post' action='folder_list.jsp'>");
			while (rs.next()) {
				out.print("<tr><td><a href='folder_detail.jsp?ID=" + rs.getString(1) + "' class='heading'>" + rs.getString(2) + "</a><img src='images/blank.gif' width='20' height='1' /></td><td></td><td>" + rs.getString(4) + "<img src='images/blank.gif' width='20' height='1' /></td><td>");
				if (rs.getInt(3) == userID || (rs.getInt(5) & 32) != 0) { //if owner or admin rights
					out.print("<a href='folder_user.jsp?FoldID=" + rs.getString(1) + "' title='Edit Users'><img src='images/prefs.gif' border='0' height='20' width='20' /></a>");
					rs2 = statement2.executeQuery("SELECT Folder_Name FROM Folder_Content_View WHERE Feature_ID IS NOT NULL AND Folder_ID = " + rs.getString(1));
					if (!rs2.next()) { //folder is empty and can be deleted
						out.print("<img src='images/blank.gif' width='20' height='1' /><a href='#' onClick='if (confirm(\"Are you sure you want to delete this folder\") == true) {document.PersForm.FoldID.value=\"" + rs.getString(1) + "\";document.PersForm.submit();}' title='Delete Folder'><img src='images/delete.gif' border='0' height='20' width='20' /></a>");
					}
				} else {
					out.print("<img src='images/blank.gif' width='20' height='20' />");
				}
				out.println("</td></tr>");
			}
			out.println("<input type='hidden' name='ActionType' value='Delete'>");
			out.println("<input type='hidden' name='FoldID' value=''>");
			out.println("<tr><td><img src='images/blank.gif' width='10' height='30' /></td></tr></form>");
		}

		//List Masterfile folders (if any)
		rs = statement.executeQuery("SELECT * FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type = 'admin'");
		if (rs.next()) {
			i = 0;
			rs = statement.executeQuery("SELECT DISTINCT Folder_ID, Folder_Name, Owner_ID, Folder_Owner, User_Rights FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type = 'admin' ORDER BY Folder_Name");
			out.println("<tr><th>Masterfile Folder<img src='images/blank.gif' width='20' height='1' /></th><td></td><th>Curator<img src='images/blank.gif' width='20' height='1' /></th><th>Options</th></tr>");
			out.println("<tr><td><img src='images/blank.gif' height='5' width='1' /></td></tr>");
			while (rs.next()) {
				out.print("<tr><td><a href='admin_folder_detail.jsp?ID=" + rs.getString(1) + "' class='heading'>" + rs.getString(2) + "</a><img src='images/blank.gif' width='20' height='1' /></td><td style='font-size: 14pt; font-weight: bold; color: #FF0000'>");
				rs2 = statement2.executeQuery("SELECT * FROM Masterfile_Content_View WHERE Folder_ID = " + rs.getString(1));
				if (rs2.next()) { out.print("*"); }
				out.print("<img src='images/blank.gif' width='10' height='1' /></td><td>" + rs.getString(4) + "<img src='images/blank.gif' width='20' height='1' /></td><td>");
				if (rs.getInt(3) == userID || (rs.getInt(5) & 32) != 0) { //if owner or admin rights
					out.println("<a href='folder_user.jsp?FoldID=" + rs.getString(1) + "' title='Edit Users'><img src='images/prefs.gif' border='0' height='20' width='20' /></a>");
				} else {
					out.println("<img src='images/blank.gif' width='10' height='20' /></td></tr>");
				}
			}
			out.println("<tr><td><img src='images/blank.gif' width='10' height='30' /></td></tr>");
		}

		//List Taxonomic groups (if any)
		rs = statement.executeQuery("SELECT * FROM Taxa_Panel_View WHERE Panelist_ID = " + userID);
		if (rs.next()) {
			i = 0;
			rs = statement.executeQuery("SELECT DISTINCT Group_ID, Group_Name FROM Taxa_Panel_View WHERE Panelist_ID = " + userID + " ORDER BY Group_Name");
			out.println("<tr><th>Taxonomic Groups<img src='images/blank.gif' width='20' height='1' /></th><td></td><td></td></th><th>Options</th></tr>");
			out.println("<tr><td><img src='images/blank.gif' height='5' width='1' /></td></tr>");
			while (rs.next()) {
				out.print("<tr><td><a href='taxa_group_detail.jsp?ID=" + rs.getString(1) + "' class='heading'>" + rs.getString(2) + "</a><img src='images/blank.gif' width='20' height='1' /></td><td style='font-size: 14pt; font-weight: bold; color: #FF0000'>");
				rs2 = statement2.executeQuery("SELECT * FROM Taxonomic_Lookup WHERE Status = 'provisional' AND Group_ID = " + rs.getString(1));
				if (rs2.next()) { out.print("*"); }
				out.print("<img src='images/blank.gif' width='10' height='1' /></td><td></td><td><a href='taxa_panelist.jsp?GroupID=" + rs.getString(1) + "' title='Edit Users'><img src='images/prefs.gif' border='0' height='20' width='20' /></a></td></tr>");
			}
		}

		out.println("</table>");
	}
	else {
		drawEndNavigation(out);
		out.println("<p><span class='heading'>Access Denied</span></p>");
		out.println("<p>You are not recognised as a FRED database user.</p>");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);

	statement2.close();
%>


