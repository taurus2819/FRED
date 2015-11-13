<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.auth.security.IpGrantedAuthority"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.fred.query.FREDQuery"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%!	public IpGrantedAuthority getRequiredRights() { return null; }
%><%
	PageState state = new PageState(request, response, getServletContext());
	FREDQuery query = FREDUtil.getFREDQuery(state);
	query.removeAllQueryElements();
	pageContext.forward("adv_query.jsp");
	if (true) return;
%>
