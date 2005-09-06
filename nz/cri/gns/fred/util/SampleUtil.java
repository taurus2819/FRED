package nz.cri.gns.fred.util;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.SampleDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RelationType;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.RelationshipType;
import nz.cri.gns.fred.model.RockColour;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SedimentaryFeatureType;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.model.Weathering;

/**
 *
 */
public class SampleUtil extends ModelUtil implements FREDConstants {

	/**
	 * An implementation of relationship that does a thorough (field by field)
	 * comparison when checking equality, <b>but</b> ignores the id field
	 */
	public class NoIdRelationship implements Relationship {

		private RelationType relationType;
		private Integer stratUnitId;
		private String stratUnit;
		private Double distance;
		private String distanceMod;
		private Double distanceRange;
		private Sample sample;
		private Feature feature;
		private RelationshipType relationshipType;
		
		/** Throws IllegalStateException...always */
		public Integer getRelationshipId() { 
			throw new IllegalStateException("'NoIdRelationship'....get it?"); 
		}
		/** Does nothing */
		public void setRelationshipId(Integer relationshipId) {
		}
		public RelationType getRelationType() {	
			return relationType; 
		}
		public void setRelationType(RelationType relationType) { 
			this.relationType = relationType; 
		}
		public Integer getStratUnitId() { 
			return stratUnitId; 
		}
		public void setStratUnitId(Integer stratUnitId) { 
			this.stratUnitId = stratUnitId; 
		}
		public Double getDistance() {
			return distance;
		}
		public void setDistance(Double distance) {
			this.distance = distance;
		}
		public String getDistanceMod() {
			return distanceMod;
		}
		public void setDistanceMod(String distanceMod) {
			this.distanceMod = distanceMod;
		}
		public Double getDistanceRange() {
			return distanceRange;
		}
		public void setDistanceRange(Double distanceRange) {
			this.distanceRange = distanceRange;
		}
		public Feature getFeature() {
			return feature;
		}
		public void setFeature(Feature feature) {
			this.feature = feature;
		}
		public RelationshipType getRelationshipType() {
			return relationshipType;
		}
		public void setRelationshipType(RelationshipType relationshipType) {
			this.relationshipType = relationshipType;
		}
		public Sample getSample() {
			return sample;
		}
		public void setSample(Sample sample) {
			this.sample = sample;
		}
		public String getStratUnit() {
			return stratUnit;
		}
		public void setStratUnit(String stratUnit) {
			this.stratUnit = stratUnit;
		}
		public boolean equals(Object o) {
			if (!(o instanceof Relationship))
				return false;
			
			Relationship rel = (Relationship)o;
			
			//Compulsory fields
			if (!sample.equals(rel.getSample()) || !relationType.equals(rel.getRelationType()) || !relationshipType.equals(rel.getRelationshipType()))
				return false;
			
			//Optional fields
			if (distance == null ^ rel.getDistance() == null || (distance != null && !distance.equals(rel.getDistance())))
				return false;
			if (distanceMod == null ^ rel.getDistanceMod() == null || (distanceMod != null && !distanceMod.equals(rel.getDistanceMod())))
				return false;
			if (distanceRange == null ^ rel.getDistanceRange() == null || (distanceRange != null && !distanceRange.equals(rel.getDistanceRange())))
				return false;
			if (feature == null ^ rel.getFeature() == null || (feature != null && !feature.equals(rel.getFeature())))
				return false;
			if (stratUnit == null ^ rel.getStratUnit() == null || (stratUnit != null && !stratUnit.equals(rel.getStratUnit())))
				return false;
			if (stratUnitId == null ^ rel.getStratUnitId() == null || (stratUnitId != null && !stratUnitId.equals(rel.getStratUnitId())))
				return false;
			return true;
		}
	}

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
		if (rel.getRelationType().getName().equals(FREDConstants.SAMPLE))
			desc.append(FeatureUtil.getFeatureIdentifyingName(rel.getFeature()));
		else
			desc.append(rel.getStratUnit());
		return desc.toString();
	}


	public Relationship decodeSampleRelationshipDescription(String desc) throws StorageAccessException {
		NoIdRelationship relationship = new NoIdRelationship();
		String name = getCommonRelationshipPropertiesFromDescription(desc, relationship, sampleDAO.getRelationType("Sample"));
		relationship.setFeature(new FeatureUtil(factory).getFeatureWithIdentifyingName(name));
		return relationship;
	}

	public Relationship decodeStratigraphicRelationshipDescription(String desc) throws StorageAccessException {
		NoIdRelationship relationship = new NoIdRelationship();
		String name = getCommonRelationshipPropertiesFromDescription(desc, relationship, sampleDAO.getRelationType("Sample"));
		//Set the unit by name
		relationship.setStratUnit(name);
		try {
			relationship.setStratUnitId(FREDUtil.getStratLexIdFor(name));
		} catch (Exception e) {
			throw new StorageAccessException(e);
		}
		return relationship;
	}
	
	/**
	 * Parses out attributes common to both sample and strat relationships
	 * @throws StorageAccessException 
	 */
	private String getCommonRelationshipPropertiesFromDescription(String desc, Relationship rel, RelationType relationType) throws StorageAccessException {
		desc = desc.trim();
		String[] parts = desc.split("\\s");
		int where = 0;
		try {
			rel.setDistance(new Double(parts[where++])); 
		} catch (Exception e) {
			//This means that there is a modifier in the way
			try {
				rel.setDistance(new Double(parts[where++]));
				rel.setDistanceMod(parts[0]);
			} catch (Exception _e) {
				throw new IllegalArgumentException("Relationship description not properly formatted");
			}
		}
		if (parts[where].equals("-")) try {
			rel.setDistanceRange(new Double(++where));
			++where;
		} catch (Exception e) {
			throw new IllegalArgumentException("Relationship description not properly formatted");
		}
		
		//Allow one or two word relationships
		RelationshipType relType = sampleDAO.getRelationshipType(relationType, parts[where++]);
		if (relType == null) {
			relType = sampleDAO.getRelationshipType(sampleDAO.getRelationType("Sample"), parts[where-1] + " " + parts[where++]);
			if (relType == null)
				throw new IllegalArgumentException("Relationship description has invalid relationship type: ('" + parts[where-2] + "' nor '" + parts[where-2] + " " + parts[where-1] + "')");
		}
		rel.setRelationshipType(relType);
		rel.setRelationType(relationType);
		
		return FREDUtil.join(parts, where);
		
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

	public Stage getStage(String startStageId, boolean startUncertain, String stopStageId, boolean stopUncertain) throws StorageAccessException, NamingException, SQLException {
		double[] startRange = null, stopRange = null;
		if (startStageId != null) {
			startRange = FREDUtil.getStageAgeRange(startStageId);
		}
		if (stopStageId != null) {
			stopRange = FREDUtil.getStageAgeRange(stopStageId);
		}
		
		if (startRange != null && stopRange != null) {
			if (startRange[0] < stopRange[0] || startRange[1] < stopRange[1])
				throw new IllegalArgumentException("Stop age is older than start age");
		}
		
		Stage stage = sampleDAO.findStage(startStageId, startUncertain, stopStageId, stopUncertain);
		if (stage == null) {
			stage = sampleDAO.createNewStage();
			stage.setStageLowerId((startStageId == null) ? null : new Integer(startStageId));
			stage.setStageLowerMod((startUncertain) ? "?" : null);
			stage.setStageUpperId((stopStageId == null) ? null : new Integer(stopStageId));
			stage.setStageUpperMod((stopUncertain) ? "?" : null);
			sampleDAO.save(stage);
		}
		return stage;
	}

	/**
	 * Returns true if the given stage differs from that described by the arguments
	 */
	public boolean stageDiffers(Stage stage, String startId, boolean startUncertain, String stopId, boolean stopUncertain) {
		if (stage == null) {
			return (startId != null || stopId != null);
		}
		
		if (stage.getStageLowerId() == null ^ startId == null)
			return true;
		
		if (stage.getStageUpperId() == null ^ stopId == null)
			return true;
		
		if (startId != null && !new Integer(startId).equals(stage.getStageLowerId()))
			return true;
		
		if (stopId != null && !new Integer(stopId).equals(stage.getStageUpperId()))
			return true;
		
		//If we're still here then all the stages are the same - check uncertainties
		if (startUncertain ^ "?".equals(stage.getStageLowerMod()))
			return true;
			
		return stopUncertain ^ "?".equals(stage.getStageUpperMod());
	}

	/**
	 * Tests for a match between the given relationship and the other arguments.  Ignores the fields of 
	 * relationship that are not passed as arguments
	 */
	public boolean isMatchingRelationship(Relationship rel, Feature feature, String relationType, String relationshipType) {
		return rel.getFeature().equals(feature) 
			&& rel.getRelationType().getName().equals(relationType)
			&& rel.getRelationshipType().getName().equals(relationshipType);
	}

	/**
	 * Creates a relationship with the given fields
	 * @throws StorageAccessException 
	 */
	public Relationship createRelationship(Sample sample, Feature feature, String relationType, String relationshipType) throws StorageAccessException {
		Relationship rel = sampleDAO.createNewRelationship();
		rel.setSample(sample);
		rel.setFeature(feature);
		rel.setRelationType(sampleDAO.getRelationType(relationType));
		rel.setRelationshipType(sampleDAO.getRelationshipType(rel.getRelationType(), relationshipType));
		sampleDAO.save(rel);
		return rel;
	}

	/**
	 * Creates a new relationship object which is a copy of the given one, but valid within the access
	 * layers world
	 * @throws StorageAccessException 
	 */
	public Relationship cloneRelationship(Relationship newRelationship) throws StorageAccessException {
		Relationship rel = sampleDAO.createNewRelationship();
		rel.setSample(newRelationship.getSample());
		rel.setFeature(newRelationship.getFeature());
		rel.setDistance(newRelationship.getDistance());
		rel.setDistanceMod(newRelationship.getDistanceMod());
		rel.setDistanceRange(newRelationship.getDistanceRange());
		rel.setRelationshipType(newRelationship.getRelationshipType());
		rel.setRelationType(newRelationship.getRelationType());
		rel.setSample(newRelationship.getSample());
		rel.setStratUnit(newRelationship.getStratUnit());
		rel.setStratUnitId(newRelationship.getStratUnitId());
		sampleDAO.save(rel);
		return rel;
	}

	public GrainSize getGrainSize(Integer id) throws StorageAccessException {
		return sampleDAO.getGrainSize(id);
	}

	public boolean isPreviousSampleRelationship(Relationship rel) {
		return rel.getRelationType().getName().equals(FREDConstants.SAMPLE)
			&& rel.getRelationshipType().getName().equals(FREDConstants.NEARBY);
	}

	public boolean isStratigraphicRelationship(Relationship rel) {
		return rel.getRelationType().getName().equals(FREDConstants.STRATIGRAPHIC);
	}

	public Hardness getHardness(Integer id) throws StorageAccessException {
		return sampleDAO.getHardness(id);
	}

	public Weathering getWeathering(Integer id) throws StorageAccessException {
		return sampleDAO.getWeathering(id);
	}

	public Bedding getBedding(Integer id) throws StorageAccessException {
		return sampleDAO.getBedding(id);
	}

	public BedThickness getBeddingThickness(Integer id) throws StorageAccessException {
		return sampleDAO.getBeddingThickness(id);
	}

	public RockColour getRockColour(Integer id) throws StorageAccessException {
		return sampleDAO.getRockColour(id);
	}

	public ColourModifier getColourModifier(Integer id) throws StorageAccessException {
		return sampleDAO.getColourModifier(id);
	}

	public Carbonate getCarbonate(Integer id) throws StorageAccessException {
		return sampleDAO.getCarbonate(id);
	}

	public SedimentaryFeature createSedimentaryFeature(Sample sample, String sedFeature, boolean isAbundant) throws StorageAccessException {
		SedimentaryFeature feature = sampleDAO.createNewSedimentaryFeature();
		feature.setAbundant((isAbundant) ? "Y" : null);
		feature.setSample(sample);
		SedimentaryFeatureType type = sampleDAO.getSedimentaryFeatureTypeWithName(sedFeature);
		if (type == null)
			throw new IllegalArgumentException("Invalid sedimentary feature type: " + sedFeature);
		
		feature.setSedimentaryFeatureType(type);
		return feature;
	}

}
