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
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

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