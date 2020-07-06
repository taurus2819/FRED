<%@page pageEncoding="utf-8"
%><%@page contentType="text/xml"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
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
	Double maxAge = (request.getParameter("max_age") != null && request.getParameter("max_age").trim().length() > 0) ? new Double(request.getParameter("max_age")) : null;
	Double minAge = (request.getParameter("min_age") != null && request.getParameter("min_age").trim().length() > 0) ? new Double(request.getParameter("min_age")) : null;
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
		<url><![CDATA[http://www.fred.org.nz/detail.jsp?ID=<%=sample.getSampleId()%>]]></url><%
		if (sv != null) {
			%><latitude><%=sv.getLatitude()%></latitude>
			<longitude><%=sv.getLongitude()%></longitude>
			<country><%=sv.getCountryName()%></country><%
		}
		%><collection_name><![CDATA[<%=sampleName%>]]></collection_name>
		<formation><![CDATA[<%=DBUtils.nvl(sample.getStratUnit())%>]]></formation>
		<taxon_name><![CDATA[<%=palList.getTaxonomicName()%>]]></taxon_name><%
		if (stage != null) {
			%><age_max><%=stageUtil.getNumericAgeStart(stage)%></age_max>
			<age_min><%=stageUtil.getNumericAgeStop(stage)%></age_min>
			<time_period_max><%=DBUtils.nvl(stageUtil.getAgeStart(stage).getPeriod())%></time_period_max>
			<time_period_min><%=DBUtils.nvl(stageUtil.getAgeStop(stage).getPeriod())%></time_period_min>
			<time_stage_max><%=stageUtil.getAgeStart(stage).getName()%></time_stage_max>
			<time_stage_min><%=stageUtil.getAgeStop(stage).getName()%></time_stage_min><%
		}
		%></occurence><%
	}
	%></occurences><%
%>