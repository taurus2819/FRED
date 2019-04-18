package nz.cri.gns.fred.importer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.db.util.SiteUtil.SiteException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.SiteUtil;
import nz.cri.gns.munginator.Create;
import nz.cri.gns.munginator.Modify;
import nz.cri.gns.munginator.Read;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.TemplateRowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.ImportColumn;
import nz.cri.gns.munginator.upload.stagingarea.Row;
import nz.cri.gns.munginator.upload.stagingarea.RowSingleValue;

public class FredRowProcessor extends TemplateRowProcessor {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.import.FredOutcropRowProcessor");
    private User user;

    DAOFactory factory;
    FeatureUtil featureUtil;
    SampleUtil sampleUtil;

    public FredRowProcessor(User user, DAOFactory factory, String code) {
        super(code);
        this.user = user;
        this.factory = factory;
        featureUtil = new FeatureUtil(factory);
        sampleUtil = new SampleUtil(factory);
    }

    @Override
    protected void beforePopulateRow(Row row, Modify update) throws RowImportException {

    }

    @Override
    protected void afterPopulateRow(Row row, Modify update) throws RowImportException {
        String featureName = getRowValueNotNull(row, "FEATURE_NAME").getValueString();

        // TODO: use FeatureUtil.createFeature(). This replicates that:
        Integer folderId = idFromName(row, "FOLDER");
        update.set("FEATURE_ID$AUDIT_ID$WORKING_FOLDER_ID", folderId);
        // default audit status is already "working".
        update.set("FEATURE_ID$AUDIT_ID$CREATED_DATE", new Date()); // TODO: update the SQL to do this.
        update.set("FEATURE_ID$AUDIT_ID$CREATED_BY_ID", user.getId());

        switch (spreadsheetType) {
            case "FRED_OUTCROP":
                update.set("FEATURE_ID$FEATURE_TYPE", "Outcrop");
                update.set("FEATURE_ID$FEATURE_NAME", featureName);
                update.set("FEATURE_ID$FIELD_NUMBER", featureName);
                findOrCreateSite(row, update);
                break;
            case "VERTICAL_SECTION":
                update.set("FEATURE_ID$FEATURE_TYPE", "Vertical Section");
                setFeatureId(row, update, featureName);
                break;
            case "DRILL_HOLE":
                update.set("FEATURE_ID$FEATURE_TYPE", "Drillhole");
                setFeatureId(row, update, featureName);
                break;
        }
    }

    @Override
    protected void afterPerformRow(Row row, Modify update) throws RowImportException {
        insertCollectors(row, update);
        insertSamplesNearby(row, update);
        insertSampleRelationships(row, update);
        insertStratRelationships(row, update);
    }

    private void findOrCreateSite(Row row, Modify update) throws RowImportException {
        List<String[]> error = new ArrayList<>(); // Used in some FRED APIs.

        // TODO: origCoords has a particular format.
        String origCoords = getRowValueString(row, "NORTHING") + "|" + getRowValueString(row, "EASTING");
        String datumCode = getDatumCode(row);
        String countryCode = idAsStringFromName(row, "COUNTRY");

        log("Searching for site... please wait...");
        SiteRecord site = SiteUtil.findOrMakeSiteInstance(
                error,
                getRowValueString(row, "FEATURE_NAME"),
                getRowValueInteger(row, "ORIG_SYSTEM_ID"), // TODO: should be a lookup value.
                origCoords,
                datumCode,
                getRowValueString(row, "EASTING"),
                getRowValueString(row, "NORTHING"),
                getRowValueString(row, "LOCALITY"),
                countryCode,
                getRowValueInteger(row, "LOCATION_METHOD"),
                toFloat(getRowValueDouble(row, "ACCURACY")),
                getRowValueString(row, "MAP_SHEET"),
                user
        );

        if (!error.isEmpty()) {
            for (String[] each : error) {
                log.log(Level.WARNING, Arrays.toString(each));
            }
            throw new RowImportException(row, "NORTHING", "Error creating the site: " + error.toString(), null);
        }

        if (!site.existsAlready()) {
            log("Site does not exist; creating a new one.");
            try {
                site = SiteUtil.save(site); // Will fail if the site has an ID already. 
            } catch (SiteException ex) {
                log.log(Level.WARNING, null, ex);
                throw new RowImportException(row, "NORTHING", "Something failed. " + ex.getMessage(), null);
            }

            if (null == site || 0 > site.getId()) {
                throw new RowImportException(row, "NORTHING", "Creating a site using the site service has failed.", null);
            }
        } else {
            log("Site already exists: \"" + site.getDirections() + "\". I will not update it.");
        }

        update.set("FEATURE_ID$SITE_ID", site.getId());
    }

    private String getDatumCode(Row row) throws RowImportException {
        String datumCode;
        Connection conn = null;
        try {
            conn = FREDUtil.getConnection();
            Integer datumId = idFromName(row, "ORIG_SYSTEM_ID");
            if (null == datumId) {
                throw new RowImportException(row, "ORIG_SYSTEM_ID", "This cell needs a value.", null);
            }
            Read s = schema.select("LU_COORD_SYSTEM");
            s.addColumn("CODE");
            s.addWhere("ORIG_SYSTEM_ID", datumId);
            try {
                s.doIt(conn);
                if (!s.next()) {
                    throw new RowImportException(row, "ORIG_SYSTEM_ID", "Datum not in the lookup table.", null);
                }
                datumCode = s.getString("CODE");
            } finally {
                s.close();
            }

        } catch (SQLException | NamingException e) {
            throw new RowImportException(row, "ORIG_SYSTEM_ID", "Could not get datum", e);
        } finally {
            try {
                if (null != conn) {
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
        return datumCode;
    }

    private Float toFloat(Double d) {
        if (null == d) {
            return null;
        } else {
            return d.floatValue();
        }

    }

    @Override
    public void close() {
        if (null != factory) {
            try {
                factory.closeSession();
            } catch (StorageAccessException ex) {

            }
        }
    }

    /**
     * Find the existing feature with the given name.
     */
    private void setFeatureId(Row row, Modify update, String featureName) throws RowImportException {
        Feature f = null;
        try {
            f = featureUtil.getFeatureWithIdentifyingName(featureName);
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, "FEATURE_NAME", "An error occurred while trying to find a feature.", ex);
        }
        if (null == f) {
            throw new RowImportException(row, "FEATURE_NAME", "Could not find this feature.", null);
        }
        update.set("FEATURE_ID", f.getFeatureId());
    }

    private void insertCollectors(Row row, Modify update) throws RowImportException {
        try {
            List<RowSingleValue> collectors = getRowMultiValue(row, "COLLECTOR_NAME");
            for (RowSingleValue each : collectors) {
                if (null != each) {
                    Integer personId;
                    if (each.isEmpty() || each.getValueString().indexOf("/") < 1) {
                        personId = insertPerson(each.getValueString());
                        if (null == personId) {
                            throw new NullPointerException("");
                        }
                    } else {
                        personId = idFromName(row, each);
                        if (null == personId) {
                            throw new NullPointerException("");
                        }
                    }
                    Integer sampleId = (Integer) (update.get("SAMPLE_ID"));
                    associateCollector(sampleId, personId);
                }
            }
        } catch (SQLException e) {
            throw new RowImportException(row, "COLLECTOR_NAME", null, e);
        }
    }

    private Integer findPerson(String collectorName) throws SQLException {
        Read s = schema.getTable("PERSON").select();
        s.addWhere("NAME", collectorName);
        try {
            s.doIt(importConn);
            if (!s.next()) {
                return null;
            } else {
                return (Integer) s.get("PERSON_ID");
            }
        } finally {
            s.close();
        }
    }

    private Integer insertPerson(String collectorName) throws SQLException {
        Create i = schema.getTable("PERSON").insert();
        i.set("NAME", collectorName);
        i.doIt(importConn);
        return (Integer) i.get("PERSON_ID");
    }

    private void associateCollector(Integer sampleId, Integer personId) throws SQLException {
        Create ps = schema.getTable("COLLECTOR").insert();
        ps.set("SAMPLE_ID", sampleId);
        ps.set("PERSON_ID", personId);
        ps.doIt(importConn);
    }

    private void insertSamplesNearby(Row row, Modify update) throws RowImportException {
        warn("Discarding 'Samples Nearby' because I don't know what to do with it (TODO).");
    }

    private void insertSampleRelationships(Row row, Modify update) throws RowImportException {
        List<RowSingleValue> mod = getRowMultiValue(row, "SAMPLE_RELATIONSHIP_MOD"); // "c." or "?" or nothing.
        List<RowSingleValue> distance = getRowMultiValue(row, "SAMPLE_RELATIONSHIP_DISTANCE"); // metres, I assume.
        List<RowSingleValue> prep = getRowMultiValue(row, "SAMPLE_RELATIONSHIP_PREP"); // above / below
        List<RowSingleValue> ref = getRowMultiValue(row, "SAMPLE_RELATIONSHIP_REFERENCE"); // sample names, possibly in this spreadsheet.

        try {
            Create relationship = schema.insert("RELATIONSHIP");
            relationship.set("RELATIONSHIP_TYPE", "Sample");
            relationship.set("SAMPLE_ID", (Integer) (update.get("SAMPLE_ID")));

            for (int i = 0; i < ref.size(); i++) {
                boolean mdpHasValue = hasValue(mod, i) || hasValue(distance, i) || hasValue(prep, i);
                
                // If nothing has a value here...
                if (!(hasValue(ref, i) || mdpHasValue )) {
                    continue;
                }

                // If ref is missing a value...
                if (mdpHasValue && !hasValue(ref, i)) {
                    throw new RowImportException(row, "SAMPLE_RELATIONSHIP_REFERENCE", "A mod, distance or prep here requires a reference.", null);
                }

                int prepId = idFromName(row, prep.get(i));
                relationship.set("RELATION_TYPE_ID", prepId); // "above" or "below".
                relationship.set("DISTANCE_MOD", mod.get(i).getValueInteger());
                relationship.set("DISTANCE_RANGE", distance.get(i).getValueDouble());

                Integer sampleId = findSampleId(ref.get(i).getValueString());
                if (null == sampleId) {
                    throw new RowImportException(row, "SAMPLE_RELATIONSHIP_REFERENCE", "I can't find this sample!", null);
                }
                relationship.set("SAMPLE_ID", sampleId);
                relationship.doIt(importConn);

            }

        } catch (SQLException e) {
            throw new RowImportException(row, "SAMPLE_RELATIONSHIP_REFERENCE", "Some error happened with the sample reference columns.", e);
        }
    }

    private boolean hasValue(List<RowSingleValue> mv, int i) {
        return (null != mv.get(i) && !mv.get(i).isEmpty());
    }

    private void insertStratRelationships(Row row, Modify update) throws RowImportException {
        List<RowSingleValue> mod = getRowMultiValue(row, "STRAT_RELATIONSHIP_MOD"); // "c." or "?" or nothing.
        List<RowSingleValue> distance = getRowMultiValue(row, "STRAT_RELATIONSHIP_DISTANCE"); // metres, I assume.
        List<RowSingleValue> prep = getRowMultiValue(row, "STRAT_RELATIONSHIP_PREP"); // above / below
        List<RowSingleValue> unit = getRowMultiValue(row, "STRAT_RELATIONSHIP_STRAT_UNIT"); // sample names, possibly in this spreadsheet.

        if (unit.isEmpty()) {
            return;
        }

        try {
            Create relationship = schema.insert("RELATIONSHIP");
            relationship.set("RELATIONSHIP_TYPE", "Sample");
            relationship.set("SAMPLE_ID", (Integer) (update.get("SAMPLE_ID")));

            for (int i = 0; i < unit.size(); i++) {
                boolean mdpHasValue = hasValue(mod, i) || hasValue(distance, i) || hasValue(prep, i);
                
                // If nothing has a value here...
                if (!(hasValue(unit, i) || mdpHasValue )) {
                    continue;
                }

                // If ref is missing a value...
                if (mdpHasValue && !hasValue(unit, i)) {
                    throw new RowImportException(row, "STRAT_RELATIONSHIP_STRAT_UNIT", "A mod, distance or prep here requires a reference.", null);
                }
                
                int prepId = idFromName(row, prep.get(i));
                relationship.set("RELATION_TYPE_ID", prepId); // "above" or "below".
                relationship.set("DISTANCE_MOD", mod.get(i).getValueInteger());
                relationship.set("DISTANCE_RANGE", distance.get(i).getValueDouble());

                String stratUnit = nameFromName(row, unit.get(i));
                Integer stratUnitId = findStratUnitId(stratUnit);
                relationship.set("STRAT_UNIT", stratUnit);
                relationship.set("STRAT_UNIT_ID", stratUnitId);
                relationship.doIt(importConn);
            }

        } catch (SQLException e) {
            throw new RowImportException(row, "STRAT_RELATIONSHIP_STRAT_UNIT", "Some error happened with the stratigraphic reference columns.", e);
        }
    }

    @Override
    public boolean isMultiValue(int columnNum) {
        ImportColumn c = columns.get(columnNum);
        if (null != c.getCode()) {
            switch (c.getCode()) {
                case "SAMPLES_NEARBY":
                case "SAMPLE_RELATIONSHIP_MOD":
                case "SAMPLE_RELATIONSHIP_DISTANCE":
                case "SAMPLE_RELATIONSHIP_PREP":
                case "SAMPLE_RELATIONSHIP_REFERENCE":
                case "STRAT_RELATIONSHIP_MOD":
                case "STRAT_RELATIONSHIP_DISTANCE":
                case "STRAT_RELATIONSHIP_PREP":
                case "STRAT_RELATIONSHIP_STRAT_UNIT":
                    return true;
            }
        }
        return super.isMultiValue(columnNum);
    }

    /**
     * Find the sample with the given FR_NUMBER (i.e. name), or null if it can't
     * be found.
     */
    private Integer findSampleId(String name) throws SQLException {
        Read s = schema.select("SAMPLE");
        s.addColumn("SAMPLE_ID");
        s.addWhere("FR_ID$FR_NUMBER", name.trim());
        try {
            s.doIt(importConn);
            if (!s.next()) {
                // Not found; return null.
                return null;
            }
            return (Integer) s.get("SAMPLE_ID");
        } finally {
            s.close();
        }
    }

    private Integer findStratUnitId(String name) throws SQLException {
        // TODO: probably use an online service?
        Read su = schema.select("SL.STRAT_UNIT");
        // TODO: it would be really good to do a WHERE LOWER(X)=LOWER(Y) here.
        su.addColumn("SU_ID");
        su.addWhere("SU_NAME_STANDARD", name);
        try {
            su.doIt(importConn);
            if (su.next()) {
                return (Integer) su.get("SU_ID");
            }

            // Not found. Try SU_NAME instead.
            su = schema.select("SL.STRAT_UNIT");
            su.addColumn("SU_ID");
            su.addWhere("SU_NAME", name);
            su.doIt(importConn);
            if (su.next()) {
                return (Integer) su.get("SU_ID");
            }
            return null;
        } finally {
            su.close();
        }
    }
}
