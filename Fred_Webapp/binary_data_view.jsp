<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%	if (request.getParameter("Src") != null) { %>
<?xml version='1.0'?>
<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>
<html xmlns='http://www.w3.org/1999/xhtml'>
<head>
<title>Fossil Record Electronic Database</title>
</head>
	<frameset rows='100,*' frameborder='0' border='0' framespacing='0'>
		<frame src='binary_head.jsp' scrolling='no'>
		<frame src='/online/DigitalDocument?src=<%=request.getParameter("Src")%>'>
	</frameset>
</html>
<%	} else {
		ExtranetTemplate et = getExtranetTemplate();

		drawTop(out, et, request, response);
		drawEndNavigation(out);
		out.println("<p><span class='heading'>No Image/File selected</span></p>");
		drawBottom(out, et);
	} %>