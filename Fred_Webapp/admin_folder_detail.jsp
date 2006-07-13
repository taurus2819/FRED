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
				new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to Folders"),
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
			<tr><td class="deHeading">Masterfile Folder Instructions</td></tr>
			<tr><td style="text-align: left">
			<ul>
			<li>Listed below are the localities currently in this masterfile folder.
			<li>Click on the icons to work with an individual locality record:
			<ul>
			<li><img src="images/map.gif" border="0" height="20" width="20" alt="" /> view a map of the locality</li>
			<li><img src="images/pdf_icon.gif" border="0"> to print the locality
			<li><img src="images/edit.gif" border="0"> to edit the locality
			<li><img src="images/review.gif" border="0"> to accept or reject the locality
			</ul>
			<li>Multiple localities may be selected by <i>ticking</i> the checkboxes on the left-hand side.  You can then use the tools in the <i>Selected Locality Actions</i> box</li>
			</ul>
			</td></tr>
			<tr><td><a href="http://www.adobe.com/products/acrobat/readstep2.html" target="getAcrobat"><img src="images/get_adobe_reader.gif" border="0" alt="Get Adobe Reader" /></a>&nbsp;&nbsp;Adobe reader is required to print localities</td></tr>
			<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table><%
			endDETable(pageContext);
			%></div>
			
			<form name="FoldForm" method="get" action="frf/frf.pdf" target="_blank">
			
			<p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="10" class="deHeading">Localities to Approve</td></tr>
			<tr><th style="text-align: left" colspan="3">Locality&nbsp;&nbsp;</th><th style="text-align: left">Type&nbsp;&nbsp;</th><th style="text-align: left">Submitted Date&nbsp;&nbsp;</th><th style="text-align: left">Submitted By&nbsp;&nbsp;</th><th style="text-align: left" colspan="4">Actions</th></tr>
			<tr><td colspan="10"><img src="images/line.gif" height="3" width="550" /></td></tr><%

			//Display the features
			Feature[] features = featureUtil.getWaitingFeatures(folder);
			for (int i=0; i<features.length; i++) {
				Feature feature = features[i];
				Audit audit = feature.getAudit();
				%><tr>
				<td style="text-align: left"><input type="checkbox" name="FeatIDs" value="<%=feature.getFeatureId()%>" /></td>
				<td style="text-align: left"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("admin_folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/loc.gif" height="20" width="20" border="0" alt="View Locality" /></a></td>
				<td style="text-align: left" class="heading"><%=FeatureUtil.getFeatureIdentifyingName(feature)%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=feature.getFeatureType()%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : ""%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=FREDUtil.getUserName(audit.getSubmittedById().intValue())%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("admin_folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" /></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="frf/frf.pdf?FeatIDs=<%=feature.getFeatureId()%>&q=<%=Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" border="0" height="20" width="20" alt="Print Locality" /></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%
				if (folder.isAllowedEditLocalities()) {
					%><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FoldID=<%=folder.getFolderId()%>&FeatID=<%=feature.getFeatureId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Locality" /></a>&nbsp;&nbsp;<%
				}
				%></td><td style="text-align: left"><%
				if (folder.isAllowedApproveLocalities()) {
					%><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("admin_folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/review.gif" width="20" height="20" border="0" alt="Review Localities" /></a><%
				}
				%></td></tr>
				<tr><td colspan="10"><img src="images/line.gif" height="3" width="550" /></td></tr><%
			}
			%></table><%
			endDETable(pageContext);
			%></p>
			
			<p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="8" class="deHeading">Localities Recently Approved</td></tr><% 
			//Recently Approved
			features = featureUtil.getFeaturesApprovedInTheLastWeek(folder);
			%><tr><th style="text-align: left" colspan="3">Locality&nbsp;&nbsp;</th><th style="text-align: left">Type&nbsp;&nbsp;</th><th style="text-align: left">Approved Date&nbsp;&nbsp;</th><th style="text-align: left">Approved By&nbsp;&nbsp;</th><th style="text-align: left" colspan="2">Actions</th></tr>
			<tr><td colspan="8"><img src="images/line.gif" height="3" width="550" /></td></tr><%
			for (int i=0; i<features.length; i++) {
				Feature feature = features[i];
				Audit audit = feature.getAudit();
				%><tr>
				<td style="text-align: left"><input type="checkbox" name="FeatIDs" value="<%=feature.getFeatureId()%>" /></td>
				<td style="text-align: left"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("admin_folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/loc.gif" height="20" width="20" border="0" alt="View Locality" /></a></td>
				<td style="text-align: left"><span class="heading"><%=FeatureUtil.getFeatureIdentifyingName(feature)%></span>&nbsp;&nbsp;<%=(feature.getFeatureName() != null) ? "<br />(" + feature.getFeatureName() + ")&nbsp;&nbsp;" : ""%></td>
				<td style="text-align: left"><%=feature.getFeatureType()%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(audit.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(audit.getApprovedDate()) : ""%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(audit.getApprovedById() != null) ? FREDUtil.getUserName(audit.getApprovedById().intValue()) : ""%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("admin_folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" /></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="frf/frf.pdf?FeatIDs=<%=feature.getFeatureId()%>&q=<%=Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" border="0" height="20" width="20" alt="Print Locality" /></a></td></tr>
				<tr><td colspan="8"><img src="images/line.gif" height="3" width="550" /></td></tr><%
			}
			%></table><%
			endDETable(pageContext);
			%></p>
			
			<input type="hidden" name="q" value="<%=Math.random()%>" />
			</form>
			
			<p><%
			//Selected Actions box
			startDETable(pageContext);
			%><table border="0" width="550">
			<tr><td colspan="2" class="deHeading">Selected Locality Actions</td></tr>
			<tr>
			<td><a href="javascript:document.FoldForm.submit();"><img src="images/pdf_icon.gif" border="0" height="20" width="20" alt="Print" /></a></td>
			<td class="heading" style="text-align: left"><a href="document.FoldForm.submit();">Print</a></td>
			</tr>	
			</table><%		
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