package nz.cri.gns.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.fred.data.FRNumber;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.fred.dataentry.DataEntryFormFactory;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.dataentry.LocalityDE;
import nz.cri.gns.fred.dataentry.RecordDE;
import nz.cri.gns.fred.dataentry.SampleDE;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;


public class FolderUtils {
	
	public static void addFolder(String name, User user, PageState state) throws SQLException, IOException {
		if (name.length() > 32)
			name = name.substring(0, 31);
		QueryDescriptor qd = new QueryDescriptor("folder");
		qd.addQueryColumn("name", Types.VARCHAR, name);
		qd.addQueryColumn("owner_id", Types.NUMERIC, new Integer(user.getPersonId()));
		qd.addQueryColumn("folder_type", Types.VARCHAR, "personal");
		DBUtils.doInsertUsingSequence(qd, "folder_id", "folder_seq", FREDUtils.getFREDConnection(state), false);
	}
	
	public static void deleteFolder(String folderID, User user, PageState state) throws IOException, InvalidCredentialsException, SQLException, FolderUtilException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		Folder folder = new Folder(Integer.parseInt(folderID), user, state);
		if (folder.isAllowedAdmin() && folder.getLocalityCount() == 0) {
			String query = "DELETE FROM folder WHERE folder_id = ?";
			conn.executeUpdate(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(folderID)});
		} else {
			throw new FolderUtilException("Cannot delete folder as either insufficient privileges or folder not empty");
		}
		conn.releaseStatement();
	}
	
	public static void deleteLocality(String featureID, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featureID), user, state);
		form.delete();
	}
	
	public static void deleteSample(String sampleID, User user, PageState state) throws NumberFormatException, InvalidCredentialsException, DataInputException, SQLException, IOException {
		Sample sample = new Sample(Integer.parseInt(sampleID), user, state);
		String featureID = sample.getAsString(Sample.FEATURE_ID);
		String featAuditID = sample.getAsString(Sample.FEATURE_AUDIT_ID);
		SampleDE form = DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(sampleID), user, state);
		form.delete();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String query = "SELECT COUNT(*) FROM sample WHERE feature_id = ?";
		ResultSet rs = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(featureID)});
		rs.next();
		if (rs.getInt(1) == 0) {
			QueryDescriptor qd = new QueryDescriptor("sample");
			qd.addQueryColumn("feature_id", Types.NUMERIC, new Integer(featureID));
			qd.addQueryColumn("audit_id", Types.NUMERIC, new Integer(featAuditID));
			DBUtils.doInsertUsingSequence(qd, "sample_id", "sample_seq", conn, false);			
		}	
	}
	
	public static void deleteRecord(String recordID, User user, PageState state) throws NumberFormatException, DataInputException, InvalidCredentialsException, SQLException, IOException {
		RecordDE form = DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(recordID), user, state);
		form.delete();
	}

	public static void removeLocality(String featureID, String folderID, User user, PageState state) throws IOException, SQLException, InvalidCredentialsException, FolderUtilException {
		Feature feature = new Feature(Integer.parseInt(featureID), user, state);
		if (!feature.getAsString(Feature.STATUS).equals("approved"))
			throw new FolderUtilException("Cannot remove a working locality");
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String query = "DELETE FROM folder_content WHERE folder_id = ? AND feature_id = ?";
		conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(folderID), new Integer(featureID)});
	}

	public static void submitLocality(String featID, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
		form.submit();
	}
	
	public static void submitSample(String sampleID, User user, PageState state) throws NumberFormatException, IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		SampleDE form = DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(sampleID), user, state);
		form.submit();
	}
	
	public static void submitRecord(String recID, User user, PageState state) throws NumberFormatException, DataInputException, InvalidCredentialsException, SQLException, IOException {
		RecordDE form = DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(recID), user, state);
		form.submit();
	}
	
	public static void revokeLocality(String featID, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
		form.revoke();	
	}
	
	public static void approveLocality(String featID, FRNumber frNum, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
		form.approve(frNum);			
	}
	
	public static void rejectLocality(String featID, String comments, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
		form.reject(comments);			
	}
	
	public static void copyLocality(String oldFeatureID, String newFeatureName, String folderID, User user, PageState state) throws IOException, SQLException {
		int userID = user.getPersonId();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		QueryDescriptor qd = new QueryDescriptor("audit_table");
		qd.addQueryColumn("status", Types.VARCHAR, "working");
		qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(userID));
		qd.addQueryColumn("created_date", Types.DATE, FREDUtils.getNowForSQL());
		qd.addQueryColumn("working_folder_id", Types.NUMERIC, new Integer(folderID));
		String featureAuditID = DBUtils.doInsertUsingSequence(qd, "audit_id", "audit_seq", conn, true);
		ResultSet seqRst = conn.executeQuery("SELECT feature_seq.NEXTVAL FROM DUAL");
		seqRst.next();
		int featureID = seqRst.getInt(1);
		String query = "SELECT feature_type FROM feature WHERE feature_id = ?";
		ResultSet rs1 = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(oldFeatureID)});
		rs1.next();
		String featureType = rs1.getString(1);
		query = "INSERT INTO feature (feature_id, site_id, audit_id, masterfile_id, feature_type, feature_name, locality, reg_area_id, person_id, start_date, start_date_rounding, finish_date, finish_date_rounding, drillhole_licence_name, datum_type, datum_elevation, start_depth, finish_depth, comments) SELECT ? AS featid, site_id ? AS auditid, masterfile_id, feature_type, ? AS featname, locality, reg_area_id, person_id, start_date, start_date_rounding, finish_date, finish_date_rounding, drillhole_licence_name, datum_type, datum_elevation, start_depth, finish_depth, comments FROM feature WHERE feature_id = ?";
		conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.NUMERIC}, new Object[] {new Integer(featureID), new Integer(featureAuditID), newFeatureName, new Integer(oldFeatureID)});
		query = "SELECT sample_id FROM sample WHERE feature_id = ?";
		rs1 = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(oldFeatureID)});
		Statement ps1 = conn.preservePreparedStatement();
		while (rs1.next()) {
			String sampleAuditID;
			if (featureType.equals(Feature.OUTCROP_LOCALITY)) {
				//if Outcrop re-use Feature AuditID in Sample
				sampleAuditID = featureAuditID; 
			} else {
				qd = new QueryDescriptor("audit_table");
				qd.addQueryColumn("status", Types.VARCHAR, "working");
				qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(userID));
				qd.addQueryColumn("created_date", Types.DATE, FREDUtils.getNowForSQL());
				qd.addQueryColumn("working_folder_id", Types.NUMERIC, new Integer(folderID));
				qd.addQueryColumn("security_class_id", Types.NUMERIC, new Integer(4));
				sampleAuditID = DBUtils.doInsertUsingSequence(qd, "audit_id", "audit_seq", conn, true);
			}
			int oldSampleID = rs1.getInt(1);
			seqRst = conn.executeQuery("SELECT Sample_Seq.NEXTVAL FROM DUAL");
			seqRst.next();
			int sampleID = seqRst.getInt(1);
			query = "INSERT INTO sample (sample_id, feature_id, audit_id, top_depth, bottom_depth, drill_type_id, collection_date, date_rounding, strat_unit, in_place, not_collected, significance, inferred_stage_id, known_stage_id, column_map, dip, dip_direction, strike, facing, primary_grainsize_id, secondary_grainsize_id, comparator_used, bed_thick_id, primary_bedding_id, secondary_bedding_id, weathering_id, hardness_id, carbonate_id, colour_modifier_id, primary_colour_id, secondary_colour_id, wet, rock_nature, deposition_env, correspondence, comments) SELECT ? AS sampid, ? AS featid, ? AS auditID, top_depth, bottom_depth, drill_type_id, collection_date, date_rounding, strat_unit, in_place, not_collected, significance, inferred_stage_id, known_stage_id, column_map, dip, dip_direction, strike, facing, primary_grainsize_id, secondary_grainsize_id, comparator_used, bed_thick_id, primary_bedding_id, secondary_bedding_id, weathering_id, hardness_id, carbonate_id, colour_modifier_id, primary_colour_id, secondary_colour_id, wet, rock_nature, deposition_env, comments FROM sample WHERE sample_id = ?";
			conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, }, new Object[] {new Integer(sampleID), new Integer(featureID), new Integer(sampleAuditID), new Integer(oldSampleID)});
			query = "INSERT INTO collector (sample_id, person_id) SELECT ? AS sampid, person_id FROM collector WHERE sample_id = ?";
			conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(sampleID), new Integer(oldSampleID)});
			query = "INSERT INTO relationship (relationship_id, sample_id, relationship_type, related_feature_id, strat_unit, distance, distance_range, distance_mod, relation_type_id) SELECT relationship_seq.NEXTVAL AS relID, ? AS sampID, relationship_type, related_feature_id, strat_unit, distance, distance_range, distance_mod, relation_type_id FROM Relationship WHERE Record_ID = ?";
			conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(sampleID), new Integer(oldSampleID)});
			query = "INSERT INTO sedimentary_feature (sample_id, sed_feature_id, abundant) SELECT ? AS sampID, sed_feature_id, abundant FROM sedimentary_feature WHERE sample_id = ?";
			conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(sampleID), new Integer(oldSampleID)});
			query = "INSERT INTO sent_to (sample_id, fossil_group_id, person_id, lab_id, comments) SELECT ? AS sampID, fossil_group_id, person_id, lab_id, comments FROM sent_to WHERE sample_id = ?";
			conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(sampleID), new Integer(oldSampleID)});
			query = "SELECT record_ID, record_type FROM record_all_view WHERE sample_id = ?";
			ResultSet rs2 = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(oldSampleID)});
			Statement ps2 = conn.preservePreparedStatement();
			while (rs2.next()) {
				int oldRecordID = rs2.getInt(1);
				String recordType = rs2.getString(2);
				qd = new QueryDescriptor("audit_table");
				qd.addQueryColumn("status", Types.VARCHAR, "working");
				qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(userID));
				qd.addQueryColumn("created_date", Types.DATE, FREDUtils.getNowForSQL());
				qd.addQueryColumn("working_folder_id", Types.NUMERIC, new Integer(folderID));
				qd.addQueryColumn("security_class_id", Types.NUMERIC, new Integer(4));
				String recordAuditID = DBUtils.doInsertUsingSequence(qd, "audit_id", "audit_seq", conn, true);
				seqRst = conn.executeQuery("SELECT Record_Seq.NEXTVAL FROM DUAL");
				seqRst.next();
				int recordID = seqRst.getInt(1);
				query = "INSERT INTO record (record_id, sample_id, audit_id) VALUES (?, ?, ?)";
				conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(recordID), new Integer(sampleID), new Integer(recordAuditID)});
				if (recordType.equals(Record.ADOPTION_RECORD)) {
					query = "INSERT INTO adoption (record_id, adoption_date, date_rounding, adopted_stage_id, comments) SELECT ? AS recid, adoption_date, date_rounding, adopted_stage_id, comments FROM adoption WHERE record_id = ?";
					conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(recordID), new Integer(oldRecordID)});
					query = "INSERT INTO adoptor (record_id, person_id) SELECT ? AS recid, person_id FROM adoptor WHERE record_id = ?";
					conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(recordID), new Integer(oldRecordID)});
				} else if (recordType.equals(Record.PALEONTOLOGY_RECORD)) {
					query = "INSERT INTO paleontology (record_id, identification_date, date_rounding, stage_id, stage_comments, lab_section_id, lab_number, collection_comments) SELECT ? AS recid, identification_date, date_rounding, stage_id, stage_comments, lab_section_id, lab_number, collection_comments FROM paleontology WHERE record_id = ?";
					conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(recordID), new Integer(oldRecordID)});
					query = "INSERT INTO identifier (record_id, person_id) SELECT ? AS recid, person_id FROM identifier WHERE record_id = ?";
					conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(recordID), new Integer(oldRecordID)});
					query = "INSERT INTO pal_list (pal_list_id, record_id, group_id, taxa_id, taxonomic_name, specimen_count, specimen_coords, comments) SELECT pal_list_seq.NEXTVAL AS palID, ? AS recid, group_id, taxa_id, taxonomic_name, specimen_count, specimen_coords, comments FROM pal_list WHERE record_id = ";
					conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(recordID), new Integer(oldRecordID)});
				}
			}
			ps2.close();
		}
		conn.releaseStatement();
		ps1.close();
		
	}
	
	public static FRNumber getNextFRNumber(String regAreaCode, String nzmsSheet, double latitude, double longitude, PageState state) throws SQLException, IOException {
		DecimalFormat latDeg = new DecimalFormat("00");
		DecimalFormat longDeg = new DecimalFormat("000");
		String latStr = latDeg.format((Math.floor(Math.abs(latitude))));
		String longStr = longDeg.format((Math.floor(Math.abs(longitude))));
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String mapSheet;
		if (nzmsSheet != null) {
			if (FREDUtils.isValidMapSheet(nzmsSheet)) {
				mapSheet = nzmsSheet;
			} else {
				mapSheet = (latitude >= 0 ? "N" : "S") + (longitude >= 0 ? "E" : "W") + latStr + longStr;
			}
		} else if (regAreaCode != null && !regAreaCode.equals("NZ") && !regAreaCode.equals("OT")) {
			mapSheet = regAreaCode;
		} else {
			mapSheet = (latitude >= 0 ? "N" : "S") + (longitude >= 0 ? "E" : "W") + latStr + longStr;
		}
		String query = "SELECT MAX(Serial_Number) FROM FR_Number WHERE Map_Sheet = ? AND Serial_Number < 6000";
		ResultSet rs = conn.executeQuery(query, new int[] {Types.VARCHAR}, new Object[] {mapSheet});
		int serialNum;
		rs.next();
		if (rs.getString(1) != null) {
			serialNum = rs.getInt(1) + 1;
		} else {
			serialNum = 1;
		}
		return new FRNumber(mapSheet, new Integer(serialNum), null);
	}
	
	public static FRNumber getNextFRNumber(String mapSheet, int serialNumber, PageState state) throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String query = "SELECT MAX(Recollection_Number) FROM FR_Number WHERE Map_Sheet = ? AND Serial_Number = ?";
		ResultSet rs = conn.executeQuery(query, new int[] {Types.VARCHAR, Types.NUMERIC}, new Object[] {mapSheet, new Integer(serialNumber)});
		rs.next();
		if (rs.getString(1) == null) {
			return new FRNumber(mapSheet, new Integer(serialNumber), "A");	
		} else {
			char recollNum = rs.getString(1).charAt(0);
			return new FRNumber(mapSheet, new Integer(serialNumber), String.valueOf((char) (recollNum + 1)));
		}
	}
}
