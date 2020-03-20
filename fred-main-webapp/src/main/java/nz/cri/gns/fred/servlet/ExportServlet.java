package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.hibernate.HibernateException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.SiteView;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.TaxonomicNameAndGroup;
import nz.cri.gns.fred.servlet.util.FredHelper;
import nz.cri.gns.fred.servlet.util.JspWriterImpl;
import nz.cri.gns.fred.util.AuditUtil;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.SiteUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.jsp.ExtranetTemplate;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.Datum.Coordinate;
import nz.cri.gns.util.map.Datum.LatLong;
import nz.cri.gns.util.map.DatumFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Was export.jsp.
 */
public class ExportServlet
        extends HttpServlet {

    private static final Logger log = Logger.getLogger(
            "nz.cri.gns.fred.servlet.ExportServlet");

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        /* Let's take a minute to muse over how aweful Microsoft Excel is.
        
         If you set the encoding to UTF-8, Excel assumes it's ISO8859-1 instead.
         If you set the encoding to UTF-16, Excel converts the BOM to þÿ in Cell A1.
         So you need to set the encoding to UTF-16LE. Screw you, Excel.
        
         */
        response.setCharacterEncoding("UTF-16LE"); // Read the blurb above.
        HttpSession session = request.getSession();
        FredHelper h = new FredHelper();
        DAOFactory factory = FredHibernate.get().getDAOFactory();

        try {
            User user = (User) h.getUser(session);
            FeatureUtil featureUtil = new FeatureUtil(factory);
            StageUtil stageUtil = new StageUtil(factory);
            SampleUtil sampleUtil = new SampleUtil(factory);
            RecordUtil recordUtil = new RecordUtil(factory);

            TreeSet<Sample> samples = new TreeSet<Sample>();

            if (request.getParameter("featId") != null) {
                Integer featureId;
                try {
                    featureId = Integer.parseInt(request.getParameter("featId"));
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Invalid featId. Not a number");
                    return;
                }
                Feature feature = featureUtil.getFeature(featureId);
                for (Sample sample : feature.getSamples()) {
                    samples.add(sample);
                }
            } else if (request.getParameter("sampId") != null) {
                samples.add(sampleUtil.getSample(Integer.parseInt(
                        request.getParameter("sampId"))));
            } else if (session.getAttribute("FRED.features") != null && ((List<Feature>) session.getAttribute(
                    "FRED.features")).size() > 0) {
                List<Feature> features = (List<Feature>) session.getAttribute(
                        "FRED.features");
                for (Feature feature : features) {
                    FredHibernate.get().currentSession().refresh(feature);
                    if (feature.getSamples() != null) {
                        for (Sample sample : feature.getSamples()) {
                            samples.add(sample);
                        }
                    }
                }
            }

            try (JspWriterImpl out = new JspWriterImpl(response.getWriter())) {
                CSVPrinter c = new CSVPrinter(out, CSVFormat.EXCEL);

                if (samples.size() > 0) {
                    // Content type, disposition.
                    response.setContentType("text/csv");
                    StringBuilder ctSb = new StringBuilder(
                            "attachment;filename=fred-export-");
                    ctSb.append(new SimpleDateFormat("yyyy-MM-dd").format(
                            new Date()));
                    ctSb.append(".csv");
                    response.addHeader("Content-Disposition", ctSb.toString());

                    boolean collectionFlag = (request.getParameter("collection") != null);
                    boolean stratigraphyFlag = (request.getParameter(
                            "stratigraphy") != null);
                    boolean sedimentaryFlag = (request.getParameter(
                            "sedimentary") != null);
                    boolean localityFlag = collectionFlag || stratigraphyFlag || sedimentaryFlag;
                    boolean adoptionFlag = (request.getParameter("adoption") != null);
                    boolean paleontologyFlag = (request.getParameter(
                            "paleontology") != null);
                    boolean palListFlag = (request.getParameter("palList") != null);

                    //file header
                    c.printRecord(
                            "**************************************************************************************************************");
                    c.printRecord(
                            "Data downloaded from FRED (https://www.fred.org.nz) on " + FREDUtil.formatDateForOutput(
                                    new Date()));
                    c.printRecord(
                            "FRED is the computer database for the NZ Fossil Record File (FRF), which is a nationally significant database administrated by GSNZ and GNS Science                                ");
                    c.printRecord(
                            "Please acknowledge use of this data in publications, reports and presentations.");
                    c.printRecord(
                            "**************************************************************************************************************");
                    c.println();

                    if (localityFlag) {
                        c.printRecord("********");
                        c.printRecord("Locality");
                        c.printRecord("********");

                        writeLocalityHeader(c);

                        final String[] coordHeader = new String[]{
                            "Original Grid Reference", "NZMG Easting",
                            "NZMG Northing", "NZGD49 Latitude",
                            "NZGD49 Longitude", "Map Year", "Method", "Accuracy",
                            "Locality", "Country", "Coordinate Comments",
                            "Locality Comments"};
                        for (String each : coordHeader) {
                            c.print(each);
                        }

                        final String[] collectionHeader = new String[]{
                            "Collectors", "Collection Date", "Fossils in Place",
                            "Sent To", "Not Collected", "Significance/Comments"};
                        if (collectionFlag) {
                            for (String each : collectionHeader) {
                                c.print(each);
                            }
                        }
                        if (stratigraphyFlag) {
                            final String[] stratigraphyHeader = new String[]{
                                "Stratigraphic Name", "Inferred Stage Lower",
                                "Inferred Lower Modifier",
                                "Inferred Stage Upper",
                                "Inferred Upper Modifier", "Inferred Age Start",
                                "Inferred Age Stop", "Known Stage Lower",
                                "Known Lower Modifier", "Known Stage Upper",
                                "Known Upper Modifier", "Known Age Start",
                                "Known Age Stop", "Samples Nearby",
                                "Sample Relationships",
                                "Stratigraphic Relationships", "Column/Map",
                                "Dip", "Dip Direction", "Strike", "Facing",
                                "Stratigraphy Comments"};
                            for (String each : stratigraphyHeader) {
                                c.print(each);
                            }
                        }
                        if (sedimentaryFlag) {
                            final String[] sedimentaryHeader = new String[]{
                                "Primary Grainsize", "Secondary Grainsize",
                                "Comparator Used", "Bedding Thickness",
                                "Bedding Features", "Weathering", "Hardness",
                                "Carbonate", "Colour", "Sedimentary Features",
                                "Inferred Environment", "Nature of Rock Unit",
                                "Correspondence"};
                            for (String each : sedimentaryHeader) {
                                c.print(each);
                            }
                        }

                        c.println();

                        for (Sample sample : samples) {
                            Feature feature = sample.getFeature();
                            if (featureUtil.isAllowedReadFeatureSite(user,
                                    feature)) {
                                writeLocality(sample, c);
                                SiteView sv = feature.getSiteView();
                                if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
                                    Datum datum = SiteUtil.getFREDDatum(feature);
                                    Coordinate coord = SiteUtil.getFREDCoordinate(
                                            feature);
                                    c.print(datum.getHumanStringFor(coord).replaceAll(
                                            "Geographic ", ""));
                                    try {
                                        Datum nzmgDatum = DatumFactory.createDatum(
                                                "NZMG");
                                        Datum.Coordinate nzmgCoord = nzmgDatum.convertFromDatum(
                                                datum, coord);
                                        c.print(nzmgCoord.getEastWest());
                                        c.print(nzmgCoord.getNorthSouth());
                                    } catch (Exception e) {
                                        skipColumns(c, 2);
                                    }
                                    if (sv != null) {
                                        LatLong ll = SiteUtil.getSiteLatLong(sv);
                                        c.print(ll.getLatAsDecDegree(5));
                                        c.print(ll.getLongAsDecDegree(5));
                                    } else {
                                        skipColumns(c, 2);
                                    }
                                } else {
                                        skipColumns(c, 5);
                                }
                                c.print(DBUtils.nvl(feature.getMapYear()));
                                c.print(((sv != null) ? DBUtils.nvl(
                                        sv.getMethod()) : ""));
                                c.print(((sv != null) ? DBUtils.nvl(
                                        sv.getAccuracy()) : ""));

                                if (featureUtil.isAllowedReadFeature(user,feature)) {
                                    c.print(DBUtils.nvl(feature.getLocality()).replaceAll(
                                            "\\s\\s+|\\n|\\r", " "));
                                    c.print(((sv != null) ? sv.getCountryName() : ""));
                                    c.print(DBUtils.nvl(
                                            feature.getCoordComments()).replaceAll(
                                                    "\\s\\s+|\\n|\\r", " "));
                                    c.print(DBUtils.nvl(feature.getComments()).replaceAll(
                                            "\\s\\s+|\\n|\\r", " "));

                                    if (collectionFlag) {
                                        if (!FREDUtil.isEmpty(
                                                sample.getCollectors())) {
                                            StringBuilder sb = new StringBuilder();
                                            for (Person collector : sample.getCollectors()) {
                                                sb.append(collector.getName());
                                                sb.append("; ");
                                            }
                                            c.print(sb.toString());
                                        }
                                        c.print(DBUtils.nvl(
                                                FREDUtil.formatDateForOutput(
                                                        sample.getCollectionDate(),
                                                        sample.getDateRounding())));
                                        c.print(DBUtils.nvl(sample.getInPlace()));
                                        if (!FREDUtil.isEmpty(
                                                sample.getSentTos())) {
                                            StringBuilder sb = new StringBuilder();
                                            for (SentTo sentTo : sample.getSentTos()) {
                                                sb.append(
                                                        SampleUtil.getSentToDescription(
                                                                sentTo).replaceAll(
                                                                "\\s\\s+|\\n|\\r",
                                                                " ")).append(
                                                                "; ");
                                            }
                                            c.print(sb.toString());
                                        }
                                        c.print(DBUtils.nvl(
                                                sample.getNotCollected()).replaceAll(
                                                        "\\s\\s+|\\n|\\r", " "));
                                        c.print(DBUtils.nvl(
                                                sample.getSignificance()).replaceAll(
                                                        "\\s\\s+|\\n|\\r", " "));
                                    }

                                    if (stratigraphyFlag) {
                                        c.print(DBUtils.nvl(sample.getStratUnit()));
                                        if (sample.getInferredStage() != null) {
                                            Stage stage = sample.getInferredStage();
                                            c.print(((stage.getLowerAge() != null) ? stage.getLowerAge().getName() : ""));
                                            c.print(DBUtils.nvl(stage.getStageLowerMod()));
                                            c.print(((stage.getUpperAge() != null) ? stage.getUpperAge().getName() : ""));
                                            c.print(DBUtils.nvl(stage.getStageUpperMod()));
                                            c.print(stageUtil.getNumericAgeStart(stage));
                                            c.print(stageUtil.getNumericAgeStop(stage));
                                        } else {
                                            skipColumns(c, 6);
                                        }
                                        if (sample.getKnownStage() != null) {
                                            Stage stage = sample.getKnownStage();
                                            c.print(((stage.getLowerAge() != null) ? stage.getLowerAge().getName() : ""));
                                            c.print(DBUtils.nvl(stage.getStageLowerMod()));
                                            c.print(((stage.getUpperAge() != null) ? stage.getUpperAge().getName() : ""));
                                            c.print(DBUtils.nvl(stage.getStageUpperMod()));
                                            c.print(stageUtil.getNumericAgeStart(stage));
                                            c.print(stageUtil.getNumericAgeStop(stage));
                                        } else {
                                            skipColumns(c, 6);
                                        }
                                        List<? extends Relationship> nearbys = sampleUtil.getRelationships(
                                                sample, "Sample", "nearby");
                                        if (nearbys != null && nearbys.size() > 0) {
                                            StringBuilder sb = new StringBuilder();
                                            for (Relationship rel : nearbys) {
                                                sb.append(SampleUtil.getRelationshipDescription(rel));
                                                sb.append("; ");
                                            }
                                            c.print(sb.toString());
                                        } else {
                                            c.print(null);
                                        }
                                        
                                        List<? extends Relationship> sampRels = sampleUtil.getRelationships(
                                                sample, "Sample",
                                                new String[]{"above", "below"});
                                        if (nearbys != null && sampRels.size() > 0) {
                                            StringBuilder sb = new StringBuilder();
                                            for (Relationship rel : sampRels) {
                                                sb.append(
                                                        SampleUtil.getRelationshipDescription(rel)).append("; ");
                                            }
                                            c.print(sb.toString());
                                        } else {
                                            c.print(null);
                                        }

                                        List<? extends Relationship> stratRels = sampleUtil.getRelationships(
                                                sample, "Stratigraphic",
                                                new String[]{"above top",
                                                    "above base", "below top",
                                                    "below base"});
                                        if (nearbys != null && stratRels.size() > 0) {
                                            StringBuilder sb = new StringBuilder();
                                            for (Relationship rel : stratRels) {
                                                sb.append(SampleUtil.getRelationshipDescription(rel)).append("; ");
                                            }
                                            c.print(sb.toString());
                                        } else {
                                            c.print(null);
                                        }
                                        
                                        c.print(DBUtils.nvl(sample.getColumnMap()));
                                        c.print(DBUtils.nvl(sample.getDip()));
                                        c.print(DBUtils.nvl(sample.getDipDirection()));
                                        c.print(DBUtils.nvl(sample.getStrike()));
                                        c.print(DBUtils.nvl(sample.getFacing()));
                                        c.print(DBUtils.nvl(sample.getStratComments()).replaceAll("\\s\\s+|\\n|\\r", " "));
                                    }
                                    
                                    
                                    if (sedimentaryFlag) {
                                        c.print(((sample.getPrimaryGrainSize() != null) ? sample.getPrimaryGrainSize().getName() : ""));
                                        c.print(((sample.getSecondaryGrainSize() != null) ? sample.getSecondaryGrainSize().getName() : ""));
                                        c.print(DBUtils.nvl(
                                                sample.getComparatorUsed()));
                                        c.print(((sample.getBedThickness() != null) ? sample.getBedThickness().getName() : ""));
                                        c.print(SampleUtil.getBeddingDescription(
                                                sample));
                                        c.print(((sample.getWeathering() != null) ? sample.getWeathering().getName() : ""));
                                        c.print(((sample.getHardness() != null) ? sample.getHardness().getName() : ""));
                                        c.print(((sample.getCarbonate() != null) ? sample.getCarbonate().getName() : ""));
                                        c.print(SampleUtil.getColourDescription(
                                                sample));
                                        if (!FREDUtil.isEmpty(sample.getSedimentaryFeatures())) {
                                            StringBuilder sb = new StringBuilder();
                                            for (SedimentaryFeature sedFeat : sample.getSedimentaryFeatures()) {
                                                sb.append(
                                                        SampleUtil.getSedFeatureDescription(
                                                                sedFeat)).append(
                                                                "; ");
                                            }
                                            c.print(sb.toString());
                                        } else {
                                            c.print(null);
                                        }
                                        c.print(DBUtils.nvl(
                                                sample.getDepositionEnv()).replaceAll(
                                                        "\\s\\s+|\\n|\\r", " "));
                                        c.print(DBUtils.nvl(
                                                sample.getRockNature()).replaceAll(
                                                        "\\s\\s+|\\n|\\r", " "));
                                        c.print(DBUtils.nvl(
                                                sample.getCorrespondence()).replaceAll(
                                                        "\\s\\s+|\\n|\\r", " "));
                                    }
                                }
                                c.println();
                            }
                        }
                        c.println();
                    }

                    if (adoptionFlag) {
                        c.printRecord("********");
                        c.printRecord("Adoption");
                        c.printRecord("********");

                        writeLocalityHeader(c);

                        final String[] adoptionHeader = new String[]{"Adoptors",
                            "Adoption Date", "Adopted Stage Lower",
                            "Adopted Lower Modifier", "Adopted Stage Upper",
                            "Adopted Upper Modifier", "Adopted Age Start",
                            "Adopted Age Stop", "Comments"};
                        for (String each : adoptionHeader) {
                            c.print(each);
                        }
                        c.println();

                        for (Sample sample : samples) {
                            for (Adoption adoption : recordUtil.getAdoptionRecords(
                                    sample)) {
                                if (recordUtil.isAllowedReadRecord(user,
                                        adoption.getRecord())) {
                                    writeLocality(
                                            adoption.getRecord().getSample(), c);
                                    if (!FREDUtil.isEmpty(adoption.getAdoptors())) {
                                        StringBuilder sb = new StringBuilder();
                                        for (Person person : adoption.getAdoptors()) {
                                            sb.append(person.getName()).append(
                                                    "; ");
                                        }
                                        c.print(sb.toString());
                                    }
                                    c.print(DBUtils.nvl(
                                            FREDUtil.formatDateForOutput(
                                                    adoption.getAdoptionDate(),
                                                    adoption.getDateRounding())));
                                    if (adoption.getStage() != null) {
                                        Stage stage = adoption.getStage();
                                        c.print(((stage.getLowerAge() != null) ? stage.getLowerAge().getName() : ""));
                                        c.print(DBUtils.nvl(
                                                stage.getStageLowerMod()));
                                        c.print(((stage.getUpperAge() != null) ? stage.getUpperAge().getName() : ""));
                                        c.print(DBUtils.nvl(
                                                stage.getStageUpperMod()));
                                        c.print(stageUtil.getNumericAgeStart(
                                                stage));
                                        c.print(stageUtil.getNumericAgeStop(
                                                stage));
                                    } else {
                                        skipColumns(c, 6);
                                    }
                                    c.print(DBUtils.nvl(adoption.getComments()).replaceAll("\\s\\s+|\\n|\\r", " "));
                                    c.println();
                                }
                            }
                        }
                        c.println();
                    }

                    if (paleontologyFlag) {
                        c.printRecord("********");
                        c.printRecord("Paleontology");
                        c.printRecord("********");

                        writeLocalityHeader(c);
                        final String[] paleontologyHeader = new String[]{
                            "Identifiers", "Identification Date", "Stage Lower",
                            "Lower Modifier", "Stage Upper", "Upper Modifier",
                            "Age Start", "Age Stop", "Stage Comments",
                            "Lab Number", "Collection Comments"};
                        c.printRecord((Object[]) paleontologyHeader);

                        for (Sample sample : samples) {
                            for (Paleontology paleontology : recordUtil.getPaleontologyRecords(
                                    sample)) {
                                if (recordUtil.isAllowedReadRecord(user, paleontology.getRecord())) {
                                    writeLocality(paleontology.getRecord().getSample(), c);
                                    if (!FREDUtil.isEmpty(paleontology.getIdentifiers())) {
                                        StringBuilder sb = new StringBuilder();
                                        for (Person person : paleontology.getIdentifiers()) {
                                            sb.append(person.getName()).append("; ");
                                        }
                                        c.print(sb.toString());
                                    } else {
                                        c.print(null);
                                    }
                                    c.print(DBUtils.nvl(
                                            FREDUtil.formatDateForOutput(
                                                    paleontology.getIdentificationDate(),
                                                    paleontology.getDateRounding())));
                                    if (paleontology.getStage() != null) {
                                        Stage stage = paleontology.getStage();
                                        c.print(((stage.getLowerAge() != null) ? stage.getLowerAge().getName() : ""));
                                        c.print(DBUtils.nvl(stage.getStageLowerMod()));
                                        c.print(((stage.getUpperAge() != null) ? stage.getUpperAge().getName() : ""));
                                        c.print(DBUtils.nvl(stage.getStageUpperMod()));
                                        c.print(stageUtil.getNumericAgeStart(stage));
                                        c.print(stageUtil.getNumericAgeStop(stage));
                                    } else {
                                        skipColumns(c, 6);
                                    }
                                    c.print(DBUtils.nvl(
                                            paleontology.getStageComments()).replaceAll(
                                                    "\\s\\s+|\\n|\\r", " "));
                                    c.print(DBUtils.nvl(
                                            RecordUtil.getLabNumberDescription(
                                                    paleontology)));
                                    c.print(DBUtils.nvl(
                                            paleontology.getCollectionComments()).replaceAll(
                                                    "\\s\\s+|\\n|\\r", " "));
                                    c.println();
                                }
                            }
                        }
                        c.println();
                    }

                    if (palListFlag) {
                        c.printRecord("********");
                        c.printRecord("Paleontology List");
                        c.printRecord("********");

                        List<List<Paleontology>> paleontologyMasterList = new Vector<List<Paleontology>>();
                        List<Paleontology> paleontologies = new Vector<Paleontology>();
                        int i = 0;
                        for (Sample sample : samples) {
                            for (Paleontology paleontology : recordUtil.getPaleontologyRecords(sample)) {
                                if (recordUtil.isAllowedReadPalList(user,paleontology)) {
                                    paleontologies.add(paleontology);
                                    if (++i == 250) {
                                        paleontologyMasterList.add(paleontologies);
                                        paleontologies = new Vector<Paleontology>();
                                        i = 0;
                                    }
                                }
                            }
                        }
                        if (paleontologies.size() > 0) {
                            paleontologyMasterList.add(paleontologies);
                        }

                        if (paleontologyMasterList.size() > 0) {
                            // These seem to be printed across the columns???
                            for (List<Paleontology> pals : paleontologyMasterList) {

                                c.print("FR Number");
                                c.print(null);
                                for (Paleontology paleontology : pals) {
                                    Sample sample = paleontology.getRecord().getSample();
                                    if (sample.getFrNumber() != null) {
                                        c.print(sample.getFrNumber().getFrNumber());
                                    } else {
                                        c.print(((sample.getFeature().getFrNumber() != null) ? sample.getFeature().getFrNumber().getFrNumber() : ""));
                                    }
                                }
                                c.println();

                                c.print("Yard FR Number");
                                c.print(null);
                                for (Paleontology paleontology : pals) {
                                    Sample sample = paleontology.getRecord().getSample();
                                    if (sample.getYardFrNumber() != null) {
                                        c.print(sample.getYardFrNumber().getFrNumber());
                                    } else {
                                        c.print(((sample.getFeature().getYardFrNumber() != null) ? sample.getFeature().getYardFrNumber().getFrNumber() : ""));
                                    }

                                }
                                c.println();

                                c.print("Locality Type");
                                c.print(null);
                                for (Paleontology paleontology : pals) {
                                    c.print(paleontology.getRecord().getSample().getFeature().getFeatureType());
                                }
                                c.println();

                                c.print("Field Number/Drillhole Name");
                                c.print(null);
                                for (Paleontology paleontology : pals) {
                                    c.print(FREDUtil.nvl(
                                            paleontology.getRecord().getSample().getFeature().getFeatureName()));
                                }
                                c.println();

                                c.print("Depth From");
                                c.print(null);
                                for (Paleontology paleontology : pals) {
                                    c.print(DBUtils.nvl(
                                            paleontology.getRecord().getSample().getTopDepth()));
                                }
                                c.println();

                                c.print("Depth To");
                                c.print(null);
                                for (Paleontology paleontology : pals) {
                                    c.print(DBUtils.nvl(
                                            paleontology.getRecord().getSample().getBottomDepth()));
                                }
                                c.println();

                                c.print("Depth Unit");
                                c.print(null);
                                for (Paleontology paleontology : pals) {
                                    c.print(DBUtils.nvl(
                                            paleontology.getRecord().getSample().getDepthUnit()));
                                }
                                c.println();

                                c.print("Drill Type");
                                c.print(null);
                                for (Paleontology paleontology : pals) {
                                    c.print(((paleontology.getRecord().getSample().getDrillType() != null) ? paleontology.getRecord().getSample().getDrillType().getName() : ""));
                                }
                                c.println();

                                c.print("Identifier");
                                c.print(null);
                                for (Paleontology paleontology : pals) {
                                    if (!FREDUtil.isEmpty(paleontology.getIdentifiers())) {
                                        StringBuilder sb = new StringBuilder();
                                        for (Person person : paleontology.getIdentifiers()) {
                                            sb.append(person.getName()).append("; ");
                                        }
                                        c.print(sb.toString());
                                    }
                                }
                                c.println();

                                TreeSet<TaxonomicNameAndGroup> taxonomicNames = new TreeSet<TaxonomicNameAndGroup>();
                                for (Paleontology paleontology : pals) {
                                    for (PaleontologyListEntry palList : paleontology.getListEntries()) {
                                        TaxonomicNameAndGroup nameAndGroup = new TaxonomicNameAndGroup(
                                                palList.getTaxonomicName(),
                                                palList.getTaxonomicGroup());
                                        taxonomicNames.add(nameAndGroup);
                                    }
                                }
                                for (TaxonomicNameAndGroup nameAndGroup : taxonomicNames) {
                                    c.print(nameAndGroup.getTaxonomicGroup().getName());
                                    c.print(DBUtils.nvl(nameAndGroup.getTaxonomicName()));
                                    for (Paleontology paleontology : pals) {
                                        String printMe = null;
                                        for (PaleontologyListEntry palList : paleontology.getListEntries()) {
                                            TaxonomicNameAndGroup check = new TaxonomicNameAndGroup(
                                                    palList.getTaxonomicName(),
                                                    palList.getTaxonomicGroup());
                                            if (check.equals(nameAndGroup)) {
                                                printMe = encodeTaxaString(palList);
                                                break;
                                            } 

                                        }
                                        c.print(printMe);
                                    }
                                    c.println();
                                }
                                c.println();
                            }
                        }
                    }

                    new AuditUtil(FredHibernate.get().getDAOFactory()).addLogEntry(
                            AuditUtil.DOWNLOAD_LOG_TYPE, user, samples.size());

                } else {
                    ExtranetTemplate et = h.getExtranetTemplate();
                    et.setDisplayLoadingMessage(true);
                    out.print("<p>No data</p>");
                    h.drawBottom(out, et);
                }

            }
        } catch (StorageAccessException | SQLException | NamingException | HibernateException e) {
            throw new ServletException(e);
        } finally {
            try {
                factory.closeSession();
            } catch (StorageAccessException ex) {
                log.log(Level.WARNING, null, ex);
            }
        }

    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private final String[] localityHeader = new String[]{"FR Number",
            "Yard FR Number", "Locality Type", "Field Number/Drillhole Name",
            "Depth From", "Depth To", "Depth Unit", "Drill Type"};
    public void writeLocalityHeader(CSVPrinter c) throws IOException {
        for (String each : localityHeader) {
            c.print(each);
        }
    }

    public void writeLocality(Sample sample, CSVPrinter c) throws IOException {
        if (sample.getFrNumber() != null) {
            c.print(sample.getFrNumber().getFrNumber());
        } else {
            c.print(((sample.getFeature().getFrNumber() != null) ? sample.getFeature().getFrNumber().getFrNumber() : ""));
        }
        if (sample.getYardFrNumber() != null) {
            c.print(sample.getYardFrNumber().getFrNumber());
        } else {
            c.print(((sample.getFeature().getYardFrNumber() != null) ? sample.getFeature().getYardFrNumber().getFrNumber() : ""));
        }
        c.print(sample.getFeature().getFeatureType());
        c.print(FREDUtil.nvl(sample.getFeature().getFeatureName()));
        c.print(DBUtils.nvl(sample.getTopDepth()));
        c.print(DBUtils.nvl(sample.getBottomDepth()));
        c.print(DBUtils.nvl(sample.getDepthUnit()));
        c.print(((sample.getDrillType() != null) ? sample.getDrillType().getName() : ""));
    }

    public String encodeTaxaString(PaleontologyListEntry palList) {
        Integer specCount = palList.getSpecimenCount();
        String specCoord = palList.getSpecimenCoords();
        String comments = palList.getComments();

        String enc = ((specCount != null) ? specCount.toString() : "") + "|" + specCoord + "|" + comments;

        if (specCount == null && FREDUtil.isEmpty(specCoord) && FREDUtil.isEmpty(comments)) {
            enc = "*";
        } else if (specCount != null && !FREDUtil.isEmpty(specCoord) && FREDUtil.isEmpty(comments)) {
            enc = specCount.toString() + "|" + specCoord;
        } else if (specCount != null && FREDUtil.isEmpty(specCoord) && FREDUtil.isEmpty(comments)) {
            enc = specCount.toString();
        } else if (specCount == null && FREDUtil.isEmpty(specCoord) && !FREDUtil.isEmpty(comments)) {
            enc = comments;
        }

        return enc;
    }

    private void skipColumns(CSVPrinter c, int numColumns) throws IOException {
        for (int i=0; i<numColumns; i++) {
            c.print(null);
        }
    }
    
}
