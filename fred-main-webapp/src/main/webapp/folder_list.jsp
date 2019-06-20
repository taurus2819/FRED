<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="java.util.List"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.fred.util.DataEntryTemplateUtil"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"

%><%!
	public String getName(HttpServletRequest request) {
        String name = ((User)getUser(request.getSession())).getFullName();
		return "FRED :: " +name + "'s Folders";
	}
	
%><%
	User user = (User)getUser(session);
	
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	FolderUtil folderUtil = new FolderUtil(factory);
	TaxonomicUtil taxaUtil = new TaxonomicUtil(factory);
		
	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);
	addButtons(et, new IconnedLink[] {new IconnedLink("javascript:doNewFolder();", "images/folder.gif", "New Folder"),
			new IconnedLink("confid_list.jsp?q=" + Math.random(), "images/lock.gif", "Confidential Data List")});

	String error = null;
	if (request.getParameter("ActionType") != null) { //do something
		String actionType = request.getParameter("ActionType");
		if (actionType.equals("Add")) { //add folder
			folderUtil.addFolder(request.getParameter("FoldName"), user);
		}
		else if (actionType.equals("Delete")) { //Delete folder
			try {
				folderUtil.deleteFolder(Integer.parseInt(request.getParameter("FoldID")), user);
			} catch (Exception e) {
				error = e.getMessage();
			}
		}
	}
	
	drawTop(out, et, request, response);
	
	%><script><!--
	function doNewFolder() {
		var newName = prompt("Please enter the folder name", "New Working Folder");
		if (newName) {
			document.NewFoldForm.FoldName.value = newName;
			document.NewFoldForm.submit();
		}
	}<%
	if (error != null) {
		%>alert("<%=error%>");<%
	}
	
	%>//--></script>
	<form name="NewFoldForm" method="post" action="folder_list.jsp">
	<input type="hidden" name="ActionType" value="Add">
	<input type="hidden" name="FoldName" value="">
	</form>
				
	<div id="showInst">
	<table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr>
	<td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td>
	</tr>
	</table>
	</div>
	<div id="inst" style="visibility: hidden; display: none">
	<table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr class="midColour"><th colspan="3">Instructions</th></tr>
	<tr class="lightColour"><td style="text-align: left">
	<ul>
	<li>Data entry can be done either in a <i>Data Entry Folder</i> or using the <i>Data Entry Spreadsheet</i>.</li>
	<li>Data entered via the <i>Data Entry Spreadsheet</i> can be edited within a <i>Data Entry Folder</i> and vice versa.</li>
	<li>Folders to which you have access are listed below and you can create more folders by clicking on the link above.</li>
	<li>Click on the folder name to open it (to add/edit data), or use the actions on the right hand side to edit the folder users or delete the folder.</li>
	</ul>
	</td></tr>
	<tr class="lightColour"><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr>
	</table></div>
		
	<p>
	<table border="0" cellpadding="3" cellspacing="2" width="550">
		
	<tr class="midColour"><td colspan="3"><b>Data Entry Folders</b>&nbsp;&nbsp;&nbsp;Use for entering/editing small amounts of data</td></tr>
	<tr class="midColour"><th style="text-align: left">Folder Name&nbsp;&nbsp;</th><th>Owner&nbsp;&nbsp;</th><th>Actions</th></tr><%
	List<UserFolder> personalFolders = folderUtil.getPersonalFolders(user);
	//List Working folders
	if (personalFolders.size() > 0) {
		%><form name="PersForm" method="post" action="folder_list.jsp"><%
		for (UserFolder folder : personalFolders) {
			%><tr class="lightColour">
			<td style="text-align: left"><a href="folder_detail.jsp?ID=<%=folder.getFolder().getFolderId()%>&q=<%=Math.random()%>" class="heading"><%=folder.getFolder().getName()%></a>&nbsp;&nbsp;</td>
			<td style="text-align: left"><%=(folder.getFolder().getOwner() != null) ? folder.getFolder().getOwner().getFullName() : ""%>&nbsp;&nbsp;</td>
			<td style="text-align: left"><%
			if (folder.isAllowedAdmin()) {
				%><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a>&nbsp;&nbsp;&nbsp;<%
				if (FolderUtil.isFolderEmpty(folder.getFolder())) {
					%><a href="javascript:if (confirm('Are you sure you want to delete this folder') == true) {document.PersForm.FoldID.value='<%=folder.getFolder().getFolderId()%>';document.PersForm.submit();}" title="Delete Folder"><img src="images/delete.gif" border="0" height="20" width="20" /></a><%
				}
			} else {
				%><img src="images/blank.gif" height="20" width="1" alt="" /><%
			}
			%></td></tr><%
		}
		%><input type="hidden" name="ActionType" value="Delete">
		<input type="hidden" name="FoldID" value="">
		</form><%
	} else {
		%><tr class="lightColour"><td colspan="3">You do not currently have any data entry folders. Click <a href="javascript:doNewFolder();">here</a> to create one.</td></tr><%
	}
	
	%><tr><td>&nbsp;</td></tr><tr class="midColour"><td colspan="3"><b>Data Entry Spreadsheet</b>&nbsp;&nbsp;&nbsp;Use for entering/editing large amounts of data</td></tr>
	<tr class="lightColour"><td colspan="3">Download the spreadsheet by clicking on the link 
                below and then choosing "Save" when prompted by your browser  (Note: in some browsers it 
                may be necessary to right-click on the link and choose <i>Save As</i> from the pop-up menu). 
                The spreadsheet has been tested with Microsoft Excel 2002,2007,2010 but should work with 
                all versions.</td></tr>
                <tr class="lightColour">
                    <td colspan="3">
                        <a href="template.xlsx?CODE=FRED_OUTCROP">
                            <img src="images/excel.gif" style=" vertical-align: middle;" border="0" width="20" height="20" alt="Data entry spreadsheet" />&nbsp;&nbsp;
                            <b>Outcrop Import Spreadsheet</b>
                       </a>
                       <span style="float:right;">&nbsp;&nbsp;<b><a href="FRED_static.zip">Static FRED template&nbsp;&nbsp;<img src="images/excel.gif" border="0" width="20" height="20" alt="Data entry spreadsheet" style=" vertical-align: middle;"/></a></span>
                   </td>
                </tr>
                <%--
                <tr class="lightColour">                
                   <td colspan="3">
                        <a href="template.xlsx?CODE=FRED_VERTICAL_SECTION">
                            <img src="images/excel.gif" style=" vertical-align: middle;" border="0" width="20" height="20" alt="Data entry spreadsheet" />&nbsp;&nbsp;
                            <b>Vertical Section Import Spreadsheet</b>
                       </a>
                   </td>
                </tr>--%>
                <tr class="lightColour">                
                   <td colspan="3">
                        <a href="template.xlsx?CODE=FRED_DRILL_HOLE">
                            <img src="images/excel.gif" style=" vertical-align: middle;" border="0" width="20" height="20" alt="Data entry spreadsheet" />&nbsp;&nbsp;
                            <b>Drill Hole Import Spreadsheet</b>
                       </a>
                   </td>
                </tr>
                <tr class="lightColour">                
                   <td colspan="3">
                        <a href="template.xlsx?CODE=FRED_PALEO">
                            <img src="images/excel.gif" style=" vertical-align: middle;" border="0" width="20" height="20" alt="Data entry spreadsheet" />&nbsp;&nbsp;
                            <b>Paleo Import Spreadsheet</b>
                       </a>
                   </td>
                </tr>
	<tr><td>&nbsp;</td></tr>

                <tr class="midColour"><td colspan="3"><b>Upload one of these spreadsheets</b> (but not the static FRED template). </td></tr>
                <tr class="lightColour">     
                     <td colspan="3">
                        <form action="xlsUploader" method="post" enctype="multipart/form-data">
                            <input type="file" name="file" />
                            <input value="Upload" type="submit" />
                        </form>
                     </td>
                 </tr>

        <%
	List<UserFolder> backlogFolders = folderUtil.getBacklogFolders(user);
	//List Backlog folders
	if (backlogFolders.size() > 0) {
		%><tr class="midColour"><th colspan="3">Backlog Edit Folders</th></tr>
		<tr class="midColour"><th style="text-align: left">Folder Name&nbsp;&nbsp;</th><th>Owner&nbsp;&nbsp;</th><th>Actions</th></tr>
		<form name="BackForm" method="post" action="folder_list.jsp"><%
		for (UserFolder folder : backlogFolders) {
			%><tr class="lightColour">
			<td style="text-align: left"><a href="folder_detail.jsp?ID=<%=folder.getFolder().getFolderId()%>&q=<%=Math.random()%>" class="heading"><%=folder.getFolder().getName()%></a>&nbsp;&nbsp;</td>
			<td style="text-align: left"><%=(folder.getFolder().getOwner() != null) ? folder.getFolder().getOwner().getFullName() : ""%>&nbsp;&nbsp;</td>
			<td style="text-align: left"><%
			if (folder.isAllowedAdmin()) {
				%><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a>&nbsp;&nbsp;&nbsp;<%
				if (FolderUtil.isFolderEmpty(folder.getFolder())) {
					%><a href="javascript:if (confirm('Are you sure you want to delete this folder') == true) {document.PersForm.FoldID.value='<%=folder.getFolder().getFolderId()%>';document.PersForm.submit();}" title="Delete Folder"><img src="images/delete.gif" border="0" height="20" width="20" /></a><%
				}
			} else {
				%><img src="images/blank.gif" height="20" width="1" alt="" /><%
			}
			%></td>
			</tr><%
		}
		%><input type="hidden" name="ActionType" value="Delete">
		<input type="hidden" name="FoldID" value="">
		</form>
		<tr><td>&nbsp;</td></tr><%
	}

	//	List Admin & Backlog Admin folders
	List<UserFolder> adminFolders = folderUtil.getAdminFolders(user);
	List<UserFolder> backlogAdminFolders = folderUtil.getBacklogAdminFolders(user);
	if (adminFolders.size() + backlogAdminFolders.size() > 0) {
		%><tr class="midColour"><th colspan="3">Masterfile Folders</th></tr>
		<tr class="midColour"><th colspan="2">Folder Name&nbsp;&nbsp;</th><th>Actions</th></tr><%
		for (UserFolder folder : backlogAdminFolders) {
			%><tr class="lightColour">
			<td style="text-align: left"><a href="backlog_folder_detail.jsp?ID=<%=folder.getFolder().getFolderId()%>&q=<%=Math.random()%>" class="heading"><%=folder.getFolder().getName()%></a>&nbsp;&nbsp;</td>
			<td style="text-align: left; font-size: 10pt; font-weight: bold; color: #FF0000"><%=(folderUtil.getMasterfileFolderFeatureCount(folder.getFolder()) > 0) ? "new data" : ""%>&nbsp;&nbsp;</td>
			<td style="text-align: left;"><%
			if (folder.isAllowedAdmin()) {
				%><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a><%
			}
			%><img src="images/blank.gif" width="1" height="20" />
			</td>
			</tr><%
		}
		for (UserFolder folder : adminFolders) {
			%><tr class="lightColour">
			<td style="text-align: left"><a href="admin_folder_detail.jsp?ID=<%=folder.getFolder().getFolderId()%>&q=<%=Math.random()%>" class="heading"><%=folder.getFolder().getName()%></a>&nbsp;&nbsp;</td>
			<td style="text-align: left; font-size: 10pt; font-weight: bold; color: #FF0000"><%=(folderUtil.getMasterfileFolderFeatureCount(folder.getFolder()) > 0) ? "new data" : ""%>&nbsp;&nbsp;</td>
			<td style="text-align: left;"><%
			if (folder.isAllowedAdmin()) {
				%><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a><%
			}
			%><img src="images/blank.gif" width="1" height="20" />
			</td>
			</tr><%
		}
		%><tr><td>&nbsp;</td></tr><%
	}

	List<TaxonomicGroup> panelList = taxaUtil.getTaxonomicGroupsIsPanelistOf(user);
	//List Taxonomic groups (if any)
	if (panelList.size() > 0) {
		%><tr class="midColour"><th colspan="3">Taxonomic Panels</th></tr>
		<tr class="midColour"><th colspan="2">Group Name&nbsp;&nbsp;</th><th>Actions</th></tr><%
		for (TaxonomicGroup group : panelList) {
			%><tr class="lightColour">
			<td style="text-align: left"><a href="taxa_group_detail.jsp?ID=<%=group.getGroupId()%>&q=<%=Math.random()%>" class="heading"><%=group.getName()%></a>&nbsp;&nbsp;</td>
			<td style="text-align: left; font-size: 10pt; font-weight: bold; color: #FF0000; text-align: left"><%=(taxaUtil.getProvisionalCount(group) > 0) ? "new data" : ""%>&nbsp;&nbsp;</td>
			<td style="text-align: left"><a href="taxa_panelist.jsp?GroupID=<%=group.getGroupId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a></td>
			</tr><%
		}
	}
	
	%></table></p><%
	drawBottom(out, et);
	try {
		FredHibernate.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>