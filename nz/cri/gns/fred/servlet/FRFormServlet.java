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

import nz.cri.gns.auth.User;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.Datum.Coordinate;
import nz.cri.gns.util.map.Datum.LatLong;

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
	private FeatureUtil featureUtil;
	private UserAccount user;
	
	private static final float MM_TO_PT = 2.8346f;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		this.request = request;
		this.response = response;
		this.factory = HibernateUtil.get().getDAOFactory();
		this.sampleUtil = new SampleUtil(factory);
		this.featureUtil = new FeatureUtil(factory);
		this.user = (UserAccount)request.getSession().getAttribute(User.USER_ATTRIBUTE);
		Sample sample = sampleUtil.getSample(Integer.parseInt(request.getParameter("ID")));
		
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
	
	private void writeSample(Sample sample, Document document, Font[] fonts) throws DocumentException, MalformedURLException, IOException, NamingException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException, StorageAccessException {
		Feature feature = sample.getFeature();
		boolean isAllowedReadFeature = featureUtil.isAllowedReadFeature(user, feature);
		boolean isAllowedReadSample = sampleUtil.isAllowedReadSample(user, sample);
		
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
			
		//Header Text
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setTotalWidth(150 * MM_TO_PT);
		headerTable.setLockedWidth(true);
		headerTable.setWidths(new float[] {80 * MM_TO_PT, 70 * MM_TO_PT});
		
		PdfPCell defaultCell = headerTable.getDefaultCell();
		defaultCell.setBorder(PdfPCell.NO_BORDER);
		defaultCell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
		defaultCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
						
		addCell(headerTable, "Geological Society of New Zealand", fonts[2]);
		FrNumber frNumber = FeatureUtil.getFrNumber(feature);
		PdfPCell cell = new PdfPCell(new Phrase(((frNumber != null) ? frNumber.getFrNumber() : "____/f_____"), fonts[3]));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		headerTable.addCell(cell);
			
		addCell(headerTable, "Fossil Record Form", fonts[4]);
		cell = new PdfPCell(new Phrase(feature.getFeatureType(), fonts[2]));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		headerTable.addCell(cell);			
			
		table.addCell(headerTable);
		
		document.add(table);
		
		//Masterfile text
		table = new PdfPTable(2);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {50 * MM_TO_PT, 125 * MM_TO_PT});
		table.setSpacingAfter(5 * MM_TO_PT);
			
		defaultCell = table.getDefaultCell();
		defaultCell.setBorder(PdfPCell.NO_BORDER);
		defaultCell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
		defaultCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		
		addCells(table, new String[] {"Masterfile", feature.getMasterFile().getName()}, new Font[] {fonts[6], fonts[5]});
		addCells(table, new String[] {"Masterfile Curator Approved", FREDUtil.getUserName(feature.getAudit().getApprovedById().intValue())
				+ " " + FREDUtil.formatDateForOutput(feature.getAudit().getApprovedDate())}, new Font[] {fonts[6], fonts[5]});	
		
		document.add(table);
			
		//Mandatory Data
		table = new PdfPTable(2);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {55 * MM_TO_PT, 120 * MM_TO_PT});
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
		addCells(table, new String[] {featTypeLbl, feature.getFeatureName()}, new Font[] {fonts[1], fonts[0]});
		addCell(table, "Original Grid Reference", fonts[1]);
		if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
			Datum datum = FREDUtil.getFREDDatum(feature);
			Coordinate coord = FREDUtil.getFREDCoordinate(feature);
			addCell(table, datum.getHumanStringFor(coord).replaceAll("Geographic ", ""), fonts[0]);
			if (!datum.getName().equals("NZMG")) {
				try {
					Datum nzmgDatum = DatumFactory.createDatum("NZMG");
					Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
					addCells(table, new String[] {"Converted Grid Reference", nzmgDatum.getHumanStringFor(nzmgCoord)}, new Font[] {fonts[1], fonts[0]});
				} catch (Exception e) { }
			}
		} else {
			addCell(table, "", fonts[0]);
		}
		SiteRecord sr = FREDUtil.getSite(feature);
		if (sr != null) {
			LatLong ll = sr.getLatLong();
			addCells(table, new String[] {"Converted Decimal Lat/Long", ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)"}, new Font[] {fonts[1], fonts[0]});
		}
		addCells(table, new Object[] {"Map Year", feature.getMapYear()}, new Font[] {fonts[1], fonts[0]});
		addCells(table, new String[] {"Method", FREDUtil.getSiteMethod(sr)}, new Font[] {fonts[1], fonts[0]});
		addCells(table, new Object[] {"Accuracy", ((sr.isNull(SiteRecord.H_ACCURACY_FIELD)) ? null : sr.getAccuracy())}, new Font[] {fonts[1], fonts[0]});
		addCells(table, new String[] {"Locality", ((sr.isNull(SiteRecord.DIRECTIONS_FIELD)) ? null : sr.getDirections())}, new Font[] {fonts[1], fonts[0]});
		if (isAllowedReadFeature) {
			addCells(table, new String[] {"Locality", feature.getLocality()}, new Font[] {fonts[1], fonts[0]});
			if (!featType.equals(FREDConstants.OUTCROP)) {
				addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Operating Company" : "Section Collector"), feature.getPerson()}, new Font[] {fonts[1], fonts[0]});
				addCells(table, new String[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Spud Date" : "Sampling Start Date"), FREDUtil.formatDateForOutput(feature.getStartDate(), feature.getStartDateRounding())}, new Font[] {fonts[1], fonts[0]});		
				addCells(table, new String[] {"Completion Date", FREDUtil.formatDateForOutput(feature.getFinishDate(), feature.getFinishDateRounding())}, new Font[] {fonts[1], fonts[0]});
				if (featType.equals(FREDConstants.DRILLHOLE))
					addCells(table, new String[] {"Licence Area", feature.getDrillholeLicenceName()}, new Font[] {fonts[1], fonts[0]});	
				addCells(table, new String[] {"Datum Type", feature.getDatumType()}, new Font[] {fonts[1], fonts[0]});
				addCells(table, new Object[] {"Datum Elevation", feature.getDatumElevation()}, new Font[] {fonts[1], fonts[0]});
				addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Kick-off Depth" : "Top Horizon"), feature.getStartDepth()}, new Font[] {fonts[1], fonts[0]});
				addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Termination Depth" : "Base Horizon"), feature.getFinishDepth()}, new Font[] {fonts[1], fonts[0]});
			}
		}
		document.add(table);
			
	}
	
	private void addCell(PdfPTable table, Object text, Font font) {
		table.addCell(new Phrase(DBUtils.nvl(text), font));
	}
	
	private void addCells(PdfPTable table, Object[] text, Font[] fonts) {
		for (int i = 0; i < text.length; i++)
			addCell(table, text[i], fonts[i]);
	}

}
