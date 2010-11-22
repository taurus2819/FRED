<%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.ConfidentialGroup"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.Date"
%><%@page import="java.util.Calendar"
%><%@page import="java.util.GregorianCalendar"
%><%@page import="java.util.List"
%><%@page import="java.util.Vector"
%><%@page import="java.io.PrintWriter"
%><%@page import="java.net.URLEncoder"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Set Confidentiality";
	}

%><%
	PageState state = new PageState(request, response, getServletContext());
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	User user = (User)getUser(session);
	FolderUtil folderUtil = new FolderUtil(factory);
	AuditUtil auditUtil = new AuditUtil(factory);
	TaxonomicUtil taxonomicUtil = new TaxonomicUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	et.setUseNavigationColumn(false);
	addButtons(et, new IconnedLink[] {
			new IconnedLink((String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "&q=" + Math.random(), "images/back_arrow.gif", "Back"),
			new IconnedLink("manage_confid_groups.jsp?backURL=" + URLEncoder.encode("set_confidentiality.jsp?ID=" + request.getParameter("ID") + "&RecType=" + request.getParameter("RecType") + "&FoldID=" + request.getParameter("FoldID"), "ISO-8859-1"), "images/edit.gif", "Manage User Groups")
		});

	drawTop(out, et, request, response);
		
	if (request.getParameter("AuditIDs") != null && "Update".equals(request.getParameter("Action"))) {
		try {
			auditUtil.updateConfidentiality(request.getParameterValues("AuditIDs"), request.getParameter("confidType"), request.getParameter("confidPeriod"), request.getParameter("confidLapseEmail"), request.getParameterValues("confidGroups"));
			if (request.getParameter("PalListAuditIDs") != null) {
				auditUtil.updateConfidentiality(request.getParameterValues("PalListAuditIDs"), request.getParameter("palConfidType"), request.getParameter("palConfidPeriod"), request.getParameter("palConfidLapseEmail"), request.getParameterValues("palConfidGroups"));
			}
			response.sendRedirect((String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "&q=" + Math.random());
			return;
		} catch (Exception e) {
			System.out.println("********** FRED confidentiality error: " + new java.util.Date());
			e.printStackTrace();
		}
	}
	
	if (request.getParameter("SampIDs") != null || request.getParameter("RecIDs") != null) {
		SampleUtil sampleUtil = new SampleUtil(factory);
		RecordUtil recordUtil = new RecordUtil(factory);
		UserFolder folder = null;
		try {
			folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
		} catch (Exception e) {	}
		List<Audit> audits = new Vector<Audit>();
		List<Audit> palListAudits = new Vector<Audit>();
		if (request.getParameter("SampIDs") != null) {
			for (String sampId : request.getParameterValues("SampIDs")) {
				Sample sample = sampleUtil.getSample(Integer.parseInt(sampId));
				if (sampleUtil.isAllowedEditSampleConfid(user, sample, folder))
					audits.add(sample.getAudit());
			}
		}
		if (request.getParameter("RecIDs") != null) {
			for (String recId : request.getParameterValues("RecIDs")) {
				Record record = recordUtil.getRecord(Integer.parseInt(recId));
				if (recordUtil.isAllowedEditRecordConfid(user, record, folder)) {
					audits.add(record.getAudit());
					if (RecordUtil.getRecordType(record).equals(FREDConstants.PALEONTOLOGICAL))
						palListAudits.add(record.getPalListAudit());
				}
			}
		}
		
		if (audits.size() > 0) {
			Audit audit = audits.get(0);
			Audit palListAudit = (palListAudits.size() > 0) ? palListAudits.get(0) : null;
			%><p><%
			startDETable(pageContext);
			%><table border="0" width="550">
			<tr><td class="deHeading">Instructions</td></tr>
			<tr><td>You may set this data to be <i>Open</i> or <i>Confidential</i>.<%
			if (palListAudits.size() > 0) {
				%>  You may set the confidentiality of the taxonomic lists seperately.<%
			}
			%><ul>
			<li>If set to <i>Open</i> any registered user will be able to view the data (after it has been submitted/approved).</li>
			<li>If set to <i>Confidential</i> only you (the submitter) plus any member of the groups you have selected will be able to view the data.  You must also select a time period that the data will remain confidential.  At the end of this period you will be notified and may increase the period or the data will automatically become <i>open</i>.</li>
			</ul>
			Note: Confidentiality can be set at sample (for drillholes and vertical sections), record and taxonomic list levels.  Locality data can not be set to confidential, but is only accessible to logged in users.  Confidentiality is inherited down the levels, so for example setting a sample as confidential will also mean any records for that sample will also be confidential.  You can increase the level of confidentiality down the levels, but not decrease it - for example you can set a sample to be open, and a particular paleontology record to be confidential, but not the other way around.
			</td></tr>
			</table><%
			endDETable(pageContext);
			%></p>
			
			<form name="confidForm" method="post" action="set_confidentiality.jsp"><%
			for (Audit a : audits) {
				%><input type="hidden" name="AuditIDs" value="<%=a.getAuditId()%>" /><%
			}
			for (Audit a : palListAudits) {
				%><input type="hidden" name="PalListAuditIDs" value="<%=a.getAuditId()%>" /><%
			}			
			if (folder != null) {
				%><input type="hidden" name="FoldID" value="<%=folder.getFolderId()%>" /><%
			}
			%><input type="hidden" name="Action" value="Update">
						
			<p><%
			startDETable(pageContext);
			%><table border="0" width="550">
			<tr><td colspan="4" class="deHeading">Confidentiality Options</td></tr>
			<tr><td><input type="radio" name="confidType" value="open" <%=(audit.getConfidentialFlag() == null || !audit.getConfidentialFlag()) ? "checked" : ""%> /></td><td style="text-align:left" class="heading">Open</td></tr>
			<tr><td><input type="radio" name="confidType" value="confid" <%=(audit.getConfidentialFlag() != null && audit.getConfidentialFlag()) ? "checked" : ""%> /></td><td style="text-align:left" class="heading">Confidential&nbsp;&nbsp;</td>
			<td style="text-align:left" class="heading">Confidential Period:&nbsp;</td>
			<td><select name="confidPeriod"><%
			double confidPeriod = (audit.getConfidPeriod() != null) ? audit.getConfidPeriod().doubleValue() : 1;
			if (confidPeriod != 0.5 && confidPeriod != 2 && confidPeriod != 5)
				confidPeriod = 1;
			%><option value="0.5"<%=(confidPeriod == 0.5) ? " selected" : ""%>>6 months</option>
			<option value="1" <%=(confidPeriod == 1) ? " selected" : ""%>>1 year</option>
			<option value="2"<%=(confidPeriod == 2) ? " selected" : ""%>>2 years</option>
			<option value="5"<%=(confidPeriod == 5) ? " selected" : ""%>>5 years</option>
			</select></td></tr>
			<tr><td colspan="4">This data will be restricted to me and the following groups</td></tr><%
			for (ConfidentialGroup confidGroup : auditUtil.getConfidentialGroups(user)) {
				%><tr><td><input type="checkbox" name="confidGroups" value="<%=confidGroup.getGroupId()%>"<%
				if (!FREDUtil.isEmpty(audit.getConfidGroups()) && audit.getConfidGroups().contains(confidGroup)) {
					%> checked<%
				}
				%> /></td><td style="text-align:left"><%=confidGroup.getName()%></td></tr><%
			}
			%><tr><td colspan="4" class="heading">Alternative email address (to notify when confidentiality is expiring)&nbsp;&nbsp;&nbsp;<input type="text" name="confidLapseEmail" value="<%=DBUtils.nvl(audit.getConfidLapseEmail())%>" /></td></tr><%
			
			if (palListAudits.size() > 0) {
				%><tr><td>&nbsp;</td></tr>
				<tr><td colspan="4" class="deHeading">Taxonomic List Confidentiality Options</td></tr>
				<tr><td><input type="radio" name="palConfidType" value="open" <%=(palListAudit.getConfidentialFlag() == null || !palListAudit.getConfidentialFlag()) ? "checked" : ""%> /></td><td style="text-align:left" class="heading">Open</td></tr>
				<tr><td><input type="radio" name="palConfidType" value="confid" <%=(palListAudit.getConfidentialFlag() != null && palListAudit.getConfidentialFlag()) ? "checked" : ""%> /></td><td style="text-align:left" class="heading">Confidential&nbsp;&nbsp;</td>
				<td style="text-align:left" class="heading">Confidential Period:&nbsp;</td>
				<td><select name="palConfidPeriod"><%
				confidPeriod = (palListAudit.getConfidPeriod() != null) ? palListAudit.getConfidPeriod().doubleValue() : 1;
				if (confidPeriod != 0.5 && confidPeriod != 2 && confidPeriod != 5)
					confidPeriod = 1;
				%><option value="0.5"<%=(confidPeriod == 0.5) ? " selected" : ""%>>6 months</option>
				<option value="1" <%=(confidPeriod == 1) ? " selected" : ""%>>1 year</option>
				<option value="2"<%=(confidPeriod == 2) ? " selected" : ""%>>2 years</option>
				<option value="5"<%=(confidPeriod == 5) ? " selected" : ""%>>5 years</option>
				</select></td></tr>
				<tr><td colspan="4">This taxonomic list will be restricted to me and the following groups</td></tr><%
				for (ConfidentialGroup confidGroup : auditUtil.getConfidentialGroups(user)) {
					%><tr><td><input type="checkbox" name="palConfidGroups" value="<%=confidGroup.getGroupId()%>"<%
					if (!FREDUtil.isEmpty(palListAudit.getConfidGroups()) && palListAudit.getConfidGroups().contains(confidGroup)) {
						%> checked<%
					}
					%> /></td><td style="text-align:left"><%=confidGroup.getName()%></td></tr><%
				}
				%><tr><td colspan="4" class="heading">Alternative email address (to notify when confidentiality is expiring)&nbsp;&nbsp;&nbsp;<input type="text" name="palConfidLapseEmail" value="<%=DBUtils.nvl(palListAudit.getConfidLapseEmail())%>" /></td></tr><%
				
			}
			%><tr><td></td><td><a href="#" onClick="document.confidForm.submit();"><img src="images/save.gif" height="20" width="20" border="0" alt="Save"/></a>&nbsp;&nbsp;<a href="#" onClick="document.confidForm.submit();" class="boldlink">Save</a></td></tr>

			</table><%
			endDETable(pageContext);
			%></p>
			</form><%
		} 
		else {
			%><p><span class="subhead">Access denied</span></p>
			You don't have sufficient rights to edit the confidentiality. <br />
			The confidentiality can only be edited by the user who created the record <b>or</b> by a user who owns one of its confidential groups.  <br /> 
			<br />
			Click <a href="index.jsp" class="heading">here</a> to return to the FRED home page.<%
		}
	}

	drawBottom(out, et);
		
%>