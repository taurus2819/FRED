<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, java.util.*, nz.cri.gns.auth.*"
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);
	drawEndNavigation(out);
	
	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");

	nz.cri.gns.intranet.DBConnection connection = FREDUtils.getFREDConnection(state);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	
	ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM Pal_List WHERE Taxa_ID IS NULL");
	rs.next();
	out.println("<p>Number of unmatched samples = " + rs.getString(1) + "</p>");

	String query;
	int[] types = {Types.NUMERIC};
	int[] types2 = {Types.NUMERIC, Types.NUMERIC};
	Object data[];
	data = new Object[1];
	Object data2[];
	data2 = new Object[2];
	rs = statement.executeQuery("SELECT Taxonomic_Name, Group_ID, Pal_List_ID FROM Pal_List WHERE Taxa_ID IS NULL");
	while (rs.next()) {
		if (rs.getString(1) != null) {
			String cleanedName = PaleontologyRecordDE.getCleanedName(rs.getString(1));
			if (cleanedName.equals("")) {
				query = "SELECT Taxa_ID FROM Taxonomic_Lookup WHERE Taxonomic_Name IS NULL AND Group_ID = ?";
				
			} else {
				query = "SELECT Taxa_ID FROM Taxonomic_Lookup WHERE Taxonomic_Name = " + JspUtils.sqlEscape(cleanedName) + " AND Group_ID = ?";
			}
			data[0] = new Integer(rs.getInt(2));
			ResultSet rs2 = connection.executeQuery(query, types, data);
			if (rs2.next()) {
				data2[0] = new Integer(rs2.getInt(1));
			} else {
				data2[0] = new Integer(-1);
			}
			query = "UPDATE Pal_List SET Taxa_ID = ? WHERE Pal_List_ID = ?";
			data2[1] = new Integer(rs.getString(3));
			connection.executeUpdate(query, types2, data2);
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);

%>


