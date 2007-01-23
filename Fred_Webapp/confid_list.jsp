<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.List"
%><%@page import="java.util.Date"
%><%@page import="java.util.Calendar"
%><%@page import="java.util.GregorianCalendar"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.Adoption"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Record"
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
	addButtons(et, new IconnedLink[] {
			new IconnedLink("confid_list.jsp?q=" + Math.random(), "images/lock.gif", "Data Soon to be Open-File"),
			new IconnedLink("confid_list.jsp?Type=All&q=" + Math.random(), "images/lock.gif", "All Data")
		});

	drawTop(out, et, request, response);
	try {
		List<Sample> confidSamples;
		List<Adoption> confidAdoptions;
		List<Paleontology> confidPaleontologies;
		List<Paleontology> confidPalLists;
		if ("All".equals(request.getParameter("Type"))) {
			confidSamples = auditUtil.getConfidentialSamples(user);
			confidAdoptions = auditUtil.getConfidentialAdoptionRecords(user);
			confidPaleontologies = auditUtil.getConfidentialPaleontologyRecords(user);
			confidPalLists = auditUtil.getConfidentialPalLists(user);
		} else {
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(Calendar.DATE, 90);
			Date lapseDate = cal.getTime();
			confidSamples = auditUtil.getConfidentialSamples(user, lapseDate);
			confidAdoptions = auditUtil.getConfidentialAdoptionRecords(user, lapseDate);
			confidPaleontologies = auditUtil.getConfidentialPaleontologyRecords(user, lapseDate);
			confidPalLists = auditUtil.getConfidentialPalLists(user, lapseDate);
		}
	
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
			<tr><td colspan="3" class="deHeading">Confidential Adoption Records</td></tr>
			<!-- <tr><td colspan="2" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr> -->
			<tr><th style="text-align: left">Locality&nbsp;&nbsp;</th><th style="text-align: left">Adoption Record&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date</th></tr><%
			for (Adoption adoption : confidAdoptions) {
				Record record = adoption.getRecord();
				%><tr><td style="text-align: left"><a href="detail.jsp?ID=<%=record.getSample().getSampleId()%>"><%=FeatureUtil.getFeatureIdentifyingName(record.getSample().getFeature())%><%=(SampleUtil.getDrillHoleDepthDescription(record.getSample()) != null) ? "<br />" + SampleUtil.getDrillHoleDepthDescription(record.getSample()) : ""%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=RecordUtil.getRecordName(record)%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=FREDUtil.formatDateForOutput(record.getAudit().getConfidLapseDate())%></td></tr><%
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
			<tr><td colspan="3" class="deHeading">Confidential Paleontology Records</td></tr>
			<!-- <tr><td colspan="2" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr> -->
			<tr><th style="text-align: left">Locality&nbsp;&nbsp;</th><th style="text-align: left">Paleontology Record&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date</th></tr><%
			for (Paleontology paleontology : confidPaleontologies) {
				Record record = paleontology.getRecord();
				%><tr><td style="text-align: left"><a href="detail.jsp?ID=<%=record.getSample().getSampleId()%>"><%=FeatureUtil.getFeatureIdentifyingName(record.getSample().getFeature())%><%=(SampleUtil.getDrillHoleDepthDescription(record.getSample()) != null) ? "<br />" + SampleUtil.getDrillHoleDepthDescription(record.getSample()) : ""%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=RecordUtil.getRecordName(record)%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=FREDUtil.formatDateForOutput(record.getAudit().getConfidLapseDate())%></td></tr><%
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
			<tr><td colspan="3" class="deHeading">Confidential Paleontology Taxonomic Lists</td></tr>
			<!-- <tr><td colspan="2" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr> -->
			<tr><th style="text-align: left">Locality&nbsp;&nbsp;</th><th style="text-align: left">Paleontology Record&nbsp;&nbsp;</th><th style="text-align: left">Lapse Date</th></tr><%
			for (Paleontology paleontology : confidPalLists) {
				Record record = paleontology.getRecord();
				%><tr><td style="text-align: left"><a href="detail.jsp?ID=<%=record.getSample().getSampleId()%>"><%=FeatureUtil.getFeatureIdentifyingName(record.getSample().getFeature())%><%=(SampleUtil.getDrillHoleDepthDescription(record.getSample()) != null) ? "<br />" + SampleUtil.getDrillHoleDepthDescription(record.getSample()) : ""%></a>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=RecordUtil.getRecordName(record)%>&nbsp;&nbsp;</td>
				<td style="text-align: left"><%=FREDUtil.formatDateForOutput(record.getPalListAudit().getConfidLapseDate())%></td></tr><%
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


