<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.query.FREDRecordQuery"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.db.querybuilder.QueryElement"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	FREDRecordQuery query = FREDUtil.getFREDRecordQuery(getPageState(request, response));
	if (query != null) {
		query.removeQueryElement(Integer.parseInt(request.getParameter("index")));

		if (query.queryElementCount() > 0) {

			QueryElement element = query.getQueryElement(0);
			element.setLogic(null);
		}
	}

	pageContext.forward("adv_query_record.jsp");
	if (true) return;
%>
