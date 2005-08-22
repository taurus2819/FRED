package nz.cri.gns.fred.util;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.FolderUtilException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FeatureDAO;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.SampleDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FeatureMeta;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.UserFolder;

/**
 *
 */
public class FeatureUtil extends ModelUtil {

	private FeatureDAO featureDAO;
	private SampleDAO sampleDAO;
	private FolderDAO folderDAO;
	
	public FeatureUtil(DAOFactory factory) {
		super(factory);
		this.featureDAO = factory.getFeatureDAO();
		this.sampleDAO = factory.getSampleDAO();
		this.folderDAO = factory.getFolderDAO();
	}
	
	public Feature copyFeature(Feature feature, String newName, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!folder.isAllowedCreateLocalities())
			throw new InsufficientPrivelegesException();
		Audit audit = featureDAO.createNewAudit();
		audit.setStatus(FREDConstants.WORKING);
		audit.setCreatedById(new Integer(user.getId()));
		audit.setCreatedDate(new Date());
		audit.setFolder(folder.getFolder());
		featureDAO.save(audit);
		
		Feature newFeature = featureDAO.cloneFeature(feature);
		newFeature.setFeatureId(null);
		newFeature.setFeatureName(newName);
		newFeature.setAudit(audit);
		//A new copy should not have an entry in folder_contents
		newFeature.setFolders(null);
		
		//Copy feature images
		Set images = feature.getFeatureMetas();
		if (images != null && images.size() > 0) {
			HashSet<FeatureMeta> newImages = new HashSet<FeatureMeta>();
			for (Iterator it = images.iterator(); it.hasNext(); ) {
				FeatureMeta meta = (FeatureMeta)it.next();
				FeatureMeta newMeta = featureDAO.createFeatureMeta();
				newMeta.setMetaId(meta.getMetaId());
				newMeta.setFeature(newFeature);
				newImages.add(newMeta);
			}
			newFeature.setFeatureMetas(newImages);
		}
		//Clear out relationships pointing _to_ it
		newFeature.setRelationships(null);
		//Remove any samples that have come across
		newFeature.setSamples(null);
		//Save the new feature!
		featureDAO.save(newFeature);
		
		if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
			//For outcrops we copy everything
			
			//Copy sample (should be only one!)
			Set samples = feature.getSamples();
			if (samples.size() != 1) {
				throw new IllegalStateException("Outcrop does not have a singleton sample"); 
			}
			Sample sample = (Sample)samples.iterator().next();
			//Copy sample - collectors clone is OK as it's many-to-many
			Sample newSample = sampleDAO.cloneSample(sample);
			newSample.setSampleId(null);
			newSample.setFeature(newFeature);
			//Clear the fr number if it has one
			newSample.setFrNumber(null);
			newSample.setRecords(null);
			//Copy relationships
			Set relationships = sample.getRelationships();
			if (relationships != null && relationships.size() > 0) {
				HashSet<Relationship> newRels = new HashSet<Relationship>();
				for (Iterator it = relationships.iterator(); it.hasNext(); ) {
					Relationship relationship = (Relationship)it.next();
					Relationship newRel = sampleDAO.cloneRelationship(relationship);
					newRel.setRelationshipId(null);
					newRel.setSample(newSample);
					newRels.add(newRel);
				}
				newSample.setRelationships(newRels);
			}
			//Copy sent to
			Set sentTos = sample.getSentTos();
			if (sentTos != null && sentTos.size() > 0) {
				HashSet<SentTo> newSent = new HashSet<SentTo>();
				for (Iterator it = sentTos.iterator(); it.hasNext(); ) {
					SentTo sentTo = (SentTo)it.next();
					SentTo newSentTo = sampleDAO.cloneSentTo(sentTo);
					newSentTo.setSample(newSample);
					newSent.add(newSentTo);
				}
				newSample.setSentTos(newSent);
			}
			//Copy sedimentary feature
			Set sedFeatures = sample.getSedimentaryFeatures();
			if (sedFeatures != null && sedFeatures.size() > 0) {
				HashSet<SedimentaryFeature> newSedFeatures = new HashSet<SedimentaryFeature>();
				for (Iterator it = sedFeatures.iterator(); it.hasNext(); ) {
					SedimentaryFeature sedFeature = (SedimentaryFeature)it.next();
					SedimentaryFeature newSedFeature = sampleDAO.cloneSedimentaryFeature(sedFeature);
					newSedFeature.setSample(newSample);
					newSedFeatures.add(newSedFeature);
				}
				newSample.setSentTos(newSedFeatures);
			}
			
			//Copy sample images
			images = sample.getSampleMetas();
			if (images != null && images.size() > 0) {
				HashSet<SampleMeta> newImages = new HashSet<SampleMeta>();
				for (Iterator it = images.iterator(); it.hasNext(); ) {
					SampleMeta meta = (SampleMeta)it.next();
					SampleMeta newMeta = sampleDAO.createSampleMeta();
					newMeta.setMetaId(meta.getMetaId());
					newMeta.setSample(newSample);
					newImages.add(newMeta);
				}
				newSample.setSampleMetas(newImages);
			}
			
			//Save the new sample
			sampleDAO.save(newSample);
		} else {
			//Anything that's not actually a feature attribute doesn't get saved
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

	public void deleteFeature(Feature feature, UserFolder folder, UserAccount user) throws InsufficientPrivelegesException, StorageAccessException {
		if (!feature.getAudit().getFolder().equals(folder.getFolder()))
			throw new IllegalArgumentException("Feature was not in the given folder");
		if (!folder.isAllowedDeleteLocalities())
			throw new InsufficientPrivelegesException();
		
		featureDAO.delete(feature);

	}

	public void removeFeature(Feature feature, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!feature.getAudit().getStatus().equals(FREDConstants.APPROVED))
			throw new IllegalStateException("Cannot remove a working locality");
		if (!folder.isAllowedDeleteLocalities())
			throw new InsufficientPrivelegesException();
		
		Set folders = feature.getFolders();
		folders.remove(folder.getFolder());
		featureDAO.update(feature);
	}
	
	public void submitFeature(Feature feature, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
		if (!folder.isAllowedSubmitLocalities())
			throw new InsufficientPrivelegesException();

		if (feature.getFeatureType() == null || feature.getSiteId() == null || feature.getRegistrationArea() == null)
			throw new DataInputException("Mandatory Fields", "Not all mandatory fields completed");

		Audit audit = feature.getAudit();
		audit.setStatus(FREDConstants.WAITING);
		audit.setSubmittedById(new Integer(user.getId()));
		audit.setSubmittedDate(new Date());
		featureDAO.update(audit);
		
		int masterfile = -1;
		try {
			masterfile = FREDUtil.getMasterfile(feature);
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
		
		feature.setMasterFile(folderDAO.getFolder(masterfile));
		featureDAO.update(feature);		
	}

	public void revokeFeature(Feature feature, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!folder.isAllowedSubmitLocalities())
			throw new InsufficientPrivelegesException();

		Audit audit = feature.getAudit();
		audit.setStatus(FREDConstants.WORKING);
		audit.setSubmittedById(null);
		audit.setSubmittedDate(null);
		
		featureDAO.update(audit);
		
		feature.setMasterFile(null);
		featureDAO.update(feature);		
	}
	
	public Feature getFeature(int featureId) throws StorageAccessException {
		return featureDAO.getFeature(featureId);
	}
	
	public boolean folderContainsFeature(UserFolder folder, Feature feature) throws StorageAccessException {
		Feature[] features = getFeaturesInFolder(folder);
		for (int i=0; i<features.length; i++) {
			if (features[i].equals(feature))
				return true;
		}
		return false;
	}
	
	public Feature[] getFeaturesInFolder(UserFolder folder) throws StorageAccessException {
		HashSet<Feature> features = new HashSet<Feature>();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSSS");
		System.out.println(format.format(new java.util.Date()) + ": Starting");
		//Get from feature_content
		features.addAll(folder.getFolder().getFeatures());
		//System.out.println(new java.util.Date() + ": Got from folder contents");
		
		//Get from audit
		List audits = folderDAO.getWorkingAuditsFor(folder.getFolder());
		System.out.println(format.format(new java.util.Date()) + ": Got relevant audit records");

		for (Iterator it = audits.iterator(); it.hasNext(); ) {
			Audit audit = (Audit)it.next();
			
			System.out.println(format.format(new java.util.Date()) + ": Starting audit features");
			
			// - features
			features.addAll(audit.getFeatures());
			/* Need this for list instead of set
			for (Iterator featIt = audit.getFeatures().iterator(); featIt.hasNext(); ) {
				Feature feature = (Feature)featIt.next();
				if (!features.contains(feature))
					features.add(feature);
			}*/
			
			
			//- samples
			System.out.println(format.format(new java.util.Date()) + ": Starting audit samples");
			features.addAll(featureDAO.getFeaturesBySample(audit));
			/*Set samples = audit.getSamples();
			if (samples != null && samples.size() > 0) {
				for (Iterator sampIt = samples.iterator(); sampIt.hasNext(); ) {
					Sample sample = (Sample)sampIt.next();
					//if (!features.contains(sample.getFeature()));
					features.add(sample.getFeature());
				}
			}*/

			//- results
			System.out.println(format.format(new java.util.Date()) + ": Starting audit results");
			features.addAll(featureDAO.getFeaturesByRecord(audit));
			/*Set records = audit.getRecords();
			if (records != null && records.size() > 0) {
				for (Iterator recIt = records.iterator(); recIt.hasNext(); ) {
					Record record = (Record)recIt.next();
					//if (!features.contains(record.getSample().getFeature()));
					features.add(record.getSample().getFeature());
				}
			}*/
		}
		
		System.out.println(format.format(new java.util.Date()) + ": Finished");
		Feature[] featuresArray = (Feature[])features.toArray(new Feature[features.size()]); 
		Arrays.sort(featuresArray);
		return featuresArray;
	}
	
	public boolean isAllowedEditFeature(UserAccount user, Feature feature, UserFolder folder) throws StorageAccessException, NamingException, SQLException {
		String status = feature.getAudit().getStatus();
		if (status.equals(FREDConstants.APPROVED))
			return hasMasterfileRights(user, feature, UserFolder.FOLDER_EDIT_RIGHT) || FREDUtil.checkEditSecurityClass(user);
		
		if (status.equals(FREDConstants.WAITING))
			return hasMasterfileRights(user, feature, UserFolder.FOLDER_EDIT_RIGHT);

		return folder.isAllowedEditLocalities();
	}

	/**
	 * Returns true is the user is allowed to approve the locality
	 */
	public boolean isAllowedApproveFeature(UserAccount user, Feature feature) throws StorageAccessException {
		if (WAITING.equals(feature.getAudit().getStatus()))
			return folderDAO.getUserFolder(feature.getAudit().getFolder().getFolderId().intValue(), Integer.parseInt(user.getId())).isAllowedApproveLocalities();
		return false;
	}

	/**
	 * Returns true if the user has masterfile rights for this locality
	 * @throws StorageAccessException
	 * @throws 
	 */
	public boolean hasMasterfileRights(UserAccount user, Feature feature, int right) throws StorageAccessException {
		return hasMasterfileRights(user, feature, right, folderDAO);
	}
	
	static boolean hasMasterfileRights(UserAccount user, Feature feature, int right, FolderDAO folderDAO) throws NumberFormatException, StorageAccessException {
		Folder masterfile = feature.getMasterFile();
		if (masterfile == null)
			return false;
		
		UserFolder masterfileFolder = folderDAO.getUserFolder(masterfile.getFolderId().intValue(), Integer.parseInt(user.getId()));
		
		return (masterfileFolder == null) ? false : (masterfileFolder.getRights() & right) > 0;
	}

	public Sample getOutcropSample(Feature feature) {
		if (!feature.getFeatureType().equals(FREDConstants.OUTCROP))
			throw new IllegalArgumentException("Feature is not an outcrop");
		
		Set samples = feature.getSamples();
		Iterator it = samples.iterator();
		return (Sample)it.next();
	}
	
	public void approveFeature(Feature feature, String mapSheet, Integer serialNum, String recollectionNum, String comments, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!hasMasterfileRights(user, feature, UserFolder.FOLDER_APPROVE_RIGHT))
			throw new InsufficientPrivelegesException();
		
		//Make an FR number
		FrNumber fr = sampleDAO.createFRNumber();
		fr.setMapSheet(mapSheet);
		fr.setSerialNumber(serialNum);
		fr.setRecollectionNumber(recollectionNum);
		
		//All samples get the same FR number
		for (Iterator it = feature.getSamples().iterator(); it.hasNext(); ) {
			Sample sample = (Sample)it.next();
			sample.setFrNumber(fr);
			sampleDAO.update(sample);
		}
		
		//explicitly add to working folder
		Audit audit = feature.getAudit();
		try {
			feature.getFolders().add(audit.getFolder());
			featureDAO.update(feature);
		} catch (Exception e) {
		}
		
		//update audit table
		audit.setStatus(APPROVED);
		audit.setApprovedById(new Integer(user.getId()));
		audit.setApprovedDate(new Date());
		audit.setFolder(null);
		audit.setWorkingComments(null);
		audit.setCuratorComments(comments);
		featureDAO.update(audit);
	}
	
	public void rejectLocality(Feature feature, String comments, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!hasMasterfileRights(user, feature, UserFolder.FOLDER_APPROVE_RIGHT))
			throw new InsufficientPrivelegesException();
		
		Audit audit = feature.getAudit();
		audit.setStatus(REJECTED);
		audit.setCuratorComments(comments);
		featureDAO.update(audit);
	}
	
	public void addToFolder(Feature feature, int folderId, UserAccount user) throws StorageAccessException, FolderUtilException {
		if (feature.getAudit().getStatus().equals(APPROVED))
			throw new FolderUtilException("Cannot add a working locality");
		
		//TODO this should check that they have rights to add to this folder...
		feature.getFolders().add(folderDAO.getFolder(folderId));
	}
	
	/**
	 * Returns the next available FR number - <b>not</b> saved to the DB
	 * @param feature
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 * @throws StorageAccessException
	 */
	public FrNumber getNextAvailableFrNumber(Feature feature) throws SQLException, NamingException, StorageAccessException {
		String mapSheet = FREDUtil.getFrNumberMapSheet(feature);
		
		int nextAvailable = featureDAO.getNextAvailableSerialNumber(mapSheet);
		
		FrNumber frNum = sampleDAO.createFRNumber();
		frNum.setMapSheet(mapSheet);
		frNum.setSerialNumber(new Integer(nextAvailable));
		
		return frNum;		
	}
}
