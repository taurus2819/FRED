<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, nz.cri.gns.auth.*"
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
	ResultSet rs;
	ComboDescriptor cd;
	String featID, sampID;
	int execUp;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);
%>
<script language='JavaScript'>

function checkDrill() {
	with (document.sampForm) {
		if ((SampID.value != "-" && (TopDepth.value != "" || BottomDepth.value != "")) || (SampID.value == "-" && TopDepth.value == "" && BottomDepth.value == "")) { //check that either a sample is selected or new data is entered BUT not both
			alert ("Please select either an existing sampling depth or enter another");
			SampID.focus();
			return false;
		}
		if (SampID.value == "-") { //check entered data if sample is not selected
			ActionType.value = "AddSamp";
			if (TopDepth.value != "" && isNaN(TopDepth.value)) {
				alert ("Please enter a numeric top depth");
				TopDepth.select();
				return false;
			}
			if (BottomDepth.value != "" && isNaN(BottomDepth.value)) {
				alert ("Please enter a numeric bottom depth");
				BottomDepth.select();
				return false;
			}
			if (TopDepth.value == "" && BottomDepth.value != "") {
				alert ("You haven't entered a top depth.  To enter only one depth put it in the top depth field");
				TopDepth.select();
				return false;
			}
			if (TopDepth.value != "" && BottomDepth.value != "" && parseFloat(TopDepth.value) > parseFloat(BottomDepth.value)) {
				alert ("Top Depth must be less than bottom depth");
				TopDepth.select();
				return false;
			}
		}
		return true;
	}
}

</script>

<%	if (request.getParameter("FeatID") != null) {
		featID = request.getParameter("FeatID");
		
		rs = statement.executeQuery("SELECT Feature_Type, Feature_Name FROM Feature WHERE Feature_ID = " + featID);
		if(rs.next() && !rs.getString(1).equals("Outcrop")) {

			out.println("<p><form name='sampForm' method='get' action='samp_select.jsp'>");

			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/drill.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' class='bigheading' align='center'>" + rs.getString(1) + ": " + noNulls(rs.getString(2)) + "</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='if(checkDrill()) {sampForm.submit();}' title='Select'><img src='images/ok.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='#' onClick='if(checkDrill()) {sampForm.submit();}' class='heading'>Select</a></td></tr>");
			out.println("<tr><td><a href='javascript:history.back();' title='Quit'><img src='images/cancel.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='javascript:history.back();' class='heading'>Quit</a></td></tr>");

			out.println("</table>");

			drawEndNavigation(out);

			if (request.getParameter("ActionType") != null) {

				//check if adding a new drillhole sample
				if (request.getParameter("ActionType").equals("AddSamp")) {
					//check existing samples.  If there is only one - the default one - then replace it with the new one, otherwise add
					rs = statement.executeQuery("SELECT S.Sample_ID FROM Sample_All_View S, Record R WHERE S.Sample_ID = R.Sample_ID(+) AND S.Feature_ID = " + featID + " AND S.Drillhole_Depth = 'Depth Not Specified' AND R.Sample_ID IS NULL");
					if (rs.next()) { //just update existing sample
						sampID = rs.getString(1);
						execUp = statement.executeUpdate("UPDATE Sample SET Top_Depth = " + JspUtils.sqlEscape(request.getParameter("TopDepth")) + ", Bottom_Depth = " + JspUtils.sqlEscape(request.getParameter("BottomDepth")) + ", Drill_Type_ID = " + JspUtils.sqlEscape(request.getParameter("DrillType")) + " WHERE Sample_ID = " + sampID);
					}
					else { //can add as no un-used default samples
						rs = statement.executeQuery("SELECT Sample_Seq.NEXTVAL FROM DUAL");
						rs.next();
						sampID = rs.getString(1);
						rs = statement.executeQuery("SELECT MIN(FR_ID) FROM Sample WHERE Feature_ID = " + featID);
						rs.next();
						execUp = statement.executeUpdate("INSERT INTO Sample (Sample_ID, Feature_ID, FR_ID, Top_Depth, Bottom_Depth, Drill_Type_ID) VALUES (" + sampID + ", " + featID + ", " + rs.getString(1) + ", " + JspUtils.sqlEscape(request.getParameter("TopDepth")) + ", " + JspUtils.sqlEscape(request.getParameter("BottomDepth")) + ", " + JspUtils.sqlEscape(request.getParameter("DrillType")) + ")");
					}
				}
				else {
					sampID = request.getParameter("SampID");
				}
				response.sendRedirect(request.getParameter("ReturnURL") + "?FoldID=" + request.getParameter("FoldID") + "&SampID=" + sampID);
			}

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			out.println("<table border='0' cellspacing='3' width='500'>");
			out.println("<tr><td colspan='2'>Please select a sampling depth from the list below or create a new one.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td class='heading'>Sampling Depth</td><td>");
			cd = new ComboDescriptor("Sample_All_View", "Sample_ID", "Drillhole_Depth");
			cd.name = "SampID";
			cd.prompt = "-- Choose --";
			cd.join = "Feature_ID = " + featID;
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.println("<tr><td></td><td class='heading'>OR</td></tr>");
			out.println("<tr><td class='heading'>Top Depth</td><td><input type='text' name='TopDepth'></td></tr>");
			out.println("<tr><td class='heading'>Bottom Depth</td><td><input type='text' name='BottomDepth'></td></tr>");
			out.println("<tr><td class='heading'>Type</td><td>");
			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Name");
			cd.name = "DrillType";
			cd.join = "FieldName = 'DrillType'";
			cd.orderBy = "Lookup_ID";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("<tr><td>&nbsp</td></tr>");
			out.println("<input type='hidden' name='FeatID' value='" + featID + "'>");
			out.println("<input type='hidden' name='FoldID' value='" + request.getParameter("FoldID") + "'>");
			out.println("<input type='hidden' name='ReturnURL' value='" + request.getParameter("ReturnURL") + "'>");
			out.println("<input type='hidden' name='ActionType' value='Go'>");
			out.println("</table></p>");
			out.println("<p><a href='#' onClick='if(checkDrill()) {sampForm.submit();}' title='Select'><img src='images/ok.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /><a href='#' onClick='if(checkDrill()) {sampForm.submit();}' class='heading'>Select</a></p>");
			out.println("</form>");
		}

		out.println("</table>");

		out.println("</td></tr></table>");
	}
	
	else {
	
		drawEndNavigation(out);	
		out.println("No drillhole/vertical section found");

	}
		
	drawBottom(out, et);

%>
