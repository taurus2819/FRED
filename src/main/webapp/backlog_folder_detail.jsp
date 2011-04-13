<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
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
			FolderUtil folderUtil = new FolderUtil(FredHibernate.get().getDAOFactory());
			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));
			return "FRED :: " + folder.getFolder().getName() + " Masterfile";
		} catch (StorageAccessException e) {
			return "FRED";
		}
	}
	
%><%
	DAOFactory factory = FredHibernate.get().getDAOFactory();
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
		addButtons(et, new IconnedLink[] {
				new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to Folders"),
				new IconnedLink("backlog_status.jsp", "images/map.gif", "Status")});
	
		drawTop(out, et, request, response);
	
		if (request.getParameter("sbmit") != null) {
			String[] toApprove = request.getParameterValues("approve");
			for (int i=0; i<toApprove.length; i++) {
				featureUtil.approveBacklogFeature(featureUtil.getFeature(Integer.parseInt(toApprove[i])), user); 
			}
		}
	
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), user);
		session.setAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT, "backlog_folder_detail.jsp?ID=" + folder.getFolderId());
		if (folder.isAllowedReadLocalities()) {
		
			%><script><!--
			function showHide(toShow, toHide) {
				document.getElementById(toShow).style.display = 'block';
				document.getElementById(toHide).style.display = 'none';
			}
			//--></script>
			
			<form name="backlogForm" action="backlog_folder_detail.jsp" method="post">
			<input type="hidden" name="ID" value="<%=folder.getFolderId()%>">
			<input type="hidden" name="sbmit" value="yes">
			<p><div id="showInst">
			<table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none"><%
			startDETable(pageContext);
			%><table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
			<tr><td colspan="3" class="deHeading">Masterfile Folder Instructions</td></tr><tr><td style="text-align: left">
			<ul>
			<li>Listed below are the localities currently in this masterfile folder.
			<li>Click on the icons to work with an individual locality record:
			<ul>
			<li><img src="images/print.gif" border="0"> to print the locality
			<li><img src="images/edit.gif" border="0"> to edit the locality
			</ul>
			</ul>
			</td></tr>
			<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table><%
			endDETable(pageContext);
			%></div></p>
			
			<p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="9" class="deHeading">Localities to Approve</td></tr>
			<tr><th colspan="2">Locality&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Submitted Date&nbsp;&nbsp;</th><th>Submitted By&nbsp;&nbsp;</th><th colspan="3">Actions</th></tr><%
	
			//Display the features
			Feature[] features = featureUtil.getWaitingFeatures(folder);
			for (int i=0; i<features.length; i++) {
				Feature feature = features[i];
				Audit audit = feature.getAudit();
				%><tr><td style="text-align: left"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("backlog_folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/loc.gif" height="20" width="20" border="0" alt="View Locality" /></a></td>
				<td style="text-align: left" class="heading"><%=FeatureUtil.getFeatureIdentifyingName(feature)%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=feature.getFeatureType()%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : ""%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=audit.getSubmittedBy().getFullName()%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("backlog_folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" /></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="frf/frf.pdf?FeatIDs=<%=feature.getFeatureId()%>&q=<%=Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" border="0" height="20" width="20" alt="Print Locality" /></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%
				if (folder.isAllowedEditLocalities()) {
					%><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FoldID=<%=folder.getFolderId()%>&FeatID=<%=feature.getFeatureId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Locality" /></a>&nbsp;&nbsp;<%
				}
				%></td>
				<td style="text-align: left"><%
				if (folder.isAllowedApproveLocalities()) {
					%><input type="checkbox" name="approve" value="<%=feature.getFeatureId()%>" /><%
				}
				%></td></tr><%
			}
			%><tr><td><img src="images/blank.gif" width="1" height="10" /></td></tr>
			<tr><td colspan="6" style="text-align: right"><a href="javascript:selectAll()">Select all</a></td></tr>
			<tr><td colspan="6" style="text-align: right"><input type="submit" value="Approve selected"></td></tr>
			</table></center></form>
			<script><!--
			function selectAll() {
				if (document.backlogForm.approve.length) {
					for (var i=0; i<document.backlogForm.approve.length; i++) {
						document.backlogForm.approve[i].checked = true;
					}
				} else {
					document.backlogForm.approve.checked = true;
				}
			}
			//--></script><%
			endDETable(pageContext);
			%><p><%
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