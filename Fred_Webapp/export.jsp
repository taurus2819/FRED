<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.Person"
%><%@page import="nz.cri.gns.fred.model.Sample"
%><%@page import="nz.cri.gns.fred.model.Adoption"
%><%@page import="nz.cri.gns.fred.model.Paleontology"
%><%@page import="nz.cri.gns.fred.model.PaleontologyListEntry"
%><%@page import="nz.cri.gns.fred.model.TaxonomicNameAndGroup"
%><%@page import="nz.cri.gns.fred.model.Relationship"
%><%@page import="nz.cri.gns.fred.model.SedimentaryFeature"
%><%@page import="nz.cri.gns.fred.model.SentTo"
%><%@page import="nz.cri.gns.fred.model.Stage"
%><%@page import="nz.cri.gns.fred.model.SiteView"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.RecordUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.AuditUtil"
%><%@page import="nz.cri.gns.fred.util.SiteUtil"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="java.util.List"
%><%@page import="java.util.Vector"
%><%@page import="java.util.TreeSet"
%><%@page import="java.util.Date"
%><%@page import="javax.servlet.jsp.JspWriter"
%><%@page import="java.io.IOException"
%><%@page import="java.io.PrintWriter"
%><%@page import="nz.cri.gns.util.map.Datum"
%><%@page import="nz.cri.gns.util.map.Datum.Coordinate"
%><%@page import="nz.cri.gns.util.map.Datum.LatLong"
%><%@page import="nz.cri.gns.util.map.DatumFactory"
%><%@page import="nz.cri.gns.auth.User"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }

	public void writeLocalityHeader(JspWriter out) throws IOException {
		out.print("FR Number\tYard FR Number\tLocality Type\tField Number/Drillhole Name\tDepth From\tDepth To\tDepth Unit\tDrill Type\t");
	}

	public void writeLocality(Sample sample, JspWriter out) throws IOException {
		if (sample.getFrNumber() != null)
			out.print(sample.getFrNumber().getFrNumber() + "\t");
		else
			out.print(((sample.getFeature().getFrNumber() != null) ? sample.getFeature().getFrNumber().getFrNumber() : "") + "\t");
		if (sample.getYardFrNumber() != null)
			out.print(sample.getYardFrNumber().getFrNumber() + "\t");	
		else
			out.print(((sample.getFeature().getYardFrNumber() != null) ? sample.getFeature().getYardFrNumber().getFrNumber() : "") + "\t");
		out.print(sample.getFeature().getFeatureType() + "\t");
		out.print(DBUtils.nvl(sample.getFeature().getFeatureName()) + "\t");
		out.print(DBUtils.nvl(sample.getTopDepth()) + "\t");
		out.print(DBUtils.nvl(sample.getBottomDepth()) + "\t");
		out.print(DBUtils.nvl(sample.getDepthUnit()) + "\t");
		out.print(((sample.getDrillType() != null) ? sample.getDrillType().getName() : "") + "\t");
	}
	
	public String encodeTaxaString(PaleontologyListEntry palList) {
		Integer specCount = palList.getSpecimenCount();
		String specCoord = palList.getSpecimenCoords();
		String comments = palList.getComments();
		  
		String enc = ((specCount != null) ? specCount.toString() : "") + "|" + specCoord + "|" + comments;
		  
		if (specCount == null && FREDUtil.isEmpty(specCoord) && FREDUtil.isEmpty(comments))
			enc = "*";
		else if (specCount != null && !FREDUtil.isEmpty(specCoord) && FREDUtil.isEmpty(comments))
			enc = specCount.toString() + "|" + specCoord;
		else if (specCount != null && FREDUtil.isEmpty(specCoord) && FREDUtil.isEmpty(comments))
			enc = specCount.toString();
		else if (specCount == null && FREDUtil.isEmpty(specCoord) && !FREDUtil.isEmpty(comments))
			enc = comments;
		 
		return enc;
	}
	
%><%
	User user = (User) getUser(session);
	FeatureUtil featureUtil = new FeatureUtil(FredHibernate.get().getDAOFactory());
	StageUtil stageUtil = new StageUtil(FredHibernate.get().getDAOFactory());
	SampleUtil sampleUtil = new SampleUtil(FredHibernate.get().getDAOFactory());
	RecordUtil recordUtil = new RecordUtil(FredHibernate.get().getDAOFactory());

	TreeSet<Sample> samples = new TreeSet<Sample>();
	try {
		if (request.getParameter("featId") != null) {
			Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("featId")));
			for (Sample sample : feature.getSamples())
				samples.add(sample);
		} else if (request.getParameter("sampId") != null) {
			samples.add(sampleUtil.getSample(Integer.parseInt(request.getParameter("sampId"))));
		} else if (session.getAttribute("FRED.features") != null && ((List<Feature>) session.getAttribute("FRED.features")).size() > 0) {
			List<Feature> features = (List<Feature>) session.getAttribute("FRED.features");
			for (Feature feature : features) {
				FredHibernate.get().currentSession().refresh(feature);
				for (Sample sample : feature.getSamples())
					samples.add(sample);
			}
		}
	} catch (Exception e) {
		e.printStackTrace(new PrintWriter(out));
	}
	
	if (samples.size() > 0) {
		try {
			boolean collectionFlag = (request.getParameter("collection") != null);
			boolean stratigraphyFlag = (request.getParameter("stratigraphy") != null);
			boolean sedimentaryFlag = (request.getParameter("sedimentary") != null);
			boolean localityFlag = collectionFlag || stratigraphyFlag || sedimentaryFlag;
			boolean adoptionFlag = (request.getParameter("adoption") != null);
			boolean paleontologyFlag = (request.getParameter("paleontology") != null);
			boolean palListFlag = (request.getParameter("palList") != null);
			
			response.setHeader("Content-Disposition", "attachment; filename=\"FRED_download.txt\"");
			response.setContentType("application/x-octet-stream");
	
			//file header
			out.println("**************************************************************************************************************");
			out.println("Data downloaded from FRED (http://www.fred.org.nz) on " + FREDUtil.formatDateForOutput(new Date()));
			out.println("FRED is the computer database for the NZ Fossil Record File (FRF), which is a nationally significant database administrated by GSNZ and GNS Science                                ");
			out.println("Please acknowledge use of this data in publications, reports and presentations.                               ");
			out.println("**************************************************************************************************************");
			out.print("\n");
			
			if (localityFlag) {
				out.println("********");
				out.println("Locality");
				out.println("********");
	
				writeLocalityHeader(out);
				out.print("Original Grid Reference\tNZMG Easting\tNZMG Northing\tNZGD49 Latitude\tNZGD49 Longitude\tMap Year\tMethod\tAccuracy\tLocality\tCountry\tCoordinate Comments\tLocality Comments\t");
				
				if (collectionFlag)
					out.print("Collectors\tCollection Date\tFossils in Place\tSent To\tNot Collected\tSignificance/Comments\t");
				if (stratigraphyFlag) {
					out.print("Stratigraphic Name\tInferred Stage Lower\tInferred Lower Modifier\tInferred Stage Upper\tInferred Upper Modifier\tInferred Age Start\tInferred Age Stop\tKnown Stage Lower\tKnown Lower Modifier\tKnown Stage Upper\tKnown Upper Modifier\tKnown Age Start\tKnown Age Stop\t");
					out.print("Samples Nearby\tSample Relationships\tStratigraphic Relationships\tColumn/Map\tDip\tDip Direction\tStrike\tFacing\tStratigraphy Comments\t");	
				}
				if (sedimentaryFlag) {
					out.print("Primary Grainsize\tSecondary Grainsize\tComparator Used\tBedding Thickness\tBedding Features\tWeathering\tHardness\tCarbonate\tColour\tSedimentary Features\tInferred Environment\tNature of Rock Unit\tCorrespondence\t");
				}
				
				out.print("\n");
	
				for (Sample sample : samples) {
					Feature feature = sample.getFeature();
					if (featureUtil.isAllowedReadFeatureSite(user, feature)) {
						writeLocality(sample, out);
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
							out.print("\t\t\t\t\t");
						}
						out.print(DBUtils.nvl(feature.getMapYear()) + "\t");
						out.print(((sv != null) ? DBUtils.nvl(sv.getMethod()) : "") + "\t");
						out.print(((sv != null) ? DBUtils.nvl(sv.getAccuracy()) : "") + "\t");
						
						if (featureUtil.isAllowedReadFeature(user, feature)) {
							out.print(DBUtils.nvl(feature.getLocality()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
							out.print(((sv != null) ? sv.getCountryName() : "") + "\t");
							out.print(DBUtils.nvl(feature.getCoordComments()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
							out.print(DBUtils.nvl(feature.getComments()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
										
							if (collectionFlag) {
								if (!FREDUtil.isEmpty(sample.getCollectors())) {
									for (Person collector : sample.getCollectors())
										out.print(collector.getName() + "; ");
								}
								out.print("\t");
								out.print(DBUtils.nvl(FREDUtil.formatDateForOutput(sample.getCollectionDate(), sample.getDateRounding())) + "\t");
								out.print(DBUtils.nvl(sample.getInPlace()) + "\t");
								if (!FREDUtil.isEmpty(sample.getSentTos())) {
									for (SentTo sentTo : sample.getSentTos())
										out.print(SampleUtil.getSentToDescription(sentTo).replaceAll("\\s\\s+|\\n|\\r", " ") + "; ");
								}
								out.print("\t");
								out.print(DBUtils.nvl(sample.getNotCollected()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
								out.print(DBUtils.nvl(sample.getSignificance()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
							}
							
							if (stratigraphyFlag) {
								out.print(DBUtils.nvl(sample.getStratUnit()) + "\t");
								if (sample.getInferredStage() != null) {
									Stage stage = sample.getInferredStage();
									out.print(((stage.getLowerAge() != null) ? stage.getLowerAge().getName() : "") + "\t");
									out.print(DBUtils.nvl(stage.getStageLowerMod()) + "\t");
									out.print(((stage.getUpperAge() != null) ? stage.getUpperAge().getName() : "") + "\t");
									out.print(DBUtils.nvl(stage.getStageUpperMod()) + "\t");
									out.print(stageUtil.getNumericAgeStart(stage) + "\t");
									out.print(stageUtil.getNumericAgeStop(stage) + "\t");
								} else
									out.print("\t\t\t\t\t\t");
								if (sample.getKnownStage() != null) {
									Stage stage = sample.getKnownStage();
									out.print(((stage.getLowerAge() != null) ? stage.getLowerAge().getName() : "") + "\t");
									out.print(DBUtils.nvl(stage.getStageLowerMod()) + "\t");
									out.print(((stage.getUpperAge() != null) ? stage.getUpperAge().getName() : "") + "\t");
									out.print(DBUtils.nvl(stage.getStageUpperMod()) + "\t");
									out.print(stageUtil.getNumericAgeStart(stage) + "\t");
									out.print(stageUtil.getNumericAgeStop(stage) + "\t");
								} else
									out.print("\t\t\t\t\t\t");
								List<? extends Relationship> nearbys = sampleUtil.getRelationships(sample, "Sample", "nearby");
								if (nearbys != null && nearbys.size() > 0) {
									for (Relationship rel : nearbys)
										out.print(SampleUtil.getRelationshipDescription(rel) + "; ");
								}
								out.print("\t");
								List<? extends Relationship> sampRels = sampleUtil.getRelationships(sample, "Sample", new String[] {"above", "below"});
								if (nearbys != null && sampRels.size() > 0) {
									for (Relationship rel : sampRels)
										out.print(SampleUtil.getRelationshipDescription(rel) + "; ");
								}
								out.print("\t");
								List<? extends Relationship> stratRels = sampleUtil.getRelationships(sample, "Stratigraphic", new String[] {"above top", "above base", "below top", "below base"});
								if (nearbys != null && stratRels.size() > 0) {
									for (Relationship rel : stratRels)
										out.print(SampleUtil.getRelationshipDescription(rel) + "; ");
								}
								out.print("\t");
								out.print(DBUtils.nvl(sample.getColumnMap()) + "\t");
								out.print(DBUtils.nvl(sample.getDip()) + "\t");
								out.print(DBUtils.nvl(sample.getDipDirection()) + "\t");
								out.print(DBUtils.nvl(sample.getStrike()) + "\t");
								out.print(DBUtils.nvl(sample.getFacing()) + "\t");
								out.print(DBUtils.nvl(sample.getStratComments()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
							}
							if (sedimentaryFlag) {
								out.print(((sample.getPrimaryGrainSize() != null) ? sample.getPrimaryGrainSize().getName() : "") + "\t");
								out.print(((sample.getSecondaryGrainSize() != null) ? sample.getSecondaryGrainSize().getName() : "") + "\t");
								out.print(DBUtils.nvl(sample.getComparatorUsed()) + "\t");
								out.print(((sample.getBedThickness() != null) ? sample.getBedThickness().getName() : "") + "\t");
								out.print(SampleUtil.getBeddingDescription(sample) + "\t");
								out.print(((sample.getWeathering() != null) ? sample.getWeathering().getName() : "") + "\t");
								out.print(((sample.getHardness() != null) ? sample.getHardness().getName() : "") + "\t");
								out.print(((sample.getCarbonate() != null) ? sample.getCarbonate().getName() : "") + "\t");
								out.print(SampleUtil.getColourDescription(sample) + "\t");
								if (!FREDUtil.isEmpty(sample.getSedimentaryFeatures())) {
									for (SedimentaryFeature sedFeat : sample.getSedimentaryFeatures())
										out.print(SampleUtil.getSedFeatureDescription(sedFeat) + "; ");
								}
								out.print("\t");
								out.print(DBUtils.nvl(sample.getDepositionEnv()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
								out.print(DBUtils.nvl(sample.getRockNature()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
								out.print(DBUtils.nvl(sample.getCorrespondence()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
							}
						}
						out.print("\n");
					}
				}
				out.print("\n");
			}
	
			if (adoptionFlag) {
				out.println("********");
				out.println("Adoption");
				out.println("********");
				
				writeLocalityHeader(out);
				out.print("Adoptors\tAdoption Date\tAdopted Stage Lower\tAdopted Lower Modifier\tAdopted Stage Upper\tAdopted Upper Modifier\tAdopted Age Start\tAdopted Age Stop\tComments\n");
				
				for (Sample sample : samples) {
					for (Adoption adoption : recordUtil.getAdoptionRecords(sample)) {
						if (recordUtil.isAllowedReadRecord(user, adoption.getRecord())) {
							writeLocality(adoption.getRecord().getSample(), out);
							if (!FREDUtil.isEmpty(adoption.getAdoptors())) {
								for (Person person : adoption.getAdoptors())
									out.print(person.getName() + "; ");
							}
							out.print("\t");
							out.print(DBUtils.nvl(FREDUtil.formatDateForOutput(adoption.getAdoptionDate(), adoption.getDateRounding())) + "\t");
							if (adoption.getStage() != null) {
								Stage stage = adoption.getStage();
								out.print(((stage.getLowerAge() != null) ? stage.getLowerAge().getName() : "") + "\t");
								out.print(DBUtils.nvl(stage.getStageLowerMod()) + "\t");
								out.print(((stage.getUpperAge() != null) ? stage.getUpperAge().getName() : "") + "\t");
								out.print(DBUtils.nvl(stage.getStageUpperMod()) + "\t");
								out.print(stageUtil.getNumericAgeStart(stage) + "\t");
								out.print(stageUtil.getNumericAgeStop(stage) + "\t");
							} else
								out.print("\t\t\t\t\t\t");
							out.print(DBUtils.nvl(adoption.getComments()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
							out.print("\n");	
						}
					}
				}
				out.print("\n");
			}
			
			if (paleontologyFlag) {
				out.println("********");
				out.println("Paleontology");
				out.println("********");
				
				writeLocalityHeader(out);
				out.print("Identifiers\tIdentification Date\tStage Lower\tLower Modifier\tStage Upper\tUpper Modifier\tAge Start\tAge Stop\tStage Comments\tnLab Number\tCollection Comments\n");
				
				for (Sample sample : samples) {
					for (Paleontology paleontology : recordUtil.getPaleontologyRecords(sample)) {
						if (recordUtil.isAllowedReadRecord(user, paleontology.getRecord())) {
							writeLocality(paleontology.getRecord().getSample(), out);
							if (!FREDUtil.isEmpty(paleontology.getIdentifiers())) {
								for (Person person : paleontology.getIdentifiers())
									out.print(person.getName() + "; ");
							}
							out.print("\t");
							out.print(DBUtils.nvl(FREDUtil.formatDateForOutput(paleontology.getIdentificationDate(), paleontology.getDateRounding())) + "\t");
							if (paleontology.getStage() != null) {
								Stage stage = paleontology.getStage();
								out.print(((stage.getLowerAge() != null) ? stage.getLowerAge().getName() : "") + "\t");
								out.print(DBUtils.nvl(stage.getStageLowerMod()) + "\t");
								out.print(((stage.getUpperAge() != null) ? stage.getUpperAge().getName() : "") + "\t");
								out.print(DBUtils.nvl(stage.getStageUpperMod()) + "\t");
								out.print(stageUtil.getNumericAgeStart(stage) + "\t");
								out.print(stageUtil.getNumericAgeStop(stage) + "\t");
							} else
								out.print("\t\t\t\t\t\t");
							out.print(DBUtils.nvl(paleontology.getStageComments()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
							out.print(DBUtils.nvl(RecordUtil.getLabNumberDescription(paleontology)) + "\t");
							out.print(DBUtils.nvl(paleontology.getCollectionComments()).replaceAll("\\s\\s+|\\n|\\r", " ") + "\t");
							out.print("\n");	
						}
					}
				}
				out.print("\n");
			}
			
			if (palListFlag) {
				out.println("********");
				out.println("Paleontology List");
				out.println("********");
				
				List<List<Paleontology>> paleontologyMasterList = new Vector<List<Paleontology>>();
				List<Paleontology> paleontologies = new Vector<Paleontology>();
				int i = 0;
				for (Sample sample : samples) {
					for (Paleontology paleontology : recordUtil.getPaleontologyRecords(sample)) {
						if (recordUtil.isAllowedReadPalList(user, paleontology)) {
							paleontologies.add(paleontology);
							if (++i == 250) {
								paleontologyMasterList.add(paleontologies);
								paleontologies = new Vector<Paleontology>();
								i =0;
							}
						}
					}
				}
				if (paleontologies.size() > 0)
					paleontologyMasterList.add(paleontologies);
								
				if (paleontologyMasterList.size() > 0) {
					for (List<Paleontology> pals : paleontologyMasterList) {
						
						out.print("FR Number\t\t");
						for (Paleontology paleontology : pals) {
							Sample sample = paleontology.getRecord().getSample();
							if (sample.getFrNumber() != null)
								out.print(sample.getFrNumber().getFrNumber() + "\t");
							else
								out.print(((sample.getFeature().getFrNumber() != null) ? sample.getFeature().getFrNumber().getFrNumber() : "") + "\t");
						}
						out.print("\n");
						
						out.print("Yard FR Number\t\t");
						for (Paleontology paleontology : pals) {
							Sample sample = paleontology.getRecord().getSample();
							if (sample.getYardFrNumber() != null)
								out.print(sample.getYardFrNumber().getFrNumber() + "\t");	
							else
								out.print(((sample.getFeature().getYardFrNumber() != null) ? sample.getFeature().getYardFrNumber().getFrNumber() : "") + "\t");
	
						}
						out.print("\n");
						
						out.print("Locality Type\t\t");
						for (Paleontology paleontology : pals)
							out.print(paleontology.getRecord().getSample().getFeature().getFeatureType() + "\t");
						out.print("\n");
						
						out.print("Field Number/Drillhole Name\t\t");
						for (Paleontology paleontology : pals)
							out.print(DBUtils.nvl(paleontology.getRecord().getSample().getFeature().getFeatureName()) + "\t");
						out.print("\n");
	
						out.print("Depth From\t\t");
						for (Paleontology paleontology : pals)
							out.print(DBUtils.nvl(paleontology.getRecord().getSample().getTopDepth()) + "\t");
						out.print("\n");
						
						out.print("Depth To\t\t");
						for (Paleontology paleontology : pals)
							out.print(DBUtils.nvl(paleontology.getRecord().getSample().getBottomDepth()) + "\t");
						out.print("\n");
						
						out.print("Depth Unit\t\t");
						for (Paleontology paleontology : pals)
							out.print(DBUtils.nvl(paleontology.getRecord().getSample().getDepthUnit()) + "\t");
						out.print("\n");
						
						out.print("Drill Type\t\t");
						for (Paleontology paleontology : pals)
							out.print(((paleontology.getRecord().getSample().getDrillType() != null) ? paleontology.getRecord().getSample().getDrillType().getName() : "") + "\t");
						out.print("\n");
						
						out.print("Identifier\t\t");
						for (Paleontology paleontology : pals) {
							if (!FREDUtil.isEmpty(paleontology.getIdentifiers())) {
								for (Person person : paleontology.getIdentifiers())
									out.print(person.getName() + "; ");
							}
							out.print("\t");
						}
						out.print("\n");
						
						TreeSet<TaxonomicNameAndGroup> taxonomicNames = new TreeSet<TaxonomicNameAndGroup>();
						for (Paleontology paleontology : pals) {
							for (PaleontologyListEntry palList : paleontology.getListEntries()) {
								TaxonomicNameAndGroup nameAndGroup = new TaxonomicNameAndGroup(palList.getTaxonomicName(), palList.getTaxonomicGroup());
								//if (!taxonomicNames.contains(nameAndGroup))
									taxonomicNames.add(nameAndGroup);
							}
						}
						//List<ReferencedTaxonomicName> sortedTaxonomicNames = new Vector<ReferencedTaxonomicName>();
						//sortedTaxonomicNames.addAll(taxonomicNames);
						//Collections.sort(taxonomicNames);
						for (TaxonomicNameAndGroup nameAndGroup : taxonomicNames) {
							out.print(nameAndGroup.getTaxonomicGroup().getName() + "\t" + DBUtils.nvl(nameAndGroup.getTaxonomicName()) + "\t");
							for (Paleontology paleontology : pals) {
								for (PaleontologyListEntry palList : paleontology.getListEntries()) {
									TaxonomicNameAndGroup check = new TaxonomicNameAndGroup(palList.getTaxonomicName(), palList.getTaxonomicGroup());
									if (check.equals(nameAndGroup)){
										out.print(encodeTaxaString(palList));
										break;
									}
									
								}
								out.print("\t");
							}
							out.print("\n");
						}
						out.print("\n");
					}
				}
			}
			
			new AuditUtil(FredHibernate.get().getDAOFactory()).addLogEntry(AuditUtil.DOWNLOAD_LOG_TYPE, user, samples.size());
			
		} catch (Exception e) {
			e.printStackTrace(new PrintWriter(out));
		}
	} else {
		ExtranetTemplate et = getExtranetTemplate();
		et.setDisplayLoadingMessage(true);
		%><p>No data</p><%
		drawBottom(out, et);
	}
%>