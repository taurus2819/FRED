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
			List<Paleontology> confidPals = auditUtil.getConfidentialPaleontologyRecords(user);
			List<Paleontology> confidPalLists = auditUtil.getConfidentialPalLists(user);
	
			//List samples
			if (confidSamples.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><td colspan=5 class=deHeading>Confidential Drillhole/Vertical Section Samples</td></tr>
				<!-- <tr><td colspan="2" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr> -->
				<tr><th style="text-align: left">Locality&nbsp;&nbsp;</th><th style="text-align: left">Sample&nbsp;&nbsp;</th></tr><%
				for (Sample sample : confidSamples) {
					%><tr><td style="text-align: left"><%=FeatureUtil.getFeatureIdentifyingName(sample.getFeature())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=SampleUtil.getDrillHoleDepthDescription(sample)%>&nbsp;&nbsp;</td></tr><%
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
				<tr><td colspan=5 class=deHeading>Confidential Drillhole/Vertical Section Samples</td></tr>
				<!-- <tr><td colspan="2" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr> -->
				<tr><th style="text-align: left">Locality&nbsp;&nbsp;</th><th style="text-align: left">Sample&nbsp;&nbsp;</th><th style="text-align: left">Adoption Record&nbsp;&nbsp;</th></tr><%
				for (Adoption adoption : confidAdoptions) {
					%><tr><td style="text-align: left"><%=FeatureUtil.getFeatureIdentifyingName(adoption.getRecord().getSample().getFeature())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=SampleUtil.getDrillHoleDepthDescription(adoption.getRecord().getSample())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=RecordUtil.getRecordName(adoption.getRecord())%>&nbsp;&nbsp;</td></tr><%
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


