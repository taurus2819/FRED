package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.PrintWriter;
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
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.fred.data.Relationship;
import nz.cri.gns.fred.data.SampPropRecord;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.fred.data.SedFeature;
import nz.cri.gns.fred.data.SentTo;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class SampPropRecordDE extends RecordDE {

	private RoundedDate collDate;
	private Vector collectors;
	private String depEnv;

	private boolean outcropSamp = false;
	private Vector prevSamp;
	private Vector sampRel;
	private Vector sedFeat;
	private Vector sentTo;
	private Vector stratRel;

	public SampPropRecordDE(User user, int sampleID, int folderID, PageState state)
		throws SQLException, IOException, DataInputException {
		super(user, sampleID, folderID, "SMP", state);
	}

	public SampPropRecordDE(User user, int folderID, PageState state)
		throws DataInputException, SQLException, IOException {
		super(user, folderID, "SMP", state);
	}

	public SampPropRecordDE(int recID, User user, PageState state)
		throws IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		super(recID, "SMP", user, state);
		try {
			setField(
				COLLECTION_DATE,
				DataEntryUtils.reverseParseDate(
					record.getAsDate(Record.COLLECTION_DATE),
					record.getAsString(Record.COLLECTION_DATE_ROUNDING)));
			if (record.get(Record.COLLECTOR) != null) {
				StringBuffer collName = new StringBuffer();
				for (Iterator i =
					record.getAsVector(Record.COLLECTOR).iterator();
					i.hasNext();
					) {
					KeyValueObject coll = (KeyValueObject) i.next();
					collName.append(coll.getValue() + "\n");
				}
				setField(COLLECTORS, collName.toString());
			}
			setField(STRAT_NAME, record.getAsString(Record.STRAT_UNIT));
			setField(FOSSILS_IN_PLACE, record.getAsString(Record.IN_PLACE));
			if (record.get(Record.SENT_TO) != null) {
				StringBuffer sTStr = new StringBuffer();
				for (Iterator i =
					record.getAsVector(Record.SENT_TO).iterator();
					i.hasNext();
					) {
					SentTo sT = (SentTo) i.next();
					sTStr.append(
						sT.getFossilGroup()
							+ "*"
							+ FREDUtils.noNulls(sT.getPerson())
							+ "*"
							+ FREDUtils.noNulls(sT.getLab())
							+ "*"
							+ FREDUtils.noNulls(sT.getComments())
							+ "\n");
				}
				setField(SENT_TO, sTStr.toString());
			}
			setField(
				NOT_COLLECTED,
				record.getAsString(Record.NOT_COLLECTED));
			setField(
				SIGNIFICANCE_COMMENTS,
				record.getAsString(Record.SIGNIFICANCE));
			setField(
				INF_AGE_START,
				record.getAsString(Record.INFERRED_STAGE_LOWER_ID));
			setField(
				INF_START_MOD,
				record.getAsString(Record.INFERRED_STAGE_LOWER_MOD));
			setField(
				INF_AGE_STOP,
				record.getAsString(Record.INFERRED_STAGE_UPPER_ID));
			setField(
				INF_STOP_MOD,
				record.getAsString(Record.INFERRED_STAGE_UPPER_MOD));
			setField(
				KNW_AGE_START,
				record.getAsString(Record.KNOWN_STAGE_LOWER_ID));
			setField(
				KNW_START_MOD,
				record.getAsString(Record.KNOWN_STAGE_LOWER_MOD));
			setField(
				KNW_AGE_STOP,
				record.getAsString(Record.KNOWN_STAGE_UPPER_ID));
			setField(
				KNW_STOP_MOD,
				record.getAsString(Record.KNOWN_STAGE_UPPER_MOD));
			if (record.get(Record.RELATIONSHIP_NEARBY) != null) {
				StringBuffer prevSamp = new StringBuffer();
				for (Iterator i =
					record
						.getAsVector(Record.RELATIONSHIP_NEARBY)
						.iterator();
					i.hasNext();
					) {
					Relationship rel = (Relationship) i.next();
					prevSamp.append(rel.getRelatedSampleName() + ";");
				}
				setField(PREVIOUS_SAMPLE, prevSamp.toString());
			}
			if (record.get(Record.RELATIONSHIP_SAMPLE) != null) {
				StringBuffer sampRel = new StringBuffer();
				for (Iterator i =
					record
						.getAsVector(Record.RELATIONSHIP_SAMPLE)
						.iterator();
					i.hasNext();
					) {
					Relationship rel = (Relationship) i.next();
					if (rel.getDistanceMod() != null)
						sampRel.append(rel.getDistanceMod() + " ");
					if (rel.getDistance() != null) {
						sampRel.append(FREDUtils.noNulls(rel.getDistance()));
						if (rel.getDistanceRange() != null)
							sampRel.append(" - " + rel.getDistanceRange());
					}
					sampRel.append(
						" "
							+ rel.getRelationType()
							+ " "
							+ rel.getRelatedSampleName()
							+ "\n");
				}
				setField(SAMPLE_RELATIONSHIP, sampRel.toString());
			}
			if (record.get(Record.RELATIONSHIP_STRAT) != null) {
				StringBuffer stratRel = new StringBuffer();
				for (Iterator i =
					record
						.getAsVector(Record.RELATIONSHIP_STRAT)
						.iterator();
					i.hasNext();
					) {
					Relationship rel = (Relationship) i.next();
					if (rel.getDistanceMod() != null)
						stratRel.append(rel.getDistanceMod() + " ");
					if (rel.getDistance() != null) {
						stratRel.append(FREDUtils.noNulls(rel.getDistance()));
						if (rel.getDistanceRange() != null)
							stratRel.append(" - " + rel.getDistanceRange());
					}
					stratRel.append(
						" "
							+ rel.getRelationType()
							+ " "
							+ rel.getRelatedStratUnit()
							+ "\n");
				}
				setField(STRAT_RELATIONSHIP, stratRel.toString());
			}
			setField(COLUMN_MAP, record.getAsString(Record.COLUMN_MAP));
			setField(DIP, record.getAsString(Record.DIP));
			setField(
				DIP_DIRECTION,
				record.getAsString(Record.DIP_DIRECTION));
			setField(STRIKE, record.getAsString(Record.STRIKE));
			setField(FACING, record.getAsString(Record.FACING));
			setField(
				GRAIN_SIZE_P,
				record.getAsString(Record.PRIMARY_GRAINSIZE_ID));
			setField(
				GRAIN_SIZE_S,
				record.getAsString(Record.SECONDARY_GRAINSIZE_ID));
			setField(GS_COMP, record.getAsString(Record.COMPARATOR_USED));
			setField(
				BEDDING_THICKNESS,
				record.getAsString(Record.BED_THICK_ID));
			setField(
				BEDDING_P,
				record.getAsString(Record.PRIMARY_BEDDING_ID));
			setField(
				BEDDING_S,
				record.getAsString(Record.SECONDARY_BEDDING_ID));
			setField(WEATHERING, record.getAsString(Record.WEATHERING_ID));
			setField(HARDNESS, record.getAsString(Record.HARDNESS_ID));
			setField(CARBONATE, record.getAsString(Record.CARBONATE_ID));
			setField(
				COLOUR_MOD,
				record.getAsString(Record.COLOUR_MODIFIER_ID));
			setField(
				COLOUR_P,
				record.getAsString(Record.PRIMARY_COLOUR_ID));
			setField(
				COLOUR_S,
				record.getAsString(Record.SECONDARY_COLOUR_ID));
			setField(WET, record.getAsString(Record.WET));
			if (record.get(Record.SED_FEATURE) != null) {
				StringBuffer sF = new StringBuffer();
				for (Iterator i =
					record.getAsVector(Record.SED_FEATURE).iterator();
					i.hasNext();
					) {
					SedFeature sFeat = (SedFeature) i.next();
					sF.append(sFeat.getFeat());
					if (sFeat.getAbundant() != null)
						sF.append("*");
					sF.append(";");
				}
				setField(SED_FEATURES, sF.toString());
			}
			String depEnv = record.getAsString(Record.DEPOSITION_ENV);
			if (depEnv != null) {
				if (depEnv.indexOf("Marine:") != -1) {
					setField(DEP_ENVIRONMENT_1, "Marine");
					setField(
						DEP_ENVIRONMENT_2,
						depEnv.substring(7, depEnv.length()).trim());
				} else if (depEnv.indexOf("Non-marine:") != -1) {
					setField(DEP_ENVIRONMENT_1, "Non-marine");
					setField(
						DEP_ENVIRONMENT_2,
						depEnv.substring(11, depEnv.length()).trim());
				} else {
					setField(DEP_ENVIRONMENT_2, depEnv);
				}
			}
			setField(ROCK_NATURE, record.getAsString(Record.ROCK_NATURE));
			setField(CORRESPONDENCE, record.getAsString(Record.CORRESPONDENCE));
		} catch (TaxonomicListException e) {}
	}

	public void setOutcropSamp(boolean outcropSamp) {
		this.outcropSamp = outcropSamp;
	}

	protected void parseField(int field, String value)
		throws DataInputException, TaxonomicListException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case COLLECTION_DATE :
					collDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case COLLECTORS :
					collectors = new Vector();
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1)
							value = value + "\n";
						rs = conn.executeQuery(
								"SELECT Person_ID FROM Person_View WHERE Name = "
									+ JspUtils.sqlEscape(value.substring(0, value.indexOf("\n")).trim()));
						try {
							rs.next();
							collectors.add(new Integer(rs.getInt(1)));
						} catch (Exception e) {
							throw new DataInputException(
								"Collector",
								value.substring(0, value.indexOf("\n")).trim()
									+ " not in database - add through builder");
						}
						value =	value.substring(value.indexOf("\n") + 1, value.length());
					}
					break;
				case SENT_TO :
					sentTo = new Vector();
					String stLine, stGroup, stPerson, stLab, stComments;
					Integer stGroupID, stPersonID = null, stLabID = null;
					SentTo sT;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1)
							value = value + "\n";
						stLine = value.substring(0, value.indexOf("\n")).trim();
						stGroup = stLine.substring(0, stLine.indexOf("*"));
						stPerson = stLine.substring(stGroup.length() + 1, stLine.indexOf("*", stGroup.length() + 1));
						stLab =	stLine.substring(stGroup.length() + stPerson.length() + 2, stLine.indexOf("*", stGroup.length() + stPerson.length() + 2));
						stComments = stLine.substring(stLine.lastIndexOf("*") + 1, stLine.length());
						//check againt lookup values
						rs = conn.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = "	+ JspUtils.sqlEscape(stGroup) + " AND FieldName = 'FossilGroup'");
						try {
							rs.next();
							stGroupID = new Integer(rs.getInt(1));
						} catch (Exception e) {
							throw new DataInputException("Sent To - Group",	stGroup + " not a valid sent to group");
						}
						if (!stPerson.equals("")) {
							rs = conn.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = " + JspUtils.sqlEscape(stPerson));
							try {
								rs.next();
								stPersonID = new Integer(rs.getInt(1));
							} catch (Exception e) {
								throw new DataInputException("Sent To - Person", stPerson + " not in database - add through builder");
							}
						}
						if (!stLab.equals("")) {
							rs = conn.executeQuery("SELECT Lab_ID FROM SC.Lab WHERE Lab_Name = " + JspUtils.sqlEscape(stLab));
							try {
								rs.next();
								stLabID = new Integer(rs.getInt(1));
							} catch (Exception e) {
								throw new DataInputException("Sent To - Lab", stLab + " not in database");
							}
						}
						sT = new SentTo();
						sT.setComments(stComments);
						sT.setFossilGroupID(stGroupID);
						sT.setPersonID(stPersonID);
						sT.setLabID(stLabID);
						sentTo.add(sT);
						value = value.substring(value.indexOf("\n") + 1, value.length()).trim();
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
					if (value != null && !value.equals("?"))
						throw new DataInputException(
							"Inferred Age",
							"Bad Modifier");
					break;
				case KNW_AGE_START :
					parseAge(value, getField(KNW_AGE_STOP), "Known Age");
					break;
				case KNW_AGE_STOP :
					parseAge(getField(KNW_AGE_START), value, "Known Age");
					break;
				case KNW_START_MOD :
				case KNW_STOP_MOD :
					if (value != null && !value.equals("?"))
						throw new DataInputException(
							"Known Age",
							"Bad Modifier");
					break;
				case PREVIOUS_SAMPLE :
					prevSamp = new Vector();
					Relationship ps;
					while (value.length() > 0) {
						if (value.indexOf(";") == -1)
							value += ";";
						String sampName =
							value.substring(0, value.indexOf(";")).trim();
						rs =
							conn.executeQuery(
								"SELECT Feature_ID FROM Sample_All_View WHERE Sample_Name = "
									+ JspUtils.sqlEscape(sampName));
						if (rs.next()) {
							ps = new Relationship();
							ps.setRelatedFeatureID(new Integer(rs.getInt(1)));
							prevSamp.add(ps);
						} else {
							rs =
								conn.executeQuery(
									"SELECT Feature_ID FROM Folder_Content_View WHERE Sample_Name = "
										+ JspUtils.sqlEscape(sampName)
										+ " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = "
										+ user.getPersonId()
										+ "))");
							if (rs.next()) {
								ps = new Relationship();
								ps.setRelatedFeatureID(
									new Integer(rs.getInt(1)));
								prevSamp.add(ps);
							} else {
								throw new DataInputException(
									"Samples Nearby",
									value
										.substring(0, value.indexOf(";"))
										.trim()
										+ " not in database - pick another");
							}
						}
						value =
							value.substring(
								value.indexOf(";") + 1,
								value.length());
					}
					break;
				case SAMPLE_RELATIONSHIP :
					sampRel = new Vector();
					String srLine, srDistance, srDistMod, srDistRange, srFeat;
					Integer srRelID, srFeatID;
					Relationship smpR;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1)
							value += "\n";
						srLine = value.substring(0, value.indexOf("\n")).trim();
						if (srLine.indexOf("above") >= 0) {
							srRelID = new Integer(232);
							srDistance =
								srLine
									.substring(0, srLine.indexOf("above"))
									.trim();
							srFeat =
								srLine
									.substring(
										srLine.indexOf("above") + 5,
										srLine.length())
									.trim();
						} else if (srLine.indexOf("below") >= 0) {
							srRelID = new Integer(233);
							srDistance =
								srLine
									.substring(0, srLine.indexOf("below"))
									.trim();
							srFeat =
								srLine
									.substring(
										srLine.indexOf("below") + 5,
										srLine.length())
									.trim();
						} else {
							throw new DataInputException(
								"Sample Relationships",
								srLine + " invalid.  Please use the builder");
						}
						srDistMod = null;
						srDistRange = null;
						if (srDistance.indexOf("c.") == 0) {
							srDistMod = "c.";
							srDistance =
								srDistance
									.substring(2, srDistance.length())
									.trim();
						} else if (srDistance.indexOf("?") == 0) {
							srDistMod = "?";
							srDistance =
								srDistance
									.substring(1, srDistance.length())
									.trim();							
						}
						if (srDistance.indexOf("-") >= 0) {
							srDistance =
								srDistance
									.substring(0, srDistance.indexOf("-"))
									.trim();
							srDistRange =
								srDistance
									.substring(
										srDistance.indexOf("-") + 1,
										srDistance.length())
									.trim();
						}
						srDistance = srDistance.trim();
						rs =
							conn.executeQuery(
								"SELECT Feature_ID FROM Sample_All_View WHERE Sample_Name = "
									+ JspUtils.sqlEscape(srFeat));
						if (rs.next()) {
							srFeatID = new Integer(rs.getInt(1));
						} else {
							rs =
								conn.executeQuery(
									"SELECT Feature_ID FROM Folder_Content_View WHERE Sample_Name = "
										+ JspUtils.sqlEscape(srFeat)
										+ " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = "
										+ user.getPersonId()
										+ "))");
							if (rs.next()) {
								srFeatID = new Integer(rs.getInt(1));
							} else {
								throw new DataInputException(
									"Sample Relationships",
									srFeat + " not a valid sample");
							}
						}
						try {
							smpR = new Relationship();
							smpR.setRelatedFeatureID(srFeatID);
							smpR.setRelationTypeID(srRelID);
							if (!srDistance.equals(""))
								smpR.setDistance(new Double(srDistance));
							smpR.setDistanceMod(srDistMod);
							if (srDistRange != null)
								smpR.setDistanceRange(new Double(srDistRange));
							sampRel.add(smpR);
						} catch (Exception e) {
							throw new DataInputException(
								"Sample Relationships",
								srLine
									+ " is invalid.  Please use the builder");
						}
						value =
							value
								.substring(
									value.indexOf("\n") + 1,
									value.length())
								.trim();
					}
					break;
				case STRAT_RELATIONSHIP :
					stratRel = new Vector();
					String strLine,
						strDistance = "",
						strDistMod,
						strDistRange,
						strStrat;
					Integer strRelID;
					Relationship strR;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1) {
							value = value + "\n";
						}
						strLine =
							value.substring(0, value.indexOf("\n")).trim();
						if (strLine.indexOf("above base") >= 0) {
							strRelID = new Integer(237);
							strDistance =
								strLine
									.substring(0, strLine.indexOf("above base"))
									.trim();
							strStrat =
								strLine
									.substring(
										strLine.indexOf("above base") + 10,
										strLine.length())
									.trim();
						} else if (strLine.indexOf("above top") >= 0) {
							strRelID = new Integer(236);
							strDistance =
								strLine
									.substring(0, strLine.indexOf("above top"))
									.trim();
							strStrat =
								strLine
									.substring(
										strLine.indexOf("above top") + 9,
										strLine.length())
									.trim();
						} else if (strLine.indexOf("below base") >= 0) {
							strRelID = new Integer(239);
							strDistance =
								strLine
									.substring(0, strLine.indexOf("below base"))
									.trim();
							strStrat =
								strLine
									.substring(
										strLine.indexOf("below base") + 10,
										strLine.length())
									.trim();
						} else if (strLine.indexOf("below top") >= 0) {
							strRelID = new Integer(238);
							strDistance =
								strLine
									.substring(0, strLine.indexOf("below top"))
									.trim();
							strStrat =
								strLine
									.substring(
										strLine.indexOf("below top") + 9,
										strLine.length())
									.trim();
						} else {
							throw new DataInputException(
								"Stratigraphic Relationships",
								strLine
									+ " not a valid entry.  Please use the builder");
						}
						strDistMod = null;
						strDistRange = null;
						if (strDistance.indexOf("c.") == 0) {
							strDistMod = "c.";
							strDistance =
								strDistance
									.substring(2, strDistance.length())
									.trim();
						} else if (strDistance.indexOf("?") == 0) {
							strDistMod = "?";
							strDistance =
								strDistance
									.substring(1, strDistance.length())
									.trim();
						}
						if (strDistance.indexOf("-") >= 0) {
							strDistance =
								strDistance
									.substring(0, strDistance.indexOf("-"))
									.trim();
							strDistRange =
								strDistance
									.substring(
										strDistance.indexOf("-") + 1,
										strDistance.length())
									.trim();
						}
						strDistance = strDistance.trim();
						try {
							strR = new Relationship();
							strR.setRelatedStratUnit(strStrat.trim());
							strR.setRelationTypeID(strRelID);
							if (!strDistance.equals(""))
								strR.setDistance(new Double(strDistance));
							strR.setDistanceMod(strDistMod);
							if (strDistRange != null)
								strR.setDistanceRange(new Double(strDistRange));
							stratRel.add(strR);
						} catch (Exception e) {
							throw new DataInputException(
								"Stratigraphic Relationships",
								strLine
									+ " is invalid.  Please use the builder");
						}
						value =
							value
								.substring(
									value.indexOf("\n") + 1,
									value.length())
								.trim();
					}
					break;
				case DIP :
					if (!FREDUtils.isNumeric(value)
						|| Integer.parseInt(value) < 0
						|| Integer.parseInt(value) > 90)
						throw new DataInputException(
							"Dip",
							value
								+ " is not valid.  Dip must be numeric and between 0 and 90");
					break;
				case DIP_DIRECTION :
					if (!(value.equals("N")
						|| value.equals("NE")
						|| value.equals("E")
						|| value.equals("SE")
						|| value.equals("S")
						|| value.equals("SW")
						|| value.equals("W")
						|| value.equals("NW")))
						throw new DataInputException(
							"Dip Direction",
							value + " is not a valid option");
					break;
				case STRIKE :
					if (!FREDUtils.isNumeric(value)
						|| Integer.parseInt(value) < 0
						|| Integer.parseInt(value) > 360)
						throw new DataInputException(
							"Strike",
							value
								+ " is not valid.  Strike must be numeric and between 0 and 360");
					break;
				case FACING :
					if (!(value.equals("Normal")
						|| value.equals("Overturned")))
						throw new DataInputException(
							"Facing",
							value + " is not a valid option");
					break;
				case GRAIN_SIZE_P :
				case GRAIN_SIZE_S :
					DataEntryUtils.parseDropDownID(
						"Grainsize",
						"SELECT * FROM Lookup WHERE Lookup_ID = "
							+ value
							+ " AND FieldName = 'GrainSize'",
						state);
					break;
				case GS_COMP :
					if (!(value.equals("Y") || value.equals("N")))
						throw new DataInputException(
							"GS Comparator",
							value + " is not a valid option");
					break;
				case BEDDING_THICKNESS :
					DataEntryUtils.parseDropDownID(
						"Bedding Thickness",
						"SELECT * FROM Lookup WHERE Lookup_ID = "
							+ value
							+ " AND FieldName = 'BedThick'",
						state);
					break;
				case BEDDING_P :
				case BEDDING_S :
					DataEntryUtils.parseDropDownID(
						"Bedding",
						"SELECT * FROM Lookup WHERE Lookup_ID = "
							+ value
							+ " AND FieldName = 'Bedding'",
						state);
					break;
				case WEATHERING :
					DataEntryUtils.parseDropDownID(
						"Weathering",
						"SELECT * FROM Lookup WHERE Lookup_ID = "
							+ value
							+ " AND FieldName = 'Weathering'",
						state);
					break;
				case HARDNESS :
					DataEntryUtils.parseDropDownID(
						"Hardness",
						"SELECT * FROM Lookup WHERE Lookup_ID = "
							+ value
							+ " AND FieldName = 'Hardness'",
						state);
					break;
				case CARBONATE :
					DataEntryUtils.parseDropDownID(
						"Carbonate",
						"SELECT * FROM Lookup WHERE Lookup_ID = "
							+ value
							+ " AND FieldName = 'Carbonate'",
						state);
					break;
				case COLOUR_MOD :
					DataEntryUtils.parseDropDownID(
						"Shade",
						"SELECT * FROM Lookup WHERE Lookup_ID = "
							+ value
							+ " AND FieldName = 'ColourMod'",
						state);
					break;
				case COLOUR_P :
				case COLOUR_S :
					DataEntryUtils.parseDropDownID(
						"Colour",
						"SELECT * FROM Lookup WHERE Lookup_ID = "
							+ value
							+ " AND FieldName = 'RockColour'",
						state);
					break;
				case WET :
					if (!(value.equals("Wet") || value.equals("Dry")))
						throw new DataInputException(
							"Wet",
							value + " is not a valid option");
					break;
				case SED_FEATURES :
					sedFeat = new Vector();
					SedFeature sFeat;
					while (value.length() > 0) {
						sFeat = new SedFeature();
						if (value.indexOf(";") == -1)
							value += ";";
						if (value.indexOf("*") != -1
							&& value.indexOf("*") < value.indexOf(";")) {
							rs =
								conn.executeQuery(
									"SELECT Lookup_ID FROM Lookup WHERE Name = '"
										+ value
											.substring(0, value.indexOf("*"))
											.trim()
										+ "' AND FieldName = 'SedFeature'");
							sFeat.setAbundant("Y");
						} else {
							rs =
								conn.executeQuery(
									"SELECT Lookup_ID FROM Lookup WHERE Name = '"
										+ value
											.substring(0, value.indexOf(";"))
											.trim()
										+ "' AND FieldName = 'SedFeature'");
						}
						try {
							rs.next();
							sFeat.setSedFeatureId(new Integer(rs.getInt(1)));
							sedFeat.add(sFeat);
						} catch (Exception e) {
							throw new DataInputException(
								"Additional Features",
								"Invalid");
						}
						value =
							value
								.substring(
									value.indexOf(";") + 1,
									value.length())
								.trim();
					}
					break;
				case DEP_ENVIRONMENT_1 :
					if (!(value.equals("Marine")
						|| value.equals("Non-marine")))
						throw new DataInputException(
							"Deposition Environment",
							value + " is not a valid option");
					if (getField(DEP_ENVIRONMENT_2) != null) {
						depEnv = value + ": " + getField(DEP_ENVIRONMENT_2);
					} else {
						depEnv = value + ":";
					}
					break;
				case DEP_ENVIRONMENT_2 :
					if (getField(DEP_ENVIRONMENT_1) != null) {
						depEnv = getField(DEP_ENVIRONMENT_1) + ": " + value;
					} else {
						depEnv = value;
					}
					break;
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException e) {
			throw new DataInputException();
		}
	}

	protected void resetHiddenField(int field) {
		switch (field) {
			case COLLECTION_DATE :
				collDate = null;
				break;
			case COLLECTORS :
				collectors = null;
				break;
			case SENT_TO :
				sentTo = null;
				break;
			case PREVIOUS_SAMPLE :
				prevSamp = null;
				break;
			case SAMPLE_RELATIONSHIP :
				sampRel = null;
				break;
			case STRAT_RELATIONSHIP :
				stratRel = null;
				break;
			case SED_FEATURES :
				sedFeat = null;
				break;
			case DEP_ENVIRONMENT_1 :
				if (getField(DEP_ENVIRONMENT_2) != null) {
					depEnv = getField(DEP_ENVIRONMENT_2);
				} else {
					depEnv = null;
				}
				break;
			case DEP_ENVIRONMENT_2 :
				if (getField(DEP_ENVIRONMENT_1) != null) {
					depEnv = getField(DEP_ENVIRONMENT_1) + ":";
				} else {
					depEnv = null;
				}
				break;
		}
	}

	public void makeDataEntryHTML(Writer out)
		throws IOException, SQLException {
		ComboDescriptor cd;
		DBConnection conn = FREDUtils.getFREDConnection(state);
		if (!outcropSamp)
			super.makeDataEntryHTML(out);
		out.write(
			"<tr><td class='heading'>Collection Date</td><td></td><td><input type='text' name='CollDate' value='"
				+ FREDUtils.noNulls(getFieldForHTML(COLLECTION_DATE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=CollDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Collectors</td><td></td><td><textarea name='Coll' cols='40' rows='2'>"
				+ FREDUtils.noNulls(getFieldForHTML(COLLECTORS))
				+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Coll\", \"Supp\", \"width=600,height=400\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Strat Name</td><td></td><td><input type='text' name='StratName' size='40' value='"
				+ FREDUtils.noNulls(getFieldForHTML(STRAT_NAME))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=StratName\", \"Supp\", \"width=600,height=300\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Fossils In Place</td><td></td><td><select name='InPlace'><option value='' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='Yes' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) != null
					&& getFieldForHTML(FOSSILS_IN_PLACE).equals("Yes"))
					? " selected"
					: "")
				+ ">Yes</option><option value='Almost' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) != null
					&& getFieldForHTML(FOSSILS_IN_PLACE).equals("Almost"))
					? " selected"
					: "")
				+ ">Almost</option><option value='No' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) != null
					&& getFieldForHTML(FOSSILS_IN_PLACE).equals("No"))
					? " selected"
					: "")
				+ ">No</option><option value='Unknown' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) != null
					&& getFieldForHTML(FOSSILS_IN_PLACE).equals("Unknown"))
					? " selected"
					: "")
				+ ">Unknown</option></select></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Sent To</td><td></td><td><textarea name='SentTo' cols='40' rows='2'>"
				+ FREDUtils.noNulls(getFieldForHTML(SENT_TO))
				+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SentTo\", \"Supp\",\"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Not Collected<br><span class='smalltext'>specify fossils seen but not collected</span></td><td></td><td><textarea name='NotColl' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(NOT_COLLECTED))
				+ "</textarea></td></tr>\n");
		out.write(
			"<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");

		out.write(
			"<tr><td class='heading' colspan='2'>Significance/Comments</td><td><textarea name='Sig' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(SIGNIFICANCE_COMMENTS))
				+ "</textarea></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Stage Limits</td><td class='smallheading'>Inferred</td><td>\n");
		out.write("<table border='0' cellspacing='0'><tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "InfStageStart";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(INF_AGE_START);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write(
			"</td><td><select name='InfStartMod'><option value='-' "
				+ ((getFieldForHTML(INF_START_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(INF_START_MOD) != null
					&& getFieldForHTML(INF_START_MOD).equals("?"))
					? " selected"
					: "")
				+ ">?</option></select></td><td class='heading'> to </td></tr>\n");
		out.write("<tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "InfStageStop";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(INF_AGE_STOP);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write(
			"</td><td class='heading'><select name='InfStopMod'><option value='-' "
				+ ((getFieldForHTML(INF_STOP_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(INF_STOP_MOD) != null
					&& getFieldForHTML(INF_STOP_MOD).equals("?"))
					? " selected"
					: "")
				+ ">?</option></select></td></tr>\n");
		out.write("</table></td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Known</td><td>\n");
		out.write("<table border='0' cellspacing='0'><tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "KnwStageStart";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(KNW_AGE_START);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write(
			"</td><td><select name='KnwStartMod'><option value='-' "
				+ ((getFieldForHTML(KNW_START_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(KNW_START_MOD) != null
					&& getFieldForHTML(KNW_START_MOD).equals("?"))
					? " selected"
					: "")
				+ ">?</option></select></td><td class='heading'> to </td></tr>\n");
		out.write("<tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "KnwStageStop";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(KNW_AGE_STOP);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write(
			"</td><td class='heading'><select name='KnwStopMod'><option value='-' "
				+ ((getFieldForHTML(KNW_STOP_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(KNW_STOP_MOD) != null
					&& getFieldForHTML(KNW_STOP_MOD).equals("?"))
					? " selected"
					: "")
				+ ">?</option></select></td></tr>\n");
		out.write("</table></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Samples Nearby</td><td></td><td><input type='text' name='PrevSamp' size='40' value='"
				+ FREDUtils.noNulls(getFieldForHTML(PREVIOUS_SAMPLE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=PrevSamp\", \"Supp\", \"width=600,height=350\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading' colspan='2'>Sample Relationships</td><td><textarea name='SampRel' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(SAMPLE_RELATIONSHIP))
				+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SampRel\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading' colspan='2'>Stratigraphic Relationships</td><td><textarea name='StratRel' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(STRAT_RELATIONSHIP))
				+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=StratRel\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Column/Map</td><td></td><td><input type='text' name='ColMap' size='40' value='"
				+ FREDUtils.noNulls(getFieldForHTML(COLUMN_MAP))
				+ "'></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Attitude</td><td class='smallheading'>Dip</td><td><input type='text' name='Dip' size='3' value='"
				+ FREDUtils.noNulls(getFieldForHTML(DIP))
				+ "'></td></tr>\n");
		out.write(
			"<tr><td></td><td class='smallheading'>Dip Dirn.</td><td><select name='DipDir'>\n<option value='' "
				+ ((getFieldForHTML(DIP_DIRECTION) == null) ? " selected" : "")
				+ ">-- Choose --</option>\n<option value='N' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null
					&& getFieldForHTML(DIP_DIRECTION).equals("N"))
					? " selected"
					: "")
				+ ">North</option>\n<option value='NE' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null
					&& getFieldForHTML(DIP_DIRECTION).equals("NE"))
					? " selected"
					: "")
				+ ">North-East</option>\n<option value='E' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null
					&& getFieldForHTML(DIP_DIRECTION).equals("E"))
					? " selected"
					: "")
				+ ">East</option>\n<option value='SE' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null
					&& getFieldForHTML(DIP_DIRECTION).equals("SE"))
					? " selected"
					: "")
				+ ">South-East</option><option value='S' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null
					&& getFieldForHTML(DIP_DIRECTION).equals("S"))
					? " selected"
					: "")
				+ ">South</option>\n<option value='SW' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null
					&& getFieldForHTML(DIP_DIRECTION).equals("SW"))
					? " selected"
					: "")
				+ ">South-West</option>\n<option value='W' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null
					&& getFieldForHTML(DIP_DIRECTION).equals("W"))
					? " selected"
					: "")
				+ ">West</option>\n<option value='NW' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null
					&& getFieldForHTML(DIP_DIRECTION).equals("NW"))
					? " selected"
					: "")
				+ ">North-West</option>\n</select></td></tr>\n");
		out.write(
			"<tr><td></td><td class='smallheading'>Strike</td><td><input type='text' name='Strike' size='4' value='"
				+ FREDUtils.noNulls(getFieldForHTML(STRIKE))
				+ "'></td></tr>\n");
		out.write(
			"<tr><td></td><td class='smallheading'>Facing</td><td><select name='Facing'><option value='' "
				+ ((getFieldForHTML(FACING) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='Normal' "
				+ ((getFieldForHTML(FACING) != null
					&& getFieldForHTML(FACING).equals("Normal"))
					? " selected"
					: "")
				+ ">Normal</option><option value='Overturned' "
				+ ((getFieldForHTML(FACING) != null
					&& getFieldForHTML(FACING).equals("Overturned"))
					? " selected"
					: "")
				+ ">Overturned</option></select></td></tr>\n");
		out.write(
			"<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");

		out.write(
			"<tr><td class='heading'>Grain Size</td><td class='smallheading'>Pri.</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "GrainSizeP";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(GRAIN_SIZE_P);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'GrainSize'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Sec.</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "GrainSizeS";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(GRAIN_SIZE_S);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'GrainSize'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write(
			"<tr><td></td><td class='smallheading'>Comp. Used</td><td><select name='GSComp'><option value='' "
				+ ((getFieldForHTML(GS_COMP) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='Y' "
				+ ((getFieldForHTML(GS_COMP) != null && getFieldForHTML(GS_COMP).equals("Y"))
					? " selected"
					: "")
				+ ">Yes</option><option value='N' "
				+ ((getFieldForHTML(GS_COMP) != null && getFieldForHTML(GS_COMP).equals("N"))
					? " selected"
					: "")
				+ ">No</option></select></td></tr>\n");
		out.write(
			"<tr><td class='heading'>Stratification</td><td class='smallheading'>Thickness</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "BedThick";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(BEDDING_THICKNESS);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'BedThick'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Features</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "BeddingP";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(BEDDING_P);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Bedding'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("&nbsp;<span class='heading'>&</span>&nbsp;");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "BeddingS";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(BEDDING_S);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Bedding'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Weathering</td><td></td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "Weath";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(WEATHERING);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Weathering'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Hardness</td><td></td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "Hard";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(HARDNESS);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Hardness'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Carbonate</td><td></td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "Carb";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(CARBONATE);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'Carbonate'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write(
			"<tr><td class='heading'>Colour</td><td class='smallheading'>Shade</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "ColMod";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(COLOUR_MOD);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'ColourMod'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Colour</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "ColourP";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(COLOUR_P);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'RockColour'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("&nbsp;<span class='heading'>-</span>&nbsp;");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
		cd.name = "ColourS";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(COLOUR_S);
		cd.orderBy = "Code";
		cd.join = "FieldName = 'RockColour'";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write(
			"<tr><td></td><td class='smallheading'>Wet/Dry</td><td><select name='Wet'><option value='' "
				+ ((getFieldForHTML(WET) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='Wet' "
				+ ((getFieldForHTML(WET) != null && getFieldForHTML(WET).equals("Wet"))
					? " selected"
					: "")
				+ ">Wet</option><option value='Dry' "
				+ ((getFieldForHTML(WET) != null && getFieldForHTML(WET).equals("Dry"))
					? " selected"
					: "")
				+ ">Dry</option></select></td></tr>\n");
		out.write(
			"<tr><td class='heading' colspan='2'>Additional Features</td><td><input type='text' name='SedFeat' size='40' value='"
				+ FREDUtils.noNulls(getFieldForHTML(SED_FEATURES))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SedFeat\", \"Supp\", \"width=600,height=350\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading' colspan='2'>Inferred Environment</td><td><select name='DepEnv1'><option value='' "
				+ ((getFieldForHTML(DEP_ENVIRONMENT_1) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='Marine' "
				+ ((getFieldForHTML(DEP_ENVIRONMENT_1) != null
					&& getFieldForHTML(DEP_ENVIRONMENT_1).equals("Marine"))
					? " selected"
					: "")
				+ ">Marine</option><option value='Non-marine' "
				+ ((getFieldForHTML(DEP_ENVIRONMENT_1) != null
					&& getFieldForHTML(DEP_ENVIRONMENT_1).equals("Non-marine"))
					? " selected"
					: "")
				+ ">Non-marine</option></select></td></tr>\n");
		out.write(
			"<tr><td></td><td></td><td><textarea name='DepEnv2' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(DEP_ENVIRONMENT_2))
				+ "</textarea></td></tr>\n");
		out.write(
			"<tr><td class='heading' colspan='2'>Nature of Rock Unit</td><td><textarea name='RockNat' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(ROCK_NATURE))
				+ "</textarea></td></tr>\n");
		out.write(
			"<tr><td class='heading' colspan='2'>Correspondence</td><td><textarea name='Corr' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(CORRESPONDENCE))
				+ "</textarea></td></tr>\n");
		if (!outcropSamp)
			super.makeEndBitHTML(out);
	}

	public int save()
		throws InvalidCredentialsException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				conn.executeUpdate(
					"DELETE FROM Sample_Property WHERE Record_ID = "
						+ record.getRecordID());
				//Create SAMPLE_PROPERTY entry
				conn.executeUpdate(
					"INSERT INTO Sample_Property (Record_ID, Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage_ID, Known_Stage_ID, Column_Map, Dip, Dip_Direction, Strike, Facing, Primary_Grainsize_ID, Secondary_Grainsize_ID, Comparator_Used, Bed_Thick_ID, Primary_Bedding_ID, Secondary_Bedding_ID, Weathering_ID, Hardness_ID, Carbonate_ID, Colour_Modifier_ID, Primary_Colour_ID, Secondary_Colour_ID, Wet, Deposition_Env, Rock_Nature, Correspondence) VALUES ("
						+ record.getRecordID()
						+ ((collDate != null)
							? ", TO_DATE('"
								+ collDate.getDateString()
								+ "'), "
								+ JspUtils.sqlEscape(collDate.getDateRounding())
							: ", NULL, NULL")
						+ ", "
						+ JspUtils.sqlEscape(getField(STRAT_NAME))
						+ ", "
						+ JspUtils.sqlEscape(getField(FOSSILS_IN_PLACE))
						+ ", "
						+ JspUtils.sqlEscape(getField(NOT_COLLECTED))
						+ ", "
						+ JspUtils.sqlEscape(getField(SIGNIFICANCE_COMMENTS))
						+ ", "
						+ JspUtils.sqlEscape(
							DataEntryUtils.getStageID(
								getField(INF_AGE_START),
								getField(INF_START_MOD),
								getField(INF_AGE_STOP),
								getField(INF_STOP_MOD),
								state))
						+ ", "
						+ JspUtils.sqlEscape(
							DataEntryUtils.getStageID(
								getField(KNW_AGE_START),
								getField(KNW_START_MOD),
								getField(KNW_AGE_STOP),
								getField(KNW_STOP_MOD),
								state))
						+ ", "
						+ JspUtils.sqlEscape(getField(COLUMN_MAP))
						+ ", "
						+ JspUtils.sqlEscape(getField(DIP))
						+ ", "
						+ JspUtils.sqlEscape(getField(DIP_DIRECTION))
						+ ", "
						+ JspUtils.sqlEscape(getField(STRIKE))
						+ ", "
						+ JspUtils.sqlEscape(getField(FACING))
						+ ", "
						+ JspUtils.sqlEscape(getField(GRAIN_SIZE_P))
						+ ", "
						+ JspUtils.sqlEscape(getField(GRAIN_SIZE_S))
						+ ", "
						+ JspUtils.sqlEscape(getField(GS_COMP))
						+ ", "
						+ JspUtils.sqlEscape(getField(BEDDING_THICKNESS))
						+ ", "
						+ JspUtils.sqlEscape(getField(BEDDING_P))
						+ ", "
						+ JspUtils.sqlEscape(getField(BEDDING_S))
						+ ", "
						+ JspUtils.sqlEscape(getField(WEATHERING))
						+ ", "
						+ JspUtils.sqlEscape(getField(HARDNESS))
						+ ", "
						+ JspUtils.sqlEscape(getField(CARBONATE))
						+ ", "
						+ JspUtils.sqlEscape(getField(COLOUR_MOD))
						+ ", "
						+ JspUtils.sqlEscape(getField(COLOUR_P))
						+ ", "
						+ JspUtils.sqlEscape(getField(COLOUR_S))
						+ ", "
						+ JspUtils.sqlEscape(getField(WET))
						+ ", "
						+ JspUtils.sqlEscape(depEnv)
						+ ", "
						+ JspUtils.sqlEscape(getField(ROCK_NATURE))
						+ ", "
						+ JspUtils.sqlEscape(getField(CORRESPONDENCE))
						+ ")");
				//Create COLLECTORS entries
				if (collectors != null) {
					for (Iterator i = collectors.iterator(); i.hasNext();) {
						conn.executeUpdate(
							"INSERT INTO Collector (Record_ID, Person_ID) VALUES ("
								+ record.getRecordID()
								+ ", "
								+ (Integer) i.next()
								+ ")");
					}
				}
				//Create SENT TO entries
				if (sentTo != null) {
					for (Iterator i = sentTo.iterator(); i.hasNext();) {
						SentTo sT = (SentTo) i.next();
						if (sT.getFossilGroupID() != null)
							conn.executeUpdate(
								"INSERT INTO Sent_To (Record_ID, Fossil_Group_ID, Person_ID, Lab_ID, Comments) VALUES ("
									+ record.getRecordID()
									+ ", "
									+ sT.getFossilGroupID()
									+ ", "
									+ JspUtils.sqlEscape(sT.getPersonID())
									+ ", "
									+ JspUtils.sqlEscape(sT.getLabID())
									+ ", "
									+ JspUtils.sqlEscape(sT.getComments())
									+ ")");
					}
				}
				//Create RELATIONSHIP entries
				if (prevSamp != null) {
					for (Iterator i = prevSamp.iterator(); i.hasNext();) {
						Relationship rel = (Relationship) i.next();
						if (rel.getRelatedFeatureID() != null)
							conn.executeUpdate(
								"INSERT INTO Relationship (Record_ID, Relationship_Type, Relation_Type_ID, Related_Feature_ID) VALUES ("
									+ record.getRecordID()
									+ ", 'Sample', 231, "
									+ rel.getRelatedFeatureID()
									+ ")");
					}
				}

				if (sampRel != null) {
					for (Iterator i = sampRel.iterator(); i.hasNext();) {
						Relationship rel = (Relationship) i.next();
						conn.executeUpdate(
							"INSERT INTO Relationship (Record_ID, Relationship_Type, Relation_Type_ID, Distance, Distance_Range, Distance_Mod, Related_Feature_ID) VALUES ("
								+ record.getRecordID()
								+ ", 'Sample', "
								+ JspUtils.sqlEscape(rel.getRelationTypeID())
								+ ", "
								+ JspUtils.sqlEscape(rel.getDistance())
								+ ", "
								+ JspUtils.sqlEscape(rel.getDistanceRange())
								+ ", "
								+ JspUtils.sqlEscape(rel.getDistanceMod())
								+ ", "
								+ JspUtils.sqlEscape(rel.getRelatedFeatureID())
								+ ")");
					}
				}

				if (stratRel != null) {
					for (Iterator i = stratRel.iterator(); i.hasNext();) {
						Relationship rel = (Relationship) i.next();
						conn.executeUpdate(
							"INSERT INTO Relationship (Record_ID, Relationship_Type, Relation_Type_ID, Distance, Distance_Range, Distance_Mod, Strat_Unit) VALUES ("
								+ record.getRecordID()
								+ ", 'Strat', "
								+ JspUtils.sqlEscape(rel.getRelationTypeID())
								+ ", "
								+ JspUtils.sqlEscape(rel.getDistance())
								+ ", "
								+ JspUtils.sqlEscape(rel.getDistanceRange())
								+ ", "
								+ JspUtils.sqlEscape(rel.getDistanceMod())
								+ ", "
								+ JspUtils.sqlEscape(rel.getRelatedStratUnit())
								+ ")");
					}
				}
				//Create SEDIMENTARY FEATURE entries
				if (sedFeat != null) {
					for (Iterator i = sedFeat.iterator(); i.hasNext();) {
						SedFeature sF = (SedFeature) i.next();
						conn.executeUpdate(
							"INSERT INTO Sedimentary_Feature (Record_ID, Sed_Feature_ID, Abundant) VALUES ("
								+ record.getRecordID()
								+ ", "
								+ sF.getSedFeatureId()
								+ ", "
								+ JspUtils.sqlEscape(sF.getAbundant())
								+ ")");
					}
				}

				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				try {
					record = (SampPropRecord) SampPropRecord.getData(record.getRecordID(), user, state, true);
					sample = new Sample(sample.getSampleID(), user, state, true);
				} catch (Exception e) {
				}
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				e.printStackTrace(new PrintWriter(System.out));
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

	protected void checkMandatoryFields() throws DataInputException {
		if (getField(COLLECTORS) == null || getField(COLLECTION_DATE) == null || getField(FOSSILS_IN_PLACE) == null)
			throw new DataInputException("Mandatory Fields", "Not all mandatory fields completed");
	}

}
