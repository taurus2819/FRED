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

	if (request.getParameter("Type") != null && request.getParameter("FoldID") != null && request.getParameter("SaveType") != null) {

		String featType = request.getParameter("Type");
		String foldID = request.getParameter("FoldID");
		String featID = request.getParameter("FeatID");
		String saveType = request.getParameter("SaveType");

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='heading'>Locality</td></tr>");
		out.println("<tr><td>&nbsp;</td></tr>");
		out.println("<tr><td><a href='javascript:history.back();' title='Back to Data Entry'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='javascript:history.back();' class='heading'>Back to Data Entry</a></td></tr>");
		out.println("<tr><td><a href='folder_detail.jsp?ID=" + foldID + "' title='Quit Without Saving'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_detail.jsp?ID=" + foldID + "' class='heading'>Quit</a></td></tr>");
		out.println("</table>");

		drawEndNavigation(out);

		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		try {
			DataEntryForm dataEntryForm;

			if (featType.equals("Outcrop") || featType.equals("Drillhole") || featType.equals("VertSect")) {
				if (featID != null) {
					dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
				} else {
					dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(featType, user, Integer.parseInt(foldID), state);
				}
			} else {
				if (featID != null) {
					dataEntryForm = DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(featID), user, state);
				} else {
					dataEntryForm = DataEntryFormFactory.getRecordDataEntryForm(featType, user, 1, Integer.parseInt(foldID), state);
				}
			}

			dataEntryForm.setField(DataEntryForm.FEATURE_NAME, request.getParameter("FeatName"));
			dataEntryForm.setField(DataEntryForm.REGISTRATION_AREA, request.getParameter("RegAreaID"));
			dataEntryForm.setField(DataEntryForm.WORKING_COMMENTS, request.getParameter("WorkComm"));
			dataEntryForm.setField(DataEntryForm.SECURITY_TYPE, request.getParameter("SecType"));
			dataEntryForm.setField(DataEntryForm.GRID_REF, request.getParameter("GridRef"));
			dataEntryForm.setField(DataEntryForm.METHOD, request.getParameter("LocMethodID"));
			dataEntryForm.setField(DataEntryForm.ACCURACY, request.getParameter("Accuracy"));
			dataEntryForm.setField(DataEntryForm.LOCALITY_DESC, request.getParameter("Loc"));
			dataEntryForm.setField(DataEntryForm.RECOLLECTION, request.getParameter("Recoll"));
			dataEntryForm.setField(DataEntryForm.OPERATING_COMPANY, request.getParameter("Person"));
			dataEntryForm.setField(DataEntryForm.START_DATE, request.getParameter("StartDate"));
			dataEntryForm.setField(DataEntryForm.COMPLETION_DATE, request.getParameter("FinishDate"));
			dataEntryForm.setField(DataEntryForm.LICENCE_AREA, request.getParameter("LicArea"));
			dataEntryForm.setField(DataEntryForm.DATUM_TYPE, request.getParameter("DatumType"));
			dataEntryForm.setField(DataEntryForm.DATUM_ELEVATION, request.getParameter("DatumEl"));
			dataEntryForm.setField(DataEntryForm.KICK_OFF_DEPTH, request.getParameter("StartDepth"));
			dataEntryForm.setField(DataEntryForm.TERMINATION_DEPTH, request.getParameter("FinishDepth"));
			
			dataEntryForm.setField(DataEntryForm.COLLECTION_DATE, request.getParameter("CollDate"));

			if (saveType.equals("Submit")) {
				dataEntryForm.submit();
			} else {
				dataEntryForm.save();
			}

			response.sendRedirect("folder_detail.jsp?ID=" + foldID);

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