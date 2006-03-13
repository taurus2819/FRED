<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.db.site.SiteRecord"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.gis.ims.IMSMap"
%><%@page import="nz.cri.gns.util.map.Datum"
%><%@page import="nz.cri.gns.util.map.Datum.Coordinate"
%><%@page import="nz.cri.gns.util.map.Datum.LatLong"
%><%@page import="nz.cri.gns.util.map.DatumFactory"
%><%@page import="java.net.URL"
%><%@page import="java.net.URLEncoder"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="com.esri.aims.mtier.model.map.layer.renderer.symbol.SimpleMarkerSymbol"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) { 
		return new Authenticable[0]; 
	}
%><%!
	public String getName(HttpServletRequest request) {
		try {
			String featID = request.getParameter("FeatID");
			DAOFactory factory = HibernateUtil.get().getDAOFactory();
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
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	FeatureUtil featureUtil = new FeatureUtil(factory);
	String featId = request.getParameter("FeatID");
	String dist = request.getParameter("Dist");
	int distance = 2500;
	try {
		distance = Integer.parseInt(dist);
	} catch (Exception e) {}

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
	il[x++] = new IconnedLink("http://maps.gns.cri.nz/website/fred", "images/map.gif", "Interactive Map");
	il[x++] = new IconnedLink("locality_map.jsp?FeatID=" + featId + "&Dist=12500" + backStr, "images/map.gif", "Small Scale");
	il[x++] = new IconnedLink("locality_map.jsp?FeatID=" + featId + "&Dist=2500" + backStr, "images/map.gif", "Large Scale");
	et.setButtons(il);

	drawTop(out, et, request, response);
	drawEndNavigation(out);
	%><p></p><p><%
	
	if (featId != null) {
		Feature feature = featureUtil.getFeature(Integer.parseInt(featId));
		if (featureUtil.isAllowedReadFeatureSite(user, feature)) {
			if (feature.getSiteId() != null) {
				SiteRecord sr = FREDUtil.getSite(feature);
				LatLong ll = sr.getLatLong();
				Datum nzms260Datum = DatumFactory.createDatum("NZMS260");
				Coordinate nzms260Coord = null;
				try {
					nzms260Coord = nzms260Datum.convertFromNZGD49(ll);
				} catch (Exception e) {}

				if (nzms260Coord != null) {
					try {
						//map
						long width = 620;
						long height = 500;
						URL imsServer = new URL("http://maps.gns.cri.nz");
						String service = "fred_nz";
						String whereClause = "FR.SITE_VIEW.FEATURE_ID = " + featId;
						int layerID = 6;
						SimpleMarkerSymbol sym = new SimpleMarkerSymbol();
						sym.setWidth(20);
						sym.setMarkerType("circle");
						sym.setColor("255,255,0");
						sym.setOutline("0,0,0");
						sym.setTransparency(0.5);
						IMSMap map = new IMSMap(imsServer, service, width, height);
						map.setSelectedFeatures(layerID, whereClause, true, sym);
						map.zoomByDistance(distance * -1);
						map.setLayerVisible(3, false);
						map.setLayerVisible(1, true);
						map.setLayerVisible(2, true);
						map.setLayerVisible(6, false);
						%><img src="<%=map.getURL()%>" width="<%=width%>" height="<%=height%>" alt="FRED locality map" border="1" /><%
						
						//details
						%><p><%
						startDETable(pageContext);
						%><table border="0" width="600">
						<tr><td colspan="2" class="deHeading">Locality Details</td></tr>
						<tr><td class="heading">Original Grid Reference</td><%
						if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
							Datum datum = FREDUtil.getFREDDatum(feature);
							Coordinate coord = FREDUtil.getFREDCoordinate(feature);
							%><td><%=datum.getHumanStringFor(coord).replaceAll("Geographic ", "")%></td><%
							if (!datum.getName().equals("NZMG")) {
								try {
									Datum nzmgDatum = DatumFactory.createDatum("NZMG");
									Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
									if (nzmgDatum.coordinateAcceptable(nzmgCoord)) {
										%></tr><tr><td class="heading">Converted Grid Reference</td><td><%=nzmgDatum.getHumanStringFor(nzmgCoord)%></td><%
									}
								} catch (Exception e) { }
							}
						}
						%></tr>
						<tr><td class="heading">Converted Dec. Lat/Long</td><td><%=ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)"%></td></tr>
						<tr><td class="heading">Map Year</td><td><%=DBUtils.nvl(feature.getMapYear())%></td></tr>
						<tr><td class="heading">Method</td><td><%=((sr != null && !sr.isNull(SiteRecord.H_METHOD_FIELD)) ? FREDUtil.getSiteMethod(sr) : "&nbsp;")%></td></tr>
						<tr><td class="heading">Accuracy</td><td><%=((sr != null && !sr.isNull(SiteRecord.H_ACCURACY_FIELD)) ? "&#177;" + String.valueOf(sr.getAccuracy()) + " m" : "&nbsp;")%></td></tr><%
						if (featureUtil.isAllowedReadFeature(user, feature)) {
							%><tr><td class="heading">Locality</td><td><%=DBUtils.nvl(feature.getLocality())%></td></tr>
							<tr><td class="heading">Country</td><td><%=((sr != null && !sr.isNull(SiteRecord.COUNTRY_FIELD)) ? FREDUtil.getSiteCountry(sr) : "&nbsp;")%></td></tr>
							<tr><td class="heading">Coordinate Comments</td><td><%=DBUtils.nvl(feature.getCoordComments())%></td></tr><%
						}
						%></table><%
						endDETable(pageContext);
						%></p><%
						
						
					} catch (Exception e) {
						e.printStackTrace();
						%>An error occured while generating this map. Please try again later.<%
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
	
	try {
		HibernateUtil.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>