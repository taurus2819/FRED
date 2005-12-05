package nz.cri.gns.fred.servlet;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class EndPage extends PdfPageEventHelper {

	private static final float MM_TO_PT = 2.8346f;
	
	public void onEndPage(PdfWriter writer, Document document) {
		Rectangle page = document.getPageSize();
		PdfPTable foot = new PdfPTable(1);
		foot.addCell("Printed from FRED database on " + new java.util.Date());
		foot.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
		foot.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(),
				writer.getDirectContent());
        
		//rectangle
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.setRGBColorStroke(110, 110, 110);
		cb.setLineWidth(2);
		cb.rectangle(20 * MM_TO_PT, 10 * MM_TO_PT, 180 * MM_TO_PT, 277 * MM_TO_PT);
		cb.stroke();
		cb.restoreState();
    }

}
