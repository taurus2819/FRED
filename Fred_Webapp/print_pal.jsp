<%@		page extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, nz.cri.gns.db.metadata.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	out.println("<!DOCTYPE html ");
	out.println("   PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" ");
	out.println("  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> ");
	out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");

	out.println(" <head>");
	out.println("  <title>Fossil Record Electronic Database</title>");
	out.println("  <link rel=\"styleSheet\" href=\"/online/style/extranet.css\" type=\"text/css\" />");
	out.println(" </head>");
	out.println(" <body>");

	try {
		String recID = request.getParameter("ID");
		PaleontologyRecord pal = (PaleontologyRecord) PaleontologyRecord.getData(Integer.parseInt(recID), user, state);
		Sample sample = new Sample(pal.getAsInt(Record.SAMPLE_ID), user, state);
		Audit audit = Audit.getAudit(sample.getAsInt(Sample.AUDIT_ID), state);
		out.println("<table border='1' cellspacing='0' cellpadding='10' width='620'>");
		out.println("<tr><td>");

		out.println("<table border='0' cellspacing='0' cellpadding='0' width='600'>");
		out.println("<tr><td rowspan='2'><img src='images/gslogo.gif' width='42' height='50' /></td><td class='smallheading'>GEOLOGICAL SOCIETY OF NEW ZEALAND</td><td class='hugeheading' align='right'>");
		if (sample.get(Sample.FR_NUMBER) != null) {
			out.print(sample.getAsString(Sample.FR_NUMBER));
		} else {
			out.print("_______/f_____");
		}
		out.println("</td></tr>");
		out.print("<tr><td class='hugeheading'>FOSSIL RECORD FORM</td><td align='right' class='heading'>" + FREDUtils.noNulls(sample.getAsString(Sample.FEATURE_TYPE)) + "</td></tr>");		
		out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
		out.println("</table>");

		out.println("<table border='0' cellspacing='0' cellpadding='0' width='600'>");
		if (sample.get(Sample.MASTERFILE_NAME) != null)
			out.println("<tr><td class='smallheading'>Masterfile:&nbsp;</td><td class='smalltext'>" + sample.getAsString(Sample.MASTERFILE_NAME) + "</td></tr>");
		if (audit.get(Audit.APPROVED_BY) != null || audit.get(Audit.APPROVED_DATE) != null) {
			out.println("<tr><td class='smallheading'>MF Curator Approved:&nbsp;</td><td class='smalltext'>");
				if (audit.get(Audit.APPROVED_BY) != null) out.print(audit.getAsString(Audit.APPROVED_BY) + "&nbsp;&nbsp;");
				if (audit.get(Audit.APPROVED_DATE) != null) out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.APPROVED_DATE)));
				out.println("</td></tr>");
			}

		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("</table>");

		out.println("<table border='0' cellspacing='0' cellpadding='2' width='600'>");

		out.println("<tr><td class='bigheading' colspan='2'>Paleontology Record</td></tr>");
		
		//identifiers (repeating)
		if (pal.get(PaleontologyRecord.IDENTIFIER) != null) {
			out.print("<tr><td class='heading'>Identifiers</td><td>");
			for (Iterator i2 = pal.getAsVector(PaleontologyRecord.IDENTIFIER).iterator(); i2.hasNext(); ) {
				KeyValueObject coll = (KeyValueObject)i2.next();
				out.print(coll.getValue() + "<br />");
			}
			out.print("</td></tr>");
		}
		if (pal.get(PaleontologyRecord.IDENTIFICATION_DATE) != null)
			out.print("<tr><td class='heading'>Identification Date</td><td>" + FREDUtils.formatDateForOutput(pal.getAsDate(PaleontologyRecord.IDENTIFICATION_DATE), pal.getAsString(PaleontologyRecord.IDENTIFICATION_DATE_ROUNDING)) + "</td></tr>");
		if (pal.get(PaleontologyRecord.STAGE) != null)
			out.println("<tr><td class='heading'>Stage</td><td>" + pal.getAsString(PaleontologyRecord.STAGE) + "</td></tr>");
		if (pal.get(PaleontologyRecord.STAGE_COMMENTS) != null)
			out.println("<tr><td class='heading'>Stage Comments</td><td>" + pal.getAsString(PaleontologyRecord.STAGE_COMMENTS) + "</td></tr>");
		if (pal.get(PaleontologyRecord.LAB) != null)
			out.println("<tr><td class='heading'>Lab</td><td>" + pal.getAsString(PaleontologyRecord.LAB) + "</td></tr>");
		if (pal.get(PaleontologyRecord.LAB_NUMBER) != null)
			out.println("<tr><td class='heading'>Lab Number</td><td>" + pal.getAsString(PaleontologyRecord.LAB_NUMBER) + "</td></tr>");
		if (pal.get(PaleontologyRecord.COLLECTION_COMMENTS) != null)
			out.println("<tr><td class='heading'>Collection Comments</td><td>" + pal.getAsString(PaleontologyRecord.COLLECTION_COMMENTS) + "</td></tr>");
	
		//taxa (double repeating)
		if (pal.get(PaleontologyRecord.TAXONOMIC_LIST) != null) {
			out.println("<tr><td colspan='2'><table border='0' cellspacing='0' cellpadding='2'>");
			for (Iterator i2 = pal.getAsVector(PaleontologyRecord.TAXONOMIC_LIST).iterator(); i2.hasNext(); ) {
				TaxaGroup taxaGroup = (TaxaGroup)i2.next();
				out.println("<tr><td colspan='4' class='heading'>" + taxaGroup.getGroupName() + "</td></tr>");
				if (taxaGroup.getTaxaList() != null) {
					out.println("<tr class='heading'><td>Taxonomic Name&nbsp;&nbsp;</td><td>Spec&nbsp;Count&nbsp;&nbsp;</td><td>Spec&nbsp;Coord&nbsp;&nbsp;</td><td>Comments&nbsp;&nbsp;</td></tr>");
					for (Iterator i3 = taxaGroup.getTaxaList().iterator(); i3.hasNext(); ) {
						Taxa taxa = (Taxa)i3.next();
						out.print("<tr><td>" + taxa.getTaxonomicName() + "&nbsp;&nbsp;</td>");
						out.print("<td class='smalltext'>" +FREDUtils.noNulls(String.valueOf(taxa.getSpecimenCount())) + "&nbsp;&nbsp;</td>");
						out.print("<td class='smalltext'>" +FREDUtils.noNulls(taxa.getSpecimenCoords()) + "&nbsp;&nbsp;</td>");
						out.print("<td class='smalltext'>" +FREDUtils.noNulls(taxa.getComments()) + "&nbsp;&nbsp;</td>");
						out.println("</tr>");
					}
				} else {
					out.println("<tr><td colspan='4'>No fossils listed</td></tr>");
				}
				out.println("<tr><td><img src='images/blank.gif' height='10' width='1' /></td></tr>");
			}
			out.println("</td></tr></table></td></tr>");
		}
		out.println("<img src='images/blank.gif' width='600' height='1' />");
		out.println("</td></tr></table>");
	}
	catch (Exception e) { //no record or no rights
		out.println("<p><span class='bigheading'>Access denied</span></p>Either there is no record matching the ID you entered or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.");
	}
%>