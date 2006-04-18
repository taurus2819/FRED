<%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
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
%><%@page import="nz.cri.gns.auth.User"
%><%!
	public String getName(HttpServletRequest request) {
		DataEntryForm form = getDataEntryForm(request);
		return "FRED :: " + form.getHeading();
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
				e.printStackTrace();
				return null; //!
			}
		}		
	}
%><%
	ExtranetTemplate et = getExtranetTemplate();
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	DataEntryForm dataEntryForm = null;
	try {
		dataEntryForm = getDataEntryForm(request);
	} catch (Exception e) {
		e.printStackTrace();
		drawTop(out, et, request, response);
		drawEndNavigation(out);
		%><table style="margin-left:20px; width:550px;" border="0">
		<tr><td>
		<p>An error has occured while generating the data entry form.</p><%
	}
	
	if (dataEntryForm != null) {
		try {
			if (dataEntryForm.usesCalendar()) {
				et.addScript("calendar-stripped.js");
				et.addScript("calendar-en-stripped.js");
				et.addScript("calendar-setup-stripped.js");
				et.addStyleSheet("skins/aqua/theme.css");
			}
			List list = dataEntryForm.getNavigation();
			IconnedLink[] links = (IconnedLink[])list.toArray(new IconnedLink[list.size()+1]);
			links[links.length-1] = new IconnedLink((String)request.getSession().getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT), "images/cancel.gif", "Quit");
			et.setButtons(links);
		
			drawTop(out, et, request, response);
			
			
			if (request.getParameter("CopyID") != null)
				dataEntryForm.copyFrom(Integer.parseInt(request.getParameter("CopyID")));
		
			String formType = request.getParameter("Type");
			String featID = request.getParameter("FeatID");
			String sampID = request.getParameter("SampID");
			String recID = request.getParameter("RecID");
			String foldID = request.getParameter("FoldID");
		
			//save DataEntryForm in session
			session.setAttribute(WebsiteConstants.DATA_ENTRY_FORM, dataEntryForm);
			if (featID != null) {
				session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&FeatID=" + featID + "&FoldID=" + foldID);
			} else if (sampID != null) {
				session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&SampID=" + sampID + "&FoldID=" + foldID);
			} else if (recID != null) {
				session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&RecID=" + recID + "&FoldID=" + foldID);
			} else {
				session.setAttribute(WebsiteConstants.DATA_ENTRY_ERROR_REDIRECT, "de.jsp?Err=Yes&Type=" + formType + "&FoldID=" + foldID);
			}
			%><form name="form1" method="post" action="dp.jsp" />
<input type="hidden" name="SaveType" value="" />
<center><p>&nbsp;<p/><div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none"><%
			startDETable(pageContext);
			%><table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
<tr><td colspan="3" class="deHeading">Instructions</td></tr><tr><td style="text-align: left">
<ul>
	<li>Please fill out the following fields.  
	<li>Click the <img src="images/build.gif" height="20" width="20" alt="Build..."> icon to open the field builder for more help.  
	<li><span style="color: #FF0000">Red</span> fields must be completed before submitting this form.
</ul>
</td></tr>
<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table><%
			endDETable(pageContext);
			%></div>
<p><%
			dataEntryForm.makeDataEntryHTML(new PrintWriter(out), factory);
			%></form><%
			dataEntryForm.makePostFormHTML(new PrintWriter(out));
			%></td></tr></table><%
		}
		catch (Exception e) {
			System.out.println("************************************************************");
			System.out.println("FRED data entry form error : " + new java.util.Date());
			e.printStackTrace();
			System.out.println("************************************************************");
			drawEndNavigation(out);
			%><table style="margin-left:20px; width:550px;" border="0">
			<tr><td>
			<p>An error has occured while writing the data entry form.</p><%
		}
	}
	%><script><!--

function submitForm(saveType) {
	document.form1.SaveType.value = saveType;
	if (!window.preSubmit || preSubmit()) {
		document.form1.submit();
	}
}
//--></script><%
	drawBottom(out, et);
	
	try {
		factory.closeSession();
	} catch (Exception e) {
	}

%>
