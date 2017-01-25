<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysLoginPage"
%><%@ page import="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@ page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@ page import="nz.cri.gns.core.Environment"
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
	<tr><td class="heading">GNS Staff</td><td>Please use your GNS Network login and password.  Contact IT Support if you are having login password problems or Ian Raine if you require more access rights on FRED.</td></tr>
        <tr><td class="heading">External Users&nbsp;&nbsp;</td>
            <td>
                <div>Please click <a href='http://data.gns.cri.nz/register/user_reg.jsp?DBase=FRED' target='register' class='boldlink'>here</a> to register for an account.</div>
                <div>If you have not changed your password since 23/11/2016 you will need to reset it using the <a href="<%=Environment.getDataUrl()%>/register/password.jsp">password reset page</a></div>
            </td>
        </tr>
        <tr><td colspan="2"><%
	drawLogin(out, request, response);
	%></td></tr>
	</table>
	</p><%

	drawBottom(out, et);
%>
