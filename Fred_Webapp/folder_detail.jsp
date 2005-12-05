<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.*"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="java.text.*"
%><%@page import="java.util.List"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.auth.*"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.dao.StorageAccessException"
%><%@page import="nz.cri.gns.fred.de.MandatoryFieldsMissingException"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.FrNumber"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			FolderUtil folderUtil = new FolderUtil(HibernateUtil.get().getDAOFactory());
			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));
			return "FRED :: " + ((User)getUser(request.getSession())).getFullName() + "'s "
					+ ((folder.isBacklogFolder()) ? "Backlog " : "")
					+ "Folders: " + folder.getFolder().getName();
		} catch (StorageAccessException e) {
			return "FRED";
		}
	}
	
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	if (request.getParameter("ID") == null) {
		factory.closeSession();
		response.sendRedirect("folder_list.jsp");
		return;
	}
	
	FolderUtil folderUtil = new FolderUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);
	User user =(User) getUser(session);
	UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), user);
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	if (folder.isAllowedCreateLocalities()) {
		et.setButtons(new IconnedLink[] {
			new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders"),
			new IconnedLink(null, "images/new.gif", "New: "),
			new IconnedLink("de.jsp?Type=Outcrop&FoldID=" + folder.getFolder().getFolderId(), null, "Outcrop"),
			new IconnedLink("de.jsp?Type=Drillhole&FoldID=" + folder.getFolder().getFolderId(), null, "Drillhole"),
			new IconnedLink("de.jsp?Type=Vertical+Section&FoldID=" + folder.getFolder().getFolderId(), null, "Vert. Section"),
			new IconnedLink("simple_query.jsp?FoldID=" + folder.getFolder().getFolderId(), "images/search.gif", "Search")
		});
	} else {
		et.setButtons(new IconnedLink[] {
			new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")
		});
	}	
	
	if (folder != null || folder.isAllowedReadLocalities()) {
		session.setAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT, "folder_detail.jsp?ID=" + folder.getFolder().getFolderId());
		String errorMessage = null;
		if (request.getParameter("ActionType") != null) { //do something
			String actionType = request.getParameter("ActionType");
			try {
				if (request.getParameter("FeatID") != null && !request.getParameter("FeatID").equals("")) {
					//Get the feature
					Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID")));
					//Copy locality
					if (actionType.equals("CopyFeat") && folder.isAllowedCreateLocalities()) {
						featureUtil.copyFeature(feature, request.getParameter("NewFeatName"), folder, user);
					}
					 //Delete locality
					else if (actionType.equals("DeleteFeat") && folder.isAllowedDeleteLocalities()) {
						featureUtil.deleteFeature(feature, user);
					}
					//Remove locality
					else if (actionType.equals("RemoveFeat")) {
						featureUtil.removeFeature(feature, folder, user);
					}
					//Merge locality
					else if (actionType.equals("MergeFeat")) {
						featureUtil.mergeFeature(feature, request.getParameter("SelFeatID"), folder, user);
					}
					//submit working locality
					else if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
						featureUtil.submitFeature(feature, folder, user);
					}
					//Revoke waiting records
					else if (actionType.equals("Revoke") && folder.isAllowedSubmitLocalities()) {
						featureUtil.revokeFeature(feature, folder, user);
					}
					//Alter locality type
					else if (actionType.equals("AlterType") && folder.isAllowedEditLocalities()) {
						featureUtil.alterFeatureType(feature, request.getParameter("NewFeatType"), folder, user);
					}
				} else if (request.getParameter("FeatIDs") != null) {
					if (actionType.equals("PrintFeatures")) {
						String[] featIDs = request.getParameterValues("FeatIDs");
						StringBuffer queryStr = new StringBuffer();
						for (int i = 0; i < featIDs.length; i++) {
							queryStr.append("FeatIDs=").append(featIDs[i]);
							if (i < featIDs.length - 1)
								queryStr.append("&");
						}
						response.sendRedirect("frf/frf.pdf?" + queryStr.toString());
						return;
					}
				}
			} catch (MandatoryFieldsMissingException e) {
				%><script><!--
alert("<%=e.getMessage()%>");
//--></script><%
			} catch (Exception e) {
				System.out.println("*********** FRED folder_detail.jsp error **********");
				e.printStackTrace();
				errorMessage = "An Error has occured: " + e.getMessage();
			}
		}
	
		drawTop(out, et, request, response);
	
		//print error message (if any) from folder_actions
		if (errorMessage != null) {
			out.println("<p><span class='heading' style='color: #FF0000'>" + errorMessage + "</span></p>");
		}
		%><script><!--
function showHide(toShow, toHide) {
	document.getElementById(toShow).style.display = 'block';
	document.getElementById(toHide).style.display = 'none';
}
//--></script>
<center><p>&nbsp;<p/><div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none">
<%
		startDETable(pageContext);
%>
<table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
<tr><td colspan="3" class="deHeading">Folder Instructions</td></tr><tr><td style="text-align: left">
<ul>
<li>Listed below are the localities you have added to this folder.
<li>Working localities are named with their field number or drillhole name until they are allocated a Fossil Record Number.
<li>Click on the locality to add/edit locality records, or use the options to work with the locality itself:
<ul>
<li><img src="images/edit.gif" border="0"> to edit the locality
<li><img src="images/copy.gif" border="0"> to make a copy of the locality (front of form data only)
<li><img src="images/delete.gif" border="0"> to delete the locality
<li><img src="images/submit.gif" border="0"> to submit the locality for entry to the masterfile
</ul>
</ul>
</td></tr>
<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table>
<%
		endDETable(pageContext);
		%></div>
<p>

<%
		startDETable(pageContext);
		%><table border="0" width="550"><tr><td colspan="12" class="deHeading">Localities</td></tr>
<tr>
<th colspan="3">Name&nbsp;&nbsp;</th><th colspan="2">Type&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Created Date&nbsp;&nbsp;</th><th colspan="5">Options</th></tr>
<tr><td colspan="12"><img src="images/line.gif" height="3" width="550" /></td></tr>

<form name="FoldForm" method="get" action="folder_detail.jsp">
<%
		//Display the features
		Feature[] features = featureUtil.getFeaturesInFolder(folder);
		for (int i = 0; i < features.length; i++) {
			Feature feature = features[i];
			Audit audit = feature.getAudit();
			String status = audit.getStatus();
			String name = FeatureUtil.getFeatureName(feature);
			String featName = feature.getFeatureName();
			%><tr><%
			
	/*		if (folder.isBacklogFolder() && !feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
				%><td><input type="radio" name="SelFeatID" value="<%=feature.getFeatureId()%>" /></td><%
			} else {
				%><td></td><%	
			}		 */	
			
			//starting work on checkboxes
			%><td><input type="checkbox" name="FeatIDs" value="<%=feature.getFeatureId()%>" /></td><%
			
			%><td><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>"><img src="images/loc.gif" border="0" height="20" width="20" alt="View Locality" /></a></td>
			
			<td class="heading" style="text-align: left"><a href="folder_feature_detail.jsp?FoldID=<%=folder.getFolderId()%>&FeatID=<%=feature.getFeatureId()%>"><%=name%></a>&nbsp;&nbsp;<%
			if (featName != null && !featName.equals(name)) {
				%><br />(<%=featName%>)&nbsp;&nbsp;<%
			}
			%></td><%

			if (folder.isBacklogFolder()) {
				%><td><a href="javascript:prmpt=prompt('Please enter the new type. Choose Outcrop, Drillhole or Vertical Section.\nPlease be aware that some information to be lost.', '');if(prmpt!=null && (prmpt == 'Outcrop' || prmpt == 'Drillhole' || prmpt == 'Vertical Section')){document.FoldForm.NewFeatType.value=prmpt;document.FoldForm.ActionType.value='AlterType';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/edit.gif" border="0" height="20" width="20" alt="Alter Locality Type" /></a><img src="images/blank.gif" height="20" width="2" /></td><%
			} else {
				%><td></td><%	
			}	
			
			%><td style="text-align: left"><%=feature.getFeatureType()%>&nbsp;&nbsp;</td><%

			%><td style="color: #FF0000; text-align: left"><%
			if (!status.equals(FREDConstants.APPROVED)) {
				%><%=status%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(audit.getCreatedDate() == null) ? "" : DateFormat.getDateInstance(DateFormat.LONG).format(audit.getCreatedDate())%></td><%
			} else {
				%></td><td></td><%
			}
			
			%><td><%
			if (featureUtil.isAllowedEditFeature(user, feature, folder)) {
				%><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=feature.getFeatureId()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
			}
			%></td><td><%
			if (folder.isAllowedCreateLocalities()) {
				%><a href="javascript:prmpt=prompt('Please enter the new name', 'New <%=feature.getFeatureType()%>');if(prmpt!=null){document.FoldForm.NewFeatName.value=prmpt;document.FoldForm.ActionType.value='CopyFeat';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/copy.gif" border="0" height="20" width="20" alt="Copy Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
			}
			%></td><td><%
			if (folder.isBacklogFolder() && folder.isAllowedEditLocalities() && !feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
				%><a href="javascript:if (confirm('Are you sure you want to merge this locality') == true) {document.FoldForm.ActionType.value='MergeFeat';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/drill.gif" border="0" height="20" width="20" alt="Merge Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
			}
			%></td><td><%
			if ((status.equals(FREDConstants.WORKING) || status.equals(FREDConstants.REJECTED)) && folder.isAllowedDeleteLocalities()) {
				%><a href="javascript:if (confirm('Are you sure you want to delete this locality') == true) {document.FoldForm.ActionType.value='DeleteFeat';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/delete.gif" border="0" height="20" width="20" alt="Delete Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
			} else if (status.equals(FREDConstants.APPROVED)) {
				%><a href="javascript:if (confirm('Are you sure you want to remove this locality from your folder') == true) {document.FoldForm.ActionType.value='RemoveFeat';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/delete.gif" border="0" height="20" width="20" alt="Remove Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
			}
			%></td><td><%
			if ((status.equals(FREDConstants.WORKING) || status.equals(FREDConstants.REJECTED)) && folder.isAllowedSubmitLocalities()) {
				%><a href="javascript:if (confirm('Are you sure you want to submit this locality') == true) {document.FoldForm.ActionType.value='Submit';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/submit.gif" border="0" height="20" width="20" alt="Submit Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
			} else if (status.equals(FREDConstants.WAITING) && folder.isAllowedSubmitLocalities()) {
				%><a href="javascript:if (confirm('Are you sure you want to revoke this locality') == true) {document.FoldForm.ActionType.value='Revoke';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/revoke.gif" border="0" height="20" width="20" alt="Revoke Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
			}
			%></td></tr>
<tr><td colspan="12"><img src="images/line.gif" height="3" width="550" /></td></tr><%
		}
		%></table><%
		endDETable(pageContext);

		%></p><p><%
		
		startDETable(pageContext);
		%><table border="0" width="550">
		<tr><td colspan="12" class="deHeading">Folder Options</td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td class="heading">
		<a href="javascript:document.FoldForm.ActionType.value='PrintFeatures';document.FoldForm.target='_blank';document.FoldForm.submit();"><img src="images/pdf_icon.gif" border="0" height="20" width="20" alt="Print Selected" />&nbsp;Print Selected</a>
		</td></tr>
		</table>
		<%		
		endDETable(pageContext);
		%>
		</p>
<input type="hidden" name="ActionType" value="" />
<input type="hidden" name="ID" value="<%=folder.getFolder().getFolderId()%>" />
<input type="hidden" name="FeatID" value="" />
<input type="hidden" name="NewFoldID" value="" />
<input type="hidden" name="NewFeatName" value="" />
<input type="hidden" name="NewFeatType" value="" />
</table></p>
<%
/*		//folder options
		out.println("<table border='0' cellspacing='0' cellpadding = '2' width='600'><tr><td height='5'></td></tr><tr class='shadegreytr'><td>");
		//Copy
		//check for multiple user folders (and if found display move option)
		rs = statement.executeQuery("SELECT * FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type='personal' AND Folder_ID <> " + foldID);
		if (rs.next()) {
			out.println("&nbsp&nbsp<a href='#' onClick='if (document.FoldForm.CopyFoldID.value!=\"-\") {document.FoldForm.ActionType.value=\"CopyFold\";document.FoldForm.NewFoldID.value=document.FoldForm.CopyFoldID.value;document.FoldForm.submit();} else {alert(\"Please select a folder\");document.FoldForm.NewFoldID.focus();}' class='smallfname'>Copy&nbspSelected&nbspto</a>&nbsp");
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, "CopyFoldID", "-- Choose --", null, null, "Folder_View", "Folder_Name", "Folder_ID", null, "User_ID = " + userID + " AND Folder_Type = 'personal' AND Folder_ID <> " + foldID);
		}
		//Move
		if ((userRights & 8) != 0) {
			//check for multiple user folders (and if found display move option)
			rs = statement.executeQuery("SELECT * FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type='personal' AND Folder_ID <> " + foldID);
			if (rs.next()) {
				out.println("  &nbsp&nbsp<a href='#' onClick='if (document.FoldForm.MoveFoldID.value!=\"-\") {document.FoldForm.ActionType.value=\"MoveFold\";document.FoldForm.NewFoldID.value=document.FoldForm.MoveFoldID.value;document.FoldForm.submit();} else {alert(\"Please select a folder\");document.FoldForm.NewFoldID.focus();}' class='smallfname'>Move&nbspSelected&nbspto</a>&nbsp");
				HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, "MoveFoldID", "-- Choose --", null, null, "Folder_View", "Folder_Name", "Folder_ID", null, "User_ID = " + userID + " AND Folder_Type = 'personal' AND Folder_ID <> " + foldID);
			}
		}
		//Delete
		if ((userRights & 8) != 0) {
			out.println("  &nbsp&nbsp<a href='#' onClick='if (confirm(\"Are you sure you want to remove these records\") == true) {document.FoldForm.ActionType.value=\"Remove\";document.FoldForm.submit();}' class='smallfname'>Remove&nbspSelected</a>");
		}
		out.println("</table></p>");  
*/

		%></form>
</td></tr></table>
<%
		drawBottom(out, et);

	} else { //no folder found
		drawEndNavigation(out);
		out.println("<p><span class='heading'>No folder found</span></p>");
		out.println("<p>An incorrect parameter has been recieved by this page.  Please press the Back button and try again</p>");
	}
	
	//Close the session
	folderUtil.closeSession();
%>