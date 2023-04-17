package nz.cri.gns.fred.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.*;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.query.FREDQuery;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.AuditUtil;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.jsp.ExtranetTemplate;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.jsp.IconnedLink;
import nz.cri.gns.auth.domain.User;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.HashSet;
import java.net.URLEncoder;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.jsp.Link;
import nz.cri.gns.jsp.CustomHTMLLink;
import java.util.Set;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.cri.gns.auth.security.IpGrantedAuthority;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.FREDHibernateServlet;
import nz.cri.gns.fred.FredGrantedAuthorities;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.servlet.util.FredHelper;
import nz.cri.gns.fred.servlet.util.JspWriterImpl;
import nz.cri.gns.fred.util.StageUtil;

public class ResultList_jsp extends FREDHibernateServlet {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.servlet.ResultList_jsp");
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String whereSQL = request.getParameter("WhereSQL");
        String tableName = request.getParameter("TableName");
        String queryStringParam = request.getParameter("QueryString");
        String page = request.getParameter("Page");
        String type = request.getParameter("Type");

        JspWriterImpl out = new JspWriterImpl(response.getOutputStream());
        HttpSession session = request.getSession();

        FredHelper h = new FredHelper(); // Replaces subclassing FREDDEIPSysJspPage. 
        User user = h.getUser(session);

        try {
            response.setContentType("text/html;charset=utf-8");
            // Define page variables and initialise head
            DAOFactory factory = FredHibernate.get().getDAOFactory();
            SampleUtil sampleUtil = new SampleUtil(factory);
            FeatureUtil featureUtil = new FeatureUtil(factory);
            AuditUtil auditUtil = new AuditUtil(factory);
            StageUtil stageUtil = new StageUtil(factory);

            // Define HTTP state variables
            PageState state = new PageState(request, response, getServletContext());

            // Define other page variables
            String queryURL = request.getParameter("QueryURL");
            if (queryURL == null) {
                queryURL = "simple_query.jsp";
            }
            int pageSize = 50;

            // Define the extranet template for this page
            ExtranetTemplate et = h.getExtranetTemplate();
            et.setDisplayLoadingMessage(true);

            // Define a vector of links
            Vector<Link> il = new Vector<Link>();
            il.add(new IconnedLink("export_setup.jsp", "images/save.gif", "Download Results"));

            // Add to Folder link
            if (user != null && new FolderUtil(factory).getPersonalFolders(user).size() > 0) {
                StringBuilder customHTML = new StringBuilder("<form method=\"post\" onsubmit=\"addFeaturesToActionURL(this)\" action=\"result_list.jsp?Page=" + ((request.getParameter("Page") == null) ? "1" : request.getParameter("Page")) + "\" name=\"FolderForm\" style=\"display: inline; margin: 0;\">");
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

            // Adds all the links in the array to the extranet template
            h.addButtons(et, il.toArray(new Link[il.size()]));

            // Add scripts to extranet template 
            et.addScript("scripts/resultList.js");
            //et.setBodyTag("onload=\"updateMasterCheckbox()\"");

            // Start drawing page from the extranet template defined	
            h.drawTop(out, et, request, response);

            // Execute any actions
            String alertText = "";
            String actionType = request.getParameter("ActionType");
            String foldId = request.getParameter("FoldID");
            String[] featureIdsStr = request.getParameterValues("fid");
            if (user != null && actionType != null && foldId != null && actionType.equals("AddtoFold") && !foldId.equals("-") && featureIdsStr != null) {
                for (int i = 0; i < featureIdsStr.length; i++) {
                    try {
                        int featureId = Integer.parseInt(featureIdsStr[i]);
                        Feature feature = featureUtil.getFeature(featureId);

                        if (featureUtil.isAllowedReadFeature(user, feature)) {
                            try {
                                featureUtil.addToFolder(feature, Integer.parseInt(request.getParameter("FoldID")), user);
                            } catch (DataInputException e) {
                                throw new ServletException(e);
                            }
                            alertText += "Locality, " + feature + " Added to Folder.\\n";
                        } else {
                            alertText += "Locality, " + feature + " Not Added to Folder. User does not have read rights for this record.\\n";
                        }
                    } catch (NumberFormatException nfe) {
                        // TODO: catch this earlier.
                    }
                }
                out.write("\t\n");
                out.write("\t\t<script type=\"text/javascript\"><!--\n");
                out.write("\t\t\talert(");
                out.print("\"" + alertText + " \"");
                out.write(");\n");
                out.write("\t\t//-->\n");
                out.write("\t\t</script>");
            }

            String queryString;

            int pageNum = 1;
            if (page != null) {
                pageNum = Integer.parseInt(page);
            }
            boolean useStored = (page != null);

            session.setAttribute("dataEntryRedirect", "result_list.jsp?Page=" + pageNum);

            List<Sample> samples = null;
            List<Feature> features = null;
            List<Object> resultsList = new Vector<Object>();
            if (useStored) {
                samples = (List<Sample>) session.getAttribute("FRED.samples");
                features = (List<Feature>) session.getAttribute("FRED.features");
                queryString = (String) session.getAttribute("FRED.queryString");
            } else if ("Adv".equals(type)) {
                if (!h.checkAccess(request, response, new IpGrantedAuthority(FredGrantedAuthorities.FR_WEBSITE_ACCESS))) {
                    // TODO: what access should they have? I can't find it.
                    return;
                }
                FREDQuery query = FREDUtil.getFREDQuery(state);
                queryString = query.getQueryAsString();
                String hq = query.getHQLQuery("Adv", "");
                samples = sampleUtil.getLightweightSamples(hq);;
                features = featureUtil.getFeaturesBySampleSubquery(hq);
                //auditUtil.addLogEntry(AuditUtil.QUERY_LOG_TYPE, user, features.size());
                log.log(Level.INFO, "Hibernate Query " + hq);
            } else {
                queryString = queryStringParam;
                //Account for large number of SAMPLE_IDs provided by polygon filter
                String idString = request.getParameter("idList");

                // Calculate Squirrel Narrow ages.
                // "From" is always older than "To". This means that (From > To) as the ages are the positive number of years ago.
                boolean hasSquirrelAge = false;
                Integer sqNarrowAgeFromId = paramAsInt(request, "SquirrelNarrowAgeFrom");
                Integer sqNarrowAgeToId = paramAsInt(request, "SquirrelNarrowAgeTo");
                Double sqNarrowAgeFrom = 999.9;
                Double sqNarrowAgeTo = 0.0;
                if (null != sqNarrowAgeFromId) {
                    sqNarrowAgeFrom = stageUtil.getAge(sqNarrowAgeFromId).getBaseAge();
                    hasSquirrelAge = true;
                }
                if (null != sqNarrowAgeToId) {
                    sqNarrowAgeTo = stageUtil.getAge(sqNarrowAgeToId).getTopAge();
                    hasSquirrelAge = true;
                }
                // Swap ages if the user got them the wrong way around.
                if (hasSquirrelAge && sqNarrowAgeFrom < sqNarrowAgeTo) {
                    Double swap = sqNarrowAgeFrom;
                    sqNarrowAgeFrom = sqNarrowAgeTo;
                    sqNarrowAgeTo = swap;
                }
                
                Integer sqWideAgeFromId = paramAsInt(request, "SquirrelWideAgeFrom");
                Integer sqWideAgeToId = paramAsInt(request, "SquirrelWideAgeTo");
                Double sqWideAgeFrom = 999.9;
                Double sqWideAgeTo = 0.0;
                if (null != sqWideAgeFromId) {
                    sqWideAgeFrom = stageUtil.getAge(sqWideAgeFromId).getBaseAge();
                    hasSquirrelAge = true;
                }
                if (null != sqWideAgeToId) {
                    sqWideAgeTo = stageUtil.getAge(sqWideAgeToId).getTopAge();
                    hasSquirrelAge = true;
                }
                // Swap ages if the user got them the wrong way around.
                // 'From' is older (base age). 'To' is newer (top age). 
                if (hasSquirrelAge && sqWideAgeFrom < sqWideAgeTo) {
                    Double swap = sqWideAgeFrom;
                    sqWideAgeFrom = sqWideAgeTo;
                    sqWideAgeTo = swap;
                }

                StringBuilder sampHqlStr = new StringBuilder();
                sampHqlStr.append("SELECT DISTINCT s.sampleId FROM ");
                sampHqlStr.append(tableName);

                if (hasSquirrelAge) {
                    sampHqlStr.append(" JOIN s.squirrelAge as squirrelAge ");
                }

                sampHqlStr.append(" WHERE ");
                sampHqlStr.append(whereSQL);

                if (hasSquirrelAge) {
                    // Grumble mumble. We should use parameters here.
//                    sampHqlStr.append(" AND (squirrelAge.narrowBaseAge > ");
//                    sampHqlStr.append(sqNarrowAgeTo);
//                    sampHqlStr.append(") AND (squirrelAge.narrowTopAge < ");
//                    sampHqlStr.append(sqNarrowAgeFrom);

                    sampHqlStr.append(" AND (squirrelAge.narrowBaseAge <= ");
                    sampHqlStr.append(sqNarrowAgeFrom);
                    sampHqlStr.append(") AND (squirrelAge.narrowTopAge >= ");
                    sampHqlStr.append(sqNarrowAgeTo);

                    sampHqlStr.append(") AND (squirrelAge.wideBaseAge > ");
                    sampHqlStr.append(sqWideAgeTo);
                    sampHqlStr.append(") AND (squirrelAge.wideTopAge < ");
                    sampHqlStr.append(sqWideAgeFrom);
                    sampHqlStr.append(") ");
                    
                    // Show this to the user.
                    StringBuilder s = new StringBuilder(queryString);
                    s.append(" AND Consensus narrow age from  ");
                    s.append(sqNarrowAgeFrom);
                    s.append(" to ");
                    s.append(sqNarrowAgeTo);
                    s.append(" AND Consensus wide age from ");
                    s.append(sqWideAgeFrom);
                    s.append(" to ");
                    s.append(sqWideAgeTo);
                    queryString = s.toString();
                    
                }
                
                FREDQuery query = FREDUtil.getFREDQuery(state);
//                queryString = query.getQueryAsString();
                String hq = query.getHQLQuery("simple", sampHqlStr.toString());

                String sampHql = sampHqlStr.toString();
             
                //if polygon vertices are set, apply spatial filter
                if (idString != null && idString.length() > 0) {
                    samples = getSpatiallyFilteredSamples(sampleUtil, idString.split(","), hq);
                } else {
                    samples = sampleUtil.getLightweightSamples(hq);
                }
                features = featureUtil.getFeaturesBySampleSubquery(samples);
                auditUtil.addLogEntry(AuditUtil.QUERY_LOG_TYPE, user, features.size());

            }
            int numRecords = features.size();
            if (numRecords > 0) {

                //save QueryRes vector
                session.setAttribute("FRED.samples", samples);
                session.setAttribute("FRED.features", features);
                session.setAttribute("FRED.queryString", queryString);                
                log.log(Level.INFO, "ResultList feat size " + (features != null ? features.size() : 0) + " sample size " + (samples != null ? samples.size() : 0) + " query " + queryString);
                //Navigation
                int startIndex = (pageNum - 1) * pageSize + 1;
                int endIndex = Math.min(numRecords, startIndex + pageSize - 1);

                //Set pages to list
                int startPage = 1;
                int endPage = (int) Math.ceil(numRecords / (float) pageSize);
                int minRangePage = pageNum - 3;
                int maxRangePage = pageNum + 3;
                //Bring bottom up
                if (minRangePage < startPage) {
                    maxRangePage += (startPage - minRangePage);
                    minRangePage = startPage;
                }
                //Pull top down
                if (maxRangePage > endPage) {
                    minRangePage = Math.max(startPage, minRangePage - maxRangePage + endPage);
                    maxRangePage = endPage;
                }

                //list matching localities
                out.write("\n");
                out.write("\t\t\t<form method=\"post\" id=\"resultsForm\" action=\"result_list.jsp\">\n");
                out.write("\t\t\t\t<table border=\"0\" cellpadding=\"3\" cellspacing=\"2\" width=\"600\">\n");
                out.write("\t\t\t\t\t<tr class=\"midColour\">\n");
                out.write("\t\t\t\t\t\t<th colspan=\"6\">Matching Localities</th>\n");
                out.write("\t\t\t\t\t</tr>\n");
                out.write("\t\t\t\t\t<tr class=\"midColour\">\n");
                out.write("\t\t\t\t\t\t<td colspan=\"6\">\n");
                out.write("\t\t\t\t\t\t\tSearch Criteria: <em>");
                out.print(queryString);
                out.write("</em>\n");
                out.write("\t\t\t\t\t\t</td>\n");
                out.write("\t\t\t\t\t</tr>");

                if (maxRangePage > 1) {
                    out.write("\n");
                    out.write("\t\t\t\t\t\t\t<tr class=\"midColour\">\n");
                    out.write("\t\t\t\t\t\t\t\t<td class=\"heading\" colspan=\"4\">\n");
                    out.write("\t\t\t\t\t\t\t\t\tDisplaying records ");
                    out.print(startIndex);
                    out.write(" to ");
                    out.print(endIndex);
                    out.write(" of ");
                    out.print(numRecords);
                    out.write("\n");
                    out.write("\t\t\t\t\t\t\t\t</td>\n");
                    out.write("\t\t\t\t\t\t\t\t<td style=\"text-align: right\" colspan=\"2\">");

                    if (pageNum > startPage) {

                        out.write("<a href=\"result_list.jsp?Page=");
                        out.print(startPage);
                        out.write('"');
                        out.print(((startPage == pageNum) ? " class=\"heading\"" : ""));
                        out.write(">First ..</a>");

                    }
                    for (int i = minRangePage; i <= maxRangePage; i++) {

                        out.write("&nbsp;<a href=\"result_list.jsp?Page=");
                        out.print(i);
                        out.write('"');
                        out.print(((i == pageNum) ? " class=\"heading\"" : ""));
                        out.write('>');
                        out.print(i);
                        out.write("</a>");

                    }
                    if (pageNum < endPage) {

                        out.write("<a href=\"result_list.jsp?Page=");
                        out.print(endPage);
                        out.write('"');
                        out.print(((endPage == pageNum) ? " class=\"heading\"" : ""));
                        out.write(">.. Last</a>");

                    }

                    out.write("</td>\n");
                    out.write("\t\t\t\t\t\t\t</tr>");

                }
                out.write("\n");
                out.write("                                               \n");
                out.write("\t\t\t\t\t\n");
                out.write("\t\t\t\t\t<tr class=\"midColour\">\n");
                out.write("\t\t\t\t\t\t<th>\n");
                out.write("\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"MasterCheckbox\" onchange=\"updateAllCheckBoxes(this.checked);\" />\n");
                out.write("\t\t\t\t\t\t</th>\n");
                out.write("\t\t\t\t\t\t<th colspan=\"2\">FR Number&nbsp;&nbsp;</th>\n");
                out.write("\t\t\t\t\t\t<th>Type&nbsp;&nbsp;</th>\n");
                out.write("\t\t\t\t\t\t<th>Name&nbsp;&nbsp;</th>\n");
                out.write("\t\t\t\t\t\t<th>Actions</th>\n");
                out.write("\t\t\t\t\t</tr>");

                // Obtains feature ids from GET
                Set<String> fids;
                if (request.getParameterValues("fid") == null) {
                    fids = new HashSet<String>();
                } else {
                    fids = new HashSet<String>(Arrays.asList(request.getParameterValues("fid")));
                }

                List<Feature> pageFeatures = features.subList(startIndex - 1, endIndex);

                for (Feature feature : pageFeatures) {
                    feature = featureUtil.getFeature(feature.getFeatureId());
                    if (featureUtil.isAllowedReadFeatureSite(user, feature)) {
                        resultsList.add(feature);

                        String checkedText = ""; // default un-checked
                        if (!fids.isEmpty() && fids.contains(feature.getFeatureId().toString())) {
                            checkedText = "checked=\"checked\"";
                        }

                        out.write("<tr class=\"lightColour\">\n");
                        out.write("                                                            <td>\n");
                        out.write("                                                                    <input type=\"checkbox\" name=\"FeatIDs\" ");
                        out.print(checkedText);
                        out.write(" onchange=\"updateMasterCheckbox()\" value=\"");
                        out.print(feature.getFeatureId());
                        out.write("\" />\n");
                        out.write("                                                            </td>\n");
                        out.write("                                                            <td>\n");
                        out.write("                                                                <a href=\"detail.jsp?FeatID=");
                        out.print(feature.getFeatureId());
                        out.write("&backURL=");
                        out.print(URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1"));
                        out.write("&backText=Back+To+Result+List\"><img src=\"images/loc.gif\" border=\"0\" height=\"20\" width=\"20\" alt=\"View Locality\" title=\"View Locality\"/></a>\n");
                        out.write("                                                            </td>\n");
                        out.write("                                                            <td class=\"heading\">");
                        out.print(feature.getFrNumber());
                        out.write(' ');
                        out.print((feature.getYardFrNumber() != null) ? "(" + feature.getYardFrNumber() + ")" : "");
                        out.write("&nbsp;&nbsp;</td>\n");
                        out.write("                                                            <td>");
                        out.print(feature.getFeatureType());
                        out.write("&nbsp;&nbsp;</td>\n");
                        out.write("                                                            <td>");
                        out.print(DBUtils.nvl(feature.getFeatureName()));
                        out.write("&nbsp;&nbsp;</td>\n");
                        out.write("                                                            <td>\n");
                        out.write("                                                                <a href=\"locality_map.jsp?FeatID=");
                        out.print(feature.getFeatureId());
                        out.write("&backURL=");
                        out.print(URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1"));
                        out.write("&backText=Back%20To%20Result%20List\">\n");
                        out.write("                                                                    <img src=\"images/map.gif\" height=\"20\" width=\"20\" border=\"0\" alt=\"View Locality Map\" />\n");
                        out.write("\t\t\t\t\t\t\t\t</a>&nbsp;&nbsp;\n");
                        out.write("                                                                ");

                        if (user != null && featureUtil.isAllowedEditApprovedFeature(user, feature)) {

                            out.write("<a href=\"de.jsp?Type=");
                            out.print(feature.getFeatureType());
                            out.write("&FeatID=");
                            out.print(feature.getFeatureId());
                            out.write("&FoldID=");
                            out.print(feature.getMasterFile().getFolderId());
                            out.write("\">\n");
                            out.write("                                                                <img src=\"images/edit.gif\" height=\"20\" width=\"20\" border=\"0\" alt=\"Edit\" title=\"Edit\"/>\n");
                            out.write("                                                            </a>");

                        }
                        out.write("\n");
                        out.write("                                                            </td>\t\t\t\t\t\t\n");
                        out.write("\t\t\t\t\t\t\t</tr>");

                        if (!FeatureUtil.OUTCROP.equals(feature.getFeatureType())) {
                            for (Sample sample : FREDUtil.getSortedList(feature.getSamples())) {
                                if (samples == null || samples.contains(sample) && sampleUtil.isAllowedReadSample(user, sample)) {
                                    resultsList.add(sample);

                                    out.write("<tr class=\"lightColour\">\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t<td></td>\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t<td>\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t\t<a href=\"detail.jsp?ID=");
                                    out.print(sample.getSampleId());
                                    out.write("&backURL=");
                                    out.print(URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1"));
                                    out.write("&backText=Back%20To%20Result%20List\">\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t<img src=\"images/drill.gif\" border=\"0\" height=\"20\" width=\"20\" alt=\"View Sample\" title=\"View Sample\"/>\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t\t</a>\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t</td>\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"heading\">&nbsp;&nbsp;&nbsp;");
                                    out.print(SampleUtil.getDrillHoleDepthDescription(sample));
                                    out.write("&nbsp;&nbsp;</td>\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t<td>Sample&nbsp;&nbsp;</td>\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t<td>&nbsp;</td>\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t\t<td>&nbsp;</td>\n");
                                    out.write("\t\t\t\t\t\t\t\t\t\t\t</tr>");

                                }
                            }
                        }
                    }
                }

                if (maxRangePage > 1) {

                    out.write("<tr class=\"midColour\">\n");
                    out.write("\t\t\t\t\t\t\t<td class=\"heading\" colspan=\"4\">\n");
                    out.write("\t\t\t\t\t\t\t\tDisplaying records ");
                    out.print(startIndex);
                    out.write(" to ");
                    out.print(endIndex);
                    out.write(" of ");
                    out.print(numRecords);
                    out.write("\n");
                    out.write("\t\t\t\t\t\t\t</td>\n");
                    out.write("\t\t\t\t\t\t\t<td style=\"text-align: right\" colspan=\"2\">");

                    if (pageNum > startPage) {

                        out.write("<a href=\"result_list.jsp?Page=");
                        out.print(startPage);
                        out.write('"');
                        out.print(((startPage == pageNum) ? " class=\"heading\"" : ""));
                        out.write(">First ..</a>");

                    }
                    for (int i = minRangePage; i <= maxRangePage; i++) {

                        out.write("&nbsp;<a href=\"result_list.jsp?Page=");
                        out.print(i);
                        out.write('"');
                        out.print(((i == pageNum) ? " class=\"heading\"" : ""));
                        out.write('>');
                        out.print(i);
                        out.write("</a>");

                    }
                    if (pageNum < endPage) {

                        out.write("<a href=\"result_list.jsp?Page=");
                        out.print(endPage);
                        out.write('"');
                        out.print(((endPage == pageNum) ? " class=\"heading\"" : ""));
                        out.write(">.. Last</a>");

                    }

                    out.write("</td></tr>");

                }
                out.write("\n");
                out.write("\t\t\t\t</table>\n");
                out.write("\t\t\t</form>");

                session.setAttribute("FRED.results", resultsList);
            } else {
                out.write("<p>No records found matching your search criteria</p>");
            }

            h.drawBottom(out, et);
        } catch (Exception e) {
            log.log(Level.WARNING, null, e);
            out.write("<p>An error occurred:</p><p> ");
            out.write(new Date().toString());
            out.write("</p><p>");
            out.write("Please try refining and submitting the search again"); //e.getMessage());
            out.write("</p>");
        } finally {
            out.flush();
        }
    }

    public Integer paramAsInt(HttpServletRequest req, String paramName) throws ServletException {
        String s = req.getParameter(paramName);
        if (null == s || s.trim().isEmpty() || "-".equals(s.trim())) {
            return null;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ServletException("Malformed parameter " + paramName + ". Value: " + s);
        }
    }

    public String getName(HttpServletRequest request) {
        return "FRED :: Search Results";
    }

    private List<Sample> getSpatiallyFilteredSamples(SampleUtil sampleUtil, String[] locIdList, String querySQL) throws StorageAccessException {
        // Maximum list elements handled by Oracle for an "in" statement
        int MAX_NUM_FEATURES = 1000;
        String subQuery;
        int offset = 0;
        int i = 0;
        List<Sample> subSamples = null;
        List<Sample> samples = new ArrayList<Sample>();

        if (locIdList.length > 0) {
            while (offset * MAX_NUM_FEATURES < locIdList.length) {
                subQuery = " and s.feature.featureId IN (";
                for (i = 0; i < MAX_NUM_FEATURES && offset * MAX_NUM_FEATURES + i < locIdList.length; i++) {
                    subQuery += locIdList[offset * MAX_NUM_FEATURES + i];
                    subQuery += ",";
                }

                subQuery = subQuery.substring(0, subQuery.length() - 1);
                subQuery += ") ";
                offset++;
                subSamples = sampleUtil.getLightweightSamples(querySQL + subQuery);
                samples.addAll(subSamples);
            }
        } else {
            samples = sampleUtil.getLightweightSamples(querySQL);
        }
        return samples;
    }

}
