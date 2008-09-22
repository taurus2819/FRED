<%@page	extends="nz.cri.gns.fred.FREDAdminIPSysJspPage"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Administration";
	}
	
%><%
	%><p>
	<ul>
	<li><a href="user.jsp">User Management</a></li>
	</ul>
	</p><%
%>