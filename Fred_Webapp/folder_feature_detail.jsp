<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.*"
%><%@page import="nz.cri.gns.db.*"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="java.net.*"
%><%@page import="nz.cri.gns.intranet.*"
%><%@page import="java.sql.*"
%><%@page import="java.text.*"
%><%@page import="java.util.*"
%><%@page import="nz.cri.gns.auth.*"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.dao.StorageAccessException"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			FeatureUtil featureUtil = new FeatureUtil(HibernateUtil.get().getDAOFactory());
			return "FRED :: " + featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID"))).getFeatureName();
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
	User user = (User)getUser(session);
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID")));
	UserFolder folder = folderUtil.getFolder(Integer.parseInt(request.getParameter("FoldID")), user);

	if (!featureUtil.getFeaturesInFolder(folder).contains(feature) || !folder.isAllowedReadLocalities()) {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
		return;
	}
	
	session.setAttribute("dataEntryRedirect", "folder_feature_detail.jsp?FoldID=" + folder.getFolderID() + "&FeatID=" + featID);

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
				featureUtil.deleteFeature(feature, folder, user);
			}
			//TODO Delete sample
			else if (actionType.equals("DeleteSamp") && folder.isAllowedDeleteLocalities()) {
				FolderUtils.deleteSample(request.getParameter("SampID"), user, state);
			}
			//TODO Delete record
			else if (actionType.equals("DeleteRec") && folder.isAllowedDeleteLocalities()) {
				FolderUtils.deleteRecord(request.getParameter("RecID"), user, state);
			}
			// submit working locality
			else if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
				featureUtil.submitFeature(feature, folder, user);
			}
			//TODO Submit sample
			else if (actionType.equals("SubmitSamp") && folder.isAllowedSubmitLocalities()) {
				FolderUtils.submitSample(request.getParameter("SampID"), user, state);
			}
			//TODO submit working record
			else if (actionType.equals("SubmitRec") && folder.isAllowedSubmitLocalities()) {
				FolderUtils.submitRecord(request.getParameter("RecID"), user, state);
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
		out.println("<p><span class='heading' style='color: #FF0000'>" + errorMessage + "</span></p>");
	}
	%><script><!--
function showHide(toShow, toHide) {
	document.getElementById(toShow).style.display = 'block';
	document.getElementById(toHide).style.display = 'none';
}
//--></script>
<center><p>&nbsp;<p/><div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none">
<%
		startDETable(out);
		%>
<table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
<tr><td colspan="3" class="deHeading">Locality Instructions</td></tr><tr><td style="text-align: left">
<ul>
<li>Listed below are the working records for this locality - adoption (blue) and paleontology (green).  
<li>Drillhole and Vertical Section localities will also have individual samples listed.
<li>Paleontology records marked with a red asterix contain taxonomic entries which have not been approved.  These records can not be submitted.</p>
<li>Click on the icons to work with the locality's records:
<ul>
<li><img src="images/edit.gif" border="0"> to edit the locality
<li><img src="images/submit.gif" border="0"> to submit the locality for entry to the masterfile
<li><img src="images/new_ado.gif" border="0"> to create a new adoption for this locality
<li><img src="images/new_pal.gif" border="0"> to create a new paleontological reocrd for this locality
</ul>
</ul>
</td></tr>
<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table>
<%
		endDETable(out);
		%></div>
<p>
<%
		startDETable(out);
		%><table border="0" width="550"><tr><td colspan="9" class="deHeading"><%=feature.getFeatureName()%></td></tr>
<tr>
<th colspan="2">Name&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Created Date&nbsp;&nbsp;</th><th>Created Date&nbsp;&nbsp;</th><th colspan="5">Options</th></tr>
<tr><td colspan="9"><img src="images/line.gif" height="3" width="550" /></td></tr>
<form name="FoldForm" method="put" action="folder_feature_detail.jsp">
<%-- Feature --%>
<tr><td><a href="detail.jsp?FeatID=<%=feature.getFeatureId()%>"><img src="images/loc.gif" border="0" height="20" width="20" alt="View Locality" /></a>&nbsp;</td>
<td class="heading"><%=feature.getFeatureName()%>&nbsp;&nbsp;
<%
/*TODO still haven't figure out what the significance of the feature name vs sample name is...
		if (featName != null && !sampName.equals(featName)) 
			out.print("<br />(" + featName +")&nbsp;&nbsp;");*/
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
		boolean editable = audit.getStatus().equals(FREDConstants.WORKING) || audit.getStatus().equals(REJECTED);
		if (editable && folder.isAllowedEditLocalities()) {
			%><a href="data_entry.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=Feature.getFeatureId()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Locality" /></a><img src="images/blank.gif" height="20" width="2" /><%
		}
	
		%></td><td><%
		//TODO why is this commented out?
	//			if ((locStatus.equals(Audit.STATUS_WORKING) || locStatus.equals(Audit.STATUS_REJECTED)) && folder.isAllowedDeleteLocalities())
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
				Sample sample = featureUtils.getOutcropSample(feature);
				%><a href="data_entry.jsp?Type=ADO&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_ado.gif" border="0" height="20" width="20" alt="Add Adoption Record" /></a><img src="images/blank.gif" height="20" width="2" />
</td><td>
<a href="data_entry.jsp?Type=PAL&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_pal.gif" border="0" height="20" width="20" alt="Add Paleontology Record" /></a><%
			} else {
				%><a href="new_sample.jsp?FeatID=<%=feature.getFeatureId()%>&FoldID=<%=folder.getFolderId()%>"><img src="images/drill.gif" border="0" height="20" width="20" alt="New Sample" /></a><img src="images/blank.gif" height="20" width="2" /><%
			}
		}
		%></td></tr>
<tr><td colspan='11'><img src='images/line.gif' height='3' width='550' /></td></tr>
		
<%
		for (Iterator i = feature.getSamples().iterator(); i.hasNext(); ) {
			Sample sample = (Sample)i.next();
			audit = sample.getAudit();
			if (audit.getStatus().equals(FREDConstants.APPROVED) || audit.getFolder() != null && audit.getFolder().equals(folder.getFolder())) {
				if (!feature.getFeatureType().equals(FREDConstants.OUTCROP) && !SampleUtil.hasDepthInformation(sample)) {
					%><tr><td><a href="detail.jsp?ID=<%=sample.getSampleId()%>"><img src="images/drill.gif" height="20" width="20" border="0" alt="View Sample Details" /></a>&nbsp;</td><td><%=SampleUtil.getDrillHoleDepthDescription(sample)%>&nbsp;&nbsp;</td>
<td style="color: #FF0000"><%
					if (!audit.getStatus().equals(FREDConstants.APPROVED)) {
						%><%=audit.getStatus()%>&nbsp;&nbsp;</td><td><%
						if (audit.getCreatedDate() != null)
							%><%=DateFormat.getDateInstance(DateFormat.LONG).format(audit.getCreatedDate())%>&nbsp;&nbsp;<%
					} else {
						%></td><td><%
					}
					%></td><td><%
					editable = audit.getStatus().equals(FREDConstants.WORKING) || audit.getStatus().equals(REJECTED);
					if (editable && folder.isAllowedEditLocalities()) {
						%><a href="data_entry.jsp?Type=Sample&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/edit.gif" border="0" height="20" width="20" alt="Edit Sample Details" /></a><img src="images/blank.gif" height="20" width="2" /><%
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
						%><a href="data_entry.jsp?Type=ADO&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_ado.gif" border="0" height="20" width="20" alt="Add Adoption Record" /></a><img src="images/blank.gif" height="20" width="2" /><%
					}
					%></td><td><%
					if (folder.isAllowedCreateLocalities()) {
						%><a href="data_entry.jsp?Type=PAL&FoldID=<%=folder.getFolderId()%>&SampID=<%=sample.getSampleId()%>"><img src="images/new_pal.gif" border="0" height="20" width="20"  /></a><%
					}
					%></td></tr><%
				}

				//Records
				for (Iterator k = sample.getRecords().iterator(); k.hasNext(); ) {
					Record record = (Record)k.next();

					boolean isAdoption = record.getAdoption() != null;
					boolean isPaleontology = record.getPaleontology() != null;
					//TODO I've swapped this because it seems to be backwards ie true = all good???
					boolean badTaxaFlag = (isPaleontology) ? !RecordUtil.isTaxaApproved(record) : true;

					audit = record.getAudit();
					if (audit.getFolder() != null && audit.getFolder.equals(folder.getFolder())) {
						%><tr><td><img src="images/child.gif" width="20" height="20" /><img src="images/<%=(isAdoption) ? "ado" : "pal"%>.gif" width="20" height="20" /></td><td class="smalltext"><%
						if (badTaxaFlag) {
							%><span class="heading" style="color: #FF0000">*</span>&nbsp;&nbsp;<%
						}
						
						//TODO up to here
						
						out.print(FREDUtils.noNulls(record.toString()) + "&nbsp;&nbsp;</td><td class='smalltext' style='color: #FF0000'>");
						if (record.getAsString(Record.STATUS).equals(Audit.STATUS_WORKING)) {
							out.print("working&nbsp;&nbsp;</td><td class='smalltext'>");
							if (record.get(Record.CREATED_DATE) != null)
								out.print(DateFormat.getDateInstance(DateFormat.LONG).format(record.getAsDate(Record.CREATED_DATE)) + "&nbsp;&nbsp;");
							out.print("</td>");
						} else {
							out.print("</td><td></td>");
						}
						out.print("<td>");
						//Record Options
						if (record.getAsString(Record.STATUS).equals(Audit.STATUS_WORKING) && folder.isAllowedEditLocalities())
							out.println("<a href='data_entry.jsp?Type=" + recType + "&FoldID=" + folder.getFolderID() + "&RecID=" + recID + "'><img src='images/edit.gif' border='0' height='20' width='20' alt='Edit Record' /></a><img src='images/blank.gif' height='20' width='2' />");
						out.println("</td><td>");
						if (record.getAsString(Record.STATUS).equals(Audit.STATUS_WORKING) && folder.isAllowedDeleteLocalities())
							out.println("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this record\") == true) {document.FoldForm.ActionType.value=\"DeleteRec\";document.FoldForm.RecID.value=\"" + recID + "\";document.FoldForm.submit();}'><img src='images/delete.gif' border='0' height='20' width='20' alt='Delete Record' /></a><img src='images/blank.gif' height='20' width='2' />");
						out.println("</td><td>");
						if (record.getAsString(Record.STATUS).equals(Audit.STATUS_WORKING) && folder.isAllowedSubmitLocalities() && !badTaxaFlag)
							out.println("<a href='#' onClick='document.FoldForm.ActionType.value=\"SubmitRec\";document.FoldForm.RecID.value=\"" + recID + "\";document.FoldForm.submit();'><img src='images/submit.gif' border='0' height='20' width='20' alt='Submit Record' /></a><img src='images/blank.gif' height='20' width='2' />");
						out.println("</td></tr>");
					}
				}
				out.println("<tr><td colspan='9'><img src='images/line.gif' height='3' width='550' /></td></tr>");
			}
		}
				%>
<input type="hidden" name="ActionType" value="">
<input type="hidden" name="FoldID" value="<%=folder.getFolderID()%>">
<input type="hidden" name="FeatID" value="<%=featID%>">
<input type="hidden" name="SampID" value="">
<input type="hidden" name="RecID" value="">
<input type="hidden" name="NewFeatName" value="">
	
</table></p>
	
</form>
</td></tr></table>
<%
			}
			else { //no folder found
				drawEndNavigation(out);
				out.println("<p><span class='bigheading'>Access Denied</span><br />");
				out.println("You don't have rights to edit this locality</p>");
			}
	/*	} catch (Exception e) {
			drawEndNavigation(out);
			out.println("<p><span class='bigheading'>Access Denied</span><br />");
			out.println("You don't have rights to edit this locality</p>");
		} */
	}
	else {
		drawTop(out, et, request, response);
		drawEndNavigation(out);
	}

	drawBottom(out, et);
%>