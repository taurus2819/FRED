<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%
	User user =(User) getUser(session);
	PageState state = new PageState(request, response, getServletContext());

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	if (request.getParameter("ID") != null) {
		Folder folder = new Folder(Integer.parseInt(request.getParameter("ID")), user, state, true);
		String redirect = URLEncoder.encode("folder_detail.jsp?ID=" + folder.getFolderID(), "UTF-8");
						
		if (request.getParameter("ActionType") != null) { //do something
			String actionType = request.getParameter("ActionType");
			String err = "";
			try {
				//Copy locality
				if (actionType.equals("CopyFeat") && folder.isAllowedCreateLocalities()) {
					FolderUtils.copyLocality(request.getParameter("FeatID"), request.getParameter("NewFeatName"), String.valueOf(folder.getFolderID()), user, state);
				}
				 //Delete feature
				else if (actionType.equals("DeleteFeat") && folder.isAllowedDeleteLocalities()) {
					FolderUtils.deleteLocality(request.getParameter("FeatID"), user, state);
				}
				// submit working locality
				else if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
					FolderUtils.submitLocality(request.getParameter("FeatID"), user, state);
				}
				//Revoke waiting records
				else if (actionType.equals("Revoke") && folder.isAllowedSubmitLocalities()) {
					FolderUtils.revokeLocality(request.getParameter("FeatID"), user, state);
				}
			} catch (Exception e) {
				err = "&ErrMsg=" + URLEncoder.encode("An Error has occured", "UTF-8");
			}
			response.sendRedirect("folder_detail.jsp?ID=" + folder.getFolderID() + err);
			return;
			//folder = new Folder(Integer.parseInt(request.getParameter("ID")), user, state, true);
		}

		drawTop(out, et, request, response);

		if (folder.isAllowedReadLocalities()) {
			
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/folder.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' class='bigheading' align='center'>" + folder.getAsString(Folder.NAME) + "</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
			if (folder.isAllowedCreateLocalities()) {
				out.println("<tr><td><a href='data_entry.jsp?Type=Outcrop&FoldID=" + folder.getFolderID() + "&Redirect=" + redirect + "'><img src='images/new.gif' width='20' height='20' border='0' alt='Add New Locality' /></a>&nbsp;&nbsp;</td><td><a href='data_entry.jsp?Type=Outcrop&FoldID=" + folder.getFolderID() + "&Redirect=" + redirect + "' class='heading'>New Outcrop Locality</a></td></tr>");
				out.println("<tr><td><a href='data_entry.jsp?Type=Drillhole&FoldID=" + folder.getFolderID() + "&Redirect=" + redirect + "'><img src='images/new.gif' width='20' height='20' border='0' alt='Add New Locality' /></a>&nbsp;&nbsp;</td><td><a href='data_entry.jsp?Type=Drillhole&FoldID=" + folder.getFolderID() + "&Redirect=" + redirect + "' class='heading'>New Drillhole Locality</a></td></tr>");
				out.println("<tr><td><a href='data_entry.jsp?Type=Vertical Section&FoldID=" + folder.getFolderID() + "&Redirect=" + redirect + "'><img src='images/new.gif' width='20' height='20' border='0' alt='Add New Locality' /></a>&nbsp;&nbsp;</td><td><a href='data_entry.jsp?Type=Vertical Section&FoldID=" + folder.getFolderID() + "&Redirect=" + redirect + "' class='heading'>New Vertical Section Locality</a></td></tr>");
				out.println("<tr><td><a href='simple_query.jsp?FoldID=" + folder.getFolderID() + "'><img src='images/search.gif' width='20' height='20' border='0' alt='Search for a Locality' /></a>&nbsp;&nbsp;</td><td><a href='simple_query.jsp?FoldID=" + folder.getFolderID() + "' class='heading'>Search</a></td></tr>");
			}
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			//List records
			out.println("<p>Listed below are the localities you have added to this folder (working localities are named with their field number or drillhole name until they are allocated a Fossil Record Number).<br />");
			out.println("Click on the locality to add/edit locality records</p>");

			//print error message (if any) from folder_actions
			if (request.getParameter("ErrMsg") != null) {
				out.println("<p><span class='heading' style='color: #FF0000'>" + request.getParameter("ErrMsg") + "</span></p>");
			}

			//Table header
			out.println("<p><table border='0' cellspacing='0' cellpadding='1' width='550'>");
			out.print("<tr>");
			//out.print("<td></td>");
			out.print("<th colspan='2'>Name&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Last Change&nbsp;&nbsp;</th><th colspan='5'>Options</th></tr>");
			out.println("<tr><td colspan='9'><img src='images/line.gif' height='3' width='550' /></td></tr>");

			out.println("<form name='FoldForm' method='put' action='folder_detail.jsp'>");

			Feature feature;
			for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
				feature = new Feature(((Integer) i.next()).intValue(), user, state, true);

				String featID  = feature.getAsString(Feature.FEATURE_ID);
				String sampName = feature.getAsString(Feature.SAMPLE_NAMES);
				String featType = feature.getAsString(Feature.FEATURE_TYPE);
				String featName = feature.getAsString(Feature.FEATURE_NAME);
				String locStatus = feature.getAsString(Feature.STATUS);
				
				out.print("<tr><td><a href='detail.jsp?FeatID=" + featID + "'><img src='images/loc.gif' border='0' height='20' width='20' alt='View Locality' /></a></td>");
				out.println("<td class='heading'><a href='folder_feature_detail.jsp?FoldID=" + folder.getFolderID() + "&FeatID=" + featID + "'>" + sampName + "</a>&nbsp;&nbsp;");
				if (featName != null && !sampName.equals(featName)) { out.print("<br />(" + featName +")&nbsp;&nbsp;"); }
				out.print("</td><td>" + featType + "&nbsp;&nbsp;</td><td style='color: #FF0000'>");
				if (!locStatus.equals("approved")) {
					out.print(locStatus + "&nbsp;&nbsp;</td><td>");
					if (feature.get(Feature.LAST_CHANGE) != null) { 
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(feature.getAsDate(Feature.LAST_CHANGE)) + "&nbsp;&nbsp;");
					}
					out.print("</td>");
				} else {
					out.print("</td><td></td>");
				}
				out.print("<td>");
				if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedEditLocalities()) {
					out.print("<a href='data_entry.jsp?Type=" + featType + "&FoldID=" + folder.getFolderID() + "&FeatID=" + featID + "&Redirect=" + redirect + "'><img src='images/edit.gif' border='0' height='20' width='20' alt='Edit Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				}
				out.print("</td><td>");
				if (folder.isAllowedCreateLocalities()) {
//					out.print("<a href='#' onClick='prmpt=prompt(\"Please enter the new name\", \"New " + featType + "\");if(prmpt!=null){document.FoldForm.NewFeatName.value=prmpt;document.FoldForm.ActionType.value=\"CopyFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}'><img src='images/copy.gif' border='0' height='20' width='20' alt='Copy Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				}
				out.print("</td><td>");
				if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedDeleteLocalities()) {
					out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this locality\") == true) {document.FoldForm.ActionType.value=\"DeleteFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}'><img src='images/delete.gif' border='0' height='20' width='20' alt='Delete Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				}
				out.print("</td><td>");
				if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedSubmitLocalities()) {
					out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to submit this locality\") == true) {document.FoldForm.ActionType.value=\"Submit\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}'><img src='images/submit.gif' border='0' height='20' width='20' alt='Submit Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				}
				else if (locStatus.equals("waiting") && folder.isAllowedSubmitLocalities()) {
					out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to revoke this locality\") == true) {document.FoldForm.ActionType.value=\"Revoke\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}'><img src='images/revoke.gif' border='0' height='20' width='20' alt='Revoke Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				}
				out.println("</td></tr>");

				out.println("<tr><td colspan='9'><img src='images/line.gif' height='3' width='550' /></td></tr>");
			}
			out.println("<input type='hidden' name='ActionType' value=''>");
			out.println("<input type='hidden' name='ID' value='" + folder.getFolderID() + "'>");
			out.println("<input type='hidden' name='FeatID' value=''>");
			out.println("<input type='hidden' name='NewFoldID' value=''>");
			out.println("<input type='hidden' name='NewFeatName' value=''>");

			out.println("</table></p>");

/*			//folder options
			out.println("<table border='0' cellspacing='0' cellpadding = '2' width='600'><tr><td height='5'></td></tr><tr class='shadegreytr'><td>");
			//Copy
			//check for multiple user folders (and if found display move option)
			rs = statement.executeQuery("SELECT * FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type='personal' AND Folder_ID <> " + foldID);
			if (rs.next()) {
				out.println("&nbsp&nbsp<a href='#' onClick='if (document.FoldForm.CopyFoldID.value!=\"-\") {document.FoldForm.ActionType.value=\"CopyFold\";document.FoldForm.NewFoldID.value=document.FoldForm.CopyFoldID.value;document.FoldForm.submit();} else {alert(\"Please select a folder\");document.FoldForm.NewFoldID.focus();}' class='smallfname'>Copy&nbspSelected&nbspto</a>&nbsp");
				HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, "CopyFoldID", "-- Choose --", null, null, "Folder_View", "Folder_Name", "Folder_ID", null, "User_ID = " + userID + " AND Folder_Type = 'personal' AND Folder_ID <> " + foldID);
			}
			//Move
			if ((userRights & 8) != 0) {
				//check for multiple user folders (and if found display move option)
				rs = statement.executeQuery("SELECT * FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type='personal' AND Folder_ID <> " + foldID);
				if (rs.next()) {
					out.println("  &nbsp&nbsp<a href='#' onClick='if (document.FoldForm.MoveFoldID.value!=\"-\") {document.FoldForm.ActionType.value=\"MoveFold\";document.FoldForm.NewFoldID.value=document.FoldForm.MoveFoldID.value;document.FoldForm.submit();} else {alert(\"Please select a folder\");document.FoldForm.NewFoldID.focus();}' class='smallfname'>Move&nbspSelected&nbspto</a>&nbsp");
					HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, "MoveFoldID", "-- Choose --", null, null, "Folder_View", "Folder_Name", "Folder_ID", null, "User_ID = " + userID + " AND Folder_Type = 'personal' AND Folder_ID <> " + foldID);
				}
			}
			//Delete
			if ((userRights & 8) != 0) {
				out.println("  &nbsp&nbsp<a href='#' onClick='if (confirm(\"Are you sure you want to remove these records\") == true) {document.FoldForm.ActionType.value=\"Remove\";document.FoldForm.submit();}' class='smallfname'>Remove&nbspSelected</a>");
			}
			out.println("</table></p>");  */
			out.println("</form>");
			out.println("</td></tr></table>");
		}
		else { //no folder found
			drawEndNavigation(out);
			out.println("<p><span class='heading'>No folder found</span></p>");
			out.println("<p>An incorrect parameter has been recieved by this page.  Please press the Back button and try again</p>");
		}
	}
	else {
		drawTop(out, et, request, response);
		drawEndNavigation(out);
	}

	drawBottom(out, et);
%>