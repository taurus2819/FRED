package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.export.AcrossSheetIterator;
import nz.cri.gns.munginator.export.GenericSpreadsheet;
import nz.cri.gns.munginator.export.SheetIterator;
import nz.cri.gns.munginator.export.SpreadsheetExporter;
import static nz.cri.gns.munginator.export.XLSXSpreadsheet.LAST_ROW;

public final class PaleoSpreadsheetExporter extends SpreadsheetExporter {

    private final DAOFactory factory;

    public PaleoSpreadsheetExporter(Connection conn, String code, String name, DAOFactory factory) {
        super(conn, code, name);
        this.factory = factory;
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
        try {
            final GenericSpreadsheet s = spreadsheet; // shorthand
            SheetIterator it = new AcrossSheetIterator();
            SheetIterator to;
            s.setOutputStream(o);

            TaxonomicUtil taxUtil = new TaxonomicUtil(factory);
            List<TaxonomicGroup> groups;
            groups = taxUtil.getTaxonomicGroups();

            StageUtil stageUtil = new StageUtil(factory);
            List<Age> ages;
            ages = stageUtil.getAges();
            String groupList = s.addList("Taxonomic Groups", groups.stream().map(TaxonomicGroup::getDisplayName).collect(Collectors.toList()));
            String ageList = s.addList("Stages", ages.stream().map(Age::getDisplayName).collect(Collectors.toList()));

            String stageModList = s.addList("Stage mod", new String[]{"?"});

            RecordUtil recordUtil = new RecordUtil(factory);
            String labList = s.addList("Laboratories", recordUtil.getLabs().stream().map(Lab::getDisplayName).collect(Collectors.toList()));

            s.addSheet("Paleo", it);

            it.nextColumn();
            s.setCellValue("Locality");
            spreadsheet.setCellAsHeading();
            it.nextColumn();
            spreadsheet.setCellsText(todoTextSize, it.copy().skipColumns(numColumns), "Enter the locality", false);
            spreadsheet.setCellsColour(GenericSpreadsheet.Colour.LIGHT_YELLOW, it.copy().skipColumns(numColumns));

            addTextCellsAcross("Locality", it, "Choose a locality.");

            cr(s, it);
            it.nextColumn();
            s.setCellValue("Identification Date");
            s.setCellAsHeading();
            it.nextColumn();
            to = it.copy().skipColumns(numColumns);
            s.setCellsTimestamp(to, "Enter an Identification Date in the format \"DD/MM/YYYY HH:MM:SS\"", false);
            s.setCellsColour(GenericSpreadsheet.Colour.LIGHT_YELLOW, to);

            String dateRoundingList = s.addList("Date Rounding", new String[]{"Year / 1", "Month / 2"});
            addCellsAcross("Date Rounding", dateRoundingList, it, "Select the accuracy of the date.");

            addTextCellsAcross("Identifier(s)", it, "Enter identifiers. TODO: better description");

            addCellsAcross("Stage Start", ageList, it, "Choose the Stage Start. TODO: better description");

            addCellsAcross("Stage Start Mod", stageModList, it, "Enter the modifier for the stage: blank or '?'.");

            addCellsAcross("Stage Stop", ageList, it, "Choose the Stop Stage. TODO");

            addCellsAcross("Stage Stop Mod", stageModList, it, "Enter the modifier for the stop stage: blank or '?'.");

            addTextCellsAcross("Stage Comments", it, "Enter comments about the stage.");

            addCellsAcross("Laboratory", labList, it, "Choose a Laboratory.");

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

                s.setCellsText(todoTextSize, it.copy().skipColumns(numColumns), "Enter funky values here. TODO: better description.", false);
            }

            s.finish();

            s.flush();
        } catch (StorageAccessException e) {
            throw new MgException(e);
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
}
