<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%
	User user = getUser(session);
	PageState state = new PageState(request, response, getServletContext());
	DBConnection connection = FREDUtils.getFREDConnection(state);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	Statement statement3 = connection.getExtraStatement();
	ResultSet rs, rs2, rs3;
	int userID = user.getPersonId(), userRights;
	boolean sampPropFlag;

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("ID") != null) {
		Folder folder = new Folder(Integer.parseInt(request.getParameter("ID")), user, state, true);
		String redirect = URLEncoder.encode("admin_folder_detail.jsp?ID=" + folder.getFolderID(), "UTF-8");

		//print FRF after accepting
		if (request.getParameter("PrintID") != null) {
			out.println("<script language='JavaScript'><!--");
			out.println("window.open(\"print_front.jsp?FeatID=" + request.getParameter("PrintID") + "\");");
			out.println("//--></script>");
		}

		if (folder.isAllowedReadLocalities()) {
			
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/folder.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' class='bigheading' align='center'>" + folder.getAsString(Folder.NAME) + " Masterfile</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='folder_list.jsp' title='Back to Folders'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Back to Folders</a></td></tr>");
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			//List records

			//Table header
			out.println("<p><table border='1' cellspacing='0' cellpadding='2' width='550'>");
			out.println("<tr><th colspan='2'>Locality&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Field No/<br>Drillhole Name&nbsp;&nbsp;</th><th>Submitted Date&nbsp;&nbsp;</th><th colspan='2'>Options</th></tr>");
			
			//To Approve
			out.println("<tr><th colspan='5'>Localities to Approve</th></tr>");
			for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
				Feature feature = new Feature(((Integer) i.next()).intValue(), user, state, true);
				String featID = String.valueOf(feature.getFeatureID());
				out.print("<tr><td><a href='detail.jsp?FeatID=" + featID + "'><img src='images/loc.gif' height='20' width='20' border='0' alt='View Locality' /></a></td><td class='heading'>" + feature.getAsString(Feature.SAMPLE_NAMES) + "</td><td>" + feature.getAsString(Feature.FEATURE_TYPE) + "</td><td>" + FREDUtils.noNulls(feature.getAsString(Feature.FEATURE_NAME)) + "</td><td>");
				if (feature.get(Feature.LAST_CHANGE) != null) 
					out.print(DateFormat.getDateInstance(DateFormat.LONG).format(feature.getAsDate(Feature.LAST_CHANGE)));
				out.print("</td><td>");
		//		if (folder.isAllowedEditLocalities()) 
		//			out.print("<a href='data_entry.jsp?Type=" + feature.getAsString(Feature.FEATURE_TYPE) + "&FoldID=" + folder.getFolderID() + "&FeatID=" + featID + "&Redirect=" + redirect + "'><img src='images/edit.gif' border='0' height='20' width='20' alt='Edit Locality' /></a>");
		//		out.print("</td><td>");
				if (folder.isAllowedApproveLocalities())
					out.print("<a href='detail.jsp?FeatID=" + featID + "'><img src='images/review.gif' width='20' height='20' border='0' alt='Review Localities' /></a>");
				out.println("</td></tr>");
			}
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			
/*			//Recently Approved
			out.println("<tr><th colspan='5'>Localities Recently Approved</th></tr>");
			rs3 = statement3.executeQuery("SELECT DISTINCT S.Feature_ID, S.Sample_Name FROM Sample_All_View S, Audit_Table A WHERE S.Audit_ID = A.Audit_ID AND S.Status = 'approved' AND S.Masterfile_ID = " + foldID + " AND A.Approved_Date >= (SYSDATE - 7) ORDER BY Sample_Name");
			String featID = "";
			while (rs3.next()) {
				if (rs3.getString(1).equals(featID)) { continue; }
				featID = rs3.getString(1);
				rs = statement.executeQuery("SELECT S.Sample_ID, S.Sample_Name, S.Feature_Type, S.Feature_Name, A.Submitted_Date FROM Sample_All_View S, Audit_View A WHERE S.Audit_ID = A.Audit_ID AND S.Feature_ID = " + featID);
				rs.next();
				if (!rs.getString(3).equals("Outcrop")) { //drillhole so loop through individual sample names
					drillSampName = "";
					rs2 = statement2.executeQuery("SELECT DISTINCT Sample_Name FROM Sample_All_View WHERE Feature_ID = " + featID + " ORDER BY Sample_Name");
					while (rs2.next()) { drillSampName = drillSampName + rs2.getString(1) + ", "; }
					drillSampName = drillSampName.substring(0, drillSampName.length() - 2);
					out.print("<tr><td><img src='images/loc.gif' height='20' width='20' /><img src='images/blank.gif' width='5' height='20' /></td><td class='heading'><a href='detail.jsp?FeatID=" + featID + "'>" + drillSampName + "</a></td><td class='heading'><a href='drillhole_detail.jsp?ID=" + featID + "'>" + FREDUtils.noNulls(rs.getString(4)) +"</a></td><td>");
				} else {
					out.print("<tr><td class='heading'><img src='images/loc.gif' height='20' width='20' /><img src='images/blank.gif' width='5' height='20' /></td><td class='heading'><a href='detail.jsp?ID=" + rs.getString(1) + "'>" + rs.getString(2) + "</a></td><td>" + FREDUtils.noNulls(rs.getString(4)) + "</td><td>");
				}
				if (rs.getString(5) != null) { 
						out.print(DateFormat.getDateInstance(DateFormat.LONG).format(rs.getDate(5)));
				}
				out.println("</td></tr>");
			}
*/			out.println("</table></p>");
		}
		else { //no record found
			out.println("No folder found");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);

	statement2.close();
	statement3.close();
%>