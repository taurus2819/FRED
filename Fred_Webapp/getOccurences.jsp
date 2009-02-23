<%@page contentType="text/xml"
%><%@page import="java.text.DateFormat"
%><%@page import="java.util.Vector"
%><%@page import="java.net.URLEncoder"
%><%@page import="java.util.Collections"
%><%@page import="java.util.List"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.PiPUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.FrNumber"
%><%@page import="nz.cri.gns.fred.model.SiteView"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.util.ByCreationDateComparator"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><?xml version="1.0" encoding="UTF-8"?><%

	PiPUtil pipUtil = new PiPUtil(FredHibernate.get().getDAOFactory());
	SampleUtil sampleUtil = new SampleUtil(FredHibernate.get().getDAOFactory());

	String country = request.getParameter("country");
	String taxonName = request.getParameter("taxonName");
	Integer maxAge = (request.getParameter("maxAge") != null) ? new Integer(request.getParameter("maxAge")) : null;
	Integer minAge = (request.getParameter("minAge") != null) ? new Integer(request.getParameter("minAge")) : null;
	Integer limit = (request.getParameter("limit") != null) ? new Integer(request.getParameter("limit")) : null;
	
	List<PaleontologyListEntry> palLists = pipUtil.getPiPSamples(country, taxonName, maxAge, minAge, limit);
	
	for (PaleontologyListEntry palList : palLists) {
		Sample sample = palList.getPaleontology().getRecord().getSample();
		SiteView sv = sample.getFeature().getSiteView();
		%><occurence>
		<occurence_no><%=palList.getPalListId()%></occurence_no>
		<collection_no><%=sample.getSampleId()%></collection_no>
		<latitude><%=sv.getLatitude()%></latitude>
		<longitude><%=sv.getLongitude()%></longitude>
		<collection_name><%=SampleUtil.getDrillHoleDepthDescription(sample)%></collection_name>
		<formation><%=sample.getStratUnit()%></formation>
		<country><%=sv.getCountryName()%></country>
		<taxon_name><%=palList.getTaxonomicName()%></taxon_name>
		</occurence><%
	}
%>