<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.model.Taxon"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.auth.User"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			TaxonomicUtil taxaUtil = new TaxonomicUtil(HibernateUtil.get().getDAOFactory());
			TaxonomicGroup group = taxaUtil.getTaxonomicGroup(Integer.parseInt(request.getParameter("ID")));
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
	addButtons(et, new IconnedLink[] {
			new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders")
		});
	
	if (request.getParameter("ID") != null) {
		TaxonomicGroup group = taxaUtil.getTaxonomicGroup(Integer.parseInt(request.getParameter("ID")));

		drawTop(out, et, request, response);
		
		if (taxaUtil.isUserPanelistOf(group, user)) {

			if (request.getParameter("ActionType") != null && request.getParameter("TaxonID") != null) { //do something
				String actionType = request.getParameter("ActionType");
				Taxon taxon = taxaUtil.getTaxon(Integer.parseInt(request.getParameter("TaxonID")));
				if (actionType.equals("Approve")) { //approve taxon
					taxaUtil.approveTaxon(taxon, user, request.getParameter("Comments"));
				} else if (actionType.equals("Reject")) { //reject taxon
					taxaUtil.rejectTaxon(taxon, user, request.getParameter("Comments"));
				} else if (actionType.equals("Delete")) {
					taxaUtil.deleteTaxon(taxon, user);
				} else if (actionType.equals("Obsolete")) {  //obsolete taxon
					taxaUtil.obsoleteTaxon(taxon, user);
				}
			}

			if (taxaUtil.getTaxaCount(group, FREDConstants.PROVISIONAL) > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" width="800"><tr><td colspan="6" class="deHeading">Provisional Entries</td></tr>
				<tr><th style="text-align: left">Name&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th style="text-align: left">Submitted By&nbsp;&nbsp;</th><th style="text-align: left">Submitted Date&nbsp;&nbsp;</th><th style="text-align: left">Locality(s)</th><th style="text-align: left">Actions</th></tr><%
				for (Taxon taxon : taxaUtil.getTaxa(group, FREDConstants.PROVISIONAL)) {
					%><form name="taxonForm<%=taxon.getTaxaId()%>" method="post" action="taxa_group_detail.jsp">
					<input type="hidden" name="ID" value="<%=group.getGroupId()%>" />
					<input type="hidden" name="TaxonID" value="<%=taxon.getTaxaId()%>" />
					<input type="hidden" name="ActionType" value="" />
					<tr>
					<td class="heading" style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getSubmittedById() != null) ? taxon.getSubmittedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td><%
					if (!FREDUtil.isEmpty(taxon.getListEntries())) {
						%><td><%
						for (PaleontologyListEntry palList : taxon.getListEntries()) {
							try {
								Feature feature = palList.getPaleontology().getRecord().getSample().getFeature();
								%><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>" target="feat"><%=FeatureUtil.getFeatureIdentifyingName(feature)%></a>&nbsp;&nbsp;<br /><%
							} catch (Exception e) {}						}
						%></td><%
					} else {
						%><td></td><%
					}
					%><td style="text-align: left"><a href="#" ocClick="taxonForm<%=taxon.getTaxaId()%>.ActionType.value='Approve';taxonForm<%=taxon.getTaxaId()%>.submit();"><img src="images/ok.gif" border="0" height="20" width="20" alt="approve" /></a>
					    &nbsp;&nbsp;<a href="#" onClick="taxonForm<%=taxon.getTaxaId()%>.ActionType.value='Reject';taxonForm<%=taxon.getTaxaId()%>.submit();"><img src="images/cancel.gif" border="0" height="20" width="20" alt="Reject" /></a><br />
						<textarea name="Comments" cols="20" rows="3"></textarea></td></tr>
					</tr>
					</form><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
			
			if (taxaUtil.getTaxaCount(group, FREDConstants.REJECTED) > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" width="800"><tr><td colspan="7" class="deHeading">Rejected Entries</td></tr>
				<tr><th style="text-align: left">Name&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th style="text-align: left">Rejected By&nbsp;&nbsp;</th><th style="text-align: left">Rejected Date&nbsp;&nbsp;</th><th style="text-align: left">Comments&nbsp;&nbsp;</th><th style="text-align: left">Locality(s)&nbsp;&nbsp;</th><th style="text-align: left">Actions</th></tr><%
				for (Taxon taxon : taxaUtil.getTaxa(group, FREDConstants.REJECTED)) {
					%><tr>
					<td class="heading" style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? taxon.getApprovedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getApprovedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getPanelistComments())%>&nbsp;&nbsp;</td><%
					if (!FREDUtil.isEmpty(taxon.getListEntries())) {
						%><td><%
						for (PaleontologyListEntry palList : taxon.getListEntries()) {
							try {
								Feature feature = palList.getPaleontology().getRecord().getSample().getFeature();
								%><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>" target="feat"><%=FeatureUtil.getFeatureIdentifyingName(feature)%></a>&nbsp;&nbsp;<br /><%
							} catch (Exception e) {}
						}
						%></td><%						
					} else {
						%><td></td><td style="text-align: left"><a href="taxa_group_detail.jsp?ID=<%=group.getGroupId()%>&TaxonID=<%=taxon.getTaxaId()%>&ActionType=Delete"><img src="images/delete.gif" height="20" width="20" border="0" alt="Delete" /></a></td><%
					}
					%><tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
			
			if (taxaUtil.getTaxaCount(group, FREDConstants.APPROVED) > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" width="800"><tr><td colspan="5" class="deHeading">Approved Entries</td></tr>
				<tr><th style="text-align: left">Name&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th style="text-align: left">Approved By&nbsp;&nbsp;</th><th style="text-align: left">Approved Date&nbsp;&nbsp;</th><th style="text-align: left">Actions</th></tr><%
				for (Taxon taxon : taxaUtil.getTaxa(group, FREDConstants.APPROVED)) {
					%><tr>
					<td class="heading" style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? taxon.getApprovedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getApprovedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><a href="taxa_group_detail.jsp?ID=<%=group.getGroupId()%>&TaxonID=<%=taxon.getTaxaId()%>&ActionType=Obsolete"><img src="images/delete.gif" height="20" width="20" border="0" alt="Make Obsolete" /></a>
					</tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}

			if (taxaUtil.getTaxaCount(group, FREDConstants.OBSOLETE) > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" width="800"><tr><td colspan="2" class="deHeading">Obsolete Entries</td></tr>
				<tr><th style="text-align: left">Name&nbsp;&nbsp;</th><th style="text-align: left">Author</th></tr><%
				for (Taxon taxon : taxaUtil.getTaxa(group, FREDConstants.OBSOLETE)) {
					%><tr>
					<td class="heading" style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					</tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}

		}
	}

	drawBottom(out, et);

%>


