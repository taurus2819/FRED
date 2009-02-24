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
%><%@page import="nz.cri.gns.fred.util.StageUtil"
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
%><%@page import="nz.cri.gns.fred.model.Stage"
%><%@page import="nz.cri.gns.fred.model.util.ByCreationDateComparator"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><?xml version="1.0" encoding="UTF-8"?><%

	PiPUtil pipUtil = new PiPUtil(FredHibernate.get().getDAOFactory());
	RecordUtil recordUtil = new RecordUtil(FredHibernate.get().getDAOFactory());
	SampleUtil sampleUtil = new SampleUtil(FredHibernate.get().getDAOFactory());
	FeatureUtil featureUtil = new FeatureUtil(FredHibernate.get().getDAOFactory());
	StageUtil stageUtil = new StageUtil(FredHibernate.get().getDAOFactory());

	String country = request.getParameter("country");
	if (country != null && country.trim().length() == 0)
		country = null;
	String taxon = request.getParameter("taxon_name");
	if (taxon != null && taxon.trim().length() == 0)
		taxon = null;	
	Integer maxAge = (request.getParameter("max_age") != null && request.getParameter("max-age").trim().length() > 0) ? new Integer(request.getParameter("max_age")) : null;
	Integer minAge = (request.getParameter("min_age") != null && request.getParameter("mmin-age").trim().length() > 0) ? new Integer(request.getParameter("min_age")) : null;
	Integer limit = (request.getParameter("limit") != null && request.getParameter("limit").trim().length() > 0) ? new Integer(request.getParameter("limit")) : null;
	
	List<PaleontologyListEntry> palLists = pipUtil.getPiPSamples(country, taxon, maxAge, minAge, limit);
	%><occurences total="<%=palLists.size()%>"><%
	for (PaleontologyListEntry palList : palLists) {
		Sample sample = palList.getPaleontology().getRecord().getSample();
		String drillhole = SampleUtil.getDrillHoleDepthDescription(sample);
		String sampleName = FeatureUtil.getFeatureIdentifyingName(sample.getFeature()) + ((drillhole != null) ? ": " + drillhole : "");		
		SiteView sv = sample.getFeature().getSiteView();
		Stage stage = recordUtil.getStage(palList.getPaleontology());
		%><occurence>
		<occurence_no><%=palList.getPalListId()%></occurence_no>
		<collection_no><%=sample.getSampleId()%></collection_no>
		<latitude><%=sv.getLatitude()%></latitude>
		<longitude><%=sv.getLongitude()%></longitude>
		<collection_name><![CDATA[<%=sampleName%>]]></collection_name>
		<formation><![CDATA[<%=sample.getStratUnit()%>]]></formation>
		<country><%=sv.getCountryName()%></country>
		<taxon_name><![CDATA[<%=palList.getTaxonomicName()%>]]></taxon_name><%
		if (stage != null) {
			%><age-max><%=stageUtil.getAgeStart(stage)%></age-max>
			<age-min><%=stageUtil.getAgeStop(stage)%></age-min><%
		}
		%></occurence><%
	}
	%></occurences><%
%>