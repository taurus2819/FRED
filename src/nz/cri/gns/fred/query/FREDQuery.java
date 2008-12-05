package nz.cri.gns.fred.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.querybuilder.BasicDateField;
import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.BasicTextField;
import nz.cri.gns.db.querybuilder.Field;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
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
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.Country;
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

public class FREDQuery extends HqlQuery implements NumberSource {

	private static final long serialVersionUID = 20060120L;
	
	private static final String SAMPLE_TABLE = "f.samples";
	private static final HqlJoin SAMPLE_JOIN = new HqlJoin(false, "sample");
	private static final String[] RECORD_TABLES = new String[] {"f.samples", "sample.records"};
	private static final HqlJoin[] RECORD_JOINS = {new HqlJoin(false, "sample"), new HqlJoin(false, "record")};
	private static final String[] PAL_LIST_TABLES = new String[] {"f.samples", "sample.records", "record.paleontology.listEntries"};
	private static final HqlJoin[] PAL_LIST_JOINS = {new HqlJoin(false, "sample"), new HqlJoin(false, "record"), new HqlJoin(false, "palList")};
	private static final String EDIT_TABLE = "f.audit.auditEdits";
	private static final HqlJoin EDIT_JOIN = new HqlJoin(false, "edit");
	
	protected int lastUsedId = 900000;
	
	//protected List<Person> people = null;
	protected List<Age> ages = null;
	protected List<FrUserView> frUsers = null;
	
	public FREDQuery() {
		//this.people = getValues("FROM Person AS p", Person.class);
		this.ages = getValues("FROM Age AS a WHERE a.code <> 'nd' AND a.code <> 'nf' AND a.obsoleteFlag = false", Age.class);
		try {
			this.frUsers = new UserUtil(FredHibernate.get().getDAOFactory()).getFrWriters();
		} catch (Exception e) {}
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
		f[0] = new BasicTextField("f.featureName", "Feature Name");
		f[1] = new PossibleValueField("f.featureType", "Feature Type", getFeatureTypes());
		f[2] = new PossibleValueField("f.masterFile", "Masterfile", getValues("FROM Folder AS f WHERE f.folderType.name='Admin'", Folder.class));
		f[3] = new BasicTextField("f.siteView.nzmgSheet", "NZMS260 Sheet");
		f[4] = new PossibleValueField("f.siteView.qmapSheet", "QMap Sheet", getQMapSheets());
		f[5] = new PossibleValueField("f.siteView.countryCode", "Country", getValues("FROM Country AS c", Country.class));
		f[6] = new PossibleValueField("f.siteView.island", "Island", getSQLValues("SELECT DISTINCT name as n, name FROM sc.island ORDER BY UPPER(name)"));
		f[7] = new BasicNumberField("f.siteView.nzmgEast", "NZMG Easting");
		f[8] = new BasicNumberField("f.siteView.nzmgNorth", "NZMG Northing");
		f[9] = new BasicNumberField("f.siteView.latitude", "Latitude");
		f[10] = new BasicNumberField("f.siteView.longitude", "Longitude");
		f[11] = new BasicTextField("f.locality", "Locality");
		f[12] = new BasicTextField("f.coordComments", "Coordinate Comments");
		f[13] = new BasicTextField("f.comments", "Locality Comments");
		add(new TwoLevelField("Locality Fields", f));
		
		f = new Field[10];
		f[0] = new BasicTextField("f.featureName", "Drillhole Name");
		//f[1] = new PossibleValueField("f.person", "Operating Company", people);
		f[1] = new BasicTextField("f.person.name", "Operating Company");
		f[2] = new BasicDateField("f.startDate", "Spud Date");
		f[3] = new BasicDateField("f.finishDate", "Completion Date");
		f[4] = new BasicTextField("f.licenceArea", "Licence Area");
		f[5] = new PossibleValueField("f.datumType", "Datum Type", getDrillholeDatumTypes());
		f[6] = new BasicNumberField("f.datumElevation", "Datum Elevation (m)");
		f[7] = new MetricDepthField("f.startDepth", "Kick-off Depth (m)", "f.depthUnit");
		f[8] = new MetricDepthField("f.finishDepth", "Termination Depth (m)", "f.depthUnit");
		f[9] = new TableRequiredPossibleValueField("sample.drillType", "Sample Type", getValues("FROM DrillType AS t", DrillType.class), SAMPLE_TABLE, SAMPLE_JOIN);
		add(new TwoLevelField("Drillhole Fields", f));
		
		f = new Field[8];
		f[0] = new BasicTextField("f.featureName", "Vertical Section Name");
		//f[1] = new PossibleValueField("f.person", "Section Collector", people);
		f[1] = new BasicTextField("f.person.name", "Section Collector");
		f[2] = new BasicDateField("f.startDate", "Sampling Start Date");
		f[3] = new BasicDateField("f.finishDate", "Completion Date");
		f[4] = new PossibleValueField("f.datumType", "Datum Type", getVertSectDatumTypes());
		f[5] = new BasicNumberField("f.datumElevation", "Datum Elevation (m)");
		f[6] = new MetricDepthField("f.startDepth", "Top Horizon (m)", "f.depthUnit");
		f[7] = new MetricDepthField("f.finishDepth", "Base Horizon (m)", "f.depthUnit");
		add(new TwoLevelField("Vertical Section Fields", f));
		
		f = new Field[9];
		f[0] = new HqlUniqueSubTableTextField("person.name", "Collector", new String[] {"f.samples", "sample.collectors"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "person")});
		f[1] = new TableRequiredDateField("sample.collectionDate", "Collection Date", SAMPLE_TABLE, SAMPLE_JOIN);
		f[2] = new TableRequiredPossibleValueField("sample.inPlace", "Fossils In Place", getInPlace(), SAMPLE_TABLE, SAMPLE_JOIN);
		f[3] = new HqlUniqueSubTablePossibleValueField("sentTo.fossilGroup", "Sent To Group", getValues("FROM FossilGroup AS f", FossilGroup.class), new String[] {"f.samples", "sample.sentTos"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "sentTo")});
		f[4] = new HqlUniqueSubTableTextField("sentTo.person.name", "Sent To Person", new String[] {"f.samples", "sample.sentTos"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "sentTo")});
		f[5] = new HqlUniqueSubTableTextField("sentTo.lab.name", "Sent To Lab", new String[] {"f.samples", "sample.sentTos"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "sentTo")});
		f[6] = new HqlUniqueSubTableTextField("sentTo.comments", "Sent To Comments", new String[] {"f.samples", "sample.sentTos"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "sentTo")});
		f[7] = new TableRequiredTextField("sample.notCollected", "Not Collected", SAMPLE_TABLE, SAMPLE_JOIN);
		f[8] = new TableRequiredTextField("sample.significance", "Significance/Comments", SAMPLE_TABLE, SAMPLE_JOIN);
		add(new TwoLevelField("Collection Fields", f));
		
		f = new Field[12];
		f[0] = new TableRequiredTextField("sample.stratUnit", "Stratigraphic Name", SAMPLE_TABLE, SAMPLE_JOIN);
		f[1] = new AgeField("sample.inferredStage", "Inferred Stage", ages, SAMPLE_TABLE, SAMPLE_JOIN);
		f[2] = new NumericAgeField("sample.inferredStage", "Inferred Stage (numeric)", SAMPLE_TABLE, SAMPLE_JOIN);
		f[3] = new AgeField("sample.knownStage", "Known Stage", ages, SAMPLE_TABLE, SAMPLE_JOIN);
		f[4] = new NumericAgeField("sample.knownStage", "Known Stage (numeric)", SAMPLE_TABLE, SAMPLE_JOIN);
		f[5] = new TableRequiredTextField("sample.columnMap", "Column/Map", SAMPLE_TABLE, SAMPLE_JOIN);
		f[6] = new TableRequiredNumberField("sample.dip", "Dip", SAMPLE_TABLE, SAMPLE_JOIN);
		f[7] = new TableRequiredPossibleValueField("sample.dipDirection", "Dip Direction", getDipDirection(), SAMPLE_TABLE, SAMPLE_JOIN);
		f[8] = new TableRequiredNumberField("sample.strike", "Strike", SAMPLE_TABLE, SAMPLE_JOIN);
		f[9] = new TableRequiredPossibleValueField("sample.facing", "Facing", getFacing(), SAMPLE_TABLE, SAMPLE_JOIN);
		f[10] = new HqlUniqueSubTableTextField("relationship.feature.frNumber.frNumber", "Sample Relationship - FR Number", new String[] {"f.samples", "sample.relationships"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "relationship")});
		f[11] = new HqlUniqueSubTableTextField("relationship.stratUnit", "Strat Relationship - Unit", new String[] {"f.samples", "sample.relationships"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "relationship")});
		add(new TwoLevelField("Stratigraphic Fields", f));
		
		f = new Field[15];
		f[0] = new TableRequiredPossibleValueField("sample.primaryGrainSize", "Primary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[1] = new TableRequiredPossibleValueField("sample.secondaryGrainSize", "Secondary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[2] = new TableRequiredPossibleValueField("sample.comparatorUsed", "Comparator Used", getComparatorUsed(), SAMPLE_TABLE, SAMPLE_JOIN);
		f[3] = new TableRequiredPossibleValueField("sample.bedThickness", "Bedding Thickness", getValues("FROM BedThickness AS b", BedThickness.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[4] = new TableRequiredPossibleValueField("sample.primaryBedding", "Primary Bedding", getValues("FROM Bedding AS b", Bedding.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[5] = new TableRequiredPossibleValueField("sample.secondaryBedding", "Secondary Bedding", getValues("FROM Bedding AS b", Bedding.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[6] = new TableRequiredPossibleValueField("sample.weathering", "Weathering", getValues("FROM Weathering AS w", Weathering.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[7] = new TableRequiredPossibleValueField("sample.hardness", "Hardness", getValues("FROM Hardness AS h", Hardness.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[8] = new TableRequiredPossibleValueField("sample.carbonate", "Carbonate", getValues("FROM Carbonate AS c", Carbonate.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[9] = new TableRequiredPossibleValueField("sample.colourModifier", "Colour Modifier", getValues("FROM ColourModifier AS c", ColourModifier.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[10] = new TableRequiredPossibleValueField("sample.primaryColour", "Primary Colour", getValues("FROM RockColour AS r", RockColour.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[11] = new TableRequiredPossibleValueField("sample.secondaryColour", "Secondary Colour", getValues("FROM RockColour AS r", RockColour.class), SAMPLE_TABLE, SAMPLE_JOIN);		
		f[12] = new TableRequiredTextField("sample.depositionEnv", "Inferred Environment", SAMPLE_TABLE, SAMPLE_JOIN);
		f[13] = new TableRequiredTextField("sample.rockNature", "Nature of Rock Unit", SAMPLE_TABLE, SAMPLE_JOIN);
		f[14] = new TableRequiredTextField("sample.stratComments", "Stratigraphy Comments", SAMPLE_TABLE, SAMPLE_JOIN);
		//need to add additional features
		add(new TwoLevelField("Sedimentary Feature Fields", f));
		
		f = new Field[1];
		f[0] = new TableRequiredTextField("sample.correspondence", "Correspondence", SAMPLE_TABLE, SAMPLE_JOIN);
		add(new TwoLevelField("Correspondence Fields", f));
		
		f = new Field[5];
		f[0] = new HqlUniqueSubTableTextField("person.name", "Adoptor", new String[] {"f.samples", "sample.records", "record.adoption", "adoption.adoptors"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "record"), new HqlJoin(false, "adoption"), new HqlJoin(false, "person")});
		f[1] = new TableRequiredDateField("record.adoption.adoptionDate", "Adoption Date", RECORD_TABLES, RECORD_JOINS);
		f[2] = new AgeField("record.adoption.stage", "Adopted Stage", ages, RECORD_TABLES, RECORD_JOINS);
		f[3] = new NumericAgeField("record.adoption.stage", "Adopted Stage (numeric)", RECORD_TABLES, RECORD_JOINS);
		f[4] = new TableRequiredTextField("record.adoption.comments", "Comments", RECORD_TABLES, RECORD_JOINS);
		add(new TwoLevelField("Adoption Fields", f));
		
		f = new Field[13];
		f[0] = new HqlUniqueSubTableTextField("person.name", "Identifier", new String[] {"f.samples", "sample.records", "record.paleontology", "paleontology.identifiers"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "record"), new HqlJoin(false, "paleontology"), new HqlJoin(false, "person")});
		f[1] = new TableRequiredDateField("record.paleontology.identificationDate", "Identification Date", RECORD_TABLES, RECORD_JOINS);
		f[2] = new AgeField("record.paleontology.stage", "Stage", ages, RECORD_TABLES, RECORD_JOINS);
		f[3] = new NumericAgeField("record.paleontology.stage", "Stage (numeric)", RECORD_TABLES, RECORD_JOINS);
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
		f[0] = new PossibleValueField("f.audit.createdById", "Created By", frUsers);
		f[1] = new BasicDateField("f.audit.createdDate", "Created Date");
		f[2] = new PossibleValueField("f.audit.submittedById", "Submitted By", frUsers);
		f[3] = new BasicDateField("f.audit.submittedDate", "Submitted Date");
		f[4] = new PossibleValueField("f.audit.approvedById", "Approved By", frUsers);
		f[5] = new BasicDateField("f.audit.approvedDate", "Approved Date");
		f[6] = new BasicTextField("f.audit.curatorComments", "Curator Comments");
		f[7] = new TableRequiredPossibleValueField("edit.editedById", "Edited By", frUsers, EDIT_TABLE, EDIT_JOIN);
		f[8] = new TableRequiredDateField("edit.editedDate", "Edited Date", EDIT_TABLE, EDIT_JOIN);
		f[9] = new TableRequiredTextField("edit.comments", "Edit Comments", EDIT_TABLE, EDIT_JOIN);
		add(new TwoLevelField("Audit Fields", f));
	}
	
	public String getQuery() throws InvalidOperatorException, InvalidValueException {
		return null;
	}

	public String getHQLQuery() throws InvalidOperatorException, InvalidValueException {
		return super.getHQLQuery("SELECT DISTINCT f", "Feature AS f", "f.audit.status = 'approved'", null, null);
	}
	
	protected <T extends Comparable<? super T>> List<T> getValues(String query, Class<T> clazz) {
		FredDAO featureDAO = FredHibernate.get().getDAOFactory().getFredDAO();
		List<T> values = null;
		try {
			values = featureDAO.getList(query, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	
	protected List<KeyValueObject> getSQLValues(String sql) {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>();
		Connection conn;
		try {
			conn = FREDUtil.getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next())
				options.add(new KeyValueObject(rs.getString(1), rs.getString(2)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return options;
	}
	
	protected List<KeyValueObject> getFeatureTypes() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("Outcrop", "Outcrop"));
		options.add(new KeyValueObject("Drillhole", "Drillhole"));
		options.add(new KeyValueObject("Vertical Section", "Vertical Section"));
		return options;
	}
	
	protected List<KeyValueObject> getDrillholeDatumTypes() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("KB", "KB"));
		options.add(new KeyValueObject("RT", "RT"));
		options.add(new KeyValueObject("Seafloor", "Seafloor"));
		return options;		
	}
	
	protected List<KeyValueObject> getVertSectDatumTypes() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(2);
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
		Vector<KeyValueObject> options = new Vector<KeyValueObject>(21);
		for (int i=0; i<21; i++) {
			options.add(o[i]);
		}
		return options;
	}
	
	protected List<KeyValueObject> getInPlace() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("Yes", "Yes"));
		options.add(new KeyValueObject("No", "No"));
		options.add(new KeyValueObject("Almost", "Almost"));
		options.add(new KeyValueObject("Unknown", "Unknown"));
		return options;
	}
	
	protected List<KeyValueObject> getDipDirection() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
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
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("Normal", "Normal"));
		options.add(new KeyValueObject("Overturned", "Overturned"));
		return options;
	}
	
	protected List<KeyValueObject> getComparatorUsed() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("Y", "Yes"));
		options.add(new KeyValueObject("N", "No"));
		return options;
	}
	
	public int getLastUsedId() {
		return lastUsedId;
	}

	public void incrementLastUsedId() {
		lastUsedId++;
	}
}


