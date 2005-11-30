package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.util.FeatureUtil;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class FRFormServlet extends HttpServlet {

	private HttpServletResponse response;
	private DAOFactory factory;
	private FeatureUtil featureUtil;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		this.response = response;
		this.factory = HibernateUtil.get().getDAOFactory();
		this.featureUtil = new FeatureUtil(factory);
		Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatID")));
		//Feature feature2 = featureUtil.getFeature(Integer.parseInt(request.getParameter("Feat2ID")));
		makePDF(new Feature[] {feature});
		} catch (Exception e) {
			System.out.println("************************************");
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
		
	private void makePDF(Feature[] features) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4, 0, 0, 50, 50);
			
		PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
		writer.setEncryption(true, null, null, PdfWriter.AllowPrinting | PdfWriter.AllowScreenReaders);
			
		document.open();
		
		Font[] fonts = new Font[1];
		fonts[0] = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
		
		for (int i = 0; i < features.length; i++) {
			writeLocality(features[i], document, fonts);
			if (i < features.length - 1)
				document.newPage();
			//out.flush();
		}
		
		document.close();

	}
	
	private void writeLocality(Feature feature, Document document, Font[] fonts) throws DocumentException, MalformedURLException, IOException {
			
			PdfPTable table = new PdfPTable(new float[] {560});
			table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			
			Image image = Image.getInstance(new URL("http://data.gns.cri.nz/fred/images/gsnz_logo.gif"));
			image.scaleToFit(61, 60);
			//image.setAbsolutePosition(460, 18);
			document.add(image);
			
			PdfPCell cell = new PdfPCell(new Phrase(new Chunk("FRF Form Test: " + FeatureUtil.getFeatureName(feature), fonts[0])));
			cell.setVerticalAlignment(PdfPCell.ALIGN_RIGHT);
			cell.setBorder(PdfPCell.BOTTOM);
			table.addCell(cell);

			document.add(table);

			
/*			Table dataTable = new Table(2);
			dataTable.setAbsWidth("100");
			dataTable.setWidths(new float[] {230, 330});
			dataTable.setBorder(Table.NO_BORDER);
			dataTable.setPadding(10);
			//dataTable.setBorderWidth(1);
			//dataTable.setBorderColor(java.awt.Color.red);

			Cell dataCell = new Cell();
			dataCell.setBorder(Cell.NO_BORDER);
			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.NORMAL);
			Font boldFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
			
			Paragraph breakParagraph = new Paragraph(new Chunk(new String(new char[] {0xA0, 0xA0}), font));
			try {
				//Get the photo
				StringBuffer url = request.getRequestURL();
				//This is located at .../nathaz/cv.pdf
				url.delete(url.indexOf("cv/cv.pdf"), url.length());
				//and we want .../nathaz/images/dynamic/??.jpg
				url.append("images/dynamic/").append(person.getPhotoId(state)).append(".jpg");
				
				Image image = Image.getInstance(new URL(url.toString())); 
				image.scaleToFit(100, 100);
				image.setAlignment(Image.ALIGN_CENTER);
				dataCell.add(image);
				dataCell.add(breakParagraph);
			} catch (Exception e) {
			}
			Paragraph paragraph = new Paragraph(new Phrase("Areas of Specialisation", boldFont));
			paragraph.setSpacingAfter(10);
			dataCell.addElement(paragraph);
			
			Expertise[] expertise = person.getExpertiseAreas(state);
			for (int i=0; i<expertise.length; i++) {
				paragraph = new Paragraph(new Phrase(expertise[i].getExpertiseString(), font));
				paragraph.setIndentationLeft(27);
				paragraph.setFirstLineIndent(18);
				
				dataCell.addElement(paragraph);
			}
			dataCell.addElement(breakParagraph);
			paragraph = new Paragraph(new Phrase("Qualifications", boldFont));
			paragraph.setSpacingAfter(10);
			paragraph.setSpacingBefore(18);
			dataCell.addElement(paragraph);
			
			
			Qualification[] quals = person.getEducationalQualifications(state);
			
			for (int i=0; i<quals.length; i++) {
				paragraph = new Paragraph(new Phrase(quals[i].getDescription(), font));
				paragraph.setIndentationLeft(27);
				paragraph.setFirstLineIndent(18);
				
				dataCell.addElement(paragraph);
			}
			dataCell.addElement(breakParagraph);
			paragraph = new Paragraph(new Phrase("Contact", boldFont));
			dataCell.addElement(paragraph);

			Company company = NatHazUtils.getCompany(person.getCompanyId(), state);
			
			String address = company.getName() + "\n" + company.getPostAddressText() + "\n" + NatHazUtils.getCountryText(company.getPostAddressCountry(), state).toUpperCase();
			paragraph = new Paragraph(new Phrase(address, font));
			paragraph.setIndentationLeft(27);
			dataCell.add(paragraph);
			dataCell.add(breakParagraph);
			
			String phone = person.getPhone();
			if (phone == null || phone.trim().length() == 0)
				phone = company.getPhone();
			
			if (phone != null && phone.length() > 0) {
				paragraph = new Paragraph(new Phrase("Phone: " + phone, font));	
				paragraph.setIndentationLeft(27);
				dataCell.add(paragraph);		
			}
			String fax = person.getFax();
			if (fax == null || fax.trim().length() == 0)
				fax = company.getFax();

			if (fax != null && phone.length() > 0) {
				paragraph = new Paragraph(new Phrase("Fax: " + fax, font));	
				paragraph.setIndentationLeft(27);
				dataCell.add(paragraph);		
			}
			
			paragraph = new Paragraph(new Phrase("Email: " + person.getEmail(), font));
			paragraph.setIndentationLeft(27);
			dataCell.add(paragraph);
			
			
			Language[] languages = person.getLanguages(state);
			String langs = "";
			for (int i=0; i<languages.length; i++) {
				langs += ", " + languages[i].getLanguageString();
			}
			
			if (langs.length() > 0) {
				dataCell.add(breakParagraph);
				paragraph = new Paragraph(new Phrase("Languages", boldFont));
				dataCell.addElement(paragraph);
				paragraph = new Paragraph(langs.substring(2), font);
				paragraph.setIndentationLeft(27);
				dataCell.addElement(paragraph);
			}
			
			dataCell.add(breakParagraph);
			paragraph = new Paragraph(new Phrase("Countries of work experience", boldFont));
			dataCell.addElement(paragraph);
			
			PublicCV cv = person.getPublicCV(state);
			paragraph = new Paragraph(new Phrase(cv.getCountries(""), font));
			paragraph.setIndentationLeft(27);
			dataCell.add(paragraph);
			
			dataTable.addCell(dataCell);
			
			dataCell = new Cell();
			dataCell.setBorder(Cell.LEFT);
			if (cv.getExperience() != null) {
				dataCell.add(new Paragraph(new Phrase("Experience", boldFont)));
				paragraph = new Paragraph(new Phrase(cv.getExperience(), font));
				paragraph.setAlignment(Paragraph.ALIGN_JUSTIFIED_ALL);
				paragraph.setLeading(12f);
				paragraph.setIndentationLeft(27);
				dataCell.add(paragraph);
				dataCell.add(breakParagraph);
			}
			
			dataCell.add(new Paragraph(new Phrase("Track Record", boldFont)));
			Experience[] experience = person.getPublicExperience(state);
			if (experience.length > 0){
				List list = new List(false, 10);
				for (int i=0; i<experience.length; i++) {
					list.add(new ListItem(new Phrase(experience[i].getPublicDescription(), font)));
				}
				dataCell.add(list);
			}
			
			dataTable.addCell(dataCell);
			document.add(new Phrase("\n"));
			document.add(dataTable);
			
			Image image = Image.getInstance(getClass().getResource("nhnzlogo.gif"));		
			image.scaleToFit(100, 100);
			image.setAbsolutePosition(460, 18);
			document.add(image);
	*/		
			
	}

}
