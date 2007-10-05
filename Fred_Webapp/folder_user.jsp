<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.FolderUser"
%><%@page import="nz.cri.gns.fred.model.FolderRight"
%><%@page import="nz.cri.gns.fred.model.FrUserView"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.UserUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="java.io.PrintWriter"
%><%@page import="java.util.List"
%><%@page import="java.util.HashSet"
%><%@page import="java.util.Set"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="nz.cri.gns.auth.User"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			FolderUtil folderUtil = new FolderUtil(HibernateUtil.get()
					.getDAOFactory());
			UserFolder folder = folderUtil.getUserFolder(Integer
					.parseInt(request.getParameter("FoldID")), getUser(request
					.getSession()));
			return "FRED :: " + folder.getFolder().getName() + " users";
		} catch (StorageAccessException e) {
			return "FRED";
		}
	}

%><%
	FolderUtil folderUtil = new FolderUtil(HibernateUtil.get().getDAOFactory());
	UserUtil userUtil = new UserUtil(HibernateUtil.get().getDAOFactory());
	User user = (User)getUser(session);

	ExtranetTemplate et = getExtranetTemplate();
	addButtons(et, new IconnedLink[] {
			new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")
		});

	if (request.getParameter("FoldID") != null) {
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);

		boolean canEdit = 
			//User is owner
			(folder.getFolder().getOwnerId() != null && folder.getFolder().getOwnerId().toString().equals(user.getId()))
			//User has admin rights
			|| folder.isAllowedAdmin();
		
		if (canEdit) { 

			//process any changes
			if (request.getParameter("ActionType") != null) {
				String actionType = request.getParameter("ActionType");
				int userId = Integer.parseInt(request.getParameter("UserID"));
				if (actionType.equals("AddUser")) {
					folderUtil.addUserToFolder(folder, userId, 1);
				} else if (actionType.equals("DeleteUser")) {
					folderUtil.removeUserFromFolder(folder, userId);
					//if user deleted themselves then redirect to folder list
					folder = folderUtil.getUserFolder(folder.getFolderId().intValue(), user);
					if (folder == null) {
						response.sendRedirect("folder_list.jsp");
						return;
					}
				}
				else if (actionType.equals("ChangeRight")) {
					folderUtil.toggleUserFolderRights(folder, userId, Integer.parseInt(request.getParameter("Right")));
					//if user removed admin rights then redirect to folder list
					folder = folderUtil.getUserFolder(folder.getFolderId().intValue(), user);
					if (!folder.isAllowedAdmin()) {
						response.sendRedirect("folder_list.jsp");
						return;
					}
				}
			}
	
			drawTop(out, et, request, response);

			List<FolderRight> rightTypes = folderUtil.getRightTypesForDisplay(folder);
			%><p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="19" class="deHeading"><%=folder.getFolderName()%> users</td></tr><%
try {
			if (folder.getFolder().getFolderType().getName().equals(UserFolder.FOLDER_TYPE_PERSONAL) || folder.getFolder().getFolderType().getName().equals(UserFolder.FOLDER_TYPE_BACKLOG)) {
				%><tr><td class="heading" colspan="19">Folder Owner: <%=(folder.getFolder().getFrUserView() != null) ? folder.getFolder().getFrUserView().getFullName() : ""%></span></td></tr>
				<tr><td>&nbsp;</td></tr><%
			}
			%><tr><td colspan="19">The users listed below have rights to this folder.  Users can be added or deleted from this list and their rights altered by clicking on the <img src="images/ok.gif" width="20" height="20" border="0" /> or <img src="images/cancel.gif" width="20" height="20" border="0" /> icons.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">User&nbsp;&nbsp;</td><td width="60" class="heading" style="width=60px; text-align=center">Read</td><%
			for (FolderRight rightType : rightTypes) {
				%><td width="60" class="heading" style="width=60px; text-align=center"><%=rightType.getRightDescription()%></td><%
			}
			Set<FrUserView> excludeFrUsers = new HashSet<FrUserView>();
			if (folder.getFolder().getOwnerId() != null)
				excludeFrUsers.add(userUtil.getFrUserView(folder.getFolder().getOwnerId()));
			for (FolderUser folderUser : folder.getFolder().getFolderUsers()) {
				FrUserView frUser = userUtil.getFrUserView(folderUser.getUserId());
				excludeFrUsers.add(frUser);
				%><tr><td><%=frUser.getFullName()%>&nbsp;&nbsp;</td>
				<td style="text-align: center;"><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>&ActionType=DeleteUser&UserID=<%=frUser.getUserId()%>"><img src="images/ok.gif" width="20" height="20" border="0" alt="Delete User" /></a></td><%
				for (FolderRight rightType : rightTypes) {
					%><td style="text-align: center;"><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>&ActionType=ChangeRight&UserID=<%=frUser.getUserId()%>&Right=<%=rightType.getRightCode()%>"><%
					if ((folderUser.getUserRights().intValue() & rightType.getRightCode()) != 0) {
						%><img src="images/ok.gif" alt="Remove Right"height="20" width="20" border="0"  /><%
					} else {
						%><img src="images/cancel.gif" alt="Add Right"height="20" width="20" border="0"  /><%
					}
					%></a></td><%
				}
				%></tr><%
			}
			
			%><form name="UserForm" method="post" action="folder_user.jsp">
			<input type="hidden" name="FoldID" value="<%=folder.getFolder().getFolderId()%>" />
			<input type="hidden" name="ActionType" value="AddUser" />
			<tr><td><%
			SelectBox<FrUserView> selectBox = new SelectBox<FrUserView>(userUtil.getFrUsersWithout(excludeFrUsers));
			Attributes attributes = Attributes.createNameOnlyAttributes("UserID");
			selectBox.writeBox(attributes, "-- Choose --", null, (FrUserView)null, new PrintWriter(out));
			%>&nbsp;&nbsp;</td><td style="text-align: center;"><a href="#" onClick="UserForm.submit();"><img src="images/cancel.gif" width="20" height="20" border="0" alt="Add User" /></a></td></tr>
			</form>
			
			</table><%
			endDETable(pageContext);
			%></td></tr></table></p><%
} catch (Exception e) {
	out.println("Error");
	e.printStackTrace();
}
		}
		else { //no rights
			%><p>Access denied</p>Either there is no folder matching the ID you entered or you have insufficient rights to edit the folder.  Click <a href='index.jsp' class='fname'>here</a> to return to the FRED home page.<%
		}
	} else {
		drawTop(out, et, request, response);
	}

	drawBottom(out, et);
	folderUtil.closeSession();

%>
