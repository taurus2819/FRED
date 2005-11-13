<%@		page extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, nz.cri.gns.db.metadata.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);
	
	//get SampleID
	if (request.getParameter("ID") != null) {
		String sampID = request.getParameter("ID");	
		Sample sample = new Sample(Integer.parseInt(sampID), user, state);

		//List data
		if (!sample.isApprovedLocality() && !sample.isUserAuthenticated())
			throw new InvalidCredentialsException();
		Audit audit = Audit.getAudit(sample.getAsInt(Sample.FEATURE_AUDIT_ID), state);
		String featType = sample.getAsString(Sample.FEATURE_TYPE);
		out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='bigheading' >" + sample.getAsString(Sample.SAMPLE_NAME) + "</td></tr>");
		out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
		if (sample.get(Sample.MASTERFILE_NAME) != null) {
			out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + sample.getAsString(Sample.MASTERFILE_NAME) + "</td></tr>");
		}
		out.println("</table>");
		
		drawEndNavigation(out);
			
		out.println("<table style=\"'margin-left:20px; width:500px;\" border=\"0\" cellspacing=\"2\">");		
		
		out.println("<tr><td class=\"bigheading\" colspan=\"3\">Locality</td></tr>");
		if (!audit.getAsString(Audit.STATUS).equals(Audit.STATUS_APPROVED))
			out.println("<tr><td class=\"smallheading\">Status:&nbsp;&nbsp;</td><td style=\"color: #FF0000\">" + audit.getAsString(Audit.STATUS) + "</td></tr>");
		if (audit.get(Audit.CREATED_BY) != null || audit.get(Audit.CREATED_DATE) != null)
			out.println("<tr><td class=\"heading\">Created:&nbsp;&nbsp;</td><td>"
				+ FREDUtils.noNulls(audit.getAsString(Audit.CREATED_BY)) + "&nbsp;&nbsp;"
				+ "</td><td>"
				+ ((audit.get(Audit.CREATED_DATE) != null) ? FREDUtils.formatDateForOutput(audit.getAsDate(Audit.CREATED_DATE)) + "&nbsp;&nbsp;" : "")
				+ "</td></tr>");
		if (audit.get(Audit.SUBMITTED_BY) != null || audit.get(Audit.SUBMITTED_DATE) != null)
			out.println("<tr><td class=\"heading\">Submitted:&nbsp;&nbsp;</td><td>"
				+ FREDUtils.noNulls(audit.getAsString(Audit.SUBMITTED_BY)) + "&nbsp;&nbsp;"
				+ "</td><td>"
				+ ((audit.get(Audit.SUBMITTED_DATE) != null) ? FREDUtils.formatDateForOutput(audit.getAsDate(Audit.SUBMITTED_DATE)) + "&nbsp;&nbsp;" : "")
				+ "</td></tr>");
		if (audit.get(Audit.APPROVED_BY) != null || audit.get(Audit.APPROVED_DATE) != null || audit.get(Audit.CURATOR_COMMENTS) != null)
			out.println("<tr><td class=\"heading\">Approved:&nbsp;&nbsp;</td><td>"
				+ FREDUtils.noNulls(audit.getAsString(Audit.APPROVED_BY)) + "&nbsp;&nbsp;"
				+ "</td><td>"
				+ ((audit.get(Audit.APPROVED_DATE) != null) ? FREDUtils.formatDateForOutput(audit.getAsDate(Audit.APPROVED_DATE)) + "&nbsp;&nbsp;" : "")
				+ "</td><td class=\"smalltext\">"
				+ FREDUtils.noNulls(audit.getAsString(Audit.CURATOR_COMMENTS))
				+ "</td></tr>");
		if (audit.get(Audit.EDIT_HISTORY) != null) {
			for (Iterator i = audit.getAsVector(Audit.EDIT_HISTORY).iterator(); i.hasNext(); ) {
				AuditEdit ae = (AuditEdit) i.next();
				out.println("<tr><td class=\"heading\">Edited:&nbsp;&nbsp;</td><td>"
					+ ((ae.getEditedBy() != null) ? ae.getEditedBy() + "&nbsp;&nbsp;" : "")
					+ "</td><td>"
					+ ((ae.getEditedDate() != null) ? FREDUtils.formatDateForOutput(ae.getEditedDate()) + "&nbsp;&nbsp;" : "")
					+ "</td><td class=\"smalltext\">"
					+ FREDUtils.noNulls(ae.getComments())
					+ "</td></tr>");
			}
		}
		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");

		if (sample.getAsInt(Sample.SAMPLE_AUDIT_ID) != sample.getAsInt(Sample.FEATURE_AUDIT_ID)) {
			audit = Audit.getAudit(sample.getAsInt(Sample.SAMPLE_AUDIT_ID), state);
			out.println("<tr><td class=\"bigheading\" colspan=\"4\">Sample&nbsp;<span class=\"smallheading\">"
					+ sample.getAsString(Sample.DRILLHOLE_DEPTH) + "</span></td></tr>");
			if (!audit.getAsString(Audit.STATUS).equals(Audit.STATUS_APPROVED))
				out.println("<tr><td class=\"heading\">Status:&nbsp;&nbsp;</td><td style=\"color: #FF0000\">" + audit.getAsString(Audit.STATUS) + "</td></tr>");
			if (audit.get(Audit.CREATED_BY) != null || audit.get(Audit.CREATED_DATE) != null)
				out.println("<tr><td class=\"heading\">Created:&nbsp;&nbsp;</td><td>"
					+ ((audit.get(Audit.CREATED_BY) != null) ? audit.getAsString(Audit.CREATED_BY) + "&nbsp;&nbsp;" : "")
					+ "</td><td>"
					+ ((audit.get(Audit.CREATED_DATE) != null) ? FREDUtils.formatDateForOutput(audit.getAsDate(Audit.CREATED_DATE)) + "&nbsp;&nbsp;" : "")
					+ "</td></tr>");
			if (audit.get(Audit.SUBMITTED_BY) != null || audit.get(Audit.SUBMITTED_DATE) != null)
				out.println("<tr><td class=\"heading\">Submitted:&nbsp;&nbsp;</td><td>"
					+ ((audit.get(Audit.SUBMITTED_BY) != null) ? audit.getAsString(Audit.SUBMITTED_BY) + "&nbsp;&nbsp;" : "")
					+ "</td><td>"
					+ ((audit.get(Audit.SUBMITTED_DATE) != null) ? FREDUtils.formatDateForOutput(audit.getAsDate(Audit.SUBMITTED_DATE)) + "&nbsp;&nbsp;" : "")
					+ "</td></tr>");
			if (audit.get(Audit.EDIT_HISTORY) != null) {
				for (Iterator i = audit.getAsVector(Audit.EDIT_HISTORY).iterator(); i.hasNext(); ) {
					AuditEdit ae = (AuditEdit) i.next();
					out.println("<tr><td class=\"heading\">Edited:&nbsp;&nbsp;</td><td>"
						+ ((ae.getEditedBy() != null) ? ae.getEditedBy() + "&nbsp;&nbsp;" : "")
						+ "</td><td>"
						+ ((ae.getEditedDate() != null) ? FREDUtils.formatDateForOutput(ae.getEditedDate()) + "&nbsp;&nbsp;" : "")
						+ "</td><td class=\"smalltext\">"
						+ FREDUtils.noNulls(ae.getComments())
						+ "</td></tr>");
				}
			}
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		}
		
		if (sample.get(Sample.RECORDS) != null) {
			//Records
			for (Iterator i = sample.getAsVector(Sample.RECORDS).iterator(); i.hasNext(); ) {
				KeyValueObject rec = (KeyValueObject)i.next();
				Record record;
				try {
					if (rec.getValue().equals(Record.ADOPTION_RECORD)) {
						record = (AdoptionRecord) AdoptionRecord.getData(Integer.parseInt(rec.getKey()), user, state, false);
					} else {
						record = (PaleontologyRecord) PaleontologyRecord.getData(Integer.parseInt(rec.getKey()), user, state, false);
					}
					audit = Audit.getAudit(record.getAsInt(Record.AUDIT_ID), state);
					out.println("<tr><td class=\"bigheading\" colspan=\"4\">"
						+ ((rec.getValue().equals(Record.ADOPTION_RECORD)) ? "Adoption" : "Paleontology")
						+ "&nbsp;<span class=\"smallheading\">" + record + "</span></td></tr>");
					if (!audit.getAsString(Audit.STATUS).equals(Audit.STATUS_APPROVED))
						out.println("<tr><td class=\"heading\">Status:&nbsp;&nbsp;</td><td style=\"color: #FF0000\">" + audit.getAsString(Audit.STATUS) + "</td></tr>");
					if (audit.get(Audit.CREATED_BY) != null || audit.get(Audit.CREATED_DATE) != null)
						out.println("<tr><td class=\"heading\">Created:&nbsp;&nbsp;</td><td>"
							+ ((audit.get(Audit.CREATED_BY) != null) ? audit.getAsString(Audit.CREATED_BY) + "&nbsp;&nbsp;" : "")
							+ "</td><td>"
							+ ((audit.get(Audit.CREATED_DATE) != null) ? FREDUtils.formatDateForOutput(audit.getAsDate(Audit.CREATED_DATE)) + "&nbsp;&nbsp;" : "")
							+ "</td></tr>");
					if (audit.get(Audit.SUBMITTED_BY) != null || audit.get(Audit.SUBMITTED_DATE) != null)
						out.println("<tr><td class=\"heading\">Submitted:&nbsp;&nbsp;</td><td>"
							+ ((audit.get(Audit.SUBMITTED_BY) != null) ? audit.getAsString(Audit.SUBMITTED_BY) + "&nbsp;&nbsp;" : "")
							+ "</td><td>"
							+ ((audit.get(Audit.SUBMITTED_DATE) != null) ? FREDUtils.formatDateForOutput(audit.getAsDate(Audit.SUBMITTED_DATE)) + "&nbsp;&nbsp;" : "")
							+ "</td></tr>");
					if (audit.get(Audit.EDIT_HISTORY) != null) {
						for (Iterator j = audit.getAsVector(Audit.EDIT_HISTORY).iterator(); j.hasNext(); ) {
							AuditEdit ae = (AuditEdit) j.next();
							out.println("<tr><td class=\"heading\">Edited:&nbsp;&nbsp;</td><td>"
								+ ((ae.getEditedBy() != null) ? ae.getEditedBy() + "&nbsp;&nbsp;" : "")
								+ "</td><td>"
								+ ((ae.getEditedDate() != null) ? FREDUtils.formatDateForOutput(ae.getEditedDate()) + "&nbsp;&nbsp;" : "")
								+ "</td><td class=\"smalltext\">"
								+ FREDUtils.noNulls(ae.getComments())
								+ "</td></tr>");
						}
					}
					out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
				} catch (Exception e) {}
			}
		}
		
		
		out.println("</table>");
	} 
	else { //no sampleID
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>No Sample</td></tr>");
		out.println("</table>");
	}
	
	drawBottom(out, et); 
%>