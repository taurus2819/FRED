<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, nz.cri.gns.db.metadata.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.text.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	Statement statement3 = connection.getExtraStatement();
	DocumentAttacher attacher = DocumentAttacher.createFREDDocumentAttacher(session, application);
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");
	SimpleDateFormat yearFormatter = new SimpleDateFormat ("yyyy");
	SimpleDateFormat monthFormatter = new SimpleDateFormat ("MMM yyyy");
	ResultSet rs, rs2, rs3;
	User user = getUser(session);
	String sampID, recID, status = "";
	int userID = 0, i = 1, userRights = 0, execUp;

	if (user != null) { userID = user.getPersonId(); }

	//if FeatureID given then get SampleID or transer to drillhole
	if (request.getParameter("FeatID") != null) {
		rs = statement.executeQuery("SELECT MIN(Sample_ID) FROM Sample_All_View WHERE Feature_ID = " + request.getParameter("FeatID"));
		if (rs.next()) {
			response.sendRedirect("print_front.jsp?ID=" + rs.getString(1));
		}
	}
	
	out.println("<!DOCTYPE html ");
	out.println("   PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" ");
	out.println("  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> ");
	out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");

	out.println(" <head>");
	out.println("  <title>Fossil Record Electronic Database</title>");
	out.println("  <link rel=\"styleSheet\" href=\"/online/style/extranet.css\" type=\"text/css\" />");
	out.println(" </head>");
	out.println(" <body>");

	if (request.getParameter("ID") != null) {
		sampID = request.getParameter("ID");

		//check if user can view this record and that record exists
		rs = statement.executeQuery("SELECT Status, User_Rights FROM Sample_Security_View WHERE Sample_ID = " + sampID + " AND (User_ID IS NULL OR User_ID = " + userID + ")");
		while (rs.next()) { //accumulate rights over multiple folders
			status = rs.getString(1);
			userRights = (userRights | rs.getInt(2));
		}
		if ((userRights & 1) != 0) { //allowed to view this record

			//List data
			out.println("<table border='1' cellspacing='0' cellpadding='10' width='620'>");
			out.println("<tr><td>");

			rs = statement.executeQuery("SELECT S.Sample_Name, S.Masterfile_Name, S.Feature_Type, A.Approved_By, A.Approved_Date FROM Sample_All_View S, Audit_View A WHERE S.Audit_ID = A.Audit_ID AND S.Sample_ID = " + sampID);
			rs.next();			
			out.println("<table border='0' cellspacing='0' cellpadding='0' width='600'>");
			out.println("<tr><td rowspan='2'><img src='images/gslogo.gif' width='42' height='50' /></td><td class='smallheading'>GEOLOGICAL SOCIETY OF NEW ZEALAND</td><td class='hugeheading' align='right'>" + rs.getString(1) + "</td></tr>");
			out.print("<tr><td class='hugeheading'>FOSSIL RECORD FORM</td><td align='right' class='heading'>" + rs.getString(3) + "</td></tr>");		
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("</table>");
			
			out.println("<table border='0' cellspacing='0' cellpadding='0' width='600'>");
			if (rs.getString(2) != null) { out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + rs.getString(2) + "</td></tr>"); }
			if (rs.getString(4) != null || rs.getString(5) != null) {
				out.println("<tr><td class='smallheading'>MF Curator Approved:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (rs.getString(4) != null) { out.print(rs.getString(4) + "&nbsp;&nbsp;"); }
				if (rs.getString(5) != null) { out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(5))); }
				out.println("</td></tr>");
			}
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("</table>");


			out.println("<table border='0' cellspacing='0' cellpadding='2' width='600'>");
			rs = statement.executeQuery("SELECT Feature_ID, Feature_Type, Feature_Name, NZMG_Sheet, NZMG_East, NZMG_North, Latitude, Longitude, Accuracy, Method, Locality, Drillhole_Depth FROM Sample_All_View WHERE Sample_ID = " + sampID);
			rs.next();
			
			out.println("<tr><td class='bigheading' colspan='2'>Mandatory Data</td></tr>");
			if (rs.getString(3) != null && userID != 0) {
				if (rs.getString(2).equals("Outcrop")) {
					out.println("<tr><td class='heading'>Field Number</td><td>" + rs.getString(3) + "</td></tr>");
				} else if (rs.getString(2).equals("Drillhole")) {
					out.println("<tr><td class='heading'>Drillhole Name</td><td>" + rs.getString(3) + "</td></tr>");
				} else {
					out.println("<tr><td class='heading'>Section Name</td><td>" + rs.getString(3) + "</td></tr>");
				}
			}
			if (rs.getString(7) != null) {
				out.print("<tr><td class='heading'>Grid Ref</td><td>");
				if (rs.getString(4) != null) {
					out.print(rs.getString(4) + ": " + nzmg.format(rs.getDouble(5)) + ", " + nzmg.format(rs.getDouble(6)));
					out.print("<img src='images/blank.gif' width='15' height='1' />|<img src='images/blank.gif' width='15' height='1' />");
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
				if (rs.getString(9) != null) { out.print("&nbsp;&nbsp;(&#177 " + rs.getString(9) + "m)"); }
				out.println("</td></tr>");
			}
			if (userID != 0) { //logged in user
			if (rs.getString(10) != null) { out.println("<tr><td class='heading'>Method</td><td>" + rs.getString(10) + "</td></tr>"); }
			if (rs.getString(11) != null) { out.println("<tr><td class='heading'>Locality</td><td>" + rs.getString(11) + "</td></tr>"); }
				rs = statement.executeQuery("SELECT Record_ID, Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage, Known_Stage, Column_Map, Dip, Dip_Direction, Strike, Facing, Grainsize, Comparator_Used, Bed_Thickness, Bedding, Weathering, Hardness, Carbonate, Colour, Deposition_Env, Rock_Nature, Correspondence, Record_ID FROM Sample_Property_All_View WHERE Sample_ID = " + sampID);
				if (rs.next()) {
					recID = rs.getString(1);
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
						rs2 = statement2.executeQuery("SELECT Name FROM SC.Person_View P, Collector C WHERE P.Person_ID = C.Person_ID AND C.Record_ID = " + recID + " ORDER BY Name");
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
					out.println("<tr><td class='bigheading' colspan='2'>Stratigraphy</td></tr>");
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
						out.print("<td>" + rs2.getString(2) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td>" + rs2.getString(2) + "</td></tr>");
						}
					}
					//Sample relationships (repeating)
					rs2 = statement2.executeQuery("SELECT COUNT(*) FROM Relationship WHERE Relation_Type_ID <> 231 AND Relationship_Type = 'Sample' AND Record_ID = " + recID);
					rs2.next();
					if (rs2.getInt(1) > 0) {
						out.print("<tr><td rowspan='" + rs2.getString(1) + "' class='heading'>Sample Relationships</td>");
						rs2 = statement2.executeQuery("SELECT Distance_Relation, Related_Feature_ID, Related_Sample_Name FROM Relationship_View WHERE Relation_Type_ID <> 231 AND Relationship_Type = 'Sample' AND Record_ID = " + recID + " ORDER BY Related_Sample_Name");
						rs2.next();
						out.print("<td>" + rs2.getString(1) + " " + rs2.getString(3) + "</td></tr>");
						while (rs2.next()) {
							out.println("<tr><td>" + rs2.getString(1) + " " + rs2.getString(3) + "</td></tr>");
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
					
					out.println("<tr><td class='bigheading' colspan='2'>Sedimentary Features</td></tr>");
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
					out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
					out.println("</table>");

					//Image/Files
					MetadataRecord[] mr = attacher.getDocumentsForId(Integer.parseInt(recID));
					if (mr != null) {
						out.println("<table border='0' cellspacing='0' cellpadding='0' width='600'>");
						out.println("<tr><td colspan='2' class='bigheading'>Attached Images/Files</td></tr>");
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
						out.println("</td></tr></table></td></tr></table>");
					}
				}
			} else {
				out.println("<tr><td colspan='2'>More data is available for this locality for logged in users</td></tr>");
			}
			out.println("<img src='images/blank.gif' width='600' height='1' />");
			out.println("</td></tr></table>");
		}
		else { //no record or no rights
			out.println("<p><span class='bigheading'>Access denied</span></p>Either there is no record matching the ID you entered or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.");
		}
	}
	
	
	statement2.close();
	statement3.close();
%>