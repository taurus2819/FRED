<%@page extends="nz.cri.gns.jsp.IPSysLoginPage"
%><%@ page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@ page import="nz.cri.gns.db.*"
%><%

	ExtranetTemplate et = getExtranetTemplate();
	KeyValueObject links[] = new KeyValueObject[1];
	links[0] = new KeyValueObject("/fred/index.jsp", "FRED Home");
	et.setLinks(links);

	drawTop(out, et, request, response);

	drawEndNavigation(out);

%>	
	<p>You must be logged in to access this part of the FRED application.</p>
	<table border="0" cellspacing="5">
	<tr align="left"><td class="heading">GNS Staff</td><td>Please use your GNS Online username and password.  Contact Mark Edge if you are having login password problems or Ian Raine if you require more access rights</td></tr>
	<tr align="left"><td class="heading">External Users&nbsp;&nbsp;</td><td>Please click <a href='/register/user_reg.jsp?DBase=FRED' target='register' class='boldlink'>here</a> to register for an account</td></tr>
	</table>

	<div height="100%" style="vertical-align: middle">
<%

	drawLogin(out, request, response);

	%></div><%

	drawBottom(out, et);

%>
