<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, java.net.*"
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
%><%!	class DataInputException extends Exception {
			private String field;
			DataInputException() { }
			DataInputException(String field, String msg) { super(msg); this.field = field; }
			public String getField() { return field; }
		}
%><%!	private String cleanAlphaChar (String taxaName, String checkString) {
		int len = taxaName.length();
		int pos = 0;
		boolean ok = true;
		while (ok) {
			pos = taxaName.indexOf(checkString, pos + 1);
			if (pos > 0 && pos + checkString.length() < len) {
				pos = pos + checkString.length();
				if (pos + 1 == len || pos + 2 == len) {
					taxaName = taxaName.substring(0, pos);
				} else if (taxaName.indexOf(" ", pos + 1) <= pos + 2 && taxaName.indexOf(" ", pos + 1) > 0) {
					taxaName = taxaName.substring(0, pos) + "  " + taxaName.substring(pos + 2, taxaName.length());
				}
			} else {
				ok = false;
			}
		}
		return taxaName;
	}
%><%!	private String cleanTaxaName (String taxaName, String checkString) {
		while (taxaName.indexOf(checkString) >= 0) {
			taxaName = taxaName.substring(0, taxaName.indexOf(checkString)).trim() + " " + taxaName.substring(taxaName.indexOf(checkString) + checkString.length(), taxaName.length()).trim();
			taxaName = taxaName.trim();
		}
		return taxaName;
	}
%><%!	private String cleanTaxaNameOpen (String taxaName, String checkString) {
		taxaName = cleanAlphaChar(taxaName, checkString);
		taxaName = cleanTaxaName(taxaName, "n." + checkString + "indet.");
		taxaName = cleanTaxaName(taxaName, "n. " + checkString + "indet.");
		taxaName = cleanTaxaName(taxaName, "n." + checkString + " indet.");
		taxaName = cleanTaxaName(taxaName, "n. " + checkString + " indet.");
		taxaName = cleanTaxaName(taxaName, "n." + checkString);
		taxaName = cleanTaxaName(taxaName, "n. " + checkString);
		taxaName = cleanTaxaName(taxaName, checkString + "indet.");
		taxaName = cleanTaxaName(taxaName, checkString + " indet.");
		taxaName = cleanTaxaName(taxaName, checkString);
		return taxaName;
	}
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	ResultSet rs;
	User user = getUser(session);
	String foldID, sampID, recID, featStatus, auditID, groupID, taxa, taxaLine, taxaGroup, taxaGroupID[], taxaName[], cleanedTaxaName[], taxaID[], taxaAuthor[], taxaSpecCount[], taxaSpecCoord[], taxaComm[];
	taxaGroupID = new String[200];
	taxaName = new String[200];
	cleanedTaxaName = new String[200];
	taxaID = new String[200];
	taxaAuthor = new String[200];
	taxaSpecCount = new String[200];
	taxaSpecCoord = new String[200];
	taxaComm = new String[200];
	int userID = user.getPersonId(), execUp, i = -1;
	boolean taxaListFlag = true, submitFlag = false;

	taxa = (String)session.getAttribute("Taxa");
	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	if (request.getParameter("FoldID") != null && request.getParameter("RecID") != null && request.getParameter("SampID") != null && request.getParameter("Status") != null && request.getParameter("AuditID") != null && taxa != null) {

		foldID = request.getParameter("FoldID");
		sampID = request.getParameter("SampID");
		recID = request.getParameter("RecID");
		featStatus = request.getParameter("Status");
		auditID = request.getParameter("Audit");

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/pal.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='heading'>Paleontology Record</td></tr>");
		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("<tr><td><a href='javascript:history.back();' title='Back to Data Entry'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='javascript:history.back();' class='heading'>Back to Data Entry</a></td></tr>");
		out.println("<tr><td><a href='pal_data_proc2.jsp?FoldID=" + foldID + "&SampID=" + sampID + "&RecID=" + recID + "&Status=" + featStatus + "&AuditID=" + auditID + "&Submit=Yes' title='Submit Taxa'><img src='images/submit.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='pal_data_proc2.jsp?FoldID=" + foldID + "&SampID=" + sampID + "&RecID=" + recID + "&Status=" + featStatus + "&AuditID=" + auditID + "&Submit=Yes' class='heading'>Submit Taxa</a></td></tr>");
		out.println("<tr><td><a href='folder_detail.jsp?ID=" + foldID + "' title='Quit Without Saving'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_detail.jsp?ID=" + foldID + "' class='heading'>Quit</a></td></tr>");
		out.println("</table>");

		drawEndNavigation(out);

		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		if (request.getParameter("Submit") != null) { submitFlag = true; }

		try { //Surround with exception testing so can throw an exception if data problem

			//Taxonomic names
			if (!taxa.equals("")) {
				while (taxa.length() > 0) {
					if (taxa.indexOf("\n") == -1) { taxa = taxa + "\n"; }
					taxaLine = taxa.substring(0, taxa.indexOf("\n")).trim();
					taxaGroup = taxaLine.substring(0, taxaLine.indexOf("*"));
					taxaName[++i] = taxaLine.substring(taxaGroup.length() + 1, taxaLine.indexOf("*", taxaGroup.length() + 1));
					taxaAuthor[i] = taxaLine.substring(taxaGroup.length() + taxaName[i].length() + 2, taxaLine.indexOf("*", taxaGroup.length() + taxaName[i].length() + 2));
					taxaSpecCount[i] = taxaLine.substring(taxaGroup.length() + taxaName[i].length() + taxaAuthor[i].length() + 3, taxaLine.indexOf("*", taxaGroup.length() + taxaName[i].length() + taxaAuthor[i].length() + 3));
					taxaSpecCoord[i] = taxaLine.substring(taxaGroup.length() + taxaName[i].length() + taxaAuthor[i].length() + taxaSpecCount[i].length() + 4, taxaLine.indexOf("*", taxaGroup.length() + taxaName[i].length() + taxaAuthor[i].length() + taxaSpecCount[i].length() + 4));
					taxaComm[i] = taxaLine.substring(taxaLine.lastIndexOf("*") + 1, taxaLine.length());
					taxa = taxa.substring(taxa.indexOf("\n") + 1, taxa.length()).trim();
					//check TaxaGroup against lookup values
					rs = statement.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = " + JspUtils.sqlEscape(taxaGroup) + " AND FieldName = 'TaxaGroup'");
					if (rs.next()) {
						taxaGroupID[i] = rs.getString(1);
					} else {  // not valid group
						throw new DataInputException("Taxonomic", taxaGroup + " not a valid taxonomic group");
					}
					//clean TaxaName
					cleanedTaxaName[i] = taxaName[i];
					cleanedTaxaName[i] = cleanTaxaNameOpen(cleanedTaxaName[i], "subsp.");
					cleanedTaxaName[i] = cleanTaxaNameOpen(cleanedTaxaName[i], "subspp.");
					cleanedTaxaName[i] = cleanTaxaNameOpen(cleanedTaxaName[i], "sp.");
					cleanedTaxaName[i] = cleanTaxaNameOpen(cleanedTaxaName[i], "spp.");
					cleanedTaxaName[i] = cleanTaxaNameOpen(cleanedTaxaName[i], "subgen.");
					cleanedTaxaName[i] = cleanTaxaNameOpen(cleanedTaxaName[i], "gen.");
					cleanedTaxaName[i] = cleanTaxaNameOpen(cleanedTaxaName[i], "subfam.");
					cleanedTaxaName[i] = cleanTaxaNameOpen(cleanedTaxaName[i], "fam.");
					cleanedTaxaName[i] = cleanTaxaName(cleanedTaxaName[i], "indet.");
					cleanedTaxaName[i] = cleanTaxaName(cleanedTaxaName[i], "?");
					cleanedTaxaName[i] = cleanTaxaName(cleanedTaxaName[i], "cf.");
					cleanedTaxaName[i] = cleanTaxaName(cleanedTaxaName[i], "aff.");
					cleanedTaxaName[i] = cleanTaxaName(cleanedTaxaName[i], "MS.");
					cleanedTaxaName[i] = cleanTaxaName(cleanedTaxaName[i], "s.s");
					cleanedTaxaName[i] = cleanTaxaName(cleanedTaxaName[i], "s.l.");
					cleanedTaxaName[i] = cleanTaxaName(cleanedTaxaName[i], "gr.");
					//check TaxaName against thesaurus
					rs = statement.executeQuery("SELECT Taxa_ID FROM Taxonomic_Lookup WHERE Taxonomic_Name = " + JspUtils.sqlEscape(cleanedTaxaName[i]) + " AND Status IN ('approved', 'provisional')");
					if (rs.next()) {
						taxaID[i] = rs.getString(1);
					} else {  // not valid name
						if (submitFlag) { //add to theasurus
							rs = statement.executeQuery("SELECT Taxa_Seq.NEXTVAL FROM Dual");
							rs.next();
							taxaID[i] = rs.getString(1);
							execUp = statement.executeUpdate("INSERT INTO Taxonomic_Lookup (Taxa_ID, Group_ID, Taxonomic_Name, Author, Status, Submitted_By_ID, Submitted_Date) VALUES (" + taxaID[i] + ", " + taxaGroupID[i] + ", " + JspUtils.sqlEscape(cleanedTaxaName[i]) + ", " + JspUtils.sqlEscape(taxaAuthor[i]) + ", 'provisional', " + userID + ", SYSDATE)");
						} else {
							taxaListFlag = false;
						}
					}
				}
			}

			if (taxaListFlag) {
				//Create PAL_LIST entry
				for (i = 0; i < 200; i++) {
					if (taxaGroupID[i] != null) {
						execUp = statement.executeUpdate("INSERT INTO Pal_List (Record_ID, Group_ID, Taxa_ID, Taxonomic_Name, Specimen_Count, Specimen_Coords, Comments) VALUES (" + recID + ", " + taxaGroupID[i] + ", " + taxaID[i] + ", " + JspUtils.sqlEscape(taxaName[i]) + ", " + JspUtils.sqlEscape(taxaSpecCount[i]) + ", " + JspUtils.sqlEscape(taxaSpecCoord[i]) + ", " + JspUtils.sqlEscape(taxaComm[i]) + ")");
					} else {
						break;
					}
				}

				if (featStatus.equals("approved")) { //submitted
					//change status & add saved record to folder
					execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'approved', Submitted_By_ID = " + userID + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL, Working_Comments = NULL WHERE Audit_ID = " + auditID);
					rs = statement.executeQuery("SELECT * FROM Folder_Content_View WHERE Sample_ID = " + sampID + " AND Folder_ID = " + foldID);
					if (!rs.next()) {
						rs = statement.executeQuery("SELECT Feature_ID FROM Sample WHERE Sample_ID = " + sampID);
						rs.next();
						execUp = statement.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) VALUES (" + foldID + ", " + rs.getString(1) + ")");
					}
				}
				response.sendRedirect("folder_detail.jsp?ID=" + foldID);
			}
			else { //bad taxa entries
				out.println("<p><div class='bigheading'>Taxonomic Name Error</div></p>");
				out.println("<p>The following list contains taxonomic entries which do not match a value in the theasurus.  This could be either because you have entered incorrect syntax or because the entry is not in the theasurus.<br />Note submitted entries will be provisional until checked by database curators and you will not be able to submit this record until the entry has been approved.</p>");
				out.println("<table border='0' cellspacing='0'>");
				out.println("<tr><th>Group</th><th>Entered Name</th><th>Parsed Name</th><th>Author</th></tr>");
				for (i = 0; i < 200; i++) {
					if (taxaGroupID[i] != null) {
						if (taxaID[i] == null) {
							rs = statement.executeQuery("SELECT Name FROM Lookup WHERE Lookup_ID = " + taxaGroupID[i] + " AND FieldName = 'TaxaGroup'");
							rs.next();
							out.println("<tr><td>" + rs.getString(1) + "<img src='images/blank.gif' width='20' height='1' /></td><td>" + taxaName[i] + "<img src='images/blank.gif' width='20' height='1' /></td><td>" + cleanedTaxaName[i] + "<img src='images/blank.gif' width='20' height='1' /></td><td>" + taxaAuthor[i] + "</td></tr>");
						}
					} else {
						break;
					}
				}
				out.println("</table>");
			}

		} catch (DataInputException e) {
			out.println("<p><div class='bigheading'>Data Error</div></p>");
			out.println("<table border='0' cellspacing='0'>");
			out.println("<tr><td class='heading'>Problem Field<img src='images/blank.gif' width='20' height='1' /></td><td>" + e.getField() + "</td></tr>");
			out.println("<tr><td class='heading'>Error</td><td>"+ e.getMessage() + "</td></tr>");
			out.println("</table>");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
