<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
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
		return "FRED :: Copy Feature";
	}
	
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	
	FeatureUtil featureUtil = new FeatureUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);
	FolderUtil folderUtil = new FolderUtil(factory);
	User user =(User) getUser(session);


	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("FoldID") != null && request.getParameter("RecType") != null) {
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
		String recID = request.getParameter("RecID");
		String sampID = request.getParameter("SampID");
		String featID = request.getParameter("FeatID");
		String recType = request.getParameter("RecType");

		if (recType.equals(FREDConstants.OUTCROP) || recType.equals(FREDConstants.DRILLHOLE) || recType.equals(FREDConstants.VERTICAL_SECTION)) {
			%><p>Choose the locality to copy from the list below by clicking on the <img src="images/load.gif" width="20" height="20" alt="Copy Icon" /> icon</p><%
			//List localities
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			if (folder.isAllowedReadLocalities() && features.length > 0) {
				%><table border="0" cellspacing="0" cellpadding="2">
				<tr class="heading"><td></td><td>Locality</td></tr><%		
				for (int i = 0; i < features.length; i++) {
					if (features[i].getFeatureType().equals(recType) && (featID == null || features[i].getFeatureId() != Integer.parseInt(featID))) {
						%><tr><td><a href="de.jsp?Type=<%=recType%>&FoldID=<%=folder.getFolderId() + ((featID != null) ? "&FeatID=" + featID : "")%>&CopyID=<%=features[i].getFeatureId()%>"><img src="images/load.gif" width="20" height="20" border="0" alt="Copy Locality" /></a>&nbsp;&nbsp;</td>
						<td><%=FeatureUtil.getFeatureName(features[i])%></td></tr><%
					}
				}
			}
		} else if (recType.equals("Sample")) {
			%><p>Choose the sample to copy from the list below by clicking on the <img src="images/load.gif" width="20" height="20" alt="Copy Icon" /> icon</p><%
			//List localities
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			if (folder.isAllowedReadLocalities() && features.length > 0) {
				%><table border="0" cellspacing="0" cellpadding="2">
				<tr class="heading"><td></td><td>Locality</td><td>Sample</td></tr><%		
				for (int i = 0; i < features.length; i++) {
					if (!features[i].getFeatureType().equals(FREDConstants.OUTCROP) && features[i].getSamples().size() > 0) {
						for (Iterator j = features[i].getSamples().iterator(); j.hasNext(); ) {
							Sample sample = (Sample) j.next();
							if (sampID == null || sample.getSampleId() != Integer.parseInt(sampID)) {
								%><tr><td><a href="data_entry.jsp?Type=<%=recType%>&FoldID=<%=folder.getFolderId() + ((sampID != null) ? "&SampID=" + sampID : "")%>&CopyID=<%=sample.getSampleId()%>"><img src="images/load.gif" width="20" height="20" border="0" alt="Copy Sample" /></a>&nbsp;&nbsp;</td>
								<td><%=FeatureUtil.getFeatureName(features[i])%>&nbsp;&nbsp;</td><td><%=SampleUtil.getDrillHoleDepthDescription(sample)%></td></tr><%
							}
						}
					}
				}
			}
		} else { //Records
			%><p>Choose the record to copy from the list below by clicking on the <img src="images/load.gif" width="20" height="20" alt="Copy Icon" /> icon</p><%
			//List localities
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			if (folder.isAllowedReadLocalities() && features.length > 0) {
				%><table border="0" cellspacing="0" cellpadding="2">
				<tr class="heading"><td></td><td>Locality</td><td>Sample</td><td>Record</td></tr><%
				for (int i = 0; i < features.length; i++) {
					if (features[i].getSamples().size() > 0) {
						for (Iterator j = features[i].getSamples().iterator(); j.hasNext(); ) {
							Sample sample = (Sample) j.next();
							if (sample.getRecords().size() > 0) {
								for (Iterator k = sample.getRecords().iterator(); k.hasNext(); ) {
									Record record = (Record) k.next();
									if (record != null && (recID == null || record.getRecordId() != Integer.parseInt(recID))) {
										%><tr><td><a href="data_entry.jsp?Type=<%=recordUtil.getRecordType(record)%>&FoldID=<%=folder.getFolderId() + ((recID != null) ? "&RecID=" + recID : "")%>&CopyID=<%=record.getRecordId()%>"><img src="images/load.gif" width="20" height="20" border="0" alt="Copy Record" /></a>&nbsp;&nbsp;</td>
										<td><%=FeatureUtil.getFeatureName(features[i])%>&nbsp;&nbsp;</td><td><%=SampleUtil.getDrillHoleDepthDescription(sample)%>&nbsp;&nbsp;</td><td><%=recordUtil.getRecordName(record)%></td></tr><%
									}
								}
							}
						}
					}
				}
			}
		}

		out.println("</table>");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
