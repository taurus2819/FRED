<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.*"
%><%@page import="nz.cri.gns.db.*"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.db.ComboDescriptor"
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
			
			out.println("<p><span class='bigheading'>" + group.getName() + "</span></p>");

			//process any changes
			/*
			if (request.getParameter("ActionType") != null) {
				String actionType = request.getParameter("ActionType");
				if (actionType.equals("Add")) {
					execUp = statement.executeUpdate("INSERT INTO Taxa_Panel (Group_ID, Panelist_ID) VALUES (" + groupID + ", " + request.getParameter("UserID") + ")");
					response.sendRedirect("taxa_panelist.jsp?GroupID=" + groupID);
				}
				else if (actionType.equals("Delete")) {
					execUp = statement.executeUpdate("DELETE FROM Taxa_Panel WHERE Group_ID = " + groupID + " AND Panelist_ID = " + request.getParameter("UserID"));
					response.sendRedirect("taxa_panelist.jsp?GroupID=" + groupID);
				}
			} */

			out.println("<p>The users listed below are on the panel for this taxonomic group and may accept or reject new entries to the thesaurus.<br />Users can be added or deleted from this list by clicking on the <img src='images/ok.gif' width='20' height='20' border='0' /> or <img src='images/cancel.gif' width='20' height='20' border='0' /> icons.</p>");

			out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
			out.println("<tr class='heading'><td>User&nbsp&nbsp</td><td width='60' align='center'>Member</td></tr>");
			out.println("<tr><td><img src='images/blank.gif width='1' height='5' /></td></tr>");
			out.println("<form name='AddForm' method='post' action='taxa_panelist.jsp'>");
			out.println("<input type='hidden' name='GroupID' value='" + group.getGroupId() + "'>");
			out.println("<input type='hidden' name='ActionType' value='Add'>");
			

			rs = statement.executeQuery("SELECT Panelist_ID, Panelist_Name FROM Taxa_Panel_View WHERE Group_ID = " + groupID);
			while (rs.next()) {
				out.print("<tr><td>" + rs.getString(2) + "<img src='images/blank.gif' width='20' height='1' /></td><td align='center'><a href='taxa_panelist.jsp?GroupID=" + groupID + "&ActionType=Delete&UserID=" + rs.getString(1) + "' title='Delete User'><img src='images/ok.gif' border='0' height='20' width='20' /></a></td></tr>");
			}
			
			
			out.print("<tr><td>");
			ComboDescriptor cd = new ComboDescriptor("FR_User_View", "PE_ID", "Full_Name");
			cd.name = "UserID";
			cd.orderBy = "Family_Name";
			cd.join = "NOT PE_ID IN (SELECT Panelist_ID FROM Taxa_Panel WHERE Group_ID = " + group.getGroupId() + ")";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			out.println("<img src='images/blank.gif' width='20' height='1' /></td><td align='center'><a href='#' onClick='AddForm.submit();' title='Add User'><img src='images/cancel.gif' border='0' height='20' width='20' /></a></td></tr>");
			out.println("</form>");


			out.println("</table></p>");
		}
		else { //no rights
			out.println("<p><span class='subhead'>Access denied</span></p>Either there is no folder matching the ID you entered or you have insufficient rights to edit the folder.  Click <a href='index.jsp' class='fname'>here</a> to return to the FRED home page.");
		}
	}

	drawBottom(out, et);

%>
