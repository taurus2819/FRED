<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.List"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.Adoption"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.auth.User"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			return "FRED :: Confidential List for " + ((User)getUser(request.getSession())).getFullName(); 
		} catch (Exception e) {
			return "FRED :: The Fossil Record Electronic Database";
		}
	}
	
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	User user = (User)getUser(session);
	AuditUtil auditUtil = new AuditUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);
	try {
			List<Sample> confidSamples = auditUtil.getConfidentialSamples(user);
			List<Adoption> confidAdoptions = auditUtil.getConfidentialAdoptionRecords(user);
			List<Paleontology> confidPaleontologies = auditUtil.getConfidentialPaleontologyRecords(user);
			List<Paleontology> confidPalLists = auditUtil.getConfidentialPalLists(user);
	
			//List samples
			if (confidSamples.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><td colspan="4" class="deHeading">Confidential Drillhole/Vertical Section Samples</td></tr>
				<!-- <tr><td colspan="2" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr> -->
				<tr><th style="text-align: left">Locality&nbsp;&nbsp;</th><th style="text-align: left">Sample&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date</th></tr><%
				for (Sample sample : confidSamples) {
					%><tr><td style="text-align: left"><a href="detail.jsp?FeatID=<%=sample.getFeature().getFeatureId()%>"><%=FeatureUtil.getFeatureIdentifyingName(sample.getFeature())%></a>&nbsp;&nbsp;</td>
					<td style="text-align: left"><a href="detail.jsp?ID=<%=sample.getSampleId()%>"><%=SampleUtil.getDrillHoleDepthDescription(sample)%></a>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=FREDUtil.formatDateForOutput(sample.getAudit().getConfidLapseDate())%></td>
					<td style="text-align: left"><a href="set_confidentiality.jsp?ID=<%=sample.getSampleId()%>&RecType=SMP"><img src="images/lock.gif" border="0" height="20" width="20" alt="Set Confidentiality" /></a>&nbsp;</td></tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
	
			//List adoptions
			if (confidAdoptions.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><td colspan="4" class="deHeading">Confidential Adoption Records</td></tr>
				<!-- <tr><td colspan="2" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr> -->
				<tr><th style="text-align: left">Locality&nbsp;&nbsp;</th><th style="text-align: left">Sample&nbsp;&nbsp;</th><th style="text-align: left">Adoption Record&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date</th></tr><%
				for (Adoption adoption : confidAdoptions) {
					%><tr><td style="text-align: left"><%=FeatureUtil.getFeatureIdentifyingName(adoption.getRecord().getSample().getFeature())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(SampleUtil.getDrillHoleDepthDescription(adoption.getRecord().getSample()))%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=RecordUtil.getRecordName(adoption.getRecord())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=FREDUtil.formatDateForOutput(adoption.getRecord().getAudit().getConfidLapseDate())%></td></tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
			
			//List paleontologies
			if (confidPaleontologies.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><td colspan="4" class="deHeading">Confidential Paleontology Records</td></tr>
				<!-- <tr><td colspan="2" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr> -->
				<tr><th style="text-align: left">Locality&nbsp;&nbsp;</th><th style="text-align: left">Sample&nbsp;&nbsp;</th><th style="text-align: left">Paleontology Record&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date</th></tr><%
				for (Paleontology paleontology : confidPaleontologies) {
					%><tr><td style="text-align: left"><%=FeatureUtil.getFeatureIdentifyingName(paleontology.getRecord().getSample().getFeature())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(SampleUtil.getDrillHoleDepthDescription(paleontology.getRecord().getSample()))%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=RecordUtil.getRecordName(paleontology.getRecord())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=FREDUtil.formatDateForOutput(paleontology.getRecord().getAudit().getConfidLapseDate())%></td></tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
			
			//List pal lists
			if (confidPalLists.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><td colspan="4" class="deHeading">Confidential Paleontology Taxonomic Lists</td></tr>
				<!-- <tr><td colspan="2" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr> -->
				<tr><th style="text-align: left">Locality&nbsp;&nbsp;</th><th style="text-align: left">Sample&nbsp;&nbsp;</th><th style="text-align: left">Paleontology Record&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date</th></tr><%
				for (Paleontology paleontology : confidPalLists) {
					%><tr><td style="text-align: left"><%=FeatureUtil.getFeatureIdentifyingName(paleontology.getRecord().getSample().getFeature())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(SampleUtil.getDrillHoleDepthDescription(paleontology.getRecord().getSample()))%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=RecordUtil.getRecordName(paleontology.getRecord())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=FREDUtil.formatDateForOutput(paleontology.getRecord().getAudit().getConfidLapseDate())%></td></tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
	
			out.println("</center>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		drawBottom(out, et);

	
	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}

%>


