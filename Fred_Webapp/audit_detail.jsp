<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Adoption"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.AuditEdit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%!
	public String getName(HttpServletRequest request) {
		try {
			String sampID = request.getParameter("ID");
			String featID = request.getParameter("FeatID");
			DAOFactory factory = HibernateUtil.get().getDAOFactory();
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
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	SampleUtil sampleUtil = new SampleUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate();
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
	drawEndNavigation(out);
	%><p>&nbsp;<p/><%
	
	if (feature != null && featureUtil.isAllowedReadFeatureSite(user, feature)) {
		Audit audit = feature.getAudit();
		String status = audit.getStatus();
		%><p><%
		startDETable(pageContext);
		%><table border="0" width="550">
		<tr><td colspan="4" class="deHeading">Locality</td></tr><%

		%><tr><td class="heading">Status:&nbsp;&nbsp;</td>
		<td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=status%>&nbsp;&nbsp;</td></tr><%
		if (status.equals(FREDConstants.REJECTED)) {
			%><tr><td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%> class="heading">Curator Comments:&nbsp;&nbsp;</td>
			<td colspan="3" <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=DBUtils.nvl(audit.getCuratorComments())%></td></tr><%
		}
		
		%><tr><td class="heading">Origin:&nbsp;&nbsp;</td>
		<td colspan="3"><%=(audit.getDataOrigin() != null) ? audit.getDataOrigin().getName() + " (" + audit.getDataOrigin().getDescription() + ")" : ""%></td>
		</tr>
				
		<tr><td class="heading">Created:&nbsp;&nbsp;</td>
		<td><%=((audit.getCreatedById() != null) ? FREDUtil.getUserName(audit.getCreatedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
		<td><%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
		</tr><%
				
		for (AuditEdit edit : AuditUtil.getOrderedAuditEdits(audit)) {
			%><tr><td class="heading">Edited:&nbsp;&nbsp;</td>
			<td><%=((edit.getEditedById() != null) ? FREDUtil.getUserName(edit.getEditedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td><%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td class="smalltext"><%=DBUtils.nvl(edit.getComments())%></td></tr><%				
		}
		
		if (!status.equals(FREDConstants.WORKING)) {
			%><tr><td class="heading">Submitted:&nbsp;&nbsp;</td>
			<td><%=((audit.getSubmittedById() != null) ? FREDUtil.getUserName(audit.getSubmittedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td><%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td></tr><%
		}
		
		if (status.equals(FREDConstants.APPROVED)) {
			%><tr><td class="heading">Approved:&nbsp;&nbsp;</td>
			<td><%=((audit.getApprovedById() != null) ? FREDUtil.getUserName(audit.getApprovedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td><%=((audit.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(audit.getApprovedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
			<td class="smalltext"><%=DBUtils.nvl(audit.getCuratorComments())%></td></tr><%
		}
		%></table><%
		endDETable(pageContext);
		%></p><%
	
		//Sample
		if (sample != null && sampleUtil.isAllowedReadSample(user, sample)) {
			if (!sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
				audit = sample.getAudit();
				status = audit.getStatus();

				%><p><%
				startDETable(pageContext);
				%><table border="0" width="550">
				<tr><td colspan="4" class="deHeading">Sample</td></tr>
				<tr><td class="heading">Sample Depth&nbsp;&nbsp;</td><td colspan="3"><%=DBUtils.nvl(SampleUtil.getDrillHoleDepthDescription(sample))%>&nbsp;&nbsp;</td></tr>

				<tr>
				<td class="heading">Status:&nbsp;&nbsp;</td>
				<td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=status%>&nbsp;&nbsp;</td>
				</tr>
				
				<tr>
				<td class="heading">Origin:&nbsp;&nbsp;</td>
				<td colspan="3"><%=(audit.getDataOrigin() != null) ? audit.getDataOrigin().getName() + " (" + audit.getDataOrigin().getDescription() + ")" : ""%></td>
				</tr><%
				
				%><tr><td class="heading">Created:&nbsp;&nbsp;</td>
				<td><%=((audit.getCreatedById() != null) ? FREDUtil.getUserName(audit.getCreatedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
				<td><%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td></tr><%

				for (AuditEdit edit : AuditUtil.getOrderedAuditEdits(audit)) {
					%><tr><td class="heading">Edited:&nbsp;&nbsp;</td>
					<td><%=((edit.getEditedById() != null) ? FREDUtil.getUserName(edit.getEditedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td><%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td class="smalltext"><%=DBUtils.nvl(edit.getComments())%></td></tr><%				
				}
				
				if (!status.equals(FREDConstants.WORKING)) {
					%><tr><td class="heading">Submitted:&nbsp;&nbsp;</td>
					<td><%=((audit.getSubmittedById() != null) ? FREDUtil.getUserName(audit.getSubmittedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td><%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td></tr><%
				}
				
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
				
			//Adoption
			for (Adoption adoRecord : sampleUtil.getAdoptionRecords(sample)) {
				if (recordUtil.isAllowedReadRecord(user, adoRecord.getRecord())) {				
					audit = adoRecord.getRecord().getAudit();
					status = audit.getStatus();
					%><p><%
					startDETable(pageContext);
					%><table border="0" width="550">
					<tr><td colspan="4" class="deHeading">Adoption Record</td></tr>
					<tr><td class="heading">Record Name&nbsp;&nbsp;</td><td colspan="3"><%=RecordUtil.getRecordName(adoRecord.getRecord())%>&nbsp;&nbsp;</td></tr>
					
					<tr>
					<td class="heading">Status:&nbsp;&nbsp;</td>
					<td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=status%>&nbsp;&nbsp;</td>
					</tr>
					
					<tr><td class="heading">Origin:&nbsp;&nbsp;</td><td colspan="3"><%=(audit.getDataOrigin() != null) ? audit.getDataOrigin().getName() + " (" + audit.getDataOrigin().getDescription() + ")" : ""%></td></tr>
					<tr><td class="heading">Created:&nbsp;&nbsp;</td>
					<td><%=((audit.getCreatedById() != null) ? FREDUtil.getUserName(audit.getCreatedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td><%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td></tr><%
					
					for (AuditEdit edit : AuditUtil.getOrderedAuditEdits(audit)) {
						%><tr><td class="heading">Edited:&nbsp;&nbsp;</td>
						<td><%=((edit.getEditedById() != null) ? FREDUtil.getUserName(edit.getEditedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td><%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td class="smalltext"><%=DBUtils.nvl(edit.getComments())%></td></tr><%				
					}
					
					if (!status.equals(FREDConstants.WORKING)) {
						%><tr><td class="heading">Submitted:&nbsp;&nbsp;</td>
						<td><%=((audit.getSubmittedById() != null) ? FREDUtil.getUserName(audit.getSubmittedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td><%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td></tr><%
					}
					
					%></table><%
					endDETable(pageContext);
					%></p><%
				}
			}
			
			//Paleontology
			for (Paleontology palRecord : sampleUtil.getPaleontologyRecords(sample)) {
				if (recordUtil.isAllowedReadRecord(user, palRecord.getRecord())) {				
					audit = palRecord.getRecord().getAudit();
					status = audit.getStatus();
					%><p><%
					startDETable(pageContext);
					%><table border="0" width="550">
					<tr><td colspan="4" class="deHeading">Paleontology Record</td></tr>
					<tr><td class="heading">Record Name&nbsp;&nbsp;</td><td colspan="3"><%=RecordUtil.getRecordName(palRecord.getRecord())%>&nbsp;&nbsp;</td></tr>
					
					<tr>
					<td class="heading">Status:&nbsp;&nbsp;</td>
					<td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%>><%=status%>&nbsp;&nbsp;</td>
					</tr>
					
					<tr><td class="heading">Origin:&nbsp;&nbsp;</td><td colspan="3"><%=(audit.getDataOrigin() != null) ? audit.getDataOrigin().getName() + " (" + audit.getDataOrigin().getDescription() + ")" : ""%></td></tr>
					<tr><td class="heading">Created:&nbsp;&nbsp;</td>
					<td><%=((audit.getCreatedById() != null) ? FREDUtil.getUserName(audit.getCreatedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
					<td><%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td></tr><%
					
					for (AuditEdit edit : AuditUtil.getOrderedAuditEdits(audit)) {
						%><tr><td class="heading">Edited:&nbsp;&nbsp;</td>
						<td><%=((edit.getEditedById() != null) ? FREDUtil.getUserName(edit.getEditedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td><%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td class="smalltext"><%=DBUtils.nvl(edit.getComments())%></td></tr><%				
					}
					
					if (!status.equals(FREDConstants.WORKING)) {
						%><tr><td class="heading">Submitted:&nbsp;&nbsp;</td>
						<td><%=((audit.getSubmittedById() != null) ? FREDUtil.getUserName(audit.getSubmittedById().intValue()) : "&nbsp;")%>&nbsp;&nbsp;</td>
						<td><%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "&nbsp;")%>&nbsp;&nbsp;</td></tr><%
					}
					
					%></table><%
					endDETable(pageContext);
					%></p><%
				}
			}

		}
	}

	drawBottom(out, et); 
%>