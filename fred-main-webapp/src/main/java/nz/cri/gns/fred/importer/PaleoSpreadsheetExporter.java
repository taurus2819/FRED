package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.LabUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.export.AcrossSheetIterator;
import nz.cri.gns.munginator.export.GenericSpreadsheet;
import nz.cri.gns.munginator.export.SheetIterator;
import nz.cri.gns.munginator.export.SpreadsheetExporter;
import static nz.cri.gns.munginator.export.XLSXSpreadsheet.LAST_ROW;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class PaleoSpreadsheetExporter extends SpreadsheetExporter {

    private final DAOFactory factory;
    private static final String SHEETNAME = "Paleo";
    private final User user;

    public PaleoSpreadsheetExporter(Connection conn, String code, String name, DAOFactory factory, User user) throws IOException {
        super(conn, code, name);
        this.factory = factory;
        this.user = user;
    }

    /**
     * "Carriage Return"
     */
    private void cr(GenericSpreadsheet ss, SheetIterator it) {
        ss.nextRow();
        it.gotoFirstColumn();
    }

    static final int numColumns = 50;
    static final int todoTextSize = 80;

    @Override
    public void write(OutputStream o) throws SQLException, IOException {
        final GenericSpreadsheet s = spreadsheet; // shorthand
        try {

            SheetIterator it = new AcrossSheetIterator();
            SheetIterator to;
            s.setOutputStream(o);

            FolderUtil folderUtil = new FolderUtil(factory);
            String folderList = s.addList("Folders", folderUtil.getPersonalFolders(user).stream().map(UserFolder::getFolderName).collect(Collectors.toList()));

            TaxonomicUtil taxUtil = new TaxonomicUtil(factory);
            List<TaxonomicGroup> groups;
            groups = taxUtil.getTaxonomicGroups();

            SampleUtil sampleUtil = new SampleUtil(factory);

            StageUtil stageUtil = new StageUtil(factory);

            List<Age> ages;
            ages = stageUtil.getAges();
            String groupList = s.addList("Taxonomic Groups", groups.stream().map(TaxonomicGroup::getDisplayName).collect(Collectors.toList()));
            String ageList = s.addList("Stages", ages.stream().map(Age::getDisplayName).collect(Collectors.toList()));

            String stageModList = s.addList("Stage mod", new String[]{"?"});

            RecordUtil recordUtil = new RecordUtil(factory);
            
                        LabUtil labUtil = new LabUtil(factory);
            
            List<Lab> labs = labUtil.getLabs();
            
            String labList = s.addList("Laboratories", labUtil.getLabs().stream().map(Lab::getName).collect(Collectors.toList()));
            ArrayList<String> sections = new ArrayList();
            for (Lab lab : labs){
                sections.add(lab.getName());
                sections.add(s.addList("sections of " + lab.getName(), lab.getSections().stream().map(LabSection::getCode).collect(Collectors.toList())));
            }

            String beatlesList = s.addList("Beatles", new String[]{"John", "Paul", "George", "Ringo", "Stu"});
            String wilburysList = s.addList("Traveling Wilburys", new String[]{"George", "Jeff", "Tom", "Bob", "Roy"});
            String bandList = s.addList("Bands", new String[]{"Beatles", "Traveling Wilburys"});
            String bandChildren = s.addList("Band Children", new String[]{"@@GNS_submenu@@", "0,-1", "Beatles", beatlesList, "Traveling Wilburys", wilburysList});

            s.addSheet(SHEETNAME, it);

            // Leave a blank row for "headings".
            addCellsAcross("Folder", folderList, it, "Choose one of your folders.");
            addTextCellsAcross("Locality", it, "Choose a locality.");

            String sampleTypeList = s.addList("Sample Type", sampleUtil.getDrillTypes().stream().map(DrillType::getName).collect(Collectors.toList()));
            addTextCellsAcross("Top Depth", it, "Enter the sample depth if it's from a drilling");
            addTextCellsAcross("Bottom Depth", it, "Select the accuracy of the date.");
            addCellsAcross("Sample Type", sampleTypeList, it, "Choose a Sample Type.");

            cr(s, it);
            it.nextColumn();
            s.setCellValue("Identification Date");
            s.setCellAsHeading();
            it.nextColumn();
            to = it.copy().skipColumns(numColumns);
            s.setCellsTimestamp(to, "Enter an Identification Date in the format \"DD/MM/YYYY HH:MM:SS\"", false);
            s.setCellsColour(GenericSpreadsheet.Colour.LIGHT_YELLOW, to);

            String dateRoundingList = s.addList("Date Rounding", new String[]{"Year", "Month"});
            addCellsAcross("Date Rounding", dateRoundingList, it, "Select the accuracy of the date.");

            addTextCellsAcross("Identifier(s)", it, "Enter identifiers. ");

            addCellsAcross("Stage Start", ageList, it, "Choose the Stage Start. ");

            addCellsAcross("Stage Start Mod", stageModList, it, "Enter the modifier for the stage: blank or '?'.");

            addCellsAcross("Stage Stop", ageList, it, "Choose the Stop Stage.");

            addCellsAcross("Stage Stop Mod", stageModList, it, "Enter the modifier for the stop stage: blank or '?'.");

            addTextCellsAcross("Stage Comments", it, "Enter comments about the stage.");

            addCellsAcross("Laboratory", labList, it, "Choose a Laboratory.");
            
            addCellsAcross("Band", bandList, it, "Choose a Band.");
            addCellsAcross("Band Member", bandChildren, it, "Choose a Band Member");

            addTextCellsAcross("Lab Number", it, "Enter the number used in the laboratory for this sample.");

            addTextCellsAcross("Collection Comments", it, "Enter comments about this collection. ");

            for (int i = 0; i < LAST_ROW; i++) {
                cr(s, it);
                s.setCellsSelection(groupList, it, "Choose a Taxonomic Group", false);
                s.setCellAsHeading();
                s.nextColumn();
                s.setCellsText(numColumns, it, "Type in a Tanonomic Name from the StratLex database, or enter a new name.", false);
                s.setCellAsHeading();
                s.nextColumn();

                s.setCellsText(todoTextSize, it.copy().skipColumns(numColumns), "Enter '*' for presence, a numeric count, count|comment, a straight comment, 'aff.' or 'cf.'", false);
            }

            s.finish();
            makeSecondColumnBigger();

            s.write();
        } catch (StorageAccessException e) {
            throw new MgException(e);
        } finally {
            s.close();
        }
    }

    private void addCellsAcross(String heading, String listReference, SheetIterator it, String placeholder) {
        cr(spreadsheet, it);
        it.nextColumn();
        spreadsheet.setCellValue(heading);
        spreadsheet.setCellAsHeading();

        SheetIterator to = it.copy().skipColumns(numColumns);
        it.nextColumn();
        spreadsheet.setCellsSelection(listReference, to, placeholder, false);
        spreadsheet.setCellsColour(GenericSpreadsheet.Colour.LIGHT_YELLOW, to);
    }

    private void addTextCellsAcross(String heading, SheetIterator it, String placeholder) {
        cr(spreadsheet, it);
        it.nextColumn();
        spreadsheet.setCellValue(heading);
        spreadsheet.setCellAsHeading();
        it.nextColumn();
        SheetIterator to = it.copy().skipColumns(numColumns);
        spreadsheet.setCellsText(todoTextSize, to, placeholder, false);
        spreadsheet.setCellsColour(GenericSpreadsheet.Colour.LIGHT_YELLOW, to);
    }

    private void makeSecondColumnBigger() {
        Object w = spreadsheet.getUnderlyingSpreadsheet();
        XSSFWorkbook wb;
        if (!(w instanceof XSSFWorkbook)) {
            return;
        }
        wb = (XSSFWorkbook) spreadsheet.getUnderlyingSpreadsheet();
        XSSFSheet s = wb.getSheet(SHEETNAME);
        if (s == null) {
            return;
        }
        s.setColumnWidth(1, 32 * 256);
    }
}
