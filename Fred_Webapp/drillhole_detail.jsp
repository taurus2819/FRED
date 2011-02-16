<%@page pageEncoding="utf-8"%>
<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%
	response.sendRedirect("detail.jsp?FeatID=" + request.getParameter("ID"));
%>
