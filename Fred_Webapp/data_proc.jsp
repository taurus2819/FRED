<%@		page extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.util.*, java.io.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, nz.cri.gns.util.map.*"
%><%
	PageState state = new PageState(request, response, getServletContext());
	User user = (User)getUser(session);

	ExtranetTemplate et = getExtranetTemplate();

	if (request.getParameter("SaveType") != null) {
		DataEntryForm dataEntryForm = (DataEntryForm) session.getAttribute("dataEntryForm");
		try {
			if (request.getParameter("Action") != null) {
				Vector tL = (Vector) session.getAttribute("badTaxaList");
				if (tL != null) {
					for (Iterator i = tL.iterator(); i.hasNext();) {
						Taxa t = (Taxa) i.next();
						t.submitProvisional(user, state);
					}
				}
				dataEntryForm.setField(DataEntryForm.TAXA_LIST, (String) session.getAttribute("taxa"));
			}
			else {
				dataEntryForm.setTempField(DataEntryForm.EDIT_COMMENTS, request.getParameter("EditComm"));
				dataEntryForm.setTempField(DataEntryForm.FEATURE_NAME, request.getParameter("FeatName"));
				dataEntryForm.setTempField(DataEntryForm.REGISTRATION_AREA, request.getParameter("RegAreaID"));
				dataEntryForm.setTempField(DataEntryForm.WORKING_COMMENTS, request.getParameter("WorkComm"));
				dataEntryForm.setTempField(DataEntryForm.SECURITY_TYPE, request.getParameter("SecType"));
				dataEntryForm.setTempField(DataEntryForm.GRID_REF, request.getParameter("GridRef"));
				dataEntryForm.setTempField(DataEntryForm.METHOD, request.getParameter("LocMethodID"));
				dataEntryForm.setTempField(DataEntryForm.ACCURACY, request.getParameter("Accuracy"));
				dataEntryForm.setTempField(DataEntryForm.LOCALITY_DESC, request.getParameter("Loc"));
				dataEntryForm.setTempField(DataEntryForm.RECOLLECTION, request.getParameter("Recoll"));
				dataEntryForm.setTempField(DataEntryForm.OPERATING_COMPANY, request.getParameter("Person"));
				dataEntryForm.setTempField(DataEntryForm.START_DATE, request.getParameter("StartDate"));
				dataEntryForm.setTempField(DataEntryForm.COMPLETION_DATE, request.getParameter("FinishDate"));
				dataEntryForm.setTempField(DataEntryForm.LICENCE_AREA, request.getParameter("LicArea"));
				dataEntryForm.setTempField(DataEntryForm.DATUM_TYPE, request.getParameter("DatumType"));
				dataEntryForm.setTempField(DataEntryForm.DATUM_ELEVATION, request.getParameter("DatumEl"));
				dataEntryForm.setTempField(DataEntryForm.KICK_OFF_DEPTH, request.getParameter("StartDepth"));
				dataEntryForm.setTempField(DataEntryForm.TERMINATION_DEPTH, request.getParameter("FinishDepth"));
				
				//sample property fields
				dataEntryForm.setTempField(DataEntryForm.COLLECTION_DATE, request.getParameter("CollDate"));
				dataEntryForm.setTempField(DataEntryForm.COLLECTORS, request.getParameter("Coll"));
				dataEntryForm.setTempField(DataEntryForm.STRAT_NAME, request.getParameter("StratName"));
				dataEntryForm.setTempField(DataEntryForm.FOSSILS_IN_PLACE, request.getParameter("InPlace"));
				dataEntryForm.setTempField(DataEntryForm.SENT_TO, request.getParameter("SentTo"));
				dataEntryForm.setTempField(DataEntryForm.NOT_COLLECTED, request.getParameter("NotColl"));
				dataEntryForm.setTempField(DataEntryForm.SIGNIFICANCE_COMMENTS, request.getParameter("Sig"));
				dataEntryForm.setTempField(DataEntryForm.INF_AGE_START, request.getParameter("InfStageStart"));
				dataEntryForm.setTempField(DataEntryForm.INF_START_MOD, request.getParameter("InfStartMod"));
				dataEntryForm.setTempField(DataEntryForm.INF_AGE_STOP, request.getParameter("InfStageStop"));
				dataEntryForm.setTempField(DataEntryForm.INF_STOP_MOD, request.getParameter("InfStopMod"));
				dataEntryForm.setTempField(DataEntryForm.KNW_AGE_START, request.getParameter("KnwStageStart"));
				dataEntryForm.setTempField(DataEntryForm.KNW_START_MOD, request.getParameter("KnwStartMod"));
				dataEntryForm.setTempField(DataEntryForm.KNW_AGE_STOP, request.getParameter("KnwStageStop"));
				dataEntryForm.setTempField(DataEntryForm.KNW_STOP_MOD, request.getParameter("KnwStopMod"));
				dataEntryForm.setTempField(DataEntryForm.PREVIOUS_SAMPLE, request.getParameter("PrevSamp"));
				dataEntryForm.setTempField(DataEntryForm.SAMPLE_RELATIONSHIP, request.getParameter("SampRel"));
				dataEntryForm.setTempField(DataEntryForm.STRAT_RELATIONSHIP, request.getParameter("StratRel"));
				dataEntryForm.setTempField(DataEntryForm.COLUMN_MAP, request.getParameter("ColMap"));
				dataEntryForm.setTempField(DataEntryForm.DIP, request.getParameter("Dip"));
				dataEntryForm.setTempField(DataEntryForm.DIP_DIRECTION, request.getParameter("DipDir"));
				dataEntryForm.setTempField(DataEntryForm.STRIKE, request.getParameter("Strike"));
				dataEntryForm.setTempField(DataEntryForm.FACING, request.getParameter("Facing"));
				dataEntryForm.setTempField(DataEntryForm.GRAIN_SIZE_P, request.getParameter("GrainSizeP"));
				dataEntryForm.setTempField(DataEntryForm.GRAIN_SIZE_S, request.getParameter("GrainSizeS"));
				dataEntryForm.setTempField(DataEntryForm.GS_COMP, request.getParameter("GSComp"));
				dataEntryForm.setTempField(DataEntryForm.BEDDING_THICKNESS, request.getParameter("BedThick"));
				dataEntryForm.setTempField(DataEntryForm.BEDDING_P, request.getParameter("BeddingP"));
				dataEntryForm.setTempField(DataEntryForm.BEDDING_S, request.getParameter("BeddingS"));
				dataEntryForm.setTempField(DataEntryForm.WEATHERING, request.getParameter("Weath"));
				dataEntryForm.setTempField(DataEntryForm.HARDNESS, request.getParameter("Hard"));
				dataEntryForm.setTempField(DataEntryForm.CARBONATE, request.getParameter("Carb"));
				dataEntryForm.setTempField(DataEntryForm.COLOUR_MOD, request.getParameter("ColMod"));
				dataEntryForm.setTempField(DataEntryForm.COLOUR_P, request.getParameter("ColourP"));
				dataEntryForm.setTempField(DataEntryForm.COLOUR_S, request.getParameter("ColourS"));
				dataEntryForm.setTempField(DataEntryForm.WET, request.getParameter("Wet"));
				dataEntryForm.setTempField(DataEntryForm.SED_FEATURES, request.getParameter("SedFeat"));
				dataEntryForm.setTempField(DataEntryForm.DEP_ENVIRONMENT_1, request.getParameter("DepEnv1"));
				dataEntryForm.setTempField(DataEntryForm.DEP_ENVIRONMENT_2, request.getParameter("DepEnv2"));
				dataEntryForm.setTempField(DataEntryForm.ROCK_NATURE, request.getParameter("RockNat"));
				dataEntryForm.setTempField(DataEntryForm.CORRESPONDENCE, request.getParameter("Corr"));
				
				//Adoption fields
				dataEntryForm.setTempField(DataEntryForm.ADOPTION_DATE, request.getParameter("AdoDate"));
				dataEntryForm.setTempField(DataEntryForm.ADOPTORS, request.getParameter("Adoptor"));
				dataEntryForm.setTempField(DataEntryForm.ADO_AGE_START, request.getParameter("StageStart"));
				dataEntryForm.setTempField(DataEntryForm.ADO_START_MOD, request.getParameter("StartMod"));
				dataEntryForm.setTempField(DataEntryForm.ADO_AGE_STOP, request.getParameter("StageStop"));
				dataEntryForm.setTempField(DataEntryForm.ADO_STOP_MOD, request.getParameter("StopMod"));
				dataEntryForm.setTempField(DataEntryForm.ADO_COMMENTS, request.getParameter("Comm"));
	
				//Paleontology fields
				dataEntryForm.setTempField(DataEntryForm.IDENTIFICATION_DATE, request.getParameter("PalDate"));
				dataEntryForm.setTempField(DataEntryForm.IDENTIFIERS, request.getParameter("Identifier"));
				dataEntryForm.setTempField(DataEntryForm.IDT_AGE_START, request.getParameter("StageStart"));
				dataEntryForm.setTempField(DataEntryForm.IDT_START_MOD, request.getParameter("StartMod"));
				dataEntryForm.setTempField(DataEntryForm.IDT_AGE_STOP, request.getParameter("StageStop"));
				dataEntryForm.setTempField(DataEntryForm.IDT_STOP_MOD, request.getParameter("StopMod"));
				dataEntryForm.setTempField(DataEntryForm.STAGE_COMMENTS, request.getParameter("StComm"));
				dataEntryForm.setTempField(DataEntryForm.LAB_SECTION, request.getParameter("SectID"));
				dataEntryForm.setTempField(DataEntryForm.LAB_NUMBER, request.getParameter("LabNum"));
				dataEntryForm.setTempField(DataEntryForm.COLLECTION_COMMENTS, request.getParameter("CollComm"));
				dataEntryForm.setTempField(DataEntryForm.TAXA_LIST, request.getParameter("Taxa"));

				session.setAttribute("dataEntryForm", dataEntryForm);

				dataEntryForm.setFieldsFromTemp();

			}

			if (request.getParameter("SaveType").equals("Submit")) {
				dataEntryForm.submit();
			} else {
				dataEntryForm.save();
			}

			response.sendRedirect((String)session.getAttribute("dataEntryRedirect"));
			return;

		} catch (DataInputException e) {
			drawTop(out, et, request, response);
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "' class='heading'>Back to Data Entry</a></td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>Data Error</span></p>");
			out.println("<table border='0' cellspacing='0'>");
			out.println("<tr><td class='heading'>Problem Field<img src='images/blank.gif' width='20' height='1' /></td><td>" + e.getField() + "</td></tr>");
			out.println("<tr><td class='heading'>Error</td><td>"+ e.getMessage() + "</td></tr>");
			out.println("</table>");
		} catch (TaxonomicListException e) {
			drawTop(out, et, request, response);
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "' class='heading'>Back to Data Entry</a></td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			session.setAttribute("taxa", request.getParameter("Taxa"));
			session.setAttribute("badTaxaList", e.getTaxaList());
			session.setAttribute("dataEntryForm", dataEntryForm);
			out.println("<p><span class='bigheading'>Data Error</span></p>");
			out.println("<p>The following list contains taxonomic entries which do not match a value in the thesaurus.  This could be either because you have entered incorrect syntax or because the entry is not in the thesaurus.<br />Note submitted entries will be provisional until checked by database curators and you will not be able to submit this record until the entry has been approved.</p>");
			out.println("<table border='0' cellspacing='2'>");
			out.println("<tr><th>Group&nbsp;&nbsp;</th><th>Entered Name&nbsp;&nbsp;</th><th>Parsed Name&nbsp;&nbsp;</th><th>Author</th></tr>");
			for (Iterator i = e.getTaxaList().iterator(); i.hasNext();) {
				Taxa t = (Taxa) i.next();
				out.println("<tr><td>" + t.getGroupName() + "&nbsp;&nbsp;</td><td>" + t.getTaxonomicName() + "&nbsp;&nbsp;</td><td>" + t.getCleanTaxonomicName() + "&nbsp;&nbsp;</td><td>" + t.getAuthor() + "</td></tr>");
			}
			out.println("</table>");
			out.println("<p><a href='data_proc.jsp?Action=SubmitTaxa&SaveType=Save'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit Taxa' /></a>&nbsp;<a href='data_proc.jsp?Action=SubmitTaxa&SaveType=Save' class='boldlink'>Submit Taxa and Save Record.</a></p>");
			out.println("<p>Note: No data has been saved yet.  You must either choose to submit the above taxa or return to the data entry form, edit and re-save</p>");
		} catch (InvalidCredentialsException e) {
			drawTop(out, et, request, response);
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "' class='heading'>Back to Data Entry</a></td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>Data Error</span></p>");
			out.println("<p>You do not have sufficient rights to save this record</p>");
		} catch (IOException e) {
			drawTop(out, et, request, response);
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "' class='heading'>Back to Data Entry</a></td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>IO Data Error</span></p>");
			out.println("<p>A Database error has occured: " + e.getMessage() + "</p>");
		} catch (SQLException e) {
			drawTop(out, et, request, response);
			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "' class='heading'>Back to Data Entry</a></td></tr>");
			out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>Database Error</span></p>");
			out.println("<p>A Database error has occured: " + e.getMessage() + "</p>");
		}
	}
	else {
		drawTop(out, et, request, response);
		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='heading'>Data Entry Error</td></tr>");
		out.println("<tr><td>&nbsp;</td></tr>");
		out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "'><img src='images/back_arrow.gif' height='20' width='20' border='0' alt='Back to Data Entry' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryErrorRedirect") + "' class='heading'>Back to Data Entry</a></td></tr>");
		out.println("<tr><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='" + (String)session.getAttribute("dataEntryRedirect") + "' class='heading'>Quit</a></td></tr>");
		out.println("</table>");
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");
		out.println("<p><span class='bigheading'>Unidentified Data Entry Error has occured</span></p>");
	}
	out.println("</td></tr></table>");
	drawBottom(out, et);
%>