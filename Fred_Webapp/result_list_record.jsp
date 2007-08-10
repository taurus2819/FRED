<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.query.FREDRecordQuery"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="java.util.List"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Search Results";
	}
%><%
	PageState state = new PageState(request, response, getServletContext());
	User user = (User)getUser(session);

	RecordUtil recordUtil = new RecordUtil(HibernateUtil.get().getDAOFactory());
	AuditUtil auditUtil = new AuditUtil(HibernateUtil.get().getDAOFactory());

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	
	drawTop(out, et, request, response);

	if ((request.getParameter("WhereSQL") != null && request.getParameter("TableName") != null && request.getParameter("QueryString") != null) || request.getParameter("Page") != null || request.getParameter("Type") != null) {
		String whereSQL = request.getParameter("WhereSQL");
		String queryString = request.getParameter("QueryString");
		

		List<Record> records = null;
		try {
			FREDRecordQuery query = FREDUtil.getFREDRecordQuery(state);
			whereSQL = query.getHQLQuery();
			queryString = query.getQueryAsString();
			System.out.println(whereSQL);
			records = recordUtil.getListFromQueryBuilder(whereSQL);
			//auditUtil.addLogEntry(AuditUtil.QUERY_LOG_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (records.size() > 0) {

			//list matching localities
			%><p><%
			startDETable(pageContext);
			%><table width="600" border="0">
			<tr><td class="deHeading" colspan="5">Matching Localities</td></tr>
			<tr><td colspan="5">Search Criteria: <em><%=queryString%></em></td></tr>


			<tr><th>FR Number&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Yard FR Number&nbsp;&nbsp;</th><th>Name&nbsp;&nbsp;</th><th>Actions</th></tr><%
			for (Record record : records) {
				%><tr><td><%=FeatureUtil.getFeatureIdentifyingName(record.getSample().getFeature())%></td></tr><%
			}


			%></table><%
			endDETable(pageContext);
			%></p><%
		} else {
			%><p>No records found matching your search criteria</p><%
		}
	}
	
	%></td></tr></table><%
	drawBottom(out, et);

%>
