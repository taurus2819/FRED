<%@page	extends="nz.cri.gns.fred.FREDAdminIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.NewExtranetTemplate"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Administration";
	}
	
%><%

	NewExtranetTemplate et = getExtranetTemplate();
	drawTop(out, et, request, response);

	%><p>
	<ul>
	<li><a href="user.jsp">User Management</a></li>
	<li><a href="age_edit.jsp">Edit Ages</a></li>
	</ul>
	</p><%
	
	drawBottom(out, et);
%>