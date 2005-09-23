<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.*"
%><%@page import="nz.cri.gns.fred.data.*"
%><%@page import="nz.cri.gns.db.*"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="nz.cri.gns.auth.*"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%
	PageState state = new PageState(request, response, getServletContext());
	User user = (User)getUser(session);
	ComboDescriptor cd;
	DAOFactory factory = HibernateUtil.get().getDAOFactory();

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	if (request.getParameter("FeatID") != null && request.getParameter("FoldID") != null) {
		String featID = request.getParameter("FeatID");
		String foldID = request.getParameter("FoldID");
		String sampID = request.getParameter("SampID");
		String recID = request.getParameter("RecID");
		String recType = request.getParameter("RecType");

		Feature feature = new Feature(Integer.parseInt(featID), user, state);
		Sample sample = null;
		if (sampID != null)
			sample = new Sample(Integer.parseInt(sampID), user, state);
		Folder folder = new Folder(Integer.parseInt(foldID), user, state);
		if (folder.isAllowedCreateLocalities()) {
			if (!feature.getAsString(Feature.FEATURE_TYPE).equals(Feature.OUTCROP_LOCALITY)) {
			
				if (request.getParameter("ActionType") != null) {
					try {
						if (sample != null) {
							sample.editSample(request.getParameter("TopDepth"), request.getParameter("BottomDepth"), request.getParameter("DrillType"));
						} else {
							FeatureUtil featureUtil = new FeatureUtil(factory);
							featureUtil.addSample(featureUtil.getFeature(feature.getFeatureID()), 
										request.getParameter("TopDepth"),
										request.getParameter("BottomDepth"),
										request.getParameter("DrillType"),
										Integer.parseInt(foldID));
							//feature.addNewSample(request.getParameter("TopDepth"), request.getParameter("BottomDepth"), request.getParameter("DrillType"), foldID);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (recType != null) {
						if (recID != null) {
							response.sendRedirect("data_entry.jsp?Type=" + recType + "&FoldID=" + foldID + "&RecID=" + recID);
						} else {
							response.sendRedirect("data_entry.jsp?Type=" + recType + "&FoldID=" + foldID + "&SampID=" + sampID);
						}
					} else if (sampID != null) {
						response.sendRedirect("data_entry.jsp?Type=Sample&FoldID=" + foldID + "&SampID=" + sampID);
					} else {
						//Add a random number to stop browser caching
						response.sendRedirect("folder_feature_detail.jsp?FeatID=" + featID + "&FoldID=" + foldID + "&q=" + Math.random());
					}
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
				out.print("<tr><td class='heading'>Top Depth&nbsp;&nbsp;</td><td><input type='text' name='TopDepth' ");
				if (sample != null)
					out.print(" value='" + FREDUtils.noNulls(sample.getAsString(Sample.TOP_DEPTH)) + "'");
				out.println("/></td></tr>");
				out.print("<tr><td class='heading'>Bottom Depth&nbsp;&nbsp;</td><td><input type='text' name='BottomDepth'");
				if (sample != null)
					out.print(" value='" + FREDUtils.noNulls(sample.getAsString(Sample.BOTTOM_DEPTH)) + "'");
				out.println("/></td></tr>");
				if (feature.getAsString(Feature.FEATURE_TYPE).equals(Feature.DRILLHOLE_LOCALITY)) {
					out.println("<tr><td class='heading'>Type&nbsp;&nbsp;</td><td>");
					cd = new ComboDescriptor("drill_type", "drill_type_ID", "Name");
					cd.name = "DrillType";
					cd.orderBy = "drill_type_ID";
					if (sample != null)
						cd.selected = sample.getAsString(Sample.DRILL_TYPE_ID);
					HTMLUtils.makeDropBox(new java.io.PrintWriter(out), FREDUtils.getFREDConnection(state), cd);
				}
				out.println("</td></tr>");
				out.println("</table>");
				out.println("<input type='hidden' name='FeatID' value='" + featID + "' />");
				out.println("<input type='hidden' name='FoldID' value='" + foldID + "' />");
				if (sampID != null)
					out.println("<input type='hidden' name='SampID' value='" + sampID + "' />");
				if (recID != null)
					out.println("<input type='hidden' name='RecID' value='" + recID + "' />");
				if (recType != null)
					out.println("<input type='hidden' name='RecType' value='" + recType + "' />");
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
	factory.closeSession();
%>
