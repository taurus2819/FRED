<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="nz.cri.gns.fred.wms.WMSClient"%>
<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.util.map.Datum"
%><%@page import="nz.cri.gns.util.map.Datum.Coordinate"
%><%@page import="nz.cri.gns.util.map.Datum.LatLong"
%><%@page import="nz.cri.gns.util.map.DatumFactory"
%><%@page import="java.net.URL"
%><%@page import="java.net.URLEncoder"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%@page import="nz.cri.gns.auth.security.IpGrantedAuthority"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.fred.model.SiteView"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.SiteModelUtil"%>
<%@page import="nz.cri.gns.fred.site.util.SiteModel"%><%!	
        private static final Logger log = Logger.getLogger("locality_map.jsp");
        
        @Override
        public IpGrantedAuthority getRequiredRights() {
            return null;
        }
%><%!
	public String getName(HttpServletRequest request) {
		try {
			String featID = request.getParameter("FeatID");
			DAOFactory factory = FredHibernate.get().getDAOFactory();
			if (featID != null) {
				Feature feature = new FeatureUtil(factory).getFeature(Integer.parseInt(featID));
				return "FRED :: Locality Map for " + FeatureUtil.getFeatureIdentifyingName(feature);
			}
			return "FRED :: The Fossil Record Electronic Database";
		} catch (Exception e) {
			return "FRED :: The Fossil Record Electronic Database";
		}
	}
%><%
	User user = (User) getUser(session);
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	FeatureUtil featureUtil = new FeatureUtil(factory);
	String featId = request.getParameter("FeatID");
	int distance = 2500;
	try {
		distance = Integer.parseInt(request.getParameter("Dist"));
	} catch (Exception e) {
        }
	String lyr = request.getParameter("Lyr");
        if (lyr==null) {
            lyr = "topo50";
        }

	String backURL = request.getParameter("backURL");
	if (backURL != null && backURL.length() == 0)
		backURL = null;
	String backText = request.getParameter("backText");
	if (backText != null && backText.length() == 0)
		backText = null;
	String backStr = (backURL != null) ? "&backURL=" + URLEncoder.encode(backURL, "ISO-8859-1") : "";
	backStr += (backText != null) ? "&backText=" + URLEncoder.encode(backText, "ISO-8859-1") : "";
	
	ExtranetTemplate et = getExtranetTemplate();
	et.setUseNavigationColumn(true);
	et.setDisplayLoadingMessage(true);
	
	IconnedLink[] il = new IconnedLink[(backURL != null) ? 4 : 3];
	int x = 0;
	if (backURL != null)
		il[x++] = new IconnedLink(backURL, "images/back_arrow.gif", (backText != null) ? request.getParameter("backText") : "Back");
	il[x++] = new IconnedLink("locality_map.jsp?FeatID=" + featId + "&Dist=12500&Lyr=topo250"+ backStr, "images/map.gif", "Small Scale");
	il[x++] = new IconnedLink("locality_map.jsp?FeatID=" + featId + "&Dist=2500&Lyr=topo50" + backStr, "images/map.gif", "Large Scale");
	il[x++] = new IconnedLink("locality_map.jsp?FeatID=" + featId + "&Dist=500&Lyr=gns:GIS.NZ_ORTHOPHOTO" + backStr, "images/map.gif", "Orthophoto");
	addButtons(et, il);
	drawTop(out, et, request, response);
	
	%><p><%
	
	if (featId != null) {
		Feature feature = featureUtil.getFeature(Integer.parseInt(featId));
		if (featureUtil.isAllowedReadFeatureSite(user, feature)) {
			SiteModel sv = SiteModelUtil.getSite(feature);
			if (sv != null) {               
				LatLong ll = SiteModelUtil.getSiteLatLong(feature);
				Coordinate nzms260Coord = null;

//				try {
//                    Datum nzms260Datum = DatumFactory.createDatum("NZMS260");
//					nzms260Coord = nzms260Datum.convertFromNZGD49(ll);
//				} catch (Exception e) {
//                                    log.log(Level.WARNING, "locality_map.jsp: error creating Datum", e);
//                                }

                        if (nzms260Coord == null) {
			try {
                        Datum datum = SiteModelUtil.getFREDDatum(feature);
                        Coordinate coord = SiteModelUtil.getFREDCoordinate(feature);
                        Datum nzmgDatum = DatumFactory.createDatum("NZMG");
                        Datum.Coordinate nzmgCoord = null;
                        if (!datum.getName().equals("NZMG")) {
                            try {
                                nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
							} catch (Exception e) { 
                                log.log(Level.WARNING, "locality_map.jsp: error converting Datum", e);                        
                            }
                        } else {
                            nzmgCoord = coord;
                        }
                        // Produces map coordinates
                        Datum nztmDatum = DatumFactory.createDatum("NZTM");
                        Datum.Coordinate nztmCoord = null;
                        if (!datum.getName().equals("NZTM")) {
                            try {
                                nztmCoord = nztmDatum.convertFromDatum(datum, coord);
							} catch (Exception e) { 
                                log.log(Level.WARNING, "locality_map.jsp: error converting Datum", e);                        
                            }
                        } else {
                            nztmCoord = coord;
                        }
                        //map
						int width = 620;
						int height = 500;
                        String fredPath = getServletConfig().getServletContext().getRealPath("/fred");
                        log.info("Getting map URL, fred base="+fredPath);
                        String url = WMSClient.getMapURL(
                                nztmCoord.getEastWest(), nztmCoord.getNorthSouth(), distance, width, height, lyr, fredPath
                        );
                        log.info("Map url: "+url);
                        %><img src="<%=url%>" width="<%=width%>" height="<%=height%>" alt="FRED locality map" border="1" /><%
						if (lyr.toUpperCase().contains("ORTHO")) {
							%><p><table border="0" width="600"><tr><td>
							Please Note: Orthophoto coverage for New Zealand is not complete.  If the above image has no background please try a different map type.
							</td></tr></table></p><%
						}
						
						//details
						%><p>
						<table border="0" cellpadding="3" cellspacing="2" width="620">
						<tr class="midColour"><th colspan="2">Locality Details</th></tr>
						<tr class="lightColour"><td class="heading">Original Grid Reference</td><%
						if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
							%><td><%=datum.getHumanStringFor(coord).replaceAll("Geographic ", "")%></td><%
                                if (nzmgDatum.coordinateAcceptable(nzmgCoord)) {
									%></tr>
									<tr class="lightColour"><td class="heading">Converted Grid Reference</td><td><%=nzmgDatum.getHumanStringFor(nzmgCoord)%></td><%
								}
						}
						%></tr>
						<tr class="lightColour"><td class="heading">Converted Dec. Lat/Long</td><td><%=ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (WGS84)"%></td></tr>
						<tr class="lightColour"><td class="heading">Map Year</td><td><%=DBUtils.nvl(feature.getMapYear())%></td></tr>
						<tr class="lightColour"><td class="heading">Method</td><td><%=((sv != null && sv.getMethodId() != null) ? sv.getMethodId() : "&nbsp;")%></td></tr>
						<tr class="lightColour"><td class="heading">Accuracy</td><td><%=((sv != null && sv.getAccuracy() != null) ? "&#177;" + String.valueOf(sv.getAccuracy()) + " m" : "&nbsp;")%></td></tr><%
						if (featureUtil.isAllowedReadFeature(user, feature)) {
							%><tr class="lightColour"><td class="heading">Locality</td><td><%=DBUtils.nvl(feature.getLocality())%></td></tr>
                                                        <tr class="lightColour"><td class="heading">Country</td><td><%=((sv != null && sv.getCountryCode() != null) ? featureUtil.getCountry(sv.getCountryCode()) : "&nbsp;")%></td></tr>
							<tr class="lightColour"><td class="heading">Coordinate Comments</td><td><%=DBUtils.nvl(feature.getCoordComments())%></td></tr><%
						}
						%></table>
						</p><%
						
                                            log.info("Map rendered");	
					} catch (Exception e) {
						e.printStackTrace();
						%>An error occurred while generating this map. Please try again later.<%
					}
				} else {
					//not in NZ
					%>The locality selected is not located in mainland New Zealand and can't be plotted.<%
				}
			} else {
				//not in NZ
				%>The locality selected does not have a coordinate defined.<%
			}
		} else {
			//didn't pass isAllowedReadFeatureSite()
			%>You do not have rights to view this locality.<%
		}
	} else {
		 //no featureID
		%>No Feature entered.<%
	}
	
	%></p><%
	drawBottom(out, et);
%>