<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.text.*, java.util.*, nz.cri.gns.auth.*"
%><%
	PageState state = new PageState(request, response, getServletContext());
	User user = (User)getUser(session);

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
	out.println("<tr><td><a href='javascript:history.back();' title='Quit'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='javascript:history.back();' class='heading'>Quit</a></td></tr>");
	out.println("</table>");

	drawEndNavigation(out);

	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");

	if (request.getParameter("FoldID") != null && request.getParameter("RecType") != null) {
		String foldID = request.getParameter("FoldID");
		Folder folder = new Folder(Integer.parseInt(foldID), user, state);
		String recID = request.getParameter("RecID");
		String sampID = request.getParameter("SampID");
		String featID = request.getParameter("FeatID");
		String recType = request.getParameter("RecType");

		if (recType.equals(Feature.OUTCROP_LOCALITY) || recType.equals(Feature.DRILLHOLE_LOCALITY) || recType.equals(Feature.VERTICAL_SECTION_LOCALITY)) {
			out.println("<p>Choose the locality to copy from the list below by clicking on the <img src='images/load.gif' width='20' height='20' /> icon</p>");
			//List localities
			if (folder.isAllowedReadLocalities() && folder.get(Folder.FEATURES) != null) {
				out.println("<table border='0' cellspacing='0' cellpadding='2'>");
				out.print("<tr class='heading'><td></td><td>Locality</td></tr>");		
				for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
					Feature feature = new Feature(((Integer) i.next()).intValue(), user, state);
					if (feature.getFeatureType().equals(recType) && (featID == null || feature.getFeatureID() != Integer.parseInt(featID)))
						out.println("<tr><td><a href='data_entry.jsp?Type=" + recType + "&FoldID=" + foldID + ((featID != null) ? "&FeatID=" + featID : "") + "&CopyID=" + feature.getFeatureID() + "' title='Copy Locality'><img src='images/load.gif' width='20' height='20' border='0' /></a><img src='images/blank.gif' width='10' height='1' /></td><td>" + feature.getAsString(Feature.SAMPLE_NAMES) + "</td></tr>");
				}
			}
		} else if (recType.equals("Sample")) {
			out.println("<p>Choose the sample to copy from the list below by clicking on the <img src='images/load.gif' width='20' height='20' /> icon</p>");
			//List localities
			if (folder.isAllowedReadLocalities() && folder.get(Folder.FEATURES) != null) {
				out.println("<table border='0' cellspacing='0' cellpadding='2'>");
				out.print("<tr class='heading'><td></td><td>Locality</td><td>Sample</td></tr>");		
				for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
					Feature feature = new Feature(((Integer) i.next()).intValue(), user, state);
					if (!feature.getFeatureType().equals(Feature.OUTCROP_LOCALITY) && feature.get(Feature.SAMPLES) != null) {
						for (Iterator j = feature.getAsVector(Feature.SAMPLES).iterator(); j.hasNext(); ) {
							Sample sample = new Sample(((Integer) j.next()).intValue(), user, state);
							if (sampID == null || sample.getSampleID() != Integer.parseInt(sampID))
								out.println("<tr><td><a href='data_entry.jsp?Type=" + recType + "&FoldID=" + foldID + ((sampID != null) ? "&SampID=" + sampID : "") + "&CopyID=" + sample.getSampleID() + "' title='Copy Locality'><img src='images/load.gif' width='20' height='20' border='0' /></a><img src='images/blank.gif' width='10' height='1' /></td><td>" + feature.getAsString(Feature.SAMPLE_NAMES) + "&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.DRILLHOLE_DEPTH) + "</td></tr>");						
						}
					}
				}
			}
		} else { //Records
			out.println("<p>Choose the record to copy from the list below by clicking on the <img src='images/load.gif' width='20' height='20' /> icon</p>");
			//List localities
			if (folder.isAllowedReadLocalities() && folder.get(Folder.FEATURES) != null) {
				out.println("<table border='0' cellspacing='0' cellpadding='2'>");
				out.print("<tr class='heading'><td></td><td>Locality</td><td>Sample</td><td>Record</td></tr>");		
				for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
					Feature feature = new Feature(((Integer) i.next()).intValue(), user, state);
					if (feature.get(Feature.SAMPLES) != null) {
						for (Iterator j = feature.getAsVector(Feature.SAMPLES).iterator(); j.hasNext(); ) {
							Sample sample = new Sample(((Integer) j.next()).intValue(), user, state);
							if (sample.get(Sample.RECORDS) != null) {
								for (Iterator k = sample.getAsVector(Sample.RECORDS).iterator(); k.hasNext(); ) {
									Record record = null;
									KeyValueObject kvo = (KeyValueObject) k.next();
									if (kvo.getValue().equals(Record.ADOPTION_RECORD) && recType.equals(Record.ADOPTION_RECORD)) {
										record = AdoptionRecord.getData(Integer.parseInt(kvo.getKey()), user, state);
									} else if (kvo.getValue().equals(Record.PALEONTOLOGY_RECORD) && recType.equals(Record.PALEONTOLOGY_RECORD)) {
										record = PaleontologyRecord.getData(Integer.parseInt(kvo.getKey()), user, state);
									}
									if (record != null && (recID == null || record.getRecordID() != Integer.parseInt(recID)))
										out.println("<tr><td><a href='data_entry.jsp?Type=" + recType + "&FoldID=" + foldID + ((recID != null) ? "&RecID=" + recID : "") + "&CopyID=" + record.getRecordID() + "' title='Copy Locality'><img src='images/load.gif' width='20' height='20' border='0' /></a><img src='images/blank.gif' width='10' height='1' /></td><td>" + feature.getAsString(Feature.SAMPLE_NAMES) + "&nbsp;&nbsp;</td><td>" + sample.getAsString(Sample.DRILLHOLE_DEPTH) + "&nbsp;&nbsp;</td><td>" + record.getRecordName() + "</td></tr>");						
								}
							}
						}
					}
				}
			}
		}

		out.println("</table>");
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);
%>
