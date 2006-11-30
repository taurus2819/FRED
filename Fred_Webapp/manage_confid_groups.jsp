<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.fred.model.FrUserView"
%><%@page import="nz.cri.gns.fred.model.ConfidentialGroup"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.UserUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="java.io.PrintWriter"
%><%!
	public String getName(HttpServletRequest request) {
		if (request.getParameter("GroupID") == null)
			return "FRED :: Manage User Groups";
		try {
			ConfidentialGroup group = new AuditUtil(HibernateUtil.get().getDAOFactory()).getConfidentialGroup(new Integer(request.getParameter("GroupID")));
			return "FRED :: Manage " + group.getName();
		} catch (Exception e) {
			return "FRED :: Manage User Groups"; 
		}
	}
%><%
	UserUtil userUtil = new UserUtil(HibernateUtil.get().getDAOFactory());
	User user = (User)getUser(session);

	ExtranetTemplate et = getExtranetTemplate();
	
	if (request.getParameter("GroupID") == null) {
		addButtons(et, new IconnedLink[] {new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")});
		drawTop(out, et, request, response);
		FrUserView frUser = userUtil.getFrUserView(new Integer(user.getId()));
		%><p><%
		startDETable(pageContext);
		%><table border="0" width="550">
		<tr><td class="deHeading">User Groups</td></tr><%
		for (ConfidentialGroup group : frUser.getConfidGroupsByOwnerId()) {
			%><tr><td><a href="manage_confid_groups.jsp?GroupID=<%=group.getGroupId()%>"><%=group.getName()%></a></td></tr><%
		}
		%></table><%
		endDETable(pageContext);
		%></p><%
	} else {
		addButtons(et, new IconnedLink[] {
				new IconnedLink("manage_confid_groups.jsp", "images/back_arrow.gif", "Back to Group List"),
				new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")
		});
		drawTop(out, et, request, response);
		AuditUtil auditUtil = new AuditUtil(HibernateUtil.get().getDAOFactory());
		ConfidentialGroup group = auditUtil.getConfidentialGroup(new Integer(request.getParameter("GroupID")));
		
		//process any changes
		if (request.getParameter("ActionType") != null) {
			String actionType = request.getParameter("ActionType");
			if (actionType.equals("Add")) {
				auditUtil.addUserToConfidGroup(group, userUtil.getFrUserView(new Integer(request.getParameter("UserID"))));
			}
			else if (actionType.equals("Delete")) {
				auditUtil.removeUserFromConfidGroup(group, userUtil.getFrUserView(new Integer(request.getParameter("UserID"))));
			}
		}
		
		%><p>
		<form name="AddForm" method="post" action="manage_confid_groups.jsp">
		<input type="hidden" name="GroupID" value="<%=group.getGroupId()%>" />
		<input type="hidden" name="ActionType" value="Add" /><%
		startDETable(pageContext);
		%><table border="0" width="550">
		<tr><td class="deHeading" colspan="2">Members of Group</td></tr><%
		for (FrUserView frUser :  group.getUsers()) {
			%><tr><td style="text-align: left"><%=frUser.getFullName()%>&nbsp;&nbsp;</td><td style="text-align: left"><a href="manage_confid_groups.jsp?GroupID=<%=group.getGroupId()%>&ActionType=Delete&UserID=<%=frUser.getUserId()%>"><img src="images/ok.gif" border="0" height="20" width="20" alt="Delete User" /></a></td></tr><%
		}
		%><tr><td style="text-align: left"><%
		SelectBox<FrUserView> selectBox = new SelectBox<FrUserView>(userUtil.getFrUsersWithout(group.getUsers()));
		Attributes attributes = Attributes.createNameOnlyAttributes("UserID");
		%><tr><td>&nbsp;</td></tr><tr><td style="text-align: left"><%
		selectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
		%>&nbsp;&nbsp;</td><td style="text-align: left"><a href="#" onClick="AddForm.submit();"><img src="images/cancel.gif" border="0" height="20" width="20" alt="Add User" /></a></td></tr>
		</table><%
		endDETable(pageContext);
		%></form>
		</p><%
	}

	drawBottom(out, et);
%>
