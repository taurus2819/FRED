<%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.db.metadata.DocumentAttacher"
%><%@page import="nz.cri.gns.db.metadata.MetadataRecord"
%><%
	PageState state = new PageState(request, response, getServletContext());
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	User user = (User)getUser(session);
	FolderUtil folderUtil = new FolderUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("ErrMsg") != null && !request.getParameter("ErrMsg").equals("")) {
		%><script language="JavaScript">
		alert("Your file can not be loaded: <%=request.getParameter("ErrMsg")%>");
		</script><%
	}

	try {
		request = DocumentAttacher.decodeRequest(request);

		if (request.getParameter("ID") != null && request.getParameter("RecType") != null && request.getParameter("FoldID") != null) {
			int id = Integer.parseInt(request.getParameter("ID"));
			String recType = request.getParameter("RecType");
			UserFolder folder = folderUtil.getUserFolder(Integer.parseInt(request.getParameter("FoldID")), user);
			String docType = "FEATURE";
			if (recType.equals(FREDConstants.ADOPTION) || recType.equals(FREDConstants.PALEONTOLOGICAL)) {
				docType = "RECORD";
			} else if (recType.equals("SMP")) {
				docType = "SAMPLE";
			}
			DocumentAttacher attacher = FREDUtil.getDocumentAttacher(docType, state);
			MetadataRecord[] mr = attacher.getDocumentsForId(id);

			if (folder.isAllowedEditLocalities()) {
				if (request.getParameter("Action") != null) {
					String errMsg = null;
					if (request.getParameter("Action").equals("Insert")) {
						try {
							int docID = attacher.insertDocument(id, request, "Upload");
							MetadataRecord mr1 = attacher.getDocumentForId(docID);
							if (request.getParameter("Name") != null) { 
								attacher.setTitle(mr1, request.getParameter("Name"));
							}
							if (request.getParameter("Desc") != null)
								attacher.setNote(mr1, request.getParameter("Desc"));
						} catch (Exception e) {
							errMsg = e.getMessage();
						}
					}
					else if (request.getParameter("Action").equals("Remove")) {
						attacher.removeDocument(id, mr[Integer.parseInt(request.getParameter("DeleteID"))]);
					}
					response.sendRedirect("binary_data_entry.jsp?ID=" + id + "&RecType=" + recType + "&FoldID=" + folder.getFolderId() + ((errMsg != null) ? "&ErrMsg=" + errMsg : ""));
					return;
				}

				%><table style="margin-left:20px; margin-top:20px; width:150px;" border="0">
				<tr><td colspan="2" align="center"><img src="images/new_file.gif" height="20" width="20" /></td></tr>
				<tr><td colspan="2" align="center" class="bigheading">File/Image</td></tr>
				<tr><td><img src="images/blank.gif" width="1" height="10" /></td></tr>
				<tr><td><a href="javascript: window.close();"><img src="images/close.gif" height="20" width="20" border="0" alt="Close" /></a>&nbsp;&nbsp;</td><td><a href="javascript: window.close();" class="heading">Close</a></td></tr>
				</table><%
				drawEndNavigation(out);
	
				%><table style="margin-left:20px; width:550px;" border="0">
				<tr><td>

				<form enctype="multipart/form-data" method="post" action="binary_data_entry.jsp">
				<input type="hidden" name="ID" value="<%=id%>">
				<input type="hidden" name="RecType" value="<%=recType%>">
				<input type="hidden" name="FoldID" value="<%=folder.getFolderId()%>">
				<input type="hidden" name="Action" value="Insert">

				<p><table border="0" cellspacing="0" cellpadding="2">
				<tr><td class="heading">File<br><span class="smalltext">The following types of files can be loaded: images (JPEG, TIFF, GIF, BMP), text, Microsoft Word/Excel and PDF files</span></td><td><input type="file" name="Upload"></td></tr>
				<tr><td class="heading">Name<br><span class="smalltext">If different to filename</span></td><td><input type="text" name="Name"></td></tr>
				<tr><td class="heading">Description</td><td><input type="text" name="Desc"></td></tr>
				<tr><td><input type="submit" value="Upload"></td></tr>
				</table></p>
				</form><%

				if (mr != null) {
					%><p><table border="0" cellspacing="0" cellpadding="2">
					<tr><td class="heading" colspan="3">Files/Images already saved</td></tr>
					<tr><td>&nbsp;</td></tr><%
					for (int i = 0; i < mr.length; i++) {
						%><tr><td><a href="binary_data_entry.jsp?ID=<%=id%>&RecType=<%=recType%>&FoldID=<%=folder.getFolderId()%>&Action=Remove&DeleteID=<%=i%>"><img src="images/cancel.gif" width="20" height="20" border="0" alt="Delete" /></a></td>
						<td><a href="/online/DigitalDocument?src=<%=mr[i].getCode()%>"><img border="0" src="/online/Thumbnail?src=<%=mr[i].getCode()%>" alt="FRED document" /></a>&nbsp;&nbsp;</td>
						<td><%=mr[i].getTitle()%></td></tr><%
					}
				}
				%></table><%
			}
		}
		else {
			%><p><span class="subhead">Access denied</span></p>You don't have sufficient rights in this folder.  Click <a href="index.jsp" class="heading">here</a> to return to the FRED home page.<%
		}

	} catch (Throwable t) {
		t.printStackTrace(new java.io.PrintWriter(out));
	}

	%></td></tr></table><%
	drawBottom(out, et);
%>
