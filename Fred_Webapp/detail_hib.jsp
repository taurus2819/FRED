<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Adoption"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.AuditEdit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Relationship"
%><%@page import="nz.cri.gns.fred.model.PersonRelationship"
%><%@page import="nz.cri.gns.fred.model.SentTo"
%><%@page import="nz.cri.gns.fred.model.SedimentaryFeature"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.db.metadata.*"
%><%@page import="nz.cri.gns.db.site.SiteRecord"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.util.map.Datum"
%><%@page import="nz.cri.gns.util.map.Datum.Coordinate"
%><%@page import="nz.cri.gns.util.map.Datum.LatLong"
%><%@page import="nz.cri.gns.util.map.DatumFactory"
%><%@page import="java.net.URLEncoder"
%><%@page import="java.io.PrintWriter"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%!
	public static void addRepeatingCells(PrintWriter out, String heading, Object[] text, boolean newLines) {
		if (text.length > 0) {
			if (newLines) {
				out.println("<tr><td class=\"heading\">" + heading + "</td><td>" + DBUtils.nvl(text[0]) + "</td></tr>");
				for (int i = 1; i < text.length; i++)
					out.println("<tr><td>&nbsp;</td><td>" + DBUtils.nvl(text[i]) + "</td></tr>");
			} else {
				StringBuffer textLine = new StringBuffer();
				for (int i = 0; i < text.length; i++) {
					textLine.append(text[i]);
					if (i < text.length - 1)
						textLine.append("; ");
				}
				out.println("<tr><td class=\"heading\">" + heading + "</td><td>" + textLine.toString() + "</td></tr>");
			}
		} else {
			out.println("<tr><td class=\"heading\">" + heading + "</td><td>&nbsp;</td></tr>");
		}
	}
%><%
	User user = (User) getUser(session);
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	SampleUtil sampleUtil = new SampleUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);

	boolean authorChk = false, sCountChk = false, sCoordChk = false, commChk = true;

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	//if FeatureID given then get SampleID or transer to drillhole
	if (request.getParameter("FeatID") != null) {
		String featID = request.getParameter("FeatID");
		try {
			Feature feature = featureUtil.getFeature(Integer.parseInt(featID));
			if (feature.getSamples() != null) {
				if (feature.getSamples().size() > 1) {
					response.sendRedirect("drillhole_detail.jsp?ID=" + featID);
				} else {
					response.sendRedirect("detail.jsp?ID=" + ((Sample)feature.getSamples().iterator().next()).getSampleId());
				}
			} else {
				response.sendRedirect("drillhole_detail.jsp?ID=" + featID);
			}
			return;
		} catch (Exception e) {
			response.sendRedirect("drillhole_detail.jsp?ID=" + featID);
			return;
		}
	}

	//get SampleID
	String sampID;
	if (request.getParameter("ID") != null) {
		sampID = request.getParameter("ID");
		session.setAttribute("SampleID", sampID);
	} else {
		sampID = (String) session.getAttribute("SampleID");
	}

	if (sampID != null) {
		Sample sample = sampleUtil.getSample(Integer.parseInt(sampID));
		Feature feature = sample.getFeature();
		boolean isAllowedReadSample = sampleUtil.isAllowedReadSample(user, sample);
		boolean isAllowedReadFeature = featureUtil.isAllowedReadFeature(user, feature);
		
		if (!featureUtil.isAllowedReadFeatureSite(user, feature)) {
			drawTop(out, et, request, response);
			%>Not allowed to read this sample<%
		} else {
			if (request.getParameter("ActionType") != null) { //do something
				String actionType = request.getParameter("ActionType");
				
				if (actionType.equals("Approve")) {
					featureUtil.approveFeature(feature, request.getParameter("MapSheet"), new Integer(request.getParameter("SerialNum")), request.getParameter("RecollNum"), request.getParameter("CurComm"), user);
					response.sendRedirect("admin_folder_detail.jsp?ID=" + feature.getMasterFile().getFolderId());
					return;
				}
				else if (actionType.equals("Reject")) {
					featureUtil.rejectLocality(feature, request.getParameter("CurComm"), user);
					response.sendRedirect("admin_folder_detail.jsp?ID=" + feature.getMasterFile().getFolderId());
					return;
				}
		/*		else if (actionType.equals("AddtoFold") && !request.getParameter("FoldID").equals("-")) {
					FolderUtils.addLocality(sample.getAsString(Sample.FEATURE_ID), request.getParameter("FoldID"), user, state);
					Folder folder = new Folder(Integer.parseInt(request.getParameter("FoldID")), user, state, true);
					out.println("<script language=\"JavaScript\">alert(\"Locality Added to " + folder.getAsString(Folder.NAME) + " folder\");</script>");
				} */
			}

			drawTop(out, et, request, response);

			if (request.getParameter("AuthorChk") != null && request.getParameter("AuthorChk").equals("true")) { authorChk = true; }
			if (request.getParameter("SCountChk") != null && request.getParameter("SCountChk").equals("true")) { sCountChk = true; }
			if (request.getParameter("SCoordChk") != null && request.getParameter("SCoordChk").equals("true")) { sCoordChk = true; }
			if (request.getParameter("CommChk") != null && request.getParameter("CommChk").equals("false")) { commChk = false; }
	
			//List data
			%><table style="margin-left:10px; margin-top:20px; width:180px;" border="0"><%

			Audit audit = feature.getAudit();
			String featType = feature.getFeatureType();
			%><tr><td colspan="2" align="center"><img src="images/loc.gif" height="20" width="20" /></td></tr>
			<tr><td colspan="2" align="center" class="bigheading"><%=sample.getSampleName()%></td></tr>
			<tr><td colspan="2" align="center"><%=featType%></td></tr>
			<tr><td class="smallheading">Masterfile:&nbsp;</td><td class="smalltext"><%=((feature.getMasterFile() != null) ? feature.getMasterFile().getName() : "undefined")%></td></tr><%
			if (!audit.getStatus().equals(FREDConstants.APPROVED)) {
				%><tr><td class="smallheading">Status:&nbsp;</td><td class="smalltext"><%=audit.getStatus()%></td></tr><%
			}
			%><tr><td class="smallheading">Created:&nbsp;</td>
			<td class="smalltext"><%=((audit.getCreatedById() != null) ? FREDUtil.getUserName(audit.getCreatedById().intValue()) + "<br />" : "")%>
				<%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "")%></td></tr>
			<tr><td class="smallheading">Edited:&nbsp;</td><%
			if (audit.getAuditEdits() != null && audit.getAuditEdits().size() > 0) {
				AuditEdit edit = (AuditEdit) audit.getAuditEdits().iterator().next();
			%><td class="smalltext"><%=((edit.getEditedById() != null) ? FREDUtil.getUserName(edit.getEditedById().intValue()) + "<br />" : "")%>
				<%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "")%></td></tr><%
			}
			%><tr><td class="smallheading">Submitted:&nbsp;</td>
			<td class="smalltext"><%=((audit.getSubmittedById() != null) ? FREDUtil.getUserName(audit.getSubmittedById().intValue()) + "<br />" : "")%>
				<%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "")%></td></tr>
			<tr><td class="smallheading">Approved:&nbsp;</td>
			<td class="smalltext"><%=((audit.getApprovedById() != null) ? FREDUtil.getUserName(audit.getApprovedById().intValue()) + "<br />" : "")%>
				<%=((audit.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(audit.getApprovedDate()) : "")%></td></tr><%

			if (isAllowedReadFeature) {

				%><tr><td class="smallheading"><a href="audit_detail.jsp?ID=<%=sample.getSampleId()%>" target="audit">More...</a></td></tr>
				<tr><td>&nbsp;</td></tr>
				
				<tr><td colspan="2"><table border="0"><%
				
				//Generate list of users folders
				if ((new FolderUtil(factory)).getPersonalFolders(user).size() > 0) {
					%><form name="FolderForm" method="post" action="detail_hib.jsp">
					<tr><td colspan="2">
					<select name="FoldID">
					<option value="-">-- Choose --</option><%
					for (Iterator i = (new FolderUtil(factory)).getPersonalFolders(user).iterator(); i.hasNext();) {
						UserFolder folder = (UserFolder) i.next();
						String folderName = folder.getFolderName();
						if (folderName.length() > 17)
							folderName = folderName.substring(0, 17);
						%><option value="<%=folder.getFolderId()%>"><%=folderName%></option><%
					}
					%></select>
					</td></tr>
					<tr><td><a href="#" onClick="FolderForm.submit();"><img src="images/folder.gif" height="20" width="20" border="0" alt="Add to Folder" /></a></td><td><a href="#" onClick="FolderForm.submit();" class="heading">Add to Folder</a></td></tr>
					<input type="hidden" name="ID" value="<%=sampID%>" />
					<input type="hidden" name="ActionType" value="AddtoFold" />
					</form>
					<tr><td>&nbsp;</td></tr><%
				}
				
				
				%><tr><td><a href="frf/frf.pdf?SampIDs=<%=sample.getSampleId()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" /></a>&nbsp;&nbsp;</td><td><a href="frf/frf.pdf?SampIDs=<%=sample.getSampleId()%>" class="heading" target="_blank">Print Front</a></td></tr><%
				
				if (sampleUtil.getPaleontologyRecordCount(sample) > 0) {
					for (Iterator i = sampleUtil.getPaleontologyRecords(sample).iterator(); i.hasNext();) {
						Paleontology palRecord = (Paleontology) i.next();
						if (recordUtil.isAllowedReadRecord(user, palRecord.getRecord())) {
							%><tr><td><a href="print_pal.jsp?ID=<%=palRecord.getRecordId()%>" target="_blank"><img src="images/print.gif" width="20" height="20" border="0" alt="Print" /></a>&nbsp;&nbsp;</td><td><a href="print_pal.jsp?ID=<%=palRecord.getRecordId()%>" class="heading" target="_blank">Print Pal Record<br /><%=RecordUtil.getRecordName(palRecord.getRecord())%></a></td></tr><%
						} 
					}
				}
				
				%></table></td></tr><%
				
				/*
				if (FREDUtils.isAllowedApproveLocality(user, sample.getAsString(Sample.FEATURE_ID), sample.getAsString(Sample.FEATURE_STATUS), state)) {
					FRNumber frNumber = FolderUtils.getNextFRNumber(sample.getAsString(Sample.REG_AREA_CODE), sample.getAsString(Sample.NZMG_SHEET), sample.getAsDouble(Sample.LATITUDE), sample.getAsDouble(Sample.LONGITUDE), state);
					out.println("<tr><td colspan='2'>");
					out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
					out.println("<form name='RevForm' method='post' action='detail.jsp'>");
					out.println("<input type='hidden' name='ID' value='" + sampID + "'>");
					out.println("<input type='hidden' name='ActionType' value=''>");
					out.println("<tr><td colspan='2' class='heading' align='center'>Locality Approval</td></tr>");
					out.println("<tr><td><a href='#' onClick='document.RevForm.ActionType.value=\"Approve\";document.RevForm.submit();'><img src='images/ok.gif' width='20' height='20' border='0' alt='Approve' /></a></td><td class='heading'>Approve</td></tr>");
					out.println("<tr><td><a href='#' onClick='document.RevForm.ActionType.value=\"Reject\";document.RevForm.submit();'><img src='images/cancel.gif' width='20' height='20' border='0' alt='reject' /></a></td><td class='heading'>Reject</td></tr>");
					//if (recoll != null) {
					//	out.println("<tr><td colspan='2'>The submitter has indicated that this record is a recollection of " + recoll + ".  If you agree then amend the FRNumber below as appropriate</td></tr>");
					//}
					out.println("<tr><td colspan='2'><input type='text' name='MapSheet' size='9' value='" + frNumber.getMapSheet() + "' />&nbsp;/f&nbsp;<input type='text' name='SerialNum' size='4' value='" + frNumber.getSerialNumber() + "' />&nbsp;<input type='text' name='RecollNum' size='1' value='' /></td></tr>");
					out.println("<tr><td><img src='images/blank.gif' height='5' width='1' /></td></tr>");
					out.println("<tr><td colspan='2' class='heading'>Comments</td></tr>");
					out.println("<tr><td colspan='2'><textarea name='CurComm' rows='5' cols='25'>" + FREDUtils.noNulls(audit.getAsString(Audit.CURATOR_COMMENTS)) + "</textarea></td></tr>");
					out.println("</form>");
					out.println("</table>");
					out.println("</td></tr>");
				} */
				
				%><tr><td><img src="images/blank.gif" width="1" height="10" /></td></tr>
				<tr><td class="heading" colspan="2" align="center">Taxonomic List Options</td></tr>
				<form name="TaxaForm" method="post" action="detail_hib.jsp">
				<input type="hidden" name="ID" value="<%=sampID%>" />
				<input type="hidden" name="AuthorChk" value="<%=authorChk%>" />
				<input type="hidden" name="SCountChk" value="<%=sCountChk%>" />
				<input type="hidden" name="SCoordChk" value="<%=sCoordChk%>" />
				<input type="hidden" name="CommChk" value="<%=commChk%>" />
				<tr><td colspan="2" class="heading"><%
				if (authorChk) {
					%><a href="#" onClick="document.TaxaForm.AuthorChk.value='false';document.TaxaForm.submit();" title="Hide"><img src="images/ok.gif" width="20" height="20" border="0" /><%
				} else {
					%><a href="#" onClick="document.TaxaForm.AuthorChk.value='true';document.TaxaForm.submit();" title="Show"><img src="images/cancel.gif" width="20" height="20" border="0" /><%
				}
				%></a>&nbsp;&nbsp;Author</td></tr>
				<tr><td colspan="2" class="heading"><%
				if (sCountChk) {
					%><a href="#" onClick="document.TaxaForm.SCountChk.value='false';document.TaxaForm.submit();" title="Hide"><img src="images/ok.gif" width="20" height="20" border="0" /><%
				} else {
					%><a href="#" onClick="document.TaxaForm.SCountChk.value='true';document.TaxaForm.submit();" title="Show"><img src="images/cancel.gif" width="20" height="20" border="0" /><%
				}
				%></a>&nbsp;&nbsp;Specimen Count</td></tr>
				<tr><td colspan="2" class="heading"><%
				if (sCoordChk) {
					%><a href="#" onClick="document.TaxaForm.SCoordChk.value='false';document.TaxaForm.submit();" title="Hide"><img src="images/ok.gif" width="20" height="20" border="0" /><%
				} else {
					%><a href="#" onClick="document.TaxaForm.SCoordChk.value='true';document.TaxaForm.submit();" title="Show"><img src="images/cancel.gif" width="20" height="20" border="0" /><%
				}
				%></a>&nbsp;&nbsp;Specimen Coord</td></tr>
				<tr><td colspan="2" class="heading"><%
				if (commChk) {
					%><a href="#" onClick="document.TaxaForm.CommChk.value='false';document.TaxaForm.submit();" title="Hide"><img src="images/ok.gif" width="20" height="20" border="0" /><%
				} else {
					%><a href="#" onClick="document.TaxaForm.CommChk.value='true';document.TaxaForm.submit();" title="Show"><img src="images/cancel.gif" width="20" height="20" border="0" /><%
				}
				%></a>&nbsp;&nbsp;Comments</td></tr>
				</form><%
			}	
			%></table><%

			drawEndNavigation(out);
			
			%><table style="margin-left:20px; width:550px;" border="0">
			<tr><td><%
	
			//Locality Data
			%><p><table border="0" cellspacing="0" cellpadding="2" width="550">
			<tr><td class="bigheading" colspan="2">Location Information</td></tr>
			<tr><td class="heading">FR Number</td><td class="heading"><%=sample.getFrNumber().getFrNumber()%></td></tr><%
			if (sample.getYardFrNumber() != null) {
				%><tr><td class="heading">Yard FR Number</td><td><%=sample.getYardFrNumber().getFrNumber()%></td></tr><%
			}
			String featTypeLbl, linkStart = "", linkStop = "";
			if (featType.equals(FREDConstants.OUTCROP)) {
				featTypeLbl = "Field Number";
			} else if (featType.equals(FREDConstants.DRILLHOLE)) {
				featTypeLbl = "Drillhole Name";
				linkStart = "<a href=\"drillhole_detail.jsp?ID=" + feature.getFeatureId() + "\">";
				linkStop = "</a>";
			} else {
				featTypeLbl = "Section Name";
				linkStart = "<a href=\"drillhole_detail.jsp?ID=" + feature.getFeatureId() + "\">";
				linkStop = "</a>";
			}
			%><tr><td class="heading"><%=featTypeLbl%></td><td><%=linkStart + DBUtils.nvl(feature.getFeatureName()) + linkStop%></td></tr><%
			if (!featType.equals(FREDConstants.OUTCROP)) {
				%><tr><td class="heading">Sample Depth</td><td><%=DBUtils.nvl(SampleUtil.getDrillHoleDepthDescription(sample))%></td></tr><%
				//check for samples above and below current one
				Sample sampleAbove = SampleUtil.getSampleAbove(sample);
				if (sampleAbove != null && sampleUtil.isAllowedReadSample(user, sampleAbove)) {
					%><tr><td class="heading">Sample Above</td><td><a href="detail_hib.jsp?ID=<%=sampleAbove.getSampleId()%>"><%=SampleUtil.getDrillHoleDepthDescription(sampleAbove)%></a></td></tr><%
				}
				Sample sampleBelow = SampleUtil.getSampleBelow(sample);
				if (sampleBelow != null && sampleUtil.isAllowedReadSample(user, sampleBelow)) {
					%><tr><td class="heading">Sample Below</td><td><a href="detail_hib.jsp?ID=<%=sampleBelow.getSampleId()%>"><%=SampleUtil.getDrillHoleDepthDescription(sampleBelow)%></a></td></tr><%
				}
			}			
			%><tr><td class="heading">Original Grid Reference</td><%
			if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
				Datum datum = FREDUtil.getFREDDatum(feature);
				Coordinate coord = FREDUtil.getFREDCoordinate(feature);
				%><td><%=datum.getHumanStringFor(coord).replaceAll("Geographic ", "")%></td><%
				if (!datum.getName().equals("NZMG")) {
					try {
						Datum nzmgDatum = DatumFactory.createDatum("NZMG");
						Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
						if (nzmgDatum.coordinateAcceptable(nzmgCoord)) {
							%></tr><tr><td class="heading">Converted Grid Reference</td><td><%=nzmgDatum.getHumanStringFor(nzmgCoord)%></td><%
						}
					} catch (Exception e) { }
				}
			}
			%></tr><%
			SiteRecord sr = null;
			if (feature.getSiteId() != null) {
				sr = FREDUtil.getSite(feature);
				LatLong ll = sr.getLatLong();
				%><tr><td class="heading">Converted Dec. Lat/Long</td><td><%=ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)"%></td></tr><%
			}			
			
			%><tr><td class="heading">Map Year</td><td><%=DBUtils.nvl(feature.getMapYear())%></td></tr>
			<tr><td class="heading">Method</td><td><%=((sr != null && !sr.isNull(SiteRecord.H_METHOD_FIELD)) ? FREDUtil.getSiteMethod(sr) : "&nbsp;")%></td></tr>
			<tr><td class="heading">Accuracy</td><td><%=((sr != null && !sr.isNull(SiteRecord.H_ACCURACY_FIELD)) ? "&#177;" + String.valueOf(sr.getAccuracy()) + " m" : "&nbsp;")%></td></tr><%

			if (isAllowedReadFeature) {
				%><tr><td class="heading">Locality</td><td><%=DBUtils.nvl(feature.getLocality())%></td></tr><%
			}
			%><tr><td class="heading">Country</td><td><%=((sr != null && !sr.isNull(SiteRecord.COUNTRY_FIELD)) ? FREDUtil.getSiteCountry(sr) : "&nbsp;")%></td></tr><%
			
			if (isAllowedReadFeature && !featType.equals(FREDConstants.OUTCROP)) {
				%><tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Operating Company" : "Section Collector")%></td><td><%=((feature.getPerson() != null) ? feature.getPerson().getName() : "&nbsp;")%></td></tr>
				<tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Spud Date" : "Sampling Start Date")%></td><td><%=((feature.getStartDate() != null) ? FREDUtil.formatDateForOutput(feature.getStartDate(), feature.getStartDateRounding()) : "&nbsp;")%></td></tr>
				<tr><td class="heading">Completion Date</td><td><%=((feature.getFinishDate() != null) ? FREDUtil.formatDateForOutput(feature.getFinishDate(), feature.getFinishDateRounding()) : "&nbsp;")%></td></tr><%
				if (featType.equals(FREDConstants.DRILLHOLE)) {
					%><tr><td class="heading">Licence Area</td><td><%=DBUtils.nvl(feature.getDrillholeLicenceName())%></td></tr><%
				}
				%><tr><td class="heading">Datum Type</td><td><%=DBUtils.nvl(feature.getDatumType())%></td></tr>
				<tr><td class="heading">Datum Elevation</td><td><%=((feature.getDatumElevation() != null) ? String.valueOf(feature.getDatumElevation()) + " m asl" : "&nbsp;")%></td></tr>
				<tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Kick-off Depth" : "Top Horizon")%></td><td><%=((feature.getStartDepth() != null) ? String.valueOf(feature.getStartDepth()) + " m" : "&nbsp;")%></td></tr>
				<tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Termination Depth" : "Base Horizon")%></td><td><%=((feature.getFinishDepth() != null) ? String.valueOf(feature.getFinishDepth()) + " m" : "&nbsp;")%></td></tr><%
			}
					
				/*

			//Image/Files
			if (sample.isUserAuthenticated() && sample.getFeatureMetadataRecordsCount() > 0) {
				out.println("<tr><td colspan='2' class="heading">Images/Files</td></tr>");
				MetadataRecord[] mr = sample.getFeatureMetadataRecords();
				out.println("<tr><td colspan='2'><table border='0' cellspacing='0' width='600'>");
				int y = 1;
				out.print("<tr>");
				for (int x = 0; x < mr.length; x++) {
					if (y++ == 5) {
						out.println("</tr><tr>");
						y = 2;
					}
					out.print("<td width='150' align='center' class='smalltext'><a href='/online/DigitalDocument?src=" + mr[x].getCode() + "'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "' alt='FRED Digital Document' /><br />" + mr[x].getTitle() + "</a></td>");
				}
				out.println("</td></tr></table></td></tr>");
			} */
			
			%><tr><td>&nbsp;</td></tr><%

			if (isAllowedReadSample) {
				//Sample Property Data
				%><tr><td class="bigheading" colspan="2">Collection Information</td></tr><%
				Object[] collectors = sample.getCollectors().toArray();
				String[] collectorStr = new String[collectors.length];
				for (int i = 0; i < collectors.length; i++)
					collectorStr[i] = ((PersonRelationship) collectors[i]).getDisplayName();
				addRepeatingCells(new PrintWriter(out), "Collectors", collectorStr, false);
				%><tr><td class="heading">Collection Date</td><td><%=((sample.getCollectionDate() != null) ? FREDUtil.formatDateForOutput(sample.getCollectionDate(), sample.getDateRounding()) : "&nbsp;")%></td></tr>
				<tr><td class="heading">Stratigraphic Name</td><td><%=DBUtils.nvl(sample.getStratUnit())%></td></tr>
				<tr><td class="heading">Fossils in Place</td><td><%=DBUtils.nvl(sample.getInPlace())%></td></tr><%
				Object[] sentTos = sample.getSentTos().toArray();
				String[] sentToStr = new String[sentTos.length];
				for (int i = 0; i < sentTos.length; i++)
					sentToStr[i] = SampleUtil.getSentToDescription((SentTo) sentTos[i]);
				addRepeatingCells(new PrintWriter(out), "Sent To", sentToStr, true);
				%><tr><td class="heading">Not Collected</td><td><%=DBUtils.nvl(sample.getNotCollected())%></td></tr>
				<tr><td class="heading">Significance/Comments</td><td><%=DBUtils.nvl(sample.getSignificance())%></td></tr>

				<tr><td>&nbsp;</td></tr>

				<tr><td class="bigheading" colspan="2">Stratigraphy</td></tr>
				<tr><td class="heading">Inferred Stage</td><td><%=((sample.getInferredStage() != null) ? StageUtil.getStageDescription(sample.getInferredStage()) : "&nbsp;")%></td></tr>
				<tr><td class="heading">Known Stage</td><td><%=((sample.getKnownStage() != null) ? StageUtil.getStageDescription(sample.getKnownStage()) : "&nbsp;")%></td></tr><%
				Object[] relationships = sampleUtil.getRelationships(sample, "Sample", "nearby").toArray();
				String[] relationshipStr = new String[relationships.length];
				for (int i = 0; i < relationships.length; i++)
					relationshipStr[i] = SampleUtil.getRelationshipDescriptionWithLink((Relationship) relationships[i], "detail.jsp?FeatID=", null);
				addRepeatingCells(new PrintWriter(out), "Samples Nearby", relationshipStr, false);			
				relationships = sampleUtil.getRelationships(sample, "Sample", new String[] {"above", "below"}).toArray();
				relationshipStr = new String[relationships.length];
				for (int i = 0; i < relationships.length; i++)
					relationshipStr[i] = SampleUtil.getRelationshipDescriptionWithLink((Relationship) relationships[i], "detail.jsp?FeatID=", null);
				addRepeatingCells(new PrintWriter(out), "Sample Relationships", relationshipStr, false);			
				relationships = sampleUtil.getRelationships(sample, "Stratigraphic", new String[] {"above top", "above base", "below top", "below base"}).toArray();
				relationshipStr = new String[relationships.length];
				for (int i = 0; i < relationships.length; i++)
					relationshipStr[i] = SampleUtil.getRelationshipDescription((Relationship) relationships[i]);
				addRepeatingCells(new PrintWriter(out), "Strat. Relationships", relationshipStr, true);	
				%><tr><td class="heading">Column/Map</td><td><%=DBUtils.nvl(sample.getColumnMap())%></td></tr>
				<tr><td class="heading">Dip</td><td><%=DBUtils.nvl(sample.getDip())%></td></tr>
				<tr><td class="heading">Dip Direction</td><td><%=DBUtils.nvl(sample.getDipDirection())%></td></tr>
				<tr><td class="heading">Strike</td><td><%=DBUtils.nvl(sample.getStrike())%></td></tr>
				<tr><td class="heading">Facing</td><td><%=DBUtils.nvl(sample.getFacing())%></td></tr>

				<tr><td>&nbsp;</td></tr>

				<tr><td class="bigheading" colspan="2">Sedimentary Features</td></tr>
								
								
				<tr><td class="heading">Grain Size</td><td><%=SampleUtil.getGrainSizeDescription(sample)%></td></tr>
				<tr><td class="heading">Bedding Thickness</td><td><%=((sample.getBedThickness() != null) ? sample.getBedThickness().getName() : "&nbsp;")%></td></tr>
				<tr><td class="heading">Bedding Features</td><td><%=SampleUtil.getBeddingDescription(sample)%></td></tr>
				<tr><td class="heading">Weathering</td><td><%=((sample.getWeathering() != null) ? sample.getWeathering().getName() : "&nbsp;")%></td></tr>
				<tr><td class="heading">Hardness</td><td><%=((sample.getHardness() != null) ? sample.getHardness().getName() : "&nbsp;")%></td></tr>
				<tr><td class="heading">Carbonate</td><td><%=((sample.getCarbonate() != null) ? sample.getCarbonate().getName() : "&nbsp;")%></td></tr>
				<tr><td class="heading">Colour</td><td><%=SampleUtil.getColourDescription(sample)%></td></tr><%
				Object[] sedFeatures = sample.getSedimentaryFeatures().toArray();
				String[] sedFeaturesStr = new String[sedFeatures.length];
				for (int i = 0; i < sedFeatures.length; i++)
					sedFeaturesStr[i] = SampleUtil.getSedFeatureDescription((SedimentaryFeature) sedFeatures[i]);
				addRepeatingCells(new PrintWriter(out), "Additional Features", sedFeaturesStr, false);
				%><tr><td class="heading">Inferred Environment</td><td><%=DBUtils.nvl(sample.getDepositionEnv())%></td></tr>
				<tr><td class="heading">Nature of Rock Unit</td><td><%=DBUtils.nvl(sample.getRockNature())%></td></tr>
				<tr><td class="heading">Correspondence</td><td><%=DBUtils.nvl(sample.getCorrespondence())%></td></tr><%
				/*
				//Image/Files
				if (sample.getSampleMetadataRecordsCount() > 0) {
					out.println("<tr><td colspan='2' class="heading">Images/Files</td></tr>");
					MetadataRecord[] mr = sample.getSampleMetadataRecords();
					out.println("<tr><td colspan='2'><table border='0' cellspacing='0' width='600'>");
					int y = 1;
					out.print("<tr>");
					for (int x = 0; x < mr.length; x++) {
						if (y++ == 5) {
							out.println("</tr><tr>");
							y = 2;
						}
						out.print("<td width='150' align='center' class='smalltext'><a href='/online/DigitalDocument?src=" + mr[x].getCode() + "'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "' alt='FRED Digital Document' /><br />" + mr[x].getTitle() + "</a></td>");
					}
					out.println("</td></tr></table></td></tr>");
				}*/

				%><tr><td>&nbsp;</td></tr><%
				
				//Adoption
				if (sampleUtil.getAdoptionRecordCount(sample) > 0) {
					for (Iterator i = sampleUtil.getAdoptionRecords(sample).iterator(); i.hasNext();) {
						Adoption adoRecord = (Adoption) i.next();
						if (recordUtil.isAllowedReadRecord(user, adoRecord.getRecord())) {
							%><tr><td colspan="2" class="bigheading">Adoption Data</td></tr><%
							Object[] adoptors = adoRecord.getAdopters().toArray();
							String[] adoptorsStr = new String[adoptors.length];
							for (int j = 0; j < adoptors.length; j++)
								adoptorsStr[j] = ((PersonRelationship) adoptors[j]).getDisplayName();
							addRepeatingCells(new PrintWriter(out), "Adoptors", adoptorsStr, false);
							%><tr><td class="heading">Adoption Date</td><td><%=((adoRecord.getAdoptionDate() != null) ? FREDUtil.formatDateForOutput(adoRecord.getAdoptionDate(), adoRecord.getDateRounding()) : "&nbsp;")%></td></tr>
							<tr><td class="heading">Adopted Stage</td><td><%=((adoRecord.getStage() != null) ? StageUtil.getStageDescription(adoRecord.getStage()) : "&nbsp;")%></td></tr>
							<tr><td class="heading">Comments</td><td><%=DBUtils.nvl(adoRecord.getComments())%></td></tr><%
					/*		//Image/Files
							if (ado.getMetadataRecordsCount() > 0) {
								out.println("<tr><td colspan='2' class="heading">Images/Files</td></tr>");
								MetadataRecord[] mr = ado.getMetadataRecords();
								out.println("<tr><td colspan='2'><table border='0' cellspacing='0' width='600'>");
								int y = 1;
								out.print("<tr>");
								for (int x = 0; x < mr.length; x++) {
									if (y++ == 5) {
										out.println("</tr><tr>");
										y = 2;
									}
									out.print("<td width='150' align='center' class='smalltext'><a href='/online/DigitalDocument?src=" + mr[x].getCode() + "'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "' alt='FRED Digital Document' /><br />" + mr[x].getTitle() + "</a></td>");
								}
								out.println("</td></tr></table></td></tr>");
							} */
							%><tr><td>&nbsp;</td></tr><%
						}
					}
				}
	
				//Paleontology
				if (sampleUtil.getPaleontologyRecordCount(sample) > 0) {
					for (Iterator i = sampleUtil.getPaleontologyRecords(sample).iterator(); i.hasNext();) {
						Paleontology palRecord = (Paleontology) i.next();
						if (recordUtil.isAllowedReadRecord(user, palRecord.getRecord())) {
							%><tr><td colspan="2" class="bigheading">Paleontology Data</td></tr><%									//identifiers (repeating)
							Object[] identifiers = palRecord.getIdentifiers().toArray();
							String[] identifiersStr = new String[identifiers.length];
							for (int j = 0; j < identifiers.length; j++)
								identifiersStr[j] = ((PersonRelationship) identifiers[j]).getDisplayName();
							addRepeatingCells(new PrintWriter(out), "Identifiers", identifiersStr, false);
							%><tr><td class="heading">Identification Date</td><td><%=((palRecord.getIdentificationDate() != null) ? FREDUtil.formatDateForOutput(palRecord.getIdentificationDate(), palRecord.getDateRounding()) : "&nbsp;")%></td></tr>
							<tr><td class="heading">Stage</td><td><%=((palRecord.getStage() != null) ? StageUtil.getStageDescription(palRecord.getStage()) : "&nbsp;")%></td></tr>
							<tr><td class="heading">Stage Comments</td><td><%=DBUtils.nvl(palRecord.getStageComments())%></td></tr>
							<tr><td class="heading">Lab</td><td><%=((palRecord.getLabSection() != null) ? RecordUtil.getLabDescription(palRecord.getLabSection()) : "&nbsp;")%></td></tr>
							<tr><td class="heading">Lab Number</td><td><%=DBUtils.nvl(palRecord.getLabNumber())%></td></tr>
							<tr><td class="heading">Collection Comments</td><td><%=DBUtils.nvl(palRecord.getCollectionComments())%></td></tr><%
			
							//taxa (Pal list)
							if (recordUtil.isAllowedReadPalList(user, palRecord) && palRecord.getListEntries() != null) {
								%><tr><td colspan="2"><table border="0" cellspacing="0" cellpadding="2"><%
								for (Iterator k = recordUtil.getTaxonomicGroups(palRecord).iterator(); k.hasNext(); ) {
									TaxonomicGroup taxaGroup = (TaxonomicGroup) k.next();
									%><tr><td colspan="4" class="heading"><%=taxaGroup.getName()%></td></tr><%
									if (recordUtil.getListEntries(palRecord, taxaGroup).size() > 0) {
										%><tr class="heading"><td>Taxonomic Name&nbsp;&nbsp;</td><%
										if (authorChk) {
											%><td>Author&nbsp;&nbsp;</td><%
										}
										if (sCountChk) {
											%><td>Spec Count&nbsp;&nbsp;</td><%
										}
										if (sCoordChk) {
											%><td>Spec Coord&nbsp;&nbsp;</td><%
										}
										if (commChk) {
											%><td>Comments&nbsp;&nbsp;</td><%
										}
										%></tr><%
										for (Iterator l = recordUtil.getListEntries(palRecord, taxaGroup).iterator(); l.hasNext(); ) {
											PaleontologyListEntry taxa = (PaleontologyListEntry) l.next();
											%><tr><td><i><%=taxa.getTaxonomicName()%></i>&nbsp;&nbsp;</td><%
											if (authorChk) {
												%><td><%=DBUtils.nvl(taxa.getTaxon().getAuthor())%>&nbsp;&nbsp;</td><%
											}
											if (sCountChk) {
												%><td><%=DBUtils.nvl(taxa.getSpecimenCount())%>&nbsp;&nbsp;</td><%
											}
											if (sCoordChk) {
												%><td><%=DBUtils.nvl(taxa.getSpecimenCoords())%>&nbsp;&nbsp;</td><%
											}
											if (commChk) {
												%><td><%=DBUtils.nvl(taxa.getComments())%>&nbsp;&nbsp;</td><%
											}
											%></tr><%
										}
									} else {
										%><tr><td colspan="4">No fossils listed</td></tr><%
									}
									%><tr><td>&nbsp;</td></tr><%
								}
								%></td></tr></table></td></tr><%
							}
							
							/*
							//Image/Files
							if (pal.getMetadataRecordsCount() > 0) {
								out.println("<tr><td colspan='2' class="heading">Images/Files</td></tr>");
								MetadataRecord[] mr = pal.getMetadataRecords();
								out.println("<tr><td colspan='2'><table border='0' cellspacing='0' width='600'>");
								int y = 1;
								out.print("<tr>");
								for (int x = 0; x < mr.length; x++) {
									if (y++ == 5) {
										out.println("</tr><tr>");
										y = 2;
									}
									out.print("<td width='150' align='center' class='smalltext'><a href='/online/DigitalDocument?src=" + mr[x].getCode() + "'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "' alt='FRED Digital Document' /><br />" + mr[x].getTitle() + "</a></td>");
								}
								out.println("</td></tr></table></td></tr>");
							}  */
							%><tr><td>&nbsp;</td></tr><%
						}
					}
				}			
				

			}
	
			if (user ==  null)
				out.println("<tr><td colspan='2'>More data may be available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/detail.jsp") + "' class='boldlink'>logged</a> in users</td></tr>");
			out.println("</table></td></tr></table>");
		}
	} 
	else { //no sampleID
		drawTop(out, et, request, response);
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>No SampleID received.  Click <a href='index.jsp' class=\"heading\">here</a> to return to the FRED home page.</td></tr>");
		out.println("</table>");
	}
	
	drawBottom(out, et); 
%>