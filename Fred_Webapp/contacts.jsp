<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="java.io.PrintWriter"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="nz.cri.gns.auth.*"
%><%@page import="nz.cri.gns.fred.website.ContentProvider"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		return new Authenticable[0];
	}
%><%
	ExtranetTemplate et = getExtranetTemplate();
	ContentProvider contentProvider = getContentProvider(getPageState(request, response));
	drawTop(out, et, request, response);

	contentProvider.getContent("index.nav").loadAll(new PrintWriter(out));
	
	drawEndNavigation(out);

	contentProvider.getContent("contacts.main").loadAll(new PrintWriter(out));

	drawBottom(out, et); 
	%>
