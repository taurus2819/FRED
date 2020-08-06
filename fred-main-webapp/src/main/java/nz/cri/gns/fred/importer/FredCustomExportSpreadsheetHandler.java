package nz.cri.gns.fred.importer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.Read;
import nz.cri.gns.munginator.SchemaSingleton;
import nz.cri.gns.munginator.columns.Column;
import nz.cri.gns.munginator.export.SpreadsheetExporter;
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
    public boolean addColumn(GenericSpreadsheet sheet, SpreadsheetExporter.Sheet sheetInfo, String heading, Column c) {
        // If you need it, the sheet is in sheetInfo.templateCode
        switch (c.getImportCode()) {
            case "FOLDER":
                addFolderList(sheet, heading, user);
                return true;
            case "LOCATION_METHOD":
                addLocationMethodColumn(sheet, heading);
                return true;
            case "COUNTRY":
                addCountryColumn(sheet, heading);
                return true;
            case "STRAT_UNIT":
                addStratUnitColumn(sheet, heading);
                return true;
            case "SAMPLE_RELATIONSHIP_MOD":
            case "STRAT_RELATIONSHIP_MOD":
                sheet.addDropDownHeader(heading, c, new String[]{"c.", "?"});
                sheet.nextColumn();
                return true;
            case "SAMPLE_RELATIONSHIP_PREP":
                sheet.addDropDownHeader(heading, c, new String[]{"above", "below"});
                sheet.nextColumn();
                return true;
            case "STRAT_RELATIONSHIP_PREP":
                sheet.addDropDownHeader(heading, c, new String[]{"above top", "above base", "below top", "below base"});
                sheet.nextColumn();
                return true;
            case "INFERRED_ENVIRONMENT":
                sheet.addDropDownHeader(heading, c, new String[]{"Marine", "Non Marine"});
                sheet.nextColumn();
                return true;
            case "COMPARATOR_USED":
            case "ABUNDANT":
                sheet.addDropDownHeader(heading, c, new String[]{"Y", "N"});
                sheet.nextColumn();
                return true;
            case "KNOWN_STAGE_LOWER":
            case "KNOWN_STAGE_UPPER":
            case "INFERRED_STAGE_LOWER":
            case "INFERRED_STAGE_UPPER":
                addStagesList(sheet, heading);
                return true;
            default:
                return false;
        }
    }

    private void addLocationMethodColumn(GenericSpreadsheet sheet, String heading) {
        Read s;
        try {
            s = SchemaSingleton.getInstance(conn).select("SC.METHOD");
            s.addColumn("METHOD_ID");
            s.addColumn("METHOD");
            s.addOrderBy("METHOD");
            sheet.addDropDownHeader(heading, null, conn, s);
            sheet.nextColumn(); // this smells bad. Why?
        } catch (SQLException ex) {
            throw new MgException(ex);
        }
    }

    private void addCountryColumn(GenericSpreadsheet sheet, String heading) {
        Read s;
        try {
            s = SchemaSingleton.getInstance(conn).select("LU_COUNTRY");
            s.addColumn("COUNTRY_CODE");
            s.addColumn("COUNTRY_NAME");
            s.addOrderBy("COUNTRY_NAME");
            sheet.addDropDownHeader(heading, null, conn, s);
            sheet.nextColumn(); // this smells bad. Why?
        } catch (SQLException ex) {
            throw new MgException(ex);
        }
    }

    private void addStratUnitColumn(GenericSpreadsheet sheet, String heading) {
        Read s;
        try {
            s = SchemaSingleton.getInstance(conn).select("SL.STRAT_UNIT");
            s.addColumn("SU_NAME_STANDARD");
            s.addOrderBy("SU_NAME_STANDARD");
            sheet.addDropDownHeader(heading, null, conn, s);
            sheet.nextColumn(); // this smells bad. Why?
        } catch (SQLException ex) {
            throw new MgException(ex);
        }
    }

    private void addFolderList(GenericSpreadsheet sheet, String heading, User user) {
        try {
            DAOFactory factory = FredHibernate.get().getDAOFactory();
            List<UserFolder> folders = new FolderUtil(factory).getPersonalFolders(user);
            List<String> columnValues = folders.stream().map(folder -> folder.getFolderName()).collect(Collectors.toList());
            sheet.addDropDownHeader(heading, null, columnValues);
            sheet.nextColumn();
        } catch (StorageAccessException e) {
            throw new MgException(e);
        }
    }

    private void addStagesList(GenericSpreadsheet sheet, String heading) {
        try {
            DAOFactory factory = FredHibernate.get().getDAOFactory();
            List<Age> ages = new StageUtil(factory).getCurrentAges();
            List<String> columnValues = ages.stream().map(age -> age.getName()).collect(Collectors.toList());
            sheet.addDropDownHeader(heading, null, columnValues);
            sheet.nextColumn();
        } catch (StorageAccessException x) {
            throw new MgException(x);
        }
    }
}
