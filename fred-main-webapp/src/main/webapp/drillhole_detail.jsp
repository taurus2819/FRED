<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.auth.security.IpGrantedAuthority"
%><%!	
    @Override
    public IpGrantedAuthority getRequiredRights() {
        return null;
    }
%><%
	response.sendRedirect("detail.jsp?FeatID=" + request.getParameter("ID"));
%>
