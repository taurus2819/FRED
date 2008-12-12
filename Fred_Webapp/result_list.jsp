<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.query.FREDQuery"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="java.util.List"
%><%@page import="java.util.Vector"
%><%@page import="java.util.HashSet"
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
	
	SampleUtil sampleUtil = new SampleUtil(FredHibernate.get().getDAOFactory());
	FeatureUtil featureUtil = new FeatureUtil(FredHibernate.get().getDAOFactory());
	AuditUtil auditUtil = new AuditUtil(FredHibernate.get().getDAOFactory());
	
	int pageSize = 50;

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);
	addButtons(et, new IconnedLink[] {
			new IconnedLink(queryURL, "images/search.gif", "Search Again"),
			new IconnedLink("export_setup.jsp", "images/save.gif", "Download Results")
		});

	drawTop(out, et, request, response);

	if ((request.getParameter("WhereSQL") != null && request.getParameter("TableName") != null && request.getParameter("QueryString") != null) || request.getParameter("Page") != null || request.getParameter("Type") != null) {

		String queryString = "";
		
		int pageNum = 1;
		if (request.getParameter("Page") != null)
			pageNum = Integer.parseInt(request.getParameter("Page"));
		boolean useStored = (request.getParameter("Page") != null);

		session.setAttribute("dataEntryRedirect", "result_list.jsp?Page=" + pageNum);

		List<Sample> samples = null;
		List<Feature> features = null;
		if (useStored) {
			samples = (List<Sample>) session.getAttribute("FRED.samples");
			features = (List<Feature>) session.getAttribute("FRED.features");
			queryString = (String) session.getAttribute("FRED.queryString");
		} else 	if ("Adv".equals(request.getParameter("Type"))) {
			try {
				FREDQuery query = FREDUtil.getFREDQuery(state);
				queryString = query.getQueryAsString();
				features = sampleUtil.getListFromHQL(query.getHQLQuery(), Feature.class);
				//features = featureUtil.getFeatures(samples);
				auditUtil.addLogEntry(AuditUtil.QUERY_LOG_TYPE, user, features.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String whereSQL = request.getParameter("WhereSQL");
			String tableName = request.getParameter("TableName");
			queryString = request.getParameter("QueryString");
			try {
				System.out.println(tableName);
				System.out.println(whereSQL);
				String sampHql = "SELECT DISTINCT s FROM " + tableName + " WHERE " + whereSQL;
				samples = sampleUtil.getListFromHQL(sampHql, Sample.class);	
				features = featureUtil.getFeatures(samples);
				auditUtil.addLogEntry(AuditUtil.QUERY_LOG_TYPE, user, features.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int numRecords = features.size();
		if (numRecords > 0) {

			//save QueryRes vector
			session.setAttribute("FRED.samples", samples);
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
			%><table border="0" cellpadding="3" cellspacing="2" width="600">
			<tr class="midColour"><th colspan="5">Matching Localities</th></tr>
			<tr class="midColour"><td colspan="5">Search Criteria: <em><%=queryString%></em></td></tr><%
			if (maxRangePage > 1) {
				%><tr class="midColour"><td class="heading" colspan="3">Displaying records <%=startIndex%> to <%=endIndex%> of <%=numRecords%></td>
				<td style="text-align: right" colspan="2"><%
				for (int i = minRangePage; i <= maxRangePage; i++) {
					%>&nbsp;<a href="result_list.jsp?Page=<%=i%>"<%=((i == pageNum) ? " class=\"heading\"" : "")%>><%=i%></a><%
				}
			}
			%></td></tr>

			<tr class="midColour"><th colspan="2">FR Number&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Name&nbsp;&nbsp;</th><th>Actions</th></tr><%
			int j = 1;
			for (Feature feature : features) {
				if (j >= startIndex && j <= endIndex) {
					%><tr class="lightColour">
					<td><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1")%>&backText=Back%20To%20Result%20List"><img src="images/loc.gif" border="0" height="20" width="20" alt="View Locality" /></a></td>
					<td class="heading"><%=feature.getFrNumber()%> <%=(feature.getYardFrNumber() != null) ? "(" + feature.getYardFrNumber() + ")" : ""%>&nbsp;&nbsp;</td>
					<td><%=feature.getFeatureType()%>&nbsp;&nbsp;</td>
					<td><%=DBUtils.nvl(feature.getFeatureName())%>&nbsp;&nbsp;</td>
					<td><a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1")%>&backText=Back%20To%20Result%20List"><img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" /></a>&nbsp;&nbsp;<%
					if (user != null && featureUtil.isAllowedEditApprovedFeature(user, feature)) {
						%><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=feature.getFeatureId()%>&FoldID=<%=feature.getMasterFile().getFolderId()%>"><img src="images/edit.gif" height="20" width="20" border="0" alt="Edit" /></a><%
					}
					%></td>
					</tr><%
					if (!FeatureUtil.OUTCROP.equals(feature.getFeatureType())) {
						feature = featureUtil.getFeature(feature.getFeatureId());
						for (Sample sample : FREDUtil.getSortedList(feature.getSamples())) {
							if (samples == null || samples.contains(sample)) {
								%><tr class="lightColour">
								<td><a href="detail.jsp?ID=<%=sample.getSampleId()%>&backURL=<%=URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1")%>&backText=Back%20To%20Result%20List"><img src="images/drill.gif" border="0" height="20" width="20" alt="View Sample" /></a></td>
								<td class="heading">&nbsp;&nbsp;&nbsp;<%=SampleUtil.getDrillHoleDepthDescription(sample)%>&nbsp;&nbsp;</td>
								<td>Sample&nbsp;&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								</tr><%
							}
						}
					}
				}
				j++;
			}

			if (maxRangePage > 1) {
				%><tr class="midColour"><td class="heading" colspan="3">Displaying records <%=startIndex%> to <%=endIndex%> of <%=numRecords%></td>
				<td style="text-align: right" colspan="2"><%
				for (int i = minRangePage; i <= maxRangePage; i++) {
					%>&nbsp;<a href="result_list.jsp?Page=<%=i%>"<%=((i == pageNum) ? " class=\"heading\"" : "")%>><%=i%></a><%
				}
			}
			%></td></tr>
			</table><%
		} else {
			%><p>No records found matching your search criteria</p><%
		}
	}
	
	drawBottom(out, et);
	try {
		FredHibernate.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>