<%@		page extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, java.io.*, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, java.text.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, nz.cri.gns.util.map.*"
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
	DataEntryForm dataEntryForm;
		
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

	if (dataEntryForm != null) {
		
		//save DataEntryForm in session
		session.setAttribute("dataEntryForm", dataEntryForm);
		
		//form creation if proper rights
		if ((folder.isAllowedCreateLocalities() && featID ==  null) || (folder.isAllowedEditLocalities() && featID != null)) {

			out.println("<form name='form1' method='post' action='data_proc.jsp'>");
			out.println("<input type='hidden' name='Type' value='" + formType + "'>");
			out.println("<input type='hidden' name='FoldID' value='" + foldID + "'>");
			if (featID != null) out.println("<input type='hidden' name='FeatID' value='" + featID + "'>");
			if (sampID != null) out.println("<input type='hidden' name='SampID' value='" + sampID + "'>");
			if (recID != null) out.println("<input type='hidden' name='RecID' value='" + recID + "'>");
			out.println("<input type='hidden' name='SaveType' value=''>");

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

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
