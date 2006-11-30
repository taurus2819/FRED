<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.User"
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
	addButtons(et, new IconnedLink[] {
			new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")});

	drawTop(out, et, request, response);

	if (folder.isAllowedReadLocalities()) {
		List<UserFolder> personalFolders = folderUtil.getBacklogFolders(user);
		%><form name="addForm" action="backlog_setup.jsp" method="post">
		<input type="hidden" name="ID" value="<%=folder.getFolderId()%>"><%
		startDETable(pageContext);
		%><table border="0" width="550">
		<tr><td colspan="2" class="deHeading">Localities to Add to Backlog Folder</td></tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr><td>Folder</td><td><select name="folderTo"><%
		for (UserFolder userFolder : personalFolders) {
			if (userFolder.isAllowedEditLocalities()) {
				%><option value="<%=userFolder.getFolderId()%>"><%=userFolder.getFolderName()%></option><%
			}
		}
		%></select></td></tr>
		<tr><td>Map sheet</td><td><input name="mapSheet" maxlength="3" ></td></tr>
		<tr><td>Start FR number</td><td>f<input name="start" maxlength="4"></td></tr>
		<tr><td>End FR number</td><td>f<input name="end" maxlength="4"></td></tr>
		<tr><td colspan="2" style="text-align: right"><input type="submit" value="Add to backlog folder"></td></tr>
		</table><%
		endDETable(pageContext);
		%></form><%
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