package nz.cri.gns.fred.importer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.Read;
import nz.cri.gns.munginator.SchemaSingleton;
import nz.cri.gns.munginator.columns.Column;
import nz.cri.gns.munginator.export.ExportSpreadsheet;
import nz.cri.gns.munginator.export.GenericSpreadsheet;
import nz.cri.gns.munginator.upload.CustomExportSpreadsheetHandler;

public class FredCustomExportSpreadsheetHandler implements CustomExportSpreadsheetHandler {

    private final Connection conn;
    private final User user;

    public FredCustomExportSpreadsheetHandler(Connection conn, User user) {
        this.conn = conn;
        this.user = user;
    }

    @Override
    public boolean addColumn(GenericSpreadsheet sheet, ExportSpreadsheet.Sheet sheetInfo, String heading, Column c) {
        switch (sheetInfo.templateCode) {
            case "FRED_OUTCROP":
                switch (c.getImportCode()) {
                    case "FOLDER":
                        addFolderList(sheet, heading, user);
                        return true;
                    case "LOCATION_METHOD":
                        addLocationMethodColumn(sheet, heading, c, "SC.METHOD", "METHOD");
                        return true;
                    case "COUNTRY":
                        addLocationMethodColumn(sheet, heading, c, "MIS.COUNTRY", "COUNTRY_NAME");
                        return true;
                    case "SAMPLE_RELATIONSHIP_MOD":
                    case "STRAT_RELATIONSHIP_MOD":
                        sheet.addDropDownHeader(heading, new String[]{"c.", "?"});
                        sheet.nextColumn();
                        return true;
                    case "SAMPLE_RELATIONSHIP_PREP":
                        sheet.addDropDownHeader(heading, new String[]{"above / 232", "below / 233"});
                        sheet.nextColumn();
                        return true;
                    case "STRAT_RELATIONSHIP_PREP":
                        sheet.addDropDownHeader(heading, new String[]{"above top / 236", "above base / 237", "below top / 238", "below base / 239"});
                        sheet.nextColumn();
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    /*SAMPLES_NEARBY
SAMPLE_RELATIONSHIP_MOD
SAMPLE_RELATIONSHIP_DISTANCE
SAMPLE_RELATIONSHIP_PREP
SAMPLE_RELATIONSHIP_REFERENCE
STRAT_RELATIONSHIP_MOD
STRAT_RELATIONSHIP_DISTANCE
STRAT_RELATIONSHIP_PREP
STRAT_RELATIONSHIP_STRAT_UNIT
    
    232	above
233	below
    
    
    236	above top
237	above base
238	below top
239	below base
     */
    private void addLocationMethodColumn(GenericSpreadsheet sheet, String heading, Column c, String tableName, String orderBy) {
        Read s;
        try {
            s = SchemaSingleton.getInstance(conn).select(tableName);
            s.allColumns();
            s.addOrderBy(orderBy);
            sheet.addDropDownHeader(heading, conn, s);
            sheet.nextColumn(); // this smells bad. Why?
        } catch (SQLException ex) {
            throw new MgException(ex);
        }
    }

    private void addFolderList(GenericSpreadsheet sheet, String heading, User user) {
        DAOFactory factory = null;
        try {
            factory = FredHibernate.get().getDAOFactory();
            List<UserFolder> folders = new FolderUtil(factory).getPersonalFolders(user);
            List<String> columnValues = folders.stream().map(folder -> folder.getFolderName() + " / " + folder.getFolderId()).collect(Collectors.toList());
            sheet.addDropDownHeader(heading, columnValues);
            sheet.nextColumn();
        } catch (StorageAccessException e) {
            throw new MgException(e);
        } finally {
            if (null != factory) {
                try {
                    factory.closeSession();
                } catch (StorageAccessException ex) {
                }
            }
        }
    }
}
