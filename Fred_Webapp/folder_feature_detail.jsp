<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());

	ExtranetTemplate et = getExtranetTemplate();

	if (request.getParameter("FoldID") != null && request.getParameter("FeatID") != null) {
		int featID = Integer.parseInt(request.getParameter("FeatID"));

		drawTop(out, et, request, response);

		try {
			Folder folder = new Folder(Integer.parseInt(request.getParameter("FoldID")), user, state);

			String redirect = URLEncoder.encode("folder_feature_detail.jsp?FoldID=" + folder.getFolderID() + "&FeatID=" + featID, "UTF-8");
	
			//check feature is in this folder
			boolean ok = false;
			for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
				if (((Integer) i.next()).intValue() == featID) {
					ok = true;
					break;
				}
			}
	
			if (request.getParameter("ActionType") != null && ok) { //do something
				String actionType = request.getParameter("ActionType");
				String err = "";
				try {
					//Copy locality
					if (actionType.equals("CopyFeat") && folder.isAllowedCreateLocalities()) {
						FolderUtils.copyLocality(request.getParameter("FeatID"), request.getParameter("NewFeatName"), String.valueOf(folder.getFolderID()), user, state);
					}
					 //Delete feature
					else if (actionType.equals("DeleteFeat") && folder.isAllowedDeleteLocalities()) {
						FolderUtils.deleteLocality(request.getParameter("FeatID"), user, state);
					}
					 //Delete sample
					else if (actionType.equals("DeleteSamp") && folder.isAllowedDeleteLocalities()) {
						FolderUtils.deleteSample(request.getParameter("SampID"), user, state);
					}
					 //Delete record
					else if (actionType.equals("DeleteRec") && folder.isAllowedDeleteLocalities()) {
						FolderUtils.deleteRecord(request.getParameter("RecID"), user, state);
					}
					// submit working locality
					else if (actionType.equals("Submit") && folder.isAllowedSubmitLocalities()) {
						FolderUtils.submitLocality(request.getParameter("FeatID"), user, state);
					}
					else if (actionType.equals("SubmitSamp") && folder.isAllowedSubmitLocalities()) {
						FolderUtils.submitSample(request.getParameter("SampID"), user, state);
					}
					// submit working record
					else if (actionType.equals("SubmitRec") && folder.isAllowedSubmitLocalities()) {
						FolderUtils.submitRecord(request.getParameter("RecID"), user, state);
					}
					//Revoke waiting records
					else if (actionType.equals("Revoke") && folder.isAllowedSubmitLocalities()) {
						FolderUtils.revokeLocality(request.getParameter("FeatID"), user, state);
					}
				} catch (Exception e) {
					err = "&ErrMsg=" + URLEncoder.encode("An Error has occured: " + e.getMessage(), "UTF-8");
				}
				response.sendRedirect("folder_feature_detail.jsp?FoldID=" + folder.getFolderID() + "&FeatID=" + featID + err);
				return;
			}
	
			if (folder.isAllowedReadLocalities() && ok) {
				Feature feature = new Feature(featID, user, state, true);
				String featType = feature.getAsString(Feature.FEATURE_TYPE);
				String sampName = feature.getAsString(Feature.SAMPLE_NAMES);
				String featName = feature.getAsString(Feature.FEATURE_NAME);
				String locStatus = feature.getAsString(Feature.STATUS);
	
				out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
				out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
				out.println("<tr><td colspan='2' align='center' class='bigheading' >" + sampName + "</td></tr>");
				out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
				out.println("<tr><td><img src='images/blank.gif' widfth='1' height='10' /></td></tr>");
				out.println("<tr><td><a href='folder_detail.jsp?ID=" + folder.getFolderID() + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Folder Contents' /></a>&nbsp;&nbsp;</td><td><a href='folder_detail.jsp?ID=" + folder.getFolderID() + "' class='boldlink'>Back to Folder Contents</a></td></tr>");
				out.println("</table>");
	
				drawEndNavigation(out);
	
				out.println("<table style='margin-left:20px; width:550px;' border='0'>");
				out.println("<tr><td>");
	
				//List records
				out.println("<p><span class='heading'>Locality Details</span><br />");
				out.println("Listed below are the working records for this locality - adoption (blue) and paleontology (green).  Drillhole and Vertical Section localities will also have individual samples listed.</p>");

				//print error message (if any) from folder_actions
				if (request.getParameter("ErrMsg") != null) {
					out.println("<p><span class='heading' style='color: #FF0000'>" + request.getParameter("ErrMsg") + "</span></p>");
				}

				//Table header
				out.println("<p><table border='0' cellspacing='0' cellpadding='0' width='550'>");
				out.print("<tr>");
				out.print("<th colspan='2'>Name&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Last Change&nbsp;&nbsp;</th><th colspan='5'>Options</th></tr>");
				out.println("<tr><td colspan='9'><img src='images/line.gif' height='3' width='550' /></td></tr>");
				
				//Record list
				out.println("<form name='FoldForm' method='put' action='folder_feature_detail.jsp'>");
	
				//Feature
				out.println("<tr><td><a href='detail.jsp?FeatID=" + featID + "'><img src='images/loc.gif' border='0' height='20' width='20' alt='View Locality' /></a>&nbsp;</td>");
				out.print("<td class='heading'>" + sampName + "&nbsp;&nbsp;");
				if (featName != null && !sampName.equals(featName)) 
					out.print("<br />(" + featName +")&nbsp;&nbsp;");
				out.print("</td><td style='color: #FF0000'>");
				if (!locStatus.equals("approved")) {
					out.print(locStatus + "&nbsp;&nbsp;</td><td>");
					if (feature.get(Feature.LAST_CHANGE) != null)
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(feature.getAsDate(Feature.LAST_CHANGE)) + "&nbsp;&nbsp;");
					out.print("</td>");
				} else {
					out.print("</td><td></td>");
				}
				out.print("<td>");
				if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedEditLocalities())
					out.print("<a href='data_entry.jsp?Type=" + featType + "&FeatID=" + featID + "&FoldID=" + folder.getFolderID() + "&Redirect=" + redirect + "'><img src='images/edit.gif' border='0' height='20' width='20' alt='Edit Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				out.print("</td><td>");
	//			if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedDeleteLocalities())
	//				out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this locality\") == true) {document.FoldForm.ActionType.value=\"DeleteFeat\";document.FoldForm.submit();}'><img src='images/delete.gif' border='0' height='20' width='20' alt='Delete Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				out.print("</td><td>");
				if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedSubmitLocalities())
					out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to submit this locality\") == true) {document.FoldForm.ActionType.value=\"Submit\";document.FoldForm.submit();}'><img src='images/submit.gif' border='0' height='20' width='20' alt='Submit Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				if (locStatus.equals("waiting") && folder.isAllowedSubmitLocalities())
					out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to revoke this locality\") == true) {document.FoldForm.ActionType.value=\"Revoke\";document.FoldForm.submit();}'><img src='images/revoke.gif' border='0' height='20' width='20' alt='Revoke Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
				out.println("</td><td>");
				if (folder.isAllowedCreateLocalities()) {
					if (featType.equals("Outcrop")) {
						int sampID = ((Integer) feature.getAsVector(Feature.SAMPLES).firstElement()).intValue();
						Sample sample = new Sample(sampID, user, state);
						out.print("<a href='data_entry.jsp?Type=ADO&FoldID=" + folder.getFolderID() + "&SampID=" + sample.getSampleID() + "&Redirect=" + redirect + "'><img src='images/new_ado.gif' border='0' height='20' width='20' alt='Add Adoption Record' /></a><img src='images/blank.gif' height='20' width='2' />");
						out.print("</td><td>");
						out.print("<a href='data_entry.jsp?Type=PAL&FoldID=" + folder.getFolderID() + "&SampID=" + sample.getSampleID() + "&Redirect=" + redirect + "'><img src='images/new_pal.gif' border='0' height='20' width='20' alt='Add Paleontology Record' /></a>");
					} else {
						out.println("<a href='new_sample.jsp?FeatID=" + featID + "&FoldID=" + folder.getFolderID() + "'><img src='images/drill.gif' border='0' height='20' width='20' alt='New Sample' /></a><img src='images/blank.gif' height='20' width='2' />");
					}
				}
				out.println("</td></tr>");
				out.println("<tr><td colspan='11'><img src='images/line.gif' height='3' width='550' /></td></tr>");
				
					
				//Samples
				for (Iterator i = feature.getAsVector(Feature.SAMPLES).iterator(); i.hasNext(); ) {
					Sample sample = new Sample(((Integer) i.next()).intValue(), user, state, true);
					if (sample.getAsString(Sample.SAMPLE_STATUS).equals("approved") || (sample.get(Sample.SAMPLE_WORKING_FOLDER_ID) != null && sample.getAsInt(Sample.SAMPLE_WORKING_FOLDER_ID) == folder.getFolderID())) {
						if (!featType.equals("Outcrop") && !sample.getAsString(Sample.DRILLHOLE_DEPTH).equals("Depth Not Specified")) {
							out.print("<tr><td><a href='detail.jsp?ID=" + sample.getSampleID() + "'><img src='images/drill.gif' height='20' width='20' border='0' alt='View Sample Details' /></a>&nbsp;</td><td>" + sample.getAsString(Sample.DRILLHOLE_DEPTH) + "&nbsp;&nbsp;</td>");
							out.print("<td style='color: #FF0000'>");
							if (!sample.getAsString(Sample.SAMPLE_STATUS).equals("approved")) {
								out.print(sample.getAsString(Sample.SAMPLE_STATUS) + "&nbsp;&nbsp;");
								out.println("</td><td>");
								if (sample.get(Sample.SAMPLE_LAST_CHANGE) != null)
									out.print(DateFormat.getDateInstance(DateFormat.LONG).format(sample.getAsDate(Sample.SAMPLE_LAST_CHANGE)) + "&nbsp;&nbsp;");
							} else {
								out.println("</td><td>");
							}
							out.print("</td>");
							out.print("<td>");
							if ((sample.getAsString(Sample.SAMPLE_STATUS).equals("working") || sample.getAsString(Sample.SAMPLE_STATUS).equals("rejected")) && folder.isAllowedEditLocalities())
								out.print("<a href='data_entry.jsp?Type=Sample&FoldID=" + folder.getFolderID() + "&SampID=" + sample.getAsString(Sample.SAMPLE_ID) + "&Redirect=" + redirect + "'><img src='images/edit.gif' border='0' height='20' width='20' alt='Edit Sample Details' /></a><img src='images/blank.gif' height='20' width='2' />");
							out.print("</td><td>");
							if ((sample.getAsString(Sample.SAMPLE_STATUS).equals("working") || sample.getAsString(Sample.SAMPLE_STATUS).equals("rejected")) && folder.isAllowedDeleteLocalities())
								out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this sample\") == true) {document.FoldForm.ActionType.value=\"DeleteSamp\";document.FoldForm.SampID.value=\"" + sample.getSampleID() + "\";document.FoldForm.submit();}' title='Delete Sample'><img src='images/delete.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
							out.print("</td><td>");
							if (sample.getAsString(Sample.SAMPLE_STATUS).equals("working") && folder.isAllowedSubmitLocalities())
								out.println("<a href='#' onClick='document.FoldForm.ActionType.value=\"SubmitSamp\";document.FoldForm.SampID.value=\"" + sample.getSampleID() + "\";document.FoldForm.submit();'><img src='images/submit.gif' border='0' height='20' width='20' alt='Submit Sample' /></a><img src='images/blank.gif' height='20' width='2' />");
							out.println("</td><td>");
							if (folder.isAllowedCreateLocalities())
								out.print("<a href='data_entry.jsp?Type=ADO&FoldID=" + folder.getFolderID() + "&SampID=" + sample.getSampleID() + "&Redirect=" + redirect + "'><img src='images/new_ado.gif' border='0' height='20' width='20' alt='Add Adoption Record' /></a><img src='images/blank.gif' height='20' width='2' />");
							out.print("</td><td>");
							if (folder.isAllowedCreateLocalities())
								out.print("<a href='data_entry.jsp?Type=PAL&FoldID=" + folder.getFolderID() + "&SampID=" + sample.getSampleID() + "&Redirect=" + redirect + "'><img src='images/new_pal.gif' border='0' height='20' width='20'  /></a>");
							out.print("</td>");
							out.println("</tr>");
						}
	
						//Records
						for (Iterator k = sample.getAsVector(Sample.RECORDS).iterator(); k.hasNext(); ) {
							KeyValueObject kvo = (KeyValueObject) k.next();
							int recID = Integer.parseInt(kvo.getKey());
							String recType = kvo.getValue();
							try {
								Record record = Record.getData(recID, user, state);
								if (!recType.equals("SMP") && (record.get(Record.WORKING_FOLDER_ID) == null || record.getAsInt(Record.WORKING_FOLDER_ID) == (folder.getFolderID()))) {
									String imageName;
									if (recType.equals("ADO")) {
										imageName = "ado.gif";
									} else {
										imageName = "pal.gif";
										//check for provisional taxa
										//rs2 = statement2.executeQuery("SELECT * FROM Taxa_View WHERE Record_ID = " + rs.getString(1) + " AND Status = 'Provisional'");
										//if (rs2.next()) {
										//	provFlag = true;
										//}
									}
									out.print("<tr><td><img src='images/child.gif' width='20' height='20' /><img src='images/" + imageName + "' width='20' height='20' /></td><td class='smalltext'");
									//if (provFlag) { returnVal.append(" style='color: #FF0000'"); }
									out.print(">" + FREDUtils.noNulls(record.getAsString(Record.RECORD_NAME)) + "&nbsp;&nbsp;</td><td class='smalltext' style='color: #FF0000'>");
									if (record.getAsString(Record.STATUS).equals("working")) {
										out.print("working&nbsp;&nbsp;</td><td class='smalltext'>");
										if (record.get(Record.LAST_CHANGE) != null)
											out.print(DateFormat.getDateInstance(DateFormat.LONG).format(record.getAsDate(Record.LAST_CHANGE)) + "&nbsp;&nbsp;");
										out.print("</td>");
									} else {
										out.print("</td><td></td>");
									}
									out.print("<td>");
									//Record Options
									if (record.getAsString(Record.STATUS).equals("working") && folder.isAllowedEditLocalities())
										out.println("<a href='data_entry.jsp?Type=" + recType + "&FoldID=" + folder.getFolderID() + "&RecID=" + recID + "&Redirect=" + redirect + "'><img src='images/edit.gif' border='0' height='20' width='20' alt='Edit Record' /></a><img src='images/blank.gif' height='20' width='2' />");
									out.println("</td><td>");
									if (record.getAsString(Record.STATUS).equals("working") && folder.isAllowedDeleteLocalities())
										out.println("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this record\") == true) {document.FoldForm.ActionType.value=\"DeleteRec\";document.FoldForm.RecID.value=\"" + recID + "\";document.FoldForm.submit();}'><img src='images/delete.gif' border='0' height='20' width='20' alt='Delete Record' /></a><img src='images/blank.gif' height='20' width='2' />");
									out.println("</td><td>");
									if (record.getAsString(Record.STATUS).equals("working") && folder.isAllowedSubmitLocalities())
										out.println("<a href='#' onClick='document.FoldForm.ActionType.value=\"SubmitRec\";document.FoldForm.RecID.value=\"" + recID + "\";document.FoldForm.submit();'><img src='images/submit.gif' border='0' height='20' width='20' alt='Submit Record' /></a><img src='images/blank.gif' height='20' width='2' />");
									out.println("</td></tr>");
								}
							} catch (Exception e) {}
						}
						out.println("<tr><td colspan='9'><img src='images/line.gif' height='3' width='550' /></td></tr>");
					}
				}
	
				out.println("<input type='hidden' name='ActionType' value=''>");
				out.println("<input type='hidden' name='FoldID' value='" + folder.getFolderID() + "'>");
				out.println("<input type='hidden' name='FeatID' value='" + featID + "'>");
				out.println("<input type='hidden' name='SampID' value=''>");
				out.println("<input type='hidden' name='RecID' value=''>");
				out.println("<input type='hidden' name='NewFeatName' value=''>");
	
				out.println("</table></p>");
	
				out.println("</form>");
				out.println("</td></tr></table>");
			}
			else { //no folder found
				drawEndNavigation(out);
				out.println("<p><span class='bigheading'>Access Denied</span><br />");
				out.println("You don't have rights to edit this locality</p>");
			}
		} catch (Exception e) {
			drawEndNavigation(out);
			out.println("<p><span class='bigheading'>Access Denied</span><br />");
			out.println("You don't have rights to edit this locality</p>");
		}
	}
	else {
		drawTop(out, et, request, response);
		drawEndNavigation(out);
	}

	drawBottom(out, et);
%>