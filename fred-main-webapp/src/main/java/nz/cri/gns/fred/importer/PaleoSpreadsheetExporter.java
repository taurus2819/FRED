package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

            // There are 30000 of these: taxUtil.getTaxa(group, FREDConstants.APPROVED);
            s.addSheet("Paleo", it);
            cr(s, it);
            s.setCellText("Locality", 0);
            // TODO: text constraint
            cr(s, it);
            s.setCellText("Identification Date", 0);
            for (int i = 0; i < numColumns; i++) {
                s.setCellTimestamp(null);
            }
            cr(s, it);
            s.setCellText("Date Rounding", 0);
            String dateRoundingList = s.addList("Date Rounding", new String[]{"Year / 1", "Month / 2"});
            addCellsAcross(dateRoundingList, it);
            cr(s, it);
            s.setCellText("Identifier(s)", 0); // TODO: multi-lists?
            cr(s, it);
            s.setCellText("Stage Start", 0);
            addCellsAcross(ageList, it);
            cr(s, it);
            s.setCellText("Stage Start Mod", 0);
            addCellsAcross(stageModList, it);
            cr(s, it);
            s.setCellText("Stage Stop", 0);
            addCellsAcross(ageList, it);
            cr(s, it);
            s.setCellText("Stage Stop Mod", 0);
            addCellsAcross(stageModList, it);
            cr(s, it);
            s.setCellText("Stage Comments", 0);
            cr(s, it);
            s.setCellText("Laboratory", 0);
            addCellsAcross(labList, it);
            cr(s, it);
            s.setCellText("Lab Number", 0);
            cr(s, it);
            s.setCellText("Collection Comments", 0);

            for (int i = 0; i < LAST_ROW; i++) {
                cr(s, it);
                s.setCellSelection("TODO: Group", groupList);
                s.nextColumn();
                s.setCellText("TODO: Taxon", 0);
            }

            s.finish();

            s.flush();
        } catch (StorageAccessException e) {
            throw new MgException(e);
        }
    }

    private void addCellsAcross(String listReference, SheetIterator it) {
        for (int i = 0; i < numColumns; i++) {
            it.nextColumn();
            this.spreadsheet.setCellSelection((String) null, listReference);
        }
    }

}
