<%@		page extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, nz.cri.gns.db.metadata.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	//if FeatureID given then get SampleID
	if (request.getParameter("FeatID") != null) {
		String featID = request.getParameter("FeatID");
		try {
			Feature feature = new Feature(Integer.parseInt(request.getParameter("FeatID")), user, state);
			if (feature.get(Feature.SAMPLES) != null)
				response.sendRedirect("print_front.jsp?ID=" + ((Integer) feature.getAsVector(Feature.SAMPLES).firstElement()).toString());
				return;
		} catch (Exception e) {}
	}
	
	out.println("<!DOCTYPE html ");
	out.println("   PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" ");
	out.println("  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> ");
	out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");

	out.println(" <head>");
	out.println("  <title>Fossil Record Electronic Database</title>");
	out.println("  <link rel=\"styleSheet\" href=\"/online/style/extranet.css\" type=\"text/css\" />");
	out.println(" </head>");
	out.println(" <body>");

	if (request.getParameter("ID") != null) {
		String sampID = request.getParameter("ID");
		String formType = request.getParameter("FormType");
		if (formType == null) formType = "Full";

		//List data
		Sample sample = new Sample(Integer.parseInt(sampID), user, state);
		Audit audit = Audit.getAudit(sample.getAsInt(Sample.AUDIT_ID), state);
		out.println("<table border='1' cellspacing='0' cellpadding='10' width='620'>");
		out.println("<tr><td>");

		out.println("<table border='0' cellspacing='0' cellpadding='0' width='600'>");
		out.println("<tr><td rowspan='2'><img src='images/gslogo.gif' width='42' height='50' /></td><td class='smallheading'>GEOLOGICAL SOCIETY OF NEW ZEALAND</td><td class='hugeheading' align='right'>");
		if (sample.get(Sample.FR_NUMBER) != null) {
			out.print(sample.getAsString(Sample.FR_NUMBER));
		} else {
			out.print("_______/f_____");
		}
		out.println("</td></tr>");
		out.print("<tr><td class='hugeheading'>FOSSIL RECORD FORM</td><td align='right' class='heading'>" + FREDUtils.noNulls(sample.getAsString(Sample.FEATURE_TYPE)) + "</td></tr>");		
		out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
		out.println("</table>");

		out.println("<table border='0' cellspacing='0' cellpadding='0' width='600'>");
		if (sample.get(Sample.MASTERFILE_NAME) != null)
			out.println("<tr><td class='smallheading'>Masterfile:&nbsp;</td><td class='smalltext'>" + sample.getAsString(Sample.MASTERFILE_NAME) + "</td></tr>");
		if (audit.get(Audit.APPROVED_BY) != null || audit.get(Audit.APPROVED_DATE) != null) {
			out.println("<tr><td class='smallheading'>MF Curator Approved:&nbsp;</td><td class='smalltext'>");
				if (audit.get(Audit.APPROVED_BY) != null) out.print(audit.getAsString(Audit.APPROVED_BY) + "&nbsp;&nbsp;");
				if (audit.get(Audit.APPROVED_DATE) != null) out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.APPROVED_DATE)));
				out.println("</td></tr>");
			}

		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("</table>");

		out.println("<table border='0' cellspacing='0' cellpadding='2' width='600'>");

		out.println("<tr><td class='bigheading' colspan='2'>Mandatory Data</td></tr>");
		String featType = sample.getAsString(Sample.FEATURE_TYPE);
		if (sample.get(Sample.FEATURE_NAME) != null) {
			if (featType.equals("Outcrop")) {
				out.println("<tr><td class='heading'>Field Number</td><td>" + sample.getAsString(Sample.FEATURE_NAME) + "</td></tr>");
			} else if (featType.equals("Drillhole")) {
				out.println("<tr><td class='heading'>Drillhole Name</td><td>" + sample.getAsString(Sample.FEATURE_NAME) + "</td></tr>");
			} else {
				out.println("<tr><td class='heading'>Section Name</td><td>" + sample.getAsString(Sample.FEATURE_NAME) + "</td></tr>");
			}
		}
		if (sample.get(Sample.LATITUDE) != null) {
			out.print("<tr><td class='heading'>Grid Ref</td><td>");
			if (sample.get(Sample.NZMG_SHEET) != null) {
				out.print(sample.getAsString(Sample.NZMG_SHEET) + ": " + nzmg.format(sample.getAsDouble(Sample.NZMG_EAST)) + ", " + nzmg.format(sample.getAsDouble(Sample.NZMG_NORTH)));
				out.print("&nbsp;&nbsp;|&nbsp;&nbsp;");
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
		if (formType.equals("Full") && sample.isUserAuthenticated() && sample.get(Sample.RECORDS) != null) {

			//Sample Property Data
			for (Iterator i = sample.getAsVector(Sample.RECORDS).iterator(); i.hasNext(); ) {
				KeyValueObject rec = (KeyValueObject)i.next();
				if (rec.getValue().equals("SMP")) {
					try {
						SampPropRecord sampProp = (SampPropRecord) SampPropRecord.getData(Integer.parseInt(rec.getKey()), user, state);
						//collectors (repeating)
						if (sampProp.get(SampPropRecord.COLLECTOR) != null) {
							out.print("<tr><td class='heading'>Collectors</td><td>");
							for (Iterator i2 = sampProp.getAsVector(SampPropRecord.COLLECTOR).iterator(); i2.hasNext(); ) {
								KeyValueObject coll = (KeyValueObject)i2.next();
								out.print(coll.getValue() + "<br />");
							}
							out.print("</td></tr>");
						}
						if (sampProp.get(SampPropRecord.COLLECTION_DATE) != null) { out.print("<tr><td class='heading'>Collection Date</td><td>" + FREDUtils.formatDateForOutput(sampProp.getAsDate(SampPropRecord.COLLECTION_DATE), sampProp.getAsString(SampPropRecord.COLLECTION_DATE_ROUNDING)) + "</td></tr>"); }
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
						
						out.println("<tr><td class='bigheading' colspan='2'>Stratigraphy</td></tr>");
						
						if (sampProp.get(SampPropRecord.SIGNIFICANCE) != null) { out.println("<tr><td class='heading'>Significance</td><td>" + sampProp.getAsString(SampPropRecord.SIGNIFICANCE) + "</td></tr>"); }
						if (sampProp.get(SampPropRecord.INFERRED_STAGE) != null) { out.println("<tr><td class='heading'>Inferred Stage</td><td>" + sampProp.getAsString(SampPropRecord.INFERRED_STAGE) + "</td></tr>"); }
						if (sampProp.get(SampPropRecord.KNOWN_STAGE) != null) { out.println("<tr><td class='heading'>Known Stage</td><td>" + sampProp.getAsString(SampPropRecord.KNOWN_STAGE) + "</td></tr>"); }
						//Nearby samples (repeating)
						if (sampProp.get(SampPropRecord.RELATIONSHIP_NEARBY) != null) {
							out.print("<tr><td class='heading'>Samples Nearby</td><td>");
							for (Iterator i2 = sampProp.getAsVector(SampPropRecord.RELATIONSHIP_NEARBY).iterator(); i2.hasNext(); ) {
								Relationship nearRel = (Relationship)i2.next();
								out.print(nearRel.getDistanceRelation() + " " + nearRel.getRelatedSampleName() +"<br />");
							}
						out.print("</td></tr>");
						}
						//Sample relationships (repeating)
						if (sampProp.get(SampPropRecord.RELATIONSHIP_SAMPLE) != null) {
							out.print("<tr><td class='heading'>Sample Relationships</td><td>");
							for (Iterator i2 = sampProp.getAsVector(SampPropRecord.RELATIONSHIP_SAMPLE).iterator(); i2.hasNext(); ) {
								Relationship sampRel = (Relationship)i2.next();
								out.print(sampRel.getDistanceRelation() + " " + sampRel.getRelatedSampleName() + "<br />");
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
						
						out.println("<tr><td class='bigheading' colspan='2'>Sedimentary Features</td></tr>");
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
						out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");
					} catch (Exception e) {}
					break;
				}
			}
		}
		out.println("<img src='images/blank.gif' width='600' height='1' />");
		out.println("</td></tr></table>");
	}
	else { //no record or no rights
		out.println("<p><span class='bigheading'>Access denied</span></p>Either there is no record matching the ID you entered or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.");
	}
%>