<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.fred.*, nz.cri.gns.db.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
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
	User user = getUser(session);
	PageState state = new PageState(request, response, getServletContext());
	String foldID, featID, featType, featName, sampName, locStatus;
	java.util.Date changeDate = new java.util.Date();

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("ID") != null) {
		foldID = request.getParameter("ID");

		//print error message (if any) from folder_actions
		if (request.getParameter("ErrMsg") != null) {
			out.println(request.getParameter("ErrMsg"));
		}

		Folder folder = new Folder(Integer.parseInt(foldID), user, state);

		if (folder.isAllowedReadLocalities()) {
			
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/folder.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' class='bigheading' align='center'>Folder: " + folder.getAsString(Folder.NAME) + "</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
			if (folder.isAllowedCreateLocalities()) {
				out.println("<tr><td><a href='feat_data_entry.jsp?Type=Outcrop&FoldID=" + foldID + "' title='Add New Locality'><img src='images/new.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='feat_data_entry.jsp?Type=Outcrop&FoldID=" + foldID + "' class='heading'>New Outcrop Locality</a></td></tr>");
				out.println("<tr><td><a href='feat_data_entry.jsp?Type=Drillhole&FoldID=" + foldID + "' title='Add New Locality'><img src='images/new.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='feat_data_entry.jsp?Type=Drillhole&FoldID=" + foldID + "' class='heading'>New Drillhole Locality</a></td></tr>");
				out.println("<tr><td><a href='feat_data_entry.jsp?Type=VertSect&FoldID=" + foldID + "' title='Add New Locality'><img src='images/new.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='feat_data_entry.jsp?Type=VertSect&FoldID=" + foldID + "' class='heading'>New Vertical Section Locality</a></td></tr>");
				out.println("<tr><td><a href='simple_query.jsp?FoldID=" + foldID + "' title='Search for a Locality' title='Search for a Locality'><img src='images/search.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='simple_query.jsp?FoldID=" + foldID + "' class='heading'>Search</a></td></tr>");
			}
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			//List records
			out.println("<p>Listed below are the localities you have added to this folder (working localities are named with their field number or drillhole name until they are allocated a Fossil Record Number).<br />Click on the Edit icon to edit locality data or to add/edit record data</p>");

			//Table header
			out.println("<p><table border='0' cellspacing='0' cellpadding='1' width='550'>");
			out.print("<tr>");
			//out.print("<td></td>");
			out.print("<th colspan='2'>Name&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Last Change&nbsp;&nbsp;</th><th colspan='7'>Options</th></tr>");
			out.println("<tr><td colspan='9'><img src='images/line.gif' height='3' width='550' /></td></tr>");

			//Record list
			out.println("<form name='FoldForm' method='get' action='folder_actions.jsp'>");

			//Feature
			Feature feature;
			for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
				feature = new Feature(((Integer) i.next()).intValue(), user, state);

				featID  = feature.getAsString(Feature.FEATURE_ID);
				sampName = feature.getAsString(Feature.SAMPLE_NAMES);
				featType = feature.getAsString(Feature.FEATURE_TYPE);
				if (featType.equals("Vertical Section")) { featType = "VertSect"; }
				featName = feature.getAsString(Feature.FEATURE_NAME);
				locStatus = feature.getAsString(Feature.STATUS);
				changeDate = feature.getAsDate(Feature.LAST_CHANGE);
				
				out.print("<tr><td width='20'><img src='images/loc.gif' height='20' width='20' /></td>");
				out.print("<td class='heading'><a href='detail.jsp?FeatID=" + featID + "'>" + sampName + "</a>&nbsp;&nbsp;");
				if (featName != null && !sampName.equals(featName)) { out.print("<br />(" + featName +")&nbsp;&nbsp;"); }
				out.print("</td><td>" + featType + "&nbsp;&nbsp;</td><td style='color: #FF0000'>");
				if (!locStatus.equals("approved")) {
					out.print(locStatus + "&nbsp;&nbsp;</td><td>");
					if (changeDate != null) { 
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(changeDate) + "&nbsp;&nbsp;");
					}
					out.print("</td>");
				} else {
					out.print("</td><td></td>");
				}
				out.print("<td>");
				if (folder.isAllowedEditLocalities()) {
					out.print("<a href='folder_feature_data.jsp?ID=" + featID + "&FoldID=" + foldID + "' title='Edit Locality'><img src='images/edit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
				}
				out.print("</td><td>");
				if (folder.isAllowedCreateLocalities()) {
					out.print("<a href='#' onClick='prmpt=prompt(\"Please enter the new name\", \"New " + featType + "\");if(prmpt!=null){document.FoldForm.NewFeatName.value=prmpt;document.FoldForm.ActionType.value=\"CopyFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Copy Locality'><img src='images/copy.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
				}
				out.print("</td><td>");
				if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedDeleteLocalities()) {
					out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this locality\") == true) {document.FoldForm.ActionType.value=\"DeleteFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Delete Locality'><img src='images/delete.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
				}
				out.print("</td><td>");
				if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedSubmitLocalities()) {
					out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to submit this locality\") == true) {document.FoldForm.ActionType.value=\"Submit\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Submit Locality'><img src='images/submit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
				}
				else if (locStatus.equals("waiting") && folder.isAllowedSubmitLocalities()) {
					out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to revoke this locality\") == true) {document.FoldForm.ActionType.value=\"Revoke\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Revoke Locality'><img src='images/revoke.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
				}
				out.println("</td></tr>");
				
				out.println("<tr><td colspan='9'><img src='images/line.gif' height='3' width='550' /></td></tr>");
			}
			out.println("<input type='hidden' name='ActionType' value=''>");
			out.println("<input type='hidden' name='ID' value='" + foldID + "'>");
			out.println("<input type='hidden' name='FeatID' value=''>");
			out.println("<input type='hidden' name='NewFoldID' value=''>");
			out.println("<input type='hidden' name='RecID' value=''>");
			out.println("<input type='hidden' name='RecType' value=''>");
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
		}
		else { //no folder found
			drawEndNavigation(out);
			out.println("<p><span class='heading'>No folder found</span></p>");
			out.println("<p>An incorrect parameter has been recieved by this page.  Please press the Back button and try again</p>");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>