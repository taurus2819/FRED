<%@page pageEncoding="utf-8"
%><%@page contentType="text/xml"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.Vector"
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
User user = (User)session.getAttribute(User.USER_ATTRIBUTE);
UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
%><fred><%
if (request.getParameter("ActionType") != null) {
	String actionType = request.getParameter("ActionType");
	%><action><%=actionType%></action><%
	try {
		if (!FREDUtil.isEmpty(request.getParameter("FeatID"))) {
			Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID")));
			%><feature id="<%=feature.getFeatureId()%>"><%
			if ("Delete".equals(actionType) || "Remove".equals(actionType)) {
				if (actionType.equals("Delete") && folder.isAllowedDeleteLocalities()) {
					featureUtil.deleteFeature(feature, user);
					%><deleted /><%
				} else if (actionType.equals("Remove")) {
					featureUtil.removeFeature(feature, folder, user);
					%><removed /><%
				}
			} else if ("Submit".equals(actionType) || "Revoke".equals(actionType)) {
				//submit locality
				if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
					featureUtil.submitFeature(feature, folder, user);
				} else if (actionType.equals("Revoke") && folder.isAllowedSubmitLocalities()) {
					featureUtil.revokeFeature(feature, folder, user);
				}
				Audit audit = feature.getAudit();
				String status = audit.getStatus();
				%><feature-identifying-name><%=FeatureUtil.getFeatureIdentifyingName(feature)%></feature-identifying-name>
				<feature-name><%=feature.getFeatureName()%></feature-name>
				<feature-type><%=feature.getFeatureType()%></feature-type>
				<status><%=status%></status>
				<status-style><%=getStatusColour(status)%></status-style><%
				if (status.equals(FREDConstants.REJECTED)) {
					%><curator-comments><%=DBUtils.nvl(audit.getCuratorComments())%></curator-comments><%
				}
				%><created-date><%=(audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : ""%></created-date><%
				if (featureUtil.isAllowedEditFeature(user, feature, folder)) {
					%><edit>TRUE</edit>
					<edit-binary>TRUE</edit-binary><%
				} else {
					%><edit>FALSE</edit>
					<edit-binary>FALSE</edit-binary><%
				}
				if (folder.isAllowedCreateLocalities()) {
					%><copy-locality>TRUE</copy-locality><%
				} else {
					%><copy-locality>FALSE</copy-locality><%
				}
				if (!status.equals(FREDConstants.APPROVED) && featureUtil.isAllowedDeleteFeature(user, feature, folder)) {
					%><delete>TRUE</delete>
					<remove>FALSE</remove><%
				} else if (status.equals(FREDConstants.APPROVED) && !FREDUtil.isEmpty(feature.getFolders())) {
					%><delete>FALSE</delete>
					<remove>TRUE</remove><%
				} else {
					%><delete>FALSE</delete>
					<remove>FALSE</remove><%
				}
				if (featureUtil.isAllowedSubmitFeature(user, feature, folder)) {
					%><submit>TRUE</submit>
					<revoke>FALSE</revoke><%
				} else if (featureUtil.isAllowedRevokeFeature(user, feature, folder)) {
					%><submit>FALSE</submit>
					<revoke>TRUE</revoke><%
				} else {
					%><submit>FALSE</submit>
					<revoke>FALSE</revoke><%
				}
				if (folder.isAllowedCreateLocalities()) {
					if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
						Sample sample = featureUtil.getOutcropSample(feature);
						%><create-sample>FALSE</create-sample>
						<sample-id><%=sample.getSampleId()%></sample-id>
						<create-adoption>TRUE</create-adoption>
						<create-paleontology>TRUE</create-paleontology><%
					} else {
						%><create-sample>TRUE</create-sample>
						<create-adoption>FALSE</create-adoption>
						<create-paleontology>FALSE</create-paleontology><%
					}
				} else {
					%><create-sample>FALSE</create-sample>
					<create-adoption>FALSE</create-adoption>
					<create-paleontology>FALSE</create-paleontology><%					
				}
			}
		} else if (!FREDUtil.isEmpty(request.getParameter("SampID"))) {
			Sample sample = sampleUtil.getSample(Integer.parseInt(request.getParameter("SampID")));
			%><sample id="<%=sample.getSampleId()%>"><%
			//submit sample
			if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
				sampleUtil.submitSample(Integer.parseInt(request.getParameter("SampID")), folder, user);
				Audit audit = sample.getAudit();
				String status = audit.getStatus();
				if (!sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
					%><sample-name><%=SampleUtil.getDrillHoleDepthDescription(sample)%></sample-name>
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
			}
			//delete sample
			else if (actionType.equals("Delete") && folder.isAllowedDeleteLocalities()) {
				Vector<Integer> deleteIds = new Vector<Integer>();
				for (Record record : sample.getRecords())
					deleteIds.add(record.getRecordId());
				sampleUtil.deleteSample(Integer.parseInt(request.getParameter("SampID")), folder, user);
				%><deleted /><%
				for (Integer deleteId : deleteIds) {
					%><delete-record id="<%=deleteId%>" /><%
				}
				
			}
		} else if (!FREDUtil.isEmpty(request.getParameter("RecID"))) {
			Record record = recordUtil.getRecord(Integer.parseInt(request.getParameter("RecID")));
			%><record id="<%=record.getRecordId()%>"><%
			//submit record
			if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
				recordUtil.submitRecord(Integer.parseInt(request.getParameter("RecID")), folder, user);
				Audit audit = record.getAudit();
				String status = audit.getStatus();
				boolean isAdoption = RecordUtil.getRecordType(record).equals(FREDConstants.ADOPTION);
				boolean isPaleontology = !isAdoption;
				%><record-type><%=RecordUtil.getRecordType(record)%></record-type>
				<record-name><%=RecordUtil.getRecordName(record)%></record-name>
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
			}
			//delete record
			else if (actionType.equals("Delete") && folder.isAllowedDeleteLocalities()) {
				recordUtil.deleteRecord(Integer.parseInt(request.getParameter("RecID")), folder, user);
				%><deleted /><%
			}
		}
	} catch (Exception e) {
		%><error><![CDATA[<%=e.getMessage()%>]]></error><%
	} finally {
		if (!FREDUtil.isEmpty(request.getParameter("FeatID"))) {
			%></feature><%
		} else if (!FREDUtil.isEmpty(request.getParameter("SampID"))) {
			%></sample><%
		} else if (!FREDUtil.isEmpty(request.getParameter("RecID"))) {
			%></record><%
		}
	}
}

%></fred>