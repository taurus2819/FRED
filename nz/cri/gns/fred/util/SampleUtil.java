package nz.cri.gns.fred.util;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.SampleDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RelationType;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.RelationshipType;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.UserFolder;

/**
 *
 */
public class SampleUtil extends ModelUtil implements FREDConstants {

	private SampleDAO sampleDAO;
	private FolderDAO folderDAO;

	/**
	 * Implements
	 * 	DECODE(F.Feature_Type, 'Outcrop', NULL, DECODE(S.Top_Depth || S.Bottom_Depth || L2.Name, NULL, 'Depth Not Specified',
		DECODE(S.Top_Depth, NULL, NULL, S.Top_Depth || 'm') || DECODE(S.Bottom_Depth, NULL, NULL, ' - ' || S.Bottom_Depth || 'm')
	    || DECODE(L2.Name, NULL, NULL, ' ' || L2.Name))) AS Drillhole_Depth, 

	 * @param sample
	 * @return
	 */
	public static String getDrillHoleDepthDescription(Sample sample) {
		Feature feature = sample.getFeature();
		
		//Not relevant for outcrops
		if (feature.getFeatureType().equals(OUTCROP))
			return null;
		
		if (sample.getTopDepth() == null && sample.getBottomDepth() == null && sample.getDrillType() == null)
			return DEPTH_NOT_SPECIFIED;
		
		String desc = (sample.getTopDepth() != null) ? sample.getTopDepth() + "m" : "";
		if (sample.getBottomDepth() != null) {
			desc += " - " + sample.getBottomDepth() + "m";
		}
		if (sample.getDrillType() != null) {
			desc += " " + sample.getDrillType().getName();
		}
		
		return desc;
	}
	
	public static boolean hasDepthInformation(Sample sample) {
		return sample.getTopDepth() != null || sample.getBottomDepth() != null || sample.getDrillType() != null;
	}
	
	public SampleUtil(DAOFactory factory) {
		super(factory);
		this.sampleDAO = factory.getSampleDAO();
		this.folderDAO = factory.getFolderDAO();
	}
	
	public void deleteSample(int sampleId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		Sample sample = sampleDAO.getSample(sampleId);
		
		if (!isAllowedDeleteSample(sample, folder, user))
			throw new InsufficientPrivelegesException();
		
		sampleDAO.delete(sample);
		//TODO Ben also checked if the feature was sampleless and added if it was.??	
	}

	public void submitSample(int sampleId, UserFolder folder, UserAccount user) throws DataInputException, InsufficientPrivelegesException, StorageAccessException {
		Sample sample = sampleDAO.getSample(sampleId);
		if (!folder.isAllowedSubmitLocalities() || sample.getAudit().getStatus().equals(WAITING))
			throw new InsufficientPrivelegesException();
		if (sample.getCollectors() == null || sample.getCollectors().size() == 0 || sample.getCollectionDate() == null || sample.getInPlace() == null)
			throw new DataInputException("Mandatory Fields", "Not all mandatory fields completed");
		
		//Update the audit log, so long as this isn't an outcrop
		if (!sample.getFeature().getFeatureType().equals(OUTCROP)) {
			Audit audit = sample.getAudit();
			audit.setStatus(APPROVED);		//Samples don't need approval
			audit.setSubmittedById(new Integer(user.getId()));
			audit.setSubmittedDate(new Date());
			audit.setWorkingComments(null);
			audit.setFolder(null);
			sampleDAO.update(audit);
		}
	}
	
	/**
	 * @param sample
	 * @param folder
	 * @param user
	 * @return
	 * @throws StorageAccessException
	 * @throws NumberFormatException
	 */
	private boolean isAllowedDeleteSample(Sample sample, UserFolder folder, UserAccount user) throws StorageAccessException {
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;

		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_DELETE_RIGHT, folderDAO);

		return folder.isAllowedDeleteLocalities();
	}
	
	public boolean isAllowedEditSample(User user, Sample sample, UserFolder userFolder) throws StorageAccessException {
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_EDIT_RIGHT, folderDAO) ||
				FREDUtil.checkEditSecurityClass(user);
		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_EDIT_RIGHT, folderDAO);

		return userFolder.isAllowedEditLocalities();
	}
	
	public boolean isAllowedDeleteSample(User user, Sample sample, UserFolder userFolder) throws StorageAccessException {
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;
		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_DELETE_RIGHT, folderDAO);

		return userFolder.isAllowedDeleteLocalities();
	}
	
	public boolean isAllowedSubmitSample(User user, Sample sample, UserFolder userFolder) throws NumberFormatException, StorageAccessException {
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;	
		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_SUBMIT_RIGHT, folderDAO);
		
		return userFolder.isAllowedSubmitLocalities();
	}

	public Sample getSample(int sampleId) throws StorageAccessException {
		return sampleDAO.getSample(sampleId);
	}
	
	public AuditEdit getMostRecentEdit(Audit audit) throws StorageAccessException {
		return sampleDAO.getMostRecentEdit(audit);
	}
	
	public int getPaleontologyRecordCount(Sample sample) {
		int count = 0;
		for (Iterator it = sample.getRecords().iterator(); it.hasNext(); ) {
			count += (((Record)it.next()).getPaleontology() != null) ? 1 : 0;
		}
		return count;
	}

	/**
	 * Return a new sample initialised with the given information
	 * @param reuseFeatureAudit 
	 * @throws StorageAccessException 
	 */
	public Sample createSample(Feature feature, int folderId, boolean reuseFeatureAudit) throws StorageAccessException {
		Sample sample = sampleDAO.createNewSample();
		Audit audit = null;
		if (reuseFeatureAudit)
			audit = feature.getAudit();
		else {
			audit = sampleDAO.createNewAudit();
			audit.setFolder(folderDAO.getFolder(folderId));
			audit.setStatus(FREDConstants.WORKING);
		}
		sample.setAudit(audit);
		sample.setFeature(feature);
		return sample;
	}

	/**
	 * Copies the given SedimentaryFeature but assigns the new one to the 
	 * given sample instead of the original
	 * @throws StorageAccessException 
	 */
	public SedimentaryFeature copyFor(SedimentaryFeature sedFeature, Sample sample) throws StorageAccessException {
		SedimentaryFeature feature = sampleDAO.createNewSedimentaryFeature();
		feature.setAbundant(sedFeature.getAbundant());
		feature.setSedimentaryFeatureType(sedFeature.getSedimentaryFeatureType());
		feature.setSample(sample);
		return feature;
	}

	public List<? extends Relationship> getRelationships(Sample sample, String relationTypeName, String relationshipTypeName) throws StorageAccessException {
		RelationType relationType = sampleDAO.getRelationType(relationTypeName);
		return getRelationships(sample, relationType, relationshipTypeName);
	}
	
	public List<? extends Relationship> getRelationships(Sample sample, RelationType relationType, String relationshipTypeName) throws StorageAccessException {
		RelationshipType relationshipType = sampleDAO.getRelationshipType(relationType, relationshipTypeName);
		
		return sampleDAO.getRelationships(sample, relationshipType);
	}

	public List<? extends Relationship>  getRelationships(Sample sample, String relationTypeName, String[] relationshipTypes) throws StorageAccessException {
		List<Relationship> relationships = new Vector<Relationship>();
		RelationType relationType = sampleDAO.getRelationType(relationTypeName);
		
		for (String typeName : relationshipTypes) {
			relationships.addAll(getRelationships(sample, relationType, typeName));
		}
		
		return relationships;
	}

	public static String getRelationshipDescription(Relationship rel) {
		StringBuffer desc = new StringBuffer();
		if (rel.getDistanceMod() != null)
			desc.append(rel.getDistanceMod()).append(" ");
		desc.append(rel.getDistance()).append(" ");
		if (rel.getDistanceRange() != null)
			desc.append("- ").append(rel.getDistanceRange()).append(" ");
		desc.append(rel.getRelationshipType().getName()).append(" ");
		FrNumber frNum = FeatureUtil.getFrNumber(rel.getFeature());
		if (frNum == null)
			desc.append(rel.getFeature().getFeatureName());
		else
			desc.append(frNum.getFrNumber());
		return desc.toString();
	}

	public void save(Audit audit) throws StorageAccessException {
		sampleDAO.save(audit);
	}

	public void save(Sample sample) throws StorageAccessException {
		sampleDAO.save(sample);
	}

	public void update(Sample sample) throws StorageAccessException {
		sampleDAO.update(sample);
	}

	public void update(Audit audit) throws StorageAccessException{
		sampleDAO.update(audit);
	}

	public void delete(Sample sample) throws StorageAccessException {
		sampleDAO.delete(sample);
	}

	public SentTo findOrCreateSentTo(Sample sample, FossilGroup group, Person person, Integer lab, String comments) {
		for (SentTo sentTo : (Set<SentTo>)sample.getSentTos()) {
			//Check group
			if (group == null && sentTo.getFossilGroup() != null)
				continue;
			if (group != null && !group.equals(sentTo.getFossilGroup()))
				continue;
			if (person == null && sentTo.getPerson() != null)
				continue;
			if (person != null && !person.equals(sentTo.getPerson()))
				continue;
			if (lab == null && sentTo.getLabId() != null)
				continue;
			if (lab != null && !lab.equals(sentTo.getLabId()))
				continue;
			if (comments == null && sentTo.getComments() != null)
				continue;
			if (comments != null && !comments.equals(sentTo.getComments()))
				continue;
			//All tests pass - it's a match
			return sentTo;
		}
		SentTo sentTo = sampleDAO.createNewSentTo();
		sentTo.setSample(sample);
		sentTo.setFossilGroup(group);
		sentTo.setPerson(person);
		sentTo.setLabId(lab);
		sentTo.setComments(comments);
		return sentTo;
	}

	/**
	 * Return the fossil group with the given name or null if one doesn't exist
	 * @throws StorageAccessException 
	 */
	public FossilGroup getFossilGroup(String name) throws StorageAccessException {
		return sampleDAO.getFossilGroup(name);
	}

}
