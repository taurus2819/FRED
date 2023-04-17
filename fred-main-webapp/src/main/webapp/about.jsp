<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDStaticIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%
	ExtranetTemplate et = getExtranetTemplate();
	drawTop(out, et, request, response);

        %>
        <jsp:include page="content/about-content.jsp" />
        <%

	drawBottom(out, et); 
	%>
