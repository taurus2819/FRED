<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.fred.*, nz.cri.gns.db.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, nz.cri.gns.db.metadata.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	User user = getUser(session);
	PageState state = new PageState(request, response, getServletContext());
	//DocumentAttacher attacher = DocumentAttacher.createFREDDocumentAttacher(session, application);
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");
	String sampID, recID, featType;
	boolean authorChk = false, sCountChk = false, sCoordChk = false, commChk = true;

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	//if FeatureID given then get SampleID or transer to drillhole
	if (request.getParameter("FeatID") != null) {
		String featID = request.getParameter("FeatID");
		try {
			Feature feature = new Feature(Integer.parseInt(request.getParameter("FeatID")), user, state);
			if (feature.get(Feature.SAMPLES) != null) {
				if (feature.getAsVector(Feature.SAMPLES).size() > 1) {
					response.sendRedirect("drillhole_detail.jsp?ID=" + featID);
				} else {
					response.sendRedirect("detail.jsp?ID=" + ((SampleHeader) feature.getAsVector(Feature.SAMPLES).firstElement()).getSampleID());
				}
			} else {
				response.sendRedirect("drillhole_detail.jsp?ID=" + featID);
			}
		} catch (Exception e) {
			response.sendRedirect("drillhole_detail.jsp?ID=" + featID);
		}
	}

	//get SampleID
	if (request.getParameter("ID") != null) {
		sampID = request.getParameter("ID");
		session.setAttribute("SampleID", sampID);
	} else {
		sampID = (String) session.getAttribute("SampleID");
	}

	drawTop(out, et, request, response);

	if (sampID != null) {

		if (request.getParameter("AuthorChk") != null && request.getParameter("AuthorChk").equals("true")) { authorChk = true; }
		if (request.getParameter("SCountChk") != null && request.getParameter("SCountChk").equals("true")) { sCountChk = true; }
		if (request.getParameter("SCoordChk") != null && request.getParameter("SCoordChk").equals("true")) { sCoordChk = true; }
		if (request.getParameter("CommChk") != null && request.getParameter("CommChk").equals("false")) { commChk = false; }

		//List data
		out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
		try {
			Sample sample = new Sample(Integer.parseInt(sampID), user, state);
			Audit audit = Audit.getAudit(sample.getAsInt(Sample.AUDIT_ID), state);
			featType = sample.getAsString(Sample.FEATURE_TYPE);
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='bigheading' >" + sample.getAsString(Sample.SAMPLE_NAME) + "</td></tr>");
			out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
			if (sample.get(Sample.MASTERFILE_NAME) != null) {
				out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + sample.getAsString(Sample.MASTERFILE_NAME) + "</td></tr>");
			}
			if (!audit.getAsString(Audit.STATUS).equals("approved")) {
				out.println("<tr><td class='smallheading'>Status:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + audit.getAsString(Audit.STATUS) + "</td></tr>");
			}
			if (audit.get(Audit.CREATED_BY) != null || audit.get(Audit.CREATED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Created:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.CREATED_BY) != null) { out.print(audit.getAsString(Audit.CREATED_BY) + "<br />"); }
				if (audit.get(Audit.CREATED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.CREATED_DATE))); }
				out.println("</td></tr>");
			}
			if (audit.get(Audit.MODIFIED_BY) != null || audit.get(Audit.MODIFIED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Edited:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.MODIFIED_BY) != null) { out.print(audit.getAsString(Audit.MODIFIED_BY) + "<br />"); }
				if (audit.get(Audit.MODIFIED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.MODIFIED_DATE))); }
				out.println("</td></tr>");
			}
			if (audit.get(Audit.SUBMITTED_BY) != null || audit.get(Audit.SUBMITTED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Submitted:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.SUBMITTED_BY) != null) { out.print(audit.getAsString(Audit.SUBMITTED_BY) + "<br />"); }
				if (audit.get(Audit.SUBMITTED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.SUBMITTED_DATE))); }
				out.println("</td></tr>");
			}
			if (audit.get(Audit.APPROVED_BY) != null || audit.get(Audit.APPROVED_DATE) != null) {
				out.println("<tr><td class='smallheading'>Approved:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>");
				if (audit.get(Audit.APPROVED_BY) != null) { out.print(audit.getAsString(Audit.APPROVED_BY) + "<br />"); }
				if (audit.get(Audit.APPROVED_DATE) != null) { out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.APPROVED_DATE))); }
				out.println("</td></tr>");
			}
			if (user != null) {
				out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
				out.println("<tr><td colspan='2'><a href='print_front.jsp?ID=" + sampID + "' title='Print' target='print'><img src='images/print.gif' width='20' height='20' border='0' /></a><img src='images/blank.gif' width='10' height='1' border='0' /><a href='print_front.jsp?ID=" + sampID + "' class='heading' target='print'>Print Front</a></td></tr>");
				out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
				out.println("<tr><td class='heading' colspan='2' align='center'>Taxonomic List Options</td></tr>");
				out.println("<form name='TaxaForm' method='post' action='detail.jsp'>");
				out.println("<input type='hidden' name='ID' value='" + sampID + "'>");
				out.println("<input type='hidden' name='AuthorChk' value='" + authorChk + "'>");
				out.println("<input type='hidden' name='SCountChk' value='" + sCountChk + "'>");
				out.println("<input type='hidden' name='SCoordChk' value='" + sCoordChk + "'>");
				out.println("<input type='hidden' name='CommChk' value='" + commChk + "'>");
				out.print("<tr><td colspan='2' class='heading'>");
				if (authorChk) {
					out.print("<a href='#' onClick='document.TaxaForm.AuthorChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
				} else {
					out.print("<a href='#' onClick='document.TaxaForm.AuthorChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
				}
				out.println("</a><img src='images/blank.gif' width='10' height='1' />Author</td></tr>");
				out.print("<tr><td colspan='2' class='heading'>");
				if (sCountChk) {
					out.print("<a href='#' onClick='document.TaxaForm.SCountChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
				} else {
					out.print("<a href='#' onClick='document.TaxaForm.SCountChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
				}
				out.println("</a><img src='images/blank.gif' width='10' height='1' />Specimen Count</td></tr>");
				out.print("<tr><td colspan='2' class='heading'>");
				if (sCoordChk) {
					out.print("<a href='#' onClick='document.TaxaForm.SCoordChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
				} else {
					out.print("<a href='#' onClick='document.TaxaForm.SCoordChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
				}
				out.println("</a><img src='images/blank.gif' width='10' height='1' />Specimen Coord</td></tr>");
				out.print("<tr><td colspan='2' class='heading'>");
				if (commChk) {
					out.print("<a href='#' onClick='document.TaxaForm.CommChk.value=\"false\";document.TaxaForm.submit();' title='Hide'><img src='images/ok.gif' width='20' height='20' border='0' />");
				} else {
					out.print("<a href='#' onClick='document.TaxaForm.CommChk.value=\"true\";document.TaxaForm.submit();' title='Show'><img src='images/cancel.gif' width='20' height='20' border='0' />");
				}
				out.println("</a><img src='images/blank.gif' width='10' height='1' />Comments</td></tr>");
				out.println("</form>");
			}
			out.println("</table>");
			drawEndNavigation(out);
			
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			
			//Locality Data
			out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
			if (sample.get(Sample.YARD_FR_ID) != null) { out.println("<tr><td class='heading'>Yard FR Number</td><td>" + sample.getAsString(Sample.YARD_FR_NUMBER) + "</td></tr>"); }
			if (sample.get(Sample.LATITUDE) != null) {
				out.print("<tr><td class='heading'>Grid Ref</td><td>");
				if (sample.get(Sample.NZMG_SHEET) != null) {
					out.print(sample.getAsString(Sample.NZMG_SHEET) + ": " + nzmg.format(sample.getAsDouble(Sample.NZMG_EAST)) + ", " + nzmg.format(sample.getAsDouble(Sample.NZMG_NORTH)));
					out.print("<img src='images/blank.gif' width='20' height='1' />|<img src='images/blank.gif' width='20' height='1' />");
				}
				if (sample.getAsDouble(Sample.LATITUDE) > 0) {
					out.print(latlong.format(sample.getAsDouble(Sample.LATITUDE)) + "&#176N");
				} else {
					out.print(latlong.format(Math.abs(sample.getAsDouble(Sample.LATITUDE))) + "&#176S");
				}
				out.print("/");
				if (sample.getAsDouble(Sample.LONGITUDE) > 0) {
					out.print(latlong.format(sample.getAsDouble(Sample.LONGITUDE)) + "&#176E");
				} else {
					out.print(latlong.format(Math.abs(sample.getAsDouble(Sample.LONGITUDE))) + "&#176W");
				}
				if (sample.get(Sample.ACCURACY) != null) { out.print(" (&#177 " + sample.getAsInt(Sample.ACCURACY) + "m)"); }
				out.println("</td></tr>");
			}
			if (sample.get(Sample.METHOD) != null) { out.println("<tr><td class='heading'>Method</td><td>" + sample.getAsString(Sample.METHOD) + "</td></tr>"); }
			if (featType.equals("Outcrop")) {
				if (sample.get(Sample.FEATURE_NAME) != null) { out.println("<tr><td class='heading'>Field Number</td><td>" + sample.getAsString(Sample.FEATURE_NAME) + "</td></tr>"); }
			} else {
				if (featType.equals("Drillhole")) {
					if (sample.get(Sample.FEATURE_NAME) != null) { out.println("<tr><td class='heading'>Drillhole Name</td><td><a href='drillhole_detail.jsp?ID=" + sample.getAsString(Sample.FEATURE_ID) + "'>" + sample.getAsString(Sample.FEATURE_NAME) + "</a></td></tr>"); }
					if (sample.get(Sample.DRILLHOLE_DEPTH) != null) { out.println("<tr><td class='heading'>Sample Depth</td><td>" + sample.getAsString(Sample.DRILLHOLE_DEPTH) + "</td></tr>"); }
					out.println("<tr><td class='heading'>Other Drillhole Samples</td><td>");
				} else { //VertSect
					if (sample.get(Sample.FEATURE_NAME) != null) { out.println("<tr><td class='heading'>Section Name</td><td><a href='drillhole_detail.jsp?ID=" + sample.getAsString(Sample.FEATURE_ID) + "'>" + sample.getAsString(Sample.FEATURE_NAME) + "</a></td></tr>"); }
					if (sample.get(Sample.DRILLHOLE_DEPTH) != null) { out.println("<tr><td class='heading'>Sample Height</td><td>" + sample.getAsString(Sample.DRILLHOLE_DEPTH) + "</td></tr>"); }
					out.println("<tr><td class='heading'>Other Section Samples</td><td>");
				}
				//check for samples above and below current one
				try {
					Sample sampleAbove = FREDUtils.getSampleAbove(sample, user, state);
					out.println("Sample Above: <a href='detail.jsp?ID=" + sampleAbove.getAsString(Sample.SAMPLE_ID) + "'>" + sampleAbove.getAsString(Sample.DRILLHOLE_DEPTH) + "</a><br>");
				} catch (Exception e) {}
				try {
					Sample sampleBelow = FREDUtils.getSampleBelow(sample, user, state);
					out.println("Sample Below: <a href='detail.jsp?ID=" + sampleBelow.getAsString(Sample.SAMPLE_ID) + "'>" + sampleBelow.getAsString(Sample.DRILLHOLE_DEPTH) + "</a><br>");
				} catch (Exception e) {}
				out.println("</td></tr>");
			}
			if (sample.isUserAuthenticated() && sample.get(Sample.LOCALITY) != null) { out.println("<tr><td class='heading'>Locality</td><td>" + sample.getAsString(Sample.LOCALITY) + "</td></tr>"); }
			if (!featType.equals("Outcrop")) {
				if (sample.isUserAuthenticated() && sample.get(Sample.PERSON) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Operating Company");
					} else {
						out.print("Section Collector");
					}
					out.println("</td><td>" + sample.getAsString(Sample.PERSON) + "</td></tr>");
				}
				if (sample.isUserAuthenticated() && sample.get(Sample.START_DATE) != null) {
					out.print("<tr><td class='heading'>");
					if (featType.equals("Drillhole")) {
						out.print("Spud Date");
					} else {
						out.print("Sampling Start Date");
					}
					out.print("</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.START_DATE), sample.getAsString(Sample.START_DATE_ROUNDING)) + "</td></tr>");
				}
				if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DATE) != null) {
					out.print("<tr><td class='heading'>Completion Date</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.FINISH_DATE), sample.getAsString(Sample.FINISH_DATE_ROUNDING)) + "</td></tr>");
				}
				if (featType.equals("Drillhole") && sample.isUserAuthenticated() && sample.get(Sample.DRILLHOLE_LICENCE_NAME) != null) { out.println("<tr><td class='heading' width='135'>Licence Area</td><td>" + sample.getAsString(Sample.DRILLHOLE_LICENCE_NAME) + "</td></tr>"); }
				if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_TYPE) != null) { out.println("<tr><td class='heading' width='135'>Datum Type</td><td>" + sample.getAsString(Sample.DATUM_TYPE) + "</td></tr>"); }
				if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_ELEVATION) != null) { out.println("<tr><td class='heading' width='135'>Datum Elevation</td><td>" + sample.getAsString(Sample.DATUM_ELEVATION) + " m asl</td></tr>"); }
				if (sample.isUserAuthenticated() && sample.get(Sample.START_DEPTH) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Kick-off Depth");
					} else {
						out.print("Top Horizon");
					}
					out.println("</td><td>" + sample.getAsString(Sample.START_DEPTH) + " m</td></tr>");
				}
				if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DEPTH) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Termination Depth");
					} else {
						out.print("Base Horizon");
					}
					out.println("</td><td>" + sample.getAsString(Sample.FINISH_DEPTH) + " m</td></tr>");
				}
			}
			out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");	

			if (sample.isUserAuthenticated() && sample.get(Sample.RECORDS) != null) {

				//Sample Property Data
				for (Iterator i = sample.getAsVector(Sample.RECORDS).iterator(); i.hasNext(); ) {
					KeyValueObject rec = (KeyValueObject)i.next();
					if (rec.getValue().equals("SMP")) {
						try {
							SampPropRecord sampProp = SampPropRecord.getData(Integer.parseInt(rec.getKey()), user, state);
							out.println("<tr><td class='bigheading' colspan='2'>Sample Property Data</td></tr>");
							//collectors (repeating)
							if (sampProp.get(SampPropRecord.COLLECTOR) != null) {
								out.print("<tr><td class='heading'>Collectors</td><td>");
								for (Iterator i2 = sampProp.getAsVector(SampPropRecord.COLLECTOR).iterator(); i2.hasNext(); ) {
									KeyValueObject coll = (KeyValueObject)i2.next();
									out.print(coll.getValue() + "<br />");
								}
								out.print("</td></tr>");
							}
							if (sampProp.get(SampPropRecord.COLLECTION_DATE) != null) { out.print("<tr><td class='heading'>Collection Date</td><td>" + FREDUtils.formatDateForOutput(sampProp.getAsDate(SampPropRecord.COLLECTION_DATE), sampProp.getAsString(SampPropRecord.DATE_ROUNDING)) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.STRAT_UNIT) != null) { out.println("<tr><td class='heading'>Strat Name</td><td>" + sampProp.getAsString(SampPropRecord.STRAT_UNIT) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.IN_PLACE) != null) { out.println("<tr><td class='heading'>In Place</td><td>" + sampProp.getAsString(SampPropRecord.IN_PLACE) + "</td></tr>"); }
							//sent to (repeating)
							if (sampProp.get(SampPropRecord.SENT_TO) != null) {
								out.print("<tr><td class='heading'>Sent To</td><td>");
								for (Iterator i2 = sampProp.getAsVector(SampPropRecord.SENT_TO).iterator(); i2.hasNext(); ) {
									SentTo sentTo = (SentTo)i2.next();
									out.print(sentTo.getSentTo() + "<br />");
								}
							out.print("</td></tr>");
							}
							if (sampProp.get(SampPropRecord.NOT_COLLECTED) != null) { out.println("<tr><td class='heading'>Not Collected</td><td>" + sampProp.getAsString(SampPropRecord.NOT_COLLECTED) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.SIGNIFICANCE) != null) { out.println("<tr><td class='heading'>Significance</td><td>" + sampProp.getAsString(SampPropRecord.SIGNIFICANCE) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.INFERRED_STAGE) != null) { out.println("<tr><td class='heading'>Inferred Stage</td><td>" + sampProp.getAsString(SampPropRecord.INFERRED_STAGE) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.KNOWN_STAGE) != null) { out.println("<tr><td class='heading'>Known Stage</td><td>" + sampProp.getAsString(SampPropRecord.KNOWN_STAGE) + "</td></tr>"); }
							//Nearby samples (repeating)
							if (sampProp.get(SampPropRecord.RELATIONSHIP_NEARBY) != null) {
								out.print("<tr><td class='heading'>Samples Nearby</td><td>");
								for (Iterator i2 = sampProp.getAsVector(SampPropRecord.RELATIONSHIP_NEARBY).iterator(); i2.hasNext(); ) {
									Relationship nearRel = (Relationship)i2.next();
									out.print(nearRel.getDistanceRelation() + " <a href='detail.jsp?FeatID=" + nearRel.getRelatedFeatureId() + "'>" + nearRel.getRelatedSampleName() +"</a><br />");
								}
							out.print("</td></tr>");
							}
							//Sample relationships (repeating)
							if (sampProp.get(SampPropRecord.RELATIONSHIP_SAMPLE) != null) {
								out.print("<tr><td class='heading'>Sample Relationships</td><td>");
								for (Iterator i2 = sampProp.getAsVector(SampPropRecord.RELATIONSHIP_SAMPLE).iterator(); i2.hasNext(); ) {
									Relationship sampRel = (Relationship)i2.next();
									out.print(sampRel.getDistanceRelation() + " <a href='detail.jsp?FeatID=" + sampRel.getRelatedFeatureId() + "'>" + sampRel.getRelatedSampleName() + "</a><br />");
								}
							out.print("</td></tr>");
							}
							//Strat relationships (repeating)
							if (sampProp.get(SampPropRecord.RELATIONSHIP_STRAT) != null) {
								out.print("<tr><td class='heading'>Stratigraphic Relationships</td><td>");
								for (Iterator i2 = sampProp.getAsVector(SampPropRecord.RELATIONSHIP_STRAT).iterator(); i2.hasNext(); ) {
									Relationship stratRel = (Relationship)i2.next();
									out.print(stratRel.getRelationship() + "<br />");
								}
							out.print("</td></tr>");
							}
							if (sampProp.get(SampPropRecord.COLUMN_MAP) != null) { out.println("<tr><td class='heading'>Column/Map</td><td>" + sampProp.getAsString(SampPropRecord.COLUMN_MAP) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.DIP) != null) { out.println("<tr><td class='heading'>Dip</td><td>" + sampProp.getAsString(SampPropRecord.DIP) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.DIP_DIRECTION) != null) { out.println("<tr><td class='heading'>Dip Direction</td><td>" + sampProp.getAsString(SampPropRecord.DIP_DIRECTION) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.STRIKE) != null) { out.println("<tr><td class='heading'>Strike</td><td>" + sampProp.getAsString(SampPropRecord.STRIKE) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.FACING) != null) { out.println("<tr><td class='heading'>Facing</td><td>" + sampProp.getAsString(SampPropRecord.FACING) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.GRAINSIZE) != null) { out.println("<tr><td class='heading'>Grain Size</td><td>" + sampProp.getAsString(SampPropRecord.GRAINSIZE) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.COMPARATOR_USED) != null) { out.println("<tr><td class='heading'>Comparator Used</td><td>" + sampProp.getAsString(SampPropRecord.COMPARATOR_USED) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.BED_THICKNESS) != null) { out.println("<tr><td class='heading'>Bed Thickness</td><td>" + sampProp.getAsString(SampPropRecord.BED_THICKNESS) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.BEDDING) != null) { out.println("<tr><td class='heading'>Bedding</td><td>" + sampProp.getAsString(SampPropRecord.BEDDING) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.WEATHERING) != null) { out.println("<tr><td class='heading'>Weathering</td><td>" + sampProp.getAsString(SampPropRecord.WEATHERING) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.HARDNESS) != null) { out.println("<tr><td class='heading'>Hardness</td><td>" + sampProp.getAsString(SampPropRecord.HARDNESS) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.CARBONATE) != null) { out.println("<tr><td class='heading'>Carbonate</td><td>" + sampProp.getAsString(SampPropRecord.CARBONATE) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.COLOUR) != null) { out.println("<tr><td class='heading'>Colour</td><td>" + sampProp.getAsString(SampPropRecord.COLOUR) + "</td></tr>"); }
							//sed features (repeating)
							if (sampProp.get(SampPropRecord.SED_FEATURE) != null) {
								out.print("<tr><td class='heading'>Additional Features</td><td>");
								for (Iterator i2 = sampProp.getAsVector(SampPropRecord.SED_FEATURE).iterator(); i2.hasNext(); ) {
									SedFeature sf = (SedFeature)i2.next();
									out.print(sf.getSedFeature() + "<br />");
								}
							out.print("</td></tr>");
							}
							if (sampProp.get(SampPropRecord.DEPOSITION_ENV) != null) { out.println("<tr><td class='heading'>Inferred Environment</td><td>" + sampProp.getAsString(SampPropRecord.DEPOSITION_ENV) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.ROCK_NATURE) != null) { out.println("<tr><td class='heading'>Nature of Rock Unit</td><td>" + sampProp.getAsString(SampPropRecord.ROCK_NATURE) + "</td></tr>"); }
							if (sampProp.get(SampPropRecord.CORRESPONDENCE) != null) { out.println("<tr><td class='heading'>Correspondence</td><td>" + sampProp.getAsString(SampPropRecord.CORRESPONDENCE) + "</td></tr>"); }
				/*			//Image/Files
							MetadataRecord[] mr = attacher.getDocumentsForId(Integer.parseInt(recID));
							if (mr != null) {
								out.println("<tr><td colspan='2' class='heading'>Images/Files</td></tr>");
								out.println("<tr><td colspan='2'><table border='0' cellspacing='0' width='600'>");
								int y = 1;
								out.print("<tr>");
								for (int x = 0; x < mr.length; x++) {
									if (y++ == 5) {
										out.println("</tr><tr>");
										y = 2;
									}
									out.print("<td width='150' align='center' class='smalltext'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "'><br />" + mr[x].getTitle() + "</td>");
								}
								out.println("</td></tr></table></td></tr>");
							}
			*/				out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");
						} catch (Exception e) {
						}
						break;
					}
				}
	
				//Adoption
				for (Iterator i = sample.getAsVector(Sample.RECORDS).iterator(); i.hasNext(); ) {
					KeyValueObject rec = (KeyValueObject)i.next();
					if (rec.getValue().equals("ADO")) {
						try {
							AdoptionRecord ado = AdoptionRecord.getData(Integer.parseInt(rec.getKey()), user, state);
							out.println("<tr><td colspan='2' class='bigheading'>Adoption Data</td></tr>");
							//adoptors (repeating)
							if (ado.get(AdoptionRecord.ADOPTOR) != null) {
								out.print("<tr><td class='heading'>Adoptors</td><td>");
								for (Iterator i2 = ado.getAsVector(AdoptionRecord.ADOPTOR).iterator(); i2.hasNext(); ) {
									KeyValueObject coll = (KeyValueObject)i2.next();
									out.print(coll.getValue() + "<br />");
								}
								out.print("</td></tr>");
							}
							if (ado.get(AdoptionRecord.ADOPTION_DATE) != null) { out.print("<tr><td class='heading'>Adoption Date</td><td>" + FREDUtils.formatDateForOutput(ado.getAsDate(AdoptionRecord.ADOPTION_DATE), ado.getAsString(AdoptionRecord.DATE_ROUNDING)) + "</td></tr>"); }
							if (ado.get(AdoptionRecord.ADOPTED_STAGE) != null) { out.println("<tr><td class='heading'>Adopted Stage</td><td>" + ado.getAsString(AdoptionRecord.ADOPTED_STAGE) + "</td></tr>"); }
							if (ado.get(AdoptionRecord.COMMENTS) != null) { out.println("<tr><td class='heading'>Comments</td><td>" + ado.getAsString(AdoptionRecord.COMMENTS) + "</td></tr>"); }
				/*			//Image/Files
							MetadataRecord[] mr = attacher.getDocumentsForId(Integer.parseInt(recID));
							if (mr != null) {
								out.println("<tr><td colspan='2' class='heading'>Images/Files</td></tr>");
								out.println("<tr><td colspan='2'><table border='0' cellspacing='0' width='600'>");
								int y = 1;
								out.print("<tr>");
								for (int x = 0; x < mr.length; x++) {
									if (y++ == 5) {
										out.println("</tr><tr>");
										y = 2;
									}
									out.print("<td width='150' align='center' class='smalltext'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "'><br />" + mr[x].getTitle() + "</td>");
								}
								out.println("</td></tr></table></td></tr>");
							}
				*/			out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");
						} catch (Exception e) {
						}
					}
				}
	
				//Paleontology
				for (Iterator i = sample.getAsVector(Sample.RECORDS).iterator(); i.hasNext(); ) {
					KeyValueObject rec = (KeyValueObject)i.next();
					if (rec.getValue().equals("PAL")) {
						try {
							PaleontologyRecord pal = PaleontologyRecord.getData(Integer.parseInt(rec.getKey()), user, state);
							out.println("<tr><td colspan='2' class='bigheading'>Paleontology Data</td></tr>");
							//identifiers (repeating)
							if (pal.get(PaleontologyRecord.IDENTIFIER) != null) {
								out.print("<tr><td class='heading'>Identifiers</td><td>");
								for (Iterator i2 = pal.getAsVector(PaleontologyRecord.IDENTIFIER).iterator(); i2.hasNext(); ) {
									KeyValueObject coll = (KeyValueObject)i2.next();
									out.print(coll.getValue() + "<br />");
								}
								out.print("</td></tr>");
							}
							if (pal.get(PaleontologyRecord.IDENTIFICATION_DATE) != null) { out.print("<tr><td class='heading'>Identification Date</td><td>" + FREDUtils.formatDateForOutput(pal.getAsDate(PaleontologyRecord.IDENTIFICATION_DATE), pal.getAsString(PaleontologyRecord.DATE_ROUNDING)) + "</td></tr>"); }
							if (pal.get(PaleontologyRecord.STAGE) != null) { out.println("<tr><td class='heading'>Stage</td><td>" + pal.getAsString(PaleontologyRecord.STAGE) + "</td></tr>"); }
							if (pal.get(PaleontologyRecord.STAGE_COMMENTS) != null) { out.println("<tr><td class='heading'>Stage Comments</td><td>" + pal.getAsString(PaleontologyRecord.STAGE_COMMENTS) + "</td></tr>"); }
							if (pal.get(PaleontologyRecord.LAB) != null) { out.println("<tr><td class='heading'>Lab</td><td>" + pal.getAsString(PaleontologyRecord.LAB) + "</td></tr>"); }
							if (pal.get(PaleontologyRecord.LAB_NUMBER) != null) { out.println("<tr><td class='heading'>Lab Number</td><td>" + pal.getAsString(PaleontologyRecord.LAB_NUMBER) + "</td></tr>"); }
							if (pal.get(PaleontologyRecord.COLLECTION_COMMENTS) != null) { out.println("<tr><td class='heading'>Collection Comments</td><td>" + pal.getAsString(PaleontologyRecord.COLLECTION_COMMENTS) + "</td></tr>"); }
	
							//taxa (double repeating)
							if (pal.get(PaleontologyRecord.TAXONOMIC_LIST) != null) {
								out.println("<tr><td colspan='2'><table border='0' cellspacing='0' cellpadding='2'>");
								for (Iterator i2 = pal.getAsVector(PaleontologyRecord.TAXONOMIC_LIST).iterator(); i2.hasNext(); ) {
									TaxaGroup taxaGroup = (TaxaGroup)i2.next();
									out.println("<tr><td colspan='4' class='heading'>" + taxaGroup.getGroupName() + "</td></tr>");
									if (taxaGroup.getTaxaList() != null) {
										out.print("<tr class='heading'><td>Taxonomic Name&nbsp;&nbsp;</td>");
										if (authorChk) { out.print("<td>Author&nbsp;&nbsp;</td>"); }
										if (sCountChk) { out.print("<td>Spec Count&nbsp;&nbsp;</td>"); }
										if (sCoordChk) { out.print("<td>Spec Coord&nbsp;&nbsp;</td>"); }
										if (commChk) { out.print("<td>Comments&nbsp;&nbsp;</td>"); }
										out.println("</tr>");
										for (Iterator i3 = taxaGroup.getTaxaList().iterator(); i3.hasNext(); ) {
											Taxa taxa = (Taxa)i3.next();
											out.print("<tr><td>" + taxa.getTaxonomicName() + "&nbsp;&nbsp;</td>");
											if (authorChk) { out.print("<td><i>" + noNulls(taxa.getAuthor()) + "</i>&nbsp;&nbsp;</td>"); }
											if (sCountChk) { out.print("<td>" + noNulls(String.valueOf(taxa.getSpecimenCount())) + "&nbsp;&nbsp;</td>"); }
											if (sCoordChk) { out.print("<td>" + noNulls(taxa.getSpecimenCoords()) + "&nbsp;&nbsp;</td>"); }
											if (commChk) { out.print("<td>" + noNulls(taxa.getComments()) + "&nbsp;&nbsp;</td>"); }
											out.println("</tr>");
										}
									} else {
										out.println("<tr><td colspan='4'>No fossils listed</td></tr>");
									}
									out.println("<tr><td><img src='images/blank.gif' height='10' width='1' /></td></tr>");
								}
								out.println("</td></tr></table></td></tr>");
							}
				/*			//Image/Files
							MetadataRecord[] mr = attacher.getDocumentsForId(Integer.parseInt(recID));
							if (mr != null) {
								out.println("<tr><td colspan='2' class='heading'>Images/Files</td></tr>");
								out.println("<tr><td colspan='2'><table border='0' cellspacing='0' width='600'>");
								int y = 1;
								out.print("<tr>");
								for (int x = 0; x < mr.length; x++) {
									if (y++ == 5) {
										out.println("</tr><tr>");
										y = 2;
									}
									out.print("<td width='150' align='center' class='smalltext'><img border='0' src='/online/Thumbnail?src=" + mr[x].getCode() + "'><br />" + mr[x].getTitle() + "</td>");
								}
								out.println("</td></tr></table></td></tr>");
							}
				*/		} catch (Exception e) {
						}
					}
				}
			}
	
			if (user ==  null) { out.println("<tr><td colspan='2'>More data may be available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/detail.jsp") + "' class='boldlink'>logged</a> in users</td></tr>"); }
			out.println("</table></td></tr></table>");
		}
		catch (Exception e) { // no record or not approved
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>Either the sample doesn't exist or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</td></tr>");
			out.println("</table>");
		}

	}
	else { //no sampleID
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>No SampleID received.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</td></tr>");
		out.println("</table>");
	}
	
	drawBottom(out, et); 
%>