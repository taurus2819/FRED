package nz.cri.gns.db.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.fred.FREDUtils;
import nz.cri.gns.db.pool.Finder;
import nz.cri.gns.db.pool.Pool;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Sample_Property_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 * Pooling is used so cannot instantiate directly - use static getDataRecord method instead.
 */
public class SampPropRecord extends Record {

	public static final int RECORD_ID = 0;
	public static final int FEATURE_ID = 1;
	public static final int SAMPLE_ID = 2;
	public static final int FEATURE_STATUS = 3;
	public static final int FEATURE_SECURITY_CLASS_ID = 4;
	public static final int AUDIT_ID = 5;
	public static final int STATUS = 6;
	public static final int SECURITY_CLASS_ID = 7;
	public static final int SAMPLE_NAME = 8;
	public static final int DRILLHOLE_DEPTH = 9;
	public static final int COLLECTOR = 10;
	public static final int COLLECTION_DATE = 11;
	public static final int DATE_ROUNDING = 12;
	public static final int STRAT_UNIT = 13;
	public static final int IN_PLACE = 14;
	public static final int SENT_TO = 15;
	public static final int NOT_COLLECTED = 16;
	public static final int SIGNIFICANCE = 17;
	public static final int INFERRED_STAGE_ID = 18;
	public static final int INFERRED_STAGE = 19;
	public static final int INFERRED_STAGE_ABBREV = 20;
	public static final int INFERRED_STAGE_LOWER_ID = 21;
	public static final int INFERRED_STAGE_LOWER = 22;
	public static final int INFERRED_STAGE_LOWER_MOD = 23;
	public static final int INFERRED_STAGE_UPPER_ID = 24;
	public static final int INFERRED_STAGE_UPPER = 25;
	public static final int INFERRED_STAGE_UPPER_MOD = 26;
	public static final int INFERRED_AGE_START = 27;
	public static final int INFERRED_AGE_STOP = 28;
	public static final int KNOWN_STAGE_ID = 29;
	public static final int KNOWN_STAGE = 30;
	public static final int KNOWN_STAGE_ABBREV = 31;
	public static final int KNOWN_STAGE_LOWER_ID = 32;
	public static final int KNOWN_STAGE_LOWER = 33;
	public static final int KNOWN_STAGE_LOWER_MOD = 34;
	public static final int KNOWN_STAGE_UPPER_ID = 35;
	public static final int KNOWN_STAGE_UPPER = 36;
	public static final int KNOWN_STAGE_UPPER_MOD = 37;
	public static final int KNOWN_AGE_START = 38;
	public static final int KNOWN_AGE_STOP = 39;
	public static final int RELATIONSHIP = 40;
	public static final int RELATIONSHIP_NEARBY = 77;
	public static final int RELATIONSHIP_SAMPLE = 78;
	public static final int RELATIONSHIP_STRAT = 79;
	public static final int COLUMN_MAP = 41;
	public static final int DIP = 42;
	public static final int DIP_DIRECTION = 43;
	public static final int STRIKE = 44;
	public static final int FACING = 45;
	public static final int GRAINSIZE = 46;
	public static final int PRIMARY_GRAINSIZE_ID = 47;
	public static final int PRIMARY_GRAINSIZE = 48;
	public static final int SECONDARY_GRAINSIZE_ID = 49;
	public static final int SECONDARY_GRAINSIZE = 50;
	public static final int COMPARATOR_USED = 51;
	public static final int BED_THICK_ID = 52;
	public static final int BED_THICKNESS = 53;
	public static final int BEDDING = 54;
	public static final int PRIMARY_BEDDING_ID = 55;
	public static final int PRIMARY_BEDDING = 56;
	public static final int SECONDARY_BEDDING_ID = 57;
	public static final int SECONDARY_BEDDING = 58;
	public static final int WEATHERING_ID = 59;
	public static final int WEATHERING = 60;
	public static final int HARDNESS_ID = 61;
	public static final int HARDNESS = 62;
	public static final int CARBONATE_ID = 63;
	public static final int CARBONATE = 64;
	public static final int COLOUR = 65;
	public static final int COLOUR_MODIFIER_ID = 66;
	public static final int COLOUR_MODIFIER = 67;
	public static final int PRIMARY_COLOUR_ID = 68;
	public static final int PRIMARY_COLOUR = 69;
	public static final int SECONDARY_COLOUR_ID = 70;
	public static final int SECONDARY_COLOUR = 71;
	public static final int WET = 72;
	public static final int SED_FEATURE = 73;
	public static final int ROCK_NATURE = 74;
	public static final int DEPOSITION_ENV = 75;
	public static final int CORRESPONDENCE = 76;

	protected static Pool pool = new Pool();
	protected int id;
	private Object[] values = new Object[80];
	private int[] types = { Types.NUMERIC };
	private Object[] data = new Object[1];

	/**
	 * Cannot be called directly. use static getContactPerson method instead.
	 */
	protected SampPropRecord(int id, PageState state)
		throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		this.id = id;
		pool.add(this);
		String query =
			"SELECT RECORD_ID, FEATURE_ID, SAMPLE_ID, FEATURE_STATUS, FEATURE_SECURITY_CLASS_ID, AUDIT_ID, "
				+ "STATUS, SECURITY_CLASS_ID, SAMPLE_NAME, DRILLHOLE_DEPTH, COLLECTION_DATE, DATE_ROUNDING, "
				+ "STRAT_UNIT, IN_PLACE, NOT_COLLECTED, SIGNIFICANCE, INFERRED_STAGE_ID, INFERRED_STAGE, "
				+ "INFERRED_STAGE_ABBREV, INFERRED_STAGE_LOWER_ID, INFERRED_STAGE_LOWER, INFERRED_STAGE_LOWER_MOD, "
				+ "INFERRED_STAGE_UPPER_ID, INFERRED_STAGE_UPPER, INFERRED_STAGE_UPPER_MOD, INFERRED_AGE_START, "
				+ "INFERRED_AGE_STOP, KNOWN_STAGE_ID, KNOWN_STAGE, KNOWN_STAGE_ABBREV, KNOWN_STAGE_LOWER_ID, KNOWN_STAGE_LOWER, "
				+ "KNOWN_STAGE_LOWER_MOD, KNOWN_STAGE_UPPER_ID, KNOWN_STAGE_UPPER, KNOWN_STAGE_UPPER_MOD, KNOWN_AGE_START, "
				+ "KNOWN_AGE_STOP, COLUMN_MAP, DIP, DIP_DIRECTION, STRIKE, FACING, GRAINSIZE, PRIMARY_GRAINSIZE_ID, "
				+ "PRIMARY_GRAINSIZE, SECONDARY_GRAINSIZE_ID, SECONDARY_GRAINSIZE, COMPARATOR_USED, BED_THICK_ID, "
				+ "BED_THICKNESS, BEDDING, PRIMARY_BEDDING_ID, PRIMARY_BEDDING, SECONDARY_BEDDING_ID, SECONDARY_BEDDING, "
				+ "WEATHERING_ID, WEATHERING, HARDNESS_ID, HARDNESS, CARBONATE_ID, CARBONATE, COLOUR, COLOUR_MODIFIER_ID, "
				+ "COLOUR_MODIFIER, PRIMARY_COLOUR_ID, PRIMARY_COLOUR, SECONDARY_COLOUR_ID, SECONDARY_COLOUR, WET, "
				+ "SED_FEATURE, ROCK_NATURE, DEPOSITION_ENV, CORRESPONDENCE "
				+ "FROM Sample_Property_All_View WHERE Record_ID = ?";
		data[0] = new Integer(this.id);
		try {
			ResultSet rs = conn.executeQuery(query, types, data);
			if (!rs.next()) {
				throw new SQLException(
					"Cannot find record in database with this id: " + this.id);
			}
			values[0] = new Integer(rs.getInt(1));
			values[1] = new Integer(rs.getInt(2));
			values[2] = new Integer(rs.getInt(3));
			values[3] = rs.getString(4);
			values[4] =
				((rs.getString(5) != null) ? new Integer(rs.getInt(5)) : null);
			values[5] =
				((rs.getString(6) != null) ? new Integer(rs.getInt(6)) : null);
			values[6] = rs.getString(7);
			values[7] =
				((rs.getString(8) != null) ? new Integer(rs.getInt(8)) : null);
			values[8] = rs.getString(9);
			values[9] = rs.getString(10);
			values[11] = rs.getDate(11);
			values[12] = rs.getString(12);
			values[13] = rs.getString(13);
			values[14] = rs.getString(14);
			values[16] = rs.getString(15);
			values[17] = rs.getString(16);
			values[18] =
				((rs.getString(17) != null)
					? new Integer(rs.getInt(17))
					: null);
			values[19] = rs.getString(18);
			values[20] = rs.getString(19);
			values[21] =
				((rs.getString(20) != null)
					? new Integer(rs.getInt(20))
					: null);
			values[22] = rs.getString(21);
			values[23] = rs.getString(22);
			values[24] =
				((rs.getString(23) != null)
					? new Integer(rs.getInt(23))
					: null);
			values[25] = rs.getString(24);
			values[26] = rs.getString(25);
			values[27] =
				((rs.getString(26) != null)
					? new Double(rs.getDouble(26))
					: null);
			values[28] =
				((rs.getString(27) != null)
					? new Double(rs.getDouble(27))
					: null);
			values[29] =
				((rs.getString(28) != null)
					? new Integer(rs.getInt(28))
					: null);
			values[30] = rs.getString(29);
			values[31] = rs.getString(30);
			values[32] =
				((rs.getString(31) != null)
					? new Integer(rs.getInt(31))
					: null);
			values[33] = rs.getString(32);
			values[34] = rs.getString(33);
			values[35] =
				((rs.getString(34) != null)
					? new Integer(rs.getInt(34))
					: null);
			values[36] = rs.getString(35);
			values[37] = rs.getString(36);
			values[38] =
				((rs.getString(37) != null)
					? new Double(rs.getDouble(37))
					: null);
			values[39] =
				((rs.getString(38) != null)
					? new Double(rs.getDouble(38))
					: null);
			values[41] = rs.getString(39);
			values[42] =
				((rs.getString(40) != null)
					? new Integer(rs.getInt(40))
					: null);
			values[43] = rs.getString(41);
			values[44] =
				((rs.getString(42) != null)
					? new Integer(rs.getInt(42))
					: null);
			values[45] = rs.getString(43);
			values[46] = rs.getString(44);
			values[47] =
				((rs.getString(45) != null)
					? new Integer(rs.getInt(45))
					: null);
			values[48] = rs.getString(46);
			values[49] =
				((rs.getString(47) != null)
					? new Integer(rs.getInt(47))
					: null);
			values[50] = rs.getString(48);
			values[51] = rs.getString(49);
			values[52] =
				((rs.getString(50) != null)
					? new Integer(rs.getInt(50))
					: null);
			values[53] = rs.getString(51);
			values[54] = rs.getString(52);
			values[55] =
				((rs.getString(53) != null)
					? new Integer(rs.getInt(53))
					: null);
			values[56] = rs.getString(54);
			values[57] =
				((rs.getString(55) != null)
					? new Integer(rs.getInt(55))
					: null);
			values[58] = rs.getString(56);
			values[59] =
				((rs.getString(57) != null)
					? new Integer(rs.getInt(57))
					: null);
			values[60] = rs.getString(58);
			values[61] =
				((rs.getString(59) != null)
					? new Integer(rs.getInt(59))
					: null);
			values[62] = rs.getString(60);
			values[63] =
				((rs.getString(61) != null)
					? new Integer(rs.getInt(61))
					: null);
			values[64] = rs.getString(62);
			values[65] = rs.getString(63);
			values[66] =
				((rs.getString(64) != null)
					? new Integer(rs.getInt(64))
					: null);
			values[67] = rs.getString(65);
			values[68] =
				((rs.getString(66) != null)
					? new Integer(rs.getInt(66))
					: null);
			values[69] = rs.getString(67);
			values[70] =
				((rs.getString(68) != null)
					? new Integer(rs.getInt(68))
					: null);
			values[71] = rs.getString(69);
			values[72] = rs.getString(70);
			values[74] = rs.getString(71);
			values[75] = rs.getString(72);
			values[76] = rs.getString(73);
			rs.close();

			query =
				"SELECT Person_ID, Name FROM Person_View NATURAL JOIN Collector WHERE Record_ID = ? ORDER BY Family_Name, Given_Name";
			rs = conn.executeQuery(query, types, data);
			Vector collVec = new Vector();
			while (rs.next()) {
				collVec.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
			}
			rs.close();
			values[10] = ((collVec.size() > 0) ? collVec : null);
			rs.close();

			query =
				"SELECT SENT_TO, FOSSIL_GROUP_ID, FOSSIL_GROUP, SENT_DATE, DATE_ROUNDING, PERSON_ID, PERSON_NAME, LAB_ID, LAB_NAME, COMMENTS "
					+ "FROM Sent_To_View WHERE Record_ID = ?";
			rs = conn.executeQuery(query, types, data);
			Vector sentToVec = new Vector();
			while (rs.next()) {
				SentTo sentTo = new SentTo(rs.getString(1));
				sentTo.setFossilGroupId(
					((rs.getString(2) != null)
						? new Integer(rs.getInt(2))
						: null));
				sentTo.setFossilGroup(rs.getString(3));
				sentTo.setDate(rs.getDate(4));
				sentTo.setDateRounding(rs.getString(5));
				sentTo.setPersonId(
					((rs.getString(6) != null)
						? new Integer(rs.getInt(6))
						: null));
				sentTo.setPerson(rs.getString(7));
				sentTo.setLabId(
					((rs.getString(8) != null)
						? new Integer(rs.getInt(8))
						: null));
				sentTo.setComments(rs.getString(9));
				sentToVec.add(sentTo);
			}
			rs.close();
			values[15] = ((sentToVec.size() > 0) ? sentToVec : null);
			rs.close();

			query =
				"SELECT RELATIONSHIP, RELATIONSHIP_TYPE, DISTANCE_RELATION, RELATED_FEATURE_ID, RELATED_SAMPLE_NAME, STRAT_UNIT, "
					+ "DISTANCE, DISTANCE_RANGE, DISTANCE_MOD, RELATION_TYPE_ID, RELATION_TYPE "
					+ "FROM Relationship_View WHERE Record_ID = ?";
			rs = conn.executeQuery(query, types, data);
			Vector relVec = new Vector();
			Vector nearRelVec = new Vector();
			Vector sampRelVec = new Vector();
			Vector stratRelVec = new Vector();
			while (rs.next()) {
				Relationship rel = new Relationship(rs.getString(1));
				rel.setRelationshipType(rs.getString(2));
				rel.setDistanceRelation(rs.getString(3));
				rel.setRelatedFeatureId(
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
				rel.setRelationTypeId(
					((rs.getString(10) != null)
						? new Integer(rs.getInt(10))
						: null));
				rel.setRelationType(rs.getString(11));
				relVec.add(rel);
				if (rel.getRelationshipType().equals("Strat")) {
					stratRelVec.add(rel);
				} else if (rel.getRelationType().equals("nearby")) {
					nearRelVec.add(rel);
				} else {
					sampRelVec.add(rel);
				}
			}
			rs.close();
			values[40] = ((relVec.size() > 0) ? relVec : null);
			values[77] = ((nearRelVec.size() > 0) ? nearRelVec : null);
			values[78] = ((sampRelVec.size() > 0) ? sampRelVec : null);
			values[79] = ((stratRelVec.size() > 0) ? stratRelVec : null);
			query =
				"SELECT SEDIMENTARY_FEATURE, SED_FEATURE_ID, SED_FEATURE, ABUNDANT "
					+ "FROM Sedimentary_Feature_View WHERE Record_ID = ?";
			rs = conn.executeQuery(query, types, data);
			Vector sfVec = new Vector();
			while (rs.next()) {
				SedFeature sf = new SedFeature(rs.getString(1));
				sf.setSedFeatureId(new Integer(rs.getInt(2)));
				sf.setFeat(rs.getString(3));
				sf.setAbundant(rs.getString(4));
				sfVec.add(sf);
			}
			rs.close();
			values[73] = ((sfVec.size() > 0) ? sfVec : null);

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
	public int getAsInt(int field) throws IllegalArgumentException {
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
	public double getAsDouble(int field) throws IllegalArgumentException {
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
	public java.util.Date getAsDate(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return (java.util.Date) thing;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws IllegalArgumentException {
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
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return (Vector) thing;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws IllegalArgumentException {
		try {
			Object thing = values[field];
			return thing;
		}
		catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Inner class used for object pooling.
	 */
	public static class DataFinder implements Finder {
		int id;
		public DataFinder(int id) {
			this.id = id;
		}
		public boolean isObject(Object o) {
			return (
				o instanceof SampPropRecord
					&& ((SampPropRecord) o).id == this.id);
		}

	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		pool.removeAllElements();
	}

	/**
	 *  Use this to get a new instance of this class. 
	 * @throws SQLException if there is not sample for given ID, as well as normal SQLExceptions.
	 */
	public static SampPropRecord getData(int id, User user, PageState state) throws SQLException, IOException, AccessDeniedException {
		SampPropRecord sp = (SampPropRecord) pool.retrieve(new DataFinder(id));
		if (sp == null) {
			sp = new SampPropRecord(id, state);
		}
		if (!FREDUtils.isAllowedLocality(user, sp.getAsString(FEATURE_SECURITY_CLASS_ID), sp.getAsString(FEATURE_STATUS), sp.getAsString(FEATURE_ID), state)
				|| !FREDUtils.isAllowedRecord(user, sp.getAsString(SECURITY_CLASS_ID), sp.getAsString(STATUS), sp.getAsString(RECORD_ID), state)) {
			throw new AccessDeniedException();
		}
		return sp;
	}

	public String toString() {
		return (values[0]).toString();
	}
	
	public void finalize() throws Throwable {
		pool.removeMe(this);
	}

}