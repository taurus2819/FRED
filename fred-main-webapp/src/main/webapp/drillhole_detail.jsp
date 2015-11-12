<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="org.springframework.security.core.GrantedAuthority"
%><%!	
    @Override
    public GrantedAuthority getRequiredRights() {
        return null;
    }
%><%
	response.sendRedirect("detail.jsp?FeatID=" + request.getParameter("ID"));
%>
