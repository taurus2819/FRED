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
	User user = getUser(session);

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

			locality.makeDataEntryHTML(new java.io.PrintWriter(out));

			out.println("<table border='0' cellpadding='0' cellspacing='2'>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>");
			if (folder.isAllowedSubmitLocalities() && !featType.equals("Outcrop")) {
				out.println("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database'/></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>");
			}
			out.println("</table>");
			
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
