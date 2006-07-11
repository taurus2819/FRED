package nz.cri.gns.fred.servlet;

import nz.cri.gns.fred.util.PDFUtil;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class EndPage extends PdfPageEventHelper {

	private static final float MM_TO_PT = 2.8346f;
	private PdfTemplate tpl;
	
	public void onEndPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		
		//footer
		Rectangle page = document.getPageSize();
		float width = page.width() - document.leftMargin() - document.rightMargin();
		//PdfPTable footer = new PdfPTable(1);
		PdfPTable footer = new PdfPTable(2);
		footer.setTotalWidth(width);
		footer.setLockedWidth(true);
		try {
			footer.setWidths(new float[] {100 * MM_TO_PT, width - (100 * MM_TO_PT)});
		} catch (DocumentException e) {
			System.out.println("*** PDF Footer Exception ***");
			e.printStackTrace();
		}
				
		PDFUtil.addCell(footer, "Printed on " + new java.util.Date() + " from FRED, the computer database for the NZ Fossil Record File (FRF).", FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD), PdfPCell.ALIGN_LEFT, 2);
		PDFUtil.addCell(footer, "FRF is a nationally significant database administered by GSNZ and GNS Science", FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD), PdfPCell.ALIGN_LEFT, 2);
		//PDFUtil.addCell(footer, "Page " + writer.getPageNumber() + " of ", FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL), PdfPCell.ALIGN_RIGHT, 1);
		cb.addTemplate(tpl, document.rightMargin() - FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL).getBaseFont().getWidthPoint("2", 7), document.bottomMargin());
		footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(),	cb);
        
		//border
		cb.setRGBColorStroke(110, 110, 110);
		cb.setLineWidth(2);
		cb.rectangle(15 * MM_TO_PT, 10 * MM_TO_PT, 185 * MM_TO_PT, 277 * MM_TO_PT);
		cb.stroke();
		cb.restoreState();
    }
	
    public void onCloseDocument(PdfWriter writer, Document document) {
        tpl.beginText();
        tpl.setFontAndSize(FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL).getBaseFont(), 7);
        tpl.setTextMatrix(0, 0);
        tpl.showText("" + (writer.getPageNumber() - 1));
        tpl.endText();
     }

}
