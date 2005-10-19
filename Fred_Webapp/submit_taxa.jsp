<%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.de.PaleontologyRecordDE"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="java.util.*"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.auth.*"
%><%
	User user = (User)getUser(session);

	PaleontologyRecordDE dataEntryForm = (PaleontologyRecordDE) session.getAttribute(WebsiteConstants.DATA_ENTRY_FORM);
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	Set tL = (Set) session.getAttribute(WebsiteConstants.BAD_TAXA_LIST);
	if (tL != null) {
		TaxonomicUtil util = new TaxonomicUtil(factory);
		for (Iterator i = tL.iterator(); i.hasNext();) {
			PaleontologyListEntry t = (PaleontologyListEntry) i.next();
			util.submitProvisional(user, t);
			dataEntryForm.reinitialise(factory);
			dataEntryForm.save(t);
		}
	}
	String whereTo = (String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT);
	if (whereTo == null)
		response.sendRedirect("folder_list.jsp");
	else
		response.sendRedirect(whereTo + "&q=" + Math.random());
	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>