package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Iterator;
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
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.PersonRelationship;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.PDFUtil;
import nz.cri.gns.fred.util.RecordUtil;
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
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class FRFormServlet extends HttpServlet {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private DAOFactory factory;
	private RecordUtil recordUtil;
	private SampleUtil sampleUtil;
	private FeatureUtil featureUtil;
	private UserAccount user;
	
	private static final float MM_TO_PT = 2.8346f;
	
	private static final float bodyTableWidth = 175 * MM_TO_PT;
	private static final float[] bodyTableColWidths = new float[] {45 * MM_TO_PT, 130 * MM_TO_PT};
	private static final float insertTableWidth = 175 * MM_TO_PT;
	private static final float[] insertTableColWidths = new float[] {45 * MM_TO_PT, 38 * MM_TO_PT, 9 * MM_TO_PT, 45 * MM_TO_PT, 38 * MM_TO_PT};

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			this.request = request;
			this.response = response;
			this.factory = HibernateUtil.get().getDAOFactory();
			this.recordUtil = new RecordUtil(factory);
			this.sampleUtil = new SampleUtil(factory);
			this.featureUtil = new FeatureUtil(factory);
			this.user = (UserAccount)request.getSession().getAttribute(User.USER_ATTRIBUTE);
			
			Record[] records = null;
			Sample[] samples = null;
			Feature[] features = null;
			
			if (request.getParameter("RecIDs") != null) {
				String[] recIDs = request.getParameterValues("RecIDs");
				records = new Record[recIDs.length];
				for (int i = 0; i < recIDs.length; i++) {
					try {
						Record record = recordUtil.getRecord(Integer.parseInt(recIDs[i]));
						records[i] = record;
					}
					catch (Exception _e) {}
				}
			}
			if (request.getParameter("SampIDs") != null) {
				String[] sampIDs = request.getParameterValues("SampIDs");
				samples = new Sample[sampIDs.length];
				for (int i = 0; i < sampIDs.length; i++) {
					try {
						Sample sample = sampleUtil.getSample(Integer.parseInt(sampIDs[i]));
						samples[i] = sample;
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
					}
					catch (Exception _e) {}
				}			
			}
			makePDF(records, samples, features);
		} catch (Exception e) {
			System.out.println("************************************");
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
		
	private void makePDF(Record[] records, Sample[] samples, Feature[] features) throws DocumentException, IOException, NamingException, SQLException {
		Document document = new Document(PageSize.A4, 20 * MM_TO_PT, 15 * MM_TO_PT, 15 * MM_TO_PT, 20 * MM_TO_PT);
		PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
		writer.setEncryption(true, null, null, PdfWriter.AllowPrinting | PdfWriter.AllowScreenReaders);
		writer.setPageEvent(new EndPage());
		document.open();
		
		Font[] fonts = new Font[4];
		fonts[0] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
		fonts[1] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
		fonts[1].setColor(40, 22, 111);
		fonts[2] = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
		fonts[2].setColor(40, 22, 111);
		fonts[3] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
		
		if (records != null) {
			for (int i =0; i < records.length; i++) {
				try {
					writeHeader(records[i], document);
					writeRecord(records[i], document, fonts);
					if (i < records.length - 1)
						document.newPage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (samples != null || features != null)
				document.newPage();
		}
		if (samples != null) {
			for (int i = 0; i < samples.length; i++) {
				try {
					writeHeader(samples[i], document);
					writeLocality(samples[i], document, fonts);
					writeSample(samples[i], document, fonts);
					if (i < samples.length - 1)
						document.newPage();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//out.flush();
			}
			if (features != null)
				document.newPage();
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
		fonts[2] = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
		fonts[3] = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
		fonts[4] = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
		fonts[5] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
		
		PdfPTable table = new PdfPTable(3);
		table.setTotalWidth(bodyTableWidth);
		table.setLockedWidth(true);
		table.setWidths(new float[] {23 * MM_TO_PT, 17 * MM_TO_PT, 135 * MM_TO_PT});
		table.setSpacingAfter(2 * MM_TO_PT);
			
		//Logo
		String baseURL = "http://" + JspUtils.getServerName(new PageState(request, response, getServletContext()))
				+ "/fred/images/";
		Image image = Image.getInstance(new URL(baseURL + "gsnz_logo_big.png"));
		image.scaleToFit(20 * MM_TO_PT, 20 * MM_TO_PT);
		PdfPCell cell = new PdfPCell(image);
		cell.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cell);
		
		image = Image.getInstance(new URL(baseURL + "gns_red_big.png"));
		image.scaleToFit(12 * MM_TO_PT, 20 * MM_TO_PT);
		cell = new PdfPCell(image);
		cell.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cell);
		
		//Header Text
		PdfPTable headerTable = new PdfPTable(3);
		headerTable.setTotalWidth(135 * MM_TO_PT);
		headerTable.setLockedWidth(true);
		headerTable.setWidths(new float[] {5 * MM_TO_PT, 65 * MM_TO_PT, 65 * MM_TO_PT});
	
		FrNumber frNumber = FeatureUtil.getFrNumber(feature);
		PDFUtil.addCells(headerTable, new String[] {null, "Fossil Record Form", ((frNumber != null) ? frNumber.getFrNumber() : "____/f_____")}
			, new Font[] {fonts[0], fonts[4], fonts[3]}, new int[] {PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_RIGHT});
		
		PDFUtil.addCell(headerTable, null, fonts[0]);
		
		//Masterfile text
		PdfPTable mfTable = new PdfPTable(2);
		mfTable.setTotalWidth(65 * MM_TO_PT);
		mfTable.setLockedWidth(true);
		mfTable.setWidths(new float[] {15 * MM_TO_PT, 50 * MM_TO_PT});
			
		PDFUtil.addCells(mfTable, new String[] {"Masterfile", ((feature.getMasterFile() != null) ? feature.getMasterFile().getName() : null)}, new Font[] {fonts[1], fonts[0]});
		String app = ((feature.getAudit().getApprovedById() != null) ? FREDUtil.getUserName(feature.getAudit().getApprovedById().intValue()) : "")
				+ ((feature.getAudit().getApprovedDate() != null) ? " " + FREDUtil.formatDateForOutput(feature.getAudit().getApprovedDate()) : "");
		PDFUtil.addCells(mfTable, new String[] {"Approved", app}, new Font[] {fonts[1], fonts[0]});	
		cell = new PdfPCell(mfTable);
		cell.setBorder(PdfPCell.NO_BORDER);
		headerTable.addCell(cell);
		
		PdfPTable localityTable = new PdfPTable(1);
		localityTable.setTotalWidth(65 * MM_TO_PT);
		localityTable.setLockedWidth(true);
		localityTable.setWidths(new float[] {65 * MM_TO_PT});		
		if (FeatureUtil.getYardFrNumber(feature) != null)
			PDFUtil.addCell(localityTable, "(" + FeatureUtil.getYardFrNumber(feature).getFrNumber() + ")", fonts[5], PdfPCell.ALIGN_RIGHT, 1);
		PDFUtil.addCell(localityTable, "Locality Type: " + feature.getFeatureType(), fonts[5], PdfPCell.ALIGN_RIGHT, 1);
		cell = new PdfPCell(localityTable);
		cell.setBorder(PdfPCell.NO_BORDER);
		headerTable.addCell(cell);
		
		cell = new PdfPCell(headerTable);
		cell.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cell);
		
		document.add(table);
	}
	
	private void writeHeader(Sample sample, Document document) throws MalformedURLException, DocumentException, IOException, NamingException, SQLException {
		writeHeader(sample.getFeature(), document);
	}
	
	private void writeHeader(Record record, Document document) throws MalformedURLException, DocumentException, IOException, NamingException, SQLException {
		writeHeader(record.getSample().getFeature(), document);
	}
	
	private void writeLocality(Feature feature, Document document, Font[] fonts) throws StorageAccessException, DocumentException, NamingException, SQLException {
		boolean isAllowedReadFeature = featureUtil.isAllowedReadFeature(user, feature);
		Font[] bodyFonts = new Font[] {fonts[1], fonts[0]};
		Font[] insertBodyFonts = new Font[] {fonts[1], fonts[0], fonts[0], fonts[1], fonts[0]};
		
		//Location Information
		PdfPTable table = new PdfPTable(2);
		table.setTotalWidth(bodyTableWidth);
		table.setLockedWidth(true);
		table.setWidths(bodyTableColWidths);
		table.setSpacingAfter(3 * MM_TO_PT);
		
		PDFUtil.addCell(table, "Location Information", fonts[2], PdfPCell.ALIGN_LEFT, 2);
		String featType = feature.getFeatureType();
		String featTypeLbl;
		if (featType.equals(FREDConstants.OUTCROP)) {
			featTypeLbl = "Field Number";
		} else if (featType.equals(FREDConstants.DRILLHOLE)) {
			featTypeLbl = "Drillhole Name";
		} else {
			featTypeLbl = "Section Name";
		}
		PDFUtil.addCells(table, new String[] {featTypeLbl, feature.getFeatureName()}, bodyFonts);
		PDFUtil.addCell(table, "Original Grid Reference", fonts[1]);
		if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
			Datum datum = FREDUtil.getFREDDatum(feature);
			Coordinate coord = FREDUtil.getFREDCoordinate(feature);
			PDFUtil.addCell(table, datum.getHumanStringFor(coord).replaceAll("Geographic ", ""), fonts[0]);
			if (!datum.getName().equals("NZMG")) {
				try {
					Datum nzmgDatum = DatumFactory.createDatum("NZMG");
					Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
					PDFUtil.addCells(table, new String[] {"Converted Grid Reference", nzmgDatum.getHumanStringFor(nzmgCoord)}, bodyFonts);
				} catch (Exception e) { }
			}
		} else {
			PDFUtil.addCell(table, "", fonts[0]);
		}
		SiteRecord sr = null;
		if (feature.getSiteId() != null) {
			sr = FREDUtil.getSite(feature);
			LatLong ll = sr.getLatLong();
			PDFUtil.addCells(table, new String[] {"Converted Dec. Lat/Long", ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (NZGD49)"}, bodyFonts);
		}
		PDFUtil.addCells(table, new Object[] {"Map Year", feature.getMapYear()}, bodyFonts);
		PDFUtil.addTable(table, new String[] {"Method", ((sr != null) ? FREDUtil.getSiteMethod(sr) : null), null, "Accuracy", ((sr != null && !sr.isNull(SiteRecord.H_ACCURACY_FIELD)) ? String.valueOf(sr.getAccuracy()) + " m" : null)}, insertBodyFonts, 2, insertTableWidth, insertTableColWidths);
		if (isAllowedReadFeature)
			PDFUtil.addCells(table, new String[] {"Locality", feature.getLocality()}, bodyFonts);
		PDFUtil.addCells(table, new String[] {"Country", ((sr != null) ? FREDUtil.getSiteCountry(sr) : null)}, bodyFonts);
		if (isAllowedReadFeature && !featType.equals(FREDConstants.OUTCROP)) {
			PDFUtil.addCells(table, new Object[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Operating Company" : "Section Collector"), ((feature.getPerson() != null) ? feature.getPerson().getName() : null)}, bodyFonts);
			PDFUtil.addTable(table, new String[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Spud Date" : "Sampling Start Date"),
					((feature.getStartDate() != null) ? FREDUtil.formatDateForOutput(feature.getStartDate(), feature.getStartDateRounding()) : null),
					null, "Completion Date",
					((feature.getFinishDate() != null) ? FREDUtil.formatDateForOutput(feature.getFinishDate(), feature.getFinishDateRounding()) : null)},
					insertBodyFonts, 2, insertTableWidth, insertTableColWidths);		
			if (featType.equals(FREDConstants.DRILLHOLE))
				PDFUtil.addCells(table, new String[] {"Licence Area", feature.getDrillholeLicenceName()}, bodyFonts);
			PDFUtil.addTable(table, new String[] {"Datum Type", feature.getDatumType(), null, "Datum Elevation", ((feature.getDatumElevation() != null) ? String.valueOf(feature.getDatumElevation()) + " m asl" : null)}, insertBodyFonts, 2, insertTableWidth, insertTableColWidths);
			PDFUtil.addTable(table, new String[] {((featType.equals(FREDConstants.DRILLHOLE)) ? "Kick-off Depth" : "Top Horizon"),
					((feature.getStartDepth() != null) ? String.valueOf(feature.getStartDepth()) + " m" : null), null,
					((featType.equals(FREDConstants.DRILLHOLE)) ? "Termination Depth" : "Base Horizon"),
					((feature.getFinishDepth() != null) ? String.valueOf(feature.getFinishDepth()) + " m" : null)},
					insertBodyFonts, 2, insertTableWidth, insertTableColWidths);
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
		if(sampleUtil.isAllowedReadSample(user, sample)) {
			Font[] bodyFonts = new Font[] {fonts[1], fonts[0]};
			Font[] insertBodyFonts = new Font[] {fonts[1], fonts[0], fonts[0], fonts[1], fonts[0]};
			
			//if not OUTCROP then add sample depth data
			if (!sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
				PdfPTable table = new PdfPTable(2);
				table.setTotalWidth(bodyTableWidth);
				table.setLockedWidth(true);
				table.setWidths(bodyTableColWidths);
				table.setSpacingAfter(3 * MM_TO_PT);
				PDFUtil.addCells(table, new String[] {"Sample", SampleUtil.getDrillHoleDepthDescription(sample)}, new Font[] {fonts[2], fonts[3]});
				document.add(table);
			}
			
			//Collection Information
			PdfPTable table = new PdfPTable(2);
			table.setTotalWidth(bodyTableWidth);
			table.setLockedWidth(true);
			table.setWidths(bodyTableColWidths);
			table.setSpacingAfter(3 * MM_TO_PT);
			
			PDFUtil.addCell(table, "Collection Information", fonts[2], PdfPCell.ALIGN_LEFT, 2);
			Object[] collectors = sample.getCollectors().toArray();
			String[] collectorStr = new String[collectors.length];
			for (int i = 0; i < collectors.length; i++)
				collectorStr[i] = ((PersonRelationship) collectors[i]).getDisplayName();
			PDFUtil.addRepeatingCells(table, "Collectors", collectorStr, bodyFonts, false);
			PDFUtil.addCells(table, new String[] {"Collection Date", ((sample.getCollectionDate() != null) ? FREDUtil.formatDateForOutput(sample.getCollectionDate(), sample.getDateRounding()) : null)}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Stratigraphic Name", sample.getStratUnit()}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Fossils in Place", sample.getInPlace()}, bodyFonts);
			Object[] sentTos = sample.getSentTos().toArray();
			String[] sentToStr = new String[sentTos.length];
			for (int i = 0; i < sentTos.length; i++)
				sentToStr[i] = SampleUtil.getSentToDescription((SentTo) sentTos[i]);
			PDFUtil.addRepeatingCells(table, "Sent To", sentToStr, bodyFonts, true);
			PDFUtil.addCells(table, new String[] {"Not Collected", sample.getNotCollected()}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Significance/Comments", sample.getSignificance()}, bodyFonts);
		
			document.add(table);
			
			//Stratigraphy
			table = new PdfPTable(2);
			table.setTotalWidth(bodyTableWidth);
			table.setLockedWidth(true);
			table.setWidths(bodyTableColWidths);
			table.setSpacingAfter(3 * MM_TO_PT);
			
			PDFUtil.addCell(table, "Stratigraphy", fonts[2], PdfPCell.ALIGN_LEFT, 2);
			
			PDFUtil.addTable(table, new String[] {"Inferred Stage",
					((sample.getInferredStage() != null) ? StageUtil.getStageDescription(sample.getInferredStage()) : null),
					"", "Known Stage",
					((sample.getKnownStage() != null) ? StageUtil.getStageDescription(sample.getKnownStage()) : null)},
					insertBodyFonts, 2, insertTableWidth, insertTableColWidths);
			Object[] relationships = sampleUtil.getRelationships(sample, "Sample", "nearby").toArray();
			String[] relationshipStr = new String[relationships.length];
			for (int i = 0; i < relationships.length; i++)
				relationshipStr[i] = FeatureUtil.getFeatureIdentifyingName(((Relationship) relationships[i]).getFeature());
			PDFUtil.addRepeatingCells(table, "Samples Nearby", relationshipStr, bodyFonts, false);			
			relationships = sampleUtil.getRelationships(sample, "Sample", new String[] {"above", "below"}).toArray();
			relationshipStr = new String[relationships.length];
			for (int i = 0; i < relationships.length; i++)
				relationshipStr[i] = SampleUtil.getRelationshipDescription((Relationship) relationships[i]);
			PDFUtil.addRepeatingCells(table, "Sample Relationships", relationshipStr, bodyFonts, false);			
			relationships = sampleUtil.getRelationships(sample, "Stratigraphic", new String[] {"above top", "above base", "below top", "below base"}).toArray();
			relationshipStr = new String[relationships.length];
			for (int i = 0; i < relationships.length; i++)
				relationshipStr[i] = SampleUtil.getRelationshipDescription((Relationship) relationships[i]);
			PDFUtil.addRepeatingCells(table, "Strat. Relationships", relationshipStr, bodyFonts, true);			
			PDFUtil.addCells(table, new String[] {"Column/Map", sample.getColumnMap()}, bodyFonts);
			PDFUtil.addTable(table, new Object[] {"Dip", sample.getDip(), null, "Dip Direction", sample.getDipDirection()}, insertBodyFonts, 2, insertTableWidth, insertTableColWidths);
			PDFUtil.addTable(table, new Object[] {"Strike", sample.getStrike(), null, "Facing", sample.getFacing()}, insertBodyFonts, 2, insertTableWidth, insertTableColWidths);
		
			document.add(table);

			//Sedimentary Features
			table = new PdfPTable(2);
			table.setTotalWidth(bodyTableWidth);
			table.setLockedWidth(true);
			table.setWidths(bodyTableColWidths);
			
			PDFUtil.addCell(table, "Sedimentary Features", fonts[2], PdfPCell.ALIGN_LEFT, 2);			
			PDFUtil.addCells(table, new String[] {"Grain Size", SampleUtil.getGrainSizeDescription(sample)}, bodyFonts);
			PDFUtil.addTable(table, new String[] {"Bedding Thickness", ((sample.getBedThickness() != null) ? sample.getBedThickness().getName() : null), null, "Bedding Features", SampleUtil.getBeddingDescription(sample)}, insertBodyFonts, 2, insertTableWidth, insertTableColWidths);
			PDFUtil.addTable(table, new String[] {"Weathering", ((sample.getWeathering() != null) ? sample.getWeathering().getName() : null), null, "Hardness", ((sample.getHardness() != null) ? sample.getHardness().getName() : null)}, insertBodyFonts, 2, insertTableWidth, insertTableColWidths);
			PDFUtil.addTable(table, new String[] {"Carbonate", ((sample.getCarbonate() != null) ? sample.getCarbonate().getName() : null), null, "Colour", SampleUtil.getColourDescription(sample)}, insertBodyFonts, 2, insertTableWidth, insertTableColWidths);
			Object[] sedFeatures = sample.getSedimentaryFeatures().toArray();
			String[] sedFeaturesStr = new String[sedFeatures.length];
			for (int i = 0; i < sedFeatures.length; i++)
				sedFeaturesStr[i] = SampleUtil.getSedFeatureDescription((SedimentaryFeature) sedFeatures[i]);
			PDFUtil.addRepeatingCells(table, "Additional Features", sedFeaturesStr, bodyFonts, false);	
			PDFUtil.addCells(table, new String[] {"Inferred Environment", sample.getDepositionEnv()}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Nature of Rock Unit", sample.getRockNature()}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Correspondence", sample.getCorrespondence()}, bodyFonts);
			
			document.add(table);
		}
	}
	
	private void writeRecord(Record record, Document document, Font[] fonts) throws StorageAccessException, DocumentException, NamingException, SQLException {
		if(recordUtil.isAllowedReadRecord(user, record) && recordUtil.getRecordType(record).equals(FREDConstants.PALEONTOLOGICAL)) {
			Paleontology palRecord = record.getPaleontology();
			Font[] bodyFonts = new Font[] {fonts[1], fonts[0]};
			Font taxonomicNameFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);

			PdfPTable table = new PdfPTable(2);
			table.setTotalWidth(bodyTableWidth);
			table.setLockedWidth(true);
			table.setWidths(bodyTableColWidths);
			table.setSpacingAfter(3 * MM_TO_PT);
			
			PDFUtil.addCell(table, "Paleontology Inforamtion", fonts[2], PdfPCell.ALIGN_LEFT, 2);
			Object[] identifiers = palRecord.getIdentifiers().toArray();
			String[] identifiersStr = new String[identifiers.length];
			for (int i = 0; i < identifiers.length; i++)
				identifiersStr[i] = ((PersonRelationship) identifiers[i]).getDisplayName();
			PDFUtil.addRepeatingCells(table, "Identifiers", identifiersStr, bodyFonts, false);
			PDFUtil.addCells(table, new String[] {"Identification Date", ((palRecord.getIdentificationDate() != null) ? FREDUtil.formatDateForOutput(palRecord.getIdentificationDate(), palRecord.getDateRounding()) : null)}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Identification Date", ((palRecord.getIdentificationDate() != null) ? FREDUtil.formatDateForOutput(palRecord.getIdentificationDate(), palRecord.getDateRounding()) : null)}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Stage", ((palRecord.getStage() != null) ? StageUtil.getStageDescription(palRecord.getStage()) : null)}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Stage Comments", palRecord.getStageComments()}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Lab", ((palRecord.getLabSection() != null) ? RecordUtil.getLabDescription(palRecord.getLabSection()) : null)}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Lab Number", palRecord.getLabNumber()}, bodyFonts);
			PDFUtil.addCells(table, new String[] {"Collection Comments", palRecord.getCollectionComments()}, bodyFonts);
			
			//taxa (Pal list)
			if (recordUtil.isAllowedReadPalList(user, palRecord) && palRecord.getListEntries() != null) {
				PdfPTable taxaTable = new PdfPTable(5);
				taxaTable.setTotalWidth(bodyTableWidth);
				taxaTable.setLockedWidth(true);
				taxaTable.setWidths(new float[] {35 * MM_TO_PT, 35 * MM_TO_PT, 35 * MM_TO_PT, 35 * MM_TO_PT, 35 * MM_TO_PT});
				taxaTable.setSpacingAfter(3 * MM_TO_PT);

				for (Iterator j = recordUtil.getTaxonomicGroups(palRecord).iterator(); j.hasNext(); ) {
					TaxonomicGroup taxaGroup = (TaxonomicGroup) j.next();
					PDFUtil.addCell(taxaTable, taxaGroup.getName(), fonts[1], PdfPCell.ALIGN_LEFT, 5);
					if (recordUtil.getListEntries(palRecord, taxaGroup).size() > 0) {
					PDFUtil.addCells(taxaTable, new String[] {"Taxonomic Name", "Author", "Spec Count", "Spec Coord", "Comments"}, new Font[] {fonts[5], fonts[5], fonts[5], fonts[5], fonts[5]});
						for (Iterator k = recordUtil.getListEntries(palRecord, taxaGroup).iterator(); k.hasNext(); ) {
							PaleontologyListEntry taxa = (PaleontologyListEntry) k.next();
							PDFUtil.addCells(taxaTable, new Object[] {taxa.getTaxonomicName(), taxa.getTaxon().getAuthor(), taxa.getSpecimenCount(), taxa.getSpecimenCoords(), taxa.getComments()},
									new Font[] {taxonomicNameFont, fonts[0], fonts[0], fonts[0], fonts[0]});
						}
					} else {
						PDFUtil.addCell(taxaTable, "No fossils listed", fonts[0], PdfPCell.ALIGN_LEFT, 5);
					}
				}
				PdfPCell cell = new PdfPCell(taxaTable);
				cell.setBorder(PdfPCell.NO_BORDER);
				table.addCell(cell);
			}
			document.add(table);
		}
	}
	
}
