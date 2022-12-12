package nz.cri.gns.fred.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import nz.cri.gns.core.NameableAndIdentifiable;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.querybuilder.BasicDateField;
import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.BasicTextField;
import nz.cri.gns.db.querybuilder.Field;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.advanced.FilteredNumberField;
import nz.cri.gns.db.querybuilder.advanced.FilteredPossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.FilteredTextField;
import nz.cri.gns.db.querybuilder.advanced.FilteredDateField;
import nz.cri.gns.db.querybuilder.advanced.NumberSource;
import nz.cri.gns.db.querybuilder.advanced.PossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredDateField;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredNumberField;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredPossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredTextField;
import nz.cri.gns.db.querybuilder.advanced.TwoLevelField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlJoin;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlQuery;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlUniqueSubTablePossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlUniqueSubTableTextField;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.hibernate.Island;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.hibernate.Country;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.RockColour;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.Weathering;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.UserUtil;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.util.SiteModelUtil;

public class FREDQuery extends HqlQuery implements NumberSource {

    private static final long serialVersionUID = 20060120L;
    private static final Logger log = Logger.getLogger(FREDQuery.class.getName());

    private static final String[] RECORD_TABLES = new String[]{"s.records"};
    private static final HqlJoin[] RECORD_JOINS = {new HqlJoin(false, "record")};
    private static final String[] PAL_LIST_TABLES = new String[]{"s.records", "record.paleontology.listEntries"};
    private static final HqlJoin[] PAL_LIST_JOINS = {new HqlJoin(false, "record"), new HqlJoin(false, "palList")};
    private static final String SAMPLE_STAGE_VIEW_TABLE = "s.sampleStageViews";
    private static final HqlJoin SAMPLE_STAGE_VIEW_JOIN = new HqlJoin(false, "sampleStageView");
    private static final String EDIT_TABLE = "s.audit.auditEdits";
    private static final HqlJoin EDIT_JOIN = new HqlJoin(false, "edit");

    protected int lastUsedId = 900000;

    //protected List<Person> people = null;
    protected List<Age> ages = null;
    protected List<FrUserView> frUsers = null;

    public FREDQuery() {
        //this.people = getValues("FROM Person AS p", Person.class);
        this.ages = getValues("FROM Age AS a WHERE a.code NOT IN (?, ?) AND a.obsoleteFlag = ?", Age.class, "nd", "nf", false);
        try {
            this.frUsers = new UserUtil(FredHibernate.get().getDAOFactory()).getFrWriters();
        } catch (StorageAccessException e) {
            log.log(Level.SEVERE, "Failed to load FRED writers", e);
        }
        addFields();
    }

    protected void addFields() {
        //this is going to be tricky.  Need to handle multiple selections and the ORs in the individual fields
        //Field[] f = new Field[4];
        //f[0] = new FrNumberTextField("frNumber", "Fr Number");
        //f[1] = new FrNumberTextField("mapSheet", "Fr Number Map Sheet");
        //f[2] = new FrNumberNumberField("serialNumber", "Fr Number Serial Number");
        //f[3] = new FrNumberTextField("recollectionNumber", "Fr Number Recollection Number");
        //add(new TwoLevelField("FR Number Fields", f));

        Field[] f = new Field[14];
        f[0] = new BasicTextField("s.feature.featureName", "Feature Name");
        f[1] = new PossibleValueField("s.feature.featureType", "Feature Type", getFeatureTypes());
        f[2] = new PossibleValueField("s.feature.masterFile", "Masterfile", getValues("FROM Folder AS f WHERE f.folderType.name='Admin'", Folder.class));
        f[3] = new BasicTextField("SITE_API.NZMG_SHEET", "NZMS260 Sheet");
        f[4] = new PossibleValueField("SITE_API.QMAP_SHEET", "QMap Sheet", getQMapSheets());
//        f[5] = new PossibleValueField("SITE_API.COUNTRY_CODE", "Country", getCountry("SELECT DISTINCT COUNTRY_CODE, COUNTRY_NAME, COUNTRY_DIAL_CODE FROM mis.country ORDER ORDER BY UPPER(COUNTRY_NAME)"));
        f[5] = new PossibleValueField("SITE_API.COUNTRY_CODE", "Country", getValues("FROM Country AS c", Country.class));
//        f[6] = new PossibleValueField("SITE_API.ISLAND", "Island", getSQLValues("SELECT DISTINCT name as n, name FROM sc.island ORDER BY UPPER(name)"));
        f[6] = new PossibleValueField("SITE_API.ISLAND", "Island", getIslands());
        f[7] = new BasicNumberField("SITE_API.NZMG_EAST", "NZMG Easting");
        f[8] = new BasicNumberField("SITE_API.NZMG_NORTH", "NZMG Northing");
        f[9] = new BasicNumberField("SITE_API.LATITUDE", "Latitude");
        f[10] = new BasicNumberField("SITE_API.LONGITUDE", "Longitude");
        f[11] = new BasicTextField("s.feature.locality", "Locality");
        f[12] = new BasicTextField("s.feature.coordComments", "Coordinate Comments");
        f[13] = new BasicTextField("s.feature.comments", "Locality Comments");
        add(new TwoLevelField("Locality Fields", f));

        f = new Field[10];
        //f[0] = new TableRequiredTextField("s.feature.featureName", "Drillhole Name", RECORD_TABLES_1, RECORD_JOINS_1);
        f[0] = new FilteredTextField("s.feature.featureName", "Drillhole Name", "s.feature.featureType='" + FREDConstants.DRILLHOLE + "'");
        f[1] = new FilteredTextField("s.feature.person.name", "Operating Company", "s.feature.featureType='" + FREDConstants.DRILLHOLE + "'");
        f[2] = new FilteredDateField("s.feature.startDate", "Spud Date", "s.feature.featureType='" + FREDConstants.DRILLHOLE + "'");
        f[3] = new FilteredDateField("s.feature.finishDate", "Completion Date", "s.feature.featureType='" + FREDConstants.DRILLHOLE + "'");
        f[4] = new BasicTextField("s.feature.drillholeLicenceName", "Licence Area");
        f[5] = new FilteredPossibleValueField("s.feature.datumType", "Datum Type", getDrillholeDatumTypes(), "s.feature.featureType='" + FREDConstants.DRILLHOLE + "'");
        f[6] = new FilteredNumberField("s.feature.datumElevation", "Datum Elevation (m)", "s.feature.featureType='" + FREDConstants.DRILLHOLE + "'");
        f[7] = new FilteredMetricDepthField("s.feature.startDepth", "Kick-off Depth (m)", "s.feature.depthUnit", "s.feature.featureType='" + FREDConstants.DRILLHOLE + "'");
        f[8] = new FilteredMetricDepthField("s.feature.finishDepth", "Termination Depth (m)", "s.feature.depthUnit", "s.feature.featureType='" + FREDConstants.DRILLHOLE + "'");
        f[9] = new PossibleValueField("s.drillType", "Sample Type", getValues("FROM DrillType AS t", DrillType.class));
        add(new TwoLevelField("Drillhole Fields", f));

        f = new Field[8];
        f[0] = new FilteredTextField("s.feature.featureName", "Vertical Section Name", "s.feature.featureType='" + FREDConstants.VERTICAL_SECTION + "'");
        //f[1] = new PossibleValueField("f.person", "Section Collector", people);
        f[1] = new FilteredTextField("s.feature.person.name", "Section Collector", "s.feature.featureType='" + FREDConstants.VERTICAL_SECTION + "'");
        f[2] = new FilteredDateField("s.feature.startDate", "Sampling Start Date", "s.feature.featureType='" + FREDConstants.VERTICAL_SECTION + "'");
        f[3] = new FilteredDateField("s.feature.finishDate", "Completion Date", "s.feature.featureType='" + FREDConstants.VERTICAL_SECTION + "'");
        f[4] = new FilteredPossibleValueField("s.feature.datumType", "Datum Type", getVertSectDatumTypes(), "s.feature.featureType='" + FREDConstants.VERTICAL_SECTION + "'");
        f[5] = new FilteredNumberField("s.feature.datumElevation", "Datum Elevation (m)", "s.feature.featureType='" + FREDConstants.VERTICAL_SECTION + "'");
        f[6] = new FilteredMetricDepthField("s.feature.startDepth", "Top Horizon (m)", "s.feature.depthUnit", "s.feature.featureType='" + FREDConstants.VERTICAL_SECTION + "'");
        f[7] = new FilteredMetricDepthField("s.feature.finishDepth", "Base Horizon (m)", "s.feature.depthUnit", "s.feature.featureType='" + FREDConstants.VERTICAL_SECTION + "'");
        add(new TwoLevelField("Vertical Section Fields", f));

        f = new Field[9];
        f[0] = new HqlUniqueSubTableTextField("person.name", "Collector", new String[]{"s.collectors"}, new HqlJoin[]{new HqlJoin(false, "person")});
        f[1] = new BasicDateField("s.collectionDate", "Collection Date");
        f[2] = new PossibleValueField("s.inPlace", "Fossils In Place", getInPlace());
        f[3] = new HqlUniqueSubTablePossibleValueField("sentTo.fossilGroup", "Sent To Group", getValues("FROM FossilGroup AS f", FossilGroup.class), new String[]{"s.sentTos"}, new HqlJoin[]{new HqlJoin(false, "sentTo")});
        f[4] = new HqlUniqueSubTableTextField("sentTo.person.name", "Sent To Person", new String[]{"s.sentTos"}, new HqlJoin[]{new HqlJoin(false, "sentTo")});
        f[5] = new HqlUniqueSubTableTextField("sentTo.lab.name", "Sent To Lab", new String[]{"s.sentTos"}, new HqlJoin[]{new HqlJoin(false, "sentTo")});
        f[6] = new HqlUniqueSubTableTextField("sentTo.comments", "Sent To Comments", new String[]{"s.sentTos"}, new HqlJoin[]{new HqlJoin(false, "sentTo")});
        f[7] = new BasicTextField("s.notCollected", "Not Collected");
        f[8] = new BasicTextField("s.significance", "Significance/Comments");
        add(new TwoLevelField("Collection Fields", f));

        f = new Field[12];
        f[0] = new BasicTextField("s.stratUnit", "Stratigraphic Name");
        f[1] = new AgeField("Inferred Stage", ages, SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "inferred");
        f[2] = new NumericAgeField("Inferred Stage (numeric)", SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "inferred");
        f[3] = new AgeField("Known Stage", ages, SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "known");
        f[4] = new NumericAgeField("Known Stage (numeric)", SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "known");
        f[5] = new BasicTextField("s.columnMap", "Column/Map");
        f[6] = new BasicNumberField("s.dip", "Dip");
        f[7] = new PossibleValueField("s.dipDirection", "Dip Direction", getDipDirection());
        f[8] = new BasicNumberField("s.strike", "Strike");
        f[9] = new PossibleValueField("s.facing", "Facing", getFacing());
        f[10] = new HqlUniqueSubTableTextField("relationship.feature.frNumber.frNumber", "Sample Relationship - FR Number", new String[]{"s.relationships"}, new HqlJoin[]{new HqlJoin(false, "relationship")});
        f[11] = new HqlUniqueSubTableTextField("relationship.stratUnit", "Strat Relationship - Unit", new String[]{"s.relationships"}, new HqlJoin[]{new HqlJoin(false, "relationship")});
        add(new TwoLevelField("Stratigraphic Fields", f));

        f = new Field[15];
        f[0] = new PossibleValueField("s.primaryGrainSize", "Primary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class));
        f[1] = new PossibleValueField("s.secondaryGrainSize", "Secondary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class));
        f[2] = new PossibleValueField("s.comparatorUsed", "Comparator Used", getComparatorUsed());
        f[3] = new PossibleValueField("s.bedThickness", "Bedding Thickness", getValues("FROM BedThickness AS b", BedThickness.class));
        f[4] = new PossibleValueField("s.primaryBedding", "Primary Bedding", getValues("FROM Bedding AS b", Bedding.class));
        f[5] = new PossibleValueField("s.secondaryBedding", "Secondary Bedding", getValues("FROM Bedding AS b", Bedding.class));
        f[6] = new PossibleValueField("s.weathering", "Weathering", getValues("FROM Weathering AS w", Weathering.class));
        f[7] = new PossibleValueField("s.hardness", "Hardness", getValues("FROM Hardness AS h", Hardness.class));
        f[8] = new PossibleValueField("s.carbonate", "Carbonate", getValues("FROM Carbonate AS c", Carbonate.class));
        f[9] = new PossibleValueField("s.colourModifier", "Colour Modifier", getValues("FROM ColourModifier AS c", ColourModifier.class));
        f[10] = new PossibleValueField("s.primaryColour", "Primary Colour", getValues("FROM RockColour AS r", RockColour.class));
        f[11] = new PossibleValueField("s.secondaryColour", "Secondary Colour", getValues("FROM RockColour AS r", RockColour.class));
        f[12] = new BasicTextField("s.depositionEnv", "Inferred Environment");
        f[13] = new BasicTextField("s.rockNature", "Nature of Rock Unit");
        f[14] = new BasicTextField("s.stratComments", "Stratigraphy Comments");
        //need to add additional features
        add(new TwoLevelField("Sedimentary Feature Fields", f));

        f = new Field[1];
        f[0] = new BasicTextField("s.correspondence", "Correspondence");
        add(new TwoLevelField("Correspondence Fields", f));

        f = new Field[9];
        f[0] = new HqlUniqueSubTableTextField("person.name", "Adoptor", new String[]{"s.records", "record.adoption", "adoption.adoptors"}, new HqlJoin[]{new HqlJoin(false, "record"), new HqlJoin(false, "adoption"), new HqlJoin(false, "person")});
        f[1] = new TableRequiredDateField("record.adoption.adoptionDate", "Adoption Date", RECORD_TABLES, RECORD_JOINS);
        f[2] = new AgeField("Adopted Stage", ages, SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "adoption");
        f[3] = new NumericAgeField("Adopted Stage (numeric)", SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "adoption");
        f[4] = new AgeField("Auto-Consensus Narrow", ages, SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "squirrelNarrow");
        f[5] = new NumericAgeField("Auto-Consensus Narrow (numeric)", SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "squirrelNarrow");
        f[6] = new AgeField("Auto-Consensus Wide", ages, SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "squirrelWide");
        f[7] = new NumericAgeField("Auto-Consensus Wide (numeric)", SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "squirrelWide");
        f[8] = new TableRequiredTextField("record.adoption.comments", "Comments", RECORD_TABLES, RECORD_JOINS);
        add(new TwoLevelField("Adoption Fields", f));

        f = new Field[13];
        f[0] = new HqlUniqueSubTableTextField("person.name", "Identifier", new String[]{"s.records", "record.paleontology", "paleontology.identifiers"}, new HqlJoin[]{new HqlJoin(false, "record"), new HqlJoin(false, "paleontology"), new HqlJoin(false, "person")});
        f[1] = new TableRequiredDateField("record.paleontology.identificationDate", "Identification Date", RECORD_TABLES, RECORD_JOINS);
        f[2] = new AgeField("Stage", ages, SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "paleontology");
        f[3] = new NumericAgeField("Stage (numeric)", SAMPLE_STAGE_VIEW_TABLE, SAMPLE_STAGE_VIEW_JOIN, "paleontology");
        f[4] = new TableRequiredTextField("record.paleontology.stageComments", "Stage Comments", RECORD_TABLES, RECORD_JOINS);
        f[5] = new TableRequiredPossibleValueField("record.paleontology.labSection", "Laboratory", getValues("FROM LabSection AS ls", LabSection.class), RECORD_TABLES, RECORD_JOINS);
        f[6] = new TableRequiredTextField("record.paleontology.labNumber", "Lab Number", RECORD_TABLES, RECORD_JOINS);
        f[7] = new TableRequiredTextField("record.paleontology.collectionComments", "Collection Comments", RECORD_TABLES, RECORD_JOINS);
        f[8] = new TableRequiredPossibleValueField("palList.taxonomicGroup", "Taxonomic Group", getValues("FROM TaxonomicGroup AS tg", TaxonomicGroup.class), PAL_LIST_TABLES, PAL_LIST_JOINS);
        f[9] = new TableRequiredTextField("palList.taxonomicName", "Taxonomic Name", PAL_LIST_TABLES, PAL_LIST_JOINS);
        f[10] = new TableRequiredNumberField("palList.specimenCount", "Specimen Count", PAL_LIST_TABLES, PAL_LIST_JOINS);
        f[11] = new TableRequiredTextField("palList.specimenCoords", "Specimen Coordinates", PAL_LIST_TABLES, PAL_LIST_JOINS);
        f[12] = new TableRequiredTextField("palList.comments", "Paleontology List Comments", PAL_LIST_TABLES, PAL_LIST_JOINS);
        //need to add identifiers
        add(new TwoLevelField("Paleontology Fields", f));

        f = new Field[10];
        f[0] = new PossibleValueField("s.audit.createdById", "Created By", frUsers);
        f[1] = new BasicDateField("s.audit.createdDate", "Created Date");
        f[2] = new PossibleValueField("s.audit.submittedById", "Submitted By", frUsers);
        f[3] = new BasicDateField("s.audit.submittedDate", "Submitted Date");
        f[4] = new PossibleValueField("s.audit.approvedById", "Approved By", frUsers);
        f[5] = new BasicDateField("s.audit.approvedDate", "Approved Date");
        f[6] = new BasicTextField("s.audit.curatorComments", "Curator Comments");
        f[7] = new TableRequiredPossibleValueField("edit.editedById", "Edited By", frUsers, EDIT_TABLE, EDIT_JOIN);
        f[8] = new TableRequiredDateField("edit.editedDate", "Edited Date", EDIT_TABLE, EDIT_JOIN);
        f[9] = new TableRequiredTextField("edit.comments", "Edit Comments", EDIT_TABLE, EDIT_JOIN);
        add(new TwoLevelField("Audit Fields", f));
    }

    @Override
    public String getQuery() throws InvalidOperatorException, InvalidValueException {
        return null;
    }

    public String getHQLQuery(String type, String query) throws InvalidOperatorException, InvalidValueException {
        //return super.getHQLQuery("SELECT DISTINCT s", "Sample AS s", "s.audit.status = 'approved' AND s.feature.audit.status = 'approved'", null, null);
        String hqlQuery;
        
        //debugging
        hqlQuery = super.getHQLQuery("SELECT DISTINCT s", "Sample AS s", null, null, null);
        
        
        if("Adv".equals(type)){
            hqlQuery = super.getHQLQuery("SELECT DISTINCT s.sampleId", "Sample AS s", "s.audit.status = 'approved' AND s.feature.audit.status = 'approved'", null, null);
        }else{
            hqlQuery = query;
        }
        String debugQuery = hqlQuery;
//        System.out.println("HQLQUERY = " + hqlQuery);
       /* if(hqlQuery.contains("SITE_API.COUNTRY_CODE"))
        {
            hqlQuery = "SELECT DISTINCT s.sampleId FROM Sample AS s WHERE (s.audit.status = 'approved' AND s.feature.audit.status = 'approved')";
        }*/
        //fetch SITE ID list from API here and embed into HQL query
        if (hqlQuery.contains("SITE_API")) {
            String patternString = "SITE_API.([A-Z0-9_]+) = '([A-Za-z0-9\\s]+)'";
            String patternTemplateString = "SITE_API.%s = '%s'";
            Pattern pattern = Pattern.compile(patternString);
            Matcher m = pattern.matcher(hqlQuery); 
            boolean match = false;
            List<Integer> siteIds = new ArrayList<>();
            while(m.find())    {   //TODO concatenate multiple spatial queries
                match = true;
                System.err.println("Attribute: " + m.group(1));
                System.err.println("Value: " + m.group(2));
//                System.out.println("Attribute: " + m.group(1) + ", " + "Value: " + m.group(2));
                siteIds = SiteModelUtil.requestSitesBySpatialFilter(String.format("%s=%s", m.group(1), m.group(2) ));
                System.err.println(String.format("# of sites: %d \n%s", siteIds.size(), siteIds));
                
                String idList = siteIds.subList(0, Math.min(1000, siteIds.size()))    //TODO - fix by splitting request into sections (JDBC limit of 1000 items per list)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")); 
                
                hqlQuery = hqlQuery.replaceAll(String.format(patternTemplateString, m.group(1), m.group(2)), String.format("s.feature.siteId IN (%s)", idList)); 
                System.err.println(String.format("hqlQuery: %s", hqlQuery));
                
                System.err.println(String.format("===>debugQuery (pre): %s", debugQuery));
                System.err.println(String.format("Regex #1: %s, Regex #2: %s", m.group(1), m.group(2)));
                debugQuery = debugQuery.replaceAll(String.format(patternTemplateString, m.group(1), m.group(2)), String.format("s.feature.siteId IN (%s)", idList.substring(0,10))); 
                System.err.println(String.format("===>debugQuery (post): %s", debugQuery));
            }
            if(!match)    {
                hqlQuery = hqlQuery.replaceAll(patternString, "1=1");
            }
        }
        
        return hqlQuery;
    }

    protected final <T extends Comparable<? super T>> List<T> getValues(String query, Class<T> clazz, Object... parameters) {
        FredDAO featureDAO = FredHibernate.get().getDAOFactory().getFredDAO();
        try {
            return featureDAO.getList(query, clazz, parameters);
        } catch (StorageAccessException ex) {
            Logger.getLogger(FREDQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    protected List<KeyValueObject> getSQLValues(String sql) {
        ArrayList<KeyValueObject> options = new ArrayList<>();
        try (Connection conn = FREDUtil.getConnection(); Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                options.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
            }
        } catch (Exception e) { // TODO: But no exceptions are thrown?
            Logger.getLogger(FREDQuery.class.getName()).log(Level.SEVERE, null, e);
        }
        return options;
    }
    
    protected List<KeyValueObject> getCountry(String sql) {
        ArrayList<KeyValueObject> options = new ArrayList<>();
        try (Connection conn = FREDUtil.getMISConnection(); Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Country country = new Country(rs.getString(1), rs.getString(2), rs.getInt(3));
//                options.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
                String countryName = country.getName();
//                options.add(new KeyValueObject("NZ", "New Zealand"));
                options.add(new KeyValueObject(countryName, countryName));
            }
        } catch (Exception e) { // TODO: But no exceptions are thrown?
            Logger.getLogger(FREDQuery.class.getName()).log(Level.SEVERE, null, e);
        }
        return options;
    }
    
    protected List<Island> getIslands() {
        return SiteModelUtil.getIslands();
    }

    protected List<KeyValueObject> getFeatureTypes() {
        ArrayList<KeyValueObject> options = new ArrayList<>(3);
        options.add(new KeyValueObject("Outcrop", "Outcrop"));
        options.add(new KeyValueObject("Drillhole", "Drillhole"));
        options.add(new KeyValueObject("Vertical Section", "Vertical Section"));
        return options;
    }

    protected List<KeyValueObject> getDrillholeDatumTypes() {
        ArrayList<KeyValueObject> options = new ArrayList<>(3);
        options.add(new KeyValueObject("KB", "KB"));
        options.add(new KeyValueObject("RT", "RT"));
        options.add(new KeyValueObject("Seafloor", "Seafloor"));
        return options;
    }

    protected List<KeyValueObject> getVertSectDatumTypes() {
        ArrayList<KeyValueObject> options = new ArrayList<>(2);
        options.add(new KeyValueObject("Top", "Top"));
        options.add(new KeyValueObject("Bottom", "Bottom"));
        return options;
    }

    protected List<KeyValueObject> getQMapSheets() {
        KeyValueObject[] o = new KeyValueObject[21];
        o[0] = new KeyValueObject("Kaitaia", "Kaitaia");
        o[1] = new KeyValueObject("Whangarei", "Whangarei");
        o[2] = new KeyValueObject("Auckland", "Auckland");
        o[3] = new KeyValueObject("Waikato", "Waikato");
        o[4] = new KeyValueObject("Rotorua", "Rotorua");
        o[5] = new KeyValueObject("Raukumara", "Raukumara");
        o[6] = new KeyValueObject("Taranaki", "Taranaki");
        o[7] = new KeyValueObject("Hawkes Bay", "Hawkes Bay");
        o[8] = new KeyValueObject("Wellington", "Wellington");
        o[9] = new KeyValueObject("Wairarapa", "Wairarapa");
        o[10] = new KeyValueObject("Nelson", "Nelson");
        o[11] = new KeyValueObject("Greymouth", "Greymouth");
        o[12] = new KeyValueObject("Kaikoura", "Kaikoura");
        o[13] = new KeyValueObject("Haast", "Haast");
        o[14] = new KeyValueObject("Aoraki", "Aoraki");
        o[15] = new KeyValueObject("Christchurch", "Christchurch");
        o[16] = new KeyValueObject("Wakatipu", "Wakatipu");
        o[17] = new KeyValueObject("Waitaki", "Waitaki");
        o[18] = new KeyValueObject("Fiordland", "Fiordland");
        o[19] = new KeyValueObject("Murihiku", "Murihiku");
        o[20] = new KeyValueObject("Dunedin", "Dunedin");
        ArrayList<KeyValueObject> options = new ArrayList<>(21);
        for (int i = 0; i < 21; i++) {
            options.add(o[i]);
        }
        return options;
    }

    protected List<KeyValueObject> getInPlace() {
        ArrayList<KeyValueObject> options = new ArrayList<>(3);
        options.add(new KeyValueObject("Yes", "Yes"));
        options.add(new KeyValueObject("No", "No"));
        options.add(new KeyValueObject("Almost", "Almost"));
        options.add(new KeyValueObject("Unknown", "Unknown"));
        return options;
    }

    protected List<KeyValueObject> getDipDirection() {
        ArrayList<KeyValueObject> options = new ArrayList<>(3);
        options.add(new KeyValueObject("N", "N"));
        options.add(new KeyValueObject("NE", "NE"));
        options.add(new KeyValueObject("E", "E"));
        options.add(new KeyValueObject("SE", "SE"));
        options.add(new KeyValueObject("S", "S"));
        options.add(new KeyValueObject("SW", "SW"));
        options.add(new KeyValueObject("W", "W"));
        options.add(new KeyValueObject("NW", "NW"));
        return options;
    }

    protected List<KeyValueObject> getFacing() {
        ArrayList<KeyValueObject> options = new ArrayList<>(3);
        options.add(new KeyValueObject("Normal", "Normal"));
        options.add(new KeyValueObject("Overturned", "Overturned"));
        return options;
    }

    protected List<KeyValueObject> getComparatorUsed() {
        ArrayList<KeyValueObject> options = new ArrayList<>(3);
        options.add(new KeyValueObject("Y", "Yes"));
        options.add(new KeyValueObject("N", "No"));
        return options;
    }

    @Override
    public int getLastUsedId() {
        return lastUsedId;
    }

    @Override
    public void incrementLastUsedId() {
        lastUsedId++;
    }
}
