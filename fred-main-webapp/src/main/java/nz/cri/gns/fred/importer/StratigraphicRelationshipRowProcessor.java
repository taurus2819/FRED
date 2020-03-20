package nz.cri.gns.fred.importer;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import nz.cri.gns.munginator.Create;
import nz.cri.gns.munginator.Read;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.ImportColumn;
import nz.cri.gns.munginator.upload.stagingarea.Row;
import nz.cri.gns.munginator.upload.stagingarea.RowSingleValue;

/**
 * Insert stuff into the RELATIONSHIP table. TODO: rename this class to make it
 * match reality.
 *
 * @author mikevdg
 */
public class StratigraphicRelationshipRowProcessor extends RowProcessor {

    private final Map<Integer, Integer> rowToSampleId;

    public StratigraphicRelationshipRowProcessor(String spreadsheetType, Map<Integer, Integer> rowToSampleId) {
        super(spreadsheetType);
        this.rowToSampleId = rowToSampleId;
    }

    @Override
    protected void importRow(Row row) throws SQLException, RowImportException {
        insertSamplesNearby(row);
        insertSampleRelationships(row);
    }

    /**
     * For the "Samples nearby" and "Sample Relationships" fields. Find the
     * SAMPLE_ID that is named with the given column value.
     *
     * These fields need to be worked with here:
     *
     * SAMPLES_NEARBY: insert into relationship with type="nearby"
     *
     * SAMPLE_RELATIONSHIP_MOD SAMPLE_RELATIONSHIP_DISTANCE
     * SAMPLE_RELATIONSHIP_PREP SAMPLE_RELATIONSHIP_REFERENCE - convert to
     * SAMPLE_ID
     *
     * The stratigraphic relationships are handled by the FredRowProcessor as
     * they don't refer to other samples.
     *
     * The cell value can be: * An existing Fossil Record Number (FRN)
     * (FR_NUMBER.FR_NUMBER) * Another sample from the current spreadsheet
     * referenced by field number (FEATURE.FIELD_NUMBER, see also FR_ID)
     *
     * FRNs are of the format [map sheet]/f[serial number]
     *
     * The serial number should be four digits. Users might enter fewer digits;
     * this should be padded to four digits with zeros.
     */
    private void insertSamplesNearby(Row row) throws RowImportException {
        List<RowSingleValue> nearby = getRowMultiValue(row, "SAMPLES_NEARBY");
        Integer fromSampleId;

        fromSampleId = rowToSampleId.get(row.getRowNum());

        Create c = schema.insert("RELATIONSHIP");
        c.set("SAMPLE_ID", fromSampleId);
        c.set("RELATIONSHIP_TYPE", "Sample");
        c.set("RELATION_TYPE_ID", 231); // "nearby"

        for (RowSingleValue each : nearby) {
            if (null == each || each.isEmpty()) {
                continue;
            }

            Integer toFeatureId;
            String name = each.getValueString();
            toFeatureId = findFeatureId(row, name, "SAMPLES_NEARBY");

            c.set("RELATED_FEATURE_ID", toFeatureId);
            try {
                c.doIt(importConn);
            } catch (SQLException e) {
                throw new RowImportException(row, "SAMPLES_NEARBY", null, e);
            }
        }
    }

    private Integer findFeatureId(Row row, String name, String columnName) throws RowImportException {
        Read r = schema.select("FEATURE");
        r.addColumn("FEATURE_ID");
        r.addWhere("FR_ID$FR_NUMBER", name);
        try {
            r.doIt(importConn);
            if (r.next()) {
                return r.getInteger("FEATURE_ID");
            } else {
                throw new RowImportException(row, columnName, "Could not find a feature with this name.", null);
            }

        } catch (SQLException e) {
            throw new RowImportException(row, columnName, null, e);
        } finally {
            r.close();
        }
    }

    private Integer samplePrepToId(Row row, RowSingleValue v) {
        if (null==v || v.isEmpty()) {
            return null;
        }
        switch (v.getValueString().trim().toLowerCase()) {
            case "above":
                return 232;
            case "below":
                return 233;
            default:
                return null;
        }
    }

    private void insertSampleRelationships(Row row) throws RowImportException {
        List<RowSingleValue> mod = getRowMultiValue(row, "SAMPLE_RELATIONSHIP_MOD"); // "c." or "?" or nothing.
        List<RowSingleValue> distance = getRowMultiValue(row, "SAMPLE_RELATIONSHIP_DISTANCE"); // metres, I assume.
        List<RowSingleValue> prep = getRowMultiValue(row, "SAMPLE_RELATIONSHIP_PREP"); // above / below
        List<RowSingleValue> ref = getRowMultiValue(row, "SAMPLE_RELATIONSHIP_REFERENCE"); // sample names, possibly in this spreadsheet.

        try {
            for (int i = 0; i < ref.size(); i++) {
                boolean mdpHasValue = hasValue(mod, i) || hasValue(distance, i) || hasValue(prep, i);

                // If nothing has a value here...
                if (!(hasValue(ref, i) || mdpHasValue)) {
                    continue;
                }

                // If ref is missing a value...
                if (mdpHasValue && !hasValue(ref, i)) {
                    throw new RowImportException(row, "SAMPLE_RELATIONSHIP_REFERENCE", "A mod, distance or prep here requires a reference.", null);
                }

                if (!hasValue(prep, i)) {
                    throw new RowImportException(row, "SAMPLE_RELATIONSHIP_PREP", "Prep must have a value.", null);
                }

                String name = ref.get(i).getValueString();
                Integer toFeatureId = findFeatureId(row, name, "SAMPLE_RELATIONSHIP_REFERENCE");

                Create relationship = schema.insert("RELATIONSHIP");
                relationship.set("RELATIONSHIP_TYPE", "Sample");
                relationship.set("SAMPLE_ID", rowToSampleId.get(row.getRowNum()));
                relationship.set("RELATED_FEATURE_ID", toFeatureId);

                int prepId = samplePrepToId(row, prep.get(i));
                relationship.set("RELATION_TYPE_ID", prepId); // "above" or "below".
                if (hasValue(mod, i)) {
                    relationship.set("DISTANCE_MOD", mod.get(i).getValueInteger());
                } else {
                    relationship.set("DISTANCE_MOD", null);
                }
                if (hasValue(distance, i)) {
                    relationship.set("DISTANCE_RANGE", distance.get(i).getValueDouble());
                } else {
                    relationship.set("DISTANCE_RANGE", null);
                }
                relationship.doIt(importConn);
            }

        } catch (SQLException e) {
            throw new RowImportException(row, "SAMPLE_RELATIONSHIP_REFERENCE", "Some error happened with the sample reference columns.", e);
        }
    }

    private boolean hasValue(List<RowSingleValue> mv, int i) {
        return (null != mv.get(i) && !mv.get(i).isEmpty());
    }

    @Override
    public boolean isMultiValue(int columnNum) {
        ImportColumn c = columns.get(columnNum);
        return FredRowProcessor.isMultiValue(c) || super.isMultiValue(columnNum);
    }

}
