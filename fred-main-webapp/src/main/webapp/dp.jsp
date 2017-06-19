<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.de.DataEntryForm"
%><%@page import="nz.cri.gns.fred.de.DataInputException"
%><%@page import="nz.cri.gns.fred.de.TaxonomicListException"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="java.io.IOException"
%><%@page import="java.sql.SQLException"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Data Entry Error";
	}
%><%
	ExtranetTemplate et = getExtranetTemplate();
	addButtons(et, new IconnedLink[] {
			new IconnedLink((String)session.getAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT), "images/back_arrow.gif", "Back to Data Entry"),
			new IconnedLink((String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT), "images/cancel.gif", "Quit")
	});
	
	if (request.getParameter("SaveType") != null) {
		DataEntryForm dataEntryForm = (DataEntryForm) session.getAttribute(WebsiteConstants.DATA_ENTRY_FORM);
		try {
			DAOFactory factory = FredHibernate.get().getDAOFactory();
			dataEntryForm.updateFromRequest(request, factory, false);
			
			if (request.getParameter("SaveType").equals("Submit")) {
				dataEntryForm.submit(FREDConstants.DATA_ORIGIN_ONLINE);
			} else {
				dataEntryForm.save(FREDConstants.DATA_ORIGIN_ONLINE);
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
			dataEntryForm.save(FREDConstants.DATA_ORIGIN_ONLINE);
			session.setAttribute("taxa", request.getParameter("Taxa"));
			session.setAttribute(WebsiteConstants.BAD_TAXA_LIST, e.getTaxaList());
			session.setAttribute(WebsiteConstants.DATA_ENTRY_FORM, dataEntryForm);

			drawTop(out, et, request, response);
			%><p><%
			startDETable(pageContext);
			%><table style="margin-left:20px; width:550px;" border="0">
			<tr><td colspan="4" class="deHeading">Taxonomic Name Error</td></tr>
			<tr><td colspan="4">The following list contains taxonomic entries which do not match a value in the thesaurus.  This could be either because you have entered incorrect syntax or because the entry is not in the thesaurus.<br />Note submitted entries will be provisional until checked by database curators and you will not be able to submit this record until the entry has been approved.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><th>Group&nbsp;&nbsp;</th><th>Entered Name&nbsp;&nbsp;</th><th>Parsed Name&nbsp;&nbsp;</th><th>Author</th></tr><%
			for (PaleontologyListEntry t : e.getTaxaList()) {
				%><tr><td><%=t.getTaxonomicGroup().getName()%>&nbsp;&nbsp;</td><td><%=t.getTaxonomicName()%>&nbsp;&nbsp;</td><td><%=t.getTaxon().getTaxonomicName()%>&nbsp;&nbsp;</td><td><%=t.getTaxon().getAuthor()%></td></tr><%
			}
			%><tr><td colsapn="4"><a href="submit_taxa.jsp"><img src="images/submit.gif" height="20" width="20" border="0" alt="Submit Taxa" title="Submit"/></a>&nbsp;<a href="submit_taxa.jsp">Submit Taxa.</a></td></tr>
			<tr><td colspan="4">Note: No reference to these taxa has been saved yet.  You must either choose to submit the above taxa or return to the data entry form, edit and re-save</td></tr>
			</table><%
			endDETable(pageContext);
			%></p><%
		} catch (DataInputException e) {
			drawTop(out, et, request, response);
			startDETable(pageContext);
			%><table style="margin-left:20px; width:550px;" border="0">
			<tr><td colspan="2" class="deHeading">Syntax Error</td></tr><%
			for (String[] error : e.getError()) {
			    %><tr><td class="heading">Problem Field&nbsp;&nbsp;</td><td><%=error[0]%></td></tr>
				<tr><td class="heading">Error</td><td><%=error[1]%></td></tr><%
			}
			%></table><%
			endDETable(pageContext);
			%></p><%
		} catch (InsufficientPrivelegesException e) {
			drawTop(out, et, request, response);
			startDETable(pageContext);
			%><table style="margin-left:20px; width:550px;" border="0">
			<tr><td class="deHeading">Insufficient Privileges Error</td></tr>
			<tr><td>You do not have sufficient rights to save this record</td></tr><%
			%></table><%
			endDETable(pageContext);
			%></p><%
		} catch (IOException e) {
			drawTop(out, et, request, response);
			startDETable(pageContext);
			%><table style="margin-left:20px; width:550px;" border="0">
			<tr><td class="deHeading">I/O Data Error</td></tr>
			<tr><td>A Database error has occured: <%=e.getMessage()%></td></tr><%
			%></table><%
			endDETable(pageContext);
			%></p><%
		} catch (SQLException e) {
			drawTop(out, et, request, response);
			startDETable(pageContext);
			%><table style="margin-left:20px; width:550px;" border="0">
			<tr><td class="deHeading">SQL Data Error</td></tr>
			<tr><td>A Database error has occured: <%=e.getMessage()%></td></tr><%
			%></table><%
			endDETable(pageContext);
			%></p><%
		} catch (StorageAccessException e) {
			drawTop(out, et, request, response);
			startDETable(pageContext);
			%><table style="margin-left:20px; width:550px;" border="0">
			<tr><td class="deHeading">Storage Data Error</td></tr>
			<tr><td>A Database error has occured: <%=e.getMessage()%></td></tr><%
			%></table><%
			endDETable(pageContext);
			e.printStackTrace();
			%></p><%
		}
	}
	else {
		drawTop(out, et, request, response);
		startDETable(pageContext);
		%><table style="margin-left:20px; width:550px;" border="0">
		<tr><td class="deHeading">Data Error</td></tr>
		<tr><td>A unidentified error has occured</td></tr><%
		%></table><%
		endDETable(pageContext);
		%></p><%
	}
	drawBottom(out, et);
	try {
		FredHibernate.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>