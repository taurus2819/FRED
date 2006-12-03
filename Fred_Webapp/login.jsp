<%@page extends="nz.cri.gns.fred.FREDIPSysLoginPage"
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
	<table border="0" cellpadding="0" cellspacing="0" width="550"><tr>
	<td width="11" height="11" style="width: 11px; height: 11px"><img border="0" src="images/frameLT.gif"></td>
	<td height="11" style="height: 11px; background: #c9c9c9 url(images/frameT.gif) repeat-x"></td>
	<td width="11" height="11" style="width: 11px; height: 11px"><img border="0" src="images/frameRT.gif"></td>
	</tr><tr>
	<td width="11" style="width: 11px; background: white url(images/frameL.gif) repeat-y; vertical-align: top" valign="top"><img src="images/frameLTi.gif" border="0"></td>
	<td style="background: white url(images/frameM.gif) repeat-x">
	<table border="0">
	<tr><td colspan="2">You must be logged in to access this part of the FRED application.</td></tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td class="heading">GNS Staff</td><td>Please use your GNS Online username and password.  Contact Mark Edge if you are having login password problems or Ian Raine if you require more access rights</td></tr>
	<tr><td class="heading">External Users&nbsp;&nbsp;</td><td>Please click <a href='http://data.gns.cri.nz/register/user_reg.jsp?DBase=FRED' target='register' class='boldlink'>here</a> to register for an account</td></tr>
	<tr><td colspan="2"><%
	drawLogin(out, request, response);
	%></td></tr>
	</table>
	<td width="11" style="width: 11px; background: white url(images/frameR.gif) repeat-y; vertical-align: top" valign="top"><img src="images/frameRTi.gif" border="0"></td>
	</tr><tr>
	<td width="11" height="11" style="width: 11px; height: 11px"><img border="0" src="images/frameLB.gif"></td>
	<td height="11" style="height: 11px; background: #c9c9c9 url(images/frameB.gif) repeat-x"></td>
	<td width="11" height="11" style="width: 11px; height: 11px"><img border="0" src="images/frameRB.gif"></td>
	</tr>
	</table>
	</p><%

	drawBottom(out, et);

%>
