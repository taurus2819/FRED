package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Sample_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getSampleView method instead.
 */
public class SampleData {

	private static Pool pool = new Pool();
	private int id;
	private Object[] values = new Object[129];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getContactPerson method instead.
	 */
	private SampleData(int id, PageState state)
		throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT Feature_ID, Sample_ID, Feature_Type,Sample_Name, FR_ID, FR_Number, Yard_FR_ID, Yard_FR_Number, Feature_Name, Map_Sheet, Serial_Number, "
			+ "Recollection_Number, Yard_Map_Sheet, Yard_Serial_Number, Yard_Recollection_Number, Drillhole_Depth, Top_Depth, Bottom_Depth, Drill_Type_ID, "
			+ "Drill_Type, Masterfile_ID, Masterfile_Name, Reg_Area_ID, Reg_Area_Name, Reg_Area_Code, Audit_ID, Status, Last_Change, Working_Folder_ID, "
			+ "Working_Comments, Security_Class_ID, Site_ID, Country_Code, Country_Name, Latitude, Longitude, QMap_Sheet, NZMG_Sheet, NZMG_East, NZMG_North, "
			+ "Method_ID, Method, Accuracy, Orig_System_ID, Coord_System, Orig_Coord, Locality, Drillhole_Licence_Name, Person_ID, Person, Start_Date, "
			+ "Start_Date_Rounding, Finish_Date, Finish_Date_Rounding, Datum_Type, Datum_Elevation, Start_Depth, Finish_Depth, Collection_Date, "
			+ "Collection_Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage_ID, Inferred_Stage, "
			+ "Inferred_Stage_Abbrev, Inferred_Stage_Lower_ID, Inferred_Stage_Lower, Inferred_Stage_Lower_Mod, Inferred_Stage_Upper_ID, "
			+ "Inferred_Stage_Upper, Inferred_Stage_Upper_Mod, Inferred_Age_Start, Inferred_Age_Stop, Known_Stage_ID, Known_Stage, Known_Stage_Abbrev, "
			+ "Known_Stage_Lower_ID, Known_Stage_Lower, Known_Stage_Lower_Mod, Known_Stage_Upper_ID, Known_Stage_Upper, Known_Stage_Upper_Mod, "
			+ "Known_Age_Start, Known_Age_Stop, Column_Map, Dip, Dip_Direction, Strike, Facing, Grainsize, Primary_Grainsize_ID, Primary_Grainsize, "
			+ "Secondary_Grainsize_ID, Secondary_Grainsize, Comparator_Used, Bed_Thick_ID, Bed_Thickness, Bedding, Primary_Bedding_ID, Primary_Bedding, "
			+ "Secondary_Bedding_ID, Secondary_Bedding, Weathering_ID, Weathering, Hardness_ID, Hardness, Carbonate_ID, Carbonate, Colour, "
			+ "Colour_Modifier_ID, Colour_Modifier, Primary_Colour_ID, Primary_Colour, Secondary_Colour_ID, Secondary_Colour, Wet, Rock_Nature, Deposition_Env, Correspondence "
			+ "FROM Sample_All_View WHERE Sample_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next())
				throw new SQLException("Cannot find record in database with this id: " + this.id);
			values[Sample.FEATURE_ID] = new Integer(rs.getInt(1));
			values[Sample.SAMPLE_ID] = new Integer(rs.getInt(2));
			values[Sample.FEATURE_TYPE] = rs.getString(3);
			values[Sample.SAMPLE_NAME] = rs.getString(4);
			values[Sample.FR_ID] = ((rs.getString(5) != null) ? new Integer(rs.getInt(5)) : null);
			values[Sample.FR_NUMBER] = rs.getString(6);
			values[Sample.YARD_FR_ID] = ((rs.getString(7) != null) ? new Integer(rs.getInt(7)) : null);
			values[Sample.YARD_FR_NUMBER] = rs.getString(8);
			values[Sample.FEATURE_NAME] = rs.getString(9);
			values[Sample.MAP_SHEET] = rs.getString(10);
			values[Sample.SERIAL_NUMBER] = ((rs.getString(11) != null) ? new Integer(rs.getInt(11)) : null);
			values[Sample.RECOLLECTION_NUMBER] = rs.getString(12);
			values[Sample.YARD_MAP_SHEET] = rs.getString(13);
			values[Sample.YARD_SERIAL_NUMBER] = ((rs.getString(14) != null) ? new Integer(rs.getInt(14)) : null);
			values[Sample.YARD_RECOLLECTION_NUMBER] = rs.getString(15);
			values[Sample.DRILLHOLE_DEPTH] = rs.getString(16);
			values[Sample.TOP_DEPTH] = ((rs.getString(17) != null) ? new Double(rs.getDouble(17)) : null);
			values[Sample.BOTTOM_DEPTH] = ((rs.getString(18) != null) ? new Double(rs.getDouble(18)) : null);
			values[Sample.DRILL_TYPE_ID] = ((rs.getString(19) != null) ? new Integer(rs.getInt(19)) : null);
			values[Sample.DRILL_TYPE] = rs.getString(20);
			values[Sample.MASTERFILE_ID] = ((rs.getString(21) != null) ? new Integer(rs.getInt(21)) : null);
			values[Sample.MASTERFILE_NAME] = rs.getString(22);
			values[Sample.REG_AREA_ID] = ((rs.getString(23) != null) ? new Integer(rs.getInt(23)) : null);
			values[Sample.REG_AREA_NAME] = rs.getString(24);
			values[Sample.REG_AREA_CODE] = rs.getString(25);
			values[Sample.AUDIT_ID] = new Integer(rs.getInt(26));
			values[Sample.STATUS] = rs.getString(27);
			values[Sample.LAST_CHANGE] = rs.getDate(28);
			values[Sample.WORKING_FOLDER_ID] = ((rs.getString(29) != null) ? new Integer(rs.getInt(29)) : null);
			values[Sample.WORKING_COMMENTS] = rs.getString(30);
			values[Sample.SECURITY_CLASS_ID] = ((rs.getString(31) != null) ? new Integer(rs.getInt(31)) : null);
			values[Sample.SITE_ID] = ((rs.getString(32) != null) ? new Integer(rs.getInt(32)) : null);
			values[Sample.COUNTRY_CODE] = rs.getString(33);
			values[Sample.COUNTRY_NAME] = rs.getString(34);
			values[Sample.LATITUDE] = ((rs.getString(35) != null) ? new Double(rs.getDouble(35)) : null);
			values[Sample.LONGITUDE] = ((rs.getString(36) != null) ? new Double(rs.getDouble(36)) : null);
			values[Sample.QMAP_SHEET] = rs.getString(37);
			values[Sample.NZMG_SHEET] = rs.getString(38);
			values[Sample.NZMG_EAST] = ((rs.getString(39) != null) ? new Double(rs.getDouble(39)) : null);
			values[Sample.NZMG_NORTH] =	((rs.getString(40) != null)	? new Double(rs.getDouble(40)) : null);
			values[Sample.METHOD_ID] = ((rs.getString(41) != null) ? new Integer(rs.getInt(41)) : null);
			values[Sample.METHOD] = rs.getString(42);
			values[Sample.ACCURACY] = ((rs.getString(43) != null) ? new Double(rs.getDouble(43)): null);
			values[Sample.ORIG_SYSTEM_ID] = ((rs.getString(44) != null) ? new Integer(rs.getInt(44)) : null);
			values[Sample.COORD_SYSTEM] = rs.getString(45);
			values[Sample.ORIG_COORD] = rs.getString(46);
			values[Sample.LOCALITY] = rs.getString(47);
			values[Sample.DRILLHOLE_LICENCE_NAME] = rs.getString(48);
			values[Sample.PERSON_ID] = ((rs.getString(49) != null) ? new Integer(rs.getInt(49)) : null);
			values[Sample.PERSON] = rs.getString(50);
			values[Sample.START_DATE] = rs.getDate(51);
			values[Sample.START_DATE_ROUNDING] = rs.getString(52);
			values[Sample.FINISH_DATE] = rs.getDate(53);
			values[Sample.FINISH_DATE_ROUNDING] = rs.getString(54);
			values[Sample.DATUM_TYPE] = rs.getString(55);
			values[Sample.DATUM_ELEVATION] = ((rs.getString(56) != null) ? new Double(rs.getDouble(56))	: null);
			values[Sample.START_DEPTH] = ((rs.getString(57) != null) ? new Double(rs.getDouble(57))	: null);
			values[Sample.FINISH_DEPTH] = ((rs.getString(58) != null) ? new Double(rs.getDouble(58)) : null);
			values[Sample.COLLECTION_DATE] = rs.getDate(59);
			values[Sample.COLLECTION_DATE_ROUNDING] = rs.getString(60);
			values[Sample.STRAT_UNIT] = rs.getString(61);
			values[Sample.IN_PLACE] = rs.getString(62);
			values[Sample.NOT_COLLECTED] = rs.getString(63);
			values[Sample.SIGNIFICANCE] = rs.getString(64);
			values[Sample.INFERRED_STAGE_ID] = ((rs.getString(65) != null) ? new Integer(rs.getInt(65)) : null);
			values[Sample.INFERRED_STAGE] = rs.getString(66);
			values[Sample.INFERRED_STAGE_ABBREV] = rs.getString(67);
			values[Sample.INFERRED_STAGE_LOWER_ID] = ((rs.getString(68) != null) ? new Integer(rs.getInt(68)) : null);
			values[Sample.INFERRED_STAGE_LOWER] = rs.getString(69);
			values[Sample.INFERRED_STAGE_LOWER_MOD] = rs.getString(70);
			values[Sample.INFERRED_STAGE_UPPER_ID] = ((rs.getString(71) != null) ? new Integer(rs.getInt(71)) : null);
			values[Sample.INFERRED_STAGE_UPPER] = rs.getString(72);
			values[Sample.INFERRED_STAGE_UPPER_MOD] = rs.getString(73);
			values[Sample.INFERRED_AGE_START] = ((rs.getString(74) != null) ? new Double(rs.getDouble(74))	: null);
			values[Sample.INFERRED_AGE_STOP] = ((rs.getString(75) != null) ? new Double(rs.getDouble(75)) : null);
			values[Sample.KNOWN_STAGE_ID] = ((rs.getString(76) != null) ? new Integer(rs.getInt(76)) : null);
			values[Sample.KNOWN_STAGE] = rs.getString(77);
			values[Sample.KNOWN_STAGE_ABBREV] = rs.getString(78);
			values[Sample.KNOWN_STAGE_LOWER_ID] = ((rs.getString(79) != null) ? new Integer(rs.getInt(79)) : null);
			values[Sample.KNOWN_STAGE_LOWER] = rs.getString(80);
			values[Sample.KNOWN_STAGE_LOWER_MOD] = rs.getString(81);
			values[Sample.KNOWN_STAGE_UPPER_ID] = ((rs.getString(82) != null) ? new Integer(rs.getInt(82)) : null);
			values[Sample.KNOWN_STAGE_UPPER] = rs.getString(83);
			values[Sample.KNOWN_STAGE_UPPER_MOD] = rs.getString(84);
			values[Sample.KNOWN_AGE_START] = ((rs.getString(85) != null) ? new Double(rs.getDouble(85))	: null);
			values[Sample.KNOWN_AGE_STOP] = ((rs.getString(86) != null) ? new Double(rs.getDouble(86)) : null);
			values[Sample.COLUMN_MAP] = rs.getString(87);
			values[Sample.DIP] = ((rs.getString(88) != null) ? new Double(rs.getDouble(88)) : null);
			values[Sample.DIP_DIRECTION] = rs.getString(89);
			values[Sample.STRIKE] = ((rs.getString(90) != null) ? new Double(rs.getDouble(90)) : null);
			values[Sample.FACING] = rs.getString(91);
			values[Sample.GRAINSIZE] = rs.getString(92);
			values[Sample.PRIMARY_GRAINSIZE_ID] = ((rs.getString(93) != null) ? new Integer(rs.getInt(93)) : null);
			values[Sample.PRIMARY_GRAINSIZE] = rs.getString(94);
			values[Sample.SECONDARY_GRAINSIZE_ID] = ((rs.getString(95) != null) ? new Integer(rs.getInt(95)) : null);
			values[Sample.SECONDARY_GRAINSIZE] = rs.getString(96);			
			values[Sample.COMPARATOR_USED] = rs.getString(97);
			values[Sample.BED_THICK_ID] = ((rs.getString(98) != null) ? new Integer(rs.getInt(98)) : null);
			values[Sample.BED_THICKNESS] = rs.getString(99);
			values[Sample.BEDDING] = rs.getString(100);
			values[Sample.PRIMARY_BEDDING_ID] = ((rs.getString(101) != null) ? new Integer(rs.getInt(101)) : null);
			values[Sample.PRIMARY_BEDDING] = rs.getString(102);
			values[Sample.SECONDARY_BEDDING_ID] = ((rs.getString(103) != null) ? new Integer(rs.getInt(103)) : null);
			values[Sample.SECONDARY_BEDDING] = rs.getString(104);				
			values[Sample.WEATHERING_ID] = ((rs.getString(105) != null) ? new Integer(rs.getInt(105)) : null);
			values[Sample.WEATHERING] = rs.getString(106);
			values[Sample.HARDNESS_ID] = ((rs.getString(107) != null) ? new Integer(rs.getInt(107)) : null);
			values[Sample.HARDNESS] = rs.getString(108);
			values[Sample.CARBONATE_ID] = ((rs.getString(109) != null) ? new Integer(rs.getInt(109)) : null);
			values[Sample.CARBONATE] = rs.getString(110);
			values[Sample.COLOUR] = rs.getString(111);
			values[Sample.COLOUR_MODIFIER_ID] = ((rs.getString(112) != null) ? new Integer(rs.getInt(112)) : null);
			values[Sample.COLOUR_MODIFIER] = rs.getString(113);
			values[Sample.PRIMARY_COLOUR_ID] = ((rs.getString(114) != null) ? new Integer(rs.getInt(114)) : null);
			values[Sample.PRIMARY_COLOUR] = rs.getString(115);
			values[Sample.SECONDARY_COLOUR_ID] = ((rs.getString(116) != null) ? new Integer(rs.getInt(116)) : null);
			values[Sample.SECONDARY_COLOUR] = rs.getString(117);	
			values[Sample.WET] = rs.getString(118);
			values[Sample.ROCK_NATURE] = rs.getString(119);
			values[Sample.DEPOSITION_ENV] = rs.getString(120);
			values[Sample.CORRESPONDENCE] = rs.getString(121);
			rs.close();
			
			query =
				"SELECT Record_ID, Record_Type, Status FROM Record_All_View WHERE Sample_ID = ? ORDER BY Record_Type, Record_Name";
			rs = conn.executeQuery(query, types, data);
			Vector rec = new Vector();
			Vector wRec = new Vector();
			while (rs.next()) {
				rec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
				if (!rs.getString(3).equals("approved"))
					wRec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			values[Sample.RECORDS] = rec;
			values[Sample.WORKING_RECORDS] = wRec;
			rs.close();
			
			query =
				"SELECT Person_ID, Name FROM Person_View NATURAL JOIN Collector WHERE Sample_ID = ? ORDER BY Family_Name, Given_Name";
			rs = conn.executeQuery(query, types, data);
			Vector collVec = new Vector();
			while (rs.next()) {
				collVec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			rs.close();
			values[Sample.COLLECTOR] = ((collVec.size() > 0) ? collVec : null);
			rs.close();			

			query =
				"SELECT SENT_TO, FOSSIL_GROUP_ID, FOSSIL_GROUP, SENT_DATE, DATE_ROUNDING, PERSON_ID, PERSON_NAME, LAB_ID, LAB_NAME, COMMENTS "
					+ "FROM Sent_To_View WHERE Sample_ID = ?";
			rs = conn.executeQuery(query, types, data);
			Vector sentToVec = new Vector();
			while (rs.next()) {
				SentTo sentTo = new SentTo(rs.getString(1));
				sentTo.setFossilGroupID(
					((rs.getString(2) != null)
						? new Integer(rs.getInt(2))
						: null));
				sentTo.setFossilGroup(rs.getString(3));
				sentTo.setDate(rs.getDate(4));
				sentTo.setDateRounding(rs.getString(5));
				sentTo.setPersonID(
					((rs.getString(6) != null)
						? new Integer(rs.getInt(6))
						: null));
				sentTo.setPerson(rs.getString(7));
				sentTo.setLabID(
					((rs.getString(8) != null)
						? new Integer(rs.getInt(8))
						: null));
				sentTo.setLab(rs.getString(9));
				sentTo.setComments(rs.getString(10));
				sentToVec.add(sentTo);
			}
			rs.close();
			values[Sample.SENT_TO] = ((sentToVec.size() > 0) ? sentToVec : null);
			rs.close();

			query =
				"SELECT RELATIONSHIP, RELATIONSHIP_TYPE, DISTANCE_RELATION, RELATED_FEATURE_ID, RELATED_SAMPLE_NAME, STRAT_UNIT, "
					+ "DISTANCE, DISTANCE_RANGE, DISTANCE_MOD, RELATION_TYPE_ID, RELATION_TYPE "
					+ "FROM Relationship_View WHERE Sample_ID = ?";
			rs = conn.executeQuery(query, types, data);
			Vector nearRelVec = new Vector();
			Vector sampRelVec = new Vector();
			Vector stratRelVec = new Vector();
			while (rs.next()) {
				Relationship rel = new Relationship(rs.getString(1));
				rel.setRelationshipType(rs.getString(2));
				rel.setDistanceRelation(rs.getString(3));
				rel.setRelatedFeatureID(
					((rs.getString(4) != null)
						? new Integer(rs.getInt(4))
						: null));
				rel.setRelatedSampleName(rs.getString(5));
				rel.setRelatedStratUnit(rs.getString(6));
				rel.setDistance(
					((rs.getString(7) != null)
						? new Double(rs.getDouble(7))
						: null));
				rel.setDistanceRange(
					((rs.getString(8) != null)
						? new Double(rs.getDouble(8))
						: null));
				rel.setDistanceMod(rs.getString(9));
				rel.setRelationTypeID(
					((rs.getString(10) != null)
						? new Integer(rs.getInt(10))
						: null));
				rel.setRelationType(rs.getString(11));
				if (rel.getRelationshipType().equals("Strat")) {
					stratRelVec.add(rel);
				} else if (rel.getRelationType().equals("nearby")) {
					nearRelVec.add(rel);
				} else {
					sampRelVec.add(rel);
				}
			}
			values[Sample.RELATIONSHIP_NEARBY] = ((nearRelVec.size() > 0) ? nearRelVec : null);
			values[Sample.RELATIONSHIP_SAMPLE] = ((sampRelVec.size() > 0) ? sampRelVec : null);
			values[Sample.RELATIONSHIP_STRAT] = ((stratRelVec.size() > 0) ? stratRelVec : null);
			rs.close();

			query =
				"SELECT SEDIMENTARY_FEATURE, SED_FEATURE_ID, SED_FEATURE, ABUNDANT "
					+ "FROM Sedimentary_Feature_View WHERE Sample_ID = ?";
			rs = conn.executeQuery(query, types, data);
			Vector sfVec = new Vector();
			while (rs.next()) {
				SedFeature sf = new SedFeature(rs.getString(1));
				sf.setSedFeatureId(new Integer(rs.getInt(2)));
				sf.setFeat(rs.getString(3));
				sf.setAbundant(rs.getString(4));
				sfVec.add(sf);
			}
			values[Sample.SED_FEATURE] = ((sfVec.size() > 0) ? sfVec : null);
			rs.close();
						
			conn.releaseStatement();
		} catch (SQLException _e) {
			pool.removeMe(this);
			throw DBUtils.fixSQLException(_e, query, conn);
		}
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	protected int getAsInt(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return ((Integer) thing).intValue();
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	protected double getAsDouble(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return ((Double) thing).doubleValue();
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	protected java.util.Date getAsDate(int field)
		throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return (java.util.Date) thing;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	protected Vector getAsVector(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return (Vector) thing;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	protected String getAsString(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			if (thing == null) {
				return null;
			}
			return thing.toString();
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	protected Object get(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return thing;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Inner class used for object pooling.
	 */
	protected static class DataFinder implements Finder {
		int id;
		public DataFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (o instanceof SampleData && ((SampleData) o).id == this.id);
		}
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	protected static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	protected static void purge() {
		pool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	protected static SampleData getData(int id, PageState state, boolean forceRefresh)
		throws SQLException, IOException {
		SampleData s = (SampleData) pool.retrieve(new DataFinder(id));
		if (forceRefresh && s != null) {
			pool.removeMe(s);
			s = null;
		}
		if (s == null) {
			s = new SampleData(id, state);
		}
		return s;
	}

	public String toString() {
		return (values[5]).toString();
	}

	//public void finalize() throws Throwable {
	//	pool.removeMe(this);
	//}

}