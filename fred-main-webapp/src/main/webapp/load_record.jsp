<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Copy Data";
	}
	
%><%
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	
	FeatureUtil featureUtil = new FeatureUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);
	FolderUtil folderUtil = new FolderUtil(factory);
	User user =(User) getUser(session);


	ExtranetTemplate et = getExtranetTemplate(request.getSession());
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("FoldID") != null && request.getParameter("RecType") != null) {
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
		String recID = request.getParameter("RecID");
		String sampID = request.getParameter("SampID");
		String featID = request.getParameter("FeatID");
		String recType = request.getParameter("RecType");
		
		%><p><%
		startDETable(pageContext);
		%><table border="0">
		<tr><td colspan="3" class="deHeading">Copy From</td></tr><%
		
		
		if (recType.equals(FREDConstants.OUTCROP) || recType.equals(FREDConstants.DRILLHOLE) || recType.equals(FREDConstants.VERTICAL_SECTION)) {
			//List localities
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			if (folder.isAllowedReadLocalities() && features.length > 0) {
				%><tr><th>Locality</th></tr><%		
				for (int i = 0; i < features.length; i++) {
					if (features[i].getFeatureType().equals(recType) && (featID == null || features[i].getFeatureId().intValue() != Integer.parseInt(featID))) {
						%><tr><td><a href="de.jsp?Type=<%=recType%>&FoldID=<%=folder.getFolderId() + ((featID != null) ? "&FeatID=" + featID : "")%>&CopyID=<%=features[i].getFeatureId()%>"><%=FeatureUtil.getFeatureIdentifyingName(features[i])%></a></td></tr><%
					}
				}
			}
		}
		
		else if (recType.equals("Sample")) {
			//List samples
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			if (folder.isAllowedReadLocalities() && features.length > 0) {
				%><tr><th>Locality</th><th>Sample</th></tr><%		
				for (int i = 0; i < features.length; i++) {
					if (!features[i].getFeatureType().equals(FREDConstants.OUTCROP)) {
						for (Sample sample : features[i].getSamples()) {
							if (sampID == null || sample.getSampleId().intValue() != Integer.parseInt(sampID)) {
								%><tr><td><%=FeatureUtil.getFeatureIdentifyingName(features[i])%>&nbsp;&nbsp;</td>
								<td><a href="de.jsp?Type=<%=recType%>&FoldID=<%=folder.getFolderId() + ((sampID != null) ? "&SampID=" + sampID : "")%>&CopyID=<%=sample.getSampleId()%>"><%=SampleUtil.getDrillHoleDepthDescription(sample)%></a></td></tr><%
							}
						}
					}
				}
			}
		}
		
		else { //Records
			//List records
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			if (folder.isAllowedReadLocalities() && features.length > 0) {
				%><tr><th>Locality</th><th>Sample</th><th>Record</th></tr><%
				for (int i = 0; i < features.length; i++) {
					for (Sample sample : features[i].getSamples()) {
						for (Record record : sample.getRecords()) {
							if (record != null && RecordUtil.getRecordType(record).equals(recType) && (recID == null || record.getRecordId().intValue() != Integer.parseInt(recID))) {
								%><tr><td><%=FeatureUtil.getFeatureIdentifyingName(features[i])%>&nbsp;&nbsp;</td>
								<td><%=(features[i].getFeatureType().equals(FREDConstants.OUTCROP)) ? "" : SampleUtil.getDrillHoleDepthDescription(sample)%>&nbsp;&nbsp;</td>
								<td><a href="de.jsp?Type=<%=RecordUtil.getRecordType(record)%>&FoldID=<%=folder.getFolderId() + ((recID != null) ? "&RecID=" + recID : "")%>&CopyID=<%=record.getRecordId()%>"><%=RecordUtil.getRecordName(record)%></a></td></tr><%
							}
						}
					}
				}
			}
		}

		%></table><%
		endDETable(pageContext);
		%></p><%
	}

	drawBottom(out, et);
%>
