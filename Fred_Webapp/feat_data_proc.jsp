<%@		page extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.io.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, nz.cri.gns.util.map.*"
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

	if (request.getParameter("Type") != null && request.getParameter("FoldID") != null && request.getParameter("SaveType") != null && request.getParameter("FeatID") != null) {

		String featType = request.getParameter("Type");
		String foldID = request.getParameter("FoldID");
		String featID = request.getParameter("FeatID");
		String saveType = request.getParameter("SaveType");



		if (featType.equals("VertSect")) { featType = "Vertical Section"; }

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='heading'>Locality</td></tr>");
		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("<tr><td><a href='javascript:history.back();' title='Back to Data Entry'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='javascript:history.back();' class='heading'>Back to Data Entry</a></td></tr>");
		out.println("<tr><td><a href='folder_detail.jsp?ID=" + foldID + "' title='Quit Without Saving'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_detail.jsp?ID=" + foldID + "' class='heading'>Quit</a></td></tr>");
		out.println("</table>");

		drawEndNavigation(out);

		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		try {
		
			Locality locality;
			if (featID != null) {
				locality = LocalityFactory.getLocality(Integer.parseInt(featID), user, state);
			} else {
				locality = LocalityFactory.getLocality(featType, user, Integer.parseInt(foldID), state);
			}
			
			locality.setField(Locality.FEATURE_NAME, request.getParameter("FeatName"));
			locality.setField(Locality.REGISTRATION_AREA, request.getParameter("RegAreaID"));
			locality.setField(Locality.WORKING_COMMENTS, request.getParameter("WorkComm"));
			locality.setField(Locality.GRID_REF, request.getParameter("GridRef"));
			locality.setField(Locality.METHOD, request.getParameter("LocMethodID"));
			locality.setField(Locality.ACCURACY, request.getParameter("Accuracy"));
			locality.setField(Locality.LOCALITY_DESC, request.getParameter("Loc"));
			locality.setField(Locality.RECOLLECTION, request.getParameter("Recoll"));
			if (!featType.equals("Outcrop")) {
				locality.setField(Locality.OPERATING_COMPANY, request.getParameter("Person"));
				locality.setField(Locality.START_DATE, request.getParameter("StartDate"));
				locality.setField(Locality.COMPLETION_DATE, request.getParameter("FinishDate"));
				if (featType.equals("Drillhole")) locality.setField(Locality.LICENCE_AREA, request.getParameter("LicArea"));
				locality.setField(Locality.DATUM_TYPE, request.getParameter("DatumType"));
				locality.setField(Locality.DATUM_ELEVATION, request.getParameter("DatumEl"));
				locality.setField(Locality.KICK_OFF_DEPTH, request.getParameter("StartDepth"));
				locality.setField(Locality.TERMINATION_DEPTH, request.getParameter("FinishDepth"));
			}

			if (saveType.equals("Submit")) {
				locality.submit();
			} else {
				locality.save();
			}

			//response.sendRedirect("folder_detail.jsp?ID=" + foldID);

		} catch (DataInputException e) {
			out.println("<p><div class='bigheading'>Data Error</div></p>");
			out.println("<table border='0' cellspacing='0'>");
			out.println("<tr><td class='heading'>Problem Field<img src='images/blank.gif' width='20' height='1' /></td><td>" + e.getField() + "</td></tr>");
			out.println("<tr><td class='heading'>Error</td><td>"+ e.getMessage() + "</td></tr>");
			out.println("</table>");
		} catch (InvalidCredentialsException e) {
			out.println("<p><div class='bigheading'>Access Denied</div></p>");
			out.println("<p>You do not have sufficient rights to save this record</p>");
		} catch (IOException e) {
			out.println("<p><div class='bigheading'>Error</div></p>");
			out.println("<p>Database Error</p>");
		} catch (SQLException e) {
			out.println("<p><div class='bigheading'>Error</div></p>");
			out.println("<p>Database Error</p>");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>