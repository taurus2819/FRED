<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Adoption"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.AuditEdit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%@page import="org.springframework.security.core.GrantedAuthority"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="java.net.URLEncoder"
%><%!	
    @Override
    public GrantedAuthority getRequiredRights() {
        return null;
    }
%><%!
	public String getName(HttpServletRequest request) {
		try {
			String sampID = request.getParameter("ID");
			String featID = request.getParameter("FeatID");
			DAOFactory factory = FredHibernate.get().getDAOFactory();
			if (featID != null) {
				Feature feature = new FeatureUtil(factory).getFeature(Integer.parseInt(featID));
				return "FRED :: Audit Detail for " + FeatureUtil.getFeatureIdentifyingName(feature);
			} else if (sampID != null) {
				Sample sample = new SampleUtil(factory).getSample(Integer.parseInt(sampID));
				return "FRED :: Audit Detail for " + ((sample.getFrNumber() != null) ? sample.getFrNumber().getFrNumber() : FeatureUtil.getFeatureIdentifyingName(sample.getFeature()));
			}
			return "FRED :: The Fossil Record Electronic Database";
		} catch (Exception e) {
			return "FRED :: The Fossil Record Electronic Database";
		}
	}
%><%
	User user = (User) getUser(session);
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	SampleUtil sampleUtil = new SampleUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate();
	
	String backURL = request.getParameter("backURL");
	if (backURL != null && backURL.length() == 0)
		backURL = null;
	String backText = request.getParameter("backText");
	if (backText != null && backText.length() == 0)
		backText = null;
	String backStr = (backURL != null) ? "&backURL=" + URLEncoder.encode(backURL, "ISO-8859-1") : "";
	backStr += (backText != null) ? "&backText=" + URLEncoder.encode(backText, "ISO-8859-1") : "";
	if (backURL != null)
		addButtons(et, new IconnedLink[] {new IconnedLink(backURL, "images/back_arrow.gif", (backText != null) ? request.getParameter("backText") : "Back")});
	
	et.setDisplayLoadingMessage(true);
	
	String sampID = request.getParameter("ID");
	String featID = request.getParameter("FeatID");
	Feature feature =  null;
	Sample sample = null;

	//if FeatureID given then check if outcrop and if redirect to display sample details
	if (featID != null) {
		session.setAttribute("FRED.FeatureID", featID);
		feature = featureUtil.getFeature(Integer.parseInt(featID));
		if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
			response.sendRedirect("audit_detail.jsp?ID=" + ((Sample)feature.getSamples().iterator().next()).getSampleId());
			return;
		}
	} else if (sampID != null) {
		session.setAttribute("FRED.SampleID", sampID);
		sample = sampleUtil.getSample(Integer.parseInt(sampID));
		feature = sample.getFeature();
	}
	
	drawTop(out, et, request, response);
	
	if (feature != null && featureUtil.isAllowedReadFeatureSite(user, feature)) {
		Audit audit = feature.getAudit();
		String status = audit.getStatus();
		%><p>
		<table border="0" cellpadding="3" cellspacing="2" width="550">
		<tr class="midColour"><th colspan="4">Locality</th></tr>

		<tr class="lightColour"><td class="heading">Status:&nbsp;&nbsp;</td>
		<td colspan="3" <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=status%></td></tr><%
		if (status.equals(FREDConstants.REJECTED)) {
			%><tr class="lightColour"><td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%> class="heading">Curator Comments:&nbsp;&nbsp;</td>
			<td colspan="3" <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=DBUtils.nvl(audit.getCuratorComments())%></td></tr><%
		}
		
		%><tr class="lightColour"><td class="heading">Origin:&nbsp;&nbsp;</td>
		<td colspan="3"><%=(audit.getDataOrigin() != null) ? audit.getDataOrigin().getName() + " (" + audit.getDataOrigin().getDescription() + ")" : ""%></td>
		</tr>
				
		<tr class="lightColour"><td class="heading">Created:&nbsp;&nbsp;</td>
		<td><%=((audit.getCreatedById() != null) ? audit.getCreatedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
		<td><%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
		<td>&nbsp;</td>
		</tr><%
				
		for (AuditEdit edit : AuditUtil.getOrderedAuditEdits(audit)) {
			%><tr class="lightColour"><td class="heading">Edited:&nbsp;&nbsp;</td>
			<td><%=((edit.getEditedById() != null) ? edit.getEditedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td><%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td class="smalltext"><%=DBUtils.nvl(edit.getComments())%></td></tr><%				
		}
		
		if (!status.equals(FREDConstants.WORKING)) {
			%><tr class="lightColour"><td class="heading">Submitted:&nbsp;&nbsp;</td>
			<td><%=((audit.getSubmittedById() != null) ? audit.getSubmittedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td><%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td>&nbsp;</td></tr><%
		}
		
		if (status.equals(FREDConstants.APPROVED)) {
			%><tr class="lightColour"><td class="heading">Approved:&nbsp;&nbsp;</td>
			<td><%=((audit.getApprovedById() != null) ? audit.getApprovedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td><%=((audit.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(audit.getApprovedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td class="smalltext"><%=DBUtils.nvl(audit.getCuratorComments())%></td></tr><%
		}

		%><tr><td>&nbsp;</td></tr><%
	
		//Sample
		if (sample != null && sampleUtil.isAllowedReadSample(user, sample)) {
			if (!sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
				audit = sample.getAudit();
				status = audit.getStatus();

				%><tr class="midColour"><th colspan="4">Sample</th></tr>
				<tr class="lightColour"><td class="heading">Sample Depth&nbsp;&nbsp;</td><td colspan="3"><%=DBUtils.nvl(SampleUtil.getDrillHoleDepthDescription(sample))%>&nbsp;&nbsp;</td></tr>

				<tr class="lightColour">
				<td class="heading">Status:&nbsp;&nbsp;</td>
				<td colspan="3" <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=status%>&nbsp;&nbsp;</td>
				</tr>
				
				<tr class="lightColour">
				<td class="heading">Origin:&nbsp;&nbsp;</td>
				<td colspan="3"><%=(audit.getDataOrigin() != null) ? audit.getDataOrigin().getName() + " (" + audit.getDataOrigin().getDescription() + ")" : ""%></td>
				</tr><%
				
				%><tr class="lightColour"><td class="heading">Created:&nbsp;&nbsp;</td>
				<td><%=((audit.getCreatedById() != null) ? audit.getCreatedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
				<td><%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
				<td>&nbsp;</td>
				</tr><%

				for (AuditEdit edit : AuditUtil.getOrderedAuditEdits(audit)) {
					%><tr class="lightColour"><td class="heading">Edited:&nbsp;&nbsp;</td>
					<td><%=((edit.getEditedById() != null) ? edit.getEditedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td><%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td class="smalltext"><%=DBUtils.nvl(edit.getComments())%></td></tr><%				
				}
				
				if (!status.equals(FREDConstants.WORKING)) {
					%><tr class="lightColour"><td class="heading">Submitted:&nbsp;&nbsp;</td>
					<td><%=((audit.getSubmittedById() != null) ? audit.getSubmittedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td><%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td>&nbsp;</td></tr><%
				}
				%><tr><td>&nbsp;</td></tr><%
			}
				
			//Adoption
			for (Adoption adoRecord : sampleUtil.getAdoptionRecords(sample)) {
				if (recordUtil.isAllowedReadRecord(user, adoRecord.getRecord())) {				
					audit = adoRecord.getRecord().getAudit();
					status = audit.getStatus();
					%><tr class="midColour"><th colspan="4">Adoption Record</th></tr>
					<tr class="lightColour"><td class="heading">Record Name&nbsp;&nbsp;</td><td colspan="3"><%=RecordUtil.getRecordName(adoRecord.getRecord())%>&nbsp;&nbsp;</td></tr>
					
					<tr class="lightColour">
					<td class="heading">Status:&nbsp;&nbsp;</td>
					<td colspan="3" <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=status%>&nbsp;&nbsp;</td>
					</tr>
					
					<tr class="lightColour"><td class="heading">Origin:&nbsp;&nbsp;</td><td colspan="3"><%=(audit.getDataOrigin() != null) ? audit.getDataOrigin().getName() + " (" + audit.getDataOrigin().getDescription() + ")" : ""%></td></tr>
					<tr class="lightColour"><td class="heading">Created:&nbsp;&nbsp;</td>
					<td><%=((audit.getCreatedById() != null) ? audit.getCreatedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td><%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td>&nbsp;</td></tr><%
					
					for (AuditEdit edit : AuditUtil.getOrderedAuditEdits(audit)) {
						%><tr class="lightColour"><td class="heading">Edited:&nbsp;&nbsp;</td>
						<td><%=((edit.getEditedById() != null) ? edit.getEditedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td><%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td class="smalltext"><%=DBUtils.nvl(edit.getComments())%></td></tr><%				
					}
					
					if (!status.equals(FREDConstants.WORKING)) {
						%><tr class="lightColour"><td class="heading">Submitted:&nbsp;&nbsp;</td>
						<td><%=((audit.getSubmittedById() != null) ? audit.getSubmittedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td><%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td>&nbsp;</td></tr><%
					}
					%><tr><td>&nbsp;</td></tr><%
				}
			}
			
			//Paleontology
			for (Paleontology palRecord : sampleUtil.getPaleontologyRecords(sample)) {
				if (recordUtil.isAllowedReadRecord(user, palRecord.getRecord())) {				
					audit = palRecord.getRecord().getAudit();
					status = audit.getStatus();
					%><tr class="midColour"><th colspan="4">Paleontology Record</th></tr>
					<tr class="lightColour"><td class="heading">Record Name&nbsp;&nbsp;</td><td colspan="3"><%=RecordUtil.getRecordName(palRecord.getRecord())%>&nbsp;&nbsp;</td></tr>
					
					<tr class="lightColour">
					<td class="heading">Status:&nbsp;&nbsp;</td>
					<td colspan="3" <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=status%>&nbsp;&nbsp;</td>
					</tr>
					
					<tr class="lightColour"><td class="heading">Origin:&nbsp;&nbsp;</td><td colspan="3"><%=(audit.getDataOrigin() != null) ? audit.getDataOrigin().getName() + " (" + audit.getDataOrigin().getDescription() + ")" : ""%></td></tr>
					<tr class="lightColour"><td class="heading">Created:&nbsp;&nbsp;</td>
					<td><%=((audit.getCreatedById() != null) ? audit.getCreatedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td><%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td>&nbsp;</td></tr><%
					
					for (AuditEdit edit : AuditUtil.getOrderedAuditEdits(audit)) {
						%><tr class="lightColour"><td class="heading">Edited:&nbsp;&nbsp;</td>
						<td><%=((edit.getEditedById() != null) ? edit.getEditedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td><%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td class="smalltext"><%=DBUtils.nvl(edit.getComments())%></td></tr><%				
					}
					
					if (!status.equals(FREDConstants.WORKING)) {
						%><tr class="lightColour"><td class="heading">Submitted:&nbsp;&nbsp;</td>
						<td><%=((audit.getSubmittedById() != null) ? audit.getSubmittedBy().getFullName() : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td><%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td>&nbsp;</td></tr><%
					}
				}
			}
			%></table></p><%
		}
	}

	drawBottom(out, et);
	try {
		FredHibernate.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>