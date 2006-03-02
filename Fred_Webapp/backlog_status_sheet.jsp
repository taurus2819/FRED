<%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
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
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Backlog Processing Status for Sheet " + DBUtils.nvl(request.getParameter("Sheet"));
	}
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	FeatureUtil featureUtil = new FeatureUtil(factory);
	AuditUtil auditUtil = new AuditUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	et.setUseNavigationColumn(false);
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	//List data
	%><table border="0">
	<tr><td><img src="images/blank.gif" width="10" height="10" /></td></tr>
	<tr><td></td><td>
	</td><td><img src="images/blank.gif" width="30" height="1" /></td><td style="text-align: left"><%
	
	String mapSheet = request.getParameter("Sheet");
	
	if (mapSheet != null) {
		%><p><%
		startDETable(pageContext);
		%><table border="0">
		<tr><td class="deHeading" colspan="5">Localities</td></tr>
		<tr><th>Locality</th><th>Type</th><th>Backlog Status&nbsp;&nbsp;</th><th>FRED Status&nbsp;&nbsp;</th><th>Working Folder&nbsp;&nbsp;</th></tr><%
		for (Iterator i = featureUtil.getFrNumbers(mapSheet).iterator(); i.hasNext();) {
			FrNumber frNumber = (FrNumber) i.next();
			try {
				Feature feature = FeatureUtil.getFeature(frNumber);
				Audit audit = feature.getAudit();
				String status = AuditUtil.getAuditBacklogStatus(audit);
				%><tr><td class="heading"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>"><%=frNumber.getFrNumber()%></a>&nbsp;&nbsp;</td>
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
	
	%></td></tr></table><%

	
	drawBottom(out, et);
	
	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>