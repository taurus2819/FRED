<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="java.text.DateFormat"
%><%@page import="java.util.Iterator"
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
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			FeatureUtil featureUtil = new FeatureUtil(HibernateUtil.get().getDAOFactory());
			return "FRED :: Locality Details for " + FeatureUtil.getFeatureIdentifyingName(featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID"))));
		} catch (StorageAccessException e) {
			return "FRED :: Fossil Record Electronic Database";
		}
	}

%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	try {
		if (request.getParameter("FeatID") == null || request.getParameter("FoldID") == null) {
			factory.closeSession();
			if (request.getParameter("FoldID") == null)
				response.sendRedirect("folder_detail.jsp?ID=" + request.getParameter("FoldID"));
			else
				response.sendRedirect("folder_list.jsp");
			return;
		}
		
		FolderUtil folderUtil = new FolderUtil(factory);
		FeatureUtil featureUtil = new FeatureUtil(factory);
		SampleUtil sampleUtil = new SampleUtil(factory);
		RecordUtil recordUtil = new RecordUtil(factory);
		
		User user = (User)getUser(session);
		
		ExtranetTemplate et = getExtranetTemplate();
		et.setDisplayLoadingMessage(true);
		addButtons(et, new IconnedLink[] {
				new IconnedLink("folder_detail.jsp?ID=" + request.getParameter("FoldID") + "&q=" + Math.random(), "images/back_arrow.gif", "Back to folder contents")
			});
	
		Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID")));
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
	
		if (!featureUtil.folderContainsFeature(folder, feature) || !folder.isAllowedReadLocalities()) {
			response.sendRedirect("folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random());
			return;
		}
		
		session.setAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT, "folder_feature_detail.jsp?FoldID=" + folder.getFolderId() + "&FeatID=" + feature.getFeatureId());
	
		String errorMessage = null;
		if (request.getParameter("ActionType") != null) { //do something
			String actionType = request.getParameter("ActionType");
			try {
				//Copy locality
				if (actionType.equals("CopyFeat") && folder.isAllowedCreateLocalities()) {
					featureUtil.copyFeature(feature, request.getParameter("NewFeatName"), folder, user);
				}
				 //Delete feature
				else if (actionType.equals("DeleteFeat") && folder.isAllowedDeleteLocalities()) {
					featureUtil.deleteFeature(feature, user);
				}
				//Delete sample
				else if (actionType.equals("DeleteSamp") && folder.isAllowedDeleteLocalities()) {
					sampleUtil.deleteSample(Integer.parseInt(request.getParameter("SampID")), folder, user);
				}
				//Delete record
				else if (actionType.equals("DeleteRec") && folder.isAllowedDeleteLocalities()) {
					recordUtil.deleteRecord(Integer.parseInt(request.getParameter("RecID")), folder, user);
				}
				// submit working locality
				else if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
					featureUtil.submitFeature(feature, folder, user);
				}
				//Submit sample
				else if (actionType.equals("SubmitSamp") && folder.isAllowedSubmitLocalities()) {
					sampleUtil.submitSample(Integer.parseInt(request.getParameter("SampID")), folder, user);
				}
				//submit working record
				else if (actionType.equals("SubmitRec") && folder.isAllowedSubmitLocalities()) {
					recordUtil.submitRecord(Integer.parseInt(request.getParameter("RecID")), folder, user);
				}
				//Revoke waiting locality
				else if (actionType.equals("Revoke") && folder.isAllowedSubmitLocalities()) {
					featureUtil.revokeFeature(feature, folder, user);
				}
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage = "An Error has occured: " + e.getMessage();
			}
		}
	
	
		drawTop(out, et, request, response);
		//print error message (if any) from folder_actions
		if (errorMessage != null) {
			%><center><p><span class="heading" style="color: #FF0000"><%=errorMessage%></span></p></center><%
		}
		%><script><!--
		function showHide(toShow, toHide) {
			document.getElementById(toShow).style.display = 'block';
			document.getElementById(toHide).style.display = 'none';
		}
		//--></script>
		<div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none"><%
		startDETable(pageContext);
		%><table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
		<tr><td colspan="3" class="deHeading">Locality Instructions</td></tr><tr><td style="text-align: left">
		<ul>
		<li>Listed below are the working records for this locality - adoption (blue) and paleontology (green).</li>
		<li>Drillhole and Vertical Section localities will also have individual samples listed.</li>
		<li>Paleontology records marked with a red asterix contain taxonomic entries which have not been approved.  These records can not be submitted.</li>
		<li>Click on the icons to work with the locality's records:</li>
		<ul>
		<li><img src="images/edit.gif" border="0" height="20" width="20" alt="" /> to edit the locality/sample/record</li>
		<li><img src="images/lock.gif" border="0" height="20" width="20" alt="" /> set the confidentiality of the sample/record. <i>Note: localities are always open</i></li>
		<li><img src="images/new_file.gif" border="0" height="20" width="20" alt="" /> to add a file/image to the locality/sample/record</li>
		<li><img src="images/map.gif" border="0" height="20" width="20" alt="" /> to view a map of the locality</li>
		<li><img src="images/submit.gif" border="0" height="20" width="20" alt="" /> to submit the locality/sample/record for entry to the masterfile</li>
		<li><img src="images/drill.gif" border="0" height="20" width="20" alt="" /> to create a sample for this locality</li>
		<li><img src="images/new_ado.gif" border="0" height="20" width="20" alt="" /> to create a new adoption record for this locality/sample</li>
		<li><img src="images/new_pal.gif" border="0" height="20" width="20" alt="" /> to create a new paleontological record for this locality/sample</li>
		</ul>
		</ul>
		</td></tr>
		<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table><%
		endDETable(pageContext);
		%></div>
		
		<p><%
		startDETable(pageContext);
		%><form name="FoldForm" method="put" action="folder_feature_detail.jsp">
		<table border="0" width="550"><tr><td colspan="11" class="deHeading"><%=FeatureUtil.getFeatureIdentifyingName(feature)%></td></tr>
		<tr>
		<th colspan="2">Name&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Created Date&nbsp;&nbsp;</th><th colspan="7">Actions</th></tr>
		<tr><td colspan="11"><img src="images/line.gif" height="3" width="550" /></td></tr>
		<%-- Feature --%>
		<tr><td style="text-align: left"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("folder_feature_detail.jsp?FoldID=" + folder.getFolderId() + "&FeatID=" + feature.getFeatureId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/loc.gif" border="0" height="20" width="20" alt="View Locality" /></a>&nbsp;</td>
		<td style="text-align: left" class="heading"><%=FeatureUtil.getFeatureIdentifyingName(feature)%>&nbsp;&nbsp;<%
		Audit audit = feature.getAudit();
		String status = audit.getStatus();
		%><td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {"text-align: left"})%>><%=status%>&nbsp;&nbsp;<%
		if (status.equals(FREDConstants.REJECTED)) {
			%><br /><div class="smalltext">Curator comments: <%=DBUtils.nvl(audit.getCuratorComments())%></div><%
		}
		%></td>
		<td style="text-align: left"><%=(audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : ""%></td><%
		if (featureUtil.isAllowedEditFeature(user, feature, folder)) {
			%><td style="text-align: left"><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=feature.getFeatureId()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Locality" /></a>&nbsp;</td>
			<td style="text-align: left">&nbsp;</td>
			<td style="text-align: left"><a href="binary_data_entry.jsp?ID=<%=feature.getFeatureId()%>&RecType=<%=feature.getFeatureType()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/new_file.gif" border="0" height="20" width="20" alt="Add Image/File" /></a>&nbsp;</td><%
		} else {
			%><td></td><td></td><td></td><%	
		}
		%><td style="text-align: left"><a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("folder_feature_detail.jsp?FoldID=" + folder.getFolderId() + "&FeatID=" + feature.getFeatureId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" /></a>&nbsp;</td>
		<td style="text-align: left"><%
		if (featureUtil.isAllowedSubmitFeature(user, feature, folder)) {
			%><a href="javascript:if (confirm('Are you sure you want to submit this locality') == true) {document.FoldForm.ActionType.value='Submit';document.FoldForm.submit();}"><img src="images/submit.gif" border="0" height="20" width="20" alt="Submit Locality" /></a>&nbsp;<%
		}
		if (featureUtil.isAllowedRevokeFeature(user, feature, folder)) {
			%><a href="javascript:if (confirm('Are you sure you want to revoke this locality') == true) {document.FoldForm.ActionType.value='Revoke';document.FoldForm.submit();}"><img src="images/revoke.gif" border="0" height="20" width="20" alt="Revoke Locality" /></a>&nbsp;<%
		}
		%></td>
		<td style="text-align: left"><%
		if (folder.isAllowedCreateLocalities()) {
			if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
				Sample sample = featureUtil.getOutcropSample(feature);
				%><a href="de.jsp?Type=<%=FREDConstants.ADOPTION%>&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_ado.gif" border="0" height="20" width="20" alt="Add Adoption Record" /></a>&nbsp;</td>
				<td style="text-align: left"><a href="de.jsp?Type=<%=FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_pal.gif" border="0" height="20" width="20" alt="Add Paleontology Record" /></a><%
			} else {
				%><a href="de.jsp?Type=Sample&FeatID=<%=feature.getFeatureId()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/drill.gif" border="0" height="20" width="20" alt="New Sample" /></a>&nbsp;<%
			}
		}
		%></td></tr>
		<tr><td colspan="11"><img src="images/line.gif" height="3" width="550" /></td></tr><%
		
		//samples
		for (Iterator i = FeatureUtil.getSortedSamples(feature).iterator(); i.hasNext(); ) {
			Sample sample = (Sample)i.next();
			audit = sample.getAudit();
			status = audit.getStatus();
			if (status.equals(FREDConstants.APPROVED) || (audit.getFolder() != null && audit.getFolder().equals(folder.getFolder()))) {
				if (!feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
					%><tr>
					<td style="text-align: left"><a href="detail.jsp?ID=<%=sample.getSampleId()%>&backURL=<%=URLEncoder.encode("folder_feature_detail.jsp?FoldID=" + folder.getFolderId() + "&FeatID=" + feature.getFeatureId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/drill.gif" height="20" width="20" border="0" alt="View Sample Details" /></a>&nbsp;</td>
					<td style="text-align: left"><%=SampleUtil.getDrillHoleDepthDescription(sample)%>&nbsp;&nbsp;</td>
					<td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {"text-align: left"})%>><%=status%>&nbsp;&nbsp;</td>
					<td style="text-align: left"><%=(audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : ""%></td><%
					if (sampleUtil.isAllowedEditSample(user, sample, folder)) {
						%><td style="text-align: left"><a href="de.jsp?Type=Sample&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Sample Details" /></a>&nbsp;</td>
						<td style="text-align: left"><a href="set_confidentiality.jsp?ID=<%=sample.getSampleId()%>&RecType=SMP&FoldID=<%=folder.getFolderId()%>"><img src="images/lock.gif" border="0" height="20" width="20" alt="Set Confidentiality" /></a>&nbsp;</td>
						<td style="text-align: left"><a href="binary_data_entry.jsp?ID=<%=sample.getSampleId()%>&RecType=SMP&FoldID=<%=folder.getFolderId()%>"><img src="images/new_file.gif" border="0" height="20" width="20" alt="Add Image/File" /></a>&nbsp;</td><%
					} else {
						%><td></td><td></td><td></td><%
					}
					%><td style="text-align: left"><%
					if (sampleUtil.isAllowedDeleteSample(user, sample, folder)) {
						%><a href="javascript:if (confirm('Are you sure you want to delete this sample') == true) {document.FoldForm.ActionType.value='DeleteSamp';document.FoldForm.SampID.value='<%=sample.getSampleId()%>';document.FoldForm.submit();}" title="Delete Sample"><img src="images/delete.gif" border="0" height="20" width="20"></a><img src="images/blank.gif" height="20" width="2" /><%
					}
					%></td><td style="text-align: left"><%
					if (sampleUtil.isAllowedSubmitSample(user, sample, folder)) {
						%><a href="javascript:document.FoldForm.ActionType.value='SubmitSamp';document.FoldForm.SampID.value='<%=sample.getSampleId()%>';document.FoldForm.submit();"><img src="images/submit.gif" border="0" height="20" width="20" alt="Submit Sample" /></a><img src="images/blank.gif" height="20" width="2" /><%
					}
					%></td><td style="text-align: left"><%
					if (folder.isAllowedCreateLocalities()) {
						%><a href="de.jsp?Type=<%=FREDConstants.ADOPTION%>&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_ado.gif" border="0" height="20" width="20" alt="Add Adoption Record" /></a><img src="images/blank.gif" height="20" width="2" /><%
					}
					%></td><td style="text-align: left"><%
					if (folder.isAllowedCreateLocalities()) {
						%><a href="de.jsp?Type=<%=FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_pal.gif" border="0" height="20" width="20"  /></a><%
					}
					%></td></tr><%
				}
				
				//Records
				if (sample.getRecords() != null) {
					Vector<Record> records = new Vector<Record>(sample.getRecords());
					Collections.sort(records);
					for (Record record : records) {
						//Record record = (Record)k.next();
						boolean isAdoption = RecordUtil.getRecordType(record).equals(FREDConstants.ADOPTION);
						boolean isPaleontology = !isAdoption;
						audit = record.getAudit();
						status = audit.getStatus();
						if (status.equals(FREDConstants.APPROVED) || (audit.getFolder() != null && audit.getFolder().equals(folder.getFolder()))) {
							%><tr>
							<td style="text-align: left"><img src="images/<%=(isAdoption) ? "ado" : "pal"%>.gif" width="20" height="20" /></td>
							<td style="text-align: left"><%
							if (isPaleontology && !RecordUtil.isTaxaApproved(record)) {
								%><span class="heading" style="color: #FF0000">*</span>&nbsp;&nbsp;<%
							}
							%><%=RecordUtil.getRecordName(record)%>&nbsp;&nbsp;
							</td>					
							<td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {"text-align: left"})%>><%=status%>&nbsp;&nbsp;</td>
							<td style="text-align: left"><%=(audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : ""%></td><%
							//Record Options
							if (recordUtil.isAllowedEditRecord(user, record, folder)) {
								%><td style="text-align: left"><a href="de.jsp?Type=<%=(isAdoption) ? FREDConstants.ADOPTION : FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&RecID=<%=record.getRecordId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Record" /></a>&nbsp;</td>
								<td style="text-align: left"><a href="set_confidentiality.jsp?ID=<%=record.getRecordId()%>&RecType=<%=(isAdoption) ? FREDConstants.ADOPTION : FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>"><img src="images/lock.gif" border="0" height="20" width="20" alt="Set Confidentiality" /></a>&nbsp;</td>
								<td style="text-align: left"><a href="binary_data_entry.jsp?ID=<%=record.getRecordId()%>&RecType=<%=(isAdoption) ? FREDConstants.ADOPTION : FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>"><img src="images/new_file.gif" border="0" height="20" width="20" alt="Add Image/File" /></a>&nbsp;</td><%
							} else {
								%><td></td><td></td><td></td><%
							}
							%><td style="text-align: left"><%
							if (recordUtil.isAllowedDeleteRecord(user, record, folder)) {
								%><a href="javascript:if (confirm('Are you sure you want to delete this record') == true) {document.FoldForm.ActionType.value='DeleteRec';document.FoldForm.RecID.value='<%=record.getRecordId()%>';document.FoldForm.submit();}"><img src="images/delete.gif" border="0" height="20" width="20" alt="Delete Record" /></a><img src="images/blank.gif" height="20" width="2" /><%
							}
							%></td><td style="text-align: left"><%
							if (recordUtil.isAllowedSubmitRecord(user, record, folder)) {
								%><a href="javascript:document.FoldForm.ActionType.value='SubmitRec';document.FoldForm.RecID.value='<%=record.getRecordId()%>';document.FoldForm.submit();"><img src="images/submit.gif" border="0" height="20" width="20" alt="Submit Record" /></a><img src="images/blank.gif" height="20" width="2" /><%
							}
							%></td></tr><%
						}
					}
				}
				%><tr><td colspan="11"><img src="images/line.gif" height="3" width="550" /></td></tr><%
			}
		}
		%><input type="hidden" name="ActionType" value="">
		<input type="hidden" name="FoldID" value="<%=folder.getFolderId()%>">
		<input type="hidden" name="FeatID" value="<%=feature.getFeatureId()%>">
		<input type="hidden" name="SampID" value="">
		<input type="hidden" name="RecID" value="">
		<input type="hidden" name="NewFeatName" value="">
		</table><%
		
		endDETable(pageContext);
		%></p>
		
		</form>
		</td></tr></table><%
		
		drawBottom(out, et);
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		factory.closeSession();
	}
%>
