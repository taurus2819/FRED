package nz.cri.gns.fred.servlet;

import com.google.common.base.Strings;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.FREDHibernateServlet;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.TaxonomicNameAndGroup;
import nz.cri.gns.fred.servlet.util.FredHelper;
import nz.cri.gns.fred.servlet.util.JspWriterImpl;
import nz.cri.gns.fred.util.AuditUtil;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.SiteModelUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.Datum.Coordinate;
import nz.cri.gns.util.map.Datum.LatLong;
import nz.cri.gns.util.map.DatumFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Was export.jsp.
 */
public class ExportServlet extends FREDHibernateServlet {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.servlet.ExportServlet");
    private static final String NEARBY = "nearby";
    private static final String ABOVE = "above";
    private static final String BELOW = "below";
    private static final String ABOVE_TOP = "above_top";
    private static final String ABOVE_BASE = "above_base";
    private static final String BELOW_TOP = "below_top";
    private static final String BELOW_BASE = "below_base";
    private static final String SAMPLE = "sample";
    private static final String STRATIGRAPHIC = "stratigraphic";

    public static enum Type {
        LOCATION,
        ADOPTION,
        PALEONTOLOGY,
        PALEONTOLOGY_TAXONOMIC;

        static Type of(String name) {
            try {
                if (!Strings.isNullOrEmpty(name)) {
                    return Type.valueOf(name.toUpperCase());
                }
            } catch (IllegalArgumentException e) {
            }
            // we'll default to location if the user hasn't provided a valid value
            return LOCATION;
        }
    }

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

        Type type = Type.of(request.getParameter("type"));
        // the following flags determine what makes up the location download.
        boolean collectionFlag = (request.getParameter("collection") != null);
        boolean stratigraphyFlag = (request.getParameter("stratigraphy") != null);
        boolean sedimentaryFlag = (request.getParameter("sedimentary") != null);

        if (type == Type.LOCATION && !(collectionFlag || stratigraphyFlag || sedimentaryFlag)) {
            // we are doing a location download but the user hasn't seleceted anything to go into it.
            // default to everything.
            // it is better to return all the possible data rather than an empty file
            collectionFlag = true;
            stratigraphyFlag = true;
            sedimentaryFlag = true;
        }

        try {
            User user = (User) h.getUser(session);
            FeatureUtil featureUtil = new FeatureUtil(factory);
            StageUtil stageUtil = new StageUtil(factory);
            SampleUtil sampleUtil = new SampleUtil(factory);
            RecordUtil recordUtil = new RecordUtil(factory);

            TreeSet<Sample> samples = new TreeSet<Sample>();

            try (JspWriterImpl out = new JspWriterImpl(response.getWriter())) {
                CSVPrinter c = new CSVPrinter(out, CSVFormat.EXCEL);

                // Content type, disposition.
                response.setContentType("text/csv");
                response.addHeader("Content-Disposition",
                    String.format("attachment;filename=fred-export-%s-%tF.csv",
                        type.toString().toLowerCase(), new Date()));

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
                c.flush();

                if (type == Type.LOCATION) {
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
                    c.flush();

                    log.log(Level.INFO, "GETTING SAMPLE DATA " + new Date());
                    final String from
                        = "FROM Sample S "
                        + "LEFT OUTER JOIN FETCH S.feature "
                        + "LEFT OUTER JOIN FETCH S.audit "
                        + "LEFT OUTER JOIN FETCH S.records R "
                        + "LEFT OUTER JOIN FETCH R.audit "
                        + "LEFT OUTER JOIN FETCH R.adoption "
                        + "LEFT OUTER JOIN FETCH R.paleontology "
                        + "LEFT OUTER JOIN FETCH S.relationships ";
                    samples = getSamples(request, response, from);
                    if (response.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
                        return;
                    }
                    log.log(Level.INFO, "START TIME " + new Date());

                    for (Sample sample : samples) {
                        Feature feature = sample.getFeature();
                        if (featureUtil.isAllowedReadFeatureSite(user,
                            feature)) {
                            writeLocality(sample, c);
//                                SiteView sv = feature.getSiteView();
                            if (feature.getOrigCoord() != null & feature.getOrigSystemId() != null) {
                                Datum datum = SiteModelUtil.getFREDDatum(feature);
                                Coordinate coord = SiteModelUtil.getFREDCoordinate(
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
                                if (feature.getOrigCoord() != null) {        //(sv != null) {
                                    LatLong ll = SiteModelUtil.getSiteLatLong(feature);
                                    c.print(ll.getLatAsDecDegree(5));
                                    c.print(ll.getLongAsDecDegree(5));
                                } else {
                                    skipColumns(c, 2);
                                }
                            } else {
                                skipColumns(c, 5);
                            }
                            c.print(DBUtils.nvl(feature.getMapYear()));

                            //TODO: needs some work to be done here
//                                c.print(((sv != null) ? DBUtils.nvl(
//                                        sv.getMethod()) : ""));
//                                c.print(((sv != null) ? DBUtils.nvl(
//                                        sv.getAccuracy()) : ""));
                            // skip the method/accuracy columns commented out above
                            skipColumns(c, 2);

                            if (featureUtil.isAllowedReadFeature(user, feature)) {
                                c.print(DBUtils.nvl(feature.getLocality()).replaceAll(
                                    "\\s\\s+|\\n|\\r", " "));

                                //TODO: needs some work to be done here
//                                    c.print(((sv != null) ? sv.getCountryName() : ""));
                                skipColumns(c, 1);  // skip the country column

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

                                    Set<Relationship> nearbys = new HashSet<>();
                                    Set<Relationship> aboveBelows = new HashSet<>();
                                    for (Relationship sampleRel : sample.getRelationships()) {
                                        if (sampleRel.getRelationType().getName().equalsIgnoreCase(SAMPLE)) {
                                            if (sampleRel.getRelationshipType().getName().equalsIgnoreCase(NEARBY)) {
                                                nearbys.add(sampleRel);
                                            }
                                            if (sampleRel.getRelationshipType().getName().equalsIgnoreCase(ABOVE) || sampleRel.getRelationshipType().getName().equalsIgnoreCase(BELOW)) {
                                                aboveBelows.add(sampleRel);
                                            }
                                        }
                                    }

                                    if (nearbys.size() > 0) {
                                        StringBuilder sb = new StringBuilder();
                                        for (Relationship rel : nearbys) {
                                            sb.append(SampleUtil.getRelationshipDescription(rel));
                                            sb.append("; ");
                                        }
                                        c.print(sb.toString());
                                    } else {
                                        c.print(null);
                                    }

                                    if (nearbys.size() > 0 && aboveBelows.size() > 0) {
                                        StringBuilder sb = new StringBuilder();
                                        for (Relationship rel : aboveBelows) {
                                            sb.append(
                                                SampleUtil.getRelationshipDescription(rel)).append("; ");
                                        }
                                        c.print(sb.toString());
                                    } else {
                                        c.print(null);
                                    }

                                    //Stratigraphic relationships
                                    Set<Relationship> stratBaseTops = new HashSet<>();
                                    for (Relationship sampleRel : sample.getRelationships()) {
                                        if (sampleRel.getRelationType().getName().equalsIgnoreCase(STRATIGRAPHIC)) {
                                            if (sampleRel.getRelationshipType().getName().equalsIgnoreCase(ABOVE_BASE)
                                                || sampleRel.getRelationshipType().getName().equalsIgnoreCase(ABOVE_TOP)
                                                || sampleRel.getRelationshipType().getName().equalsIgnoreCase(BELOW_TOP)
                                                || sampleRel.getRelationshipType().getName().equalsIgnoreCase(BELOW_BASE)) {
                                                stratBaseTops.add(sampleRel);
                                            }
                                        }
                                    }

                                    if (nearbys.size() > 0 && stratBaseTops.size() > 0) {
                                        StringBuilder sb = new StringBuilder();
                                        for (Relationship rel : stratBaseTops) {
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
                }//end locality flag

                else if (type == Type.ADOPTION) {
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
                    c.flush();

                    log.log(Level.INFO, "GETTING SAMPLE DATA " + new Date());
                    String from = "FROM Sample S "
                                + "LEFT OUTER JOIN FETCH S.feature "
                                + "LEFT OUTER JOIN FETCH S.audit "
                                + "LEFT OUTER JOIN FETCH S.records R "
                                + "LEFT OUTER JOIN FETCH R.adoption "
                                + "LEFT OUTER JOIN FETCH R.audit "
                                + "LEFT OUTER JOIN FETCH R.paleontology "
                                + "";
                    samples = getSamples(request, response, from);
                    if (response.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
                        return;
                    }

                    log.log(Level.INFO, "START TIME " + new Date());
                    for (Sample sample : samples) {
                        Set<Record> records = sample.getRecords();
                        for (Record r : records) {
                            Adoption adoption = r.getAdoption();
                            if (recordUtil.isAllowedReadRecord(user, r)) {
                                writeLocality(sample, c);
                                if (adoption != null) {
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
                                }//end adoption!=null
                                c.println();
                            }
                        }
                    }
                    c.println();
                }

                else if (type == Type.PALEONTOLOGY) {
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

                    c.flush();

                    log.log(Level.INFO, "GETTING SAMPLE DATA " + new Date());
                    String from = "FROM Sample S "
                                + "LEFT OUTER JOIN FETCH S.feature "
                                + "LEFT OUTER JOIN FETCH S.audit "
                                + "LEFT OUTER JOIN FETCH S.records R "
                                + "LEFT OUTER JOIN FETCH R.adoption "
                                + "LEFT OUTER JOIN FETCH R.audit "
                                + "LEFT OUTER JOIN FETCH R.paleontology "
                                + "";
                    samples = getSamples(request, response, from);
                    if (response.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
                        return;
                    }

                    log.log(Level.INFO, "START TIME " + new Date());
                    for (Sample sample : samples) {
                        Set<Record> records = sample.getRecords();
                        for (Record r : records) {
                            if (recordUtil.isAllowedReadRecord(user, r)) {
                                writeLocality(sample, c);
                                Paleontology paleontology = r.getPaleontology();
                                if (paleontology != null) {
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

                                }//paleontology
                                c.println();
                            }
                        }//record
                    }//sample
                    c.println();
                }

                else if (type == Type.PALEONTOLOGY_TAXONOMIC) {
                    c.printRecord("********");
                    c.printRecord("Paleontology List");
                    c.printRecord("********");
                    c.flush();

                    List<List<Paleontology>> paleontologyMasterList = new Vector<List<Paleontology>>();
                    List<Paleontology> paleontologies = new Vector<Paleontology>();

                    log.log(Level.INFO, "GETTING SAMPLE DATA " + new Date());
                    String from = "FROM Sample S "
                                + "LEFT OUTER JOIN FETCH S.feature "
                                + "LEFT OUTER JOIN FETCH S.audit "
                                + "LEFT OUTER JOIN FETCH S.records R "
                                + "LEFT OUTER JOIN FETCH R.adoption "
                                + "LEFT OUTER JOIN FETCH R.audit "
                                + "LEFT OUTER JOIN FETCH R.paleontology "
                                + "";
                    samples = getSamples(request, response, from);
                    if (response.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
                        return;
                    }

                    log.log(Level.INFO, "START TIME " + new Date());
                    int i = 0;
                    for (Sample sample : samples) {
                        for (Record r : sample.getRecords()) {
                            Paleontology paleontology = r.getPaleontology();
                            if (paleontology != null && recordUtil.isAllowedReadPalList(user, paleontology)) {
                                paleontologies.add(paleontology);
                                if (++i == 250) {
                                    paleontologyMasterList.add(paleontologies);
                                    paleontologies = new Vector<Paleontology>();
                                    i = 0;
                                }
                            }//recordUtil
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
                                } else {
                                    c.print(null);
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
                log.log(Level.INFO, "END TIME " + new Date());

            }
        } catch (StorageAccessException | SQLException | NamingException | HibernateException e) {
            throw new ServletException(e);
        }
    }

    /** Maximum number of samples to request from the database at a time
        Oracle imposes a limit of 1000 id in an IN clause
    */
    private static final int SAMPLE_BATCH_SIZE = 999;
    /**
     * Load the sample details from the database
     *
     * Note: The error handling needs to be re-thought as it currently
     *       signals an error by calling HttpServletResponse.sendError()
     *
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @param from The from and join portion of the HQL query
     * @return An ordered set of the samples to process
     *
     * @throws IOException
     * @throws StorageAccessException
     * @throws HibernateException
     */
    private TreeSet<Sample> getSamples(HttpServletRequest request, HttpServletResponse response, String from) throws IOException, StorageAccessException, HibernateException {
        HttpSession session = request.getSession();
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        FeatureUtil featureUtil = new FeatureUtil(factory);
        SampleUtil sampleUtil = new SampleUtil(factory);
        TreeSet<Sample> samples = new TreeSet<Sample>();

        if (request.getParameter("featId") != null) {
            Integer featureId;
            try {
                featureId = Integer.parseInt(request.getParameter("featId"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid featId. Not a number");
                return samples;
            }
            Feature feature = featureUtil.getFeature(featureId);
            for (Sample sample : feature.getSamples()) {
                samples.add(sample);
}
        } else if (request.getParameter("sampId") != null) {
            samples.add(sampleUtil.getSample(Integer.parseInt(
                request.getParameter("sampId"))));
        } else if (session.getAttribute("FRED.samples") != null && ((List<Sample>) session.getAttribute(          //from here
            "FRED.samples")).size() > 0) {
            List<Sample> samps = (List<Sample>) session.getAttribute(
                "FRED.samples");
            //use samples (if comes from simple and adv searches)
            List<String> ids = new ArrayList(SAMPLE_BATCH_SIZE);
            for (Sample samp : samps) {
                ids.add(samp.getSampleId().toString());
                // Oracle has a limit of 1000 values in an IN clause
                // So we batch. An in clause is used to reduce the number of
                // round trips over the network.
                if (ids.size() == SAMPLE_BATCH_SIZE) {
                    samples.addAll(getBatch(from, ids));
                    ids.clear();
                }
            }
            if (!ids.isEmpty()) {
                samples.addAll(getBatch(from, ids));
            }
        } else if (session.getAttribute("FRED.features") != null && ((List<Feature>) session.getAttribute(
            "FRED.features")).size() > 0) {
            List<Feature> features = (List<Feature>) session.getAttribute(
                "FRED.features");
            //use features for localityServlet
            for (Feature feature : features) {
                FredHibernate.get().currentSession().refresh(feature);
                Set<Sample> featSamples = feature.getSamples();
                if (featSamples != null && featSamples.size() > 0) {
                    for (Sample sample : featSamples) {
                        samples.add(sample);                                            //to here
                    }
                }
            }
        }
        return samples;
    }
    /**
     * Retrieve a batch of Samples from the database.
     * The from clause allows the callers to specify join clauses to eagerly
     * load any required child objects.
     * Batching the requests reduces the round trips to the database, and
     * improves performance.
     *
     * @param from The from and join portion of the HQL query
     * @param ids list of sample ID's
     * @return the retrieved samples
     * @throws HibernateException
     */
    private List<Sample> getBatch(String from, List<String> ids) throws HibernateException {
        String idList = ids.stream().collect(Collectors.joining(", "));
        String qstr = from  + " WHERE S.id IN (" + idList + ")";
        Query query = FredHibernate.get().currentSession().createQuery(qstr);
        return query.list();
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
        for (int i = 0; i < numColumns; i++) {
            c.print(null);
        }
    }

}
