<%@page extends="nz.cri.gns.jsp.IPSysLoginPage"
	import="nz.cri.gns.jsp.*,java.net.URL"
%><%

	ExtranetTemplate et = new ExtranetTemplate();
	et.setImageURL(new URL("http://data.gns.cri.nz/fred/images/fred.jpg"));
	et.setShowGnsLogo(false);

	drawTop(out, et, request, response);

	drawEndNavigation(out);

	%><div height="100%" style="vertical-align: middle"><%

	drawLogin(out, request, response);

	%></div><%

	drawBottom(out, et);

%>
