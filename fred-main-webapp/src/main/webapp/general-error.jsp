<%@page pageEncoding="utf-8" extends="nz.cri.gns.fred.FREDStaticIPSysJspPage" import="nz.cri.gns.jsp.ExtranetTemplate,nz.cri.gns.core.Environment"%><%@page isErrorPage="true"%>
<%
    ExtranetTemplate et = getExtranetTemplate(request.getSession());
    et.addStyleSheet(Environment.getDataUrl().replace("http://", "https://") + "/web-app-msg/tomcat_paleo/fred.css");

    drawTop(out, et, request, response);
%>
<%-- 
    Used to show a temporary 'upcoming maintenance' message for major outages.
    See http://intrawiki.gns.cri.nz/IT/Applications_Department/Miscellaneous/Upcoming_maintenance_outage_message for more details.
--%>
<iframe class="web-app-msg" src="<%=Environment.getDataUrl().replace("http://", "https://")%>/web-app-msg/tomcat_paleo/fred.html" sandbox></iframe> 
<h3>FRED encountered an error, we apologise for the inconvenience.</h3>
<p style="color:white">
<%=exception%>
</p>
<div style="margin: 0 auto;font-size: 0.7em; padding: 10px; text-align: center; color: #999; background-color: #fff;">Version PROJECT_VERSION created BUILD_DATE</div>
<%
    drawBottom(out, et);
%>
