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
			
			Sample[] samples = null;
			Feature[] features = null;
			if (request.getParameter("SampIDs") != null) {
				String[] sampIDs = request.getParameterValues("SampIDs");
				samples = new Sample[sampIDs.length];
				for (int i = 0; i < sampIDs.length; i++) {
					try {
						Sample sample = sampleUtil.getSample(Integer.parseInt(sampIDs[i]));
						samples[i] = sample;
						System.out.println("Sample " + sample.getSampleId());
					}
					catch (Exception _e) {}
				}			
			}
			if (request.getParameter("FeatIDs") != null) {
				String[] featIDs = request.getParameterValues("FeatIDs");
				features = new Feature[featIDs.length];
				for (int i = 0; i < featIDs.length; i++) {
					try {
						Feature feature = featureUtil.getFeature(Integer.parseInt(featIDs[i]));
						features[i] = feature;
						System.out.println("Feature " + feature.getFeatureId());
					}
					catch (Exception _e) {}
				}			
			}
	
			makePDF(samples, features);
		} catch (Exception e) {
			System.out.println("************************************");
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
		
	private void makePDF(Sample[] samples, Feature[] features) throws DocumentException, IOException, NamingException, SQLException {
		Document document = new Document(PageSize.A4, 20 * MM_TO_PT, 15 * MM_TO_PT, 15 * MM_TO_PT, 15 * MM_TO_PT);
		PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
		writer.setEncryption(true, null, null, PdfWriter.AllowPrinting | PdfWriter.AllowScreenReaders);
		document.open();
		
		Font[] fonts = new Font[4];
		fonts[0] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
		fonts[1] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
		fonts[1].setColor(110, 110, 110);
		fonts[2] = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
		fonts[3] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
		
		if (samples != null) {
			for (int i = 0; i < samples.length; i++) {
				try {
					writeHeader(samples[i], document);
					writeLocality(samples[i], document, fonts);
					writeSample(samples[i], document, fonts);
					if (i < samples.length - 1)
						document.newPage();
				} catch (Exception e) {
					System.out.println("************************************");
					e.printStackTrace();				
				}
				//out.flush();
			}
		}
		if (features != null) {
			for (int i = 0; i < features.length; i++) {
				try {
					writeHeader(features[i], document);
					writeLocality(features[i], document, fonts);
					if (features[i].getFeatureType().equals(FREDConstants.OUTCROP))
						writeSample(features[i], document, fonts);
					if (i < features.length - 1)
						document.newPage();
				} catch (Exception e) {
					System.out.println("************************************");
					e.printStackTrace();				
				}
				//out.flush();
			}			
		}
		
		document.close();
	}
	
	private void writeHeader(Feature feature, Document document) throws DocumentException, MalformedURLException, IOException, NamingException, SQLException {
		Font[] fonts = new Font[6];
		fonts[0] = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);
		fonts[1] = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
		fonts[1].setColor(110, 110, 110);
		fonts[2] = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
		fonts[3] = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
		fonts[4] = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
		fonts[5] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
		
		PdfPTable table = new PdfPTable(2);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {25 * MM_TO_PT, 150 * MM_TO_PT});
		table.setSpacingAfter(2 * MM_TO_PT);
			
		//Logo
		String url = "http://" + JspUtils.getServerName(new PageState(request, response, getServletContext()))
				+ "/fred/images/gsnz_logo.gif";
		Image image = Image.getInstance(new URL(url));
		image.scaleToFit(20 * MM_TO_PT, 20 * MM_TO_PT);
		image.setBorder(Image.NO_BORDER);
		table.addCell(image);
		
		//Header Text
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setTotalWidth(150 * MM_TO_PT);
		headerTable.setLockedWidth(true);
		headerTable.setWidths(new float[] {80 * MM_TO_PT, 70 * MM_TO_PT});
	
		FrNumber frNumber = FeatureUtil.getFrNumber(feature);
		addCells(headerTable, new String[] {"Geological Society of New Zealand", ((frNumber != null) ? frNumber.getFrNumber() : "____/f_____")}
			, new Font[] {fonts[2], fonts[3]}, new int[] {PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_RIGHT});
		addCells(headerTable, new String[] {"Fossil Record Form", "Locality Type: " + feature.getFeatureType()}, new Font[] {fonts[4], fonts[5]}, new int[] {PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_RIGHT});
		table.addCell(headerTable);
		
		document.add(table);
		
		//Masterfile text
		table = new PdfPTable(4);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {15 * MM_TO_PT, 40 * MM_TO_PT, 40 * MM_TO_PT, 80 * MM_TO_PT});
		table.setSpacingAfter(5 * MM_TO_PT);
			
		addCells(table, new String[] {"Masterfile", ((feature.getMasterFile() != null) ? feature.getMasterFile().getName() : null)}, new Font[] {fonts[1], fonts[0]});
		String app = ((feature.getAudit().getApprovedById() != null) ? FREDUtil.getUserName(feature.getAudit().getApprovedById().intValue()) : "")
				+ ((feature.getAudit().getApprovedDate() != null) ? " " + FREDUtil.formatDateForOutput(feature.getAudit().getApprovedDate()) : "");
		addCells(table, new String[] {"Masterfile Curator Approved", app}, new Font[] {fonts[1], fonts[0]});	
		
		document.add(table);		
	}
	
	private void writeHeader(Sample sample, Document document) throws MalformedURLException, DocumentException, IOException, NamingException, SQLException {
		writeHeader(sample.getFeature(), document);
	}
	
	private void writeLocality(Feature feature, Document document, Font[] fonts) throws StorageAccessException, DocumentException, NamingException, SQLException {
		boolean isAllowedReadFeature = featureUtil.isAllowedReadFeature(user, feature);
		Font[] bodyFonts = new Font[] {fonts[1], fonts[0]};
		
		//Location Information
		PdfPTable table = new PdfPTable(2);
		table.setTotalWidth(175 * MM_TO_PT);
		table.setLockedWidth(true);
		table.setWidths(new float[] {55 * MM_TO_PT, 120 * MM_TO_PT});
		table.setSpacingAfter(3 * MM_TO_PT);
		
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
		SiteRecord sr = null;
		if (feature.getSiteId() != null) {
			sr = FREDUtil.getSite(feature);
			LatLong ll = sr.getLatLong();
			addCells(table, new String[] {"Converted Decimal Lat/Long", ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)"}, bodyFonts);
		}
		addCells(table, new Object[] {"Map Year", feature.getMapYear()}, bodyFonts);
		addCells(table, new String[] {"Method", ((sr != null) ? FREDUtil.getSiteMethod(sr) : null)}, bodyFonts);
		addCells(table, new String[] {"Accuracy", ((sr != null && !sr.isNull(SiteRecord.H_ACCURACY_FIELD)) ? String.valueOf(sr.getAccuracy()) : null)}, bodyFonts);
		if (isAllowedReadFeature) {
			addCells(table, new String[] {"Locality", feature.getLocality()}, bodyFonts);
			if (!featType.equals(FREDConstants.OUTCROP)) {
				addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Operating Company" : "Section Collector"), ((feature.getPerson() != null) ? feature.getPerson().getName() : null)}, bodyFonts);
				addCells(table, new String[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Spud Date" : "Sampling Start Date"), ((feature.getStartDate() != null) ? FREDUtil.formatDateForOutput(feature.getStartDate(), feature.getStartDateRounding()) : null)}, bodyFonts);		
				addCells(table, new String[] {"Completion Date", ((feature.getFinishDate() != null) ? FREDUtil.formatDateForOutput(feature.getFinishDate(), feature.getFinishDateRounding()) : null)}, bodyFonts);
				if (featType.equals(FREDConstants.DRILLHOLE))
					addCells(table, new String[] {"Licence Area", feature.getDrillholeLicenceName()}, bodyFonts);	
				addCells(table, new String[] {"Datum Type", feature.getDatumType()}, bodyFonts);
				addCells(table, new Object[] {"Datum Elevation", ((feature.getDatumElevation() != null) ? feature.getDatumElevation() + " m asl" : null)}, bodyFonts);
				addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Kick-off Depth" : "Top Horizon"), ((feature.getStartDepth() != null) ? feature.getStartDepth() + " m" : null)}, bodyFonts);
				addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Termination Depth" : "Base Horizon"), ((feature.getFinishDepth() != null) ? feature.getFinishDepth() + " m" : null)}, bodyFonts);
			}
		}
		document.add(table);		
	}
	
	private void writeLocality(Sample sample, Document document, Font[] fonts) throws StorageAccessException, DocumentException, NamingException, SQLException {
		writeLocality(sample.getFeature(), document, fonts);
	}
	
	private void writeSample(Feature feature, Document document, Font[] fonts) throws MalformedURLException, DocumentException, IOException, NamingException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException, StorageAccessException {
		Set<Sample> samples = feature.getSamples();
		for (Sample sample : samples)
			writeSample(sample, document, fonts);
	}
	
	private void writeSample(Sample sample, Document document, Font[] fonts) throws DocumentException, MalformedURLException, IOException, NamingException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException, StorageAccessException {
		boolean isAllowedReadSample = sampleUtil.isAllowedReadSample(user, sample);
		Font[] bodyFonts = new Font[] {fonts[1], fonts[0]};
		
		if (isAllowedReadSample) {
			
			//if not OUTCROP then add sample depth data
			if (!sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
				PdfPTable table = new PdfPTable(2);
				table.setTotalWidth(175 * MM_TO_PT);
				table.setLockedWidth(true);
				table.setWidths(new float[] {55 * MM_TO_PT, 120 * MM_TO_PT});
				table.setSpacingAfter(3 * MM_TO_PT);
				
				addCells(table, new String[] {"Sample Information", SampleUtil.getDrillHoleDepthDescription(sample)}, new Font[] {fonts[2], fonts[3]});
				
				document.add(table);
			}
			
			//Collection Information
			PdfPTable table = new PdfPTable(2);
			table.setTotalWidth(175 * MM_TO_PT);
			table.setLockedWidth(true);
			table.setWidths(new float[] {55 * MM_TO_PT, 120 * MM_TO_PT});
			table.setSpacingAfter(3 * MM_TO_PT);
			
			addCell(table, "Collection Information", fonts[2], PdfPCell.ALIGN_LEFT, 2);			

			addCells(table, new String[] {"Collection Date", ((sample.getCollectionDate() != null) ? FREDUtil.formatDateForOutput(sample.getCollectionDate(), sample.getDateRounding()) : null)}, bodyFonts);
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
			table.setSpacingAfter(3 * MM_TO_PT);
			
			addCell(table, "Stratigraphy", fonts[2], PdfPCell.ALIGN_LEFT, 2);
			String infStage = ((sample.getInferredStage() != null) ? StageUtil.getStageDescription(sample.getInferredStage()) : "");
			String knwStage = ((sample.getKnownStage() != null) ? StageUtil.getStageDescription(sample.getKnownStage()) : "");
			addCells(table, new String[] {"Stages", "Inferred: " + infStage + "    Known: " + knwStage}, bodyFonts);
			Object[] relationships = sampleUtil.getRelationships(sample, "Sample", "nearby").toArray();
			String[] relationshipStr = new String[relationships.length];
			for (int i = 0; i < relationships.length; i++)
				relationshipStr[i] = FeatureUtil.getFeatureIdentifyingName(((Relationship) relationships[i]).getFeature());
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
			addCells(table, new String[] {"Column/Map", sample.getColumnMap()}, bodyFonts);
			addCells(table, new Object[] {"Dip", sample.getDip()}, bodyFonts);
			addCells(table, new String[] {"Dip Direction", sample.getDipDirection()}, bodyFonts);
			addCells(table, new Object[] {"Strike", sample.getStrike()}, bodyFonts);
			addCells(table, new String[] {"Facing", sample.getFacing()}, bodyFonts);
		
			document.add(table);

			//Sedimentary Features
			table = new PdfPTable(2);
			table.setTotalWidth(175 * MM_TO_PT);
			table.setLockedWidth(true);
			table.setWidths(new float[] {55 * MM_TO_PT, 120 * MM_TO_PT});
			
			addCell(table, "Sedimentary Features", fonts[2], PdfPCell.ALIGN_LEFT, 2);			
			addCells(table, new String[] {"Grain Size", SampleUtil.getGrainSizeDescription(sample)}, bodyFonts);
			addCells(table, new String[] {"Comparator Used", sample.getComparatorUsed()}, bodyFonts);
			addCells(table, new String[] {"Bedding Thickness", ((sample.getBedThickness() != null) ? sample.getBedThickness().getName() : null)}, bodyFonts);
			addCells(table, new String[] {"Bedding Features", SampleUtil.getBeddingDescription(sample)}, bodyFonts);
			addCells(table, new String[] {"Weathering", ((sample.getWeathering() != null) ? sample.getWeathering().getName() : null)}, bodyFonts);
			addCells(table, new String[] {"Hardness", ((sample.getHardness() != null) ? sample.getHardness().getName() : null)}, bodyFonts);
			addCells(table, new String[] {"Carbonate", ((sample.getCarbonate() != null) ? sample.getCarbonate().getName() : null)}, bodyFonts);
			addCells(table, new String[] {"Colour", SampleUtil.getColourDescription(sample)}, bodyFonts);
			addCells(table, new String[] {"Inferred Environment", sample.getDepositionEnv()}, bodyFonts);
			addCells(table, new String[] {"Nature of Rock Unit", sample.getRockNature()}, bodyFonts);
			addCells(table, new String[] {"Correspondence", sample.getCorrespondence()}, bodyFonts);
			
			document.add(table);
		}
	}
	
	private void addCell(PdfPTable table, Object text, Font font) {
		addCell(table, text, font, PdfPCell.ALIGN_LEFT, 1);
	}
	
	private void addCell(PdfPTable table, Object text, Font font, int align, int colSpan) {
		PdfPCell cell = new PdfPCell(new Phrase(DBUtils.nvl(text), font));
		cell.setHorizontalAlignment(align);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
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
		if (text.length > 0) {
			addCells(table, new String[] {heading, text[0]}, fonts);
			for (int i = 1; i < text.length; i++)
				addCells(table, new String[] {null, text[i]}, fonts);
		} else {
			addCells(table, new String[] {heading, null}, fonts);
		}
	}

}
