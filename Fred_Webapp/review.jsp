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
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	Statement statement3 = connection.getExtraStatement();
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");
	DecimalFormat latdeg = new DecimalFormat("00");
	DecimalFormat longdeg = new DecimalFormat("000");
	SimpleDateFormat yearFormatter = new SimpleDateFormat ("yyyy");
	SimpleDateFormat monthFormatter = new SimpleDateFormat ("MMM yyyy");
	User user = getUser(session);
	ResultSet rs, rs2, rs3;
	String featID, sampID = "0", recID, auditID, frID, foldID = "", mapSheet, workComm, recoll = null, recollNum = "";
	int i = 1, userID = user.getPersonId(), userRights = 0, execUp, serialNum;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	if (request.getParameter("ID") != null) {
		featID = request.getParameter("ID");

		//check if user can view this record and that record exists
		rs = statement.executeQuery("SELECT User_Rights, Folder_ID FROM Masterfile_Content_View WHERE Feature_ID = " + featID + " AND User_ID = " + userID);
		if (rs.next()) {
			userRights = rs.getInt(1);
			foldID = rs.getString(2);
		}

		if ((userRights & 64) != 0) { //allowed to re-view this record

			if (request.getParameter("ActionType") != null) { //do something
				String actionType = request.getParameter("ActionType");
				if (actionType.equals("Accept")) {
					//generate FR number record
					rs = statement.executeQuery("SELECT FR_Seq.NEXTVAL FROM DUAL");
					rs.next();
					frID = rs.getString(1);
					execUp = statement.executeUpdate("INSERT INTO FR_Number (FR_ID, Map_Sheet, Serial_Number, Recollection_Number) VALUES (" + frID + ", '" + request.getParameter("MapSheet") + "', " + request.getParameter("SerialNum") + ", " + JspUtils.sqlEscape(request.getParameter("RecollNum")) + ")");
					execUp = statement.executeUpdate("UPDATE Sample SET FR_ID = " + frID + " WHERE Feature_ID = " + featID);
					//explicitly add to folders
					execUp = statement.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) SELECT DISTINCT Folder_ID, " + featID + " FROM Folder_Content_View WHERE Feature_ID = " + featID + " AND Folder_Type <> 'admin' AND Folder_ID NOT IN (SELECT Folder_ID FROM Folder_Content WHERE Feature_ID = " + featID + ")");
					//update audit table
					rs = statement.executeQuery("SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featID);
					rs.next();
					auditID = rs.getString(1);
					execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'approved', Approved_By_ID = " + userID + ", Approved_Date = SYSDATE, Working_Folder_ID = NULL, Working_Comments = NULL, Curator_Comments = NULL WHERE Audit_ID = " + auditID);
					response.sendRedirect("admin_folder_detail.jsp?ID=" + foldID + "&PrintID=" + featID);
				}
				else if (actionType.equals("Reject")) {
					//update audit table
					rs = statement.executeQuery("SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featID);
					rs.next();
					auditID = rs.getString(1);
					execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'rejected', Curator_Comments = " + JspUtils.sqlEscape(request.getParameter("RejComm")) + " WHERE Audit_ID = " + auditID);
					response.sendRedirect("admin_folder_detail.jsp?ID=" + foldID);
				}
			}

			out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
			rs = statement.executeQuery("SELECT DISTINCT S.Sample_Name, S.Feature_Type, S.Masterfile_Name, A.Created_By, A.Created_Date, A.Modified_By, A.Modified_Date, A.Submitted_By, A.Submitted_Date, A.Working_Comments FROM Sample_All_View S, Audit_View A WHERE S.Audit_ID = A.Audit_ID AND S.Feature_ID = " + featID);
			rs.next();
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='bigheading' >" + rs.getString(1) + "</td></tr>");
			out.println("<tr><td colspan='2' align='center'>" + rs.getString(2) + "</td></tr>");
			if (rs.getString(3) != null) {
				out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + rs.getString(3) + "</td></tr>");
			}
			if (rs.getString(4) != null || rs.getString(5) != null) {
				out.println("<tr><td class='smallheading'>Created:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (rs.getString(4) != null) { out.print(rs.getString(4) + "<br />"); }
				if (rs.getString(5) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(5))); }
				out.println("</td></tr>");
			}
			if (rs.getString(6) != null || rs.getString(7) != null) {
				out.println("<tr><td class='smallheading'>Edited:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (rs.getString(6) != null) { out.print(rs.getString(6) + "<br />"); }
				if (rs.getString(7) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(7))); }
				out.println("</td></tr>");
			}
			if (rs.getString(8) != null || rs.getString(9) != null) {
				out.println("<tr><td class='smallheading'>Submitted:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (rs.getString(8) != null) { out.print(rs.getString(8) + "<br />"); }
				if (rs.getString(9) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(9))); }
				out.println("</td></tr>");
			}

			workComm = noNulls(rs.getString(10));
			if (workComm.indexOf("*Recoll:") >= 0) {
				recoll = workComm.substring(8, workComm.indexOf("*", 2)).trim();
				workComm = workComm.substring(workComm.indexOf("*", 2) + 1, workComm.length()).trim();
			}

			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("</table>");

			out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
			out.println("<tr><td><a href='detail.jsp?FeatID=" + featID + "' title='View Entire Record' target='_blank'><img src='images/loc.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='detail.jsp?FeatID=" + featID + "' class='heading' target='_blank'>View Entire Record</a></td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("</table>");

			//Generate new FRNumber
			rs = statement.executeQuery("SELECT L.Code, S.NZMG_Sheet, S.Latitude, S.Longitude FROM Sample_All_View S, Lookup L WHERE S.Reg_Area_ID = L.Lookup_ID(+) AND S.Feature_ID = " + featID);
			if (rs.next()) {
				if (rs.getString(2) != null) {
					rs2 = statement2.executeQuery("SELECT COUNT(*) FROM SC.Map_Sheet WHERE MS_Series = 'NZMS260' AND MS_Map_Code = '" + rs.getString(2) + "'");
					if (rs2.next()) {
						mapSheet = rs.getString(2);
					} else {
						if (rs.getInt(3) >= 0) {
							mapSheet = "N";
						} else {
							mapSheet = "S";
						}
						if (rs.getInt(4) >= 0) {
							mapSheet = mapSheet + "E";
						} else {
							mapSheet = mapSheet + "W";
						}
					mapSheet = mapSheet + latdeg.format(Math.abs(rs.getDouble(3))) + longdeg.format(Math.abs(rs.getDouble(4)));
					}
				}
				else if (rs.getString(1) != null && !rs.getString(1).equals("NZ") && !rs.getString(1).equals("OT")) {
					mapSheet = rs.getString(1);
				}
				else {
					if (rs.getInt(3) >= 0) {
						mapSheet = "N";
					} else {
						mapSheet = "S";
					}
					if (rs.getInt(4) >= 0) {
						mapSheet = mapSheet + "E";
					} else {
						mapSheet = mapSheet + "W";
					}
					mapSheet = mapSheet + latdeg.format(Math.abs(rs.getDouble(3))) + longdeg.format(Math.abs(rs.getDouble(4)));
				}
				rs = statement.executeQuery("SELECT MAX(Serial_Number) FROM FR_Number WHERE Map_Sheet = '" + mapSheet + "' AND Serial_Number < 6000");
				rs.next();
				serialNum = rs.getInt(1) + 1;
				out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
				out.println("<form name='RevForm' method='get' action='review.jsp'>");
				out.println("<input type='hidden' name='ID' value='" + featID + "'>");
				out.println("<input type='hidden' name='ActionType' value=''>");
				out.println("<tr><td><a href='#' onClick='document.RevForm.ActionType.value=\"Accept\";document.RevForm.submit();' title='Approve'><img src='images/ok.gif' width='20' height='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' /></td><td class='heading'>FR Number</td></tr>");
				if (recoll != null) {
					out.println("<tr><td colspan='2'>The submitter has indicated that this record is a recollection of " + recoll + ".  If you agree then amend the FRNumber below as appropriate</td></tr>");
				}
				out.println("<tr><td colspan='2'><input type='text' name='MapSheet' size='9' value='" + mapSheet + "'>&nbsp;/f&nbsp;<input type='text' name='SerialNum' size='4' value='" + serialNum + "'>&nbsp;<input type='text' name='RecollNum' size='1' value='" + noNulls(recollNum) + "'></td></tr>");
				out.println("<tr><td><img src='images/blank.gif' height='5' width='1' /></td></tr>");
				out.println("<tr><td><a href='#' onClick='document.RevForm.ActionType.value=\"Reject\";document.RevForm.submit();' title='reject'><img src='images/cancel.gif' width='20' height='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' /></td><td class='heading'>Comments to Submitter</td></tr>");
				out.println("<tr><td colspan='2'><textarea name='RejComm' rows='5' cols='25'></textarea></td></tr>");
				out.println("</form></table>");
			}

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			out.println("<p><table border='0' cellspacing='0' width='500'>");
			rs = statement.executeQuery("SELECT Feature_ID, Feature_Type, Feature_Name, NZMG_Sheet, NZMG_East, NZMG_North, Latitude, Longitude, Accuracy, Method, Locality FROM Sample_All_View S WHERE Feature_ID = " + featID);
			rs.next();
			if (rs.getString(7) != null) {
				out.print("<tr><td class='heading'>Grid Ref</td><td>");
				if (rs.getString(4) != null) {
					out.print(rs.getString(4) + ": " + nzmg.format(rs.getDouble(5)) + ", " + nzmg.format(rs.getDouble(6)));
					out.print("<img src='images/blank.gif' width='20' height='1' />|<img src='images/blank.gif' width='20' height='1' />");
				}
				if (rs.getDouble(7) > 0) {
					out.print(latlong.format(rs.getDouble(7)) + "&#176N");
				} else {
					out.print(latlong.format(Math.abs(rs.getDouble(7))) + "&#176S");
				}
				out.println("/");
				if (rs.getDouble(8) > 0) {
					out.print(latlong.format(rs.getDouble(8)) + "&#176E");
				} else {
					out.print(latlong.format(Math.abs(rs.getDouble(8))) + "&#176W");
				}
				if (rs.getString(9) != null) { out.print(" (&#177 " + rs.getString(9) + "m)"); }
				out.println("</td></tr>");
			}
			if (rs.getString(10) != null) { out.println("<tr><td class='heading' width='135'>Method</td><td>" + rs.getString(10) + "</td></tr>"); }
			if (rs.getString(3) != null) {
				if (rs.getString(2).equals("Outcrop")) {
					out.println("<tr><td class='heading'>Field Number</td><td>" + (rs.getString(3)) + "</td></tr>");
				} else if (rs.getString(2).equals("Drillhole")) {
					out.println("<tr><td class='heading'>Drillhole Name</td><td><a href='drillhole_detail.jsp?ID=" + rs.getString(1) + "'>" + rs.getString(3) + "</a></td></tr>");
				} else {
					out.println("<tr><td class='heading'>Section Name</td><td><a href='drillhole_detail.jsp?ID=" + rs.getString(1) + "'>" + rs.getString(3) + "</a></td></tr>");
				}
			}
			if (rs.getString(11) != null) { out.println("<tr><td class='heading' width='135'>Locality</td><td>" + rs.getString(11) + "</td></tr>"); }
			if (!workComm.equals("")) { out.println("<tr><td class='heading' width='135'>Working Comments</td><td>" + workComm + "</td></tr>"); }
			out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");

			if (rs.getString(2).equals("Outcrop")) { //outcrop locality so show sample property record as well
				rs = statement.executeQuery("SELECT Record_ID, Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage, Known_Stage, Column_Map, Dip, Dip_Direction, Strike, Facing, Grainsize, Comparator_Used, Bed_Thickness, Bedding, Weathering, Hardness, Carbonate, Colour, Deposition_Env, Rock_Nature, Correspondence, Record_ID FROM Sample_Property_All_View WHERE Feature_ID = " + featID);
				if (rs.next()) {
					recID = rs.getString(1);

					out.println("<tr><td class='bigheading' colspan='2'>Sample Property Data</td></tr>");

					if (rs.getString(2) != null) {
						out.print("<tr><td class='heading'>Collection Date</td><td>");
						if (rs.getString(3) == null) {
							out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(2)));
						} else if (rs.getString(3).equals("Year")) {
							out.print(yearFormatter.format(rs.getDate(2)));
						} else if (rs.getString(3).equals("Month")) {
							out.print(monthFormatter.format(rs.getDate(2)));
						}
						out.println("</td></tr>");
					}
					//collectors (repeating)
					rs2 = statement2.executeQuery("SELECT COUNT(*) FROM Collector WHERE Record_ID = " + recID);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Collectors</td>");
						rs2 = statement2.executeQuery("SELECT Name FROM Person_View P, Collector C WHERE P.Person_ID = C.Person_ID AND C.Record_ID = " + recID + " ORDER BY Name");
						rs2.next();
						out.println("<td>" + rs2.getString(1) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td>" + rs2.getString(1) + "</td></tr>");
						}
					}
					if (rs.getString(4) != null) { out.println("<tr><td class='heading'>Strat Name</td><td>" + rs.getString(4) + "</td></tr>"); }
					if (rs.getString(5) != null) { out.println("<tr><td class='heading'>In Place</td><td>" + rs.getString(5) + "</td></tr>"); }
					//sent to (repeating)
					rs2 = statement2.executeQuery("SELECT COUNT(*) FROM Sent_To WHERE Record_ID = " + recID);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Sent To</td>");
						rs2 = statement2.executeQuery("SELECT Sent_To FROM Sent_To_View WHERE Record_ID = " + recID + " ORDER BY Sent_To");
						rs2.next();
						out.print("<td>" + rs2.getString(1) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td>" + rs2.getString(1) + "</td></tr>");
						}
					}
					if (rs.getString(6) != null) { out.println("<tr><td class='heading'>Not Collected</td><td>" + rs.getString(6) + "</td></tr>"); }

					//Stratigraphy
					if (rs.getString(7) != null) { out.println("<tr><td class='heading'>Significance</td><td>" + rs.getString(7) + "</td></tr>"); }
					if (rs.getString(8) != null) { out.println("<tr><td class='heading'>Inferred Stage</td><td>" + rs.getString(8) + "</td></tr>"); }
					if (rs.getString(9) != null) { out.println("<tr><td class='heading'>Known Stage</td><td>" + rs.getString(9) + "</td></tr>"); }
					//Nearby samples (repeating)
					rs2 = statement2.executeQuery("SELECT COUNT(*) FROM Relationship WHERE Relationship_Type = 'Sample' AND Relation_Type_ID = 231 AND Record_ID = " + recID);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Samples Nearby</td>");
						rs2 = statement2.executeQuery("SELECT Related_Feature_ID, Related_Sample_Name FROM Relationship_View WHERE Relationship_Type = 'Sample' AND Relation_Type_ID = 231 AND Record_ID = " + recID + " ORDER BY Related_Sample_Name");
						rs2.next();
						out.print("<td><a href='detail.jsp?FeatID=" + rs2.getString(1) + "'>" + rs2.getString(2) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td><a href='detail.jsp?FeatID=" + rs2.getString(1) + "'>" + rs2.getString(2) + "</a></td></tr>");
						}
					}
					//Sample relationships (repeating)
					rs2 = statement2.executeQuery("SELECT COUNT(*) FROM Relationship WHERE Relation_Type_ID <> 231 AND Relationship_Type = 'Sample' AND Record_ID = " + recID);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Sample Relationships</td>");
						rs2 = statement2.executeQuery("SELECT Distance_Relation, Related_Feature_ID, Related_Sample_Name FROM Relationship_View WHERE Relation_Type_ID <> 231 AND Relationship_Type = 'Sample' AND Record_ID = " + recID + " ORDER BY Related_Sample_Name");
						rs2.next();
						out.print("<td>" + rs2.getString(1) + " <a href='detail.jsp?FeatID=" + rs2.getString(2) + "'>" + rs2.getString(3) + "</a></td></tr>");
						while (rs2.next()) {
							out.println("<tr><td>" + rs2.getString(1) + " <a href='detail.jsp?FeatID=" + rs2.getString(2) + "'>" + rs2.getString(3) + "</a></td></tr>");
						}
					}
					//Strat relationships (repeating)
					rs2 = statement2.executeQuery("SELECT COUNT(*) FROM Relationship WHERE Relationship_Type = 'Strat' AND Record_ID = " + recID);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Stratigraphic Relationships</td>");
						rs2 = statement2.executeQuery("SELECT Relationship FROM Relationship_View WHERE Relationship_Type = 'Strat' AND Record_ID = " + recID + " ORDER BY Strat_Unit");
						rs2.next();
						out.print("<td>" + rs2.getString(1) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td>" + rs2.getString(1) + "</td></tr>");
						}
					}
					if (rs.getString(10) != null) { out.println("<tr><td class='heading'>Column/Map</td><td>" + rs.getString(10) + "</td></tr>"); }
					if (rs.getString(11) != null) { out.println("<tr><td class='heading'>Dip</td><td>" + rs.getString(11) + "</td></tr>"); }
					if (rs.getString(12) != null) { out.println("<tr><td class='heading'>Dip Direction</td><td>" + rs.getString(12) + "</td></tr>"); }
					if (rs.getString(13) != null) { out.println("<tr><td class='heading'>Strike</td><td>" + rs.getString(13) + "</td></tr>"); }
					if (rs.getString(14) != null) { out.println("<tr><td class='heading'>Facing</td><td>" + rs.getString(14) + "</td></tr>"); }
					if (rs.getString(15) != null) { out.println("<tr><td class='heading'>Grain Size</td><td>" + rs.getString(15) + "</td></tr>"); }
					if (rs.getString(16) != null) { out.println("<tr><td class='heading'>Comparator Used</td><td>" + rs.getString(16) + "</td></tr>"); }
					if (rs.getString(17) != null) { out.println("<tr><td class='heading'>Bed Thickness</td><td>" + rs.getString(17) + "</td></tr>"); }
					if (rs.getString(18) != null) { out.println("<tr><td class='heading'>Bedding</td><td>" + rs.getString(18) + "</td></tr>"); }
					if (rs.getString(19) != null) { out.println("<tr><td class='heading'>Weathering</td><td>" + rs.getString(19) + "</td></tr>"); }
					if (rs.getString(20) != null) { out.println("<tr><td class='heading'>Hardness</td><td>" + rs.getString(20) + "</td></tr>"); }
					if (rs.getString(21) != null) { out.println("<tr><td class='heading'>Carbonate</td><td>" + rs.getString(21) + "</td></tr>"); }
					if (rs.getString(22) != null) { out.println("<tr><td class='heading'>Colour</td><td>" + rs.getString(22) + "</td></tr>"); }
					//sed features (repeating)
					rs2 = statement2.executeQuery("SELECT COUNT(*) FROM Sedimentary_Feature WHERE Record_ID = " + recID);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Additional Features</td>");
						rs2 = statement2.executeQuery("SELECT Sedimentary_Feature FROM Sedimentary_Feature_View WHERE Record_ID = " + recID + " ORDER BY Sed_Feature");
						rs2.next();
						out.print("<td>" + rs2.getString(1) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td>" + rs2.getString(1) + "</td></tr>");
						}
					}
					if (rs.getString(23) != null) { out.println("<tr><td class='heading'>Inf Environment</td><td>" + rs.getString(23) + "</td></tr>"); }
					if (rs.getString(24) != null) { out.println("<tr><td class='heading'>Nature of Rock Unit</td><td>" + rs.getString(24) + "</td></tr>"); }
					if (rs.getString(25) != null) { out.println("<tr><td class='heading'>Correspondence</td><td>" + rs.getString(25) + "</td></tr>"); }
/*					//Image/Files
					MetadataRecord[] mr = attacher.getDocumentsForId(Integer.parseInt(recID));
					if (mr != null) {
						out.println("<tr><td colspan='2' class='heading'>Images/Files</td></tr>");
						out.println("<tr><td colspan='2'><table border='0' cellspacing='0' width='600'>");
						int y = 1;
						out.print("<tr>");
						for (int x = 0; x < mr.length; x++) {
							if (y++ == 5) {
								out.println("</tr><tr>");
								y = 2;
							}
							out.print("<td width='150' align='center' class='smalltext'><a href='binary_data_view.jsp?Src=" + mr[x].getCode() + "&Title=" + mr[x].getTitle() + "' target='binary'><img border=0 src='/online/Thumbnail?src=" + mr[x].getCode() + "'></a><br><a href='binary_data_view.jsp?Src=" + mr[x].getCode() + "&Title=" + mr[x].getTitle() + "' target='binary'>" + mr[x].getTitle() + "</a></td>");
						}
						out.println("</td></tr></table></td></tr>");
					} */
					out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");
				}
			}

			out.println("</table></p>");
		}

		else { //no record or no rights
			out.println("<p><span class='subhead'>Access denied</span></p>Either there is no record matching the ID you entered or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.");
			out.println("<p>" + userRights + "</p>");
		}
	}
	out.println("</td></tr></table>");
	drawBottom(out, et);

	statement2.close();
	statement3.close();
%>