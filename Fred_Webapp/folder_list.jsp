<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, java.util.*, nz.cri.gns.auth.*"
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	if (request.getParameter("ActionType") != null) { //do something
		String actionType = request.getParameter("ActionType");
		if (actionType.equals("Add")) { //add folder
			FolderUtils.addFolder(request.getParameter("FoldName"), user, state);
		}
		else if (actionType.equals("Delete")) { //Delete folder
			try {
				FolderUtils.deleteFolder(request.getParameter("FoldID"), user, state);
			} catch (Exception e) {}
		}
		response.sendRedirect("folder_list.jsp");
		return;
	}

	drawTop(out, et, request, response);

	out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
	out.println("<tr><td colspan='2' class='bigheading' align='center'>" + user.getFullName() + "'s<br />Folders</td></tr>");
	out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
	out.println("<form name='NewFoldForm' method='post' action='folder_list.jsp'>");
	out.println("<tr><td><a href='#' onClick='document.NewFoldForm.FoldName.value=prompt(\"Please enter the folder name\", \"New Working Folder\");document.NewFoldForm.submit();' title='Add New Folder'><img src='images/folder.gif' width='20' height='20' border='0' />&nbsp;</a></td><td><a href='#' onClick='document.NewFoldForm.FoldName.value=prompt(\"Please enter the folder name\", \"New Folder\");document.NewFoldForm.submit();' class='heading'>New Folder</a></td></tr>");
	out.println("<input type='hidden' name='ActionType' value='Add'>");
	out.println("<input type='hidden' name='FoldName' value=''>");
	out.println("</form>");
	out.println("</table>");

	drawEndNavigation(out);
	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");

	out.println("<p>All data entry is done within a folder.<br />Your current folders are listed below and you can create more folders by clicking on the link on the left.</p>");

	out.println("<table border='0' cellspacing='0' cellpadding='2' width='550'>");

	FolderList folderList = new FolderList(user, state);
	Vector folders = new Vector();

	//List Working folders
	if (folderList.getPersonalFolderCount() > 0) {
		out.println("<tr><th>Working Folder&nbsp;&nbsp;</th><th>Owner&nbsp;&nbsp;</th><th>Options</th></tr>");
		out.println("<tr><td><img src='images/blank.gif' height='5' width='1' /></td></tr>");
		out.println("<form name='PersForm' method='post' action='folder_list.jsp'>");
		for (Iterator i = folderList.getPersonalFolders().iterator(); i.hasNext(); ) {
			KeyValueObject kv = (KeyValueObject) i.next();
			Folder folder = new Folder(Integer.parseInt(kv.getKey()), user, state);
			folders.add(folder);
			out.print("<tr><td><a href='folder_detail.jsp?ID=" + folder.getAsString(Folder.FOLDER_ID) + "' class='heading'>" + folder.getAsString(Folder.NAME) + "</a>&nbsp;&nbsp;</td><td>" + folder.getAsString(Folder.OWNER) + "&nbsp;&nbsp;</td><td>");
			if (folder.isAllowedAdmin())
				out.print("<a href='folder_user.jsp?FoldID=" + folder.getAsString(Folder.FOLDER_ID) + "' title='Edit Users'><img src='images/prefs.gif' border='0' height='20' width='20' /></a>&nbsp;&nbsp;&nbsp;<a href='#' onClick='if (confirm(\"Are you sure you want to delete this folder\") == true) {document.PersForm.FoldID.value=\"" + folder.getAsString(Folder.FOLDER_ID) + "\";document.PersForm.submit();}' title='Delete Folder'><img src='images/delete.gif' border='0' height='20' width='20' /></a>");
			out.println("<img src='images/blank.gif' width='1' height='20' /></td></tr>");
		}
		out.println("<input type='hidden' name='ActionType' value='Delete'>");
		out.println("<input type='hidden' name='FoldID' value=''>");
		out.println("</form>");
		out.println("<tr><td>&nbsp;</td></tr>");
	}


	//List Masterfile folders (if any)
	if (folderList.getAdminFolderCount() > 0) {
		out.println("<tr><th>Masterfile Folder&nbsp;&nbsp;</th><th></th><th>Options</th></tr>");
		out.println("<tr><td><img src='images/blank.gif' height='5' width='1' /></td></tr>");
		for (Iterator i = folderList.getAdminFolders().iterator(); i.hasNext(); ) {
			KeyValueObject kv = (KeyValueObject) i.next();
			Folder folder = new Folder(Integer.parseInt(kv.getKey()), user, state);
			folders.add(folder);
			out.print("<tr><td><a href='admin_folder_detail.jsp?ID=" + folder.getAsString(Folder.FOLDER_ID) + "' class='heading'>" + folder.getAsString(Folder.NAME) + "</a>&nbsp;&nbsp;</td><td style='font-size: 10pt; font-weight: bold; color: #FF0000'>");
			if (folder.getLocalityCount() > 0)
				out.print("new data");
			out.print("&nbsp;</td><td>");
			if (folder.isAllowedAdmin())
				out.print("<a href='folder_user.jsp?FoldID=" + folder.getAsString(Folder.FOLDER_ID) + "' title='Edit Users'><img src='images/prefs.gif' border='0' height='20' width='20' /></a>");
			out.println("<img src='images/blank.gif' width='1' height='20' /></td></tr>");
		}
		out.println("<tr><td>&nbsp;</td></tr>");
	}

	session.setAttribute("folders", folders);

	TaxaPanelList panelList = new TaxaPanelList(user, state);
	Vector panels = new Vector();

	//List Taxonomic groups (if any)
	if (panelList.getPanelCount() > 0) {
		out.println("<tr><th>Taxonomic Groups<img src='images/blank.gif' width='20' height='1' /></th><td></td></th><th>Options</th></tr>");
		out.println("<tr><td><img src='images/blank.gif' height='5' width='1' /></td></tr>");
		for (Iterator i = panelList.getPanels().iterator(); i.hasNext(); ) {
			KeyValueObject kv = (KeyValueObject) i.next();
			TaxaPanel panel = new TaxaPanel(Integer.parseInt(kv.getKey()), user, state);
			panels.add(panel);
			out.print("<tr><td><a href='taxa_group_detail.jsp?ID=" + panel.getPanelID() + "' class='heading'>" + panel.getAsString(TaxaPanel.NAME) + "</a><img src='images/blank.gif' width='20' height='1' /></td><td style='font-size: 10pt; font-weight: bold; color: #FF0000'>");
			if (panel.getProvisionalCount() > 0)
				out.print("new data");
			out.print("<img src='images/blank.gif' width='10' height='1' /></td><td><a href='taxa_panelist.jsp?GroupID=" + panel.getPanelID() + "' title='Edit Users'><img src='images/prefs.gif' border='0' height='20' width='20' /></a></td></tr>");
		}
	}
	session.setAttribute("panels", panels);


	out.println("</table>");

	out.println("</td></tr></table>");
	drawBottom(out, et);

%>


