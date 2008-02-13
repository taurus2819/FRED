<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="java.util.Iterator"
%><%@page import="java.util.List"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: " + ((User)getUser(request.getSession())).getFullName() + "'s Folders";
	}
	
%><%
	try {
		User user = (User)getUser(session);
		
		DAOFactory factory = HibernateUtil.get().getDAOFactory();
		FolderUtil folderUtil = new FolderUtil(factory);
		TaxonomicUtil taxaUtil = new TaxonomicUtil(factory);
		
		ExtranetTemplate et = getExtranetTemplate();
		et.setDisplayLoadingMessage(true);
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
		<table border="0" width="550" style="border: none; width: 550px">
		<tr>
		<td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td>
		</tr>
		</table>
		</div>
		<div id="inst" style="visibilty: hidden; display: none"><%
		
		startDETable(pageContext);
		%><table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
		<tr><td colspan="3" class="deHeading">Instructions</td></tr><tr><td style="text-align: left">
		<ul>
		<li>Data entry can be done either in a <i>Data Entry Folder</i> or using the <i>Data Entry Spreadsheet</i>.</li>
		<li>Data entered via the <i>Data Entry Spreadsheet</i> can be edited within a <i>Data Entry Folder</i> and vice versa.</li>
		<li>Folders to which you have access are listed below and you can create more folders by clicking on the link above.</li>
		<li>Click on the folder name to open it (to add/edit data), or use the actions on the right hand side to edit the folder users or delete the folder.</li>
		</ul>
		</td></tr>
		<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table><%
		endDETable(pageContext);
		%></div><%
		
		%><p><%
		startDETable(pageContext);
		%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Data Entry Folders</td></tr>
		<tr><td colspan="3">Use for entering/editing small amounts of data</td></tr>
		<tr><td>&nbsp;</td></tr><%
		List personalFolders = folderUtil.getPersonalFolders(user);
		//List Working folders
		if (personalFolders.size() > 0) {
			%><tr><th style="text-align: left">Folder Name&nbsp;&nbsp;</th><th>Owner&nbsp;&nbsp;</th><th>Actions</th></tr>
			<form name="PersForm" method="post" action="folder_list.jsp"><%
			for (Iterator i = personalFolders.iterator(); i.hasNext(); ) {
				UserFolder folder = (UserFolder) i.next();
				%><tr><td style="text-align: left"><a href="folder_detail.jsp?ID=<%=folder.getFolder().getFolderId()%>&q=<%=Math.random()%>" class="heading"><%=folder.getFolder().getName()%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(folder.getFolder().getFrUserView() != null) ? folder.getFolder().getFrUserView().getFullName() : ""%>&nbsp;&nbsp;</td>
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
			%><tr><td class="heading">You do not currently have any data entry folders. Click <a href="javascript:doNewFolder();">here</a> to create one.</td></tr><%
		}
		%></table><%
		endDETable(pageContext);
		%></p><%
	
		%><p><%
		startDETable(pageContext);
		%><table border="0" width="550"><tr><td colspan="2" class="deHeading">Data Entry Spreadsheet</td></tr>
		<tr><td colspan="3">Use for entering/editing large amounts of data</td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td colspan="2">Download the spreadsheet by clicking on the link below and then choosing "Save" when prompted by your browser. The spreadsheet has been tested with Microsoft Excel 2002, but should work with all versions.  Full instructions are included in the spreadsheet.</td></tr>
		<tr><td colspan="2" style="color: #ff0000;">January 2008: A bug has been discovered in the code for saving taxonomic lists in version 2.1 of the spreadsheet.  Please use the latest version which you can download below.</td></tr>
		<tr><td><a href="FRED.xlt"><img src="images/excel.gif" border="0" width="20" height="20" alt="Data entry spreadsheet" /></a>&nbsp;&nbsp;</td>
		<td class="heading"><a href="FRED.xlt">Download FRED Excel template</a></td></tr>
		</table><%
		endDETable(pageContext);
		%></p><%
		
		List backlogFolders = folderUtil.getBacklogFolders(user);
		//List Backlog folders
		if (backlogFolders.size() > 0) {
			%><p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Backlog Edit Folders</td></tr>
			<tr><th style="text-align: left">Folder Name&nbsp;&nbsp;</th><th>Owner&nbsp;&nbsp;</th><th>Actions</th></tr>
			<form name="BackForm" method="post" action="folder_list.jsp"><%
			for (Iterator i = backlogFolders.iterator(); i.hasNext(); ) {
				UserFolder folder = (UserFolder) i.next();
				%><tr>
				<td style="text-align: left"><a href="folder_detail.jsp?ID=<%=folder.getFolder().getFolderId()%>&q=<%=Math.random()%>" class="heading"><%=folder.getFolder().getName()%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=(folder.getFolder().getFrUserView() != null) ? folder.getFolder().getFrUserView().getFullName() : ""%>&nbsp;&nbsp;</td>
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
			</table><%
			endDETable(pageContext);
			%></p><%
		}
	
		//	List Admin & Backlog Admin folders
		List adminFolders = folderUtil.getAdminFolders(user);
		List backlogAdminFolders = folderUtil.getBacklogAdminFolders(user);
		if (adminFolders.size() + backlogAdminFolders.size() > 0) {
			%><p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Masterfile Folders</td></tr>
			<tr><th colspan="2">Folder Name&nbsp;&nbsp;</th><th>Actions</th></tr><%
			for (Iterator i = backlogAdminFolders.iterator(); i.hasNext(); ) {
				UserFolder folder = (UserFolder) i.next();
				%><tr>
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
			for (Iterator i = adminFolders.iterator(); i.hasNext(); ) {
				UserFolder folder = (UserFolder) i.next();
				%><tr>
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
			%></table><%
			endDETable(pageContext);
			%></p><%
		}
	
		List panelList = taxaUtil.getTaxonomicGroupsIsPanelistOf(user);
		//List Taxonomic groups (if any)
		if (panelList.size() > 0) {
			%><p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Taxonomic Panels</td></tr>
			<tr><th colspan="2">Group Name&nbsp;&nbsp;</th><th>Actions</th></tr><%
			for (Iterator i = panelList.iterator(); i.hasNext(); ) {
				TaxonomicGroup group = (TaxonomicGroup)i.next();
				%><tr>
				<td style="text-align: left"><a href="taxa_group_detail.jsp?ID=<%=group.getGroupId()%>&q=<%=Math.random()%>" class="heading"><%=group.getName()%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left font-size: 10pt; font-weight: bold; color: #FF0000; text-align: left"><%=(taxaUtil.getProvisionalCount(group) > 0) ? "new data" : ""%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><a href="taxa_panelist.jsp?GroupID=<%=group.getGroupId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a></td>
				</tr><%
			}
			%></table><%
			endDETable(pageContext);
			%></p><%
		}
	
		%></table>
	
		</td></tr></table><%
		drawBottom(out, et);
	
		//Close the session
		folderUtil.closeSession();
	
	} catch (Throwable t) {
		t.printStackTrace();
	}
%>