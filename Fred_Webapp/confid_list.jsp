<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.List"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
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
			//List<Taxon> provTaxa = recordUtil.getTaxon(pal, Taxon.PROVISIONAL_STATUS);
			//List<Taxon> rejTaxa = recordUtil.getTaxon(pal, Taxon.REJECTED_STATUS);
			//List<Taxon> obTaxa = recordUtil.getTaxon(pal, Taxon.OBSOLETE_STATUS);
	
			//List provisional taxa
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
	
/*			//List rejected taxa
			if (rejTaxa.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><th colspan="5" class="deHeading">Rejected Entries</th></tr>
				<tr><td colspan="5" style="text-align: left; color: #FF0000">This record contains rejected taxonomic entries. You must remove these entries before submitting the record</td></tr>
				<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Rejected By&nbsp;&nbsp;</th><th style="text-align: left">Comments</th></tr><%
				for (Taxon taxon : rejTaxa) {
					%><tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? taxon.getApprovedBy().getFullName() : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getApprovedDate()) : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getPanelistComments())%>&nbsp;&nbsp;</td><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
	
			//List obsoloete taxa
			if (obTaxa.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><th colspan="3" class="deHeading">Obsolete Entries</th></tr>
				<tr><td colspan="3" style="text-align: left; color: #FF0000">This record contains obsolete taxonomic entries. You must remove these entries before submitting the record</td></tr>
				<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author</th></tr><%
				for (Taxon taxon : obTaxa) {
					%><tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
	
			//List approved taxa
			if (appTaxa.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><th colspan="6" class="deHeading">Approved Entries</th></tr>
				<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Approved By</th><th style="text-align: left">Comments</th></tr><%
				for (Taxon taxon : appTaxa) {
					%><tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? taxon.getApprovedBy().getFullName() : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getApprovedDate()) : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getPanelistComments())%>&nbsp;&nbsp;</td><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}	*/
	
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


