<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.*"
%><%@page import="nz.cri.gns.fred.dao.StorageAccessException"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.FolderRight"
%><%@page import="nz.cri.gns.fred.model.FolderAccessor"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.db.*"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="java.util.List"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.auth.*"
%><%!public String getName(HttpServletRequest request) {
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

	protected IconnedLink[] getButtons(HttpServletRequest request) {
		return new IconnedLink[] {
			new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")
		};
	}

%><%
	FolderUtil folderUtil = new FolderUtil(HibernateUtil.get().getDAOFactory());
	User user = (User)getUser(session);

	ExtranetTemplate et = getExtranetTemplate();

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
				}
				if (actionType.equals("DeleteUser")) {
					folderUtil.removeUserFromFolder(folder, userId);
				}
				else if (actionType.equals("ChangeRight")) {
					folderUtil.toggleUserFolderRights(folder, userId, Integer.parseInt(request.getParameter("Right")));
				}
			}
	
			drawTop(out, et, request, response);

			List rightTypes = folderUtil.getRightTypesForDisplay(folder);	
			boolean isPersonal = folder.getFolder().getFolderType().getName().equals("Personal");
			%><center><p>&nbsp;<p/><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="19" class="deHeading"><%=folder.getFolderName()%> users</td></tr>
<tr><td><%

			if (isPersonal) {
				out.println("<p><span class='bigheading'>Folder: " + folder.getFolderName() + "</span><br>");
				out.println("<span class='heading'>Owner: " + FREDUtil.getUserName(folder.getFolder().getOwnerId().intValue()) + "</span></p>");
			} else {
				out.println("<p><span class='bigheading'>Masterfile: " + folder.getFolderName() + "</span></p>");
			}

			out.println("<p>The users listed below have rights to this folder.<br>Users can be added or deleted from this list and their rights altered by clicking on the <img src='images/ok.gif' width='20' height='20' border='0' /> or <img src='images/cancel.gif' width='20' height='20' border='0' /> icons.</p>");

			out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
			out.print("<tr class='heading' align='center'><td align='left'>User&nbsp&nbsp</td><td width='60'>Read</td>");
			for (Iterator it = rightTypes.iterator(); it.hasNext(); ) {
				%><td width="60"><%=((FolderRight)it.next()).getRightDescription()%></td><%
			}
			%><tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr><%

			List users = folderUtil.getNonOwningUsers(folder);
			String foldID = folder.getFolder().getFolderId().toString();
			for (Iterator it = users.iterator(); it.hasNext(); ) {
				FolderAccessor folderUser = (FolderAccessor)it.next();
				%><tr><td><%=folderUser.getUserName()%>&nbsp;&nbsp;</td>
<td align="center"><a href="folder_user.jsp?FoldID=<%=foldID%>&ActionType=DeleteUser&UserID=<%=folderUser.getUserId()%>" title="Delete User"><img src="images/ok.gif" width="20" height="20" border="0" /></a></td>
<%
				for (Iterator it1 = rightTypes.iterator();  it1.hasNext(); ) {
					FolderRight rightType = (FolderRight)it1.next();
					out.print("<td align='center'><a href='folder_user.jsp?FoldID=" + foldID + "&ActionType=ChangeRight&UserID=" + folderUser.getUserId() + "&Right=");
					if ((folderUser.getUserRights().intValue() & rightType.getRightCode()) != 0) {
						out.print((rightType.getRightCode() * -1) + "' title='Remove Right'><img src='images/ok.gif'");
					} else {
						out.print(rightType.getRightCode() + "' title='Add Right'><img src='images/cancel.gif'");
					}
					out.print(" width='20' height='20' border='0' /></a></td>");
				}
				out.println("</tr>");
			}
			
			out.println("<form name='UserForm' method='post' action='folder_user.jsp'>");
			out.println("<input type='hidden' name='FoldID' value='" + foldID + "'>");
			out.println("<input type='hidden' name='ActionType' value='AddUser'>");
			out.print("<tr><td>");
			ComboDescriptor cd = new ComboDescriptor("FR_User_View", "PE_ID", "Full_Name");
			cd.name = "UserID";
			cd.orderBy = "Family_Name";
			cd.join = "NOT PE_ID IN (SELECT User_ID FROM Folder_View WHERE Folder_ID = " + foldID + ")";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			out.println("&nbsp&nbsp</td><td align='center'><a href='#' onClick='UserForm.submit();' title='Add User'><img src='images/cancel.gif' width='20' height='20' border='0' /></a></td></tr>");
			out.println("</form>");
			
			out.println("</table></p>");
			out.println("</table>");
			endDETable(pageContext);
			out.println("</td></tr></table>");
		}
		else { //no rights
			out.println("<p><span class='subhead'>Access denied</span></p>Either there is no folder matching the ID you entered or you have insufficient rights to edit the folder.  Click <a href='index.jsp' class='fname'>here</a> to return to the FRED home page.");
		}
	} else {
		drawTop(out, et, request, response);
		drawEndNavigation(out);
	}

	drawBottom(out, et);
	folderUtil.closeSession();
%>
