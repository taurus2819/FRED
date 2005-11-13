<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.*"
%><%@page import="nz.cri.gns.fred.data.AdoptionRecord"
%><%@page import="nz.cri.gns.fred.data.Audit"
%><%@page import="nz.cri.gns.fred.data.Feature"
%><%@page import="nz.cri.gns.fred.data.Folder"
%><%@page import="nz.cri.gns.fred.data.FolderList"
%><%@page import="nz.cri.gns.fred.data.FRNumber"
%><%@page import="nz.cri.gns.fred.data.Relationship"
%><%@page import="nz.cri.gns.fred.data.PaleontologyRecord"
%><%@page import="nz.cri.gns.fred.data.Record"
%><%@page import="nz.cri.gns.fred.data.Sample"
%><%@page import="nz.cri.gns.fred.data.SedFeature"
%><%@page import="nz.cri.gns.fred.data.SentTo"
%><%@page import="nz.cri.gns.fred.data.Taxa"
%><%@page import="nz.cri.gns.fred.data.TaxaGroup"
%><%@page import="nz.cri.gns.db.*"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="nz.cri.gns.db.metadata.*"
%><%@page import="nz.cri.gns.db.site.*"
%><%@page import="nz.cri.gns.util.map.*"
%><%@page import="java.net.*"
%><%@page import="java.text.*"
%><%@page import="java.util.*"
%><%@page import="nz.cri.gns.auth.*"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");
	String featType, featID;

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	if (request.getParameter("ID") != null) {
		featID = request.getParameter("ID");
		session.setAttribute("FeatureID", featID);
	} else {
		featID = (String) session.getAttribute("FeatureID");
	}

	drawTop(out, et, request, response);

	if (featID != null) {
		try {
			Feature feature = new Feature(Integer.parseInt(featID), user, state);
			
			if (request.getParameter("ActionType") != null) { //do something
				String actionType = request.getParameter("ActionType");
				if (actionType.equals("Approve")) {
					FRNumber frNum = new FRNumber(request.getParameter("MapSheet"), new Integer(request.getParameter("SerialNum")), request.getParameter("RecollNum"));
					FolderUtils.approveLocality(String.valueOf(feature.getFeatureID()), frNum, request.getParameter("CurComm"), user, state);
					feature = new Feature(feature.getFeatureID(), user, state, true);
					response.sendRedirect("admin_folder_detail.jsp?ID=" + feature.getAsString(Feature.MASTERFILE_ID));
					return;
				}
				else if (actionType.equals("Reject")) {
					FolderUtils.rejectLocality(String.valueOf(feature.getFeatureID()), request.getParameter("CurComm"), user, state);
					feature = new Feature(feature.getFeatureID(), user, state, true);
					response.sendRedirect("admin_folder_detail.jsp?ID=" + feature.getAsString(Feature.MASTERFILE_ID));
					return;
				}
				else if (actionType.equals("AddtoFold") && !request.getParameter("FoldID").equals("-")) {
					FolderUtils.addLocality(featID, request.getParameter("FoldID"), user, state);
					Folder folder = new Folder(Integer.parseInt(request.getParameter("FoldID")), user, state, true);
					out.println("<script language=\"JavaScript\">alert(\"Locality Added to " + folder.getAsString(Folder.NAME) + " folder\");</script>");
				}
			}
			
			if (feature.isUserAuthenticated() || feature.isApprovedLocality()) {
				Audit audit = Audit.getAudit(feature.getAsInt(Feature.AUDIT_ID), state);
				featType = feature.getAsString(Feature.FEATURE_TYPE);
		
				if (!featType.equals(Feature.OUTCROP_LOCALITY)) {
					if (feature.get(Feature.SAMPLES) != null) {
						int minSampID = ((Integer) feature.getAsVector(Feature.SAMPLES).firstElement()).intValue();
						Sample sample = new Sample(minSampID, user, state);			
	
						out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
						out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
						out.println("<tr><td colspan='2' align='center' class='bigheading' >" + feature.getAsString(Feature.FEATURE_NAME) + "</td></tr>");
						out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
						if (feature.get(Feature.MASTERFILE_NAME) != null) {
							out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + feature.getAsString(Feature.MASTERFILE_NAME) + "</td></tr>");
						}
						if (!audit.getAsString(Audit.STATUS).equals(Audit.STATUS_APPROVED)) {
							out.println("<tr><td class='smallheading'>Status:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + audit.getAsString(Audit.STATUS) + "</td></tr>");
						}
						if (audit.get(Audit.CREATED_BY) != null || audit.get(Audit.CREATED_DATE) != null) {
							out.println("<tr><td class='smallheading'>Created:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
							if (audit.get(Audit.CREATED_BY) != null) { out.print(audit.getAsString(Audit.CREATED_BY) + "<br />"); }
							if (audit.get(Audit.CREATED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.CREATED_DATE))); }
							out.println("</td></tr>");
						}
						if (audit.get(Audit.EDITED_BY) != null || audit.get(Audit.EDITED_DATE) != null) {
							out.println("<tr><td class='smallheading'>Edited:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
							if (audit.get(Audit.EDITED_BY) != null) { out.print(audit.getAsString(Audit.EDITED_BY) + "<br />"); }
							if (audit.get(Audit.EDITED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.EDITED_DATE))); }
							out.println("</td></tr>");
						}
						if (audit.get(Audit.SUBMITTED_BY) != null || audit.get(Audit.SUBMITTED_DATE) != null) {
							out.println("<tr><td class='smallheading'>Submitted:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
							if (audit.get(Audit.SUBMITTED_BY) != null) { out.print(audit.getAsString(Audit.SUBMITTED_BY) + "<br />"); }
							if (audit.get(Audit.SUBMITTED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.SUBMITTED_DATE))); }
							out.println("</td></tr>");
						}
						if (audit.get(Audit.APPROVED_BY) != null || audit.get(Audit.APPROVED_DATE) != null) {
							out.println("<tr><td class='smallheading'>Approved:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
							if (audit.get(Audit.APPROVED_BY) != null) { out.print(audit.getAsString(Audit.APPROVED_BY) + "<br />"); }
							if (audit.get(Audit.APPROVED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.APPROVED_DATE))); }
							out.println("</td></tr>");
						}
						
						if (user != null) {
							out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");

							out.println("<tr><td colspan='2'><table border='0'>");

							//Generate list of users folders
							FolderList folderList = new FolderList(user, state);
							if (folderList.getPersonalFolderCount() > 0) {
								out.println("<form name=\"FolderForm\" method=\"post\" action=\"drillhole_detail.jsp\">");
								out.println("<tr><td colspan=\"2\"><select name=\"FoldID\"><option value=\"-\">-- Choose --</option>");
								for (Iterator i = folderList.getPersonalFolders().iterator(); i.hasNext(); ) {
									KeyValueObject kv = (KeyValueObject) i.next();
									out.println("<option value=\"" + kv.getKey() + "\">" + ((kv.getValue().length() <= 17) ? kv.getValue() : kv.getValue().substring(0, 17)) + "</option>");
								}
								out.println("</select></td></tr>");
								out.println("<tr><td><a href=\"#\" onClick=\"FolderForm.submit();\"><img src=\"images/folder.gif\" height=\"20\" width=\"20\" border=\"0\" alt=\"Add to Folder\" /></a></td><td><a href=\"#\" onClick=\"FolderForm.submit();\" class=\"heading\">Add to Folder</a></td></tr>");
								out.println("<input type=\"hidden\" name=\"ID\" value=\"" + featID + "\" />");
								out.println("<input type=\"hidden\" name=\"ActionType\" value=\"AddtoFold\" />");
								out.println("</form>");
								out.println("<tr><td>&nbsp;</td></tr>");
							}

							out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
							out.println("<tr><td colspan='2'><a href='print_front.jsp?ID=" + minSampID + "&FormType=Short' target='print'><img src='images/print.gif' width='20' height='20' border='0' alt='Print' /></a>&nbsp;&nbsp;<a href='print_front.jsp?ID=" + minSampID + "&FormType=Short' class='heading' target='print'>Print Front</a></td></tr>");
							out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
							out.println("</table></td></tr>");
							
							if (FREDUtils.isAllowedApproveLocality(user, featID, feature.getAsString(Feature.STATUS), state)) {
								FRNumber frNumber = FolderUtils.getNextFRNumber(sample.getAsString(Sample.REG_AREA_CODE), sample.getAsString(Sample.NZMG_SHEET), sample.getAsDouble(Sample.LATITUDE), sample.getAsDouble(Sample.LONGITUDE), state);
								out.println("<tr><td colspan='2'>");
								out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
								out.println("<form name='RevForm' method='post' action='drillhole_detail.jsp'>");
								out.println("<input type='hidden' name='ID' value='" + featID + "'>");
								out.println("<input type='hidden' name='ActionType' value=''>");
								out.println("<tr><td colspan='2' class='heading' align='center'>Locality Approval</td></tr>");
								out.println("<tr><td><a href='#' onClick='document.RevForm.ActionType.value=\"Accept\";document.RevForm.submit();'><img src='images/ok.gif' width='20' height='20' border='0' alt='Approve' /></a>&nbsp;</td><td class='heading'>FR Number</td></tr>");
								//if (recoll != null) {
								//	out.println("<tr><td colspan='2'>The submitter has indicated that this record is a recollection of " + recoll + ".  If you agree then amend the FRNumber below as appropriate</td></tr>");
								//}
								out.println("<tr><td colspan='2'><input type='text' name='MapSheet' size='9' value='" + frNumber.getMapSheet() + "'>&nbsp;/f&nbsp;<input type='text' name='SerialNum' size='4' value='" + frNumber.getSerialNumber() + "'>&nbsp;<input type='text' name='RecollNum' size='1' value=''></td></tr>");
								out.println("<tr><td><img src='images/blank.gif' height='5' width='1' /></td></tr>");
								out.println("<tr><td><a href='#' onClick='document.RevForm.ActionType.value=\"Reject\";document.RevForm.submit();'><img src='images/cancel.gif' width='20' height='20' border='0' alt='reject' /></a>&nbsp;</td><td class='heading'>Comments</td></tr>");
								out.println("<tr><td colspan='2'><textarea name='RejComm' rows='5' cols='25'></textarea></td></tr>");
								out.println("</form>");
								out.println("</table>");
								out.println("</td></tr>");
							}
						}

						out.println("</table>");

						drawEndNavigation(out);
		
						out.println("<table style='margin-left:20px; width:550px;' border='0'>");
						out.println("<tr><td>");
	
						out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
						if (sample.get(Sample.YARD_FR_ID) != null) { out.println("<tr><td class='heading' width='135'>Yard FR Number&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.YARD_FR_NUMBER) + "</td></tr>"); }
						if (sample.get(Sample.LATITUDE) != null) {
							SiteRecord sr = SiteRecord.querySite(FREDUtils.getFREDConnection(state), sample.getAsInt(Sample.SITE_ID));			
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
								if (!datum.getName().equals("NZMG")) {
									try {
										Datum nzmgDatum = DatumFactory.createDatum("NZMG");
										Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
										out.println("<tr><td class='heading'>Grid Ref</td><td>" + nzmgDatum.getHumanStringFor(nzmgCoord) + "</td></tr>");
									} catch (Exception e) { System.out.println(e.getMessage()); }
								}
							}
							Datum.LatLong ll = sr.getLatLong();
							if (ll.getNorthSouth() != 999)
								out.println("<tr><td class='heading'>Lat/Long</td><td>" + ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)</td></tr>");
						}
						if (sample.get(Sample.METHOD) != null) { out.println("<tr><td class='heading'>Method</td><td>" + sample.getAsString(Sample.METHOD) + "</td></tr>"); }
						if (sample.get(Sample.ACCURACY) != null) { out.println("<tr><td class='heading'>Accuracy</td><td>&#177 " + sample.getAsDouble(Sample.ACCURACY) + "m</td></tr>"); }
						if (sample.isUserAuthenticated() && sample.get(Sample.LOCALITY) != null) { out.println("<tr><td class='heading'>Locality&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.LOCALITY) + "</td></tr>"); }
						if (sample.isUserAuthenticated() && sample.get(Sample.PERSON) != null) {
							out.print("<tr><td class='heading'>");
							if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
								out.print("Operating Company");
							} else {
								out.print("Section Collector");
							}
							out.println("&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.PERSON) + "</td></tr>");
						}
						if (sample.isUserAuthenticated() && sample.get(Sample.START_DATE) != null) {
							out.print("<tr><td class='heading'>");
							if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
								out.print("Spud Date");
							} else {
								out.print("Sampling Start Date");
							}
							out.println("&nbsp;&nbsp;</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.START_DATE), sample.getAsString(Sample.START_DATE_ROUNDING)) + "</td></tr>");
						}
						if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DATE) != null) {
							out.print("<tr><td class='heading'>Completion Date&nbsp;&nbsp;</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.FINISH_DATE), sample.getAsString(Sample.FINISH_DATE_ROUNDING)) + "</td></tr>");
						}
						if (featType.equals(Feature.DRILLHOLE_LOCALITY) && sample.isUserAuthenticated() && sample.get(Sample.DRILLHOLE_LICENCE_NAME) != null) { out.println("<tr><td class='heading'>Licence Area&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.DRILLHOLE_LICENCE_NAME) + "</td></tr>"); }
						if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_TYPE) != null) { out.println("<tr><td class='heading'>Datum Type&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.DATUM_TYPE) + "</td></tr>"); }
						if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_ELEVATION) != null) { out.println("<tr><td class='heading'>Datum Elevation&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.DATUM_ELEVATION) + " m asl</td></tr>"); }
						if (sample.isUserAuthenticated() && sample.get(Sample.START_DEPTH) != null) {
							out.print("<tr><td class='heading'>");
							if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
								out.print("Kick-off Depth");
							} else {
								out.print("Top Horizon");
							}
							out.println("&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.START_DEPTH) + " m</td></tr>");
						}
						if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DEPTH) != null) {
							out.print("<tr><td class='heading'>");
							if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
								out.print("Termination Depth");
							} else {
								out.print("Base Horizon");
							}
							out.println("&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.FINISH_DEPTH) + " m</td></tr>");
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
						out.println("</table></p>");
	
						if (feature.get(Feature.PETWELL_LINK) != null)
							out.println("<p>Click <a href='" + feature.getAsString(Feature.PETWELL_LINK) + "' class='boldlink' target='petwell'>here</a> to link to the GNS Petroleum Wells database</p>");
	
						out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
						if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
							out.println("<tr class='heading'><td>Locality Name&nbsp;&nbsp;</td><td>Sample Depth</td></tr>");
						} else {
							out.println("<tr class='heading'><td>Locality Name&nbsp;&nbsp;</td><td>Section Height</td></tr>");
						}
						for (Iterator i = feature.getAsVector(Feature.SAMPLES).iterator(); i.hasNext(); ) {
							Sample sampleList = new Sample(((Integer) i.next()).intValue(), user, state);
							out.println("<tr><td><a href='detail.jsp?ID=" + sampleList.getSampleID() + "'>" + sampleList.getAsString(Sample.SAMPLE_NAME) + "</a>&nbsp;&nbsp;</td><td><a href='detail.jsp?ID=" + sampleList.getSampleID() + "'>" + sampleList.getAsString(Sample.DRILLHOLE_DEPTH) + "</a></td></tr>");
						}
						out.println("</table></p>");
					}
					if (user ==  null)
						out.println("<p>More data may be available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/drillhole_detail.jsp") + "' class='boldlink'>logged</a> in users</p>");
				}
	
				else { // outcrop
					drawEndNavigation(out);
					out.println("<table style='margin-left:20px; width:550px;' border='0'>");
					out.println("<tr><td>");
					out.println("<p>The Feature ID entered is not a drillhole or vertical section locality  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
				}
			}	else { // not allowed to view
				drawEndNavigation(out);
				out.println("<table style='margin-left:20px; width:550px;' border='0'>");
				out.println("<tr><td>");
				out.println("<p>You have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
			}
		} catch (Exception e) {
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p>No matching locality found.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
		}
	} else { //ID not specified
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");
		out.println("<p>No Feature ID recieved.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>