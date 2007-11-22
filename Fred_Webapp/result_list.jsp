<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.query.FREDQuery"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="java.util.List"
%><%@page import="java.util.Vector"
%><%@page import="java.util.Collections"
%><%@page import="java.net.URLEncoder"
%><%@page import="java.sql.Connection"
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
	User user = (User)getUser(session);

	String queryURL = request.getParameter("QueryURL");
	if (queryURL == null)
		queryURL = "simple_query.jsp";
	
	FeatureUtil featureUtil = new FeatureUtil(HibernateUtil.get().getDAOFactory());
	AuditUtil auditUtil = new AuditUtil(HibernateUtil.get().getDAOFactory());
	
	int pageSize = 50;

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	addButtons(et, new IconnedLink[] {
			new IconnedLink(queryURL, "images/search.gif", "Search Again"),
			new IconnedLink("export_setup.jsp", "images/save.gif", "Download Results")
		});

	drawTop(out, et, request, response);

	if ((request.getParameter("WhereSQL") != null && request.getParameter("TableName") != null && request.getParameter("QueryString") != null) || request.getParameter("Page") != null || request.getParameter("Type") != null) {
		String whereSQL = request.getParameter("WhereSQL");
		String tableName = request.getParameter("TableName");
		String queryString = request.getParameter("QueryString");
		
		int pageNum = 1;
		if (request.getParameter("Page") != null)
			pageNum = Integer.parseInt(request.getParameter("Page"));
		boolean useStored = (request.getParameter("Page") != null);

		session.setAttribute("dataEntryRedirect", "result_list.jsp?Page=" + pageNum);

		List<Feature> features = null;
		if (useStored) {
			features = (List<Feature>) session.getAttribute("FRED.features");
			queryString = (String) session.getAttribute("FRED.queryString");
		} else 	if ("Adv".equals(request.getParameter("Type"))) {
			try {
				FREDQuery query = FREDUtil.getFREDQuery(state);
				whereSQL = query.getHQLQuery();
				//System.out.println(whereSQL);
				queryString = query.getQueryAsString();
				features = featureUtil.getListFromQueryBuilder(whereSQL);
				auditUtil.addLogEntry(AuditUtil.QUERY_LOG_TYPE, user, features.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			features = new Vector<Feature>();
			//System.out.println("SELECT fv.feature_id FROM " + tableName + " WHERE " + whereSQL);
			Connection conn = null;
			try {
				conn = FREDUtil.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery("SELECT DISTINCT fv.feature_id FROM " + tableName + " WHERE " + whereSQL);
				while (rs.next()) {
					Feature feature = featureUtil.getFeature(rs.getInt(1));
					if (feature.getAudit().getStatus().equals(FREDConstants.APPROVED))
						features.add(feature);
				}
				Collections.sort(features);
				rs.close();
				statement.close();
				conn.close();
				auditUtil.addLogEntry(AuditUtil.QUERY_LOG_TYPE, user, features.size());
			} finally {
				if (conn != null) try {
					conn.close();
				} catch (Exception _e) {
				}
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

			//list matching localities
			%><p><%
			startDETable(pageContext);
			%><table width="600" border="0">
			<tr><td class="deHeading" colspan="5">Matching Localities</td></tr>
			<tr><td colspan="5">Search Criteria: <em><%=queryString%></em></td></tr><%
			if (maxRangePage > 1) {
				%><tr><td></td></tr>
				<tr><td class="heading" colspan="4">Displaying records <%=startIndex%> to <%=endIndex%> of <%=numRecords%></td>
				<td style="text-align: right"><%
				for (int i = minRangePage; i <= maxRangePage; i++) {
					%>&nbsp;<a href="result_list.jsp?Page=<%=i%>"<%=((i == pageNum) ? " class=\"heading\"" : "")%>><%=i%></a><%
				}
			}
			%></td></tr>

			<tr><th>FR Number&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Yard FR Number&nbsp;&nbsp;</th><th>Name&nbsp;&nbsp;</th><th>Actions</th></tr><%
			int j = 1;
			for (Feature feature : features) {
				if (j >= startIndex && j <= endIndex) {
					%><tr><td class="heading"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1")%>&backText=Back%20To%20Result%20List"><%=FeatureUtil.getFeatureIdentifyingName(feature)%></a>&nbsp;&nbsp;</td>
					<td><%=feature.getFeatureType()%>&nbsp;&nbsp;</td><td><%=((feature.getYardFrNumber() != null) ? feature.getYardFrNumber().getFrNumber() : "")%>&nbsp;&nbsp;</td>
					<td><%=DBUtils.nvl(feature.getFeatureName())%>&nbsp;&nbsp;</td>
					<td><a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1")%>&backText=Back%20To%20Result%20List"><img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" /></a>&nbsp;&nbsp;<%
					if (user != null && featureUtil.isAllowedEditApprovedFeature(user, feature)) {
						%><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=feature.getFeatureId()%>&FoldID=<%=feature.getMasterFile().getFolderId()%>"><img src="images/edit.gif" height="20" width="20" border="0" alt="Edit" /></a><%
					}
					%></td><%
				}
				j++;
				%></tr><%
			}

			if (maxRangePage > 1) {
				%><tr><td></td></tr>
				<tr><td class="heading" colspan="4">Displaying records <%=startIndex%> to <%=endIndex%> of <%=numRecords%></td>
				<td align="right"><%
				for (int i = minRangePage; i <= maxRangePage; i++) {
					%>&nbsp;<a href="result_list.jsp?Page=<%=i%>"<%=((i == pageNum) ? " class=\"heading\"" : "")%>><%=i%></a><%
				}
			}
			%></td></tr>
			</table><%
			endDETable(pageContext);
			%></p><%
		} else {
			%><p>No records found matching your search criteria</p><%
		}
	}
	
	%></td></tr></table><%
	drawBottom(out, et);

%>
