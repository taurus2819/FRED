<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Person"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.SentTo"
%><%@page import="nz.cri.gns.fred.model.Stage"
%><%@page import="nz.cri.gns.fred.model.SiteView"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.SiteUtil"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="java.util.List"
%><%@page import="nz.cri.gns.util.map.Datum"
%><%@page import="nz.cri.gns.util.map.Datum.Coordinate"
%><%@page import="nz.cri.gns.util.map.Datum.LatLong"
%><%@page import="nz.cri.gns.util.map.DatumFactory"
%><%@page import="nz.cri.gns.auth.User"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	List<Feature> features = (List<Feature>) session.getAttribute("FRED.features");
	if (features != null && features.size() > 0) {
		User user = (User) getUser(session);
		FeatureUtil featureUtil = new FeatureUtil(HibernateUtil.get().getDAOFactory());
		StageUtil stageUtil = new StageUtil(HibernateUtil.get().getDAOFactory());
		
		boolean localityFlag = (request.getParameter("locality") != null);
		boolean collectionFlag = (request.getParameter("collection") != null);
		boolean stratigraphyFlag = (request.getParameter("stratigraphy") != null);
		boolean sedimentaryFlag = (request.getParameter("sedimentary") != null);
		
		response.setHeader("Content-Disposition", "filename=\"PETLAB_download.txt\"");
		response.setContentType("application/x-octet-stream");

		//file header
		out.println("**************************************************************************************************************");
		out.println("Data downloaded from FRED (http://www.fred.org.nz), the computer database for the NZ Fossil Record File (FRF).");
		out.println("FRF is a nationally significant database administrated by GSNZ and GNS Science                                ");
		out.println("Please acknowledge use of this data in publications, reports and presentations.                               ");
		out.println("**************************************************************************************************************");
		out.println("");
		
		if (localityFlag) {
			out.println("********");
			out.println("Locality");
			out.println("********");

			out.print("FR Number\tYard FR Number\tLocality Type\tField Number/Drillhole Name\tOriginal Grid Reference\tNZMG Easting\tNZMG Northing\tNZGD49 Latitude\tNZGD49 Longitude\tMap Year\tMethod\tAccuracy\tLocality\tCountry\tCoordinate Comments\tLocality Comments\tDepth From\tDepth To\tDepth Unit\tDrill Type\t");
			
			if (collectionFlag)
				out.print("Collectors\tCollection Date\tFossils in Place\tSent To\tNot Collected\tSignificance/Comments\t");
			if (stratigraphyFlag)
				out.print("Stratigraphic Name\tInferred Stage Lower\tInferred Lower Modifier\tInferred Stage Upper\tInferred Upper Modifier\tInferred Age Start\tInferred Age Stop\tKnown Stage Lower\tKnown Lower Modifier\tKnown Stage Upper\tKnown Upper Modifier\tKnown Age Start\tKnown Age Stop\t");
			
			out.print("\n");
		}
		
		//Data
		if (localityFlag) {
			for (Feature feature : features) {
				if (featureUtil.isAllowedReadFeature(user, feature)) {
					HibernateUtil.get().currentSession().refresh(feature);
					for (Sample sample : FeatureUtil.getSortedSamples(feature)) {
						if (sample.getFrNumber() != null)
							out.print(sample.getFrNumber().getFrNumber() + "\t");
						else
							out.print(feature.getFrNumber().getFrNumber() + "\t");
						if (sample.getYardFrNumber() != null)
							out.print(sample.getYardFrNumber().getFrNumber() + "\t");	
						else
							out.print(((feature.getYardFrNumber() != null) ? feature.getYardFrNumber().getFrNumber() : "") + "\t");
						out.print(feature.getFeatureType() + "\t");
						out.print(DBUtils.nvl(feature.getFeatureName()) + "\t");
						
						SiteView sv = feature.getSiteView();
						if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
							Datum datum = SiteUtil.getFREDDatum(feature);
							Coordinate coord = SiteUtil.getFREDCoordinate(feature);
							out.print(datum.getHumanStringFor(coord).replaceAll("Geographic ", "") + "\t");
							try {
								Datum nzmgDatum = DatumFactory.createDatum("NZMG");
								Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
								out.print(nzmgCoord.getEastWest() + "\t" + nzmgCoord.getNorthSouth() + "\t");
							} catch (Exception e) {
								out.print("\t\t");
							}
							if (sv != null) {
								LatLong ll = SiteUtil.getSiteLatLong(sv);
								out.print(ll.getLatAsDecDegree(5) + "\t" + ll.getLongAsDecDegree(5) + "\t");
							} else {
								out.print("\t\t");
							}
						} else {
							out.print("\t\t\t\t");
						}
						out.print(DBUtils.nvl(feature.getMapYear()) + "\t");
						out.print(((sv != null) ? DBUtils.nvl(sv.getMethod()) : "") + "\t");
						out.print(((sv != null) ? DBUtils.nvl(sv.getAccuracy()) : "") + "\t");
						out.print(DBUtils.nvl(feature.getLocality()) + "\t");
						out.print(((sv != null) ? sv.getCountryName() : "") + "\t");
						out.print(DBUtils.nvl(feature.getCoordComments()) + "\t");
						out.print(DBUtils.nvl(feature.getComments()) + "\t");
						
						out.print(DBUtils.nvl(sample.getTopDepth()) + "\t");
						out.print(DBUtils.nvl(sample.getBottomDepth()) + "\t");
						out.print(DBUtils.nvl(sample.getDepthUnit()) + "\t");
						out.print(((sample.getDrillType() != null) ? sample.getDrillType().getName() : "") + "\t");
						
						if (collectionFlag) {
							if (!FREDUtil.isEmpty(sample.getCollectors())) {
								for (Person collector : sample.getCollectors())
									out.print(collector.getName() + ";");
							}
							out.print("\t");
							out.print(DBUtils.nvl(FREDUtil.formatDateForOutput(sample.getCollectionDate(), sample.getDateRounding())) + "\t");
							out.print(DBUtils.nvl(sample.getInPlace()) + "\t");
							if (!FREDUtil.isEmpty(sample.getSentTos())) {
								for (SentTo sentTo : sample.getSentTos())
									out.print(SampleUtil.getSentToDescription(sentTo) + ";");
							}
							out.print("\t");
							out.print(DBUtils.nvl(sample.getNotCollected()) + "\t");
							out.print(DBUtils.nvl(sample.getSignificance()) + "\t");
						}
						
						if (stratigraphyFlag) {
							out.print(DBUtils.nvl(sample.getStratUnit()) + "\t");
							if (sample.getInferredStage() != null) {
								Stage stage = sample.getInferredStage();
								out.print(((stage.getLowerAgeView() != null) ? stage.getLowerAgeView().getAgeName() : "") + "\t");
								out.print(DBUtils.nvl(stage.getStageLowerMod()) + "\t");
								out.print(((stage.getUpperAgeView() != null) ? stage.getUpperAgeView().getAgeName() : "") + "\t");
								out.print(DBUtils.nvl(stage.getStageUpperMod()) + "\t");
								out.print(stageUtil.getAgeStart(stage) + "\t");
								out.print(stageUtil.getAgeStop(stage) + "\t");
							} else
								out.print("\t\t\t\t\t\t");
							if (sample.getKnownStage() != null) {
								Stage stage = sample.getKnownStage();
								out.print(((stage.getLowerAgeView() != null) ? stage.getLowerAgeView().getAgeName() : "") + "\t");
								out.print(DBUtils.nvl(stage.getStageLowerMod()) + "\t");
								out.print(((stage.getUpperAgeView() != null) ? stage.getUpperAgeView().getAgeName() : "") + "\t");
								out.print(DBUtils.nvl(stage.getStageUpperMod()) + "\t");
								out.print(stageUtil.getAgeStart(stage) + "\t");
								out.print(stageUtil.getAgeStop(stage) + "\t");
							} else
								out.print("\t\t\t\t\t\t");
						}
						
						out.print("\n");
					}
				}
			}
		}
		
	} else {
		ExtranetTemplate et = getExtranetTemplate();
		et.setDisplayLoadingMessage(true);
		%><p>No data</p><%
		drawBottom(out, et);
	}
%>