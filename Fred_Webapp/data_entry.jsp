<%@		page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, java.io.*, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, java.text.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, nz.cri.gns.util.map.*"
%><%
	PageState state = new PageState(request, response, getServletContext());
	User user = (User)getUser(session);
	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);
	
	String formType = request.getParameter("Type");
	String foldID = request.getParameter("FoldID");
	String featID = request.getParameter("FeatID");
	String sampID = request.getParameter("SampID");
	String recID = request.getParameter("RecID");
	Folder folder = new Folder(Integer.parseInt(foldID), user, state);
	DataEntryForm dataEntryForm = null;
	
	try {
		if (request.getParameter("Err") != null) {
			dataEntryForm = (DataEntryForm) session.getAttribute("dataEntryForm");
		} else if (request.getParameter("CopyID") != null) {
			dataEntryForm = (DataEntryForm) session.getAttribute("dataEntryForm");
			dataEntryForm.copyFrom(Integer.parseInt(request.getParameter("CopyID")));
		} else {
			if (formType.equals(Feature.OUTCROP_LOCALITY) || formType.equals(Feature.DRILLHOLE_LOCALITY) || formType.equals(Feature.VERTICAL_SECTION_LOCALITY)) {
				if (featID != null) { //editing
					dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
				} else {
					dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(formType, user, Integer.parseInt(foldID), state);
				}
				if (request.getParameter("LoadFeatID") != null) //copying
					dataEntryForm.copyFrom(Integer.parseInt(request.getParameter("LoadFeatID")));
			} else if (formType.equals("Sample")) {
				if (sampID != null) { //editing
					dataEntryForm = DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(sampID), user, state);
				} else {
					dataEntryForm = DataEntryFormFactory.getSampleDataEntryForm(user, Integer.parseInt(featID), Integer.parseInt(foldID), state);
				}
			} else {
				if (recID != null) { //editing
					dataEntryForm = DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(recID), user, state);
				} else {
					dataEntryForm = DataEntryFormFactory.getRecordDataEntryForm(formType, user, Integer.parseInt(sampID), Integer.parseInt(foldID), state);
				}
			}
		}
	} catch (Exception e) {
		System.out.println(e.getMessage());
	}

	if (dataEntryForm != null) {
		
		//save DataEntryForm in session
		session.setAttribute("dataEntryForm", dataEntryForm);
		
		//form creation if proper rights
		if ((folder.isAllowedCreateLocalities() && featID ==  null) || (folder.isAllowedEditLocalities() && featID != null)) {

			out.println("<form name='form1' method='post' action='data_proc.jsp' />");
			out.println("<input type='hidden' name='SaveType' value='' />");
			if (featID != null) {
				session.setAttribute("dataEntryErrorRedirect", "data_entry.jsp?Err=Yes&Type=" + formType + "&FoldID=" + foldID + "&FeatID=" + featID);
			} else if (sampID != null) {
				session.setAttribute("dataEntryErrorRedirect", "data_entry.jsp?Err=Yes&Type=" + formType + "&FoldID=" + foldID + "&SampID=" + sampID);
			} else if (recID != null) {
				session.setAttribute("dataEntryErrorRedirect", "data_entry.jsp?Err=Yes&Type=" + formType + "&FoldID=" + foldID + "&RecID=" + recID);
			} else {
				session.setAttribute("dataEntryErrorRedirect", "data_entry.jsp?Err=Yes&Type=" + formType + "&FoldID=" + foldID);
			}

			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			dataEntryForm.makeNavPanelHTML(new PrintWriter(out));
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a>&nbsp;&nbsp;</td><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			dataEntryForm.makeDataEntryHTML(new PrintWriter(out));

			out.println("</td></tr></table>");
			out.println("</form>");
		}
		else {
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>Access denied</span><br />You don't have sufficient rights in this folder</p>");
		}
	}
	else {
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");
		out.println("<p><span class='bigheading'>Access denied</span><br />You don't have rights to edit this locality/record</p>");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
