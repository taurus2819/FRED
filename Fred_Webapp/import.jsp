<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.fred.data.*, nz.cri.gns.fred.dataentry.*, nz.cri.gns.jsp.*, nz.cri.gns.util.map.*, nz.cri.gns.db.*, nz.cri.gns.intranet.*, nz.cri.gns.db.site.*, java.sql.*, java.text.*, java.net.*, nz.cri.gns.auth.*, java.lang.*, java.util.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	PageState state = new PageState(request, response, getServletContext());
	String status = "";
	String message = "";

    String userName = request.getParameter("user");
    String password = request.getParameter("pass");
    String foldID = request.getParameter("folderID");
    String button = request.getParameter("button");
    String docType = request.getParameter("doc_type");
    if (button == null || button.equals(""))
    	button = "save";

	User user = null;
	try {
		user = new User(userName, password, JspUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext()));
	} catch (Exception e) {
		status = "AuthError";
		message = "Invalid username/password";
	}
	
	if (docType != null && user != null) {
	    try {
	    	String locType = request.getParameter("loc_type");
	    	String id = request.getParameter("id");
	    	DataEntryForm dataEntryForm = null;
	    	if (docType.equals("Locality")) {
	    		if (id == null || id.equals("")) {
	    			dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(locType, user, Integer.parseInt(foldID), state);
	    		} else {
		    		dataEntryForm = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(id), user, state);
	    		}
	    	} else if (docType.equals("Sample")) {
	    		if (id == null || id.equals("")) {
	    			Feature feature = null;
					Folder folder = new Folder(Integer.parseInt(foldID), user, state);
					if (folder.isAllowedReadLocalities() && folder.get(Folder.FEATURES) != null) {
						for (Iterator i = folder.getAsVector(Folder.FEATURES).iterator(); i.hasNext(); ) {
							feature = new Feature(((Integer) i.next()).intValue(), user, state);
							if (feature.getAsString(Feature.FEATURE_NAME).equals(request.getParameter("featName")))
								break;
						}
					}
					if (feature != null) {
						id = String.valueOf(feature.addNewSample(request.getParameter("TopDepth"), request.getParameter("BottomDepth"), request.getParameter("DrillType"), foldID));
					} else {
						throw new DataInputException("Feature Name", "Feature name not found");
					}
	    		} else {
	    			Sample sample = new Sample(Integer.parseInt(id), user, state);
	    			sample.editSample(request.getParameter("TopDepth"), request.getParameter("BottomDepth"), request.getParameter("DrillType"));
	    		}
	    		dataEntryForm = DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(id), user, state);
		    }

			dataEntryForm.setTempField(DataEntryForm.FEATURE_NAME, request.getParameter("FeatName"));
			dataEntryForm.setTempField(DataEntryForm.REGISTRATION_AREA, request.getParameter("RegAreaID"));
			dataEntryForm.setTempField(DataEntryForm.WORKING_COMMENTS, request.getParameter("WorkComm"));
			dataEntryForm.setTempField(DataEntryForm.SECURITY_TYPE, request.getParameter("SecType"));
			dataEntryForm.setTempField(DataEntryForm.GRID_REF, request.getParameter("GridRef"));
			dataEntryForm.setTempField(DataEntryForm.METHOD, request.getParameter("LocMethodID"));
			dataEntryForm.setTempField(DataEntryForm.ACCURACY, request.getParameter("Accuracy"));
			dataEntryForm.setTempField(DataEntryForm.LOCALITY_DESC, request.getParameter("Loc"));
			dataEntryForm.setTempField(DataEntryForm.RECOLLECTION, request.getParameter("Recoll"));
			dataEntryForm.setTempField(DataEntryForm.OPERATING_COMPANY, request.getParameter("Person"));
			dataEntryForm.setTempField(DataEntryForm.START_DATE, request.getParameter("StartDate"));
			dataEntryForm.setTempField(DataEntryForm.COMPLETION_DATE, request.getParameter("FinishDate"));
			dataEntryForm.setTempField(DataEntryForm.LICENCE_AREA, request.getParameter("LicArea"));
			dataEntryForm.setTempField(DataEntryForm.DATUM_TYPE, request.getParameter("DatumType"));
			dataEntryForm.setTempField(DataEntryForm.DATUM_ELEVATION, request.getParameter("DatumEl"));
			dataEntryForm.setTempField(DataEntryForm.KICK_OFF_DEPTH, request.getParameter("StartDepth"));
			dataEntryForm.setTempField(DataEntryForm.TERMINATION_DEPTH, request.getParameter("FinishDepth"));

				//sample property fields
				dataEntryForm.setTempField(DataEntryForm.COLLECTION_DATE, request.getParameter("CollDate"));
				dataEntryForm.setTempField(DataEntryForm.COLLECTORS, request.getParameter("Coll"));
				dataEntryForm.setTempField(DataEntryForm.STRAT_NAME, request.getParameter("StratName"));
				dataEntryForm.setTempField(DataEntryForm.FOSSILS_IN_PLACE, request.getParameter("InPlace"));
				dataEntryForm.setTempField(DataEntryForm.SENT_TO, request.getParameter("SentTo"));
				dataEntryForm.setTempField(DataEntryForm.NOT_COLLECTED, request.getParameter("NotColl"));
				dataEntryForm.setTempField(DataEntryForm.SIGNIFICANCE_COMMENTS, request.getParameter("Sig"));
				dataEntryForm.setTempField(DataEntryForm.INF_AGE_START, request.getParameter("InfStageStart"));
				dataEntryForm.setTempField(DataEntryForm.INF_START_MOD, request.getParameter("InfStartMod"));
				dataEntryForm.setTempField(DataEntryForm.INF_AGE_STOP, request.getParameter("InfStageStop"));
				dataEntryForm.setTempField(DataEntryForm.INF_STOP_MOD, request.getParameter("InfStopMod"));
				dataEntryForm.setTempField(DataEntryForm.KNW_AGE_START, request.getParameter("KnwStageStart"));
				dataEntryForm.setTempField(DataEntryForm.KNW_START_MOD, request.getParameter("KnwStartMod"));
				dataEntryForm.setTempField(DataEntryForm.KNW_AGE_STOP, request.getParameter("KnwStageStop"));
				dataEntryForm.setTempField(DataEntryForm.KNW_STOP_MOD, request.getParameter("KnwStopMod"));
				dataEntryForm.setTempField(DataEntryForm.PREVIOUS_SAMPLE, request.getParameter("PrevSamp"));
				dataEntryForm.setTempField(DataEntryForm.SAMPLE_RELATIONSHIP, request.getParameter("SampRel"));
				dataEntryForm.setTempField(DataEntryForm.STRAT_RELATIONSHIP, request.getParameter("StratRel"));
				dataEntryForm.setTempField(DataEntryForm.COLUMN_MAP, request.getParameter("ColMap"));
				dataEntryForm.setTempField(DataEntryForm.DIP, request.getParameter("Dip"));
				dataEntryForm.setTempField(DataEntryForm.DIP_DIRECTION, request.getParameter("DipDir"));
				dataEntryForm.setTempField(DataEntryForm.STRIKE, request.getParameter("Strike"));
				dataEntryForm.setTempField(DataEntryForm.FACING, request.getParameter("Facing"));
				dataEntryForm.setTempField(DataEntryForm.GRAIN_SIZE_P, request.getParameter("GrainSizeP"));
				dataEntryForm.setTempField(DataEntryForm.GRAIN_SIZE_S, request.getParameter("GrainSizeS"));
				dataEntryForm.setTempField(DataEntryForm.GS_COMP, request.getParameter("GSComp"));
				dataEntryForm.setTempField(DataEntryForm.BEDDING_THICKNESS, request.getParameter("BedThick"));
				dataEntryForm.setTempField(DataEntryForm.BEDDING_P, request.getParameter("BeddingP"));
				dataEntryForm.setTempField(DataEntryForm.BEDDING_S, request.getParameter("BeddingS"));
				dataEntryForm.setTempField(DataEntryForm.WEATHERING, request.getParameter("Weath"));
				dataEntryForm.setTempField(DataEntryForm.HARDNESS, request.getParameter("Hard"));
				dataEntryForm.setTempField(DataEntryForm.CARBONATE, request.getParameter("Carb"));
				dataEntryForm.setTempField(DataEntryForm.COLOUR_MOD, request.getParameter("ColMod"));
				dataEntryForm.setTempField(DataEntryForm.COLOUR_P, request.getParameter("ColourP"));
				dataEntryForm.setTempField(DataEntryForm.COLOUR_S, request.getParameter("ColourS"));
				dataEntryForm.setTempField(DataEntryForm.WET, request.getParameter("Wet"));
				dataEntryForm.setTempField(DataEntryForm.SED_FEATURES, request.getParameter("SedFeat"));
				dataEntryForm.setTempField(DataEntryForm.DEP_ENVIRONMENT_1, request.getParameter("DepEnv1"));
				dataEntryForm.setTempField(DataEntryForm.DEP_ENVIRONMENT_2, request.getParameter("DepEnv2"));
				dataEntryForm.setTempField(DataEntryForm.ROCK_NATURE, request.getParameter("RockNat"));
				dataEntryForm.setTempField(DataEntryForm.CORRESPONDENCE, request.getParameter("Corr"));
				
				//Adoption fields
				dataEntryForm.setTempField(DataEntryForm.ADOPTION_DATE, request.getParameter("AdoDate"));
				dataEntryForm.setTempField(DataEntryForm.ADOPTORS, request.getParameter("Adoptor"));
				dataEntryForm.setTempField(DataEntryForm.ADO_AGE_START, request.getParameter("StageStart"));
				dataEntryForm.setTempField(DataEntryForm.ADO_START_MOD, request.getParameter("StartMod"));
				dataEntryForm.setTempField(DataEntryForm.ADO_AGE_STOP, request.getParameter("StageStop"));
				dataEntryForm.setTempField(DataEntryForm.ADO_STOP_MOD, request.getParameter("StopMod"));
				dataEntryForm.setTempField(DataEntryForm.ADO_COMMENTS, request.getParameter("Comm"));
	
				//Paleontology fields
				dataEntryForm.setTempField(DataEntryForm.IDENTIFICATION_DATE, request.getParameter("PalDate"));
				dataEntryForm.setTempField(DataEntryForm.IDENTIFIERS, request.getParameter("Identifier"));
				dataEntryForm.setTempField(DataEntryForm.IDT_AGE_START, request.getParameter("StageStart"));
				dataEntryForm.setTempField(DataEntryForm.IDT_START_MOD, request.getParameter("StartMod"));
				dataEntryForm.setTempField(DataEntryForm.IDT_AGE_STOP, request.getParameter("StageStop"));
				dataEntryForm.setTempField(DataEntryForm.IDT_STOP_MOD, request.getParameter("StopMod"));
				dataEntryForm.setTempField(DataEntryForm.STAGE_COMMENTS, request.getParameter("StComm"));
				dataEntryForm.setTempField(DataEntryForm.LAB_SECTION, request.getParameter("SectID"));
				dataEntryForm.setTempField(DataEntryForm.LAB_NUMBER, request.getParameter("LabNum"));
				dataEntryForm.setTempField(DataEntryForm.COLLECTION_COMMENTS, request.getParameter("CollComm"));
				dataEntryForm.setTempField(DataEntryForm.TAXA_LIST, request.getParameter("Taxa"));

			dataEntryForm.setFieldsFromTemp();
	    	if (button.equals("save")) {
	    		id = String.valueOf(dataEntryForm.save());
	    		status = "Saved OK";
	    	} else {
	    		id = String.valueOf(dataEntryForm.submit());
	    		if (request.getParameter("FRNum") != null) {
	    			FRNumber frNum = FRNumber.parseFRNumber(request.getParameter("FRNum"));
	    			FolderUtils.approveLocality(id, frNum, null, user, state);
	    		}
	    		status = "Submitted OK";
	    	}
			message = id;
		} catch (InsufficientPrivelegesException e) {
			status = "AuthError";
			message = "User not authorised";
	    } catch (DataInputException e) {
	    	status = "Error";
			message = "Data Error: " + e.getField() + " - " + e.getMessage();
	    } catch (Exception e) {
	    	status = "Error";
			message = "Unspecified Error: " + e.toString();
	    }
	}

%>
<html>
<head></head>
<body>
<table><tr><td><%=status%></td><td><%=message%></td></tr></table>
</body>
</html>