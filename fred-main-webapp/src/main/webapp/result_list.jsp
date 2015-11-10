<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FREDConstants"
%><%@page import="nz.cri.gns.fred.query.FREDQuery"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.PageState"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.jsp.JspUtils"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%@page import="java.util.List"
%><%@page import="java.util.ArrayList"
%><%@page import="java.util.Vector"
%><%@page import="java.util.HashSet"
%><%@page import="java.util.Collections"
%><%@page import="java.net.URLEncoder"
%><%@page import="java.sql.Connection"
%><%@page import="java.sql.Statement"
%><%@page import="java.sql.ResultSet"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.jsp.Link"
%><%@page import="nz.cri.gns.jsp.CustomHTMLLink"
%><%@page import="java.util.Set"
%><%@page import="java.util.Arrays"
%><%@page import="java.util.Date"
%><%@page import="java.util.Calendar"
%><%@page import="java.util.GregorianCalendar"
%><%@page import="java.io.*"
%><%@page import="java.net.Socket"
%><%@page import="java.net.InetAddress"
%><%@page import="java.net.UnknownHostException"
%><%@page import="java.util.logging.Logger"
%><%@page import="java.util.Enumeration"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%>
<%!
	// Define Util classes
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	SampleUtil sampleUtil = new SampleUtil(factory);
	FeatureUtil featureUtil = new FeatureUtil(factory);
	AuditUtil auditUtil = new AuditUtil(factory);
%>
<%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Search Results";
	}
%>
<%!    private List<Sample> getSpatiallyFilteredSamples(String[] locIdList, String querySQL) throws Exception {

        //System.err.println("#samples altogether " + locIdList.length);
		// Maximum list elements handled by Oracle for an "in" statement
        int MAX_NUM_FEATURES = 1000;
        String subQuery;
        int offset = 0;
        int i = 0;
        List<Sample> subSamples = null;
        List<Sample> samples = new ArrayList<Sample>();

		//System.out.println(querySQL);
		Calendar cal = new GregorianCalendar();
        long t1 = cal.getTimeInMillis();
                
		if(locIdList.length > 0)	{
			
			while (offset * MAX_NUM_FEATURES < locIdList.length) {

				//System.err.println("Iteration "+ (offset+1));
				subQuery = " and s.feature.featureId IN (";	
				for (i = 0; i < MAX_NUM_FEATURES && offset * MAX_NUM_FEATURES + i < locIdList.length; i++) {

					subQuery += locIdList[offset * MAX_NUM_FEATURES + i];	
					subQuery += ",";
				}

				subQuery = subQuery.substring(0, subQuery.length() - 1);	
				subQuery += ") ";
				offset++;
				//System.err.println("#query "+ querySQL + subQuery);
				subSamples = sampleUtil.getLightweightSamples(querySQL+subQuery);
				//System.err.println("#samples "+ subSamples.size());
				samples.addAll(subSamples);
			}
		}
		else	{
			samples = sampleUtil.getLightweightSamples(querySQL);
		}
        cal = new GregorianCalendar();
        long t2 = cal.getTimeInMillis();
        System.out.println("Retrieved light samples in " + (t2-t1) + " ms.");
		return samples;
    }
%>

<%



	// Define page variables and initialise head
    ExtranetTemplate et = null;
	try {
	
	
	// Define HTTP state variables
	PageState state = new PageState(request, response, getServletContext());
	User user = (User)getUser(session);

	// Define other page variables
	String queryURL = request.getParameter("QueryURL");
	if (queryURL == null)
		queryURL = "simple_query.jsp";	
	int pageSize = 50;

	// Define the extranet template for this page
	et = getExtranetTemplate();	
	et.setDisplayLoadingMessage(true);
	
	// Define a vector of links
	Vector<Link> il = new Vector<Link>();	
	il.add(new IconnedLink(queryURL, "images/search.gif", "Search Again"));
	il.add(new IconnedLink("export_setup.jsp", "images/save.gif", "Download Results"));	
	
	
	// Add to Folder link
	if (user != null && new FolderUtil(factory).getPersonalFolders(user).size() > 0) {		
		StringBuffer customHTML = new StringBuffer("<form method=\"post\" onsubmit=\"addFeaturesToActionURL(this)\" action=\"result_list.jsp?Page=" + ((request.getParameter("Page") == null) ? "1" : request.getParameter("Page")) + "\" name=\"FolderForm\" style=\"display: inline; margin: 0;\">");
		customHTML.append("<input type=\"hidden\" name=\"ActionType\" value=\"AddtoFold\" />");
		customHTML.append("<img src=\"images/blank.gif\" height=\"20\" width=\"10\" alt=\"\" /><select name=\"FoldID\">");
		customHTML.append("<option value=\"-\">-- Choose --</option>");
		for (UserFolder folder : (new FolderUtil(factory)).getPersonalFolders(user)) {
			String folderName = folder.getFolderName();
			if (folderName.length() > 17)
				folderName = folderName.substring(0, 14) + "...";
			customHTML.append("<option value=\"").append(folder.getFolderId()).append("\">").append(folderName).append("</option>");
		}
		customHTML.append("</select><br />");
		customHTML.append("<img src=\"images/blank.gif\" height=\"20\" width=\"10\" alt=\"\" /><input type=\"submit\" value=\"Add to Folder\" />");
		customHTML.append("</form>");
		il.add(new CustomHTMLLink(customHTML.toString()));
	}
	
	
	// Adds all the links in the array to the extranet template
	addButtons(et, il.toArray(new Link[il.size()]));	
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
					featureUtil.addToFolder(feature, Integer.parseInt(request.getParameter("FoldID")), user);					
					alertText += "Locality, " + feature + " Added to Folder.\\n";
				} else {
					alertText += "Locality, " + feature + " Not Added to Folder. User does not have read rights for this record.\\n";
				}
			} catch (NumberFormatException nfe){
				nfe.printStackTrace();
			}
		}%>	
		<script type="text/javascript"><!--
			alert(<%= "\"" + alertText + " \"" %>);
		//-->
		</script><%
	}
	
	// Add scripts to extranet template 
	et.addScript("scripts/resultList.js");	
	et.setBodyTag("onload=\"updateMasterCheckbox()\"");
	
	
	
	// Start drawing page from the extranet template defined	
	drawTop(out, et, request, response);

	if ((request.getParameter("WhereSQL") != null && request.getParameter("TableName") != null && request.getParameter("QueryString") != null) || request.getParameter("Page") != null || request.getParameter("Type") != null) {

		String queryString = "";
		
		int pageNum = 1;
		if (request.getParameter("Page") != null)
			pageNum = Integer.parseInt(request.getParameter("Page"));
		boolean useStored = (request.getParameter("Page") != null);

		session.setAttribute("dataEntryRedirect", "result_list.jsp?Page=" + pageNum);

		List<Sample> samples = null;
		List<Feature> features = null;
                List<Object> resultsList = new Vector<Object>();
		if (useStored) {
			samples = (List<Sample>) session.getAttribute("FRED.samples");
			features = (List<Feature>) session.getAttribute("FRED.features");
			queryString = (String) session.getAttribute("FRED.queryString");
		} else 	if ("Adv".equals(request.getParameter("Type"))) {
			try {
				FREDQuery query = FREDUtil.getFREDQuery(state);
				queryString = query.getQueryAsString();                               
                                String hq = query.getHQLQuery();
                                System.out.println ("ADV hq " + hq);
                                samples = sampleUtil.getLightweightSamples(hq);;
                                features = featureUtil.getFeaturesBySampleSubquery(hq);
				auditUtil.addLogEntry(AuditUtil.QUERY_LOG_TYPE, user, features.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String whereSQL = request.getParameter("WhereSQL");
			String tableName = request.getParameter("TableName");
			queryString = request.getParameter("QueryString");
			//Account for large number of SAMPLE_IDs provided by polygon filter
			String idString = request.getParameter("idList");

            try {
				String sampHql = "SELECT DISTINCT s.sampleId FROM " + tableName + " WHERE " + whereSQL;
                String featHql = "SELECT DISTINCT s.feature.featureId FROM " + tableName + " WHERE " + whereSQL;
                                			
				//if polygon vertices are set, apply spatial filter
				if (idString != null && idString.length() > 0) { 
                    samples = getSpatiallyFilteredSamples(idString.split(","), sampHql);
				} else {
                    samples = sampleUtil.getLightweightSamples(sampHql);
                }
                Calendar cal = new GregorianCalendar();
                long t1 = cal.getTimeInMillis();
                features = featureUtil.getFeaturesBySampleSubquery(samples);
                cal = new GregorianCalendar();
                long t2 = cal.getTimeInMillis();
                System.out.println("Retrieved light features in " + (t2-t1) + " ms.");
				auditUtil.addLogEntry(AuditUtil.QUERY_LOG_TYPE, user, features.size());
                        
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int numRecords = features.size();
		if (numRecords > 0) {
			
			//save QueryRes vector
			session.setAttribute("FRED.samples", samples);
			session.setAttribute("FRED.features", features);
			session.setAttribute("FRED.queryString", queryString);

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
			%>
			<form method="post" id="resultsForm" action="result_list.jsp">
				<table border="0" cellpadding="3" cellspacing="2" width="600">
					<tr class="midColour">
						<th colspan="6">Matching Localities</th>
					</tr>
					<tr class="midColour">
						<td colspan="6">
							Search Criteria: <em><%=queryString%></em>
						</td>
					</tr><%
						if (maxRangePage > 1) {%>
							<tr class="midColour">
								<td class="heading" colspan="4">
									Displaying records <%=startIndex%> to <%=endIndex%> of <%=numRecords%>
								</td>
								<td style="text-align: right" colspan="2"><%
									if (pageNum > startPage) {
                                                                            %><a href="result_list.jsp?Page=<%=startPage%>"<%=((startPage == pageNum) ? " class=\"heading\"" : "")%>>First ..</a><%
                                                                        }
                                                                        for (int i = minRangePage; i <= maxRangePage; i++) {
                                                                                %>&nbsp;<a href="result_list.jsp?Page=<%=i%>"<%=((i == pageNum) ? " class=\"heading\"" : "")%>><%=i%></a><%
                                                                        }                                                
                                                                        if (pageNum < endPage) {
                                                                            %><a href="result_list.jsp?Page=<%=endPage%>"<%=((endPage== pageNum) ? " class=\"heading\"" : "")%>>.. Last</a><%
                                                                        }	
                                                                %></td>
							</tr><%
						}%>
                                               
					
					<tr class="midColour">
						<th>
							<input type="checkbox" name="MasterCheckbox" onchange="updateAllCheckBoxes(this.checked);" />
						</th>
						<th colspan="2">FR Number&nbsp;&nbsp;</th>
						<th>Type&nbsp;&nbsp;</th>
						<th>Name&nbsp;&nbsp;</th>
						<th>Actions</th>
					</tr><%
					
					// Obtains feature ids from GET
					Set<String> fids;
					if (request.getParameterValues("fid") == null)
						fids = new HashSet<String>();
					else					
						fids = new HashSet<String>(Arrays.asList(request.getParameterValues("fid")));
					

                    List<Feature> pageFeatures = features.subList(startIndex-1, endIndex);

					for (Feature feature : pageFeatures) {
        					feature = featureUtil.getFeature(feature.getFeatureId());
						if (featureUtil.isAllowedReadFeatureSite(user, feature)) {
                                                    resultsList.add(feature);

                                                        String checkedText = ""; // default un-checked
							if (!fids.isEmpty() && fids.contains(feature.getFeatureId().toString()))
                                                            checkedText = "checked=\"checked\"";
							%><tr class="lightColour">
                                                            <td>
                                                                    <input type="checkbox" name="FeatIDs" <%= checkedText %> onchange="updateMasterCheckbox()" value="<%=feature.getFeatureId()%>" />
                                                            </td>
                                                            <td>
                                                                <a href="detail.jsp?resultsIndex=<%=(resultsList.size()-1)%>&backURL=<%=URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1")%>&backText=Back+To+Result+List"><img src="images/loc.gif" border="0" height="20" width="20" alt="View Locality" title="View Locality"/></a>
                                                            </td>
                                                            <td class="heading"><%=feature.getFrNumber()%> <%=(feature.getYardFrNumber() != null) ? "(" + feature.getYardFrNumber() + ")" : ""%>&nbsp;&nbsp;</td>
                                                            <td><%=feature.getFeatureType()%>&nbsp;&nbsp;</td>
                                                            <td><%=DBUtils.nvl(feature.getFeatureName())%>&nbsp;&nbsp;</td>
                                                            <td>
                                                                <a href="locality_map.jsp?FeatID=<%=feature.getFeatureId()%>&backURL=<%=URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1")%>&backText=Back%20To%20Result%20List">
                                                                    <img src="images/map.gif" height="20" width="20" border="0" alt="View Locality Map" />
								</a>&nbsp;&nbsp;
                                                                <%
                                                        if (user != null && featureUtil.isAllowedEditApprovedFeature(user, feature)) 
                                                        {
                                                            %><a href="de.jsp?Type=<%=feature.getFeatureType()%>&FeatID=<%=feature.getFeatureId()%>&FoldID=<%=feature.getMasterFile().getFolderId()%>">
                                                                <img src="images/edit.gif" height="20" width="20" border="0" alt="Edit" title="Edit"/>
                                                            </a><%
							}%>
                                                            </td>						
							</tr><%
							if (!FeatureUtil.OUTCROP.equals(feature.getFeatureType())) {
								for (Sample sample : FREDUtil.getSortedList(feature.getSamples())) {
									if (samples == null || samples.contains(sample) && sampleUtil.isAllowedReadSample(user, sample)) 
                                                                        {
                                                                            resultsList.add(sample);
                                                                                %><tr class="lightColour">
												<td></td>
												<td>
													<a href="detail.jsp?resultsIndex=<%=(resultsList.size()-1)%>&backURL=<%=URLEncoder.encode("result_list.jsp?Page=" + pageNum, "ISO-8859-1")%>&backText=Back%20To%20Result%20List">
														<img src="images/drill.gif" border="0" height="20" width="20" alt="View Sample" title="View Sample"/>
													</a>
												</td>
												<td class="heading">&nbsp;&nbsp;&nbsp;<%=SampleUtil.getDrillHoleDepthDescription(sample)%>&nbsp;&nbsp;</td>
												<td>Sample&nbsp;&nbsp;</td>
												<td>&nbsp;</td>
												<td>&nbsp;</td>
											</tr><%
                                                                        }
									}
								}
						}
					}
										
		
					if (maxRangePage > 1) {                                           
						%><tr class="midColour">
							<td class="heading" colspan="4">
								Displaying records <%=startIndex%> to <%=endIndex%> of <%=numRecords%>
							</td>
							<td style="text-align: right" colspan="2"><%                                                        
                                                            if (pageNum > startPage) {
                                                                %><a href="result_list.jsp?Page=<%=startPage%>"<%=((startPage == pageNum) ? " class=\"heading\"" : "")%>>First ..</a><%
                                                            }
                                                            for (int i = minRangePage; i <= maxRangePage; i++) {
                                                                    %>&nbsp;<a href="result_list.jsp?Page=<%=i%>"<%=((i == pageNum) ? " class=\"heading\"" : "")%>><%=i%></a><%
                                                            }                                                
                                                            if (pageNum < endPage) {
                                                                %><a href="result_list.jsp?Page=<%=endPage%>"<%=((endPage== pageNum) ? " class=\"heading\"" : "")%>>.. Last</a><%
                                                            }	
                                                        %></td></tr><%
					 }%>
				</table>
			</form><%				
			session.setAttribute("FRED.results", resultsList);
		} else {
			%><p>No records found matching your search criteria</p><%
		}
	}
		
	drawBottom(out, et);
   
   } catch (Exception ex) {
            %><tr></tr><td>An error has occurred</td>
            <p>IT support have been notified and we'll be fixing it as soon as possible.</p><%
           
        String server = "smtp1.gns.cri.nz";
        String from = "developer@gns.cri.nz";
        String to = "developer@gns.cri.nz";
        Logger log = Logger.getLogger("GNSOnline.error.general.jsp");

        try {
            String hostname = "???";
            try {
                InetAddress addr = InetAddress.getLocalHost();
                byte[] ipAddr = addr.getAddress();
                hostname = addr.getHostName();
            } catch (UnknownHostException e) {
            }

            log = Logger.getLogger("GNSOnline.error.general.jsp");
            Socket sockPuppet = new Socket(server, 25);
            BufferedReader ismtp = new BufferedReader(
                    new InputStreamReader(sockPuppet.getInputStream()));
            PrintWriter osmtp = new PrintWriter(
                    sockPuppet.getOutputStream(), true);

            osmtp.println("HELO gns.cri.nz");
            log.info(ismtp.readLine());
            osmtp.println("MAIL FROM: " + from);
            log.info(ismtp.readLine());
            osmtp.println("RCPT TO: " + to);
            log.info(ismtp.readLine());
            osmtp.println("DATA");
            log.info(ismtp.readLine());

            osmtp.println("From: " + from);
            osmtp.println("To: " + to);
            osmtp.print("Subject: http://");
            osmtp.print(hostname);
            osmtp.print(request.getAttribute("javax.servlet.forward.context_path"));
            osmtp.print(request.getAttribute("javax.servlet.forward.servlet_path"));
            osmtp.println(" fail.");
            osmtp.print("URL (in theory): http://");
            osmtp.print(hostname);
            osmtp.print(request.getAttribute("javax.servlet.forward.context_path"));
            osmtp.println(request.getAttribute("javax.servlet.forward.servlet_path"));
            osmtp.print("Logged in user was: ");
            osmtp.println(JspUtils.getLoggedInUserString(session));
            osmtp.print("Remote addr: ");
            osmtp.println(request.getRemoteAddr());
            osmtp.print("Remote host name: ");
            osmtp.println(request.getRemoteHost());
            osmtp.println();
            osmtp.println("Application caught an exception, printed below: ");

            ex.printStackTrace(osmtp);

            osmtp.print("\nContext path: ");
            osmtp.println(request.getContextPath());
            osmtp.print("\nPath info: ");
            osmtp.println(request.getPathInfo());
            osmtp.print("\nQuery string: ");
            osmtp.println(request.getQueryString());
            osmtp.print("\nRequest URI: ");
            osmtp.println(request.getRequestURI());
            osmtp.print("\nRequest URL: ");
            osmtp.println(request.getRequestURL().toString());
            osmtp.print("\nServlet path: ");
            osmtp.println(request.getServletPath());

            osmtp.println("\nHeader is:");
            Enumeration<String> javaSucks = request.getHeaderNames();
            while (javaSucks.hasMoreElements()) {
                String headerName = javaSucks.nextElement();
                osmtp.print(headerName);
                osmtp.print(": <<<");
                osmtp.print(request.getHeader(headerName));
                osmtp.println(">>>");
            }

            osmtp.println("\nParameters are:");
            javaSucks = request.getParameterNames();
            while (javaSucks.hasMoreElements()) {
                String headerName = javaSucks.nextElement();
                osmtp.print(headerName);
                osmtp.print(": <<<");
                osmtp.print(request.getParameter(headerName));
                osmtp.println(">>>");
            }
        
            osmtp.println("\nAttributes are:");
            javaSucks = request.getAttributeNames();
            while (javaSucks.hasMoreElements()) {
                String headerName = javaSucks.nextElement();
                osmtp.print(headerName);
                osmtp.print(": <<<");
                osmtp.print(request.getAttribute(headerName));
                osmtp.println(">>>");
            }

            osmtp.println(".");
            osmtp.println("QUIT");
            log.info(ismtp.readLine());
            osmtp.close();
  
            drawBottom(out, et);
            
       } catch (Exception e) {
           log.warning(ex.toString());
       }
    
	try {
		factory.closeSession();
	} catch (Exception e) {
	}
	
   }
%>