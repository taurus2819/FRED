<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.*, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		try {
			return new Authenticable[] {
				 new IPRightAccess(
					new IPRight(
						"FRED data entry",
						getIPApp(
							request.getSession(),
							getServletConfig().getServletContext())),
					Right.ANY_RIGHT)};
		} catch (Exception e) {
			//Database error, so just block them
			return new Authenticable[] {
				 new IPRightAccess(
					IPRight.BLOCKED_IP_RIGHT,
					Right.BLOCKED_RIGHT)};
		}
	}
%><%!	class BadRightsException extends Exception {}
%><%!	class DataInputException extends Exception {
			private String field;
			DataInputException() { }
			DataInputException(String field, String msg) { super(msg); this.field = field; }
			public String getField() { return field; }
		}
%><%!	private int isMonth (String in) {
			in = in.toUpperCase();
			if (in.equals("JAN") || in.equals("FEB") || in.equals("MAR") || in.equals("APR") || in.equals("MAY") || in.equals("JUN") || in.equals("JUL") || in.equals("AUG") || in.equals("SEP") || in.equals("OCT") || in.equals("NOV") || in.equals("DEC")) {
				return 1;
			} else {
				return 0;
			}
		}
%><%!	private int checkStage (String stageStart, String stageStop, Statement statement) throws java.sql.SQLException {
			if (!stageStop.equals("-")) {
				ResultSet rs = statement.executeQuery("SELECT Ta_Age_Start, Ta_Age_Stop FROM Age_View WHERE Ag_ID = " + stageStart);
				rs.next();
				double startStart = rs.getDouble(1);
				double startStop = rs.getDouble(2);
				rs = statement.executeQuery("SELECT Ta_Age_Start, Ta_Age_Stop FROM Age_View WHERE Ag_ID = " + stageStop);
				rs.next();
				double stopStart = rs.getDouble(1);
				double stopStop = rs.getDouble(2);
				if (startStart < stopStart || startStop < stopStop) { return 0; }
			}
			return 1;
		}
%><%	
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	Statement statement2 = connection.getExtraStatement();
	ResultSet rs;
	int userRights = 0, execUp, i;
	String foldID, sampID, recID, auditID = "", featStatus, collDate = "", infStageID = "", knwStageID = "", sRel = "", srLine, srRel, srRelThing, srDistTemp, depEnv;
	String[] stPersonID = new String[20];
	String[] stLabID = new String[20];
	String[] stComm = new String[20];
	String[] srType = new String[20];
	String[] srDist = new String[20];
	String[] srDistRange = new String[20];
	String[] srDistMod = new String[20];
	String[] srRelID = new String[20];
	String[] srFeatID = new String[20];
	String[] srStratUnit = new String[20];
	String[] abund = new String[30];
	long[] collID = new long[20];
	long[] stGroupID = new long[20];
	long[] prevFeatID = new long[20];
	long[] sedFeatID = new long[30];
	User user = getUser(session);
	int userID = user.getPersonId();
	double checkDist;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);


	if (request.getParameter("FoldID") != null && request.getParameter("SaveType") != null && request.getParameter("RecID") != null && request.getParameter("SampID") != null) {

		foldID = request.getParameter("FoldID");
		sampID = request.getParameter("SampID");
		recID = request.getParameter("RecID");

		//set user rights
		rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID);
		if (rs.next()) { userRights = rs.getInt(1); }

		out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
		out.println("<tr><td colspan='2' align='center'><img src='images/sprop.gif' height='20' width='20' /></td></tr>");
		out.println("<tr><td colspan='2' align='center' class='heading'>Sample Property Record</td></tr>");
		out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
		out.println("<tr><td><a href='javascript:history.back();' title='Back to Data Entry'><img src='images/back_arrow.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='javascript:history.back();' class='heading'>Back to Data Entry</a></td></tr>");
		out.println("<tr><td><a href='folder_detail.jsp?ID=" + foldID + "' title='Quit Without Saving'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_detail.jsp?ID=" + foldID + "' class='heading'>Quit</a></td></tr>");
		out.println("</table>");

		drawEndNavigation(out);

		out.println("<table style='margin-left:20px; width:550px;' border='0'>");
		out.println("<tr><td>");

		try { //Surround with exception testing so can throw an exception if data problem

			//Parse tricky fields
			//Status
			if (request.getParameter("SaveType").equals("Submit")) {
				if ((userRights & 16) == 0) { throw new BadRightsException(); }
				featStatus = "approved";
			} else {
				featStatus = "working";
			}

			//CollDate
			if (request.getParameter("CollDateUnk") == null) {
				collDate = request.getParameter("CollDateDay") + "-" + request.getParameter("CollDateMonth") + "-" + request.getParameter("CollDateYear");
			}
			//Collector_ID
			if (!request.getParameter("Coll").equals("")) {
				String coll = request.getParameter("Coll");
				i = 0;
				while (coll.length() > 0) {
					if (coll.indexOf("\n") == -1) { coll = coll + "\n"; }
					rs = statement.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = '" + coll.substring(0, coll.indexOf("\n")).trim() + "'");
					if (rs.next()) {
						collID[++i] = rs.getLong(1);
					} else {  //Collector not in database so throw exception
						throw new DataInputException("Collector", coll.substring(0, coll.indexOf("\n")).trim() + " not in database - add through builder");
					}
					coll = coll.substring(coll.indexOf("\n") + 1, coll.length());
				}
			}
			//Sent To
			if (!request.getParameter("SentTo").equals("")) {
				String st = request.getParameter("SentTo"), stLine, stGroup, stPerson, stLab;
				i = 0;
				while (st.length() > 0) {
					if (st.indexOf("\n") == -1) { st = st + "\n"; }
					stLine = st.substring(0, st.indexOf("\n")).trim();
					stGroup = stLine.substring(0, stLine.indexOf("*"));
					stPerson = stLine.substring(stGroup.length() + 1, stLine.indexOf("*", stGroup.length() + 1));
					stLab = stLine.substring(stGroup.length() + stPerson.length() + 2, stLine.indexOf("*", stGroup.length() + stPerson.length() + 2));
					stComm[++i] = stLine.substring(stLine.lastIndexOf("*") + 1, stLine.length());
					st = st.substring(st.indexOf("\n") + 1, st.length()).trim();
					//check againt lookup values
					rs = statement.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = " + JspUtils.sqlEscape(stGroup) + " AND FieldName = 'FossilGroup'");
					if (rs.next()) {
						stGroupID[i] = rs.getLong(1);
					} else {  // not valid group
						throw new DataInputException("Sent To - Group", stGroup + " not a valid sent to group");
					}
					if (!stPerson.equals("")) {
						rs = statement.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = " + JspUtils.sqlEscape(stPerson));
						if (rs.next()) {
							stPersonID[i] = rs.getString(1);
						} else { // not valid person
							throw new DataInputException("Sent To - Person", stPerson + " not in database - add through builder");
						}
					}
					if (!stLab.equals("")) {
						rs = statement.executeQuery("SELECT Lab_ID FROM SC.Lab WHERE Lab_Name = " + JspUtils.sqlEscape(stLab));
						if (rs.next()) {
							stLabID[i] = rs.getString(1);
						} else { // not valid lab
							throw new DataInputException("Sent To - Lab", stLab + " not in database");
						}
					}
				}
			}
			//Stage ages
			if (checkStage(request.getParameter("InfStageStart"), request.getParameter("InfStageStop"), statement) == 0) { throw new DataInputException("Inferred Stage", "Stop age greater than Start age"); }
			if (checkStage(request.getParameter("KnwStageStart"), request.getParameter("KnwStageStop"), statement) == 0) { throw new DataInputException("Known Stage", "Stop age greater than Start age"); }
			//Samples nearby
			if (!request.getParameter("PrevSamp").equals("")) {
				String samp = request.getParameter("PrevSamp");
				i = 0;
				while (samp.length() > 0) {
					if (samp.indexOf(";") == -1) { samp = samp + ";"; }
					rs = statement.executeQuery("SELECT Feature_ID FROM Feature_Security_View WHERE Sample_Name = " + JspUtils.sqlEscape(samp.substring(0, samp.indexOf(";")).trim()) + " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = " + userID + "))");
					if (rs.next()) {
						prevFeatID[++i] = rs.getLong(1);
					} else {  //Sample not in database so throw exception
						throw new DataInputException("Samples Nearby", samp.substring(0, samp.indexOf(";")).trim() + " not in database - pick another");
					}
					samp = samp.substring(samp.indexOf(";") + 1, samp.length());
				}
			}
			i = 0;
			//SampRel
			if (!request.getParameter("SampRel").equals("")) {
				sRel = request.getParameter("SampRel");
				while (sRel.length() > 0) {
					if (sRel.indexOf("\n") == -1) { sRel = sRel + "\n"; }
					srLine = sRel.substring(0, sRel.indexOf("\n")).trim();
					srType[++i] = "Samp";
					if (srLine.indexOf("above") >= 0) {
						srRel = "above";
						srDistTemp = srLine.substring(0, srLine.indexOf("above")).trim();
						srRelThing = srLine.substring(srLine.indexOf("above") + 5, srLine.length()).trim();
					} else if (srLine.indexOf("below") >= 0) {
						srRel = "below";
						srDistTemp = srLine.substring(0, srLine.indexOf("below")).trim();
						srRelThing = srLine.substring(srLine.indexOf("below") + 5, srLine.length()).trim();
					} else {
						throw new DataInputException("Sample Relationships", srLine + " not a valid entry.  Please use the builder");
					}
					if (srDistTemp.indexOf("c.") == 0) {
						srDistMod[i] = "c.";
						srDistTemp = srDistTemp.substring(2, srDistTemp.length()).trim();
					}
					if (srDistTemp.indexOf("-") >= 0) {
						srDist[i] = srDistTemp.substring(0, srDistTemp.indexOf("-")).trim();
						srDistRange[i] = srDistTemp.substring(srDistTemp.indexOf("-") + 1, srDistTemp.length()).trim();
					} else {
						srDist[i] = srDistTemp;
					}
					sRel = sRel.substring(sRel.indexOf("\n") + 1, sRel.length()).trim();
					//check againt lookup values
					rs = statement.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = " + JspUtils.sqlEscape(srRel) + " AND FieldName = 'SampRel' AND Name <> 'Nearby'");
					if (rs.next()) {
						srRelID[i] = rs.getString(1);
					} else {  // not valid group
						throw new DataInputException("Sample Relationships - Relationship", srRel + " not a valid relationship");
					}
					rs = statement.executeQuery("SELECT Feature_ID FROM Feature_Security_View WHERE Sample_Name = " + JspUtils.sqlEscape(srRelThing) + " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = " + userID + "))");
					if (rs.next()) {
						srFeatID[i] = rs.getString(1);
					} else {  // not valid group
						throw new DataInputException("Sent To - Sample", srRelThing + " not a valid sample");
					}
				}
			}
			//StratRel
			if (!request.getParameter("StratRel").equals("")) {
				sRel = request.getParameter("StratRel");
				while (sRel.length() > 0) {
					if (sRel.indexOf("\n") == -1) { sRel = sRel + "\n"; }
					srLine = sRel.substring(0, sRel.indexOf("\n")).trim();
					srType[++i] = "Strat";
					if (srLine.indexOf("above base") >= 0) {
						srRel = "above base";
						srDistTemp = srLine.substring(0, srLine.indexOf("above base")).trim();
						srRelThing = srLine.substring(srLine.indexOf("above base") + 10, srLine.length()).trim();
					} else if (srLine.indexOf("above top") >= 0) {
						srRel = "above top";
						srDistTemp = srLine.substring(0, srLine.indexOf("above top")).trim();
						srRelThing = srLine.substring(srLine.indexOf("above top") + 9, srLine.length()).trim();
					} else if (srLine.indexOf("below base") >= 0) {
						srRel = "below base";
						srDistTemp = srLine.substring(0, srLine.indexOf("below base")).trim();
						srRelThing = srLine.substring(srLine.indexOf("below base") + 10, srLine.length()).trim();
					} else if (srLine.indexOf("below top") >= 0) {
						srRel = "below top";
						srDistTemp = srLine.substring(0, srLine.indexOf("below top")).trim();
						srRelThing = srLine.substring(srLine.indexOf("below top") + 9, srLine.length()).trim();
					} else {
						throw new DataInputException("Stratigraphic Relationships", srLine + " not a valid entry.  Please use the builder");
					}
					if (srDistTemp.indexOf("c.") == 0) {
						srDistMod[i] = "c.";
						srDistTemp = srDistTemp.substring(2, srDistTemp.length()).trim();
					}
					if (srDistTemp.indexOf("-") >= 0) {
						srDist[i] = srDistTemp.substring(0, srDistTemp.indexOf("-")).trim();
						srDistRange[i] = srDistTemp.substring(srDistTemp.indexOf("-") + 1, srDistTemp.length()).trim();
					} else {
						srDist[i] = srDistTemp;
					}
					sRel = sRel.substring(sRel.indexOf("\n") + 1, sRel.length()).trim();
					//check againt lookup values
					rs = statement.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = " + JspUtils.sqlEscape(srRel) + " AND FieldName = 'StratRel'");
					if (rs.next()) {
						srRelID[i] = rs.getString(1);
					} else {  // not valid group
						throw new DataInputException("Sent To - Relationship", srRel + " not a valid relationship");
					}
					srStratUnit[i] = srRelThing.trim();
				}
			}
			//Sedimentary Features
			if (!request.getParameter("SedFeat").equals("")) {
				String sedFeat = request.getParameter("SedFeat");
				i = 0;
				while (sedFeat.length() > 0) {
					i++;
					if (sedFeat.indexOf(";") == -1) { sedFeat = sedFeat + ";"; }
					if (sedFeat.indexOf("*") != -1 && sedFeat.indexOf("*") < sedFeat.indexOf(";")) {
						rs = statement.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = '" + sedFeat.substring(0, sedFeat.indexOf("*")).trim() + "' AND FieldName = 'SedFeature'");
						abund[i] = "Y";
					} else {
						rs = statement.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = '" + sedFeat.substring(0, sedFeat.indexOf(";")).trim() + "' AND FieldName = 'SedFeature'");
					}
					if (rs.next()) {
						sedFeatID[i] = rs.getLong(1);
					} else {  //Sample not in database so throw exception
						throw new DataInputException("Additional Features", sedFeat.substring(0, 1) + " not in database - pick another");
					}
					sedFeat = sedFeat.substring(sedFeat.indexOf(";") + 1, sedFeat.length()).trim();
				}
			}
			//Deposition Env
			if (!request.getParameter("DepEnv1").equals("")) {
				if (!request.getParameter("DepEnv2").equals("")) {
					depEnv = request.getParameter("DepEnv1") + ": " + request.getParameter("DepEnv2");
				} else {
					depEnv = request.getParameter("DepEnv1");
				}
			} else {
				depEnv = request.getParameter("DepEnv2");
			}

			if (recID.equals("0")) {
				//create new audit, record data
				if ((userRights & 4) == 0) { throw new BadRightsException(); }
				rs = statement.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
				rs.next();
				auditID = rs.getString(1);
				execUp = statement.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Comments, Working_Folder_ID) VALUES (" + auditID + ", 'working', " + userID + ", SYSDATE, " + JspUtils.sqlEscape(request.getParameter("WorkComm")) + ", " + foldID + ")");
				rs = statement.executeQuery("SELECT Record_Seq.NEXTVAL FROM DUAL");
				rs.next();
				recID = rs.getString(1);
				execUp = statement.executeUpdate("INSERT INTO Record (Record_ID, Sample_ID, Audit_ID) VALUES (" + recID + ", " + sampID + ", " + auditID + ")");
			} else { // edit
				//Update edited by fields and delete SAMPLE_PROPERTY (ready for adding new data from form)
				if ((userRights & 2) == 0) { throw new BadRightsException(); }
				rs = statement.executeQuery("SELECT Audit_ID FROM Record WHERE Record_ID = " + recID);
				rs.next();
				auditID = rs.getString(1);
				execUp = statement.executeUpdate("UPDATE Audit_Table SET Modified_By_ID = " + userID + ", Modified_Date = SYSDATE, Working_Comments = " + JspUtils.sqlEscape(request.getParameter("WorkComm")) + " WHERE Audit_ID = " + auditID);
				execUp = statement.executeUpdate("DELETE FROM Sample_Property WHERE Record_ID = " + recID);
			}

			//Create STAGE entry
			if (!request.getParameter("InfStageStart").equals("-")) {
				rs = statement.executeQuery("SELECT Get_Stage_ID(" + request.getParameter("InfStageStart") + ", " + makeDropDownNulls(request.getParameter("InfStartMod")) + ", " + makeDropDownNulls(request.getParameter("InfStageStop")) + ", " + makeDropDownNulls(request.getParameter("InfStopMod")) + ") FROM DUAL");
				rs.next();
				if (rs.getString(1) != null) {
					infStageID = rs.getString(1);
				} else {
					rs = statement.executeQuery("SELECT Stage_Seq.NEXTVAL FROM DUAL");
					rs.next();
					infStageID = rs.getString(1);
					execUp = statement.executeUpdate("INSERT INTO Stage (Stage_ID, Stage_Lower_ID, Stage_Lower_Mod, Stage_Upper_ID, Stage_Upper_Mod) VALUES (" + infStageID + ", " + request.getParameter("InfStageStart") + ", " + makeDropDownNulls(request.getParameter("InfStartMod")) + ", " + makeDropDownNulls(request.getParameter("InfStageStop")) + ", " + makeDropDownNulls(request.getParameter("InfStopMod")) + ")");
				}
			}
			if (!request.getParameter("KnwStageStart").equals("-")) {
				rs = statement.executeQuery("SELECT Get_Stage_ID(" + request.getParameter("KnwStageStart") + ", " + makeDropDownNulls(request.getParameter("KnwStartMod")) + ", " + makeDropDownNulls(request.getParameter("KnwStageStop")) + ", " + makeDropDownNulls(request.getParameter("KnwStopMod")) + ") FROM DUAL");
				rs.next();
				if (rs.getString(1) != null) {
					knwStageID = rs.getString(1);
				} else {
					rs = statement.executeQuery("SELECT Stage_Seq.NEXTVAL FROM DUAL");
					rs.next();
					knwStageID = rs.getString(1);
					execUp = statement.executeUpdate("INSERT INTO Stage (Stage_ID, Stage_Lower_ID, Stage_Lower_Mod, Stage_Upper_ID, Stage_Upper_Mod) VALUES (" + knwStageID + ", " + request.getParameter("KnwStageStart") + ", " + makeDropDownNulls(request.getParameter("KnwStartMod")) + ", " + makeDropDownNulls(request.getParameter("KnwStageStop")) + ", " + makeDropDownNulls(request.getParameter("KnwStopMod")) + ")");
				}
			}

			//Create SAMPLE_PROPERTY entry
			execUp = statement.executeUpdate("INSERT INTO Sample_Property (Record_ID, Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage_ID, Known_Stage_ID, Column_Map, Dip, Dip_Direction, Strike, Facing, Primary_Grainsize_ID, Secondary_Grainsize_ID, Comparator_Used, Bed_Thick_ID, Primary_Bedding_ID, Secondary_Bedding_ID, Weathering_ID, Hardness_ID, Carbonate_ID, Colour_Modifier_ID, Primary_Colour_ID, Secondary_Colour_ID, Wet, Deposition_Env, Rock_Nature, Correspondence) VALUES (" + recID + ", TO_DATE('" + collDate + "'), '" + request.getParameter("DateRnd") + "', " + JspUtils.sqlEscape(request.getParameter("StratName")) + ", '" + request.getParameter("InPlace") + "', " + JspUtils.sqlEscape(request.getParameter("NotColl")) + ", " + JspUtils.sqlEscape(request.getParameter("Sig")) + ", " + JspUtils.sqlEscape(infStageID) + ", " + JspUtils.sqlEscape(knwStageID) + ", " + JspUtils.sqlEscape(request.getParameter("ColMap")) + ", " + JspUtils.sqlEscape(request.getParameter("Dip")) + ", '" + request.getParameter("DipDir") + "', " + JspUtils.sqlEscape(request.getParameter("Strike")) + ", '" + request.getParameter("Facing") + "', " + makeDropDownNulls(request.getParameter("GrainSizeP")) + ", " + makeDropDownNulls(request.getParameter("GrainSizeP")) + ", '" + request.getParameter("GSComp") + "', " + makeDropDownNulls(request.getParameter("BedThick")) + ", " + makeDropDownNulls(request.getParameter("BeddingP")) + ", " + makeDropDownNulls(request.getParameter("BeddingS")) + ", " + makeDropDownNulls(request.getParameter("Weath")) + ", " + makeDropDownNulls(request.getParameter("Hard")) + ", " + makeDropDownNulls(request.getParameter("Carb")) + ", " + makeDropDownNulls(request.getParameter("ColMod")) + ", " + makeDropDownNulls(request.getParameter("ColourP")) + ", " + makeDropDownNulls(request.getParameter("ColourS")) + ", '" + request.getParameter("Wet") + "', " + JspUtils.sqlEscape(depEnv) + ", " + JspUtils.sqlEscape(request.getParameter("RockNat")) + ", " + JspUtils.sqlEscape(request.getParameter("Corr")) + ")");

			//Create COLLECTORS entries
			for (int j = 0; j < collID.length; j++) {
				if (collID[j] > 0) { execUp = statement.executeUpdate("INSERT INTO Collector (Record_ID, Person_ID) VALUES (" + recID + ", " + collID[j] + ")"); }
			}

			//Create SENT TO entries
			for (int j = 0; j < stGroupID.length; j++) {
				if (stGroupID[j] != 0) {
					execUp = statement.executeUpdate("INSERT INTO Sent_To (Record_ID, Fossil_Group_ID, Person_ID, Lab_ID, Comments) VALUES (" + recID + ", " + stGroupID[j] + ", " + JspUtils.sqlEscape(stPersonID[j]) + ", " + JspUtils.sqlEscape(stLabID[j]) + ", " + JspUtils.sqlEscape(stComm[j]) + ")");
				}
			}

			//Create RELATIONSHIP entries
			for (int j = 0; j < prevFeatID.length; j++) {
				if (prevFeatID[j] != 0) {
					execUp = statement.executeUpdate("INSERT INTO Relationship (Record_ID, Relationship_Type, Relation_Type_ID, Related_Feature_ID) VALUES (" + recID + ", 'Sample', 231, " + prevFeatID[j] + ")");
				}
			}
			for (int j = 0; j < srType.length; j++) {
				if (srType[j] != null && srType[j].equals("Samp")) {
					execUp = statement.executeUpdate("INSERT INTO Relationship (Record_ID, Relationship_Type, Relation_Type_ID, Distance, Distance_Range, Distance_Mod, Related_Feature_ID) VALUES (" + recID + ", 'Sample', " + srRelID[j] + ", " + srDist[j] + ", " + JspUtils.sqlEscape(srDistRange[j]) + ", " + JspUtils.sqlEscape(srDistMod[j]) + ", " + srFeatID[j] + ")");
				} else if (srType[j] != null && srType[j].equals("Strat")) { //Strat
					execUp = statement.executeUpdate("INSERT INTO Relationship (Record_ID, Relationship_Type, Relation_Type_ID, Distance, Distance_Range, Distance_Mod, Strat_Unit) VALUES (" + recID + ", 'Strat', " + srRelID[j] + ", " + srDist[j] + ", " + JspUtils.sqlEscape(srDistRange[j]) + ", " + JspUtils.sqlEscape(srDistMod[j]) + ", " + JspUtils.sqlEscape(srStratUnit[j]) + ")");
				}
			}

			//Create SEDIMENTARY FEATURE entries
			for (int j = 0; j < sedFeatID.length; j++) {
				if (sedFeatID[j] != 0) {
					execUp = statement.executeUpdate("INSERT INTO Sedimentary_Feature (Record_ID, Sed_Feature_ID, Abundant) VALUES (" + recID + ", " + sedFeatID[j] + ", " + JspUtils.sqlEscape(abund[j]) + ")");
				}
			}

			if (featStatus.equals("approved")) { //submitted
				//change status & add saved record to folder
				execUp = statement.executeUpdate("UPDATE Audit_Table SET Status = 'approved', Submitted_By_ID = " + userID + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL, Working_Comments = NULL WHERE Audit_ID = " + auditID);
				rs = statement.executeQuery("SELECT * FROM Folder_Content_View WHERE Sample_ID = " + sampID + " AND Folder_ID = " + foldID);
				if (!rs.next()) {
					rs = statement.executeQuery("SELECT Feature_ID FROM Sample WHERE Sample_ID = " + sampID);
					rs.next();
					execUp = statement.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) VALUES (" + foldID + ", " + rs.getString(1) + ")");
				}
			}
			response.sendRedirect("folder_detail.jsp?ID=" + foldID);

		} catch (DataInputException e) {
			out.println("<p><div class='bigheading'>Data Error</div></p>");
			out.println("<table border='0' cellspacing='0'>");
			out.println("<tr><td class='heading'>Problem Field<img src='images/blank.gif' width='20' height='1' /></td><td>" + e.getField() + "</td></tr>");
			out.println("<tr><td class='heading'>Error</td><td>"+ e.getMessage() + "</td></tr>");
			out.println("</table>");
		} catch (BadRightsException e) {
			out.println("<p><div class='bigheading'>Access Denied</div></p>");
			out.println("<p>You do not have sufficient rights to save this record</p>");
		} catch (NullPointerException e) {
			out.println("<p><div class='bigheading'>Error</div></p>");
			out.println("<p>Not all fields recieved</p>");
		}
	}

	out.println("</td></tr></table>");
	drawBottom(out, et);

	statement2.close();
%>