<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, java.util.*, nz.cri.gns.auth.*, java.text.*"
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	Record record = null;
	try {
		record = PaleontologyRecord.getData(Integer.parseInt(request.getParameter("RecID")), user, state);
	} catch (Exception e) {}

	if (record != null) {

		drawTop(out, et, request, response);

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td align='center'><img src='images/pal.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td align='center' class='heading'>" + record + "</td></tr>");
		out.println("</table>");

		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		Vector appTaxa = new Vector();
		Vector provTaxa = new Vector();
		Vector rejTaxa = new Vector();
		Vector obTaxa = new Vector();

		if (record.get(Record.TAXA_IDS) != null) {
			for (Iterator i = record.getAsVector(Record.TAXA_IDS).iterator(); i.hasNext(); ) {
				TaxonomicLookup tl = new TaxonomicLookup(((Integer)i.next()).intValue(), user, state);
				if (tl.getAsString(TaxonomicLookup.STATUS).equals(TaxonomicLookup.APPROVED_STATUS)) {
					appTaxa.add(tl);
				} else if (tl.getAsString(TaxonomicLookup.STATUS).equals(TaxonomicLookup.PROVISIONAL_STATUS)) {
					provTaxa.add(tl);
				} else if (tl.getAsString(TaxonomicLookup.STATUS).equals(TaxonomicLookup.REJECTED_STATUS)) {
					rejTaxa.add(tl);
				} else if (tl.getAsString(TaxonomicLookup.STATUS).equals(TaxonomicLookup.OBSOLETE_STATUS)) {
					obTaxa.add(tl);
				}
			}
		}

		out.println("<p>Listed below are the taxonomic entries for the selected Paleontology Record grouped by status. This list will update as the status of an entry is changed by the database curators.<p>");

		out.println("<table border='0' cellspacing='0' cellpadding='2' width='550'>");
		//List provisional taxa
		if (provTaxa.size() > 0) {
			out.println("<tr><th colspan=\"5\">Provisional Entries</th></tr>");
			out.println("<tr><td colspan=\"5\" style=\"color: #FF0000\">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr>"); 
			out.println("<tr><th>Taxonomic Name&nbsp;&nbsp;</th><th>Group&nbsp;&nbsp;</th><th>Author&nbsp;&nbsp;</th><th colspan=\"2\">Submitted By</th></tr>");
			for (Iterator i = provTaxa.iterator(); i.hasNext(); ) {
				TaxonomicLookup tl =(TaxonomicLookup)i.next();
				out.println("<tr><td>" + tl.getAsString(TaxonomicLookup.TAXONOMIC_NAME) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + tl.getAsString(TaxonomicLookup.GROUP_NAME) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.AUTHOR)) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.SUBMITTED_BY)) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + ((tl.get(TaxonomicLookup.SUBMITTED_DATE) != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(tl.getAsDate(TaxonomicLookup.SUBMITTED_DATE)) : "") + "&nbsp;&nbsp;</td>");
				out.println("<tr><td>&nbsp;</td></tr>");
			}
		}

		//List rejected taxa
		if (rejTaxa.size() > 0) {
			out.println("<tr><th colspan=\"5\">Rejected Entries</th></tr>");
			out.println("<tr><td colspan=\"5\" style=\"color: #FF0000\">This record contains rejected taxonomic entries. You must remove these entries before submitting the record</td></tr>");
			out.println("<tr><th>Taxonomic Name&nbsp;&nbsp;</th><th>Group&nbsp;&nbsp;</th><th>Author&nbsp;&nbsp;</th><th colspan=\"2\">Rejected By</th></tr>");
			for (Iterator i = rejTaxa.iterator(); i.hasNext(); ) {
				TaxonomicLookup tl =(TaxonomicLookup)i.next();
				out.println("<tr><td>" + tl.getAsString(TaxonomicLookup.TAXONOMIC_NAME) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + tl.getAsString(TaxonomicLookup.GROUP_NAME) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.AUTHOR)) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.APPROVED_BY)) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + ((tl.get(TaxonomicLookup.APPROVED_DATE) != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(tl.getAsDate(TaxonomicLookup.SUBMITTED_DATE)) : "") + "&nbsp;&nbsp;</td>");
				out.println("<tr><td>&nbsp;</td></tr>");
			}
		}

		//List obsoloete taxa
		if (obTaxa.size() > 0) {
			out.println("<tr><th colspan=\"5\">Obsolete Entries</th></tr>");
			out.println("<tr><td colspan=\"5\" style=\"color: #FF0000\">This record contains obsolete taxonomic entries. You must remove these entries before submitting the record</td></tr>");
			out.println("<tr><th>Taxonomic Name&nbsp;&nbsp;</th><th>Group&nbsp;&nbsp;</th><th>Author</th></tr>");
			for (Iterator i = obTaxa.iterator(); i.hasNext(); ) {
				TaxonomicLookup tl =(TaxonomicLookup)i.next();
				out.println("<tr><td>" + tl.getAsString(TaxonomicLookup.TAXONOMIC_NAME) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + tl.getAsString(TaxonomicLookup.GROUP_NAME) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.AUTHOR)) + "&nbsp;&nbsp;</td>");
				out.println("<tr><td>&nbsp;</td></tr>");
			}
		}

		//List approved taxa
		if (appTaxa.size() > 0) {
			out.println("<tr><th colspan=\"5\">Approved Entries</th></tr>");
			out.println("<tr><th>Taxonomic Name&nbsp;&nbsp;</th><th>Group&nbsp;&nbsp;</th><th>Author&nbsp;&nbsp;</th><th colspan=\"2\">Approved By</th></tr>");
			for (Iterator i = appTaxa.iterator(); i.hasNext(); ) {
				TaxonomicLookup tl =(TaxonomicLookup)i.next();
				out.println("<tr><td>" + tl.getAsString(TaxonomicLookup.TAXONOMIC_NAME) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + tl.getAsString(TaxonomicLookup.GROUP_NAME) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.AUTHOR)) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(tl.getAsString(TaxonomicLookup.APPROVED_BY)) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + ((tl.get(TaxonomicLookup.APPROVED_DATE) != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(tl.getAsDate(TaxonomicLookup.SUBMITTED_DATE)) : "") + "&nbsp;&nbsp;</td>");
			}
		}
		
		out.println("</table>");

		out.println("</td></tr></table>");
		drawBottom(out, et);
	
	}





%>


