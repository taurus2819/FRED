package nz.cri.gns.fred.de;

import java.io.IOException;
import java.sql.SQLException;

import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.website.ContentProvider;

public class DataEntryFormFactorySiteApi {

    public static LocalitySiteapiDE getLocalityDataEntryForm(int featureID, int folderID, User user, DAOFactory factory, ContentProvider provider) throws IOException, SQLException, DataInputException, InsufficientPrivelegesException, StorageAccessException {
        Feature feature = new FeatureUtil(factory).getFeature(featureID);
        String featureType = feature.getFeatureType();
        if (featureType.equals(FREDConstants.OUTCROP)) {
            return new OutcropLocalitySiteApiDE(feature, folderID, user, factory, provider);
//        } else if (featureType.equals(FREDConstants.DRILLHOLE)) {
//            return new DrillholeLocalityDE(feature, folderID, user, factory, provider);
//        } else if (featureType.equals(FREDConstants.VERTICAL_SECTION)) {
//            return new VertSectLocalityDE(feature, folderID, user, factory, provider);
        } else {
            throw new DataInputException("Feature Type", "Invalid");
        }
    }

    public static LocalitySiteapiDE getLocalityDataEntryForm(String type, User user, int folderID, DAOFactory factory, ContentProvider provider) throws IOException, SQLException, DataInputException, InsufficientPrivelegesException, StorageAccessException {
        if (type.equals(FREDConstants.OUTCROP)) {
            return new OutcropLocalitySiteApiDE(user, folderID, factory, provider);
//        } else if (type.equals(FREDConstants.DRILLHOLE)) {
//            return new DrillholeLocalityDE(user, folderID, factory, provider);
//        } else if (type.equals(FREDConstants.VERTICAL_SECTION)) {
//            return new VertSectLocalityDE(user, folderID, factory, provider);
        } else {
            throw new DataInputException("Feature Type", "Invalid");
        }
    }

    public static LocalitySiteapiDE copyLocalityDataEntryForm(int copyID, int toID, int folderId, User user, DAOFactory factory, ContentProvider provider) throws IOException, SQLException, DataInputException, InsufficientPrivelegesException, StorageAccessException {
        LocalitySiteapiDE toLoc = getLocalityDataEntryForm(toID, folderId, user, factory, provider);
        toLoc.copyFrom(copyID);
        return toLoc;
    }

    public static SampleDE getSampleDataEntryForm(int sampleID, int folderID, User user, DAOFactory factory, ContentProvider provider) throws IllegalArgumentException, DataInputException, SQLException, IOException, InsufficientPrivelegesException, StorageAccessException {
        return new SampleDE(new SampleUtil(factory).getSample(sampleID), folderID, user, factory, provider);
    }

    public static SampleDE getSampleDataEntryForm(User user, int featureID, int folderID, DAOFactory factory, ContentProvider provider) throws SQLException, IOException, DataInputException, StorageAccessException, InsufficientPrivelegesException {
        return new SampleDE(user, new FeatureUtil(factory).getFeature(featureID), folderID, factory, provider);
    }

    public static RecordDE getRecordDataEntryForm(int recordID, int folderID, User user, DAOFactory factory, ContentProvider provider) throws DataInputException, InsufficientPrivelegesException, SQLException, IOException, IllegalArgumentException, StorageAccessException {
        Record record = new RecordUtil(factory).getRecord(recordID);
        if (record.getAdoption() != null) {
            return new AdoptionRecordDE(record, folderID, user, factory, provider);
        } else {
            return new PaleontologyRecordDE(record, folderID, user, factory, provider);
        }
    }

    public static RecordDE getRecordDataEntryForm(String type, User user, int sampleID, int folderID, DAOFactory factory, ContentProvider provider) throws SQLException, IOException, DataInputException, StorageAccessException, InsufficientPrivelegesException {
        Sample sample = new SampleUtil(factory).getSample(sampleID);
        if (type.equals(FREDConstants.ADOPTION)) {
            return new AdoptionRecordDE(user, sample, folderID, factory, provider);
        } else if (type.equals(FREDConstants.PALEONTOLOGICAL)) {
            return new PaleontologyRecordDE(user, sample, folderID, factory, provider);
        } else {
            throw new DataInputException("Feature Type", "Invalid");
        }
    }
}
