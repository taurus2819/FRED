<%@		page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*"
%><%
	PageState state = new PageState(request, response, getServletContext());
	DBConnection connection = FREDUtils.getFREDConnection(state);
	Statement statement = connection.statement;
	ResultSet rs;

	User user = (User)getUser(session);
	int execUp;

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	if (request.getParameter("ErrMsg") != null && !request.getParameter("ErrMsg").equals("")) {
		out.println("<script language='JavaScript'>alert(\"Your file can not be loaded: " + request.getParameter("ErrMsg") + "\");</script>");
	}

	try {
		request = DocumentAttacher.decodeRequest(request);

		if (request.getParameter("ID") != null && request.getParameter("RecType") != null && request.getParameter("FoldID") != null) {
			String id = request.getParameter("ID");
			String recType = request.getParameter("RecType");
			Folder folder = new Folder(Integer.parseInt(request.getParameter("FoldID")), user, state);
			DocumentAttacher attacher = null;
			MetadataRecord[] mr = null;
			if (recType.equals(Record.ADOPTION_RECORD) || recType.equals(Record.PALEONTOLOGY_RECORD)) {
				Record record = Record.getData(Integer.parseInt(id), user, state, true);
				attacher = DocumentAttacher.createFREDRecordDocumentAttacher(state.session, state.context);
				mr = record.getMetadataRecords();
			} else if (recType.equals("SMP")) {
				Sample sample = new Sample(Integer.parseInt(id), user, state, true);
				attacher = DocumentAttacher.createFREDSampleDocumentAttacher(state.session, state.context);
				mr = sample.getSampleMetadataRecords();
			} else if (recType.equals(Feature.OUTCROP_LOCALITY) || recType.equals(Feature.DRILLHOLE_LOCALITY) || recType.equals(Feature.VERTICAL_SECTION_LOCALITY)) {
				Feature feature = new Feature(Integer.parseInt(id), user, state, true);
				Sample sample = new Sample(((Integer) feature.getAsVector(Feature.SAMPLES).firstElement()).intValue(), user, state, true);
				attacher = DocumentAttacher.createFREDFeatureDocumentAttacher(state.session, state.context);
				mr = feature.getMetadataRecords();
			}

			if (folder.isAllowedEditLocalities()) {

				if (request.getParameter("Action") != null) {
					String errMsg = null;
					if (request.getParameter("Action").equals("Insert")) {
						try {
							int docID = attacher.insertDocument(Integer.parseInt(id), request, "Upload");
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
						attacher.removeDocument(Integer.parseInt(id), mr[Integer.parseInt(request.getParameter("DeleteID"))]);
					}
					response.sendRedirect("binary_data_entry.jsp?ID=" + id + "&RecType=" + recType + "&FoldID=" + folder.getFolderID() + ((errMsg != null) ? "&ErrMsg=" + errMsg : ""));
					return;
				}

				out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
				out.println("<tr><td colspan='2' align='center'><img src='images/new_file.gif' height='20' width='20' /></td></tr>");
				out.println("<tr><td colspan='2' align='center' class='bigheading'>File/Image</td></tr>");
				out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
				out.println("<tr><td><a href='javascript: window.close();'><img src='images/close.gif' height='20' width='20' border='0' alt='Close' /></a>&nbsp;&nbsp;</td><td><a href='javascript: window.close();' class='heading'>Close</a></td></tr>");
				out.println("</table>");
				drawEndNavigation(out);
	
				out.println("<table style='margin-left:20px; width:550px;' border='0'>");
				out.println("<tr><td>");

				out.println("<form enctype='multipart/form-data' method='post' action='binary_data_entry.jsp'>");
				out.println("<input type='hidden' name='ID' value='" + id + "'>");
				out.println("<input type='hidden' name='RecType' value='" + recType + "'>");
				out.println("<input type='hidden' name='FoldID' value='" + folder.getFolderID() + "'>");
				out.println("<input type='hidden' name='Action' value='Insert'>");

				out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
				out.println("<tr><td class='heading'>File<br><span class='smalltext'>The following types of files can be loaded: images (JPEG, TIFF, GIF, BMP), text, Microsoft Word/Excel and PDF files</span></td><td><input type='file' name='Upload'></td></tr>");
				out.println("<tr><td class='heading'>Name<br><span class='smalltext'>If different to filename</span></td><td><input type='text' name='Name'></td></tr>");
				out.println("<tr><td class='heading'>Description</td><td><input type='text' name='Desc'></td></tr>");
				out.println("<tr><td><input type='submit' value='Upload'></td></tr>");
				out.println("</table></p>");
				out.println("</form>");

				if (mr != null) {
					out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
					out.println("<tr><td class='heading' colspan='3'>Files/Images already saved</td></tr>");
					out.println("<tr><td>&nbsp</td></tr>");
					for (int i = 0; i < mr.length; i++) {
						out.println("<tr><td><a href='binary_data_entry.jsp?ID=" + id + "&RecType=" + recType + "&FoldID=" + folder.getFolderID() + "&Action=Remove&DeleteID=" + i + "'><img src='images/cancel.gif' width='20' height='20' border='0' alt='Delete' /></a></td><td><a href='/online/DigitalDocument?src=" + mr[i].getCode() + "'><img border='0' src='/online/Thumbnail?src=" + mr[i].getCode() + "' alt='FRED document' /></a>&nbsp;&nbsp;</td><td>" + mr[i].getTitle() + "</td></tr>");
					}
				}
				out.println("</table>");
			}
		}
		else {
			out.println("<p><span class='subhead'>Access denied</span></p>You don't have sufficient rights in this folder.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.");
		}

	} catch (Throwable t) {
		t.printStackTrace(new java.io.PrintWriter(out));
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
