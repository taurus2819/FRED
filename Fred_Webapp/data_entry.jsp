<%@		page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, java.io.*, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, java.text.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, nz.cri.gns.util.map.*"
%><%
	PageState state = new PageState(request, response, getServletContext());
	User user = getUser(session);
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
		if (formType.equals("Outcrop") || formType.equals("Drillhole") || formType.equals("VertSect")) {
			if (request.getParameter("LoadFeatID") != null) { //copying
				if (featID == null) {
					dataEntryForm = DataEntryFormFactory.copyLocalityDataEntryForm(Integer.parseInt(request.getParameter("LoadFeatID")), user, Integer.parseInt(foldID), state);
				} else {
					dataEntryForm = DataEntryFormFactory.copyLocalityDataEntryForm(Integer.parseInt(request.getParameter("LoadFeatID")), Integer.parseInt(featID), user, state);
				}
			} else if (featID != null) { //editing
				dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
			} else {
				dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(formType, user, Integer.parseInt(foldID), state);
			}
		} else {
			if (recID != null) { //editing
				dataEntryForm = DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(recID), user, state);
			} else {
				dataEntryForm = DataEntryFormFactory.getRecordDataEntryForm(formType, user, Integer.parseInt(sampID), Integer.parseInt(foldID), state);
			}
		}
	} catch (Exception e) {}

	if (dataEntryForm != null) {
		
		//save DataEntryForm in session
		session.setAttribute("dataEntryForm", dataEntryForm);
		
		//form creation if proper rights
		if ((folder.isAllowedCreateLocalities() && featID ==  null) || (folder.isAllowedEditLocalities() && featID != null)) {

			out.println("<form name='form1' method='post' action='data_proc.jsp' />");
			out.println("<input type='hidden' name='SaveType' value='' />");
			if (request.getParameter("Redirect") != null) 
				out.println("<input type='hidden' name='Redirect' value='" + request.getParameter("Redirect") + "' />");

			dataEntryForm.makeNavPanelHTML(new PrintWriter(out));

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
