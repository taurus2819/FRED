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
import nz.cri.gns.core.SimpleNameableAndIdentifiable;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.de.MandatoryFieldsMissingException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.Country;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.UserFolder;

public class FeatureUtil extends ModelUtil implements AuditedUtil {

	private FredDAO fredDAO;
	private FolderUtil folderUtil;

	private static String BACKLOG_PREPARE_COMMENTS = "Locality prepared for backlog editing";
	
	public FeatureUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
		this.folderUtil = new FolderUtil(factory);
	}
	
	public Feature copyFeature(Feature feature, String newName, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
		if (!folder.isAllowedCreateLocalities())
			throw new InsufficientPrivelegesException();
		Audit audit = fredDAO.createNewAudit();
		audit.setStatus(FREDConstants.WORKING);
		audit.setCreatedById(new Integer(user.getId()));
		audit.setCreatedDate(new Date());
		audit.setFolder(folder.getFolder());
		fredDAO.saveOrUpdate(audit);
		
		Feature newFeature = (Feature)((nz.cri.gns.fred.hibernate.Feature)feature).clone();
		newFeature.setFeatureId(null);
		newFeature.setFeatureName(newName);
		newFeature.setAudit(audit);
		newFeature.setFrNumber(null);
		//A new copy should not have an entry in folder_contents
		newFeature.setFolders(null);
		
		//Copy feature images
		newFeature.setMetaCats(feature.getMetaCats());

		//Clear out relationships pointing _to_ it
		newFeature.setRelationships(null);
		//Remove any samples that have come across
		newFeature.setSamples(null);
		//Save the new feature!
		fredDAO.saveOrUpdate(newFeature);
		
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
			fredDAO.saveOrUpdate(newSample);
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
		Sample newSample = fredDAO.createNewSample(newFeature);
		FREDUtil.beanCopy(sample, newSample, 
				new FREDUtil.ExcludeByType(Set.class, 
				new FREDUtil.ExcludeByName(FREDUtil.toVector("audit", "sampleId", "feature", "frNumber")))
		);
		//Clear the fr number if it has one
		//Copy relationships
		Set<Relationship> relationships = sample.getRelationships();
		if (relationships != null && relationships.size() > 0) {
			HashSet<Relationship> newRels = new HashSet<Relationship>();
			for (Relationship rel : relationships) {
				Relationship newRel = fredDAO.createNewRelationship();
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
				SentTo newSentTo = fredDAO.createNewSentTo();
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
				SedimentaryFeature newSedFeature = fredDAO.createNewSedimentaryFeature();
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
		newSample.setMetaCats(sample.getMetaCats());
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
		
		UserFolder userFolder = folderUtil.getUserFolder(folder.getFolderId(), Integer.parseInt(user.getId()));
		
		if (!userFolder.isAllowedDeleteLocalities())
			throw new InsufficientPrivelegesException();
		
		if (!FREDUtil.isEmpty(feature.getRelationships())) {
			try {
				Feature relFeature = ((Relationship)feature.getRelationships().iterator().next()).getSample().getFeature();
				throw new IllegalStateException("Cannot delete this locality as it is referenced in a relationship by " + getFeatureIdentifyingName(relFeature));
			} catch (Exception e) {
				throw new IllegalStateException("Cannot delete this locality as it is referenced in a relationship");
			}
		}
		
		fredDAO.delete(feature);
	}
	
	public void removeFeature(Feature feature, UserFolder userFolder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!feature.getAudit().getStatus().equals(FREDConstants.APPROVED))
			throw new IllegalStateException("Cannot remove a working locality");
		if (!userFolder.isAllowedDeleteLocalities())
			throw new InsufficientPrivelegesException();
		
		Folder folder = userFolder.getFolder();
		folder.getFeatures().remove(feature);
		feature.getFolders().remove(folder);
		fredDAO.saveOrUpdate(feature);
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
		
		int masterfileId = -1;
		try {
			masterfileId = SiteUtil.getMasterfile(feature);
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
		
		Audit audit = feature.getAudit();
		audit.setStatus(FREDConstants.WAITING);
		audit.setSubmittedById(new Integer(user.getId()));
		audit.setSubmittedDate(new Date());
		
		feature.setMasterFile(fredDAO.get(masterfileId, nz.cri.gns.fred.hibernate.Folder.class));
		fredDAO.saveOrUpdate(audit);
		fredDAO.saveOrUpdate(feature);		
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
		
		fredDAO.saveOrUpdate(audit);
		
		feature.setMasterFile(null);
		fredDAO.saveOrUpdate(feature);		
	}

	public void alterFeatureTypes(String[] featIDs, String newFeatureType, UserFolder folder, UserAccount user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
		for (int i = 0; i < featIDs.length; i++) {
			alterFeatureType(getFeature(Integer.parseInt(featIDs[i])), newFeatureType, folder, user);
		}
	}
	
	public void alterFeatureType(Feature feature, String newFeatureType, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
		if (!folder.isAllowedEditLocalities())
			throw new InsufficientPrivelegesException();
		if (feature.getAudit().getStatus().equals(FREDConstants.WAITING))
			throw new IllegalStateException("Cannot change type as status = waiting");
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
					fredDAO.saveOrUpdate(sample);
				}
			} else if (newFeatureType.equals(FREDConstants.DRILLHOLE)) {
				for (Sample sample : samples) {
					breakApartSampleAudit(sample);
					fredDAO.saveOrUpdate(sample);
				}
			} else if (newFeatureType.equals(FREDConstants.VERTICAL_SECTION)) {
				feature.setDrillholeLicenceName(null);
				for (Sample sample : samples) {
					breakApartSampleAudit(sample);
					sample.setDrillType(null);
					fredDAO.saveOrUpdate(sample);
				}
			}
			AuditEdit edit = fredDAO.createNewAuditEdit();
			edit.setAudit(feature.getAudit());
			edit.setEditedById(Integer.parseInt(user.getId()));
			edit.setEditedDate(new Date());
			edit.setComments("Locality type changed from " + oldFeatureType + " to " + newFeatureType);
			fredDAO.saveOrUpdate(edit);
			
			feature.setFeatureType(newFeatureType);
			fredDAO.saveOrUpdate(feature);
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
		
	public void mergeFeature(Feature mergeToFeature, Feature mergeFromFeature, UserFolder folder, UserAccount user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
		if (!folder.isAllowedEditLocalities())
			throw new InsufficientPrivelegesException();
		if (!mergeFromFeature.equals(mergeToFeature)) {
			if (mergeToFeature.getFeatureType().equals(FREDConstants.OUTCROP) || mergeFromFeature.getFeatureType().equals(FREDConstants.OUTCROP))
				throw new IllegalStateException("Cannot merge outcrop localities");
			
			FrNumber mergeFromFrNumber = mergeFromFeature.getFrNumber();
			FrNumber mergeFromYardFrNumber = mergeFromFeature.getYardFrNumber();
			//put in array as feature.getSamples() changes as you change sample's feature
			Object[] samples = mergeFromFeature.getSamples().toArray();
			
			//move all samples from merge feature to parent feature
			for (int i = 0; i < samples.length; i++) {
				Sample sample = (Sample) samples[i];
				//check audits - if same as feature then create new onw
				if (sample.getAudit().equals(mergeFromFeature.getAudit())) {
					Audit newAudit = new AuditUtil(factory).cloneAudit(sample.getAudit());
					fredDAO.saveOrUpdate(newAudit);
					sample.setAudit(newAudit);
				}
	
				//add comments
				AuditEdit edit = fredDAO.createNewAuditEdit();
				edit.setAudit(sample.getAudit());
				edit.setEditedById(new Integer(user.getId()));
				edit.setEditedDate(new Date());
				edit.setComments("Sample merged into " + getFeatureIdentifyingName(mergeToFeature) + " from " + getFeatureIdentifyingName(mergeFromFeature));
				fredDAO.saveOrUpdate(edit);
	
				//set sample FRNumber if currently null
				if (sample.getFrNumber() == null && mergeFromFrNumber != null)
					sample.setFrNumber(mergeFromFrNumber);
				if (sample.getYardFrNumber() == null && mergeFromYardFrNumber != null)
					sample.setYardFrNumber(mergeFromYardFrNumber);
				
				//delete any relationships that reference MergeToFeature (otherwise would be referencing itself
				Set<Relationship> relationships = sample.getRelationships();
				for (Relationship relationship : relationships) {
					if (relationship.getFeature() != null && relationship.getFeature().equals(mergeToFeature)) {
						sample.getRelationships().remove(relationship);
						fredDAO.delete(relationship);
					}
				}
				
				sample.setFeature(mergeToFeature);
				mergeToFeature.getSamples().add(sample);
				mergeFromFeature.getSamples().remove(sample);
				fredDAO.saveOrUpdate(sample);
			}
			
			
			//need to remove FRNumbers to stop hibernate cascade deleting them
			mergeFromFeature.setFrNumber(null);
			mergeFromFeature.setYardFrNumber(null);
			
			//move any relationships referencing soon to be deleted feature
			Set<Relationship> relationships = mergeFromFeature.getRelationships();
			for (Relationship relationship : relationships) {
				if (relationship.getSample().getFeature().equals(mergeToFeature))
					fredDAO.delete(relationship);
				else {
					relationship.setFeature(mergeToFeature);
					mergeFromFeature.getRelationships().remove(relationship);
					fredDAO.saveOrUpdate(relationship);
				}
			}
			mergeFromFeature.setRelationships(null);
			
			//delete merge feature
			deleteFeature(mergeFromFeature, user);
		}
	}	
	
	public Feature getFeature(int featureId) throws StorageAccessException {
		return fredDAO.get(featureId, nz.cri.gns.fred.hibernate.Feature.class);
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
		List<Audit> audits = getAuditsFor(folder.getFolder());
		
		for (Audit audit : audits) {
			// - features
			featuresToAdd = audit.getFeatures();
			if (featuresToAdd != null)
				features.addAll(featuresToAdd);
			
			//- samples
			featuresToAdd = getFeaturesBySample(audit);
			if (featuresToAdd != null)
				features.addAll(featuresToAdd);
			
			//- records
			featuresToAdd = getFeaturesByRecord(audit);
			if (featuresToAdd != null)
				features.addAll(featuresToAdd);
		}
		
		Feature[] featuresArray = features.toArray(new Feature[features.size()]); 
		Arrays.sort(featuresArray);
		return featuresArray;
	}
	
	public List<Audit> getAuditsFor(Folder folder) throws StorageAccessException {
		return fredDAO.getList("FROM AuditTable as a WHERE a.folder = ?", Audit.class, folder);
	}
	
	public List<Feature> getFeaturesBySample(Audit audit) throws StorageAccessException {
		return fredDAO.getList("SELECT s.feature FROM Sample AS s WHERE s.audit = ?", Feature.class, audit);
	}
	
	public List<Feature> getFeaturesByRecord(Audit audit) throws StorageAccessException {
		return fredDAO.getList("SELECT r.sample.feature FROM Record AS r WHERE r.audit = ?", Feature.class, audit);
	}
	
	public List<Feature> getFeatures(List<Sample> samples) {
		Set<Feature> features = new HashSet<Feature>();
		for (Sample sample : samples)
			features.add(sample.getFeature());
		return FREDUtil.getSortedList(features);
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
		
		List<Feature> features = getFeaturesInMasterfile(masterfile.getFolder(), then, now, FREDConstants.APPROVED);
		Collections.sort(features);
		return features.toArray(new Feature[features.size()]);
	}
	
	public Feature[] getWaitingFeatures(UserFolder masterfile) throws StorageAccessException {
		List<Feature> features = getFeaturesInMasterfile(masterfile.getFolder(), FREDConstants.WAITING);
		Collections.sort(features);
		return features.toArray(new Feature[features.size()]);
	}
	
	public List<Feature> getFeaturesInMasterfile(Folder masterfileFolder, Date startDate, Date endDate, String status) throws StorageAccessException {
        return fredDAO.getList("FROM Feature as f WHERE f.masterFile = ? AND "
           		+ (status.equals(FREDConstants.WAITING) ? "f.audit.submittedDate" : "f.audit.approvedDate")
           		+ " BETWEEN ? AND ? AND f.audit.status = ?"
           		, Feature.class, masterfileFolder, startDate, endDate, status);
	}

	public List<Feature> getFeaturesInMasterfile(Folder masterfileFolder, String status) throws StorageAccessException {
        return fredDAO.getList("FROM Feature as f WHERE f.masterFile = ? AND f.audit.status = ?"
           		, Feature.class, masterfileFolder, status);
	}
	
	/**
	 * Returns true if a user is allowed to view the locality
	 * always true if user != null && status == approved
	 */
	public boolean isAllowedReadFeature(UserAccount user, Feature feature) throws StorageAccessException {
		if (user == null)
			return false;
		String status = feature.getAudit().getStatus();
		if (!status.equals(FREDConstants.APPROVED)) {
			UserFolder folder = new FolderUtil(factory).getUserFolder(feature.getAudit().getFolder().getFolderId().intValue(), user);
			UserFolder mfFolder = null;
			if (feature.getMasterFile() != null)
				mfFolder = folderUtil.getUserFolder(feature.getMasterFile().getFolderId().intValue(), Integer.parseInt(user.getId()));
			return ((folder != null && folder.isAllowedReadLocalities()) || (mfFolder != null && mfFolder.isAllowedReadLocalities()));
		}
		return true;
	}
	
	/**
	 * Returns true if a user is allowed to view the locality site information
	 * always true if status == approved
	 */
	public boolean isAllowedReadFeatureSite(UserAccount user, Feature feature) throws StorageAccessException {
		String status = feature.getAudit().getStatus();
		if (!status.equals(FREDConstants.APPROVED)) {
			if (user == null)
				return false;
			UserFolder folder = folderUtil.getUserFolder(feature.getAudit().getFolder().getFolderId().intValue(), Integer.parseInt(user.getId()));
			UserFolder mfFolder = null;
			if (feature.getMasterFile() != null)
				mfFolder = folderUtil.getUserFolder(feature.getMasterFile().getFolderId().intValue(), Integer.parseInt(user.getId()));
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
			return hasMasterfileRights(user, feature, UserFolder.FOLDER_DELETE_RIGHT, fredDAO);

		return userFolder.isAllowedDeleteLocalities();
	}
	
	/**
	 * Returns true is the user is allowed to approve the locality
	 */
	public boolean isAllowedApproveFeature(UserAccount user, Feature feature) throws StorageAccessException {
		if (WAITING.equals(feature.getAudit().getStatus())) {
			UserFolder folder = folderUtil.getUserFolder(feature.getMasterFile().getFolderId().intValue(), Integer.parseInt(user.getId()));
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
		return hasMasterfileRights(user, feature, right, fredDAO);
	}
	
	public boolean hasMasterfileRights(UserAccount user, Feature feature, int right, FredDAO fredDAO) throws NumberFormatException, StorageAccessException {
		Folder masterfile = feature.getMasterFile();
		if (masterfile == null)
			return false;
		
		UserFolder masterfileFolder = folderUtil.getUserFolder(masterfile.getFolderId().intValue(), Integer.parseInt(user.getId()));
		
		return (masterfileFolder == null) ? false : (masterfileFolder.getRights() & right) > 0;
	}

	public Sample getOutcropSample(Feature feature) {
		if (!feature.getFeatureType().equals(FREDConstants.OUTCROP))
			throw new IllegalArgumentException("Feature is not an outcrop");
		
		return new Vector<Sample>(feature.getSamples()).get(0);
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
			fredDAO.saveOrUpdate(feature);
		} catch (Exception e) {
		}
		
		//update audit table
		audit.setStatus(APPROVED);
		audit.setApprovedById(new Integer(user.getId()));
		audit.setApprovedDate(new Date());
		audit.setFolder(null);
		audit.setWorkingComments(null);
		audit.setCuratorComments(comments);
		fredDAO.saveOrUpdate(audit);
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
			for (AuditEdit edit : audit.getAuditEdits()) {
				if (edit.getComments().equals(BACKLOG_PREPARE_COMMENTS)) {
					audit.getAuditEdits().remove(edit);
					break;
				}
			}
		} catch (Exception e) {	}
		feature.setMasterFile(fredDAO.get(SiteUtil.getMasterfile(feature), nz.cri.gns.fred.hibernate.Folder.class));
		fredDAO.saveOrUpdate(audit);
		fredDAO.saveOrUpdate(feature);
	}
	
	public void rejectLocality(Feature feature, String comments, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!hasMasterfileRights(user, feature, UserFolder.FOLDER_APPROVE_RIGHT))
			throw new InsufficientPrivelegesException();
		
		Audit audit = feature.getAudit();
		audit.setStatus(REJECTED);
		audit.setCuratorComments(comments);
		fredDAO.saveOrUpdate(audit);
	}
	
	public void addToFolder(Feature feature, int folderId, UserAccount user) throws StorageAccessException, DataInputException {
		if (!feature.getAudit().getStatus().equals(APPROVED))
			throw new DataInputException("Folder", "Cannot add a working locality");
		
		UserFolder userFolder = new FolderUtil(factory).getUserFolder(folderId, user);
		if (!userFolder.isAllowedCreateLocalities())
			throw new DataInputException("Folder", "Do not have appropriate rights to add to this folder");
				
		feature.getFolders().add(fredDAO.get(folderId, nz.cri.gns.fred.hibernate.Folder.class));
	}
	
	/**
	 * Returns the next available FR number - <b>not</b> saved to the DB
	 * @param feature
	 */
	public FrNumber getNextAvailableFrNumber(Feature feature) throws SQLException, NamingException, StorageAccessException {
		String mapSheet = SiteUtil.getFrNumberMapSheet(feature);
		return getNextAvailableFrNumber(mapSheet);
	}

	/**
	 * Returns the next available FR number - <b>not</b> saved to the DB
	 * @param mapSheet
	 */
	public FrNumber getNextAvailableFrNumber(String mapSheet) throws StorageAccessException {
		int nextAvailable = getNextAvailableSerialNumber(mapSheet);
		FrNumber frNum = fredDAO.createFRNumber();
		frNum.setMapSheet(mapSheet);
		frNum.setSerialNumber(nextAvailable);
		return frNum;
	}

	public Integer getNextAvailableSerialNumber(String mapSheet) throws StorageAccessException {
		Integer maxNum = fredDAO.getFirst("SELECT max(fr.serialNumber) FROM FrNumber AS fr WHERE fr.serialNumber < 6000 AND fr.obsolete IS NULL AND fr.mapSheet = ?", Integer.class, mapSheet);
		if (maxNum == null)
			return 1;
		return maxNum;
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
		Feature feature = fredDAO.createNewFeature();
		feature.setFeatureType(featureType);
		Audit audit = fredDAO.createNewAudit();
		audit.setFolder(fredDAO.get(folderId, nz.cri.gns.fred.hibernate.Folder.class));
		audit.setStatus(FREDConstants.WORKING);
		audit.setCreatedDate(new Date());
		audit.setCreatedById(new Integer(user.getId()));
		feature.setAudit(audit);
		return feature;
	}

	public RegistrationArea getRegistrationArea(int regAreaId) throws StorageAccessException {
		return fredDAO.get(regAreaId, nz.cri.gns.fred.hibernate.RegistrationArea.class);
	}

	public List<RegistrationArea> getRegistrationAreas() throws StorageAccessException {
		return fredDAO.getList("FROM RegistrationArea AS r", RegistrationArea.class);
	}
	
	public List<FrNumber> getFrNumbersByString(String frNumStr) throws DataInputException, StorageAccessException {
		try {
			if (frNumStr.indexOf("-") > 0) {
				Object[] frNumBits = parseFrNumber(frNumStr.substring(0, frNumStr.indexOf("-")));
				Integer endSerialNum = new Integer(frNumStr.substring(frNumStr.indexOf("-") + 1));
				return fredDAO.getList("FROM FrNumber AS f WHERE f.mapSheet = ? AND f.serialNumber BETWEEN ? AND ?", FrNumber.class, (String)frNumBits[0], (Integer)frNumBits[1], endSerialNum);
			} else {
				List<FrNumber> frNumbers = new Vector<FrNumber>();
				FrNumber frNum = getMetricFrNumberByString(frNumStr, false);
				if (frNum != null)
					frNumbers.add(frNum);
				frNum = getYardFrNumberByString(frNumStr, false);
				if (frNum != null)
					frNumbers.add(frNum);
				return frNumbers;
			}
		} catch (Exception e) {}
		return null;
	}
	
	public FrNumber getMetricFrNumberByString(String frNumStr, boolean createNew) throws DataInputException, StorageAccessException {
		return getFrNumberByString(frNumStr, createNew, false);
	}
	
	public FrNumber getYardFrNumberByString(String frNumStr, boolean createNew) throws DataInputException, StorageAccessException {
		return getFrNumberByString(frNumStr, createNew, true);
	}
	
	private FrNumber getFrNumberByString(String frNumStr, boolean createNew, boolean yard) throws DataInputException, StorageAccessException {
		Object[] frNumBits = parseFrNumber(frNumStr);
		FrNumber frNumber = null;
		if (yard)
			frNumber = getYardFrNumber(frNumBits[0] + "/f" + frNumBits[1] + ((frNumBits[2] != null) ? frNumBits[2] : ""));
		else
			frNumber = getFrNumber(frNumBits[0] + "/f" + frNumBits[1] + ((frNumBits[2] != null) ? frNumBits[2] : ""));
		if (frNumber == null && createNew) {
			frNumber = new nz.cri.gns.fred.hibernate.FrNumber();
			frNumber.setMapSheet((String)frNumBits[0]);
			frNumber.setSerialNumber((Integer)frNumBits[1]);
			frNumber.setRecollectionNumber((String)frNumBits[2]);
			if (yard)
				frNumber.setObsolete("Y");
		}
		return frNumber;	
	}
	
	/**
	* returns array containing
	* 0. Map Sheet (String)
	* 1. Serial Number (Integer)
	* 2. Recollection Number (String)
	 * @throws DataInputException 
	 */
	public Object[] parseFrNumber(String frNumStr) throws DataInputException {
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
					recollectionNumber = num.substring(num.length() - 1).toUpperCase();
				} catch (Exception e1) {
					throw new DataInputException("FR Number", "Badly formed FR Number");
				}
			}
			String serialNumStr = String.valueOf(serialNumber);
			while (serialNumStr.length() < 4)
				serialNumStr = "0" + serialNumStr;
			
			Object[] frNumBits = {mapSheet, serialNumber, recollectionNumber};
			return frNumBits;
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
		} catch (Exception e) {}
		try {
			return (Feature) frNum.getFeaturesByYard().iterator().next();
		} catch (Exception e) {}
		try {
			Sample sample = (Sample) frNum.getSamples().iterator().next();
			return sample.getFeature();
		} catch (Exception e) {}
		try {
			Sample sample = (Sample) frNum.getSamplesByYard().iterator().next();
			return sample.getFeature();
		} catch (Exception e) {}
		return null;
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
			sample.setDrillType(fredDAO.get(drillTypeId, nz.cri.gns.fred.hibernate.DrillType.class));
		
		//add first FRNumber (if one defined)
		//sample.setFrNumber(FeatureUtil.getFrNumber(feature));
		
		fredDAO.saveOrUpdate(sample);
	}

    public Audit update(Audit audit) throws StorageAccessException {
        return fredDAO.saveOrUpdate(audit);
    }

    public Audit saveOrUpdate(Audit audit) throws StorageAccessException {
        return fredDAO.saveOrUpdate(audit);
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
			AuditEdit edit = fredDAO.createNewAuditEdit();
			edit.setAudit(audit);
			edit.setEditedById(user.getPersonId());
			edit.setEditedDate(new Date());
			edit.setComments("Backlog data editing");
			fredDAO.saveOrUpdate(edit);
		} else if (audit.getStatus().equals(FREDConstants.APPROVED)) {
			AuditEdit edit = fredDAO.createNewAuditEdit();
			edit.setAudit(audit);
			edit.setEditedById(user.getPersonId());
			edit.setEditedDate(new Date());
			edit.setComments(comments);
			fredDAO.saveOrUpdate(edit);
		}
        
		fredDAO.saveOrUpdate(audit);
		fredDAO.saveOrUpdate(feature);
		
		//add blank sample if one drillhole or vert section and one doesn't exist 
		Set<Sample> samples = feature.getSamples();
		if (!feature.getFeatureType().equals(FREDConstants.OUTCROP) && samples == null) {
			SampleUtil sampleUtil = new SampleUtil(factory);
			Sample sample = sampleUtil.createSample(feature, audit.getFolder().getFolderId(), false, user);
			sampleUtil.saveOrUpdate(sample);		
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
		try {
			FrNumber frNum = getMetricFrNumberByString(ident, false);
			if (frNum == null)
				frNum = getYardFrNumberByString(ident, false);
			if (frNum != null) 
				return getFeature(frNum);
		} catch (Exception e) {}
		return getFeatureWithName(ident);
	}
	
	public Feature getFeatureWithName(String name) throws StorageAccessException {
		return fredDAO.getFirst("FROM Feature AS f WHERE f.featureName = ?", Feature.class, name);
	}
	
	/**
	 * Finds feature in folder with matching FeatureName
	 * @throws StorageAccessException 
	 */
	public Feature getFeatureWithName(String ident, UserFolder folder) throws StorageAccessException {
		if (ident == null)
			return null;
		for (Feature feature : getFeaturesInFolder(folder)) {
			if (ident.equals(feature.getFeatureName()))
				return feature;
		}
		return null;
	}
	

	
	public FrNumber getFrNumber(String frNum) throws StorageAccessException {
		return fredDAO.getFirst("FROM FrNumber AS f WHERE f.frNumber = ? AND f.obsolete IS NULL", FrNumber.class, frNum);
	}

	public FrNumber getYardFrNumber(String frNum) throws StorageAccessException {
		return fredDAO.getFirst("FROM FrNumber AS f WHERE f.frNumber = ? AND f.obsolete IS NOT NULL", FrNumber.class, frNum);
	}
	
	/**
	 * Backlog method
	 * @throws StorageAccessException 
	 */
	public void addToBacklog(UserFolder folderToAddTo, String mapSheet, int start, int end, UserFolder masterFile, UserAccount user) throws StorageAccessException {
		if (!masterFile.isAllowedReadLocalities())
			return;
		List<FrNumber> numbers = getFrNumbers(mapSheet.toUpperCase(), start, end);
		Folder folder = folderToAddTo.getFolder();
		Folder masterFileFolder = masterFile.getFolder();
		for (FrNumber num : numbers) {
			Feature feature = getFeature(num);
			if (feature == null
					|| !feature.getMasterFile().equals(masterFileFolder)
					|| !feature.getAudit().getStatus().equals(APPROVED))
				continue;
			Audit audit = feature.getAudit();
			audit.setFolder(folder);
			audit.setStatus(WORKING);
			AuditEdit edit = fredDAO.createNewAuditEdit();
			edit.setAudit(audit);
			edit.setEditedById(new Integer(user.getId()));
			edit.setEditedDate(new Date());
			edit.setComments(BACKLOG_PREPARE_COMMENTS);
			fredDAO.saveOrUpdate(edit);
			fredDAO.saveOrUpdate(audit);
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
		return getFrNumber(mapSheet + "/f" + serialNum + DBUtils.nvl(recollectionNumber));		
	}
	
	public List<FrNumber> getFrNumbers(String mapSheet) throws StorageAccessException {
		return fredDAO.getList("FROM FrNumber AS f WHERE f.mapSheet = ? AND f.obsolete IS NULL", FrNumber.class, mapSheet);
	}
	
	public List<FrNumber> getFrNumbers(String mapSheet, Integer start, Integer end) throws StorageAccessException {
		return fredDAO.getList("FROM FrNumber AS f WHERE f.mapSheet = ? AND f.serialNumber BETWEEN ? AND ? AND f.obsolete IS NULL", FrNumber.class, mapSheet, start, end);
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
	
	public Integer getTotalFeatureCount() throws StorageAccessException {
		return fredDAO.getFirst("SELECT COUNT(*) FROM Feature AS f WHERE f.audit.status=?", Integer.class, AuditUtil.APPROVED);
	}
	
	public Date getLastFeatureApprovalDate() throws StorageAccessException {
		try {
			return fredDAO.getList("SELECT MAX(f.audit.approvedDate) FROM Feature AS f", Date.class).get(0);
		} catch (Exception e) {
			return null;
		}
	}
		
	public static String formatDepthForOutput(Double depth, String unit) {
		StringBuffer d = new StringBuffer(FREDUtil.formatDoubleForOutput(depth, 3)).append(" ").append(unit);
		if (FEET_UNIT.equals(unit))
			d.append(" (").append(FREDUtil.formatDoubleForOutput(new Double(depth.doubleValue() * FT_TO_M), 3)).append(" m)");
		return d.toString();
	}
	
	public Country getCountry(String countryCode) throws StorageAccessException {
		return fredDAO.getFirst("FROM Country AS c WHERE c.countryCode = ?", Country.class, countryCode);
	}
	
	public List<Country> getCountries() throws StorageAccessException {
		return fredDAO.getList("FROM Country AS c", Country.class);
	}
	
	public List<SimpleNameableAndIdentifiable> getFrMapSheetsAsNameable() throws StorageAccessException {
		List<String> sheetsAsString = getFrMapSheets();
		List<SimpleNameableAndIdentifiable> sheets = new Vector<SimpleNameableAndIdentifiable>();
		for (String sheetAsString : sheetsAsString) {
			SimpleNameableAndIdentifiable sheet = new SimpleNameableAndIdentifiable(sheetAsString, sheetAsString);
			sheets.add(sheet);
		}
		return sheets;
	}
	
	public List<String> getFrMapSheets() throws StorageAccessException {
	       return fredDAO.getList("SELECT DISTINCT fr.mapSheet FROM FrNumber AS fr", String.class);
		}
	
	public String getFullLocalityPDFURL(Feature feature) {
		StringBuffer sb = new StringBuffer("FeatIDs=").append(feature.getFeatureId());
		for (Sample sample : feature.getSamples()) {
			if (!FREDConstants.OUTCROP.equals(feature.getFeatureType()))
				sb.append("&SampIDs=").append(sample.getSampleId());
			for (Record record : sample.getRecords())
				sb.append("&RecIDs=").append(record.getRecordId());
		}
		return sb.toString();
	}
	
}