package nz.cri.gns.fred.importer;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.Row;
import nz.cri.gns.munginator.upload.stagingarea.RowSingleValue;

public class StratigraphicRelationshipRowProcessor extends RowProcessor {

    private User user;

    DAOFactory factory;
    FeatureUtil featureUtil;
    SampleUtil sampleUtil;
    Set<Feature> commitUs;

    public StratigraphicRelationshipRowProcessor(String spreadsheetType, User user, DAOFactory factory) {
        super(spreadsheetType);
        this.user = user;
        this.factory = factory;
        featureUtil = new FeatureUtil(factory);
        sampleUtil = new SampleUtil(factory);
        commitUs = new HashSet<>();
    }

    @Override
    protected void importRow(Row row) throws SQLException, RowImportException {
        setFeatureByName(row);
    }

    /**
     * For the "Samples nearby" and "Sample Relationships" fields. Find the
     * SAMPLE_ID that is named with the given column value.
     *
     * The cell value can be: An existing Fossil Record Number (FRN)
     * (FR_NUMBER.FR_NUMBER) Another sample from the current spreadsheet
     * referenced by field number (FEATURE.FIELD_NUMBER, see also FR_ID)
     *
     * FRNs are of the format [map sheet]/f[serial number]
     *
     * The serial number should be four digits. Users might enter fewer digits;
     * this should be padded to four digits with zeros.
     */
    private void setFeatureByName(Row row) throws RowImportException {
        if (!hasRowValue(row, "SAMPLES_NEARBY")) {
            return;
        }

        String fromName = getRowValueString(row, "FEATURE_NAME");
        String toName = getRowValueString(row, "SAMPLES_NEARBY");
        Feature from = null;
        Feature to = null;

        try {
            from = featureUtil.getFeatureWithIdentifyingName(fromName);
            to = featureUtil.getFeatureWithIdentifyingName(toName);
            commitUs.add(from);

            if (null == to) {
                throw new RowImportException(row, "SAMPLES_NEARBY", "Cannot find a feature with this FRN: ", null);
            }

            Sample fromSample = first(from.getSamples());

            Relationship r = sampleUtil.createRelationship(fromSample, to, FREDConstants.SAMPLE, FREDConstants.NEARBY);
            from.getRelationships().add(r);
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, "SAMPLES_NEARBY", null, ex);

        }
        /* TODO: SAMPLES_NEARBY should be multi-value. 
        It seems that getRowMultiValue() fails with a classcastexception. I need to investigate this further.
        
        String fromName = getRowValueString(row, "FEATURE_NAME");
        Feature from = null;

        try {
            from = featureUtil.getFeatureWithIdentifyingName(fromName);
            commitUs.add(from);
            List<RowSingleValue> tos = getRowMultiValue(row, "SAMPLES_NEARBY");

            for (RowSingleValue toRV : tos) {
                String toName = toRV.getValueString();
                Feature to = featureUtil.getFeatureWithIdentifyingName(toName);
                if (null == to) {
                    throw new RowImportException(row, "SAMPLES_NEARBY", "Cannot find a feature with this FRN: ", null);
                }

                Sample fromSample = first(from.getSamples());

                Relationship r = sampleUtil.createRelationship(fromSample, to, FREDConstants.SAMPLE, FREDConstants.NEARBY);
                from.getRelationships().add(r);
            }
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, "SAMPLES_NEARBY", null, ex);
        }
         */
    }

    @Override
    public void close() {
    }

    @Override
    public void commit() {
        try {
            for (Feature each : commitUs) {
                factory.getFredDAO().saveOrUpdate(each);
            }
        } catch (StorageAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: rollback(). FredDAO does not support this yet.
    private Sample first(Set<Sample> in) {
        if (null == in || in.isEmpty()) {
            return null;
        } else {
            return in.iterator().next();
        }
    }

}
