<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.IconnedLink"
%><%@page import="java.text.DateFormat"
%><%@page import="java.util.Iterator"
%><%@page import="java.util.Vector"
%><%@page import="java.util.Collections"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.dao.StorageAccessException"
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
%><%!
	public String getName(HttpServletRequest request) {
		try {
			FeatureUtil featureUtil = new FeatureUtil(HibernateUtil.get().getDAOFactory());
			return "FRED :: " + FeatureUtil.getFeatureName(featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID"))));
		} catch (StorageAccessException e) {
			return "FRED";
		}
	}
	
	protected IconnedLink[] getButtons(HttpServletRequest request) {
		return new IconnedLink[] {
			new IconnedLink("folder_detail.jsp?ID=" + request.getParameter("FoldID"), "images/back_arrow.gif", "Back to folder contents")
		};
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

	Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID")));
	UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);

	if (!featureUtil.folderContainsFeature(folder, feature) || !folder.isAllowedReadLocalities()) {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
		return;
	}
	
	session.setAttribute("dataEntryRedirect", "folder_feature_detail.jsp?FoldID=" + folder.getFolderId() + "&FeatID=" + feature.getFeatureId());

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
			errorMessage = "An Error has occured: " + e.getMessage();
		}
	}


	drawTop(out, et, request, response);
	//print error message (if any) from folder_actions
	if (errorMessage != null) {
		out.println("<center><p><span class='heading' style='color: #FF0000'>" + errorMessage + "</span></p></center>");
	}
	%><script><!--
function showHide(toShow, toHide) {
	document.getElementById(toShow).style.display = 'block';
	document.getElementById(toHide).style.display = 'none';
}
//--></script>
<center><p>&nbsp;<p/><div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none">
<%
		startDETable(pageContext);
		%>
<table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
<tr><td colspan="3" class="deHeading">Locality Instructions</td></tr><tr><td style="text-align: left">
<ul>
<li>Listed below are the working records for this locality - adoption (blue) and paleontology (green).  
<li>Drillhole and Vertical Section localities will also have individual samples listed.
<li>Paleontology records marked with a red asterix contain taxonomic entries which have not been approved.  These records can not be submitted.
<li>Click on the icons to work with the locality's records:
<ul>
<li><img src="images/edit.gif" border="0"> to edit the locality
<li><img src="images/submit.gif" border="0"> to submit the locality for entry to the masterfile
<li><img src="images/drill.gif" border="0"> to create a sample this locality
<li><img src="images/new_ado.gif" border="0"> to create a new adoption for this locality
<li><img src="images/new_pal.gif" border="0"> to create a new paleontological reocrd for this locality
</ul>
</ul>
</td></tr>
<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table>
<%
		endDETable(pageContext);
		%></div>
<p>
<%
		startDETable(pageContext);
		%><form name="FoldForm" method="put" action="folder_feature_detail.jsp">
<table border="0" width="550"><tr><td colspan="9" class="deHeading"><%=FeatureUtil.getFeatureName(feature)%></td></tr>
<tr>
<th colspan="2">Name&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Created Date&nbsp;&nbsp;</th><th colspan="5">Options</th></tr>
<tr><td colspan="9"><img src="images/line.gif" height="3" width="550" /></td></tr>
<%-- Feature --%>
<tr><td><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>"><img src="images/loc.gif" border="0" height="20" width="20" alt="View Locality" /></a>&nbsp;</td>
<td class="heading"><%

		//The name displayed here is either 1) The FR number if it has one or
		//									2) The field number if it's an outcrop or		}
		//									3) The drillhole name if it's a drillhole or	} = feature.feature_name
		//									4) The section name if it's a vert. sect.		}
	
		FrNumber frNum = null;
		if (feature.getSamples().size() > 0)
			frNum = ((Sample)feature.getSamples().iterator().next()).getFrNumber();
		
		if (frNum != null) {
			%><%=frNum.getFrNumber()%><%
		} else {
			%><%=feature.getFeatureName()%><%
		}
		%>&nbsp;&nbsp;<%

		Audit audit = feature.getAudit();
		%></td><td style="color: #FF0000"><%
		if (!audit.getStatus().equals(FREDConstants.APPROVED)) {
			%><%=audit.getStatus()%>&nbsp;&nbsp;</td><td><%
			if (audit.getCreatedDate() != null) {
				%><%=DateFormat.getDateInstance(DateFormat.LONG).format(audit.getCreatedDate())%>&nbsp;&nbsp;<%
			}
			%></td><%
		} else {
			%></td><td></td><%
		}
		%><td><%
		boolean editable = audit.getStatus().equals(FREDConstants.WORKING) || audit.getStatus().equals(FREDConstants.REJECTED);
		if (editable && folder.isAllowedEditLocalities()) {
			%><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=feature.getFeatureId()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
		}
	
		%></td><td><%
		//TODO why is this commented out?
	//			if ((locStatus.equals(FREDConstants.WORKING) || locStatus.equals(FREDConstants.REJECTED)) && folder.isAllowedDeleteLocalities())
	//				out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this locality\") == true) {document.FoldForm.ActionType.value=\"DeleteFeat\";document.FoldForm.submit();}'><img src='images/delete.gif' border='0' height='20' width='20' alt='Delete Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
		%></td><td><%
		if (editable && folder.isAllowedSubmitLocalities()) {
			%><a href="javascript:if (confirm('Are you sure you want to submit this locality') == true) {document.FoldForm.ActionType.value='Submit';document.FoldForm.submit();}"><img src="images/submit.gif" border="0" height="20" width="20" alt="Submit Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
		}
		if (audit.getStatus().equals(FREDConstants.WAITING) && folder.isAllowedSubmitLocalities()) {
			%><a href="javascript:if (confirm('Are you sure you want to revoke this locality') == true) {document.FoldForm.ActionType.value='Revoke';document.FoldForm.submit();}"><img src="images/revoke.gif" border="0" height="20" width="20" alt="Revoke Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
		}
		%></td><td><%
		
		if (folder.isAllowedCreateLocalities()) {
			if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
				Sample sample = featureUtil.getOutcropSample(feature);
				%><a href="de.jsp?Type=<%=FREDConstants.ADOPTION%>&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_ado.gif" border="0" height="20" width="20" alt="Add Adoption Record" /></a><img src="images/blank.gif" height="20" width="2" />
</td><td>
<a href="de.jsp?Type=<%=FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_pal.gif" border="0" height="20" width="20" alt="Add Paleontology Record" /></a><%
			} else {
				%><a href="new_sample.jsp?FeatID=<%=feature.getFeatureId()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/drill.gif" border="0" height="20" width="20" alt="New Sample" /></a><img src="images/blank.gif" height="20" width="2" /><%
			}
		}
		%></td></tr>
<tr><td colspan='11'><img src='images/line.gif' height='3' width='550' /></td></tr>
		
<%		
		DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);
		for (Iterator i = featureUtil.getSortedSamples(feature).iterator(); i.hasNext(); ) {
			Sample sample = (Sample)i.next();
			audit = sample.getAudit();
			if (audit.getStatus().equals(FREDConstants.APPROVED) || audit.getFolder() != null && audit.getFolder().equals(folder.getFolder())) {
				//Commented out the not-showing if no depth fields cos I don't see why...
				if (!feature.getFeatureType().equals(FREDConstants.OUTCROP)/* && SampleUtil.hasDepthInformation(sample)*/) {
					%><tr><td><a href="detail.jsp?ID=<%=sample.getSampleId()%>"><img src="images/drill.gif" height="20" width="20" border="0" alt="View Sample Details" /></a>&nbsp;</td><td><%=SampleUtil.getDrillHoleDepthDescription(sample)%>&nbsp;&nbsp;</td>
<td style="color: #FF0000"><%
					if (!audit.getStatus().equals(FREDConstants.APPROVED)) {
						%><%=audit.getStatus()%>&nbsp;&nbsp;</td><td><%
						if (audit.getCreatedDate() != null)
							%><%=format.format(audit.getCreatedDate())%>&nbsp;&nbsp;<%
					} else {
						%></td><td><%
					}
					%></td><td><%
					editable = audit.getStatus().equals(FREDConstants.WORKING) || audit.getStatus().equals(FREDConstants.REJECTED);
					if (editable && folder.isAllowedEditLocalities()) {
						%><a href="de.jsp?Type=Sample&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Sample Details" /></a><img src="images/blank.gif" height="20" width="2" /><%
					}
					%></td><td><%
					if (editable && folder.isAllowedDeleteLocalities()) {
						%><a href="javascript:if (confirm('Are you sure you want to delete this sample') == true) {document.FoldForm.ActionType.value='DeleteSamp';document.FoldForm.SampID.value='<%=sample.getSampleId()%>';document.FoldForm.submit();}" title="Delete Sample"><img src="images/delete.gif" border="0" height="20" width="20"></a><img src="images/blank.gif" height="20" width="2" /><%
					}
					%></td><td><%
					if (audit.getStatus().equals(FREDConstants.WORKING) && folder.isAllowedSubmitLocalities()) {
						%><a href="javascript:document.FoldForm.ActionType.value='SubmitSamp';document.FoldForm.SampID.value='<%=sample.getSampleId()%>';document.FoldForm.submit();"><img src="images/submit.gif" border="0" height="20" width="20" alt="Submit Sample" /></a><img src="images/blank.gif" height="20" width="2" /><%
					}
					%></td><td><%
					if (folder.isAllowedCreateLocalities()) {
						%><a href="de.jsp?Type=<%=FREDConstants.ADOPTION%>&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_ado.gif" border="0" height="20" width="20" alt="Add Adoption Record" /></a><img src="images/blank.gif" height="20" width="2" /><%
					}
					%></td><td><%
					if (folder.isAllowedCreateLocalities()) {
						%><a href="de.jsp?Type=<%=FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_pal.gif" border="0" height="20" width="20"  /></a><%
					}
					%></td></tr><%
				}

				//Records
				if (sample.getRecords() != null) {
					Vector records = new Vector(sample.getRecords());
					Collections.sort(records, new ByCreationDateComparator());
					for (Iterator k = records.iterator(); k.hasNext(); ) {
						Record record = (Record)k.next();
	
						boolean isAdoption = record.getAdoption() != null;
						boolean isPaleontology = record.getPaleontology() != null;
						boolean badTaxaFlag = (isPaleontology) ? !RecordUtil.isTaxaApproved(record) : false;
	
						audit = record.getAudit();
						if (audit.getFolder() != null && audit.getFolder().equals(folder.getFolder())) {
							%><tr><td><img src="images/child.gif" width="20" height="20" /><img src="images/<%=(isAdoption) ? "ado" : "pal"%>.gif" width="20" height="20" /></td><td class="smalltext"><%
							if (badTaxaFlag) {
								%><span class="heading" style="color: #FF0000">*</span>&nbsp;&nbsp;<%
							}
							
							%><%=RecordUtil.getRecordName(record)%>&nbsp;&nbsp;</td><td class="smalltext" style="color: #FF0000"><%
							
							if (audit.getStatus().equals(FREDConstants.WORKING)) {
								%>working&nbsp;&nbsp;</td><td class="smalltext"><%
								if (audit.getCreatedDate() != null) {
									%><%=DateFormat.getDateInstance(DateFormat.LONG).format(audit.getCreatedDate())%>&nbsp;&nbsp;<%
								}
								%></td><%
							} else {
								%></td><td></td><%
							}
							%><td><%
							//Record Options
							editable = audit.getStatus().equals(FREDConstants.WORKING);
							if (editable && folder.isAllowedEditLocalities()) {
								%><a href="de.jsp?Type=<%=(isAdoption) ? FREDConstants.ADOPTION : FREDConstants.PALEONTOLOGICAL%>&FoldID=<%=folder.getFolderId()%>&RecID=<%=record.getRecordId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Record" /></a><img src="images/blank.gif" height="20" width="2" /><%
							}
							%></td><td><%
							if (editable && folder.isAllowedDeleteLocalities()) {
								%><a href="javascript:if (confirm('Are you sure you want to delete this record') == true) {document.FoldForm.ActionType.value='DeleteRec';document.FoldForm.RecID.value='<%=record.getRecordId()%>';document.FoldForm.submit();}"><img src="images/delete.gif" border="0" height="20" width="20" alt="Delete Record" /></a><img src="images/blank.gif" height="20" width="2" /><%
							}
							%></td><td><%
							if (editable && folder.isAllowedSubmitLocalities() && !badTaxaFlag) {
								%><a href="javascript:document.FoldForm.ActionType.value='SubmitRec';document.FoldForm.RecID.value='<%=record.getRecordId()%>';document.FoldForm.submit();"><img src="images/submit.gif" border="0" height="20" width="20" alt="Submit Record" /></a><img src="images/blank.gif" height="20" width="2" /><%
							}
							%></td></tr><%
						}
					}
				}
				%><tr><td colspan='9'><img src='images/line.gif' height='3' width='550' /></td></tr><%
			}
		}
		%>
<input type="hidden" name="ActionType" value="">
<input type="hidden" name="FoldID" value="<%=folder.getFolderId()%>">
<input type="hidden" name="FeatID" value="<%=feature.getFeatureId()%>">
<input type="hidden" name="SampID" value="">
<input type="hidden" name="RecID" value="">
<input type="hidden" name="NewFeatName" value="">
	
</table>
<%
	endDETable(pageContext);
	%></p>
</form>
</td></tr></table>
<%
	drawBottom(out, et);
} catch (Exception e) {
	e.printStackTrace();
	e.printStackTrace(new java.io.PrintWriter(out));
} finally {
	factory.closeSession();
}
%>
