package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.PersonRelationship;
import nz.cri.gns.fred.model.FREDRecord;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.PDFUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.StageUtil;
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
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.util.function.Function;
import nz.cri.gns.fred.FREDHibernateServlet;
import nz.cri.gns.fred.model.Country;
import nz.cri.gns.fred.site.util.SiteModel;
import static nz.cri.gns.fred.util.PDFUtil.NOT_AVAILABLE;
import nz.cri.gns.fred.util.SiteModelUtil;
import org.apache.commons.lang3.StringEscapeUtils;

public class FRFormServlet extends FREDHibernateServlet implements PdfPageEvent {

    private static final long serialVersionUID = 20050818L;

    private HttpServletResponse response;
    private DAOFactory factory;
    private RecordUtil recordUtil;
    private SampleUtil sampleUtil;
    private FeatureUtil featureUtil;
    private User user;

    private Date generateDate;
    private String username;
    private PdfTemplate[] templates;
    private int formNumber;
    private BaseFont baseFont;
    private FrNumber currentFrNumber = null;

    /*private boolean confidFlag;
	private boolean workingFlag; */
    private static final float MM_TO_PT = 2.8346f;

    private static final float bodyTableWidth = 175 * MM_TO_PT;
    private static final float[] bodyTableColWidths = new float[]{45 * MM_TO_PT, 130 * MM_TO_PT};

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.response = response;
            this.factory = FredHibernate.get().getDAOFactory();
            this.recordUtil = new RecordUtil(factory);
            this.sampleUtil = new SampleUtil(factory);
            this.featureUtil = new FeatureUtil(factory);
            this.user = (User) request.getSession().getAttribute(User.USER_ATTRIBUTE);
            this.generateDate = new Date();
            this.username = ((user != null) ? user.getGivenName() + " " + user.getFamilyName() : "unknown");

            List<FREDRecord> records = new Vector<FREDRecord>();
            List<Sample> samples = new Vector<Sample>();
            List<Feature> features = new Vector<Feature>();

            if (request.getParameter("FeatureID") != null) {
                // FeatureID is a new parameter that will fetch the feature information
                // including all the samples and records that are owned by that feature.
                // It replaces FeatIDs/SampIDs/RecIDs that had been built with FeatureUtil.getFullLocalityPDFURL
                try {
                    Feature feature = featureUtil.getFeature(Integer.parseInt(request.getParameter("FeatureID")));
                    features.add(feature);
                    for (Sample sample : feature.getSamples()) {
                        if (!FREDConstants.OUTCROP.equals(feature.getFeatureType())) {
                            samples.add(sample);
                        }
                        for (FREDRecord record : sample.getRecords()) {
                            records.add(record);
                        }
                    }
                } catch (Exception _e) {
                }
            } else {
                if (request.getParameter("RecIDs") != null) {
                    String[] recIDs = request.getParameterValues("RecIDs");
                    for (int i = 0; i < recIDs.length; i++) {
                        try {
                            FREDRecord record = recordUtil.getRecord(Integer.parseInt(recIDs[i]));
                            records.add(record);
                        } catch (Exception _e) {
                        }
                    }
                }
                if (request.getParameter("SampIDs") != null) {
                    String[] sampIDs = request.getParameterValues("SampIDs");
                    for (int i = 0; i < sampIDs.length; i++) {
                        try {
                            Sample sample = sampleUtil.getSample(Integer.parseInt(sampIDs[i]));
                            if (sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
                                features.add(sample.getFeature());
                            } else {
                                samples.add(sample);
                            }
                        } catch (Exception _e) {
                        }
                    }
                }
                if (request.getParameter("FeatIDs") != null) {
                    String[] featIDs = request.getParameterValues("FeatIDs");
                    for (int i = 0; i < featIDs.length; i++) {
                        try {
                            Feature feature = featureUtil.getFeature(Integer.parseInt(featIDs[i]));
                            features.add(feature);
                        } catch (Exception _e) {
                        }
                    }
                }
            }
            templates = new PdfTemplate[records.size() + samples.size() + features.size()];
            formNumber = 0;
            Collections.sort(records);
            Collections.sort(samples);
            Collections.sort(features);
            makePdf(records, samples, features);
        } catch (Exception e) {
            System.out.println("************************************ " + new java.util.Date());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    private void makePdf(List<FREDRecord> records, List<Sample> samples, List<Feature> features) throws DocumentException, IOException, NamingException, SQLException {
        Document document = new Document(PageSize.A4, 20 * MM_TO_PT, 15 * MM_TO_PT, 15 * MM_TO_PT, 20 * MM_TO_PT);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        //writer.setEncryption(true, null, null, PdfWriter.AllowPrinting | PdfWriter.AllowScreenReaders | PdfWriter.AllowCopy | PdfWriter.AllowAssembly);
        writer.setPageEvent(this);
        document.open();

        //initialise template array
        for (int i = 0; i < templates.length; i++) {
            templates[i] = writer.getDirectContent().createTemplate(20, 20);
        }

        Font[] fonts = new Font[4];
        fonts[0] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
        fonts[1] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
        //fonts[1].setColor(40, 22, 111);
        fonts[2] = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
        //fonts[2].setColor(40, 22, 111);
        fonts[3] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);

        if (features.size() > 0) {
            int i = 0;
            for (Feature feature : features) {
                try {
                    writeHeader(feature, document, "Locality");
                    writeLocality(feature, document, fonts);
                    if (feature.getFeatureType().equals(FREDConstants.OUTCROP)) {
                        writeSample(feature, document, fonts);
                    }
                    if (++i < features.size()) {
                        endForm(document, writer, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (samples.size() + records.size() > 0) {
                endForm(document, writer, true);
            }
        }
        if (samples.size() > 0) {
            int i = 0;
            for (Sample sample : samples) {
                try {
                    writeHeader(sample, document);
                    writeSample(sample, document, fonts);
                    if (++i < samples.size()) {
                        endForm(document, writer, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (records.size() > 0) {
                endForm(document, writer, true);
            }
        }
        if (records.size() > 0) {
            int i = 0;
            for (FREDRecord record : records) {
                try {
                    writeHeader(record, document);
                    writeRecord(record, document, fonts);
                    if (++i < records.size()) {
                        endForm(document, writer, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        endForm(document, writer, false);
        document.close();
    }

    private void endForm(Document document, PdfWriter writer, boolean newPage) throws DocumentException {
        templates[formNumber].beginText();
        templates[formNumber].setFontAndSize(baseFont, 7);
        templates[formNumber].setTextMatrix(0, 0);
        templates[formNumber].showText("" + (writer.getPageNumber()));
        templates[formNumber].endText();
        if (newPage) {
            document.newPage();
            document.setPageCount(1);
            formNumber++;
        }
    }

    private void writeHeader(Feature feature, Document document, String formType) throws DocumentException, MalformedURLException, IOException, NamingException, SQLException {
        Font[] fonts = new Font[8];
        fonts[0] = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);
        fonts[1] = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
        fonts[2] = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
        fonts[3] = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD);
        fonts[4] = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
        fonts[5] = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
        fonts[6] = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
        fonts[7] = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD);

        //Set FRNumber
        currentFrNumber = feature.getFrNumber();

        PdfPTable table = new PdfPTable(3);
        table.setTotalWidth(bodyTableWidth);
        table.setLockedWidth(true);
        table.setWidths(new float[]{23 * MM_TO_PT, 17 * MM_TO_PT, 135 * MM_TO_PT});
        table.setSpacingAfter(2 * MM_TO_PT);

        //Logo
        String realPath = getServletContext().getRealPath("/images") + "/";
        Image image = Image.getInstance(realPath + "gsnz_logo_big.png");
        image.scaleToFit(20 * MM_TO_PT, 20 * MM_TO_PT);
        PdfPCell cell = new PdfPCell(image);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        image = Image.getInstance(realPath + "GNS_logo_black.png");
        image.scaleToFit(12 * MM_TO_PT, 20 * MM_TO_PT);
        cell = new PdfPCell(image);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        //Header Text
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setTotalWidth(135 * MM_TO_PT);
        headerTable.setLockedWidth(true);
        headerTable.setWidths(new float[]{5 * MM_TO_PT, 75 * MM_TO_PT, 55 * MM_TO_PT});

        PDFUtil.addCells(headerTable, new String[]{"", "NEW ZEALAND FOSSIL RECORD FILE", "FOSSIL RECORD NUMBER"}, new Font[]{fonts[0], fonts[6], fonts[1]}, new int[]{PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_RIGHT});
        FrNumber frNumber = feature.getFrNumber();
        PDFUtil.addCells(headerTable, new String[]{"", formType + " Record", ((frNumber != null) ? frNumber.getFrNumber() : "____/f_____")},
                 new Font[]{fonts[0], fonts[4], fonts[3]}, new int[]{PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_RIGHT});

        PDFUtil.addCell(headerTable, "", fonts[0]);

        //Masterfile text
        PdfPTable mfTable = new PdfPTable(2);
        mfTable.setTotalWidth(75 * MM_TO_PT);
        mfTable.setLockedWidth(true);
        mfTable.setWidths(new float[]{18 * MM_TO_PT, 57 * MM_TO_PT});

        PDFUtil.addCells(mfTable, new String[]{"Masterfile:", valueOrNA(feature.getMasterFile(), mf -> mf.getName())}, new Font[]{fonts[1], fonts[0]});
        String approveStr = valueOrEmpty(feature.getAudit().getApprovedBy(), a -> a.getFullName())
                + valueOrEmpty(feature.getAudit().getApprovedDate(), ad ->  " " + FREDUtil.formatDateForOutput(ad));
        PDFUtil.addCells(mfTable, new String[]{"Approved:", approveStr}, new Font[]{fonts[1], fonts[0]});
        if (feature.getAudit().getCuratorComments() != null) {
            PDFUtil.addCells(mfTable, new String[]{"Comments:", valueOrEmpty(feature.getAudit().getCuratorComments())}, new Font[]{fonts[1], fonts[0]});
        }
        cell = new PdfPCell(mfTable);
        cell.setBorder(PdfPCell.NO_BORDER);
        headerTable.addCell(cell);

        //locality text
        PdfPTable localityTable = new PdfPTable(1);
        localityTable.setTotalWidth(55 * MM_TO_PT);
        localityTable.setLockedWidth(true);
        localityTable.setWidths(new float[]{55 * MM_TO_PT});
        if (feature.getYardFrNumber() != null) {
            PDFUtil.addCell(localityTable, "(" + feature.getYardFrNumber().getFrNumber() + ")", fonts[5], PdfPCell.ALIGN_RIGHT, 1);
        }
        if (feature.getFrNumber() != null) {
            PDFUtil.addCell(localityTable, "www.fred.org.nz/locality/" + feature.getFrNumber().getFrNumber(), fonts[7], PdfPCell.ALIGN_RIGHT, 1);
        }
        PDFUtil.addCell(localityTable, feature.getFeatureType(), fonts[5], PdfPCell.ALIGN_RIGHT, 1);
        cell = new PdfPCell(localityTable);
        cell.setBorder(PdfPCell.NO_BORDER);
        headerTable.addCell(cell);

        cell = new PdfPCell(headerTable);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        document.add(table);
    }

    private void writeHeader(Sample sample, Document document) throws MalformedURLException, DocumentException, IOException, NamingException, SQLException {
        writeHeader(sample.getFeature(), document, ((sample.getFeature().getFeatureType().equals("Vertical Section")) ? "V. Section" : sample.getFeature().getFeatureType()) + " Sample");
    }

    private void writeHeader(FREDRecord record, Document document) throws MalformedURLException, DocumentException, IOException, NamingException, SQLException {
        writeHeader(record.getSample().getFeature(), document, RecordUtil.getRecordType(record));
    }

    private void writeLocality(Feature feature, Document document, Font[] fonts) throws StorageAccessException, DocumentException, NamingException, SQLException, IOException {
        Font[] bodyFonts = new Font[]{fonts[1], fonts[0]};
        PdfPTable table = new PdfPTable(2);
        table.setTotalWidth(bodyTableWidth);
        table.setLockedWidth(true);
        table.setWidths(bodyTableColWidths);
        table.setSpacingAfter(3 * MM_TO_PT);

        //confidFlag = false;
        //workingFlag = FREDConstants.WORKING.equals(feature.getAudit().getStatus()) || FREDConstants.REJECTED.equals(feature.getAudit().getStatus());
        if (featureUtil.isAllowedReadFeatureSite(user, feature)) {
            //Location Information
            PDFUtil.addCell(table, "Location", fonts[2], PdfPCell.ALIGN_LEFT, 2);
            String featType = feature.getFeatureType();
            String featTypeLbl;
            if (featType.equals(FREDConstants.OUTCROP)) {
                featTypeLbl = "Field Number";
            } else if (featType.equals(FREDConstants.DRILLHOLE)) {
                featTypeLbl = "Drillhole Name";
            } else {
                featTypeLbl = "Section Name";
            }
            PDFUtil.addCells(table, new String[]{featTypeLbl, feature.getFeatureName()}, bodyFonts);
            PDFUtil.addCell(table, "Original Grid Reference", fonts[1]);
            if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
                Datum datum = SiteModelUtil.getFREDDatum(feature);
                Coordinate coord = SiteModelUtil.getFREDCoordinate(feature);
                PDFUtil.addCell(table, datum.getHumanStringFor(coord).replaceAll("Geographic ", ""), fonts[0]);
                if (!datum.getName().equals("NZMG")) {
                    try {
                        Datum nzmgDatum = DatumFactory.createDatum("NZMG");
                        Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(datum, coord);
                        PDFUtil.addCells(table, new String[]{"Converted Grid Reference", nzmgDatum.getHumanStringFor(nzmgCoord)}, bodyFonts);
                    } catch (Exception e) {
                    }
                }
            } else {
                PDFUtil.addCell(table, "", fonts[0]);
            }
//			SiteView sv = null;
            SiteModel sm = SiteModelUtil.getSite(feature);
            Country country = null;
            if (sm != null) {
//				sv = feature.getSiteView();
                LatLong ll = SiteModelUtil.getSiteLatLong(feature);
                PDFUtil.addCells(table, new String[]{"Converted Dec. Lat/Long", ll.getLatAsDecDegree(5) + " " + ll.getLongAsDecDegree(5) + " (WGS84)"}, bodyFonts);
                country = featureUtil.getCountry(sm.getCountryCode());
            }
            PDFUtil.addCells(table, new Object[]{"Map Year", valueOrNA(feature.getMapYear(), my -> String.valueOf(my))}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Method", valueOrNA(sm, sm1 -> valueOrNA(sm1.getMethodId(), mid -> String.valueOf(mid)))}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Accuracy", valueOrNA(sm, sm1 -> valueOrNA(sm1.getAccuracy(), a -> String.valueOf(a) + " m"))}, bodyFonts);

            if (featureUtil.isAllowedReadFeature(user, feature)) {
                // Convert HTML code back to String here
                String cleanedLocalityString = StringEscapeUtils.unescapeHtml4(feature.getLocality());
                PDFUtil.addCells(table, new String[]{"Locality", cleanedLocalityString}, bodyFonts);
                PDFUtil.addCells(table, new String[]{"Country", valueOrNA(country, c -> c.toString())}, bodyFonts);
                PDFUtil.addCells(table, new String[]{"Coordinate Comments", valueOrNA(feature.getCoordComments())}, bodyFonts);
                PDFUtil.addCells(table, new String[]{"Locality Comments", valueOrNA(feature.getComments())}, bodyFonts);
                if (!featType.equals(FREDConstants.OUTCROP)) {
                    PDFUtil.addCells(table, new Object[]{((featType.equals(FREDConstants.DRILLHOLE)) ? "Operating Company" : "Section Collector"), valueOrNA(feature.getPerson(), p -> p.getName())}, bodyFonts);
                    PDFUtil.addCells(table, new String[]{((featType.equals(FREDConstants.DRILLHOLE)) ? "Spud Date" : "Sampling Start Date"),
                        valueOrNA(feature.getStartDate(), sd -> FREDUtil.formatDateForOutput(sd, feature.getStartDateRounding()))}, bodyFonts);
                    PDFUtil.addCells(table, new String[]{"Completion Date",
                        valueOrNA(feature.getFinishDate(), fd -> FREDUtil.formatDateForOutput(fd, feature.getFinishDateRounding()))}, bodyFonts);
                    if (featType.equals(FREDConstants.DRILLHOLE)) {
                        PDFUtil.addCells(table, new String[]{"Licence Area", valueOrNA(feature.getDrillholeLicenceName())}, bodyFonts);
                    }
                    PDFUtil.addCells(table, new String[]{"Datum Type", valueOrNA(feature.getDatumType())}, bodyFonts);
                    PDFUtil.addCells(table, new String[]{"Datum Elevation", valueOrNA(feature.getDatumElevation(), de -> FeatureUtil.formatDepthForOutput(de, feature.getDepthUnit()) + " asl")}, bodyFonts);
                    PDFUtil.addCells(table, new String[]{((featType.equals(FREDConstants.DRILLHOLE)) ? "Kick-off Depth" : "Top Horizon"),
                        valueOrNA(feature.getStartDepth(), sd -> FeatureUtil.formatDepthForOutput(sd, feature.getDepthUnit()))}, bodyFonts);
                    PDFUtil.addCells(table, new String[]{((featType.equals(FREDConstants.DRILLHOLE)) ? "Termination Depth" : "Base Horizon"),
                        valueOrNA(feature.getFinishDepth(), fd -> FeatureUtil.formatDepthForOutput(fd, feature.getDepthUnit()))}, bodyFonts);
                }
                if (!FREDUtil.isEmpty(feature.getMetaCats())) {
                    PDFUtil.addCells(table, new Object[]{"Attached Images", "Images have been attached to this locality and can be viewed online"}, bodyFonts);
                }
            } else {
                PDFUtil.addCell(table, "", fonts[1], PdfPCell.ALIGN_LEFT, 2);
                PDFUtil.addCell(table, "You do not have rights to view full data for this locality", fonts[1], PdfPCell.ALIGN_LEFT, 2);
            }
        } else {
            PDFUtil.addCell(table, "You do not have rights to view this locality", fonts[1], PdfPCell.ALIGN_LEFT, 2);
        }
        document.add(table);
    }

    private void writeSample(Feature feature, Document document, Font[] fonts) throws MalformedURLException, DocumentException, IOException, NamingException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException, StorageAccessException {
        Set<Sample> samples = feature.getSamples();
        for (Sample sample : samples) {
            writeSample(sample, document, fonts);
        }
    }

    private void writeSample(Sample sample, Document document, Font[] fonts) throws DocumentException, MalformedURLException, IOException, NamingException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException, StorageAccessException {
        //confidFlag = sampleUtil.isSampleConfidential(sample);
        //workingFlag = FREDConstants.WORKING.equals(sample.getAudit().getStatus()) || FREDConstants.REJECTED.equals(sample.getAudit().getStatus());
        if (sampleUtil.isAllowedReadSample(user, sample)) {
            Font[] bodyFonts = new Font[]{fonts[1], fonts[0]};

            //if not OUTCROP then add name and sample depth data
            if (!sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
                PdfPTable table = new PdfPTable(2);
                table.setTotalWidth(bodyTableWidth);
                table.setLockedWidth(true);
                table.setWidths(bodyTableColWidths);
                table.setSpacingAfter(3 * MM_TO_PT);
                PDFUtil.addCells(table, new String[]{((sample.getFeature().getFeatureType().equals(FREDConstants.DRILLHOLE)) ? "Drillhole" : "Section") + " Name", sample.getFeature().getFeatureName()}, new Font[]{fonts[2], fonts[3]});
                PDFUtil.addCells(table, new String[]{"Sample", SampleUtil.getDrillHoleDepthDescription(sample)}, new Font[]{fonts[2], fonts[3]});
                if (sample.getFrNumber() != null) {
                    PDFUtil.addCells(table, new String[]{"Sample FRNumber", sample.getFrNumber().getFrNumber()}, new Font[]{fonts[2], fonts[3]});
                }
                if (sample.getYardFrNumber() != null) {
                    PDFUtil.addCells(table, new String[]{"Sample Yard FRNumber", sample.getYardFrNumber().getFrNumber()}, new Font[]{fonts[2], fonts[3]});
                }
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
            for (int i = 0; i < collectors.length; i++) {
                collectorStr[i] = ((PersonRelationship) collectors[i]).getDisplayName();
            }
            PDFUtil.addRepeatingCells(table, "Collector(s)", collectorStr, bodyFonts, false);
            PDFUtil.addCells(table, new String[]{"Collection Date", valueOrNA(sample.getCollectionDate(), cd -> FREDUtil.formatDateForOutput(cd, sample.getDateRounding()))}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Fossils in Place", valueOrNA(sample.getInPlace())}, bodyFonts);
            Object[] sentTos = sample.getSentTos().toArray();
            String[] sentToStr = new String[sentTos.length];
            for (int i = 0; i < sentTos.length; i++) {
                sentToStr[i] = SampleUtil.getSentToDescription((SentTo) sentTos[i]);
            }
            PDFUtil.addRepeatingCells(table, "Sent To", sentToStr, bodyFonts, true);
            PDFUtil.addCells(table, new String[]{"Not Collected", valueOrNA(sample.getNotCollected())}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Significance/Comments", valueOrNA(sample.getSignificance())}, bodyFonts);
            document.add(table);

            //Stratigraphy
            table = new PdfPTable(2);
            table.setTotalWidth(bodyTableWidth);
            table.setLockedWidth(true);
            table.setWidths(bodyTableColWidths);
            table.setSpacingAfter(3 * MM_TO_PT);

            PDFUtil.addCell(table, "Stratigraphy", fonts[2], PdfPCell.ALIGN_LEFT, 2);
            PDFUtil.addCells(table, new String[]{"Stratigraphic Name", valueOrNA(sample.getStratUnit())}, bodyFonts);
            String inferredStageDesc = sample.getInferredStage() == null ? null : StageUtil.getStageDescription(sample.getInferredStage());
            PDFUtil.addCells(table, new String[]{"Inferred Stage",
                valueOrNA(inferredStageDesc)}, bodyFonts);
            String knownStageDesc = sample.getKnownStage() == null ? null : StageUtil.getStageDescription(sample.getKnownStage());
            PDFUtil.addCells(table, new String[]{"Known Stage",
                valueOrNA(knownStageDesc)}, bodyFonts);
            Object[] relationships = sampleUtil.getRelationships(sample, "Sample", "nearby").toArray();
            String[] relationshipStr = new String[relationships.length];
            for (int i = 0; i < relationships.length; i++) {
                relationshipStr[i] = FeatureUtil.getFeatureIdentifyingName(((Relationship) relationships[i]).getFeature());
            }
            PDFUtil.addRepeatingCells(table, "Samples Nearby", relationshipStr, bodyFonts, false);
            relationships = sampleUtil.getRelationships(sample, "Sample", new String[]{"above", "below"}).toArray();
            relationshipStr = new String[relationships.length];
            for (int i = 0; i < relationships.length; i++) {
                relationshipStr[i] = SampleUtil.getRelationshipDescription((Relationship) relationships[i]);
            }
            PDFUtil.addRepeatingCells(table, "Sample Relationships", relationshipStr, bodyFonts, false);
            relationships = sampleUtil.getRelationships(sample, "Stratigraphic", new String[]{"above top", "above base", "below top", "below base"}).toArray();
            relationshipStr = new String[relationships.length];
            for (int i = 0; i < relationships.length; i++) {
                relationshipStr[i] = SampleUtil.getRelationshipDescription((Relationship) relationships[i]);
            }
            PDFUtil.addRepeatingCells(table, "Strat. Relationships", relationshipStr, bodyFonts, true);
            PDFUtil.addCells(table, new String[]{"Column/Map", valueOrNA(sample.getColumnMap())}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Dip/Strike", valueOrNA(SampleUtil.getDipStrikeDescription(sample))}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Stratigraphy Comments", valueOrNA(sample.getStratComments())}, bodyFonts);
            document.add(table);

            //Sedimentary Features
            table = new PdfPTable(2);
            table.setTotalWidth(bodyTableWidth);
            table.setLockedWidth(true);
            table.setWidths(bodyTableColWidths);
            table.setSpacingAfter(3 * MM_TO_PT);

            PDFUtil.addCell(table, "Sedimentary Features", fonts[2], PdfPCell.ALIGN_LEFT, 2);
            PDFUtil.addCells(table, new String[]{"Grain Size", SampleUtil.getGrainSizeDescription(sample)}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Bedding Thickness", valueOrNA(sample.getBedThickness(), bt -> bt.getName())}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Bedding Features", valueOrNA(SampleUtil.getBeddingDescription(sample))}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Weathering", valueOrNA(sample.getWeathering(), w -> w.getName())}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Hardness", valueOrNA(sample.getHardness(), h -> h.getName())}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Carbonate", valueOrNA(sample.getCarbonate(), c -> c.getName())}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Colour", valueOrNA(SampleUtil.getColourDescription(sample))}, bodyFonts);
            Object[] sedFeatures = sample.getSedimentaryFeatures().toArray();
            String[] sedFeaturesStr = new String[sedFeatures.length];
            for (int i = 0; i < sedFeatures.length; i++) {
                sedFeaturesStr[i] = SampleUtil.getSedFeatureDescription((SedimentaryFeature) sedFeatures[i]);
            }
            PDFUtil.addRepeatingCells(table, "Additional Features", sedFeaturesStr, bodyFonts, false);
            PDFUtil.addCells(table, new String[]{"Inferred Environment", valueOrNA(sample.getDepositionEnv())}, bodyFonts);
            PDFUtil.addCells(table, new String[]{"Nature of Rock Unit", valueOrNA(sample.getRockNature())}, bodyFonts);
            document.add(table);

            //Consensus age
            for (nz.cri.gns.fred.model.SquirrelAgeView ageView : sample.getSquirrelAge()) {
                if (ageView.isDeterminedValue()) {
                    table = new PdfPTable(2);
                    table.setTotalWidth(bodyTableWidth);
                    table.setLockedWidth(true);
                    table.setWidths(bodyTableColWidths);
                    table.setSpacingAfter(3 * MM_TO_PT);

                    PDFUtil.addCell(table, "Consensus Age", fonts[2], PdfPCell.ALIGN_LEFT, 2);
                    PDFUtil.addCells(table, new String[]{"Consensus Age (wide)", String.format("%s - %s Ma", ageView.getWideBaseAge(), ageView.getWideTopAge())}, bodyFonts);
                    PDFUtil.addCells(table, new String[]{"Consensus Age (narrow)", String.format("%s - %s Ma", ageView.getNarrowBaseAge(), ageView.getNarrowTopAge())}, bodyFonts);

                    document.add(table);
                }
            }

            //Correspondence
            if (!FREDUtil.isEmpty(sample.getMetaCats()) || (sample.getCorrespondence() != null && !sample.getCorrespondence().isEmpty())) {
                table = new PdfPTable(2);
                table.setTotalWidth(bodyTableWidth);
                table.setLockedWidth(true);
                table.setWidths(bodyTableColWidths);

                PDFUtil.addCells(table, new String[]{"Correspondence", sample.getCorrespondence()}, new Font[]{fonts[2], fonts[0]});

                if (!FREDUtil.isEmpty(sample.getMetaCats())) {
                    PDFUtil.addCells(table, new Object[]{"Attached Images", "Images have been attached to this sample and can be viewed online"}, bodyFonts);
                }

                document.add(table);
            }
        }
    }

    private void writeRecord(FREDRecord record, Document document, Font[] fonts) throws StorageAccessException, DocumentException, NamingException, SQLException {
        Font[] bodyFonts = new Font[]{fonts[1], fonts[0]};

        //confidFlag = recordUtil.isRecordConfidential(record);
        //workingFlag = FREDConstants.WORKING.equals(record.getAudit().getStatus()) || FREDConstants.REJECTED.equals(record.getAudit().getStatus());
        //Locality information
        PdfPTable table = new PdfPTable(2);
        table.setTotalWidth(bodyTableWidth);
        table.setLockedWidth(true);
        table.setWidths(bodyTableColWidths);
        table.setSpacingAfter(3 * MM_TO_PT);

        if (recordUtil.isAllowedReadRecord(user, record)) {
            String featType = record.getSample().getFeature().getFeatureType();
            String featTypeLbl;
            switch (featType) {
                case FREDConstants.OUTCROP:
                    featTypeLbl = "Field Number";
                    break;
                case FREDConstants.DRILLHOLE:
                    featTypeLbl = "Drillhole Name";
                    break;
                default:
                    featTypeLbl = "Section Name";
                    break;
            }
            PDFUtil.addCells(table, new String[]{featTypeLbl, record.getSample().getFeature().getFeatureName()}, new Font[]{fonts[2], fonts[3]});
            //if not OUTCROP then add name and sample depth data
            if (!record.getSample().getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
                PDFUtil.addCells(table, new String[]{"Sample", SampleUtil.getDrillHoleDepthDescription(record.getSample())}, new Font[]{fonts[2], fonts[3]});
            }
            document.add(table);

            if (RecordUtil.getRecordType(record).equals(FREDConstants.PALEONTOLOGICAL)) {
                Paleontology palRecord = record.getPaleontology();
                Font taxonomicNameFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);

                table = new PdfPTable(2);
                table.setTotalWidth(bodyTableWidth);
                table.setLockedWidth(true);
                table.setWidths(bodyTableColWidths);
                table.setSpacingAfter(3 * MM_TO_PT);

                PDFUtil.addCell(table, "Paleontology", fonts[2], PdfPCell.ALIGN_LEFT, 2);
                Object[] identifiers = palRecord.getIdentifiers().toArray();
                String[] identifiersStr = new String[identifiers.length];
                for (int i = 0; i < identifiers.length; i++) {
                    identifiersStr[i] = ((PersonRelationship) identifiers[i]).getDisplayName();
                }
                PDFUtil.addRepeatingCells(table, "Identifier(s)", identifiersStr, bodyFonts, false);
                PDFUtil.addCells(table, new String[]{"Identification Date", valueOrNA(palRecord.getIdentificationDate(), d -> FREDUtil.formatDateForOutput(d, palRecord.getDateRounding()))}, bodyFonts);
                String stageDesc = palRecord.getStage() == null ? null : StageUtil.getStageDescription(palRecord.getStage());
                PDFUtil.addCells(table, new String[]{"Stage", valueOrNA(stageDesc)}, bodyFonts);
                PDFUtil.addCells(table, new String[]{"Stage Comments", valueOrNA(palRecord.getStageComments())}, bodyFonts);
                PDFUtil.addCells(table, new String[]{"Lab Number", valueOrNA(palRecord.getLabNumber(), ln -> RecordUtil.getLabNumberDescription(palRecord))}, bodyFonts);
                PDFUtil.addCells(table, new String[]{"Collection Comments", valueOrNA(palRecord.getCollectionComments())}, bodyFonts);

                if (!FREDUtil.isEmpty(record.getMetaCats())) {
                    PDFUtil.addCells(table, new String[]{"Attached Images", "Images have been attached to this record and can be viewed online"}, bodyFonts);
                }

                document.add(table);

                //taxa (Pal list)
                if (recordUtil.isAllowedReadPalList(user, palRecord) && palRecord.getListEntries() != null) {
                    //confidFlag = confidFlag || recordUtil.isPalListConfidential(palRecord);
                    for (TaxonomicGroup taxaGroup : recordUtil.getTaxonomicGroups(palRecord)) {
                        PdfPTable taxaTable = new PdfPTable(4);
                        taxaTable.setTotalWidth(bodyTableWidth);
                        taxaTable.setLockedWidth(true);
                        taxaTable.setWidths(new float[]{65 * MM_TO_PT, 25 * MM_TO_PT, 25 * MM_TO_PT, 60 * MM_TO_PT});
                        taxaTable.setSpacingAfter(3 * MM_TO_PT);
                        PDFUtil.addCell(taxaTable, taxaGroup.getName(), fonts[1], PdfPCell.ALIGN_LEFT, 5);
                        if (!recordUtil.getListEntries(palRecord, taxaGroup).isEmpty()) {
                            PDFUtil.addCells(taxaTable, new String[]{"Taxonomic Name", "Spec Count", "Spec Coord", "Comments"}, new Font[]{fonts[1], fonts[1], fonts[1], fonts[1], fonts[1]});
                            for (PaleontologyListEntry taxa : recordUtil.getListEntries(palRecord, taxaGroup)) {
                                PDFUtil.addCells(taxaTable, new Object[]{valueOr(taxa, t -> t.getTaxonomicName(), "no taxa identified"), taxa.getSpecimenCount(), taxa.getSpecimenCoords(), taxa.getComments()},
                                        new Font[]{taxonomicNameFont, fonts[0], fonts[0], fonts[0]});
                            }
                        } else {
                            PDFUtil.addCell(taxaTable, "No fossils listed", fonts[0], PdfPCell.ALIGN_LEFT, 4);
                        }
                        document.add(taxaTable);
                    }
                } else {
                    PdfPTable taxaTable = new PdfPTable(4);
                    taxaTable.setTotalWidth(bodyTableWidth);
                    taxaTable.setLockedWidth(true);
                    taxaTable.setWidths(new float[]{65 * MM_TO_PT, 25 * MM_TO_PT, 25 * MM_TO_PT, 60 * MM_TO_PT});
                    taxaTable.setSpacingAfter(3 * MM_TO_PT);
                    PDFUtil.addCell(taxaTable, "You do not have rights to view the taxonomic list for this record", fonts[1], PdfPCell.ALIGN_LEFT, 4);
                    document.add(taxaTable);
                }
            } else if (RecordUtil.getRecordType(record).equals(FREDConstants.ADOPTION)) {
                Adoption adoRecord = record.getAdoption();

                table = new PdfPTable(2);
                table.setTotalWidth(bodyTableWidth);
                table.setLockedWidth(true);
                table.setWidths(bodyTableColWidths);
                table.setSpacingAfter(3 * MM_TO_PT);

                PDFUtil.addCell(table, "Adopted Age", fonts[2], PdfPCell.ALIGN_LEFT, 2);
                Object[] adoptors = adoRecord.getAdoptors().toArray();
                String[] adoptorsStr = new String[adoptors.length];
                for (int i = 0; i < adoptors.length; i++) {
                    adoptorsStr[i] = ((PersonRelationship) adoptors[i]).getDisplayName();
                }
                PDFUtil.addRepeatingCells(table, "Adoptor(s)", adoptorsStr, bodyFonts, false);
                PDFUtil.addCells(table, new String[]{"Adoption Date", valueOrNA(adoRecord.getAdoptionDate(), ad -> FREDUtil.formatDateForOutput(ad, adoRecord.getDateRounding()))}, bodyFonts);
                String stageDesc = adoRecord.getStage() == null ? null : StageUtil.getStageDescription(adoRecord.getStage());
                PDFUtil.addCells(table, new String[]{"Stage", valueOrNA(stageDesc)}, bodyFonts);
                String cleanedCommentString = StringEscapeUtils.unescapeHtml4(adoRecord.getComments());
                PDFUtil.addCells(table, new String[]{"Comments", cleanedCommentString}, bodyFonts);

                if (!FREDUtil.isEmpty(record.getMetaCats())) {
                    PDFUtil.addCells(table, new String[]{"Attached Images", "Images have been attached to this record and can be viewed online"}, bodyFonts);
                }

                document.add(table);
            }
        } else {
            PDFUtil.addCell(table, "You do not have rights to view this record", fonts[1], PdfPCell.ALIGN_LEFT, 2);
            document.add(table);
        }
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        baseFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD).getBaseFont();
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();

        //footer
        String pageNumStr = "Page " + writer.getPageNumber() + " of ";
        String frNumStr = (currentFrNumber != null) ? currentFrNumber.getFrNumber() : "____/f_____";
        cb.beginText();
        cb.setFontAndSize(baseFont, 7);
        cb.setTextMatrix(document.left(), document.bottomMargin() - 10);
        cb.showText("Printed on " + generateDate + " by " + username + " from FRED, the computer database for the NZ Fossil Record File (FRF).");
        cb.setTextMatrix(document.right() - baseFont.getWidthPoint(frNumStr, 7), document.bottomMargin() - 10);
        cb.showText(frNumStr);
        cb.setTextMatrix(document.left(), document.bottomMargin() - 20);
        cb.showText("FRF is a nationally significant database administered by GSNZ and GNS Science");
        cb.setTextMatrix(document.right() - baseFont.getWidthPoint(pageNumStr + "0", 7), document.bottomMargin() - 20);
        cb.showText(pageNumStr);
        cb.endText();
        cb.addTemplate(templates[formNumber], document.right() - baseFont.getWidthPoint("0", 7), document.bottomMargin() - 20);

        //border
        cb.setRGBColorStroke(110, 110, 110);
        cb.setLineWidth(2);
        cb.rectangle(15 * MM_TO_PT, 10 * MM_TO_PT, 185 * MM_TO_PT, 277 * MM_TO_PT);
        cb.stroke();
        cb.restoreState();

        //watermark
        /*if (confidFlag) {
			PdfContentByte cb2 = writer.getDirectContentUnder();
			cb2.saveState();
			cb2.setRGBColorFill(255, 165, 165);
			cb2.beginText();
			cb2.setFontAndSize(baseFont, 80);
			cb2.showTextAligned(Element.ALIGN_CENTER, "Confidential", document.getPageSize().width() / 2, document.getPageSize().height() / 2, 60);
			cb2.endText();
			cb2.restoreState();
		} else if (workingFlag) {
			PdfContentByte cb2 = writer.getDirectContentUnder();
			cb2.saveState();
			cb2.setRGBColorFill(255, 165, 165);
			cb2.beginText();
			cb2.setFontAndSize(baseFont, 80);
			cb2.showTextAligned(Element.ALIGN_CENTER, "DRAFT", document.getPageSize().width() / 2, document.getPageSize().height() / 2, 60);
			cb2.endText();
			cb2.restoreState();
		} */
    }

    private String valueOrEmpty(String subject) {
        return valueOrEmpty(subject, Function.identity());
    }

    private <T> String valueOrEmpty(T subject, Function<T, String> valueFunc) {
        return valueOr(subject, valueFunc, "");
    }

    private String valueOrNA(String subject) {
        return valueOrNA(subject, Function.identity());
    }

    private <T> String valueOrNA(T subject, Function<T, String> valueFunc) {
        return valueOr(subject, valueFunc, NOT_AVAILABLE);
    }

    private <T> String valueOr(T subject, Function<T, String> valueFunc, String orElse) {
        String value = subject == null ? null : valueFunc.apply(subject);
        if (value == null || value.isBlank()) {
            return orElse;
        }
        return value;
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
    }

    @Override
    public void onParagraph(PdfWriter arg0, Document arg1, float arg2) {
    }

    @Override
    public void onParagraphEnd(PdfWriter arg0, Document arg1, float arg2) {
    }

    @Override
    public void onChapter(PdfWriter arg0, Document arg1, float arg2, Paragraph arg3) {
    }

    @Override
    public void onChapterEnd(PdfWriter arg0, Document arg1, float arg2) {
    }

    @Override
    public void onSection(PdfWriter arg0, Document arg1, float arg2, int arg3, Paragraph arg4) {
    }

    @Override
    public void onSectionEnd(PdfWriter arg0, Document arg1, float arg2) {
    }

    @Override
    public void onGenericTag(PdfWriter arg0, Document arg1, Rectangle arg2, String arg3) {
    }

}
