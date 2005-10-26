<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.fred.IconnedLink"
%><%@page import="nz.cri.gns.fred.dao.StorageAccessException"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="java.sql.*"
%><%@page import="java.text.*"
%><%@page import="java.util.*"
%><%@page import="nz.cri.gns.auth.*"
%><%!
	public String getName(HttpServletRequest request) {
		try {
			FolderUtil folderUtil = new FolderUtil(HibernateUtil.get().getDAOFactory());
			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));
			return "FRED :: " + folder.getFolder().getName() + " Masterfile";
		} catch (StorageAccessException e) {
			return "FRED";
		}
	}
	
	protected IconnedLink[] getButtons(HttpServletRequest request) {
		try {
			/*DAOFactory factory = HibernateUtil.get().getDAOFactory();
			FolderUtil folderUtil = new FolderUtil(factory);
			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), getUser(request.getSession()));*/
			return new IconnedLink[] {
				new IconnedLink("folder_list.jsp", "images/back_arrow.gif", "Back to folders"),
				new IconnedLink("backlog_setup.jsp?ID=" + request.getParameter("ID"), "images/revoke.gif", "Backlog setup")
			};
		} catch (Exception e) {
			return new IconnedLink[]{};
		}
	}

%><%
DAOFactory factory = HibernateUtil.get().getDAOFactory();
try {
	if (request.getParameter("ID") == null) {
		factory.closeSession();
		response.sendRedirect("folder_list.jsp");
		return;
	} 
	//	This is for backlog entry...
	else if (request.getParameter("ID").equals(String.valueOf(FREDUtil.MASTERFILE_BACKLOG))) {
		factory.closeSession();
		response.sendRedirect("backlog_folder_detail.jsp?ID=" + FREDUtil.MASTERFILE_BACKLOG);
		return;
	}		
		

	FolderUtil folderUtil = new FolderUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);
	User user =(User) getUser(session);

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("ID")), user);
	String redirect = "admin_folder_detail.jsp?ID=" + folder.getFolderId();

	if (folder.isAllowedReadLocalities()) {
		
		%><script><!--
function showHide(toShow, toHide) {
	document.getElementById(toShow).style.display = 'block';
	document.getElementById(toHide).style.display = 'none';
}
//--></script>
<center><p>&nbsp;<p/><div id="showInst"><table border="0" width="550" style="border: none; width: 550px"><tr><td style="text-align: left"><a href="javascript:showHide('inst', 'showInst');">Instructions...</a></td></tr></table></div><div id="inst" style="visibilty: hidden; display: none">
<%
		startDETable(pageContext);
		%><table border="0" style="border: none; width: 550px" width="550"><tr><td style="text-align: left">
<tr><td colspan="3" class="deHeading">Masterfile Folder Instructions</td></tr><tr><td style="text-align: left">
<ul>
<li>Listed below are the localities currently in this masterfile folder.
<li>Click on the icons to work with an individual locality record:
<ul>
<li><img src="images/print.gif" border="0"> to print the locality
<li><img src="images/edit.gif" border="0"> to edit the locality
<li><img src="images/review.gif" border="0"> to accept or reject the locality
</ul>
</ul>
</td></tr>
<tr><td style="text-align: right"><a href="javascript:showHide('showInst', 'inst');">Hide instructions...</a></td></tr></table>
<%
		endDETable(pageContext);
		%></div>
<p>

<%
		startDETable(pageContext);
		%><table border="0" width="550"><tr><td colspan="9" class="deHeading">Localities to Approve</td></tr><% 
		out.println("<tr><th colspan='2'>Locality&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Submitted Date&nbsp;&nbsp;</th><th>Submitted By&nbsp;&nbsp;</th><th colspan='3'>Options</th></tr>");

		//Display the features
		Feature[] features = featureUtil.getWaitingFeatures(folder);
		for (int i=0; i<features.length; i++) {
			Feature feature = features[i];
			Audit audit = feature.getAudit();

			out.print("<tr><td><a href='detail.jsp?FeatID=" + feature.getFeatureId() + "'><img src='images/loc.gif' height='20' width='20' border='0' alt='View Locality' /></a></td><td class='heading'>" + FeatureUtil.getFeatureName(feature) + "&nbsp;&nbsp;</td><td>" + feature.getFeatureType() + "&nbsp;&nbsp;</td><td>");
			if (audit.getSubmittedDate() != null)
				out.print(DateFormat.getDateInstance(DateFormat.LONG).format(audit.getSubmittedDate()));
			out.print("&nbsp;&nbsp;</td><td>" + FREDUtil.getUserName(audit.getSubmittedById().intValue()) + "&nbsp;&nbsp;</td><td>");
			out.print("<a href='print_front.jsp?FeatID=" + feature.getFeatureId() + (feature.getFeatureType().equals(FREDConstants.OUTCROP) ? "" : "&FormType=Short") + "' target='print'><img src='images/print.gif' border='0' height='20' width='20' alt='Print Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
			out.print("</td><td>");
			if (folder.isAllowedEditLocalities()) 
				out.print("<a href='data_entry.jsp?Type=" + feature.getFeatureType() + "&FoldID=" + folder.getFolderId() + "&FeatID=" + feature.getFeatureId() + "&Redirect=" + redirect + "'><img src='images/edit.gif' border='0' height='20' width='20' alt='Edit Locality' /></a><img src='images/blank.gif' height='20' width='2' />");
			out.print("</td><td>");
			if (folder.isAllowedApproveLocalities())
				out.print("<a href='detail.jsp?FeatID=" + feature.getFeatureId() + "'><img src='images/review.gif' width='20' height='20' border='0' alt='Review Localities' /></a>");
			out.println("</td></tr>");
		}
%><tr><td><img src="images/blank.gif" width="1" height="10" /></td></tr>
		</table><%
		endDETable(pageContext);
		%></p><p>
<%
		startDETable(pageContext);
		%><table border="0" width="550"><tr><td colspan="9" class="deHeading">Localities Recently Approved</td></tr><% 
		//Recently Approved
		features = featureUtil.getFeaturesApprovedInTheLastWeek(folder);
%><tr><th style="text-align: left" colspan='2'>Locality&nbsp;&nbsp;</th><th>Type&nbsp;&nbsp;</th><th>Approved Date&nbsp;&nbsp;</th><th>Approved By&nbsp;&nbsp;</th><th colspan='3'>Options</th></tr>
<%
		for (int i=0; i<features.length; i++) {
			Feature feature = features[i];
			Audit audit = feature.getAudit();

			out.print("<tr><td><a href='detail.jsp?FeatID=" + feature.getFeatureId() + "'><img src='images/loc.gif' height='20' width='20' border='0' alt='View Locality' /></a></td><td style=\"text-align: left\"><span class='heading'>" + FeatureUtil.getFeatureName(feature) + "</span>&nbsp;&nbsp;<br />(" + DBUtils.nvl(feature.getFeatureName()) + ")&nbsp;&nbsp;</td><td>" + feature.getFeatureType() + "&nbsp;&nbsp;</td><td>");
			if (audit.getApprovedDate() != null) 
				out.print(DateFormat.getDateInstance(DateFormat.LONG).format(audit.getApprovedDate()));
			out.print("&nbsp;&nbsp;</td><td>" + FREDUtil.getUserName(audit.getApprovedById().intValue()) + "&nbsp;&nbsp;</td><td>");
			out.print("<a href='print_front.jsp?FeatID=" + feature.getFeatureId() + (feature.getFeatureType().equals(FREDConstants.OUTCROP) ? "" : "&FormType=Short") + "' target='print'><img src='images/print.gif' border='0' height='20' width='20' alt='Print Locality' /></a>");
			out.print("</td><td></td></tr>");
		}
		out.println("</table>");
		endDETable(pageContext);
		%></p><%
		}
	else { //no record found
		out.println("No folder found");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
} catch (Exception e) {
	e.printStackTrace();
} finally {
	//Close the session
	factory.closeSession();
}
%>