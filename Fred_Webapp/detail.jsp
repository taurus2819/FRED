<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.fred.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, nz.cri.gns.db.metadata.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	nz.cri.gns.intranet.DBConnection frConn = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	nz.cri.gns.intranet.DBConnection connection;
	User user = getUser(session);

	PageState state = new PageState(request, response, getServletContext());
	
	Statement preserveStatement;
	Statement preserveStatement2;
	//DocumentAttacher attacher = DocumentAttacher.createFREDDocumentAttacher(session, application);
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");
	SimpleDateFormat yearFormatter = new SimpleDateFormat ("yyyy");
	SimpleDateFormat monthFormatter = new SimpleDateFormat ("MMM yyyy");
	ResultSet rs, rs2, rs3;

	String sampID, recID, featType, status = "", query;
	boolean authorChk = false, sCountChk = false, sCoordChk = false, commChk = true;
	int[] types = {Types.NUMERIC};
	Object data[];
	data = new Object[1];
	int[] doubleTypes = {Types.NUMERIC, Types.NUMERIC};
	Object doubleData[];
	doubleData = new Object[2];

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	//if FeatureID given then get SampleID or transer to drillhole
	if (request.getParameter("FeatID") != null) {
		query = "SELECT MIN(Sample_ID), COUNT(*) FROM Sample WHERE Feature_ID = ?";
		data[0] = new Integer(Integer.parseInt(request.getParameter("FeatID")));
		rs = frConn.executeQuery(query, types, data);
		if (rs.next()) {
			if (rs.getInt(2) > 1) {
				response.sendRedirect("drillhole_detail.jsp?ID=" + request.getParameter("FeatID"));
			} else {
				response.sendRedirect("detail.jsp?ID=" + rs.getString(1));
			}
		}
	}

	//get SampleID
	if (request.getParameter("ID") != null) {
		sampID = request.getParameter("ID");
		session.setAttribute("SampleID", sampID);
	} else {
		sampID = (String) session.getAttribute("SampleID");
	}

	drawTop(out, et, request, response);

	if (sampID != null) {

		//create connection:  userConnection if logged in, otherwise FR
		if (user !=  null) {
			connection = user.getUsersConnection(new PageState(request, response, application), frConn);
		} else {
			connection = frConn;
		}

		if (request.getParameter("AuthorChk") != null && request.getParameter("AuthorChk").equals("true")) { authorChk = true; }
		if (request.getParameter("SCountChk") != null && request.getParameter("SCountChk").equals("true")) { sCountChk = true; }
		if (request.getParameter("SCoordChk") != null && request.getParameter("SCoordChk").equals("true")) { sCoordChk = true; }
		if (request.getParameter("CommChk") != null && request.getParameter("CommChk").equals("false")) { commChk = false; }

		//List data
		out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
		try {
			FullSample fullSample = FullSample.getFullSample(Integer.parseInt(sampID), user, state);
			Audit audit = Audit.getAudit(fullSample.getAsInt(FullSample.AUDIT_ID), state);

		query = "SELECT S.Feature_Type, S.Sample_Name, S.Masterfile_Name, S.Status, A.Created_By, A.Created_Date, A.Modified_By, A.Modified_Date, A.Submitted_By, A.Submitted_Date, A.Approved_By, A.Approved_Date FROM Sample_View S, Audit_View A WHERE S.Audit_ID = A.Audit_ID AND S.Sample_ID = ?";
		data[0] = new Integer(Integer.parseInt(sampID));
		rs = frConn.executeQuery(query, types, data);
		rs.next();

		//if (rs.next()) {

			featType = fullSample.getAsString(FullSample.FEATURE_TYPE);
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='bigheading' >" + fullSample.getAsString(FullSample.SAMPLE_NAME) + "</td></tr>");
			out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
			if (fullSample.get(fullSample.MASTERFILE_NAME) != null) {
				out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + fullSample.getAsString(FullSample.MASTERFILE_NAME) + "</td></tr>");
			}
			if (!fullSample.getAsString(FullSample.STATUS).equals("approved")) {
				out.println("<tr><td class='smallheading'>Status:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + fullSample.getAsString(FullSample.STATUS) + "</td></tr>");
			}
			if (audit.get(Audit.CREATED_BY) != null || audit.get(Audit.CREATED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Created:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.CREATED_BY) != null) { out.print(audit.getAsString(Audit.CREATED_BY) + "<br />"); }
				if (audit.get(Audit.CREATED_DATE) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(audit.getAsDate(Audit.CREATED_DATE))); }
				out.println("</td></tr>");
			}
			if (audit.get(Audit.MODIFIED_BY) != null || audit.get(Audit.MODIFIED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Edited:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.MODIFIED_BY) != null) { out.print(audit.getAsString(Audit.MODIFIED_BY) + "<br />"); }
				if (audit.get(Audit.MODIFIED_DATE) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(audit.getAsDate(Audit.MODIFIED_DATE))); }
				out.println("</td></tr>");
			}
			if (audit.get(Audit.SUBMITTED_BY) != null || audit.get(Audit.SUBMITTED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Submitted:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.SUBMITTED_BY) != null) { out.print(audit.getAsString(Audit.SUBMITTED_BY) + "<br />"); }
				if (audit.get(Audit.SUBMITTED_DATE) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(audit.getAsDate(Audit.SUBMITTED_DATE))); }
				out.println("</td></tr>");
			}
			if (audit.get(Audit.APPROVED_BY) != null || audit.get(Audit.APPROVED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Approved:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.APPROVED_BY) != null) { out.print(audit.getAsString(Audit.APPROVED_BY) + "<br />"); }
				if (audit.get(Audit.APPROVED_DATE) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(audit.getAsDate(Audit.APPROVED_DATE))); }
				out.println("</td></tr>");
			}
				if (user != null) {
				out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
				out.println("<tr><td colspan='2'><a href='print_front.jsp?ID=" + sampID + "' title='Print' target='print'><img src='images/print.gif' width='20' height='20' border='0' /></a><img src='images/blank.gif' width='10' height='1' border='0' /><a href='print_front.jsp?ID=" + sampID + "' class='heading' target='print'>Print Front</a></td></tr>");
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
				out.println("</a><img src='images/blank.gif' width='10' height='1' />Author</td></tr>");
				out.print("<tr><td colspan='2' class='heading'>");
				if (sCountChk) {
					out.print("<a href='#' onClick='document.TaxaForm.SCountChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
				} else {
					out.print("<a href='#' onClick='document.TaxaForm.SCountChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
				}
				out.println("</a><img src='images/blank.gif' width='10' height='1' />Specimen Count</td></tr>");
				out.print("<tr><td colspan='2' class='heading'>");
				if (sCoordChk) {
					out.print("<a href='#' onClick='document.TaxaForm.SCoordChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
				} else {
					out.print("<a href='#' onClick='document.TaxaForm.SCoordChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
				}
				out.println("</a><img src='images/blank.gif' width='10' height='1' />Specimen Coord</td></tr>");
				out.print("<tr><td colspan='2' class='heading'>");
				if (commChk) {
					out.print("<a href='#' onClick='document.TaxaForm.CommChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
				} else {
					out.print("<a href='#' onClick='document.TaxaForm.CommChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
				}
				out.println("</a><img src='images/blank.gif' width='10' height='1' />Comments</td></tr>");
				out.println("</form>");
			}
			out.println("</table>");
			drawEndNavigation(out);
			
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			
			//Locality Data
			query = "SELECT Feature_ID, Yard_FR_Number, Feature_Name, NZMG_Sheet, NZMG_East, NZMG_North, Latitude, Longitude, Accuracy, Method, Locality, Drillhole_Depth, Person, Start_Date, Start_Date_Rounding, Finish_Date, Finish_Date_Rounding, Drillhole_Licence_Name, Datum_Type, Datum_Elevation, Start_Depth, Finish_Depth FROM FR.Sample_View WHERE Sample_ID = ?";
			data[0] = new Integer(Integer.parseInt(sampID));
			rs = connection.executeQuery(query, types, data);
			preserveStatement = connection.preservePreparedStatement();
			rs.next();
				out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
			if (rs.getString(2) != null) { out.println("<tr><td class='heading'>Yard FR Number</td><td>" + (rs.getString(2)) + "</td></tr>"); }
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
			if (rs.getString(10) != null) { out.println("<tr><td class='heading'>Method</td><td>" + rs.getString(10) + "</td></tr>"); }
			if (featType.equals("Outcrop")) {
				if (rs.getString(3) != null) { out.println("<tr><td class='heading'>Field Number</td><td>" + (rs.getString(3)) + "</td></tr>"); }
			} else {
				if (featType.equals("Drillhole")) {
					if (rs.getString(3) != null) { out.println("<tr><td class='heading'>Drillhole Name</td><td><a href='drillhole_detail.jsp?ID=" + rs.getString(1) + "'>" + rs.getString(3) + "</a></td></tr>"); }
					if (rs.getString(12) != null) { out.println("<tr><td class='heading'>Sample Depth</td><td>" + rs.getString(12) + "</td></tr>"); }
					out.println("<tr><td class='heading'>Other Drillhole Samples</td><td>");
				} else { //VertSect
					if (rs.getString(3) != null) { out.println("<tr><td class='heading'>Section Name</td><td><a href='drillhole_detail.jsp?ID=" + rs.getString(1) + "'>" + rs.getString(3) + "</a></td></tr>"); }
					if (rs.getString(12) != null) { out.println("<tr><td class='heading'>Sample Height</td><td>" + rs.getString(12) + "</td></tr>"); }
					out.println("<tr><td class='heading'>Other Section Samples</td><td>");					
				}
				//check for samples above and below current one
				query = "SELECT Sample_ID, Sample_Name, Drillhole_Depth FROM FR.Sample_View WHERE Feature_ID = ? AND Top_Depth IS NOT NULL ORDER BY Top_Depth";
				data[0] = new Integer(rs.getInt(1));
				rs2 = connection.executeQuery(query, types, data);
				String dholeID = "", dholeSampName = "", dhole = "";
				while (rs2.next()) {
					if (rs2.getString(1).equals(sampID)) {
						if (!dholeID.equals("")) {out.println("Sample Above: <a href='detail.jsp?ID=" + dholeID + "'>" + dhole + "</a><br>"); }
						if (rs2.next()) { out.println("Sample Below: <a href='detail.jsp?ID=" + rs2.getString(1) + "'>" + rs2.getString(2)  + " - " + rs2.getString(3)+ "</a><br>"); }
						break;
					}
					dholeID = rs2.getString(1);
					dhole = rs2.getString(2) + " - " + rs2.getString(3);
				}
				out.println("</td></tr>");
			}
			if (rs.getString(11) != null ) { out.println("<tr><td class='heading'>Locality</td><td>" + rs.getString(11) + "</td></tr>"); }
			if (!featType.equals("Outcrop")) {
				if (rs.getString(13) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Operating Company");
					} else {
						out.print("Section Collector");
					}
					out.println("</td><td>" + rs.getString(13) + "</td></tr>");
				}
				if (rs.getString(14) != null) {
					out.print("<tr><td class='heading'>");
					if (featType.equals("Drillhole")) {
						out.print("Spud Date");
					} else {
						out.print("Sampling Start Date");
					}
					out.print("</td><td>");
					if (rs.getString(15) == null) {
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(14)));
					} else if (rs.getString(15).equals("Year")) {
						out.print(yearFormatter.format(rs.getDate(14)));
					} else if (rs.getString(15).equals("Month")) {
						out.print(monthFormatter.format(rs.getDate(14)));
					}
					out.println("</td></tr>");
				}
				if (rs.getString(16) != null) {
					out.print("<tr><td class='heading'>	Completion Date</td><td>");
					if (rs.getString(17) == null) {
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(16)));
					} else if (rs.getString(17).equals("Year")) {
						out.print(yearFormatter.format(rs.getDate(16)));
					} else if (rs.getString(17).equals("Month")) {
						out.print(monthFormatter.format(rs.getDate(16)));
					}
					out.println("</td></tr>");
				}
				if (featType.equals("Drillhole") && rs.getString(18) != null) { out.println("<tr><td class='heading' width='135'>Licence Area</td><td>" + rs.getString(14) + "</td></tr>"); }
				if (rs.getString(19) != null) { out.println("<tr><td class='heading' width='135'>Datum Type</td><td>" + rs.getString(19) + "</td></tr>"); }
				if (rs.getString(20) != null) { out.println("<tr><td class='heading' width='135'>Datum Elevation</td><td>" + rs.getString(20) + " m asl</td></tr>"); }
				if (rs.getString(21) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Kick-off Depth");
					} else {
						out.print("Top Horizon");
					}
					out.println("</td><td>" + rs.getString(21) + " m</td></tr>");
				}
				if (rs.getString(22) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Termination Depth");
					} else {
						out.print("Base Horizon");
					}
					out.println("</td><td>" + rs.getString(22) + " m</td></tr>");
				}
			}
			preserveStatement.close();
			out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");	
	
			//Sample Property Data
			query = "SELECT Record_ID FROM Record NATURAL JOIN Sample_Property WHERE Sample_ID = ?";
			data[0] = new Integer(Integer.parseInt(sampID));
			rs = frConn.executeQuery(query, types, data);
			if (rs.next()) {
				recID = rs.getString(1);
				query = "SELECT Record_ID, Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage, Known_Stage, Column_Map, Dip, Dip_Direction, Strike, Facing, Grainsize, Comparator_Used, Bed_Thickness, Bedding, Weathering, Hardness, Carbonate, Colour, Deposition_Env, Rock_Nature, Correspondence, Collector_ID, Sent_To_Fossil_Group_ID, Sed_Feature_ID FROM FR.Sample_Property_View WHERE Record_ID = ?";
				data[0] = new Integer(Integer.parseInt(recID));
				rs = connection.executeQuery(query, types, data);
				preserveStatement = connection.preservePreparedStatement();
				if (rs.next()) {
					//recID = rs.getString(1);
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
					if (rs.getString(26) != null) {
						out.print("<tr><td class='heading'>Collectors</td>");
						query = "SELECT Name FROM Person_View P, Collector C WHERE P.Person_ID = C.Person_ID AND C.Record_ID = ? ORDER BY Name";
						data[0] = new Integer(Integer.parseInt(recID));
						rs2 = frConn.executeQuery(query, types, data);
						rs2.next();
						out.println("<td>" + rs2.getString(1) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td></td><td>" + rs2.getString(1) + "</td></tr>");
						}
					}
					if (rs.getString(4) != null) { out.println("<tr><td class='heading'>Strat Name</td><td>" + rs.getString(4) + "</td></tr>"); }
					if (rs.getString(5) != null) { out.println("<tr><td class='heading'>In Place</td><td>" + rs.getString(5) + "</td></tr>"); }
					//sent to (repeating)
					if (rs.getString(27) != null) {
						out.print("<tr><td class='heading'>Sent To</td>");
						query = "SELECT Sent_To FROM Sent_To_View WHERE Record_ID = ? ORDER BY Sent_To";
						data[0] = new Integer(Integer.parseInt(recID));
						rs2 = frConn.executeQuery(query, types, data);
						rs2.next();
						out.print("<td>" + rs2.getString(1) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td></td><td>" + rs2.getString(1) + "</td></tr>");
						}
					}
					if (rs.getString(6) != null) { out.println("<tr><td class='heading'>Not Collected</td><td>" + rs.getString(6) + "</td></tr>"); }
					//Stratigraphy
					if (rs.getString(7) != null) { out.println("<tr><td class='heading'>Significance</td><td>" + rs.getString(7) + "</td></tr>"); }
					if (rs.getString(8) != null) { out.println("<tr><td class='heading'>Inferred Stage</td><td>" + rs.getString(8) + "</td></tr>"); }
					if (rs.getString(9) != null) { out.println("<tr><td class='heading'>Known Stage</td><td>" + rs.getString(9) + "</td></tr>"); }
					//Nearby samples (repeating)
					query = "SELECT COUNT(*) FROM Relationship WHERE Relationship_Type = 'Sample' AND Relation_Type_ID = 231 AND Record_ID = ?";
					data[0] = new Integer(Integer.parseInt(recID));
					rs2 = frConn.executeQuery(query, types, data);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Samples Nearby</td>");
						query = "SELECT Related_Feature_ID, Related_Sample_Name FROM Relationship_View WHERE Relationship_Type = 'Sample' AND Relation_Type_ID = 231 AND Record_ID = ? ORDER BY Related_Sample_Name";
						data[0] = new Integer(Integer.parseInt(recID));
						rs2 = frConn.executeQuery(query, types, data);
						rs2.next();
						out.print("<td><a href='detail.jsp?FeatID=" + rs2.getString(1) + "'>" + rs2.getString(2) + "</a></td></tr>");
						while (rs2.next()) {
							out.println("<tr><td><a href='detail.jsp?FeatID=" + rs2.getString(1) + "'>" + rs2.getString(2) + "</a></td></tr>");
						}
					}
					//Sample relationships (repeating)
					query = "SELECT COUNT(*) FROM Relationship WHERE Relation_Type_ID <> 231 AND Relationship_Type = 'Sample' AND Record_ID = ?";
					data[0] = new Integer(Integer.parseInt(recID));
					rs2 = frConn.executeQuery(query, types, data);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Sample Relationships</td>");
						query = "SELECT Distance_Relation, Related_Feature_ID, Related_Sample_Name FROM Relationship_View WHERE Relation_Type_ID <> 231 AND Relationship_Type = 'Sample' AND Record_ID = ? ORDER BY Related_Sample_Name";
						data[0] = new Integer(Integer.parseInt(recID));
						rs2 = frConn.executeQuery(query, types, data);
						rs2.next();
						out.print("<td>" + rs2.getString(1) + " <a href='detail.jsp?FeatID=" + rs2.getString(2) + "'>" + rs2.getString(3) + "</a></td></tr>");
						while (rs2.next()) {
							out.println("<tr><td>" + rs2.getString(1) + " <a href='detail.jsp?FeatID=" + rs2.getString(2) + "'>" + rs2.getString(3) + "</a></td></tr>");
						}
					}
					//Strat relationships (repeating)
					query = "SELECT COUNT(*) FROM Relationship WHERE Relationship_Type = 'Strat' AND Record_ID = ?";
					data[0] = new Integer(Integer.parseInt(recID));
					rs2 = frConn.executeQuery(query, types, data);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Stratigraphic Relationships</td>");
						query = "SELECT Relationship FROM Relationship_View WHERE Relationship_Type = 'Strat' AND Record_ID = ? ORDER BY Strat_Unit";
						data[0] = new Integer(Integer.parseInt(recID));
						rs2 = frConn.executeQuery(query, types, data);
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
					if (rs.getString(28) != null) {
						out.print("<tr><td class='heading'>Additional Features</td>");
						query = "SELECT Sedimentary_Feature FROM Sedimentary_Feature_View WHERE Record_ID = ? ORDER BY Sed_Feature";
						data[0] = new Integer(Integer.parseInt(recID));
						rs2 = frConn.executeQuery(query, types, data);
						rs2.next();
						out.print("<td>" + rs2.getString(1) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td></td><td>" + rs2.getString(1) + "</td></tr>");
						}
					}
					if (rs.getString(23) != null) { out.println("<tr><td class='heading'>Inferred Environment</td><td>" + rs.getString(23) + "</td></tr>"); }
					if (rs.getString(24) != null) { out.println("<tr><td class='heading'>Nature of Rock Unit</td><td>" + rs.getString(24) + "</td></tr>"); }
					if (rs.getString(25) != null) { out.println("<tr><td class='heading'>Correspondence</td><td>" + rs.getString(25) + "</td></tr>"); }
		/*			//Image/Files
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
							out.print("<td width='150' align='center' class='smalltext'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "'><br />" + mr[x].getTitle() + "</td>");
						}
						out.println("</td></tr></table></td></tr>");
					}
*/
				out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");
				}
				preserveStatement.close();
			}
	
			//Adoption
			query = "SELECT Record_ID, Adoption_Date, Date_Rounding, Adopted_Stage, Comments FROM FR.Adoption_View WHERE Sample_ID = ?";
			data[0] = new Integer(Integer.parseInt(sampID));
			rs = connection.executeQuery(query, types, data);
			preserveStatement = connection.preservePreparedStatement();
			while (rs.next()) {
				out.println("<tr><td colspan='2' class='bigheading'>Adoption Data</td></tr>");
				recID = rs.getString(1);
				//adoptors (repeating)
				query = "SELECT COUNT(*) FROM Adoptor WHERE Record_ID = ?";
				data[0] = new Integer(Integer.parseInt(recID));
				rs2 = frConn.executeQuery(query, types, data);
				rs2.next();
				if (rs2.getInt(1) > 0) {
					out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Adoptors</td>");
					query = "SELECT Name FROM Person_View P, Adoptor A WHERE P.Person_ID = A.Person_ID AND A.Record_ID = ? ORDER BY Name";
					data[0] = new Integer(Integer.parseInt(recID));
					rs2 = frConn.executeQuery(query, types, data);
					rs2.next();
					out.println("<td>" + rs2.getString(1) + "</td></tr>");
					while (rs2.next()) {
						out.println("<tr><td>" + rs2.getString(1) + "</td></tr>");
					}
				}
				if (rs.getString(2) != null) {
					out.print("<tr><td class='heading'>Adoption Date</td><td>");
					if (rs.getString(3) == null) {
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(2)));
					} else if (rs.getString(3).equals("Year")) {
						out.print(yearFormatter.format(rs.getDate(2)));
					} else if (rs.getString(3).equals("Month")) {
						out.print(monthFormatter.format(rs.getDate(2)));
					}
					out.println("</td></tr>");
				}
				if (rs.getString(4) != null) { out.println("<tr><td class='heading'>Adopted Stage</td><td>" + rs.getString(4) + "</td></tr>"); }
				if (rs.getString(5) != null) { out.println("<tr><td class='heading'>Comments</td><td>" + rs.getString(5) + "</td></tr>"); }
	/*			//Image/Files
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
						out.print("<td width='150' align='center' class='smalltext'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "'><br />" + mr[x].getTitle() + "</td>");
					}
					out.println("</td></tr></table></td></tr>");
				}
	*/			out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");
			}
			preserveStatement.close();
	
			//Paleontology
			query = "SELECT DISTINCT Record_ID, Identification_Date, Date_Rounding, Stage, Stage_Comments, Lab, Lab_Number, Collection_Comments FROM FR.Paleontology_View WHERE Sample_ID = ?";
			data[0] = new Integer(Integer.parseInt(sampID));
			rs = connection.executeQuery(query, types, data);
			preserveStatement = connection.preservePreparedStatement();
			while (rs.next()) {
				out.println("<tr><td colspan='2' class='bigheading'>Paleontology Data</td></tr>");
				recID = rs.getString(1);
				//identifiers (repeating)
				query = "SELECT COUNT(*) FROM Identifier WHERE Record_ID = ?";
				data[0] = new Integer(Integer.parseInt(recID));
				rs2 = frConn.executeQuery(query, types, data);
				rs2.next();
				if (rs2.getInt(1) > 0) {
					out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Identifiers</td>");
					query = "SELECT Name FROM Person_View P, Identifier I WHERE P.Person_ID = I.Person_ID AND I.Record_ID = ? ORDER BY Name";
					data[0] = new Integer(Integer.parseInt(recID));
					rs2 = frConn.executeQuery(query, types, data);
					rs2.next();
					out.println("<td>" + rs2.getString(1) + "</td></tr>");
					while (rs2.next()) {
						out.println("<tr><td>" + rs2.getString(1) + "</td></tr>");
					}
				}
				if (rs.getString(2) != null) {
					out.print("<tr><td class='heading'>Identification Date</td><td>");
					if (rs.getString(3) == null) {
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(2)));
					} else if (rs.getString(3).equals("Year")) {
						out.print(yearFormatter.format(rs.getDate(2)));
					} else if (rs.getString(3).equals("Month")) {
						out.print(monthFormatter.format(rs.getDate(2)));
					}
					out.println("</td></tr>");
				}
				if (rs.getString(4) != null) { out.println("<tr><td class='heading'>Stage</td><td>" + rs.getString(4) + "</td></tr>"); }
				if (rs.getString(5) != null) { out.println("<tr><td class='heading'>Stage Comments</td><td>" + rs.getString(5) + "</td></tr>"); }
				if (rs.getString(6) != null) { out.println("<tr><td class='heading'>Lab</td><td>" + rs.getString(6) + "</td></tr>"); }
				if (rs.getString(7) != null) { out.println("<tr><td class='heading'>Lab Number</td><td>" + rs.getString(7) + "</td></tr>"); }
				if (rs.getString(8) != null) { out.println("<tr><td class='heading'>Collection Comments</td><td>" + rs.getString(8) + "</td></tr>"); }
				//taxa (double repeating)
				query = "SELECT COUNT(*) FROM Pal_List WHERE Record_ID = ?";
				data[0] = new Integer(Integer.parseInt(recID));
				rs2 = frConn.executeQuery(query, types, data);
				rs2.next();
				if (rs2.getInt(1) > 0) {
					out.println("<tr><td colspan='2'><table border='0' cellspacing='0' width='600'>");
					query = "SELECT DISTINCT P.Group_ID, L.Name FROM Pal_List P, Lookup L WHERE P.Group_ID = L.Lookup_ID AND P.Record_ID = ? ORDER BY P.Group_ID";
					data[0] = new Integer(Integer.parseInt(recID));
					rs2 = frConn.executeQuery(query, types, data);
					preserveStatement2 = frConn.preservePreparedStatement();
					while (rs2.next()) {
						out.println("<tr><td colspan='4' class='heading'>" + rs2.getString(2) + "</td></tr>");
						query = "SELECT * FROM Pal_List WHERE Record_ID = ? AND Group_ID = ? AND Taxonomic_Name IS NOT NULL";
						doubleData[0] = new Integer(Integer.parseInt(recID));
						doubleData[1] = new Integer(rs2.getInt(1));
						rs3 = frConn.executeQuery(query, doubleTypes, doubleData);
						if (rs3.next()) {
							out.print("<tr class='heading'><td>Taxonomic Name&nbsp;&nbsp;</td>");
							if (authorChk) { out.print("<td>Author&nbsp;&nbsp;</td>"); }
							if (sCountChk) { out.print("<td>Spec Count&nbsp;&nbsp;</td>"); }
							if (sCoordChk) { out.print("<td>Spec Coord&nbsp;&nbsp;</td>"); }
							if (commChk) { out.print("<td>Comments&nbsp;&nbsp;</td>"); }
							out.println("</tr>");
							query = "SELECT P.Taxonomic_Name, T.Author, P.Specimen_Count, P.Specimen_Coords, P.Comments FROM Pal_List P, Taxonomic_Lookup T WHERE P.Taxa_ID = T.Taxa_ID AND Record_ID = ? AND T.Group_ID = ? ORDER BY P.Taxonomic_Name";
							doubleData[0] = new Integer(Integer.parseInt(recID));
							doubleData[1] = new Integer(rs2.getInt(1));
							rs3 = frConn.executeQuery(query, doubleTypes, doubleData);
							while (rs3.next()) {
								out.print("<tr><td>" + rs3.getString(1) + "&nbsp;&nbsp;</td>");
								if (authorChk) { out.print("<td><i>" + noNulls(rs3.getString(2)) + "</i>&nbsp;&nbsp;</td>"); }
								if (sCountChk) { out.print("<td>" + noNulls(rs3.getString(3)) + "&nbsp;&nbsp;</td>"); }
								if (sCoordChk) { out.print("<td>" + noNulls(rs3.getString(4)) + "&nbsp;&nbsp;</td>"); }
								if (commChk) { out.print("<td>" + noNulls(rs3.getString(5)) + "&nbsp;&nbsp;</td>"); }
								out.println("</tr>");
							}
						} else {
							out.println("<tr><td colspan='4'>No fossils listed</td></tr>");
						}
						out.println("<tr><td><img src='images/blank.gif' height='10' width='1' /></td></tr>");
					}
					preserveStatement2.close();
					out.println("</td></tr></table></td></tr>");
				}
	/*			//Image/Files
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
						out.print("<td width='150' align='center' class='smalltext'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "'><br />" + mr[x].getTitle() + "</td>");
					}
					out.println("</td></tr></table></td></tr>");
				}
	*/		}
			preserveStatement.close();
	
			if (user ==  null) { out.println("<tr><td colspan='2'>More data may be available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/detail.jsp") + "' class='boldlink'>logged</a> in users</td></tr>"); }
			out.println("</table></td></tr></table>");
		}
		catch (Exception e) { // no record
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p>Either the sample doesn't exist or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
		}

	}
	else { //no sampleID
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");
		out.println("<p>Either the sample doesn't exist or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
	}
	
	drawBottom(out, et); 
%>