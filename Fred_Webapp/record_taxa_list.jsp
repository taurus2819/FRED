<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.FREDUtils"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="java.util.*"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.dao.StorageAccessException"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.Taxon"
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
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	Paleontology pal = null;
	try {
		pal = recordUtil.getRecord(Integer.parseInt(request.getParameter("RecID"))).getPaleontology();
	} catch (Exception e) {e.printStackTrace(new PrintWriter(out));}

	if (pal != null) {
		try {
		drawTop(out, et, request, response);

		//drawEndNavigation(out);
		//out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		//out.println("<tr><td>");

		List appTaxa = recordUtil.getTaxon(pal, Taxon.APPROVED_STATUS);
		List provTaxa = recordUtil.getTaxon(pal, Taxon.PROVISIONAL_STATUS);
		List rejTaxa = recordUtil.getTaxon(pal, Taxon.REJECTED_STATUS);
		List obTaxa = recordUtil.getTaxon(pal, Taxon.OBSOLETE_STATUS);
%>
	<center><p>&nbsp;</p>
	
<%		
		//List provisional taxa
		if (provTaxa.size() > 0) {
			out.println("<p>");
			startDETable(pageContext);
%>
	<table border="0" cellspacing="0" cellpadding="2" width="550">
	<tr><td colspan=5 class=deHeading>Provisional Entries</td></tr>
	<tr><td colspan="5" style="text-align: left; color: #FF0000">This record contains provisional taxonomic entries. You must wait until these entries are approved before submitting the record</td></tr>
	<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Submitted By</th></tr>
<%
			for (Iterator i = provTaxa.iterator(); i.hasNext(); ) {
				Taxon taxon = (Taxon)i.next();
%>
	<tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=FREDUtils.noNulls(taxon.getAuthor())%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=((taxon.getSubmittedById() != null) ? FREDUtil.getUserName(taxon.getSubmittedById().intValue()) : "")%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=((taxon.getSubmittedDate() != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(taxon.getSubmittedDate()) : "")%>&nbsp;&nbsp;</td>
<%
			}
%>
	</table>
<%
			endDETable(pageContext);
			out.println("</p>");
		}

		//List rejected taxa
		if (rejTaxa.size() > 0) {
			out.println("<p>");
			startDETable(pageContext);
%>
	<table border="0" cellspacing="0" cellpadding="2" width="550">
	<tr><th colspan="5" class="deHeading">Rejected Entries</th></tr>
	<tr><td colspan="5" style="text-align: left; color: #FF0000">This record contains rejected taxonomic entries. You must remove these entries before submitting the record</td></tr>
	<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Rejected By</th></tr>
<%
			for (Iterator i = rejTaxa.iterator(); i.hasNext(); ) {
				Taxon taxon = (Taxon)i.next();
%>
	<tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=FREDUtils.noNulls(taxon.getAuthor())%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? FREDUtil.getUserName(taxon.getApprovedById().intValue()) : "")%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(taxon.getApprovedDate()) : "")%>&nbsp;&nbsp;</td>
<%
			}
%>
	</table>
<%
			endDETable(pageContext);
			out.println("</p>");
		}

		//List obsoloete taxa
		if (obTaxa.size() > 0) {
			out.println("<p>");
			startDETable(pageContext);
%>
	<table border="0" cellspacing="0" cellpadding="2" width="550">
	<tr><th colspan="3" class="deHeading">Obsolete Entries</th></tr>
	<tr><td colspan="3" style="text-align: left; color: #FF0000">This record contains obsolete taxonomic entries. You must remove these entries before submitting the record</td></tr>
	<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author</th></tr>
<%
			for (Iterator i = obTaxa.iterator(); i.hasNext(); ) {
				Taxon taxon = (Taxon)i.next();
%>
	<tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=FREDUtils.noNulls(taxon.getAuthor())%>&nbsp;&nbsp;</td>
<%
			}
%>
	</table>
<%
			endDETable(pageContext);
			out.println("</p>");
		}

		//List approved taxa
		if (appTaxa.size() > 0) {
			out.println("<p>");
			startDETable(pageContext);
%>
	<table border="0" cellspacing="0" cellpadding="2" width="550">
	<tr><th colspan="5" class="deHeading">Approved Entries</th></tr>
	<tr><th style="text-align: left">Taxonomic Name&nbsp;&nbsp;</th><th style="text-align: left">Group&nbsp;&nbsp;</th><th style="text-align: left">Author&nbsp;&nbsp;</th><th colspan="2" style="text-align: left">Approved By</th></tr>
<%
			for (Iterator i = appTaxa.iterator(); i.hasNext(); ) {
				Taxon taxon = (Taxon)i.next();
%>
	<tr><td style="text-align: left"><%=taxon.getTaxonomicName()%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=taxon.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=FREDUtils.noNulls(taxon.getAuthor())%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=((taxon.getApprovedById() != null) ? FREDUtil.getUserName(taxon.getApprovedById().intValue()) : "")%>&nbsp;&nbsp;</td>
	<td style="text-align: left"><%=((taxon.getApprovedDate() != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(taxon.getApprovedDate()) : "")%>&nbsp;&nbsp;</td>
<%
			}
%>
	</table>
<%
			endDETable(pageContext);
			out.println("</p>");
		}	

		out.println("</center>");
		} catch (Exception e) {
			e.printStackTrace(new PrintWriter(out));
		}
		drawBottom(out, et);
	
	}

%>


