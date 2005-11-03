<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.*"
%><%@page import="nz.cri.gns.fred.data.*"
%><%@page import="nz.cri.gns.db.*"
%><%@page import="nz.cri.gns.jsp.*"
%><%@page import="java.util.*"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.db.site.SiteRecord"
%><%@page import="nz.cri.gns.util.map.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	User user = (User)getUser(session);
	PageState state = new PageState(request, response, getServletContext());

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	//if FeatureID given then get SampleID
	if (request.getParameter("FeatID") != null) {
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
		Audit audit = Audit.getAudit(sample.getAsInt(Sample.FEATURE_AUDIT_ID), state);
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
		out.println("<tr><td class='smallheading'>MF Curator Approved:&nbsp;</td><td class='smalltext'>");
		if (audit.get(Audit.APPROVED_BY) != null || audit.get(Audit.APPROVED_DATE) != null) {
			if (audit.get(Audit.APPROVED_BY) != null)
				out.print(audit.getAsString(Audit.APPROVED_BY) + "&nbsp;&nbsp;");
			if (audit.get(Audit.APPROVED_DATE) != null)
				out.print(FREDUtils.formatDateForOutput(audit.getAsDate(Audit.APPROVED_DATE)));
			out.println("</td></tr>");
		}

		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("</table>");

		out.println("<table border='0' cellspacing='0' cellpadding='2' width='600'>");

		out.println("<tr><td class='bigheading' colspan='2'>Mandatory Data</td></tr>");
		String featType = sample.getAsString(Sample.FEATURE_TYPE);
		if (sample.get(Sample.FEATURE_NAME) != null) {
			if (featType.equals(Feature.OUTCROP_LOCALITY)) {
				out.println("<tr><td class='heading'>Field Number</td><td>" + sample.getAsString(Sample.FEATURE_NAME) + "</td></tr>");
			} else if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
				out.println("<tr><td class='heading'>Drillhole Name</td><td>" + sample.getAsString(Sample.FEATURE_NAME) + "</td></tr>");
			} else {
				out.println("<tr><td class='heading'>Section Name</td><td>" + sample.getAsString(Sample.FEATURE_NAME) + "</td></tr>");
			}
		}
//		if (sample.get(Sample.LATITUDE) != null) {
//			if (sample.get(Sample.NZMG_SHEET) != null) {
//				out.print("<tr><td class='heading'>Grid Ref</td><td>" + sample.getAsString(Sample.NZMG_SHEET) + ":" + nzmg.format(sample.getAsDouble(Sample.NZMG_EAST)) + "|" + nzmg.format(sample.getAsDouble(Sample.NZMG_NORTH)) + " (NZMG)");
//			} else if (sample.getAsInt(Sample.ORIG_SYSTEM_ID) == 29) {
//			} else if (sample.getAsInt(Sample.ORIG_SYSTEM_ID) == 28 || sample.getAsInt(Sample.ORIG_SYSTEM_ID) == 30) {
//				String str = sample.getAsString(Sample.ORIG_COORD);
//				int index = str.indexOf("|");
//				double lat = Double.parseDouble(str.substring(0, index));
//				double lon = Double.parseDouble(str.substring(index+1));
//				out.print("<tr><td class='heading'>Lat/Long</td><td>" + FREDUtils.formatLatLongForOutput(lat, lon) + " (" + sample.getAsString(Sample.COORD_SYSTEM).replaceAll("Lat/long ", "") + ")");
//			} else {
//				out.print("<tr><td class='heading'>Grid Ref</td><td>" + sample.getAsString(Sample.ORIG_COORD) + " (" + sample.getAsString(Sample.COORD_SYSTEM) + ")");
//			}
//			out.println("</td></tr>");
//			out.print("<tr><td class='heading'>Lat/Long</td><td>");
//			out.print(FREDUtils.formatLatLongForOutput(sample.getAsDouble(Sample.LATITUDE), sample.getAsDouble(Sample.LONGITUDE)));
//			out.println(" (NZGD49 Datum)</td></tr>");
//		}
		
		if (sample.get(Sample.LATITUDE) != null) {
			SiteRecord sr = SiteRecord.querySite(FREDUtils.getFREDConnection(state), sample.getAsInt(Sample.SITE_ID));			
			int origID = sr.getOriginalId();
			if (origID != -1) {
				Datum datum = sr.getOrigCoordDatum();
				Datum.Coordinate coord = sr.getOrigCoordAsCoord();	
				if (!(datum.getName().equals("NZGD49") && !(datum.getName().equals("NZMG")))) {
					if (coord instanceof Datum.LatLong) {
						out.print("<tr><td class='heading'>Lat/Long</td>");
					} else {
						out.print("<tr><td class='heading'>Grid Ref</td>");
					}
					out.println("<td>" + datum.getHumanStringFor(coord).replaceAll("Geographic ", "") + "</td></tr>");
				}
				try {
					Datum nzmgDatum = DatumFactory.createDatum("NZMG");
					Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
					out.println("<tr><td class='heading'>Grid Ref</td><td>" + nzmgDatum.getHumanStringFor(nzmgCoord) + "</td></tr>");
				} catch (Exception e) { System.out.println(e.getMessage()); }
			}
			Datum.LatLong ll = sr.getLatLong();
			if (ll.getNorthSouth() != 999)
				out.println("<tr><td class='heading'>Lat/Long</td><td>" + ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)</td></tr>");
		}		
		
		if (sample.get(Sample.METHOD) != null) { out.println("<tr><td class='heading'>Method</td><td>" + sample.getAsString(Sample.METHOD) + "</td></tr>"); }
		if (sample.get(Sample.ACCURACY) != null) { out.println("<tr><td class='heading'>Accuracy</td><td>&#177 " + sample.getAsDouble(Sample.ACCURACY) + "m</td></tr>"); }
		if (sample.isUserAuthenticated() && sample.get(Sample.LOCALITY) != null) { out.println("<tr><td class='heading'>Locality</td><td>" + sample.getAsString(Sample.LOCALITY) + "</td></tr>"); }
		if (!featType.equals(Feature.OUTCROP_LOCALITY)) {
			if (sample.isUserAuthenticated() && sample.get(Sample.PERSON) != null) {
				out.print("<tr><td class='heading' width='135'>");
				if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
					out.print("Operating Company");
				} else {
					out.print("Section Collector");
				}
				out.println("</td><td>" + sample.getAsString(Sample.PERSON) + "</td></tr>");
			}
			if (sample.isUserAuthenticated() && sample.get(Sample.START_DATE) != null) {
				out.print("<tr><td class='heading'>");
				if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
					out.print("Spud Date");
				} else {
					out.print("Sampling Start Date");
				}
				out.print("</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.START_DATE), sample.getAsString(Sample.START_DATE_ROUNDING)) + "</td></tr>");
			}
			if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DATE) != null) {
				out.print("<tr><td class='heading'>Completion Date</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.FINISH_DATE), sample.getAsString(Sample.FINISH_DATE_ROUNDING)) + "</td></tr>");
			}
			if (featType.equals(Feature.DRILLHOLE_LOCALITY) && sample.isUserAuthenticated() && sample.get(Sample.DRILLHOLE_LICENCE_NAME) != null) { out.println("<tr><td class='heading' width='135'>Licence Area</td><td>" + sample.getAsString(Sample.DRILLHOLE_LICENCE_NAME) + "</td></tr>"); }
			if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_TYPE) != null) { out.println("<tr><td class='heading' width='135'>Datum Type</td><td>" + sample.getAsString(Sample.DATUM_TYPE) + "</td></tr>"); }
			if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_ELEVATION) != null) { out.println("<tr><td class='heading' width='135'>Datum Elevation</td><td>" + sample.getAsString(Sample.DATUM_ELEVATION) + " m asl</td></tr>"); }
			if (sample.isUserAuthenticated() && sample.get(Sample.START_DEPTH) != null) {
				out.print("<tr><td class='heading' width='135'>");
				if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
					out.print("Kick-off Depth");
				} else {
					out.print("Top Horizon");
				}
				out.println("</td><td>" + sample.getAsString(Sample.START_DEPTH) + " m</td></tr>");
			}
			if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DEPTH) != null) {
				out.print("<tr><td class='heading' width='135'>");
				if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
					out.print("Termination Depth");
				} else {
					out.print("Base Horizon");
				}
				out.println("</td><td>" + sample.getAsString(Sample.FINISH_DEPTH) + " m</td></tr>");
			}
		}
		if (formType.equals("Full") && sample.isUserAuthenticated()) {

			//Sample Property Data
			//collectors (repeating)
			if (sample.get(Sample.COLLECTOR) != null) {
				out.print("<tr><td class='heading'>Collectors</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.COLLECTOR).iterator(); i2.hasNext(); ) {
					KeyValueObject coll = (KeyValueObject)i2.next();
					out.print(coll.getValue() + "<br />");
				}
				out.print("</td></tr>");
			}
			if (sample.get(Sample.COLLECTION_DATE) != null) { out.print("<tr><td class='heading'>Collection Date</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.COLLECTION_DATE), sample.getAsString(Sample.COLLECTION_DATE_ROUNDING)) + "</td></tr>"); }
			if (sample.get(Sample.STRAT_UNIT) != null) { out.println("<tr><td class='heading'>Strat Name</td><td>" + sample.getAsString(Sample.STRAT_UNIT) + "</td></tr>"); }
			if (sample.get(Sample.IN_PLACE) != null) { out.println("<tr><td class='heading'>In Place</td><td>" + sample.getAsString(Sample.IN_PLACE) + "</td></tr>"); }
			//sent to (repeating)
			if (sample.get(Sample.SENT_TO) != null) {
				out.print("<tr><td class='heading'>Sent To</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.SENT_TO).iterator(); i2.hasNext(); ) {
					SentTo sentTo = (SentTo)i2.next();
					out.print(sentTo.getSentTo() + "<br />");
				}
			out.print("</td></tr>");
			}
			if (sample.get(Sample.NOT_COLLECTED) != null) { out.println("<tr><td class='heading'>Not Collected</td><td>" + sample.getAsString(Sample.NOT_COLLECTED) + "</td></tr>"); }
			
			out.println("<tr><td class='bigheading' colspan='2'>Stratigraphy</td></tr>");
			
			if (sample.get(Sample.SIGNIFICANCE) != null) { out.println("<tr><td class='heading'>Significance</td><td>" + sample.getAsString(Sample.SIGNIFICANCE) + "</td></tr>"); }
			if (sample.get(Sample.INFERRED_STAGE) != null) { out.println("<tr><td class='heading'>Inferred Stage</td><td>" + sample.getAsString(Sample.INFERRED_STAGE) + "</td></tr>"); }
			if (sample.get(Sample.KNOWN_STAGE) != null) { out.println("<tr><td class='heading'>Known Stage</td><td>" + sample.getAsString(Sample.KNOWN_STAGE) + "</td></tr>"); }
			//Nearby samples (repeating)
			if (sample.get(Sample.RELATIONSHIP_NEARBY) != null) {
				out.print("<tr><td class='heading'>Samples Nearby</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_NEARBY).iterator(); i2.hasNext(); ) {
					Relationship nearRel = (Relationship)i2.next();
					out.print(nearRel.getDistanceRelation() + " " + nearRel.getRelatedSampleName() +"<br />");
				}
			out.print("</td></tr>");
			}
			//Sample relationships (repeating)
			if (sample.get(Sample.RELATIONSHIP_SAMPLE) != null) {
				out.print("<tr><td class='heading'>Sample Relationships</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_SAMPLE).iterator(); i2.hasNext(); ) {
					Relationship sampRel = (Relationship)i2.next();
					out.print(sampRel.getDistanceRelation() + " " + sampRel.getRelatedSampleName() + "<br />");
				}
			out.print("</td></tr>");
			}
			//Strat relationships (repeating)
			if (sample.get(Sample.RELATIONSHIP_STRAT) != null) {
				out.print("<tr><td class='heading'>Stratigraphic Relationships</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_STRAT).iterator(); i2.hasNext(); ) {
					Relationship stratRel = (Relationship)i2.next();
					out.print(stratRel.getRelationship() + "<br />");
				}
			out.print("</td></tr>");
			}
			if (sample.get(Sample.COLUMN_MAP) != null) { out.println("<tr><td class='heading'>Column/Map</td><td>" + sample.getAsString(Sample.COLUMN_MAP) + "</td></tr>"); }
			if (sample.get(Sample.DIP) != null) { out.println("<tr><td class='heading'>Dip</td><td>" + sample.getAsString(Sample.DIP) + "</td></tr>"); }
			if (sample.get(Sample.DIP_DIRECTION) != null) { out.println("<tr><td class='heading'>Dip Direction</td><td>" + sample.getAsString(Sample.DIP_DIRECTION) + "</td></tr>"); }
			if (sample.get(Sample.STRIKE) != null) { out.println("<tr><td class='heading'>Strike</td><td>" + sample.getAsString(Sample.STRIKE) + "</td></tr>"); }
			if (sample.get(Sample.FACING) != null) { out.println("<tr><td class='heading'>Facing</td><td>" + sample.getAsString(Sample.FACING) + "</td></tr>"); }
			
			out.println("<tr><td class='bigheading' colspan='2'>Sedimentary Features</td></tr>");
			if (sample.get(Sample.GRAINSIZE) != null) { out.println("<tr><td class='heading'>Grain Size</td><td>" + sample.getAsString(Sample.GRAINSIZE) + "</td></tr>"); }
			if (sample.get(Sample.COMPARATOR_USED) != null) { out.println("<tr><td class='heading'>Comparator Used</td><td>" + sample.getAsString(Sample.COMPARATOR_USED) + "</td></tr>"); }
			if (sample.get(Sample.BED_THICKNESS) != null) { out.println("<tr><td class='heading'>Bed Thickness</td><td>" + sample.getAsString(Sample.BED_THICKNESS) + "</td></tr>"); }
			if (sample.get(Sample.BEDDING) != null) { out.println("<tr><td class='heading'>Bedding</td><td>" + sample.getAsString(Sample.BEDDING) + "</td></tr>"); }
			if (sample.get(Sample.WEATHERING) != null) { out.println("<tr><td class='heading'>Weathering</td><td>" + sample.getAsString(Sample.WEATHERING) + "</td></tr>"); }
			if (sample.get(Sample.HARDNESS) != null) { out.println("<tr><td class='heading'>Hardness</td><td>" + sample.getAsString(Sample.HARDNESS) + "</td></tr>"); }
			if (sample.get(Sample.CARBONATE) != null) { out.println("<tr><td class='heading'>Carbonate</td><td>" + sample.getAsString(Sample.CARBONATE) + "</td></tr>"); }
			if (sample.get(Sample.COLOUR) != null) { out.println("<tr><td class='heading'>Colour</td><td>" + sample.getAsString(Sample.COLOUR) + "</td></tr>"); }
			//sed features (repeating)
			if (sample.get(Sample.SED_FEATURE) != null) {
				out.print("<tr><td class='heading'>Additional Features</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.SED_FEATURE).iterator(); i2.hasNext(); ) {
					SedFeature sf = (SedFeature)i2.next();
					out.print(sf.getSedFeature() + "<br />");
				}
				out.print("</td></tr>");
			}
			if (sample.get(Sample.DEPOSITION_ENV) != null) { out.println("<tr><td class='heading'>Inferred Environment</td><td>" + sample.getAsString(Sample.DEPOSITION_ENV) + "</td></tr>"); }
			if (sample.get(Sample.ROCK_NATURE) != null) { out.println("<tr><td class='heading'>Nature of Rock Unit</td><td>" + sample.getAsString(Sample.ROCK_NATURE) + "</td></tr>"); }
			if (sample.get(Sample.CORRESPONDENCE) != null) { out.println("<tr><td class='heading'>Correspondence</td><td>" + sample.getAsString(Sample.CORRESPONDENCE) + "</td></tr>"); }
			out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");
		}
		out.println("<img src='images/blank.gif' width='600' height='1' />");
		out.println("</td></tr></table>");
	}
	else { //no record or no rights
		out.println("<p><span class='bigheading'>Access denied</span></p>Either there is no record matching the ID you entered or you have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.");
	}
%>