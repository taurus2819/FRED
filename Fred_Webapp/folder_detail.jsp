<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.text.*, nz.cri.gns.auth.*"
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
%><%!	private String generateWorkRecords(String sampID, String foldID, int userRights, String locType, String locStatus, nz.cri.gns.intranet.DBConnection connection, int offset) throws java.sql.SQLException {
			String recType, imageName = "";
			StringBuffer returnVal;
			Statement statement = connection.getExtraStatement();
			Statement statement2 = connection.getExtraStatement();
			ResultSet rs, rs2;
			boolean provFlag = false;
			rs = statement.executeQuery("SELECT Record_ID, Last_Change, Record_Name, Record_Type FROM Record_All_View WHERE Sample_ID = " + sampID + " AND Working_Folder_ID = " + foldID);
			returnVal = new StringBuffer();
			while (rs.next()) {
				returnVal.append("<tr>");
				if (offset == 1) { returnVal.append("<td width='20'><img src='images/blank.gif' width='20' height='20' /></td>"); }
				recType = rs.getString(4);
				if (recType.equals("SMP")) {
					imageName = "sprop";
				}
				else if (recType.equals("ADO")) {
					imageName = "ado";
				}
				else if (recType.equals("PAL")) {
					imageName = "pal";
					 //check for provisional taxa
					rs2 = statement2.executeQuery("SELECT * FROM Taxa_View WHERE Record_ID = " + rs.getString(1) + " AND Status = 'Provisional'");
					if (rs2.next()) {
						provFlag = true;
					}
				}
				returnVal.append("<td width='20'><img src='images/child.gif' width='20' height='20' /></td><td width='20'><img src='images/" + imageName + ".gif' width='20' height='20' /></td><td colspan='" + (4 - offset) + "' class='smallheading'");
				if (provFlag) { returnVal.append(" style='color: #FF0000'"); }
				returnVal.append(">" + rs.getString(3) + "</td><td class='smalltext'>");
				if (rs.getString(2) != null) {
					returnVal.append(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(2)));
				}
				returnVal.append("&nbsp;</td><td>");

				//Record Options
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
				returnVal.append("</td></tr>\n");
			}
			statement.close();
			statement2.close();
			return returnVal.toString();
		}
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	Statement statement3 = connection.getExtraStatement();
	Statement statement4 = connection.getExtraStatement();
	ResultSet rs, rs2, rs3, rs4;
	User user = getUser(session);
	String foldID, featID, recID, featType, sampID, auditID, drillSampName, locStatus;
	int userID = user.getPersonId(), i = 0, j = 0, k = 0, tableWidth, recCount, execUp, userRights;
	boolean sampPropFlag;

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("ID") != null) {
		foldID = request.getParameter("ID");

		//print error message (if any) from folder_actions
		if (request.getParameter("ErrMsg") != null) {
			out.println(request.getParameter("ErrMsg"));
		}

		//get user rights
		rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID + " AND Folder_Type = 'personal'");
		if (rs.next()) {
			userRights = rs.getInt(1);
		} else { //no record
			userRights = 0;
		}

		rs = statement.executeQuery("SELECT Name FROM Folder WHERE Folder_ID = " + foldID);
		if ((userRights & 1) != 0 && rs.next()) {
			
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/folder.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' class='bigheading' align='center'>Folder: " + rs.getString(1) + "</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
			if ((userRights & 4) != 0) {
				out.println("<tr><td><a href='feat_data_entry.jsp?Type=Outcrop&FoldID=" + foldID + "' title='Add New Locality'><img src='images/new.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='feat_data_entry.jsp?Type=Outcrop&FoldID=" + foldID + "' class='heading'>New Outcrop Locality</a></td></tr>");
				out.println("<tr><td><a href='feat_data_entry.jsp?Type=Drillhole&FoldID=" + foldID + "' title='Add New Locality'><img src='images/new.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='feat_data_entry.jsp?Type=Drillhole&FoldID=" + foldID + "' class='heading'>New Drillhole Locality</a></td></tr>");
				out.println("<tr><td><a href='feat_data_entry.jsp?Type=VertSect&FoldID=" + foldID + "' title='Add New Locality'><img src='images/new.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='feat_data_entry.jsp?Type=VertSect&FoldID=" + foldID + "' class='heading'>New Vertical Section Locality</a></td></tr>");
				out.println("<tr><td><a href='simple_query.jsp?FoldID=" + foldID + "' title='Search for a Locality' title='Search for a Locality'><img src='images/search.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='simple_query.jsp?FoldID=" + foldID + "' class='heading'>Search</a></td></tr>");
			}
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			//List records
			out.println("<p>Listed below are the localities you have added to this folder (working localities are named with their field number or drillhole name until they are allocated a Fossil Record Number).  Listed below the localities are working records.  Click on the icons in the Options column to work with these localities.<br /><span class='smalltext'>Note (1):  Outcrop localities can not be submitted until a sample property record has been created (this record will be viewed by the Masterfile curator alongside the locality data).<br />Note (2):  Records can not be submitted until the parent locality has been approved.</span></p>");
			out.println("<p><span class='smalltext'><img src='images/loc.gif' height='20' width='20' /> = Locality<image src='images/blank.gif' height='20' width='10' />");
			out.println("<img src='images/drill.gif' height='20' width='20' /> = Drillhole Sample<image src='images/blank.gif' height='20' width='10' /><br />");
			out.println("<span class='smalltext'><img src='images/sprop.gif' height='20' width='20' /> = Sample Property record<image src='images/blank.gif' height='20' width='10' />");
			out.println("<img src='images/ado.gif' height='20' width='20' /> = Adoption record<image src='images/blank.gif' height='20' width='10' />");
			out.println("<img src='images/pal.gif' height='20' width='20' /> = Paleontology record</span></p>");

			//Table header
			out.println("<p><table border='0' cellspacing='0' cellpadding='1' width='550'>");
			out.print("<tr>");
			//out.print("<td></td>");
			out.print("<th colspan='4'>Name<img src='blank.gif' width='10' height='1' /></th><th>Type<src='images/blank.gif' width='10' height='1' /></th><th>Status<img src='images/blank.gif' width='10' height='1' /></th><th>Last Change<img src='images/blank.gif' width='10' height='1' /></th><th colspan='7'>Options</th></tr>");
			out.println("<tr><td colspan='14'><img src='images/line.gif' height='3' width='550' /></td></tr>");

			//Record list
			out.println("<form name='FoldForm' method='get' action='folder_actions.jsp'>");

			//Feature
			rs3 = statement3.executeQuery("SELECT DISTINCT Feature_ID, Sample_Name FROM Folder_Content_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID + " ORDER BY Sample_Name");
			featID = "";
			while (rs3.next()) {
				sampPropFlag = false;
				if (rs3.getString(1).equals(featID)) { continue; }
				featID = rs3.getString(1);
				rs = statement.executeQuery("SELECT Sample_ID, Sample_Name, Feature_Type, Feature_Name, Status, Last_Change FROM Sample_All_View WHERE Feature_ID = " + featID);
				rs.next();
				featType = rs.getString(3);
				if (featType.equals("Vertical Section")) { featType = "VertSect"; }
				locStatus = rs.getString(5);
				if (!featType.equals("Outcrop")) { //drillhole/vertsect so loop through individual samples
					drillSampName = "";
					rs2 = statement2.executeQuery("SELECT DISTINCT Sample_Name FROM Sample_All_View WHERE Feature_ID = " + featID + " ORDER BY Sample_Name");
					rs2.next();
					drillSampName = rs2.getString(1);
					while (rs2.next()) { drillSampName = drillSampName + ", " + rs2.getString(1); }
					out.print("<tr>");
					//out.print("<td><input type='checkbox' name='Check" + k++ + "' value='" + featID + "'></td>");
					out.print("<td width='20'><img src='images/loc.gif' height='20' width='20' /></td><td colspan='3' class='heading'><a href='detail.jsp?FeatID=" + featID + "'>" + drillSampName + "</a>&nbsp;&nbsp;");
					if (rs.getString(4) != null && !drillSampName.equals(rs.getString(4))) { out.print("<br /><a href='detail.jsp?FeatID=" + featID + "'>(" + rs.getString(4) +")</a>&nbsp;&nbsp;"); }
					out.print("</td><td>" + featType + "</td><td class='smallstar'>");
					if (!rs.getString(5).equals("approved")) {
						out.print(rs.getString(5) + "</td><td>");
						if (rs.getString(6) != null) { 
							out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(6)));
						}
						out.print("</td>");
					} else {
						out.print("</td><td></td>");
					}
					out.print("<td>");
					if ((rs.getString(5).equals("working") || rs.getString(5).equals("rejected")) && (userRights & 2) != 0) {
						out.print("<a href='feat_data_entry.jsp?Type=" + featType + "&FeatID=" + featID + "&FoldID=" + foldID + "' title='Edit Locality'><img src='images/edit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					out.print("</td><td>");
					if ((userRights & 4) != 0) {
						out.print("<a href='#' onClick='prmpt=prompt(\"Please enter the new name\", \"New " + featType + "\");if(prmpt!=null){document.FoldForm.NewFeatName.value=prmpt;document.FoldForm.ActionType.value=\"CopyFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Copy Locality'><img src='images/copy.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					out.print("</td><td>");
					if ((rs.getString(5).equals("working") || rs.getString(5).equals("rejected")) && (userRights & 8) != 0) {
						out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this locality\") == true) {document.FoldForm.ActionType.value=\"DeleteFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Delete Locality'><img src='images/delete.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					out.print("</td><td>");
					if ((rs.getString(5).equals("working") || rs.getString(5).equals("rejected")) && (userRights & 16) != 0) {
						out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to submit this locality\") == true) {document.FoldForm.ActionType.value=\"Submit\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Submit Locality'><img src='images/submit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					else if (rs.getString(5).equals("waiting") && (userRights & 16) != 0) {
						out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to revoke this locality\") == true) {document.FoldForm.ActionType.value=\"Revoke\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Revoke Locality'><img src='images/revoke.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					out.print("</td><td>");
					out.print("<a href='samp_select.jsp?FoldID=" + foldID + "&FeatID=" + featID + "&ReturnURL=samp_prop_data_entry.jsp' title='Add Sample Property Record'><img src='images/new_sprop.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					out.print("</td><td>");
					out.print("<a href='samp_select.jsp?FoldID=" + foldID + "&FeatID=" + featID + "&ReturnURL=ado_data_entry.jsp' title='Add Adoption Record'><img src='images/new_ado.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					out.print("</td><td>");
					out.print("<a href='samp_select.jsp?FoldID=" + foldID + "&FeatID=" + featID + "&ReturnURL=pal_data_entry.jsp' title='Add Paleontology Record'><img src='images/new_pal.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					out.print("</td></tr>");

					//Drillhole Samples
					//only list if contain working records
					rs = statement.executeQuery("SELECT DISTINCT S.Sample_ID, S.Drillhole_Depth, S.Top_Depth FROM Sample_All_View S, Record_All_View R WHERE R.Sample_ID = S.Sample_ID AND R.Status = 'working' AND S.Feature_ID = " + featID + " ORDER BY S.Top_Depth");
					while (rs.next()) {
						sampID = rs.getString(1);
						//check if sample property
						rs2 = statement2.executeQuery("SELECT * FROM Record_All_View WHERE Record_Type = 'SMP' AND Sample_ID = " + sampID); //check if sample property record already created
						if (rs2.next()) { sampPropFlag = true; }
						out.print("<tr>");
						//out.print("<td></td>");
						out.print("<td width='20'><img src='images/child.gif' width='20' height='20' /></td><td width='20'><img src='images/drill.gif' height='20' width='20' /></td><td colspan='5' class='heading'>" + rs.getString(2) + "</td><td>");
						out.print("</td><td></td><td></td><td></td><td>");
						if (!sampPropFlag) {
							out.print("<a href='samp_prop_data_entry.jsp?FoldID=" + foldID + "&SampID=" + sampID + "' title='Add Sample Property Record'><img src='images/new_sprop.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
						}
						out.print("</td><td>");
						out.print("<a href='ado_data_entry.jsp?FoldID=" + foldID + "&SampID=" + sampID + "' title='Add Adoption Record'><img src='images/new_ado.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
						out.print("</td><td>");
						out.print("<a href='pal_data_entry.jsp?FoldID=" + foldID + "&SampID=" + sampID + "' title='Add Paleontology Record'><img src='images/new_pal.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
						out.println("</td></tr>");
						out.println(generateWorkRecords(sampID, foldID, userRights, "Drill", locStatus, connection, 1));
					}
				}

				else { //not drillhole so go straight to records
					sampID = rs.getString(1);
					//check if sample property
					rs2 = statement2.executeQuery("SELECT * FROM Record_All_View WHERE Record_Type = 'SMP' AND Sample_ID = " + sampID); //check if sample property record already created
					if (rs2.next()) { sampPropFlag = true; }
					//check if working records
					rs2 = statement2.executeQuery("SELECT COUNT(*) FROM Record_All_View WHERE Sample_ID = " + sampID + " AND Working_Folder_ID = " + foldID);
					rs2.next();
					out.print("<tr>");
					//out.print("<td><input type='checkbox' name='Check" + k++ + "' value='" + featID + "'></td>");
					out.print("<td width='20'><img src='images/loc.gif' height='20' width='20' /></td><td colspan='3' class='heading'><a href='detail.jsp?ID=" + sampID + "'>" + rs.getString(2) + "</a>&nbsp;&nbsp;");
					if (rs.getString(4) != null && !rs.getString(2).equals(rs.getString(4))) { out.print("<br /><a href='detail.jsp?ID=" + sampID + "'>(" + rs.getString(4) + ")</a>&nbsp;&nbsp;"); }
					out.print("</td><td>Outcrop</td><td class='smallstar'>");
					if (!rs.getString(5).equals("approved")) {
						out.print(rs.getString(5) + "</td><td>");
						if (rs.getString(6) != null) { 
							out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(6)));
						}
						out.print("</td>");
					} else {
						out.print("</td><td></td>");
					}
					out.print("<td>");
					if ((rs.getString(5).equals("working") || rs.getString(5).equals("rejected")) && (userRights & 2) != 0) {
						out.print("<a href='feat_data_entry.jsp?Type=Outcrop&FeatID=" + featID + "&FoldID=" + foldID + " 'title='Edit Locality'><img src='images/edit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					out.print("</td><td>");
					if ((userRights & 4) != 0) {
						out.print("<a href='#' onClick='prmpt=prompt(\"Please enter the new fieldnumber\", \"New Outcrop\");if(prmpt!=null){document.FoldForm.NewFeatName.value=prmpt;document.FoldForm.ActionType.value=\"CopyFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Copy Locality'><img src='images/copy.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					out.print("</td><td>");
					if ((rs.getString(5).equals("working") || rs.getString(5).equals("rejected")) && (userRights & 8) != 0) {
						out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to delete this locality\") == true) {document.FoldForm.ActionType.value=\"DeleteFeat\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Delete Locality'><img src='images/delete.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					out.print("</td><td>");
					if ((rs.getString(5).equals("working") || rs.getString(5).equals("rejected")) && (userRights & 16) != 0 && sampPropFlag) {
						out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to submit this locality\") == true) {document.FoldForm.ActionType.value=\"Submit\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Submit Locality'><img src='images/submit.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					else if (rs.getString(5).equals("waiting") && (userRights & 16) != 0) {
						out.print("<a href='#' onClick='if (confirm(\"Are you sure you want to revoke this record\") == true) {document.FoldForm.ActionType.value=\"Revoke\";document.FoldForm.FeatID.value=\"" + featID + "\";document.FoldForm.submit();}' title='Revoke Locality'><img src='images/revoke.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					out.print("</td><td>");
					if (!sampPropFlag) {
						out.print("<a href='samp_prop_data_entry.jsp?FoldID=" + foldID + "&SampID=" + sampID + "' title='Add Sample Property Record'><img src='images/new_sprop.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					}
					out.print("</td><td>");
					out.print("<a href='ado_data_entry.jsp?FoldID=" + foldID + "&SampID=" + sampID + "' title='Add Adoption Record'><img src='images/new_ado.gif' border='0' height='20' width='20'></a><img src='images/blank.gif' height='20' width='2' />");
					out.print("</td><td>");
					out.print("<a href='pal_data_entry.jsp?FoldID=" + foldID + "&SampID=" + sampID + "' title='Add Paleontology Record'><img src='images/new_pal.gif' border='0' height='20' width='20'></a>");
					out.println("</td></tr>");
					if (rs2.getInt(1) > 0) { //working records
						out.println(generateWorkRecords(sampID, foldID, userRights, "Outcrop", locStatus, connection, 0));
					}
				}
				out.println("<tr><td colspan='14'><img src='images/line.gif' height='3' width='550' /></td></tr>");
			}
			out.println("<input type='hidden' name='ActionType' value=''>");
			out.println("<input type='hidden' name='ID' value='" + foldID + "'>");
			out.println("<input type='hidden' name='FeatID' value=''>");
			out.println("<input type='hidden' name='NewFoldID' value=''>");
			out.println("<input type='hidden' name='RecID' value=''>");
			out.println("<input type='hidden' name='RecType' value=''>");
			out.println("<input type='hidden' name='NewFeatName' value=''>");

			out.println("</table></p>");

/*			//folder options
			out.println("<table border='0' cellspacing='0' cellpadding = '2' width='600'><tr><td height='5'></td></tr><tr class='shadegreytr'><td>");
			//Copy
			//check for multiple user folders (and if found display move option)
			rs = statement.executeQuery("SELECT * FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type='personal' AND Folder_ID <> " + foldID);
			if (rs.next()) {
				out.println("&nbsp&nbsp<a href='#' onClick='if (document.FoldForm.CopyFoldID.value!=\"-\") {document.FoldForm.ActionType.value=\"CopyFold\";document.FoldForm.NewFoldID.value=document.FoldForm.CopyFoldID.value;document.FoldForm.submit();} else {alert(\"Please select a folder\");document.FoldForm.NewFoldID.focus();}' class='smallfname'>Copy&nbspSelected&nbspto</a>&nbsp");
				HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, "CopyFoldID", "-- Choose --", null, null, "Folder_View", "Folder_Name", "Folder_ID", null, "User_ID = " + userID + " AND Folder_Type = 'personal' AND Folder_ID <> " + foldID);
			}
			//Move
			if ((userRights & 8) != 0) {
				//check for multiple user folders (and if found display move option)
				rs = statement.executeQuery("SELECT * FROM Folder_View WHERE User_ID = " + userID + " AND Folder_Type='personal' AND Folder_ID <> " + foldID);
				if (rs.next()) {
					out.println("  &nbsp&nbsp<a href='#' onClick='if (document.FoldForm.MoveFoldID.value!=\"-\") {document.FoldForm.ActionType.value=\"MoveFold\";document.FoldForm.NewFoldID.value=document.FoldForm.MoveFoldID.value;document.FoldForm.submit();} else {alert(\"Please select a folder\");document.FoldForm.NewFoldID.focus();}' class='smallfname'>Move&nbspSelected&nbspto</a>&nbsp");
					HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, "MoveFoldID", "-- Choose --", null, null, "Folder_View", "Folder_Name", "Folder_ID", null, "User_ID = " + userID + " AND Folder_Type = 'personal' AND Folder_ID <> " + foldID);
				}
			}
			//Delete
			if ((userRights & 8) != 0) {
				out.println("  &nbsp&nbsp<a href='#' onClick='if (confirm(\"Are you sure you want to remove these records\") == true) {document.FoldForm.ActionType.value=\"Remove\";document.FoldForm.submit();}' class='smallfname'>Remove&nbspSelected</a>");
			}
			out.println("</table></p>");  */
			out.println("</form>");
		}
		else { //no folder found
			drawEndNavigation(out);
			out.println("<p><span class='heading'>No folder found</span></p>");
			out.println("<p>An incorrect parameter has been recieved by this page.  Please press the Back button and try again</p>");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);

	statement2.close();
	statement3.close();
	statement4.close();
%>
