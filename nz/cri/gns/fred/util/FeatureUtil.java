package nz.cri.gns.fred.util;

import java.beans.IntrospectionException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.FolderUtilException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FeatureDAO;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.SampleDAO;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.de.MandatoryFieldsMissingException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FeatureMeta;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.UserFolder;

/**
 *
 */
public class FeatureUtil extends ModelUtil implements AuditedUtil {

	private FeatureDAO featureDAO;
	private SampleDAO sampleDAO;
	private FolderDAO folderDAO;
	
	private static String BACKLOG_PREPARE_COMMENTS = "Locality prepared for backlog editing";
	
	public FeatureUtil(DAOFactory factory) {
		super(factory);
		this.featureDAO = factory.getFeatureDAO();
		this.sampleDAO = factory.getSampleDAO();
		this.folderDAO = factory.getFolderDAO();
	}
	
	public Feature copyFeature(Feature feature, String newName, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
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
		newFeature.setFrNumber(null);
		//A new copy should not have an entry in folder_contents
		newFeature.setFolders(null);
		
		//Copy feature images
		Set images = feature.getFeatureMetas();
		if (images != null && images.size() > 0) {
			HashSet<FeatureMeta> newImages = new HashSet<FeatureMeta>();
			for (Iterator it = images.iterator(); it.hasNext(); ) {
				FeatureMeta meta = (FeatureMeta)it.next();
				FeatureMeta newMeta = featureDAO.createNewFeatureMeta();
				newMeta.setMetaId(meta.getMetaId());
				newMeta.setFeature(newFeature);
				newImages.add(newMeta);
			}
			newFeature.setFeatureMetas(newImages);
		} else {
			newFeature.setFeatureMetas(null);
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
			Sample newSample = cloneSample(newFeature, sample);
			//set newSample's audit to be same as newFeature
			newSample.setAudit(newFeature.getAudit());
			
			//Save the new sample
			try {
			sampleDAO.save(newSample);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	public Sample cloneSample(Feature newFeature, Sample sample) throws StorageAccessException, IntrospectionException {
		Set images;
		Sample newSample = sampleDAO.createNewSample();
		FREDUtil.beanCopy(sample, newSample, 
				new FREDUtil.ExcludeByType(Set.class, 
				new FREDUtil.ExcludeByName(FREDUtil.toVector("audit", "sampleId", "feature", "frNumber")))
		);
		newSample.setFeature(newFeature);
		//Clear the fr number if it has one
		//Copy relationships
		Set<Relationship> relationships = sample.getRelationships();
		if (relationships != null && relationships.size() > 0) {
			HashSet<Relationship> newRels = new HashSet<Relationship>();
			for (Relationship rel : relationships) {
				Relationship newRel = sampleDAO.createNewRelationship();
				FREDUtil.beanCopy(rel, newRel, new FREDUtil.ExcludeByName(FREDUtil.toVector("relationshipId", "sample")));
				newRel.setSample(newSample);
				newRels.add(newRel);
			}
			newSample.setRelationships(newRels);
		}
		//Copy sent to
		Set<SentTo> sentTos = sample.getSentTos();
		if (sentTos != null && sentTos.size() > 0) {
			HashSet<SentTo> newSent = new HashSet<SentTo>();
			for (SentTo sentTo : sentTos) {
				SentTo newSentTo = sampleDAO.createNewSentTo();
				FREDUtil.beanCopy(sentTo, newSentTo, new FREDUtil.ExcludeByName(FREDUtil.toVector("sentToId", "sample")));
				newSentTo.setSample(newSample);
				newSent.add(newSentTo);
			}
			newSample.setSentTos(newSent);
		}
		
		//Copy sedimentary feature
		Set<SedimentaryFeature> sedFeatures = sample.getSedimentaryFeatures();
		if (!FREDUtil.isEmpty(sedFeatures)) {
			HashSet<SedimentaryFeature> newSedFeatures = new HashSet<SedimentaryFeature>();
			for (SedimentaryFeature sedFeature : sedFeatures) {
				SedimentaryFeature newSedFeature = sampleDAO.createNewSedimentaryFeature();
				FREDUtil.beanCopy(sedFeature, newSedFeature, new FREDUtil.CopyAll());
				newSedFeatures.add(newSedFeature);
			}
			newSample.setSedimentaryFeatures(newSedFeatures);
		}
		
		//Copy Collectors
		Set<Person> collectors = sample.getCollectors();
		if (!FREDUtil.isEmpty(collectors)) {
			HashSet<Person> newCollectors = new HashSet<Person>(collectors.size());
			newCollectors.addAll(collectors);
			newSample.setCollectors(newCollectors);
		}
		
		//Copy sample images
		images = sample.getSampleMetas();
		if (images != null && images.size() > 0) {
			HashSet<SampleMeta> newImages = new HashSet<SampleMeta>();
			for (Iterator it = images.iterator(); it.hasNext(); ) {
				SampleMeta meta = (SampleMeta)it.next();
				SampleMeta newMeta = sampleDAO.createNewSampleMeta();
				newMeta.setMetaId(meta.getMetaId());
				newMeta.setSample(newSample);
				newImages.add(newMeta);
			}
			newSample.setSampleMetas(newImages);
		}
		return newSample;
	}

	public void deleteRemoveFeatures(String[] featIDs, UserFolder folder, UserAccount user) {
		boolean errFlag = false;
		for (int i = 0; i < featIDs.length; i++) {
			try {
				Feature feature = getFeature(Integer.parseInt(featIDs[i]));
				if (feature.getAudit().getStatus().equals(FREDConstants.APPROVED))
					removeFeature(feature, folder, user);
				else
					deleteFeature(feature, user);
			} catch (Exception e) {
				errFlag = true;
			}
		}
		if (errFlag)
			throw new IllegalStateException("An error has occured. Not all localities have been removed/deleted");
	}
	
	public void deleteFeature(Feature feature, UserAccount user) throws InsufficientPrivelegesException, StorageAccessException {
		Folder folder = feature.getAudit().getFolder();
		if (folder == null)
			folder = feature.getMasterFile();
		
		UserFolder userFolder = folderDAO.getUserFolder(folder.getFolderId(), Integer.parseInt(user.getId()));
		
		if (!userFolder.isAllowedDeleteLocalities())
			throw new InsufficientPrivelegesException();
		
		if (feature.getFrNumber() != null)
			feature.setFrNumber(null);
		
		Audit audit = feature.getAudit();

		featureDAO.delete(feature);
		featureDAO.delete(audit);
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
	
	public void submitFeatures(String[] featIds, UserFolder folder, UserAccount user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, DataInputException {
		for (int i = 0; i < featIds.length; i++) {
			submitFeature(getFeature(Integer.parseInt(featIds[i])), folder, user);
		}
	}
	
	public void submitFeature(Feature feature, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
		if (!isAllowedSubmitFeature(user, feature, folder))
			throw new InsufficientPrivelegesException();
		if (feature.getFeatureType() == null || feature.getRegistrationArea() == null
				|| feature.getSiteId() == null || (!isBacklogFeature(feature) && feature.getLocality() == null))
				throw new MandatoryFieldsMissingException();			
		
		//if outcrop also check sample mandatory fields
		if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
			for (Sample sample : feature.getSamples()) {
				if (!SampleUtil.isMandatoryFieldComplete(sample))
					throw new MandatoryFieldsMissingException();
			}
		}
		
		int masterfile = -1;
		try {
			masterfile = FREDUtil.getMasterfile(feature);
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
		
		Audit audit = feature.getAudit();
		audit.setStatus(FREDConstants.WAITING);
		audit.setSubmittedById(new Integer(user.getId()));
		audit.setSubmittedDate(new Date());
		
		feature.setMasterFile(folderDAO.getFolder(masterfile));
		featureDAO.update(audit);
		featureDAO.update(feature);		
	}
	
	public void revokeFeatures(String[] featIds, UserFolder folder, UserAccount user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException { 
		for (int i = 0; i < featIds.length; i++) {
			revokeFeature(getFeature(Integer.parseInt(featIds[i])), folder, user);
		}
	}

	public void revokeFeature(Feature feature, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!isAllowedRevokeFeature(user, feature, folder))
			throw new InsufficientPrivelegesException();

		Audit audit = feature.getAudit();
		audit.setStatus(FREDConstants.WORKING);
		audit.setSubmittedById(null);
		audit.setSubmittedDate(null);
		
		featureDAO.update(audit);
		
		feature.setMasterFile(null);
		featureDAO.update(feature);		
	}

	public void alterFeatureTypes(String[] featIDs, String newFeatureType, UserFolder folder, UserAccount user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
		for (int i = 0; i < featIDs.length; i++) {
			alterFeatureType(getFeature(Integer.parseInt(featIDs[i])), newFeatureType, folder, user);
		}
	}
	
	public void alterFeatureType(Feature feature, String newFeatureType, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
		if (!folder.isAllowedEditLocalities())
			throw new InsufficientPrivelegesException();
		String oldFeatureType = feature.getFeatureType();
		if (!oldFeatureType.equals(newFeatureType)) {
			Set<Sample> samples = feature.getSamples();
			if (newFeatureType.equals(FREDConstants.OUTCROP)) {
				if (!samples.isEmpty() && samples.size() > 1)
					throw new IllegalStateException("Cannot change to Outcrop as locality has more than one sample"); 
				feature.setDatumElevation(null);
				feature.setDatumType(null);
				feature.setDrillholeLicenceName(null);
				feature.setFinishDate(null);
				feature.setFinishDateRounding(null);
				feature.setFinishDepth(null);
				feature.setPerson(null);
				feature.setStartDate(null);
				feature.setStartDateRounding(null);
				feature.setStartDepth(null);
				for (Sample sample : samples) {
					sample.setAudit(feature.getAudit());
					sample.setBottomDepth(null);
					sample.setTopDepth(null);
					sample.setDrillType(null);
					sampleDAO.update(sample);
				}
			} else if (newFeatureType.equals(FREDConstants.DRILLHOLE)) {
				for (Sample sample : samples) {
					breakApartSampleAudit(sample);
					sampleDAO.update(sample);
				}
			} else if (newFeatureType.equals(FREDConstants.VERTICAL_SECTION)) {
				feature.setDrillholeLicenceName(null);
				for (Sample sample : samples) {
					breakApartSampleAudit(sample);
					sample.setDrillType(null);
					sampleDAO.update(sample);
				}
			}
			AuditEdit edit = featureDAO.createNewAuditEdit();
			edit.setAudit(feature.getAudit());
			edit.setEditedById(Integer.parseInt(user.getId()));
			edit.setEditedDate(new Date());
			edit.setComments("Locality type changed from " + oldFeatureType + " to " + newFeatureType);
			featureDAO.save(edit);
			
			feature.setFeatureType(newFeatureType);
			featureDAO.update(feature);
		}
	}	

	private void breakApartSampleAudit(Sample sample) throws IntrospectionException, StorageAccessException {
		if (sample.getAudit().equals(sample.getFeature().getAudit())) {
			sample.setAudit(new AuditUtil(factory).cloneAudit(sample.getAudit()));
		}
	}
	
	public void mergeFeatures(Feature mergeToFeature, String[] mergeFeatIDs, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, NumberFormatException, IntrospectionException {
		for (int i = 0; i < mergeFeatIDs.length; i++) {
			mergeFeature(mergeToFeature, getFeature(Integer.parseInt(mergeFeatIDs[i])), folder, user);
		}
	}
		
	private void mergeFeature(Feature mergeToFeature, Feature mergeFromFeature, UserFolder folder, UserAccount user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
		if (!folder.isAllowedEditLocalities())
			throw new InsufficientPrivelegesException();
		if (!mergeFromFeature.equals(mergeToFeature.getFeatureId())) {
			if (mergeToFeature.getFeatureType().equals(FREDConstants.OUTCROP) || mergeFromFeature.getFeatureType().equals(FREDConstants.OUTCROP))
				throw new IllegalStateException("Cannot merge outcrop localities");
			
			//FrNumber mergeToFRNumber = FeatureUtil.getFrNumber(mergeToFeature);
			//put in array as feature.getSamples() changes as you change sample's feature
			Object[] samples = mergeFromFeature.getSamples().toArray();
			
			//move all samples from merge feature to parent feature
			for (int i = 0; i < samples.length; i++) {
				Sample sample = (Sample) samples[i];
				//check audits - if same as feature then create new onw
				if (sample.getAudit().equals(mergeFromFeature.getAudit())) {
					Audit newAudit = new AuditUtil(factory).cloneAudit(sample.getAudit());
					featureDAO.save(newAudit);
					sample.setAudit(newAudit);
					sampleDAO.update(sample);
				}
	
				//add comments
				AuditEdit edit = featureDAO.createNewAuditEdit();
				edit.setAudit(sample.getAudit());
				edit.setEditedById(new Integer(user.getId()));
				edit.setEditedDate(new Date());
				edit.setComments("Sample merged into " + getFeatureIdentifyingName(mergeToFeature) + " from " + getFeatureIdentifyingName(mergeFromFeature));
				featureDAO.save(edit);
	
				//set sample FRNumber if currently null
				//if (sample.getFrNumber() == null)
				//	sample.setFrNumber(mergeToFRNumber);
				//sample.setFeature(mergeToFeature);			
				
				sampleDAO.update(sample);
			}
			
			//delete merge feature
			deleteFeature(mergeFromFeature, user);
		}
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
		
		//Get from feature_content
		Collection<? extends Feature> featuresToAdd = folder.getFolder().getFeatures();
		if (featuresToAdd != null)
			features.addAll(featuresToAdd);
		
		//Get from audit
		List<Audit> audits = folderDAO.getAuditsFor(folder.getFolder());
		
		for (Audit audit : audits) {
			// - features
			featuresToAdd = audit.getFeatures();
			if (featuresToAdd != null)
				features.addAll(featuresToAdd);
			
			//- samples
			featuresToAdd = featureDAO.getFeaturesBySample(audit);
			if (featuresToAdd != null)
				features.addAll(featuresToAdd);

			//- results
			featuresToAdd = featureDAO.getFeaturesByRecord(audit);
			if (featuresToAdd != null)
				features.addAll(featuresToAdd);

		}
		
		Feature[] featuresArray = features.toArray(new Feature[features.size()]); 
		Arrays.sort(featuresArray);
		return featuresArray;
	}
	
	public static Feature[] getOrderedFeaturesInMasterfile(Folder masterfile) {
		Set<Feature> features = masterfile.getMasterfileFeatures();
		Feature[] featuresArray = features.toArray(new Feature[features.size()]); 
		Arrays.sort(featuresArray);
		return featuresArray;		
	}
	
	public Feature[] getFeaturesApprovedInTheLastWeek(UserFolder masterfile) throws StorageAccessException {
		GregorianCalendar cal = new GregorianCalendar();
		Date now = cal.getTime();
		cal.add(Calendar.DATE, -7);
		Date then = cal.getTime();
		
		List<Feature> features = featureDAO.getFeaturesInMasterfile(masterfile.getFolder(), then, now, FREDConstants.APPROVED);
		Collections.sort(features);
		return features.toArray(new Feature[features.size()]);
	}
	
	public Feature[] getWaitingFeatures(UserFolder masterfile) throws StorageAccessException {
		List<Feature> features = featureDAO.getFeaturesInMasterfile(masterfile.getFolder(), FREDConstants.WAITING);
		Collections.sort(features);
		return features.toArray(new Feature[features.size()]);
	}
	
	/**
	 * Returns true if a user is allowed to view the locality
	 */
	public boolean isAllowedReadFeature(UserAccount user, Feature feature) throws StorageAccessException {
		String status = feature.getAudit().getStatus();
		if (user == null)
			return false;
		if (!status.equals(FREDConstants.APPROVED)) {
			UserFolder folder = new FolderUtil(factory).getUserFolder(feature.getAudit().getFolder().getFolderId().intValue(), user);
			UserFolder mfFolder = null;
			if (feature.getMasterFile() != null)
				mfFolder = folderDAO.getUserFolder(feature.getMasterFile().getFolderId().intValue(), Integer.parseInt(user.getId()));
			return ((folder != null && folder.isAllowedReadLocalities()) || (mfFolder != null && mfFolder.isAllowedReadLocalities()));

		}
		return true;
	}
	
	/**
	 * Returns true if a user is allowed to view the locality site information
	 */
	public boolean isAllowedReadFeatureSite(UserAccount user, Feature feature) throws StorageAccessException {
		String status = feature.getAudit().getStatus();
		if (!status.equals(FREDConstants.APPROVED)) {
			if (user == null)
				return false;
			UserFolder folder = folderDAO.getUserFolder(feature.getAudit().getFolder().getFolderId().intValue(), Integer.parseInt(user.getId()));
			UserFolder mfFolder = null;
			if (feature.getMasterFile() != null)
				mfFolder = folderDAO.getUserFolder(feature.getMasterFile().getFolderId().intValue(), Integer.parseInt(user.getId()));
			return ((folder != null && folder.isAllowedReadLocalities()) || (mfFolder != null && mfFolder.isAllowedReadLocalities()));
		}
		return true;
	}
	
	public boolean isAllowedEditFeature(UserAccount user, Feature feature, UserFolder folder) throws StorageAccessException {
		String status = feature.getAudit().getStatus();
		if (status.equals(FREDConstants.APPROVED))
			return hasMasterfileRights(user, feature, UserFolder.FOLDER_EDIT_RIGHT) || FREDUtil.checkEditSecurityClass(user);
		
		if (status.equals(FREDConstants.WAITING))
			return hasMasterfileRights(user, feature, UserFolder.FOLDER_EDIT_RIGHT);

		return folder.isAllowedEditLocalities();
	}

	public boolean isAllowedEditApprovedFeature(UserAccount user, Feature feature) throws StorageAccessException {
		if (!feature.getAudit().getStatus().equals(FREDConstants.APPROVED))
			return false;
		return hasMasterfileRights(user, feature, UserFolder.FOLDER_EDIT_RIGHT);
	}
	
	public boolean isAllowedSubmitFeature(UserAccount user, Feature feature, UserFolder folder) {
		String status = feature.getAudit().getStatus();
		if (status.equals(FREDConstants.WAITING) || status.equals(FREDConstants.APPROVED))
			return false;
		return folder.isAllowedSubmitLocalities();
	}
	
	public boolean isAllowedRevokeFeature(UserAccount user, Feature feature, UserFolder folder) {
		if (!feature.getAudit().getStatus().equals(FREDConstants.WAITING))
			return false;
		return folder.isAllowedSubmitLocalities();		
	}
	
	public boolean isAllowedDeleteFeature(UserAccount user, Feature feature, UserFolder userFolder) throws StorageAccessException {
		Audit audit = feature.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;
		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, feature, UserFolder.FOLDER_DELETE_RIGHT, folderDAO);

		return userFolder.isAllowedDeleteLocalities();
	}
	
	/**
	 * Returns true is the user is allowed to approve the locality
	 */
	public boolean isAllowedApproveFeature(UserAccount user, Feature feature) throws StorageAccessException {
		if (WAITING.equals(feature.getAudit().getStatus())) {
			UserFolder folder = folderDAO.getUserFolder(feature.getMasterFile().getFolderId().intValue(), Integer.parseInt(user.getId()));
			if (folder != null)
				return folder.isAllowedApproveLocalities();
		}
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
	
	public void approveFeature(Feature feature, String mapSheet, Integer serialNumber, String recollectionNumber, String comments, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
		if (!hasMasterfileRights(user, feature, UserFolder.FOLDER_APPROVE_RIGHT))
			throw new InsufficientPrivelegesException();
		
		//Check FR number and throw exception if already exists
		FrNumber frNumber = getFrNumber(mapSheet, serialNumber, recollectionNumber);
		if (frNumber != null)
			throw new DataInputException("FR Number", "FR Number already defined in database");
		frNumber = new nz.cri.gns.fred.hibernate.FrNumber();
		frNumber.setMapSheet(mapSheet);
		frNumber.setSerialNumber(serialNumber);
		frNumber.setRecollectionNumber(recollectionNumber);
		
		//update feature and explicitly add to working folder
		Audit audit = feature.getAudit();
		try {
			feature.setFrNumber(frNumber);
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
	
	/**
	 * 'Approves' a backlog entered feature
	 * @param feature
	 * @param user
	 * @throws InsufficientPrivelegesException
	 * @throws StorageAccessException
	 * @throws NamingException 
	 * @throws SQLException 
	 */
	public void approveBacklogFeature(Feature feature, UserAccount user) throws InsufficientPrivelegesException, StorageAccessException, SQLException, NamingException {
		//Put it back in the correct folder
		if (!hasMasterfileRights(user, feature, UserFolder.FOLDER_APPROVE_RIGHT))
			throw new InsufficientPrivelegesException();
		
		Audit audit = feature.getAudit();
		audit.setFolder(null);
		audit.setWorkingComments(null);
		audit.setStatus(APPROVED);
		audit.setApprovedById(new Integer(user.getId()));
		audit.setApprovedDate(new Date());
		audit.setSubmittedById(null);
		audit.setSubmittedDate(null);
		audit.setCuratorComments((audit.getCuratorComments() != null ? audit.getCuratorComments() + "\n" : "") + "Approved after backlog editing");
		
		//delete initial backlog edit comments
		try {
			Set<AuditEdit> edits = audit.getAuditEdits();
			for (AuditEdit edit : edits) {
				if (edit.getComments().equals(BACKLOG_PREPARE_COMMENTS)) {
					featureDAO.delete(edit);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		feature.setMasterFile(folderDAO.getFolder(FREDUtil.getMasterfile(feature)));
		featureDAO.update(audit);
		featureDAO.update(feature);
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
		if (!feature.getAudit().getStatus().equals(APPROVED))
			throw new FolderUtilException("Cannot add a working locality");
		
		UserFolder userFolder = new FolderUtil(factory).getUserFolder(folderId, user);
		if (!userFolder.isAllowedCreateLocalities())
			throw new FolderUtilException("Do not have appropriate rights to add to this folder");
				
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

	/**
	 * Creates a blank feature of the given type, in the given folder.  The
	 * feature and its associated entries are _not_ committed to persistent
	 * storage.
	 * @throws StorageAccessException 
	 */
	public Feature createFeature(int folderId, String featureType, UserAccount user) throws StorageAccessException {
		if (!(featureType.equals(FREDConstants.OUTCROP) 
				|| featureType.equals(FREDConstants.DRILLHOLE) || featureType.equals(FREDConstants.VERTICAL_SECTION))) 
			throw new IllegalArgumentException("Invalid feature type given: " + featureType);
		Feature feature = featureDAO.createNewFeature();
		feature.setFeatureType(featureType);
		Audit audit = featureDAO.createNewAudit();
		audit.setFolder(folderDAO.getFolder(folderId));
		audit.setStatus(FREDConstants.WORKING);
		audit.setCreatedDate(new Date());
		audit.setCreatedById(new Integer(user.getId()));
		feature.setAudit(audit);
		return feature;
	}

	public RegistrationArea getRegistrationArea(int regAreaId) throws StorageAccessException {
		return featureDAO.getRegistrationArea(regAreaId);
	}

	/**
	 * Parses FR Number as string and returns FrNumber object
	 * If FRNumber exists it is returned, or a new FRNumber object is created
	 * @throws StorageAccessException 
	 */
	public FrNumber parseFrNumber(String frNumStr) throws DataInputException, StorageAccessException {
		if (frNumStr != null && frNumStr.indexOf("/f") > 0) {
			String recollectionNumber;
			Integer serialNumber;
			String mapSheet = frNumStr.substring(0, frNumStr.indexOf("/f")).toUpperCase();
			String num = frNumStr.substring(frNumStr.indexOf("/f") + 2);
			try {
				serialNumber = new Integer(num);
				recollectionNumber = null;
			} catch (Exception e) {
				try {
					serialNumber = new Integer(num.substring(0, num.length() - 1));
					recollectionNumber = num.substring(num.length() - 1);
				} catch (Exception e1) {
					throw new DataInputException("FR Number", "Badly formed FR Number");
				}
			}
			FrNumber frNumber = new nz.cri.gns.fred.hibernate.FrNumber();
			frNumber.setMapSheet(mapSheet);
			frNumber.setSerialNumber(serialNumber);
			frNumber.setRecollectionNumber(recollectionNumber);
			return frNumber;
		} else {
			throw new DataInputException("FR Number", "Badly formed or missing FR Number");
		}
	}
	
	/**
	 * Returns the feature for this FR number.  If FEATURE not found then also checks SAMPLE
	 */
	public Feature getFeature(FrNumber frNum) {
		try {
			return (Feature) frNum.getFeatures().iterator().next();
		} catch (Exception e) {
			try {
				Sample sample = (Sample) frNum.getSamples().iterator().next();
				return sample.getFeature();
			} catch (Exception e1) {
				return null;
			}
		}
	}

	public void addSample(Feature feature, String topDepthAsString, String bottomDepthAsString, String drillTypeIdAsString, int folderId, UserAccount user) throws StorageAccessException, DataInputException {
		if (feature.getFeatureType().equals(OUTCROP))
			throw new DataInputException("Sample", "Cannot add samples to an outcrop");
		
		Double bottomDepth = null, topDepth = null;
		Integer drillTypeId = null;
		if (bottomDepthAsString.length() > 0) try {
			bottomDepth = Double.parseDouble(bottomDepthAsString);
		} catch (Exception e) {
			throw new DataInputException("Sample Depths", "Data Missing or Invalid");
		}

		if (drillTypeIdAsString.length() > 0) try {
			drillTypeId = Integer.parseInt(drillTypeIdAsString);
		} catch (Exception e) {
			throw new DataInputException("Sample Depths", "Data Missing or Invalid");
		}

		try {
			topDepth = Double.parseDouble(topDepthAsString);
		} catch (Exception e) {
			throw new DataInputException("Sample Depths", "Data Missing or Invalid");
		}

		Sample sample = new SampleUtil(factory).createSample(feature, folderId, false, user);
		sample.setTopDepth(topDepth);
		sample.setBottomDepth(bottomDepth);
		if (drillTypeId != null)
			sample.setDrillType(sampleDAO.getDrillType(drillTypeId.intValue()));
		
		//add first FRNumber (if one defined)
		//sample.setFrNumber(FeatureUtil.getFrNumber(feature));
		
		sampleDAO.save(sample);
	}

    public Audit update(Audit audit) throws StorageAccessException {
        return featureDAO.update(audit);
    }

    public Audit save(Audit audit) throws StorageAccessException {
        return featureDAO.save(audit);
    }
    
    public static Collection<Sample> getSortedSamples(Feature feature) {
    	Set<Sample> sampleSet = feature.getSamples();
    	Vector<Sample> v = new Vector<Sample>(sampleSet);
    	Collections.sort(v);
    	return v;
    }
    
	/**
	 * @deprecated use saveFeature(Feature feature, User user, String comments, int dataOriginId)
	 */
    public void saveFeature(Feature feature, User user, String comments) throws StorageAccessException {
    	saveFeature(feature, user, comments, FREDConstants.DATA_ORIGIN_ONLINE);
    }
    
	public void saveFeature(Feature feature, User user, String comments, int dataOriginId) throws StorageAccessException {
		
		Audit audit = feature.getAudit();

		if (feature.getFeatureId() == null) {
			//New feature
			audit.setStatus(FREDConstants.WORKING);
			audit.setCreatedById(user.getPersonId());
			audit.setCreatedDate(new Date());
			audit.setDataOrigin((new AuditUtil(factory)).getDataOrigin(new Integer(dataOriginId)));
		} else if (FeatureUtil.isBacklogFeature(feature)) {
			//Backlog editing feature
			AuditEdit edit = featureDAO.createNewAuditEdit();
			edit.setAudit(audit);
			edit.setEditedById(user.getPersonId());
			edit.setEditedDate(new Date());
			edit.setComments("Backlog data editing");
			featureDAO.save(edit);
		} else if (audit.getStatus().equals(FREDConstants.APPROVED)) {
			AuditEdit edit = featureDAO.createNewAuditEdit();
			edit.setAudit(audit);
			edit.setEditedById(user.getPersonId());
			edit.setEditedDate(new Date());
			edit.setComments(comments);
			featureDAO.save(edit);
		}
        
		featureDAO.saveOrUpdate(audit);
		featureDAO.saveOrUpdate(feature);
		
		//add blank sample if one drillhole or vert section and one doesn't exist 
		Set<Sample> samples = feature.getSamples();
		if (!feature.getFeatureType().equals(FREDConstants.OUTCROP) && samples == null) {
			SampleUtil sampleUtil = new SampleUtil(factory);
			Sample sample = sampleUtil.createSample(feature, audit.getFolder().getFolderId(), false, user);
			sampleUtil.save(sample);		
		}		
		
	}
	
	public static String getFeatureIdentifyingName(Feature feature) {
		if (feature.getFrNumber() != null)
			return feature.getFrNumber().getFrNumber();
		if (feature.getFeatureName() != null)
			return feature.getFeatureName();
		if (feature.getOrigCoord() != null)
			return feature.getOrigCoord();
		return "Unnamed " + feature.getFeatureType();
	}
	
	/**
	 * Performs the inverse of getFeatureIdentifyingName (except where name is a coordinate)
	 * @throws StorageAccessException 
	 */
	public Feature getFeatureWithIdentifyingName(String ident) throws StorageAccessException {
		FrNumber frNum = featureDAO.getFrNumber(ident);
		if (frNum != null) 
			return FeatureUtil.getFeature(frNum);	
		return featureDAO.getFeatureWithName(ident);
	}
	
	/**
	 * Backlog method
	 * @throws StorageAccessException 
	 */
	public void addToBacklog(UserFolder folderToAddTo, String mapSheet, int start, int end, UserFolder masterFile, UserAccount user) throws StorageAccessException {
		if (!masterFile.isAllowedReadLocalities())
			return;
		List<FrNumber> numbers = featureDAO.getFrNumbers(mapSheet.toUpperCase(), start, end);
		Folder folder = folderToAddTo.getFolder();
		Folder masterFileFolder = masterFile.getFolder();
		for (FrNumber num : numbers) {
			Feature feature = FeatureUtil.getFeature(num);
			if (feature == null
					|| !feature.getMasterFile().equals(masterFileFolder)
					|| !feature.getAudit().getStatus().equals(APPROVED))
				continue;
			Audit audit = feature.getAudit();
			audit.setFolder(folder);
			audit.setStatus(WORKING);
			AuditEdit edit = featureDAO.createNewAuditEdit();
			edit.setAudit(audit);
			edit.setEditedById(new Integer(user.getId()));
			edit.setEditedDate(new Date());
			edit.setComments(BACKLOG_PREPARE_COMMENTS);
			featureDAO.save(edit);
			featureDAO.update(audit);
		}
	}
	
	public static boolean isBacklogFeature(Feature feature) {
		Folder folder = feature.getAudit().getFolder();
		return (folder != null && folder.getFolderType().getName().equals(Folder.FOLDER_TYPE_BACKLOG));
	}

	public FrNumber getFrNumber(String mapSheet, Integer serialNumber, String recollectionNumber) throws StorageAccessException {
		String serialNum = String.valueOf(serialNumber);
		while (serialNum.length() < 4)
			serialNum = "0" + serialNum;
		return featureDAO.getFrNumber(mapSheet + "/f" + serialNum + DBUtils.nvl(recollectionNumber));		
	}
	
	public List<FrNumber> getFrNumbers(String mapSheet) throws StorageAccessException {
		return featureDAO.getFrNumbers(mapSheet);
	}
	
	public List<FrNumber> getFrNumbers(String mapSheet, int start, int end) throws StorageAccessException {
		return featureDAO.getFrNumbers(mapSheet, start, end);
	}
	
	private static String RECOLL_COMMENTS = "*Recoll:";
	
	public static String combineWorkingComments(String recoll, String workComm) {
		if (recoll != null && recoll.length() > 0)
			return RECOLL_COMMENTS + recoll + "*" + workComm;
		return workComm;
	}
	
	/**
	 * Splits the recollection data from AUDIT.WORKING_COMMENTS.
	 * Returns a String array with two values. First value contains
	 * Working Comments and second value contains Recollection (if present) or NULL 
	 */
	public static String[] splitWorkingComments(String comments) {
		if (comments == null)
			return new String[] {null, null};
		if (comments.startsWith(RECOLL_COMMENTS)) {
			String recoll = comments.substring(8, comments.indexOf("*", 8));
			String workComm = comments.substring(comments.indexOf("*", 8) + 1);
			return new String[] {workComm, recoll};
		} else {
			return new String[] {comments, null};
		}
	}
	
}
