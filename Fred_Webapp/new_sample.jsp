<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, nz.cri.gns.auth.*"
%><%
	PageState state = new PageState(request, response, getServletContext());
	User user = getUser(session);
	ComboDescriptor cd;

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	if (request.getParameter("FeatID") != null && request.getParameter("FoldID") != null) {
		String featID = request.getParameter("FeatID");
		String foldID = request.getParameter("FoldID");

		Feature feature = new Feature(Integer.parseInt(featID), user, state);
		Folder folder = new Folder(Integer.parseInt(foldID), user, state);
		if (folder.isAllowedCreateLocalities()) {
			if (!feature.getAsString(Feature.FEATURE_TYPE).equals("Outcrop")) {
			
				if (request.getParameter("ActionType") != null) {
					try {
						feature.addNewSample(request.getParameter("TopDepth"), request.getParameter("BottomDepth"), request.getParameter("DrillType"));
					} catch (Exception e) {}
					response.sendRedirect("folder_feature_detail.jsp?FeatID=" + featID + "&FoldID=" + foldID);
					return;
				}
				
				drawTop(out, et, request, response);
%>
<script language='JavaScript'>

function checkDrill() {
	with (document.sampForm) {
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
			alert ("You haven't entered a depth.  To enter only one depth put it in the top depth field");
			TopDepth.select();
			return false;
		}
		if (TopDepth.value != "" && BottomDepth.value != "" && parseFloat(TopDepth.value) > parseFloat(BottomDepth.value)) {
			alert ("Top Depth must be less than bottom depth");
			TopDepth.select();
			return false;
		}
		return true;
	}
}

</script>

<%
				out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
				out.println("<tr><td colspan='2' align='center'><img src='images/drill.gif' height='20' width='20' /></td></tr>");
				out.println("<tr><td colspan='2' class='bigheading' align='center'>" + feature.getAsString(Feature.SAMPLE_NAMES) + "</td></tr>");
				out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
				out.println("<tr><td><a href='javascript:history.back();' title='Quit'><img src='images/cancel.gif' width='20' height='20' border='0' /><img src='images/blank.gif' width='10' height='1' border='0' /></a></td><td><a href='javascript:history.back();' class='heading'>Quit</a></td></tr>");
				out.println("</table>");
	
				drawEndNavigation(out);
	
				out.println("<table style='margin-left:20px; width:550px;' border='0'>");
				out.println("<tr><td>");
	
				out.println("<p>Please enter either a top depth or both a top depth and bottom depth and for drillholes a type</p>");
	
				out.println("<form name='sampForm' method='get' action='new_sample.jsp'>");
				out.println("<table border='0' cellspacing='3'>");
				out.println("<tr><td class='heading'>Top Depth&nbsp;&nbsp;</td><td><input type='text' name='TopDepth' /></td></tr>");
				out.println("<tr><td class='heading'>Bottom Depth&nbsp;&nbsp;</td><td><input type='text' name='BottomDepth' /></td></tr>");
				if (feature.getAsString(Feature.FEATURE_TYPE).equals("Drillhole")) {
					out.println("<tr><td class='heading'>Type&nbsp;&nbsp;</td><td>");
					cd = new ComboDescriptor("Lookup", "Lookup_ID", "Name");
					cd.name = "DrillType";
					cd.join = "FieldName = 'DrillType'";
					cd.orderBy = "Lookup_ID";
					HTMLUtils.makeDropBox(new java.io.PrintWriter(out), FREDUtils.getFREDConnection(state), cd);
				}
				out.println("</td></tr>");
				out.println("</table>");
				out.println("<input type='hidden' name='FeatID' value='" + featID + "' />");
				out.println("<input type='hidden' name='FoldID' value='" + foldID + "' />");
				out.println("<input type='hidden' name='ActionType' value='Go' />");
				out.println("<p><a href='#' onClick='if(checkDrill()) {sampForm.submit();}'><img src='images/ok.gif' height='20' width='20' border='0' alt='Add' /></a>&nbsp;&nbsp;<a href='#' onClick='if(checkDrill()) {sampForm.submit();}' class='heading'>Add</a></p>");
				out.println("</form>");
				out.println("</td></tr></table>");
	
			} else {
				drawTop(out, et, request, response);
				drawEndNavigation(out);	
				out.println("No drillhole/vertical section found");
			}
		} else {
			drawTop(out, et, request, response);
			drawEndNavigation(out);
			out.println("You don't have sufficient rights to create samples in this folder");
		}
	}
	else {
		drawTop(out, et, request, response);
		drawEndNavigation(out);
	}
	drawBottom(out, et);

%>
