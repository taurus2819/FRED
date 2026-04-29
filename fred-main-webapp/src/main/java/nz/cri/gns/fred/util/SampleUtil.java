package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.auth.domain.User;
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
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.FrUserView;
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
import nz.cri.gns.fred.model.Stage;
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

        /**
         * Throws IllegalStateException...always
         *
         * @return
         */
        @Override
        public Integer getRelationshipId() {
            throw new IllegalStateException("'NoIdRelationship'....get it?");
        }

        /**
         * Does nothing
         *
         * @param relationshipId
         */
        @Override
        public void setRelationshipId(Integer relationshipId) {
        }

        @Override
        public RelationType getRelationType() {
            return relationType;
        }

        @Override
        public void setRelationType(RelationType relationType) {
            this.relationType = relationType;
        }

        @Override
        public Integer getStratUnitId() {
            return stratUnitId;
        }

        @Override
        public void setStratUnitId(Integer stratUnitId) {
            this.stratUnitId = stratUnitId;
        }

        @Override
        public Double getDistance() {
            return distance;
        }

        @Override
        public void setDistance(Double distance) {
            this.distance = distance;
        }

        @Override
        public String getDistanceMod() {
            return distanceMod;
        }

        @Override
        public void setDistanceMod(String distanceMod) {
            this.distanceMod = distanceMod;
        }

        @Override
        public Double getDistanceRange() {
            return distanceRange;
        }

        @Override
        public void setDistanceRange(Double distanceRange) {
            this.distanceRange = distanceRange;
        }

        @Override
        public Feature getFeature() {
            return feature;
        }

        @Override
        public void setFeature(Feature feature) {
            this.feature = feature;
        }

        @Override
        public RelationshipType getRelationshipType() {
            return relationshipType;
        }

        @Override
        public void setRelationshipType(RelationshipType relationshipType) {
            this.relationshipType = relationshipType;
        }

        @Override
        public Sample getSample() {
            return sample;
        }

        @Override
        public void setSample(Sample sample) {
            this.sample = sample;
        }

        @Override
        public String getStratUnit() {
            return stratUnit;
        }

        @Override
        public void setStratUnit(String stratUnit) {
            this.stratUnit = stratUnit;
        }

        /**
         *
         * @param o
         * @return
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Relationship)) {
                return false;
            }

            Relationship rel = (Relationship) o;

            return Objects.equals(distance, rel.getDistance())
                    && Objects.equals(distanceRange, rel.getDistanceRange())
                    && FREDUtil.equals(distanceMod, rel.getDistanceMod(), true)
                    && FREDUtil.equals(feature, rel.getFeature(), true)
                    && FREDUtil.equals(relationshipType, rel.getRelationshipType(), true)
                    && FREDUtil.equals(relationType, rel.getRelationType(), true)
                    && FREDUtil.equals(sample, rel.getSample(), true)
                    && FREDUtil.equals(stratUnit, rel.getStratUnit(), true)
                    && FREDUtil.equals(stratUnitId, rel.getStratUnitId(), true);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + Objects.hashCode(this.relationType);
            hash = 41 * hash + Objects.hashCode(this.stratUnitId);
            hash = 41 * hash + Objects.hashCode(this.stratUnit);
            hash = 41 * hash + Objects.hashCode(this.distance);
            hash = 41 * hash + Objects.hashCode(this.distanceMod);
            hash = 41 * hash + Objects.hashCode(this.distanceRange);
            hash = 41 * hash + Objects.hashCode(this.sample);
            hash = 41 * hash + Objects.hashCode(this.feature);
            hash = 41 * hash + Objects.hashCode(this.relationshipType);
            return hash;
        }

        @Override
        public String toString() {
            return SampleUtil.getRelationshipDescription(this);
        }

        @Override
        public int compareTo(Relationship arg0) {
            try {
                if (sample.equals(arg0.getSample())) {
                    if (relationshipType.equals(arg0.getRelationshipType())) {
                        return distance.compareTo(arg0.getDistance());
                    }
                    return relationshipType.compareTo(arg0.getRelationshipType());
                }
                return sample.compareTo(arg0.getSample());
            } catch (Exception e) {
            }
            return 0;
        }
    }

    private final FredDAO fredDAO;
    private FeatureUtil featureUtil;

    public SampleUtil(DAOFactory factory) {
        super(factory);
        this.fredDAO = factory.getFredDAO();
        featureUtil = new FeatureUtil(factory);
    }

    public Sample findSample(String localityName) throws StorageAccessException {
        featureUtil = new FeatureUtil(factory);
        if (!localityName.contains(":")) {
            Feature feature = featureUtil.getFeatureWithIdentifyingName(localityName);
            if (feature != null && feature.getFeatureType().equals(OUTCROP)) {
                if (!feature.getSamples().isEmpty()){
                    return featureUtil.getOutcropSample(feature);
                }
            }
            return null;
        }
        Feature feature = featureUtil.getFeatureWithIdentifyingName(localityName.substring(0, localityName.indexOf(":")).trim());
        if (feature != null && !feature.getFeatureType().equals(OUTCROP)) {
            String sampleName = localityName.substring(localityName.indexOf(":") + 1).trim();
            for (Sample sample : feature.getSamples()) {
                if (sampleName.equals(getDrillHoleDepthDescription(sample))) {
                    return sample;
                }
            }
        }
        return null;
    }

    public Sample findOrCreateSample(String localityName, User user) throws StorageAccessException, DataInputException, InsufficientPrivelegesException {
        if (!localityName.contains(":")) {
            return findSample(localityName);
        }
        featureUtil = new FeatureUtil(factory);
        Feature feature = featureUtil.getFeatureWithIdentifyingName(localityName.substring(0, localityName.indexOf(":")).trim());
        if (feature != null && !feature.getFeatureType().equals(OUTCROP)) {
            String sampleName = localityName.substring(localityName.indexOf(":") + 1).trim();
            for (Sample sample : feature.getSamples()) {
                if (sampleName.equals(getDrillHoleDepthDescription(sample))) {
                    return sample;
                }
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

    public <T extends Comparable<? super T>> List<T> getListFromHQL(String query, Class<T> clazz) throws StorageAccessException {
        return fredDAO.getList(query, clazz);
    }

    public List<Sample> getLightweightSamples(String sampleSubquery) throws StorageAccessException {
        return fredDAO.getList("select s FROM Sample AS s WHERE s.sampleId in (" + sampleSubquery + ")", Sample.class);
    }

    public static String getDrillHoleDepthDescription(Sample sample) {
        Feature feature = sample.getFeature();

        //Not relevant for outcrops
        if (feature.getFeatureType().equals(OUTCROP)) {
            return null;
        }

        if (!hasDepthInformation(sample)) {
            return DEPTH_NOT_SPECIFIED;
        }

        return getDrillHoleDepthDescription(sample.getTopDepth(), sample.getBottomDepth(), sample.getDepthUnit(), sample.getDrillType());
    }

    /**
     * @param topDepth
     * @param bottomDepth
     * @param unit
     * @param drillType
     * @return
     */
    public static String getDrillHoleDepthDescription(Double topDepth, Double bottomDepth, String unit, DrillType drillType) {
        StringBuilder desc = new StringBuilder();

        if (topDepth != null) {
            desc.append(FREDUtil.formatDoubleForOutput(topDepth, 3)).append(" ").append(unit);
        }
        if (bottomDepth != null) {
            desc.append(" - ").append(FREDUtil.formatDoubleForOutput(bottomDepth, 3)).append(" ").append(unit);
        }

        if (FEET_UNIT.equals(unit)) {
            desc.append(" (");
            if (topDepth != null) {
                desc.append(FREDUtil.formatDoubleForOutput((topDepth * FT_TO_M), 3)).append(" m");
            }
            if (bottomDepth != null) {
                desc.append(" - ").append(FREDUtil.formatDoubleForOutput((bottomDepth * FT_TO_M), 3)).append(" m");
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
            if (!desc.contains("-")) {
                String[] bits = desc.split(" ");
                depths[0] = Double.valueOf(bits[0]);
                depths[1] = null;
                depths[2] = bits[1];
                if (bits.length > 2) {
                    depths[3] = getDrillType(bits[2]);
                } else {
                    depths[3] = null;
                }
            } else {
                String[] bits = desc.split("-");
                String[] littleBits = bits[0].trim().split(" ");
                depths[0] = Double.valueOf(littleBits[0]);
                littleBits = bits[1].trim().split(" ");
                depths[1] = Double.valueOf(littleBits[0]);
                depths[2] = littleBits[1];
                if (littleBits.length > 2) {
                    depths[3] = getDrillType(littleBits[2]);
                } else {
                    depths[3] = null;
                }
            }
            return depths;
        } catch (StorageAccessException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw new DataInputException("Drillhole Depth", "Incorrectly formatted");
        }
    }

    public static boolean hasDepthInformation(Sample sample) {
        return sample.getTopDepth() != null || sample.getBottomDepth() != null || sample.getDrillType() != null;
    }

    /**
     * Returns the Sample immediately above the given Sample in a drillhole or
     * vertical section
     *
     * @param sample
     * @return
     */
    public static Sample getSampleAbove(Sample sample) {
        if (!hasDepthInformation(sample)) {
            return null;
        }
        Vector<Sample> samples = new Vector<>(FeatureUtil.getSortedSamples(sample.getFeature()));
        if (samples == null || samples.size() == 1) {
            return null;
        }
        int sampleIdx = samples.indexOf(sample);
        if (sampleIdx == 0) {
            return null;
        }
        Sample aboveSample = samples.elementAt(sampleIdx - 1);
        return (hasDepthInformation(aboveSample) ? aboveSample : null);
    }

    /**
     * Returns the Sample immediately below the given Sample in a drillhole or
     * vertical section
     *
     * @param sample
     * @return
     */
    public static Sample getSampleBelow(Sample sample) {
        if (!hasDepthInformation(sample)) {
            return null;
        }
        Vector<Sample> samples = new Vector<>(FeatureUtil.getSortedSamples(sample.getFeature()));
        if (samples == null || samples.size() == 1) {
            return null;
        }
        int sampleIdx = samples.indexOf(sample);
        if (sampleIdx == samples.size() - 1) {
            return null;
        }
        Sample belowSample = samples.elementAt(sampleIdx + 1);
        return (hasDepthInformation(belowSample) ? belowSample : null);
    }

    public void deleteSamples(String[] sampIds, UserFolder folder, User user) {
        boolean errFlag = false;
        for (String sampId : sampIds) {
            try {
                deleteSample(getSample(Integer.parseInt(sampId)), folder, user);
            } catch (NumberFormatException | InsufficientPrivelegesException | StorageAccessException | DataInputException e) {
                errFlag = true;
            }
        }
        if (errFlag) {
            throw new IllegalStateException("An error has occurred. Not all localities have been removed/deleted");
        }
    }

    public void deleteSample(int sampleId, UserFolder folder, User user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
        Sample sample = getSample(sampleId);
        deleteSample(sample, folder, user);
    }

    public void deleteSample(Sample sample, UserFolder folder, User user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
        if (!isAllowedDeleteSample(sample, folder, user)) {
            throw new InsufficientPrivelegesException();
        }

        //Remove from the feature
        Feature feature = sample.getFeature();
        int sampleCount = feature.getSamples().size();

        //throw exception if only one sample (ie stop user deleting all samples. Can remove once database restructured
        if (sampleCount == 1) {
            throw new DataInputException("Samples", "Cannot delete the last sample. Please add new one first");
        }

        feature.getSamples().remove(sample);

        Audit audit = sample.getAudit();

        //And then delete it from DB
        fredDAO.delete(sample);

        //try and delete audit record (if can't then probably also used by feature) so just ignore error
        try {
            fredDAO.delete(audit);
        } catch (StorageAccessException e) {
        }
    }

    public void submitSample(int sampleId, UserFolder folder, User user) throws DataInputException, InsufficientPrivelegesException, StorageAccessException {
        submitSample(getSample(sampleId), folder, user);
    }

    public void submitSample(Sample sample, UserFolder folder, User user) throws DataInputException, InsufficientPrivelegesException, StorageAccessException {
        //Update the audit log, so long as this isn't an outcrop
        if (!sample.getFeature().getFeatureType().equals(OUTCROP)) {
            if (!isAllowedSubmitSample(user, sample, folder)) {
                throw new InsufficientPrivelegesException();
            }
            if (!isMandatoryFieldComplete(sample)) {
                throw new MandatoryFieldsMissingException();
            }

            setAuditApproved(sample, user);
        }
    }

    public void submitSamples(String[] sampIds, UserFolder folder, User user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, DataInputException {
        for (String sampId : sampIds) {
            submitSample(getSample(Integer.parseInt(sampId)), folder, user);
        }
    }

    private void setAuditApproved(Sample sample, User user) throws StorageAccessException {
        Audit audit = sample.getAudit();
        audit.setStatus(APPROVED);		//Samples don't need approval
        audit.setSubmittedById(user.getId().intValue());
        audit.setSubmittedDate(new Date());
        audit.setWorkingComments(null);
        audit.setFolder(null);
        if (audit.getConfidentialFlag()) {
            audit.setConfidLapseDate(AuditUtil.getLapseDate(audit.getConfidPeriod()));
        }
        fredDAO.saveOrUpdate(audit);
    }

    public static boolean isMandatoryFieldComplete(Sample sample) {
        if (isBacklogSample(sample)) {
            return true;
        }
        return !(FREDUtil.isEmpty(sample.getCollectors())
                || sample.getCollectionDate() == null || sample.getInPlace() == null
                || (FREDUtil.isEmpty(sample.getSentTos()) && FREDUtil.isEmpty(sample.getNotCollected())));
    }

    public static boolean isBacklogSample(Sample sample) {
        Folder folder = sample.getAudit().getFolder();
        return (folder != null && folder.getFolderType().getName().equals(Folder.FOLDER_TYPE_BACKLOG));
    }

    public boolean isSampleConfidential(Sample sample) {
        if (sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
            return false;
        }
        return sample.getAudit().getConfidentialFlag();
    }

    public String getSampleConfidAccessListDescription(Sample sample) {
        return new AuditUtil(factory).getConfidAccessListDescription(sample.getAudit());
    }

    /**
     * Returns true if a user is allowed to view the locality
     *
     * @param user
     * @param sample
     * @return
     * @throws nz.cri.gns.dataaccess.StorageAccessException
     */
    public boolean isAllowedReadSample(User user, Sample sample) throws StorageAccessException {
        if (user == null) {
            return false;
        }

        //check feature type - if not outcrop then check sample
        if (!sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
            if (sample.getAudit().getStatus().equals(FREDConstants.APPROVED)) {
                if (!new AuditUtil(factory).isAllowedReadApproved(sample.getAudit(), user)) {
                    return false;
                }
            } else {
                UserFolder folder = new FolderUtil(factory).getUserFolder(sample.getAudit().getFolder().getFolderId(), user);
                if (folder == null || !folder.isAllowedReadLocalities()) {
                    return false;
                }
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
    private boolean isAllowedDeleteSample(Sample sample, UserFolder folder, User user) throws StorageAccessException {
        Audit audit = sample.getAudit();
        if (audit.getStatus().equals(APPROVED)) {
            return false;
        }

        if (audit.getStatus().equals(WAITING)) {
            return featureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_DELETE_RIGHT, fredDAO);
        }

        return folder.isAllowedDeleteLocalities();
    }

    public boolean isAllowedEditSample(User user, Sample sample, UserFolder userFolder) throws StorageAccessException {
        Audit audit = sample.getAudit();
        if (audit.getStatus().equals(APPROVED)) {
            return featureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_EDIT_RIGHT, fredDAO)
                    || FREDUtil.checkEditSecurityClass(user);
        }
        if (audit.getStatus().equals(WAITING)) {
            return featureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_EDIT_RIGHT, fredDAO);
        }

        return userFolder.isAllowedEditLocalities();
    }

    public boolean isAllowedDeleteSample(User user, Sample sample, UserFolder userFolder) throws StorageAccessException {
        Audit audit = sample.getAudit();
        if (audit.getStatus().equals(APPROVED)) {
            return false;
        }
        if (audit.getStatus().equals(WAITING)) {
            return featureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_DELETE_RIGHT, fredDAO);
        }

        return userFolder.isAllowedDeleteLocalities();
    }

    public boolean isAllowedSubmitSample(User user, Sample sample, UserFolder userFolder) throws NumberFormatException, StorageAccessException {
        Audit audit = sample.getAudit();
        if (audit.getStatus().equals(APPROVED)) {
            return false;
        }
        if (audit.getStatus().equals(WAITING)) {
            return featureUtil.hasMasterfileRights(user, sample.getFeature(), UserFolder.FOLDER_SUBMIT_RIGHT, fredDAO);
        }

        return userFolder.isAllowedSubmitLocalities();
    }

    public boolean isAllowedEditSampleConfid(User user, Sample sample, UserFolder userFolder) throws StorageAccessException {
        FrUserView frUser = new UserUtil(factory).getFrUserView(user.getId().intValue());
        Audit audit = sample.getAudit();

        // If an Outcrop feature, confidentiality does not apply
        if (FREDConstants.OUTCROP.equals(sample.getFeature().getFeatureType())) {
            return false;
        }

        // If approved check if user created sample or is an owner of a group containing it
        if (audit.getStatus().equals(APPROVED)) {
            if (audit.getCreatedBy().getUserId().toString().equals(user.getId())) {
                return true;
            }

            Set<ConfidentialGroup> confidGroups = frUser.getConfidGroupsByOwnerId();
            for (ConfidentialGroup confidentialGroup : confidGroups) {
                if (audit.getConfidGroups().contains(confidentialGroup)) {
                    return true;
                }
            }
        }

        // Otherwise, check folder edit rights
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
        List<Paleontology> palRecords = new Vector<>();
        for (Record record : sample.getRecords()) {
            if (record.getPaleontology() != null) {
                palRecords.add(record.getPaleontology());
            }
        }
        Collections.sort(palRecords);
        return palRecords;
    }

    public List<Adoption> getAdoptionRecords(Sample sample) {
        List<Adoption> adoRecords = new Vector<>();
        for (Record record : sample.getRecords()) {
            if (record.getAdoption() != null) {
                adoRecords.add(record.getAdoption());
            }
        }
        Collections.sort(adoRecords);
        return adoRecords;
    }

    /**
     * Return a new sample initialised with the given information
     *
     * @param feature
     * @param folderId
     * @param reuseFeatureAudit
     * @param user
     * @return
     * @throws StorageAccessException
     */
    public Sample createSample(Feature feature, Integer folderId, boolean reuseFeatureAudit, User user) throws StorageAccessException {
        Sample sample = fredDAO.createNewSample();
        sample.setFeature(feature);
        Audit audit = null;
        if (reuseFeatureAudit) {
            audit = feature.getAudit();
        } else {
            audit = fredDAO.createNewAudit();
            if (folderId != null) {
                audit.setFolder(fredDAO.get(folderId, nz.cri.gns.fred.hibernate.Folder.class));
            }
            audit.setStatus(FREDConstants.WORKING);
            audit.setCreatedDate(new Date());
            audit.setCreatedById(user.getId().intValue());
        }
        sample.setAudit(audit);
        return sample;
    }

    public List<Sample> getSamplesByAge(Double startAge, Double stopAge) throws StorageAccessException {
        Set<Sample> samples = new HashSet<>();
        samples.addAll(fredDAO.getList("SELECT DISTINCT s FROM Sample AS s WHERE s.inferredStage.baseAge >= ? AND s.inferredStage.topAge <= ?", Sample.class, stopAge, startAge));
        samples.addAll(fredDAO.getList("SELECT DISTINCT s FROM Sample AS s WHERE s.knownStage.baseAge >= ? AND s.knownStage.topAge <= ?", Sample.class, stopAge, startAge));
        samples.addAll(fredDAO.getList("SELECT DISTINCT s FROM Sample AS s JOIN s.records AS record WHERE record.adoption.stage.baseAge >= ? AND record.adoption.stage.topAge <= ?", Sample.class, stopAge, startAge));
        samples.addAll(fredDAO.getList("SELECT DISTINCT s FROM Sample AS s JOIN s.records AS record WHERE record.paleontology.stage.baseAge >= ? AND record.paleontology.stage.topAge <= ?", Sample.class, stopAge, startAge));
        return FREDUtil.getSortedList(samples);
    }

    /**
     * Copies the given SedimentaryFeature but assigns the new one to the given
     * sample instead of the original
     *
     * @param sedFeature
     * @param sample
     * @return
     * @throws StorageAccessException
     */
    public SedimentaryFeature copyFor(SedimentaryFeature sedFeature, Sample sample) throws StorageAccessException {
        SedimentaryFeature feature = fredDAO.createNewSedimentaryFeature();
        feature.setAbundant(sedFeature.getAbundant());
        feature.setSedimentaryFeatureType(sedFeature.getSedimentaryFeatureType());
        return feature;
    }

    public List<Relationship> getRelationships(Sample sample, String relationTypeName, String relationshipTypeName) throws StorageAccessException {
        RelationType relationType = getRelationType(relationTypeName);
        return getRelationships(sample, relationType, relationshipTypeName);
    }

    public List<Relationship> getRelationships(Sample sample, RelationType relationType, String relationshipTypeName) throws StorageAccessException {
        RelationshipType relationshipType = getRelationshipType(relationType, relationshipTypeName);
        return getRelationships(sample, relationshipType);
    }

    public List<Relationship> getRelationships(Sample sample, String relationTypeName, String[] relationshipTypes) throws StorageAccessException {
        List<Relationship> relationships = new Vector<>();
        RelationType relationType = getRelationType(relationTypeName);

        for (String typeName : relationshipTypes) {
            relationships.addAll(getRelationships(sample, relationType, typeName));
        }

        return relationships;
    }

    public static String getRelationshipDescription(Relationship rel) {
        StringBuilder desc = new StringBuilder();
        if (rel.getDistanceMod() != null) {
            desc.append(rel.getDistanceMod()).append(" ");
        }
        if (rel.getDistance() != null) {
            desc.append(FREDUtil.formatDoubleForOutput(rel.getDistance(), 2)).append(" m ");
            if (rel.getDistanceRange() != null) {
                desc.append("- ").append(FREDUtil.formatDoubleForOutput(rel.getDistanceRange(), 2)).append(" m ");
            }
        }
        if (!rel.getRelationshipType().getName().equals(FREDConstants.NEARBY)) {
            desc.append(rel.getRelationshipType().getName()).append(" ");
        }
        if (rel.getRelationType().getName().equals(FREDConstants.SAMPLE)) {
            desc.append(FeatureUtil.getFeatureIdentifyingName(rel.getFeature()));
        } else {
            desc.append(rel.getStratUnit());
        }
        return desc.toString();
    }

    public static String getRelationshipDescriptionWithLink(Relationship rel, String path, String target) {
        if (!rel.getRelationType().getName().equals(FREDConstants.SAMPLE)) {
            return getRelationshipDescription(rel);
        }

        StringBuilder desc = new StringBuilder();
        desc.append("<a href=\"").append(path).append(rel.getFeature().getFeatureId()).append("\"");
        if (target != null) {
            desc.append(" target=\"").append(target).append("\"");
        }
        desc.append(">");
        desc.append(getRelationshipDescription(rel));
        desc.append("</a>");
        return desc.toString();
    }

    public Relationship decodeSampleRelationshipDescription(String desc) throws StorageAccessException, DataInputException {
        NoIdRelationship relationship = new NoIdRelationship();
        String name = getCommonRelationshipPropertiesFromDescription(desc, relationship, getRelationType("Sample"));
        Feature feature = new FeatureUtil(factory).getFeatureWithIdentifyingName(name);
        if (feature != null) {
            relationship.setFeature(feature);
            return relationship;
        } else {
            throw new DataInputException("Sample Relationships", "Feature " + name + " not found");
        }
    }

    public Relationship decodeStratigraphicRelationshipDescription(String desc) throws StorageAccessException {
        NoIdRelationship relationship = new NoIdRelationship();
        String name = getCommonRelationshipPropertiesFromDescription(desc, relationship, getRelationType("Stratigraphic"));
        //Set the unit by name
        relationship.setStratUnit(name);
        StratigraphicUnit stratUnit = findStratigraphicUnit(name);
        relationship.setStratUnitId((stratUnit != null) ? stratUnit.getId() : null);
        return relationship;
    }

    /**
     * Parses out attributes common to both sample and strat relationships
     *
     * @throws StorageAccessException
     */
    private String getCommonRelationshipPropertiesFromDescription(String desc, Relationship rel, RelationType relationType) throws StorageAccessException {
        //assume no distance data first

        //modified for broken stratlex link AS-1295
        desc = (desc == null) ? "" : desc.trim();
        if (desc.isEmpty()) {
            throw new IllegalArgumentException("Relationship description is blank");
        }
        String[] parts = desc.split("\\s+");
        int where = 0;

        try {
            rel.setDistance(Double.valueOf(parts[where]));
            where++;
        } catch (NumberFormatException e) {
            //This means that there is a modifier in the way
            try {
                rel.setDistance(Double.valueOf(parts[where + 1]));
                rel.setDistanceMod(parts[where]);
                where++;
                where++;
            } catch (NumberFormatException _e) {
                //don't throw exception as distance bits may be NULL
            }
        }

        //skip "m" if present
        if (parts[where].equals("m")) {
            where++;
        }

        if (parts[where].equals("-")) {
            try {
                rel.setDistanceRange(Double.valueOf(parts[++where]));
                ++where;
                //skip "m" if present
                if (parts[where].equals("m")) {
                    where++;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Relationship description is invalid. token1=[" + parts[where-1] + "], token2=["
                    + ((where < parts.length) ? parts[where] : "<none>") + "], desc=[" + desc + "]"
                );
            }
        }

        //Allow one or two word relationships
        RelationshipType relType = getRelationshipType(relationType, parts[where++]);
        if (relType == null) {
            relType = getRelationshipType(relationType, parts[where - 1] + " " + parts[where++]);
            if (relType == null) {
                throw new IllegalArgumentException(
                    "Relationship description is invalid. token1=[" + parts[where-1] + "], token2=["
                    + ((where < parts.length) ? parts[where] : "<none>") + "], desc=[" + desc + "]"
                );
            }
        }
        rel.setRelationshipType(relType);
        rel.setRelationType(relationType);

        return FREDUtil.join(parts, where);
    }
	//modified for broken stratlex link AS-1295

    public RelationType getRelationType(String relationTypeName) throws StorageAccessException {
        return fredDAO.getFirst("FROM RelationType AS rt WHERE rt.name = ?1", RelationType.class, relationTypeName);
    }

    //modified for broken stratlex link AS-1295
    public RelationshipType getRelationshipType(RelationType relationType, String relationshipTypeName) throws StorageAccessException {
        if (relationType == null) return null;
        if (relationshipTypeName == null) return null;

        String name = relationshipTypeName.trim();
        if (name.isEmpty()) return null;

        List<RelationshipType> list =
                fredDAO.getList("FROM RelationshipType AS r WHERE r.relationType = ?1 AND r.name = ?2",
                        RelationshipType.class, relationType, name);

        return (list == null || list.isEmpty()) ? null : list.get(0);
    }
	//modified for stratlex link AS-1295

    public List<Relationship> getRelationships(Sample sample, RelationshipType relationshipType) throws StorageAccessException {
        return fredDAO.getList("FROM Relationship AS r WHERE r.relationshipType = ?1 AND r.sample = ?2", Relationship.class, relationshipType, sample);
    }

    public static String getDipStrikeDescription(Sample sample) {
        StringBuilder desc = new StringBuilder();
        if (sample.getDip() != null) {
            desc.append(sample.getDip()).append((char) 176);
        }
        if (sample.getDipDirection() != null) {
            desc.append(sample.getDipDirection());
        }
        if (sample.getStrike() != null) {
            if (desc.length() > 0) {
                desc.append("/");
            }
            String strikeStr = String.valueOf(sample.getStrike());
            while (strikeStr.length() < 3) {
                strikeStr = "0" + strikeStr;
            }
            desc.append(strikeStr).append((char) 176);
        }
        if (sample.getFacing() != null) {
            desc.append(" (Facing: ").append(sample.getFacing()).append(")");
        }
        return desc.toString();
    }

    /**
     * Returns string representing a single SentTo
     *
     * @param sentTo
     * @return
     */
    public static String getSentToDescription(SentTo sentTo) {
        StringBuilder desc = new StringBuilder();
        if (sentTo.getFossilGroup() != null) {
            desc.append("(").append(sentTo.getFossilGroup().getName()).append(") ");
        }
        if (sentTo.getPerson() != null) {
            desc.append(sentTo.getPerson().getDisplayName());
            if (sentTo.getLab() != null) {
                desc.append("/");
            }
        }
        if (sentTo.getLab() != null) {
            try {
                desc.append(sentTo.getLab().getName());
            } catch (Exception e) {
            }
        }
        if (sentTo.getComments() != null) {
            desc.append(": ").append(sentTo.getComments());
        }
        return desc.toString();
    }

    public static String getGrainSizeDescription(Sample sample) {
        StringBuilder desc = new StringBuilder();
        if (sample.getPrimaryGrainSize() != null) {
            desc.append(sample.getPrimaryGrainSize().getName()).append(" (pri)");
            if (sample.getSecondaryGrainSize() != null) {
                desc.append(", ");
            }
        }
        if (sample.getSecondaryGrainSize() != null) {
            desc.append(sample.getSecondaryGrainSize().getName()).append(" (sec)");
        }
        if (sample.getComparatorUsed() != null) {
            if (sample.getComparatorUsed().equals("Y")) {
                desc.append(" (Comparator used)");
            } else {
                desc.append(" (Comparator not used)");
            }
        }
        return desc.toString();
    }

    public static String getBeddingDescription(Sample sample) {
        StringBuilder desc = new StringBuilder();
        if (sample.getPrimaryBedding() != null) {
            desc.append(sample.getPrimaryBedding().getName());
            if (sample.getSecondaryBedding() != null) {
                desc.append(", ");
            }
        }
        if (sample.getSecondaryBedding() != null) {
            desc.append(sample.getSecondaryBedding().getName());
        }
        return desc.toString();
    }

    public static String getColourDescription(Sample sample) {
        StringBuilder desc = new StringBuilder();
//        if (sample.getColourModifier() != null) {
//            desc.append(sample.getColourModifier().getName()).append(" ");
//        }
        if (sample.getPrimaryColour() != null) {
            desc.append(sample.getPrimaryColour().getName());
            if (sample.getSecondaryColour() != null) {
                desc.append("-");
            }
        }
        if (sample.getSecondaryColour() != null) {
            desc.append(sample.getSecondaryColour().getName());
        }
        if (sample.getWet() != null) {
            desc.append(" (").append(sample.getWet()).append(")");
        }
        return desc.toString();
    }

    public static String getSedFeatureDescription(SedimentaryFeature sedFeat) {
        StringBuilder desc = new StringBuilder();
        desc.append(sedFeat.getSedimentaryFeatureType().getName());
        if (sedFeat.getAbundant() != null && sedFeat.getAbundant().equals("Y")) {
            desc.append(" (abundant)");
        }
        return desc.toString();
    }

    @Override
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
     *
     * @param name
     * @return
     * @throws StorageAccessException
     */
    public FossilGroup getFossilGroup(String name) throws StorageAccessException {
        return fredDAO.getFossilGroup(name);
    }

    public Lab findLab(String labName) throws StorageAccessException {
        return fredDAO.findLab(labName);
    }

    /**
     * Tests for a match between the given relationship and the other
     * arguments.Ignores the fields of relationship that are not passed as
     * arguments
     *
     * @param rel
     * @param feature
     * @param relationType
     * @param relationshipType
     * @return
     */
    public boolean isMatchingRelationship(Relationship rel, Feature feature, String relationType, String relationshipType) {
        return rel.getFeature().equals(feature)
                && rel.getRelationType().getName().equals(relationType)
                && rel.getRelationshipType().getName().equals(relationshipType);
    }

    public boolean isMatchingRelationship(Relationship rel1, Relationship rel2) throws StorageAccessException {
        if (!FREDUtil.equals(rel1.getRelationType(), rel2.getRelationType(), true)) {
            return false;
        }
        if (!FREDUtil.equals(rel1.getRelationshipType(), rel2.getRelationshipType(), true)) {
            return false;
        }
        if (!FREDUtil.equals(rel1.getFeature(), rel2.getFeature(), true)) {
            return false;
        }
        if (!FREDUtil.equals(rel1.getStratUnit(), rel2.getStratUnit(), true)) {
            return false;
        }
        if (!FREDUtil.equals(rel1.getDistanceMod(), rel2.getDistanceMod(), true)) {
            return false;
        }
        if (!FREDUtil.equals(rel1.getDistance(), rel2.getDistance(), true)) {
            return false;
        }
        return FREDUtil.equals(rel1.getDistanceRange(), rel2.getDistanceRange(), true);
    }

    /**
     * Creates a relationship with the given fields
     *
     * @param sample
     * @param feature
     * @param relationType
     * @param relationshipType
     * @return
     * @throws StorageAccessException
     */
    public Relationship createRelationship(Sample sample, Feature feature, String relationType, String relationshipType) throws StorageAccessException {
        Relationship rel = fredDAO.createNewRelationship();
        rel.setSample(sample);
        rel.setFeature(feature);
        rel.setRelationType(getRelationType(relationType));
        rel.setRelationshipType(getRelationshipType(rel.getRelationType(), relationshipType));
        //sampleDAO.saveOrUpdate(rel);
        return rel;
    }

    /**
     * Creates a new relationship object which is a copy of the given one, but
     * valid within the access layers world
     *
     * @param newRelationship
     * @return
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
        return fredDAO.getFirst("FROM DrillType AS t WHERE t.name = ?1", DrillType.class, drillType);
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
        RelationType relType = getRelationType(relationType);
        return fredDAO.getList("FROM RelationshipType AS r WHERE r.relationType = ?1", RelationshipType.class, relType);
    }

    public RelationshipType findRelationshipType(String name) throws StorageAccessException {
        return fredDAO.findRelationshipType(name);
    }

    public SedimentaryFeature createSedimentaryFeature(String sedFeature, boolean isAbundant) throws StorageAccessException {
        SedimentaryFeature feature = fredDAO.createNewSedimentaryFeature();
        feature.setAbundant((isAbundant) ? "Y" : "N");
        if (sedFeature.contains(":")) {
            sedFeature = sedFeature.substring(sedFeature.indexOf(":") + 1).trim();
        }
        SedimentaryFeatureType type = fredDAO.getSedimentaryFeatureTypeWithName(sedFeature);
        if (type == null) {
            throw new IllegalArgumentException("Invalid sedimentary feature type: " + sedFeature);
        }

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
     *
     * @param sample
     * @throws StorageAccessException
     */
    public void attach(Sample sample) throws StorageAccessException {
        fredDAO.attach(sample);
    }

    /**
     * Ensures the given sample is attached to the current persistence mechanism
     *
     * @param audit
     * @throws StorageAccessException
     */
    public void attach(Audit audit) throws StorageAccessException {
        fredDAO.attach(audit);
    }

    public void delete(SentTo sentTo) throws StorageAccessException {
        fredDAO.delete(sentTo);
    }

    public void delete(Relationship rel) throws StorageAccessException {
        fredDAO.delete(rel);
    }

    public Stage getStage(Sample sample) {
        List<Adoption> adoRecords = getAdoptionRecords(sample);
        if (adoRecords != null && !adoRecords.isEmpty()) {
            for (int i = adoRecords.size() - 1; i >= 0; i--) {
                if (adoRecords.get(i).getStage() != null) {
                    return adoRecords.get(i).getStage();
                }
            }
        }
        List<Paleontology> palRecords = getPaleontologyRecords(sample);
        if (palRecords != null && !palRecords.isEmpty()) {
            for (int i = palRecords.size() - 1; i >= 0; i--) {
                if (palRecords.get(i).getStage() != null) {
                    return palRecords.get(i).getStage();
                }
            }
        }
        if (sample.getKnownStage() != null) {
            return sample.getKnownStage();
        }
        return sample.getInferredStage();
    }

}
