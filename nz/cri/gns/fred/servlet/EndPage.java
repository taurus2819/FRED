package nz.cri.gns.fred.servlet;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class EndPage extends PdfPageEventHelper {
	
	private static final float MM_TO_PT = 2.8346f;
	private PdfTemplate tpl;
	private BaseFont baseFont;
	
	public void onOpenDocument(PdfWriter writer, Document document) {
		baseFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD).getBaseFont();
		tpl = writer.getDirectContent().createTemplate(20, 20);
	}  
	
	public void onEndPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		
		//footer
		String pageNumStr = "Page " + writer.getPageNumber() + " of ";
		cb.beginText();
		cb.setFontAndSize(baseFont, 7);
		cb.setTextMatrix(document.left(), document.bottomMargin() - 10);
		cb.showText("Printed on " + new java.util.Date() + " from FRED, the computer database for the NZ Fossil Record File (FRF).");
		cb.setTextMatrix(document.left(), document.bottomMargin() - 20);
		cb.showText("FRF is a nationally significant database administered by GSNZ and GNS Science");
		cb.setTextMatrix(document.right() - (baseFont.getWidthPoint(pageNumStr + "0", 7)), document.bottomMargin() - 20);
		cb.showText(pageNumStr);
		cb.endText();
		cb.addTemplate(tpl, document.right() - baseFont.getWidthPoint("0", 7), document.bottomMargin() - 20);
		
		//border
		cb.setRGBColorStroke(110, 110, 110);
		cb.setLineWidth(2);
		cb.rectangle(15 * MM_TO_PT, 10 * MM_TO_PT, 185 * MM_TO_PT, 277 * MM_TO_PT);
		cb.stroke();
		cb.restoreState();
	}
	
	public void onCloseDocument(PdfWriter writer, Document document) {
		tpl.beginText();
		tpl.setFontAndSize(baseFont, 7);
		tpl.setTextMatrix(0, 0);
		tpl.showText("" + (writer.getPageNumber() - 1));
		tpl.endText();
	}
	
}
