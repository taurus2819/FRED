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
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Backlog Processing Status for Sheet " + DBUtils.nvl(request.getParameter("Sheet"));
	}
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	FeatureUtil featureUtil = new FeatureUtil(factory);

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
		<tr><th>Locality</th><th>Backlog Status&nbsp;&nbsp;</th><th>FRED Status&nbsp;&nbsp;</th><th>Working Folder&nbsp;&nbsp;</th></tr><%
		for (Iterator i = featureUtil.getFrNumbers(mapSheet).iterator(); i.hasNext();) {
			FrNumber frNumber = (FrNumber) i.next();
			try {
				Feature feature = FeatureUtil.getFeature(frNumber);
				String status = null;
				Audit audit = feature.getAudit();
				if (audit.getStatus().equals(FREDConstants.APPROVED) && audit.getCuratorComments() != null && audit.getCuratorComments().indexOf("backlog") > 0) {
					status = FREDConstants.BACKLOG_COMPLETE;
				} else {
					for (Iterator j = audit.getAuditEdits().iterator(); j.hasNext();) {
						AuditEdit edit = (AuditEdit) j.next();
						if (edit.getComments() != null && edit.getComments().indexOf("backlog") > 0) {
							status = FREDConstants.BACKLOG_PROCESSING;
							break;
						}
					}
				}
				%><tr><td class="heading"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>"><%=frNumber.getFrNumber()%></a>&nbsp;&nbsp;</td><%
				if (status != null) {
					String statusColour = ((status.equals(FREDConstants.BACKLOG_COMPLETE)) ? "#00FF00" : "#FF0000");
					%><td style="color: <%=statusColour%>"><%=status%>&nbsp;&nbsp;</td><%
				} else {
					%><td>not started&nbsp;&nbsp;</td><%
				}
				if (status.equals(FREDConstants.BACKLOG_PROCESSING)) {
					Folder workingFolder = audit.getFolder();
					String auditStatus = audit.getStatus();
					%><td><%=(auditStatus != null) ? auditStatus : ""%></td>
					<td><%=(workingFolder != null) ? "<a href=\"folder_detail.jsp?ID=" + workingFolder.getFolderId() + "\">" + workingFolder.getName() + "</a>" : ""%></td><%
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