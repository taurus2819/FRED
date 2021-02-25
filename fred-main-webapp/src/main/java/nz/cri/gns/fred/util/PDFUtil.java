package nz.cri.gns.fred.util;

//import nz.cri.gns.db.DBUtils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;


public class PDFUtil {
    
                   private static SpecialCharactersFormatting charFormatting = new SpecialCharactersFormatting();

	public static void addCell(PdfPTable table, Object text, Font font) {
		addCell(table, text, font, Element.ALIGN_LEFT, 1);
	}
	
	public static void addCell(PdfPTable table, Object text, Font font, int align, int colSpan) {
		//PdfPCell cell = new PdfPCell(new Phrase(DBUtils.nvl(text), font));
		PdfPCell cell = new PdfPCell(new Phrase( charFormatting.getText(String.valueOf(text)), font));
		cell.setHorizontalAlignment(align);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		if (colSpan > 1)
			cell.setColspan(colSpan);
		table.addCell(cell);
	}
	
	public static void addCells(PdfPTable table, Object[] text, Font[] fonts) {
		for (int i = 0; i < text.length; i++)
			addCell(table, text[i], fonts[i], Element.ALIGN_LEFT, 1);
	}
	
	public static void addCells(PdfPTable table, Object[] text, Font[] fonts, int[] align) {
		for (int i = 0; i < text.length; i++)
			addCell(table, text[i], fonts[i], align[i], 1);
	}
	
	public static void addRepeatingCells(PdfPTable table, String heading, Object[] text, Font[] fonts, boolean newLines) {
		if (text.length > 0) {
			if (newLines) {
				addCells(table, new Object[] {heading, text[0]}, fonts);
				for (int i = 1; i < text.length; i++)
					addCells(table, new Object[] {null, text[i]}, fonts);
			} else {
				StringBuffer textLine = new StringBuffer();
				for (int i = 0; i < text.length; i++) {
					textLine.append(text[i]);
					if (i < text.length - 1)
						textLine.append("; ");
				}
				addCells(table, new String[] {heading, textLine.toString()}, fonts);
			}
		} else {
			addCells(table, new String[] {heading, null}, fonts);
		}
	}

	public static void addTable(PdfPTable table, Object[] text, Font[] fonts, int colspan, float width, float[] colWidths) throws DocumentException {
		PdfPTable insertTable = new PdfPTable(text.length);
		insertTable.setTotalWidth(width);
		insertTable.setLockedWidth(true);
		insertTable.setWidths(colWidths);
		for (int i = 0; i < text.length; i++)
			addCell(insertTable, text[i], fonts[i]);
		PdfPCell cell = new PdfPCell(insertTable);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(colspan);
		table.addCell(cell);
	}

}
