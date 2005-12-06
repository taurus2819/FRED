<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.*"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.AuditEdit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Folder"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.db.*"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="nz.cri.gns.db.metadata.*"
%><%@page import="nz.cri.gns.db.site.*"
%><%@page import="nz.cri.gns.util.map.*"
%><%@page import="java.net.*"
%><%@page import="java.util.*"
%><%@page import="nz.cri.gns.auth.*"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%
	User user = (User)getUser(session);
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	SampleUtil sampleUtil = new SampleUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);

	boolean authorChk = false, sCountChk = false, sCoordChk = false, commChk = true;

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	//if FeatureID given then get SampleID or transer to drillhole
	if (request.getParameter("FeatID") != null) {
		String featID = request.getParameter("FeatID");
		try {
			Feature feature = featureUtil.getFeature(Integer.parseInt(featID));
			if (feature.getSamples() != null) {
				if (feature.getSamples().size() > 1) {
					response.sendRedirect("drillhole_detail.jsp?ID=" + featID);
				} else {
					response.sendRedirect("detail.jsp?ID=" + feature.getSamples().iterator().next().getSampleId());
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
			<td class="smalltext"><%=((audit.getCreatedById() != null) ? FREDUtil.getUserName(audit.getCreatedById()) + "<br />" : "")%>
				<%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "")%></td></tr>
			<tr><td class="smallheading">Edited:&nbsp;</td><%
			if (audit.getAuditEdits() != null) {
				AuditEdit edit = audit.getAuditEdits().iterator().next();
			%><td class="smalltext"><%=((edit.getEditedById() != null) ? FREDUtil.getUserName(edit.getEditedById()) + "<br />" : "")%>
				<%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "")%></td></tr><%
			}
			%><tr><td class="smallheading">Submitted:&nbsp;</td>
			<td class="smalltext"><%=((audit.getSubmittedById() != null) ? FREDUtil.getUserName(audit.getSubmittedById()) + "<br />" : "")%>
				<%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "")%></td></tr>
			<tr><td class="smallheading">Approved:&nbsp;</td>
			<td class="smalltext"><%=((audit.getApprovedById() != null) ? FREDUtil.getUserName(audit.getApprovedById()) + "<br />" : "")%>
				<%=((audit.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(audit.getApprovedDate()) : "")%></td></tr><%

			if (isAllowedReadFeature) {

				%><tr><td class="smallheading"><a href="audit_detail.jsp?ID=<%=sample.getSampleId()%>" target="audit">More...</a></td></tr>
				<tr><td>&nbsp;</td></tr>
				
				<tr><td colspan="2"><table border="0"><%
				
				/*
				//Generate list of users folders
				FolderList folderList = new FolderList(user, state);
				if (folderList.getPersonalFolderCount() > 0) {
					out.println("<form name=\"FolderForm\" method=\"post\" action=\"detail.jsp\">");
					out.println("<tr><td colspan=\"2\"><select name=\"FoldID\"><option value=\"-\">-- Choose --</option>");
					for (Iterator i = folderList.getPersonalFolders().iterator(); i.hasNext(); ) {
						KeyValueObject kv = (KeyValueObject) i.next();
						out.println("<option value=\"" + kv.getKey() + "\">" + ((kv.getValue().length() <= 17) ? kv.getValue() : kv.getValue().substring(0, 17)) + "</option>");
					}
					out.println("</select></td></tr>");
					out.println("<tr><td><a href=\"#\" onClick=\"FolderForm.submit();\"><img src=\"images/folder.gif\" height=\"20\" width=\"20\" border=\"0\" alt=\"Add to Folder\" /></a></td><td><a href=\"#\" onClick=\"FolderForm.submit();\" class=\"heading\">Add to Folder</a></td></tr>");
					out.println("<input type=\"hidden\" name=\"ID\" value=\"" + sampID + "\" />");
					out.println("<input type=\"hidden\" name=\"ActionType\" value=\"AddtoFold\" />");
					out.println("</form>");
					out.println("<tr><td>&nbsp;</td></tr>");
				}
				*/
				
				%><tr><td><a href="frf/frf.pdf?SampIDs=<%=sample.getSampleId()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" /></a>&nbsp;&nbsp;</td><td><a href="frf/frf.pdf?SampIDs=<%=sample.getSampleId()%>" class="heading" target="_blank">Print Front</a></td></tr><%
				/*
				try {
					if (sample.getPaleontologyRecordCount() > 0) {
						for (Iterator i = sample.getAsVector(Sample.RECORDS).iterator(); i.hasNext(); ) {
							KeyValueObject rec = (KeyValueObject)i.next();
							if (rec.getValue().equals(Record.PALEONTOLOGY_RECORD)) {
								PaleontologyRecord pal = (PaleontologyRecord) PaleontologyRecord.getData(Integer.parseInt(rec.getKey()), user, state, false);
								out.println("<tr><td><a href='print_pal.jsp?ID=" + pal.getRecordID() + "' target='print'><img src='images/print.gif' width='20' height='20' border='0' alt='Print' /></a>&nbsp;&nbsp;</td><td><a href='print_pal.jsp?ID=" + pal.getRecordID() + "' class='heading' target='print'>Print Pal Record</br >" + pal.getRecordName() + "</a></td></tr>");
							} 
						}
					}
				} catch (Exception e) {}
				*/
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
				}
				else {
					out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
					out.println("<tr><td class='heading' colspan='2' align='center'>Taxonomic List Options</td></tr>");
					out.println("<form name='TaxaForm' method='post' action='detail.jsp'>");
					out.println("<input type='hidden' name='ID' value='" + sampID + "'>");
					out.println("<input type='hidden' name='AuthorChk' value='" + authorChk + "'>");
					out.println("<input type='hidden' name='SCountChk' value='" + sCountChk + "'>");
					out.println("<input type='hidden' name='SCoordChk' value='" + sCoordChk + "'>");
					out.println("<input type='hidden' name='CommChk' value='" + commChk + "'>");
					out.print("<tr><td colspan='2' class='heading'>");
					if (authorChk) {
						out.print("<a href='#' onClick='document.TaxaForm.AuthorChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
					} else {
						out.print("<a href='#' onClick='document.TaxaForm.AuthorChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
					}
					out.println("</a>&nbsp;&nbsp;Author</td></tr>");
					out.print("<tr><td colspan='2' class='heading'>");
					if (sCountChk) {
						out.print("<a href='#' onClick='document.TaxaForm.SCountChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
					} else {
						out.print("<a href='#' onClick='document.TaxaForm.SCountChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
					}
					out.println("</a>&nbsp;&nbsp;Specimen Count</td></tr>");
					out.print("<tr><td colspan='2' class='heading'>");
					if (sCoordChk) {
						out.print("<a href='#' onClick='document.TaxaForm.SCoordChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
					} else {
						out.print("<a href='#' onClick='document.TaxaForm.SCoordChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
					}
					out.println("</a>&nbsp;&nbsp;Specimen Coord</td></tr>");
					out.print("<tr><td colspan='2' class='heading'>");
					if (commChk) {
						out.print("<a href='#' onClick='document.TaxaForm.CommChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
					} else {
						out.print("<a href='#' onClick='document.TaxaForm.CommChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
					}
					out.println("</a>&nbsp;&nbsp;Comments</td></tr>");
					out.println("</form>");
				}  */
			}
				
			%></table><%

			drawEndNavigation(out);
			
			%><table style="margin-left:20px; width:550px;" border="0">
			<tr><td><%
			
			//Locality Data
			%><p><table border="0" cellspacing="0" cellpadding="2" width="550">
			<tr><td class="heading">FR Number</td><td><%=sample.getFrNumber().getFrNumber()%></td></tr>
			<tr><td class="heading">Yard FR Number</td><td><%=sample.getYardFrNumber().getFrNumber()%></td></tr><%
			String featTypeLbl;
			if (featType.equals(FREDConstants.OUTCROP)) {
				featTypeLbl = "Field Number";
			} else if (featType.equals(FREDConstants.DRILLHOLE)) {
				featTypeLbl = "<a href=\"drillhole_detail.jsp?ID=" + feature.getFeatureId() + "\">Drillhole Name</a>";
			} else {
				featTypeLbl = "<a href=\"drillhole_detail.jsp?ID=" + feature.getFeatureId() + "\">Section Name</a>";
			}
			%><tr><td class="heading"><%=featTypeLbl%></td><td><%=feature.getFeatureName()%></td></tr><%
			if (!featType.equals(FREDConstants.OUTCROP)) {
				%><tr><td class="heading">Sample Depth</td><td><%=sampleUtil.getDrillHoleDepthDescription(sample)%></td></tr><%
				//check for samples above and below current one
				Sample sampleAbove = SampleUtil.getSampleAbove(sample);
				if (sampleAbove != null && sampleUtil.isAllowedReadSample(sampleAbove)) {
					%><tr><td class="heading">Sample Above</td><td><a href="detail.jsp?ID=<%=sampleAbove.getSampleId()%>"><%=sampleUtil.getDrillHoleDepthDescription(sampleAbove)%></a></td></tr><%
				}
				Sample sampleBelow = SampleUtil.getSampleBelow(sample);
				if (sampleBelow != null && sampleUtil.isAllowedReadSample(sampleBelow)) {
					%><tr><td class="heading">Sample Below</td><td><a href="detail.jsp?ID=<%=sampleBelow.getSampleId()%>"><%=sampleUtil.getDrillHoleDepthDescription(sampleBelow)%></a></td></tr><%
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
						%></tr><tr><td class="heading">Converted Grid Reference</td><td><%=nzmgDatum.getHumanStringFor(nzmgCoord)%></td><%
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
			
			%><tr><td class="heading">Map Year</td><td><%=feature.getMapYear()%></td></tr>
			<tr><td class="heading">Method</td><td><%=((sr != null) ? FREDUtil.getSiteMethod(sr) : "&nbsp;")%></td></tr>
			<tr><td class="heading">Accuracy</td><td>&#177<%=((sr != null && !sr.isNull(SiteRecord.H_ACCURACY_FIELD)) ? String.valueOf(sr.getAccuracy()) + " m" : "&nbsp;")%></td></tr><%

			if (isAllowedReadFeature) {
				%><tr><td class="heading">Locality</td><td><%=sample.getLocality()%></td></tr><%
			}
			%><tr><td class="heading">Country</td><td><%=((sr != null) ? FREDUtil.getSiteCountry(sr) : "&nbsp;")%></td></tr><%
			
			if (isAllowedReadFeature && !featType.equals(FREDConstants.OUTCROP)) {
				%><tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Operating Company" : "Section Collector")%></td><td><%=((feature.getPerson() != null) ? feature.getPerson().getName() : "&nbsp;")%></td></tr>
				<tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Spud Date" : "Sampling Start Date")%></td><td><%=((feature.getStartDate() != null) ? FREDUtil.formatDateForOutput(feature.getStartDate(), feature.getStartDateRounding()) : "&nbsp;")%></td></tr>
				<tr><td class="heading">Completion Date</td><td><%=((feature.getFinishDate() != null) ? FREDUtil.formatDateForOutput(feature.getFinishDate(), feature.getFinishDateRounding()) : "&nbsp;")%></td></tr><%
				if (featType.equals(FREDConstants.DRILLHOLE)) {
					%><tr><td class="heading">Licence Area</td><td><%=feature.getDrillholeLicenceName()%></td></tr><%
				}
				%><tr><td class="heading">Datum Type</td><td><%=feature.getDatumType()%></td></tr>
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
			}
			out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");	

			if (sample.isUserAuthenticated()) {
				//Sample Property Data
				//collectors (repeating)
				if (sample.get(Sample.COLLECTOR) != null) {
					out.print("<tr><td class="heading">Collectors</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.COLLECTOR).iterator(); i2.hasNext(); ) {
						KeyValueObject coll = (KeyValueObject)i2.next();
						out.print(coll.getValue() + "<br />");
					}
					out.print("</td></tr>");
				}
				if (sample.get(Sample.COLLECTION_DATE) != null) { out.print("<tr><td class="heading">Collection Date</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.COLLECTION_DATE), sample.getAsString(Sample.COLLECTION_DATE_ROUNDING)) + "</td></tr>"); }
				if (sample.get(Sample.STRAT_UNIT) != null) { out.println("<tr><td class="heading">Strat Name</td><td>" + sample.getAsString(Sample.STRAT_UNIT) + "</td></tr>"); }
				if (sample.get(Sample.IN_PLACE) != null) { out.println("<tr><td class="heading">In Place</td><td>" + sample.getAsString(Sample.IN_PLACE) + "</td></tr>"); }
				//sent to (repeating)
				if (sample.get(Sample.SENT_TO) != null) {
					out.print("<tr><td class="heading">Sent To</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.SENT_TO).iterator(); i2.hasNext(); ) {
						SentTo sentTo = (SentTo)i2.next();
						out.print(sentTo.getSentTo() + "<br />");
					}
				out.print("</td></tr>");
				}
				if (sample.get(Sample.NOT_COLLECTED) != null) { out.println("<tr><td class="heading">Not Collected</td><td>" + sample.getAsString(Sample.NOT_COLLECTED) + "</td></tr>"); }
				if (sample.get(Sample.SIGNIFICANCE) != null) { out.println("<tr><td class="heading">Significance</td><td>" + sample.getAsString(Sample.SIGNIFICANCE) + "</td></tr>"); }
				if (sample.get(Sample.INFERRED_STAGE) != null) { out.println("<tr><td class="heading">Inferred Stage</td><td>" + sample.getAsString(Sample.INFERRED_STAGE) + "</td></tr>"); }
				if (sample.get(Sample.KNOWN_STAGE) != null) { out.println("<tr><td class="heading">Known Stage</td><td>" + sample.getAsString(Sample.KNOWN_STAGE) + "</td></tr>"); }
				//Nearby samples (repeating)
				if (sample.get(Sample.RELATIONSHIP_NEARBY) != null) {
					out.print("<tr><td class="heading">Samples Nearby</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_NEARBY).iterator(); i2.hasNext(); ) {
						Relationship nearRel = (Relationship)i2.next();
						out.print(nearRel.getDistanceRelation() + " <a href='detail.jsp?FeatID=" + nearRel.getRelatedFeatureID() + "'>" + nearRel.getRelatedSampleName() +"</a><br />");
					}
				out.print("</td></tr>");
				}
				//Sample relationships (repeating)
				if (sample.get(Sample.RELATIONSHIP_SAMPLE) != null) {
					out.print("<tr><td class="heading">Sample Relationships</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_SAMPLE).iterator(); i2.hasNext(); ) {
						Relationship sampRel = (Relationship)i2.next();
						out.print(sampRel.getDistanceRelation() + " <a href='detail.jsp?FeatID=" + sampRel.getRelatedFeatureID() + "'>" + sampRel.getRelatedSampleName() + "</a><br />");
					}
				out.print("</td></tr>");
				}
				//Strat relationships (repeating)
				if (sample.get(Sample.RELATIONSHIP_STRAT) != null) {
					out.print("<tr><td class="heading">Stratigraphic Relationships</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_STRAT).iterator(); i2.hasNext(); ) {
						Relationship stratRel = (Relationship)i2.next();
						out.print(stratRel.getRelationship() + "<br />");
					}
				out.print("</td></tr>");
				}
				if (sample.get(Sample.COLUMN_MAP) != null) { out.println("<tr><td class="heading">Column/Map</td><td>" + sample.getAsString(Sample.COLUMN_MAP) + "</td></tr>"); }
				if (sample.get(Sample.DIP) != null) { out.println("<tr><td class="heading">Dip</td><td>" + sample.getAsString(Sample.DIP) + "</td></tr>"); }
				if (sample.get(Sample.DIP_DIRECTION) != null) { out.println("<tr><td class="heading">Dip Direction</td><td>" + sample.getAsString(Sample.DIP_DIRECTION) + "</td></tr>"); }
				if (sample.get(Sample.STRIKE) != null) { out.println("<tr><td class="heading">Strike</td><td>" + sample.getAsString(Sample.STRIKE) + "</td></tr>"); }
				if (sample.get(Sample.FACING) != null) { out.println("<tr><td class="heading">Facing</td><td>" + sample.getAsString(Sample.FACING) + "</td></tr>"); }
				if (sample.get(Sample.GRAINSIZE) != null) { out.println("<tr><td class="heading">Grain Size</td><td>" + sample.getAsString(Sample.GRAINSIZE) + "</td></tr>"); }
				if (sample.get(Sample.COMPARATOR_USED) != null) { out.println("<tr><td class="heading">Comparator Used</td><td>" + sample.getAsString(Sample.COMPARATOR_USED) + "</td></tr>"); }
				if (sample.get(Sample.BED_THICKNESS) != null) { out.println("<tr><td class="heading">Bed Thickness</td><td>" + sample.getAsString(Sample.BED_THICKNESS) + "</td></tr>"); }
				if (sample.get(Sample.BEDDING) != null) { out.println("<tr><td class="heading">Bedding</td><td>" + sample.getAsString(Sample.BEDDING) + "</td></tr>"); }
				if (sample.get(Sample.WEATHERING) != null) { out.println("<tr><td class="heading">Weathering</td><td>" + sample.getAsString(Sample.WEATHERING) + "</td></tr>"); }
				if (sample.get(Sample.HARDNESS) != null) { out.println("<tr><td class="heading">Hardness</td><td>" + sample.getAsString(Sample.HARDNESS) + "</td></tr>"); }
				if (sample.get(Sample.CARBONATE) != null) { out.println("<tr><td class="heading">Carbonate</td><td>" + sample.getAsString(Sample.CARBONATE) + "</td></tr>"); }
				if (sample.get(Sample.COLOUR) != null) { out.println("<tr><td class="heading">Colour</td><td>" + sample.getAsString(Sample.COLOUR) + "</td></tr>"); }
				//sed features (repeating)
				if (sample.get(Sample.SED_FEATURE) != null) {
					out.print("<tr><td class="heading">Additional Features</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.SED_FEATURE).iterator(); i2.hasNext(); ) {
						SedFeature sf = (SedFeature)i2.next();
						out.print(sf.getSedFeature() + "<br />");
					}
				out.print("</td></tr>");
				}
				if (sample.get(Sample.DEPOSITION_ENV) != null) { out.println("<tr><td class="heading">Inferred Environment</td><td>" + sample.getAsString(Sample.DEPOSITION_ENV) + "</td></tr>"); }
				if (sample.get(Sample.ROCK_NATURE) != null) { out.println("<tr><td class="heading">Nature of Rock Unit</td><td>" + sample.getAsString(Sample.ROCK_NATURE) + "</td></tr>"); }
				if (sample.get(Sample.CORRESPONDENCE) != null) { out.println("<tr><td class="heading">Correspondence</td><td>" + sample.getAsString(Sample.CORRESPONDENCE) + "</td></tr>"); }
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
				}
				out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");

				if (sample.get(Sample.RECORDS) != null) {
					//Adoption
					for (Iterator i = sample.getAsVector(Sample.RECORDS).iterator(); i.hasNext(); ) {
						KeyValueObject rec = (KeyValueObject)i.next();
						if (rec.getValue().equals(Record.ADOPTION_RECORD)) {
							try {
								AdoptionRecord ado = (AdoptionRecord) AdoptionRecord.getData(Integer.parseInt(rec.getKey()), user, state);
								out.println("<tr><td colspan='2' class='bigheading'>Adoption Data</td></tr>");
								//adoptors (repeating)
								if (ado.get(AdoptionRecord.ADOPTOR) != null) {
									out.print("<tr><td class="heading">Adoptors</td><td>");
									for (Iterator i2 = ado.getAsVector(AdoptionRecord.ADOPTOR).iterator(); i2.hasNext(); ) {
										KeyValueObject coll = (KeyValueObject)i2.next();
										out.print(coll.getValue() + "<br />");
									}
									out.print("</td></tr>");
								}
								if (ado.get(AdoptionRecord.ADOPTION_DATE) != null) { out.print("<tr><td class="heading">Adoption Date</td><td>" + FREDUtils.formatDateForOutput(ado.getAsDate(AdoptionRecord.ADOPTION_DATE), ado.getAsString(AdoptionRecord.ADOPTION_DATE_ROUNDING)) + "</td></tr>"); }
								if (ado.get(AdoptionRecord.ADOPTED_STAGE) != null) { out.println("<tr><td class="heading">Adopted Stage</td><td>" + ado.getAsString(AdoptionRecord.ADOPTED_STAGE) + "</td></tr>"); }
								if (ado.get(AdoptionRecord.COMMENTS) != null) { out.println("<tr><td class="heading">Comments</td><td>" + ado.getAsString(AdoptionRecord.COMMENTS) + "</td></tr>"); }
								//Image/Files
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
								}
								out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");
							} catch (Exception e) {
							}
						}
					}
	
					//Paleontology
					for (Iterator i = sample.getAsVector(Sample.RECORDS).iterator(); i.hasNext(); ) {
						KeyValueObject rec = (KeyValueObject)i.next();
						if (rec.getValue().equals(Record.PALEONTOLOGY_RECORD)) {
							try {
								PaleontologyRecord pal = (PaleontologyRecord) PaleontologyRecord.getData(Integer.parseInt(rec.getKey()), user, state, false);
								//if (!"working".equals(pal.getAsString(Record.STATUS))) {
									out.println("<tr><td colspan='2' class='bigheading'>Paleontology Data</td></tr>");
									//identifiers (repeating)
									if (pal.get(PaleontologyRecord.IDENTIFIER) != null) {
										out.print("<tr><td class="heading">Identifiers</td><td>");
										for (Iterator i2 = pal.getAsVector(PaleontologyRecord.IDENTIFIER).iterator(); i2.hasNext(); ) {
											KeyValueObject coll = (KeyValueObject)i2.next();
											out.print(coll.getValue() + "<br />");
										}
										out.print("</td></tr>");
									}
									if (pal.get(PaleontologyRecord.IDENTIFICATION_DATE) != null) { out.print("<tr><td class="heading">Identification Date</td><td>" + FREDUtils.formatDateForOutput(pal.getAsDate(PaleontologyRecord.IDENTIFICATION_DATE), pal.getAsString(PaleontologyRecord.IDENTIFICATION_DATE_ROUNDING)) + "</td></tr>"); }
									if (pal.get(PaleontologyRecord.STAGE) != null) { out.println("<tr><td class="heading">Stage</td><td>" + pal.getAsString(PaleontologyRecord.STAGE) + "</td></tr>"); }
									if (pal.get(PaleontologyRecord.STAGE_COMMENTS) != null) { out.println("<tr><td class="heading">Stage Comments</td><td>" + pal.getAsString(PaleontologyRecord.STAGE_COMMENTS) + "</td></tr>"); }
									if (pal.get(PaleontologyRecord.LAB) != null) { out.println("<tr><td class="heading">Lab</td><td>" + pal.getAsString(PaleontologyRecord.LAB) + "</td></tr>"); }
									if (pal.get(PaleontologyRecord.LAB_NUMBER) != null) { out.println("<tr><td class="heading">Lab Number</td><td>" + pal.getAsString(PaleontologyRecord.LAB_NUMBER) + "</td></tr>"); }
									if (pal.get(PaleontologyRecord.COLLECTION_COMMENTS) != null) { out.println("<tr><td class="heading">Collection Comments</td><td>" + pal.getAsString(PaleontologyRecord.COLLECTION_COMMENTS) + "</td></tr>"); }
			
									//taxa (double repeating)
									if (pal.get(PaleontologyRecord.TAXONOMIC_LIST) != null) {
										out.println("<tr><td colspan='2'><table border='0' cellspacing='0' cellpadding='2'>");
										for (Iterator i2 = pal.getAsVector(PaleontologyRecord.TAXONOMIC_LIST).iterator(); i2.hasNext(); ) {
											TaxaGroup taxaGroup = (TaxaGroup)i2.next();
											out.println("<tr><td colspan='4' class="heading">" + taxaGroup.getGroupName() + "</td></tr>");
											if (taxaGroup.getTaxaList() != null) {
												out.print("<tr class="heading"><td>Taxonomic Name&nbsp;&nbsp;</td>");
												if (authorChk) { out.print("<td>Author&nbsp;&nbsp;</td>"); }
												if (sCountChk) { out.print("<td>Spec Count&nbsp;&nbsp;</td>"); }
												if (sCoordChk) { out.print("<td>Spec Coord&nbsp;&nbsp;</td>"); }
												if (commChk) { out.print("<td>Comments&nbsp;&nbsp;</td>"); }
												out.println("</tr>");
												for (Iterator i3 = taxaGroup.getTaxaList().iterator(); i3.hasNext(); ) {
													Taxa taxa = (Taxa)i3.next();
													out.print("<tr><td><i>" + taxa.getTaxonomicName() + "</i>&nbsp;&nbsp;</td>");
													if (authorChk) { out.print("<td>" +FREDUtils.noNulls(taxa.getAuthor()) + "&nbsp;&nbsp;</td>"); }
													if (sCountChk) { out.print("<td>" +FREDUtils.noNulls(String.valueOf(taxa.getSpecimenCount())) + "&nbsp;&nbsp;</td>"); }
													if (sCoordChk) { out.print("<td>" +FREDUtils.noNulls(taxa.getSpecimenCoords()) + "&nbsp;&nbsp;</td>"); }
													if (commChk) { out.print("<td>" +FREDUtils.noNulls(taxa.getComments()) + "&nbsp;&nbsp;</td>"); }
													out.println("</tr>");
												}
											} else {
												out.println("<tr><td colspan='4'>No fossils listed</td></tr>");
											}
											out.println("<tr><td><img src='images/blank.gif' height='10' width='1' /></td></tr>");
										}
										out.println("</td></tr></table></td></tr>");
									}
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
									}
								//}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			} */
	
			if (user ==  null) { out.println("<tr><td colspan='2'>More data may be available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/detail.jsp") + "' class='boldlink'>logged</a> in users</td></tr>"); }
			out.println("</table></td></tr></table>");
		}
	} 
	else { //no sampleID
		drawTop(out, et, request, response);
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>No SampleID received.  Click <a href='index.jsp' class="heading">here</a> to return to the FRED home page.</td></tr>");
		out.println("</table>");
	}
	
	drawBottom(out, et); 
%>