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

				if (feature.get(Feature.SAMPLE) != null) {
					int minSampID = ((Integer) feature.getAsVector(Feature.SAMPLE).firstElement()).intValue();
					Sample fullSample = new Sample(minSampID, user, state);

					out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
					if (fullSample.get(Sample.YARD_FR_ID) != null) { out.println("<tr><td class='heading'>Yard FR Number</td><td>" + fullSample.getAsString(Sample.YARD_FR_NUMBER) + "</td></tr>"); }
					if (fullSample.get(Sample.LATITUDE) != null) {
						out.print("<tr><td class='heading'>Grid Ref</td><td>");
						if (fullSample.get(Sample.NZMG_SHEET) != null) {
							out.print(fullSample.getAsString(Sample.NZMG_SHEET) + ": " + nzmg.format(fullSample.getAsDouble(Sample.NZMG_EAST)) + ", " + nzmg.format(fullSample.getAsDouble(Sample.NZMG_NORTH)));
							out.print("<img src='images/blank.gif' width='20' height='1' />|<img src='images/blank.gif' width='20' height='1' />");
						}
						if (fullSample.getAsDouble(Sample.LATITUDE) > 0) {
							out.print(latlong.format(fullSample.getAsDouble(Sample.LATITUDE)) + "&#176N");
						} else {
							out.print(latlong.format(Math.abs(fullSample.getAsDouble(Sample.LATITUDE))) + "&#176S");
						}
						out.print("/");
						if (fullSample.getAsDouble(Sample.LONGITUDE) > 0) {
							out.print(latlong.format(fullSample.getAsDouble(Sample.LONGITUDE)) + "&#176E");
						} else {
							out.print(latlong.format(Math.abs(fullSample.getAsDouble(Sample.LONGITUDE))) + "&#176W");
						}
						if (fullSample.get(Sample.ACCURACY) != null) { out.print(" (&#177 " + fullSample.getAsDouble(Sample.ACCURACY) + "m)"); }
						out.println("</td></tr>");
					}
					if (fullSample.get(Sample.METHOD) != null) { out.println("<tr><td class='heading'>Method</td><td>" + fullSample.getAsString(Sample.METHOD) + "</td></tr>"); }
					if (fullSample.isUserAuthenticated() && fullSample.get(Sample.LOCALITY) != null) { out.println("<tr><td class='heading'>Locality</td><td>" + fullSample.getAsString(Sample.LOCALITY) + "</td></tr>"); }
					if (fullSample.isUserAuthenticated() && fullSample.get(Sample.PERSON) != null) {
						out.print("<tr><td class='heading' width='135'>");
						if (featType.equals("Drillhole")) {
							out.print("Operating Company");
						} else {
							out.print("Section Collector");
						}
						out.println("</td><td>" + fullSample.getAsString(Sample.PERSON) + "</td></tr>");
					}
					if (fullSample.isUserAuthenticated() && fullSample.get(Sample.START_DATE) != null) {
						out.print("<tr><td class='heading'>");
						if (featType.equals("Drillhole")) {
							out.print("Spud Date");
						} else {
							out.print("Sampling Start Date");
						}
						out.println("</td><td>" + FREDUtils.formatDateForOutput(fullSample.getAsDate(Sample.START_DATE), fullSample.getAsString(Sample.START_DATE_ROUNDING)) + "</td></tr>");
					}
					if (fullSample.isUserAuthenticated() && fullSample.get(Sample.FINISH_DATE) != null) {
						out.print("<tr><td class='heading'>Completion Date</td><td>" + FREDUtils.formatDateForOutput(fullSample.getAsDate(Sample.FINISH_DATE), fullSample.getAsString(Sample.FINISH_DATE_ROUNDING)) + "</td></tr>");
					}
					if (featType.equals("Drillhole") && fullSample.isUserAuthenticated() && fullSample.get(Sample.DRILLHOLE_LICENCE_NAME) != null) { out.println("<tr><td class='heading' width='135'>Licence Area</td><td>" + fullSample.getAsString(Sample.DRILLHOLE_LICENCE_NAME) + "</td></tr>"); }
					if (fullSample.isUserAuthenticated() && fullSample.get(Sample.DATUM_TYPE) != null) { out.println("<tr><td class='heading' width='135'>Datum Type</td><td>" + fullSample.getAsString(Sample.DATUM_TYPE) + "</td></tr>"); }
					if (fullSample.isUserAuthenticated() && fullSample.get(Sample.DATUM_ELEVATION) != null) { out.println("<tr><td class='heading' width='135'>Datum Elevation</td><td>" + fullSample.getAsString(Sample.DATUM_ELEVATION) + " m asl</td></tr>"); }
					if (fullSample.isUserAuthenticated() && fullSample.get(Sample.START_DEPTH) != null) {
						out.print("<tr><td class='heading' width='135'>");
						if (featType.equals("Drillhole")) {
							out.print("Kick-off Depth");
						} else {
							out.print("Top Horizon");
						}
						out.println("</td><td>" + fullSample.getAsString(Sample.START_DEPTH) + " m</td></tr>");
					}
					if (fullSample.isUserAuthenticated() && fullSample.get(Sample.FINISH_DEPTH) != null) {
						out.print("<tr><td class='heading' width='135'>");
						if (featType.equals("Drillhole")) {
							out.print("Termination Depth");
						} else {
							out.print("Base Horizon");
						}
						out.println("</td><td>" + fullSample.getAsString(Sample.FINISH_DEPTH) + " m</td></tr>");
					}
					out.println("</table></p>");

					out.println("<p><table border='0' cellspacing='0' cellpadding='2' width='550'>");
					out.println("<tr><th>Locality Name<img src='images/blank.gif' height='1' width='20' /></th><th colspan='2'>Sample Depth</th></tr>");
					Sample drillSample;
					for (Iterator i = feature.getAsVector(Feature.SAMPLE).iterator(); i.hasNext(); ) {
						drillSample = new Sample(((Integer) i.next()).intValue(), user, state);
						out.println("<tr><td class='heading'>" + drillSample.getAsString(Sample.SAMPLE_NAME) + "&nbsp;&nbsp;</td><td width='25'><img src='images/drill.gif' height='20' width='20' /></td><td><a href='detail.jsp?ID=" + drillSample.getAsInt(Sample.SAMPLE_ID) + "' class='heading'>" + drillSample.getAsString(Sample.DRILLHOLE_DEPTH) + "</a></td></tr>");
					}
				} else {
					out.println("<tr><td></td></tr><tr><td colspan='2'>More data is available for this locality for <a href='login.jsp?loginpage=" + URLEncoder.encode("/fred/drillhole_detail.jsp") + "' class='boldlink'>logged</a> in users</td></tr>");
				}
				out.println("</table></p>");
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