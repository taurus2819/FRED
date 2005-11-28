package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Vector;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.db.metadata.MetadataRecord;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

/**
 * Class that represents a Sample_View record.
 * Fields map to columns in database - use as arguments for the get methods.
 */
public class Sample {

	public static final int FEATURE_ID = 0;
	public static final int SAMPLE_ID = 1;
	public static final int FEATURE_TYPE = 2;
	public static final int SAMPLE_NAME = 3;
	public static final int FR_ID = 4;
	public static final int FR_NUMBER = 5;
	public static final int YARD_FR_ID = 6;
	public static final int YARD_FR_NUMBER = 7;
	public static final int FEATURE_NAME = 8;
	public static final int MAP_SHEET = 9;
	public static final int SERIAL_NUMBER = 10;
	public static final int RECOLLECTION_NUMBER = 11;
	public static final int YARD_MAP_SHEET = 12;
	public static final int YARD_SERIAL_NUMBER = 13;
	public static final int YARD_RECOLLECTION_NUMBER = 14;
	public static final int DRILLHOLE_DEPTH = 15;
	public static final int TOP_DEPTH = 16;
	public static final int BOTTOM_DEPTH = 17;
	public static final int DRILL_TYPE_ID = 18;
	public static final int DRILL_TYPE = 19;
	public static final int MASTERFILE_ID = 20;
	public static final int MASTERFILE_NAME = 21;
	public static final int REG_AREA_ID = 22;
	public static final int REG_AREA_NAME = 23;
	public static final int REG_AREA_CODE = 24;
	public static final int FEATURE_AUDIT_ID = 25;
	public static final int FEATURE_STATUS = 26;
	public static final int FEATURE_CREATED_DATE = 27;
	public static final int FEATURE_WORKING_FOLDER_ID = 28;
	public static final int FEATURE_WORKING_COMMENTS = 29;
	public static final int FEATURE_SECURITY_CLASS_ID = 30;
	public static final int SAMPLE_AUDIT_ID = 129;
	public static final int SAMPLE_STATUS = 130;
	public static final int SAMPLE_CREATED_DATE = 131;
	public static final int SAMPLE_WORKING_FOLDER_ID = 132;
	public static final int SAMPLE_WORKING_COMMENTS = 133;
	public static final int SAMPLE_SECURITY_CLASS_ID = 134;
	public static final int SITE_ID = 31;
	public static final int COUNTRY_CODE = 32;
	public static final int COUNTRY_NAME = 33;
	public static final int LATITUDE = 34;
	public static final int LONGITUDE = 35;
	public static final int QMAP_SHEET = 36;
	public static final int NZMG_SHEET = 37;
	public static final int NZMG_EAST = 38;
	public static final int NZMG_NORTH = 39;
	public static final int METHOD_ID = 40;
	public static final int METHOD = 41;
	public static final int ACCURACY = 42;
	public static final int ORIG_SYSTEM_ID = 43;
	public static final int COORD_SYSTEM = 44;
	public static final int ORIG_COORD = 45;
	public static final int MAP_YEAR = 135;
	public static final int LOCALITY = 46;
	public static final int DRILLHOLE_LICENCE_NAME = 47;
	public static final int PERSON_ID = 48;
	public static final int PERSON = 49;
	public static final int START_DATE = 50;
	public static final int START_DATE_ROUNDING = 51;
	public static final int FINISH_DATE = 52;
	public static final int FINISH_DATE_ROUNDING = 53;
	public static final int DATUM_TYPE = 54;
	public static final int DATUM_ELEVATION = 55;
	public static final int START_DEPTH = 56;
	public static final int FINISH_DEPTH = 57;
	public static final int RECORDS = 58;
	public static final int WORKING_RECORDS = 59;

	//SampPropRecord
	public static final int COLLECTOR = 60;
	public static final int COLLECTION_DATE = 61;
	public static final int COLLECTION_DATE_ROUNDING = 62;
	public static final int STRAT_UNIT = 63;
	public static final int IN_PLACE = 64;
	public static final int SENT_TO = 65;
	public static final int NOT_COLLECTED = 66;
	public static final int SIGNIFICANCE = 67;
	public static final int INFERRED_STAGE_ID = 68;
	public static final int INFERRED_STAGE = 69;
	public static final int INFERRED_STAGE_ABBREV = 70;
	public static final int INFERRED_STAGE_LOWER_ID = 71;
	public static final int INFERRED_STAGE_LOWER = 72;
	public static final int INFERRED_STAGE_LOWER_MOD = 73;
	public static final int INFERRED_STAGE_UPPER_ID = 74;
	public static final int INFERRED_STAGE_UPPER = 75;
	public static final int INFERRED_STAGE_UPPER_MOD = 76;
	public static final int INFERRED_AGE_START = 77;
	public static final int INFERRED_AGE_STOP = 78;
	public static final int KNOWN_STAGE_ID = 79;
	public static final int KNOWN_STAGE = 80;
	public static final int KNOWN_STAGE_ABBREV = 81;
	public static final int KNOWN_STAGE_LOWER_ID = 82;
	public static final int KNOWN_STAGE_LOWER = 83;
	public static final int KNOWN_STAGE_LOWER_MOD = 84;
	public static final int KNOWN_STAGE_UPPER_ID = 85;
	public static final int KNOWN_STAGE_UPPER = 86;
	public static final int KNOWN_STAGE_UPPER_MOD = 87;
	public static final int KNOWN_AGE_START = 88;
	public static final int KNOWN_AGE_STOP = 89;
	public static final int RELATIONSHIP_NEARBY = 90;
	public static final int RELATIONSHIP_SAMPLE = 91;
	public static final int RELATIONSHIP_STRAT = 92;
	public static final int COLUMN_MAP = 93;
	public static final int DIP = 94;
	public static final int DIP_DIRECTION = 95;
	public static final int STRIKE = 96;
	public static final int FACING = 97;
	public static final int GRAINSIZE = 98;
	public static final int PRIMARY_GRAINSIZE_ID = 99;
	public static final int PRIMARY_GRAINSIZE = 100;
	public static final int SECONDARY_GRAINSIZE_ID = 101;
	public static final int SECONDARY_GRAINSIZE = 102;
	public static final int COMPARATOR_USED = 103;
	public static final int BED_THICK_ID = 104;
	public static final int BED_THICKNESS = 105;
	public static final int BEDDING = 106;
	public static final int PRIMARY_BEDDING_ID = 107;
	public static final int PRIMARY_BEDDING = 108;
	public static final int SECONDARY_BEDDING_ID = 109;
	public static final int SECONDARY_BEDDING = 110;
	public static final int WEATHERING_ID = 111;
	public static final int WEATHERING = 112;
	public static final int HARDNESS_ID = 113;
	public static final int HARDNESS = 114;
	public static final int CARBONATE_ID = 115;
	public static final int CARBONATE = 116;
	public static final int COLOUR = 117;
	public static final int COLOUR_MODIFIER_ID = 118;
	public static final int COLOUR_MODIFIER = 119;
	public static final int PRIMARY_COLOUR_ID = 120;
	public static final int PRIMARY_COLOUR = 121;
	public static final int SECONDARY_COLOUR_ID = 122;
	public static final int SECONDARY_COLOUR = 123;
	public static final int WET = 124;
	public static final int SED_FEATURE = 125;
	public static final int ROCK_NATURE = 126;
	public static final int DEPOSITION_ENV = 127;
	public static final int CORRESPONDENCE = 128;

	private SampleData sd;
	private PageState state;
	private User user;
	private boolean authenticated = false;

	public Sample(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException {
		this.sd = SampleData.getData(id, state, forceRefresh);
		this.state = state;
		this.user = user;
		if (!FREDUtils.isAllowedLocality(user, sd.getAsString(FEATURE_STATUS), sd.getAsString(FEATURE_ID), state) 
				|| !FREDUtils.isAllowedSample(user, sd.getAsString(SAMPLE_SECURITY_CLASS_ID), sd.getAsString(SAMPLE_STATUS), sd.getAsString(SAMPLE_ID), state)) {
			authenticated = false;
		} else {
			authenticated = true;
		}
	}

	public Sample(int id, User user, PageState state) throws SQLException, IOException {
		this(id, user, state, false);
	}

	public int getSampleID() {
		return sd.getAsInt(SAMPLE_ID);
	}

	public int getFeatureID() {
		return sd.getAsInt(FEATURE_ID);
	}

	public boolean isUserAuthenticated() {
		return authenticated;
	}

	public boolean isApprovedLocality() {
		return (sd.getAsString(FEATURE_STATUS).equals(Audit.STATUS_APPROVED));
	}

	public boolean isApprovedSample() {
		return (sd.getAsString(SAMPLE_STATUS).equals(Audit.STATUS_APPROVED));
	}

	public int getRecordCount() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		return sd.getAsVector(RECORDS).size();
	}

	public int getWorkingRecordCount() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		return sd.getAsVector(WORKING_RECORDS).size();
	}

	public int getAdoptionRecordCount() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		int adoCount = 0;
		for (Iterator i = sd.getAsVector(RECORDS).iterator(); i.hasNext(); ) {
			KeyValueObject rec = (KeyValueObject)i.next();
			if (rec.getValue().equals(Record.ADOPTION_RECORD))
				adoCount++;
		}
		return adoCount;		
	}

	public int getPaleontologyRecordCount() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		int palCount = 0;
		for (Iterator i = sd.getAsVector(RECORDS).iterator(); i.hasNext(); ) {
			KeyValueObject rec = (KeyValueObject)i.next();
			if (rec.getValue().equals(Record.PALEONTOLOGY_RECORD))
				palCount++;
		}
		return palCount;		
	}
	
	public MetadataRecord[] getSampleMetadataRecords() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		return sd.sampMR;
	}
	
	public int getSampleMetadataRecordsCount() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		if (sd.sampMR != null)
			return sd.sampMR.length;
		return 0;
	}	

	public MetadataRecord[] getFeatureMetadataRecords() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		return sd.featMR;
	}
	
	public int getFeatureMetadataRecordsCount() throws InsufficientPrivelegesException {
		if (!authenticated)
			throw new InsufficientPrivelegesException();
		if (sd.featMR != null)
			return sd.featMR.length;
		return 0;
	}	
	
	private boolean isAllowedField(int field) {
		if (authenticated) {
			return true;
		}
		switch (field) {
			case FEATURE_ID :
			case SAMPLE_ID :
			case FEATURE_TYPE :
			case SAMPLE_NAME :
			case FR_ID:
			case FR_NUMBER:
			case YARD_FR_ID:
			case YARD_FR_NUMBER:
			case FEATURE_NAME:
			case MAP_SHEET:
			case SERIAL_NUMBER:
			case RECOLLECTION_NUMBER:
			case YARD_MAP_SHEET:
			case YARD_SERIAL_NUMBER:
			case YARD_RECOLLECTION_NUMBER:
			case DRILLHOLE_DEPTH:
			case MASTERFILE_ID:
			case MASTERFILE_NAME:
			case REG_AREA_CODE:
			case FEATURE_AUDIT_ID:
			case FEATURE_SECURITY_CLASS_ID:
			case FEATURE_STATUS:
			case SAMPLE_AUDIT_ID:
			case SAMPLE_SECURITY_CLASS_ID:
			case SAMPLE_STATUS:
			case SITE_ID:
			case COUNTRY_CODE:
			case COUNTRY_NAME:
			case LATITUDE:
			case LONGITUDE:
			case QMAP_SHEET:
			case NZMG_SHEET:
			case NZMG_EAST:
			case NZMG_NORTH:
			case METHOD_ID:
			case METHOD:
			case ACCURACY:
			case ORIG_SYSTEM_ID:
			case COORD_SYSTEM:
			case ORIG_COORD:
			return true;
		}
		return false;
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	public int getAsInt(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return sd.getAsInt(field);
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return sd.getAsDouble(field);
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return sd.getAsDate(field);
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return sd.getAsVector(field);
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return sd.getAsString(field);
	}

	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws InsufficientPrivelegesException {
		if (!isAllowedField(field)) {
			throw new InsufficientPrivelegesException();
		}
		return sd.get(field);
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return SampleData.getPoolSize();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		SampleData.purge();
	}

	public String toString() {
		return sd.toString();
	}

	public void editSample(String topDepth, String bottomDepth, String drillTypeID) throws IOException, SQLException, DataInputException {
		if (bottomDepth.equals(""))
			bottomDepth = null;
		if (drillTypeID.equals(""))
			drillTypeID = null;
		if (!FREDUtils.isNumeric(topDepth) || (bottomDepth != null && !FREDUtils.isNumeric(bottomDepth)) || (drillTypeID != null && !FREDUtils.isNumeric(drillTypeID)))
			throw new DataInputException("Sample Depths", "Data Missing or Invalid");
		DBConnection conn = FREDUtils.getFREDConnection(state);
		QueryDescriptor qd;
		if (sd.getAsString(Sample.SAMPLE_STATUS).equals(Audit.STATUS_APPROVED)) {
			qd = new QueryDescriptor("audit_edit");
			qd.addQueryColumn("audit_id", Types.NUMERIC, new Integer(sd.getAsInt(Sample.SAMPLE_AUDIT_ID)));
			qd.addQueryColumn("edited_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
			qd.addQueryColumn("edited_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
			qd.addQueryColumn("comments", Types.VARCHAR, "Depth change - from " + sd.getAsString(Sample.DRILLHOLE_DEPTH));
			DBUtils.doInsertUsingSequence(qd, "audit_edit_id", "audit_edit_seq", conn, false);
		}
		qd = new QueryDescriptor("sample");
		qd.addQueryColumn("top_depth", Types.NUMERIC, new Double(topDepth));
		if (bottomDepth != null)
			qd.addQueryColumn("bottom_depth", Types.NUMERIC, new Double(bottomDepth));
		if (drillTypeID != null)
			qd.addQueryColumn("drill_type_id", Types.NUMERIC, new Integer(drillTypeID));
		qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(getSampleID()));
		DBUtils.doUpdate(qd, "sample_id = ?", conn);
		this.sd = SampleData.getData(getSampleID(), state, true);
	}

}