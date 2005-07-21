package nz.cri.gns.fred.util;

import java.util.Date;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FeatureDAO;
import nz.cri.gns.fred.dao.SampleDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Folder;

import nz.cri.gns.auth.UserAccount;

/**
 *
 */
public class FeatureUtil extends ModelUtil {

	private FeatureDAO featureDAO;
	private SampleDAO sampleDAO;
	
	public FeatureUtil(DAOFactory factory) {
		super(factory);
		this.featureDAO = factory.getFeatureDAO();
		this.sampleDAO = factory.getSampleDAO();
	}
	
	public Feature copyFeature(Feature feature, String newName, Folder folder, UserAccount user) throws StorageAccessException {
		Audit audit = featureDAO.createNewAudit();
		audit.setStatus(FREDConstants.WORKING);
		audit.setCreatedById(new Integer(user.getId()));
		audit.setCreatedDate(new Date());
		audit.setFolder(folder);
		featureDAO.save(audit);
		
		Feature newFeature = featureDAO.cloneFeature(feature);
		newFeature.setFeatureId(null);
		newFeature.setFeatureName(newName);
		newFeature.setAudit(audit);
		newFeature.setFeatureMetas(null);
		if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
			//For outcrops we copy everything
			
			//Copy sample (should be only one!)
			
			//Copy relationships
			
			//Copy collectors
			
			//Copy sent to
			
			//Copy sedimentary feature
			
		} else {
			//Clear out anything that's not actually a feature attribute
			//Maybe images should be copied????
			newFeature.setFeatureMetas(null);
			newFeature.setFolders(null);
			newFeature.setRelationships(null);
			newFeature.setSamples(null);
			
		}
		
		
		return newFeature;
		
/*		newFeature.setSiteId(feature.getSiteId());
		newFeature.setAudit(feature.getAudit());
		newFeature.setMasterFile(feature.getMasterFile());
		newFeature.setFeatureType(feature.getFeatureType());
		newFeature.setFeatureName(newName);
		newFeature.setLocality(feature.getLocality());
		newFeature.setRegistrationArea(feature.getRegistrationArea());
		newFeature.setPerson(feature.getPerson());
		newFeature.setStartDate(feature.getStartDate());
		newFeature.setStartDateRounding(feature.getStartDateRounding());
*/
		
/*
 * Here follows the original code from FolderUtils.  According to JIR this is _not_ 
 * appropriate for vert sections and drillholes
 * we should only be copying the Front of form data.  Left here for posterity
 *
		
		String featureAuditID = DBUtils.doInsertUsingSequence(qd, "audit_id", "audit_seq", conn, true);
		ResultSet seqRst = conn.executeQuery("SELECT feature_seq.NEXTVAL FROM DUAL");
		seqRst.next();
		int featureID = seqRst.getInt(1);
		String query = "SELECT feature_type FROM feature WHERE feature_id = ?";
		ResultSet rs1 = conn.executeQuery(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(oldFeatureID)});
		rs1.next();
		String featureType = rs1.getString(1);
		query = 
			"INSERT INTO feature (feature_id, site_id, audit_id, masterfile_id, feature_type, feature_name, " +
							"locality, reg_area_id, person_id, start_date, start_date_rounding, finish_date, finish_date_rounding, " +
							"drillhole_licence_name, datum_type, datum_elevation, start_depth, finish_depth, comments) " +
			"SELECT ? AS featid, site_id ? AS auditid, masterfile_id, feature_type, ? AS featname, locality, reg_area_id, person_id, start_date, start_date_rounding, finish_date, finish_date_rounding, drillhole_licence_name, datum_type, datum_elevation, start_depth, finish_depth, comments FROM feature WHERE feature_id = ?";
		conn.executeUpdate(query, 
				new int[] {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.NUMERIC}, 
				new Object[] {new Integer(featureID), new Integer(featureAuditID), newFeatureName, new Integer(oldFeatureID)});
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
				qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WORKING);
				qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(userID));
				qd.addQueryColumn("created_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
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
				qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WORKING);
				qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(userID));
				qd.addQueryColumn("created_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
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
	*/	
	}

	public void deleteFeature(Feature feature, UserAccount user) {
		
	}

	public void removeFeature(Feature feature, Folder folder, UserAccount user) {
		
	}
	
	public void submitFeature(Feature feature, UserAccount user) {
		
	}

	public void revokeFeature(Feature feature, UserAccount user) {
		
	}
}
