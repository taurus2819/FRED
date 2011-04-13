<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.query.FREDQuery"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.db.querybuilder.QueryElement"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	FREDQuery query = FREDUtil.getFREDQuery(getPageState(request, response));
	if (query != null) {
		query.removeQueryElement(Integer.parseInt(request.getParameter("index")));

		if (query.queryElementCount() > 0) {

			QueryElement element = query.getQueryElement(0);
			element.setLogic(null);
		}
	}

	pageContext.forward("adv_query.jsp");
	if (true) return;
%>
