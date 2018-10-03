package nz.cri.gns.fred.importer;

import java.util.ArrayList;
import java.util.List;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.fred.util.SiteUtil;
import nz.cri.gns.munginator.SQLModify;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.TemplateRowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.Row;

/**
 * TODO: move this out of the Munginator package
 */
public class FredOutcropRowProcessor extends TemplateRowProcessor {
    private User user;
    
    public FredOutcropRowProcessor(User user) {
        super("FRED_OUTCROP");
        this.user=user;
    }

    @Override
    protected void beforePopulateRow(Row row, SQLModify update) throws RowImportException {

    }

    @Override
    protected void afterPopulateRow(Row row, SQLModify update) throws RowImportException {
        update.set("FEATURE_ID$FEATURE_TYPE", "Outcrop");

        List<String[]> error = new ArrayList<>();

        // TODO: I made up all these codes. Implement them.
        SiteUtil.findOrMakeSiteInstance(
                error,
                getRowValueString(row, "FEATURE_NAME"),
                getRowValueInteger(row, "ORIG_SYSTEM_ID"), // TODO: should be a lookup value.
                getRowValueString(row, "ORIG_COORDS"),
                getRowValueString(row, "DATUM"),
                getRowValueString(row, "EASTING"),
                getRowValueString(row, "NORTHING"),
                getRowValueString(row, "LOCALITY"),
                getRowValueString(row, "COUNTRY"),
                getRowValueInteger(row, "LOCATION_METHOD"),
                getRowValueDouble(row, "ACCURACY").floatValue(),
                getRowValueString(row, "MAP_SHEET"),
                user
        );

    }

    @Override
    protected void afterCommitRow(Row row, SQLModify update) throws RowImportException {

    }
}
