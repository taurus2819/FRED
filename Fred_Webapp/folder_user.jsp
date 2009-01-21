<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.FolderUser"
%><%@page import="nz.cri.gns.fred.model.FolderRight"
%><%@page import="nz.cri.gns.fred.model.FrUserView"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.UserUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="java.io.PrintWriter"
%><%@page import="java.util.List"
%><%@page import="java.util.Vector"
%><%@page import="java.util.HashSet"
%><%@page import="java.util.Set"
%><%@page import="java.util.Collections"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="nz.cri.gns.auth.User"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			FolderUtil folderUtil = new FolderUtil(FredHibernate.get()
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
	FolderUtil folderUtil = new FolderUtil(FredHibernate.get().getDAOFactory());
	UserUtil userUtil = new UserUtil(FredHibernate.get().getDAOFactory());
	User user = (User)getUser(session);

	ExtranetTemplate et = getExtranetTemplate();
	addButtons(et, new IconnedLink[] {
			new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")
		});

	if (request.getParameter("FoldID") != null) {
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);

		boolean canEdit = 
			//User is owner
			(folder.getFolder().getOwner() != null && folder.getFolder().getOwner().getUserId().toString().equals(user.getId()))
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
			%><p>
			<table border="0" cellpadding="3" cellspacing="2" width="550"><%
		try {
			if (folder.getFolder().getFolderType().getName().equals(UserFolder.FOLDER_TYPE_PERSONAL) || folder.getFolder().getFolderType().getName().equals(UserFolder.FOLDER_TYPE_BACKLOG)) {
				%><tr class="midColour"><td class="heading" colspan="99">Folder Owner: <%=(folder.getFolder().getOwner() != null) ? folder.getFolder().getOwner().getFullName() : ""%></span></td></tr><%
			}
			%><tr class="midColour"><td colspan="99">The users listed below have rights to this folder.  Users can be added or deleted from this list and their rights altered by clicking on the <img src="images/ok.gif" width="20" height="20" border="0" /> or <img src="images/cancel.gif" width="20" height="20" border="0" /> icons.</td></tr>
			<tr class="midColour"><th>User&nbsp;&nbsp;</th><th width="60" style="width=60px; text-align=center">Read</th><%
			for (FolderRight rightType : rightTypes) {
				%><th width="60" style="width=60px; text-align=center"><%=rightType.getRightDescription()%></th><%
			}
			Set<FrUserView> excludeFrUsers = new HashSet<FrUserView>();
			if (folder.getFolder().getOwner() != null)
				excludeFrUsers.add(folder.getFolder().getOwner());
			List<FolderUser> folderUsers = new Vector<FolderUser>(folder.getFolder().getFolderUsers());
			Collections.sort(folderUsers);
			for (FolderUser folderUser : folderUsers) {
				excludeFrUsers.add(folderUser.getUser());
				%><tr class="lightColour"><td><%=folderUser.getUser().getFullName()%>&nbsp;&nbsp;</td>
				<td style="text-align: center;"><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>&ActionType=DeleteUser&UserID=<%=folderUser.getUser().getUserId()%>"><img src="images/ok.gif" width="20" height="20" border="0" alt="Delete User" /></a></td><%
				for (FolderRight rightType : rightTypes) {
					%><td style="text-align: center;"><a href="folder_user.jsp?FoldID=<%=folder.getFolder().getFolderId()%>&ActionType=ChangeRight&UserID=<%=folderUser.getUser().getUserId()%>&Right=<%=rightType.getCode()%>"><%
					if ((folderUser.getUserRights().intValue() & rightType.getCode()) != 0) {
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
			<tr class="lightColour"><td><%
			SelectBox<FrUserView> selectBox = new SelectBox<FrUserView>(userUtil.getActiveFrWritersWithout(excludeFrUsers));
			Attributes attributes = Attributes.createNameOnlyAttributes("UserID");
			selectBox.writeBox(attributes, "-- Choose --", null, (FrUserView)null, new PrintWriter(out));
			%>&nbsp;&nbsp;</td><td style="text-align: center;"><a href="#" onClick="UserForm.submit();"><img src="images/cancel.gif" width="20" height="20" border="0" alt="Add User" /></a></td><%
			for (int i = 0; i < rightTypes.size(); i++) {
				%><td>&nbsp;</td><%
			}
			%></tr>
			</form>
			
			</table>
			</p><%
		} catch (Exception e) {
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
