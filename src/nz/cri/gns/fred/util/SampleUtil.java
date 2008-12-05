package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
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
import nz.cri.gns.fred.model.Folder;
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

	private FredDAO fredDAO;

	public SampleUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
	}	

	public Sample findSample(String localityName) throws StorageAccessException {
		FeatureUtil featureUtil = new FeatureUtil(factory);
		if (localityName.indexOf(":") < 0) {
			Feature feature = featureUtil.getFeatureWithIdentifyingName(localityName);
			if (feature != null && feature.getFeatureType().equals(OUTCROP))
				return featureUtil.getOutcropSample(feature);
			return null;
		}
		Feature feature = featureUtil.getFeatureWithIdentifyingName(localityName.substring(0, localityName.indexOf(":")).trim());
		if (feature != null && !feature.getFeatureType().equals(OUTCROP)) {
			String sampleName = localityName.substring(localityName.indexOf(":") + 1).trim();
			for (Sample sample : feature.getSamples()) {
				if (sampleName.equals(getDrillHoleDepthDescription(sample)))
					return sample;
			}
		}
		return null;
	}
	
	public Sample findOrCreateSample(String localityName, UserAccount user) throws StorageAccessException, DataInputException, InsufficientPrivelegesException {
		if (localityName.indexOf(":") < 0)
			return findSample(localityName);
		FeatureUtil featureUtil = new FeatureUtil(factory);
		Feature feature = featureUtil.getFeatureWithIdentifyingName(localityName.substring(0, localityName.indexOf(":")).trim());
		if (feature != null && !feature.getFeatureType().equals(OUTCROP)) {
			String sampleName = localityName.substring(localityName.indexOf(":") + 1).trim();
			for (Sample sample : feature.getSamples()) {
				if (sampleName.equals(getDrillHoleDepthDescription(sample)))
					return sample;
			}
			if (featureUtil.isAllowedReadFeature(user, feature)) {
				Sample sample = createSample(feature, null, false, user);
				setAuditApproved(sample, user);
				Object[] drillDepths = parseDrillHoleDepthDescription(sampleName);
				sample.setTopDepth((Double) drillDepths[0]);
				sample.setBottomDepth((Double) drillDepths[1]);
				sample.setDepthUnit((String) drillDepths[2]);
				sample.setDrillType((DrillType) drillDepths[3]);
				saveOrUpdate(sample);
				return sample;
			}
		}
		return null;		
	}
	
	public static String getDrillHoleDepthDescription(Sample sample) {
		Feature feature = sample.getFeature();
		
		//Not relevant for outcrops
		if (feature.getFeatureType().equals(OUTCROP))
			return null;
		
		if (!hasDepthInformation(sample))
			return DEPTH_NOT_SPECIFIED;
		
		return getDrillHoleDepthDescription(sample.getTopDepth(), sample.getBottomDepth(), sample.getDepthUnit(), sample.getDrillType());
	}
	
	 /** @param sample
	 * @return
	 */
	public static String getDrillHoleDepthDescription(Double topDepth, Double bottomDepth, String unit, DrillType drillType) {
		StringBuffer desc = new StringBuffer();
		
		if (topDepth != null)
			desc.append(FREDUtil.formatDoubleForOutput(topDepth, 3)).append(" ").append(unit);
		if (bottomDepth != null) {
			desc.append(" - ").append(FREDUtil.formatDoubleForOutput(bottomDepth, 3)).append(" ").append(unit);
		}
		
		if (FEET_UNIT.equals(unit)) {
			desc.append(" (");
			if (topDepth != null)
				desc.append(FREDUtil.formatDoubleForOutput(new Double(topDepth * FT_TO_M), 3)).append(" m");
			if (bottomDepth != null) {
				desc.append(" - ").append(FREDUtil.formatDoubleForOutput(new Double(bottomDepth * FT_TO_M), 3)).append(" m");
			}
			desc.append(")");
		}
		
		if (drillType != null) {
			desc.append(" ").append(drillType.getName());
		}
		
		return desc.toString();
	}
	
	public Object[] parseDrillHoleDepthDescription(String desc) throws StorageAccessException, DataInputException {
		try {
			Object[] depths = new Object[4];
			if (desc.indexOf("-") < 0) {
				String[] bits = desc.split(" ");
				depths[0] = new Double(bits[0]);
				depths[1] = null;
				depths[2] = bits[1];
				if (bits.length > 2)
					depths[3] = getDrillType(bits[2]);
				else
					depths[3] = null;
			} else {
				String[] bits = desc.split("-");
				String[] littleBits = bits[0].trim().split(" ");
				depths[0] = new Double(littleBits[0]);
				littleBits = bits[1].trim().split(" ");
				depths[1] = new Double(littleBits[0]);
				depths[2] = littleBits[1];
				if (littleBits.length > 2)
					depths[3] = getDrillType(littleBits[2]);
				else
					depths[3] = null;
			}
			return depths;
		} catch (StorageAccessException e) {
			throw e;
		} catch (Exception e) {
			throw new DataInputException("Drillhole Depth", "Incorrectly formatted");
		}
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
	
	public void deleteSamples(String[] sampIds, UserFolder folder, UserAccount user) {
		boolean errFlag = false;
		for (int i = 0; i < sampIds.length; i++) {
			try {
				deleteSample(getSample(Integer.parseInt(sampIds[i])), folder, user);
			} catch (Exception e) {
				errFlag = true;
			}
		}
		if (errFlag)
			throw new IllegalStateException("An error has occured. Not all localities have been removed/deleted");
	}
	
	public void deleteSample(int sampleId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
		Sample sample = getSample(sampleId);
		deleteSample(sample, folder, user);
	}
	
	public void deleteSample(Sample sample, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
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
		fredDAO.delete(sample);
		
		//try and delete audit record (if can't then probably also used by feature) so just ignore error
		try {
			fredDAO.delete(audit);
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
			
			setAuditApproved(sample, user);
		}
	}
	
	public void submitSamples(String[] sampIds, UserFolder folder, UserAccount user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, DataInputException {
		for (int i = 0; i < sampIds.length; i++) {
			submitSample(getSample(Integer.parseInt(sampIds[i])), folder, user);
		}
	}
	
	private void setAuditApproved(Sample sample, UserAccount user) throws StorageAccessException {
		Audit audit = sample.getAudit();
		audit.setStatus(APPROVED);		//Samples don't need approval
		audit.setSubmittedById(new Integer(user.getId()));
		audit.setSubmittedDate(new Date());
		audit.setWorkingComments(null);
		audit.setFolder(null);
		if (audit.getConfidentialFlag())
			audit.setConfidLapseDate(AuditUtil.getLapseDate(audit.getConfidPeriod()));
		fredDAO.saveOrUpdate(audit);
	}
	
	public static boolean isMandatoryFieldComplete(Sample sample) {
		if (isBacklogSample(sample))
			return true;
		if (FREDUtil.isEmpty(sample.getCollectors())
				|| sample.getCollectionDate() == null || sample.getInPlace() == null
				|| (FREDUtil.isEmpty(sample.getSentTos()) && FREDUtil.isEmpty(sample.getNotCollected())))
			return false;
		return true;
	}
	
	public static boolean isBacklogSample(Sample sample) {
		Folder folder = sample.getAudit().getFolder();
		return (folder != null && folder.getFolderType().getName().equals(Folder.FOLDER_TYPE_BACKLOG));
	}
	
	public boolean isSampleConfidential(Sample sample) {
		if (sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP))
			return false;
		return sample.getAudit().getConfidentialFlag().booleanValue();
	}
	
	public String getSampleConfidAccessListDescription(Sample sample) {
		return new AuditUtil(factory).getConfidAccessListDescription(sample.getAudit());
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
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_DELETE_RIGHT, fredDAO);

		return folder.isAllowedDeleteLocalities();
	}
	
	public boolean isAllowedEditSample(UserAccount user, Sample sample, UserFolder userFolder) throws StorageAccessException {
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_EDIT_RIGHT, fredDAO) ||
				FREDUtil.checkEditSecurityClass(user);
		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_EDIT_RIGHT, fredDAO);

		return userFolder.isAllowedEditLocalities();
	}
	
	public boolean isAllowedDeleteSample(UserAccount user, Sample sample, UserFolder userFolder) throws StorageAccessException {
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;
		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_DELETE_RIGHT, fredDAO);

		return userFolder.isAllowedDeleteLocalities();
	}
	
	public boolean isAllowedSubmitSample(UserAccount user, Sample sample, UserFolder userFolder) throws NumberFormatException, StorageAccessException {
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;	
		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_SUBMIT_RIGHT, fredDAO);
		
		return userFolder.isAllowedSubmitLocalities();
	}

	public boolean isAllowedEditSampleConfid(UserAccount user, Sample sample, UserFolder userFolder) {
		if (FREDConstants.OUTCROP.equals(sample.getFeature().getFeatureType()))
			return false;
		
		Audit audit = sample.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return audit.getCreatedBy().getUserId().toString().equals(user.getId());

		return userFolder.isAllowedEditLocalities();		
	}
	
	public Sample getSample(int sampleId) throws StorageAccessException {
		return fredDAO.get(sampleId, nz.cri.gns.fred.hibernate.Sample.class);
	}
	
	public AuditEdit getMostRecentEdit(Audit audit) throws StorageAccessException {
		return fredDAO.getMostRecentEdit(audit);
	}
	
	public int getPaleontologyRecordCount(Sample sample) {
		int count = 0;
		for (Record record : sample.getRecords()) {
			count += (record.getPaleontology() != null) ? 1 : 0;
		}
		return count;
	}
	
	public int getAdoptionRecordCount(Sample sample) {
		int count = 0;
		for (Record record : sample.getRecords()) {
			count += (record.getAdoption() != null) ? 1 : 0;
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
		for (Record record : sample.getRecords()) {
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
	public Sample createSample(Feature feature, Integer folderId, boolean reuseFeatureAudit, UserAccount user) throws StorageAccessException {
		Sample sample = fredDAO.createNewSample(feature);
		Audit audit = null;
		if (reuseFeatureAudit)
			audit = feature.getAudit();
		else {
			audit = fredDAO.createNewAudit();
			if (folderId != null)
				audit.setFolder(fredDAO.get(folderId, nz.cri.gns.fred.hibernate.Folder.class));
			audit.setStatus(FREDConstants.WORKING);
			audit.setCreatedDate(new Date());
			audit.setCreatedById(new Integer(user.getId()));
		}
		sample.setAudit(audit);
		return sample;
	}
	
	public Sample createSample(Feature feature, int folderId, boolean reuseFeatureAudit, UserAccount user) throws StorageAccessException {
		return createSample(feature, new Integer(folderId), reuseFeatureAudit, user);
	}
	
	/**
	 * Copies the given SedimentaryFeature but assigns the new one to the 
	 * given sample instead of the original
	 * @throws StorageAccessException 
	 */
	public SedimentaryFeature copyFor(SedimentaryFeature sedFeature, Sample sample) throws StorageAccessException {
		SedimentaryFeature feature = fredDAO.createNewSedimentaryFeature();
		feature.setAbundant(sedFeature.getAbundant());
		feature.setSedimentaryFeatureType(sedFeature.getSedimentaryFeatureType());
		return feature;
	}

	public List<? extends Relationship> getRelationships(Sample sample, String relationTypeName, String relationshipTypeName) throws StorageAccessException {
		RelationType relationType = fredDAO.getRelationType(relationTypeName);
		return getRelationships(sample, relationType, relationshipTypeName);
	}
	
	public List<? extends Relationship> getRelationships(Sample sample, RelationType relationType, String relationshipTypeName) throws StorageAccessException {
		RelationshipType relationshipType = fredDAO.getRelationshipType(relationType, relationshipTypeName);
		
		return fredDAO.getRelationships(sample, relationshipType);
	}

	public List<? extends Relationship> getRelationships(Sample sample, String relationTypeName, String[] relationshipTypes) throws StorageAccessException {
		List<Relationship> relationships = new Vector<Relationship>();
		RelationType relationType = fredDAO.getRelationType(relationTypeName);
		
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
		String name = getCommonRelationshipPropertiesFromDescription(desc, relationship, fredDAO.getRelationType("Sample"));
		relationship.setFeature(new FeatureUtil(factory).getFeatureWithIdentifyingName(name));
		return relationship;
	}

	public Relationship decodeStratigraphicRelationshipDescription(String desc) throws StorageAccessException {
		NoIdRelationship relationship = new NoIdRelationship();
		String name = getCommonRelationshipPropertiesFromDescription(desc, relationship, fredDAO.getRelationType("Stratigraphic"));
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
		RelationshipType relType = fredDAO.getRelationshipType(relationType, parts[where++]);
		if (relType == null) {
			relType = fredDAO.getRelationshipType(relationType, parts[where-1] + " " + parts[where++]);
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
	
	public Audit saveOrUpdate(Audit audit) throws StorageAccessException {
		return fredDAO.saveOrUpdate(audit);
	}

	public void delete(Sample sample) throws StorageAccessException {
		fredDAO.delete(sample);
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
		SentTo sentTo = fredDAO.createNewSentTo();
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
		return fredDAO.getFossilGroup(name);
	}

    public Lab findLab(String labName) throws StorageAccessException {
    	return fredDAO.findLab(labName);
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
		Relationship rel = fredDAO.createNewRelationship();
		rel.setSample(sample);
		rel.setFeature(feature);
		rel.setRelationType(fredDAO.getRelationType(relationType));
		rel.setRelationshipType(fredDAO.getRelationshipType(rel.getRelationType(), relationshipType));
		//sampleDAO.saveOrUpdate(rel);
		return rel;
	}

	/**
	 * Creates a new relationship object which is a copy of the given one, but valid within the access
	 * layers world
	 * @throws StorageAccessException 
	 */
	public Relationship cloneRelationship(Relationship newRelationship) throws StorageAccessException {
		Relationship rel = fredDAO.createNewRelationship();
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
		//sampleDAO.saveOrUpdate(rel);
		return rel;
	}

	public boolean isPreviousSampleRelationship(Relationship rel) {
		return rel.getRelationType().getName().equals(FREDConstants.SAMPLE)
			&& rel.getRelationshipType().getName().equals(FREDConstants.NEARBY);
	}

	public boolean isStratigraphicRelationship(Relationship rel) {
		return rel.getRelationType().getName().equals(FREDConstants.STRATIGRAPHIC);
	}
	
	public DrillType getDrillType(Integer id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.DrillType.class);
	}
	
	public DrillType getDrillType(String drillType) throws StorageAccessException {
		return fredDAO.getFirst("FROM DrillType AS t WHERE t.name = ?", DrillType.class, drillType);
	}
	
	public List<DrillType> getDrillTypes() throws StorageAccessException {
		return fredDAO.getList("FROM DrillType AS t", DrillType.class);
	}
	
	public GrainSize getGrainSize(Integer id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.GrainSize.class);
	}
	
	public List<GrainSize> getGrainSizes() throws StorageAccessException {
		return fredDAO.getList("FROM GrainSize AS a", GrainSize.class);
	}

	public BedThickness getBeddingThickness(Integer id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.BedThickness.class);
	}
	
	public List<BedThickness> getBeddingThicknesses() throws StorageAccessException {
		return fredDAO.getList("FROM BedThickness AS b", BedThickness.class);
	}
	
	public Bedding getBedding(Integer id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.Bedding.class);
	}
	
	public List<Bedding> getBeddings() throws StorageAccessException {
		return fredDAO.getList("FROM Bedding AS b", Bedding.class);
	}
	
	public Weathering getWeathering(Integer id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.Weathering.class);
	}
	
	public List<Weathering> getWeatherings() throws StorageAccessException {
		return fredDAO.getList("FROM Weathering AS w", Weathering.class);
	}
	
	public Hardness getHardness(Integer id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.Hardness.class);
	}

	public List<Hardness> getHardnesses() throws StorageAccessException {
		return fredDAO.getList("FROM Hardness AS h", Hardness.class);
	}

	public Carbonate getCarbonate(Integer id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.Carbonate.class);
	}
	
	public List<Carbonate> getCarbonates() throws StorageAccessException {
		return fredDAO.getList("FROM Carbonate AS c", Carbonate.class);
	}
	
	public ColourModifier getColourModifier(Integer id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.ColourModifier.class);
	}
	
	public List<ColourModifier> getColourModifiers() throws StorageAccessException {
		return fredDAO.getList("FROM ColourModifier AS c", ColourModifier.class);
	}
	
	public RockColour getRockColour(Integer id) throws StorageAccessException {
		return fredDAO.get(id, nz.cri.gns.fred.hibernate.RockColour.class);
	}

	public List<RockColour> getRockColours() throws StorageAccessException {
		return fredDAO.getList("FROM RockColour AS r", RockColour.class);
	}
	
	public List<SedimentaryFeatureType> getSedimentaryFeatureTypes() throws StorageAccessException {
		return fredDAO.getList("FROM SedimentaryFeatureType AS s", SedimentaryFeatureType.class);
	}
	
	public List<FossilGroup> getFossilGroups() throws StorageAccessException {
		return fredDAO.getList("FROM FossilGroup AS f", FossilGroup.class);
	}
	
	public List<Lab> getLabs() throws StorageAccessException {
		return fredDAO.getList("FROM Lab AS l", Lab.class);
	}
	
	public List<StratigraphicUnit> getStratigraphicUnits() throws StorageAccessException {
		return fredDAO.getList("FROM StratigraphicUnit AS s", StratigraphicUnit.class);
	}
	
	public StratigraphicUnit findStratigraphicUnit(String name) throws StorageAccessException {
		return fredDAO.findStratigraphicUnit(name);
	}
	
	public List<RelationshipType> getRelationshipTypes(String relationType) throws StorageAccessException {
		RelationType relType = fredDAO.getRelationType(relationType);
		return fredDAO.getList("FROM RelationshipType AS r WHERE r.relationType = ?", RelationshipType.class, relType);
	}
	
	public RelationshipType findRelationshipType(String name) throws StorageAccessException {
		return fredDAO.findRelationshipType(name);
	}
	
	public SedimentaryFeature createSedimentaryFeature(String sedFeature, boolean isAbundant) throws StorageAccessException {
		SedimentaryFeature feature = fredDAO.createNewSedimentaryFeature();
		feature.setAbundant((isAbundant) ? "Y" : "N");
		if (sedFeature.indexOf(":") >= 0)
			sedFeature = sedFeature.substring(sedFeature.indexOf(":") + 1).trim();
		SedimentaryFeatureType type = fredDAO.getSedimentaryFeatureTypeWithName(sedFeature);
		if (type == null)
			throw new IllegalArgumentException("Invalid sedimentary feature type: " + sedFeature);
		
		feature.setSedimentaryFeatureType(type);
		return feature;
	}

    public void saveOrUpdate(Sample sample) throws StorageAccessException {
    	if (sample.getAudit() == null) {
    		throw new IllegalStateException("Cannot save a sample without an audit");
    	}
        fredDAO.saveOrUpdate(sample.getAudit());
        fredDAO.saveOrUpdate(sample);
    }

    /**
     * Ensures the given sample is attached to the current persistence mechanism
     * @param sample
     * @throws StorageAccessException 
     */
    public void attach(Sample sample) throws StorageAccessException {
        fredDAO.attach(sample);
    }

    /**
     * Ensures the given sample is attached to the current persistence mechanism
     * @param sample
     * @throws StorageAccessException 
     * @throws StorageAccessException 
     */
    public void attach(Audit audit) throws StorageAccessException {
        fredDAO.attach(audit);
    }

	public void delete(SentTo sentTo) throws StorageAccessException {
		fredDAO.delete(sentTo);
	}
	
	public void delete(Relationship rel)  throws StorageAccessException {
		fredDAO.delete(rel);
	}

}
