<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());
	DBConnection connection = FREDUtils.getFREDConnection(state);

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("ID") != null) {
		Folder folder = new Folder(Integer.parseInt(request.getParameter("ID")), user, state, true);
		String redirect = URLEncoder.encode("admin_folder_detail.jsp?ID=" + folder.getFolderID(), "UTF-8");

		if (folder.isAllowedReadLocalities()) {
			
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/folder.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' class='bigheading' align='center'>" + folder.getAsString(Folder.NAME) + " Masterfile</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			//List records

			//Table header
			out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
			
			//To Approve
			out.println("<tr><th colspan='5'>Localities to Approve</th></tr>");
			out.println("<tr><th colspan='2'>Locality&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Submitted Date&nbsp;&nbsp;</th><th>Submitted By&nbsp;&nbsp;</th><th colspan='3'>Options</th></tr>");
			for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
				Feature feature = new Feature(((Integer) i.next()).intValue(), user, state);
				Audit audit = Audit.getAudit(feature.getAsInt(Feature.AUDIT_ID), state);
				int featID = feature.getFeatureID();
				out.print("<tr><td><a href='detail.jsp?FeatID=" + featID + "'><img src='images/loc.gif' height='20' width='20' border='0' alt='View Locality' /></a></td><td class='heading'>" + feature.getAsString(Feature.SAMPLE_NAMES) + "&nbsp;&nbsp;</td><td>" + feature.getAsString(Feature.FEATURE_TYPE) + "&nbsp;&nbsp;</td><td>");
				if (audit.get(Audit.SUBMITTED_DATE) != null) 
					out.print(DateFormat.getDateInstance(DateFormat.LONG).format(audit.getAsDate(Audit.SUBMITTED_DATE)));
				out.print("&nbsp;&nbsp;</td><td>" + audit.getAsString(Audit.SUBMITTED_BY) + "&nbsp;&nbsp;</td><td>");
				out.print("<a href='print_front.jsp?FeatID=" + featID + (feature.getAsString(Feature.FEATURE_TYPE).equals("Outcrop") ? "" : "&FormType=Short") + "' target='print'><img src='images/print.gif' border='0' height='20' width='20' alt='Print Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				out.print("</td><td>");
				if (folder.isAllowedEditLocalities()) 
					out.print("<a href='data_entry.jsp?Type=" + feature.getAsString(Feature.FEATURE_TYPE) + "&FoldID=" + folder.getFolderID() + "&FeatID=" + featID + "&Redirect=" + redirect + "'><img src='images/edit.gif' border='0' height='20' width='20' alt='Edit Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				out.print("</td><td>");
				if (folder.isAllowedApproveLocalities())
					out.print("<a href='detail.jsp?FeatID=" + featID + "'><img src='images/review.gif' width='20' height='20' border='0' alt='Review Localities' /></a>");
				out.println("</td></tr>");
			}
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			
			//Recently Approved
			out.println("<tr><th colspan='5'>Localities Recently Approved</th></tr>");
			out.println("<tr><th colspan='2'>Locality&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Approved Date&nbsp;&nbsp;</th><th>Approved By&nbsp;&nbsp;</th><th colspan='3'>Options</th></tr>");
			String query = "SELECT DISTINCT S.Feature_ID, S.FR_Number FROM Sample_All_View S, Audit_Table A WHERE S.Feature_Audit_ID = A.Audit_ID AND S.Feature_Status = 'approved' AND S.Masterfile_ID = ? AND A.Approved_Date >= (SYSDATE - 7) ORDER BY S.FR_Number";
			int[] types = { Types.NUMERIC };
			Object[] data = { new Integer(folder.getFolderID()) };
			ResultSet rs = connection.executeQuery(query, types, data);
			connection.preservePreparedStatement();
			while (rs.next()) {
				Feature feature = new Feature(rs.getInt(1), user, state);
				Audit audit = Audit.getAudit(feature.getAsInt(Feature.AUDIT_ID), state);
				int featID = feature.getFeatureID();
				out.print("<tr><td><a href='detail.jsp?FeatID=" + featID + "'><img src='images/loc.gif' height='20' width='20' border='0' alt='View Locality' /></a></td><td><span class='heading'>" + feature.getAsString(Feature.SAMPLE_NAMES) + "</span>&nbsp;&nbsp;<br />(" + FREDUtils.noNulls(feature.getAsString(Feature.FEATURE_NAME)) + ")&nbsp;&nbsp;</td><td>" + feature.getAsString(Feature.FEATURE_TYPE) + "&nbsp;&nbsp;</td><td>");
				if (audit.get(Audit.APPROVED_DATE) != null) 
					out.print(DateFormat.getDateInstance(DateFormat.LONG).format(audit.getAsDate(Audit.APPROVED_DATE)));
				out.print("&nbsp;&nbsp;</td><td>" + audit.getAsString(Audit.APPROVED_BY) + "&nbsp;&nbsp;</td><td>");
				out.print("<a href='print_front.jsp?FeatID=" + featID + (feature.getAsString(Feature.FEATURE_TYPE).equals("Outcrop") ? "" : "&FormType=Short") + "' target='print'><img src='images/print.gif' border='0' height='20' width='20' alt='Print Locality' /></a>");
				out.print("</td><td></td></tr>");
			}
			connection.releaseStatement();
			out.println("</table></p>");
		}
		else { //no record found
			out.println("No folder found");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>