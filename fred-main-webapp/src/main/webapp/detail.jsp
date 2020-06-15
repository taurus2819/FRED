<%@page pageEncoding="utf-8"
        %><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
        %><%@page import="nz.cri.gns.fred.model.Adoption"
        %><%@page import="nz.cri.gns.fred.model.Audit"
        %><%@page import="nz.cri.gns.fred.model.AuditEdit"
        %><%@page import="nz.cri.gns.fred.model.Feature"
        %><%@page import="nz.cri.gns.fred.model.FrNumber"
        %><%@page import="nz.cri.gns.fred.model.UserFolder"
        %><%@page import="nz.cri.gns.fred.model.Paleontology"
        %><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
        %><%@page import="nz.cri.gns.fred.model.Sample"
        %><%@page import="nz.cri.gns.fred.model.Relationship"
        %><%@page import="nz.cri.gns.fred.model.PersonRelationship"
        %><%@page import="nz.cri.gns.fred.model.SiteView"
        %><%@page import="nz.cri.gns.fred.model.SentTo"
        %><%@page import="nz.cri.gns.fred.model.SedimentaryFeature"
        %><%@page import="nz.cri.gns.fred.model.Taxon"
        %><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
        %><%@page import="nz.cri.gns.fred.model.MetaCat"
        %><%@page import="nz.cri.gns.fred.model.FREDConstants"
        %><%@page import="nz.cri.gns.db.DBUtils"
        %><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
        %><%@page import="nz.cri.gns.jsp.CustomHTMLLink"
        %><%@page import="nz.cri.gns.jsp.Link"
        %><%@page import="nz.cri.gns.jsp.IconnedLink"
        %><%@page import="nz.cri.gns.util.map.Datum"
        %><%@page import="nz.cri.gns.util.map.Datum.Coordinate"
        %><%@page import="nz.cri.gns.util.map.Datum.LatLong"
        %><%@page import="nz.cri.gns.util.map.DatumFactory"
        %><%@page import="java.net.URLEncoder"
        %><%@page import="java.io.PrintWriter"
        %><%@page import="java.util.List"
        %><%@page import="java.util.Set"
        %><%@page import="java.util.Arrays"
        %><%@page import="java.util.Vector"
        %><%@page import="java.util.logging.Logger"
        %><%@page import="java.util.logging.Level"
        %><%@page import="nz.cri.gns.auth.domain.User"
        %><%@page import="nz.cri.gns.auth.security.IpGrantedAuthority"
        %><%@page import="nz.cri.gns.fred.dao.DAOFactory"
        %><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
        %><%@page import="nz.cri.gns.fred.util.AuditUtil"
        %><%@page import="nz.cri.gns.fred.util.FeatureUtil"
        %><%@page import="nz.cri.gns.fred.util.SampleUtil"
        %><%@page import="nz.cri.gns.fred.util.RecordUtil"
        %><%@page import="nz.cri.gns.fred.util.StageUtil"
        %><%@page import="nz.cri.gns.fred.util.SiteUtil"
        %><%@page import="nz.cri.gns.fred.util.FolderUtil"
        %><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
        %><%@page import="nz.cri.gns.fred.util.FREDUtil"
        %><%@page import="nz.cri.gns.fred.de.DataInputException"
        %><%@page import="nz.cri.gns.intranet.ServletUtils"
%><%!
    private static final Logger log = Logger.getLogger("detail.jsp");

    @Override
    public IpGrantedAuthority getRequiredRights() {
        return null;
    }
%><%!    public String getName(HttpServletRequest request) {
        try {
            String sampID = request.getParameter("ID");
            String featID = request.getParameter("FeatID");
            DAOFactory factory = FredHibernate.get().getDAOFactory();
            if (featID != null) {
                Feature feature = new FeatureUtil(factory).getFeature(Integer.parseInt(featID));
                return "FRED :: Locality Detail for " + FeatureUtil.getFeatureIdentifyingName(feature);
            } else if (sampID != null) {
                Sample sample = new SampleUtil(factory).getSample(Integer.parseInt(sampID));
                return "FRED :: Sample Detail for " + ((sample.getFrNumber() != null) ? sample.getFrNumber().getFrNumber() : FeatureUtil.getFeatureIdentifyingName(sample.getFeature()));
            }
            return "FRED :: The Fossil Record Electronic Database";
        } catch (Exception e) {
            return "FRED :: The Fossil Record Electronic Database";
        }
    }
%><%!    public static void addRepeatingCells(PrintWriter out, String heading, Object[] text, boolean newLines) {
        if (text.length > 0) {
            if (newLines) {
                out.println("<tr class=\"lightColour\"><td class=\"heading\">" + heading + "</td><td>" + DBUtils.nvl(text[0]));
                for (int i = 1; i < text.length; i++) {
                    out.println("<br/>" + DBUtils.nvl(text[i]));
                }
                out.println("</td></tr>");
            } else {
                StringBuffer textLine = new StringBuffer();
                for (int i = 0; i < text.length; i++) {
                    textLine.append(text[i]);
                    if (i < text.length - 1) {
                        textLine.append("; ");
                    }
                }
                out.println("<tr class=\"lightColour\"><td class=\"heading\">" + heading + "</td><td>" + textLine.toString() + "</td></tr>");
            }
        }
    }
%><%
    try {
        User user = (User) getUser(session);
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        SampleUtil sampleUtil = new SampleUtil(factory);
        FeatureUtil featureUtil = new FeatureUtil(factory);
        RecordUtil recordUtil = new RecordUtil(factory);

        ExtranetTemplate et = getExtranetTemplate();
        et.setUseNavigationColumn(false);
        et.setDisplayLoadingMessage(true);

        String sampID = request.getParameter("ID");
        String featID = request.getParameter("FeatID");

        List<Object> resultsList = (List<Object>) session.getAttribute("FRED.results");
        if (featID != null || sampID != null) {
            resultsList = null;
        }
        int resultsSize = (resultsList == null ? 0 : resultsList.size());

        String resultsIndex = request.getParameter("resultsIndex");
        resultsIndex = resultsIndex == null ? "-1" : resultsIndex;
        resultsIndex = resultsIndex.equals("") ? "-1" : resultsIndex;
        int thisIndex = Integer.decode(resultsIndex).intValue();
        int nextIndex = thisIndex + 1;
        nextIndex = Math.min(nextIndex, resultsSize - 1);
        int prevIndex = thisIndex - 1;
        prevIndex = Math.max(prevIndex, 0);

        String featureIndex = request.getParameter("featureIndex");
        Feature feature = null;
        Sample sample = null;
        if ((featID == null || featID.equals("")) && resultsIndex != "-1" && resultsList != null && resultsSize > 0) {
            Object result = resultsList.get(thisIndex);
            if (result instanceof Feature) {
                feature = (Feature) result;
                featID = "" + feature.getFeatureId();
            } else {
                sample = (Sample) result;
                sampID = "" + sample.getSampleId();
            }
        }

        int site = ServletUtils.getSite(getPageState(request, response));
        if (request.getRemoteAddr().equals("127.0.0.1")) {
            site = 1;
        }

        String backURL = request.getParameter("backURL");
        if (backURL != null && backURL.length() == 0) {
            backURL = null;
        }
        String backText = request.getParameter("backText");
        if (backText != null && backText.length() == 0) {
            backText = null;
        }
        String backStr = (backURL != null) ? "&backURL=" + URLEncoder.encode(backURL, "ISO-8859-1") : "";
        backStr += (backText != null) ? "&backText=" + URLEncoder.encode(backText, "ISO-8859-1") : "";

        //if FeatureID given then check if outcrop and if redirect to display sample details
        try {
            // avoid the redirect if possible
            if (featID != null) {
                session.setAttribute("FRED.FeatureID", featID);
                feature = featureUtil.getFeature(Integer.parseInt(featID));
                if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
                    sampID = "" + ((Sample) feature.getSamples().iterator().next()).getSampleId();
                }
            }
            if (featID != null && sampID == null) {
                session.setAttribute("FRED.FeatureID", featID);
                feature = featureUtil.getFeature(Integer.parseInt(featID));
                if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
                    response.sendRedirect("detail.jsp?ID=" + ((Sample) feature.getSamples().iterator().next()).getSampleId() + backStr);
                    return;
                }
            } else if (sampID != null) {
                session.setAttribute("FRED.SampleID", sampID);
                sample = sampleUtil.getSample(Integer.parseInt(sampID));
                feature = sample.getFeature();
            } else if (session.getAttribute("FRED.FeatureID") != null) {
                response.sendRedirect("detail.jsp?FeatID=" + ((String) session.getAttribute("FRED.FeatureID")));
                return;
            } else if (session.getAttribute("FRED.SampleID") != null) {
                response.sendRedirect("detail.jsp?ID=" + ((String) session.getAttribute("FRED.SampleID")));
                return;
            }
        } catch (Exception e) {
        }

        boolean authorChk = true;
        try {
            authorChk = (Boolean) session.getAttribute("FRED.AuthorChk");
        } catch (Exception e) {
        }
        if (request.getParameter("AuthorChk") != null) {
            authorChk = request.getParameter("AuthorChk").equals("true");
        }
        session.setAttribute("FRED.AuthorChk", new Boolean(authorChk));
        boolean sCountChk = false;
        try {
            sCountChk = (Boolean) session.getAttribute("FRED.SCountChk");
        } catch (Exception e) {
        }
        if (request.getParameter("SCountChk") != null) {
            sCountChk = request.getParameter("SCountChk").equals("true");
        }
        session.setAttribute("FRED.SCountChk", new Boolean(sCountChk));
        boolean sCoordChk = false;
        try {
            sCoordChk = (Boolean) session.getAttribute("FRED.SCoordChk");
        } catch (Exception e) {
        }
        if (request.getParameter("SCoordChk") != null) {
            sCoordChk = request.getParameter("SCoordChk").equals("true");
        }
        session.setAttribute("FRED.SCoordChk", new Boolean(sCoordChk));
        boolean commChk = false;
        try {
            commChk = (Boolean) session.getAttribute("FRED.CommChk");
        } catch (Exception e) {
        }
        if (request.getParameter("CommChk") != null) {
            commChk = request.getParameter("CommChk").equals("true");
        }
        session.setAttribute("FRED.CommChk", new Boolean(commChk));

        if (feature != null) {
            boolean isAllowedReadFeature = featureUtil.isAllowedReadFeature(user, feature);
            if (isAllowedReadFeature) {
                AuditUtil auditUtil = new AuditUtil(FredHibernate.get().getDAOFactory());
                auditUtil.addLogEntry(AuditUtil.DETAIL_LOG_TYPE, user, null);
            }

            Vector<Link> il = new Vector<Link>();
            if (backURL != null) {
                il.add(new IconnedLink(backURL, "images/back_arrow.gif", (backText != null) ? request.getParameter("backText") : "Back"));
            }
            il.add(new IconnedLink("locality_map.jsp?FeatID=" + feature.getFeatureId() + "&backURL=" + URLEncoder.encode("detail.jsp?" + ((sample != null) ? "ID=" + sample.getSampleId() : "?FeatID=" + feature.getFeatureId()) + backStr, "ISO-8859-1") + "&backText=Back%20To%20Locality", "images/map.gif", "Locality Map"));
            il.add(new IconnedLink("export_setup.jsp?" + ((sample != null) ? "sampId=" + sample.getSampleId() : "featId=" + feature.getFeatureId()), "images/save.gif", "Download"));
            if (isAllowedReadFeature) {
                il.add(new IconnedLink("audit_detail.jsp?" + ((sample != null) ? "ID=" + sample.getSampleId() : "FeatID=" + feature.getFeatureId()) + "&backURL=" + URLEncoder.encode("detail.jsp?" + ((sample != null) ? "ID=" + sample.getSampleId() : "?FeatID=" + feature.getFeatureId()) + backStr, "ISO-8859-1") + "&backText=Back%20To%20Locality", "images/loc.gif", "Audit Details"));
                il.add(new IconnedLink("frf/frf/pdf?" + featureUtil.getFullLocalityPDFURL(feature) + "&q=" + Math.random(), "images/pdf_icon.gif", "Print Full Locality"));
            }
            //Add to Folder link
            if (isAllowedReadFeature && feature.getAudit().getStatus().equals(FREDConstants.APPROVED) && (new FolderUtil(factory)).getPersonalFolders(user).size() > 0) {
                StringBuffer customHTML = new StringBuffer("<form method=\"post\" action=\"detail.jsp\" name=\"FolderForm\" style=\"display: inline; margin: 0;\">");
                if (sample != null) {
                    customHTML.append("<input type=\"hidden\" name=\"ID\" value=\"").append(sample.getSampleId()).append("\" />");
                } else {
                    customHTML.append("<input type=\"hidden\" name=\"FeatID\" value=\"").append(feature.getFeatureId()).append("\" />");
                }
                if (backURL != null) {
                    customHTML.append("<input type=\"hidden\" name=\"backURL\" value=\"").append(backURL).append("\" />");
                    if (backText != null) {
                        customHTML.append("<input type=\"hidden\" name=\"backText\" value=\"").append(backText).append("\" />");
                    }
                }
                customHTML.append("<input type=\"hidden\" name=\"ActionType\" value=\"AddtoFold\" />");
                customHTML.append("<img src=\"images/blank.gif\" height=\"20\" width=\"10\" alt=\"\" /><select name=\"FoldID\">");
                customHTML.append("<option value=\"-\">-- Choose --</option>");
                for (UserFolder folder : (new FolderUtil(factory)).getPersonalFolders(user)) {
                    String folderName = folder.getFolderName();
                    if (folderName.length() > 17) {
                        folderName = folderName.substring(0, 14) + "...";
                    }
                    customHTML.append("<option value=\"").append(folder.getFolderId()).append("\">").append(folderName).append("</option>");
                }
                customHTML.append("</select><br />");
                customHTML.append("<img src=\"images/blank.gif\" height=\"20\" width=\"10\" alt=\"\" /><input type=\"submit\" value=\"Add to Folder\" />");
                customHTML.append("</form>");
                il.add(new CustomHTMLLink(customHTML.toString()));
            }
            //Taxa list options
            if (isAllowedReadFeature && sample != null && sampleUtil.getPaleontologyRecordCount(sample) > 0) {
                il.add(new IconnedLink("detail.jsp?" + ((sample != null) ? "ID=" + sample.getSampleId() : "?FeatID=" + feature.getFeatureId()) + backStr + "&AuthorChk=" + ((authorChk) ? "false" : "true"), ((authorChk) ? "images/ok.gif" : "images/cancel.gif"), "Show Taxonomic Author"));
                il.add(new IconnedLink("detail.jsp?" + ((sample != null) ? "ID=" + sample.getSampleId() : "?FeatID=" + feature.getFeatureId()) + backStr + "&SCountChk=" + ((sCountChk) ? "false" : "true"), ((sCountChk) ? "images/ok.gif" : "images/cancel.gif"), "Show Specimen Count"));
                il.add(new IconnedLink("detail.jsp?" + ((sample != null) ? "ID=" + sample.getSampleId() : "?FeatID=" + feature.getFeatureId()) + backStr + "&SCoordChk=" + ((sCoordChk) ? "false" : "true"), ((sCoordChk) ? "images/ok.gif" : "images/cancel.gif"), "Show Specimen Coords"));
                il.add(new IconnedLink("detail.jsp?" + ((sample != null) ? "ID=" + sample.getSampleId() : "?FeatID=" + feature.getFeatureId()) + backStr + "&CommChk=" + ((commChk) ? "false" : "true"), ((commChk) ? "images/ok.gif" : "images/cancel.gif"), "Show Taxonomic Comments"));
            }
            addButtons(et, il.toArray(new Link[il.size()]));

            Audit audit = feature.getAudit();
            String featType = feature.getFeatureType();

            if (featureUtil.isAllowedReadFeatureSite(user, feature)) {
                if (request.getParameter("ActionType") != null) { //do something
                    String actionType = request.getParameter("ActionType");
                    if (actionType.equals("Approve") && featureUtil.isAllowedApproveFeature(user, feature)) {
                        try {
                            featureUtil.approveFeature(feature, request.getParameter("MapSheet"), new Integer(request.getParameter("SerialNum")), request.getParameter("RecollNum"), request.getParameter("CurComm"), user);
                            response.sendRedirect("admin_folder_detail.jsp?ID=" + feature.getMasterFile().getFolderId() + "&q=" + Math.random());
                            return;
                        } catch (DataInputException e) {
%><script language="JavaScript">alert("The FR Number already exists please try another one");</script><%                                                    }
} else if (actionType.equals("Reject") && featureUtil.isAllowedApproveFeature(user, feature)) {
    featureUtil.rejectLocality(feature, request.getParameter("CurComm"), user);
    response.sendRedirect("admin_folder_detail.jsp?ID=" + feature.getMasterFile().getFolderId() + "&q=" + Math.random());
    return;
} else if (actionType.equals("AddtoFold") && !request.getParameter("FoldID").equals("-")) {
    featureUtil.addToFolder(feature, Integer.parseInt(request.getParameter("FoldID")), user);
%><script language="JavaScript">alert("Locality Added to folder");</script><%
        }
    }

    drawTop(out, et, request, response);

    //Approve/Reject
    if (featureUtil.isAllowedApproveFeature(user, feature)) {
        FrNumber frNumber = featureUtil.getNextAvailableFrNumber(feature);
        String[] comms = FeatureUtil.splitWorkingComments(feature.getAudit().getWorkingComments());
        String workComm = comms[0];
        String recoll = comms[1];
%><p>
<form name="RevForm" method="post" action="detail.jsp"><%
    if (sample != null) {
    %><input type="hidden" name="ID" value="<%=sample.getSampleId()%>" /><%
    } else {
    %><input type="hidden" name="FeatID" value="<%=feature.getFeatureId()%>" /><%
        }
        if (backURL != null) {
    %><input type="hidden" name="backURL" value="<%=backURL%>" /><%
        if (backText != null) {
    %><input type="hidden" name="backText" value="<%=backText%>" /><%
            }
        }
    %><input type="hidden" name="ActionType" value="" />
    <table border="0" cellpadding="3" cellspacing="2" width="550">
        <tr class="midColour"><th colspan="4">Masterfile Curator Options</th></tr><%
            if (workComm != null) {
                %><tr class="lightColour"><td colspan="4" class="heading">User Comments</td></tr>
        <tr class="lightColour"><td colspan="4"><%=DBUtils.nvl(workComm)%></td></tr><%
            }
            if (recoll != null) {
            %><tr class="lightColour"><td colspan="4">The submitter has indicated that this record is a recollection of <%=recoll%>.</td></tr><%
            }
        %><tr class="lightColour"><td colspan="4"><img src="images/blank.gif" height="5" width="1" /></td></tr>
        <tr class="lightColour"><td colspan="4" class="heading">FR Number</td></tr>
        <tr class="lightColour"><td colspan="4">
                <input type="text" name="MapSheet" size="8" value="<%=frNumber.getMapSheet()%>" />&nbsp;
                /f&nbsp;<input type="text" name="SerialNum" size="3" value="<%=frNumber.getSerialNumber()%>" />&nbsp;
                <input type="text" name="RecollNum" size="1" value="" />
            </td></tr>
        <tr class="lightColour"><td colspan="4" class="heading">Curator Comments</td></tr>
        <tr class="lightColour"><td colspan="4"><textarea name="CurComm" rows="2" cols="80"><%=DBUtils.nvl(audit.getCuratorComments())%></textarea></td></tr>
        <tr class="lightColour"><td><a href="#" onClick="document.RevForm.ActionType.value = 'Approve';document.RevForm.submit();"><img src="images/ok.gif" width="20" height="20" border="0" alt="Approve" /></a></td><td class="heading" style="text-align: left">Approve</td>
            <td><a href="#" onClick="document.RevForm.ActionType.value = 'Reject';document.RevForm.submit();"><img src="images/cancel.gif" width="20" height="20" border="0" alt="reject" /></a></td><td class="heading" style="text-align: left">Reject</td></tr>
    </table>
</form>
</p><%
    }

    //Locality Data
%><p>
<table border="0" cellpadding="3" cellspacing="2" width="550">
    <tr class="midColour">
        <th colspan="2">
            Locality Information&nbsp;&nbsp;&nbsp;
            <a href="frf/frf.pdf?FeatIDs=<%=feature.getFeatureId() + "&q=" + Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" title="Print"/></a>
                <%
                    if (resultsSize > 0) {
                %><div style="float: right"><%
                    if (thisIndex == 0) {
                %>_<%                } else {
                %>
                <a href="detail.jsp?backURL=result_list.jsp%3FPage%3D1&backText=Back+To+Result+List&resultsIndex=<%=prevIndex%>">prev</a>
                <%
                    }
                %>&nbsp;<%
                    if (thisIndex == (resultsSize - 1)) {
                %>_<%                                           } else {
                %><a href="detail.jsp?backURL=result_list.jsp%3FPage%3D1&backText=Back+To+Result+List&resultsIndex=<%=nextIndex%>">next</a><%
                    }
                %></div><%
            }%>
        </th>
    </tr>
    <tr class="lightColour"><td class="heading">FR Number</td><td class="heading"><%=((feature.getFrNumber() != null) ? feature.getFrNumber().getFrNumber() : "not yet allocated")%></td></tr><%
        if (feature.getYardFrNumber() != null) {
        %><tr class="lightColour"><td class="heading">Yard FR Number</td><td><%=feature.getYardFrNumber().getFrNumber()%></td></tr><%
        }
    %><tr class="lightColour"><td class="heading">Masterfile</td><td><%=((feature.getMasterFile() != null) ? feature.getMasterFile().getName() : "undefined")%></td></tr>
    <tr class="lightColour"><td class="heading">Locality Type</td><td><%=featType%></td></tr><%
        if (feature.getFeatureName() != null) {
            String featTypeLbl, linkStart = "", linkStop = "", petWellLink = null;
            if (featType.equals(FREDConstants.OUTCROP)) {
                featTypeLbl = "Field Number";
            } else if (featType.equals(FREDConstants.DRILLHOLE)) {
                featTypeLbl = "Drillhole Name";
                linkStart = "<a href=\"detail.jsp?FeatID=" + feature.getFeatureId() + backStr + "\">";
                linkStop = "</a>";
                petWellLink = FREDUtil.getPetWellLink(feature);
            } else {
                featTypeLbl = "Section Name";
                linkStart = "<a href=\"detail.jsp?FeatID=" + feature.getFeatureId() + backStr + "\">";
                linkStop = "</a>";
            }
    %><tr class="lightColour"><td class="heading"><%=featTypeLbl%></td><td><%=linkStart + DBUtils.nvl(feature.getFeatureName()) + linkStop%>
            <%=((petWellLink != null) ? "&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"" + petWellLink + "\" target=\"_blank\" class=\"boldlink\">Open GNS Petroleum Wells Database</a>" : "")%></td></tr><%
                }
                SiteView sv = null;
                if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
                    Datum datum = SiteUtil.getFREDDatum(feature);
                    Coordinate coord = SiteUtil.getFREDCoordinate(feature);
            %><tr class="lightColour"><td class="heading">Original Grid Reference</td><td><%=datum.getHumanStringFor(coord).replaceAll("Geographic ", "")%></td></tr><%
                if (!datum.getName().equals("NZMG")) {
                    try {
                        Datum nzmgDatum = DatumFactory.createDatum("NZMG");
                        Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
                        if (nzmgDatum.coordinateAcceptable(nzmgCoord)) {
    %><tr class="lightColour"><td class="heading">Converted Grid Reference</td><td><%=nzmgDatum.getHumanStringFor(nzmgCoord)%></td></tr><%
                }
            } catch (Exception e) {
            }
        }
        if (feature.getSiteView() != null) {
            sv = feature.getSiteView();
            LatLong ll = SiteUtil.getSiteLatLong(sv);
    %><tr class="lightColour"><td class="heading">Converted Dec. Lat/Long</td><td><%=ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)"%></td></tr><%
            }
        }
        if (feature.getMapYear() != null) {
    %><tr class="lightColour"><td class="heading">Map Year</td><td><%=DBUtils.nvl(feature.getMapYear())%></td></tr><%
        }
        if (sv != null && sv.getMethod() != null) {
    %><tr class="lightColour"><td class="heading">Method</td><td><%=sv.getMethod()%></td></tr><%
        }
        if (sv != null && sv.getAccuracy() != null) {
    %><tr class="lightColour"><td class="heading">Accuracy</td><td>&#177;<%=String.valueOf(sv.getAccuracy())%> m</td></tr><%
        }
        if (isAllowedReadFeature) {
            if (feature.getLocality() != null) {
    %><tr class="lightColour"><td class="heading">Locality</td><td><%=feature.getLocality()%></td></tr><%
        }
        if (sv != null && sv.getCountryName() != null) {
    %><tr class="lightColour"><td class="heading">Country</td><td><%=sv.getCountryName()%></td></tr><%
        }
        if (feature.getCoordComments() != null) {
    %><tr class="lightColour"><td class="heading">Coordinate Comments</td><td><%=DBUtils.nvl(feature.getCoordComments())%></td></tr><%
        }

        //Drillhole/Vert Sect fields
        if (!featType.equals(FREDConstants.OUTCROP)) {
            if (feature.getPerson() != null) {
    %><tr class="lightColour"><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Operating Company" : "Section Collector")%></td><td><%=feature.getPerson().getName()%></td></tr><%
        }
        if (feature.getStartDate() != null) {
            %><tr class="lightColour"><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Spud Date" : "Sampling Start Date")%></td><td><%=FREDUtil.formatDateForOutput(feature.getStartDate(), feature.getStartDateRounding())%></td></tr><%
        }
        if (feature.getFinishDate() != null) {
            %><tr class="lightColour"><td class="heading">Completion Date</td><td><%=FREDUtil.formatDateForOutput(feature.getFinishDate(), feature.getFinishDateRounding())%></td></tr><%
        }
        if (featType.equals(FREDConstants.DRILLHOLE) && feature.getDrillholeLicenceName() != null) {
    %><tr class="lightColour"><td class="heading">Licence Area</td><td><%=feature.getDrillholeLicenceName()%></td></tr><%
        }
        if (feature.getDatumType() != null) {
    %><tr class="lightColour"><td class="heading">Datum Type</td><td><%=feature.getDatumType()%></td></tr><%
        }
        if (feature.getDatumElevation() != null) {
    %><tr class="lightColour"><td class="heading">Datum Elevation</td><td><%=FeatureUtil.formatDepthForOutput(feature.getDatumElevation(), feature.getDepthUnit())%> asl</td></tr><%
        }
        if (feature.getStartDepth() != null) {
    %><tr class="lightColour"><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Kick-off Depth" : "Top Horizon")%></td><td><%=FeatureUtil.formatDepthForOutput(feature.getStartDepth(), feature.getDepthUnit())%></td></tr><%
        }
        if (feature.getFinishDepth() != null) {
            %><tr class="lightColour"><td class="heading"><%=((featType.equals(FREDConstants.DRILLHOLE)) ? "Termination Depth" : "Base Horizon")%></td><td><%=FeatureUtil.formatDepthForOutput(feature.getFinishDepth(), feature.getDepthUnit())%></td></tr><%
            }
        }
        if (feature.getComments() != null) {
            %><tr class="lightColour"><td class="heading">Locality Comments</td><td><%=feature.getComments()%></td></tr><%
        }

        //Image/Files
        if (feature.getMetaCats().size() > 0) {
    %><tr class="lightColour"><td colspan="2" class="heading">Images/Files</td></tr>
    <tr class="lightColour"><td colspan="2"><table border="0" cellspacing="0" width="550"><%
        int y = 1;
                %><tr><%
                    for (MetaCat metaCat : feature.getMetaCats()) {
                        if (y++ == 5) {
                    %></tr><tr><%
                            y = 2;
                        }
                        %><td width="150" align="center" class="smalltext"><a href="/online/DigitalDocument?src=<%=metaCat.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=metaCat.getMetaId()%>" alt="FRED Digital Document" /><br /><%=metaCat.getTitle()%></a></td><%
                            }
                            %></td></tr></table></td></tr><%
                                }

                    %><tr><td>&nbsp;</td></tr><%//Sample
                        if (sample != null) {
                            if (sampleUtil.isAllowedReadSample(user, sample)) {
                                //Sample Data
        %><tr class="midColour"><th colspan="2">Sample Information<%
        if (!featType.equals(FREDConstants.OUTCROP)) {
            //add PDF link
            %>&nbsp;&nbsp;&nbsp;<a href="frf/frf.pdf?SampIDs=<%=sample.getSampleId() + "&q=" + Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" title="Print" /></a><%
        }
                %></th></tr><%
                    if (!featType.equals(FREDConstants.OUTCROP)) {
                        if (sampleUtil.isSampleConfidential(sample)) {
            %><tr class="lightColour"><td style="text-align: left; color: #FF0000" colspan="2">This sample has been marked as confidential.  The following people/groups have been granted access to this sample: <%=sampleUtil.getSampleConfidAccessListDescription(sample)%>.<%=(sample.getAudit().getConfidLapseDate() != null) ? " This sample will become <i>open-file</i> on " + FREDUtil.formatDateForOutput(sample.getAudit().getConfidLapseDate()) + "." : ""%></td></tr><%
                }
                if (sample.getFrNumber() != null && !sample.getFrNumber().equals(feature.getFrNumber())) {
        %><tr class="lightColour"><td class="heading">Sample FR Number</td><td class="heading"><%=sample.getFrNumber().getFrNumber()%></td></tr><%
        }
        if (sample.getYardFrNumber() != null && !sample.getYardFrNumber().equals(feature.getYardFrNumber())) {
        %><tr class="lightColour"><td class="heading">Sample Yard FR Number</td><td><%=sample.getYardFrNumber().getFrNumber()%></td></tr><%
        }
        if (SampleUtil.getDrillHoleDepthDescription(sample) != null) {
    %><tr class="lightColour"><td class="heading">Sample Depth</td><td><%=SampleUtil.getDrillHoleDepthDescription(sample)%></td></tr><%
        }
        //check for samples above and below current one
        Sample sampleAbove = SampleUtil.getSampleAbove(sample);
        if (sampleAbove != null && sampleUtil.isAllowedReadSample(user, sampleAbove)) {
    %><tr class="lightColour"><td class="heading">Sample Above</td><td><a href="detail.jsp?ID=<%=sampleAbove.getSampleId() + backStr%>"><%=SampleUtil.getDrillHoleDepthDescription(sampleAbove)%></a></td></tr><%
        }
        Sample sampleBelow = SampleUtil.getSampleBelow(sample);
        if (sampleBelow != null && sampleUtil.isAllowedReadSample(user, sampleBelow)) {
            %><tr class="lightColour"><td class="heading">Sample Below</td><td><a href="detail.jsp?ID=<%=sampleBelow.getSampleId() + backStr%>"><%=SampleUtil.getDrillHoleDepthDescription(sampleBelow)%></a></td></tr><%
            }
        }

            %><tr class="midColour"><th colspan="2">Collection Information</th></tr><%    Object[] collectors = sample.getCollectors().toArray();
        String[] collectorStr = new String[collectors.length];
        for (int i = 0; i < collectors.length; i++) {
            collectorStr[i] = ((PersonRelationship) collectors[i]).getDisplayName();
        }
        addRepeatingCells(new PrintWriter(out), "Collectors", collectorStr, false);
        if (sample.getCollectionDate() != null) {
            %><tr class="lightColour"><td class="heading">Collection Date</td><td><%=FREDUtil.formatDateForOutput(sample.getCollectionDate(), sample.getDateRounding())%></td></tr><%
        }
        if (sample.getInPlace() != null) {
        %><tr class="lightColour"><td class="heading">Fossils in Place</td><td><%=sample.getInPlace()%></td></tr><%
        }
        Object[] sentTos = sample.getSentTos().toArray();
        String[] sentToStr = new String[sentTos.length];
        for (int i = 0; i < sentTos.length; i++) {
            sentToStr[i] = SampleUtil.getSentToDescription((SentTo) sentTos[i]);
        }
        addRepeatingCells(new PrintWriter(out), "Sent To", sentToStr, true);
        if (sample.getNotCollected() != null) {
    %><tr class="lightColour"><td class="heading">Not Collected</td><td><%=sample.getNotCollected()%></td></tr><%
        }
        if (sample.getSignificance() != null) {
    %><tr class="lightColour"><td class="heading">Significance/Comments</td><td><%=sample.getSignificance()%></td></tr><%
        }

    %><tr class="midColour"><th colspan="2">Stratigraphy</th></tr><%    if (sample.getStratUnit() != null) {
    %><tr class="lightColour"><td class="heading">Stratigraphic Name</td><td><%=sample.getStratUnit()%></td></tr><%
        }
        if (sample.getInferredStage() != null) {
        %><tr class="lightColour"><td class="heading">Inferred Stage</td><td><%=StageUtil.getStageDescription(sample.getInferredStage())%></td></tr><%
        }
        if (sample.getKnownStage() != null) {
    %><tr class="lightColour"><td class="heading">Known Stage</td><td><%=StageUtil.getStageDescription(sample.getKnownStage())%></td></tr><%
        }
        Object[] relationships = sampleUtil.getRelationships(sample, "Sample", "nearby").toArray();
        String[] relationshipStr = new String[relationships.length];
        for (int i = 0; i < relationships.length; i++) {
            relationshipStr[i] = SampleUtil.getRelationshipDescriptionWithLink((Relationship) relationships[i], "detail.jsp?FeatID=", null);
        }
        addRepeatingCells(new PrintWriter(out), "Samples Nearby", relationshipStr, false);
        relationships = sampleUtil.getRelationships(sample, "Sample", new String[]{"above", "below"}).toArray();
        relationshipStr = new String[relationships.length];
        for (int i = 0; i < relationships.length; i++) {
            relationshipStr[i] = SampleUtil.getRelationshipDescriptionWithLink((Relationship) relationships[i], "detail.jsp?FeatID=", null);
        }
        addRepeatingCells(new PrintWriter(out), "Sample Relationships", relationshipStr, false);
        relationships = sampleUtil.getRelationships(sample, "Stratigraphic", new String[]{"above top", "above base", "below top", "below base"}).toArray();
        relationshipStr = new String[relationships.length];
        for (int i = 0; i < relationships.length; i++) {
            relationshipStr[i] = SampleUtil.getRelationshipDescription((Relationship) relationships[i]);
        }
        addRepeatingCells(new PrintWriter(out), "Strat. Relationships", relationshipStr, true);
        if (sample.getColumnMap() != null) {
    %><tr class="lightColour"><td class="heading">Column/Map</td><td><%=sample.getColumnMap()%></td></tr><%
        }
        String dipStrike = SampleUtil.getDipStrikeDescription(sample);
        if (dipStrike != null && dipStrike.length() > 0) {
    %><tr class="lightColour"><td class="heading">Dip/Strike</td><td><%=dipStrike%></td></tr><%
        }
        if (sample.getStratComments() != null) {
    %><tr class="lightColour"><td class="heading">Stratigraphy Comments</td><td><%=sample.getStratComments()%></td></tr><%
        }

    %><tr class="midColour"><th colspan="2">Sedimentary Features</th></tr><%    String grainSize = SampleUtil.getGrainSizeDescription(sample);
        if (grainSize != null && grainSize.length() > 0) {
            %><tr class="lightColour"><td class="heading">Grain Size</td><td><%=grainSize%></td></tr><%
        }
        if (sample.getBedThickness() != null) {
        %><tr class="lightColour"><td class="heading">Bedding Thickness</td><td><%=sample.getBedThickness().getName()%></td></tr><%
        }
        String bedDesc = SampleUtil.getBeddingDescription(sample);
        if (bedDesc != null && bedDesc.length() > 0) {
    %><tr class="lightColour"><td class="heading">Bedding Features</td><td><%=bedDesc%></td></tr><%
        }
        if (sample.getWeathering() != null) {
    %><tr class="lightColour"><td class="heading">Weathering</td><td><%=sample.getWeathering().getName()%></td></tr><%
        }
        if (sample.getHardness() != null) {
    %><tr class="lightColour"><td class="heading">Hardness</td><td><%=sample.getHardness().getName()%></td></tr><%
        }
        if (sample.getCarbonate() != null) {
    %><tr class="lightColour"><td class="heading">Carbonate</td><td><%=sample.getCarbonate().getName()%></td></tr><%
        }
        String colourDesc = SampleUtil.getColourDescription(sample);
        if (colourDesc != null && colourDesc.length() > 0) {
    %><tr class="lightColour"><td class="heading">Colour</td><td><%=colourDesc%></td></tr><%
        }
        Object[] sedFeatures = sample.getSedimentaryFeatures().toArray();
        String[] sedFeaturesStr = new String[sedFeatures.length];
        for (int i = 0; i < sedFeatures.length; i++) {
            sedFeaturesStr[i] = SampleUtil.getSedFeatureDescription((SedimentaryFeature) sedFeatures[i]);
        }
        addRepeatingCells(new PrintWriter(out), "Additional Features", sedFeaturesStr, false);
        if (sample.getDepositionEnv() != null) {
    %><tr class="lightColour"><td class="heading">Inferred Environment</td><td><%=sample.getDepositionEnv()%></td></tr><%
        }
        if (sample.getRockNature() != null) {
    %><tr class="lightColour"><td class="heading">Nature of Rock Unit</td><td><%=sample.getRockNature()%></td></tr><%
        }
        if (sample.getCorrespondence() != null) {
    %><tr class="lightColour"><td class="heading">Correspondence</td><td><%=sample.getCorrespondence()%></td></tr><%
        }

        //Image/Files
        if (sample.getMetaCats().size() > 0) {
    %><tr class="lightColour"><td colspan="2" class="heading">Images/Files</td></tr>
    <tr class="lightColour"><td colspan="2"><table border="0" cellspacing="0" width="550"><%
        int y = 1;
                %><tr><%
                    for (MetaCat metaCat : sample.getMetaCats()) {
                        if (y++ == 5) {
                    %></tr><tr><%
                            y = 2;
                        }
                        %><td width="150" align="center" class="smalltext"><a href="/online/DigitalDocument?src=<%=metaCat.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=metaCat.getMetaId()%>" alt="FRED Digital Document" /><br /><%=metaCat.getTitle()%></a></td><%
                            }
                            %></td></tr></table></td></tr><%
                                }

                    %><tr><td>&nbsp;</td></tr><%//Adoption
                        for (Adoption adoRecord : sampleUtil.getAdoptionRecords(sample)) {
                            if (recordUtil.isAllowedReadRecord(user, adoRecord.getRecord())) {
        %><tr class="midColour"><th colspan="2">Adoption Information&nbsp;&nbsp;&nbsp;<a href="frf/frf.pdf?RecIDs=<%=adoRecord.getRecordId()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" title="Print" /></th></tr><%
        if (recordUtil.isRecordConfidential(adoRecord.getRecord())) {
                %><tr class="lightColour"><td style="text-align: left; color: #FF0000" colspan="2">This adoption record has been marked as confidential. The following people/groups have been granted access to this record: <%=recordUtil.getRecordConfidAccessListDescription(adoRecord.getRecord())%>.<%=(adoRecord.getRecord().getAudit().getConfidLapseDate() != null) ? " This record will become <i>open-file</i> on " + FREDUtil.formatDateForOutput(adoRecord.getRecord().getAudit().getConfidLapseDate()) + "." : ""%></td></tr><%
                    }
                    Object[] adoptors = adoRecord.getAdoptors().toArray();
                    String[] adoptorsStr = new String[adoptors.length];
                    for (int j = 0; j < adoptors.length; j++) {
                        adoptorsStr[j] = ((PersonRelationship) adoptors[j]).getDisplayName();
                    }
                    addRepeatingCells(new PrintWriter(out), "Adoptors", adoptorsStr, false);
                    if (adoRecord.getAdoptionDate() != null) {
        %><tr class="lightColour"><td class="heading">Adoption Date</td><td><%=FREDUtil.formatDateForOutput(adoRecord.getAdoptionDate(), adoRecord.getDateRounding())%></td></tr><%
        }
        if (adoRecord.getStage() != null) {
    %><tr class="lightColour"><td class="heading">Adopted Stage</td><td><%=StageUtil.getStageDescription(adoRecord.getStage())%></td></tr><%
        }
        if (adoRecord.getComments() != null) {
    %><tr class="lightColour"><td class="heading">Comments</td><td><%=adoRecord.getComments()%></td></tr><%
        }

        //Image/Files
        if (adoRecord.getRecord().getMetaCats().size() > 0) {
    %><tr class="lightColour"><td colspan="2" class="heading">Images/Files</td></tr>
    <tr class="lightColour"><td colspan="2"><table border="0" cellspacing="0" width="600"><%
        int y = 1;
                %><tr><%
                    for (MetaCat metaCat : adoRecord.getRecord().getMetaCats()) {
                        if (y++ == 5) {
                    %></tr><tr><%
                            y = 2;
                        }
                        %><td width="150" align="center" class="smalltext"><a href="/online/DigitalDocument?src=<%=metaCat.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=metaCat.getMetaId()%>" alt="FRED Digital Document" /><br /><%=metaCat.getTitle()%></a></td><%
                            }
                            %></td></tr></table></td></tr><%
                                }
                    %><tr><td>&nbsp;</td></tr><%
                            }
                        }


    //Consensus age 
    for (nz.cri.gns.fred.model.SquirrelAgeView ageView : sample.getSquirrelAge()) {
        if (true) { //check if this needs to be protected
            %>
            <tr class="midColour">
                <th colspan="2">
                    Consensus Age&nbsp;&nbsp;&nbsp;
                    <!--a href="#" target="_blank">
                    <img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" title="Print" /-->
                </th>
            </tr>
            <tr class="lightColour"><td class="heading">Consensus Stage (wide)</td><td><%=ageView.getWideBaseAge()%> - <%=ageView.getWideTopAge()%> Ma</td></tr>
            <tr class="lightColour"><td class="heading">Consensus Stage (narrow)</td><td><%=ageView.getNarrowBaseAge()%> - <%=ageView.getNarrowTopAge()%> Ma</td></tr>
            <%
        }
    }
    %><tr><td>&nbsp;</td></tr><%

    //Paleontology
    boolean hasPalRecords = false;
    for (Paleontology palRecord : sampleUtil.getPaleontologyRecords(sample)) {
        hasPalRecords = true;
        if (recordUtil.isAllowedReadRecord(user, palRecord.getRecord())) {
            %>
            <tr class="midColour">
                <th colspan="2">
                    Paleontology Information&nbsp;&nbsp;&nbsp;
                    <a href="frf/frf.pdf?RecIDs=<%=palRecord.getRecordId()%>" target="_blank">
                    <img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" title="Print" />
                </th>
            </tr>
            
            <% if (recordUtil.isRecordConfidential(palRecord.getRecord())) { %>
            
            <tr class="lightColour">
                <td style="text-align: left; color: #FF0000" colspan="2">
                    This paleontology record has been marked as confidential. The following people/groups have 
                    been granted access to this record: 
                    <%=recordUtil.getRecordConfidAccessListDescription(palRecord.getRecord())%>.
                    <%=(palRecord.getRecord().getAudit().getConfidLapseDate() != null) 
                            ? " This record will become <i>open-file</i> on " 
                                + FREDUtil.formatDateForOutput(palRecord.getRecord().getAudit().getConfidLapseDate()) + "." 
                            : ""%>
                </td>
            </tr>
            
            <% } else if (recordUtil.isPalListConfidential(palRecord)) { %>
            
            <tr class="lightColour">
                <td style="text-align: left; color: #FF0000" colspan="2">
                    The taxonomic list in this paleontology record has been marked as confidential. The following 
                    people/groups have been granted access to this record: 
                    <%=recordUtil.getPalListConfidAccessListDescription(palRecord)%>.
                    <%=(palRecord.getRecord().getPalListAudit().getConfidLapseDate() != null) 
                            ? " This list will become <i>open-file</i> on " 
                                + FREDUtil.formatDateForOutput(palRecord.getRecord().getPalListAudit().getConfidLapseDate()) + "." : ""%>
                </td>
            </tr>
        <% }
            
        Object[] identifiers = palRecord.getIdentifiers().toArray();
        String[] identifiersStr = new String[identifiers.length];
        for (int j = 0; j < identifiers.length; j++) {
            identifiersStr[j] = ((PersonRelationship) identifiers[j]).getDisplayName();
        }
        addRepeatingCells(new PrintWriter(out), "Identifiers", identifiersStr, false);
        if (palRecord.getIdentificationDate() != null) {
    %><tr class="lightColour"><td class="heading">Identification Date</td><td><%=FREDUtil.formatDateForOutput(palRecord.getIdentificationDate(), palRecord.getDateRounding())%></td></tr><%
        }
        if (palRecord.getStage() != null) {
    %><tr class="lightColour"><td class="heading">Stage</td><td><%=StageUtil.getStageDescription(palRecord.getStage())%></td></tr><%
        }
        if (palRecord.getStageComments() != null) {
    %><tr class="lightColour"><td class="heading">Stage Comments</td><td><%=palRecord.getStageComments()%></td></tr><%
        }
        if (palRecord.getLabNumber() != null) {
    %><tr class="lightColour"><td class="heading">Lab Number</td><td><%
        if (site != 0 && palRecord.getLabSection() != null && palRecord.getLabSection().getLab().getName().equals("GNS")) {
            %><a href="http://data.gns.cri.nz/npc/manage/number/<%=palRecord.getLabSection().getCode()%>/<%=palRecord.getLabNumber()%>" target="npc"><%
                }
                %><%=RecordUtil.getLabNumberDescription(palRecord)%><%
                    if (site != 0 && palRecord.getLabSection() != null && palRecord.getLabSection().getLab().getName().equals("GNS")) {
                %></a><%                                                                                    }
            %></td></tr><%
                }
                if (palRecord.getCollectionComments() != null) {
            %><tr class="lightColour"><td class="heading">Collection Comments</td><td><%=palRecord.getCollectionComments()%></td></tr><%
                }

                //taxa (Pal list)
                if (recordUtil.isAllowedReadPalList(user, palRecord) && palRecord.getListEntries() != null) {
        %><tr><td colspan="2"><table border="0" cellpadding="3" cellspacing="2" width="100%"><%
        for (TaxonomicGroup taxaGroup : recordUtil.getTaxonomicGroups(palRecord)) {
                %><tr class="midColour"><th colspan="6"><%=taxaGroup.getName()%></th></tr><%
                    if (recordUtil.getListEntries(palRecord, taxaGroup).size() > 0) {
                        %><tr class="midColour">
                    <td class="heading">Taxonomic Name&nbsp;&nbsp;</td><%
                        if (authorChk) {
                    %><td class="heading">Author&nbsp;&nbsp;</td><%                                                                                                    }
                        if (sCountChk) {
                    %><td class="heading">Spec Count&nbsp;&nbsp;</td><%                                                                                                    }
                        if (sCoordChk) {
                    %><td class="heading">Spec Coord&nbsp;&nbsp;</td><%                                                                                                    }
                        if (commChk) {
                    %><td class="heading">Comments&nbsp;&nbsp;</td><%                                                                                                    }
                    %><td>&nbsp;</td>
                </tr><%
                    for (PaleontologyListEntry taxa : recordUtil.getListEntries(palRecord, taxaGroup)) {
                        Taxon taxon = taxa.getTaxon();
                %><tr class="lightColour"><td><%
                    boolean taxonInNpc = TaxonomicUtil.isTaxonInNpc(taxon);
                    if (taxonInNpc) {
                        %><a href="http://data.gns.cri.nz/npc/catalogue/taxon.jsp?taxonId=<%=taxon.getTaxaId()%>" target="npc"><%
                            }
                            %><i><%=(taxa.getTaxonomicName() != null) ? taxa.getTaxonomicName() : "no taxa identified"%></i><%
                                if (taxonInNpc) {
                                %></a><%                                                                                                            }
                        %>&nbsp;&nbsp;</td><%
                            if (authorChk) {
                        %><td><%=(taxon != null) ? DBUtils.nvl(taxa.getTaxon().getAuthor()) : ""%>&nbsp;&nbsp;</td><%
                            }
                            if (sCountChk) {
                    %><td><%=DBUtils.nvl(taxa.getSpecimenCount())%>&nbsp;&nbsp;</td><%
                        }
                        if (sCoordChk) {
                    %><td><%=DBUtils.nvl(taxa.getSpecimenCoords())%>&nbsp;&nbsp;</td><%
                        }
                        if (commChk) {
                    %><td><%=DBUtils.nvl(taxa.getComments())%>&nbsp;&nbsp;</td><%
                        }
                    %><td><%
                        for (MetaCat metaCat : taxa.getMetaCats()) {
                        %><a href="/online/DigitalDocument?src=<%=metaCat.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=metaCat.getMetaId()%>" alt="FRED Digital Document" /><br /><%=metaCat.getTitle()%></a><br /><%
                            }
                            %></td>
                </tr><%
                    }
                } else {
                %><tr class="lightColour"><td colspan="4">No fossils listed</td></tr><%                                                                                                }
                    }
                %></table></td></tr><%
                } else {
                %><tr><td colspan="2">You do not have the rights to view the taxonomic list for this record</td></tr><%                                                                            }
    //Image/Files
                    if (palRecord.getRecord().getMetaCats().size() > 0) {
    %><tr class="lightColour"><td colspan="2" class="heading">Images/Files</td></tr>
    <tr class="lightColour"><td colspan="2"><table border="0" cellspacing="0" width="600"><%
        int y = 1;
                %><tr><%
                    boolean hasMetaCats = false;
                    for (MetaCat metaCat : palRecord.getRecord().getMetaCats()) {
                        hasMetaCats = true;
                        if (y++ == 5) {
                    %></tr><tr><%
                            y = 2;
                        }
                        %><td width="150" align="center" class="smalltext"><a href="/online/DigitalDocument?src=<%=metaCat.getMetaId()%>"><img border="0" src="/online/Thumbnail?src=<%=metaCat.getMetaId()%>" alt="FRED Digital Document" /><br /><%=metaCat.getTitle()%></a></td><%
                            }
                            if (!hasMetaCats) {
                                %><tr class="lightColour"><td >No Images/Files available</td></tr><%
                            }
                            %></td></tr></table></td></tr><%
                    } // for (metaCat: ...
        } else {
            // IS-814: Chris and Marianna have decided not to let the user know.
            log.log(Level.INFO, "User is not allowed to view this record.");            
        }
     } // for (palRecord ... 
                                } else { // if (user can read sample ...
                                    log.log(Level.INFO, "User is not allowed to view this sample.");
                                }
                            } else {
                                //Sample List
%></table>
<table border="0" cellpadding="3" cellspacing="2" width="550">
    <tr class="midColour"><th colspan="2"><%=featType%> Samples</th></tr><%
        for (Sample locSample : FeatureUtil.getSortedSamples(feature)) {
            if (sampleUtil.isAllowedReadSample(user, locSample)) {
            %><tr class="lightColour">
        <td><a href="detail.jsp?ID=<%=locSample.getSampleId() + backStr%>"><%=SampleUtil.getDrillHoleDepthDescription(locSample) + ((locSample.getFrNumber() != null && !locSample.getFrNumber().equals(feature.getFrNumber())) ? " (" + locSample.getFrNumber().getFrNumber() + ")" : "")%></a>&nbsp;&nbsp;</td>
        <td><a href="frf/frf.pdf?SampIDs=<%=locSample.getSampleId() + "&q=" + Math.random()%>" target="_blank"><img src="images/pdf_icon.gif" width="20" height="20" border="0" alt="Print" title="Print" /></a></td>
    </tr><%
                }
            }
        }
    } else //didn't pass isAllowedReadFeature()
    if (user == null) {
    %><tr class="lightColour"><td colspan="2">&nbsp;</td></tr>
    <tr class="lightColour"><td colspan="2">More data may be available for this locality for <a href="<%=this.getLoginURL() + "?loginpage=" + java.net.URLEncoder.encode(request.getRequestURI(), "ISO-8859-1")%>" class="boldlink">logged</a> in users</td></tr><%
        }
    %></table></p><%
    } else {
//didn't pass isAllowedReadFeatureSite()
        drawTop(out, et, request, response);
    %><table style="margin-left:20px; margin-top:20px; width:550px;" border="0">
    <tr><td>You do not have rights to view this sample</td></tr><%
        if (user == null) {
    %><tr><td colspan="2">You may be able to view it if you <a href="<%=this.getLoginURL() + "?loginpage=" + java.net.URLEncoder.encode(request.getRequestURI(), "ISO-8859-1")%>" class="boldlink">login</a></td></tr><%
        }
    %></table></p><%
        }
    } else {
        //no sampleID
        drawTop(out, et, request, response);
    %><table style="margin-left:20px; margin-top:20px; width:550px;" border="0">
    <tr><td>No Locality found</td></tr>
</table><%
        }

        drawBottom(out, et);
    } finally {
        try {
            FredHibernate.get().getDAOFactory().closeSession();
        } catch (Exception e) {
        }
    }
%>