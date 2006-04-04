<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.Collections"
%><%@page import="java.util.Iterator"
%><%@page import="java.util.List"
%><%@page import="java.util.Set"
%><%@page import="java.util.HashSet"
%><%@page import="java.util.Vector"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.Taxon"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%!
public String getName(HttpServletRequest request) {
	try {
		FolderUtil folderUtil = new FolderUtil(HibernateUtil.get().getDAOFactory());
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));
		return "FRED :: " + folder.getFolder().getName();
	} catch (Exception e) {
		return "FRED :: The Fossil Record Electronic Database";
	}
}	
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	FolderUtil folderUtil = new FolderUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	if (request.getParameter("ID") != null) {
		drawTop(out, et, request, response);
		%><center><p>&nbsp;</p><%	
		try {
			Set<Taxon> provTaxa = new HashSet<Taxon>();
			Set<Taxon> rejTaxa = new HashSet<Taxon>();
			Set<Taxon> obTaxa = new HashSet<Taxon>();
			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			System.out.println("Feature count: " + features.length);
			for (int x = 0; x < features.length; x++) {
				Set<Sample> samples = features[x].getSamples();
				System.out.println("Sample count: " + samples.size());
				for (Sample sample : samples) {
					Set<Record> records = sample.getRecords();
					System.out.println("Record count: " + records.size());
					for (Record record : records) {
						if (RecordUtil.getRecordType(record).equals(FREDConstants.PALEONTOLOGICAL)) {
							Paleontology pal = record.getPaleontology();
							provTaxa.addAll(recordUtil.getTaxon(pal, Taxon.PROVISIONAL_STATUS));
							rejTaxa.addAll(recordUtil.getTaxon(pal, Taxon.REJECTED_STATUS));
							obTaxa.addAll(recordUtil.getTaxon(pal, Taxon.OBSOLETE_STATUS));
						}
					}
				}
			}
	
			//List provisional taxa
			if (provTaxa.size() > 0) {
				//Collections.sort(provTaxa);
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><td colspan=5 class=deHeading>Provisional Entries</td></tr>
				<tr><td colspan="5" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr>
				<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Submitted By</th></tr><%
				for (Iterator i = provTaxa.iterator(); i.hasNext(); ) {
					Taxon taxon = (Taxon)i.next();
					%><tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getSubmittedById() != null) ? FREDUtil.getUserName(taxon.getSubmittedById().intValue()) : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getSubmittedDate()) : "")%>&nbsp;&nbsp;</td><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
	
			//List rejected taxa
			if (rejTaxa.size() > 0) {
				//Collections.sort(rejTaxa);
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><th colspan="6" class="deHeading">Rejected Entries</th></tr>
				<tr><td colspan="6" style="text-align: left; color: #FF0000">This record contains rejected taxonomic entries. You must remove these entries before submitting the record</td></tr>
				<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Rejected By&nbsp;&nbsp;</th><th style="text-align: left">Comments</th></tr><%
				for (Iterator i = rejTaxa.iterator(); i.hasNext(); ) {
					Taxon taxon = (Taxon)i.next();
					%><tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? FREDUtil.getUserName(taxon.getApprovedById().intValue()) : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getApprovedDate()) : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getPanelistComments())%>&nbsp;&nbsp;</td><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
	
			//List obsoloete taxa
			if (obTaxa.size() > 0) {
				//Collections.sort(obTaxa);
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="550">
				<tr><th colspan="3" class="deHeading">Obsolete Entries</th></tr>
				<tr><td colspan="3" style="text-align: left; color: #FF0000">This record contains obsolete taxonomic entries. You must remove these entries before submitting the record</td></tr>
				<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author</th></tr><%
				for (Iterator i = obTaxa.iterator(); i.hasNext(); ) {
					Taxon taxon = (Taxon)i.next();
					%><tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td><%
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
	}
	
	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}

%>


