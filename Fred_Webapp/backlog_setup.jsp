<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.fred.dao.StorageAccessException"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="java.util.Iterator"
%><%@page import="java.util.List"
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
	UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), user);

	//Process any incoming requests
	if (request.getParameter("folderTo") != null) {
		UserFolder folderTo = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("folderTo")), user);
		if (!folderTo.isAllowedEditLocalities()) {
			factory.closeSession();
			response.sendRedirect("folder_list.jsp");
			return;
		}
		featureUtil.addToBacklog(folderTo, request.getParameter("mapSheet"), Integer.parseInt(request.getParameter("start")), Integer.parseInt(request.getParameter("end")), folder, user);
	}
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	et.setButtons(new IconnedLink[] {
			new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")});

	drawTop(out, et, request, response);

	if (folder.isAllowedReadLocalities()) {
		
		%><script><!--
function showHide(toShow, toHide) {
	document.getElementById(toShow).style.display = 'block';
	document.getElementById(toHide).style.display = 'none';
}
//--></script>
<center><p>&nbsp;<p/><div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none">
<%
		startDETable(pageContext);
		%><table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
<tr><td colspan="3" class="deHeading">Backlog Setup Instructions</td></tr><tr><td style="text-align: left">
<ul>
<li>You can add a subset of this masterfile's localities for backlog entry
<li>Choose the folder to add to, the map sheet and fossil record number range to add
</ul>
</td></tr>
<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table>
<%
		endDETable(pageContext);
		%></div>
<p>

<%
		startDETable(pageContext);
		List personalFolders = folderUtil.getBacklogFolders(user);

		%><form name="addForm" action="backlog_setup.jsp" method="post">
<input type="hidden" name="ID" value="<%=folder.getFolderId()%>">
<table border="0" width="550"><tr><td colspan="2" class="deHeading">Localities to add</td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td>Folder to which to add</td><td><select name="folderTo"><%
		for (Iterator it = personalFolders.iterator(); it.hasNext(); ) {
			UserFolder userfolder = (UserFolder)it.next();
			if (userfolder.isAllowedEditLocalities()) {
				%><option value="<%=userfolder.getFolderId()%>"><%=userfolder.getFolderName()%></option>
<%
			}
		}
		%></select></td></tr>
<tr><td>Map sheet</td><td><input name="mapSheet" maxlength="3" ></td></tr>
<tr><td>Start FR number</td><td>f<input name="start" maxlength="4"></td></tr>
<tr><td>End FR number</td><td>f<input name="end" maxlength="4"></td></tr>
<tr><td colspan="2" style="text-align: right"><input type="submit" value="Add to backlog folder"></td></tr>
</table><%
		endDETable(pageContext);
		%></form>
<%
	}
	else { //no record found
		out.println("No folder found");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
} catch (Exception e) {
	e.printStackTrace();
} finally {
	//Close the session
	factory.closeSession();
}
%>