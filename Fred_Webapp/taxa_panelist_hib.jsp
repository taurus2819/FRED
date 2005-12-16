<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.db.ComboDescriptor"
%><%@page import="java.util.Iterator"
%><%
	TaxonomicUtil taxaUtil = new TaxonomicUtil(HibernateUtil.get().getDAOFactory());
	User user = (User)getUser(session);

	ExtranetTemplate et = getExtranetTemplate();
	et.setUseNavigationColumn(false);
	et.setButtons(new IconnedLink[] {
			new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")
		});

	if (request.getParameter("GroupID") != null) {
		TaxonomicGroup group = taxaUtil.getTaxonomicGroup(Integer.parseInt(request.getParameter("GroupID")));
		if (group != null && taxaUtil.isUserMemberOf(group, user)) {
			
			drawTop(out, et, request, response);
			
			%><p><span class='bigheading'><%=group.getName()%></span></p><%

			//process any changes
			
			if (request.getParameter("ActionType") != null) {
				String actionType = request.getParameter("ActionType");
				int userId = Integer.parseInt(request.getParameter("UserID"));
				if (actionType.equals("Add")) {
					taxaUtil.addUserToPanel(group, userId);
				}
				else if (actionType.equals("Delete")) {
					taxaUtil.removeUserFromPanel(group, userId);
				}
			}
			
			%><p>The users listed below are on the panel for this taxonomic group and may accept or reject new entries to the thesaurus.<br />Users can be added or deleted from this list by clicking on the <img src='images/ok.gif' width='20' height='20' border='0' /> or <img src='images/cancel.gif' width='20' height='20' border='0' /> icons.</p>
			<p><table border="0" cellspacing="0" cellpadding="2">
			<tr class="heading"><td>User&nbsp;&nbsp;</td><td width="60" style="text-align: center">Member</td></tr>

			<form name="AddForm" method="get" action="taxa_panelist_hib.jsp">
			<input type="hidden" name="GroupID" value="<%=group.getGroupId()%>" />
			<input type="hidden" name="ActionType" value="Add" /><%

			for (Iterator i = taxaUtil.getMembersOfPanel(group).iterator(); i.hasNext();) {
				int memberId = ((Integer) i.next()).intValue();
				%><tr><td><%=FREDUtil.getUserName(memberId)%>&nbsp;&nbsp;</td><td style="text-align: center"><a href="taxa_panelist_hib.jsp?GroupID=<%=group.getGroupId()%>&ActionType=Delete&UserID=<%=memberId%>"><img src="images/ok.gif" border="0" height="20" width="20" alt="Delete User" /></a></td></tr><%
			}
			
			%><tr><td><%
			ComboDescriptor cd = new ComboDescriptor("FR_User_View", "PE_ID", "Full_Name");
			cd.name = "UserID";
			cd.orderBy = "Family_Name";
			cd.join = "NOT PE_ID IN (SELECT Panelist_ID FROM Taxa_Panel WHERE Group_ID = " + group.getGroupId() + ")";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%>&nbsp;&nbsp;</td><td style="text-align: center"><a href="#" onClick="AddForm.submit();"><img src="images/cancel.gif" border="0" height="20" width="20" alt="Add User" /></a></td></tr>
			</form>


			</table></p><%
		}
		else { //no rights
			out.println("<p><span class='subhead'>Access denied</span></p>Either there is no folder matching the ID you entered or you have insufficient rights to edit the folder.  Click <a href='index.jsp' class='fname'>here</a> to return to the FRED home page.");
		}
	}

	drawBottom(out, et);
%>
