<%@page pageEncoding="utf-8"%>
<%@page contentType="text/xml"
%><%@page import="java.text.DateFormat"
%><%@page import="java.util.Vector"
%><%@page import="java.net.URLEncoder"
%><%@page import="java.util.Collections"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.FrNumber"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.util.ByCreationDateComparator"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%!
	public String getStatusColour(String status) {
		if (status.equals(FREDConstants.WORKING))
			return "color: #00FF00";
		if (status.equals(FREDConstants.WAITING))
			return "color: #FF9900";
		if (status.equals(FREDConstants.REJECTED))
			return "color: #FF0000";
		return "";
	}
%><?xml version="1.0" encoding="UTF-8"?><%
DAOFactory factory = FredHibernate.get().getDAOFactory();
FolderUtil folderUtil = new FolderUtil(factory);
FeatureUtil featureUtil = new FeatureUtil(factory);
SampleUtil sampleUtil = new SampleUtil(factory);
RecordUtil recordUtil = new RecordUtil(factory);
Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID")));
User user = (User)session.getAttribute(User.USER_ATTRIBUTE);
UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
%><fred>
<feature id="<%=feature.getFeatureId()%>">
<feature-type><%=feature.getFeatureType()%></feature-type>
<samples><%

//samples
for (Sample sample : FeatureUtil.getSortedSamples(feature)) {
	Audit audit = sample.getAudit();
	String status = audit.getStatus();
	if (sampleUtil.isAllowedReadSample(user, sample) && status.equals(FREDConstants.APPROVED) || (audit.getFolder() != null && audit.getFolder().equals(folder.getFolder()))) {
		%><sample id="<%=sample.getSampleId()%>"><%
		if (!feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
			%><sample-name><![CDATA[<%=SampleUtil.getDrillHoleDepthDescription(sample)%>]]></sample-name>
			<status><%=status%></status>
			<status-style><%=getStatusColour(status)%></status-style>
			<created-date><%=(audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : ""%></created-date><%
			if (sampleUtil.isAllowedEditSample(user, sample, folder)) {
				%><edit>TRUE</edit>
				<set-confidentiality>TRUE</set-confidentiality>
				<edit-binary>TRUE</edit-binary><%
			} else {
				%><edit>FALSE</edit>
				<set-confidentiality>FALSE</set-confidentiality>
				<edit-binary>FALSE</edit-binary><%
			}
			if (sampleUtil.isAllowedDeleteSample(user, sample, folder)) {
				%><delete>TRUE</delete><%
			} else {
				%><delete>FALSE</delete><%
			}
			if (sampleUtil.isAllowedSubmitSample(user, sample, folder)) {
				%><submit>TRUE</submit><%
			} else {
				%><submit>FALSE</submit><%
			}
			if (folder.isAllowedCreateLocalities()) {
				%><create-adoption>TRUE</create-adoption>
				<create-paleontology>TRUE</create-paleontology><%
			} else {
				%><create-adoption>FALSE</create-adoption>
				<create-paleontology>FALSE</create-paleontology><%
			}
		}
		
		//Records
		if (sample.getRecords() != null) {
			%><records><%
			Vector<Record> records = new Vector<Record>(sample.getRecords());
			Collections.sort(records);
			for (Record record : records) {
				boolean isAdoption = RecordUtil.getRecordType(record).equals(FREDConstants.ADOPTION);
				boolean isPaleontology = !isAdoption;
				audit = record.getAudit();
				status = audit.getStatus();
				if (recordUtil.isAllowedReadRecord(user, record) && status.equals(FREDConstants.APPROVED) || (audit.getFolder() != null && audit.getFolder().equals(folder.getFolder()))) {
					%><record id="<%=record.getRecordId()%>">
					<record-type><%=RecordUtil.getRecordType(record)%></record-type>
					<record-name><![CDATA[<%=RecordUtil.getRecordName(record)%>]]></record-name>
					<status><%=status%></status>
					<status-style><%=getStatusColour(status)%></status-style>
					<created-date><%=(audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : ""%></created-date><%
					//Record Options
					if (recordUtil.isAllowedEditRecord(user, record, folder)) {
						%><edit>TRUE</edit>
						<set-confidentiality>TRUE</set-confidentiality>
						<edit-binary>TRUE</edit-binary><%
					} else {
						%><edit>FALSE</edit>
						<set-confidentiality>FALSE</set-confidentiality>
						<edit-binary>FALSE</edit-binary><%
					}
					if (recordUtil.isAllowedDeleteRecord(user, record, folder)) {
						%><delete>TRUE</delete><%
					} else {
						%><delete>FALSE</delete><%
					}
					if (recordUtil.isAllowedSubmitRecord(user, record, folder)) {
						%><submit>TRUE</submit><%
					} else {
						%><submit>FALSE</submit><%
					}
					if (isPaleontology && !RecordUtil.isTaxaApproved(record)) {
						%><bad-taxa />><%
					}
					%></record><%
				}
			}
			%></records><%
		}
		%></sample><%
	}
}
%></samples>
</feature>
</fred><%
folderUtil.closeSession();
%>