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
%><%@page import="nz.cri.gns.fred.model.ConfidentialGroup"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.Date"
%><%@page import="java.util.Calendar"
%><%@page import="java.util.GregorianCalendar"
%><%@page import="java.util.Set"
%><%@page import="java.util.HashSet"
%><%@page import="java.io.PrintWriter"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Set Confidentiality";
	}

%><%!
	public void updateConfidentiality(Audit audit, String confidType, String confidPeriod, String confidLapseEmail, String[] confidGroupIds) throws StorageAccessException {
		AuditUtil auditUtil = new AuditUtil(HibernateUtil.get().getDAOFactory());
		if ("confid".equals(confidType)) {
			audit.setConfidentialFlag(true);
			audit.setConfidPeriod(new Double(confidPeriod));
			audit.setConfidLapseEmail(confidLapseEmail);
			if (FREDConstants.APPROVED.equals(audit.getStatus())) {
				GregorianCalendar cal = new GregorianCalendar();
				if (audit.getConfidPeriod().doubleValue() == 0.5)
					cal.add(Calendar.MONTH, 6);
				else
					cal.add(Calendar.YEAR, audit.getConfidPeriod().intValue());
				audit.setConfidLapseDate(cal.getTime());
			} else
				audit.setConfidLapseDate(null);
			if (confidGroupIds != null) {
				Set<ConfidentialGroup> confidGroups = new HashSet<ConfidentialGroup>();
				for (int i = 0; i < confidGroupIds.length; i++)
					confidGroups.add(auditUtil.getConfidentialGroup(new Integer(confidGroupIds[i])));
				audit.setConfidGroups(confidGroups);
			}
		} else {
			audit.setConfidentialFlag(false);
			audit.setConfidLapseDate(null);
			audit.setConfidPeriod(null);
			audit.setConfidLapseEmail(null);
			audit.setConfidGroups(null);
		}
		auditUtil.update(audit);	
	}
%><%
	PageState state = new PageState(request, response, getServletContext());
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	User user = (User)getUser(session);
	FolderUtil folderUtil = new FolderUtil(factory);
	AuditUtil auditUtil = new AuditUtil(factory);
	TaxonomicUtil taxonomicUtil = new TaxonomicUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);
	et.setUseNavigationColumn(false);
	addButtons(et, new IconnedLink[] {
			new IconnedLink((String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "&q=" + Math.random(), "images/back_arrow.gif", "Back")
		});

	drawTop(out, et, request, response);
		
	if (request.getParameter("ID") != null && request.getParameter("RecType") != null && request.getParameter("FoldID") != null) {
		int id = Integer.parseInt(request.getParameter("ID"));
		String recType = request.getParameter("RecType");
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
		
		Audit audit = null;
		Audit palListAudit = null;
		String dataType = "";
		if (FREDConstants.ADOPTION.equals(recType) || FREDConstants.PALEONTOLOGICAL.equals(recType)) {
			audit = new RecordUtil(factory).getRecord(id).getAudit();
			dataType = "record";
			if (FREDConstants.PALEONTOLOGICAL.equals(recType))
				palListAudit = new RecordUtil(factory).getRecord(id).getPalListAudit();
		} else if ("SMP".equals(recType)) {
			audit = new SampleUtil(factory).getSample(id).getAudit();
			dataType = "sample";
		}

		if (folder.isAllowedEditLocalities() && audit != null) {
					
			if ("Update".equals(request.getParameter("Action"))) {
				try {
					updateConfidentiality(audit, request.getParameter("confidType"), request.getParameter("confidPeriod"), request.getParameter("confidLapseEmail"), request.getParameterValues("confidGroups"));
					if (FREDConstants.PALEONTOLOGICAL.equals(recType))
						updateConfidentiality(palListAudit, request.getParameter("palConfidType"), request.getParameter("palConfidPeriod"), request.getParameter("palConfidLapseEmail"), request.getParameterValues("palConfidGroups"));
					response.sendRedirect((String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "&q=" + Math.random());
					return;
				} catch (Exception e) {
					System.out.println("********** FRED confidentiality error: " + new java.util.Date());
					e.printStackTrace();
				}
			}
				
			%><center>
			<p>&nbsp;</p><p><%
			startDETable(pageContext);
			%><table border="0" width="550">
			<tr><td class="deHeading">Instructions</td></tr>
			<tr><td>You may set this <%=dataType%> to be <i>Open</i> or <i>Confidential</i>.<%
			if (FREDConstants.PALEONTOLOGICAL.equals(recType)) {
				%>  You may set the confidentiality of the taxonomic list seperately to the rest of the paleontology record.<%
			}
			%><ul>
			<li>If set to <i>Open</i> any registered user will be able to view it (after it has been submitted/approved).</li>
			<li>If set to <i>Confidential</i> only you (the submitter) plus any member of the groups you have selected will be able to view it.  You must also select a time period that the <%=dataType%> will remain confidential.  At the end of this period you will be notified and may increase the period or the <%=dataType%> will automatically become <i>open</i>.</li>
			</ul>
			Note: Confidentiality can be set at locality, sample (for drillholes and vertical sections), record and taxonomic list levels.  Confidentiality is inheirited down the levels, so for example setting a locality as confidential will also mean any records for that locality will also be confidential.  You can increase the level of confidentiality down the levels, but not decrease it - for example you can set a locality to be open, and a particular paleontology record to be confidential, but not the other way around.
			</td></tr>
			</table><%
			endDETable(pageContext);
			%></p>
			
			<form name="confidForm" method="get" action="set_confidentiality.jsp">
			<input type="hidden" name="ID" value="<%=id%>">
			<input type="hidden" name="RecType" value="<%=recType%>">
			<input type="hidden" name="FoldID" value="<%=folder.getFolderId()%>">
			<input type="hidden" name="Action" value="Update">
						
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
			<tr><td colspan="4">This <%=dataType%> will be restricted to me and the following groups</td></tr><%
			for (ConfidentialGroup confidGroup : auditUtil.getConfidentialGroups(user)) {
				%><tr><td><input type="checkbox" name="confidGroups" value="<%=confidGroup.getGroupId()%>"<%
				if (!FREDUtil.isEmpty(audit.getConfidGroups()) && audit.getConfidGroups().contains(confidGroup)) {
					%> checked<%
				}
				%> /></td><td style="text-align:left"><%=confidGroup.getName()%></td></tr><%
			}
			%><tr><td colspan="4" class="heading">Alternative email address (to notify when confidentiality is expiring)&nbsp;&nbsp;&nbsp;<input type="text" name="confidLapseEmail" value="<%=DBUtils.nvl(audit.getConfidLapseEmail())%>" /></td></tr><%
			
			if (FREDConstants.PALEONTOLOGICAL.equals(recType)) {
				%><tr><td>&nbsp;</td></tr>
				<tr><td colspan="4" class="deHeading">Taxonimic List Confidentiality Options</td></tr>
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
				<tr><td colspan="4">This taxonimic list will be restricted to me and the following groups</td></tr><%
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
			</form>
			</center><%
		} 
		else {
			%><p><span class="subhead">Access denied</span></p>You don't have sufficient rights in this folder.  Click <a href="index.jsp" class="heading">here</a> to return to the FRED home page.<%
		}
	}
		
	%></td></tr></table><%
	drawBottom(out, et);
		
%>
