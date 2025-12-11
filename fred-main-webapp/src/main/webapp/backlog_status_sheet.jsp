<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.model.FrNumber"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.AuditEdit"
%><%@page import="nz.cri.gns.fred.model.Folder"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="java.net.URLEncoder"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Backlog Processing Status for Sheet " + DBUtils.nvl(request.getParameter("Sheet"));
	}
%><%
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	FeatureUtil featureUtil = new FeatureUtil(factory);
	AuditUtil auditUtil = new AuditUtil(factory);

	ExtranetTemplate et = getExtranetTemplate(request.getSession());
	et.setUseNavigationColumn(false);
	et.setDisplayLoadingMessage(true);

	String backURL = "backlog_status.jsp" + ((request.getParameter("MF") != null) ? "?ID=" + request.getParameter("MF") : "");
	IconnedLink[] il = new IconnedLink[] {new IconnedLink(backURL, "images/back_arrow.gif", "Back to Status Map")};
	addButtons(et, il);
	
	drawTop(out, et, request, response);

	String mapSheet = request.getParameter("Sheet");
	
	if (mapSheet != null) {
		%><p><%
		startDETable(pageContext);
		%><table border="0">
		<tr><td class="deHeading" colspan="6">Localities</td></tr>
		<tr><th colspan="2">Locality</th><th>Type</th><th>Backlog Status&nbsp;&nbsp;</th><th>FRED Status&nbsp;&nbsp;</th><th>Working Folder&nbsp;&nbsp;</th></tr><%
		for (FrNumber frNumber : featureUtil.getFrNumbers(mapSheet)) {
			try {
				Feature feature = featureUtil.getFeature(frNumber);
				Audit audit = feature.getAudit();
				String status = AuditUtil.getAuditBacklogStatus(audit);
				%><tr><td class="heading"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("backlog_status_sheet.jsp?Sheet=" + mapSheet, "ISO-8859-1")%>&backText=Back%20To%20<%=mapSheet%>%20List"><%=frNumber.getFrNumber()%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("backlog_status_sheet.jsp?Sheet=" + mapSheet, "ISO-8859-1")%>&backText=Back%20To%20<%=mapSheet%>%20List"><img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" /></a>&nbsp;&nbsp;</td>
				<td><%=feature.getFeatureType()%>&nbsp;&nbsp;<%
				String statusColour = "#00FF00";
				if (status.equals(FREDConstants.BACKLOG_PROCESSING))
					statusColour = "#FF0000";
				else if (status.equals(FREDConstants.BACKLOG_NOT_STARTED))
					statusColour = "#000000";
				%><td style="color: <%=statusColour%>"><%=status%>&nbsp;&nbsp;</td><%
				if (status.equals(FREDConstants.BACKLOG_PROCESSING)) {
					%><td><%=(audit.getStatus() != null) ? audit.getStatus() : ""%></td>
					<td><%=(audit.getFolder() != null) ? "<a href=\"folder_detail.jsp?ID=" + audit.getFolder().getFolderId() + "\">" + audit.getFolder().getName() + "</a>" : ""%></td><%
				} else {
					%><td></td><td></td><%
				}
				%></tr><%
			} catch (Exception e) {}
		}
		%></table><%
		endDETable(pageContext);
		%></p><%		
	}
	
	drawBottom(out, et);
%>