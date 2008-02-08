<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="java.util.List"
%><%@page import="java.util.Iterator"
%><%@page import="java.net.URLEncoder"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.dataaccess.StorageAccessException"
%><%@page import="nz.cri.gns.fred.de.MandatoryFieldsMissingException"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.FrNumber"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			FolderUtil folderUtil = new FolderUtil(HibernateUtil.get().getDAOFactory());
			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));
			return "FRED :: " + folder.getFolder().getName();
		} catch (Exception e) {
			return "FRED :: The Fossil Record Electronic Database";
		}
	}	
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	if (request.getParameter("ID") == null) {
		factory.closeSession();
		response.sendRedirect("folder_list.jsp");
		return;
	}
	
	FolderUtil folderUtil = new FolderUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);
	SampleUtil sampleUtil = new SampleUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);
	User user =(User) getUser(session);
	UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), user);
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	et.addScript("/online/scripts/ajax.js");
	
	if (folder != null && folder.isAllowedCreateLocalities()) {
		IconnedLink[] iLink = new IconnedLink[((folder.isBacklogFolder()) ? 6 : 5)];
		iLink[0] = new IconnedLink("folder_list.jsp?&q=" + Math.random(), "images/back_arrow.gif", "Back to Folders");
		iLink[1] = new IconnedLink("de.jsp?Type=Outcrop&FoldID=" + folder.getFolder().getFolderId(), "images/new.gif", "New Outcrop");
		iLink[2] = new IconnedLink("de.jsp?Type=Drillhole&FoldID=" + folder.getFolder().getFolderId(), "images/new.gif", "New Drillhole");
		iLink[3] = new IconnedLink("de.jsp?Type=Vertical+Section&FoldID=" + folder.getFolder().getFolderId(), "images/new.gif", "New Vert. Section");
		iLink[4] = new IconnedLink("folder_taxa_list.jsp?ID=" + folder.getFolder().getFolderId() + "&q=" + Math.random() , "images/loc.gif", "Taxa Status");
		if (folder.isBacklogFolder())
			iLink[5] = new IconnedLink("backlog_status.jsp", "images/map.gif", "Backlog Status");
		addButtons(et, iLink);
	} else {
		addButtons(et, new IconnedLink[] {
			new IconnedLink("folder_list.jsp?q=" + Math.random(), "images/back_arrow.gif", "Back to folders"),
			new IconnedLink("folder_taxa_list.jsp?ID=" + folder.getFolder().getFolderId() + "&q=" + Math.random(), "images/loc.gif", "Taxa Status")
		});
	}	
	
	drawTop(out, et, request, response);
	
	if (folder != null && folder.isAllowedReadLocalities()) {
		session.setAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT, "folder_detail.jsp?ID=" + folder.getFolder().getFolderId());
		String errorMessage = null;
		if (request.getParameter("ActionType") != null) { //do something
			String actionType = request.getParameter("ActionType");
			try {
				if (!FREDUtil.isEmpty(request.getParameter("FeatID"))) {
					//Get the feature
					Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID")));
					//Copy locality
					if (actionType.equals("CopyFeat") && folder.isAllowedCreateLocalities()) {
						featureUtil.copyFeature(feature, request.getParameter("NewFeatName"), folder, user);
					}
					 //Delete locality
					else if (actionType.equals("DeleteFeat") && folder.isAllowedDeleteLocalities()) {
						featureUtil.deleteFeature(feature, user);
					}
					//Remove locality
					else if (actionType.equals("RemoveFeat")) {
						featureUtil.removeFeature(feature, folder, user);
					}
					//submit locality
					else if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
						featureUtil.submitFeature(feature, folder, user);
					}
					//revoke locality
					else if (actionType.equals("Revoke") && folder.isAllowedSubmitLocalities()) {
						featureUtil.revokeFeature(feature, folder, user);
					}
				} else if (!FREDUtil.isEmpty(request.getParameter("SampID"))) {
					//submit sample
					if (actionType.equals("SubmitSamp") && folder.isAllowedSubmitLocalities()) {
						sampleUtil.submitSample(Integer.parseInt(request.getParameter("SampID")), folder, user);
					}
					//delete sample
					else if (actionType.equals("DeleteSamp") && folder.isAllowedDeleteLocalities()) {
						sampleUtil.deleteSample(Integer.parseInt(request.getParameter("SampID")), folder, user);
					}
				} else if (!FREDUtil.isEmpty(request.getParameter("RecID"))) {
					//submit record
					if (actionType.equals("SubmitRec") && folder.isAllowedSubmitLocalities()) {
						recordUtil.submitRecord(Integer.parseInt(request.getParameter("RecID")), folder, user);
					}
					//delete record
					else if (actionType.equals("DeleteRec") && folder.isAllowedDeleteLocalities()) {
						recordUtil.deleteRecord(Integer.parseInt(request.getParameter("RecID")), folder, user);
					}
				} else if (request.getParameter("FeatIDs") != null) {
					if (actionType.equals("SubmitFeatures")) {
						featureUtil.submitFeatures(request.getParameterValues("FeatIDs"), folder, user);
					} else if (actionType.equals("RevokeFeatures")) {
						featureUtil.revokeFeatures(request.getParameterValues("FeatIDs"), folder, user);
					} else if (actionType.equals("DeleteFeatures")) {
						featureUtil.deleteRemoveFeatures(request.getParameterValues("FeatIDs"), folder, user);
					} else if (actionType.equals("MergeFeatures")) {
						Feature mergeToFeature = featureUtil.getFeature(Integer.parseInt(request.getParameter("MergeToFeatID")));
						featureUtil.mergeFeatures(mergeToFeature, request.getParameterValues("FeatIDs"), folder, user);
					} else if (actionType.equals("AlterType")) {
						featureUtil.alterFeatureTypes(request.getParameterValues("FeatIDs"), request.getParameter("NewFeatType"), folder, user);
					}
				}
			} catch (MandatoryFieldsMissingException e) {
				%><script><!--
				alert("<%=e.getMessage()%>");
				//--></script><%
			} catch (Exception e) {
				System.out.println("*********** FRED folder_detail.jsp error ********** " + new java.util.Date());
				e.printStackTrace();
				errorMessage = "An Error has occured: " + e.getMessage();
			}
		}
		
		try {	
			//print error message (if any) from folder_actions
			if (errorMessage != null) {
				out.println("<p><span class='heading' style='color: #FF0000'>" + errorMessage + "</span></p>");
			}
			%><script><!--
			function showHide(toShow, toHide) {
				document.getElementById(toShow).style.display = 'block';
				document.getElementById(toHide).style.display = 'none';
			}
			//--></script>
			
			<form name="FoldForm" method="post" action="folder_detail.jsp">
			
			<div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-al`ign: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none"><%
			startDETable(pageContext);
			%><table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
			<tr><td class="deHeading">Folder Instructions</td></tr>
			<tr><td style="text-align: left">
			<ul>
			<li>Listed below are the localities you have added to this folder.</li>
			<li>To create a new locality click on the New: Outcrop, Drillhole or V. Section links above.</li>
			<li>Working localities are named with their field number or drillhole name until they are allocated a Fossil Record Number.</li>
			<li>Click on the <i>Plus</i> icon to show/edit drillhole/vertical section samples, and paleontology and adopted age data records</li>
			<ul>
			<li><img src="images/edit.gif" border="0" height="20" width="20" alt="" /> edit the locality</li>
			<li><img src="images/lock.gif" border="0" height="20" width="20" alt="" /> set the confidentiality of the sample/record. <i>Note: localities are always open</i></li>
			<li><img src="images/new_file.gif" border="0" height="20" width="20" alt="" /> to add a file/image to the locality/sample/record</li>
			<li><img src="images/map.gif" border="0" height="20" width="20" alt="" /> view a map of the locality</li>
			<li><img src="images/copy.gif" border="0" height="20" width="20" alt="" /> make a copy of the locality (front of form data only)</li>
			<li><img src="images/delete.gif" border="0" height="20" width="20" alt="" /> delete the locality/sample/record</li>
			<li><img src="images/submit.gif" border="0" height="20" width="20" alt="" /> submit the locality/sample/record for entry to the masterfile. <i>Note: sample and records are automatically approved and visible in FRED (according to the confidentiality rules you have specified), but localities are checked by the masterfile curator first</i></li>
			<li><img src="images/revoke.gif" border="0" height="20" width="20" alt="" /> revoke the locality for entry from the masterfile</li>
			<li><img src="images/new_ado.gif" border="0" height="20" width="20" alt="" /> to create a new adoption record for this locality/sample</li>
			<li><img src="images/new_pal.gif" border="0" height="20" width="20" alt="" /> to create a new paleontological record for this locality/sample</li>
			<li><img src="images/pdf_icon.gif" border="0" height="20" width="20" alt="" /> to print the locality/sample/record</li>
			</ul>
			<li>Multiple localities may be selected by <i>ticking</i> the checkboxes on the left-hand side.  You can then use the tools in the <i>Selected Locality Actions</i> box</li>
			</ul>
			</td></tr>
			<tr><td><a href="http://www.adobe.com/products/acrobat/readstep2.html" target="getAcrobat"><img src="images/get_adobe_reader.gif" border="0" alt="Get Adobe Reader" /></a>&nbsp;&nbsp;Adobe reader is required to print localities</td></tr>
			<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table><%
			endDETable(pageContext);
			%></div>
			
			<p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="17" class="deHeading">Localities</td></tr>
			<tr>
			<th colspan="4">Name&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Created Date&nbsp;&nbsp;</th><th colspan="10">Actions</th></tr>
			<tr><td colspan="17"><img src="images/line.gif" height="3" width="550" /></td></tr><%
			
			//Display the features
			
			String backURL = URLEncoder.encode("folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1");
			String backText = "Back%20To%20Folder";

			for (Feature feature : featureUtil.getFeaturesInFolder(folder)) {
				Audit audit = feature.getAudit();
				String status = audit.getStatus();
				String name = FeatureUtil.getFeatureIdentifyingName(feature);
				String featName = feature.getFeatureName();
				%><tr>
				<td style="text-align: left"><input type="checkbox" name="FeatIDs" value="<%=feature.getFeatureId()%>" /></td>	
				<td id="plusMinus<%=feature.getFeatureId()%>" class="heading"><a href="javascript: getFeatureDetails('<%=feature.getFeatureId()%>');"><img src="images/plus.gif" border="0" /></a></td>
				<td style="text-align: left"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=backURL%>&backText=<%=backText%>"><img src="images/loc.gif" border="0" height="20" width="20" alt="View Locality" /></a></td>
				<td style="text-align: left" class="heading"><%=name%>&nbsp;&nbsp;<%
				if (featName != null && !featName.equals(name)) {
					%><br />(<%=featName%>)&nbsp;&nbsp;<%
				}
				%></td>
				<td style="text-align: left"><%=feature.getFeatureType()%>&nbsp;&nbsp;</td>
				<td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {"text-align: left"})%>><%=status%>&nbsp;&nbsp;<%
				if (status.equals(FREDConstants.REJECTED)) {
					%><br /><div class="smalltext">Curator comments: <%=DBUtils.nvl(audit.getCuratorComments())%></div><%
				}
				%></td>
				<td style="text-align: left"><%=(audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : ""%></td><%
				if (featureUtil.isAllowedEditFeature(user, feature, folder)) {
					%><td style="text-align: left"><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=feature.getFeatureId()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Locality" /></a>&nbsp;</td>
					<td></td>
					<td style="text-align: left"><a href="binary_data_entry.jsp?ID=<%=feature.getFeatureId()%>&RecType=<%=feature.getFeatureType()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/new_file.gif" border="0" height="20" width="20" alt="Add Image/File" /></a>&nbsp;</td><%
				} else {
					%><td></td><td></td><td></td><%
				}
				%><td style="text-align: left"><a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" /></a>&nbsp;</td>
				<td style="text-align: left"><%
				if (folder.isAllowedCreateLocalities()) {
					%><a href="javascript:prmpt=prompt('Please enter the new name', 'Copy of <%=FeatureUtil.getFeatureIdentifyingName(feature)%>');if(prmpt!=null){document.FoldForm.NewFeatName.value=prmpt;document.FoldForm.ActionType.value='CopyFeat';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/copy.gif" border="0" height="20" width="20" alt="Copy Locality" /></a>&nbsp;<%
				}
				%></td><td style="text-align: left"><%
				if (!status.equals(FREDConstants.APPROVED) && featureUtil.isAllowedDeleteFeature(user, feature, folder)) {
					%><a href="javascript:if (confirm('Are you sure you want to delete this locality') == true) {document.FoldForm.ActionType.value='DeleteFeat';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/delete.gif" border="0" height="20" width="20" alt="Delete Locality" /></a>&nbsp;<%
				} else if (status.equals(FREDConstants.APPROVED) && !FREDUtil.isEmpty(feature.getFolders())) {
					%><a href="javascript:if (confirm('Are you sure you want to remove this locality from your folder') == true) {document.FoldForm.ActionType.value='RemoveFeat';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/delete.gif" border="0" height="20" width="20" alt="Remove Locality" /></a>&nbsp;<%
				}
				%></td><td style="text-align: left"><%
				if (featureUtil.isAllowedSubmitFeature(user, feature, folder)) {
					%><a href="javascript:if (confirm('Are you sure you want to submit this locality') == true) {document.FoldForm.ActionType.value='Submit';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/submit.gif" border="0" height="20" width="20" alt="Submit Locality" /></a>&nbsp;<%
				} else if (featureUtil.isAllowedRevokeFeature(user, feature, folder)) {
					%><a href="javascript:if (confirm('Are you sure you want to revoke this locality') == true) {document.FoldForm.ActionType.value='Revoke';document.FoldForm.FeatID.value='<%=feature.getFeatureId()%>';document.FoldForm.submit();}"><img src="images/revoke.gif" border="0" height="20" width="20" alt="Revoke Locality" /></a>&nbsp;<%
				}
				%></td><td></td><td></td>
				<td style="text-align: left">
				<a href="frf/frf.pdf?FeatIDs=<%=feature.getFeatureId()%>&q=<%=Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" border="0" width="20" height="20" alt="Print Locality" /></a>&nbsp;
				</td></tr>
				<tbody id="featureDetails<%=feature.getFeatureId()%>"></tbody>
				<tr><td colspan="17"><img src="images/line.gif" height="3" width="650" /></td></tr><%
			}
			%>
			<tr><td colspan="17" style="text-align: left"><a href="javascript:selectAll()">Select All</a>&nbsp;&nbsp;<a href="javascript:unselectAll()">Unselect All</a></td></tr>
			</table><%
			endDETable(pageContext);
			%></p>
			
			<script type="text/javascript"><!--
			function selectAll() {
				if (document.FoldForm.FeatIDs.length) {
					for (var i=0; i<document.FoldForm.FeatIDs.length; i++) {
						document.FoldForm.FeatIDs[i].checked = true;
					}
				} else {
					document.FoldForm.FeatIDs.checked = true;
				}
			}
			function unselectAll() {
				if (document.FoldForm.FeatIDs.length) {
					for (var i=0; i<document.FoldForm.FeatIDs.length; i++) {
						document.FoldForm.FeatIDs[i].checked = false;
					}
				} else {
					document.FoldForm.FeatIDs.checked = false;
				}
			}

		
			function getFeatureDetails(featureId) {
				ajaxXML("getFeatureDetails.jsp?FoldID=<%=folder.getFolderId()%>&FeatID=" + featureId, updatePage);
			}
			
			function updatePage(xmlDoc) {
				var featureNode = xmlDoc.getElementsByTagName("feature")[0];
				var featureId = featureNode.getAttributeNode("id").nodeValue;
				var featureType = featureNode.getElementsByTagName("feature-type")[0].firstChild.nodeValue;
				var tbody = document.getElementById("featureDetails" + featureId);
				var samplesNode = featureNode.getElementsByTagName("samples")[0];
				for (i = 0; i < samplesNode.getElementsByTagName("sample").length; i++) {
					var sampleNode = xmlDoc.getElementsByTagName("sample")[i];
					if (featureType != '<%=FREDConstants.OUTCROP%>') {
						var sampleId = sampleNode.getAttributeNode("id").nodeValue;
						var statusStyleNode = sampleNode.getElementsByTagName("status-style")[0];
		
						var tr = document.createElement("tr");
						tr.appendChild(document.createElement("td"));
						tr.appendChild(document.createElement("td"));
						var iconTd = document.createElement("td");
						iconTd.appendChild(createIcon("images/drill.gif", "detail.jsp?ID=" + sampleId + "&backURL=<%=backURL%>&backText=<%=backText%>"));
						tr.appendChild(iconTd);
						var nameTd = document.createElement("td");
						nameTd.appendChild(document.createTextNode(sampleNode.getElementsByTagName("sample-name")[0].firstChild.nodeValue));
						tr.appendChild(nameTd);
						var typeTd = document.createElement("td");
						typeTd.appendChild(document.createTextNode("Sample"));
						tr.appendChild(typeTd);
						var statusTd = document.createElement("td");
						statusTd.appendChild(document.createTextNode(sampleNode.getElementsByTagName("status")[0].firstChild.nodeValue));
						if (statusStyleNode.firstChild != null)
							statusTd.style.cssText = statusStyleNode.firstChild.nodeValue;
						tr.appendChild(statusTd);
						var createdTd = document.createElement("td");
						createdTd.appendChild(document.createTextNode(sampleNode.getElementsByTagName("created-date")[0].firstChild.nodeValue));
						tr.appendChild(createdTd);
						if (sampleNode.getElementsByTagName("edit")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/edit.gif", "de.jsp?Type=Sample&FoldID=<%=folder.getFolderId()%>&SampID=" + sampleId, ""));
							tr.appendChild(td);
						} else {
							tr.appendChild(document.createElement("td"));
						}
						if (sampleNode.getElementsByTagName("set-confidentiality")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/lock.gif", "set_confidentiality.jsp?RecType=SMP&FoldID=<%=folder.getFolderId()%>&ID=" + sampleId, ""));
							tr.appendChild(td);
						} else {
							tr.appendChild(document.createElement("td"));
						}
						if (sampleNode.getElementsByTagName("edit-binary")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/new_file.gif", "binary_data_entry.jsp?RecType=SMP&FoldID=<%=folder.getFolderId()%>&ID=" + sampleId, ""));
							tr.appendChild(td);
						} else {
							tr.appendChild(document.createElement("td"));
						}
						tr.appendChild(document.createElement("td"));
						tr.appendChild(document.createElement("td"));
						if (sampleNode.getElementsByTagName("delete")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/delete.gif", "javascript:if (confirm('Are you sure you want to delete this sample') == true) {document.FoldForm.ActionType.value='DeleteSamp';document.FoldForm.SampID.value='" + sampleId + "';document.FoldForm.submit();}", ""));
							tr.appendChild(td);
						} else {
							tr.appendChild(document.createElement("td"));
						}
						if (sampleNode.getElementsByTagName("submit")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/submit.gif", "javascript:document.FoldForm.ActionType.value='SubmitSamp';document.FoldForm.SampID.value='" + sampleId + "';document.FoldForm.submit();", ""));
							tr.appendChild(td);
						} else {
							tr.appendChild(document.createElement("td"));
						}
						if (sampleNode.getElementsByTagName("create-adoption")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/new_ado.gif", "de.jsp?Type=<%=FREDConstants.ADOPTION%>&FoldID=<%=folder.getFolderId()%>&SampID=" + sampleId, ""));
							tr.appendChild(td);
						} else {
							tr.appendChild(document.createElement("td"));
						}
						if (sampleNode.getElementsByTagName("create-paleontology")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/new_pal.gif", "de.jsp?Type=<%=FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&SampID=" + sampleId, ""));
							tr.appendChild(td);
						} else {
							tr.appendChild(document.createElement("td"));
						}
						var pdfTd = document.createElement("td");
						pdfTd.appendChild(createIcon("images/pdf_icon.gif", "frf/frf.pdf?SampIDs=" + sampleId, "_blank"));
						tr.appendChild(pdfTd);
						tbody.appendChild(tr);
					}
					var recordsNode = sampleNode.getElementsByTagName("records")[0];
					for (j = 0; j < recordsNode.getElementsByTagName("record").length; j++) {
						var recordNode = xmlDoc.getElementsByTagName("record")[j];
						var recordId = recordNode.getAttributeNode("id").nodeValue;
						var recordType = recordNode.getElementsByTagName("record-type")[0].firstChild.nodeValue;
						var recStatusStyleNode = recordNode.getElementsByTagName("status-style")[0];
				
						var rectr = document.createElement("tr");
						rectr.appendChild(document.createElement("td"));
						rectr.appendChild(document.createElement("td"));
						var iconTd = document.createElement("td");
						var iconImg = document.createElement("img");
						if (recordType == '<%=FREDConstants.ADOPTION%>') {
							iconImg.setAttribute("src", "images/ado.gif");
						} else {
							iconImg.setAttribute("src", "images/pal.gif");
						}
						iconTd.appendChild(iconImg);
						rectr.appendChild(iconTd);
						var recNameTd = document.createElement("td");
						recNameTd.appendChild(document.createTextNode(recordNode.getElementsByTagName("record-name")[0].firstChild.nodeValue));
						rectr.appendChild(recNameTd);
						var recTypeTd = document.createElement("td");
						recTypeTd.appendChild(document.createTextNode(recordType))
						rectr.appendChild(recTypeTd);
						var recStatusTd = document.createElement("td");
						recStatusTd.appendChild(document.createTextNode(recordNode.getElementsByTagName("status")[0].firstChild.nodeValue));
						if (recStatusStyleNode.firstChild != null)
							recStatusTd.style.cssText = recStatusStyleNode.firstChild.nodeValue;
						rectr.appendChild(recStatusTd);
						var recDateTd = document.createElement("td");
						recDateTd.appendChild(document.createTextNode(recordNode.getElementsByTagName("created-date")[0].firstChild.nodeValue));
						rectr.appendChild(recDateTd);
						if (recordNode.getElementsByTagName("edit")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/edit.gif", "de.jsp?Type=" + recordType + "&FoldID=<%=folder.getFolderId()%>&RecID=" + recordId, ""));
							rectr.appendChild(td);
						} else {
							rectr.appendChild(document.createElement("td"));
						}
						if (recordNode.getElementsByTagName("set-confidentiality")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/lock.gif", "set_confidentiality.jsp?RecType=" + recordType + "&FoldID=<%=folder.getFolderId()%>&ID=" + recordId, ""));
							rectr.appendChild(td);
						} else {
							rectr.appendChild(document.createElement("td"));
						}
						if (recordNode.getElementsByTagName("edit-binary")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/new_file.gif", "binary_data_entry.jsp?RecType=" + recordType + "&FoldID=<%=folder.getFolderId()%>&ID=" + recordId, ""));
							rectr.appendChild(td);
						} else {
							rectr.appendChild(document.createElement("td"));
						}
						rectr.appendChild(document.createElement("td"));
						rectr.appendChild(document.createElement("td"));
						if (recordNode.getElementsByTagName("delete")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/delete.gif", "javascript:if (confirm('Are you sure you want to delete this record') == true) {document.FoldForm.ActionType.value='DeleteRec';document.FoldForm.RecID.value='" + recordId + "';document.FoldForm.submit();}", ""));
							rectr.appendChild(td);
						} else {
							rectr.appendChild(document.createElement("td"));
						}
						if (recordNode.getElementsByTagName("submit")[0].firstChild.nodeValue == 'TRUE') {
							var td = document.createElement("td");
							td.appendChild(createIcon("images/submit.gif", "javascript:document.FoldForm.ActionType.value='SubmitRec';document.FoldForm.RecID.value='" + recordId + "';document.FoldForm.submit();", ""));
							rectr.appendChild(td);
						} else {
							rectr.appendChild(document.createElement("td"));
						}
						rectr.appendChild(document.createElement("td"));
						rectr.appendChild(document.createElement("td"));
						var recPdfTd = document.createElement("td");
						recPdfTd.appendChild(createIcon("images/pdf_icon.gif", "frf/frf.pdf?RecIDs=" + recordId, "_blank"));
						rectr.appendChild(recPdfTd);
						tbody.appendChild(rectr);					
					}
				}

				var lnkCell = document.getElementById("plusMinus" + featureId);
				while (lnkCell.firstChild) {
				//The list is LIVE so it will re-index each call
					lnkCell.removeChild(lnkCell.firstChild);
				}
				var img = document.createElement("img");
				img.setAttribute("src", "images/minus.gif");
				img.setAttribute("border", "0");
				var lnk = document.createElement("a");
				lnk.setAttribute("href", "javascript: removeFeatureDetails('" + featureId + "');");
				lnk.appendChild(img);
				lnkCell.appendChild(lnk);
			}
			
			function createIcon(imageSrc, href, target) {
				var anchor = document.createElement("a");
				anchor.setAttribute("href", href);
				if (target != '')
					anchor.setAttribute("target", target);
				var image = document.createElement("img");
				image.setAttribute("src", imageSrc);
				image.setAttribute("border", "0");
				anchor.appendChild(image);
				return anchor;
			}
			
			function removeFeatureDetails(featureId) {
				var tbody = document.getElementById("featureDetails" + featureId);
				while (tbody.firstChild) {
				//The list is LIVE so it will re-index each call
					tbody.removeChild(tbody.firstChild);
				}							    
				var lnkCell = document.getElementById("plusMinus" + featureId);
				while (lnkCell.firstChild) {
				//The list is LIVE so it will re-index each call
					lnkCell.removeChild(lnkCell.firstChild);
				}
				var img = document.createElement("img");
				img.setAttribute("src", "images/plus.gif");
				img.setAttribute("border", "0");
				var lnk = document.createElement("a");
				lnk.setAttribute("href", "javascript: getFeatureDetails('" + featureId + "');");
				lnk.appendChild(img);
				lnkCell.appendChild(lnk);
			}
		
			//--></script>		

			<p><%
			//Selected Actions box
			startDETable(pageContext);
			%><table border="0" width="550">
			<tr><td colspan="11" class="deHeading">Selected Locality Actions</td></tr>
			<tr>
			<td><a href="javascript:document.FoldForm.ActionType.value='SubmitFeatures';document.FoldForm.submit();"><img src="images/submit.gif" border="0" height="20" width="20" alt="Submit" /></a></td>
			<td class="heading" style="text-align: left"><a href="javascript:document.FoldForm.ActionType.value='SubmitFeatures';document.FoldForm.submit();">Submit</a></td>
			</tr>
			<tr>
			<td><a href="javascript:document.FoldForm.ActionType.value='RevokeFeatures';document.FoldForm.submit();"><img src="images/revoke.gif" border="0" height="20" width="20" alt="Revoke" /></a></td>
			<td class="heading" style="text-align: left"><a href="javascript:document.FoldForm.ActionType.value='RevokeFeatures';document.FoldForm.submit();">Revoke</a></td>
			</tr>
			<tr>
			<td><a href="javascript:document.FoldForm.action='frf/frf.pdf';document.FoldForm.method='get';document.FoldForm.target='_blank';document.FoldForm.submit();"><img src="images/pdf_icon.gif" border="0" height="20" width="20" alt="Print" /></a></td>
			<td class="heading" style="text-align: left"><a href="javascript:document.FoldForm.action='frf/frf.pdf';document.FoldForm.method='get';document.FoldForm.target='_blank';document.FoldForm.submit();">Print</a></td>
			</tr>
			<tr>
			<td><a href="javascript:document.FoldForm.ActionType.value='DeleteFeatures';document.FoldForm.submit();"><img src="images/delete.gif" border="0" height="20" width="20" alt="Delete/Remove" /></a></td>
			<td class="heading" style="text-align: left"><a href="javascript:document.FoldForm.ActionType.value='DeleteFeatures';document.FoldForm.submit();">Delete/Remove</a></td>
			</tr>
			<tr>
			<td><a href="javascript:document.FoldForm.ActionType.value='MergeFeatures';document.FoldForm.submit();"><img src="images/edit.gif" border="0" height="20" width="20" alt="Merge" /></a></td>
			<td class="heading" style="text-align: left"><a href="javascript:document.FoldForm.ActionType.value='MergeFeatures';document.FoldForm.submit();">Merge To:</a>&nbsp;
			<select name="MergeToFeatID"><option value="-">-- Choose --</option><%
			for (Feature mergeToFeature : featureUtil.getFeaturesInFolder(folder)) {
				if (!mergeToFeature.getFeatureType().equals(FREDConstants.OUTCROP)) {
					%><option value="<%=mergeToFeature.getFeatureId()%>"><%=FeatureUtil.getFeatureIdentifyingName(mergeToFeature)%></option><%		
				}
			}
			%></select>
			</td>
			</tr>
			<tr>
			<td><a href="javascript:document.FoldForm.ActionType.value='AlterType';document.FoldForm.submit();"><img src="images/edit.gif" border="0" height="20" width="20" alt="Alter Type" /></a></td>
			<td class="heading" style="text-align: left"><a href="javascript:document.FoldForm.ActionType.value='AlterType';document.FoldForm.submit();">Alter Locality Type To:</a>&nbsp;
			<select name="NewFeatType"><option value="-">-- Choose --</option>
				<option value="Outcrop">Outcrop</option>
				<option value="Drillhole">Drillhole</option>
				<option value="Vertical Section">Vertical Section</option>
			</select>
			</td>
			</tr>		
			</table><%		
			endDETable(pageContext);
			%></p>
			
			<input type="hidden" name="ActionType" value="" />
			<input type="hidden" name="ID" value="<%=folder.getFolder().getFolderId()%>" />
			<input type="hidden" name="FeatID" value="" />
			<input type="hidden" name="SampID" value="">
			<input type="hidden" name="RecID" value="">
			<input type="hidden" name="NewFoldID" value="" />
			<input type="hidden" name="NewFeatName" value="" />
			<input type="hidden" name="q" value="<%=Math.random()%>" />
			</table></p>
			</td></tr></table>
			</form><%
			
		} catch (Exception e) {
			System.out.println("*********** FRED folder_detail.jsp error ********** " + new java.util.Date());
			e.printStackTrace();
			%>A database error has occured loading this page.<%
		}
	} else { //no folder found
		%><p><span class="heading">You do not have sufficient rights to view this folder</span></p><%
	}
	
	drawBottom(out, et);
	
	//Close the session
	folderUtil.closeSession();
%>