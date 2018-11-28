package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import nz.cri.gns.munginator.export.AcrossSheetIterator;
import nz.cri.gns.munginator.export.GenericSpreadsheet;
import nz.cri.gns.munginator.export.SheetIterator;
import nz.cri.gns.munginator.export.SpreadsheetExporter;
import static nz.cri.gns.munginator.export.XLSXSpreadsheet.LAST_ROW;

public final class PaleoSpreadsheetExporter extends SpreadsheetExporter {

    public PaleoSpreadsheetExporter(Connection conn, String code, String name) {
        super(conn, code, name);
    }

    private void cr(GenericSpreadsheet ss, SheetIterator it) {
        ss.nextRow();
        it.gotoFirstColumn();
    }

    @Override
    public void write(OutputStream o) throws SQLException, IOException {
        final GenericSpreadsheet s = spreadsheet; // shorthand
        SheetIterator it = new AcrossSheetIterator();
        final int numColumns = 50;
        final int todoTextSize = 80;
        s.setOutputStream(o);

        s.addSheet("Paleo", it);
        cr(s, it);
        s.setCellText("Locality", 0);
        // TODO: text constraint
        cr(s, it);
        s.setCellText("Identification Date", 0);
        // TODO: timestamp constraint
        cr(s, it);
        s.setCellText("Date Rounding", 0);
        cr(s, it);
        s.setCellText("Identifier(s)", 0); // TODO: multi-lists?
        cr(s, it);
        s.setCellText("Stage Start", 0);
        cr(s, it);
        s.setCellText("Stage Start Mod", 0);
        cr(s, it);
        s.setCellText("Stage Stop", 0);
        cr(s, it);
        s.setCellText("Stage Stop Mod", 0);
        cr(s, it);
        s.setCellText("Stage Comments", 0);
        cr(s, it);
        s.setCellText("Laboratory", 0);
        cr(s, it);
        s.setCellText("Lab Number", 0);
        cr(s, it);
        s.setCellText("Collection Comments", 0);

        for (int i = 0; i < LAST_ROW; i++) {
            cr(s, it);
            s.setCellText("TODO: Taxon Group", 0);
            s.nextColumn();
            s.setCellText("TODO: Taxon", 0);
        }

        s.finish();

        s.flush();
    }

}
