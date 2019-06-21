package nz.cri.gns.fred.importer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.Modify;
import nz.cri.gns.munginator.Read;
import nz.cri.gns.munginator.Table;
import nz.cri.gns.munginator.Update;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.TemplateRowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.ImportColumn;
import nz.cri.gns.munginator.upload.stagingarea.Row;
import nz.cri.gns.munginator.upload.stagingarea.RowSingleValue;

/**
 * I am the importer for the Outcrop, Vertical Section and Drillhole
 * spreadsheets.
 */
public class FredRowProcessor extends TemplateRowProcessor {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.import.FredOutcropRowProcessor");
    private User user;

    DAOFactory factory;
    FeatureUtil featureUtil;
    SampleUtil sampleUtil;

    private Map<Integer, String> relationshipSamples; // Mapping RELATIONSHIP_ID to sample names. 

    public FredRowProcessor(User user, DAOFactory factory, String code) {
        super(code);
        this.user = user;
        this.factory = factory;
        featureUtil = new FeatureUtil(factory);
        sampleUtil = new SampleUtil(factory);
        relationshipSamples = new HashMap<>();
    }

    @Override
    protected void verifyRow(Row row) throws SQLException, RowImportException {
        checkDatumCode(row);
        super.verifyRow(row);
    }

    
    
    @Override
    protected void beforePopulateRow(Row row, Modify update) throws RowImportException {
    }

    @Override
    protected void afterPopulateRow(Row row, Modify update) throws RowImportException {
        String featureName = getRowValueNotNull(row, "FEATURE_NAME").getValueString();

        // TODO: use FeatureUtil.createFeature(). This replicates that but in a rollbackable transaction:
        createAudit(row, update);
        insertStages(row, update);

        // The FR_NUMBER and MASTERSHEET are populated during the approval process.
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
            default:
                throw new MgException("Invalid spreadsheet type.");
        }
    }

    private void createAudit(Row row, Modify update) throws RowImportException {
        Integer folderId = idFromName(row, "FOLDER");

        Create c = schema.insert("AUDIT_TABLE");
        c.set("WORKING_FOLDER_ID", folderId);
        // default audit status is already "working".
        c.set("CREATED_DATE", new Date()); // TODO: should be in the DDL.
        c.set("CREATED_BY_ID", user.getId());
        c.set("DATA_ORIGIN_ID", 909); // Excel template.
        String workingComments = null;
        String recollectionOf = null;
        if (hasRowValue(row, "WORKING_COMMENTS")) {
            workingComments = getRowValueString(row, "WORKING_COMMENTS");
        }
        if (hasRowValue(row, "RECOLLECTION_OF"))  {
            recollectionOf = getRowValueString(row, "RECOLLECTION_OF");
        }
        if (null != workingComments || null != recollectionOf) {
            c.set("WORKING_COMMENTS", FeatureUtil.combineWorkingComments(recollectionOf, workingComments));
        }

        try {
            c.doIt(importConn);
        } catch (SQLException ex) {
            throw new RowImportException(row, "FOLDER", "Could not create AUDIT entry.", ex);
        }
        Integer auditId = (Integer) c.get("AUDIT_ID");

        update.set("AUDIT_ID", auditId);
        update.set("FEATURE_ID$AUDIT_ID", auditId);
    }

    @Override
    protected void afterPerformRow(Row row, Modify update) throws RowImportException {
        insertCollectors(row, update);
        insertSampleRelationships(row, update);
        insertStratRelationships(row, update);
    }

    private void findOrCreateSite(Row row, Modify update) throws RowImportException {
        List<String[]> error = new ArrayList<>(); // Used in some FRED APIs.

        // TODO: origCoords has a particular format.
        String origCoords = getRowValueString(row, "NORTHING") + "|" + getRowValueString(row, "EASTING");
        String countryCode = idAsStringFromName(row, "COUNTRY");

        log("Searching for site... please wait...");
        SiteRecord site = SiteUtil.findOrMakeSiteInstance(
                error,
                getRowValueString(row, "FEATURE_NAME"),
                idFromName(row, "ORIG_SYSTEM_ID"),
                origCoords,
                null,
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

    private void checkDatumCode(Row row) throws RowImportException {
        Connection conn = null;
        try {
            conn = FREDUtil.getConnection();
            Integer datumId = idFromName(row, "ORIG_SYSTEM_ID");
            if (null == datumId) {
                throw new RowImportException(row, "ORIG_SYSTEM_ID", "This cell needs a value.", null);
            }
            Read s = schema.select("SC.ORIG_SYSTEM");
            s.addColumn("SYSTEM_CODE");
            s.addWhere("SYSTEM_ID", datumId);
            try {
                s.doIt(conn);
                if (!s.next()) {
                    throw new RowImportException(row, "ORIG_SYSTEM_ID", "Datum not in the lookup table.", null);
                }
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
    }

    private Float toFloat(Double d) {
        if (null == d) {
            return null;
        } else {
            return d.floatValue();
        }

    }

    /* TODO
    @Override
    public void close() {
        try {
            matchUpSamples();
        } catch (SQLException ex) {
            throw new MgException(ex);
        }
        if (null != factory) {
            try {
                factory.closeSession();
            } catch (StorageAccessException ex) {

            }
        }
    }*/

    
    /** The column SAMPLE_RELATIONSHIP_REFERENCE contains 
    private void matchUpSamples() throws MgException, SQLException {
        // TODO: relationshipSamples is never populated???
        Table relationshipTable = schema.getTable("RELATIONSHIP");
        for (Integer relationshipId : relationshipSamples.keySet()) {
            Update relationship = relationshipTable.update();
            Integer sampleId = findSampleId(relationshipSamples.get(relationshipId));
            if (null == sampleId) {
                throw new MgException("I can't find a sample called \"" + relationshipSamples.get(relationshipId));
            }
            relationship.set("SAMPLE_ID", sampleId);
            relationship.addWhere("RELATIONSHIP_ID", relationshipId);
            relationship.doIt(importConn);
        }
    }

    /**
     * Find the sample with the given FR_NUMBER (i.e. name), or null if it can't
     * be found.
     */
    private Integer findSampleId(String name) throws SQLException {
        // TODO: only used from matchUpSamples(), which never iterates.
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

    private void insertSampleRelationships(Row row, Modify update) throws RowImportException {
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

                Create relationship = schema.insert("RELATIONSHIP");
                relationship.set("RELATIONSHIP_TYPE", "Sample");
                relationship.set("SAMPLE_ID", (Integer) (update.get("SAMPLE_ID")));
                relationship.set("RELATED_FEATURE_ID", (Integer) (update.get("FEATURE_ID")));

                int prepId = idFromName(row, prep.get(i));
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
                // The SAMPLE_ID will be set later after all rows are imported.
                // TODO this has not been done.

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

            for (int i = 0; i < unit.size(); i++) {
                boolean mdpHasValue = hasValue(mod, i) || hasValue(distance, i) || hasValue(prep, i);

                // If nothing has a value here...
                if (!(hasValue(unit, i) || mdpHasValue)) {
                    continue;
                }

                // If ref is missing a value...
                if (mdpHasValue && !hasValue(unit, i)) {
                    throw new RowImportException(row, "STRAT_RELATIONSHIP_STRAT_UNIT", "A mod, distance or prep here requires a reference.", null);
                }

                if (!hasValue(prep, i)) {
                    throw new RowImportException(row, "STRAT_RELATIONSHIP_PREP", "Prep must have a value (the missing value is on this row or a following row).", null);
                }

                Create relationship = schema.insert("RELATIONSHIP");
                relationship.set("RELATIONSHIP_TYPE", "Sample");
                relationship.set("SAMPLE_ID", (Integer) (update.get("SAMPLE_ID")));
                relationship.set("RELATED_FEATURE_ID", (Integer) (update.get("FEATURE_ID")));

                Integer prepId = idFromName(row, prep.get(i));
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

    private void insertStages(Row row, Modify m) throws RowImportException {
        /* We need to do this manulally. The magic doesn't work here as it sets all the stage columns
        to the same value as they have the same destination table.
         */
        Integer knownId = insertStage(row, "KNOWN_STAGE_LOWER", "KNOWN_STAGE_UPPER");
        m.set("KNOWN_STAGE_ID", knownId);

        Integer inferredId = insertStage(row, "INFERRED_STAGE_LOWER", "INFERRED_STAGE_UPPER");
        m.set("INFERRED_STAGE_ID", inferredId);
    }

    private Integer insertStage(Row row, String lowerCode, String upperCode) throws RowImportException {
        Integer lowerAgeId = null;
        String lowerAge = nameFromName(row, lowerCode);
        if (null != lowerAge) {
            try {
                lowerAgeId = findAge(lowerAge);
            } catch (SQLException ex) {
                throw new RowImportException(row, lowerCode, null, ex);
            }
            if (null == lowerAgeId) {
                throw new RowImportException(row, lowerCode, "Cannot find this age", null);
            }
        }

        Integer upperAgeId = null;
        String upperAge = nameFromName(row, upperCode);
        if (null != upperAge) {
            try {
                upperAgeId = findAge(upperAge);
            } catch (SQLException ex) {
                throw new RowImportException(row, lowerCode, null, ex);
            }
            if (null == upperAgeId) {
                throw new RowImportException(row, upperCode, "Cannot find this age", null);
            }
        }

        if (null != lowerAge || null != upperAge) {
            Create u = schema.getTable("STAGE").insert();
            u.set("AGE_LOWER_ID", lowerAgeId);
            u.set("AGE_UPPER_ID", upperAgeId);
            try {
                u.doIt(importConn);
            } catch (SQLException ex) {
                throw new RowImportException(row, "KNOWN_STAGE_LOWER", "Could not create an entry in the STAGE table.", ex);
            }
            return (Integer) u.get("STAGE_ID");
        } else {
            return null;
        }
    }

    private Integer findAge(String ageName) throws SQLException {
        Read r = schema.getTable("AGE").select();
        r.addColumn("AGE_ID");
        r.addWhere("NAME", ageName);
        r.addWhere("OBSOLETE_FLAG", 0);
        r.addWhere("DUPLICATE_FLAG", 0);
        try {
            r.doIt(importConn);
            if (!r.next()) {
                return null;
            }
            return r.getInteger("AGE_ID");
        } finally {
            r.close();
        }
    }

}
