package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.Datum.Coordinate;

import org.xml.sax.SAXException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class FRFormServlet extends HttpServlet {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private DAOFactory factory;
	private SampleUtil sampleUtil;
	
	private static final float MM_TO_PT = 2.8346f;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		this.request = request;
		this.response = response;
		this.factory = HibernateUtil.get().getDAOFactory();
		this.sampleUtil = new SampleUtil(factory);
		Sample sample = sampleUtil.getSample(Integer.parseInt(request.getParameter("ID")));
		//Feature feature2 = featureUtil.getFeature(Integer.parseInt(request.getParameter("Feat2ID")));
		makePDF(new Sample[] {sample});
		} catch (Exception e) {
			System.out.println("************************************");
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
		
	private void makePDF(Sample[] samples) throws DocumentException, IOException, NamingException, SQLException {
		Document document = new Document(PageSize.A4, 20 * MM_TO_PT, 15 * MM_TO_PT, 15 * MM_TO_PT, 15 * MM_TO_PT);
			
		PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
		writer.setEncryption(true, null, null, PdfWriter.AllowPrinting | PdfWriter.AllowScreenReaders);
			
		document.open();
		
		Font[] fonts = new Font[7];
		fonts[0] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
		fonts[1] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
		fonts[2] = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
		fonts[3] = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
		fonts[4] = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
		fonts[5] = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);
		fonts[6] = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
		
		for (int i = 0; i < samples.length; i++) {
			try {
			writeSample(samples[i], document, fonts);
			if (i < samples.length - 1)
				document.newPage();
			} catch (Exception e) {
				System.out.println("************************************");
				e.printStackTrace();				
			}
			//out.flush();
		}
		
		document.close();

	}
	
	private void writeSample(Sample sample, Document document, Font[] fonts) throws DocumentException, MalformedURLException, IOException, NamingException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException {

		Feature feature = sample.getFeature();
		
		PdfPTable table = new PdfPTable(2);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {25 * MM_TO_PT, 150 * MM_TO_PT});
		table.setSpacingAfter(5 * MM_TO_PT);
				
		//Logos
		String url = "http://" + JspUtils.getServerName(new PageState(request, response, getServletContext()))
				+ "/fred/images/gsnz_logo.gif";
		Image image = Image.getInstance(new URL(url));
		image.scaleToFit(20 * MM_TO_PT, 20 * MM_TO_PT);
		table.addCell(image);
		//PdfPCell cell = new PdfPCell(new Phrase("GSNZ\nLogo", fonts[2]));
		//table.addCell(cell);
			
		//Header Text
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setTotalWidth(150 * MM_TO_PT);
		headerTable.setLockedWidth(true);
		headerTable.setWidths(new float[] {90 * MM_TO_PT, 60 * MM_TO_PT});
		
		PdfPCell defaultCell = headerTable.getDefaultCell();
		defaultCell.setBorder(PdfPCell.NO_BORDER);
		defaultCell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
		defaultCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
						
		PdfPCell cell = new PdfPCell(new Phrase("Geological Society of New Zealand", fonts[2]));
		headerTable.addCell(cell);
		cell = new PdfPCell(new Phrase(FeatureUtil.getFeatureName(feature), fonts[3]));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		headerTable.addCell(cell);
			
		cell = new PdfPCell(new Phrase("Fossil Record Form", fonts[4]));
		headerTable.addCell(cell);
		cell = new PdfPCell(new Phrase(feature.getFeatureType(), fonts[2]));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		headerTable.addCell(cell);			
			
		table.addCell(headerTable);
		
		document.add(table);
		
		//Masterfile text
		table = new PdfPTable(2);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {60 * MM_TO_PT, 115 * MM_TO_PT});
		table.setSpacingAfter(5 * MM_TO_PT);
			
		defaultCell = table.getDefaultCell();
		defaultCell.setBorder(PdfPCell.NO_BORDER);
		defaultCell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
		defaultCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		
		cell = new PdfPCell(new Phrase("Masterfile", fonts[6]));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(feature.getMasterFile().getName(), fonts[5]));
		table.addCell(cell);
			
		cell = new PdfPCell(new Phrase("Masterfile Curator Approved", fonts[6]));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(FREDUtil.getUserName(feature.getAudit().getApprovedById().intValue())
				+ " " + FREDUtil.formatDateForOutput(feature.getAudit().getApprovedDate()), fonts[5]));
		table.addCell(cell);
		
		document.add(table);
			
		//Mandatory Data
		table = new PdfPTable(2);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {60 * MM_TO_PT, 115 * MM_TO_PT});
		table.setSpacingAfter(5 * MM_TO_PT);

		defaultCell = table.getDefaultCell();
		defaultCell.setBorder(PdfPCell.NO_BORDER);
		defaultCell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
		defaultCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		
		cell = new PdfPCell(new Phrase("Mandatory Data", fonts[2]));
		cell.setColspan(2);
		table.addCell(cell);

		String featType = feature.getFeatureType();
		String featTypeLbl;
		if (featType.equals(FREDConstants.OUTCROP)) {
			featTypeLbl = "Field Number";
		} else if (featType.equals(FREDConstants.DRILLHOLE)) {
			featTypeLbl = "Drillhole Name";
		} else {
			featTypeLbl = "Section Name";
		}
		addCell(table, featTypeLbl, fonts[1]);
		addCell(table, feature.getFeatureName(), fonts[0]);
		
		addCell(table, "Original Grid Ref", fonts[1]);
		if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
			Datum datum = DatumFactory.createDatum(feature.getOrigSystemId().intValue());
			Coordinate coord = datum.parseCoordinate(feature.getOrigCoord());
			addCell(table, datum.getHumanStringFor(coord).replaceAll("Geographic ", ""), fonts[0]);
		} else {
			addCell(table, "", fonts[0]);
		}
	
		
/*
		
		if (sample.get(Sample.LATITUDE) != null) {
			SiteRecord sr = SiteRecord.querySite(FREDUtils.getFREDConnection(state), sample.getAsInt(Sample.SITE_ID));
			if (sample.get(Sample.ORIG_SYSTEM_ID) != null && sample.get(Sample.ORIG_COORD) != null) {
				Datum datum = DatumFactory.createDatum(sample.getAsInt(Sample.ORIG_SYSTEM_ID));
				Datum.Coordinate coord = datum.parseCoordinate(sample.getAsString(Sample.ORIG_COORD));
				out.print("<tr><td class='heading'>"
						+ ((coord instanceof Datum.LatLong) ? "Lat/Long" : "Grid Ref")
						+ "</td><td>" + datum.getHumanStringFor(coord).replaceAll("Geographic ", "") + "</td></tr>");
				if (!datum.getName().equals("NZMG")) {
					try {
						Datum nzmgDatum = DatumFactory.createDatum("NZMG");
						Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
						out.println("<tr><td class='heading'>Grid Ref</td><td>" + nzmgDatum.getHumanStringFor(nzmgCoord) + "</td></tr>");
					} catch (Exception e) { }
				}
			}
			Datum.LatLong ll = sr.getLatLong();
			if (ll.getNorthSouth() != 999)
				out.println("<tr><td class='heading'>Lat/Long</td><td>" + ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)</td></tr>");
		}
		
		if (sample.get(Sample.MAP_YEAR) != null) { out.println("<tr><td class='heading'>Map Year</td><td>" + sample.getAsString(Sample.MAP_YEAR) + "</td></tr>"); }
		if (sample.get(Sample.METHOD) != null) { out.println("<tr><td class='heading'>Method</td><td>" + sample.getAsString(Sample.METHOD) + "</td></tr>"); }
		if (sample.get(Sample.ACCURACY) != null) { out.println("<tr><td class='heading'>Accuracy</td><td>&#177 " + sample.getAsDouble(Sample.ACCURACY) + "m</td></tr>"); }
		if (sample.isUserAuthenticated() && sample.get(Sample.LOCALITY) != null) { out.println("<tr><td class='heading'>Locality</td><td>" + sample.getAsString(Sample.LOCALITY) + "</td></tr>"); }
		if (!featType.equals(Feature.OUTCROP_LOCALITY)) {
			if (sample.isUserAuthenticated() && sample.get(Sample.PERSON) != null) {
				out.print("<tr><td class='heading' width='135'>");
				if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
					out.print("Operating Company");
				} else {
					out.print("Section Collector");
				}
				out.println("</td><td>" + sample.getAsString(Sample.PERSON) + "</td></tr>");
			}
			if (sample.isUserAuthenticated() && sample.get(Sample.START_DATE) != null) {
				out.print("<tr><td class='heading'>");
				if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
					out.print("Spud Date");
				} else {
					out.print("Sampling Start Date");
				}
				out.print("</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.START_DATE), sample.getAsString(Sample.START_DATE_ROUNDING)) + "</td></tr>");
			}
			if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DATE) != null) {
				out.print("<tr><td class='heading'>Completion Date</td><td>" + FREDUtils.formatDateForOutput(sample.getAsDate(Sample.FINISH_DATE), sample.getAsString(Sample.FINISH_DATE_ROUNDING)) + "</td></tr>");
			}
			if (featType.equals(Feature.DRILLHOLE_LOCALITY) && sample.isUserAuthenticated() && sample.get(Sample.DRILLHOLE_LICENCE_NAME) != null) { out.println("<tr><td class='heading' width='135'>Licence Area</td><td>" + sample.getAsString(Sample.DRILLHOLE_LICENCE_NAME) + "</td></tr>"); }
			if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_TYPE) != null) { out.println("<tr><td class='heading' width='135'>Datum Type</td><td>" + sample.getAsString(Sample.DATUM_TYPE) + "</td></tr>"); }
			if (sample.isUserAuthenticated() && sample.get(Sample.DATUM_ELEVATION) != null) { out.println("<tr><td class='heading' width='135'>Datum Elevation</td><td>" + sample.getAsString(Sample.DATUM_ELEVATION) + " m asl</td></tr>"); }
			if (sample.isUserAuthenticated() && sample.get(Sample.START_DEPTH) != null) {
				out.print("<tr><td class='heading' width='135'>");
				if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
					out.print("Kick-off Depth");
				} else {
					out.print("Top Horizon");
				}
				out.println("</td><td>" + sample.getAsString(Sample.START_DEPTH) + " m</td></tr>");
			}
			if (sample.isUserAuthenticated() && sample.get(Sample.FINISH_DEPTH) != null) {
				out.print("<tr><td class='heading' width='135'>");
				if (featType.equals(Feature.DRILLHOLE_LOCALITY)) {
					out.print("Termination Depth");
				} else {
					out.print("Base Horizon");
				}
				out.println("</td><td>" + sample.getAsString(Sample.FINISH_DEPTH) + " m</td></tr>");
			}
		}
		
		
	*/
		
		document.add(table);
			
	}
	
	private void addCell(PdfPTable table, String text, Font font) {
		table.addCell(new Phrase(DBUtils.nvl(text), font));
	}

}
