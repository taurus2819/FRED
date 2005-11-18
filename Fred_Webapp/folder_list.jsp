<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="java.util.Iterator"
%><%@page import="java.util.List"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.fred.IconnedLink"
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
	
	protected IconnedLink[] getButtons(HttpServletRequest request) {
		return new IconnedLink[] {
			new IconnedLink("javascript:doNewFolder();", "images/folder.gif", "New Folder"),
			new IconnedLink("javascript:doNewBacklogFolder();", "images/folder.gif", "New Backlog Folder")
		};
	}
	
%><%
try {
	User user = (User)getUser(session);
	
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	FolderUtil folderUtil = new FolderUtil(factory);
	TaxonomicUtil taxaUtil = new TaxonomicUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	String error = null;
	if (request.getParameter("ActionType") != null) { //do something
		String actionType = request.getParameter("ActionType");
		if (actionType.equals("Add")) { //add folder
			if (request.getParameter("FoldType") != null && request.getParameter("FoldType").equals("Backlog")) {
				folderUtil.addBacklogFolder(request.getParameter("FoldName"), user);
			} else {
				folderUtil.addFolder(request.getParameter("FoldName"), user);
			}
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
	var newName = prompt('Please enter the folder name', 'New Working Folder');
	if (newName) {
		document.NewFoldForm.FoldName.value = newName;
		document.NewFoldForm.FoldType.value = "Personal";
		document.NewFoldForm.submit();
	}
}

function doNewBacklogFolder() {
	var newName = prompt('Please enter the folder name', 'New Backlog Folder');
	if (newName) {
		document.NewFoldForm.FoldName.value = newName;
		document.NewFoldForm.FoldType.value = "Backlog";
		document.NewFoldForm.submit();
	}
}
<%
	if (error != null) {
		%>
alert("<%=error%>");<%
	}
	%>
//--></script>
<form name="NewFoldForm" method="post" action="folder_list.jsp">
<input type="hidden" name="ActionType" value="Add">
<input type="hidden" name="FoldName" value="">
<input type="hidden" name="FoldType" value="">
</form>
<center><p>&nbsp;<p/><div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none">
<%
	startDETable(pageContext);
	%>
<table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
<tr><td colspan="3" class="deHeading">Instructions</td></tr><tr><td style="text-align: left">
<ul>
<li>All data entry is done within a folder.
<li>Folders to which you have access are listed below and you can create more folders by clicking on the button above.
<li>Click on the folder name to view its contents, or use the options on the right hand side to edit the folder properties or delete the folder.
</ul>
</td></tr>
<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table>
<%
	endDETable(pageContext);
	%></div>
<p>

<%
	startDETable(pageContext);
	%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Personal Folders</td></tr><%
	List personalFolders = folderUtil.getPersonalFolders(user);

	//List Working folders
	if (personalFolders.size() > 0) {
		%>
		<tr><th style="text-align: left">Working Folder&nbsp;&nbsp;</th><th>Owner&nbsp;&nbsp;</th><th>Options</th></tr>
		<tr><td><img src="images/blank.gif" height="5" width="1" /></td></tr>
		<form name="PersForm" method="post" action="folder_list.jsp">
<%
		for (Iterator i = personalFolders.iterator(); i.hasNext(); ) {
			UserFolder folder = (UserFolder) i.next();
			%><tr><td style="text-align: left"><a href="folder_detail.jsp?ID=<%=folder.getFolder().getFolderId()%>" class="heading"><%=folder.getFolder().getName()%></a>&nbsp;&nbsp;</td><td style="text-align: left"><%=FREDUtil.getUserName(folder.getFolder().getOwnerId().intValue())%>&nbsp;&nbsp;</td><td style="text-align: left">
<%
			if (folder.isAllowedAdmin()) {
				%><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a>&nbsp;&nbsp;&nbsp;<a href="javascript:if (confirm('Are you sure you want to delete this folder') == true) {document.PersForm.FoldID.value='<%=folder.getFolder().getFolderId()%>';document.PersForm.submit();}" title="Delete Folder"><img src="images/delete.gif" border="0" height="20" width="20" /></a>
<img src="images/blank.gif" width="1" height="20" /><%
			}
			%></td></tr>
<%
		}
		%>
<input type="hidden" name="ActionType" value="Delete">
<input type="hidden" name="FoldID" value="">
</form>
<tr><td>&nbsp;</td></tr>
</table>
<%
	} else {
		%>You do not currently have any personal folders<%
	}
	endDETable(pageContext);


	List backlogFolders = folderUtil.getBacklogFolders(user);
	//List Backlog folders
	if (backlogFolders.size() > 0) {
		%><p><%
		startDETable(pageContext);
		%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Backlog Folders</td></tr>
		<tr><th style="text-align: left">BacklogFolder&nbsp;&nbsp;</th><th>Owner&nbsp;&nbsp;</th><th>Options</th></tr>
		<tr><td><img src="images/blank.gif" height="5" width="1" /></td></tr>
		<form name="BackForm" method="post" action="folder_list.jsp">
<%
		for (Iterator i = backlogFolders.iterator(); i.hasNext(); ) {
			UserFolder folder = (UserFolder) i.next();
			%><tr><td style="text-align: left"><a href="folder_detail.jsp?ID=<%=folder.getFolder().getFolderId()%>" class="heading"><%=folder.getFolder().getName()%></a>&nbsp;&nbsp;</td><td style="text-align: left"><%=FREDUtil.getUserName(folder.getFolder().getOwnerId().intValue())%>&nbsp;&nbsp;</td><td style="text-align: left">
<%
			if (folder.isAllowedAdmin()) {
				%><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a>&nbsp;&nbsp;&nbsp;<a href="javascript:if (confirm('Are you sure you want to delete this folder') == true) {document.BackForm.FoldID.value='<%=folder.getFolder().getFolderId()%>';document.BackForm.submit();}" title="Delete Folder"><img src="images/delete.gif" border="0" height="20" width="20" /></a>
<img src="images/blank.gif" width="1" height="20" /><%
			}
			%></td></tr>
<%
		}
		%>
<input type="hidden" name="ActionType" value="Delete">
<input type="hidden" name="FoldID" value="">
</form>
<tr><td>&nbsp;</td></tr>
</table>
<%
		endDETable(pageContext);
	}

	List adminFolders = folderUtil.getAdminFolders(user);

	//List Admin folders
	if (adminFolders.size() > 0) {
		%><p><%
		startDETable(pageContext);
		%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Masterfile Folders</td></tr>
<tr><th>Masterfile Folder&nbsp;&nbsp;</th><th></th><th>Options</th></tr>
<tr><td colspan="3"><img src="images/blank.gif" height="5" width="1" /></td></tr><%
		for (Iterator i = adminFolders.iterator(); i.hasNext(); ) {
			UserFolder folder = (UserFolder) i.next();
			%><tr><td style="text-align: left"><a href="admin_folder_detail.jsp?ID=<%=folder.getFolder().getFolderId()%>" class="heading"><%=folder.getFolder().getName()%></a>&nbsp;&nbsp;</td>
<td style="text-align: left; font-size: 10pt; font-weight: bold; color: #FF0000"><%
			if (folderUtil.getMasterfileFolderFeatureCount(folder.getFolder()) > 0)
				out.print("new data");
			out.print("&nbsp;</td><td style=\"text-align: left;\">");
			if (folder.isAllowedAdmin()) {
				%><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a>
<img src="images/blank.gif" width="1" height="20" /><%
			}
			%></td></tr>
<%
		}
		%></table>
<%
		endDETable(pageContext);
	}

	List panelList = taxaUtil.getPanelsIsMemberOf(user);

	//List Taxonomic groups (if any)
	if (panelList.size() > 0) {
		%><p><%
		startDETable(pageContext);
		%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Taxonomic Panels</td></tr>
<tr><th>Taxonomic Groups<img src="images/blank.gif" width="20" height="1" /></th><td></td></th><th>Options</th></tr>
<tr><td><img src="images/blank.gif" height="5" width="1" /></td></tr><%
		for (Iterator i = panelList.iterator(); i.hasNext(); ) {
			TaxonomicGroup group = (TaxonomicGroup)i.next();

			%><tr><td style="text-align: left"><a href="taxa_group_detail.jsp?ID=<%=group.getGroupId()%>" class="heading"><%=group.getName()%></a><img src="images/blank.gif" width="20" height="1" /></td><td style="font-size: 10pt; font-weight: bold; color: #FF0000; text-align: left">
<%
			if (taxaUtil.getProvisionalCount(group) > 0)
				out.print("new data");
			%><img src="images/blank.gif" width="10" height="1" /></td><td style="text-align: left"><a href="taxa_panelist.jsp?GroupID=<%=group.getGroupId()%>" title="Edit Users"><img src="images/prefs.gif" border="0" height="20" width="20" /></a></td></tr>
<%
		}
		%></table>
<%
		endDETable(pageContext);
	}

	out.println("</table>");

	out.println("</td></tr></table>");
	drawBottom(out, et);

	//Close the session
	folderUtil.closeSession();
} catch (Throwable t) {
	t.printStackTrace();
}
%>