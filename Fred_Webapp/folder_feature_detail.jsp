<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		try {
			return new Authenticable[] {
				 new IPRightAccess(
					new IPRight(
						"FRED data entry",
						getIPApp(
							request.getSession(),
							getServletConfig().getServletContext())),
					Right.ANY_RIGHT)};
		} catch (Exception e) {
			//Database error, so just block them
			return new Authenticable[] {
				 new IPRightAccess(
					IPRight.BLOCKED_IP_RIGHT,
					Right.BLOCKED_RIGHT)};
		}
	}
%><%
	User user = getUser(session);
	PageState state = new PageState(request, response, getServletContext());

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("FeatID") != null && request.getParameter("FeatID") != null) {
		String foldID = request.getParameter("FoldID");
		String featID = request.getParameter("FeatID");

/*		//print error message (if any) from folder_actions
		if (request.getParameter("ErrMsg") != null) {
			out.println(request.getParameter("ErrMsg"));
		}
*/

		Folder folder = new Folder(Integer.parseInt(foldID), user, state);

		if (folder.isAllowedReadLocalities()) {
		
			Feature feature = new Feature(Integer.parseInt(featID), user, state);
			String featType = feature.getAsString(Feature.FEATURE_TYPE);
			String sampName = feature.getAsString(Feature.SAMPLE_NAMES);
			if (featType.equals("Vertical Section")) { featType = "VertSect"; }
			String featName = feature.getAsString(Feature.FEATURE_NAME);
			String locStatus = feature.getAsString(Feature.STATUS);

			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='bigheading' >" + sampName + "</td></tr>");
			out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' widfth='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='folder_detail.jsp?ID=" + foldID + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Folder Detail'/></a>&nbsp;&nbsp;</td><td><a href='folder_detail.jsp?ID=" + foldID + "' class='boldlink'>Back to Folder Detail</a></td></tr>");
/*			if (folder.isAllowedCreateLocalities()) {
				out.println("<tr><td><a href='feat_data_entry.jsp?Type=Outcrop&FoldID=" + foldID + "' title='Add New Locality'><img src='images/new.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='feat_data_entry.jsp?Type=Outcrop&FoldID=" + foldID + "' class='heading'>New Outcrop Locality</a></td></tr>");
				out.println("<tr><td><a href='feat_data_entry.jsp?Type=Drillhole&FoldID=" + foldID + "' title='Add New Locality'><img src='images/new.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='feat_data_entry.jsp?Type=Drillhole&FoldID=" + foldID + "' class='heading'>New Drillhole Locality</a></td></tr>");
				out.println("<tr><td><a href='feat_data_entry.jsp?Type=VertSect&FoldID=" + foldID + "' title='Add New Locality'><img src='images/new.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='feat_data_entry.jsp?Type=VertSect&FoldID=" + foldID + "' class='heading'>New Vertical Section Locality</a></td></tr>");
				out.println("<tr><td><a href='simple_query.jsp?FoldID=" + foldID + "' title='Search for a Locality' title='Search for a Locality'><img src='images/search.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='simple_query.jsp?FoldID=" + foldID + "' class='heading'>Search</a></td></tr>");
			}
*/			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			//Table header
			out.println("<p><table border='1' cellspacing='0' cellpadding='1' width='550'>");
			out.print("<tr>");
			out.print("<th colspan='2'>Name&nbsp;&nbsp;</th><th>Status&nbsp;&nbsp;</th><th>Last Change&nbsp;&nbsp;</th><th colspan='7'>Options</th></tr>");

			//Record list
			out.println("<form name='FoldForm' method='get' action='folder_actions.jsp'>");

			//Feature
			
			out.println("<tr><td><img src='images/loc.gif' height='20' width='20' alt='' />&nbsp;</td>");
			out.print("<td class='heading'>" + sampName + "&nbsp;&nbsp;");
			if (featName != null && !sampName.equals(featName)) { out.print("<br />(" + featName +")&nbsp;&nbsp;"); }
			out.print("</td><td style='color: #FF0000'>");
			if (!locStatus.equals("approved")) {
				out.print(locStatus + "&nbsp;&nbsp;</td><td>");
				if (feature.get(Feature.LAST_CHANGE) != null) { 
					out.print(DateFormat.getDateInstance(DateFormat.LONG).format(feature.getAsDate(Feature.LAST_CHANGE)) + "&nbsp;&nbsp;");
				}
				out.print("</td>");
			} else {
				out.print("</td><td></td>");
			}
			out.print("<td><a href='detail.jsp?FeatID=" + featID + "'><img src='images/loc.gif' border='0' height='20' width='20' alt='View Locality' /></a><img src='images/blank.gif' height='20' width='2' /></td>");
			out.print("<td>");
			if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedEditLocalities())
				out.print("<a href='data_entry.jsp?Type=" + featType + "&FeatID=" + featID + "&FoldID=" + foldID + "' title='Edit Locality'><img src='images/edit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
			out.print("</td><td>");
			if (folder.isAllowedCreateLocalities())
				out.print("<a href='#' onClick='prmpt=prompt(\"Please enter the new name\", \"New " + featType + "\");if(prmpt!=null){document.FoldForm.NewFeatName.value=prmpt;document.FoldForm.ActionType.value=\"CopyFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Copy Locality'><img src='images/copy.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
			out.print("</td><td>");
			if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedDeleteLocalities()) {
				out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this locality\") == true) {document.FoldForm.ActionType.value=\"DeleteFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Delete Locality'><img src='images/delete.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
			out.print("</td><td>");
			if ((locStatus.equals("working") || locStatus.equals("rejected")) && folder.isAllowedSubmitLocalities()) {
				out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to submit this locality\") == true) {document.FoldForm.ActionType.value=\"Submit\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Submit Locality'><img src='images/submit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
			}
			else if (locStatus.equals("waiting") && folder.isAllowedSubmitLocalities()) {
				out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to revoke this locality\") == true) {document.FoldForm.ActionType.value=\"Revoke\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Revoke Locality'><img src='images/revoke.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
			}
			out.println("</td><td>");
			if (folder.isAllowedCreateLocalities()) {
				if (featType.equals("Outcrop")) {
					out.print("<a href='data_entry.jsp'><img src='images/new_ado.gif' border='0' height='20' width='20' alt='Add Adoption Record' /></a><img src='images/blank.gif' height='20' width='2' />");
					out.print("</td><td>");
					out.print("<a href='data_entry.jsp'><img src='images/new_pal.gif' border='0' height='20' width='20' alt='Add Paleontology Record' /></a><img src='images/blank.gif' height='20' width='2' />");
				} else {
					out.println("<a href='new_sample.jsp?FeatID=" + featID + "&FoldID=" + foldID + "'><img src='images/drill.gif' border='0' height='20' width='20' alt='New Sample' /></a><img src='images/blank.gif' height='20' width='2' />");
				}
			}
			out.println("</td></tr>");
			
				
			//Samples
			for (Iterator i = feature.getAsVector(Feature.SAMPLES).iterator(); i.hasNext(); ) {
				Sample sample = new Sample(((Integer) i.next()).intValue(), user, state);
				int sampID = sample.getSampleID();
				if (!featType.equals("Outcrop")) {
					out.print("<tr><td><img src='images/drill.gif' height='20' width='20' />&nbsp;</td><td colspan='3'>" + sample.getAsString(Sample.DRILLHOLE_DEPTH) + "</td>");
					out.print("<td></td><td>");
					if (folder.isAllowedEditLocalities())
						out.print("<a href='data_entry.jsp?Type=SMP&FoldID=" + foldID + "&SampID=" + sampID + "' title='Edit Sample Details'><img src='images/edit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					out.print("</td><td></td><td>");
					if (folder.isAllowedDeleteLocalities())
						out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this sample\") == true) {document.FoldForm.ActionType.value=\"DeleteSamp\";document.FoldForm.SampID.value=\"" + sample.getSampleID() + "\";document.FoldForm.submit();}' title='Delete Sample'><img src='images/delete.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					out.print("</td><td></td><td>");
					if (folder.isAllowedCreateLocalities())
						out.print("<a href='ado_data_entry.jsp?FoldID=" + foldID + "&SampID=" + sampID + "' title='Add Adoption Record'><img src='images/new_ado.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					out.print("</td><td>");
					if (folder.isAllowedCreateLocalities())
						out.print("<a href='pal_data_entry.jsp?FoldID=" + foldID + "&SampID=" + sampID + "' title='Add Paleontology Record'><img src='images/new_pal.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					out.print("</td>");
					out.println("</tr>");
				}
			}
/*						for (Iterator k = sample.getAsVector(Sample.WORKING_RECORDS).iterator(); k.hasNext(); ) {
							recordHeader = (KeyValueObject) k.next(); 
							record = Record.getData(Integer.parseInt(recordHeader.getKey()), user, state);
							if (record.getAsString(Record.WORKING_FOLDER_ID).equals(foldID)) {
								recType = recordHeader.getValue();
								if (recType.equals("SMP")) {
									imageName = "sprop";
								}
								else if (recType.equals("ADO")) {
									imageName = "ado";
								}
								else { //PAL
									imageName = "pal";
									//check for provisional taxa
									//rs2 = statement2.executeQuery("SELECT * FROM Taxa_View WHERE Record_ID = " + rs.getString(1) + " AND Status = 'Provisional'");
									//if (rs2.next()) {
									//	provFlag = true;
									//}
								}
								
								out.print("<tr><td><img src='images/child.gif' width='20' height='20' /><img src='images/" + imageName + ".gif' width='20' height='20' /></td><td colspan='3' class='smallheading'");
								//if (provFlag) { returnVal.append(" style='color: #FF0000'"); }
								out.print(">" + record.getAsString(Record.RECORD_NAME) + "</td><td class='smalltext'>");
								if (record.get(Record.LAST_CHANGE) != null) {
									out.print(DateFormat.getDateInstance(DateFormat.LONG).format(record.getAsDate(Record.LAST_CHANGE)));
								}
								out.print("&nbsp;</td><td>");
*/	 /*							//Record Options
								if (recType.equals("SMP") && (userRights & 2) != 0) {
									returnVal.append("<a href='samp_prop_data_entry.jsp?RecID=" + rs.getString(1) + "&FoldID=" + foldID + "' title='Edit Record'><img src='images/edit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
								}
								else if (recType.equals("ADO") && (userRights & 2) != 0) {
									returnVal.append("<a href='ado_data_entry.jsp?RecID=" + rs.getString(1) + "&FoldID=" + foldID + "' title='Edit Record'><img src='images/edit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
								}
								else if (recType.equals("PAL") && (userRights & 2) != 0) {
									returnVal.append("<a href='pal_data_entry.jsp?RecID=" + rs.getString(1) + "&FoldID=" + foldID + "' title='Edit Record'><img src='images/edit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
								}
								returnVal.append("</td><td></td><td>");
								if ((userRights & 8) != 0) {
									returnVal.append("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this record\") == true) {document.FoldForm.ActionType.value=\"DeleteRec\";document.FoldForm.RecID.value=\"" + rs.getString(1) + "\";document.FoldForm.submit();}' title='Delete Record'><img src='images/delete.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
								}
								returnVal.append("</td><td>");
								if ((locType.equals("Drill") || recType.equals("ADO") || (recType.equals("PAL")) && !provFlag) && (userRights & 16) != 0 && locStatus.equals("approved")) {
									returnVal.append("<a href='#' onClick='document.FoldForm.ActionType.value=\"SubmitRec\";document.FoldForm.RecID.value=\"" + rs.getString(1) + "\";document.FoldForm.RecType.value=\"" + rs.getString(4) + "\";document.FoldForm.submit();' title='Submit Record'><img src='images/submit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
								}
	*/			/*				out.println("</td></tr>");
							}
						}
*/

			out.println("<input type='hidden' name='ActionType' value=''>");
			out.println("<input type='hidden' name='ID' value='" + foldID + "'>");
			out.println("<input type='hidden' name='FeatID' value=''>");
			out.println("<input type='hidden' name='NewFoldID' value=''>");
			out.println("<input type='hidden' name='RecID' value=''>");
			out.println("<input type='hidden' name='RecType' value=''>");
			out.println("<input type='hidden' name='NewFeatName' value=''>");

			out.println("</table></p>");

			out.println("</form>");
			}
		}
		else { //no folder found
			drawEndNavigation(out);
			out.println("<p><span class='heading'>No folder found</span></p>");
			out.println("<p>An incorrect parameter has been recieved by this page.  Please press the Back button and try again</p>");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>