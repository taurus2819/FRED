<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDStaticIPSysJspPage"
%><%@page import="java.io.PrintWriter"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.website.ContentProvider"
%><%
	ExtranetTemplate et = getExtranetTemplate(request.getSession());
	ContentProvider contentProvider = getContentProvider(getPageState(request, response));
	drawTop(out, et, request, response);

	contentProvider.getContent("whats_new.main").loadAll(new PrintWriter(out));

	drawBottom(out, et); 
	%>
