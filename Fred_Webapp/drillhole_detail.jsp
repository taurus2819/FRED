<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.fred.*, nz.cri.gns.db.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	User user = getUser(session);
	PageState state = new PageState(request, response, getServletContext());
	DecimalFormat nzmg = new DecimalFormat("######0");
	DecimalFormat latlong = new DecimalFormat("#00.0000");
	String featType, featID;

	ExtranetTemplate et = getExtranetTemplate();
	//et.setDisplayLoadingMessage(true);

	if (request.getParameter("ID") != null) {
		featID = request.getParameter("ID");
		session.setAttribute("FeatureID", featID);
	} else {
		featID = (String) session.getAttribute("FeatureID");
	}

	drawTop(out, et, request, response);

	if (featID != null) {

		Feature feature = new Feature(Integer.parseInt(featID), user, state);
		if (feature.isUserAuthenticated() || feature.isApprovedLocality()) {
			Audit audit = Audit.getAudit(feature.getAsInt(Feature.AUDIT_ID), state);
			featType = feature.getAsString(Feature.FEATURE_TYPE);
	
			if (!featType.equals("Outcrop")) {
				out.println("<table style='margin-left:10px; margin-top:20px; width:180px;' border='0'>");
				out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
				out.println("<tr><td colspan='2' align='center' class='bigheading' >" + feature.getAsString(Feature.FEATURE_NAME) + "</td></tr>");
				out.println("<tr><td colspan='2' align='center'>" + featType + "</td></tr>");
				if (feature.get(Feature.MASTERFILE_NAME) != null) {
					out.println("<tr><td class='smallheading'>Masterfile:<img src='images/blank.gif' height='1' width='5' /></td><td class='smalltext'>" + feature.getAsString(Feature.MASTERFILE_NAME) + "</td></tr>");
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
				out.println("</table>");

				drawEndNavigation(out);

				out.println("<table style='margin-left:20px; width:550px;' border='0'>");
				out.println("<tr><td>");

				if (feature.get(Feature.SAMPLES) != null) {
					int minSampID = ((SampleHeader) feature.getAsVector(Feature.SAMPLES).firstElement()).getSampleID();
					Sample sample = new Sample(minSampID, user, state);

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
					if (sample.isUserAuthenticated() && sample.get(Sample.LOCALITY) != null) { out.println("<tr><td class='heading'>Locality</td><td>" + sample.getAsString(Sample.LOCALITY) + "</td></tr>"); }
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
						out.println("</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.START_DATE), sample.getAsString(Sample.START_DATE_ROUNDING)) + "</td></tr>");
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
					out.println("</table></p>");

					out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
					out.println("<tr><th>Locality Name<img src='images/blank.gif' height='1' width='20' /></th><th colspan='2'>Sample Depth</th></tr>");
					SampleHeader sampleHeader;
					for (Iterator i = feature.getAsVector(Feature.SAMPLES).iterator(); i.hasNext(); ) {
						sampleHeader = (SampleHeader) i.next();
						out.println("<tr><td class='heading'>" + sampleHeader.getSampleName() + "&nbsp;&nbsp;</td><td width='25'><img src='images/drill.gif' height='20' width='20' /></td><td><a href='detail.jsp?ID=" + sampleHeader.getSampleID() + "' class='heading'>" + sampleHeader.getDrillholeDepth() + "</a></td></tr>");
					}
				}
				out.println("</table></p>");
				if (user ==  null) { out.println("<tr><td></td></tr><tr><td colspan='2'>More data may be available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/drillhole_detail.jsp") + "' class='boldlink'>logged</a> in users</td></tr>"); }
			}

			else { // outcrop
				drawEndNavigation(out);
				out.println("<table style='margin-left:20px; width:550px;' border='0'>");
				out.println("<tr><td>");
				out.println("<p>The Feature ID entered is not a drillhole or vertical section locality  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
			}
		}	else { // not allowed to view
			drawEndNavigation(out);
			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p>You have insufficient rights to view the record.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
		}
	} else { //ID not specified
		drawEndNavigation(out);
		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");
		out.println("<p>No Feature ID recieved.  Click <a href='index.jsp' class='heading'>here</a> to return to the FRED home page.</p>");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>