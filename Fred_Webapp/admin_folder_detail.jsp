<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="java.net.URLEncoder"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			FolderUtil folderUtil = new FolderUtil(HibernateUtil.get().getDAOFactory());
			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));
			return "FRED :: " + folder.getFolder().getName() + " Masterfile";
		} catch (StorageAccessException e) {
			return "FRED";
		}
	}
	
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	try {
		if (request.getParameter("ID") == null) {
			factory.closeSession();
			response.sendRedirect("folder_list.jsp");
			return;
		} 
	
		FolderUtil folderUtil = new FolderUtil(factory);
		FeatureUtil featureUtil = new FeatureUtil(factory);
		User user =(User) getUser(session);
	
		ExtranetTemplate et = getExtranetTemplate();
		et.setDisplayLoadingMessage(true);
		et.setButtons(new IconnedLink[] {
				new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders"),
				new IconnedLink("backlog_setup.jsp?ID=" + request.getParameter("ID"), "images/revoke.gif", "Backlog setup"),
				new IconnedLink("javascript:doNewBacklogFolder();", "images/folder.gif", "New Backlog Edit Folder")});
	
		if (request.getParameter("ActionType") != null && request.getParameter("ActionType").equals("Add")) { //do something
			folderUtil.addBacklogFolder(request.getParameter("FoldName"), user);
		}
		
		drawTop(out, et, request, response);
	
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), user);
		session.setAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT, "admin_folder_detail.jsp?ID=" + folder.getFolderId());
	
		if (folder.isAllowedReadLocalities()) {
			
			%><script><!--
			function showHide(toShow, toHide) {
				document.getElementById(toShow).style.display = 'block';
				document.getElementById(toHide).style.display = 'none';
			}
			function doNewBacklogFolder() {
				var newName = prompt('Please enter the folder name', 'New Backlog Folder');
				if (newName) {
					document.NewFoldForm.FoldName.value = newName;
					document.NewFoldForm.submit();
				}
			}
			//--></script>
			<form name="NewFoldForm" method="post" action="admin_folder_detail.jsp">
			<input type="hidden" name="ActionType" value="Add">
			<input type="hidden" name="ID" value="<%=folder.getFolderId()%>">
			<input type="hidden" name="FoldName" value="">
			</form>
	
			<center><p>&nbsp;<p/><div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none"><%
			startDETable(pageContext);
			%><table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
			<tr><td colspan="3" class="deHeading">Masterfile Folder Instructions</td></tr><tr><td style="text-align: left">
			<ul>
			<li>Listed below are the localities currently in this masterfile folder.
			<li>Click on the icons to work with an individual locality record:
			<ul>
			<li><img src="images/print.gif" border="0"> to print the locality
			<li><img src="images/edit.gif" border="0"> to edit the locality
			<li><img src="images/review.gif" border="0"> to accept or reject the locality
			</ul>
			</ul>
			</td></tr>
			<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table><%
			endDETable(pageContext);
			%></div>
			
			<p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="9" class="deHeading">Localities to Approve</td></tr>
			<tr><th style="text-align: left" colspan="2">Locality&nbsp;&nbsp;</th><th style="text-align: left">Type&nbsp;&nbsp;</th><th style="text-align: left">Submitted Date&nbsp;&nbsp;</th><th style="text-align: left">Submitted By&nbsp;&nbsp;</th><th style="text-align: left" colspan="3">Options</th></tr><%

			//Display the features
			Feature[] features = featureUtil.getWaitingFeatures(folder);
			for (int i=0; i<features.length; i++) {
				Feature feature = features[i];
				Audit audit = feature.getAudit();
				%><tr><td style="text-align: left"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("admin_folder_detail.jsp?ID=" + folder.getFolderId(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/loc.gif" height="20" width="20" border="0" alt="View Locality" /></a></td>
				<td style="text-align: left" class="heading"><%=FeatureUtil.getFeatureName(feature)%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=feature.getFeatureType()%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : ""%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=FREDUtil.getUserName(audit.getSubmittedById().intValue())%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="print_front.jsp?FeatID=<%=feature.getFeatureId() + (feature.getFeatureType().equals(FREDConstants.OUTCROP) ? "" : "&FormType=Short")%>" target="print"><img src="images/print.gif" border="0" height="20" width="20" alt="Print Locality" /></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%
				if (folder.isAllowedEditLocalities()) {
					%><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FoldID=<%=folder.getFolderId()%>&FeatID=<%=feature.getFeatureId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Locality" /></a>&nbsp;&nbsp;<%
				}
				%></td><td style="text-align: left"><%
				if (folder.isAllowedApproveLocalities()) {
					%><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("admin_folder_detail.jsp?ID=" + folder.getFolderId(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/review.gif" width="20" height="20" border="0" alt="Review Localities" /></a><%
				}
				%></td></tr><%
			}
			%></table><%
			endDETable(pageContext);
			%></p>
			
			<p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="9" class="deHeading">Localities Recently Approved</td></tr><% 
			//Recently Approved
			features = featureUtil.getFeaturesApprovedInTheLastWeek(folder);
			%><tr><th style="text-align: left" colspan="2">Locality&nbsp;&nbsp;</th><th style="text-align: left">Type&nbsp;&nbsp;</th><th style="text-align: left">Approved Date&nbsp;&nbsp;</th><th style="text-align: left">Approved By&nbsp;&nbsp;</th><th style="text-align: left" colspan="3">Options</th></tr><%
			for (int i=0; i<features.length; i++) {
				Feature feature = features[i];
				Audit audit = feature.getAudit();
				%><tr><td style="text-align: left"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>"><img src="images/loc.gif" height="20" width="20" border="0" alt="View Locality" /></a></td>
				<td style="text-align: left"><span class="heading"><%=FeatureUtil.getFeatureName(feature)%></span>&nbsp;&nbsp;<%=(feature.getFeatureName() != null) ? "<br />(" + feature.getFeatureName() + ")&nbsp;&nbsp;" : ""%></td>
				<td style="text-align: left"><%=feature.getFeatureType()%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(audit.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(audit.getApprovedDate()) : ""%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(audit.getApprovedById() != null) ? FREDUtil.getUserName(audit.getApprovedById().intValue()) : ""%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="print_front.jsp?FeatID=<%=feature.getFeatureId()%><%=(feature.getFeatureType().equals(FREDConstants.OUTCROP)) ? "" : "&FormType=Short"%>" target="print"><img src="images/print.gif" border="0" height="20" width="20" alt="Print Locality" /></a></td></tr><%
			}
			%></table><%
			endDETable(pageContext);
			%></p><%
		}
		else { //no record found
			%>No folder found<%
		}

		%></td></tr></table><%
		drawBottom(out, et);
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		//Close the session
		factory.closeSession();
	}
%>