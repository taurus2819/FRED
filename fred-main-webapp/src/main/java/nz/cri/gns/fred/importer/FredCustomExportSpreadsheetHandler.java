package nz.cri.gns.fred.importer;

import java.sql.Connection;
import java.sql.SQLException;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.SQLSelect;
import nz.cri.gns.munginator.SchemaSingleton;
import nz.cri.gns.munginator.columns.Column;
import nz.cri.gns.munginator.export.ExportSpreadsheet;
import nz.cri.gns.munginator.export.GenericSpreadsheet;
import nz.cri.gns.munginator.upload.CustomExportSpreadsheetHandler;

public class FredCustomExportSpreadsheetHandler implements CustomExportSpreadsheetHandler {

    private final Connection conn;

    public FredCustomExportSpreadsheetHandler(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean addColumn(GenericSpreadsheet sheet, ExportSpreadsheet.Sheet sheetInfo, String heading, Column c) {
        switch (sheetInfo.templateCode) {
            case "FRED_OUTCROP":
                switch (c.getImportCode()) {
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
        SQLSelect s;
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

}
