<%@		page extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, java.text.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, nz.cri.gns.util.map.*"
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
	DBConnection conn = FREDUtils.getFREDConnection(state);
	User user = getUser(session);
	ComboDescriptor cd;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);
%>
<script language="JavaScript">

function setAccuracy(datID, form) {
	if (datID != "-") { form.Accuracy.value = datumMethod[datID]; }
}

</script>

<%
	if (request.getParameter("Type") != null && request.getParameter("FoldID") != null) {
		String featType = request.getParameter("Type");
		String foldID = request.getParameter("FoldID");
		String featID = request.getParameter("FeatID");
		Folder folder = new Folder(Integer.parseInt(foldID), user, state);
		
		Locality locality;
		if (request.getParameter("LoadFeatID") != null) { //copying
			if (featID == null) {
				locality = LocalityFactory.copyLocality(Integer.parseInt(request.getParameter("LoadFeatID")), user, Integer.parseInt(foldID), state);
			} else {
				locality = LocalityFactory.copyLocality(Integer.parseInt(request.getParameter("LoadFeatID")), Integer.parseInt(featID), user, state);
			}
		} else if (featID != null) { //editing
			locality = LocalityFactory.getLocality(Integer.parseInt(featID), user, state);
		} else {
			locality = LocalityFactory.getLocality(featType, user, Integer.parseInt(foldID), state);
		}

		//form creation if proper rights
		if ((folder.isAllowedCreateLocalities() && featID ==  null) || (folder.isAllowedEditLocalities() && featID != null)) {

			out.println("<form name='form1' method='get' action='feat_data_proc.jsp'>");
			out.println("<input type='hidden' name='Type' value='" + featType + "'>");
			out.println("<input type='hidden' name='FoldID' value='" + foldID + "'>");
			out.println("<input type='hidden' name='FeatID' value='" + featID + "'>");
			out.println("<input type='hidden' name='SaveType' value=''>");

			//build array of datum methods
			ResultSet rs = conn.executeQuery("SELECT MAX(Method_ID) FROM SC.Method");
			rs.next();
			out.println("<script language='JavaScript'>var datumMethod = new Array(" + (rs.getInt(1) + 1) + ");");
			rs = conn.executeQuery("SELECT Method_ID, Nom_Accuracy_XY FROM SC.Method WHERE Nom_Accuracy_XY IS NOT NULL ORDER BY Method_ID");
			while (rs.next()) {
				out.println("datumMethod[" + rs.getString(1) + "] = '" +FREDUtils.noNulls(rs.getString(2)) + "';");
			}
			out.println("</script>");
			conn.releaseStatement();

			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.print("<tr><td colspan='2' align='center' class='heading'>");
			if (featType.equals("Outcrop")) {
				out.println("Outcrop");
			} else if (featType.equals("Drillhole")) {
				out.println("Drillhole");
			} else if (featType.equals("VertSect")) {
				out.println("Vertical Section");
			}
			out.println(" Locality</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='load_record.jsp?FoldID=" + foldID + "&FeatID=" + featID + "&RecType=" + featType + "'><img src='images/load.gif' height='20' width='20' border='0' alt='Copy From' /></a>&nbsp;&nbsp;</td><td><a href='load_record.jsp?FoldID=" + foldID + "&FeatID=" + featID + "&RecType=" + featType + "' class='boldlink'>Copy From</a></td></tr>");
			out.println("<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>");
			if (folder.isAllowedSubmitLocalities() && !featType.equals("Outcrop")) {
				out.println("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database' /></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>");
			}
			out.println("<tr><td><a href='folder_detail.jsp?ID=" + foldID + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a>&nbsp;&nbsp;</td><td><a href='folder_detail.jsp?ID=" + foldID + "' class='boldlink'>Quit</a></td></tr>");
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			out.println("<table border='0' cellspacing='0' cellpadding='2'>");
			if (featType.equals("Outcrop")) {
				out.println("<tr><td class='heading' colspan='2'>Field Number</td><td><input type='text' name='FeatName' value='" + FREDUtils.noNulls(locality.getField(Locality.FIELD_NUMBER)) + "'></td></tr>");
			} else if (featType.equals("Drillhole")) {
				out.println("<tr><td class='heading' colspan='2'>Drillhole Name</td><td><input type='text' name='FeatName' value='" + FREDUtils.noNulls(locality.getField(Locality.DRILLHOLE_NAME)) + "'></td></tr>");
			} else if (featType.equals("VertSect")) {
				out.println("<tr><td class='heading' colspan='2'>Section Name</td><td><input type='text' name='FeatName' value='" + FREDUtils.noNulls(locality.getField(Locality.SECTION_NAME)) + "'></td></tr>");
			}
			out.println("<tr><td class='heading'>Registration Area</td><td></td><td>");
			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Name");
			cd.name = "RegAreaID";
			cd.selected = locality.getField(Locality.REGISTRATION_AREA);
			cd.join = "FieldName = 'RegArea'";
			cd.orderBy = "Lookup_ID";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
			out.println("</td></tr>");
			out.print("<tr><td class='heading'>");
			if (featType.equals("Drillhole")) {
				out.print("Sidetrack of");
			} else {
				out.print("Recollection of");
			}
%>
			</td><td></td><td><input type='text' name='Recoll' value='<%=FREDUtils.noNulls(locality.getField(Locality.RECOLLECTION))%>' /></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Recoll", "Supp", "width=600,height=500");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading' colspan='2'>Working Comments<br><span class='smalltext'>On submission these comments will be deleted</span></td><td><textarea name='WorkComm' rows='3' cols='40'><%=FREDUtils.noNulls(locality.getField(Locality.WORKING_COMMENTS))%></textarea></td></tr>

			<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>

			<tr><td class='heading'>Location</td><td class='smallheading'>Grid Ref.</td><td><input type='text' name='GridRef' size='40' value='<%=FREDUtils.noNulls(locality.getField(Locality.GRID_REF))%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Coord", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td></td><td class='smallheading'>Method</td><td>
<%			cd = new ComboDescriptor("SC.Method", "Method_ID", "Method");
			cd.name = "LocMethodID";
			cd.prompt = "-- Choose --";
			cd.selected = locality.getField(Locality.METHOD);
			cd.orderBy = "Method_ID";
			cd.join = "Nom_Accuracy_XY IS NOT NULL";
			cd.tagParams = "onChange='setAccuracy(this.value, this.form)'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
%>
			</td></tr>
			<tr><td></td><td class='smallheading'>Accuracy</td><td><input type='text' name='Accuracy' value='<%=FREDUtils.noNulls(locality.getField(Locality.ACCURACY))%>'></td></tr>
			<tr><td></td><td class='smallheading'>Locality<br />Description</td><td><textarea name='Loc' cols='40' rows='5'><%=FREDUtils.noNulls(locality.getField(Locality.LOCALITY_DESC))%></textarea></td></tr>
<%			if (featType.equals("Drillhole")) {	%>
				<tr><td class='heading'>Operating Company</td><td></td><td><input type='text' name='Person' value='<%=FREDUtils.noNulls(locality.getField(Locality.OPERATING_COMPANY))%>' size='40'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=OpComp", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
				<tr><td class='heading'>Drilling Dates</td><td class='smallheading'>Spud Date</td><td><input type='text' name='StartDate' value='<%=FREDUtils.noNulls(locality.getField(Locality.SPUD_DATE))%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Date&Field=StartDate", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
				<tr><td class='heading'></td><td class='smallheading'>Completion Date</td><td><input type='text' name='FinishDate' value='<%=FREDUtils.noNulls(locality.getField(Locality.COMPLETION_DATE))%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Date&Field=FinishDate", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
				<tr><td class='heading'>Licence Area</td><td></td><td><input type='text' name='LicArea' value='<%=FREDUtils.noNulls(locality.getField(Locality.LICENCE_AREA))%>' size='40'></td></tr>
				<tr><td class='heading'>Datum Elevation</td><td></td>
				<td class='smallheading'><select name='DatumType'><option value='-'<%=((locality.getField(Locality.DATUM_TYPE) == null) ? " selected" : "")%>>-- Choose --</option><option value='RT'<%=((locality.getField(Locality.DATUM_TYPE).equals("RT")) ? " selected" : "")%>>RT</option><option value='KB'<%=((locality.getField(Locality.DATUM_TYPE).equals("KB")) ? " selected" : "")%>>KB</option></select>&nbsp;&nbsp;
				<input type='text' name='DatumEl' value='<%=FREDUtils.noNulls(locality.getField(Locality.DATUM_ELEVATION))%>' size='10'>&nbsp;m&nbsp;asl</td></tr>
				<tr><td class='heading'>Drillhole Depths</td><td class='smallheading'>Kick-off</td><td class='smallheading'><input type='text' name='StartDepth' value='<%=FREDUtils.noNulls(locality.getField(Locality.KICK_OFF_DEPTH))%>'>&nbsp;m</td></tr>
				<tr><td class='heading'></td><td class='smallheading'>Termination (TD)</td><td class='smallheading'><input type='text' name='FinishDepth' value='<%=FREDUtils.noNulls(locality.getField(Locality.TERMINATION_DEPTH))%>'>&nbsp;m</td></tr>		
<%			} else if (featType.equals("VertSect")) {	%>
				<tr><td class='heading'>Section Collector</td><td></td><td><input type='text' name='Person' value='<%=FREDUtils.noNulls(locality.getField(Locality.SECTION_COLLECTOR))%>' size='40'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=VertPerson", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
				<tr><td class='heading'>Sampling Dates</td><td class='smallheading'>Start Date</td><td><input type='text' name='StartDate' value='<%=FREDUtils.noNulls(locality.getField(Locality.START_DATE))%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Date&Field=StartDate", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
				<tr><td class='heading'></td><td class='smallheading'>Completion Date</td><td><input type='text' name='FinishDate' value='<%=FREDUtils.noNulls(locality.getField(Locality.COMPLETION_DATE))%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Date&Field=FinishDate", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
				<tr><td class='heading'>Datum Elevation</td><td></td>
				<td class='smallheading'><select name='DatumType'><option value='-'<%=((locality.getField(Locality.DATUM_TYPE).equals("")) ? " selected" : "")%>>-- Choose --</option><option value='Top'<%=((locality.getField(Locality.DATUM_TYPE).equals("Top")) ? " selected" : "")%>>Top</option><option value='Bottom'<%=((locality.getField(Locality.DATUM_TYPE).equals("Bottom")) ? " selected" : "")%>>Bottom</option></select>&nbsp;&nbsp;
				<input type='text' name='DatumEl' value='<%=FREDUtils.noNulls(locality.getField(Locality.DATUM_ELEVATION))%>' size='10'>&nbsp;m&nbsp;asl</td></tr>
				<tr><td class='heading'>Section Heights</td><td class='smallheading'>Top Horizon</td><td class='smallheading'><input type='text' name='StartDepth' value='<%=FREDUtils.noNulls(locality.getField(Locality.TOP_HORIZON))%>'>&nbsp;m</td></tr>
				<tr><td class='heading'></td><td class='smallheading'>Base Horizon</td><td class='smallheading'><input type='text' name='FinishDepth' value='<%=FREDUtils.noNulls(locality.getField(Locality.BASE_HORIZON))%>'>&nbsp;m</td></tr>				
				<input type='hidden' name='LicArea' value='' />
<%			}	%>
			</table>
<%			out.println("<table border='0' cellpadding='0' cellspacing='2'>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>");
			if (folder.isAllowedSubmitLocalities() && !featType.equals("Outcrop")) {
				out.println("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database'/></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>");
			}
			out.println("</table>");
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
