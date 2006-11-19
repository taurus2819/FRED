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
%><%@page import="nz.cri.gns.db.metadata.DocumentAttacher"
%><%@page import="nz.cri.gns.db.metadata.MetadataRecord"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
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
		if (recType.equals(FREDConstants.ADOPTION) || recType.equals(FREDConstants.PALEONTOLOGICAL)) {
			audit = new RecordUtil(factory).getRecord(id).getAudit();
		} else if (recType.equals("SMP")) {
			audit = new SampleUtil(factory).getSample(id).getAudit();
		} else {
			audit = new FeatureUtil(factory).getFeature(id).getAudit();
		}

		if (folder.isAllowedEditLocalities()) {
		
			
			if ("Update".equals(request.getParameter("Action"))) {
				try {
					if ("confid".equals(request.getParameter("confidType"))) {
						audit.setConfidentialFlag(true);
						audit.setConfidPeriod(new Double(request.getParameter("confidPeriod")));
						if (FREDConstants.APPROVED.equals(audit.getStatus())) {
							GregorianCalendar cal = new GregorianCalendar();
							if (audit.getConfidPeriod().doubleValue() == 0.5)
								cal.add(Calendar.MONTH, 6);
							else
								cal.add(Calendar.YEAR, audit.getConfidPeriod().intValue());
							audit.setConfidLapseDate(cal.getTime());
						} else
							audit.setConfidLapseDate(null);
						if (request.getParameter("confidGroups") != null) {
							String[] confidGroupIds = request.getParameterValues("confidGroups");
							Set<ConfidentialGroup> confidGroups = new HashSet<ConfidentialGroup>();
							for (int i = 0; i < confidGroupIds.length; i++)
								confidGroups.add(auditUtil.getConfidentialGroup(new Integer(confidGroupIds[i])));
							audit.setConfidGroups(confidGroups);
						}
					} else {
						audit.setConfidentialFlag(false);
						audit.setConfidLapseDate(null);
						audit.setConfidPeriod(null);
						audit.setConfidGroups(null);
					}
					auditUtil.update(audit);
				} catch (Exception e) {
					System.out.println("********** FRED confidentiality error: " + new java.util.Date());
					e.printStackTrace();
				}
			}
				
			%><center><%
			
			%><p>&nbsp;</p><p><%
			startDETable(pageContext);
			%><table border="0" width="550">
			<form method="get" action="set_confidentiality.jsp">
			<tr><td colspan="2" class="deHeading">Confidentiality Options</td></tr>
			<input type="hidden" name="ID" value="<%=id%>">
			<input type="hidden" name="RecType" value="<%=recType%>">
			<input type="hidden" name="FoldID" value="<%=folder.getFolderId()%>">
			<input type="hidden" name="Action" value="Update">
			<tr><td><input type="radio" name="confidType" value="open" <%=audit.getConfidentialFlag() ? "" : "checked"%> /></td><td style="text-align:left" class="heading">Open</td></tr>
			<tr><td><input type="radio" name="confidType" value="confid" <%=audit.getConfidentialFlag() ? "checked" : ""%> /></td><td style="text-align:left" class="heading">Confidential&nbsp;&nbsp;</td>
			<td style="text-align:left" class="heading">Confidential Period:&nbsp;</td>
			<td><select name="confidPeriod"><%
			double confidPeriod = (audit.getConfidPeriod() != null) ? audit.getConfidPeriod().doubleValue() : 1;
			if (confidPeriod != 0.5 && confidPeriod != 2 && confidPeriod != 5)
				confidPeriod = 1;
			%><option value="0.5"<%=(confidPeriod == 0.5) ? " selected" : ""%>>6 months</option>
			<option value="1" <%=(confidPeriod == 1) ? " selected" : ""%>>1 year</option>
			<option value="2"<%=(confidPeriod == 2) ? " selected" : ""%>>2 years</option>
			<option value="5"<%=(confidPeriod == 5) ? " selected" : ""%>>5 years</option>
			</select></td></tr><%
			for (ConfidentialGroup confidGroup : auditUtil.getConfidentialGroups()) {
				%><tr><td><input type="checkbox" name="confidGroups" value="<%=confidGroup.getGroupId()%>"<%
				for (ConfidentialGroup auditConfidGroup : audit.getConfidGroups()) {
					if (confidGroup.equals(auditConfidGroup)) {
						%>checked<%
						break;
					}
				}
				%> /></td><td style="text-align:left"><%=confidGroup.getName()%></td></tr><%
			}
			%><tr><td colspan="2"><input type="submit" /></td></tr>
			</form>
			</table><%
			endDETable(pageContext);
			%></p><%
				
			%></center><%
		} 
		else {
			%><p><span class="subhead">Access denied</span></p>You don't have sufficient rights in this folder.  Click <a href="index.jsp" class="heading">here</a> to return to the FRED home page.<%
		}
	}
		
	%></td></tr></table><%
	drawBottom(out, et);
		
%>
