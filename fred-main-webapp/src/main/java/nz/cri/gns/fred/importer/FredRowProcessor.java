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
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.SiteUtil;
import nz.cri.gns.munginator.Modify;
import nz.cri.gns.munginator.Read;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.TemplateRowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.Row;

public class FredRowProcessor extends TemplateRowProcessor {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.import.FredOutcropRowProcessor");
    private User user;

    DAOFactory factory;
    FeatureUtil featureUtil;
    SampleUtil sampleUtil;

    public FredRowProcessor(User user, DAOFactory factory) {
        super("FRED_OUTCROP");
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

        update.set("FEATURE_ID$FEATURE_TYPE", "Outcrop");

        // TODO: use FeatureUtil.createFeature(). This replicates that:
        Integer folderId = idFromName(row, "FOLDER");
        update.set("FEATURE_ID$AUDIT_ID$WORKING_FOLDER_ID", folderId);
        // default audit status is already "working".
        update.set("FEATURE_ID$AUDIT_ID$CREATED_DATE", new Date()); // TODO: update the SQL to do this.
        update.set("FEATURE_ID$AUDIT_ID$CREATED_BY_ID", user.getId());
        
        findOrCreateSite(row, update);
        
    }

    @Override
    protected void afterPerformRow(Row row, Modify update) throws RowImportException {

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

}
