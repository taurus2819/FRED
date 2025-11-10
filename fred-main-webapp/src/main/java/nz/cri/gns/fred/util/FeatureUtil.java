package nz.cri.gns.fred.util;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.auth.domain.User;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.cri.gns.fred.model.MetaCat;

public class FeatureUtil extends ModelUtil implements AuditedUtil {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.util.FeatureUtil");

    private final FredDAO fredDAO;
    private final FolderUtil folderUtil;

    private static final String BACKLOG_PREPARE_COMMENTS = "Locality prepared for backlog editing";

    public FeatureUtil(DAOFactory factory) {
        super(factory);
        this.fredDAO = factory.getFredDAO();
        this.folderUtil = new FolderUtil(factory);
    }

    public Feature copyFeature(Feature feature, String newName, UserFolder folder, User user) throws StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
        if (!folder.isAllowedCreateLocalities()) {
            throw new InsufficientPrivelegesException();
        }
        Audit audit = fredDAO.createNewAudit();
        audit.setStatus(FREDConstants.WORKING);
        audit.setCreatedById(user.getId().intValue());
        audit.setCreatedDate(new Date());
        audit.setFolder(folder.getFolder());
        fredDAO.saveOrUpdate(audit);

        Feature newFeature = (Feature) ((nz.cri.gns.fred.hibernate.Feature) feature).clone();
        newFeature.setFeatureId(null);
        newFeature.setFeatureName(newName);
        newFeature.setAudit(audit);
        newFeature.setFrNumber(null);
        //A new copy should not have an entry in folder_contents
        newFeature.setFolders(null);

        //Copy feature images
//        newFeature.setMetaCats(new HashSet<>()); //feature.getMetaCats());

        //Clear out relationships pointing _to_ it
        newFeature.setRelationships(null);
        //Remove any samples that have come across
        newFeature.setSamples(null);
        //Save the new feature!
        fredDAO.saveOrUpdate(newFeature);

        if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
            //For outcrops we copy everything

            //Copy sample (should be only one!)
            Set<Sample> samples = feature.getSamples();
            if (samples.size() != 1) {
                throw new IllegalStateException("Outcrop does not have a singleton sample");
            }
            Sample sample = samples.iterator().next();
            //Copy sample - collectors clone is OK as it's many-to-many
            Sample newSample = cloneSample(newFeature, sample);
            //set newSample's audit to be same as newFeature
            newSample.setAudit(newFeature.getAudit());

            //Save the new sample
            try {
                fredDAO.saveOrUpdate(newSample);
            } catch (StorageAccessException e) {
                Logger.getLogger("FRED").log(Level.SEVERE, e.getMessage());
            }
        } else {
            //Anything that's not actually a feature attribute doesn't get saved
        }

        return newFeature;

    }

    public Sample cloneSample(Feature newFeature, Sample sample) throws StorageAccessException, IntrospectionException {
        Sample newSample = fredDAO.createNewSample();
        newSample.setFeature(newFeature);
        FREDUtil.beanCopy(sample, newSample,
                new FREDUtil.ExcludeByType(Set.class,
                        new FREDUtil.ExcludeByName(FREDUtil.toVector("audit", "sampleId", "feature", "frNumber")))
        );
        //Clear the fr number if it has one
        //Copy relationships
        Set<Relationship> relationships = sample.getRelationships();
        if (relationships != null && !relationships.isEmpty()) {
            HashSet<Relationship> newRels = new HashSet<>();
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
        if (sentTos != null && !sentTos.isEmpty()) {
            HashSet<SentTo> newSent = new HashSet<>();
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
            HashSet<SedimentaryFeature> newSedFeatures = new HashSet<>();
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
            HashSet<Person> newCollectors = new HashSet<>(collectors.size());
            newCollectors.addAll(collectors);
            newSample.setCollectors(newCollectors);
        }

        //Copy sample images
        newSample.setMetaCats(new HashSet<>(sample.getMetaCats()));
        return newSample;
    }

    public void deleteRemoveFeatures(String[] featIDs, UserFolder folder, User user) {
        boolean errFlag = false;
        for (String featID : featIDs) {
            try {
                Feature feature = getFeature(Integer.parseInt(featID));
                if (feature.getAudit().getStatus().equals(FREDConstants.APPROVED)) {
                    removeFeature(feature, folder, user);
                } else {
                    deleteFeature(feature, user);
                }
            } catch (NumberFormatException | InsufficientPrivelegesException | StorageAccessException e) {
                errFlag = true;
            }
        }
        if (errFlag) {
            throw new IllegalStateException("An error has occurred. Not all localities have been removed/deleted");
        }
    }

    public void deleteFeature(Feature feature, User user) throws InsufficientPrivelegesException, StorageAccessException {
        Folder folder = feature.getAudit().getFolder();
        if (folder == null) {
            folder = feature.getMasterFile();
        }

        UserFolder userFolder = folderUtil.getUserFolder(folder.getFolderId(), user.getId().intValue());

        if (!userFolder.isAllowedDeleteLocalities()) {
            throw new InsufficientPrivelegesException();
        }

        if (!FREDUtil.isEmpty(feature.getRelationships())) {
            try {
                Feature relFeature = (feature.getRelationships().iterator().next()).getSample().getFeature();
                throw new IllegalStateException("Cannot delete this locality as it is referenced in a relationship by " + getFeatureIdentifyingName(relFeature));
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Cannot delete this locality as it is referenced in a relationship");
            }
        }

        fredDAO.delete(feature);
    }

    public void removeFeature(Feature feature, UserFolder userFolder, User user) throws StorageAccessException, InsufficientPrivelegesException {
        if (!feature.getAudit().getStatus().equals(FREDConstants.APPROVED)) {
            throw new IllegalStateException("Cannot remove a working locality");
        }
        if (!userFolder.isAllowedDeleteLocalities()) {
            throw new InsufficientPrivelegesException();
        }

        Folder folder = userFolder.getFolder();
        folder.getFeatures().remove(feature);
        feature.getFolders().remove(folder);
        fredDAO.saveOrUpdate(feature);
    }

    public void submitFeatures(String[] featIds, UserFolder folder, User user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, DataInputException {
        for (String featId : featIds) {
            submitFeature(getFeature(Integer.parseInt(featId)), folder, user);
        }
    }

    public void submitFeature(Feature feature, UserFolder folder, User user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
        if (!isAllowedSubmitFeature(user, feature, folder)) {           
            throw new InsufficientPrivelegesException("Outcrop/Drillhole selected : Please select relevant paleontological/sample");
        }
        if (feature.getFeatureType() == null || feature.getRegistrationArea() == null
                || feature.getSiteId() == null || (!isBacklogFeature(feature) && feature.getLocality() == null)) {
            throw new MandatoryFieldsMissingException();
        }

        //if outcrop also check sample mandatory fields
        if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
            for (Sample sample : feature.getSamples()) {
                if (!SampleUtil.isMandatoryFieldComplete(sample)) {
                    throw new MandatoryFieldsMissingException();
                }
            }
        }

        int masterfileId = -1;
        try {
            masterfileId = SiteModelUtil.getMasterfile(feature);
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }

        Audit audit = feature.getAudit();
        audit.setStatus(FREDConstants.WAITING);
        audit.setSubmittedById(user.getId().intValue());
        audit.setSubmittedDate(new Date());
        feature.setMasterFile(fredDAO.get(masterfileId, nz.cri.gns.fred.hibernate.Folder.class));
        fredDAO.saveOrUpdate(audit);
        fredDAO.saveOrUpdate(feature);
    }

    public void revokeFeatures(String[] featIds, UserFolder folder, User user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException {
        for (String featId : featIds) {
            revokeFeature(getFeature(Integer.parseInt(featId)), folder, user);
        }
    }

    public void revokeFeature(Feature feature, UserFolder folder, User user) throws StorageAccessException, InsufficientPrivelegesException {
        if (!isAllowedRevokeFeature(user, feature, folder)) {
            throw new InsufficientPrivelegesException();
        }

        Audit audit = feature.getAudit();
        audit.setStatus(FREDConstants.WORKING);
        audit.setSubmittedById(null);
        audit.setSubmittedDate(null);

        fredDAO.saveOrUpdate(audit);

        feature.setMasterFile(null);
        fredDAO.saveOrUpdate(feature);
    }

    public void alterFeatureTypes(String[] featIDs, String newFeatureType, UserFolder folder, User user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
        for (String featID : featIDs) {
            alterFeatureType(getFeature(Integer.parseInt(featID)), newFeatureType, folder, user);
        }
    }

    public void alterFeatureType(Feature feature, String newFeatureType, UserFolder folder, User user) throws StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
        if (!folder.isAllowedEditLocalities()) {
            throw new InsufficientPrivelegesException();
        }
        if (feature.getAudit().getStatus().equals(FREDConstants.WAITING)) {
            throw new IllegalStateException("Cannot change type as status = waiting");
        }
        String oldFeatureType = feature.getFeatureType();
        if (!oldFeatureType.equals(newFeatureType)) {
            Set<Sample> samples = feature.getSamples();
            switch (newFeatureType) {
                case FREDConstants.OUTCROP:
                    if (!samples.isEmpty() && samples.size() > 1) {
                        throw new IllegalStateException("Cannot change to Outcrop as locality has more than one sample");
                    }
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
                    break;
                case FREDConstants.DRILLHOLE:
                    for (Sample sample : samples) {
                        breakApartSampleAudit(sample);
                        fredDAO.saveOrUpdate(sample);
                    }
                    break;
                case FREDConstants.VERTICAL_SECTION:
                    feature.setDrillholeLicenceName(null);
                    for (Sample sample : samples) {
                        breakApartSampleAudit(sample);
                        sample.setDrillType(null);
                        fredDAO.saveOrUpdate(sample);
                    }
                    break;
                default:
                    break;
            }
            AuditEdit edit = fredDAO.createNewAuditEdit();
            edit.setAudit(feature.getAudit());
            edit.setEditedById(user.getId().intValue());
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

    public void mergeFeatures(Feature mergeToFeature, String[] mergeFeatIDs, UserFolder folder, User user) throws StorageAccessException, InsufficientPrivelegesException, NumberFormatException, IntrospectionException {
        for (String mergeFeatID : mergeFeatIDs) {
            mergeFeature(mergeToFeature, getFeature(Integer.parseInt(mergeFeatID)), folder, user);
        }
    }

    public void mergeFeature(Feature mergeToFeature, Feature mergeFromFeature, UserFolder folder, User user) throws NumberFormatException, StorageAccessException, InsufficientPrivelegesException, IntrospectionException {
        if (!folder.isAllowedEditLocalities()) {
            throw new InsufficientPrivelegesException();
        }
        if (!mergeFromFeature.equals(mergeToFeature)) {
            if (mergeToFeature.getFeatureType().equals(FREDConstants.OUTCROP) || mergeFromFeature.getFeatureType().equals(FREDConstants.OUTCROP)) {
                throw new IllegalStateException("Cannot merge outcrop localities");
            }

            FrNumber mergeFromFrNumber = mergeFromFeature.getFrNumber();
            FrNumber mergeFromYardFrNumber = mergeFromFeature.getYardFrNumber();
            //put in array as feature.getSamples() changes as you change sample's feature
            Set<Sample> samples = mergeFromFeature.getSamples();

            //move all samples from merge feature to parent feature
            for (Sample sample : samples) {
                //check audits - if same as feature then create new onw
                if (sample.getAudit().equals(mergeFromFeature.getAudit())) {
                    Audit newAudit = new AuditUtil(factory).cloneAudit(sample.getAudit());
                    fredDAO.saveOrUpdate(newAudit);
                    sample.setAudit(newAudit);
                }
                //add comments
                AuditEdit edit = fredDAO.createNewAuditEdit();
                edit.setAudit(sample.getAudit());
                edit.setEditedById(user.getId().intValue());
                edit.setEditedDate(new Date());
                edit.setComments("Sample merged into " + getFeatureIdentifyingName(mergeToFeature) + " from " + getFeatureIdentifyingName(mergeFromFeature));
                fredDAO.saveOrUpdate(edit);
                //set sample FRNumber if currently null
                if (sample.getFrNumber() == null && mergeFromFrNumber != null) {
                    sample.setFrNumber(mergeFromFrNumber);
                }
                if (sample.getYardFrNumber() == null && mergeFromYardFrNumber != null) {
                    sample.setYardFrNumber(mergeFromYardFrNumber);
                }
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
                if (relationship.getSample().getFeature().equals(mergeToFeature)) {
                    fredDAO.delete(relationship);
                } else {
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
        for (Feature feature1 : features) {
            if (feature1.equals(feature)) {
                return true;
            }
        }
        return false;
    }

    public Feature[] getFeaturesInFolder(UserFolder folder) throws StorageAccessException {
        HashSet<Feature> features = new HashSet<>();

        //Get from feature_content
        Collection<? extends Feature> featuresToAdd = folder.getFolder().getFeatures();
        if (featuresToAdd != null) {
            features.addAll(featuresToAdd);
        }

        //Get from audit
        List<Audit> audits = getAuditsFor(folder.getFolder());

        for (Audit audit : audits) {
            // - features
            featuresToAdd = audit.getFeatures();
            if (featuresToAdd != null) {
                features.addAll(featuresToAdd);
            }

            //- samples
            featuresToAdd = getFeaturesBySample(audit);
            if (featuresToAdd != null) {
                features.addAll(featuresToAdd);
            }

            //- records
            featuresToAdd = getFeaturesByRecord(audit);
            if (featuresToAdd != null) {
                features.addAll(featuresToAdd);
            }
        }

        Feature[] featuresArray = features.toArray(Feature[]::new);
        Arrays.sort(featuresArray);
        return featuresArray;
    }

    public List<Audit> getAuditsFor(Folder folder) throws StorageAccessException {
        return fredDAO.getList("FROM AuditTable as a WHERE a.folder = ?1", Audit.class, folder);
    }

    public List<Feature> getFeaturesBySample(Audit audit) throws StorageAccessException {
        return fredDAO.getList("SELECT s.feature FROM Sample AS s WHERE s.audit = ?1", Feature.class, audit);
    }

    public List<Feature> getFeaturesByRecord(Audit audit) throws StorageAccessException {
        return fredDAO.getList("SELECT r.sample.feature FROM Record AS r WHERE r.audit = ?1", Feature.class, audit);
    }

    public List<Feature> getFeaturesBySampleSubquery(String sampleSubquery) throws StorageAccessException {
        List<Feature> cartesianFeatures = fredDAO.getList("select new Feature(s.feature.featureId, s.feature.frNumber) FROM Sample s WHERE s.sampleId in (" + sampleSubquery + ")", Feature.class);
        Set<Feature> features = new HashSet<>();
        features.addAll(cartesianFeatures);
        return FREDUtil.getSortedList(features);
    }

    public List<Feature> getFeaturesBySampleSubquery(List<Sample> lightweightSamples) throws StorageAccessException {
        if (lightweightSamples.isEmpty()) {
            return new Vector<>();
        }

        int MAX_ITEMS = 1000;
        int offset = 0;
        List<Feature> features = new ArrayList<>();

        //chunk request due to Oracle list size limit
        while (offset * MAX_ITEMS < lightweightSamples.size()) {
            StringBuilder buffer = new StringBuilder(1024);
            for (int i = 0; i < MAX_ITEMS && offset * MAX_ITEMS + i < lightweightSamples.size(); i++) {
//                int count = offset * MAX_ITEMS + i;
                buffer.append(lightweightSamples.get(offset * MAX_ITEMS + i).getSampleId());
                buffer.append(",");
            }
            String subQuery = buffer.substring(0, buffer.length() - 1);
            features.addAll(getFeaturesBySampleSubquery(subQuery));
            offset++;
        }

        Collections.sort(features);
        return features;
    }

    public List<Feature> getFeaturesByFeatureSubquery(String featureSubquery) throws StorageAccessException {
        return fredDAO.getList("select new Feature(f.featureId, f.frNumber) FROM Feature AS f join fetch f.frNumber WHERE f.featureId in (" + featureSubquery + ")", Feature.class);
    }

    public List<Feature> getFeatures(List<Sample> samples) {
        Set<Feature> features = new HashSet<>();
        for (Sample sample : samples) {
            features.add(sample.getFeature());
        }
        return FREDUtil.getSortedList(features);
    }

    public static Feature[] getOrderedFeaturesInMasterfile(Folder masterfile) {
        Set<Feature> features = masterfile.getMasterfileFeatures();
        Feature[] featuresArray = features.toArray(Feature[]::new);
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
        return features.toArray(Feature[]::new);
    }

    public Feature[] getWaitingFeatures(UserFolder masterfile) throws StorageAccessException {
        List<Feature> features = getFeaturesInMasterfile(masterfile.getFolder(), FREDConstants.WAITING);
        Collections.sort(features);
        return features.toArray(Feature[]::new);
    }

    public List<Feature> getFeaturesInMasterfile(Folder masterfileFolder, Date startDate, Date endDate, String status) throws StorageAccessException {
        return fredDAO.getList("FROM Feature as f WHERE f.masterFile = ? AND "
                + (status.equals(FREDConstants.WAITING) ? "f.audit.submittedDate" : "f.audit.approvedDate")
                + " BETWEEN ? AND ? AND f.audit.status = ?", Feature.class, masterfileFolder, startDate, endDate, status);
    }

    public List<Feature> getFeaturesInMasterfile(Folder masterfileFolder, String status) throws StorageAccessException {
        return fredDAO.getList("FROM Feature as f WHERE f.masterFile = ? AND f.audit.status = ?", Feature.class, masterfileFolder, status);
    }

    /**
     * Returns true if a user is allowed to view the locality always true if
     * user != null && status == approved
     *
     * @param user
     * @param feature
     * @return
     * @throws nz.cri.gns.dataaccess.StorageAccessException
     */
    public boolean isAllowedReadFeature(User user, Feature feature) throws StorageAccessException {
        if (user == null) {
            return false;
        }
        String status = feature.getAudit().getStatus();
        if (!status.equals(FREDConstants.APPROVED)) {
            UserFolder folder = new FolderUtil(factory).getUserFolder(feature.getAudit().getFolder().getFolderId(), user);
            UserFolder mfFolder = null;
            if (feature.getMasterFile() != null) {
                mfFolder = folderUtil.getUserFolder(feature.getMasterFile().getFolderId(), user.getId().intValue());
            }
            return ((folder != null && folder.isAllowedReadLocalities()) || (mfFolder != null && mfFolder.isAllowedReadLocalities()));
        }
        return true;
    }

    /**
     * Returns true if a user is allowed to view the locality site information
     * always true if status == approved
     *
     * @param user
     * @param feature
     * @return
     * @throws nz.cri.gns.dataaccess.StorageAccessException
     */
    public boolean isAllowedReadFeatureSite(User user, Feature feature) throws StorageAccessException {
        String status = feature.getAudit().getStatus();
        if (!status.equals(FREDConstants.APPROVED)) {
            if (user == null) {
                return false;
            }
            UserFolder folder = folderUtil.getUserFolder(feature.getAudit().getFolder().getFolderId(), user.getId().intValue());
            UserFolder mfFolder = null;
            if (feature.getMasterFile() != null) {
                mfFolder = folderUtil.getUserFolder(feature.getMasterFile().getFolderId(), user.getId().intValue());
            }
            return ((folder != null && folder.isAllowedReadLocalities()) || (mfFolder != null && mfFolder.isAllowedReadLocalities()));
        }
        return true;
    }

    public boolean isAllowedEditFeature(User user, Feature feature, UserFolder folder) throws StorageAccessException {
        String status = feature.getAudit().getStatus();
        if (status.equals(FREDConstants.APPROVED)) {
            return hasMasterfileRights(user, feature, UserFolder.FOLDER_EDIT_RIGHT) || FREDUtil.checkEditSecurityClass(user);
        }

        if (status.equals(FREDConstants.WAITING)) {
            return hasMasterfileRights(user, feature, UserFolder.FOLDER_EDIT_RIGHT);
        }

        return folder.isAllowedEditLocalities();
    }

    public boolean isAllowedEditApprovedFeature(User user, Feature feature) throws StorageAccessException {
        if (!feature.getAudit().getStatus().equals(FREDConstants.APPROVED)) {
            return false;
        }
        return hasMasterfileRights(user, feature, UserFolder.FOLDER_EDIT_RIGHT);
    }

    public boolean isAllowedSubmitFeature(User user, Feature feature, UserFolder folder){
        String status = feature.getAudit().getStatus();
            if (status.equals(FREDConstants.WAITING) || status.equals(FREDConstants.APPROVED)) {
                return false;
            }
        return folder.isAllowedSubmitLocalities();
    }

    public boolean isAllowedRevokeFeature(User user, Feature feature, UserFolder folder) {
        if (!feature.getAudit().getStatus().equals(FREDConstants.WAITING)) {
            return false;
        }
        return folder.isAllowedSubmitLocalities();
    }

    public boolean isAllowedDeleteFeature(User user, Feature feature, UserFolder userFolder) throws StorageAccessException {
        Audit audit = feature.getAudit();
        if (audit.getStatus().equals(APPROVED)) {
            return false;
        }
        if (audit.getStatus().equals(WAITING)) {
            return hasMasterfileRights(user, feature, UserFolder.FOLDER_DELETE_RIGHT, fredDAO);
        }

        return userFolder.isAllowedDeleteLocalities();
    }

    /**
     * Returns true is the user is allowed to approve the locality
     *
     * @param user
     * @param feature
     * @return
     * @throws nz.cri.gns.dataaccess.StorageAccessException
     */
    public boolean isAllowedApproveFeature(User user, Feature feature) throws StorageAccessException {
        if (WAITING.equals(feature.getAudit().getStatus())) {
            UserFolder folder = folderUtil.getUserFolder(feature.getMasterFile().getFolderId(), user.getId().intValue());
            if (folder != null) {
                return folder.isAllowedApproveLocalities();
            }
        }
        return false;
    }

    /**
     * Returns true if the user has masterfile rights for this locality
     *
     * @param user
     * @param feature
     * @param right
     * @return
     * @throws StorageAccessException
     * @throws
     */
    public boolean hasMasterfileRights(User user, Feature feature, int right) throws StorageAccessException {
        return hasMasterfileRights(user, feature, right, fredDAO);
    }

    public boolean hasMasterfileRights(User user, Feature feature, int right, FredDAO fredDAO) throws NumberFormatException, StorageAccessException {
        Folder masterfile = feature.getMasterFile();
        if (masterfile == null) {
            return false;
        }

        UserFolder masterfileFolder = folderUtil.getUserFolder(masterfile.getFolderId(), user.getId().intValue());

        return (masterfileFolder == null) ? false : (masterfileFolder.getRights() & right) > 0;
    }

    public Sample getOutcropSample(Feature feature) {
        if (!feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
            throw new IllegalArgumentException("Feature is not an outcrop");
        }  
        Sample sample = null;
        try{        //jira AS-829 : copy function; catching an exception in the backend preventing it getting displayed on the frontend
            if(!feature.getSamples().isEmpty()){
                sample =  new Vector<>(feature.getSamples()).get(0);
            }else {
                sample = fredDAO.createNewSample();
            }
        }catch(NullPointerException ne){
            ne.printStackTrace();
        }
        return sample;
    }

    public void approveFeature(Feature feature, String mapSheet, Integer serialNumber, String recollectionNumber, String comments, User user) throws StorageAccessException, InsufficientPrivelegesException, DataInputException {
        if (!hasMasterfileRights(user, feature, UserFolder.FOLDER_APPROVE_RIGHT)) {
            throw new InsufficientPrivelegesException();
        }

        //Check FR number and throw exception if already exists
        FrNumber frNumber = getFrNumber(mapSheet, serialNumber, recollectionNumber);
        if (frNumber != null) {
            throw new DataInputException("FR Number", "FR Number already defined in database");
        }
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
        } catch (StorageAccessException e) {
        }

        //update audit table
        audit.setStatus(APPROVED);
        audit.setApprovedById(user.getId().intValue());
        audit.setApprovedDate(new Date());
        audit.setFolder(null);
        audit.setWorkingComments(null);
        audit.setCuratorComments(comments);
        fredDAO.saveOrUpdate(audit);
    }

    /**
     * 'Approves' a backlog entered feature
     *
     * @param feature
     * @param user
     * @throws InsufficientPrivelegesException
     * @throws StorageAccessException
     * @throws NamingException
     * @throws SQLException
     */
    public void approveBacklogFeature(Feature feature, User user) throws InsufficientPrivelegesException, StorageAccessException, SQLException, NamingException, IOException {
        //Put it back in the correct folder
        if (!hasMasterfileRights(user, feature, UserFolder.FOLDER_APPROVE_RIGHT)) {
            throw new InsufficientPrivelegesException();
        }

        Audit audit = feature.getAudit();
        audit.setFolder(null);
        audit.setWorkingComments(null);
        audit.setStatus(APPROVED);
        audit.setApprovedById(user.getId().intValue());
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
        } catch (Exception e) {
        }
        feature.setMasterFile(fredDAO.get(SiteModelUtil.getMasterfile(feature), nz.cri.gns.fred.hibernate.Folder.class));
        fredDAO.saveOrUpdate(audit);
        fredDAO.saveOrUpdate(feature);
    }

    public void rejectLocality(Feature feature, String comments, User user) throws StorageAccessException, InsufficientPrivelegesException {
        if (!hasMasterfileRights(user, feature, UserFolder.FOLDER_APPROVE_RIGHT)) {
            throw new InsufficientPrivelegesException();
        }

        Audit audit = feature.getAudit();
        audit.setStatus(REJECTED);
        audit.setCuratorComments(comments);
        fredDAO.saveOrUpdate(audit);
    }

    public void addToFolder(Feature feature, int folderId, User user) throws StorageAccessException, DataInputException {
        if (!feature.getAudit().getStatus().equals(APPROVED)) {
            throw new DataInputException("Folder", "Cannot add a working locality");
        }

        UserFolder userFolder = new FolderUtil(factory).getUserFolder(folderId, user);
        if (!userFolder.isAllowedCreateLocalities()) {
            throw new DataInputException("Folder", "Do not have appropriate rights to add to this folder");
        }

        feature.getFolders().add(fredDAO.get(folderId, nz.cri.gns.fred.hibernate.Folder.class));
        fredDAO.saveOrUpdate(feature);
    }

    /**
     * Returns the next available FR number - <b>not</b> saved to the DB
     *
     * @param feature
     * @return
     * @throws java.sql.SQLException
     * @throws javax.naming.NamingException
     * @throws nz.cri.gns.dataaccess.StorageAccessException
     */
    public FrNumber getNextAvailableFrNumber(Feature feature) throws SQLException, NamingException, StorageAccessException, IOException {
        String mapSheet = SiteModelUtil.getFrNumberMapSheet(feature);
        return getNextAvailableFrNumber(mapSheet);
    }

    /**
     * Returns the next available FR number - <b>not</b> saved to the DB
     *
     * @param mapSheet
     * @return
     * @throws nz.cri.gns.dataaccess.StorageAccessException
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
        if (maxNum == null) {
            return 1;
        }
        return maxNum + 1;
    }

    /**
     * Creates a blank feature of the given type, in the given folder.The
     * feature and its associated entries are _not_ committed to persistent
     * storage.
     *
     * @param folderId
     * @param featureType
     * @param user
     * @return
     * @throws StorageAccessException
     */
    public Feature createFeature(int folderId, String featureType, User user) throws StorageAccessException {
        if (!(featureType.equals(FREDConstants.OUTCROP)
                || featureType.equals(FREDConstants.DRILLHOLE) || featureType.equals(FREDConstants.VERTICAL_SECTION))) {
            throw new IllegalArgumentException("Invalid feature type given: " + featureType);
        }
        Feature feature = fredDAO.createNewFeature();
        feature.setFeatureType(featureType);
        Audit audit = fredDAO.createNewAudit();
        audit.setFolder(fredDAO.get(folderId, nz.cri.gns.fred.hibernate.Folder.class));
        audit.setStatus(FREDConstants.WORKING);
        audit.setCreatedDate(new Date());
        audit.setCreatedById(user.getId().intValue());
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
            //range: e.g. A44/f0001-0003
            if (frNumStr.indexOf("-") > 0) {
                String[] frNumBits = parseFrNumber(frNumStr.substring(0, frNumStr.indexOf("-")));
                Integer endSerialNum = Integer.valueOf(frNumStr.substring(frNumStr.indexOf("-") + 1));
                return fredDAO.getList("FROM FrNumber AS f WHERE f.mapSheet = ? AND f.serialNumber BETWEEN ? AND ?", FrNumber.class, frNumBits[0], Integer.parseInt(frNumBits[1]), endSerialNum);
            }

            //single
            frNumStr = sanitiseFredNumberString(frNumStr);

            List<FrNumber> frNumbers = new Vector<>();
            FrNumber frNum = getMetricFrNumberByString(frNumStr, false);
            if (frNum != null) {
                frNumbers.add(frNum);
            }
            frNum = getYardFrNumberByString(frNumStr, false);
            if (frNum != null) {
                frNumbers.add(frNum);
            }

            // now add recollections
            if (!frNumbers.isEmpty()) {
                String num = frNumbers.get(0).toString(); //"A44/f0001"
                if (num.indexOf("/") > 0) {
                    String mapSheet = num.substring(0, frNumStr.indexOf("/f")).toUpperCase(); //e.g. A44
                    String serial = null;
                    Integer serialNum;
                    String recoll = frNumbers.get(0).getRecollectionNumber();
                    if (recoll == null) {
                        serial = num.substring(frNumStr.indexOf("/f") + 2); //serial: "0001"
                        serialNum = Integer.valueOf(serial);
                        return fredDAO.getList("FROM FrNumber AS f WHERE f.mapSheet = ? AND f.serialNumber = ?",
                                FrNumber.class, mapSheet, serialNum);
                    } else {
                        serial = num.substring(0, num.indexOf(recoll)).substring(frNumStr.indexOf("/f") + 2);
                        serialNum = Integer.valueOf(serial);
                        return fredDAO.getList("FROM FrNumber AS f WHERE f.mapSheet = ? AND f.serialNumber = ? AND f.recollectionNumber=?",
                                FrNumber.class, mapSheet, serialNum, recoll);
                    }
                }
            }
        } catch (DataInputException e) {
            // Reduce log spam. This happens when the user enters a funny FR number.
            log.log(Level.FINE, "User entered something funny:", e);
        }

        return null;
    }

    public FrNumber getMetricFrNumberByString(String frNumStr, boolean createNew) throws DataInputException, StorageAccessException {
        return getFrNumberByString(frNumStr, createNew, false);
    }

    public FrNumber getYardFrNumberByString(String frNumStr, boolean createNew) throws DataInputException, StorageAccessException {
        return getFrNumberByString(frNumStr, createNew, true);
    }

    private FrNumber getFrNumberByString(String frNumStr, boolean createNew, boolean yard) throws DataInputException, StorageAccessException {
        String[] frNumBits = parseFrNumber(frNumStr);
        FrNumber frNumber = null;
        if (yard) {
            frNumber = getYardFrNumber(frNumBits[0] + "/f" + frNumBits[1] + ((frNumBits[2] != null) ? frNumBits[2] : ""));
        } else {
            frNumber = getFrNumber(frNumBits[0] + "/f" + frNumBits[1] + ((frNumBits[2] != null) ? frNumBits[2] : ""));
        }
        if (frNumber == null && createNew) {
            frNumber = new nz.cri.gns.fred.hibernate.FrNumber();
            frNumber.setMapSheet(frNumBits[0]);
            frNumber.setSerialNumber(Integer.valueOf(frNumBits[1]));
            frNumber.setRecollectionNumber(frNumBits[2]);
            if (yard) {
                frNumber.setObsolete("Y");
            }
        }
        System.out.print(frNumber);
        return frNumber;
    }

    private String sanitiseFredNumberString(String origFrNumStr) {
        String frNumStr = origFrNumStr;

        if (!frNumStr.contains("/f")) {
            if (frNumStr.contains("/F")) {
                frNumStr = origFrNumStr.replace("/F", "/f");
            } else if (frNumStr.contains("/")) {
                frNumStr = origFrNumStr.replace("/", "/f");
            } else if (frNumStr.indexOf("f") > 1) {
                frNumStr = origFrNumStr.replace("f", "/f");
            } else if (frNumStr.indexOf("F") > 1) {
                frNumStr = origFrNumStr.replace("F", "/f");
            }
        }

        return frNumStr;
    }

    /**
     * returns array containing 0.Map Sheet 1.Serial Number (with leading zeros)
     * 2. Recollection Number
     *
     * @param frNumStr
     * @return
     * @throws DataInputException
     */
    public String[] parseFrNumber(String frNumStr) throws DataInputException {

        frNumStr = sanitiseFredNumberString(frNumStr);
        if (frNumStr != null && frNumStr.indexOf("/f") > 0) {
            String recollectionNumber;
            Integer serialNumber;
            String mapSheet = frNumStr.substring(0, frNumStr.indexOf("/f")).toUpperCase();
            String num = frNumStr.substring(frNumStr.indexOf("/f") + 2);
            try {
                serialNumber = Integer.valueOf(num);
                recollectionNumber = null;
            } catch (NumberFormatException e) {
                try {
                    serialNumber = Integer.valueOf(num.substring(0, num.length() - 1));
                    recollectionNumber = num.substring(num.length() - 1).toUpperCase();
                } catch (NumberFormatException e1) {
                    throw new DataInputException("FR Number", "Badly formed FR Number");
                }
            }
            String serialNumStr = String.valueOf(serialNumber);
            while (serialNumStr.length() < 4) {
                serialNumStr = "0" + serialNumStr;
            }

            String[] frNumBits = {mapSheet, serialNumStr, recollectionNumber};
            return frNumBits;
        } else {
            throw new DataInputException("FR Number", "Badly formed or missing FR Number: [" + frNumStr + "]");
        }
    }

    /**
     * Returns the feature for this FR number.If FEATURE not found then also
     * checks SAMPLE
     *
     * @param frNum
     * @return
     */
    public Feature getFeature(FrNumber frNum) {
        try {
            return frNum.getFeatures().iterator().next();
        } catch (Exception e) {
        }
        try {
            return frNum.getFeaturesByYard().iterator().next();
        } catch (Exception e) {
        }
        try {
            Sample sample = (Sample) frNum.getSamples().iterator().next();
            return sample.getFeature();
        } catch (Exception e) {
        }
        try {
            Sample sample = (Sample) frNum.getSamplesByYard().iterator().next();
            return sample.getFeature();
        } catch (Exception e) {
        }
        return null;
    }

    public void addSample(Feature feature, String topDepthAsString, String bottomDepthAsString,
            String drillTypeIdAsString, int folderId, User user) throws StorageAccessException, DataInputException {
        if (feature.getFeatureType().equals(OUTCROP)) {
            throw new DataInputException("Sample", "Cannot add samples to an outcrop");
        }

        Double bottomDepth = null, topDepth = null;
        Integer drillTypeId = null;
        if (bottomDepthAsString.length() > 0) {
            try {
                bottomDepth = Double.parseDouble(bottomDepthAsString);
            } catch (NumberFormatException e) {
                throw new DataInputException("Sample Depths", "Data Missing or Invalid");
            }
        }

        if (drillTypeIdAsString.length() > 0) {
            try {
                drillTypeId = Integer.parseInt(drillTypeIdAsString);
            } catch (NumberFormatException e) {
                throw new DataInputException("Sample Depths", "Data Missing or Invalid");
            }
        }

        try {
            topDepth = Double.parseDouble(topDepthAsString);
        } catch (Exception e) {
            throw new DataInputException("Sample Depths", "Data Missing or Invalid");
        }

        Sample sample = new SampleUtil(factory).createSample(feature, folderId, false, user);
        sample.setTopDepth(topDepth);
        sample.setBottomDepth(bottomDepth);

        if (drillTypeId != null) {
            sample.setDrillType(fredDAO.get(drillTypeId, nz.cri.gns.fred.hibernate.DrillType.class
            ));
        }

        //add first FRNumber (if one defined)
        //sample.setFrNumber(FeatureUtil.getFrNumber(feature));
        fredDAO.saveOrUpdate(sample);
    }

    public Audit update(Audit audit) throws StorageAccessException {
        return fredDAO.saveOrUpdate(audit);
    }

    @Override
    public Audit saveOrUpdate(Audit audit) throws StorageAccessException {
        return fredDAO.saveOrUpdate(audit);
    }

    public static Collection<Sample> getSortedSamples(Feature feature) {
        Set<Sample> sampleSet = feature.getSamples();
        Vector<Sample> v = new Vector<>(sampleSet);
        Collections.sort(v);
        return v;
    }

    /**
     * @param feature
     * @param user
     * @param comments
     * @throws nz.cri.gns.dataaccess.StorageAccessException
     * @deprecated use saveFeature(Feature feature, User user, String comments,
     * int dataOriginId)
     */
    @Deprecated
    public void saveFeature(Feature feature, User user, String comments) throws StorageAccessException {
        saveFeature(feature, user, comments, FREDConstants.DATA_ORIGIN_ONLINE);
    }

    public void saveFeature(Feature feature, User user, String comments, int dataOriginId) throws StorageAccessException {

        Audit audit = feature.getAudit();

        if (feature.getFeatureId() == null) {
            //New feature
            audit.setStatus(FREDConstants.WORKING);
            audit.setCreatedById(user.getId().intValue());
            audit.setCreatedDate(new Date());
            audit.setDataOrigin((new AuditUtil(factory)).getDataOrigin(dataOriginId));
        } else if (FeatureUtil.isBacklogFeature(feature)) {
            //Backlog editing feature
            AuditEdit edit = fredDAO.createNewAuditEdit();
            edit.setAudit(audit);
            edit.setEditedById(user.getId().intValue());
            edit.setEditedDate(new Date());
            edit.setComments("Backlog data editing");
            fredDAO.saveOrUpdate(edit);
        } else if (audit.getStatus().equals(FREDConstants.APPROVED)) {
            AuditEdit edit = fredDAO.createNewAuditEdit();
            edit.setAudit(audit);
            edit.setEditedById(user.getId().intValue());
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
        if (feature.getFrNumber() != null) {
            return feature.getFrNumber().getFrNumber();
        }
        if (feature.getFeatureName() != null) {
            return feature.getFeatureName();
        }
        if (feature.getOrigCoord() != null) {
            return feature.getOrigCoord();
        }
        return "Unnamed " + feature.getFeatureType();
    }

    /**
     * Performs the inverse of getFeatureIdentifyingName (except where name is a
     * coordinate)
     *
     * @param ident
     * @return
     * @throws StorageAccessException
     */
    public Feature getFeatureWithIdentifyingName(String ident) throws StorageAccessException {
        try {
            FrNumber frNum = getMetricFrNumberByString(ident, false);
            if (frNum == null) {
                frNum = getYardFrNumberByString(ident, false);
            }
            if (frNum != null) {
                return getFeature(frNum);
            }
        } catch (StorageAccessException | DataInputException e) {
        }
        return getFeatureWithName(ident);
    }

    public Feature
            getFeatureWithName(String name) throws StorageAccessException {
        return fredDAO.getFirst("FROM Feature AS f WHERE f.featureName = ?", Feature.class, name);
    }

    /**
     * Finds feature in folder with matching FeatureName
     *
     * @param ident
     * @param folder
     * @return
     * @throws StorageAccessException
     */
    public Feature getFeatureWithName(String ident, UserFolder folder) throws StorageAccessException {
        if (ident == null) {
            return null;
        }
        for (Feature feature : getFeaturesInFolder(folder)) {
            if (ident.equals(feature.getFeatureName())) {
                return feature;
            }
        }
        return null;
    }

    public FrNumber
            getFrNumber(String frNum) throws StorageAccessException {
        return fredDAO.getFirst("FROM FrNumber AS f WHERE f.frNumber = ? AND f.obsolete IS NULL", FrNumber.class, frNum);
    }

    public FrNumber
            getYardFrNumber(String frNum) throws StorageAccessException {
        return fredDAO.getFirst("FROM FrNumber AS f WHERE f.frNumber = ? AND f.obsolete IS NOT NULL", FrNumber.class, frNum);
    }

    /**
     * Backlog method
     *
     * @param folderToAddTo
     * @param mapSheet
     * @param masterFile
     * @param end
     * @param start
     * @param user
     * @throws StorageAccessException
     */
    public void addToBacklog(UserFolder folderToAddTo, String mapSheet, int start, int end, UserFolder masterFile, User user) throws StorageAccessException {
        if (!masterFile.isAllowedReadLocalities()) {
            return;
        }
        List<FrNumber> numbers = getFrNumbers(mapSheet.toUpperCase(), start, end);
        Folder folder = folderToAddTo.getFolder();
        Folder masterFileFolder = masterFile.getFolder();
        for (FrNumber num : numbers) {
            Feature feature = getFeature(num);
            if (feature == null
                    || !feature.getMasterFile().equals(masterFileFolder)
                    || !feature.getAudit().getStatus().equals(APPROVED)) {
                continue;
            }
            Audit audit = feature.getAudit();
            audit.setFolder(folder);
            audit.setStatus(WORKING);
            AuditEdit edit = fredDAO.createNewAuditEdit();
            edit.setAudit(audit);
            edit.setEditedById(user.getId().intValue());
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
        while (serialNum.length() < 4) {
            serialNum = "0" + serialNum;
        }
        return getFrNumber(mapSheet + "/f" + serialNum + DBUtils.nvl(recollectionNumber));
    }

    /**
     * Same logic as getFrNumber but with no database access or GOL.
     *
     * @param mapSheet
     * @param serialNumber
     * @param recollectionNumber
     * @return
     */
    public String asFrNumberString(String mapSheet, Integer serialNumber, String recollectionNumber) {
        String serialNum = String.valueOf(serialNumber);
        while (serialNum.length() < 4) {
            serialNum = "0" + serialNum;
        }

        return mapSheet + "/f" + serialNum + Objects.toString(recollectionNumber, "");
    }

    public List<FrNumber> getFrNumbers(String mapSheet) throws StorageAccessException {
        return fredDAO.getList("FROM FrNumber AS f WHERE f.mapSheet = ? AND f.obsolete IS NULL", FrNumber.class, mapSheet);
    }

    public List<FrNumber> getFrNumbers(String mapSheet, Integer start, Integer end) throws StorageAccessException {
        return fredDAO.getList("FROM FrNumber AS f WHERE f.mapSheet = ? AND f.serialNumber BETWEEN ? AND ? AND f.obsolete IS NULL", FrNumber.class, mapSheet, start, end);
    }

    private static final String RECOLL_COMMENTS = "*Recoll:";

    public static String combineWorkingComments(String recoll, String workComm) {
        if (recoll != null && recoll.length() > 0) {
            return RECOLL_COMMENTS + recoll + "*" + workComm;
        }
        return workComm;
    }

    /**
     * Splits the recollection data from AUDIT.WORKING_COMMENTS.Returns a String
     * array with two values.First value contains Working Comments and second
     * value contains Recollection (if present) or NULL
     *
     * @param comments
     * @return
     */
    public static String[] splitWorkingComments(String comments) {
        if (comments == null) {
            return new String[]{null, null};
        }
        if (comments.startsWith(RECOLL_COMMENTS)) {
            String recoll = comments.substring(8, comments.indexOf("*", 8));
            String workComm = comments.substring(comments.indexOf("*", 8) + 1);
            return new String[]{workComm, recoll};
        } else {
            return new String[]{comments, null};
        }
    }

    public Integer
            getTotalFeatureCount() throws StorageAccessException {
        return fredDAO.getFirst("SELECT COUNT(*) FROM Feature AS f WHERE f.audit.status=?", Integer.class, AuditUtil.APPROVED);
    }

    public Date
            getLastFeatureApprovalDate() throws StorageAccessException {
        try {
            return fredDAO.getList("SELECT MAX(f.audit.approvedDate) FROM Feature AS f", Date.class
            ).get(0);
        } catch (StorageAccessException e) {
            return null;
        }
    }

    public static String formatDepthForOutput(Double depth, String unit) {
        StringBuffer d = new StringBuffer(FREDUtil.formatDoubleForOutput(depth, 3)).append(" ").append(unit);
        if (FEET_UNIT.equals(unit)) {
            d.append(" (").append(FREDUtil.formatDoubleForOutput(depth * FT_TO_M, 3)).append(" m)");
        }
        return d.toString();
    }

    public Country
            getCountry(String countryCode) throws StorageAccessException {
        return fredDAO.getFirst("FROM Country AS c WHERE c.countryCode = ?", Country.class, countryCode);
    }

    public List<Country> getCountries() throws StorageAccessException {
        return fredDAO.getList("FROM Country AS c", Country.class
        );
    }

    public List<SimpleNameableAndIdentifiable> getFrMapSheetsAsNameable() throws StorageAccessException {
        List<String> sheetsAsString = getFrMapSheets();
        List<SimpleNameableAndIdentifiable> sheets = new Vector<>();
        for (String sheetAsString : sheetsAsString) {
            SimpleNameableAndIdentifiable sheet = new SimpleNameableAndIdentifiable(sheetAsString, sheetAsString);
            sheets.add(sheet);
        }
        return sheets;
    }

    public List<String> getFrMapSheets() throws StorageAccessException {
        return fredDAO.getList("SELECT DISTINCT fr.mapSheet FROM FrNumber AS fr", String.class
        );
    }

    /**
     * @deprecated User "FeatureID=x" instead of spinning the entity model wheel here
     * building a link that may not be clicked anyway
     */
    public String getFullLocalityPDFURL(Feature feature) {
        StringBuffer sb = new StringBuffer("FeatIDs=").append(feature.getFeatureId());
        for (Sample sample : feature.getSamples()) {
            if (!FREDConstants.OUTCROP.equals(feature.getFeatureType())) {
                sb.append("&SampIDs=").append(sample.getSampleId());
            }
            for (Record record : sample.getRecords()) {
                sb.append("&RecIDs=").append(record.getRecordId());
            }
        }
        return sb.toString();
    }

}
