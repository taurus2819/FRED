package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.AccessDeniedException;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Relationship;
import nz.cri.gns.fred.data.SampPropRecord;
import nz.cri.gns.fred.data.SedFeature;
import nz.cri.gns.fred.data.SentTo;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class SampPropRecordDE extends RecordDE {

	private RoundedDate collDate;
	private Vector collectors = new Vector();
	private Vector sentTo = new Vector();
	private Vector prevSamp = new Vector();
	private Vector sampRel = new Vector();
	private Vector stratRel = new Vector();
	
	private boolean outcropSamp = false;

	public SampPropRecordDE(User user, int folderID, PageState state) throws DataInputException, SQLException, IOException {
		super(user, folderID, "SMP", state);
	}

	public SampPropRecordDE(User user, int sampleID, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		super(user, sampleID, folderID, "SMP", state);
	}
	
	public SampPropRecordDE(int recID, User user, PageState state) throws AccessDeniedException, IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		super(recID, user, state);
		record = SampPropRecord.getSampPropData(recID, user, state);
		recordType = "SMP";
		folder = new Folder(record.getAsInt(SampPropRecord.WORKING_FOLDER_ID), user, state);
		setField(COLLECTION_DATE, DataEntryUtils.reverseParseDate(record.getAsDate(SampPropRecord.COLLECTION_DATE), record.getAsString(SampPropRecord.DATE_ROUNDING)));
		if (record.get(SampPropRecord.COLLECTOR) != null) {
			StringBuffer collName = new StringBuffer();
			for (Iterator i = record.getAsVector(SampPropRecord.COLLECTOR).iterator(); i.hasNext(); ) {
				KeyValueObject coll = (KeyValueObject) i.next();
				collName.append(coll.getValue() + "\n");
			}
			setField(COLLECTORS, collName.toString());
		}
		setField(STRAT_NAME, record.getAsString(SampPropRecord.STRAT_UNIT));
		setField(FOSSILS_IN_PLACE, record.getAsString(SampPropRecord.IN_PLACE));
		if (record.get(SampPropRecord.SENT_TO) != null) {
			StringBuffer sTStr = new StringBuffer();
			for (Iterator i = record.getAsVector(SampPropRecord.SENT_TO).iterator(); i.hasNext(); ) {
				SentTo sT = (SentTo) i.next();
				sTStr.append(sT.getFossilGroup() + "*" + FREDUtils.noNulls(sT.getPerson()) + "*" + FREDUtils.noNulls(sT.getLab()) + "*" + FREDUtils.noNulls(sT.getComments()) + "\n");
			}
			setField(SENT_TO, sTStr.toString());
		}
		setField(NOT_COLLECTED, record.getAsString(SampPropRecord.NOT_COLLECTED));
		setField(SIGNIFICANCE_COMMENTS, record.getAsString(SampPropRecord.SIGNIFICANCE));
		setField(INF_AGE_START, record.getAsString(SampPropRecord.INFERRED_STAGE_LOWER_ID));
		setField(INF_START_MOD, record.getAsString(SampPropRecord.INFERRED_STAGE_LOWER_MOD));
		setField(INF_AGE_STOP, record.getAsString(SampPropRecord.INFERRED_STAGE_UPPER_ID));
		setField(INF_STOP_MOD, record.getAsString(SampPropRecord.INFERRED_STAGE_UPPER_MOD));
		setField(PREVIOUS_SAMPLE, record.getAsString(SampPropRecord.RELATIONSHIP_NEARBY));
		if (record.get(SampPropRecord.RELATIONSHIP_NEARBY) != null) {
			StringBuffer prevSamp = new StringBuffer();
			for (Iterator i = record.getAsVector(SampPropRecord.RELATIONSHIP_NEARBY).iterator(); i.hasNext(); ) {
				Relationship rel = (Relationship) i.next();
				prevSamp.append(rel.getRelatedSampleName() + "*");
			}
			setField(PREVIOUS_SAMPLE, prevSamp.toString());	
		}
		if (record.get(SampPropRecord.RELATIONSHIP_SAMPLE) != null) {
			StringBuffer sampRel = new StringBuffer();
			for (Iterator i = record.getAsVector(SampPropRecord.RELATIONSHIP_SAMPLE).iterator(); i.hasNext(); ) {
				Relationship rel = (Relationship) i.next();
				if (rel.getDistance() != null) {
					if (rel.getDistanceMod() != null) sampRel.append("c. ");
					sampRel.append(FREDUtils.noNulls(rel.getDistance().toString()));
					if (rel.getDistanceRange() != null) sampRel.append(" - " + rel.getDistanceRange());
				}
				sampRel.append(" " + rel.getRelationType() + " " + rel.getRelatedSampleName() + "\n");
			}
			setField(SAMPLE_RELATIONSHIP, sampRel.toString());
		}
		if (record.get(SampPropRecord.RELATIONSHIP_STRAT) != null) {
			StringBuffer stratRel = new StringBuffer();
			for (Iterator i = record.getAsVector(SampPropRecord.RELATIONSHIP_STRAT).iterator(); i.hasNext(); ) {
				Relationship rel = (Relationship) i.next();
				if (rel.getDistance() != null) {
					if (rel.getDistanceMod() != null) stratRel.append("c. ");
					stratRel.append(FREDUtils.noNulls(rel.getDistance().toString()));
					if (rel.getDistanceRange() != null) stratRel.append(" - " + rel.getDistanceRange());
				}
				stratRel.append(" " + rel.getRelationType() + " " + rel.getRelatedStratUnit() + "\n");
			}
			setField(STRAT_RELATIONSHIP, stratRel.toString());
		}
		setField(COLUMN_MAP, record.getAsString(SampPropRecord.COLUMN_MAP));
		setField(DIP, record.getAsString(SampPropRecord.DIP));
		setField(DIP_DIRECTION, record.getAsString(SampPropRecord.DIP_DIRECTION));
		setField(STRIKE, record.getAsString(SampPropRecord.STRIKE));
		setField(FACING, record.getAsString(SampPropRecord.FACING));
		setField(GRAIN_SIZE_P, record.getAsString(SampPropRecord.PRIMARY_GRAINSIZE_ID));
		setField(GRAIN_SIZE_S, record.getAsString(SampPropRecord.SECONDARY_GRAINSIZE_ID));
		setField(GS_COMP, record.getAsString(SampPropRecord.COMPARATOR_USED));
		setField(BEDDING_THICKNESS, record.getAsString(SampPropRecord.BED_THICK_ID));
		setField(BEDDING_P, record.getAsString(SampPropRecord.PRIMARY_BEDDING_ID));
		setField(BEDDING_S, record.getAsString(SampPropRecord.SECONDARY_BEDDING_ID));
		setField(WEATHERING, record.getAsString(SampPropRecord.WEATHERING_ID));
		setField(HARDNESS, record.getAsString(SampPropRecord.HARDNESS_ID));
		setField(CARBONATE, record.getAsString(SampPropRecord.CARBONATE_ID));
		setField(COLOUR_MOD, record.getAsString(SampPropRecord.COLOUR_MODIFIER_ID));
		setField(COLOUR_P, record.getAsString(SampPropRecord.PRIMARY_COLOUR_ID));
		setField(COLOUR_S, record.getAsString(SampPropRecord.SECONDARY_COLOUR_ID));
		setField(WET, record.getAsString(SampPropRecord.WET));
		if (record.get(SampPropRecord.SED_FEATURE) != null) {
			StringBuffer sF = new StringBuffer();
			for (Iterator i = record.getAsVector(SampPropRecord.SED_FEATURE).iterator(); i.hasNext(); ) {
				SedFeature sFeat = (SedFeature) i.next();
				sF.append(sFeat.getFeat());
				if (sFeat.getAbundant() != null) sF.append("*");
				sF.append(";");			}
			setField(SED_FEATURES, sF.toString());
		}
		String depEnv = record.getAsString(SampPropRecord.DEPOSITION_ENV);
		if (depEnv != null) {
			if (depEnv.indexOf("Marine:") != -1) {
				setField(DEP_ENVIRONMENT_1, "Marine");
				setField(DEP_ENVIRONMENT_2, depEnv.substring(7, depEnv.length()).trim());
			} else if (depEnv.indexOf("Non-marine:") != -1) {
				setField(DEP_ENVIRONMENT_1, "Non-marine");
				setField(DEP_ENVIRONMENT_2, depEnv.substring(11, depEnv.length()).trim());
			} else {
				setField(DEP_ENVIRONMENT_2, depEnv);
			}
		}
		setField(ROCK_NATURE, record.getAsString(SampPropRecord.ROCK_NATURE));
		setField(CORRESPONDENCE, record.getAsString(SampPropRecord.CORRESPONDENCE));
			
	}

	protected void parseField(int field, String value) throws DataInputException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case COLLECTION_DATE :
					collDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case COLLECTORS :
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1) value = value + "\n";
						rs = conn.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = " + JspUtils.sqlEscape(value.substring(0, value.indexOf("\n")).trim()));
						if (rs.next()) {
							collectors.add(new Integer(rs.getInt(1)));
						} else {  //Collector not in database so throw exception
							throw new DataInputException("Collector", value.substring(0, value.indexOf("\n")).trim() + " not in database - add through builder");
						}
						value = value.substring(value.indexOf("\n") + 1, value.length());
					}
					break;
				case SENT_TO :
					String stLine, stGroup, stPerson, stLab, stComments;
					int stGroupID = 0, stPersonID = 0, stLabID = 0;
					SentTo sT;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1) value = value + "\n";
						stLine = value.substring(0, value.indexOf("\n")).trim();
						stGroup = stLine.substring(0, stLine.indexOf("*"));
						stPerson = stLine.substring(stGroup.length() + 1, stLine.indexOf("*", stGroup.length() + 1));
						stLab = stLine.substring(stGroup.length() + stPerson.length() + 2, stLine.indexOf("*", stGroup.length() + stPerson.length() + 2));
						stComments = stLine.substring(stLine.lastIndexOf("*") + 1, stLine.length());
						value = value.substring(value.indexOf("\n") + 1, value.length()).trim();
						//check againt lookup values
						rs = conn.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = " + JspUtils.sqlEscape(stGroup) + " AND FieldName = 'FossilGroup'");
						if (rs.next()) {
							stGroupID = rs.getInt(1);
						} else {  // not valid group
							throw new DataInputException("Sent To - Group", stGroup + " not a valid sent to group");
						}
						if (!stPerson.equals("")) {
							rs = conn.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = " + JspUtils.sqlEscape(stPerson));
							if (rs.next()) {
								stPersonID = rs.getInt(1);
							} else { // not valid person
								throw new DataInputException("Sent To - Person", stPerson + " not in database - add through builder");
							}
						}
						if (!stLab.equals("")) {
							rs = conn.executeQuery("SELECT Lab_ID FROM SC.Lab WHERE Lab_Name = " + JspUtils.sqlEscape(stLab));
							if (rs.next()) {
								stLabID = rs.getInt(1);
							} else { // not valid lab
								throw new DataInputException("Sent To - Lab", stLab + " not in database");
							}
						}
						sT = new SentTo();
						sT.setComments(stComments);
						sT.setFossilGroupId(new Integer(stGroupID));
						sT.setPersonId(new Integer(stPersonID));
						sT.setLabId(new Integer(stLabID));
						sentTo.add(sT);
					}
					break;
				case INF_AGE_START :
					parseAge(value, getField(INF_AGE_STOP), "Inferred Age");
					break;
				case INF_AGE_STOP :
					parseAge(getField(INF_AGE_START), value, "Inferred Age");
					break;
				case INF_START_MOD :
				case INF_STOP_MOD :
					if (value != null && !value.equals("?")) throw new DataInputException("Inferred Age", "Bad Modifier");
					break;
				case KNW_AGE_START :
					parseAge(value, getField(KNW_AGE_STOP), "Known Age");
					break;
				case KNW_AGE_STOP :
					parseAge(getField(KNW_AGE_START), value, "Known Age");
					break;
				case KNW_START_MOD :
				case KNW_STOP_MOD :
					if (value != null && !value.equals("?")) throw new DataInputException("Known Age", "Bad Modifier");
					break;
				case PREVIOUS_SAMPLE :
					Relationship ps;
					while (value.length() > 0) {
						if (value.indexOf("*") == -1) value += "*";
						rs = conn.executeQuery("SELECT Feature_ID FROM Feature_Security_View WHERE Sample_Name = " + JspUtils.sqlEscape(value.substring(0, value.indexOf(";")).trim()) + " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = " + user.getPersonId() + "))");
						if (rs.next()) {
							ps = new Relationship();
							ps.setRelatedFeatureId(new Integer(rs.getInt(1)));
							prevSamp.add(ps);
						} else {  //Sample not in database so throw exception
							throw new DataInputException("Samples Nearby", value.substring(0, value.indexOf(";")).trim() + " not in database - pick another");
						}
						value = value.substring(value.indexOf(";") + 1, value.length());
					}
					break;
				case SAMPLE_RELATIONSHIP :
					String srLine, srRel, srDistance, srDistMod, srDistRange, srFeat;
					Integer srFeatID;
					Relationship smpR;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1) value += "\n";
						srLine = value.substring(0, value.indexOf("\n")).trim();
						if (srLine.indexOf("above") >= 0) {
							srRel = "above";
							srDistance = srLine.substring(0, srLine.indexOf("above")).trim();
							srFeat = srLine.substring(srLine.indexOf("above") + 5, srLine.length()).trim();
						} else if (srLine.indexOf("below") >= 0) {
							srRel = "below";
							srDistance = srLine.substring(0, srLine.indexOf("below")).trim();
							srFeat = srLine.substring(srLine.indexOf("below") + 5, srLine.length()).trim();
						} else {
							System.out.println(srLine);
							throw new DataInputException("Sample Relationships", srLine + " invalid.  Please use the builder");
						}
						srDistMod = null;
						srDistRange = null;
						if (srDistance.indexOf("c.") == 0) {
							srDistMod = "c.";
							srDistance = srDistance.substring(2, srDistance.length()).trim();
						}
						if (srDistance.indexOf("-") >= 0) {
							srDistance = srDistance.substring(0, srDistance.indexOf("-")).trim();
							srDistRange = srDistance.substring(srDistance.indexOf("-") + 1, srDistance.length()).trim();
						}
						srDistance = srDistance.trim();
						rs = conn.executeQuery("SELECT Feature_ID FROM Feature_Security_View WHERE Sample_Name = " + JspUtils.sqlEscape(srFeat) + " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = " + user.getPersonId() + "))");
						if (rs.next()) {
							srFeatID = new Integer(rs.getInt(1));
						} else {
							throw new DataInputException("Sample Relationships", srFeat + " not a valid sample");
						}
						try {
							smpR = new Relationship();
							smpR.setRelatedFeatureId(srFeatID);
							smpR.setRelationType(srRel);
							if (!srDistance.equals("")) smpR.setDistance(new Double(srDistance));
							smpR.setDistanceMod(srDistMod);
							if (srDistRange != null) smpR.setDistanceRange(new Double(srDistRange));
						} catch (Exception e) {
							throw new DataInputException("Sample Relationships", srLine + " is invalid.  Please use the builder");
						}
						value = value.substring(value.indexOf("\n") + 1, value.length()).trim();
					}
					break;
				case STRAT_RELATIONSHIP :
					String strLine, strRel, strDistance = "", strDistMod, strDistRange, strStrat;
					Relationship strR;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1) { value = value + "\n"; }
						strLine = value.substring(0, value.indexOf("\n")).trim();
						if (strLine.indexOf("above base") >= 0) {
							strRel = "above base";
							strDistance = strLine.substring(0, strLine.indexOf("above base")).trim();
							strStrat = strLine.substring(strLine.indexOf("above base") + 10, strLine.length()).trim();
						} else if (strLine.indexOf("above top") >= 0) {
							strRel = "above top";
							strDistance = strLine.substring(0, strLine.indexOf("above top")).trim();
							strStrat = strLine.substring(strLine.indexOf("above top") + 9, strLine.length()).trim();
						} else if (strLine.indexOf("below base") >= 0) {
							strRel = "below base";
							strDistance = strLine.substring(0, strLine.indexOf("below base")).trim();
							strStrat = strLine.substring(strLine.indexOf("below base") + 10, strLine.length()).trim();
						} else if (strLine.indexOf("below top") >= 0) {
							strRel = "below top";
							strDistance = strLine.substring(0, strLine.indexOf("below top")).trim();
							strStrat = strLine.substring(strLine.indexOf("below top") + 9, strLine.length()).trim();
						} else {
							throw new DataInputException("Stratigraphic Relationships", strLine + " not a valid entry.  Please use the builder");
						}
						strDistMod = null;
						strDistRange = null;
						if (strDistance.indexOf("c.") == 0) {
							strDistMod = "c.";
							strDistance = strDistance.substring(2, strDistance.length()).trim();
						}
						if (strDistance.indexOf("-") >= 0) {
							strDistance = strDistance.substring(0, strDistance.indexOf("-")).trim();
							strDistRange = strDistance.substring(strDistance.indexOf("-") + 1, strDistance.length()).trim();
						}
						strDistance = strDistance.trim();
						try {
							strR = new Relationship();
							strR.setRelatedStratUnit(strStrat.trim());
							strR.setRelationType(strRel);
							if (!strDistance.equals("")) strR.setDistance(new Double(strDistance));
							strR.setDistanceMod(strDistMod);
							if (strDistRange != null) strR.setDistanceRange(new Double(strDistRange));
						} catch (Exception e) {
							throw new DataInputException("Stratigraphic Relationships", strLine + " is invalid.  Please use the builder");
						}
						value = value.substring(value.indexOf("\n") + 1, value.length()).trim();
					}
					break;
				case DIP :
					if (!FREDUtils.isNumeric(value) || Integer.parseInt(value) < 0 || Integer.parseInt(value) > 90)
						throw new DataInputException("Dip", value + " is not valid.  Dip must be numeric and between 0 and 90");
					break;
				case DIP_DIRECTION :
					if (!(value.equals("N") || value.equals("NE") || value.equals("E") || value.equals("SE") || value.equals("S") || value.equals("SW") || value.equals("W") || value.equals("NW")))
						throw new DataInputException("Dip Direction", value + " is not a valid option");
				case STRIKE :
					if (!FREDUtils.isNumeric(value) || Integer.parseInt(value) < 0 || Integer.parseInt(value) > 360)
						throw new DataInputException("Strike", value + " is not valid.  Strike must be numeric and between 0 and 360");
					break;
				case FACING :
					if (!(value.equals("Normal") || value.equals("Overturned")))
						throw new DataInputException("Facing", value + " is not a valid option");
					break;
				case GRAIN_SIZE_P :
				case GRAIN_SIZE_S :
					DataEntryUtils.parseDropDownID("Grainsize", "SELECT * FROM Lookup WHERE Lookup_ID = " + value + " AND FieldName = 'GrainSize'", state);
					break;
				case GS_COMP :
					if (!(value.equals("Y") || value.equals("N")))
						throw new DataInputException("GS Comparator", value + " is not a valid option");
					break;
				case BEDDING_THICKNESS :
					DataEntryUtils.parseDropDownID("Bedding Thickness", "SELECT * FROM Lookup WHERE Lookup_ID = " + value + " AND FieldName = 'BedThick'", state);
					break;
				case BEDDING_P :
				case BEDDING_S :
					DataEntryUtils.parseDropDownID("Bedding", "SELECT * FROM Lookup WHERE Lookup_ID = " + value + " AND FieldName = 'Bedding'", state);
					break;
				case WEATHERING :
					DataEntryUtils.parseDropDownID("Weathering", "SELECT * FROM Lookup WHERE Lookup_ID = " + value + " AND FieldName = 'Weathering'", state);
					break;
				case HARDNESS :
					DataEntryUtils.parseDropDownID("Hardness", "SELECT * FROM Lookup WHERE Lookup_ID = " + value + " AND FieldName = 'Hardness'", state);
					break;
				case CARBONATE :
					DataEntryUtils.parseDropDownID("Carbonate", "SELECT * FROM Lookup WHERE Lookup_ID = " + value + " AND FieldName = 'Carbonate'", state);
					break;
				case COLOUR_MOD :
					DataEntryUtils.parseDropDownID("Shade", "SELECT * FROM Lookup WHERE Lookup_ID = " + value + " AND FieldName = 'ColourMod'", state);
					break;
				case COLOUR_P :
				case COLOUR_S :
					DataEntryUtils.parseDropDownID("Colour", "SELECT * FROM Lookup WHERE Lookup_ID = " + value + " AND FieldName = 'RockColour'", state);
					break;
				case WET :
					if (!(value.equals("Wet") || value.equals("Dry")))
						throw new DataInputException("Wet", value + " is not a valid option");
					break;
				case DEP_ENVIRONMENT_1 :
					if (!(value.equals("Marine") || value.equals("Non-marine")))
						throw new DataInputException("Deposition Environment", value + " is not a valid option");
					break;
			}

		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException e) {
			throw new DataInputException();	
		}
	}

	public void setOutcropSamp(boolean outcropSamp) {
		this.outcropSamp = outcropSamp;
	}

	public void makeDataEntryHTML(Writer out) throws IOException, SQLException {
		ComboDescriptor cd;
		DBConnection conn = FREDUtils.getFREDConnection(state);
		if (!outcropSamp) super.makeDataEntryHTML(out);
		out.write("<tr><td class='heading'>Collection Date</td><td></td><td><input type='text' name='CollDate' value='" + FREDUtils.noNulls(getField(COLLECTION_DATE)) + "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=CollDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Collectors</td><td></td><td><textarea name='Coll' cols='40' rows='2'>" + FREDUtils.noNulls(getField(COLLECTORS)) + "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Coll\", \"Supp\", \"width=600,height=400\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Strat Name</td><td></td><td><input type='text' name='StratName' size='40' value='" + FREDUtils.noNulls(getField(STRAT_NAME)) + "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=StratName\", \"Supp\", \"width=600,height=300\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Fossils In Place</td><td></td><td><select name='InPlace'><option value='' " + ((getField(FOSSILS_IN_PLACE) == null) ? " selected" : "") + ">-- Choose --</option><option value='Yes' " + ((getField(FOSSILS_IN_PLACE) != null && getField(FOSSILS_IN_PLACE).equals("Yes")) ? " selected" : "") + ">Yes</option><option value='Almost' " + ((getField(FOSSILS_IN_PLACE) != null && getField(FOSSILS_IN_PLACE).equals("Almost")) ? " selected" : "") + ">Almost</option><option value='No' " + ((getField(FOSSILS_IN_PLACE) != null && getField(FOSSILS_IN_PLACE).equals("No")) ? " selected" : "") + ">No</option><option value='Unknown' " + ((getField(FOSSILS_IN_PLACE) != null && getField(FOSSILS_IN_PLACE).equals("Unknown")) ? " selected" : "") + ">Unknown</option></select></td></tr>\n");
		out.write("<tr><td class='heading'>Sent To</td><td></td><td><textarea name='SentTo' cols='40' rows='2'>" + FREDUtils.noNulls(getField(SENT_TO)) + "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SentTo\", \"Supp\",\"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Not Collected<br><span class='smalltext'>specify fossils seen but not collected</span></td><td></td><td><textarea name='NotColl' cols='40' rows='3'>" + FREDUtils.noNulls(getField(NOT_COLLECTED)) + "</textarea></td></tr>\n");
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
		
		out.write("<tr><td class='heading' colspan='2'>Significance/Comments</td><td><textarea name='Sig' cols='40' rows='3'>" + FREDUtils.noNulls(getField(SIGNIFICANCE_COMMENTS)) + "</textarea></td></tr>\n");
		out.write("<tr><td class='heading'>Stage Limits</td><td class='smallheading'>Inferred</td><td>\n");
		out.write("<table border='0' cellspacing='0'><tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "InfStageStart";
		cd.prompt = "-- Choose --";
		cd.selected = getField(INF_AGE_START);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td><td><select name='InfStartMod'><option value='-' " + ((getField(INF_START_MOD) == null) ? " selected" : "") + "></option><option value='?' " + ((getField(INF_START_MOD) != null && getField(INF_START_MOD).equals("?")) ? " selected" : "") + ">?</option></select></td><td class='heading'> to </td></tr>\n");
		out.write("<tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "InfStageStop";
		cd.prompt = "-- Choose --";
		cd.selected = getField(INF_AGE_STOP);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td><td class='heading'><select name='InfStopMod'><option value='-' " + ((getField(INF_STOP_MOD) == null) ? " selected" : "") + "></option><option value='?' " + ((getField(INF_STOP_MOD) != null && getField(INF_STOP_MOD).equals("?")) ? " selected" : "") + ">?</option></select></td></tr>\n");
		out.write("</table></td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Known</td><td>\n");
		out.write("<table border='0' cellspacing='0'><tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "KnwStageStart";
		cd.prompt = "-- Choose --";
		cd.selected = getField(KNW_AGE_START);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td><td><select name='KnwStartMod'><option value='-' " + ((getField(KNW_START_MOD) == null) ? " selected" : "") + "></option><option value='?' " + ((getField(KNW_START_MOD) != null && getField(KNW_START_MOD).equals("?")) ? " selected" : "") + ">?</option></select></td><td class='heading'> to </td></tr>\n");
		out.write("<tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "KnwStageStop";
		cd.prompt = "-- Choose --";
		cd.selected = getField(KNW_AGE_STOP);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td><td class='heading'><select name='KnwStopMod'><option value='-' " + ((getField(KNW_STOP_MOD) == null) ? " selected" : "") + "></option><option value='?' " + ((getField(KNW_STOP_MOD) != null && getField(KNW_STOP_MOD).equals("?")) ? " selected" : "") + ">?</option></select></td></tr>\n");
		out.write("</table></td></tr>\n");
		out.write("<tr><td class='heading'>Samples Nearby</td><td></td><td><input type='text' name='PrevSamp' size='40' value='" + FREDUtils.noNulls(getField(PREVIOUS_SAMPLE)) + "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=PrevSamp\", \"Supp\", \"width=600,height=350\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Sample Relationships</td><td><textarea name='SampRel' cols='40' rows='3'>" + FREDUtils.noNulls(getField(SAMPLE_RELATIONSHIP)) + "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SampRel\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Stratigraphic Relationships</td><td><textarea name='StratRel' cols='40' rows='3'>" + FREDUtils.noNulls(getField(STRAT_RELATIONSHIP)) + "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=StratRel\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Column/Map</td><td></td><td><input type='text' name='ColMap' size='40' value='" + FREDUtils.noNulls(getField(COLUMN_MAP)) + "'></td></tr>\n");
		out.write("<tr><td class='heading'>Attitude</td><td class='smallheading'>Dip</td><td><input type='text' name='Dip' size='3' value='" + FREDUtils.noNulls(getField(DIP)) + "'></td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Dip Dirn.</td><td><select name='DipDir'>\n<option value='' " + ((getField(DIP_DIRECTION) == null) ? " selected" : "") + ">-- Choose --</option>\n<option value='N' " + ((getField(DIP_DIRECTION) != null && getField(DIP_DIRECTION).equals("N")) ? " selected" : "") + ">North</option>\n<option value='NE' " + ((getField(DIP_DIRECTION) != null && getField(DIP_DIRECTION).equals("NE")) ? " selected" : "") + ">North-East</option>\n<option value='E' " + ((getField(DIP_DIRECTION) != null && getField(DIP_DIRECTION).equals("E")) ? " selected" : "") + ">East</option>\n<option value='SE' " + ((getField(DIP_DIRECTION) != null && getField(DIP_DIRECTION).equals("SE")) ? " selected" : "") + ">South-East</option><option value='S' " + ((getField(DIP_DIRECTION) != null && getField(DIP_DIRECTION).equals("S")) ? " selected" : "") + ">South</option>\n<option value='SW' " + ((getField(DIP_DIRECTION) != null && getField(DIP_DIRECTION).equals("SW")) ? " selected" : "") + ">South-West</option>\n<option value='W' " + ((getField(DIP_DIRECTION) != null && getField(DIP_DIRECTION).equals("W")) ? " selected" : "") + ">West</option>\n<option value='NW' " + ((getField(DIP_DIRECTION) != null && getField(DIP_DIRECTION).equals("NW")) ? " selected" : "") + ">North-West</option>\n</select></td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Strike</td><td><input type='text' name='Strike' size='4' value='" + FREDUtils.noNulls(getField(STRIKE)) + "'></td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Facing</td><td><select name='Facing'><option value='' " + ((getField(FACING) == null) ? " selected" : "") + ">-- Choose --</option><option value='Normal' " + ((getField(FACING) != null && getField(FACING).equals("Normal")) ? " selected" : "") + ">Normal</option><option value='Overturned' " + ((getField(FACING) != null &&getField(FACING).equals("Overturned")) ? " selected" : "") + ">Overturned</option></select></td></tr>\n");
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");
		
		out.write("<tr><td class='heading'>Grain Size</td><td class='smallheading'>Pri.</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "GrainSizeP";
		cd.prompt = "-- Choose --";
		cd.selected = getField(GRAIN_SIZE_P);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'GrainSize'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Sec.</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "GrainSizeS";
		cd.prompt = "-- Choose --";
		cd.selected = getField(GRAIN_SIZE_S);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'GrainSize'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Comp. Used</td><td><select name='GSComp'><option value='' " + ((getField(GS_COMP) == null) ? " selected" : "") + ">-- Choose --</option><option value='Y' " + ((getField(GS_COMP) != null && getField(GS_COMP).equals("Y")) ? " selected" : "") + ">Yes</option><option value='N' " + ((getField(GS_COMP) != null && getField(GS_COMP).equals("N")) ? " selected" : "") + ">No</option></select></td></tr>\n");
		out.write("<tr><td class='heading'>Stratification</td><td class='smallheading'>Thickness</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "BedThick";
		cd.prompt = "-- Choose --";
		cd.selected = getField(BEDDING_THICKNESS);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'BedThick'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Features</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "BeddingP";
		cd.prompt = "-- Choose --";
		cd.selected = getField(BEDDING_P);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Bedding'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("&nbsp;<span class='heading'>&</span>&nbsp;");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "BeddingS";
		cd.prompt = "-- Choose --";
		cd.selected = getField(BEDDING_S);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Bedding'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Weathering</td><td></td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "Weath";
		cd.prompt = "-- Choose --";
		cd.selected = getField(WEATHERING);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Weathering'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Hardness</td><td></td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "Hard";
		cd.prompt = "-- Choose --";
		cd.selected = getField(HARDNESS);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Hardness'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Carbonate</td><td></td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "Carb";
		cd.prompt = "-- Choose --";
		cd.selected = getField(CARBONATE);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Carbonate'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Colour</td><td class='smallheading'>Shade</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "ColMod";
		cd.prompt = "-- Choose --";
		cd.selected = getField(COLOUR_MOD);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'ColourMod'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Colour</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "ColourP";
		cd.prompt = "-- Choose --";
		cd.selected = getField(COLOUR_P);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'RockColour'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("&nbsp;<span class='heading'>-</span>&nbsp;");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "ColourS";
		cd.prompt = "-- Choose --";
		cd.selected = getField(COLOUR_S);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'RockColour'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Wet/Dry</td><td><select name='Wet'><option value='' " + ((getField(WET) == null) ? " selected" : "") + ">-- Choose --</option><option value='Wet' " + ((getField(WET) != null && getField(WET).equals("Wet")) ? " selected" : "") + ">Wet</option><option value='Dry' " + ((getField(WET) != null && getField(WET).equals("Dry")) ? " selected" : "") + ">Dry</option></select></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Additional Features</td><td><input type='text' name='SedFeat' size='40' value='" + FREDUtils.noNulls(getField(SED_FEATURES)) + "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SedFeat\", \"Supp\", \"width=600,height=350\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Inferred Environment</td><td><select name='DepEnv1'><option value='' " + ((getField(DEP_ENVIRONMENT_1) == null) ? " selected" : "") + ">-- Choose --</option><option value='Marine' " + ((getField(DEP_ENVIRONMENT_1) != null && getField(DEP_ENVIRONMENT_1).equals("marine")) ? " selected" : "") + ">Marine</option><option value='Non-marine' " + ((getField(DEP_ENVIRONMENT_1) != null && getField(DEP_ENVIRONMENT_1).equals("Non-marine")) ? " selected" : "") + ">Non-marine</option></select></td></tr>\n");
		out.write("<tr><td></td><td></td><td><textarea name='DepEnv2' cols='40' rows='3'>" + FREDUtils.noNulls(getField(DEP_ENVIRONMENT_2)) + "</textarea></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Nature of Rock Unit</td><td><textarea name='RockNat' cols='40' rows='3'>" + FREDUtils.noNulls(getField(ROCK_NATURE)) + "</textarea></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Correspondence</td><td><textarea name='Corr' cols='40' rows='3'>" + FREDUtils.noNulls(getField(CORRESPONDENCE)) + "</textarea></td></tr>\n");
		if (!outcropSamp) super.makeEndBitHTML(out);
	}

	public int save() throws InvalidCredentialsException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				conn.executeUpdate("DELETE FROM Sample_Property WHERE Record_ID = " + record.getRecordID());
				conn.executeUpdate("INSERT INTO Sample_Property (Record_ID, Collection_Date, Date_Rounding) VALUES (" + record.getRecordID() + ", TO_DATE('" + collDate.getDateString() + "'), " + JspUtils.sqlEscape(collDate.getDateRounding()) + ")");
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				try {
					record = SampPropRecord.getSampPropData(record.getRecordID(), user, state, true);
				} catch (Exception e) {}
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new SQLException();
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new IOException();
			} catch (InvalidCredentialsException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new InvalidCredentialsException();
			}
		}
		return record.getRecordID();
	}

}
