<%@page extends="nz.cri.gns.jsp.IPSysLoginPage"
	import="nz.cri.gns.jsp.*,java.net.URL,nz.cri.gns.db.*"
%><%

	ExtranetTemplate et = new ExtranetTemplate();
	et.setImageURL(new URL("http://data.gns.cri.nz/fred/images/fred.jpg"));
	et.setShowGnsLogo(false);
	KeyValueObject links[] = new KeyValueObject[1];
	links[0] = new KeyValueObject("/fred/index.jsp", "FRED Home");
	et.setLinks(links);

	drawTop(out, et, request, response);

	drawEndNavigation(out);

	%><div height="100%" style="vertical-align: middle"><%

	drawLogin(out, request, response);

	%></div><%

	drawBottom(out, et);

%>
