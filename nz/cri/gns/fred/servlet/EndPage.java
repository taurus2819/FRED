package nz.cri.gns.fred.servlet;

import nz.cri.gns.fred.util.PDFUtil;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class EndPage extends PdfPageEventHelper {

	private static final float MM_TO_PT = 2.8346f;
	
	public void onEndPage(PdfWriter writer, Document document) {
		//footer
		Rectangle page = document.getPageSize();
		PdfPTable footer = new PdfPTable(1);
		PDFUtil.addCell(footer, "Generated from FRED database on " + new java.util.Date(), FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD), PdfPCell.ALIGN_RIGHT, 1);
		footer.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
		footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(),	writer.getDirectContent());
        
		//border
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.setRGBColorStroke(110, 110, 110);
		cb.setLineWidth(2);
		cb.rectangle(15 * MM_TO_PT, 10 * MM_TO_PT, 185 * MM_TO_PT, 277 * MM_TO_PT);
		cb.stroke();
		cb.restoreState();
    }

}
