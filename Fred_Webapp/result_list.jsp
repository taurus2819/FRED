<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.FREDUtils"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.intranet.DBConnection"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="java.util.Collections"
%><%@page import="java.util.TreeSet"
%><%@page import="java.sql.Statement"
%><%@page import="java.sql.ResultSet"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Search Results";
	}
%><%
	PageState state = new PageState(request, response, getServletContext());
	DBConnection connection = FREDUtils.getFREDConnection(state);
	Statement statement = connection.statement;
	User user = (User)getUser(session);

	FeatureUtil featureUtil = new FeatureUtil(HibernateUtil.get().getDAOFactory());
	
	int pageSize = 50;

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	%><table style="margin-left:20px; margin-top:20px; width:150px;" border="0">
	<tr><td colspan="2" align="center"><img src="images/mult_loc.gif" height="20" width="20" /></td></tr>
	<tr><td colspan="2" class="bigheading" align="center">Search Results</td></tr>
	<tr><td><img src="images/blank.gif" width="1" height="10" /></td></tr><%
	if (request.getParameter("FoldID") != null) {
		%><tr><td><a href="simple_query.jsp?FoldID=<%=request.getParameter("FoldID")%>" title="Search Again"><img src="images/search.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="simple_query.jsp?FoldID=<%=request.getParameter("FoldID")%>" class="heading">Search Again</a></td></tr><%
	} else {
		%><tr><td><a href="simple_query.jsp" title="Search Again"><img src="images/search.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="simple_query.jsp" class="heading">Search Again</a></td></tr><%
	}
	%></table><%

	drawEndNavigation(out);

	%><table style="margin-left:20px; width:550px;" border="0">
	<tr><td><%

	if ((request.getParameter("WhereSQL") != null && request.getParameter("TableName") != null && request.getParameter("QueryString") != null) || request.getParameter("Page") != null) {
		String whereSQL = request.getParameter("WhereSQL");
		String tableName = request.getParameter("TableName");
		String queryString = request.getParameter("QueryString");
		
		//System.out.println("TableName: " + tableName + " * WhereSQL: " + whereSQL);
		
		int pageNum = 0;
		if (request.getParameter("Page") != null)
			pageNum = Integer.parseInt(request.getParameter("Page"));
		boolean useStored = (request.getParameter("Page") != null);

		session.setAttribute("dataEntryRedirect", "result_list.jsp?Page=" + pageNum);

		TreeSet<Feature> features = new TreeSet<Feature>();
		if (useStored) {
			features = (TreeSet<Feature>) session.getAttribute("FRED.features");
			queryString = (String) session.getAttribute("FRED.queryString");
		} else {
			//System.out.println("SELECT DISTINCT fv.feature_id FROM " + tableName + " WHERE " + whereSQL);
			ResultSet rs = statement.executeQuery("SELECT DISTINCT fv.feature_id FROM " + tableName + " WHERE " + whereSQL);
			while (rs.next()) {
				Feature feature = featureUtil.getFeature(rs.getInt(1));
				if (feature.getAudit().getStatus().equals(FREDConstants.APPROVED))
					features.add(feature);
			}
		}
		int numRecords = features.size();
		if (numRecords > 0) {

			//save QueryRes vector
			session.setAttribute("FRED.features", features);
			session.setAttribute("FRED.queryString", queryString);

			//Navigation
			int startIndex = (pageNum - 1) * pageSize + 1;
			int endIndex = Math.min(numRecords, startIndex + pageSize - 1);

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
			%><table border="0" width="400">
			<tr><td colspan="2">Search Criteria: <em>" + queryString + "</em></td></tr><%
			if (maxRangePage > 1) {
				%><tr><td></td></tr>
				<tr><td class="heading">Displaying records <%=startIndex%> to <%=endIndex%> of <%=numRecords%></td>
				<td align="right"><%
				for (int i = minRangePage; i <= maxRangePage; i++) {
					%>&nbsp;<a href="result_list.jsp?Page=<%=i%>"<%=((i == pageNum) ? " class=\"heading\"" : "")%>><%=i%></a><%
				}
			}
			%></td></tr>
			</table>

			<table border="0" cellspacing="0" cellpadding="3" width="400">
			<tr><th>FR Number&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Yard FR Number&nbsp;&nbsp;</th><th>Field Number/<br />Drillhole Name&nbsp;&nbsp;</th></tr><%
			int i = 0;
			for (Feature feature : features) {
				if (++i > startIndex && i <= endIndex) {
					%><tr><td class="heading"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>"><%=FeatureUtil.getFeatureIdentifyingName(feature)%></a>&nbsp;&nbsp;</td><td><%=feature.getFeatureType()%>&nbsp;&nbsp;</td><td><%=((feature.getYardFrNumber() != null) ? feature.getYardFrNumber().getFrNumber() : "")%>&nbsp;&nbsp;</td><td><%=DBUtils.nvl(feature.getFeatureName())%>&nbsp;&nbsp;</td><%
					if (featureUtil.isAllowedEditApprovedFeature(user, feature)) {
						%><td><a href="data_entry.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=feature.getFeatureId()%>"><img src="images/edit.gif" height="20" width="20" border="0" alt="Edit" /></a></td><%
					}
				}
				%></tr><%
			}
			%></table><%

			%><table border="0" width="400">
			<tr><td colspan="2">Search Criteria: <em>" + queryString + "</em></td></tr><%
			if (maxRangePage > 1) {
				%><tr><td></td></tr>
				<tr><td class="heading">Displaying records <%=startIndex%> to <%=endIndex%> of <%=numRecords%></td>
				<td align="right"><%
				for (int i = minRangePage; i <= maxRangePage; i++) {
					%>&nbsp;<a href="result_list.jsp?Page=<%=i%>"<%=((i == pageNum) ? " class=\"heading\"" : "")%>><%=i%></a><%
				}
			}
			%></td></tr>
			</table>

		}
		else {
			%><p>No records found matching your search criteria</p><%
		}
	}
	
	%></td></tr></table><%
	drawBottom(out, et);

%>
