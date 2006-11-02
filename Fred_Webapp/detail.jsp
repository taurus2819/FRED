<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Adoption"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.AuditEdit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FrNumber"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Relationship"
%><%@page import="nz.cri.gns.fred.model.PersonRelationship"
%><%@page import="nz.cri.gns.fred.model.SentTo"
%><%@page import="nz.cri.gns.fred.model.SedimentaryFeature"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.model.Meta"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.db.site.SiteRecord"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.util.map.Datum"
%><%@page import="nz.cri.gns.util.map.Datum.Coordinate"
%><%@page import="nz.cri.gns.util.map.Datum.LatLong"
%><%@page import="nz.cri.gns.util.map.DatumFactory"
%><%@page import="java.net.URLEncoder"
%><%@page import="java.io.PrintWriter"
%><%@page import="java.util.Arrays"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.de.DataInputException"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%!
	public String getName(HttpServletRequest request) {
		try {
			String sampID = request.getParameter("ID");
			String featID = request.getParameter("FeatID");
			DAOFactory factory = HibernateUtil.get().getDAOFactory();
			if (featID != null) {
				Feature feature = new FeatureUtil(factory).getFeature(Integer.parseInt(featID));
				return "FRED :: Locality Detail for " + FeatureUtil.getFeatureIdentifyingName(feature);
			} else if (sampID != null) {
				Sample sample = new SampleUtil(factory).getSample(Integer.parseInt(sampID));
				return "FRED :: Sample Detail for " + ((sample.getFrNumber() != null) ? sample.getFrNumber().getFrNumber() : FeatureUtil.getFeatureIdentifyingName(sample.getFeature()));
			}
			return "FRED :: The Fossil Record Electronic Database";
		} catch (Exception e) {
			return "FRED :: The Fossil Record Electronic Database";
		}
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
		}
	}
%><%
try {
	User user = (User) getUser(session);
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	SampleUtil sampleUtil = new SampleUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	et.setUseNavigationColumn(false);
	et.setDisplayLoadingMessage(true);

	String sampID = request.getParameter("ID");
	String featID = request.getParameter("FeatID");
	Feature feature =  null;
	Sample sample = null;
	
	String backURL = request.getParameter("backURL");
	if (backURL != null && backURL.length() == 0)
		backURL = null;
	String backText = request.getParameter("backText");
	if (backText != null && backText.length() == 0)
		backText = null;
	String backStr = (backURL != null) ? "&backURL=" + URLEncoder.encode(backURL, "ISO-8859-1") : "";
	backStr += (backText != null) ? "&backText=" + URLEncoder.encode(backText, "ISO-8859-1") : "";
	
	//if FeatureID given then check if outcrop and if redirect to display sample details
	try {
	if (featID != null) {
		session.setAttribute("FRED.FeatureID", featID);
		feature = featureUtil.getFeature(Integer.parseInt(featID));
		if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
			response.sendRedirect("detail.jsp?ID=" + ((Sample)feature.getSamples().iterator().next()).getSampleId() + backStr);
			return;
		}
	} else if (sampID != null) {
		session.setAttribute("FRED.SampleID", sampID);
		sample = sampleUtil.getSample(Integer.parseInt(sampID));
		feature = sample.getFeature();
	} else if (session.getAttribute("FRED.FeatureID") != null) {
		response.sendRedirect("detail.jsp?FeatID=" + ((String) session.getAttribute("FRED.FeatureID")));
		return;
	} else if (session.getAttribute("FRED.SampleID") != null) {
		response.sendRedirect("detail.jsp?ID=" + ((String) session.getAttribute("FRED.SampleID")));
		return;
	}
	} catch (Exception e) {}
	
	if (feature != null) {
		
		IconnedLink[] il = new IconnedLink[(backURL != null) ? 2 : 1];
		if (backURL != null)
			il[0] = new IconnedLink(backURL, "images/back_arrow.gif", (backText != null) ? request.getParameter("backText") : "Back");
		il[(backURL != null) ? 1 : 0] = new IconnedLink("locality_map.jsp?FeatID=" + feature.getFeatureId() + "&backURL=" + URLEncoder.encode("detail.jsp?" + ((sample != null) ? "ID=" + sample.getSampleId() : "?FeatID=" + feature.getFeatureId()) + backStr, "ISO-8859-1")+ "&backText=Back%20To%20Locality", "images/map.gif", "Locality Map");
		addButtons(et, il);
	
		Audit audit = feature.getAudit();
		String featType = feature.getFeatureType();
		boolean isAllowedReadFeature = featureUtil.isAllowedReadFeature(user, feature);
		
		boolean authorChk = (request.getParameter("AuthorChk") != null && request.getParameter("AuthorChk").equals("true"));
		boolean sCountChk = (request.getParameter("SCountChk") != null && request.getParameter("SCountChk").equals("true"));
		boolean sCoordChk = (request.getParameter("SCoordChk") != null && request.getParameter("SCoordChk").equals("true"));
		boolean commChk = (request.getParameter("CommChk") != null && request.getParameter("CommChk").equals("true"));		
		
		if (featureUtil.isAllowedReadFeatureSite(user, feature)) {
			if (request.getParameter("ActionType") != null) { //do something
				String actionType = request.getParameter("ActionType");
				if (actionType.equals("Approve")) {
					try {
						featureUtil.approveFeature(feature, request.getParameter("MapSheet"), new Integer(request.getParameter("SerialNum")), request.getParameter("RecollNum"), request.getParameter("CurComm"), user);
						response.sendRedirect("admin_folder_detail.jsp?ID=" + feature.getMasterFile().getFolderId() + "&q=" + Math.random());
						return;
					} catch (DataInputException e) {
						%><script language="JavaScript">alert("The FR Number already exists please try another one");</script><%
					}
				}
				else if (actionType.equals("Reject")) {
					featureUtil.rejectLocality(feature, request.getParameter("CurComm"), user);
					response.sendRedirect("admin_folder_detail.jsp?ID=" + feature.getMasterFile().getFolderId() + "&q=" + Math.random());
					return;
				}
				else if (actionType.equals("AddtoFold") && !request.getParameter("FoldID").equals("-")) {
					featureUtil.addToFolder(feature, Integer.parseInt(request.getParameter("FoldID")), user);
					%><script language="JavaScript">alert("Locality Added to folder");</script><%
				}
			}

			drawTop(out, et, request, response);

			//List data
			%><table border="0"><tr><td><img src="images/blank.gif" width="10" height="10" /></td></tr>
			<tr><td></td><td><%
			
			//Audit details
			%><p><%
			startDETable(pageContext);
			%><table border="0" width="160">
			<tr><td colspan="2" class="deHeading">Audit Details</td></tr><%
			String status = audit.getStatus();
			%><tr><td class="smallheading">Status:&nbsp;</td>
			<td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%> class="smalltext"><%=status%>&nbsp;&nbsp;</td></tr><%
			if (status.equals(FREDConstants.REJECTED)) {
				%><tr><td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%> class="smallheading">Curator Comments:&nbsp;&nbsp;</td>
				<td <%=AuditUtil.getStatusHTMLOutputStyle(status, new String[] {})%> class="smalltext"><%=DBUtils.nvl(audit.getCuratorComments())%></td></tr><%
			}
			%><tr><td class="smallheading">Created:&nbsp;</td>
			<td class="smalltext"><%=((audit.getCreatedById() != null) ? audit.getCreatedBy().getFullName() + "<br />" : "")%>
				<%=((audit.getCreatedDate() != null) ? FREDUtil.formatDateForOutput(audit.getCreatedDate()) : "")%></td></tr>
			<tr><td class="smallheading">Edited:&nbsp;</td><%
			if (audit.getAuditEdits() != null && audit.getAuditEdits().size() > 0) {
				AuditEdit edit = (AuditEdit) audit.getAuditEdits().iterator().next();
			%><td class="smalltext"><%=((edit.getEditedById() != null) ? edit.getEditedBy().getFullName() + "<br />" : "")%>
				<%=((edit.getEditedDate() != null) ? FREDUtil.formatDateForOutput(edit.getEditedDate()) : "")%></td></tr><%
			}
			%><tr><td class="smallheading">Submitted:&nbsp;</td>
			<td class="smalltext"><%=((audit.getSubmittedById() != null) ? audit.getSubmittedBy().getFullName() + "<br />" : "")%>
				<%=((audit.getSubmittedDate() != null) ? FREDUtil.formatDateForOutput(audit.getSubmittedDate()) : "")%></td></tr>
			<tr><td class="smallheading">Approved:&nbsp;</td>
			<td class="smalltext"><%=((audit.getApprovedById() != null) ? audit.getApprovedBy().getFullName() + "<br />" : "")%>
				<%=((audit.getApprovedDate() != null) ? FREDUtil.formatDateForOutput(audit.getApprovedDate()) : "")%></td></tr>

			<tr><td class="smallheading"><a href="audit_detail.jsp?<%=((sample != null) ? "ID=" + sample.getSampleId() : "FeatID=" + feature.getFeatureId())%>" target="audit">More...</a></td></tr>
			</table><%
			endDETable(pageContext);
			%></p><%
			
			if (isAllowedReadFeature) {	

				//Approve/Reject
				if (featureUtil.isAllowedApproveFeature(user, feature)) {
					FrNumber frNumber = featureUtil.getNextAvailableFrNumber(feature);
					String[] comms = FeatureUtil.splitWorkingComments(feature.getAudit().getWorkingComments());
					String workComm = comms[0];
					String recoll = comms[1];
					%><p><%
					startDETable(pageContext);
					%><form name="RevForm" method="post" action="detail.jsp">
					<%
					if (sample != null) {
						%><input type="hidden" name="ID" value="<%=sample.getSampleId()%>" /><%
					} else {
						%><input type="hidden" name="FeatID" value="<%=feature.getFeatureId()%>" /><%
					}
					if (backURL != null) {
						%><input type="hidden" name="backURL" value="<%=backURL%>" /><%
						if (backText != null) {
							%><input type="hidden" name="backText" value="<%=backText%>" /><%
						}
					}
					%><input type="hidden" name="ActionType" value="" />
					<table border="0" width="160">
					<tr><td colspan="2" class="deHeading">Masterfile Curator</td></tr>
					<tr><td colspan="2" class="heading">User Comments</td></tr>
					<tr><td colspan="2"><%=DBUtils.nvl(workComm)%></td></tr><%
					if (recoll != null) {
						%><tr><td colspan="2">The submitter has indicated that this record is a recollection of <%=recoll%>.</td></tr><%
					}
					%><tr><td><img src="images/blank.gif" height="5" width="1" /></td></tr>
					<tr><td colspan="2" class="heading">FR Number</td></tr>
					<tr><td colspan="2">
						<input type="text" name="MapSheet" size="8" value="<%=frNumber.getMapSheet()%>" />&nbsp;
						/f&nbsp;<input type="text" name="SerialNum" size="3" value="<%=frNumber.getSerialNumber()%>" />&nbsp;
						<input type="text" name="RecollNum" size="1" value="" />
					</td></tr>

					<tr><td colspan="2" class="heading">Curator Comments</td></tr>
					<tr><td colspan="2"><textarea name="CurComm" rows="5" cols="22"><%=DBUtils.nvl(audit.getCuratorComments())%></textarea></td></tr>
					<tr><td><a href="#" onClick="document.RevForm.ActionType.value='Approve';document.RevForm.submit();"><img src="images/ok.gif" width="20" height="20" border="0" alt="Approve" /></a></td><td class="heading" style="text-align: left">Approve</td></tr>
					<tr><td><a href="#" onClick="document.RevForm.ActionType.value='Reject';document.RevForm.submit();"><img src="images/cancel.gif" width="20" height="20" border="0" alt="reject" /></a></td><td class="heading" style="text-align: left">Reject</td></tr>
					</form>
					</table><%
					endDETable(pageContext);
					%></p><%
				}				
				
				//Add to Folder
				if (feature.getAudit().getStatus().equals(FREDConstants.APPROVED) && (new FolderUtil(factory)).getPersonalFolders(user).size() > 0) {		
					%><p><%
					startDETable(pageContext);
					%><table border="0" width="160">
					<tr><td class="deHeading">Add to Folder</td></tr>
					<tr><td>You can add this locality to one of your personal folders by selecting it from the list and clicking <i>Add</i>.</td></tr>
					<form name="FolderForm" method="post" action="detail.jsp"><%
					if (sample != null) {
						%><input type="hidden" name="ID" value="<%=sample.getSampleId()%>" /><%
					} else {
						%><input type="hidden" name="FeatID" value="<%=feature.getFeatureId()%>" /><%
					}
					if (backURL != null) {
						%><input type="hidden" name="backURL" value="<%=backURL%>" /><%
						if (backText != null) {
							%><input type="hidden" name="backText" value="<%=backText%>" /><%
						}
					}
					%><input type="hidden" name="ActionType" value="AddtoFold" />
					<tr><td>
					<select name="FoldID">
					<option value="-">-- Choose --</option><%
					for (UserFolder folder : (new FolderUtil(factory)).getPersonalFolders(user)) {
						String folderName = folder.getFolderName();
						if (folderName.length() > 17)
							folderName = folderName.substring(0, 17);
						%><option value="<%=folder.getFolderId()%>"><%=folderName%></option><%
					}
					%></select>
					</td></tr>
					<tr><td style="text-align: right" class="heading"><a href="#" onClick="FolderForm.submit();">Add</a></td></tr>
					</form>
					</table><%
					endDETable(pageContext);
					%></p><%
				}		
				
				//Taxa list options
				if (sample != null && sampleUtil.getPaleontologyRecordCount(sample) > 0) {
					%><p><%
					startDETable(pageContext);
					%><table border="0" width="160">
					<tr><td class="deHeading">Taxonomic Display Options</td></tr>
					<form name="TaxaForm" method="post" action="detail.jsp"><%
					if (sample != null) {
						%><input type="hidden" name="ID" value="<%=sample.getSampleId()%>" /><%
					} else {
						%><input type="hidden" name="FeatID" value="<%=feature.getFeatureId()%>" /><%
					}
					if (backURL != null) {
						%><input type="hidden" name="backURL" value="<%=backURL%>" /><%
						if (backText != null) {
							%><input type="hidden" name="backText" value="<%=backText%>" /><%
						}
					}
					%><input type="hidden" name="AuthorChk" value="<%=authorChk%>" />
					<input type="hidden" name="SCountChk" value="<%=sCountChk%>" />
					<input type="hidden" name="SCoordChk" value="<%=sCoordChk%>" />
					<input type="hidden" name="CommChk" value="<%=commChk%>" />
					<tr><td class="heading"><%
					if (authorChk) {
						%><a href="#" onClick="document.TaxaForm.AuthorChk.value='false';document.TaxaForm.submit();" title="Hide"><img src="images/ok.gif" width="20" height="20" border="0" /><%
					} else {
						%><a href="#" onClick="document.TaxaForm.AuthorChk.value='true';document.TaxaForm.submit();" title="Show"><img src="images/cancel.gif" width="20" height="20" border="0" /><%
					}
					%></a>&nbsp;&nbsp;Author</td></tr>
					<tr><td class="heading"><%
					if (sCountChk) {
						%><a href="#" onClick="document.TaxaForm.SCountChk.value='false';document.TaxaForm.submit();" title="Hide"><img src="images/ok.gif" width="20" height="20" border="0" /><%
					} else {
						%><a href="#" onClick="document.TaxaForm.SCountChk.value='true';document.TaxaForm.submit();" title="Show"><img src="images/cancel.gif" width="20" height="20" border="0" /><%
					}
					%></a>&nbsp;&nbsp;Specimen Count</td></tr>
					<tr><td class="heading"><%
					if (sCoordChk) {
						%><a href="#" onClick="document.TaxaForm.SCoordChk.value='false';document.TaxaForm.submit();" title="Hide"><img src="images/ok.gif" width="20" height="20" border="0" /><%
					} else {
						%><a href="#" onClick="document.TaxaForm.SCoordChk.value='true';document.TaxaForm.submit();" title="Show"><img src="images/cancel.gif" width="20" height="20" border="0" /><%
					}
					%></a>&nbsp;&nbsp;Specimen Coord</td></tr>
					<tr><td class="heading"><%
					if (commChk) {
						%><a href="#" onClick="document.TaxaForm.CommChk.value='false';document.TaxaForm.submit();" title="Hide"><img src="images/ok.gif" width="20" height="20" border="0" /><%
					} else {
						%><a href="#" onClick="document.TaxaForm.CommChk.value='true';document.TaxaForm.submit();" title="Show"><img src="images/cancel.gif" width="20" height="20" border="0" /><%
					}
					%></a>&nbsp;&nbsp;Comments</td></tr>
					</form></table><%
					endDETable(pageContext);
					%></p><%
				}
			}	
			
			//start data column
			%></td><td><img src="images/blank.gif" width="30" height="1" /></td><td style="text-align: left"><%
			
			//Locality Data
			%><p><%
			startDETable(pageContext);
			%><table border="0" width="550"><tr><td colspan="2" class="deHeading">Locality Information&nbsp;&nbsp;&nbsp;<a href="frf/frf.pdf?FeatIDs=<%=feature.getFeatureId() + "&q=" + Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" /></a></td></tr>
			<tr><td class="heading">FR Number</td><td class="heading"><%=((feature.getFrNumber() != null) ? feature.getFrNumber().getFrNumber() : "not yet allocated")%></td></tr><%
			if (feature.getYardFrNumber() != null) {
				%><tr><td class="heading">Yard FR Number</td><td><%=feature.getYardFrNumber().getFrNumber()%></td></tr><%
			}
			%><tr><td class="heading">Masterfile</td><td><%=((feature.getMasterFile() != null) ? feature.getMasterFile().getName() : "undefined")%></td></tr>
			<tr><td class="heading">Locality Type</td><td><%=featType%></td></tr><%
			if (feature.getFeatureName() != null) {
				String featTypeLbl, linkStart = "", linkStop = "", petWellLink = null;
				if (featType.equals(FREDConstants.OUTCROP)) {
					featTypeLbl = "Field Number";
				} else if (featType.equals(FREDConstants.DRILLHOLE)) {
					featTypeLbl = "Drillhole Name";
					linkStart = "<a href=\"detail.jsp?FeatID=" + feature.getFeatureId() + backStr + "\">";
					linkStop = "</a>";
					petWellLink = FREDUtil.getPetWellLink(feature);
				} else {
					featTypeLbl = "Section Name";
					linkStart = "<a href=\"detail.jsp?FeatID=" + feature.getFeatureId() + backStr + "\">";
					linkStop = "</a>";
				}
				%><tr><td class="heading"><%=featTypeLbl%></td><td><%=linkStart + DBUtils.nvl(feature.getFeatureName()) + linkStop%>
				<%=((petWellLink != null) ? "&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"" + petWellLink + "\" target=\"_blank\" class=\"boldlink\">Open GNS Petroleum Wells Database</a>" : "")%></td></tr><%
			}
			SiteRecord sr = null;
			if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
				Datum datum = FREDUtil.getFREDDatum(feature);
				Coordinate coord = FREDUtil.getFREDCoordinate(feature);
				%><tr><td class="heading">Original Grid Reference</td><td><%=datum.getHumanStringFor(coord).replaceAll("Geographic ", "")%></td></tr><%
				if (!datum.getName().equals("NZMG")) {
					try {
						Datum nzmgDatum = DatumFactory.createDatum("NZMG");
						Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
						if (nzmgDatum.coordinateAcceptable(nzmgCoord)) {
							%><tr><td class="heading">Converted Grid Reference</td><td><%=nzmgDatum.getHumanStringFor(nzmgCoord)%></td></tr><%
						}
					} catch (Exception e) { }
				}
				if (feature.getSiteId() != null) {
					sr = FREDUtil.getSite(feature);
					LatLong ll = sr.getLatLong();
					%><tr><td class="heading">Converted Dec. Lat/Long</td><td><%=ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)"%></td></tr><%
				}	
			}
			if (feature.getMapYear() != null) {
				%><tr><td class="heading">Map Year</td><td><%=DBUtils.nvl(feature.getMapYear())%></td></tr><%
			}
			if (sr != null && !sr.isNull(SiteRecord.H_METHOD_FIELD)) {
				%><tr><td class="heading">Method</td><td><%=FREDUtil.getSiteMethod(sr)%></td></tr><%
			}
			if (sr != null && !sr.isNull(SiteRecord.H_ACCURACY_FIELD)) {
				%><tr><td class="heading">Accuracy</td><td>&#177;<%=String.valueOf(sr.getAccuracy())%> m</td></tr><%
			}
			if (isAllowedReadFeature) {
				if (feature.getLocality() != null) {
					%><tr><td class="heading">Locality</td><td><%=feature.getLocality()%></td></tr><%
				}
				if (sr != null && !sr.isNull(SiteRecord.COUNTRY_FIELD)) {
					%><tr><td class="heading">Country</td><td><%=FREDUtil.getSiteCountry(sr)%></td></tr><%
				}
				if (feature.getCoordComments() != null) {
					%><tr><td class="heading">Coordinate Comments</td><td><%=DBUtils.nvl(feature.getCoordComments())%></td></tr><%
				}

				//Drillhole/Vert Sect fields
				if (!featType.equals(FREDConstants.OUTCROP)) {
					if (feature.getPerson() != null) {
					%><tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Operating Company" : "Section Collector")%></td><td><%=feature.getPerson().getName()%></td></tr><%
					}
					if (feature.getStartDate() != null) {
						%><tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Spud Date" : "Sampling Start Date")%></td><td><%=FREDUtil.formatDateForOutput(feature.getStartDate(), feature.getStartDateRounding())%></td></tr><%
					}
					if (feature.getFinishDate() != null) {
						%><tr><td class="heading">Completion Date</td><td><%=FREDUtil.formatDateForOutput(feature.getFinishDate(), feature.getFinishDateRounding())%></td></tr><%
					}
					if (featType.equals(FREDConstants.DRILLHOLE) && feature.getDrillholeLicenceName() != null) {
						%><tr><td class="heading">Licence Area</td><td><%=feature.getDrillholeLicenceName()%></td></tr><%
					}
					if (feature.getDatumType() != null) {
						%><tr><td class="heading">Datum Type</td><td><%=feature.getDatumType()%></td></tr><%
					}
					if (feature.getDatumElevation() != null) {
						%><tr><td class="heading">Datum Elevation</td><td><%=FeatureUtil.formatDepthForOutput(feature.getDatumElevation(), feature.getDepthUnit())%> asl</td></tr><%
					}
					if (feature.getStartDepth() != null) {
						%><tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Kick-off Depth" : "Top Horizon")%></td><td><%=FeatureUtil.formatDepthForOutput(feature.getStartDepth(), feature.getDepthUnit())%></td></tr><%
					}
					if (feature.getFinishDepth() != null) {
						%><tr><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Termination Depth" : "Base Horizon")%></td><td><%=FeatureUtil.formatDepthForOutput(feature.getFinishDepth(), feature.getDepthUnit())%></td></tr><%
					}
				}
				if (feature.getComments() != null) {
					%><tr><td class="heading">Locality Comments</td><td><%=feature.getComments()%></td></tr><%
				}
			
				//Image/Files
				if (feature.getFeatureMetas().size() > 0) {
					%><tr><td colspan="2" class="heading">Images/Files</td></tr>
					<tr><td colspan="2"><table border="0" cellspacing="0" width="600"><%
					int y = 1;
					%><tr><%
					for (Meta meta : feature.getFeatureMetas()) {
						if (y++ == 5) {
							%></tr><tr><%
							y = 2;
						}
						%><td width="150" align="center" class="smalltext"><a href="/online/DigitalDocument?src=<%=meta.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=meta.getMetaId()%>" alt="FRED Digital Document" /><br /><%=FREDUtil.getMetaTitle(meta)%></a></td><%
					}
					%></td></tr></table></td></tr><%
				}

				%></table><%
				endDETable(pageContext);
				%></p><%

				//Sample
				if (sample != null) {
					if (sampleUtil.isAllowedReadSample(user, sample)) {
						//Sample Property Data
						%><p><%
						startDETable(pageContext);
						%><table border="0" width="550">
						<tr><td colspan="2" class="deHeading">Sample Information<%
						if (!featType.equals(FREDConstants.OUTCROP)) {
							//add PDF link
							%>&nbsp;&nbsp;&nbsp;<a href="frf/frf.pdf?SampIDs=<%=sample.getSampleId() + "&q=" + Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" /></a><%
						}
						%></td></tr><%
						if (sample.getFrNumber() != null && !sample.getFrNumber().equals(feature.getFrNumber())) {
							%><tr><td class="heading">Sample FR Number</td><td class="heading"><%=sample.getFrNumber().getFrNumber()%></td></tr><%
						}
						if (sample.getYardFrNumber() != null && !sample.getYardFrNumber().equals(feature.getYardFrNumber())) {
							%><tr><td class="heading">Sample Yard FR Number</td><td><%=sample.getYardFrNumber().getFrNumber()%></td></tr><%
						}
						if (!featType.equals(FREDConstants.OUTCROP)) {
							if (SampleUtil.getDrillHoleDepthDescription(sample) != null) {
								%><tr><td class="heading">Sample Depth</td><td><%=SampleUtil.getDrillHoleDepthDescription(sample)%></td></tr><%
							}
							//check for samples above and below current one
							Sample sampleAbove = SampleUtil.getSampleAbove(sample);
							if (sampleAbove != null && sampleUtil.isAllowedReadSample(user, sampleAbove)) {
								%><tr><td class="heading">Sample Above</td><td><a href="detail.jsp?ID=<%=sampleAbove.getSampleId() + backStr%>"><%=SampleUtil.getDrillHoleDepthDescription(sampleAbove)%></a></td></tr><%
							}
							Sample sampleBelow = SampleUtil.getSampleBelow(sample);
							if (sampleBelow != null && sampleUtil.isAllowedReadSample(user, sampleBelow)) {
								%><tr><td class="heading">Sample Below</td><td><a href="detail.jsp?ID=<%=sampleBelow.getSampleId() + backStr%>"><%=SampleUtil.getDrillHoleDepthDescription(sampleBelow)%></a></td></tr><%
							}
						}
						%><tr><td>&nbsp;</td></tr>
						
						<tr><td class="bigheading" colspan="2">Collection Information</td></tr><%
						Object[] collectors = sample.getCollectors().toArray();
						Arrays.sort(collectors);
						String[] collectorStr = new String[collectors.length];
						for (int i = 0; i < collectors.length; i++)
							collectorStr[i] = ((PersonRelationship) collectors[i]).getDisplayName();
						addRepeatingCells(new PrintWriter(out), "Collectors", collectorStr, false);
						if (sample.getCollectionDate() != null) {
							%><tr><td class="heading">Collection Date</td><td><%=FREDUtil.formatDateForOutput(sample.getCollectionDate(), sample.getDateRounding())%></td></tr><%
						}
						if (sample.getInPlace() != null) {
							%><tr><td class="heading">Fossils in Place</td><td><%=sample.getInPlace()%></td></tr><%
						}
						Object[] sentTos = sample.getSentTos().toArray();
						String[] sentToStr = new String[sentTos.length];
						for (int i = 0; i < sentTos.length; i++)
							sentToStr[i] = SampleUtil.getSentToDescription((SentTo) sentTos[i]);
						addRepeatingCells(new PrintWriter(out), "Sent To", sentToStr, true);
						if (sample.getNotCollected() != null) {
							%><tr><td class="heading">Not Collected</td><td><%=sample.getNotCollected()%></td></tr><%
						}
						if (sample.getSignificance() != null) {
							%><tr><td class="heading">Significance/Comments</td><td><%=sample.getSignificance()%></td></tr><%
						}
		
						%><tr><td>&nbsp;</td></tr>
		
						<tr><td class="bigheading" colspan="2">Stratigraphy</td></tr><%
						if (sample.getStratUnit() != null) {
							%><tr><td class="heading">Stratigraphic Name</td><td><%=sample.getStratUnit()%></td></tr><%
						}
						if (sample.getInferredStage() != null) {
							%><tr><td class="heading">Inferred Stage</td><td><%=StageUtil.getStageDescription(sample.getInferredStage())%></td></tr><%
						}
						if (sample.getKnownStage() != null) {
							%><tr><td class="heading">Known Stage</td><td><%=StageUtil.getStageDescription(sample.getKnownStage())%></td></tr><%
						}
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
						if (sample.getColumnMap() != null) {
							%><tr><td class="heading">Column/Map</td><td><%=sample.getColumnMap()%></td></tr><%
						}
						String dipStrike = SampleUtil.getDipStrikeDescription(sample);
						if (dipStrike != null && dipStrike.length() > 0) {
							%><tr><td class="heading">Dip/Strike</td><td><%=dipStrike%></td></tr><%
						}
						
						%><tr><td>&nbsp;</td></tr>
		
						<tr><td class="bigheading" colspan="2">Sedimentary Features</td></tr><%
						String grainSize = SampleUtil.getGrainSizeDescription(sample);
						if (grainSize != null && grainSize.length() > 0) {
							%><tr><td class="heading">Grain Size</td><td><%=grainSize%></td></tr><%
						}
						if (sample.getBedThickness() != null) {
							%><tr><td class="heading">Bedding Thickness</td><td><%=sample.getBedThickness().getName()%></td></tr><%
						}
						String bedDesc = SampleUtil.getBeddingDescription(sample);
						if (bedDesc != null && bedDesc.length() > 0) {
							%><tr><td class="heading">Bedding Features</td><td><%=bedDesc%></td></tr><%
						}
						if (sample.getWeathering() != null) {
							%><tr><td class="heading">Weathering</td><td><%=sample.getWeathering().getName()%></td></tr><%
						}
						if (sample.getHardness() != null) {
							%><tr><td class="heading">Hardness</td><td><%=sample.getHardness().getName()%></td></tr><%
						}
						if (sample.getCarbonate() != null) {
							%><tr><td class="heading">Carbonate</td><td><%=sample.getCarbonate().getName()%></td></tr><%
						}
						String colourDesc = SampleUtil.getColourDescription(sample);
						if (colourDesc != null && colourDesc.length() > 0) {
							%><tr><td class="heading">Colour</td><td><%=colourDesc%></td></tr><%
						}
						Object[] sedFeatures = sample.getSedimentaryFeatures().toArray();
						String[] sedFeaturesStr = new String[sedFeatures.length];
						for (int i = 0; i < sedFeatures.length; i++)
							sedFeaturesStr[i] = SampleUtil.getSedFeatureDescription((SedimentaryFeature) sedFeatures[i]);
						addRepeatingCells(new PrintWriter(out), "Additional Features", sedFeaturesStr, false);
						if (sample.getDepositionEnv() != null) {
							%><tr><td class="heading">Inferred Environment</td><td><%=sample.getDepositionEnv()%></td></tr><%
						}
						if (sample.getRockNature() != null) {
							%><tr><td class="heading">Nature of Rock Unit</td><td><%=sample.getRockNature()%></td></tr><%
						}
						if (sample.getCorrespondence() != null) {
							%><tr><td class="heading">Correspondence</td><td><%=sample.getCorrespondence()%></td></tr><%
						}
						
						//Image/Files
						if (sample.getSampleMetas().size() > 0) {
							%><tr><td colspan="2" class="heading">Images/Files</td></tr>
							<tr><td colspan="2"><table border="0" cellspacing="0" width="600"><%
							int y = 1;
							%><tr><%
							for (Meta meta : sample.getSampleMetas()) {
								if (y++ == 5) {
									%></tr><tr><%
									y = 2;
								}
								%><td width="150" align="center" class="smalltext"><a href="/online/DigitalDocument?src=<%=meta.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=meta.getMetaId()%>" alt="FRED Digital Document" /><br /><%=FREDUtil.getMetaTitle(meta)%></a></td><%
							}
							%></td></tr></table></td></tr><%
						}
		
						%></table><%
						endDETable(pageContext);
						%></p><%
						
						//Adoption
						for (Adoption adoRecord : sampleUtil.getAdoptionRecords(sample)) {
							if (recordUtil.isAllowedReadRecord(user, adoRecord.getRecord())) {
								%><p><%
								startDETable(pageContext);
								%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Adoption Information&nbsp;&nbsp;&nbsp;<a href="frf/frf.pdf?RecIDs=<%=adoRecord.getRecordId()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" /></td></tr><%
								Object[] adoptors = adoRecord.getAdopters().toArray();
								String[] adoptorsStr = new String[adoptors.length];
								for (int j = 0; j < adoptors.length; j++)
									adoptorsStr[j] = ((PersonRelationship) adoptors[j]).getDisplayName();
								addRepeatingCells(new PrintWriter(out), "Adoptors", adoptorsStr, false);
								if (adoRecord.getAdoptionDate() != null) {
									%><tr><td class="heading">Adoption Date</td><td><%=FREDUtil.formatDateForOutput(adoRecord.getAdoptionDate(), adoRecord.getDateRounding())%></td></tr><%
								}
								if (adoRecord.getStage() != null) {
									%><tr><td class="heading">Adopted Stage</td><td><%=StageUtil.getStageDescription(adoRecord.getStage())%></td></tr><%
								}
								if (adoRecord.getComments() != null) {
									%><tr><td class="heading">Comments</td><td><%=adoRecord.getComments()%></td></tr><%
								}
								
								//Image/Files
								if (adoRecord.getRecord().getRecordMetas().size() > 0) {
									%><tr><td colspan="2" class="heading">Images/Files</td></tr>
									<tr><td colspan="2"><table border="0" cellspacing="0" width="600"><%
									int y = 1;
									%><tr><%
									for (Meta meta : adoRecord.getRecord().getRecordMetas()) {
										if (y++ == 5) {
											%></tr><tr><%
											y = 2;
										}
										%><td width="150" align="center" class="smalltext"><a href="/online/DigitalDocument?src=<%=meta.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=meta.getMetaId()%>" alt="FRED Digital Document" /><br /><%=FREDUtil.getMetaTitle(meta)%></a></td><%
									}
									%></td></tr></table></td></tr><%
								}
								%></table><%
								endDETable(pageContext);
								%></p><%
							}
						}
			
						//Paleontology
						for (Paleontology palRecord : sampleUtil.getPaleontologyRecords(sample)) {
							if (recordUtil.isAllowedReadRecord(user, palRecord.getRecord())) {
								%><p><%
								startDETable(pageContext);
								%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Paleontology Information&nbsp;&nbsp;&nbsp;<a href="frf/frf.pdf?RecIDs=<%=palRecord.getRecordId()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" /></td></tr><%
								Object[] identifiers = palRecord.getIdentifiers().toArray();
								String[] identifiersStr = new String[identifiers.length];
								for (int j = 0; j < identifiers.length; j++)
									identifiersStr[j] = ((PersonRelationship) identifiers[j]).getDisplayName();
								addRepeatingCells(new PrintWriter(out), "Identifiers", identifiersStr, false);
								if (palRecord.getIdentificationDate() != null) {
									%><tr><td class="heading">Identification Date</td><td><%=FREDUtil.formatDateForOutput(palRecord.getIdentificationDate(), palRecord.getDateRounding())%></td></tr><%
								}
								if (palRecord.getStage() != null) {
									%><tr><td class="heading">Stage</td><td><%=StageUtil.getStageDescription(palRecord.getStage())%></td></tr><%
								}
								if (palRecord.getStageComments() != null) {
									%><tr><td class="heading">Stage Comments</td><td><%=palRecord.getStageComments()%></td></tr><%
								}
								if (palRecord.getLabNumber() != null) {
									%><tr><td class="heading">Lab Number</td><td><%=RecordUtil.getLabNumberDescription(palRecord)%></td></tr><%
								}
								if (palRecord.getCollectionComments() != null) {
									%><tr><td class="heading">Collection Comments</td><td><%=palRecord.getCollectionComments()%></td></tr><%
								}
				
								//taxa (Pal list)
								if (recordUtil.isAllowedReadPalList(user, palRecord) && palRecord.getListEntries() != null) {
									%><tr><td colspan="2"><table border="0" cellspacing="0" cellpadding="2"><%
									for (TaxonomicGroup taxaGroup : recordUtil.getTaxonomicGroups(palRecord)) {
										%><tr><td colspan="5" class="heading"><%=taxaGroup.getName()%></td></tr><%
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
											for (PaleontologyListEntry taxa : recordUtil.getListEntries(palRecord, taxaGroup)) {
												%><tr><td><i><%=taxa.getTaxonomicName()%></i>&nbsp;&nbsp;</td><%
												if (authorChk) {
													%><td><%=(taxa.getTaxon() != null) ? DBUtils.nvl(taxa.getTaxon().getAuthor()) : ""%>&nbsp;&nbsp;</td><%
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
												%><td><%
												for (Meta meta : taxa.getPalListMetas()) {
													%><a href="/online/DigitalDocument?src=<%=meta.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=meta.getMetaId()%>" alt="FRED Digital Document" /><br /><%=FREDUtil.getMetaTitle(meta)%></a><br /><%
												}
												%></td></tr><%
											}
										} else {
											%><tr><td colspan="4">No fossils listed</td></tr><%
										}
										%><tr><td>&nbsp;</td></tr><%
									}
									%></td></tr></table></td></tr><%
								}
								//Image/Files
								if (palRecord.getRecord().getRecordMetas().size() > 0) {
									%><tr><td colspan="2" class="heading">Images/Files</td></tr>
									<tr><td colspan="2"><table border="0" cellspacing="0" width="600"><%
									int y = 1;
									%><tr><%
									for (Meta meta : palRecord.getRecord().getRecordMetas()) {
										if (y++ == 5) {
											%></tr><tr><%
											y = 2;
										}
										%><td width="150" align="center" class="smalltext"><a href="/online/DigitalDocument?src=<%=meta.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=meta.getMetaId()%>" alt="FRED Digital Document" /><br /><%=FREDUtil.getMetaTitle(meta)%></a></td><%
									}
									%></td></tr></table></td></tr><%
								}
								%></table><%
								endDETable(pageContext);
								%></p><%
							}
						}
					}
				} else  {
					//Sample List
					%><p><%
					startDETable(pageContext);
					%><table border="0" width="550">
					<tr><td colspan="3" class="deHeading"><%=featType%> Samples</td></tr>
					<tr class="heading"><td>Sample</td></tr><%
					for (Sample locSample : FeatureUtil.getSortedSamples(feature)) {
						%><tr><td><a href="detail.jsp?ID=<%=locSample.getSampleId() + backStr%>"><%=SampleUtil.getDrillHoleDepthDescription(locSample) + ((locSample.getFrNumber() != null && !locSample.getFrNumber().equals(feature.getFrNumber())) ? " (" + locSample.getFrNumber().getFrNumber() + ")" : "")%></a>&nbsp;&nbsp;</td>
						<td><a href="frf/frf.pdf?SampIDs=<%=locSample.getSampleId() + "&q=" + Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" /></a></td></tr><%
					}
					%></table><%
					endDETable(pageContext);
					%></p><%
				}
			} else {
				//didn't pass isAllowedReadFeature()
				if (user ==  null) {
					%><tr><td>&nbsp;</td></tr>
					<tr><td colspan="2">More data may be available for this locality for <a href="login.jsp?loginpage=<%=URLEncoder.encode("/detail.jsp", "ISO-8859-1")%>" class="boldlink">logged</a> in users</td></tr><%
				}
				%></table><%
				endDETable(pageContext);
				%></p><%
			}
			%></td></tr></table><%
		} else {
			//didn't pass isAllowedReadFeatureSite()
			drawTop(out, et, request, response);
			%><table style="margin-left:20px; margin-top:20px; width:550px;" border="0">
			<tr><td>You do not have rights to view this sample</td></tr><%
			if (user == null) {
				%><tr><td colspan="2">You may be able to view it if you <a href="login.jsp?loginpage=<%=URLEncoder.encode("/detail.jsp", "ISO-8859-1")%>" class="boldlink">login</a></td></tr><%
			}
			%></table><%
		}
	} 
	else {
		 //no sampleID
		drawTop(out, et, request, response);
		%><table style="margin-left:20px; margin-top:20px; width:550px;" border="0">
		<tr><td>No Locality found</td></tr>
		</table><%
	}
	
	drawBottom(out, et);
} catch (Exception e) {
	e.printStackTrace();
}
	
try {
	HibernateUtil.get().getDAOFactory().closeSession();
} catch (Exception e) {
}
%>