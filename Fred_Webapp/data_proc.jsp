<%@		page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.util.*, java.io.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, nz.cri.gns.util.map.*"
%><%
	PageState state = new PageState(request, response, getServletContext());
	User user = getUser(session);

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	if (request.getParameter("SaveType") != null) {

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='heading'>Locality</td></tr>");
		out.println("<tr><td>&nbsp;</td></tr>");
		out.println("<tr><td><a href='javascript:history.back();' title='Back to Data Entry'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='javascript:history.back();' class='heading'>Back to Data Entry</a></td></tr>");
		out.println("<tr><td><a href='" + request.getParameter("Redirect") + "' title='Quit Without Saving'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + request.getParameter("Redirect") + "' class='heading'>Quit</a></td></tr>");
		out.println("</table>");

		drawEndNavigation(out);

		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		DataEntryForm dataEntryForm = (DataEntryForm) session.getAttribute("dataEntryForm");

		if (request.getParameter("Action") != null) {
			Vector tL = (Vector) session.getAttribute("badTaxaList");
			if (tL != null) {
				for (Iterator i = tL.iterator(); i.hasNext();) {
					Taxa t = (Taxa) i.next();
					t.submitProvisional(user, state);
				}
			}
			try {
				dataEntryForm.setField(DataEntryForm.TAXA_LIST, (String) session.getAttribute("taxa"));
			} catch (DataInputException e) {
				out.println("<p><span class='bigheading'>Data Error</span></p>");
				out.println("<table border='0' cellspacing='0'>");
				out.println("<tr><td class='heading'>Problem Field<img src='images/blank.gif' width='20' height='1' /></td><td>" + e.getField() + "</td></tr>");
				out.println("<tr><td class='heading'>Error</td><td>"+ e.getMessage() + "</td></tr>");
				out.println("</table>");
			} catch (TaxonomicListException e) {
				session.setAttribute("taxa", request.getParameter("Taxa"));
				session.setAttribute("badTaxaList", e.getTaxaList());
				session.setAttribute("dataEntryForm", dataEntryForm);
				out.println("<p><span class='bigheading'>Data Error</span></p>");
				out.println("<p>The following list contains taxonomic entries which do not match a value in the theasurus.  This could be either because you have entered incorrect syntax or because the entry is not in the theasurus.<br />Note submitted entries will be provisional until checked by database curators and you will not be able to submit this record until the entry has been approved.</p>");
				out.println("<table border='0' cellspacing='2'>");
				out.println("<tr><th>Group</th><th>Entered Name</th><th>Parsed Name</th><th>Author</th></tr>");
				for (Iterator i = e.getTaxaList().iterator(); i.hasNext();) {
					Taxa t = (Taxa) i.next();
					out.println("<tr><td>" + t.getGroupName() + "&nbsp;&nbsp;</td><td>" + t.getTaxonomicName() + "&nbsp;&nbsp;</td><td>" + t.getCleanTaxonomicName() + "&nbsp;&nbsp;</td><td>" + t.getAuthor() + "</td></tr>");
				}
				out.println("</table>");
				out.println("<p><a href='data_proc.jsp?Action=SubmitTaxa&SaveType=" + request.getParameter("SaveType") + "&Redirect=" + request.getParameter("Redirect") + "'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit Taxa' /></a>&nbsp;<a href='data_proc.jsp?Action=SubmitTaxa&SaveType=" + request.getParameter("SaveType") + "&Redirect=" + request.getParameter("Redirect") + "' class='boldlink'>Submit Taxa and " + request.getParameter("SaveType") + " record.</a></p>");
				out.println("<p>Note: No data has been saved yet.  You must either choose to submit the above taxa or return to the data entry form, edit and re-save</p>");
			}
		}
		else {
			try {
				dataEntryForm.setField(DataEntryForm.FEATURE_NAME, request.getParameter("FeatName"));
				dataEntryForm.setField(DataEntryForm.REGISTRATION_AREA, request.getParameter("RegAreaID"));
				dataEntryForm.setField(DataEntryForm.WORKING_COMMENTS, request.getParameter("WorkComm"));
				dataEntryForm.setField(DataEntryForm.SECURITY_TYPE, request.getParameter("SecType"));
				dataEntryForm.setField(DataEntryForm.GRID_REF, request.getParameter("GridRef"));
				dataEntryForm.setField(DataEntryForm.METHOD, request.getParameter("LocMethodID"));
				dataEntryForm.setField(DataEntryForm.ACCURACY, request.getParameter("Accuracy"));
				dataEntryForm.setField(DataEntryForm.LOCALITY_DESC, request.getParameter("Loc"));
				dataEntryForm.setField(DataEntryForm.RECOLLECTION, request.getParameter("Recoll"));
				dataEntryForm.setField(DataEntryForm.OPERATING_COMPANY, request.getParameter("Person"));
				dataEntryForm.setField(DataEntryForm.START_DATE, request.getParameter("StartDate"));
				dataEntryForm.setField(DataEntryForm.COMPLETION_DATE, request.getParameter("FinishDate"));
				dataEntryForm.setField(DataEntryForm.LICENCE_AREA, request.getParameter("LicArea"));
				dataEntryForm.setField(DataEntryForm.DATUM_TYPE, request.getParameter("DatumType"));
				dataEntryForm.setField(DataEntryForm.DATUM_ELEVATION, request.getParameter("DatumEl"));
				dataEntryForm.setField(DataEntryForm.KICK_OFF_DEPTH, request.getParameter("StartDepth"));
				dataEntryForm.setField(DataEntryForm.TERMINATION_DEPTH, request.getParameter("FinishDepth"));
				
				//sample property fields
				dataEntryForm.setField(DataEntryForm.COLLECTION_DATE, request.getParameter("CollDate"));
				dataEntryForm.setField(DataEntryForm.COLLECTORS, request.getParameter("Coll"));
				dataEntryForm.setField(DataEntryForm.STRAT_NAME, request.getParameter("StratName"));
				dataEntryForm.setField(DataEntryForm.FOSSILS_IN_PLACE, request.getParameter("InPlace"));
				dataEntryForm.setField(DataEntryForm.SENT_TO, request.getParameter("SentTo"));
				dataEntryForm.setField(DataEntryForm.NOT_COLLECTED, request.getParameter("NotColl"));
				dataEntryForm.setField(DataEntryForm.SIGNIFICANCE_COMMENTS, request.getParameter("Sig"));
				dataEntryForm.setField(DataEntryForm.INF_AGE_START, request.getParameter("InfStageStart"));
				dataEntryForm.setField(DataEntryForm.INF_START_MOD, request.getParameter("InfStartMod"));
				dataEntryForm.setField(DataEntryForm.INF_AGE_STOP, request.getParameter("InfStageStop"));
				dataEntryForm.setField(DataEntryForm.INF_STOP_MOD, request.getParameter("InfStopMod"));
				dataEntryForm.setField(DataEntryForm.KNW_AGE_START, request.getParameter("KnwStageStart"));
				dataEntryForm.setField(DataEntryForm.KNW_START_MOD, request.getParameter("KnwStartMod"));
				dataEntryForm.setField(DataEntryForm.KNW_AGE_STOP, request.getParameter("KnwStageStop"));
				dataEntryForm.setField(DataEntryForm.KNW_STOP_MOD, request.getParameter("KnwStopMod"));
				dataEntryForm.setField(DataEntryForm.PREVIOUS_SAMPLE, request.getParameter("PrevSamp"));
				dataEntryForm.setField(DataEntryForm.SAMPLE_RELATIONSHIP, request.getParameter("SampRel"));
				dataEntryForm.setField(DataEntryForm.STRAT_RELATIONSHIP, request.getParameter("StratRel"));
				dataEntryForm.setField(DataEntryForm.COLUMN_MAP, request.getParameter("ColMap"));
				dataEntryForm.setField(DataEntryForm.DIP, request.getParameter("Dip"));
				dataEntryForm.setField(DataEntryForm.DIP_DIRECTION, request.getParameter("DipDir"));
				dataEntryForm.setField(DataEntryForm.STRIKE, request.getParameter("Strike"));
				dataEntryForm.setField(DataEntryForm.FACING, request.getParameter("Facing"));
				dataEntryForm.setField(DataEntryForm.GRAIN_SIZE_P, request.getParameter("GrainSizeP"));
				dataEntryForm.setField(DataEntryForm.GRAIN_SIZE_S, request.getParameter("GrainSizeS"));
				dataEntryForm.setField(DataEntryForm.GS_COMP, request.getParameter("GSComp"));
				dataEntryForm.setField(DataEntryForm.BEDDING_THICKNESS, request.getParameter("BedThick"));
				dataEntryForm.setField(DataEntryForm.BEDDING_P, request.getParameter("BeddingP"));
				dataEntryForm.setField(DataEntryForm.BEDDING_S, request.getParameter("BeddingS"));
				dataEntryForm.setField(DataEntryForm.WEATHERING, request.getParameter("Weath"));
				dataEntryForm.setField(DataEntryForm.HARDNESS, request.getParameter("Hard"));
				dataEntryForm.setField(DataEntryForm.CARBONATE, request.getParameter("Carb"));
				dataEntryForm.setField(DataEntryForm.COLOUR_MOD, request.getParameter("ColMod"));
				dataEntryForm.setField(DataEntryForm.COLOUR_P, request.getParameter("ColourP"));
				dataEntryForm.setField(DataEntryForm.COLOUR_S, request.getParameter("ColourS"));
				dataEntryForm.setField(DataEntryForm.WET, request.getParameter("Wet"));
				dataEntryForm.setField(DataEntryForm.SED_FEATURES, request.getParameter("SedFeat"));
				dataEntryForm.setField(DataEntryForm.DEP_ENVIRONMENT_1, request.getParameter("DepEnv1"));
				dataEntryForm.setField(DataEntryForm.DEP_ENVIRONMENT_2, request.getParameter("DepEnv2"));
				dataEntryForm.setField(DataEntryForm.ROCK_NATURE, request.getParameter("RockNat"));
				dataEntryForm.setField(DataEntryForm.CORRESPONDENCE, request.getParameter("Corr"));
				
				//Adoption fields
				dataEntryForm.setField(DataEntryForm.ADOPTION_DATE, request.getParameter("AdoDate"));
				dataEntryForm.setField(DataEntryForm.ADOPTORS, request.getParameter("Adoptor"));
				dataEntryForm.setField(DataEntryForm.ADO_AGE_START, request.getParameter("StageStart"));
				dataEntryForm.setField(DataEntryForm.ADO_START_MOD, request.getParameter("StartMod"));
				dataEntryForm.setField(DataEntryForm.ADO_AGE_STOP, request.getParameter("StageStop"));
				dataEntryForm.setField(DataEntryForm.ADO_STOP_MOD, request.getParameter("StopMod"));
				dataEntryForm.setField(DataEntryForm.ADO_COMMENTS, request.getParameter("Comm"));
	
				//Paleontology fields
				dataEntryForm.setField(DataEntryForm.IDENTIFICATION_DATE, request.getParameter("PalDate"));
				dataEntryForm.setField(DataEntryForm.IDENTIFIERS, request.getParameter("Identifier"));
				dataEntryForm.setField(DataEntryForm.IDT_AGE_START, request.getParameter("StageStart"));
				dataEntryForm.setField(DataEntryForm.IDT_START_MOD, request.getParameter("StartMod"));
				dataEntryForm.setField(DataEntryForm.IDT_AGE_STOP, request.getParameter("StageStop"));
				dataEntryForm.setField(DataEntryForm.IDT_STOP_MOD, request.getParameter("StopMod"));
				dataEntryForm.setField(DataEntryForm.STAGE_COMMENTS, request.getParameter("StComm"));
				dataEntryForm.setField(DataEntryForm.LAB_SECTION, request.getParameter("SectID"));
				dataEntryForm.setField(DataEntryForm.LAB_NUMBER, request.getParameter("LabNum"));
				dataEntryForm.setField(DataEntryForm.COLLECTION_COMMENTS, request.getParameter("CollComm"));
				dataEntryForm.setField(DataEntryForm.TAXA_LIST, request.getParameter("Taxa"));

				
			} catch (DataInputException e) {
				out.println("<p><span class='bigheading'>Data Error</span></p>");
				out.println("<table border='0' cellspacing='0'>");
				out.println("<tr><td class='heading'>Problem Field<img src='images/blank.gif' width='20' height='1' /></td><td>" + e.getField() + "</td></tr>");
				out.println("<tr><td class='heading'>Error</td><td>"+ e.getMessage() + "</td></tr>");
				out.println("</table>");
			} catch (TaxonomicListException e) {
				session.setAttribute("taxa", request.getParameter("Taxa"));
				session.setAttribute("badTaxaList", e.getTaxaList());
				session.setAttribute("dataEntryForm", dataEntryForm);
				out.println("<p><span class='bigheading'>Data Error</span></p>");
				out.println("<p>The following list contains taxonomic entries which do not match a value in the theasurus.  This could be either because you have entered incorrect syntax or because the entry is not in the theasurus.<br />Note submitted entries will be provisional until checked by database curators and you will not be able to submit this record until the entry has been approved.</p>");
				out.println("<table border='0' cellspacing='2'>");
				out.println("<tr><th>Group&nbsp;&nbsp;</th><th>Entered Name&nbsp;&nbsp;</th><th>Parsed Name&nbsp;&nbsp;</th><th>Author</th></tr>");
				for (Iterator i = e.getTaxaList().iterator(); i.hasNext();) {
					Taxa t = (Taxa) i.next();
					out.println("<tr><td>" + t.getGroupName() + "&nbsp;&nbsp;</td><td>" + t.getTaxonomicName() + "&nbsp;&nbsp;</td><td>" + t.getCleanTaxonomicName() + "&nbsp;&nbsp;</td><td>" + t.getAuthor() + "</td></tr>");
				}
				out.println("</table>");
				out.println("<p><a href='data_proc.jsp?Action=SubmitTaxa&SaveType=" + request.getParameter("SaveType") + "&Redirect=" + request.getParameter("Redirect") + "'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit Taxa' /></a>&nbsp;<a href='data_proc.jsp?Action=SubmitTaxa&SaveType=" + request.getParameter("SaveType") + "&Redirect=" + request.getParameter("Redirect") + "' class='boldlink'>Submit Taxa and " + request.getParameter("SaveType") + " record.</a></p>");
				out.println("<p>Note: No data has been saved yet.  You must either choose to submit the above taxa or return to the data entry form, edit and re-save</p>");
			}
		}

		try {
			if (request.getParameter("SaveType").equals("Submit")) {
				dataEntryForm.submit();
			} else {
				dataEntryForm.save();
			}
			response.sendRedirect(request.getParameter("Redirect"));
		} catch (InvalidCredentialsException e) {
			out.println("<p><span class='bigheading'>Data Error</span></p>");
			out.println("<p>You do not have sufficient rights to save this record</p>");
		} catch (IOException e) {
			out.println("<p><span class='bigheading'>IO Data Error</span></p>");
			out.println("<p>Database Error</p>");
		} catch (SQLException e) {
			out.println("<p><span class='bigheading'>SQL Data Error</span></p>");
			out.println("<p>Database Error</p>");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>