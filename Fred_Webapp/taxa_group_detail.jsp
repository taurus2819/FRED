<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, java.util.*, java.text.*, nz.cri.gns.auth.*"
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("ID") != null) {
		TaxaPanel panel = new TaxaPanel(Integer.parseInt(request.getParameter("ID")), user, state);

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center' class='bigheading'>" + panel.getAsString(TaxaPanel.NAME) + "</td></tr>");
		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
		out.println("</table>");

		drawEndNavigation(out);

		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		if (panel.isPanelMember()) {
			if (request.getParameter("ActionType") != null && request.getParameter("TaxaID") != null) { //do something
				String actionType = request.getParameter("ActionType");
				if (actionType.equals("Approve")) { //approve taxa
					FolderUtils.approveTaxa(request.getParameter("TaxaID"), String.valueOf(panel.getPanelID()), user, state);
				}
				else if (actionType.equals("Reject")) { //reject taxa
					FolderUtils.rejectTaxa(request.getParameter("TaxaID"), String.valueOf(panel.getPanelID()), user, state);
				}
				response.sendRedirect("taxa_group_detail.jsp?ID=" + panel.getPanelID());
				return;
			}

			Vector tls = new Vector();

			out.println("<p>Listed below are the Taxonomic names in the above group.  Any provisional entries are listed first and need to be either approved or rejected</p>");

			//List theasurus - provisional entries at top
			out.println("<table border='0' cellspacing='0' cellpadding='2' width='550'>");

			if (panel.getProvisionalCount() > 0) {
				out.println("<tr><th colspan='6'>Provisional Entries</th></tr>");
				out.println("<tr><th>Name&nbsp;&nbsp;</th><th>Author&nbsp;&nbsp;</th><th>Submitted By&nbsp;&nbsp;</th><th>Submitted Date&nbsp;&nbsp;</th><th colspan=\"2\">Options</th></tr>");
				for (Iterator i = panel.getAsVector(TaxaPanel.PROVISIONAL_TAXA).iterator(); i.hasNext(); ) {
					KeyValueObject kv = (KeyValueObject) i.next();
					TaxonomicLookup tl = new TaxonomicLookup(Integer.parseInt(kv.getKey()), user, state);
					tls.add(tl);
					out.println("<tr><td class='heading'>" + tl.getAsString(TaxonomicLookup.TAXONOMIC_NAME) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.AUTHOR)) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + tl.getAsString(TaxonomicLookup.SUBMITTED_BY) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + ((tl.get(TaxonomicLookup.SUBMITTED_DATE) != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(tl.getAsDate(TaxonomicLookup.SUBMITTED_DATE)) : "") + "&nbsp;&nbsp;</td>");
					out.println("<td><a href='taxa_group_detail.jsp?ID=" + panel.getPanelID() + "&ActionType=Approve&TaxaID=" + tl.getTaxaID() + "'><img src='images/ok.gif' border='0' height='20' width='20' alt='approve' />&nbsp;</td><td><a href='taxa_group_detail.jsp?ID=" + panel.getPanelID() + "&ActionType=Reject&TaxaID=" + tl.getTaxaID() + "'><img src='images/cancel.gif' border='0' height='20' width='20' alt='Reject' /></a></td></tr>");
				}
			}
			out.println("<tr><td>&nbsp;</td></tr>");

			if (panel.getRejectedCount() > 0) {
				out.println("<tr><th colspan='6'>Rejected Entries</th></tr>");
				out.println("<tr><th>Name&nbsp;&nbsp;</th><th>Author&nbsp;&nbsp;</th><th>Rejected By&nbsp;&nbsp;</th><th>Rejected Date</th></tr>");
				for (Iterator i = panel.getAsVector(TaxaPanel.REJECTED_TAXA).iterator(); i.hasNext(); ) {
					KeyValueObject kv = (KeyValueObject) i.next();
					TaxonomicLookup tl = new TaxonomicLookup(Integer.parseInt(kv.getKey()), user, state);
					tls.add(tl);
					out.println("<tr><td class='heading'>" + tl.getAsString(TaxonomicLookup.TAXONOMIC_NAME) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.AUTHOR)) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + tl.getAsString(TaxonomicLookup.APPROVED_BY) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + ((tl.get(TaxonomicLookup.APPROVED_DATE) != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(tl.getAsDate(TaxonomicLookup.APPROVED_DATE)) : "") + "&nbsp;&nbsp;</td></tr>");
				}
			}
			out.println("<tr><td>&nbsp;</td></tr>");

			if (panel.getApprovedCount() > 0) {
				out.println("<tr><th colspan='6'>Approved Entries</th></tr>");
				out.println("<tr><th>Name&nbsp;&nbsp;</th><th>Author&nbsp;&nbsp;</th><th>Approved By</th><th>Approved Date</th></tr>");
				for (Iterator i = panel.getAsVector(TaxaPanel.APPROVED_TAXA).iterator(); i.hasNext(); ) {
					KeyValueObject kv = (KeyValueObject) i.next();
					TaxonomicLookup tl = new TaxonomicLookup(Integer.parseInt(kv.getKey()), user, state);
					tls.add(tl);
					out.println("<tr><td class='heading'>" + tl.getAsString(TaxonomicLookup.TAXONOMIC_NAME) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.AUTHOR)) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.APPROVED_BY)) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + ((tl.get(TaxonomicLookup.APPROVED_DATE) != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(tl.getAsDate(TaxonomicLookup.APPROVED_DATE)) : "") + "&nbsp;&nbsp;</td></tr>");
				}
			}
			out.println("<tr><td>&nbsp;</td></tr>");

			if (panel.getObsoleteCount() > 0) {
				out.println("<tr><th colspan='6'>Obsolete Entries</th></tr>");
				out.println("<tr><th>Name&nbsp;&nbsp;</th><th>Author</th></tr>");
				for (Iterator i = panel.getAsVector(TaxaPanel.OBSOLETE_TAXA).iterator(); i.hasNext(); ) {
					KeyValueObject kv = (KeyValueObject) i.next();
					TaxonomicLookup tl = new TaxonomicLookup(Integer.parseInt(kv.getKey()), user, state);
					tls.add(tl);
					out.println("<tr><td class='heading'>" + tl.getAsString(TaxonomicLookup.TAXONOMIC_NAME) + "&nbsp;&nbsp;</td>");
					out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.AUTHOR)) + "&nbsp;&nbsp;</td></tr>");
				}
			}
			out.println("<tr><td>&nbsp;</td></tr>");

			out.println("</table>");
			
			session.setAttribute("tls", tls);
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);

%>


