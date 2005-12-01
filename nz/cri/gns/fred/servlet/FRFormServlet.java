package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Set;

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
import nz.cri.gns.fred.model.PersonRelationship;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.StageUtil;
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
		fonts[1].setColor(110, 110, 110);
		fonts[2] = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
		fonts[3] = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
		fonts[4] = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
		fonts[5] = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);
		fonts[6] = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
		fonts[6].setColor(110, 110, 110);
		
		for (int i = 0; i < samples.length; i++) {
			try {
				System.out.println("Generating PDF for sample " + samples[i].getSampleId());
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
		Font[] bodyFonts = new Font[] {fonts[1], fonts[0]};
		
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
	
		FrNumber frNumber = FeatureUtil.getFrNumber(feature);
		addCells(headerTable, new String[] {"Geological Society of New Zealand", ((frNumber != null) ? frNumber.getFrNumber() : "____/f_____")}
			, new Font[] {fonts[2], fonts[3]}, new int[] {PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_RIGHT});
		addCells(headerTable, new String[] {"Fossil Record Form", feature.getFeatureType()}, new Font[] {fonts[4], fonts[2]}, new int[] {PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_RIGHT});			
		table.addCell(headerTable);
		
		document.add(table);
		
		//Masterfile text
		table = new PdfPTable(2);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {50 * MM_TO_PT, 125 * MM_TO_PT});
		table.setSpacingAfter(5 * MM_TO_PT);
			
		addCells(table, new String[] {"Masterfile", feature.getMasterFile().getName()}, new Font[] {fonts[6], fonts[5]});
		addCells(table, new String[] {"Masterfile Curator Approved", FREDUtil.getUserName(feature.getAudit().getApprovedById().intValue())
				+ " " + FREDUtil.formatDateForOutput(feature.getAudit().getApprovedDate())}, new Font[] {fonts[6], fonts[5]});	
		
		document.add(table);
			
		//Location Information
		table = new PdfPTable(2);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {55 * MM_TO_PT, 120 * MM_TO_PT});
		table.setSpacingAfter(5 * MM_TO_PT);
		
		addCell(table, "Location Information", fonts[2], PdfPCell.ALIGN_LEFT, 2);
		String featType = feature.getFeatureType();
		String featTypeLbl;
		if (featType.equals(FREDConstants.OUTCROP)) {
			featTypeLbl = "Field Number";
		} else if (featType.equals(FREDConstants.DRILLHOLE)) {
			featTypeLbl = "Drillhole Name";
		} else {
			featTypeLbl = "Section Name";
		}
		addCells(table, new String[] {featTypeLbl, feature.getFeatureName()}, bodyFonts);
		addCell(table, "Original Grid Reference", fonts[1]);
		if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
			Datum datum = FREDUtil.getFREDDatum(feature);
			Coordinate coord = FREDUtil.getFREDCoordinate(feature);
			addCell(table, datum.getHumanStringFor(coord).replaceAll("Geographic ", ""), fonts[0]);
			if (!datum.getName().equals("NZMG")) {
				try {
					Datum nzmgDatum = DatumFactory.createDatum("NZMG");
					Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
					addCells(table, new String[] {"Converted Grid Reference", nzmgDatum.getHumanStringFor(nzmgCoord)}, bodyFonts);
				} catch (Exception e) { }
			}
		} else {
			addCell(table, "", fonts[0]);
		}
		SiteRecord sr = FREDUtil.getSite(feature);
		if (sr != null) {
			LatLong ll = sr.getLatLong();
			addCells(table, new String[] {"Converted Decimal Lat/Long", ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)"}, bodyFonts);
		}
		addCells(table, new Object[] {"Map Year", feature.getMapYear()}, bodyFonts);
		addCells(table, new String[] {"Method", FREDUtil.getSiteMethod(sr)}, bodyFonts);
		addCells(table, new Object[] {"Accuracy", ((sr.isNull(SiteRecord.H_ACCURACY_FIELD)) ? null : sr.getAccuracy())}, bodyFonts);
		if (isAllowedReadFeature) {
			addCells(table, new String[] {"Locality", feature.getLocality()}, bodyFonts);
			if (!featType.equals(FREDConstants.OUTCROP)) {
				addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Operating Company" : "Section Collector"), feature.getPerson().getName()}, bodyFonts);
				addCells(table, new String[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Spud Date" : "Sampling Start Date"), FREDUtil.formatDateForOutput(feature.getStartDate(), feature.getStartDateRounding())}, bodyFonts);		
				addCells(table, new String[] {"Completion Date", FREDUtil.formatDateForOutput(feature.getFinishDate(), feature.getFinishDateRounding())}, bodyFonts);
				if (featType.equals(FREDConstants.DRILLHOLE))
					addCells(table, new String[] {"Licence Area", feature.getDrillholeLicenceName()}, bodyFonts);	
				addCells(table, new String[] {"Datum Type", feature.getDatumType()}, bodyFonts);
				addCells(table, new Object[] {"Datum Elevation", ((feature.getDatumElevation() != null) ? feature.getDatumElevation() + " m asl" : null)}, bodyFonts);
				addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Kick-off Depth" : "Top Horizon"), ((feature.getStartDepth() != null) ? feature.getStartDepth() + " m" : null)}, bodyFonts);
				addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Termination Depth" : "Base Horizon"), ((feature.getFinishDepth() != null) ? feature.getFinishDepth() + " m" : null)}, bodyFonts);
			}
		}
		document.add(table);
	
		if (isAllowedReadSample) {
			//Collection Information
			table = new PdfPTable(2);
			table.setTotalWidth(175 * MM_TO_PT);
			table.setLockedWidth(true);
			table.setWidths(new float[] {55 * MM_TO_PT, 120 * MM_TO_PT});
			table.setSpacingAfter(5 * MM_TO_PT);
			
			addCell(table, "Collection Information", fonts[2], PdfPCell.ALIGN_LEFT, 2);			

			addCells(table, new String[] {"Collection Date", FREDUtil.formatDateForOutput(sample.getCollectionDate(), sample.getDateRounding())}, bodyFonts);
			Object[] collectors = sample.getCollectors().toArray();
			String[] collectorStr = new String[collectors.length];
			for (int i = 0; i < collectors.length; i++)
				collectorStr[i] = ((PersonRelationship) collectors[i]).getDisplayName();
			addRepeatingCells(table, "Collectors", collectorStr, bodyFonts);
			addCells(table, new String[] {"Stratigraphic Name", sample.getStratUnit()}, bodyFonts);
			addCells(table, new String[] {"Fossils in Place", sample.getInPlace()}, bodyFonts);
			Object[] sentTos = sample.getSentTos().toArray();
			String[] sentToStr = new String[sentTos.length];
			for (int i = 0; i < sentTos.length; i++)
				sentToStr[i] = SampleUtil.getSentToDescription((SentTo) sentTos[i]);
			addRepeatingCells(table, "Sent To", sentToStr, bodyFonts);
			addCells(table, new String[] {"Not Collected", sample.getNotCollected()}, bodyFonts);
			addCells(table, new String[] {"Significance/Comments", sample.getSignificance()}, bodyFonts);
		
			document.add(table);
			
			//Stratigraphy
			table = new PdfPTable(2);
			table.setTotalWidth(175 * MM_TO_PT);
			table.setLockedWidth(true);
			table.setWidths(new float[] {55 * MM_TO_PT, 120 * MM_TO_PT});
			table.setSpacingAfter(5 * MM_TO_PT);
			
			addCell(table, "Stratigraphy", fonts[2], PdfPCell.ALIGN_LEFT, 2);			
			addCells(table, new String[] {"Inferred Stage", ((sample.getInferredStage() != null) ? StageUtil.getStageDescription(sample.getInferredStage()) : null)}, bodyFonts);
			addCells(table, new String[] {"Known Stage", ((sample.getKnownStage() != null) ? StageUtil.getStageDescription(sample.getKnownStage()) : null)}, bodyFonts);
			Object[] relationships = sampleUtil.getRelationships(sample, "Sample", "nearby").toArray();
			String[] relationshipStr = new String[relationships.length];
			for (int i = 0; i < relationships.length; i++)
				relationshipStr[i] = SampleUtil.getRelationshipDescription((Relationship) relationships[i]);
			addRepeatingCells(table, "Samples Nearby", relationshipStr, bodyFonts);			
			relationships = sampleUtil.getRelationships(sample, "Sample", new String[] {"above", "below"}).toArray();
			relationshipStr = new String[relationships.length];
			for (int i = 0; i < relationships.length; i++)
				relationshipStr[i] = SampleUtil.getRelationshipDescription((Relationship) relationships[i]);
			addRepeatingCells(table, "Sample Relationships", relationshipStr, bodyFonts);			
			relationships = sampleUtil.getRelationships(sample, "Stratigraphic", new String[] {"above top", "above base", "below top", "below base"}).toArray();
			relationshipStr = new String[relationships.length];
			for (int i = 0; i < relationships.length; i++)
				relationshipStr[i] = SampleUtil.getRelationshipDescription((Relationship) relationships[i]);
			addRepeatingCells(table, "Stratigraphic Relationships", relationshipStr, bodyFonts);			

		
			document.add(table);
			
		}
	/*	if (formType.equals("Full") && sample.isUserAuthenticated()) {

			if (sample.get(Sample.INFERRED_STAGE) != null) { out.println("<tr><td class='heading'>Inferred Stage</td><td>" + sample.getAsString(Sample.INFERRED_STAGE) + "</td></tr>"); }
			if (sample.get(Sample.KNOWN_STAGE) != null) { out.println("<tr><td class='heading'>Known Stage</td><td>" + sample.getAsString(Sample.KNOWN_STAGE) + "</td></tr>"); }
			//Nearby samples (repeating)
			if (sample.get(Sample.RELATIONSHIP_NEARBY) != null) {
				out.print("<tr><td class='heading'>Samples Nearby</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_NEARBY).iterator(); i2.hasNext(); ) {
					Relationship nearRel = (Relationship)i2.next();
					out.print(nearRel.getDistanceRelation() + " " + nearRel.getRelatedSampleName() +"<br />");
				}
			out.print("</td></tr>");
			}
			//Sample relationships (repeating)
			if (sample.get(Sample.RELATIONSHIP_SAMPLE) != null) {
				out.print("<tr><td class='heading'>Sample Relationships</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_SAMPLE).iterator(); i2.hasNext(); ) {
					Relationship sampRel = (Relationship)i2.next();
					out.print(sampRel.getDistanceRelation() + " " + sampRel.getRelatedSampleName() + "<br />");
				}
			out.print("</td></tr>");
			}
			//Strat relationships (repeating)
			if (sample.get(Sample.RELATIONSHIP_STRAT) != null) {
				out.print("<tr><td class='heading'>Stratigraphic Relationships</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.RELATIONSHIP_STRAT).iterator(); i2.hasNext(); ) {
					Relationship stratRel = (Relationship)i2.next();
					out.print(stratRel.getRelationship() + "<br />");
				}
			out.print("</td></tr>");
			}
			if (sample.get(Sample.COLUMN_MAP) != null) { out.println("<tr><td class='heading'>Column/Map</td><td>" + sample.getAsString(Sample.COLUMN_MAP) + "</td></tr>"); }
			if (sample.get(Sample.DIP) != null) { out.println("<tr><td class='heading'>Dip</td><td>" + sample.getAsString(Sample.DIP) + "</td></tr>"); }
			if (sample.get(Sample.DIP_DIRECTION) != null) { out.println("<tr><td class='heading'>Dip Direction</td><td>" + sample.getAsString(Sample.DIP_DIRECTION) + "</td></tr>"); }
			if (sample.get(Sample.STRIKE) != null) { out.println("<tr><td class='heading'>Strike</td><td>" + sample.getAsString(Sample.STRIKE) + "</td></tr>"); }
			if (sample.get(Sample.FACING) != null) { out.println("<tr><td class='heading'>Facing</td><td>" + sample.getAsString(Sample.FACING) + "</td></tr>"); }
			
			out.println("<tr><td class='bigheading' colspan='2'>Sedimentary Features</td></tr>");
			if (sample.get(Sample.GRAINSIZE) != null) { out.println("<tr><td class='heading'>Grain Size</td><td>" + sample.getAsString(Sample.GRAINSIZE) + "</td></tr>"); }
			if (sample.get(Sample.COMPARATOR_USED) != null) { out.println("<tr><td class='heading'>Comparator Used</td><td>" + sample.getAsString(Sample.COMPARATOR_USED) + "</td></tr>"); }
			if (sample.get(Sample.BED_THICKNESS) != null) { out.println("<tr><td class='heading'>Bed Thickness</td><td>" + sample.getAsString(Sample.BED_THICKNESS) + "</td></tr>"); }
			if (sample.get(Sample.BEDDING) != null) { out.println("<tr><td class='heading'>Bedding</td><td>" + sample.getAsString(Sample.BEDDING) + "</td></tr>"); }
			if (sample.get(Sample.WEATHERING) != null) { out.println("<tr><td class='heading'>Weathering</td><td>" + sample.getAsString(Sample.WEATHERING) + "</td></tr>"); }
			if (sample.get(Sample.HARDNESS) != null) { out.println("<tr><td class='heading'>Hardness</td><td>" + sample.getAsString(Sample.HARDNESS) + "</td></tr>"); }
			if (sample.get(Sample.CARBONATE) != null) { out.println("<tr><td class='heading'>Carbonate</td><td>" + sample.getAsString(Sample.CARBONATE) + "</td></tr>"); }
			if (sample.get(Sample.COLOUR) != null) { out.println("<tr><td class='heading'>Colour</td><td>" + sample.getAsString(Sample.COLOUR) + "</td></tr>"); }
			//sed features (repeating)
			if (sample.get(Sample.SED_FEATURE) != null) {
				out.print("<tr><td class='heading'>Additional Features</td><td>");
				for (Iterator i2 = sample.getAsVector(Sample.SED_FEATURE).iterator(); i2.hasNext(); ) {
					SedFeature sf = (SedFeature)i2.next();
					out.print(sf.getSedFeature() + "<br />");
				}
				out.print("</td></tr>");
			}
			if (sample.get(Sample.DEPOSITION_ENV) != null) { out.println("<tr><td class='heading'>Inferred Environment</td><td>" + sample.getAsString(Sample.DEPOSITION_ENV) + "</td></tr>"); }
			if (sample.get(Sample.ROCK_NATURE) != null) { out.println("<tr><td class='heading'>Nature of Rock Unit</td><td>" + sample.getAsString(Sample.ROCK_NATURE) + "</td></tr>"); }
			if (sample.get(Sample.CORRESPONDENCE) != null) { out.println("<tr><td class='heading'>Correspondence</td><td>" + sample.getAsString(Sample.CORRESPONDENCE) + "</td></tr>"); }
			out.println("<tr><td><img src='images/blank.gif' height='30' width='1' /></td></tr>");
		}
	
		
		*/
		
	}
	
	private void addCell(PdfPTable table, Object text, Font font) {
		addCell(table, text, font, PdfPCell.ALIGN_LEFT, 1);
	}
	
	private void addCell(PdfPTable table, Object text, Font font, int align, int colSpan) {
		PdfPCell cell = new PdfPCell(new Phrase(DBUtils.nvl(text), font));
		cell.setHorizontalAlignment(align);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
		if (colSpan > 1)
			cell.setColspan(colSpan);
		table.addCell(cell);
	}
	
	private void addCells(PdfPTable table, Object[] text, Font[] fonts) {
		for (int i = 0; i < text.length; i++)
			addCell(table, text[i], fonts[i], PdfPCell.ALIGN_LEFT, 1);
	}
	
	private void addCells(PdfPTable table, Object[] text, Font[] fonts, int[] align) {
		for (int i = 0; i < text.length; i++)
			addCell(table, text[i], fonts[i], align[i], 1);
	}
	
	private void addRepeatingCells(PdfPTable table, String heading, String[] text, Font[] fonts) {
		addCells(table, new String[] {heading, text[0]}, fonts);
		for (int i = 1; i < text.length; i++)
			addCells(table, new String[] {null, text[i]}, fonts);
	}

}
