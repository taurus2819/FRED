<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDStaticIPSysJspPage"
%><%@page import="nz.cri.gns.fred.util.BacklogStatusUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%
	
	ExtranetTemplate et = getExtranetTemplate(request.getSession());
	try {
		FeatureUtil featureUtil = new FeatureUtil(FredHibernate.get().getDAOFactory());
		BacklogStatusUtil bsUtil = new BacklogStatusUtil(FredHibernate.get().getDAOFactory());
		
		drawTop(out, et, request, response);
	
		%>

        <%@ include file="map_openlayers.jsp" %>

        <%
	} catch (Exception e) {
		%><p style="color: red">An error has occurred while loading this page. Please contact IT support.<br/><%=e.getMessage()%></p><%
 		e.printStackTrace();
	}				
	drawBottom(out, et); 
	%>
