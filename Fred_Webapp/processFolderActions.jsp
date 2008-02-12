<%@page contentType="text/xml"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
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
DAOFactory factory = HibernateUtil.get().getDAOFactory();
FolderUtil folderUtil = new FolderUtil(factory);
FeatureUtil featureUtil = new FeatureUtil(factory);
SampleUtil sampleUtil = new SampleUtil(factory);
RecordUtil recordUtil = new RecordUtil(factory);
User user = (User)session.getAttribute(User.USER_ATTRIBUTE);
UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
%><fred><%
if (request.getParameter("ActionType") != null) { //do something
	String actionType = request.getParameter("ActionType");
	try {
		if (!FREDUtil.isEmpty(request.getParameter("FeatID"))) {
			%><feature id="<%=request.getParameter("FeatID")%>">
			<action><%=actionType%></action><%
			//Get the feature
			Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID")));
			if ("DeleteFeat".equals(actionType) || "RemoveFeat".equals(actionType)) {
				if (actionType.equals("DeleteFeat") && folder.isAllowedDeleteLocalities()) {
					featureUtil.deleteFeature(feature, user);
					%><deleted /><%
				} else if (actionType.equals("RemoveFeat")) {
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
						%><create-sample>FALSE</create-sample>
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
			%><sample id="<%=request.getParameter("SampID")%>">
			<action><%=actionType%></action><%
			//submit sample
			if (actionType.equals("SubmitSamp") && folder.isAllowedSubmitLocalities()) {
				sampleUtil.submitSample(Integer.parseInt(request.getParameter("SampID")), folder, user);
				%><status><%=FREDConstants.APPROVED%></status>
				<status-stlye><%=getStatusColour(FREDConstants.APPROVED)%></status-stlye><%

			}
			//delete sample
			else if (actionType.equals("DeleteSamp") && folder.isAllowedDeleteLocalities()) {
				sampleUtil.deleteSample(Integer.parseInt(request.getParameter("SampID")), folder, user);
			}
		} else if (!FREDUtil.isEmpty(request.getParameter("RecID"))) {
			%><record id="<%=request.getParameter("RecID")%>">
			<action><%=actionType%></action><%
			//submit record
			if (actionType.equals("SubmitRec") && folder.isAllowedSubmitLocalities()) {
				recordUtil.submitRecord(Integer.parseInt(request.getParameter("RecID")), folder, user);
				%><status><%=FREDConstants.APPROVED%></status>
				<status-stlye><%=getStatusColour(FREDConstants.APPROVED)%></status-stlye><%

			}
			//delete record
			else if (actionType.equals("DeleteRec") && folder.isAllowedDeleteLocalities()) {
				recordUtil.deleteRecord(Integer.parseInt(request.getParameter("RecID")), folder, user);
			}
		}
	} catch (Exception e) {
		%><error><![CDATA[e.printStackTrace()]]></error><%
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

%></fred><%