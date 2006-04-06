<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.Set"
%><%@page import="java.util.TreeSet"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.model.Taxon"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%!
public String getName(HttpServletRequest request) {
	try {
		FolderUtil folderUtil = new FolderUtil(HibernateUtil.get().getDAOFactory());
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));
		return "FRED :: Problem Taxa for " + folder.getFolder().getName();
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
			Set<Taxon> provTaxa = new TreeSet<Taxon>();
			Set<Taxon> rejTaxa = new TreeSet<Taxon>();
			Set<Taxon> obTaxa = new TreeSet<Taxon>();
			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));
			session.setAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT, "folder_taxa_list.jsp?ID=" + folder.getFolder().getFolderId());
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			for (int x = 0; x < features.length; x++) {
				Set<Sample> samples = features[x].getSamples();
				for (Sample sample : samples) {
					Set<Record> records = sample.getRecords();
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
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="650">
				<tr><td colspan=6 class=deHeading>Provisional Entries</td></tr>
				<tr><td colspan="6" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr>
				<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Submitted By&nbsp;&nbsp;</th><th style="text-align: left">Locality(s)</th></tr><%
				for (Taxon taxon : provTaxa) {
					%><tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getSubmittedById() != null) ? FREDUtil.getUserName(taxon.getSubmittedById().intValue()) : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getSubmittedDate()) : "")%>&nbsp;&nbsp;</td><%
					if (!FREDUtil.isEmpty(taxon.getListEntries())) {
						%><td style="text-align: left"><%
						for (PaleontologyListEntry palList : taxon.getListEntries()) {
							try {
								Feature feature = palList.getPaleontology().getRecord().getSample().getFeature();
								%><a href="de.jsp?Type=<%=FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&RecID=<%=palList.getPaleontology().getRecordId()%>"><%=FeatureUtil.getFeatureIdentifyingName(feature)%></a>&nbsp;&nbsp;<br /><%
							} catch (Exception e) {}						}
						%></td><%
					}
					%></tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
	
			//List rejected taxa
			if (rejTaxa.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="650">
				<tr><th colspan="7" class="deHeading">Rejected Entries</th></tr>
				<tr><td colspan="7" style="text-align: left; color: #FF0000">This record contains rejected taxonomic entries. You must remove these entries before submitting the record</td></tr>
				<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Rejected By&nbsp;&nbsp;</th><th style="text-align: left">Comments&nbsp;&nbsp;</th><th style="text-align: left">Locality(s)</th></tr><%
				for (Taxon taxon : rejTaxa) {
					%><tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? FREDUtil.getUserName(taxon.getApprovedById().intValue()) : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(taxon.getApprovedDate()) : "")%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getPanelistComments())%>&nbsp;&nbsp;</td><%
					if (!FREDUtil.isEmpty(taxon.getListEntries())) {
						%><td style="text-align: left"><%
						for (PaleontologyListEntry palList : taxon.getListEntries()) {
							try {
								Feature feature = palList.getPaleontology().getRecord().getSample().getFeature();
								%><a href="de.jsp?Type=<%=FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&RecID=<%=palList.getPaleontology().getRecordId()%>"><%=FeatureUtil.getFeatureIdentifyingName(feature)%></a>&nbsp;&nbsp;<br /><%
							} catch (Exception e) {}						}
						%></td><%
					}
					%></tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
	
			//List obsoloete taxa
			if (obTaxa.size() > 0) {
				%><p><%
				startDETable(pageContext);
				%><table border="0" cellspacing="0" cellpadding="2" width="650">
				<tr><th colspan="4" class="deHeading">Obsolete Entries</th></tr>
				<tr><td colspan="4" style="text-align: left; color: #FF0000">This record contains obsolete taxonomic entries. You must remove these entries before submitting the record</td></tr>
				<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th style="text-align: left">Locality(s)</th></tr><%
				for (Taxon taxon : obTaxa) {
					%><tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=DBUtils.nvl(taxon.getAuthor())%>&nbsp;&nbsp;</td><%
					if (!FREDUtil.isEmpty(taxon.getListEntries())) {
						%><td style="text-align: left"><%
						for (PaleontologyListEntry palList : taxon.getListEntries()) {
							try {
								Feature feature = palList.getPaleontology().getRecord().getSample().getFeature();
								%><a href="de.jsp?Type=<%=FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&RecID=<%=palList.getPaleontology().getRecordId()%>"><%=FeatureUtil.getFeatureIdentifyingName(feature)%></a>&nbsp;&nbsp;<br /><%
							} catch (Exception e) {}						}
						%></td><%
					}
					%></tr><%
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