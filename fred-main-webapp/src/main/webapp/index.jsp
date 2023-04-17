<%@page import="nz.cri.gns.core.Environment"%>
<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDStaticIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%
	
	ExtranetTemplate et = getExtranetTemplate();
        et.addStyleSheet(Environment.getDataUrl().replace("http://", "https://")+"/web-app-msg/tomcat_paleo/fred.css");
	try {
		drawTop(out, et, request, response);
	
		%>
                
                <%-- 
                    Used to show a temporary 'upcoming maintenance' message for major outages.
                    See http://intrawiki.gns.cri.nz/IT/Applications_Department/Miscellaneous/Upcoming_maintenance_outage_message for more details.
                --%>
                <iframe class="web-app-msg" src="<%=Environment.getDataUrl().replace("http://", "https://")%>/web-app-msg/tomcat_paleo/fred.html"></iframe> 
                 
                <jsp:include page="content/index-content.jsp"/>
                <div style="margin: 0 auto;font-size: 0.7em; padding: 10px; text-align: center; color: #999; background-color: #fff;">Version ${project.version} created ${timestamp}</div>
        <%
	} catch (Exception e) {
		%><p style="color: red">An error has occurred while loading this page. Please contact IT support.<br/><%=e.getMessage()%></p><%
 		e.printStackTrace();
	}				
	drawBottom(out, et); 
	%>
