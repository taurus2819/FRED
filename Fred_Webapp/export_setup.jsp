<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.jsp.*, nz.cri.gns.db.*, java.sql.*, java.text.*, java.net.*, nz.cri.gns.auth.*, java.lang.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	ResultSet rs, rs2;
	String whereSQL, tableName, redir;
	int i=0, j=3;
	User user = getUser(session);
	int userID = 0;

	if (user != null) { userID = user.getPersonId(); }

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
	out.println("<tr><td class='heading'><a href='result_list.jsp'>Back to Sample List</a></td></tr>");
	out.println("<tr><td class='heading'><a href='simple_query.jsp'>Search Again</a></td></tr>");
	out.println("</table>");

	drawEndNavigation(out);

	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");

	if (request.getParameter("WhereSQL") != null) {
		whereSQL = request.getParameter("WhereSQL");
	
		out.println("<form method='get' action='export_setup2.jsp' name='exportForm'>");

		out.println("<table border='0' cellspacing='0'>");
		out.println("<tr><td><input type='checkbox' name='LocCheck' checked />&nbsp;&nbsp;</td><td class='heading'>Locality Data Only</td></tr>");
		out.println("<tr><td><input type='checkbox' name='SmpCheck' />&nbsp;&nbsp;</td><td class='heading'>Sample Property Data (incl. Locality Data)</td></tr>");
		out.println("<tr><td><input type='checkbox' name='AdoCheck' />&nbsp;&nbsp;</td><td class='heading'>Adoption Data (incl. Locality Data)</td></tr>");
		out.println("<tr><td><input type='checkbox' name='PalCheck' />&nbsp;&nbsp;</td><td class='heading'>Paleontology Data (incl. Locality Data)</td></tr>");
		out.println("<tr><td></td><td class='heading'><input type='checkbox' name='TaxaCheck' />&nbsp;&nbsp;Taxonomic Lists</td></tr>");
		out.println("</table>");
		out.println("<input type='hidden' name='WhereSQL' value=\"" + request.getParameter("WhereSQL") + "\" />");
		out.println("<p><input type='submit' value='Export' /></form></p>");
		out.println("</form>");

	} else {
		out.println("<p><span class='heading'>No query entered</span></p>");
	}


	out.println("</td></tr>");
	out.println("</table>");
	drawBottom(out, et);
%>
