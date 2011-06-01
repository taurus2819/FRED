<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysLoginPage"
%><%@ page import="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@ page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED Login";
	}
%><%

	ExtranetTemplate et = FREDIPSysJspPage.getFREDTemplate();

	drawTop(out, et, request, response);

	%><p>
	<table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr><td colspan="2"><b>You must be logged in to access this part of the FRED application.</b></td></tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td class="heading">GNS Staff</td><td>Please use your GNS Online username and password.  Contact Mark Edge if you are having login password problems or Ian Raine if you require more access rights</td></tr>
	<tr><td class="heading">External Users&nbsp;&nbsp;</td><td>Please click <a href='http://data.gns.cri.nz/register/user_reg.jsp?DBase=FRED' target='register' class='boldlink'>here</a> to register for an account</td></tr>
	<tr><td colspan="2"><%
	drawLogin(out, request, response);
	%></td></tr>
	</table>
	</p><%

	drawBottom(out, et);
%>
