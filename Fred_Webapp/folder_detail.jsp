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
	User user =(User) getUser(session);
	UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), user);
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	
	if (folder != null && folder.isAllowedCreateLocalities()) {
		IconnedLink[] iLink = new IconnedLink[((folder.isBacklogFolder()) ? 7 : 6)];
		iLink[0] = new IconnedLink("folder_list.jsp?&q=" + Math.random(), "images/back_arrow.gif", "Back to Folders");
		iLink[1] = new IconnedLink(null, "images/new.gif", "New: ");
		iLink[2] = new IconnedLink("de.jsp?Type=Outcrop&FoldID=" + folder.getFolder().getFolderId(), null, "Outcrop");
		iLink[3] = new IconnedLink("de.jsp?Type=Drillhole&FoldID=" + folder.getFolder().getFolderId(), null, "Drillhole");
		iLink[4] = new IconnedLink("de.jsp?Type=Vertical+Section&FoldID=" + folder.getFolder().getFolderId(), null, "V. Section");
		iLink[5] = new IconnedLink("folder_taxa_list.jsp?ID=" + folder.getFolder().getFolderId() + "&q=" + Math.random() , "images/loc.gif", "Taxa Status");
		if (folder.isBacklogFolder())
			iLink[6] = new IconnedLink("backlog_status.jsp", "images/map.gif", "Backlog Status");
		et.setButtons(iLink);
	} else {
		et.setButtons(new IconnedLink[] {
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
				if (request.getParameter("FeatID") != null && !request.getParameter("FeatID").equals("")) {
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
					//submit working locality
					else if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
						featureUtil.submitFeature(feature, folder, user);
					}
					//Revoke waiting records
					else if (actionType.equals("Revoke") && folder.isAllowedSubmitLocalities()) {
						featureUtil.revokeFeature(feature, folder, user);
					}
				} else if (request.getParameter("FeatIDs") != null) {
					if (actionType.equals("SubmitFeatures")) {
						featureUtil.submitFeatures(request.getParameterValues("FeatIDs"), folder, user);
					} else if (actionType.equals("RevokeFeatures")) {
						featureUtil.revokeFeatures(request.getParameterValues("FeatIDs"), folder, user);
					} else if (actionType.equals("PrintFeatures")) {
						String[] featIDs = request.getParameterValues("FeatIDs");
						StringBuffer queryStr = new StringBuffer();
						for (int i = 0; i < featIDs.length; i++) {
							queryStr.append("FeatIDs=").append(featIDs[i]);
							if (i < featIDs.length - 1)
								queryStr.append("&");
						}
						response.sendRedirect("frf/frf.pdf?" + queryStr.toString());
						return;
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
			<center><p>&nbsp;<p/><div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-al`ign: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none"><%
			startDETable(pageContext);
			%><table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
			<tr><td colspan="3" class="deHeading">Folder Instructions</td></tr><tr><td style="text-align: left">
			<ul>
			<li>Listed below are the localities you have added to this folder.</li>
			<li>To create a new locality click on the New: Outcrop, Drillhole or V. Section links above.</li>
			<li>Working localities are named with their field number or drillhole name until they are allocated a Fossil Record Number.</li>
			<li>Click on the locality to add/edit drillhole/vertical section samples, and paleontology and adopted age data records, or use the actions to work with the locality itself:</li>
			<ul>
			<li><img src="images/edit.gif" border="0" height="20" width="20" alt="" /> edit the locality</li>
			<li><img src="images/new_file.gif" border="0" height="20" width="20" alt="" /> add a file/image to the locality/sample/record</li>
			<li><img src="images/map.gif" border="0" height="20" width="20" alt="" /> view a map of the locality</li>
			<li><img src="images/copy.gif" border="0" height="20" width="20" alt="" /> make a copy of the locality (front of form data only)</li>
			<li><img src="images/delete.gif" border="0" height="20" width="20" alt="" /> delete the locality</li>
			<li><img src="images/submit.gif" border="0" height="20" width="20" alt="" /> submit the locality for entry to the masterfile</li>
			<li><img src="images/revoke.gif" border="0" height="20" width="20" alt="" /> revoke the locality for entry from the masterfile</li>
			</ul>
			<li>Multiple localities may be selected by <i>ticking</i> the checkboxes on the left-hand side.  You can then use the tools in the <i>Selected Locality Actions</i> box</li>
			</ul>
			</td></tr>
			<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table><%
			endDETable(pageContext);
			%></div>
			<p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="12" class="deHeading">Localities</td></tr>
			<tr>
			<th colspan="3">Name&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Created Date&nbsp;&nbsp;</th><th colspan="6">Actions</th></tr>
			<tr><td colspan="12"><img src="images/line.gif" height="3" width="550" /></td></tr>
	
			<form name="FoldForm" method="post" action="folder_detail.jsp"><%
			
			//Display the features
			Feature[] features = featureUtil.getFeaturesInFolder(folder);
			for (int i = 0; i < features.length; i++) {
				Feature feature = features[i];
				Audit audit = feature.getAudit();
				String status = audit.getStatus();
				String name = FeatureUtil.getFeatureIdentifyingName(feature);
				String featName = feature.getFeatureName();
				%><tr>
				<td style="text-align: left"><input type="checkbox" name="FeatIDs" value="<%=feature.getFeatureId()%>" /></td>	
				<td style="text-align: left"><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/loc.gif" border="0" height="20" width="20" alt="View Locality" /></a></td>
				<td style="text-align: left" class="heading"><a href="folder_feature_detail.jsp?FoldID=<%=folder.getFolderId()%>&FeatID=<%=feature.getFeatureId() + "&q=" + Math.random()%>"><%=name%></a>&nbsp;&nbsp;<%
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
				<td style="text-align: left"><%=(audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : ""%></td>
				<td style="text-align: left"><%
				if (featureUtil.isAllowedEditFeature(user, feature, folder)) {
					%><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=feature.getFeatureId()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Locality" /></a>&nbsp;</td>
					<td><a href="binary_data_entry.jsp?ID=<%=feature.getFeatureId()%>&RecType=<%=feature.getFeatureType()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/new_file.gif" border="0" height="20" width="20" alt="Add Image/File" /></a>&nbsp;<%
				} else {
					%></td><td><%
				}
				%></td>
				<td style="text-align: left"><a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("folder_detail.jsp?ID=" + folder.getFolderId() + "&q=" + Math.random(), "ISO-8859-1")%>&backText=Back%20To%20Folder"><img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" /></a>&nbsp;</td>
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
				%></td></tr>
				<tr><td colspan="12"><img src="images/line.gif" height="3" width="550" /></td></tr><%
			}
			%></table><%
			endDETable(pageContext);
			%></p>
			
			
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
			<td><a href="javascript:document.FoldForm.ActionType.value='PrintFeatures';document.FoldForm.target='_blank';document.FoldForm.submit();"><img src="images/pdf_icon.gif" border="0" height="20" width="20" alt="Print" /></a></td>
			<td class="heading" style="text-align: left"><a href="javascript:document.FoldForm.ActionType.value='PrintFeatures';document.FoldForm.target='_blank';document.FoldForm.submit();">Print</a></td>
			</tr>
			<tr>
			<td><a href="javascript:document.FoldForm.ActionType.value='DeleteFeatures';document.FoldForm.submit();"><img src="images/delete.gif" border="0" height="20" width="20" alt="Delete/Remove" /></a></td>
			<td class="heading" style="text-align: left"><a href="javascript:document.FoldForm.ActionType.value='DeleteFeatures';document.FoldForm.submit();">Delete/Remove</a></td>
			</tr>
			<tr>
			<td><a href="javascript:document.FoldForm.ActionType.value='MergeFeatures';document.FoldForm.submit();"><img src="images/edit.gif" border="0" height="20" width="20" alt="Merge" /></a></td>
			<td class="heading" style="text-align: left"><a href="javascript:document.FoldForm.ActionType.value='MergeFeatures';document.FoldForm.submit();">Merge To:</a>&nbsp;
			<select name="MergeToFeatID"><option value="-">-- Choose --</option><%
			Feature[] mergeToFeatures = featureUtil.getFeaturesInFolder(folder);
			for (int i = 0; i < features.length; i++) {
				if (!mergeToFeatures[i].getFeatureType().equals(FREDConstants.OUTCROP)) {
					%><option value="<%=mergeToFeatures[i].getFeatureId()%>"><%=FeatureUtil.getFeatureIdentifyingName(mergeToFeatures[i])%></option><%		
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
			<input type="hidden" name="NewFoldID" value="" />
			<input type="hidden" name="NewFeatName" value="" />
			</table></p>
			</form>
			</td></tr></table><%
			
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