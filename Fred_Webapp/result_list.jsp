<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, java.util.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	ResultSet rs, rs2;
	User user = getUser(session);
	String whereSQL, tableName, queryString, featID;
	StringBuffer featIDs;
	int numRecords, userID = 0, startIndex, endIndex, pageNum = 1;
	boolean useStored;
	Vector queryRes;

	int pageSize=50;

	if (user != null) { userID = user.getPersonId(); }

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
	out.println("<tr><td colspan='2' align='center'><img src='images/mult_loc.gif' height='20' width='20' /></td></tr>");
	out.println("<tr><td colspan='2' class='bigheading' align='center'>Search Results</td></tr>");
	out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
	if (request.getParameter("FoldID") != null) {
		out.println("<tr><td><a href='simple_query.jsp?FoldID=" + request.getParameter("FoldID") + "' title='Search Again'><img src='images/search.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='simple_query.jsp?FoldID=" + request.getParameter("FoldID") + "' class='heading'>Search Again</a></td></tr>");
	} else {
		out.println("<tr><td><a href='simple_query.jsp' title='Search Again'><img src='images/search.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='simple_query.jsp' class='heading'>Search Again</a></td></tr>");
	}
	out.println("</table>");

	drawEndNavigation(out);

	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");

	if ((request.getParameter("WhereSQL") != null && request.getParameter("TableName") != null && request.getParameter("QueryString") != null) || request.getParameter("Page") != null) {
		whereSQL = request.getParameter("WhereSQL");
		tableName = request.getParameter("TableName");
		queryString = request.getParameter("QueryString");
		
		if (request.getParameter("Page") != null) { pageNum = Integer.parseInt(request.getParameter("Page")); }
		useStored = (request.getParameter("Page") != null);

		if (useStored) {
			queryRes = (Vector) session.getAttribute("QueryRes");
			queryString = (String) session.getAttribute("QueryString");
		} else {
			queryRes = new Vector();
			rs = statement.executeQuery("SELECT DISTINCT Feature_ID, Sample_Name FROM " + tableName + " WHERE " + whereSQL + " ORDER BY Sample_Name");
			while (rs.next()) {
				queryRes.add(rs.getString(1));
			}
		}
		numRecords = queryRes.size();
		if (numRecords > 0) {

			//save QueryRes vector
			session.setAttribute("QueryRes", queryRes);
			session.setAttribute("QueryString", queryString);

			//Navigation
			startIndex = (pageNum - 1) * pageSize + 1;
			endIndex = Math.min(numRecords, startIndex + pageSize - 1);

			//Set pages to list
			int startPage = 1;
			int endPage = (int) Math.ceil(numRecords / (float) pageSize);
			int minRangePage = pageNum - 3;
			int maxRangePage = pageNum + 3;
			//Bring bottom up
			if (minRangePage < startPage) {
				maxRangePage += (startPage - minRangePage);
				minRangePage = startPage;
			}
			//Pull top down
			if (maxRangePage > endPage) {
				minRangePage = Math.max(startPage, minRangePage - maxRangePage + endPage);
				maxRangePage = endPage;
			}

			//list matching records
			out.println("<p><table border='0' width='400'>");
			out.println("<tr><td colspan='2'>Search Criteria: <em>" + queryString + "</em></td></tr>");
			if (maxRangePage > 1) {
				out.println("<tr><td></td></tr>");
				out.println("<tr><td class='heading'>Displaying records " + startIndex + " to " + endIndex + " of " + numRecords + "</td>");
				out.print("<td align='right'>");
				for (int i = minRangePage; i <= maxRangePage; i++) {
					out.print("&nbsp;<a href='result_list.jsp?Page=" + i + "'");
					if (i == pageNum) { out.print(" class='heading'"); }
					out.print(">" + i + "</a>");
				}
			}
			out.println("</td></tr>");
			out.println("</table></p>");

			out.println("<p><table border='0' cellspacing='0' cellpadding='3' width='400'>");
			out.print("<tr><th>FR Number&nbsp;&nbsp;</th><th>Yard FR Number&nbsp;&nbsp</th><th>Drillhole Name&nbsp;&nbsp;</th></tr>");
			Iterator it = queryRes.iterator();
			try {
				for (int i = 0; i < startIndex - 1; i++) { it.next(); }
			} catch (Exception e) {
			}
			featIDs = new StringBuffer((String) it.next());
			for (int i = startIndex + 1; i <= endIndex  && it.hasNext(); i++) {
				featIDs.append("," + (String) it.next());
			}

			rs = statement.executeQuery("SELECT DISTINCT Feature_ID, Sample_Name, Drillhole_Name, Yard_FR_Number FROM Sample_View WHERE Feature_ID IN (" + featIDs + ") ORDER BY Sample_Name");
			while (rs.next()) {
				out.print("<tr>");
				if (rs.getString(3) != null) { //drillhole
					out.print("<td><a href='detail.jsp?FeatID=" + rs.getString(1) + "' class='heading'>" + rs.getString(2) + "</a>&nbsp;&nbsp;</td><td>" + noNulls(rs.getString(4)) + "&nbsp;&nbsp;</td><td><a href='detail.jsp?FeatID=" + rs.getString(1) + "' class='heading'>" + rs.getString(3) + "</a></td>");
				} else {
					rs2 = statement2.executeQuery("SELECT Sample_ID FROM Sample_View WHERE Feature_ID = " + rs.getString(1));
					rs2.next();
					out.print("<td><a href='detail.jsp?ID=" + rs2.getString(1) + "' class='heading'>" + rs.getString(2) + "</a>&nbsp;&nbsp;</td><td>" + noNulls(rs.getString(4)) + "&nbsp;&nbsp;</td>");
				}
				out.print("</tr>");
			}
			out.println("</table></p>");

			//list matching records
			if (maxRangePage > 1) {
				out.println("<p><table border='0' width='400'>");
				out.println("<tr><td class='heading'>Displaying records " + startIndex + " to " + endIndex + " of " + numRecords + "</td>");
				out.print("<td align='right'>");
				for (int i = minRangePage; i <= maxRangePage; i++) {
					out.print("&nbsp;<a href='result_list.jsp?Page=" + i + "'");
					if (i == pageNum) { out.print(" class='heading'"); }
					out.print(">" + i + "</a>");
				}
				out.println("</td></tr>");
				out.println("</table></p>");
			}

		}
		else {
			out.println("<p>No records found matching your search criteria</p>");
		}
	}
	
	out.println("</td></tr></table>");
	drawBottom(out, et);

	statement2.close();
%>
