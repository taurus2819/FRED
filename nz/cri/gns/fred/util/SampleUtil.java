package nz.cri.gns.fred.util;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import nz.cri.gns.auth.InsufficientPrivelegesException;
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
import nz.cri.gns.fred.model.Lab;
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
import nz.cri.gns.fred.model.StratigraphicUnit;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.model.Weathering;

public class SampleUtil extends ModelUtil implements FREDConstants, AuditedUtil {
	
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

	 /** @param sample
	 * @return
	 */
	public static String getDrillHoleDepthDescription(Sample sample) {
		Feature feature = sample.getFeature();
		
		//Not relevant for outcrops
		if (feature.getFeatureType().equals(OUTCROP))
			return null;
		
		if (!hasDepthInformation(sample))
			return DEPTH_NOT_SPECIFIED;
		
		String unit = sample.getDepthUnit();
		
		StringBuffer desc = new StringBuffer();
		
		if (sample.getTopDepth() != null)
			desc.append(FREDUtil.formatDoubleForOutput(sample.getTopDepth(), 3)).append(" ").append(unit);
		if (sample.getBottomDepth() != null) {
			desc.append(" - ").append(FREDUtil.formatDoubleForOutput(sample.getBottomDepth(), 3)).append(" ").append(unit);
		}
		
		if (FEET_UNIT.equals(unit)) {
			desc.append(" (");
			if (sample.getTopDepth() != null)
				desc.append(FREDUtil.formatDoubleForOutput(new Double(sample.getTopDepth().doubleValue() * FT_TO_M), 3)).append(" m");
			if (sample.getBottomDepth() != null) {
				desc.append(" - ").append(FREDUtil.formatDoubleForOutput(new Double(sample.getBottomDepth().doubleValue() * FT_TO_M), 3)).append(" m");
			}
			desc.append(")");
		}
		
		if (sample.getDrillType() != null) {
			desc.append(" ").append(sample.getDrillType().getName());
		}
		
		return desc.toString();
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
		Vector<Sample> samples = new Vector<Sample>(FeatureUtil.getSortedSamples(sample.getFeature()));
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
		Vector<Sample> samples = new Vector<Sample>(FeatureUtil.getSortedSamples(sample.getFeature()));
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
		submitSample(getSample(sampleId), folder, user);
	}
	
	public void submitSample(Sample sample, UserFolder folder, UserAccount user) throws DataInputException, InsufficientPrivelegesException, StorageAccessException {
		//Update the audit log, so long as this isn't an outcrop
		if (!sample.getFeature().getFeatureType().equals(OUTCROP)) {
			if (!isAllowedSubmitSample(user, sample, folder))
				throw new InsufficientPrivelegesException();
			if (!isMandatoryFieldComplete(sample))
				throw new MandatoryFieldsMissingException();
			
			Audit audit = sample.getAudit();
			audit.setStatus(APPROVED);		//Samples don't need approval
			audit.setSubmittedById(new Integer(user.getId()));
			audit.setSubmittedDate(new Date());
			audit.setWorkingComments(null);
			audit.setFolder(null);
			if (audit.getConfidentialFlag()) {
				GregorianCalendar cal = new GregorianCalendar();
				if (audit.getConfidPeriod().doubleValue() == 0.5)
					cal.add(Calendar.MONTH, 6);
				else
					cal.add(Calendar.YEAR, audit.getConfidPeriod().intValue());
				audit.setConfidLapseDate(cal.getTime());
			}
			sampleDAO.update(audit);
		}
	}
	
	public static boolean isMandatoryFieldComplete(Sample sample) {
		if (FeatureUtil.isBacklogFeature(sample.getFeature()))
			return true;
		if (FREDUtil.isEmpty(sample.getCollectors())
				|| sample.getCollectionDate() == null || sample.getInPlace() == null
				|| (FREDUtil.isEmpty(sample.getSentTos()) && FREDUtil.isEmpty(sample.getNotCollected())))
			return false;
		return true;
	}
	
	public boolean isSampleConfidential(Sample sample) {
		if (sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP))
			return false;
		return sample.getAudit().getConfidentialFlag().booleanValue();
	}
	
	/**
	 * Returns true if a user is allowed to view the locality
	 */
	public boolean isAllowedReadSample(UserAccount user, Sample sample) throws StorageAccessException {
		if (user == null)
			return false;
		
		//check feature type - if not outcrop then check sample
		if (!sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
			if (sample.getAudit().getStatus().equals(FREDConstants.APPROVED)) {
				if(!new AuditUtil(factory).isAllowedReadApproved(sample.getAudit(), user))
					return false;
			} else {
				UserFolder folder = new FolderUtil(factory).getUserFolder(sample.getAudit().getFolder().getFolderId().intValue(), user);
				if (folder == null || !folder.isAllowedReadLocalities())
					return false;
			}
		}
		
		//then check allowed to read feature
		return new FeatureUtil(factory).isAllowedReadFeature(user, sample.getFeature());
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
	
	public boolean isAllowedEditSample(UserAccount user, Sample sample, UserFolder userFolder) throws StorageAccessException {
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_EDIT_RIGHT, folderDAO) ||
				FREDUtil.checkEditSecurityClass(user);
		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_EDIT_RIGHT, folderDAO);

		return userFolder.isAllowedEditLocalities();
	}
	
	public boolean isAllowedDeleteSample(UserAccount user, Sample sample, UserFolder userFolder) throws StorageAccessException {
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;
		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_DELETE_RIGHT, folderDAO);

		return userFolder.isAllowedDeleteLocalities();
	}
	
	public boolean isAllowedSubmitSample(UserAccount user, Sample sample, UserFolder userFolder) throws NumberFormatException, StorageAccessException {
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
	
	public List<Paleontology> getPaleontologyRecords(Sample sample) {
		List<Paleontology> palRecords = new Vector<Paleontology>();
		for (Record record : sample.getRecords()) {
			if (record.getPaleontology() != null)
				palRecords.add(record.getPaleontology());
		}
		Collections.sort(palRecords);
		return palRecords;
	}

	public List<Adoption> getAdoptionRecords(Sample sample) {
		List<Adoption> adoRecords = new Vector<Adoption>();
		for (Iterator it = sample.getRecords().iterator(); it.hasNext(); ) {
			Record record = (Record) it.next();
			if (record.getAdoption() != null)
				adoRecords.add(record.getAdoption());
		}
		Collections.sort(adoRecords);
		return adoRecords;
	}	
	
	/**
	 * Return a new sample initialised with the given information
	 * @param reuseFeatureAudit 
	 * @throws StorageAccessException 
	 */
	public Sample createSample(Feature feature, int folderId, boolean reuseFeatureAudit, UserAccount user) throws StorageAccessException {
		Sample sample = sampleDAO.createNewSample(feature);
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
			desc.append(FREDUtil.formatDoubleForOutput(rel.getDistance(), 2)).append(" m ");
			if (rel.getDistanceRange() != null)
				desc.append("- ").append(FREDUtil.formatDoubleForOutput(rel.getDistanceRange(), 2)).append(" m ");
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
		StratigraphicUnit stratUnit = findStratigraphicUnit(name);
		relationship.setStratUnitId((stratUnit != null) ? stratUnit.getId() : null);
		return relationship;
	}
	
	/**
	 * Parses out attributes common to both sample and strat relationships
	 * @throws StorageAccessException 
	 */
	private String getCommonRelationshipPropertiesFromDescription(String desc, Relationship rel, RelationType relationType) throws StorageAccessException {
		//assume no distance data first
		
		desc = desc.trim();
		String[] parts = desc.split("\\s");
		int where = 0;
		
		try {
			rel.setDistance(new Double(parts[where]));
			where++;
		} catch (Exception e) {
			//This means that there is a modifier in the way
			try {
				rel.setDistance(new Double(parts[where + 1]));
				rel.setDistanceMod(parts[where]);
				where++;
				where++;
			} catch (Exception _e) {
				//don't throw exception as distance bits may be NULL
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
				throw new IllegalArgumentException("Relationship description is invalid");
		}
		rel.setRelationshipType(relType);
		rel.setRelationType(relationType);
		
		return FREDUtil.join(parts, where);
		
	}
		
	public static String getDipStrikeDescription(Sample sample) {
		StringBuffer desc = new StringBuffer();
		if (sample.getDip() != null)
			desc.append(sample.getDip()).append((char)176);
		if (sample.getDipDirection() != null)
			desc.append(sample.getDipDirection());
		if (sample.getStrike() != null) {
			if (desc.length() > 0)
				desc.append("/");
			String strikeStr = String.valueOf(sample.getStrike());
			while (strikeStr.length() < 3)
				strikeStr = "0" + strikeStr;
			desc.append(strikeStr).append((char)176);
		}
		if (sample.getFacing() != null)
			desc.append(" (Facing: ").append(sample.getFacing()).append(")");
		return desc.toString();
	}
	
	/**
	 * Returns string representing a single SentTo
	 */
	public static String getSentToDescription(SentTo sentTo) {
		StringBuffer desc = new StringBuffer();
		if (sentTo.getFossilGroup() != null)
			desc.append("(").append(sentTo.getFossilGroup().getName()).append(") ");
		if (sentTo.getPerson() != null) {
			desc.append(sentTo.getPerson().getDisplayName());
			if (sentTo.getLab() != null)
				desc.append("/");
		}
		if (sentTo.getLab() != null) {
			try {
				desc.append(sentTo.getLab().getName());
			} catch (Exception e) {}
		}
		if (sentTo.getComments() != null)
			desc.append(": ").append(sentTo.getComments());
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

	public SentTo findOrCreateSentTo(Sample sample, FossilGroup group, Person person, Lab lab, String comments) throws StorageAccessException {
		if (sample.getSentTos() != null) {
			for (SentTo sentTo : sample.getSentTos()) {
				//Check group
				if (group == null && sentTo.getFossilGroup() != null) {
					continue;
				}
				if (group != null && !group.equals(sentTo.getFossilGroup())) {
					continue;
				}
				if (person == null && sentTo.getPerson() != null) {
					continue;
				}
				if (person != null && !person.equals(sentTo.getPerson())) {
					continue;
				}
				if (lab == null && sentTo.getLab() != null) {
					continue;
				}
				if (lab != null && !lab.equals(sentTo.getLab())) {
					continue;
				}
				if (comments == null && sentTo.getComments() != null) {
					continue;
				}
				if (comments != null && !comments.equals(sentTo.getComments())) {
					continue;
				}
				//All tests pass - it's a match
				return sentTo;
			}
		}
		SentTo sentTo = sampleDAO.createNewSentTo();
		sentTo.setSample(sample);
		sentTo.setFossilGroup(group);
		sentTo.setPerson(person);
		sentTo.setLab(lab);
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

    public Lab findLab(String labName) throws StorageAccessException {
    	return sampleDAO.findLab(labName);
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

	public boolean isMatchingRelationship(Relationship rel1, Relationship rel2) throws StorageAccessException {
		if (!FREDUtil.equals(rel1.getRelationType(), rel2.getRelationType(), true))
			return false;
		if (!FREDUtil.equals(rel1.getRelationshipType(), rel2.getRelationshipType(), true))
			return false;
		if (!FREDUtil.equals(rel1.getFeature(), rel2.getFeature(), true))
			return false;
		if (!FREDUtil.equals(rel1.getStratUnit(), rel2.getStratUnit(), true))
			return false;
		if (!FREDUtil.equals(rel1.getDistanceMod(), rel2.getDistanceMod(), true))
			return false;
		if (!FREDUtil.equals(rel1.getDistance(), rel2.getDistance(), true))
			return false;
		if (!FREDUtil.equals(rel1.getDistanceRange(), rel2.getDistanceRange(), true))
			return false;
		return true;
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

	public boolean isPreviousSampleRelationship(Relationship rel) {
		return rel.getRelationType().getName().equals(FREDConstants.SAMPLE)
			&& rel.getRelationshipType().getName().equals(FREDConstants.NEARBY);
	}
	
	public GrainSize getGrainSize(Integer id) throws StorageAccessException {
		return sampleDAO.getGrainSize(id);
	}
	
	public List<GrainSize> getGrainSizes() throws StorageAccessException {
		return sampleDAO.getList("FROM GrainSize AS a", GrainSize.class);
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
	
	public List<DrillType> getDrillTypes() throws StorageAccessException {
		return sampleDAO.getList("FROM DrillType AS t", DrillType.class);
	}
	
	public List<SedimentaryFeatureType> getSedimentaryFeatureTypes() throws StorageAccessException {
		return sampleDAO.getList("FROM SedimentaryFeatureType AS s", SedimentaryFeatureType.class);
	}
	
	public List<FossilGroup> getFossilGroups() throws StorageAccessException {
		return sampleDAO.getList("FROM FossilGroup AS f", FossilGroup.class);
	}
	
	public List<Lab> getLabs() throws StorageAccessException {
		return sampleDAO.getList("FROM Lab AS l", Lab.class);
	}
	
	public List<StratigraphicUnit> getStratigraphicUnits() throws StorageAccessException {
		return sampleDAO.getList("FROM StratigraphicUnit AS s", StratigraphicUnit.class);
	}
	
	public StratigraphicUnit findStratigraphicUnit(String name) throws StorageAccessException {
		return sampleDAO.findStratigraphicUnit(name);
	}
	
	public List<RelationshipType> getRelationshipTypes(String relationType) throws StorageAccessException {
		RelationType relType = sampleDAO.getRelationType(relationType);
		return sampleDAO.getList("FROM RelationshipType AS r WHERE r.relationType = ?", RelationshipType.class, relType);
	}
	
	public RelationshipType findRelationshipType(String name) throws StorageAccessException {
		return sampleDAO.findRelationshipType(name);
	}
	
	public SedimentaryFeature createSedimentaryFeature(String sedFeature, boolean isAbundant) throws StorageAccessException {
		SedimentaryFeature feature = sampleDAO.createNewSedimentaryFeature();
		feature.setAbundant((isAbundant) ? "Y" : "N");
		if (sedFeature.indexOf(":") >= 0)
			sedFeature = sedFeature.substring(sedFeature.indexOf(":") + 1).trim();
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

	public void delete(SentTo sentTo) throws StorageAccessException {
		sampleDAO.delete(sentTo);
	}
	
	public void delete(Relationship rel)  throws StorageAccessException {
		sampleDAO.delete(rel);
	}

}
