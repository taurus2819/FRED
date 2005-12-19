<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Taxon"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="java.util.Iterator"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			TaxonomicUtil taxaUtil = new TaxonomicUtil(HibernateUtil.get().getDAOFactory());
			TaxonomicGroup group = taxaUtil.getTaxonomicGroup(Integer.parseInt(request.getParameter("GroupID")));
			return "FRED :: " + group.getName() + " Thesaurus";
		} catch (Exception e) {
			return "FRED :: The Fossil Record Electronic Database";
		}
	}
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

		drawTop(out, et, request, response);
		
		if (taxaUtil.isUserMemberOf(group, user)) {
			
			if (request.getParameter("ActionType") != null && request.getParameter("TaxaID") != null) { //do something
				String actionType = request.getParameter("ActionType");
				Taxon taxon = taxaUtil.getTaxon(Integer.parseInt(request.getParameter("TaxonID")));
				if (actionType.equals("Approve")) { //approve taxa
					taxaUtil.approveTaxon(taxon, user);
				} else if (actionType.equals("Reject")) { //reject taxa
					taxaUtil.rejectTaxon(taxon, user, request.getParameter("RejComments"));
				} else if (actionType.equals("Obsolete")) {
					taxaUtil.obsoleteTaxon(taxon, user);
				}
			}

			if (taxaUtil.getTaxaCount(group, FREDConstants.PROVISIONAL) > 0) {
				%><p>&nbsp;</p><center><p><%
				startDETable(pageContext);
				%><table border="0" width="550"><tr><td colspan="19" class="deHeading">Provisional Entries</td></tr>
				<tr><th style="text-align: left">Name&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th style="text-align: left">Submitted By&nbsp;&nbsp;</th><th style="text-align: left">Submitted Date&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Options</th></tr><%
				for (Iterator i = taxaUtil.getTaxa(group, FREDConstants.PROVISIONAL).iterator(); i.hasNext(); ) {
					Taxon taxon = (Taxon) i.next();
					%><tr><td class="heading" style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getSubmittedById() != null) ? FREDUtil.getUserName(taxon.getSubmittedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td><%
					//out.println("<td><a href='taxa_group_detail.jsp?ID=" + panel.getPanelID() + "&ActionType=Approve&TaxaID=" + tl.getTaxaID() + "'><img src='images/ok.gif' border='0' height='20' width='20' alt='approve' />&nbsp;</td><td><a href='taxa_group_detail.jsp?ID=" + panel.getPanelID() + "&ActionType=Reject&TaxaID=" + tl.getTaxaID() + "'><img src='images/cancel.gif' border='0' height='20' width='20' alt='Reject' /></a></td></tr>");
				}
				%></table><%
				endDETable(pageContext);
				%></p></center><%
			}
			
			if (taxaUtil.getTaxaCount(group, FREDConstants.REJECTED) > 0) {
				%><p>&nbsp;</p><center><p><%
				startDETable(pageContext);
				%><table border="0" width="550"><tr><td colspan="19" class="deHeading">Rejected Entries</td></tr>
				<tr><th style="text-align: left">Name&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th style="text-align: left">Rejected By&nbsp;&nbsp;</th><th style="text-align: left">Rejected Date&nbsp;&nbsp;</th></tr><%
				for (Iterator i = taxaUtil.getTaxa(group, FREDConstants.REJECTED).iterator(); i.hasNext(); ) {
					Taxon taxon = (Taxon) i.next();
					%><tr><td class="heading" style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? FREDUtil.getUserName(taxon.getApprovedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getApprovedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td><%
				}
				%></table><%
				endDETable(pageContext);
				%></p></center><%
			}
			
			if (taxaUtil.getTaxaCount(group, FREDConstants.APPROVED) > 0) {
				%><p>&nbsp;</p><center><p><%
				startDETable(pageContext);
				%><table border="0" width="550"><tr><td colspan="19" class="deHeading">Approved Entries</td></tr>
				<tr><th style="text-align: left">Name&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th style="text-align: left">Approved By&nbsp;&nbsp;</th><th style="text-align: left">Approved Date&nbsp;&nbsp;</th><th style="text-align: left">Options&nbsp;&nbsp;</th></tr><%
				for (Iterator i = taxaUtil.getTaxa(group, FREDConstants.APPROVED).iterator(); i.hasNext(); ) {
					Taxon taxon = (Taxon) i.next();
					%><tr><td class="heading" style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? FREDUtil.getUserName(taxon.getApprovedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getApprovedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><a href="taxa_group_detail_hib.jsp?GroupID=<%=group.getGroupId()%>&TaxonID=<%=taxon.getTaxaId()%>&ActionType=Obsolete"><img src="images/delete.gif" height="20" width="20" border="0" alt="Make Obsolete" /></a><%
				}
				%></table><%
				endDETable(pageContext);
				%></p></center><%
			}

			if (taxaUtil.getTaxaCount(group, FREDConstants.OBSOLETE) > 0) {
				%><p>&nbsp;</p><center><p><%
				startDETable(pageContext);
				%><table border="0" width="550"><tr><td colspan="19" class="deHeading">Obsolete Entries</td></tr>
				<tr><th style="text-align: left">Name&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th></tr><%
				for (Iterator i = taxaUtil.getTaxa(group, FREDConstants.OBSOLETE).iterator(); i.hasNext(); ) {
					Taxon taxon = (Taxon) i.next();
					%><tr><td class="heading" style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td><%
				}
				%></table><%
				endDETable(pageContext);
				%></p></center><%
			}

		}
	}

	drawBottom(out, et);

%>


