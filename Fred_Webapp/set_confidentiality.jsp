<%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.model.Audit"
%><%@page import="nz.cri.gns.fred.model.ConfidentialGroup"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.website.WebsiteConstants"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.db.metadata.DocumentAttacher"
%><%@page import="nz.cri.gns.db.metadata.MetadataRecord"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="java.io.PrintWriter"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Set Confidentiality";
	}

%><%
	PageState state = new PageState(request, response, getServletContext());
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	User user = (User)getUser(session);
	FolderUtil folderUtil = new FolderUtil(factory);
	AuditUtil auditUtil = new AuditUtil(factory);
	TaxonomicUtil taxonomicUtil = new TaxonomicUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);
	et.setUseNavigationColumn(false);
	addButtons(et, new IconnedLink[] {
			new IconnedLink((String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "&q=" + Math.random(), "images/back_arrow.gif", "Back")
		});

	drawTop(out, et, request, response);
		
	if (request.getParameter("ID") != null && request.getParameter("RecType") != null && request.getParameter("FoldID") != null) {
		int id = Integer.parseInt(request.getParameter("ID"));
		String recType = request.getParameter("RecType");
		UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
		
		Audit audit = null;
		if (recType.equals(FREDConstants.ADOPTION) || recType.equals(FREDConstants.PALEONTOLOGICAL)) {
			audit = new RecordUtil(factory).getRecord(id).getAudit();
		} else if (recType.equals("SMP")) {
			audit = new SampleUtil(factory).getSample(id).getAudit();
		} else {
			audit = new FeatureUtil(factory).getFeature(id).getAudit();
		}

		if (folder.isAllowedEditLocalities()) {
		
			/*
			if (request.getParameter("Action") != null) {
				try {
					if (request.getParameter("Action").equals("Insert")) {
						int docID = attacher.insertDocument(loadId, request, "Upload");
						MetadataRecord mr = attacher.getDocumentForId(docID);
						if (request.getParameter("Name") != null)
							attacher.setTitle(mr, request.getParameter("Name"));
						if (request.getParameter("Desc") != null)
							attacher.setNote(mr, request.getParameter("Desc"));
					} else if (request.getParameter("Action").equals("Remove")) {
						attacher.removeDocument(loadId, mrs[Integer.parseInt(request.getParameter("DeleteID"))]);
					}
					mrs = attacher.getDocumentsForId(loadId);
				} catch (Exception e) {
					System.out.println("********** FRED binary data entry error: " + new java.util.Date());
					e.printStackTrace();
				}
			} */
				
			%><center><%
			
			%><p>&nbsp;</p><p><%
			startDETable(pageContext);
			%><table border="0" width="550">
			<form method="get" action="set_confidential.jsp">
			<tr><td colspan="2" class="deHeading">Confidentiality Options</td></tr>
			<input type="hidden" name="ID" value="<%=id%>">
			<input type="hidden" name="RecType" value="<%=recType%>">
			<input type="hidden" name="FoldID" value="<%=folder.getFolderId()%>">
			<input type="hidden" name="Action" value="Update">
			<tr><td><input type="radio" name="confidType" value="open" selected /></td><td style="text-align:left" class="heading">Open</td></tr>
			<tr><td><input type="radio" name="confidType" value="confid" /></td><td style="text-align:left" class="heading">Confidential&nbsp;&nbsp;</td>
			<td style="text-align:left" class="heading">Confidential Period:&nbsp;</td>
			<td><select name="confidPeriod">
			  <option value="0.5">6 months</option>
			  <option value="1" selected>1 year</option>
			  <option value="2">2 years</option>
			  <option value="5">5 years</option>
			</select></td></tr><%
			for (ConfidentialGroup confidGroup : auditUtil.getConfidentialGroups()) {
				%><input type="checkbox" name="confidGroups" value="<%=confidGroup.getGroupId()%>" /></td><td style="text-align:left"><%=confidGroup.getName()%></td></tr><%
			}
			%></form>
			</table><%
			endDETable(pageContext);
			%></p><%
				
			%></center><%
		} 
		else {
			%><p><span class="subhead">Access denied</span></p>You don't have sufficient rights in this folder.  Click <a href="index.jsp" class="heading">here</a> to return to the FRED home page.<%
		}
	}
		
	%></td></tr></table><%
	drawBottom(out, et);
		
%>
