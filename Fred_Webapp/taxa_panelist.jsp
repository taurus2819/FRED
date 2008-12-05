<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.fred.model.FrUserView"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.fred.util.UserUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="java.io.PrintWriter"
%><%
	TaxonomicUtil taxaUtil = new TaxonomicUtil(FredHibernate.get().getDAOFactory());
	UserUtil userUtil = new UserUtil(FredHibernate.get().getDAOFactory());
	User user = (User)getUser(session);

	ExtranetTemplate et = getExtranetTemplate();
	et.setUseNavigationColumn(false);
	addButtons(et, new IconnedLink[] {new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")});

	if (request.getParameter("GroupID") != null) {
		TaxonomicGroup group = taxaUtil.getTaxonomicGroup(Integer.parseInt(request.getParameter("GroupID")));
		if (group != null && taxaUtil.isUserPanelistOf(group, user)) {
			
			drawTop(out, et, request, response);

			//process any changes
			if (request.getParameter("ActionType") != null) {
				String actionType = request.getParameter("ActionType");
				FrUserView frUser = userUtil.getFrUserView(new Integer(request.getParameter("UserID")));
				if (actionType.equals("Add")) {
					taxaUtil.addPanelistToTaxonomicGroup(group, frUser);
				}
				else if (actionType.equals("Delete")) {
					taxaUtil.removePanelistFromTaxonomicGroup(group, frUser);
				}
			}
			
			%><p>
			<form name="AddForm" method="post" action="taxa_panelist.jsp">
			<input type="hidden" name="GroupID" value="<%=group.getGroupId()%>" />
			<input type="hidden" name="ActionType" value="Add" /><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="19" class="deHeading"><%=group.getName()%> Panel users</td></tr>
			<tr><td colspan="2">
			The users listed below are on the panel for this taxonomic group and may accept or reject new entries to the thesaurus.<br />Users can be added or deleted from this list by clicking on the <img src='images/ok.gif' width='20' height='20' border='0' /> or <img src='images/cancel.gif' width='20' height='20' border='0' /> icons.
			</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr class="heading"><td style="text-align: left">User&nbsp;&nbsp;</td><td width="60" style="text-align: left">Member</td></tr><%
			for (FrUserView panelist :  group.getPanelists()) {
				%><tr><td style="text-align: left"><%=panelist.getFullName()%>&nbsp;&nbsp;</td><td style="text-align: left"><a href="taxa_panelist.jsp?GroupID=<%=group.getGroupId()%>&ActionType=Delete&UserID=<%=panelist.getUserId().intValue()%>"><img src="images/ok.gif" border="0" height="20" width="20" alt="Delete User" /></a></td></tr><%
			}
			%><tr><td style="text-align: left"><%
			SelectBox<FrUserView> selectBox = new SelectBox<FrUserView>(userUtil.getActiveFrWritersWithout(group.getPanelists()));
			Attributes attributes = Attributes.createNameOnlyAttributes("UserID");
			%><tr><td>&nbsp;</td></tr><tr><td style="text-align: left"><%
			selectBox.writeBox(attributes, "-- Choose --", null, (FrUserView)null, new PrintWriter(out));
			%>&nbsp;&nbsp;</td><td style="text-align: left"><a href="#" onClick="AddForm.submit();"><img src="images/cancel.gif" border="0" height="20" width="20" alt="Add User" /></a></td></tr>
			</table><%
			endDETable(pageContext);
			%></form></p><%
		}
		else { //no rights
			%><p>Access denied</p>Either there is no folder matching the ID you entered or you have insufficient rights to edit the folder.  Click <a href='index.jsp' class='fname'>here</a> to return to the FRED home page.<%
		}
	}

	drawBottom(out, et);
%>
