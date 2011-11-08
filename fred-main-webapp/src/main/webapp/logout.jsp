<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.jsp.IPSysLogoutPage"
%><%@ page import="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@ page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED Logout";
	}
%><%

	ExtranetTemplate et = FREDIPSysJspPage.getFREDTemplate();
        //et.setDisplayLogin(false);
        
	drawTop(out, et, request, response);

	%>
	<table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr><td colspan="2"><b>You have been logged out.</b></td></tr>
	<tr><td colspan="2"><b>Thank you for using the FRED application. </b></td></tr>
	<tr><td>&nbsp;</td></tr>
	</table>
	<%

	drawBottom(out, et);
%>
