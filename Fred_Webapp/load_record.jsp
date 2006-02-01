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
		return "FRED :: Copy Data";
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
		
		%><table border="0">
		<tr><td><img src="images/blank.gif" width="200" height="10" alt="" /></td></tr>
		<tr><td></td>
		<td>
		
		
		<p><%
		startDETable(pageContext);
		%><table border="0">
		<tr><td colspan="4" class="deHeading">Copy From</td></tr><%
		
		
		if (recType.equals(FREDConstants.OUTCROP) || recType.equals(FREDConstants.DRILLHOLE) || recType.equals(FREDConstants.VERTICAL_SECTION)) {
			//List localities
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			if (folder.isAllowedReadLocalities() && features.length > 0) {
				%><tr class="heading"><td></td><td>Locality</td></tr><%		
				for (int i = 0; i < features.length; i++) {
					if (features[i].getFeatureType().equals(recType) && (featID == null || features[i].getFeatureId() != new Integer(featID))) {
						%><tr><td><a href="de.jsp?Type=<%=recType%>&FoldID=<%=folder.getFolderId() + ((featID != null) ? "&FeatID=" + featID : "")%>&CopyID=<%=features[i].getFeatureId()%>"><img src="images/load.gif" width="20" height="20" border="0" alt="Copy Locality" /></a>&nbsp;&nbsp;</td>
						<td><a href="de.jsp?Type=<%=recType%>&FoldID=<%=folder.getFolderId() + ((featID != null) ? "&FeatID=" + featID : "")%>&CopyID=<%=features[i].getFeatureId()%>"><%=FeatureUtil.getFeatureName(features[i])%></a></td></tr><%
					}
				}
			}
		}
		
		else if (recType.equals("Sample")) {
			//List samples
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			if (folder.isAllowedReadLocalities() && features.length > 0) {
				%><tr class="heading"><td></td><td>Locality</td><td>Sample</td></tr><%		
				for (int i = 0; i < features.length; i++) {
					if (!features[i].getFeatureType().equals(FREDConstants.OUTCROP) && features[i].getSamples().size() > 0) {
						for (Iterator j = features[i].getSamples().iterator(); j.hasNext(); ) {
							Sample sample = (Sample) j.next();
							if (sampID == null || sample.getSampleId() != new Integer(sampID)) {
								%><tr><td><a href="de.jsp?Type=<%=recType%>&FoldID=<%=folder.getFolderId() + ((sampID != null) ? "&SampID=" + sampID : "")%>&CopyID=<%=sample.getSampleId()%>"><img src="images/load.gif" width="20" height="20" border="0" alt="Copy Sample" /></a>&nbsp;&nbsp;</td>
								<td><%=FeatureUtil.getFeatureName(features[i])%>&nbsp;&nbsp;</td>
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
				%><tr class="heading"><td></td><td>Locality</td><td>Sample</td><td>Record</td></tr><%
				for (int i = 0; i < features.length; i++) {
					if (features[i].getSamples().size() > 0) {
						for (Iterator j = features[i].getSamples().iterator(); j.hasNext(); ) {
							Sample sample = (Sample) j.next();
							if (sample.getRecords().size() > 0) {
								for (Iterator k = sample.getRecords().iterator(); k.hasNext(); ) {
									Record record = (Record) k.next();
									if (record != null && recordUtil.getRecordType(record).equals(recType) && (recID == null || record.getRecordId() != new Integer(recID))) {
										%><tr><td><a href="de.jsp?Type=<%=recordUtil.getRecordType(record)%>&FoldID=<%=folder.getFolderId() + ((recID != null) ? "&RecID=" + recID : "")%>&CopyID=<%=record.getRecordId()%>"><img src="images/load.gif" width="20" height="20" border="0" alt="Copy Record" /></a>&nbsp;&nbsp;</td>
										<td><%=FeatureUtil.getFeatureName(features[i])%>&nbsp;&nbsp;</td>
										<td><%=(features[i].getFeatureType().equals(FREDConstants.OUTCROP)) ? "" : SampleUtil.getDrillHoleDepthDescription(sample)%>&nbsp;&nbsp;</td>
										<td><a href="de.jsp?Type=<%=recordUtil.getRecordType(record)%>&FoldID=<%=folder.getFolderId() + ((recID != null) ? "&RecID=" + recID : "")%>&CopyID=<%=record.getRecordId()%>"><%=RecordUtil.getRecordName(record)%></a></td></tr><%
									}
								}
							}
						}
					}
				}
			}
		}

		%></table><%
		endDETable(pageContext);
		%></p>
		</td></tr></table><%
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
