package nz.cri.gns.fred.util;

import java.sql.SQLException;
import java.util.Date;
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
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.SampleDAO;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.de.MandatoryFieldsMissingException;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.Paleontology;
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

public class SampleUtil extends ModelUtil implements FREDConstants, AuditedUtil {

	private static String NOT_DETERMINED_STAGE = "166";
	private static String NO_FOSSILS_STAGE = "167";
	
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
				    	
	    	return 
	    		distance == rel.getDistance()
	    	 && distanceRange == rel.getDistanceRange()
	    	 && FREDUtil.equals(distanceMod, rel.getDistanceMod(), true)
	    	 && FREDUtil.equals(feature, rel.getFeature(), true)
	    	 && FREDUtil.equals(relationshipType, rel.getRelationshipType(), true)
	    	 && FREDUtil.equals(relationType, rel.getRelationType(), true)
	    	 && FREDUtil.equals(sample, rel.getSample(), true)
	    	 && FREDUtil.equals(stratUnit, rel.getStratUnit(), true)
	    	 && FREDUtil.equals(stratUnitId, rel.getStratUnitId(), true);
	    }
		public String toString() {
			return SampleUtil.getRelationshipDescription(this);
		}
	}

	private SampleDAO sampleDAO;
	private FolderDAO folderDAO;

	
	public SampleUtil(DAOFactory factory) {
		super(factory);
		this.sampleDAO = factory.getSampleDAO();
		this.folderDAO = factory.getFolderDAO();
	}	
	
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
		
		if (!hasDepthInformation(sample))
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
	
	/**
	 * Returns the Sample immediately above the given Sample in a drillhole or vertical section
	 */
	public static Sample getSampleAbove(Sample sample) {
		if (!hasDepthInformation(sample))
			return null;
		Vector<Sample> samples = new Vector(FeatureUtil.getSortedSamples(sample.getFeature()));
		if (samples == null || samples.size() == 1)
			return null;
		int sampleIdx = samples.indexOf(sample);
		if (sampleIdx == 0)
			return null;
		Sample aboveSample = samples.elementAt(sampleIdx - 1);
		return (hasDepthInformation(aboveSample) ? aboveSample : null);
	}

	/**
	 * Returns the Sample immediately below the given Sample in a drillhole or vertical section
	 */
	public static Sample getSampleBelow(Sample sample) {
		if (!hasDepthInformation(sample))
			return null;
		Vector<Sample> samples = new Vector(FeatureUtil.getSortedSamples(sample.getFeature()));
		if (samples == null || samples.size() == 1)
			return null;
		int sampleIdx = samples.indexOf(sample);
		if (sampleIdx == samples.size() - 1)
			return null;
		Sample belowSample = samples.elementAt(sampleIdx + 1);
		return (hasDepthInformation(belowSample) ? belowSample : null);
	}
	
	/**
	 * @throws DataInputException 
	 *  
	 */
	public void deleteSample(int sampleId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
		Sample sample = sampleDAO.getSample(sampleId);
		
		if (!isAllowedDeleteSample(sample, folder, user))
			throw new InsufficientPrivelegesException();
		
		//Remove from the feature
		Feature feature = sample.getFeature();
		int sampleCount = feature.getSamples().size();
		
		//throw exception if only one sample (ie stop user deleting all samples. Can remove once database restructured
		if (sampleCount == 1)
			throw new DataInputException("Samples", "Cannot delete the last sample. Please add new one first");
		
		feature.getSamples().remove(sample);
		
		Audit audit = sample.getAudit();
		
		//And then delete it from DB
		sampleDAO.delete(sample);
		
		//try and delete audit record (if can't then probably also used by feature) so just ignore error
		try {
			sampleDAO.delete(audit);
		} catch (Exception e) {}
	}

	public void submitSample(int sampleId, UserFolder folder, UserAccount user) throws DataInputException, InsufficientPrivelegesException, StorageAccessException {
		Sample sample = sampleDAO.getSample(sampleId);
		if (!folder.isAllowedSubmitLocalities() || sample.getAudit().getStatus().equals(WAITING))
			throw new InsufficientPrivelegesException();
		if (sample.getCollectors() == null || sample.getCollectors().size() == 0 || sample.getCollectionDate() == null || sample.getInPlace() == null)
			throw new MandatoryFieldsMissingException();
		
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
	 * Returns true if a user is allowed to view the locality
	 */
	public boolean isAllowedReadSample(UserAccount user, Sample sample) throws StorageAccessException {
		if (user == null)
			return false;
		
		//first check allowed to read feature if not then return false
		Feature feature = sample.getFeature();
		if (!(new FeatureUtil(factory).isAllowedReadFeature(user, feature)))
			return false;
		
		//then check feature type - if outcrop then return as already allowed to view feature
		if (feature.getFeatureType().equals(FREDConstants.OUTCROP))
			return true;
		
		//now check sample
		if (!sample.getAudit().getStatus().equals(FREDConstants.APPROVED)) {
			UserFolder folder = new FolderUtil(factory).getUserFolder(sample.getAudit().getFolder().getFolderId().intValue(), user);
			return (folder != null && folder.isAllowedReadLocalities());
		}
		
		/**
		 * @TODO last stage is to check sample security code assume OK for moment
		 */
		return true;
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
	
	public int getAdoptionRecordCount(Sample sample) {
		int count = 0;
		for (Iterator it = sample.getRecords().iterator(); it.hasNext(); ) {
			count += (((Record)it.next()).getAdoption() != null) ? 1 : 0;
		}
		return count;
	}
	
	public Set<Paleontology> getPaleontologyRecords(Sample sample) {
		Set<Paleontology> palRecords = new HashSet<Paleontology>();
		for (Iterator it = sample.getRecords().iterator(); it.hasNext(); ) {
			Record record = (Record) it.next();
			if (record.getPaleontology() != null)
				palRecords.add(record.getPaleontology());
		}		
		return palRecords;
	}

	public Set<Adoption> getAdoptionRecords(Sample sample) {
		Set<Adoption> adoRecords = new HashSet<Adoption>();
		for (Iterator it = sample.getRecords().iterator(); it.hasNext(); ) {
			Record record = (Record) it.next();
			if (record.getAdoption() != null)
				adoRecords.add(record.getAdoption());
		}		
		return adoRecords;
	}	
	
	/**
	 * Return a new sample initialised with the given information
	 * @param reuseFeatureAudit 
	 * @throws StorageAccessException 
	 */
	public Sample createSample(Feature feature, int folderId, boolean reuseFeatureAudit, UserAccount user) throws StorageAccessException {
		Sample sample = sampleDAO.createNewSample();
		Audit audit = null;
		if (reuseFeatureAudit)
			audit = feature.getAudit();
		else {
			audit = sampleDAO.createNewAudit();
			audit.setFolder(folderDAO.getFolder(folderId));
			audit.setStatus(FREDConstants.WORKING);
			audit.setCreatedDate(new Date());
			audit.setCreatedById(new Integer(user.getId()));
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

	public List<? extends Relationship> getRelationships(Sample sample, String relationTypeName, String[] relationshipTypes) throws StorageAccessException {
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
		if (rel.getDistance() != null) {
			desc.append(rel.getDistance()).append(" m ");
			if (rel.getDistanceRange() != null)
				desc.append("- ").append(rel.getDistanceRange()).append(" m ");
		}
		if (!rel.getRelationshipType().getName().equals(FREDConstants.NEARBY))
			desc.append(rel.getRelationshipType().getName()).append(" ");
		if (rel.getRelationType().getName().equals(FREDConstants.SAMPLE))
			desc.append(FeatureUtil.getFeatureIdentifyingName(rel.getFeature()));
		else
			desc.append(rel.getStratUnit());
		return desc.toString();
	}

	public static String getRelationshipDescriptionWithLink(Relationship rel, String path, String target) {
		if (!rel.getRelationType().getName().equals(FREDConstants.SAMPLE))
			return getRelationshipDescription(rel);
		
		StringBuffer desc = new StringBuffer();
		desc.append("<a href=\"").append(path).append(rel.getFeature().getFeatureId()).append("\"");
		if (target != null)
			desc.append(" target=\"").append(target).append("\"");
		desc.append(">");
		desc.append(getRelationshipDescription(rel));
		desc.append("</a>");
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
		String name = getCommonRelationshipPropertiesFromDescription(desc, relationship, sampleDAO.getRelationType("Stratigraphic"));
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
		
		//skip "m" if present
		if (parts[where].equals("m"))
			where++;
		
		if (parts[where].equals("-")) try {
			rel.setDistanceRange(new Double(parts[++where]));
			++where;
			//skip "m" if present
			if (parts[where].equals("m"))
				where++;
		} catch (Exception e) {
			throw new IllegalArgumentException("Relationship description not properly formatted");
		}
		
		//Allow one or two word relationships
		RelationshipType relType = sampleDAO.getRelationshipType(relationType, parts[where++]);
		if (relType == null) {
			relType = sampleDAO.getRelationshipType(relationType, parts[where-1] + " " + parts[where++]);
			if (relType == null)
				throw new IllegalArgumentException("Relationship description has invalid relationship type: ('" + parts[where-2] + "' nor '" + parts[where-2] + " " + parts[where-1] + "')");
		}
		rel.setRelationshipType(relType);
		rel.setRelationType(relationType);
		
		return FREDUtil.join(parts, where);
		
	}

	/**
	 * Returns string representing a single SentTo
	 */
	public static String getSentToDescription(SentTo sentTo) {
		StringBuffer desc = new StringBuffer();
		desc.append(sentTo.getFossilGroup().getName()).append(": ");
		if (sentTo.getPerson() != null) {
			desc.append(sentTo.getPerson().getDisplayName());
			if (sentTo.getLabId() != null)
				desc.append("/");
		}
		if (sentTo.getLabId() != null) {
			try {
				desc.append(FREDUtil.getLabName(sentTo.getLabId()));
			} catch (Exception e) {}
		}
		if (sentTo.getComments() != null)
			desc.append(" (").append(sentTo.getComments()).append(")");
		return desc.toString();
	}
	
	public static String getGrainSizeDescription(Sample sample) {
		StringBuffer desc = new StringBuffer();
		if (sample.getPrimaryGrainSize() != null) {
			desc.append(sample.getPrimaryGrainSize().getName()).append(" (pri)");
			if (sample.getSecondaryGrainSize() != null)
				desc.append(", ");
		}
		if (sample.getSecondaryGrainSize() != null)
			desc.append(sample.getSecondaryGrainSize().getName()).append(" (sec)");
		if (sample.getComparatorUsed() != null) {
			if (sample.getComparatorUsed().equals("Y"))
				desc.append(" (Comparator used)");
			else
				desc.append(" (Comparator not used)");
		}
		return desc.toString();
	}

	public static String getBeddingDescription(Sample sample) {
		StringBuffer desc = new StringBuffer();
		if (sample.getPrimaryBedding() != null) {
			desc.append(sample.getPrimaryBedding().getName());
			if (sample.getSecondaryBedding() != null)
				desc.append(", ");
		}
		if (sample.getSecondaryBedding() != null)
			desc.append(sample.getSecondaryBedding().getName());
		return desc.toString();
	}

	public static String getColourDescription(Sample sample) {
		StringBuffer desc = new StringBuffer();
		if (sample.getColourModifier() != null)
			desc.append(sample.getColourModifier().getName()).append(" ");
		if (sample.getPrimaryColour() != null) {
			desc.append(sample.getPrimaryColour().getName());
			if (sample.getSecondaryColour() != null)
				desc.append("-");
		}
		if (sample.getSecondaryColour() != null)
			desc.append(sample.getSecondaryColour().getName());
		if (sample.getWet() != null)
			desc.append(" (").append(sample.getWet()).append(")");
		return desc.toString();
	}
	
	public static String getSedFeatureDescription(SedimentaryFeature sedFeat) {
		StringBuffer desc = new StringBuffer();
		desc.append(sedFeat.getSedimentaryFeatureType().getName());
		if (sedFeat.getAbundant() != null && sedFeat.getAbundant().equals("Y"))
			desc.append(" (abundant)");
		return desc.toString();
	}
	
	public Audit save(Audit audit) throws StorageAccessException {
		return sampleDAO.save(audit);
	}

	public void save(Sample sample) throws StorageAccessException {
		sampleDAO.save(sample);
	}

	public void update(Sample sample) throws StorageAccessException {
		sampleDAO.update(sample);
	}

	public Audit update(Audit audit) throws StorageAccessException{
		return sampleDAO.update(audit);
	}

	public void delete(Sample sample) throws StorageAccessException {
		sampleDAO.delete(sample);
	}

	public SentTo findOrCreateSentTo(Sample sample, FossilGroup group, Person person, Integer lab, String comments) throws StorageAccessException {
		if (sample.getSentTos() != null) {
			for (SentTo sentTo : sample.getSentTos()) {
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
		}
		SentTo sentTo = sampleDAO.createNewSentTo();
		sentTo.setSample(sample);
		sentTo.setFossilGroup(group);
		sentTo.setPerson(person);
		sentTo.setLabId(lab);
		sentTo.setComments(comments);
		sampleDAO.save(sentTo);
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
		if (startStageId == null)
			throw new IllegalArgumentException("Start age is null");		
		
		//check start/stop ages if both entered unless "not determined" or "no fossils"
		if (stopStageId != null
				&& !(startStageId.equals(NOT_DETERMINED_STAGE)
				|| startStageId.equals(NO_FOSSILS_STAGE)
				|| stopStageId.equals(NOT_DETERMINED_STAGE)
				|| stopStageId.equals(NO_FOSSILS_STAGE))) {
			double[] startRange = null, stopRange = null;
			startRange = FREDUtil.getStageAgeRange(startStageId);
			stopRange = FREDUtil.getStageAgeRange(stopStageId);
			if (startRange != null && stopRange != null) {
				if (startRange[0] < stopRange[0] || startRange[1] < stopRange[1])
					throw new IllegalArgumentException("Stop age is older than start age");
			} else {
				throw new IllegalArgumentException("Invalid stage(s)");
			}
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

		if (stage == null)
			return (startId != null || stopId != null);
		
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

	public DrillType getDrillType(Integer id) throws StorageAccessException {
		return sampleDAO.getDrillType(id);
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
		SedimentaryFeatureType type = sampleDAO.getSedimentaryFeatureTypeWithName(sedFeature);
		if (type == null)
			throw new IllegalArgumentException("Invalid sedimentary feature type: " + sedFeature);
		
		feature.setSedimentaryFeatureType(type);
		return feature;
	}

    public void saveOrUpdate(Sample sample) throws StorageAccessException {
    	if (sample.getAudit() == null) {
    		throw new IllegalStateException("Cannot save a sample without an audit");
    	}
        sampleDAO.saveOrUpdate(sample.getAudit());
        sampleDAO.saveOrUpdate(sample);
    }

    /**
     * Ensures the given sample is attached to the current persistence mechanism
     * @param sample
     * @throws StorageAccessException 
     */
    public void attach(Sample sample) throws StorageAccessException {
        sampleDAO.attach(sample);
    }

    /**
     * Ensures the given sample is attached to the current persistence mechanism
     * @param sample
     * @throws StorageAccessException 
     * @throws StorageAccessException 
     */
    public void attach(Audit audit) throws StorageAccessException {
        sampleDAO.attach(audit);
    }

}
