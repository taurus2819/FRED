<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.fred.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, nz.cri.gns.db.metadata.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	nz.cri.gns.intranet.DBConnection frConn = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	nz.cri.gns.intranet.DBConnection connection;
	User user = getUser(session);

	PageState state = new PageState(request, response, getServletContext());
	
	Statement preserveStatement;
	Statement preserveStatement2;
	//DocumentAttacher attacher = DocumentAttacher.createFREDDocumentAttacher(session, application);
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");
	ResultSet rs, rs2, rs3;

	String sampID, recID, featType, status = "", query;
	boolean authorChk = false, sCountChk = false, sCoordChk = false, commChk = true;
	int[] types = {Types.NUMERIC};
	Object data[];
	data = new Object[1];
	int[] doubleTypes = {Types.NUMERIC, Types.NUMERIC};
	Object doubleData[];
	doubleData = new Object[2];

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	//if FeatureID given then get SampleID or transer to drillhole
	if (request.getParameter("FeatID") != null) {
		query = "SELECT MIN(Sample_ID), COUNT(*) FROM Sample WHERE Feature_ID = ?";
		data[0] = new Integer(Integer.parseInt(request.getParameter("FeatID")));
		rs = frConn.executeQuery(query, types, data);
		if (rs.next()) {
			if (rs.getInt(2) > 1) {
				response.sendRedirect("drillhole_detail.jsp?ID=" + request.getParameter("FeatID"));
			} else {
				response.sendRedirect("detail.jsp?ID=" + rs.getString(1));
			}
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

		//create connection:  userConnection if logged in, otherwise FR
		if (user !=  null) {
			connection = user.getUsersConnection(new PageState(request, response, application), frConn);
		} else {
			connection = frConn;
		}

		if (request.getParameter("AuthorChk") != null && request.getParameter("AuthorChk").equals("true")) { authorChk = true; }
		if (request.getParameter("SCountChk") != null && request.getParameter("SCountChk").equals("true")) { sCountChk = true; }
		if (request.getParameter("SCoordChk") != null && request.getParameter("SCoordChk").equals("true")) { sCoordChk = true; }
		if (request.getParameter("CommChk") != null && request.getParameter("CommChk").equals("false")) { commChk = false; }

		//List data
		out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
		//try {
			FullSample fullSample = FullSample.getFullSample(Integer.parseInt(sampID), user, state);
			Audit audit = Audit.getAudit(fullSample.getAsInt(FullSample.AUDIT_ID), state);

			out.println("SecID: " + fullSample.getAsString(FullSample.SECURITY_CLASS_ID) + "<br/>");
			out.println("Authenticated: " + fullSample.isAuthenticated() + "<br/>");
			out.println("Pool Size: " + FullSample.getPoolSize() + "<br />");

			featType = fullSample.getAsString(FullSample.FEATURE_TYPE);
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='bigheading' >" + fullSample.getAsString(FullSample.SAMPLE_NAME) + "</td></tr>");
			out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
			if (fullSample.get(fullSample.MASTERFILE_NAME) != null) {
				out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + fullSample.getAsString(FullSample.MASTERFILE_NAME) + "</td></tr>");
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
			if (fullSample.get(FullSample.YARD_FR_ID) != null) { out.println("<tr><td class='heading'>Yard FR Number</td><td>" + fullSample.getAsString(FullSample.YARD_FR_NUMBER) + "</td></tr>"); }
			if (fullSample.get(FullSample.LATITUDE) != null) {
				out.print("<tr><td class='heading'>Grid Ref</td><td>");
				if (fullSample.get(FullSample.NZMG_SHEET) != null) {
					out.print(fullSample.getAsString(FullSample.NZMG_SHEET) + ": " + nzmg.format(fullSample.getAsDouble(FullSample.NZMG_EAST)) + ", " + nzmg.format(fullSample.getAsDouble(FullSample.NZMG_NORTH)));
					out.print("<img src='images/blank.gif' width='20' height='1' />|<img src='images/blank.gif' width='20' height='1' />");
				}
				if (fullSample.getAsDouble(FullSample.LATITUDE) > 0) {
					out.print(latlong.format(fullSample.getAsDouble(FullSample.LATITUDE)) + "&#176N");
				} else {
					out.print(latlong.format(Math.abs(fullSample.getAsDouble(FullSample.LATITUDE))) + "&#176S");
				}
				out.println("/");
				if (fullSample.getAsDouble(FullSample.LONGITUDE) > 0) {
					out.print(latlong.format(fullSample.getAsDouble(FullSample.LONGITUDE)) + "&#176E");
				} else {
					out.print(latlong.format(Math.abs(fullSample.getAsDouble(FullSample.LONGITUDE))) + "&#176W");
				}
				if (fullSample.get(FullSample.ACCURACY) != null) { out.print(" (&#177 " + fullSample.getAsDouble(FullSample.ACCURACY) + "m)"); }
				out.println("</td></tr>");
			}
			if (fullSample.get(FullSample.METHOD) != null) { out.println("<tr><td class='heading'>Method</td><td>" + fullSample.getAsString(FullSample.METHOD) + "</td></tr>"); }
			if (featType.equals("Outcrop")) {
				if (fullSample.get(FullSample.FEATURE_NAME) != null) { out.println("<tr><td class='heading'>Field Number</td><td>" + fullSample.getAsString(FullSample.FEATURE_NAME) + "</td></tr>"); }
			} else {
				if (featType.equals("Drillhole")) {
					if (fullSample.get(FullSample.FEATURE_NAME) != null) { out.println("<tr><td class='heading'>Drillhole Name</td><td><a href='drillhole_detail.jsp?ID=" + fullSample.getAsString(FullSample.FEATURE_ID) + "'>" + fullSample.getAsString(FullSample.FEATURE_NAME) + "</a></td></tr>"); }
					if (fullSample.isAuthenticated() && fullSample.get(FullSample.DRILLHOLE_DEPTH) != null) { out.println("<tr><td class='heading'>Sample Depth</td><td>" + fullSample.getAsString(FullSample.DRILLHOLE_DEPTH) + "</td></tr>"); }
					out.println("<tr><td class='heading'>Other Drillhole Samples</td><td>");
				} else { //VertSect
					if (fullSample.get(FullSample.FEATURE_NAME) != null) { out.println("<tr><td class='heading'>Section Name</td><td><a href='drillhole_detail.jsp?ID=" + fullSample.getAsString(FullSample.FEATURE_ID) + "'>" + fullSample.getAsString(FullSample.FEATURE_NAME) + "</a></td></tr>"); }
					if (fullSample.isAuthenticated() && fullSample.get(FullSample.DRILLHOLE_DEPTH) != null) { out.println("<tr><td class='heading'>Sample Height</td><td>" + fullSample.getAsString(FullSample.DRILLHOLE_DEPTH) + "</td></tr>"); }
					out.println("<tr><td class='heading'>Other Section Samples</td><td>");
				}
				//check for samples above and below current one
				try {
					FullSample sampleAbove = FREDUtils.getSampleAbove(fullSample, user, state);
					out.println("Sample Above: <a href='detail.jsp?ID=" + sampleAbove.getAsString(FullSample.SAMPLE_ID) + "'>" + sampleAbove.getAsString(FullSample.DRILLHOLE_DEPTH) + "</a><br>");
				} catch (Exception e) {}
				try {
					FullSample sampleBelow = FREDUtils.getSampleBelow(fullSample, user, state);
					out.println("Sample Below: <a href='detail.jsp?ID=" + sampleBelow.getAsString(FullSample.SAMPLE_ID) + "'>" + sampleBelow.getAsString(FullSample.DRILLHOLE_DEPTH) + "</a><br>");
				} catch (Exception e) {}
				out.println("</td></tr>");
			}
			if (fullSample.isAuthenticated() && fullSample.get(FullSample.LOCALITY) != null) { out.println("<tr><td class='heading'>Locality</td><td>" + fullSample.getAsString(FullSample.LOCALITY) + "</td></tr>"); }
			if (!featType.equals("Outcrop")) {
				if (fullSample.isAuthenticated() && fullSample.get(FullSample.PERSON) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Operating Company");
					} else {
						out.print("Section Collector");
					}
					out.println("</td><td>" + fullSample.getAsString(FullSample.PERSON) + "</td></tr>");
				}
				if (fullSample.isAuthenticated() && fullSample.get(FullSample.START_DATE) != null) {
					out.print("<tr><td class='heading'>");
					if (featType.equals("Drillhole")) {
						out.print("Spud Date");
					} else {
						out.print("Sampling Start Date");
					}
					out.print("</td><td>" + FREDUtils.formatDateForOutput(fullSample.getAsDate(FullSample.START_DATE), fullSample.getAsString(FullSample.START_DATE_ROUNDING)) + "</td></tr>");
				}
				if (fullSample.isAuthenticated() && fullSample.get(FullSample.FINISH_DATE) != null) {
					out.print("<tr><td class='heading'>Completion Date</td><td>" + FREDUtils.formatDateForOutput(fullSample.getAsDate(FullSample.FINISH_DATE), fullSample.getAsString(FullSample.FINISH_DATE_ROUNDING)) + "</td></tr>");
				}
				if (featType.equals("Drillhole") && fullSample.isAuthenticated() && fullSample.get(FullSample.DRILLHOLE_LICENCE_NAME) != null) { out.println("<tr><td class='heading' width='135'>Licence Area</td><td>" + fullSample.getAsString(FullSample.DRILLHOLE_LICENCE_NAME) + "</td></tr>"); }
				if (fullSample.isAuthenticated() && fullSample.get(FullSample.DATUM_TYPE) != null) { out.println("<tr><td class='heading' width='135'>Datum Type</td><td>" + fullSample.getAsString(FullSample.DATUM_TYPE) + "</td></tr>"); }
				if (fullSample.isAuthenticated() && fullSample.get(FullSample.DATUM_ELEVATION) != null) { out.println("<tr><td class='heading' width='135'>Datum Elevation</td><td>" + fullSample.getAsString(FullSample.DATUM_ELEVATION) + " m asl</td></tr>"); }
				if (fullSample.isAuthenticated() && fullSample.get(FullSample.START_DEPTH) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Kick-off Depth");
					} else {
						out.print("Top Horizon");
					}
					out.println("</td><td>" + fullSample.getAsString(FullSample.START_DEPTH) + " m</td></tr>");
				}
				if (fullSample.isAuthenticated() && fullSample.get(FullSample.FINISH_DEPTH) != null) {
					out.print("<tr><td class='heading' width='135'>");
					if (featType.equals("Drillhole")) {
						out.print("Termination Depth");
					} else {
						out.print("Base Horizon");
					}
					out.println("</td><td>" + fullSample.getAsString(FullSample.FINISH_DEPTH) + " m</td></tr>");
				}
			}
			out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");	
	
			//Sample Property Data
			for (Iterator i = fullSample.getAsVector(FullSample.RECORD).iterator(); i.hasNext(); ) {
				KeyValueObject rec = (KeyValueObject)i.next();
				out.println("<tr><td>" + rec.getKey() + ":" + rec.getValue() + "</td></tr>");
				if (rec.getValue().equals("SMP")) {
					try {
						FullSampPropRecord sampProp = FullSampPropRecord.getFullSampPropRecord(Integer.parseInt(rec.getKey()), user, state);
						out.println("<tr><td class='bigheading' colspan='2'>Sample Property Data</td></tr>");
						//collectors (repeating)
						if (sampProp.get(FullSampPropRecord.COLLECTOR) != null) {
							out.print("<tr><td class='heading'>Collectors</td><td>");
							for (Iterator i2 = sampProp.getAsVector(FullSampPropRecord.COLLECTOR).iterator(); i2.hasNext(); ) {
								KeyValueObject coll = (KeyValueObject)i2.next();
								out.print(coll.getValue() + "<br />");
							}
							out.print("</td></tr>");
						}
						if (sampProp.get(FullSampPropRecord.COLLECTION_DATE) != null) { out.print("<tr><td class='heading'>Collection Date</td><td>" + FREDUtils.formatDateForOutput(sampProp.getAsDate(FullSampPropRecord.COLLECTION_DATE), sampProp.getAsString(FullSampPropRecord.DATE_ROUNDING)) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.STRAT_UNIT) != null) { out.println("<tr><td class='heading'>Strat Name</td><td>" + sampProp.getAsString(FullSampPropRecord.STRAT_UNIT) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.IN_PLACE) != null) { out.println("<tr><td class='heading'>In Place</td><td>" + sampProp.getAsString(FullSampPropRecord.IN_PLACE) + "</td></tr>"); }
						//sent to (repeating)
						if (sampProp.get(FullSampPropRecord.SENT_TO) != null) {
							out.print("<tr><td class='heading'>Sent To</td><td>");
							for (Iterator i2 = sampProp.getAsVector(FullSampPropRecord.SENT_TO).iterator(); i2.hasNext(); ) {
								SentTo sentTo = (SentTo)i2.next();
								out.print(sentTo.getSentTo() + "<br />");
							}
						out.print("</td></tr>");
						}
						if (sampProp.get(FullSampPropRecord.NOT_COLLECTED) != null) { out.println("<tr><td class='heading'>Not Collected</td><td>" + sampProp.getAsString(FullSampPropRecord.NOT_COLLECTED) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.SIGNIFICANCE) != null) { out.println("<tr><td class='heading'>Significance</td><td>" + sampProp.getAsString(FullSampPropRecord.SIGNIFICANCE) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.INFERRED_STAGE) != null) { out.println("<tr><td class='heading'>Inferred Stage</td><td>" + sampProp.getAsString(FullSampPropRecord.INFERRED_STAGE) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.KNOWN_STAGE) != null) { out.println("<tr><td class='heading'>Known Stage</td><td>" + sampProp.getAsString(FullSampPropRecord.KNOWN_STAGE) + "</td></tr>"); }
						//Nearby samples (repeating)
						if (sampProp.get(FullSampPropRecord.RELATIONSHIP_NEARBY) != null) {
							out.print("<tr><td class='heading'>Samples Nearby</td><td>");
							for (Iterator i2 = sampProp.getAsVector(FullSampPropRecord.RELATIONSHIP_NEARBY).iterator(); i2.hasNext(); ) {
								Relationship nearRel = (Relationship)i2.next();
								out.print(nearRel.getDistanceRelation() + " <a href='detail.jsp?FeatID=" + nearRel.getRelatedFeatureId() + "'>" + nearRel.getRelatedSampleName() +"</a><br />");
							}
						out.print("</td></tr>");
						}
						//Sample relationships (repeating)
						if (sampProp.get(FullSampPropRecord.RELATIONSHIP_SAMPLE) != null) {
							out.print("<tr><td class='heading'>Sample Relationships</td><td>");
							for (Iterator i2 = sampProp.getAsVector(FullSampPropRecord.RELATIONSHIP_SAMPLE).iterator(); i2.hasNext(); ) {
								Relationship sampRel = (Relationship)i2.next();
								out.print(sampRel.getDistanceRelation() + " <a href='detail.jsp?FeatID=" + sampRel.getRelatedFeatureId() + "'>" + sampRel.getRelatedSampleName() + "</a><br />");
							}
						out.print("</td></tr>");
						}
						//Strat relationships (repeating)
						if (sampProp.get(FullSampPropRecord.RELATIONSHIP_STRAT) != null) {
							out.print("<tr><td class='heading'>Stratigraphic Relationships</td><td>");
							for (Iterator i2 = sampProp.getAsVector(FullSampPropRecord.RELATIONSHIP_STRAT).iterator(); i2.hasNext(); ) {
								Relationship stratRel = (Relationship)i2.next();
								out.print(stratRel.getRelationship() + "<br />");
							}
						out.print("</td></tr>");
						}
						if (sampProp.get(FullSampPropRecord.COLUMN_MAP) != null) { out.println("<tr><td class='heading'>Column/Map</td><td>" + sampProp.getAsString(FullSampPropRecord.COLUMN_MAP) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.DIP) != null) { out.println("<tr><td class='heading'>Dip</td><td>" + sampProp.getAsString(FullSampPropRecord.DIP) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.DIP_DIRECTION) != null) { out.println("<tr><td class='heading'>Dip Direction</td><td>" + sampProp.getAsString(FullSampPropRecord.DIP_DIRECTION) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.STRIKE) != null) { out.println("<tr><td class='heading'>Strike</td><td>" + sampProp.getAsString(FullSampPropRecord.STRIKE) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.FACING) != null) { out.println("<tr><td class='heading'>Facing</td><td>" + sampProp.getAsString(FullSampPropRecord.FACING) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.GRAINSIZE) != null) { out.println("<tr><td class='heading'>Grain Size</td><td>" + sampProp.getAsString(FullSampPropRecord.GRAINSIZE) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.COMPARATOR_USED) != null) { out.println("<tr><td class='heading'>Comparator Used</td><td>" + sampProp.getAsString(FullSampPropRecord.COMPARATOR_USED) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.BED_THICKNESS) != null) { out.println("<tr><td class='heading'>Bed Thickness</td><td>" + sampProp.getAsString(FullSampPropRecord.BED_THICKNESS) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.BEDDING) != null) { out.println("<tr><td class='heading'>Bedding</td><td>" + sampProp.getAsString(FullSampPropRecord.BEDDING) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.WEATHERING) != null) { out.println("<tr><td class='heading'>Weathering</td><td>" + sampProp.getAsString(FullSampPropRecord.WEATHERING) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.HARDNESS) != null) { out.println("<tr><td class='heading'>Hardness</td><td>" + sampProp.getAsString(FullSampPropRecord.HARDNESS) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.CARBONATE) != null) { out.println("<tr><td class='heading'>Carbonate</td><td>" + sampProp.getAsString(FullSampPropRecord.CARBONATE) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.COLOUR) != null) { out.println("<tr><td class='heading'>Colour</td><td>" + sampProp.getAsString(FullSampPropRecord.COLOUR) + "</td></tr>"); }
						//sed features (repeating)
						if (sampProp.get(FullSampPropRecord.SED_FEATURE) != null) {
							out.print("<tr><td class='heading'>Additional Features</td><td>");
							for (Iterator i2 = sampProp.getAsVector(FullSampPropRecord.SED_FEATURE).iterator(); i2.hasNext(); ) {
								SedFeature sf = (SedFeature)i2.next();
								out.print(sf.getSedFeature() + "<br />");
							}
						out.print("</td></tr>");
						}
						if (sampProp.get(FullSampPropRecord.DEPOSITION_ENV) != null) { out.println("<tr><td class='heading'>Inferred Environment</td><td>" + sampProp.getAsString(FullSampPropRecord.DEPOSITION_ENV) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.ROCK_NATURE) != null) { out.println("<tr><td class='heading'>Nature of Rock Unit</td><td>" + sampProp.getAsString(FullSampPropRecord.ROCK_NATURE) + "</td></tr>"); }
						if (sampProp.get(FullSampPropRecord.CORRESPONDENCE) != null) { out.println("<tr><td class='heading'>Correspondence</td><td>" + sampProp.getAsString(FullSampPropRecord.CORRESPONDENCE) + "</td></tr>"); }
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
			for (Iterator i = fullSample.getAsVector(FullSample.RECORD).iterator(); i.hasNext(); ) {
				KeyValueObject rec = (KeyValueObject)i.next();
				out.println("<tr><td>" + rec.getKey() + ":" + rec.getValue() + "</td></tr>");
				if (rec.getValue().equals("ADO")) {
					try {
						FullAdoptionRecord ado = FullAdoptionRecord.getFullAdoptionRecord(Integer.parseInt(rec.getKey()), user, state);
						out.println("<tr><td colspan='2' class='bigheading'>Adoption Data</td></tr>");
						//adoptors (repeating)
						if (ado.get(FullAdoptionRecord.ADOPTOR) != null) {
							out.print("<tr><td class='heading'>Adoptors</td><td>");
							for (Iterator i2 = ado.getAsVector(FullAdoptionRecord.ADOPTOR).iterator(); i2.hasNext(); ) {
								KeyValueObject coll = (KeyValueObject)i2.next();
								out.print(coll.getValue() + "<br />");
							}
							out.print("</td></tr>");
						}
						if (ado.get(FullAdoptionRecord.ADOPTION_DATE) != null) { out.print("<tr><td class='heading'>Adoption Date</td><td>" + FREDUtils.formatDateForOutput(ado.getAsDate(FullAdoptionRecord.ADOPTION_DATE), ado.getAsString(FullAdoptionRecord.DATE_ROUNDING)) + "</td></tr>"); }
						if (ado.get(FullAdoptionRecord.ADOPTED_STAGE) != null) { out.println("<tr><td class='heading'>Adopted Stage</td><td>" + ado.getAsString(FullAdoptionRecord.ADOPTED_STAGE) + "</td></tr>"); }
						if (ado.get(FullAdoptionRecord.COMMENTS) != null) { out.println("<tr><td class='heading'>Comments</td><td>" + ado.getAsString(FullAdoptionRecord.COMMENTS) + "</td></tr>"); }
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
			for (Iterator i = fullSample.getAsVector(FullSample.RECORD).iterator(); i.hasNext(); ) {
				KeyValueObject rec = (KeyValueObject)i.next();
				out.println("<tr><td>" + rec.getKey() + ":" + rec.getValue() + "</td></tr>");
				if (rec.getValue().equals("PAL")) {
					try {
						FullPaleontologyRecord pal = FullPaleontologyRecord.getFullPaleontologyRecord(Integer.parseInt(rec.getKey()), user, state);
						out.println("<tr><td colspan='2' class='bigheading'>Paleontology Data</td></tr>");
						//identifiers (repeating)
						if (pal.get(FullPaleontologyRecord.IDENTIFIER) != null) {
							out.print("<tr><td class='heading'>Identifiers</td><td>");
							for (Iterator i2 = pal.getAsVector(FullPaleontologyRecord.IDENTIFIER).iterator(); i2.hasNext(); ) {
								KeyValueObject coll = (KeyValueObject)i2.next();
								out.print(coll.getValue() + "<br />");
							}
							out.print("</td></tr>");
						}
						if (pal.get(FullPaleontologyRecord.IDENTIFICATION_DATE) != null) { out.print("<tr><td class='heading'>Identification Date</td><td>" + FREDUtils.formatDateForOutput(pal.getAsDate(FullPaleontologyRecord.IDENTIFICATION_DATE), pal.getAsString(FullPaleontologyRecord.DATE_ROUNDING)) + "</td></tr>"); }
						if (pal.get(FullPaleontologyRecord.STAGE) != null) { out.println("<tr><td class='heading'>Stage</td><td>" + pal.getAsString(FullPaleontologyRecord.STAGE) + "</td></tr>"); }
						if (pal.get(FullPaleontologyRecord.STAGE_COMMENTS) != null) { out.println("<tr><td class='heading'>Stage Comments</td><td>" + pal.getAsString(FullPaleontologyRecord.STAGE_COMMENTS) + "</td></tr>"); }
						if (pal.get(FullPaleontologyRecord.LAB) != null) { out.println("<tr><td class='heading'>Lab</td><td>" + pal.getAsString(FullPaleontologyRecord.LAB) + "</td></tr>"); }
						if (pal.get(FullPaleontologyRecord.LAB_NUMBER) != null) { out.println("<tr><td class='heading'>Lab Number</td><td>" + pal.getAsString(FullPaleontologyRecord.LAB_NUMBER) + "</td></tr>"); }
						if (pal.get(FullPaleontologyRecord.COLLECTION_COMMENTS) != null) { out.println("<tr><td class='heading'>Collection Comments</td><td>" + pal.getAsString(FullPaleontologyRecord.COLLECTION_COMMENTS) + "</td></tr>"); }

						//taxa (double repeating)
						if (pal.get(FullPaleontologyRecord.TAXONOMIC_LIST) != null) {
							out.println("<tr><td colspan='2'><table border='0' cellspacing='0' cellpadding='2'>");
							for (Iterator i2 = pal.getAsVector(FullPaleontologyRecord.TAXONOMIC_LIST).iterator(); i2.hasNext(); ) {
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
	
			if (user ==  null) { out.println("<tr><td colspan='2'>More data may be available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/detail.jsp") + "' class='boldlink'>logged</a> in users</td></tr>"); }
			out.println("</table></td></tr></table>");
		//}
		//catch (Exception e) { // no record
		//	drawEndNavigation(out);
		//	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		//	out.println("<tr><td>");
		//	out.println("<p>Either the sample doesn't exist or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
		//}

	}
	else { //no sampleID
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");
		out.println("<p>Either the sample doesn't exist or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
	}
	
	drawBottom(out, et); 
%>