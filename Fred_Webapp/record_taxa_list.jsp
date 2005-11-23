<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.FREDUtils"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="nz.cri.gns.intranet.*
%><%@page import="java.util.*"
%><%@page import="nz.cri.gns.auth.*"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.dao.StorageAccessException"
%><%@page import="nz.cri.gns.fred.de.MandatoryFieldsMissingException"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.Taxon"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="java.text.*"
%><%@page import="java.io.PrintWriter"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			RecordUtil recordUtil = new RecordUtil(HibernateUtil.get().getDAOFactory());
			Record record = recordUtil.getRecord(Integer.parseInt(request.getParameter("RecID")));
			return "FRED :: Taxonomic List for " + RecordUtil.getRecordName(record); 
		} catch (StorageAccessException e) {
			return "FRED";
		}
	}
	
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	
	RecordUtil recordUtil = new RecordUtil(factory);
	User user =(User) getUser(session);
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	Paleontology pal = null;
	try {
		pal = recordUtil.getRecord(Integer.parseInt(request.getParameter("RecID"))).getPaleontology();
	} catch (Exception e) {}

	if (pal != null) {
		try {
		drawTop(out, et, request, response);

		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		List<Taxon> appTaxa = recordUtil.getTaxon(pal, Taxon.APPROVED_STATUS);
		List<Taxon> provTaxa = recordUtil.getTaxon(pal, Taxon.PROVISIONAL_STATUS);
		List<Taxon> rejTaxa = recordUtil.getTaxon(pal, Taxon.REJECTED_STATUS);
		List<Taxon> obTaxa = recordUtil.getTaxon(pal, Taxon.OBSOLETE_STATUS);

		out.println("<p>Listed below are the taxonomic entries for the selected Paleontology Record grouped by status. This list will update as the status of an entry is changed by the database curators.<p>");

		out.println("<table border='0' cellspacing='0' cellpadding='2' width='550'>");
		//List provisional taxa
		if (provTaxa.size() > 0) {
			out.println("<tr><th colspan=\"5\">Provisional Entries</th></tr>");
			out.println("<tr><td colspan=\"5\" style=\"color: #FF0000\">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr>"); 
			out.println("<tr><th>Taxonomic Name&nbsp;&nbsp;</th><th>Group&nbsp;&nbsp;</th><th>Author&nbsp;&nbsp;</th><th colspan=\"2\">Submitted By</th></tr>");
			for (Iterator i = provTaxa.iterator(); i.hasNext(); ) {
				Taxon taxon = (Taxon)i.next();
				out.println("<tr><td>" + taxon.getTaxonomicName() + "&nbsp;&nbsp;</td>");
				out.println("<td>" + taxon.getTaxonomicGroup().getName() + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(taxon.getAuthor()) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(FREDUtil.getUserName(taxon.getSubmittedById())) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + ((taxon.getSubmittedDate() != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(taxon.getSubmittedDate()) : "") + "&nbsp;&nbsp;</td>");
				out.println("<tr><td>&nbsp;</td></tr>");
			}
		}

		//List rejected taxa
		if (rejTaxa.size() > 0) {
			out.println("<tr><th colspan=\"5\">Rejected Entries</th></tr>");
			out.println("<tr><td colspan=\"5\" style=\"color: #FF0000\">This record contains rejected taxonomic entries. You must remove these entries before submitting the record</td></tr>");
			out.println("<tr><th>Taxonomic Name&nbsp;&nbsp;</th><th>Group&nbsp;&nbsp;</th><th>Author&nbsp;&nbsp;</th><th colspan=\"2\">Rejected By</th></tr>");
			for (Iterator i = rejTaxa.iterator(); i.hasNext(); ) {
				Taxon taxon = (Taxon)i.next();
				out.println("<tr><td>" + taxon.getTaxonomicName() + "&nbsp;&nbsp;</td>");
				out.println("<td>" + taxon.getTaxonomicGroup().getName() + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(taxon.getAuthor()) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(FREDUtil.getUserName(taxon.getApprovedById())) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + ((taxon.getApprovedDate() != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(taxon.getApprovedDate()) : "") + "&nbsp;&nbsp;</td>");
				out.println("<tr><td>&nbsp;</td></tr>");
			}
		}

		//List obsoloete taxa
		if (obTaxa.size() > 0) {
			out.println("<tr><th colspan=\"5\">Obsolete Entries</th></tr>");
			out.println("<tr><td colspan=\"5\" style=\"color: #FF0000\">This record contains obsolete taxonomic entries. You must remove these entries before submitting the record</td></tr>");
			out.println("<tr><th>Taxonomic Name&nbsp;&nbsp;</th><th>Group&nbsp;&nbsp;</th><th>Author</th></tr>");
			for (Iterator i = obTaxa.iterator(); i.hasNext(); ) {
				Taxon taxon = (Taxon)i.next();
				out.println("<tr><td>" + taxon.getTaxonomicName() + "&nbsp;&nbsp;</td>");
				out.println("<td>" + taxon.getTaxonomicGroup().getName() + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(taxon.getAuthor()) + "&nbsp;&nbsp;</td>");
				out.println("<tr><td>&nbsp;</td></tr>");
			}
		}

		//List approved taxa
		if (appTaxa.size() > 0) {
			out.println("<tr><th colspan=\"5\">Approved Entries</th></tr>");
			out.println("<tr><th>Taxonomic Name&nbsp;&nbsp;</th><th>Group&nbsp;&nbsp;</th><th>Author&nbsp;&nbsp;</th><th colspan=\"2\">Approved By</th></tr>");
			for (Iterator i = appTaxa.iterator(); i.hasNext(); ) {
				Taxon taxon = (Taxon)i.next();
				out.println("<tr><td>" + taxon.getTaxonomicName() + "&nbsp;&nbsp;</td>");
				out.println("<td>" + taxon.getTaxonomicGroup().getName() + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(taxon.getAuthor()) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + FREDUtils.noNulls(FREDUtil.getUserName(taxon.getApprovedById())) + "&nbsp;&nbsp;</td>");
				out.println("<td>" + ((taxon.getApprovedDate() != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(taxon.getApprovedDate()) : "") + "&nbsp;&nbsp;</td>");
				out.println("<tr><td>&nbsp;</td></tr>");
			}
		}
		
		out.println("</table>");

		out.println("</td></tr></table>");
		} catch (Exception e) {
			e.printStackTrace(new PrintWriter(out));
		}
		drawBottom(out, et);
	
	}





%>


