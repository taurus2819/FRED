<%@page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
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
%><%@page import="java.io.PrintWriter"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Add Image/File";
	}

%><%
	PageState state = new PageState(request, response, getServletContext());
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	User user = (User)getUser(session);
	FolderUtil folderUtil = new FolderUtil(factory);
	RecordUtil recordUtil = new RecordUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	et.setUseNavigationColumn(false);
	et.setButtons(new IconnedLink[] {
			new IconnedLink((String)session.getAttribute(WebsiteConstants.DATA_ENTRY_REDIRECT) + "&q=" + Math.random(), "images/back_arrow.gif", "Back")
		});

	drawTop(out, et, request, response);

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
					try {
						if (request.getParameter("Action").equals("Insert")) {
							int docID = attacher.insertDocument(id, request, "Upload");
							MetadataRecord mr1 = attacher.getDocumentForId(docID);
							if (request.getParameter("Name") != null)
								attacher.setTitle(mr1, request.getParameter("Name"));
							if (request.getParameter("Desc") != null)
								attacher.setNote(mr1, request.getParameter("Desc"));
						} else if (request.getParameter("Action").equals("Remove")) {
							attacher.removeDocument(id, mr[Integer.parseInt(request.getParameter("DeleteID"))]);
						}
						mr = attacher.getDocumentsForId(id);
					} catch (Exception e) {
						System.out.println("********** FRED binary data entry error: " + new java.util.Date());
						e.printStackTrace();
						%><script language="JavaScript">
						alert("Your file can not be loaded: <%=e%>");
						</script><%
					}
				}
				
				%><center>
				<form enctype="multipart/form-data" method="post" action="binary_data_entry.jsp"><%
				
				if (recType.equals(FREDConstants.PALEONTOLOGICAL)) {
					%><p>&nbsp;</p><p><%
					startDETable(pageContext);
					%><table border="0" width="550"><tr><td colspan="2" class="deHeading">Select Taxon</td></tr><%
					SelectBox<PaleontologyListEntry> selectBox = new SelectBox<PaleontologyListEntry>(recordUtil.getRecord(id).getPaleontology().getListEntries());
					selectBox.writeBox(null, "Entire Paleo record", null, null, new PrintWriter(out));
					%></table><%
					endDETable(pageContext);
					%></p><%
				}
				
				%><p>&nbsp;</p><p><%
				startDETable(pageContext);
				%><table border="0" width="550"><tr><td colspan="2" class="deHeading">Add Image/File</td></tr>
				<input type="hidden" name="ID" value="<%=id%>">
				<input type="hidden" name="RecType" value="<%=recType%>">
				<input type="hidden" name="FoldID" value="<%=folder.getFolderId()%>">
				<input type="hidden" name="Action" value="Insert">
				<tr><td style="text-align:left" class="heading">File<br><span class="smalltext">The following types of files can be loaded: images (JPEG, TIFF, GIF, BMP), text, Microsoft Word/Excel and PDF files</span></td><td style="text-align:left"><input type="file" name="Upload"></td></tr>
				<tr><td style="text-align:left" class="heading">Name<br><span class="smalltext">If different to filename</span></td><td style="text-align:left"><input type="text" name="Name"></td></tr>
				<tr><td style="text-align:left" class="heading">Description</td><td style="text-align:left"><input type="text" name="Desc"></td></tr>
				<tr><td style="text-align:left"><input type="submit" value="Upload"></td></tr>
				</table><%
				endDETable(pageContext);
				%></p>

				</form><%
				
				if (mr != null) {
					%><p><%
					startDETable(pageContext);
					%><table border="0" width="550"><tr><td colspan="3" class="deHeading">Existing Images/Files</td></tr>
						<tr><td>&nbsp;</td></tr><%
					for (int i = 0; i < mr.length; i++) {
						%><tr><td style="text-align:left"><a href="binary_data_entry.jsp?ID=<%=id%>&RecType=<%=recType%>&FoldID=<%=folder.getFolderId()%>&Action=Remove&DeleteID=<%=i%>"><img src="images/cancel.gif" width="20" height="20" border="0" alt="Delete" /></a></td>
						<td style="text-align:left"><a href="/online/DigitalDocument?src=<%=mr[i].getCode()%>"><img border="0" src="/online/Thumbnail?src=<%=mr[i].getCode()%>" alt="FRED document" /></a>&nbsp;&nbsp;</td>
						<td style="text-align:left"><%=mr[i].getTitle()%></td></tr><%
					}
					%></table><%
					endDETable(pageContext);
					%></p><%
				}
				%></center><%
			}
		}
		else {
			%><p><span class="subhead">Access denied</span></p>You don't have sufficient rights in this folder.  Click <a href="index.jsp" class="heading">here</a> to return to the FRED home page.<%
		}
		
		%></td></tr></table><%
		drawBottom(out, et);
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		factory.closeSession();
	}
%>
