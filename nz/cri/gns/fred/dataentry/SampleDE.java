package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Vector;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.db.metadata.MetadataRecord;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Audit;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Relationship;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.fred.data.SedFeature;
import nz.cri.gns.fred.data.SentTo;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class SampleDE implements DataEntryForm {

	private User user;
	private PageState state;
	private Folder workingFolder;
	private int featureID = -1;
	private int auditID = -1;
	private Sample sample;
	private Integer secClassID;
	private String[] fields = new String[120];
	private String[] tempFields = new String[120];
	private boolean savedFlag = false;
	private boolean isAllowedSubmit = false;

	private RoundedDate collDate;
	private Vector collectors;
	private String depEnv;

	private boolean outcropSample = false;
	private Vector prevSamp;
	private Vector sampRel;
	private Vector sedFeat;
	private Vector sentTo;
	private Vector stratRel;

	private static final int RELATIONSHIP_NEARBY = 231;
	private static final int RELATIONSHIP_ABOVE = 232;
	private static final int RELATIONSHIP_BELOW = 233;
	private static final int RELATIONSHIP_ABOVE_TOP = 236;
	private static final int RELATIONSHIP_ABOVE_BASE = 237;
	private static final int RELATIONSHIP_BELOW_TOP = 238;
	private static final int RELATIONSHIP_BELOW_BASE = 239;

	public SampleDE(User user, int featureID, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		this(user, folderID, state);
		this.featureID = featureID;
	}		

	public SampleDE(User user, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		this.user = user;
		this.state = state;
		this.workingFolder = new Folder(folderID, user, state);
		workingFolder = new Folder(folderID, user, state);
		if (!workingFolder.isAllowedCreateLocalities())
			throw new DataInputException("Locality", "Insufficient rights to create locality");
		isAllowedSubmit = workingFolder.isAllowedSubmitLocalities();
	}

	public SampleDE(int sampleID, User user, PageState state) throws IllegalArgumentException, DataInputException, SQLException, IOException, InsufficientPrivelegesException {
		this.user = user;
		this.state = state;
		sample = new Sample(sampleID, user, state, true);
		
		//check status for editing
		if (!FREDUtils.isAllowedEditSample(user, sample.getAsString(Sample.SAMPLE_STATUS), String.valueOf(sampleID), state))
			throw new DataInputException("Sample", "Insufficient rights to edit this sample");
		if (sample.get(Sample.FEATURE_WORKING_FOLDER_ID) != null)
			workingFolder = new Folder(sample.getAsInt(Sample.FEATURE_WORKING_FOLDER_ID), user, state);
		this.featureID = sample.getFeatureID();
		this.auditID = sample.getAsInt(Sample.SAMPLE_AUDIT_ID);
		isAllowedSubmit = FREDUtils.isAllowedSubmitSample(user, sample.getAsString(Sample.SAMPLE_STATUS), String.valueOf(sample.getSampleID()), state);
		getFromDatabase(sample);
	}

	public void copyFrom(int sampleID) throws DataInputException, InsufficientPrivelegesException, SQLException, IOException {
		Sample copySample = new Sample(sampleID, user, state);
		getFromDatabase(copySample);
	}

	private void getFromDatabase(Sample sample) throws DataInputException, InsufficientPrivelegesException {
		//set fields
		try {
			setField(COLLECTION_DATE, DataEntryUtils.reverseParseDate(
					sample.getAsDate(Sample.COLLECTION_DATE),
					sample.getAsString(Sample.COLLECTION_DATE_ROUNDING)));
			if (sample.get(Sample.COLLECTOR) != null) {
				StringBuffer collName = new StringBuffer();
				for (Iterator i = sample.getAsVector(Sample.COLLECTOR).iterator(); i.hasNext();) {
					KeyValueObject coll = (KeyValueObject) i.next();
					collName.append(coll.getValue() + "\n");
				}
				setField(COLLECTORS, collName.toString());
			}
			setField(STRAT_NAME, sample.getAsString(Sample.STRAT_UNIT));
			setField(FOSSILS_IN_PLACE, sample.getAsString(Sample.IN_PLACE));
			if (sample.get(Sample.SENT_TO) != null) {
				StringBuffer sTStr = new StringBuffer();
				for (Iterator i = sample.getAsVector(Sample.SENT_TO).iterator(); i.hasNext();) {
					SentTo sT = (SentTo) i.next();
					sTStr.append(sT.getFossilGroup() + "*" + FREDUtils.noNulls(sT.getPerson()) + "*" + FREDUtils.noNulls(sT.getLab()) + "*" + FREDUtils.noNulls(sT.getComments()) + "\n");
				}
				setField(SENT_TO, sTStr.toString());
			}
			setField(NOT_COLLECTED, sample.getAsString(Sample.NOT_COLLECTED));
			setField(SIGNIFICANCE_COMMENTS, sample.getAsString(Sample.SIGNIFICANCE));
			setField(INF_AGE_START, sample.getAsString(Sample.INFERRED_STAGE_LOWER_ID));
			setField(INF_START_MOD, sample.getAsString(Sample.INFERRED_STAGE_LOWER_MOD));
			setField(INF_AGE_STOP, sample.getAsString(Sample.INFERRED_STAGE_UPPER_ID));
			setField(INF_STOP_MOD, sample.getAsString(Sample.INFERRED_STAGE_UPPER_MOD));
			setField(KNW_AGE_START, sample.getAsString(Sample.KNOWN_STAGE_LOWER_ID));
			setField(KNW_START_MOD, sample.getAsString(Sample.KNOWN_STAGE_LOWER_MOD));
			setField(KNW_AGE_STOP, sample.getAsString(Sample.KNOWN_STAGE_UPPER_ID));
			setField(KNW_STOP_MOD, sample.getAsString(Sample.KNOWN_STAGE_UPPER_MOD));
			if (sample.get(Sample.RELATIONSHIP_NEARBY) != null) {
				StringBuffer prevSamp = new StringBuffer();
				for (Iterator i = sample.getAsVector(Sample.RELATIONSHIP_NEARBY).iterator(); i.hasNext();) {
					Relationship rel = (Relationship) i.next();
					prevSamp.append(rel.getRelatedSampleName() + ";");
				}
				setField(PREVIOUS_SAMPLE, prevSamp.toString());
			}
			if (sample.get(Sample.RELATIONSHIP_SAMPLE) != null) {
				StringBuffer sampRel = new StringBuffer();
				for (Iterator i = sample.getAsVector(Sample.RELATIONSHIP_SAMPLE).iterator(); i.hasNext();) {
					Relationship rel = (Relationship) i.next();
					if (rel.getDistanceMod() != null)
						sampRel.append(rel.getDistanceMod() + " ");
					if (rel.getDistance() != null) {
						sampRel.append(FREDUtils.noNulls(rel.getDistance()));
						if (rel.getDistanceRange() != null)
							sampRel.append(" - " + rel.getDistanceRange());
					}
					sampRel.append(" " + rel.getRelationType() + " " + rel.getRelatedSampleName() + "\n");
				}
				setField(SAMPLE_RELATIONSHIP, sampRel.toString());
			}
			if (sample.get(Sample.RELATIONSHIP_STRAT) != null) {
				StringBuffer stratRel = new StringBuffer();
				for (Iterator i = sample.getAsVector(Sample.RELATIONSHIP_STRAT).iterator(); i.hasNext();) {
					Relationship rel = (Relationship) i.next();
					if (rel.getDistanceMod() != null)
						stratRel.append(rel.getDistanceMod() + " ");
					if (rel.getDistance() != null) {
						stratRel.append(FREDUtils.noNulls(rel.getDistance()));
						if (rel.getDistanceRange() != null)
							stratRel.append(" - " + rel.getDistanceRange());
					}
					stratRel.append(" " + rel.getRelationType() + " " + rel.getRelatedStratUnit() + "\n");
				}
				setField(STRAT_RELATIONSHIP, stratRel.toString());
			}
			setField(COLUMN_MAP, sample.getAsString(Sample.COLUMN_MAP));
			setField(DIP, sample.getAsString(Sample.DIP));
			setField(DIP_DIRECTION, sample.getAsString(Sample.DIP_DIRECTION));
			setField(STRIKE, sample.getAsString(Sample.STRIKE));
			setField(FACING, sample.getAsString(Sample.FACING));
			setField(GRAIN_SIZE_P, sample.getAsString(Sample.PRIMARY_GRAINSIZE_ID));
			setField(GRAIN_SIZE_S, sample.getAsString(Sample.SECONDARY_GRAINSIZE_ID));
			setField(GS_COMP, sample.getAsString(Sample.COMPARATOR_USED));
			setField(BEDDING_THICKNESS, sample.getAsString(Sample.BED_THICK_ID));
			setField(BEDDING_P, sample.getAsString(Sample.PRIMARY_BEDDING_ID));
			setField(BEDDING_S, sample.getAsString(Sample.SECONDARY_BEDDING_ID));
			setField(WEATHERING, sample.getAsString(Sample.WEATHERING_ID));
			setField(HARDNESS, sample.getAsString(Sample.HARDNESS_ID));
			setField(CARBONATE, sample.getAsString(Sample.CARBONATE_ID));
			setField(COLOUR_MOD, sample.getAsString(Sample.COLOUR_MODIFIER_ID));
			setField(COLOUR_P, sample.getAsString(Sample.PRIMARY_COLOUR_ID));
			setField(COLOUR_S, sample.getAsString(Sample.SECONDARY_COLOUR_ID));
			setField(WET, sample.getAsString(Sample.WET));
			if (sample.get(Sample.SED_FEATURE) != null) {
				StringBuffer sF = new StringBuffer();
				for (Iterator i = sample.getAsVector(Sample.SED_FEATURE).iterator(); i.hasNext();) {
					SedFeature sFeat = (SedFeature) i.next();
					sF.append(sFeat.getFeat());
					if (sFeat.getAbundant() != null)
						sF.append("*");
					sF.append(";");
				}
				setField(SED_FEATURES, sF.toString());
			}
			String depEnv = sample.getAsString(Sample.DEPOSITION_ENV);
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
			setField(ROCK_NATURE, sample.getAsString(Sample.ROCK_NATURE));
			setField(CORRESPONDENCE, sample.getAsString(Sample.CORRESPONDENCE));
			try {
				setField(SECURITY_TYPE, String.valueOf(FREDUtils.getSecurityType(sample.getAsInt(Sample.SAMPLE_SECURITY_CLASS_ID), user, state)));
			} catch (Exception e) {
				setField(SECURITY_TYPE, "21");
			}
		} catch (TaxonomicListException e) {}
	}

	public void setOutcropSamp(boolean outcropSamp) {
		this.outcropSample = outcropSamp;
	}

	public void setFeatureID(int featureID) {
		this.featureID = featureID;
	}
	
	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	protected void parseField(int field, String value) throws DataInputException, TaxonomicListException {
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case SECURITY_TYPE :
					try {
						secClassID = new Integer(FREDUtils.getSecurityClass(Integer.parseInt(value), user, state));
					} catch (Exception e) {
						throw new DataInputException("Security Class", "Invalid");
					}
					break;
				case COLLECTION_DATE :
					collDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case COLLECTORS :
					collectors = new Vector();
					String query = "SELECT person_id FROM person_view WHERE name = ?";
					int personID = 0;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1)
							value = value + "\n";	
						try {
							rs = conn.executeQuery(query, new int[] {Types.VARCHAR}, new Object[] {value.substring(0, value.indexOf("\n")).trim()});
							rs.next();
							personID = rs.getInt(1);
						} catch (Exception e) {
							throw new DataInputException("Collector", value.substring(0, value.indexOf("\n")).trim() + " not in database - add through builder");
						}
						if (collectors.indexOf(new Integer(personID)) != -1)
							throw new DataInputException("Collector", value.substring(0, value.indexOf("\n")).trim() + " duplicated");
						collectors.add(new Integer(rs.getInt(1)));
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
						try {
							query = "SELECT group_id FROM fossil_group WHERE name = ?";
							rs = conn.executeQuery(query, new int[] {Types.VARCHAR}, new Object[] {stGroup});
							rs.next();
							stGroupID = new Integer(rs.getInt(1));
						} catch (Exception e) {
							throw new DataInputException("Sent To - Group",	stGroup + " not a valid sent to group");
						}
						if (!stPerson.equals("")) {
							try {
								query = "SELECT person_id FROM person_view WHERE name = ?";
								rs = conn.executeQuery(query, new int[] {Types.VARCHAR}, new Object[] {stPerson});
								rs.next();
								stPersonID = new Integer(rs.getInt(1));
							} catch (Exception e) {
								throw new DataInputException("Sent To - Person", stPerson + " not in database - add through builder");
							}
						}
						if (!stLab.equals("")) {
							try {
								query = "SELECT lab_id FROM sc.lab WHERE lab_name = ?";
								rs = conn.executeQuery(query, new int[] {Types.VARCHAR}, new Object[] {stLab});
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
					DataEntryUtils.parseAge(value, getField(INF_AGE_STOP), "Inferred Age", state);
					break;
				case INF_AGE_STOP :
					DataEntryUtils.parseAge(getField(INF_AGE_START), value, "Inferred Age", state);
					break;
				case INF_START_MOD :
				case INF_STOP_MOD :
					if (value != null && !value.equals("?"))
						throw new DataInputException("Inferred Age", "Bad Modifier");
					break;
				case KNW_AGE_START :
					DataEntryUtils.parseAge(value, getField(KNW_AGE_STOP), "Known Age", state);
					break;
				case KNW_AGE_STOP :
					DataEntryUtils.parseAge(getField(KNW_AGE_START), value, "Known Age", state);
					break;
				case KNW_START_MOD :
				case KNW_STOP_MOD :
					if (value != null && !value.equals("?"))
						throw new DataInputException("Known Age", "Bad Modifier");
					break;
				case PREVIOUS_SAMPLE :
					prevSamp = new Vector();
					Relationship ps;
					while (value.length() > 0) {
						if (value.indexOf(";") == -1)
							value += ";";
						String sampName = value.substring(0, value.indexOf(";")).trim();
						query = "SELECT feature_id FROM feature_view WHERE UPPER(sample_name) = ? AND feature_status = ?";
						rs = conn.executeQuery(query, new int[] {Types.VARCHAR, Types.VARCHAR}, new Object[] {sampName.toUpperCase(), Audit.STATUS_APPROVED});
						if (rs.next()) {
							ps = new Relationship();
							ps.setRelatedFeatureID(new Integer(rs.getInt(1)));
							prevSamp.add(ps);
						} else {
							query = "SELECT feature_id FROM feature_view fv, folder_view fd WHERE fv.feature_working_folder_id = fd.folder_id AND fv.feature_status <> ? AND fd.user_id = ? AND fd.folder_type = ? AND UPPER(fv.sample_name) = ?";
							rs = conn.executeQuery(query, new int[] {Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR}, new Object[] {Audit.STATUS_APPROVED, new Integer(user.getPersonId()), new Integer(2), sampName.toUpperCase()});
							if (rs.next()) {
								ps = new Relationship();
								ps.setRelatedFeatureID(new Integer(rs.getInt(1)));
								prevSamp.add(ps);
							} else {
								throw new DataInputException("Samples Nearby", value.substring(0, value.indexOf(";")).trim() + " not in database - pick another");
							}
						}
						value =	value.substring(value.indexOf(";") + 1,	value.length());
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
							srRelID = new Integer(RELATIONSHIP_ABOVE);
							srDistance = srLine.substring(0, srLine.indexOf("above")).trim();
							srFeat = srLine.substring(srLine.indexOf("above") + 5, srLine.length()).trim();
						} else if (srLine.indexOf("below") >= 0) {
							srRelID = new Integer(RELATIONSHIP_BELOW);
							srDistance = srLine.substring(0, srLine.indexOf("below")).trim();
							srFeat = srLine.substring(srLine.indexOf("below") + 5, srLine.length()).trim();
						} else {
							throw new DataInputException("Sample Relationships", srLine + " invalid.  Please use the builder");
						}
						srDistMod = null;
						srDistRange = null;
						if (srDistance.indexOf("c.") == 0) {
							srDistMod = "c.";
							srDistance = srDistance.substring(2, srDistance.length()).trim();
						} else if (srDistance.indexOf("?") == 0) {
							srDistMod = "?";
							srDistance = srDistance.substring(1, srDistance.length()).trim();							
						}
						if (srDistance.indexOf("-") >= 0) {
							srDistance = srDistance.substring(0, srDistance.indexOf("-")).trim();
							srDistRange = srDistance.substring(srDistance.indexOf("-") + 1, srDistance.length()).trim();
						}
						srDistance = srDistance.trim();
						query = "SELECT feature_id FROM feature_view WHERE UPPER(sample_name) = ? AND feature_status = ?";
						rs = conn.executeQuery(query, new int[] {Types.VARCHAR, Types.VARCHAR}, new Object[] {srFeat.toUpperCase(), Audit.STATUS_APPROVED});
						if (rs.next()) {
							srFeatID = new Integer(rs.getInt(1));
						} else {
							query = "SELECT feature_id FROM feature_view fv, folder_view fd WHERE fv.feature_working_folder_id = fd.folder_id AND fv.feature_status <> ? AND fd.user_id = ? AND fd.folder_type = ? AND UPPER(fv.sample_name) = ?";
							rs = conn.executeQuery(query, new int[] {Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR}, new Object[] {Audit.STATUS_APPROVED, new Integer(user.getPersonId()), new Integer(2), srFeat.toUpperCase()});
							if (rs.next()) {
								srFeatID = new Integer(rs.getInt(1));
							} else {
								throw new DataInputException("Sample Relationships", srFeat + " not a valid sample");
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
							throw new DataInputException("Sample Relationships", srLine + " is invalid.  Please use the builder");
						}
						value = value.substring(value.indexOf("\n") + 1, value.length()).trim();
					}
					break;
				case STRAT_RELATIONSHIP :
					stratRel = new Vector();
					String strLine,	strDistance = "", strDistMod, strDistRange, strStrat;
					Integer strRelID;
					Relationship strR;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1)
							value = value + "\n";
						strLine = value.substring(0, value.indexOf("\n")).trim();
						if (strLine.indexOf("above base") >= 0) {
							strRelID = new Integer(RELATIONSHIP_ABOVE_BASE);
							strDistance = strLine.substring(0, strLine.indexOf("above base")).trim();
							strStrat = strLine.substring(strLine.indexOf("above base") + 10, strLine.length()).trim();
						} else if (strLine.indexOf("above top") >= 0) {
							strRelID = new Integer(RELATIONSHIP_ABOVE_TOP);
							strDistance = strLine.substring(0, strLine.indexOf("above top")).trim();
							strStrat = strLine.substring(strLine.indexOf("above top") + 9, strLine.length()).trim();
						} else if (strLine.indexOf("below base") >= 0) {
							strRelID = new Integer(RELATIONSHIP_BELOW_BASE);
							strDistance = strLine.substring(0, strLine.indexOf("below base")).trim();
							strStrat = strLine.substring(strLine.indexOf("below base") + 10, strLine.length()).trim();
						} else if (strLine.indexOf("below top") >= 0) {
							strRelID = new Integer(RELATIONSHIP_BELOW_TOP);
							strDistance = strLine.substring(0, strLine.indexOf("below top")).trim();
							strStrat = strLine.substring(strLine.indexOf("below top") + 9, strLine.length()).trim();
						} else {
							throw new DataInputException("Stratigraphic Relationships", strLine	+ " not a valid entry.  Please use the builder");
						}
						strDistMod = null;
						strDistRange = null;
						if (strDistance.indexOf("c.") == 0) {
							strDistMod = "c.";
							strDistance = strDistance.substring(2, strDistance.length()).trim();
						} else if (strDistance.indexOf("?") == 0) {
							strDistMod = "?";
							strDistance = strDistance.substring(1, strDistance.length()).trim();
						}
						if (strDistance.indexOf("-") >= 0) {
							strDistance = strDistance.substring(0, strDistance.indexOf("-")).trim();
							strDistRange = strDistance.substring(strDistance.indexOf("-") + 1, strDistance.length()).trim();
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
							throw new DataInputException("Stratigraphic Relationships",	strLine + " is invalid.  Please use the builder");
						}
						value =	value.substring(value.indexOf("\n") + 1, value.length()).trim();
					}
					break;
				case DIP :
					if (!FREDUtils.isNumeric(value) || Integer.parseInt(value) < 0 || Integer.parseInt(value) > 90)
						throw new DataInputException("Dip",	value + " is not valid.  Dip must be numeric and between 0 and 90");
					break;
				case DIP_DIRECTION :
					if (!(value.equals("N") || value.equals("NE") || value.equals("E") || value.equals("SE") || value.equals("S") || value.equals("SW") || value.equals("W") || value.equals("NW")))
						throw new DataInputException("Dip Direction", value + " is not a valid option");
					break;
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
					DataEntryUtils.checkDropDownID("Grainsize", "SELECT * FROM grain_size WHERE grain_size_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)}, state);
					break;
				case GS_COMP :
					if (!(value.equals("Y") || value.equals("N")))
						throw new DataInputException("GS Comparator", value + " is not a valid option");
					break;
				case BEDDING_THICKNESS :
					DataEntryUtils.checkDropDownID("Bedding Thickness", "SELECT * FROM bed_thickness WHERE thickness_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)}, state);
					break;
				case BEDDING_P :
				case BEDDING_S :
					DataEntryUtils.checkDropDownID("Bedding", "SELECT * FROM bedding WHERE bedding_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)}, state);
					break;
				case WEATHERING :
					DataEntryUtils.checkDropDownID("Weathering", "SELECT * FROM weathering WHERE weathering_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)}, state);
					break;
				case HARDNESS :
					DataEntryUtils.checkDropDownID("Hardness", "SELECT * FROM hardness WHERE hardness_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)}, state);
					break;
				case CARBONATE :
					DataEntryUtils.checkDropDownID("Carbonate", "SELECT * FROM carbonate WHERE carbonate_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)}, state);
					break;
				case COLOUR_MOD :
					DataEntryUtils.checkDropDownID("Shade", "SELECT * FROM colour_modifier WHERE modifier_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)}, state);
					break;
				case COLOUR_P :
				case COLOUR_S :
					DataEntryUtils.checkDropDownID("Colour", "SELECT * FROM rock_colour WHERE colour_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)}, state);
					break;
				case WET :
					if (!(value.equals("Wet") || value.equals("Dry")))
						throw new DataInputException("Wet", value + " is not a valid option");
					break;
				case SED_FEATURES :
					sedFeat = new Vector();
					SedFeature sFeat;
					String sedFeatStr;
					while (value.length() > 0) {
						sFeat = new SedFeature();
						if (value.indexOf(";") == -1)
							value += ";";
						query = "SELECT sedfeature_type_id FROM sedimentary_feature_type WHERE name = ?";
						if (value.indexOf("*") != -1 && value.indexOf("*") < value.indexOf(";")) {
							sedFeatStr = value.substring(0, value.indexOf("*")).trim();
							sFeat.setAbundant("Y");
						} else {
							sedFeatStr = value.substring(0, value.indexOf(";")).trim();
						}
						try {
							rs = conn.executeQuery(query, new int[] {Types.VARCHAR}, new Object[] {sedFeatStr});
							rs.next();
							sFeat.setSedFeatureId(new Integer(rs.getInt(1)));
							sedFeat.add(sFeat);
						} catch (Exception e) {
							throw new DataInputException("Additional Features", "Invalid");
						}
						value = value.substring(value.indexOf(";") + 1,	value.length()).trim();
					}
					break;
				case DEP_ENVIRONMENT_1 :
					if (!(value.equals("Marine") || value.equals("Non-marine")))
						throw new DataInputException("Deposition Environment", value + " is not a valid option");
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

	private void resetHiddenField(int field) {
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

	protected String getFieldForHTML(int field) {
		if (getTempField(field) != null) {
			return getTempField(field);
		}
		return getField(field);
	}

	public void makeNavPanelHTML(Writer out) throws IOException {
		out.write("<tr><td colspan='2' align='center'><img src='images/drill.gif' height='20' width='20' /></td></tr>");
		out.write("<tr><td colspan='2' align='center' class='heading'>Sample</td></tr>\n");
		out.write("<tr><td>&nbsp;</td></tr>");
		if (workingFolder != null) {
			out.write("<tr><td><a href='load_record.jsp?FoldID=" + workingFolder.getFolderID()
				+ ((sample != null) ? "&SampID=" + sample.getSampleID() : "")
				+ "&RecType=Sample'><img src='images/load.gif' height='20' width='20' border='0' alt='Copy From' /></a>&nbsp;&nbsp;</td><td><a href='load_record.jsp?FoldID=" + workingFolder.getFolderID()
				+ ((sample != null) ? "&SampID=" + sample.getSampleID() : "")
				+ "&RecType=Sample' class='boldlink'>Copy From</a></td></tr>\n");
		}
		out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>\n");
		if (isAllowedSubmit)
			out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database' /></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>\n");
	}

	public void makeDataEntryHTML(Writer out) throws IOException, SQLException {
		ComboDescriptor cd;
		DBConnection conn = FREDUtils.getFREDConnection(state);

		if (!outcropSample) {
			try {
				out.write("<tr><td class='heading'>Sample Name</td><td></td><td class='heading'>" + sample.getAsString(Sample.SAMPLE_NAME));
				out.write("<br>" + FREDUtils.noNulls(sample.getAsString(Sample.FEATURE_NAME)) + ": " + FREDUtils.noNulls(sample.getAsString(Sample.DRILLHOLE_DEPTH)));
			} catch (Exception e) {	}
			out.write("</td>");
			try {
				out.write("<td><a href='new_sample.jsp?FeatID=" + sample.getAsString(Sample.FEATURE_ID) + "&SampID=" + sample.getSampleID() + "&FoldID=" + sample.getAsString(Sample.FEATURE_WORKING_FOLDER_ID) + "'><img src='images/edit.gif' width='20' height='20' border='0' alt='Edit' /></a></td>");
			} catch (Exception e) {}
			out.write("</tr>\n");
			if (sample != null) {
				out.write("<tr><td class='heading' colspan='2'>Attached Files/Images<br /><span class='smalltext'>Click <a href='binary_data_entry.jsp?ID="
					+ sample.getSampleID() + "&RecType=SMP"
					+ ((workingFolder != null) ? "&FoldID=" + workingFolder.getFolderID() : "")
					+ "' target='fredBinary'>here</a> to add/edit</span></td><td>");
				try {
					if (sample != null && sample.getSampleMetadataRecordsCount() > 0) {	
						MetadataRecord[] mr = sample.getSampleMetadataRecords();
						for (int i = 0; i < mr.length; i++)
							out.write(mr[i].getTitle() + "<br />");
					}
				} catch (Exception e) {}
				out.write("</td></tr>");
			}
		}
		if (!outcropSample) {
			out.write("<tr><td class='heading' colspan='2'>Security Setting</td><td>");
			cd = new ComboDescriptor("security_class", "class_id", "Name");
			cd.name = "SecType";
			if (getField(SECURITY_TYPE) != null) {
				cd.selected = getFieldForHTML(SECURITY_TYPE);
			} else {
				cd.selected = "21";
			}
			cd.orderBy = "class_ID";
			HTMLUtils.makeDropBox(out, conn, cd);
			out.write("</td></tr>\n");
		}
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");

		out.write("<tr><td class='heading' style=\"color: #FF0000\">Collection Date</td><td></td><td><input type='text' name='CollDate' value='"
				+ FREDUtils.noNulls(getFieldForHTML(COLLECTION_DATE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=CollDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading' style=\"color: #FF0000\">Collectors</td><td></td><td><textarea name='Coll' cols='40' rows='2'>"
				+ FREDUtils.noNulls(getFieldForHTML(COLLECTORS))
				+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Coll\", \"Supp\", \"width=600,height=400\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Strat Name</td><td></td><td><input type='text' name='StratName' size='40' value='"
				+ FREDUtils.noNulls(getFieldForHTML(STRAT_NAME))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=StratName\", \"Supp\", \"width=600,height=300\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading' style=\"color: #FF0000\">Fossils In Place</td><td></td><td><select name='InPlace'><option value='' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='Yes' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) != null && getFieldForHTML(FOSSILS_IN_PLACE).equals("Yes")) ? " selected" : "")
				+ ">Yes</option><option value='Almost' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) != null && getFieldForHTML(FOSSILS_IN_PLACE).equals("Almost")) ? " selected" : "")
				+ ">Almost</option><option value='No' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) != null && getFieldForHTML(FOSSILS_IN_PLACE).equals("No")) ? " selected" : "")
				+ ">No</option><option value='Unknown' "
				+ ((getFieldForHTML(FOSSILS_IN_PLACE) != null && getFieldForHTML(FOSSILS_IN_PLACE).equals("Unknown")) ? " selected" : "")
				+ ">Unknown</option></select></td></tr>\n");
		out.write("<tr><td class='heading'>Sent To</td><td></td><td><textarea name='SentTo' cols='40' rows='2'>"
				+ FREDUtils.noNulls(getFieldForHTML(SENT_TO))
				+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SentTo\", \"Supp\",\"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Not Collected<br><span class='smalltext'>specify fossils seen but not collected</span></td><td></td><td><textarea name='NotColl' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(NOT_COLLECTED))
				+ "</textarea></td></tr>\n");
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");

		out.write("<tr><td class='heading' colspan='2'>Significance/Comments</td><td><textarea name='Sig' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(SIGNIFICANCE_COMMENTS))
				+ "</textarea></td></tr>\n");
		out.write("<tr><td class='heading'>Stage Limits</td><td class='smallheading'>Inferred</td><td>\n");
		out.write("<table border='0' cellspacing='0'><tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "InfStageStart";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(INF_AGE_START);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td><td><select name='InfStartMod'><option value='-' "
				+ ((getFieldForHTML(INF_START_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(INF_START_MOD) != null && getFieldForHTML(INF_START_MOD).equals("?")) ? " selected" : "")
				+ ">?</option></select></td><td class='heading'> to </td></tr>\n");
		out.write("<tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "InfStageStop";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(INF_AGE_STOP);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td><td class='heading'><select name='InfStopMod'><option value='-' "
				+ ((getFieldForHTML(INF_STOP_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(INF_STOP_MOD) != null && getFieldForHTML(INF_STOP_MOD).equals("?")) ? " selected" : "")
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
		out.write("</td><td><select name='KnwStartMod'><option value='-' "
				+ ((getFieldForHTML(KNW_START_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(KNW_START_MOD) != null && getFieldForHTML(KNW_START_MOD).equals("?")) ? " selected" : "")
				+ ">?</option></select></td><td class='heading'> to </td></tr>\n");
		out.write("<tr><td>");
		cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
		cd.name = "KnwStageStop";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(KNW_AGE_STOP);
		cd.orderBy = "Ag_Name";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td><td class='heading'><select name='KnwStopMod'><option value='-' "
				+ ((getFieldForHTML(KNW_STOP_MOD) == null) ? " selected" : "")
				+ "></option><option value='?' "
				+ ((getFieldForHTML(KNW_STOP_MOD) != null && getFieldForHTML(KNW_STOP_MOD).equals("?")) ? " selected" : "")
				+ ">?</option></select></td></tr>\n");
		out.write("</table></td></tr>\n");
		out.write("<tr><td class='heading'>Samples Nearby</td><td></td><td><input type='text' name='PrevSamp' size='40' value='"
				+ FREDUtils.noNulls(getFieldForHTML(PREVIOUS_SAMPLE))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=PrevSamp\", \"Supp\", \"width=600,height=350\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Sample Relationships</td><td><textarea name='SampRel' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(SAMPLE_RELATIONSHIP))
				+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SampRel\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Stratigraphic Relationships</td><td><textarea name='StratRel' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(STRAT_RELATIONSHIP))
				+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=StratRel\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Column/Map</td><td></td><td><input type='text' name='ColMap' size='40' value='"
				+ FREDUtils.noNulls(getFieldForHTML(COLUMN_MAP))
				+ "'></td></tr>\n");
		out.write("<tr><td class='heading'>Attitude</td><td class='smallheading'>Dip</td><td><input type='text' name='Dip' size='3' value='"
				+ FREDUtils.noNulls(getFieldForHTML(DIP))
				+ "'></td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Dip Dirn.</td><td><select name='DipDir'>\n<option value='' "
				+ ((getFieldForHTML(DIP_DIRECTION) == null) ? " selected" : "")
				+ ">-- Choose --</option>\n<option value='N' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null && getFieldForHTML(DIP_DIRECTION).equals("N")) ? " selected" : "")
				+ ">North</option>\n<option value='NE' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null && getFieldForHTML(DIP_DIRECTION).equals("NE")) ? " selected" : "")
				+ ">North-East</option>\n<option value='E' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null && getFieldForHTML(DIP_DIRECTION).equals("E")) ? " selected" : "")
				+ ">East</option>\n<option value='SE' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null && getFieldForHTML(DIP_DIRECTION).equals("SE")) ? " selected" : "")
				+ ">South-East</option><option value='S' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null && getFieldForHTML(DIP_DIRECTION).equals("S")) ? " selected"	: "")
				+ ">South</option>\n<option value='SW' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null && getFieldForHTML(DIP_DIRECTION).equals("SW")) ? " selected" : "")
				+ ">South-West</option>\n<option value='W' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null && getFieldForHTML(DIP_DIRECTION).equals("W")) ? " selected" : "")
				+ ">West</option>\n<option value='NW' "
				+ ((getFieldForHTML(DIP_DIRECTION) != null && getFieldForHTML(DIP_DIRECTION).equals("NW")) ? " selected" : "")
				+ ">North-West</option>\n</select></td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Strike</td><td><input type='text' name='Strike' size='4' value='"
				+ FREDUtils.noNulls(getFieldForHTML(STRIKE))
				+ "'></td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Facing</td><td><select name='Facing'><option value='' "
				+ ((getFieldForHTML(FACING) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='Normal' "
				+ ((getFieldForHTML(FACING) != null && getFieldForHTML(FACING).equals("Normal")) ? " selected" : "")
				+ ">Normal</option><option value='Overturned' "
				+ ((getFieldForHTML(FACING) != null && getFieldForHTML(FACING).equals("Overturned")) ? " selected" : "")
				+ ">Overturned</option></select></td></tr>\n");
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");

		out.write("<tr><td class='heading'>Grain Size</td><td class='smallheading'>Pri.</td><td>");
		cd = new ComboDescriptor("grain_size", "grain_size_id", "Code || ': ' || Name");
		cd.name = "GrainSizeP";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(GRAIN_SIZE_P);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Sec.</td><td>");
		cd = new ComboDescriptor("grain_size", "grain_size_id", "Code || ': ' || Name");
		cd.name = "GrainSizeS";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(GRAIN_SIZE_S);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Comp. Used</td><td><select name='GSComp'><option value='' "
				+ ((getFieldForHTML(GS_COMP) == null) ? " selected" : "") + ">-- Choose --</option><option value='Y' " + ((getFieldForHTML(GS_COMP) != null && getFieldForHTML(GS_COMP).equals("Y")) ? " selected" : "")
				+ ">Yes</option><option value='N' "
				+ ((getFieldForHTML(GS_COMP) != null && getFieldForHTML(GS_COMP).equals("N")) ? " selected" : "")
				+ ">No</option></select></td></tr>\n");
		out.write("<tr><td class='heading'>Stratification</td><td class='smallheading'>Thickness</td><td>");
		cd = new ComboDescriptor("bed_thickness", "thickness_id", "Code || ': ' || Name");
		cd.name = "BedThick";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(BEDDING_THICKNESS);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Features</td><td>");
		cd = new ComboDescriptor("bedding", "bedding_ID", "Code || ': ' || Name");
		cd.name = "BeddingP";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(BEDDING_P);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("&nbsp;<span class='heading'>&</span>&nbsp;");
		cd = new ComboDescriptor("bedding", "bedding_ID", "Code || ': ' || Name");
		cd.name = "BeddingS";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(BEDDING_S);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Weathering</td><td></td><td>");
		cd = new ComboDescriptor("weathering", "weathering_ID", "Code || ': ' || Name");
		cd.name = "Weath";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(WEATHERING);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Hardness</td><td></td><td>");
		cd = new ComboDescriptor("hardness", "hardness_ID", "Code || ': ' || Name");
		cd.name = "Hard";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(HARDNESS);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Carbonate</td><td></td><td>");
		cd = new ComboDescriptor("carbonate", "carbonate_ID", "Code || ': ' || Name");
		cd.name = "Carb";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(CARBONATE);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>Colour</td><td class='smallheading'>Shade</td><td>");
		cd = new ComboDescriptor("colour_modifier", "modifier_ID", "Code || ': ' || Name");
		cd.name = "ColMod";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(COLOUR_MOD);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Colour</td><td>");
		cd = new ComboDescriptor("rock_colour", "colour_id", "Code || ': ' || Name");
		cd.name = "ColourP";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(COLOUR_P);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("&nbsp;<span class='heading'>-</span>&nbsp;");
		cd = new ComboDescriptor("rock_colour", "colour_id", "Code || ': ' || Name");
		cd.name = "ColourS";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(COLOUR_S);
		cd.orderBy = "Code";
		HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Wet/Dry</td><td><select name='Wet'><option value='' "
				+ ((getFieldForHTML(WET) == null) ? " selected" : "") + ">-- Choose --</option><option value='Wet' " + ((getFieldForHTML(WET) != null && getFieldForHTML(WET).equals("Wet")) ? " selected" : "")
				+ ">Wet</option><option value='Dry' "
				+ ((getFieldForHTML(WET) != null && getFieldForHTML(WET).equals("Dry")) ? " selected" : "")
				+ ">Dry</option></select></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Additional Features</td><td><input type='text' name='SedFeat' size='40' value='"
				+ FREDUtils.noNulls(getFieldForHTML(SED_FEATURES))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SedFeat\", \"Supp\", \"width=600,height=350\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Inferred Environment</td><td><select name='DepEnv1'><option value='' "
				+ ((getFieldForHTML(DEP_ENVIRONMENT_1) == null) ? " selected" : "")
				+ ">-- Choose --</option><option value='Marine' "
				+ ((getFieldForHTML(DEP_ENVIRONMENT_1) != null && getFieldForHTML(DEP_ENVIRONMENT_1).equals("Marine")) ? " selected" : "")
				+ ">Marine</option><option value='Non-marine' "
				+ ((getFieldForHTML(DEP_ENVIRONMENT_1) != null && getFieldForHTML(DEP_ENVIRONMENT_1).equals("Non-marine")) ? " selected" : "")
				+ ">Non-marine</option></select></td></tr>\n");
		out.write("<tr><td></td><td></td><td><textarea name='DepEnv2' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(DEP_ENVIRONMENT_2))
				+ "</textarea></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Nature of Rock Unit</td><td><textarea name='RockNat' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(ROCK_NATURE))
				+ "</textarea></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Correspondence</td><td><textarea name='Corr' cols='40' rows='3'>"
				+ FREDUtils.noNulls(getFieldForHTML(CORRESPONDENCE))
				+ "</textarea></td></tr>\n");
		if (!outcropSample) {
			out.write("<table border='0' cellpadding='0' cellspacing='2'>\n");
			out.write("<tr><td>&nbsp;</td></tr>\n");
			out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>\n");
			if (isAllowedSubmit)
				out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database'/></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>\n");
			out.write("</table>\n");
		}
	}

	public void makeExcelImportHTML(Writer out) throws SQLException, IOException {
		try {
		if (!outcropSample) { //blank columns for where Locality data goes
			out.write("<tr><td>" + sample.getFeatureID() + "</td>");
			out.write("<td>Sample</td>");
			out.write("<td>" + sample.getAsString(Sample.FEATURE_TYPE) + "</td>");
			out.write("<td>" + sample.getAsString(Sample.SAMPLE_NAME) + "</td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");	
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
			out.write("<td></td>");
		}
		out.write("<td>" + sample.getSampleID() + "</td>");
		out.write("<td>" + FREDUtils.noNulls(sample.getAsString(Sample.TOP_DEPTH)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(sample.getAsString(Sample.BOTTOM_DEPTH)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(sample.getAsString(Sample.DRILL_TYPE)) + "</td>");
		out.write("<td>#" + FREDUtils.noNulls(getFieldForHTML(COLLECTION_DATE)) + "#</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(COLLECTORS)).replaceAll("\n", "#") + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(STRAT_NAME)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(FOSSILS_IN_PLACE)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(SENT_TO)).replaceAll("\n", "#") + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(NOT_COLLECTED)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(SIGNIFICANCE_COMMENTS)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(INF_AGE_START)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(INF_START_MOD)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(INF_AGE_STOP)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(INF_STOP_MOD)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(KNW_AGE_START)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(KNW_START_MOD)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(KNW_AGE_STOP)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(KNW_STOP_MOD)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(PREVIOUS_SAMPLE)).replaceAll(";", "#") + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(SAMPLE_RELATIONSHIP)).replaceAll("\n", "#") + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(STRAT_RELATIONSHIP)).replaceAll("\n", "#") + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(COLUMN_MAP)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(DIP)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(DIP_DIRECTION)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(STRIKE)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(FACING)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(GRAIN_SIZE_P)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(GRAIN_SIZE_S)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(GS_COMP)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(BEDDING_THICKNESS)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(BEDDING_P)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(BEDDING_S)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(WEATHERING)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(HARDNESS)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(CARBONATE)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(COLOUR_MOD)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(COLOUR_P)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(COLOUR_S)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(WET)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(SED_FEATURES)).replaceAll(";", "#").replaceAll("\\*", "\\$") + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(DEP_ENVIRONMENT_1)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(DEP_ENVIRONMENT_2)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(ROCK_NATURE)) + "</td>");
		out.write("<td>" + FREDUtils.noNulls(getFieldForHTML(CORRESPONDENCE)) + "</td>");
		if (!outcropSample)
			out.write("</tr>");
		out.flush();
		} catch (Exception e) {}
	}
	
	public int save() throws InsufficientPrivelegesException, SQLException, IOException {
		if (featureID == -1)
			throw new InsufficientPrivelegesException();
		if (!savedFlag) {
			String sampleID;
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			conn.getConnection().setAutoCommit(false);
			try {
				if (sample == null) {
					if (auditID == -1) {	//ie not an outcrop sample
						//create new AUDIT record
						QueryDescriptor qd = new QueryDescriptor("audit_table");
						qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WORKING);
						qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
						qd.addQueryColumn("created_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
						qd.addQueryColumn("working_comments", Types.VARCHAR, fields[WORKING_COMMENTS]);
						if (workingFolder != null)
							qd.addQueryColumn("working_folder_id", Types.NUMERIC, new Integer(workingFolder.getFolderID()));
						qd.addQueryColumn("security_class_id", Types.NUMERIC, ((secClassID != null) ? secClassID : new Integer(4)));
						String auditID = DBUtils.doInsertUsingSequence(qd, "audit_id", "audit_seq", conn, true);
					}
					//create new SAMPLE record
					QueryDescriptor qd = getSampleQD();
					qd.addQueryColumn("feature_id", Types.NUMERIC, new Integer(featureID));
					qd.addQueryColumn("audit_id", Types.NUMERIC, new Integer(auditID));
					sampleID = DBUtils.doInsertUsingSequence(qd, "sample_id", "sample_seq", conn, true);
				} else { // edit
					sampleID = String.valueOf(sample.getSampleID());
					if (!outcropSample) {
						//Update AUDIT
						QueryDescriptor qd = new QueryDescriptor("audit_table");
						qd.addQueryColumn("working_comments", Types.VARCHAR, fields[WORKING_COMMENTS]);
						qd.addQueryColumn("security_class_id", Types.NUMERIC, ((secClassID != null) ? secClassID : new Integer(4)));
						qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(auditID));
						DBUtils.doUpdate(qd, "audit_id = ?", conn);
					}
					//Update SAMPLE
					QueryDescriptor qd = getSampleQD();
					qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(sampleID));
					DBUtils.doUpdate(qd, "sample_id = ?", conn);
				}
				
				//Delete and then add new repeating records
				int[] numericType = new int[] {Types.NUMERIC};
				Object[] sampIDObj = new Object[] {new Integer(sampleID)};
				//Create COLLECTORS entries
				conn.executeUpdate("DELETE FROM collector WHERE sample_id = ?", numericType, sampIDObj);
				if (collectors != null) {
					String query = "INSERT INTO collector (sample_id, person_id) VALUES (?, ?)";
					for (Iterator i = collectors.iterator(); i.hasNext();)
						conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(sampleID), (Integer) i.next()});
				}
				//Create SENT TO entries
				conn.executeUpdate("DELETE FROM sent_to WHERE sample_id = ?", numericType, sampIDObj);
				if (sentTo != null) {
					String query = "INSERT INTO sent_to (sample_id, fossil_group_id, person_id, lab_id, comments) VALUES (?, ?, ?, ?, ?)";
					for (Iterator i = sentTo.iterator(); i.hasNext();) {
						SentTo sT = (SentTo) i.next();
						if (sT.getFossilGroupID() != null)
							conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR},
								new Object[] {new Integer(sampleID), sT.getFossilGroupID(), sT.getPersonID(),sT.getLabID(), sT.getComments()});
					}
				}
				//Create RELATIONSHIP entries
				conn.executeUpdate("DELETE FROM relationship WHERE sample_id = ?", numericType, sampIDObj);
				String query = "INSERT INTO relationship (sample_id, relationship_type, relation_type_id, distance, distance_range, distance_mod, related_feature_id, strat_unit) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				int[] relType = new int[] {Types.NUMERIC, Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.NUMERIC, Types.VARCHAR};
				if (prevSamp != null) {
					for (Iterator i = prevSamp.iterator(); i.hasNext();) {
						Relationship rel = (Relationship) i.next();
						if (rel.getRelatedFeatureID() != null)
							conn.executeUpdate(query, relType, new Object[] {new Integer(sampleID), "Sample", new Integer(RELATIONSHIP_NEARBY), null, null, null, rel.getRelatedFeatureID(), null});
					}
				}
				if (sampRel != null) {
					for (Iterator i = sampRel.iterator(); i.hasNext();) {
						Relationship rel = (Relationship) i.next();
						if (rel.getRelatedFeatureID() != null)
							conn.executeUpdate(query, relType, new Object[] {new Integer(sampleID), "Sample", rel.getRelationTypeID(), rel.getDistance(), rel.getDistanceRange(), rel.getDistanceMod(), rel.getRelatedFeatureID(), null});
					}
				}
				if (stratRel != null) {
					for (Iterator i = stratRel.iterator(); i.hasNext();) {
						Relationship rel = (Relationship) i.next();
						if (rel.getRelatedStratUnit() != null)
							conn.executeUpdate(query, relType, new Object[] {new Integer(sampleID), "Strat", rel.getRelationTypeID(), rel.getDistance(), rel.getDistanceRange(), rel.getDistanceMod(), null, rel.getRelatedStratUnit()});
					}
				}
				//Create SEDIMENTARY FEATURE entries
				conn.executeUpdate("DELETE FROM sedimentary_feature WHERE sample_id = ?", numericType, sampIDObj);
				if (sedFeat != null) {
					query = "INSERT INTO sedimentary_feature (sample_id, sed_feature_id, abundant) VALUES (?, ?, ?)";
					for (Iterator i = sedFeat.iterator(); i.hasNext();) {
						SedFeature sF = (SedFeature) i.next();
						conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR},
							 new Object[] {new Integer(sampleID), sF.getSedFeatureId(), sF.getAbundant()});
					}
				}
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				try {
					sample = new Sample(Integer.parseInt(sampleID), user, state, true);
				} catch (Exception e) {}
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			}
		}
		return sample.getSampleID();
	}

	private QueryDescriptor getSampleQD() throws NumberFormatException, IOException, SQLException {
		QueryDescriptor qd = new QueryDescriptor("sample");
		qd.addQueryColumn("collection_date", Types.DATE ,((collDate != null) ? collDate.getDate() : null));
		qd.addQueryColumn("date_rounding", Types.VARCHAR, ((collDate != null) ? collDate.getDateRounding() : null));
		qd.addQueryColumn("strat_unit", Types.VARCHAR, getField(STRAT_NAME));
		qd.addQueryColumn("in_place", Types.VARCHAR, getField(FOSSILS_IN_PLACE));
		qd.addQueryColumn("not_collected", Types.VARCHAR, getField(NOT_COLLECTED));
		qd.addQueryColumn("significance", Types.VARCHAR, getField(SIGNIFICANCE_COMMENTS));
		String infStageID = DataEntryUtils.getStageID(getField(INF_AGE_START), getField(INF_START_MOD), getField(INF_AGE_STOP), getField(INF_STOP_MOD), state);
		qd.addQueryColumn("inferred_stage_id", Types.NUMERIC, ((infStageID != null) ? new Integer(infStageID) : null));
		String knwStageID = DataEntryUtils.getStageID(getField(KNW_AGE_START), getField(KNW_START_MOD), getField(KNW_AGE_STOP), getField(KNW_STOP_MOD), state);
		qd.addQueryColumn("known_stage_id", Types.NUMERIC, ((knwStageID != null) ? new Integer(knwStageID) : null));
		qd.addQueryColumn("column_map", Types.VARCHAR, getField(COLUMN_MAP));
		qd.addQueryColumn("dip", Types.NUMERIC, ((getField(DIP) != null) ? new Integer(getField(DIP)) : null));
		qd.addQueryColumn("dip_direction", Types.VARCHAR, getField(DIP_DIRECTION));
		qd.addQueryColumn("strike", Types.NUMERIC, ((getField(STRIKE) != null) ? new Integer(getField(STRIKE)) : null));
		qd.addQueryColumn("facing", Types.VARCHAR, getField(FACING));
		qd.addQueryColumn("primary_grainsize_id", Types.NUMERIC, ((getField(GRAIN_SIZE_P) != null) ? new Integer(getField(GRAIN_SIZE_P)) : null));
		qd.addQueryColumn("secondary_grainsize_id", Types.NUMERIC, ((getField(GRAIN_SIZE_S) != null) ? new Integer(getField(GRAIN_SIZE_S)) : null));
		qd.addQueryColumn("comparator_used", Types.VARCHAR, getField(GS_COMP));
		qd.addQueryColumn("bed_thick_id", Types.NUMERIC, ((getField(BEDDING_THICKNESS) != null) ? new Integer(getField(BEDDING_THICKNESS)) : null));
		qd.addQueryColumn("primary_bedding_id", Types.NUMERIC, ((getField(BEDDING_P) != null) ? new Integer(getField(BEDDING_P)) : null));
		qd.addQueryColumn("secondary_bedding_id", Types.NUMERIC, ((getField(BEDDING_S) != null) ? new Integer(getField(BEDDING_S)) : null));
		qd.addQueryColumn("weathering_id", Types.NUMERIC, ((getField(WEATHERING) != null) ? new Integer(getField(WEATHERING)) : null));
		qd.addQueryColumn("hardness_id", Types.NUMERIC, ((getField(HARDNESS) != null) ? new Integer(getField(HARDNESS)) : null));
		qd.addQueryColumn("carbonate_id", Types.NUMERIC, ((getField(CARBONATE) != null) ? new Integer(getField(CARBONATE)) : null));
		qd.addQueryColumn("colour_modifier_id", Types.NUMERIC, ((getField(COLOUR_MOD) != null) ? new Integer(getField(COLOUR_MOD)) : null));
		qd.addQueryColumn("primary_colour_id", Types.NUMERIC, ((getField(COLOUR_P) != null) ? new Integer(getField(COLOUR_P)) : null));
		qd.addQueryColumn("secondary_colour_id", Types.NUMERIC, ((getField(COLOUR_S) != null) ? new Integer(getField(COLOUR_S)) : null));
		qd.addQueryColumn("wet", Types.VARCHAR, getField(WET));
		qd.addQueryColumn("deposition_env", Types.VARCHAR, depEnv);
		qd.addQueryColumn("rock_nature", Types.VARCHAR, getField(ROCK_NATURE));
		qd.addQueryColumn("correspondence", Types.VARCHAR, getField(CORRESPONDENCE));
		return qd;
	}

	public int getWorkingFolderID() {
		if (workingFolder != null)
			return workingFolder.getFolderID();
		return -1;
	}

	public int getFieldCount() {
		return fields.length;
	}

	public void setField(int field, String value) throws DataInputException, TaxonomicListException {
		if (value != null && (value.equals("") || value.equals("-") || value.equals("null")))
			value = null;
		if (value != null) {
			parseField(field, value);
		} else {
			resetHiddenField(field);
		}
		fields[field] = value;
		savedFlag = false;
	}

	public void setTempField(int field, String value) {
		tempFields[field] = value;	
	}

	public String getField(int field) {
		return fields[field];
	}

	public String getTempField(int field) {
		return tempFields[field];
	}

	public void setFieldsFromTemp() throws DataInputException, TaxonomicListException {
		for (int i = 0; i < getFieldCount(); i++) {
			setField(i, tempFields[i]);
			setTempField(i, null);
		}
	}

	public int submit() throws SQLException, IOException, InsufficientPrivelegesException, DataInputException {
		if ((sample != null && sample.getAsString(Sample.SAMPLE_STATUS).equals(Audit.STATUS_WAITING)) || !isAllowedSubmit)
			throw new InsufficientPrivelegesException();
		if (getField(COLLECTORS) == null || getField(COLLECTION_DATE) == null || getField(FOSSILS_IN_PLACE) == null)
			throw new DataInputException("Mandatory Fields", "Not all mandatory fields completed");
		save();
		if (!outcropSample) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			QueryDescriptor qd = new QueryDescriptor("audit_table");
			qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_APPROVED);
			qd.addQueryColumn("submitted_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
			qd.addQueryColumn("submitted_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
			qd.addQueryColumn("working_comments", Types.VARCHAR, null);
			qd.addQueryColumn("working_folder_id", Types.NUMERIC, null);
			qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(sample.getAsInt(Sample.SAMPLE_AUDIT_ID)));
			DBUtils.doUpdate(qd, "audit_id = ?", conn);
		}
		sample = new Sample(sample.getSampleID(), user, state, true);
		return sample.getSampleID();
	}

	public void delete() throws IOException, SQLException, InsufficientPrivelegesException {
		if (!FREDUtils.isAllowedDeleteSample(user, sample.getAsString(Sample.SAMPLE_STATUS), String.valueOf(sample.getSampleID()), state) && sample != null)
			throw new InsufficientPrivelegesException();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		conn.executeUpdate("DELETE FROM Sample WHERE Sample_ID = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(sample.getSampleID())});
		conn.releaseStatement();	
	}

}
