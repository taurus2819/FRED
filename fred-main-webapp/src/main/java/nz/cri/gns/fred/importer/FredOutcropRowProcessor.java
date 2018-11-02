package nz.cri.gns.fred.importer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.db.util.SiteUtil.SiteException;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.SiteUtil;
import nz.cri.gns.munginator.SQLModify;
import nz.cri.gns.munginator.SQLSelect;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.TemplateRowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.Row;

public class FredOutcropRowProcessor extends TemplateRowProcessor {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.import.FredOutcropRowProcessor");
    private User user;

    public FredOutcropRowProcessor(User user) {
        super("FRED_OUTCROP");
        this.user = user;
    }

    @Override
    protected void beforePopulateRow(Row row, SQLModify update) throws RowImportException {

    }

    @Override
    protected void afterPopulateRow(Row row, SQLModify update) throws RowImportException {
        update.set("FEATURE_ID$FEATURE_TYPE", "Outcrop");
        List<String[]> error = new ArrayList<>();

        // Convert ACCURACY to a float.
        Double accuracyD = getRowValueDouble(row, "ACCURACY");
        Float accuracy;
        if (null == accuracyD) {
            accuracy = null;
        } else {
            accuracy = accuracyD.floatValue();
        }

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
                accuracy,
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
            log("Site already exists: \""+site.getDirections()+"\". I will not update it.");
        }

        update.set("FEATURE_ID$SITE_ID", site.getId());
    }

    @Override
    protected void afterCommitRow(Row row, SQLModify update) throws RowImportException {

    }

    private String getDatumCode(Row row) throws RowImportException {
        String datumCode;
        Connection conn = null;
        try {
            conn = FREDUtil.getConnection();
            Integer datumId = idFromName(row, "ORIG_SYSTEM_ID");
            if (null==datumId) {
                throw new RowImportException(row, "ORIG_SYSTEM_ID", "This cell needs a value.", null );
            }
            SQLSelect s = schema.select("LU_COORD_SYSTEM");
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
                conn.close();
            } catch (SQLException ex) {
            }
        }
        return datumCode;
    }
}
