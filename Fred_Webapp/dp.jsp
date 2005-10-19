<%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.de.DataEntryForm"
%><%@page import="nz.cri.gns.fred.de.DataInputException"
%><%@page import="nz.cri.gns.fred.de.TaxonomicListException"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="java.util.*"
%><%@page import="java.io.IOException"
%><%@page import="java.sql.SQLException"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.auth.*"
%><%
	ExtranetTemplate et = getExtranetTemplate();

	if (request.getParameter("SaveType") != null) {
		DataEntryForm dataEntryForm = (DataEntryForm) session.getAttribute(WebsiteConstants.DATA_ENTRY_FORM);
		try {
			DAOFactory factory = HibernateUtil.get().getDAOFactory();
			dataEntryForm.updateFromRequest(request, factory);
			
			if (request.getParameter("SaveType").equals("Submit")) {
				dataEntryForm.submit();
			} else {
				dataEntryForm.save();
			}
			factory.closeSession();
			String whereTo = (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT);
			if (whereTo == null)
				response.sendRedirect("folder_list.jsp");
			else
				response.sendRedirect(whereTo + "&q=" + Math.random());
			return;

		} catch (TaxonomicListException e) {
			//Still save it
			dataEntryForm.save();
			drawTop(out, et, request, response);
			%>
<table style="margin-left:20px; margin-top:20px; width:150px;" border="0">
<tr><td colspan="2" align="center"><img src="images/loc.gif" height="20" width="20" /></td></tr>
<tr><td colspan="2" align="center" class="heading">Data Entry Error</td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td><a href="<%=(String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT)%>"><img src="images/back_arrow.gif" height="20" width="20" border="0" alt="Back to Data Entry" /></a><img src="images/blank.gif" height="20" width="10" border="0" /></td><td><a href="<%=(String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT)%>" class="heading">Back to Data Entry</a></td></tr>
<tr><td><a href="<%=(String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT)%>"><img src="images/cancel.gif" height="20" width="20" border="0" alt="Quit Without Saving" /></a><img src="images/blank.gif" height="20" width="10" border="0" /></td><td><a href="<%=(String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT)%>" class="heading">Quit</a></td></tr>
</table><% 
			drawEndNavigation(out);
%><table style="margin-left:20px; width:550px;" border="0">
<tr><td><%
			session.setAttribute("taxa", request.getParameter("Taxa"));
			session.setAttribute(WebsiteConstants.BAD_TAXA_LIST, e.getTaxaList());
			session.setAttribute(WebsiteConstants.DATA_ENTRY_FORM, dataEntryForm);
%>
<p><span class="bigheading">Data Error</span></p>
<p>The following list contains taxonomic entries which do not match a value in the thesaurus.  This could be either because you have entered incorrect syntax or because the entry is not in the thesaurus.<br />Note submitted entries will be provisional until checked by database curators and you will not be able to submit this record until the entry has been approved.</p>
<table border="0" cellspacing="2">
<tr><th>Group&nbsp;&nbsp;</th><th>Entered Name&nbsp;&nbsp;</th><th>Parsed Name&nbsp;&nbsp;</th><th>Author</th></tr><%
			for (Iterator i = e.getTaxaList().iterator(); i.hasNext();) {
				PaleontologyListEntry t = (PaleontologyListEntry) i.next();
				out.println("<tr><td>" + t.getTaxonomicGroup().getName() + "&nbsp;&nbsp;</td><td>" + t.getTaxonomicName() + "&nbsp;&nbsp;</td><td>" + t.getTaxon().getTaxonomicName() + "&nbsp;&nbsp;</td><td>" + t.getTaxon().getAuthor() + "</td></tr>");
			}
%></table><%
			out.println("<p><a href='submit_taxa.jsp'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit Taxa' /></a>&nbsp;<a href='submit_taxa.jsp' class='boldlink'>Submit Taxa.</a></p>");
			out.println("<p>Note: No reference to these taxa has been saved yet.  You must either choose to submit the above taxa or return to the data entry form, edit and re-save</p>");
		} catch (DataInputException e) {
			out.println(e.getError().size());
			drawTop(out, et, request, response);
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "' class='heading'>Back to Data Entry</a></td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>Data Error</span></p>");
			out.println("<table border='0' cellspacing='0'>");
			for (Iterator it = e.getError().iterator(); it.hasNext(); ) {
				String[] error = (String[])it.next();
			    out.println("<tr><td class='heading'>Problem Field<img src='images/blank.gif' width='20' height='1' /></td><td>" + error[0] + "</td></tr>");
				out.println("<tr><td class='heading'>Error</td><td>"+ error[1] + "</td></tr>");
			}
			out.println("</table>");
		} catch (InsufficientPrivelegesException e) {
			drawTop(out, et, request, response);
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "' class='heading'>Back to Data Entry</a></td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>Data Error</span></p>");
			out.println("<p>You do not have sufficient rights to save this record</p>");
		} catch (IOException e) {
			drawTop(out, et, request, response);
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "' class='heading'>Back to Data Entry</a></td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>IO Data Error</span></p>");
			out.println("<p>A Database error has occured: " + e.getMessage() + "</p>");
		} catch (SQLException e) {
			drawTop(out, et, request, response);
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "' class='heading'>Back to Data Entry</a></td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>Database Error</span></p>");
			out.println("<p>A Database error has occured: " + e.getMessage() + "</p>");
		}
	}
	else {
		drawTop(out, et, request, response);
		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
		out.println("<tr><td>&nbsp;</td></tr>");
		out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT) + "' class='heading'>Back to Data Entry</a></td></tr>");
		out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "' class='heading'>Quit</a></td></tr>");
		out.println("</table>");
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");
		out.println("<p><span class='bigheading'>Unidentified Data Entry Error has occured</span></p>");
	}
	out.println("</td></tr></table>");
	drawBottom(out, et);
	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>