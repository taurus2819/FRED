<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.*"
%><%@page import="nz.cri.gns.db.*"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="nz.cri.gns.db.metadata.*"
%><%@page import="nz.cri.gns.db.site.*"
%><%@page import="nz.cri.gns.util.map.*"
%><%@page import="java.net.*"
%><%@page import="java.text.*"
%><%@page import="java.util.List"
%><%@page import="java.util.Iterator"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.AuditEdit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FrNumber"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.Record"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%
	User user = (User)getUser(session);
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");

	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	FeatureUtil featureUtil = new FeatureUtil(factory);
	SampleUtil sampleUtil = new SampleUtil(factory);
	FolderUtil folderUtil = new FolderUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();

	et.setDisplayLoadingMessage(true);

	Sample sample = null;
	//if FeatureID given then get SampleID or transer to drillhole
	if (request.getParameter("FeatID") != null) {
		Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatId")));
		if (!feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
			response.sendRedirect("drillhole_detail.jsp?ID=" + feature.getFeatureId());
		}
		sample = featureUtil.getOutcropSample(feature);
	} else if (request.getParameter("ID") != null) {
		sample = sampleUtil.getSample(Integer.parseInt(request.getParameter("ID")));
	} else try {
		sample = sampleUtil.getSample(Integer.parseInt((String)session.getAttribute("SampleID")));
	} catch (Exception e) {
		//Can't find the sample
		response.sendRedirect("folder_list.jsp");
	}
	
	if (request.getParameter("ActionType") != null) { //do something
		String actionType = request.getParameter("ActionType");
		if (actionType.equals("Approve")) {
			featureUtil.approveFeature(sample.getFeature(),	//Feature
				request.getParameter("MapSheet"),			//Map sheet
				new Integer(request.getParameter("SerialNum")),	// Serial
				request.getParameter("RecollNum"),			//Recollection
				request.getParameter("CurComm"),			//Comments
				user);
			response.sendRedirect("admin_folder_detail.jsp?ID=" + sample.getFeature().getMasterFile().getFolderId());
			return;
		}
		else if (actionType.equals("Reject")) {
			featureUtil.rejectLocality(sample.getFeature(), request.getParameter("CurComm"), user);
			response.sendRedirect("admin_folder_detail.jsp?ID=" + sample.getFeature().getMasterFile().getFolderId());
			return;
		}
		else if (actionType.equals("AddtoFold") && !request.getParameter("FoldID").equals("-")) {
			featureUtil.addToFolder(sample.getFeature(), Integer.parseInt(request.getParameter("FoldID")), user);
			%><script language="JavaScript"><!--
alert("Locality added to <%=folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user).getFolder().getName()%>");
//--</script><%
		}
	}

	drawTop(out, et, request, response);

	boolean authorChk = "true".equals(request.getParameter("AuthorChk"));
	boolean sCountChk = "true".equals(request.getParameter("SCountChk"));
	boolean sCoordChk = "true".equals(request.getParameter("SCoordChk"));
	boolean commChk = !"false".equals(request.getParameter("CommChk"));

	Feature feature = sample.getFeature();
	
	//List data
	%><table style="margin-left:10px; margin-top:20px; width:180px;" border="0"><%
	Audit audit = sample.getAudit();
	if (!audit.getStatus().equals(FREDConstants.APPROVED)) {
		%><tr><td style="color: red">The locality that you have requested has not yet been approved for masterfile inclusion.  </td></tr><%
		if (user == null || !folderUtil.getUserFolder(audit.getFolder().getFolderId().intValue(), user).isAllowedReadLocalities()) {
			%></table><%
			drawBottom(out, et); 
			folderUtil.closeSession();
			return;
		}
	}
	%><tr><td colspan="2" align="center"><img src="images/loc.gif" height="20" width="20" /></td></tr>
<tr><td colspan="2" align="center" class="bigheading" ><%=sample.getSampleName()%></td></tr>
<tr><td colspan="2" align="center"><%=feature.getFeatureType()%></td></tr><%
	if (feature.getMasterFile() != null) {
		%><tr><td class="smallheading">Masterfile:<img src="images/blank.gif" height="1" width="5" /></td><td class="smalltext"><%=feature.getMasterFile().getName()%></td></tr><%
	}
	if (!audit.getStatus().equals(FREDConstants.APPROVED)) {
		%><tr><td class="smallheading">Status:<img src="images/blank.gif" height="1" width="5" /></td><td class="smalltext"><%=audit.getStatus()%></td></tr><%
	}
	if (audit.getCreatedById() != null || audit.getCreatedDate() != null) {
		%><tr><td class='smallheading'>Created:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'><%
		if (audit.getCreatedById() != null) { 
			%><%=FREDUtil.getUserName(audit.getCreatedById().intValue())%><br /><%
		}
		if (audit.getCreatedDate() != null) { 
			%><%=FREDUtil.formatDateForOutput(audit.getCreatedDate(), "Day")%><%
		}
		%></td></tr><%
	}
	AuditEdit edit = sampleUtil.getMostRecentEdit(audit);
	if (edit != null && edit.getEditedById() != null || edit.getEditedDate() != null) {
		%><tr><td class='smallheading'>Last Editted:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'><%
		if (edit.getEditedById() != null) { 
			%><%=FREDUtil.getUserName(edit.getEditedById().intValue())%><br /><%
		}
		if (edit.getEditedDate() != null) { 
			%><%=FREDUtil.formatDateForOutput(edit.getEditedDate(), "Day")%><%
		}
		%></td></tr><%
	}
	if (audit.getSubmittedById() != null || audit.getSubmittedDate() != null) {
		%><tr><td class='smallheading'>Submitted:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'><%
		if (audit.getSubmittedById() != null) { 
			%><%=FREDUtil.getUserName(audit.getSubmittedById().intValue())%><br /><%
		}
		if (audit.getSubmittedDate() != null) { 
			%><%=FREDUtil.formatDateForOutput(audit.getSubmittedDate(), "Day")%><%
		}
		%></td></tr><%
	}
	if (audit.getApprovedById() != null || audit.getApprovedDate() != null) {
		%><tr><td class='smallheading'>Approved:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'><%
		if (audit.getApprovedById() != null) { 
			%><%=FREDUtil.getUserName(audit.getApprovedById().intValue())%><br /><%
		}
		if (audit.getApprovedDate() != null) { 
			%><%=FREDUtil.formatDateForOutput(audit.getApprovedDate(), "Day")%><%
		}
		%></td></tr><%
	}

	if (user != null) {

		%><tr><td class="smallheading"><a href="audit_detail.jsp?ID=<%=sample.getSampleId()%>" target="audit">More...</a></td></tr>");
<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr><%
		
		out.println("<tr><td colspan='2'><table border='0'>");
		
		//Generate list of users folders
		List folders = folderUtil.getPersonalFolders(user);
		boolean hasFolderWrite = false;
		for (Iterator it = folders.iterator(); it.hasNext(); ) {
			if (((UserFolder)it.next()).isAllowedCreateLocalities()) {
				hasFolderWrite = true;
				break;
			}
		}
		if (hasFolderWrite) {
			%><form name="FolderForm" method="post" action="detail.jsp">
<tr><td colspan="2"><select name="FoldID"><option value="-">-- Choose --</option>
<%
			for (Iterator i = folders.iterator(); i.hasNext(); ) {
				UserFolder folder = (UserFolder)i.next();
				if (folder.isAllowedCreateLocalities()) {
					%><option value="<%=folder.getFolderId()%>"><%=(folder.getFolderName().length() <= 17) ? folder.getFolderName() : folder.getFolderName().substring(0, 17)%></option>
<%
				}
			}
			%>
</select></td></tr>
<tr><td><a href="javascript:FolderForm.submit();"><img src="images/folder.gif" height="20" width="20" border="0" alt="Add to Folder" /></a></td><td><a href="#" onClick="FolderForm.submit();" class="heading">Add to Folder</a></td></tr>
<input type="hidden" name="ID" value="<%=sample.getSampleId()%>" />
<input type="hidden" name="ActionType" value="AddtoFold" />
</form>
<tr><td>&nbsp;</td></tr>
<%
		}
		
		%><tr><td><a href="print_front.jsp?ID=<%=sample.getSampleId()%>&FormType=Full" target="print"><img src="images/print.gif" width="20" height="20" border="0" alt="Print" /></a>&nbsp;&nbsp;</td><td><a href="print_front.jsp?ID=<%=sample.getSampleId()%>&FormType=Full" class="heading" target="print">Print Front</a></td></tr><%
		for (Iterator i = sample.getRecords().iterator(); i.hasNext(); ) {
			Record record = (Record)i.next();
			if (record.getPaleontology() != null) {
				%><tr><td><a href="print_pal.jsp?ID=<%=record.getRecordId()%>" target="print"><img src="images/print.gif" width="20" height="20" border="0" alt="Print" /></a>&nbsp;&nbsp;</td><td><a href="print_pal.jsp?ID=<%=record.getRecordId()%>" class="heading" target="print">Print Pal Record</br ><%=RecordUtil.getRecordName(record)%></a></td></tr><%
			}
		}
		%></table></td></tr><%

		if (featureUtil.isAllowedApproveFeature(user, sample.getFeature())) {
			FrNumber frNumber = featureUtil.getNextAvailableFrNumber(sample.getFeature());
			out.println("<tr><td colspan='2'>");
			out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
			out.println("<form name='RevForm' method='post' action='detail.jsp'>");
			out.println("<input type='hidden' name='ID' value='" + sample.getSampleId() + "'>");
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
			out.println("<tr><td colspan='2'><textarea name='CurComm' rows='5' cols='25'>" + DBUtils.nvl(sample.getAudit().getCuratorComments()) + "</textarea></td></tr>");
			out.println("</form>");
			out.println("</table>");
			out.println("</td></tr>");
		}
		else {
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td class='heading' colspan='2' align='center'>Taxonomic List Options</td></tr>");
			out.println("<form name='TaxaForm' method='post' action='detail.jsp'>");
			out.println("<input type='hidden' name='ID' value='" + sample.getSampleId() + "'>");
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
		}
	}
	out.println("</table>");

	drawEndNavigation(out);
	
	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");
	
	//Locality Data
	out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
	if (sample.getYardFrNumber() != null) { 
		out.println("<tr><td class='heading'>Yard FR Number</td><td>" + sample.getYardFrNumber().getFrNumber() + "</td></tr>"); 
	}
	if (sample.getFeature().getSiteId() != null) {
		SiteRecord sr = FREDUtil.getSite(sample.getFeature());
		int origID = sr.getOriginalId();
		if (origID != -1) {
			Datum datum = sr.getOrigCoordDatum();
			Datum.Coordinate coord = sr.getOrigCoordAsCoord();	
			if (!(datum.getName().equals("NZGD49") && !(datum.getName().equals("NZMG")))) {
				if (coord instanceof Datum.LatLong) {
					out.print("<tr><td class='heading'>Lat/Long</td>");
				} else {
					out.print("<tr><td class='heading'>Grid Ref</td>");
				}
				out.println("<td>" + datum.getHumanStringFor(coord).replaceAll("Geographic ", "") + "</td></tr>");
			}
			try {
				Datum nzmgDatum = DatumFactory.createDatum("NZMG");
				Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
				out.println("<tr><td class='heading'>Grid Ref</td><td>" + nzmgDatum.getHumanStringFor(nzmgCoord) + "</td></tr>");
			} catch (Exception e) { System.out.println(e.getMessage()); }
		}
		Datum.LatLong ll = sr.getLatLong();
		if (ll.getNorthSouth() != 999)
			out.println("<tr><td class='heading'>Lat/Long</td><td>" + ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)</td></tr>");
		if (sr.getMethod() != null) { 
			out.println("<tr><td class='heading'>Method</td><td>" + sample.getAsString(Sample.METHOD) + "</td></tr>"); 
		}
		if (sample.get(Sample.ACCURACY) != null) { 
			out.println("<tr><td class='heading'>Accuracy</td><td>&#177 " + sample.getAsDouble(Sample.ACCURACY) + "m</td></tr>"); 
		}
	}
			
	if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
		if (sample.getFeature().getFeatureName() != null) { 
			out.println("<tr><td class='heading'>Field Number</td><td>" + sample.getFeature().getFeatureName() + "</td></tr>"); \
		}
	} else {
		if (featType.getFeatureType().equals(FREDConstants.DRILLHOLE)) {
			if (sample.getFeature().getFeatureName() != null) { 
				out.println("<tr><td class='heading'>Drillhole Name</td><td><a href='drillhole_detail.jsp?ID=" + sample.getFeature().getFeatureId() + "'>" + sample.getFeature().getFeatureName() + "</a></td></tr>"); 
			}
			if (sample.get(Sample.DRILLHOLE_DEPTH) != null) { 
				out.println("<tr><td class='heading'>Sample Depth</td><td>" + sample.getAsString(Sample.DRILLHOLE_DEPTH) + "</td></tr>"); 
			}
			out.println("<tr><td class='heading'>Other Drillhole Samples</td><td>");
		} else { //VertSect
			if (sample.get(Sample.FEATURE_NAME) != null) { out.println("<tr><td class='heading'>Section Name</td><td><a href='drillhole_detail.jsp?ID=" + sample.getAsString(Sample.FEATURE_ID) + "'>" + sample.getAsString(Sample.FEATURE_NAME) + "</a></td></tr>"); }
			if (sample.get(Sample.DRILLHOLE_DEPTH) != null) { out.println("<tr><td class='heading'>Sample Height</td><td>" + sample.getAsString(Sample.DRILLHOLE_DEPTH) + "</td></tr>"); }
			out.println("<tr><td class='heading'>Other Section Samples</td><td>");
		}
		//check for samples above and below current one
		try {
			Sample sampleAbove = FREDUtils.getSampleAbove(sample, user, state);
			out.println("Sample Above: <a href='detail.jsp?ID=" + sampleAbove.getAsString(Sample.SAMPLE_ID) + "'>" + sampleAbove.getAsString(Sample.DRILLHOLE_DEPTH) + "</a><br>");
		} catch (Exception e) {}
		try {
			Sample sampleBelow = FREDUtils.getSampleBelow(sample, user, state);
			out.println("Sample Below: <a href='detail.jsp?ID=" + sampleBelow.getAsString(Sample.SAMPLE_ID) + "'>" + sampleBelow.getAsString(Sample.DRILLHOLE_DEPTH) + "</a><br>");
		} catch (Exception e) {}
		out.println("</td></tr>");
	}
		if (sample.isUserAuthenticated() && sample.get(Sample.LOCALITY) != null) { out.println("<tr><td class='heading'>Locality</td><td>" + sample.getAsString(Sample.LOCALITY) + "</td></tr>"); }
			if (!featType.equals(Feature.OUTCROP_LOCALITY)) {
				if (sample.isUserAuthenticated() && sample.get(Sample.PERSON) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
						out.print("Operating Company");
					} else {
						out.print("Section Collector");
					}
					out.println("</td><td>" + sample.getAsString(Sample.PERSON) + "</td></tr>");
				}
				if (sample.isUserAuthenticated() && sample.get(Sample.START_DATE) != null) {
					out.print("<tr><td class='heading'>");
					if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
						out.print("Spud Date");
					} else {
						out.print("Sampling Start Date");
					}
					out.print("</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.START_DATE), sample.getAsString(Sample.START_DATE_ROUNDING)) + "</td></tr>");
				}
				if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DATE) != null) {
					out.print("<tr><td class='heading'>Completion Date</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.FINISH_DATE), sample.getAsString(Sample.FINISH_DATE_ROUNDING)) + "</td></tr>");
				}
				if (featType.equals(Feature.DRILLHOLE_LOCALITY) && sample.isUserAuthenticated() && sample.get(Sample.DRILLHOLE_LICENCE_NAME) != null) { out.println("<tr><td class='heading' width='135'>Licence Area</td><td>" + sample.getAsString(Sample.DRILLHOLE_LICENCE_NAME) + "</td></tr>"); }
				if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_TYPE) != null) { out.println("<tr><td class='heading' width='135'>Datum Type</td><td>" + sample.getAsString(Sample.DATUM_TYPE) + "</td></tr>"); }
				if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_ELEVATION) != null) { out.println("<tr><td class='heading' width='135'>Datum Elevation</td><td>" + sample.getAsString(Sample.DATUM_ELEVATION) + " m asl</td></tr>"); }
				if (sample.isUserAuthenticated() && sample.get(Sample.START_DEPTH) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
						out.print("Kick-off Depth");
					} else {
						out.print("Top Horizon");
					}
					out.println("</td><td>" + sample.getAsString(Sample.START_DEPTH) + " m</td></tr>");
				}
				if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DEPTH) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
						out.print("Termination Depth");
					} else {
						out.print("Base Horizon");
					}
					out.println("</td><td>" + sample.getAsString(Sample.FINISH_DEPTH) + " m</td></tr>");
				}
			}
			//Image/Files
			if (sample.isUserAuthenticated() && sample.getFeatureMetadataRecordsCount() > 0) {
				out.println("<tr><td colspan='2' class='heading'>Images/Files</td></tr>");
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
					out.print("<tr><td class='heading'>Collectors</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.COLLECTOR).iterator(); i2.hasNext(); ) {
						KeyValueObject coll = (KeyValueObject)i2.next();
						out.print(coll.getValue() + "<br />");
					}
					out.print("</td></tr>");
				}
				if (sample.get(Sample.COLLECTION_DATE) != null) { out.print("<tr><td class='heading'>Collection Date</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.COLLECTION_DATE), sample.getAsString(Sample.COLLECTION_DATE_ROUNDING)) + "</td></tr>"); }
				if (sample.get(Sample.STRAT_UNIT) != null) { out.println("<tr><td class='heading'>Strat Name</td><td>" + sample.getAsString(Sample.STRAT_UNIT) + "</td></tr>"); }
				if (sample.get(Sample.IN_PLACE) != null) { out.println("<tr><td class='heading'>In Place</td><td>" + sample.getAsString(Sample.IN_PLACE) + "</td></tr>"); }
				//sent to (repeating)
				if (sample.get(Sample.SENT_TO) != null) {
					out.print("<tr><td class='heading'>Sent To</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.SENT_TO).iterator(); i2.hasNext(); ) {
						SentTo sentTo = (SentTo)i2.next();
						out.print(sentTo.getSentTo() + "<br />");
					}
				out.print("</td></tr>");
				}
				if (sample.get(Sample.NOT_COLLECTED) != null) { out.println("<tr><td class='heading'>Not Collected</td><td>" + sample.getAsString(Sample.NOT_COLLECTED) + "</td></tr>"); }
				if (sample.get(Sample.SIGNIFICANCE) != null) { out.println("<tr><td class='heading'>Significance</td><td>" + sample.getAsString(Sample.SIGNIFICANCE) + "</td></tr>"); }
				if (sample.get(Sample.INFERRED_STAGE) != null) { out.println("<tr><td class='heading'>Inferred Stage</td><td>" + sample.getAsString(Sample.INFERRED_STAGE) + "</td></tr>"); }
				if (sample.get(Sample.KNOWN_STAGE) != null) { out.println("<tr><td class='heading'>Known Stage</td><td>" + sample.getAsString(Sample.KNOWN_STAGE) + "</td></tr>"); }
				//Nearby samples (repeating)
				if (sample.get(Sample.RELATIONSHIP_NEARBY) != null) {
					out.print("<tr><td class='heading'>Samples Nearby</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_NEARBY).iterator(); i2.hasNext(); ) {
						Relationship nearRel = (Relationship)i2.next();
						out.print(nearRel.getDistanceRelation() + " <a href='detail.jsp?FeatID=" + nearRel.getRelatedFeatureID() + "'>" + nearRel.getRelatedSampleName() +"</a><br />");
					}
				out.print("</td></tr>");
				}
				//Sample relationships (repeating)
				if (sample.get(Sample.RELATIONSHIP_SAMPLE) != null) {
					out.print("<tr><td class='heading'>Sample Relationships</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_SAMPLE).iterator(); i2.hasNext(); ) {
						Relationship sampRel = (Relationship)i2.next();
						out.print(sampRel.getDistanceRelation() + " <a href='detail.jsp?FeatID=" + sampRel.getRelatedFeatureID() + "'>" + sampRel.getRelatedSampleName() + "</a><br />");
					}
				out.print("</td></tr>");
				}
				//Strat relationships (repeating)
				if (sample.get(Sample.RELATIONSHIP_STRAT) != null) {
					out.print("<tr><td class='heading'>Stratigraphic Relationships</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_STRAT).iterator(); i2.hasNext(); ) {
						Relationship stratRel = (Relationship)i2.next();
						out.print(stratRel.getRelationship() + "<br />");
					}
				out.print("</td></tr>");
				}
				if (sample.get(Sample.COLUMN_MAP) != null) { out.println("<tr><td class='heading'>Column/Map</td><td>" + sample.getAsString(Sample.COLUMN_MAP) + "</td></tr>"); }
				if (sample.get(Sample.DIP) != null) { out.println("<tr><td class='heading'>Dip</td><td>" + sample.getAsString(Sample.DIP) + "</td></tr>"); }
				if (sample.get(Sample.DIP_DIRECTION) != null) { out.println("<tr><td class='heading'>Dip Direction</td><td>" + sample.getAsString(Sample.DIP_DIRECTION) + "</td></tr>"); }
				if (sample.get(Sample.STRIKE) != null) { out.println("<tr><td class='heading'>Strike</td><td>" + sample.getAsString(Sample.STRIKE) + "</td></tr>"); }
				if (sample.get(Sample.FACING) != null) { out.println("<tr><td class='heading'>Facing</td><td>" + sample.getAsString(Sample.FACING) + "</td></tr>"); }
				if (sample.get(Sample.GRAINSIZE) != null) { out.println("<tr><td class='heading'>Grain Size</td><td>" + sample.getAsString(Sample.GRAINSIZE) + "</td></tr>"); }
				if (sample.get(Sample.COMPARATOR_USED) != null) { out.println("<tr><td class='heading'>Comparator Used</td><td>" + sample.getAsString(Sample.COMPARATOR_USED) + "</td></tr>"); }
				if (sample.get(Sample.BED_THICKNESS) != null) { out.println("<tr><td class='heading'>Bed Thickness</td><td>" + sample.getAsString(Sample.BED_THICKNESS) + "</td></tr>"); }
				if (sample.get(Sample.BEDDING) != null) { out.println("<tr><td class='heading'>Bedding</td><td>" + sample.getAsString(Sample.BEDDING) + "</td></tr>"); }
				if (sample.get(Sample.WEATHERING) != null) { out.println("<tr><td class='heading'>Weathering</td><td>" + sample.getAsString(Sample.WEATHERING) + "</td></tr>"); }
				if (sample.get(Sample.HARDNESS) != null) { out.println("<tr><td class='heading'>Hardness</td><td>" + sample.getAsString(Sample.HARDNESS) + "</td></tr>"); }
				if (sample.get(Sample.CARBONATE) != null) { out.println("<tr><td class='heading'>Carbonate</td><td>" + sample.getAsString(Sample.CARBONATE) + "</td></tr>"); }
				if (sample.get(Sample.COLOUR) != null) { out.println("<tr><td class='heading'>Colour</td><td>" + sample.getAsString(Sample.COLOUR) + "</td></tr>"); }
				//sed features (repeating)
				if (sample.get(Sample.SED_FEATURE) != null) {
					out.print("<tr><td class='heading'>Additional Features</td><td>");
					for (Iterator i2 = sample.getAsVector(Sample.SED_FEATURE).iterator(); i2.hasNext(); ) {
						SedFeature sf = (SedFeature)i2.next();
						out.print(sf.getSedFeature() + "<br />");
					}
				out.print("</td></tr>");
				}
				if (sample.get(Sample.DEPOSITION_ENV) != null) { out.println("<tr><td class='heading'>Inferred Environment</td><td>" + sample.getAsString(Sample.DEPOSITION_ENV) + "</td></tr>"); }
				if (sample.get(Sample.ROCK_NATURE) != null) { out.println("<tr><td class='heading'>Nature of Rock Unit</td><td>" + sample.getAsString(Sample.ROCK_NATURE) + "</td></tr>"); }
				if (sample.get(Sample.CORRESPONDENCE) != null) { out.println("<tr><td class='heading'>Correspondence</td><td>" + sample.getAsString(Sample.CORRESPONDENCE) + "</td></tr>"); }
				//Image/Files
				if (sample.getSampleMetadataRecordsCount() > 0) {
					out.println("<tr><td colspan='2' class='heading'>Images/Files</td></tr>");
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
									out.print("<tr><td class='heading'>Adoptors</td><td>");
									for (Iterator i2 = ado.getAsVector(AdoptionRecord.ADOPTOR).iterator(); i2.hasNext(); ) {
										KeyValueObject coll = (KeyValueObject)i2.next();
										out.print(coll.getValue() + "<br />");
									}
									out.print("</td></tr>");
								}
								if (ado.get(AdoptionRecord.ADOPTION_DATE) != null) { out.print("<tr><td class='heading'>Adoption Date</td><td>" + FREDUtils.formatDateForOutput(ado.getAsDate(AdoptionRecord.ADOPTION_DATE), ado.getAsString(AdoptionRecord.ADOPTION_DATE_ROUNDING)) + "</td></tr>"); }
								if (ado.get(AdoptionRecord.ADOPTED_STAGE) != null) { out.println("<tr><td class='heading'>Adopted Stage</td><td>" + ado.getAsString(AdoptionRecord.ADOPTED_STAGE) + "</td></tr>"); }
								if (ado.get(AdoptionRecord.COMMENTS) != null) { out.println("<tr><td class='heading'>Comments</td><td>" + ado.getAsString(AdoptionRecord.COMMENTS) + "</td></tr>"); }
								//Image/Files
								if (ado.getMetadataRecordsCount() > 0) {
									out.println("<tr><td colspan='2' class='heading'>Images/Files</td></tr>");
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
								PaleontologyRecord pal = (PaleontologyRecord) PaleontologyRecord.getData(Integer.parseInt(rec.getKey()), user, state);
								out.println("<tr><td colspan='2' class='bigheading'>Paleontology Data</td></tr>");
								//identifiers (repeating)
								if (pal.get(PaleontologyRecord.IDENTIFIER) != null) {
									out.print("<tr><td class='heading'>Identifiers</td><td>");
									for (Iterator i2 = pal.getAsVector(PaleontologyRecord.IDENTIFIER).iterator(); i2.hasNext(); ) {
										KeyValueObject coll = (KeyValueObject)i2.next();
										out.print(coll.getValue() + "<br />");
									}
									out.print("</td></tr>");
								}
								if (pal.get(PaleontologyRecord.IDENTIFICATION_DATE) != null) { out.print("<tr><td class='heading'>Identification Date</td><td>" + FREDUtils.formatDateForOutput(pal.getAsDate(PaleontologyRecord.IDENTIFICATION_DATE), pal.getAsString(PaleontologyRecord.IDENTIFICATION_DATE_ROUNDING)) + "</td></tr>"); }
								if (pal.get(PaleontologyRecord.STAGE) != null) { out.println("<tr><td class='heading'>Stage</td><td>" + pal.getAsString(PaleontologyRecord.STAGE) + "</td></tr>"); }
								if (pal.get(PaleontologyRecord.STAGE_COMMENTS) != null) { out.println("<tr><td class='heading'>Stage Comments</td><td>" + pal.getAsString(PaleontologyRecord.STAGE_COMMENTS) + "</td></tr>"); }
								if (pal.get(PaleontologyRecord.LAB) != null) { out.println("<tr><td class='heading'>Lab</td><td>" + pal.getAsString(PaleontologyRecord.LAB) + "</td></tr>"); }
								if (pal.get(PaleontologyRecord.LAB_NUMBER) != null) { out.println("<tr><td class='heading'>Lab Number</td><td>" + pal.getAsString(PaleontologyRecord.LAB_NUMBER) + "</td></tr>"); }
								if (pal.get(PaleontologyRecord.COLLECTION_COMMENTS) != null) { out.println("<tr><td class='heading'>Collection Comments</td><td>" + pal.getAsString(PaleontologyRecord.COLLECTION_COMMENTS) + "</td></tr>"); }
		
								//taxa (double repeating)
								if (pal.get(PaleontologyRecord.TAXONOMIC_LIST) != null) {
									out.println("<tr><td colspan='2'><table border='0' cellspacing='0' cellpadding='2'>");
									for (Iterator i2 = pal.getAsVector(PaleontologyRecord.TAXONOMIC_LIST).iterator(); i2.hasNext(); ) {
										TaxaGroup taxaGroup = (TaxaGroup)i2.next();
										out.println("<tr><td colspan='4' class='heading'>" + taxaGroup.getGroupName() + "</td></tr>");
										if (taxaGroup.getTaxaList() != null) {
											out.print("<tr class='heading'><td>Taxonomic Name&nbsp;&nbsp;</td>");
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
									out.println("<tr><td colspan='2' class='heading'>Images/Files</td></tr>");
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
							} catch (Exception e) {
							}
						}
					}
				}
			}
	
			if (user ==  null) { out.println("<tr><td colspan='2'>More data may be available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/detail.jsp") + "' class='boldlink'>logged</a> in users</td></tr>"); }
			out.println("</table></td></tr></table>");
		}
		catch (Exception e) { // no record or not approved
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>Either the sample doesn't exist or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</td></tr>");
			out.println("</table>");
		} 
	} 
	else { //no sampleID
		drawTop(out, et, request, response);
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>No SampleID received.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</td></tr>");
		out.println("</table>");
	}
	
	drawBottom(out, et); 
	folderUtil.closeSession();
%>