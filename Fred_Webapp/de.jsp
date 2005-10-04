<%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.IconnedLink"
%><%@page import="nz.cri.gns.fred.de.DataEntryForm"
%><%@page import="nz.cri.gns.fred.de.DataEntryFormFactory"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.website.ContentProvider"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="java.io.File"
%><%@page import="java.io.PrintWriter"
%><%@page import="java.util.List"
%><%@page import="nz.cri.gns.auth.*"
%><%!
	public String getName(HttpServletRequest request) {
		DataEntryForm form = getDataEntryForm(request);
		return "FRED :: " + form.getHeading();
	}
	
	protected IconnedLink[] getButtons(HttpServletRequest request) {
		DataEntryForm form = getDataEntryForm(request);
		List list = form.getNavigation();
		return (IconnedLink[])list.toArray(new IconnedLink[list.size()]);
	}

	protected DataEntryForm getDataEntryForm(HttpServletRequest request) {
		DataEntryForm form = getDataEntryFormImpl(request);
		request.setAttribute(WebsiteConstants.DATA_ENTRY_FORM, form);
		request.getSession().setAttribute(WebsiteConstants.DATA_ENTRY_FORM, form);
		return form;
	}
	
	private DataEntryForm getDataEntryFormImpl(HttpServletRequest request) {
		if (request.getAttribute(WebsiteConstants.DATA_ENTRY_FORM) != null)
			return (DataEntryForm) request.getAttribute(WebsiteConstants.DATA_ENTRY_FORM);
		HttpSession session = request.getSession();
		if (request.getParameter("Err") != null || request.getParameter("CopyID") != null) {
			return (DataEntryForm) session.getAttribute(WebsiteConstants.DATA_ENTRY_FORM);
		} else {
			DAOFactory factory = HibernateUtil.get().getDAOFactory();
			try {
				User user = (User)getUser(session);
				String formType = request.getParameter("Type");
				String foldID = request.getParameter("FoldID");
				ContentProvider provider = new ContentProvider(new File(request.getSession().getServletContext().getRealPath("/content")));
				if (formType.equals(FREDConstants.OUTCROP) || formType.equals(FREDConstants.DRILLHOLE) || formType.equals(FREDConstants.VERTICAL_SECTION)) {
					String featID = request.getParameter("FeatID");
					if (featID != null) { //editing
						return DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), Integer.parseInt(foldID), user, factory, provider);
					} else {
						return DataEntryFormFactory.getLocalityDataEntryForm(formType, user, Integer.parseInt(foldID), factory, provider);
					}
					//if (request.getParameter("LoadFeatID") != null) //copying
					//	dataEntryForm.copyFrom(Integer.parseInt(request.getParameter("LoadFeatID")));
				} else if (formType.equals("Sample")) {
					String sampID = request.getParameter("SampID");
					if (sampID != null) { //editing
						return DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(sampID), Integer.parseInt(foldID), user, factory, provider);
					} else {
						String featID = request.getParameter("FeatID");
						return DataEntryFormFactory.getSampleDataEntryForm(user, Integer.parseInt(featID), Integer.parseInt(foldID), factory, provider);
					}
				} else {
					String recID = request.getParameter("RecID");
					if (recID != null) { //editing
						return DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(recID), Integer.parseInt(foldID), user, factory, provider);
					} else {
						String sampID = request.getParameter("SampID");
						return DataEntryFormFactory.getRecordDataEntryForm(formType, user, Integer.parseInt(sampID), Integer.parseInt(foldID), factory, provider);
					}
				}
			} catch (Exception e) {
				return null; //!
			} finally {
				try {
					factory.closeSession();
				} catch (Exception e) {
				}
			}
		}		
	}
%><%
	
	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);
	
	DataEntryForm dataEntryForm = getDataEntryForm(request);
	
	if (request.getParameter("CopyID") != null) {
		dataEntryForm.copyFrom(Integer.parseInt(request.getParameter("CopyID")));
	}

	String formType = request.getParameter("Type");
	String featID = request.getParameter("FeatID");
	String sampID = request.getParameter("SampID");
	String recID = request.getParameter("RecID");
	String foldID = request.getParameter("FoldID");

	if (dataEntryForm != null) {
		//save DataEntryForm in session
		session.setAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT, dataEntryForm);
		if (featID != null) {
			session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&FeatID=" + featID);
		} else if (sampID != null) {
			session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&SampID=" + sampID);
		} else if (recID != null) {
			session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&RecID=" + recID);
		} else {
			session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&FoldID=" + foldID);
		}
		/*
		out.println("<tr><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a>&nbsp;&nbsp;</td><td><a href='" + (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "' class='heading'>Quit</a></td></tr>");
		*/
		%><form name="form1" method="post" action="dp.jsp" />
<input type="hidden" name="SaveType" value="" />
<table style="margin-left:20px; width:550px;" border="0">
<tr><td>Please fill out the following fields.  Click the <img src=\"images/build.gif\" height=\"20\" width=\"20\" alt=\"Build...\"> icon to open the field builder for more help.  <span style=\"color: #FF0000\">Red</span> fields must be completed before submitting this form.</td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td><%
		dataEntryForm.makeDataEntryHTML(new PrintWriter(out));
		%></td></tr></table>
</form><%
		dataEntryForm.makePostFormHTML(new PrintWriter(out));
	}
	else {
		drawEndNavigation(out);
		%><table style="margin-left:20px; width:550px;" border="0">
<tr><td>
<p><span class="bigheading">Access denied</span><br />You don't have rights to edit this locality/record</p><%
	
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
