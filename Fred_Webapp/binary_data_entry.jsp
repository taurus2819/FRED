<%@		page extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*"
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
	PageState state = new PageState(request, response, getServletContext());
	DBConnection connection = FREDUtils.getFREDConnection(state);
	Statement statement = connection.statement;
	ResultSet rs;
	DocumentAttacher attacher = DocumentAttacher.createFREDDocumentAttacher(session, application);
	User user = (User)getUser(session);
	String recID, recType, foldID;
	int userID = user.getPersonId(), userRights = 0, execUp, docID;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	if (request.getParameter("ErrMsg") != null && !request.getParameter("ErrMsg").equals("")) {
		out.println("<script language='JavaScript'>alert(\"Your file can not be loaded: " + request.getParameter("ErrMsg") + "\");</script>");
	}

	try {

		request = DocumentAttacher.decodeRequest(request);

		if (request.getParameter("RecID") != null && request.getParameter("RecType") != null && request.getParameter("FoldID") != null) {
			recID = request.getParameter("RecID");
			recType = request.getParameter("RecType");
			foldID = request.getParameter("FoldID");

			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/new_file.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='bigheading'>File/Image</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			if (recType.equals("SMP")) {
				out.println("<tr><td><a href='samp_prop_data_entry.jsp?RecID=" + recID + "&FoldID=" + foldID + "' title='Back'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='samp_prop_data_entry.jsp?RecID=" + recID + "&FoldID=" + foldID + "' class='heading'>Back</a></td></tr>");
			} else if (recType.equals("ADO")) {
				out.println("<tr><td><a href='ado_data_entry.jsp?RecID=" + recID + "&FoldID=" + foldID + "' title='Back'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='ado_data_entry.jsp?RecID=" + recID + "&FoldID=" + foldID + "' class='heading'>Back</a></td></tr>");
			} else if (recType.equals("PAL")) {
				out.println("<tr><td><a href='pal_data_entry.jsp?RecID=" + recID + "&FoldID=" + foldID + "' title='Back'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='pal_data_entry.jsp?RecID=" + recID + "&FoldID=" + foldID + "' class='heading'>Back</a></td></tr>");
			}
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			//get user rights for this folder
			rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID);
			if (rs.next()) { userRights = rs.getInt(1); }

			//check rights match folder rights and record is editable
			rs = statement.executeQuery("SELECT * FROM Record_All_View WHERE Record_ID = " +  recID + " AND Working_Folder_ID = " + foldID);
			if (rs.next() && (userRights & 2) != 0) {
				//OK

				if (request.getParameter("Action") != null) {
					String errMsg = "";
					if (request.getParameter("Action").equals("Insert")) {
						try {
							docID = attacher.insertDocument(Integer.parseInt(recID), request, "Upload");
							MetadataRecord mr = attacher.getDocumentForId(docID);
							if (request.getParameter("Name") != null && !request.getParameter("Name").equals("")) { attacher.setTitle(mr, request.getParameter("Name")); }
							if (request.getParameter("Desc") != null && !request.getParameter("Desc").equals("")) { attacher.setNote(mr, request.getParameter("Desc")); }
						} catch (Exception e) {
							errMsg = e.getMessage();
						}
					}
					else if (request.getParameter("Action").equals("Remove")) {
						MetadataRecord[] mr1 = attacher.getDocumentsForId(Integer.parseInt(recID));
						attacher.removeDocument(Integer.parseInt(recID), mr1[Integer.parseInt(request.getParameter("DeleteID"))]);
					}
					response.sendRedirect("binary_data_entry.jsp?RecID=" + recID + "&RecType=" + recType + "&FoldID=" + foldID + "&ErrMsg=" + errMsg);
				}

				out.println("<form enctype='multipart/form-data' method='post' action='binary_data_entry.jsp'>");
				out.println("<input type='hidden' name='RecID' value='" + recID + "'>");
				out.println("<input type='hidden' name='RecType' value='" + recType + "'>");
				out.println("<input type='hidden' name='FoldID' value='" + foldID + "'>");
				out.println("<input type='hidden' name='Action' value='Insert'>");

				out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
				out.println("<tr><td class='heading'>File<br><span class='smalltext'>The following types of files can be loaded: images (JPEG, TIFF, GIF, BMP), Microsoft Word/Excel documents and PDF files</span></td><td><input type='file' name='Upload'></td></tr>");
				out.println("<tr><td class='heading'>Name<br><span class='smalltext'>If different to filename</span></td><td><input type='text' name='Name'></td></tr>");
				out.println("<tr><td class='heading'>Description</td><td><input type='text' name='Desc'></td></tr>");
				out.println("<tr><td><input type='submit' value='Load File'></td></tr>");
				out.println("</table></p>");
				out.println("</form>");


				MetadataRecord[] mr = attacher.getDocumentsForId(Integer.parseInt(recID));
				if (mr != null) {
					out.println("<p><table border='0' cellspacing='0' cellpadding='2'>");
					out.println("<tr><td class='heading' colspan='3'>Files/Images already saved</td></tr>");
					out.println("<tr><td>&nbsp</td></tr>");
					for (int i = 0; i < mr.length; i++) {
						out.println("<tr><td><a href='binary_data_entry.jsp?RecID=" + recID + "&RecType=" + recType + "&FoldID=" + foldID + "&Action=Remove&DeleteID=" + i + "' title='Delete'><img src='images/cancel.gif' width='20' height='20' border='0' /></a></td><td><img border='0' src=/online/Thumbnail?src=" + mr[i].getCode() + " />&nbsp;&nbsp;</td><td>" + mr[i].getTitle() + "</td></tr>");
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
