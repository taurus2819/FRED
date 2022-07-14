package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.db.util.SiteUtil.SiteException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.site.util.OrigCoordInfoUtil;
import nz.cri.gns.fred.site.util.SiteModel;
import nz.cri.gns.fred.site.util.SiteModelInput;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.SiteModelUtil;
import nz.cri.gns.munginator.Create;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.Modify;
import nz.cri.gns.munginator.Read;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.TemplateRowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.ImportColumn;
import nz.cri.gns.munginator.upload.stagingarea.Row;
import nz.cri.gns.munginator.upload.stagingarea.RowSingleValue;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;

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
    private final Map<Integer, Integer> rowToSampleId;

    public FredRowProcessor(User user, DAOFactory factory, String code, Map<Integer, Integer> rowToSampleId) {
        super(code);
        this.user = user;
        this.factory = factory;
        featureUtil = new FeatureUtil(factory);
        sampleUtil = new SampleUtil(factory);
        this.rowToSampleId = rowToSampleId;
    }

    @Override
    protected void verifyRow(Row row) throws SQLException, RowImportException {
        if ("FRED_OUTCROP".equals(spreadsheetType)) {
            findOrigSystemId(row, "ORIG_SYSTEM_ID");
        }
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
                update.set("FEATURE_ID$ORIG_COORD", getOrigCoords(row));
                update.set("FEATURE_ID$ORIG_SYSTEM_ID", findOrigSystemId(row, "ORIG_SYSTEM_ID"));
//                update.set("FEATURE_ID$COORD_COMMENTS", getRowValueString(row, "COORD_COMMENTS"));
                update.set("FEATURE_ID$LOCALITY", getRowValueString(row, "LOCALITY"));
                update.set("STRAT_UNIT", getRowValueString(row, "STRAT_UNIT"));
                update.set("COMPARATOR_USED", getRowValueString(row, "COMPARATOR_USED"));
                update.set("DEPOSITION_ENV", getRowValueString(row, "INFERRED_ENVIRONMENT"));
                update.set("COLUMN_MAP", getRowValueString(row, "MAP_SHEET"));
                {
                    try {
                        findOrCreateSite(row, update);
                    } catch (IOException ex) {
                        Logger.getLogger(FredRowProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

            case "VERTICAL_SECTION":
                update.set("FEATURE_ID$FEATURE_TYPE", "Vertical Section");
                update.set("STRAT_UNIT", getRowValueString(row, "STRAT_UNIT"));
                update.set("COMPARATOR_USED", getRowValueString(row, "COMPARATOR_USED"));
                update.set("DEPOSITION_ENV", getRowValueString(row, "INFERRED_ENVIRONMENT"));
                update.set("COLUMN_MAP", getRowValueString(row, "MAP_SHEET"));
                setFeatureId(row, update, featureName);
                break;
            case "DRILL_HOLE":
                update.set("FEATURE_ID$FEATURE_TYPE", "Drillhole");
                update.set("STRAT_UNIT", getRowValueString(row, "STRAT_UNIT"));
                update.set("COMPARATOR_USED", getRowValueString(row, "COMPARATOR_USED"));
                update.set("DEPOSITION_ENV", getRowValueString(row, "INFERRED_ENVIRONMENT"));
                update.set("COLUMN_MAP", getRowValueString(row, "MAP_SHEET"));
                setFeatureId(row, update, featureName);
                break;
            default:
                throw new MgException("Invalid spreadsheet type.");
        }
    }

    private void createAudit(Row row, Modify update) throws RowImportException {
        Integer folderId = findFolderId(row, "FOLDER");

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
        if (hasRowValue(row, "RECOLLECTION_OF")) {
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

    private Integer findFolderId(Row row, String code) throws RowImportException {
        String folderName = getRowValueString(row, code);

        try (Read s = schema.getTable("FOLDER").select()) {
            s.addColumn("FOLDER_ID");
            s.addWhere("NAME", folderName);
            s.doIt(importConn);
            if (!s.next()) {
                throw new RowImportException(row, "FOLDER", "This is not a valid Folder ID", null);
            }
            return s.getInteger("FOLDER_ID");
        } catch (SQLException e) {
            throw new RowImportException(row, "FOLDER", null, e);
        }
    }

    @Override
    protected void afterPerformRow(Row row, Modify update) throws RowImportException {
        rowToSampleId.put(row.getRowNum(), (Integer) update.get("SAMPLE_ID"));
        insertCollectors(row, update);
        insertStratRelationships(row, update);
        insertAdditionalFeatures(row, update);

        /**
         * These rows are processed in StratigraphicRelationshipRowProcessor.
         */
        for (String each : new String[]{
            "SAMPLE_RELATIONSHIP_DISTANCE",
            "SAMPLE_RELATIONSHIP_MOD",
            "SAMPLE_RELATIONSHIP_PREP",
            "SAMPLE_RELATIONSHIP_REFERENCE",
            "SAMPLES_NEARBY"}) {
            for (RowSingleValue rv : getRowMultiValue(row, each)) {
                if (null != rv) {
                    rv.markUsed();
                }
            }
        }
        row.checkAllValuesUsed();
    }

    private String findCountryId(Row row, String code) throws RowImportException {
        String name = getRowValueString(row, code);

        try (Read s = schema.getTable("LU_COUNTRY").select()) {
            s.addColumn("COUNTRY_CODE");
            s.addWhere("COUNTRY_NAME", name);
            s.doIt(importConn);
            if (!s.next()) {
                throw new RowImportException(row, code, "This is not a valid country", null);
            }
            return s.getString("COUNTRY_CODE");
        } catch (SQLException e) {
            throw new RowImportException(row, code, null, e);
        }
    }

    private Integer findOrigSystemId(Row row, String code) throws RowImportException {
        String name = getRowValueString(row, code);
        
        if (name == null){
            throw new RowImportException(row, code, "Must not be empty",null );
        }
        
        Datum datum = DatumFactory.createDatum(name);
        
        if (null == datum){
              throw new RowImportException(row, code, "This is not a valid system.", null);
        } else {
            return datum.getDatabaseId();
        }
    }

    private void findOrCreateSite(Row row, Modify update) throws RowImportException, IOException {
        List<String[]> error = new ArrayList<>(); // Used in some FRED APIs.

        // TODO: origCoords has a particular format.
//        String origCoords = getRowValueString(row, "NORTHING") + "|" + getRowValueString(row, "EASTING");
        String origCoords = null;
        String countryCode = findCountryId(row, "COUNTRY");
        Integer origSystemId = findOrigSystemId(row, "ORIG_SYSTEM_ID");

        printDebug("Searching for site... please wait...");
//        SiteRecord site = SiteUtil.findOrMakeSiteInstance(
//                error,
//                getRowValueString(row, "FEATURE_NAME"),
//                origSystemId,
//                origCoords,
//                null,
//                getRowValueString(row, "EASTING"),
//                getRowValueString(row, "NORTHING"),
//                getRowValueString(row, "LOCALITY"),
//                countryCode,
//                findMethod(row, "LOCATION_METHOD"),
//                toFloat(getRowValueDouble(row, "ACCURACY")),
//                getRowValueString(row, "MAP_SHEET"),
//                user
//        );

        switch(origSystemId){
            case 29: //lat|lng
            case 30:
            case 28:
            case 73:
//                this.origCoord = request.getParameter("North") + "|" + request.getParameter("East");
                origCoords = getRowValueString(row, "NORTHING") + "|" +  getRowValueString(row, "EASTING") ;
                System.out.println("OrigCoord = " + origCoords);
                break;
            case 33: //easting|northing
            case 38: //easting|northing
            case 7: //easting|northing
            case 67: //easting|northing
            case 68: //easting|northing
            case 70: //easting|northing
            case 71: //easting|northing
            case 74: //easting|northing
//                this.origCoord = request.getParameter("East") + "|" + request.getParameter("North"); 
                origCoords = getRowValueString(row, "EASTING") + "|" + getRowValueString(row, "NORTHING");
                System.out.println("OrigCoord = " + origCoords);
                break;            
            case 16:
            case 72:
            case 17:
            case 69:
//                this.origCoord = request.getParameter("MapSheet") + "|" + request.getParameter("East") + "|" + request.getParameter("North");
                origCoords = getRowValueString(row, "MAP_SHEET") + "|" + getRowValueString(row, "EASTING") + "|" + getRowValueString(row, "NORTHING");
                System.out.println("OrigCoord = " + origCoords);
                break;            
        }        
        
        String siteName = getRowValueString(row, "FEATURE_NAME");
        Integer methodID = findMethod(row, "LOCATION_METHOD");
        methodID = (methodID != null) ? methodID : 0;
        Double accuracy = getRowValueDouble(row, "ACCURACY");
        accuracy = (accuracy != null) ? accuracy : 0.0;
        String directions = (getRowValueString(row, "MAP_SHEET") != null ) ? (getRowValueString(row, "MAP_SHEET") + " - " + getRowValueString(row, "LOCALITY")) : (getRowValueString(row, "LOCALITY"));
        double height = -1;//site.getHeight();
        int heightMethodId = -1; //site.getHeightMethodId();
        double heightAccuracy = -1; //site.getHeightAccuracy();
        String comment = directions;   //getRowValueString(row, "COORD_COMMENTS");
        int ownerId = Math.toIntExact(user.getId());
        OrigCoordInfoUtil.OrigCoord epsgInfo = OrigCoordInfoUtil.getJson(origSystemId, origCoords);
        int epsg = epsgInfo.getEpsg();
        String gridref = epsgInfo.getGridref();
        Double easting  = epsgInfo.getEasting();
        Double northing = epsgInfo.getNorthing();
        String latitude = epsgInfo.getLatitude();
        String longitude = epsgInfo.getLongitude();
        String format = epsgInfo.getFormat();
        
        SiteModelInput smi = new SiteModelInput(siteName, methodID, accuracy, directions, height, heightMethodId, heightAccuracy, countryCode, comment, ownerId,
                epsg, gridref, easting, northing, latitude, longitude, format, "new site creation");
        SiteModel site = SiteModelUtil.getSite(smi);

        if (!error.isEmpty()) {
            for (String[] each : error) {
                log.log(Level.WARNING, Arrays.toString(each));
            }
            throw new RowImportException(row, "NORTHING", "Error creating the site: " + error.toString(), null);
        }

        /*TODO: should we check if there is a site in existance already
        if (!site.existsAlready()) {
            printDebug("Site does not exist; creating a new one.");
            try {
                site = SiteUtil.save(site); // Will fail if the site has an ID already. 
            } catch (SiteException ex) {
                log.log(Level.WARNING, "Site: " + site.toJson().toString(), ex);
                throw new RowImportException(row, "NORTHING", "Something failed. " + ex.getMessage(), null);
            }

            if (null == site || 0 > site.getId()) {
                throw new RowImportException(row, "NORTHING", "Creating a site using the site service has failed.", null);
            }
        } else {
            printDebug("Site already exists: \"" + site.getDirections() + "\". I will not update it.");
        }
        //TODO: should we check if there is a site in existance already */
        
        update.set("FEATURE_ID$SITE_ID", site.getSiteId());
    }

    private String getOrigCoords(Row row) throws RowImportException {
        return getRowValueString(row, "EASTING") + "|" + getRowValueString(row, "NORTHING");
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
    /**
     * The column SAMPLE_RELATIONSHIP_REFERENCE contains private void
     * matchUpSamples() throws MgException, SQLException { // TODO:
     * relationshipSamples is never populated??? Table relationshipTable =
     * schema.getTable("RELATIONSHIP"); for (Integer relationshipId :
     * relationshipSamples.keySet()) { Update relationship =
     * relationshipTable.update(); Integer sampleId =
     * findSampleId(relationshipSamples.get(relationshipId)); if (null ==
     * sampleId) { throw new MgException("I can't find a sample called \"" +
     * relationshipSamples.get(relationshipId)); } relationship.set("SAMPLE_ID",
     * sampleId); relationship.addWhere("RELATIONSHIP_ID", relationshipId);
     * relationship.doIt(importConn); } }
     *
     * /**
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
                    personId = findPersonId(row, each, () -> insertPerson(row, each));
                    Integer sampleId = (Integer) (update.get("SAMPLE_ID"));
                    associateCollector(sampleId, personId);
                }
            }
        } catch (SQLException e) {
            throw new RowImportException(row, "COLLECTOR_NAME", null, e);
        }
    }

    interface PersonNotFound {
        Integer op() throws RowImportException;
    }

    /** Do a database query to find that person by name. */
    private Integer findPersonId(Row row, RowSingleValue v, PersonNotFound notFound) throws RowImportException {
        if (null == v || v.isEmpty()) {
            return notFound.op();
        }
        String name = v.getValueString();

        try (Read s = schema.getTable("PERSON").select()) {
            s.addColumn("PERSON_ID");
            s.addWhere("NAME", name);
            s.addOrderBy("NAME", "ASC");
            s.doIt(importConn);
            if (!s.next()) {
                return notFound.op();
            }
            return s.getInteger("PERSON_ID");
        } catch (SQLException e) {
            throw new RowImportException(row, v, null, e);
        }
    }

    private Integer insertPerson(Row row, RowSingleValue v) throws RowImportException {
        String collectorName = v.getValueString();
        Create i = schema.getTable("PERSON").insert();
        i.set("NAME", collectorName);
        try {
            i.doIt(importConn);
        } catch (SQLException e) {
            throw new RowImportException(row, v, null, e);
        }
        return (Integer) i.get("PERSON_ID");
    }

    private void associateCollector(Integer sampleId, Integer personId) throws SQLException {
        Create ps = schema.getTable("COLLECTOR").insert();
        ps.set("SAMPLE_ID", sampleId);
        ps.set("PERSON_ID", personId);
        ps.doIt(importConn);
    }

    private boolean hasValue(List<RowSingleValue> mv, int i) {
        return (null != mv.get(i) && !mv.get(i).isEmpty());
    }

    private Integer stratPrepNameToId(Row r, RowSingleValue v) {
        if (null == v || v.isEmpty()) {
            return null;
        }
        switch (v.getValueString().trim().toLowerCase()) {
            case "above top":
                return 236;
            case "above base":
                return 237;
            case "below top":
                return 238;
            case "below base":
                return 239;
            default:
                return null;
        }
    }

    private void insertStratRelationships(Row row, Modify update) throws RowImportException {
        List<RowSingleValue> mod = getRowMultiValue(row, "STRAT_RELATIONSHIP_MOD"); // "c." or "?" or nothing.
        List<RowSingleValue> distance = getRowMultiValue(row, "STRAT_RELATIONSHIP_DISTANCE"); // metres, I assume.
        List<RowSingleValue> prep = getRowMultiValue(row, "STRAT_RELATIONSHIP_PREP"); // "above base" / ... etc
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
                relationship.set("RELATIONSHIP_TYPE", "Stratigraphic");
                relationship.set("SAMPLE_ID", (Integer) (update.get("SAMPLE_ID")));
                // I don't think this is correct. relationship.set("RELATED_FEATURE_ID", (Integer) (update.get("FEATURE_ID")));

                Integer prepId = stratPrepNameToId(row, prep.get(i));
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

                String stratUnitName = unit.get(i).getValueString();
                Integer stratUnitId = findStratUnitId(stratUnitName);

                if (null == stratUnitId) {
                    throw new RowImportException(row, "STRAT_RELATIONSHIP_STRAT_UNIT", "Could not find this stratigraphic unit.", null);
                }

                relationship.set("STRAT_UNIT", stratUnitName); // er what???
                relationship.set("STRAT_UNIT_ID", stratUnitId);
                relationship.doIt(importConn);
            }

        } catch (SQLException e) {
            throw new RowImportException(row, "STRAT_RELATIONSHIP_STRAT_UNIT", "Some error happened with the stratigraphic reference columns: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isMultiValue(int columnNum) {
        ImportColumn c = columns.get(columnNum);
        return isMultiValue(c) || super.isMultiValue(columnNum);
    }

    public static boolean isMultiValue(ImportColumn c) {
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
                case "ADDITIONAL_FEATURES":
                case "ABUNDANT":
                    return true;
            }
        }
        return false;
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
        } finally {
            su.close();
        }
        try {
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
        String lowerAge = getRowValueString(row, lowerCode);
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
        String upperAge = getRowValueString(row, upperCode);
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

    private void insertAdditionalFeatures(Row row, Modify update) throws RowImportException {
        List<RowSingleValue> additionalFeatures = getRowMultiValue(row, "ADDITIONAL_FEATURES");
        List<RowSingleValue> abundant = null;

        if (hasRowValue(row, "ABUNDANT")) {
            abundant = getRowMultiValue(row, "ABUNDANT");
        }

        Create c = null; // Created lazily to prevent warnings about it not being used (In Create>>finalize()).

        for (int i = 0; i < additionalFeatures.size(); i++) {
            RowSingleValue each = additionalFeatures.get(i);
            if (null != each && !each.isEmpty()) {
                if (null == c) { // Create c lazily.
                    c = schema.insert("SEDIMENTARY_FEATURE");
                    c.set("SAMPLE_ID", update.get("SAMPLE_ID"));
                }

                try {
                    c.set(importConn, "SED_FEATURE_ID$NAME", each.getValueString());
                } catch (SQLException e) {
                    throw new RowImportException(row, "ADDITIONAL_FEATURES", "Could not find this value.", e);
                }

                if (null != abundant) {
                    RowSingleValue ab = abundant.get(i);
                    if (null != ab && !ab.isEmpty()) {
                        c.set("ABUNDANT", ab.getValueString());
                    } else {
                        c.set("ABUNDANT", null);
                    }
                }

                try {
                    c.doIt(importConn);
                } catch (SQLException ex) {
                    throw new RowImportException(row, each, "Could not insert into SEDIMENTARY_FEATURE", ex);
                }
            }
        }
    }

    private Integer findMethod(Row row, String code) throws RowImportException {
        RowSingleValue v = getRowValue(row, code);
        if (null == v || v.isEmpty()) {
            return null;
        }

        try (Read r = schema.getTable("SC.METHOD").select()) {
            r.addColumn("METHOD_ID");
            r.addWhere("METHOD", v.getValueString());

            r.doIt(importConn);
            if (!r.next()) {
                throw new RowImportException(row, code, "Could not find this method", null);
            }
            return r.getInteger("METHOD_ID");
        } catch (SQLException e) {
            throw new RowImportException(row, code, "Could not find this method", e);
        }
    }
}
