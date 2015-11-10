<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.fred.model.FrUserView"
%><%@page import="nz.cri.gns.fred.model.ConfidentialGroup"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.UserUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="java.io.PrintWriter"
%><%@page import="java.net.URLEncoder"
%><%!
	public String getName(HttpServletRequest request) {
		if (request.getParameter("GroupID") == null)
			return "FRED :: Manage User Groups";
		try {
			ConfidentialGroup group = new AuditUtil(FredHibernate.get().getDAOFactory()).getConfidentialGroup(new Integer(request.getParameter("GroupID")));
			return "FRED :: Manage " + group.getName();
		} catch (Exception e) {
			return "FRED :: Manage User Groups"; 
		}
	}
%><%
	UserUtil userUtil = new UserUtil(FredHibernate.get().getDAOFactory());
	AuditUtil auditUtil = new AuditUtil(FredHibernate.get().getDAOFactory());
	User user = (User)getUser(session);
	String backURL = request.getParameter("backURL");

	ExtranetTemplate et = getExtranetTemplate();
	
	if (request.getParameter("GroupID") == null) {
		addButtons(et, new IconnedLink[] {
				new IconnedLink(backURL + "&q=" + Math.random(), "images/back_arrow.gif", "Back to Set Confidentiality"),
				new IconnedLink("javascript:doNewGroup();", "images/lock.gif", "New User Group")		
		});
		
		if (request.getParameter("ActionType") != null) { //do something
			String actionType = request.getParameter("ActionType");
			if (actionType.equals("Add")) //add folder
				auditUtil.addConfidentialGroup(request.getParameter("GroupName"), user);
			else if (actionType.equals("Delete")) //Delete group
				auditUtil.deleteConfidentialGroup(Integer.parseInt(request.getParameter("GrpID")), user);
		}
		
		drawTop(out, et, request, response);
		
		%><script><!--
		function doNewGroup() {
			var newName = prompt("Please enter the group name", "New User Group");
			if (newName) {
				document.NewGroupForm.GroupName.value = newName;
				document.NewGroupForm.submit();
			}
		}
	
		//--></script>
		<form name="NewGroupForm" method="post" action="manage_confid_groups.jsp">
		<input type="hidden" name="ActionType" value="Add" />
		<input type="hidden" name="backURL" value="<%=backURL%>" />
		<input type="hidden" name="GroupName" value="" />
		</form><%
		FrUserView frUser = userUtil.getFrUserView(new Integer(user.getId()));
		%><p><%
		startDETable(pageContext);
		%><table border="0" width="550">
		<tr><td class="deHeading" colspan="2">User Groups</td></tr>
		<tr><th>User Group Name&nbsp;&nbsp;</th><th>Actions</th></tr><%
		for (ConfidentialGroup group : frUser.getConfidGroupsByOwnerId()) {
			%><tr>
			<td><a href="manage_confid_groups.jsp?GroupID=<%=group.getGroupId()%>&backURL=<%=URLEncoder.encode(backURL, "ISO-8859-1")%>"><%=group.getName()%></a>&nbsp;&nbsp;</td>
			<td><a href="manage_confid_groups.jsp?ActionType=Delete&GrpID=<%=group.getGroupId()%>&backURL=<%=URLEncoder.encode(backURL, "ISO-8859-1")%>"><img src="images/delete.gif" height="20" width="20" border="0" alt="Delete" /></a></td>
			</tr><%
		}
		%></table><%
		endDETable(pageContext);
		%></p>
		</form><%
	} else {
		addButtons(et, new IconnedLink[] {
				new IconnedLink("manage_confid_groups.jsp", "images/back_arrow.gif", "Back to Group List"),
				new IconnedLink(backURL + "&q=" + Math.random(), "images/back_arrow.gif", "Back to Set Confidentiality")
		});
		drawTop(out, et, request, response);
		ConfidentialGroup group = auditUtil.getConfidentialGroup(new Integer(request.getParameter("GroupID")));
		
		//process any changes
		if (request.getParameter("ActionType") != null) {
			String actionType = request.getParameter("ActionType");
			if (actionType.equals("Add"))
				auditUtil.addUserToConfidGroup(group, userUtil.getFrUserView(new Integer(request.getParameter("UserID"))));
			else if (actionType.equals("Delete"))
				auditUtil.removeUserFromConfidGroup(group, userUtil.getFrUserView(new Integer(request.getParameter("UserID"))));
		}
		
		%><p>
		<form name="AddForm" method="post" action="manage_confid_groups.jsp">
		<input type="hidden" name="GroupID" value="<%=group.getGroupId()%>" />
		<input type="hidden" name="backURL" value="<%=backURL%>" />
		<input type="hidden" name="ActionType" value="Add" /><%
		startDETable(pageContext);
		%><table border="0" width="550">
		<tr><td class="deHeading" colspan="2">Members of <%=group.getName()%></td></tr><%
		for (FrUserView frUser : group.getUsers()) {
			%><tr><td style="text-align: left"><%=frUser.getFullName()%>&nbsp;&nbsp;</td><td style="text-align: left"><a href="manage_confid_groups.jsp?GroupID=<%=group.getGroupId()%>&ActionType=Delete&UserID=<%=frUser.getUserId()%>&backURL=<%=URLEncoder.encode(backURL, "ISO-8859-1")%>"><img src="images/cancel.gif" border="0" height="20" width="20" alt="Delete User" /></a></td></tr><%
		}
		%><tr><td style="text-align: left"><%
		SelectBox<FrUserView> selectBox = new SelectBox<FrUserView>(userUtil.getActiveFrWritersWithout(group.getUsers()));
		Attributes attributes = Attributes.createNameOnlyAttributes("UserID");
		%><tr><td>&nbsp;</td></tr><tr><td style="text-align: left"><%
		selectBox.writeBox(attributes, "-- Choose --", null, (FrUserView)null, new PrintWriter(out));
		%>&nbsp;&nbsp;</td><td style="text-align: left"><a href="#" onClick="AddForm.submit();"><img src="images/ok.gif" border="0" height="20" width="20" alt="Add User" /></a></td></tr>
		</table><%
		endDETable(pageContext);
		%></form>
		</p><%
	}

	drawBottom(out, et);
%>
