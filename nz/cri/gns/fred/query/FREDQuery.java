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
import nz.cri.gns.db.querybuilder.advanced.TwoLevelField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlAliasedJoin;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlQuery;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlTableRequiredDateField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlTableRequiredNumberField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlTableRequiredPossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlTableRequiredTextField;
import nz.cri.gns.fred.dao.FeatureDAO;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.RockColour;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.Weathering;
import nz.cri.gns.fred.util.FREDUtil;

public class FREDQuery extends HqlQuery implements NumberSource {

	private static final long serialVersionUID = 20060120L;
	
	private int lastUsedId = 900000;
	
	private List<Person> people = null;
	
	public FREDQuery() {

		this.people = getValues("FROM Person AS p", Person.class);
		
		Field[] f = new Field[14];
		f[0] = new BasicTextField("f.featureName", "Feature Name");
		f[1] = new PossibleValueField("f.featureType", "Feature Type", getFeatureTypes());
		f[2] = new PossibleValueField("f.masterfile", "Masterfile", getValues("FROM Folder AS f WHERE f.folderType.name='Admin'", Folder.class));
		f[3] = new BasicTextField("f.siteView.nzmgSheet", "NZMS260 Sheet");
		f[4] = new PossibleValueField("f.siteView.qmapSheet", "QMap Sheet", getQMapSheets());
		f[5] = new PossibleValueField("f.siteView.countryName", "Country", getSQLValues("SELECT country_name as cn, country_name FROM mis.country ORDER BY UPPER(country_name)"));
		f[6] = new PossibleValueField("f.siteView.island", "Island", getSQLValues("SELECT DISTINCT name as n, name FROM sc.island ORDER BY UPPER(name)"));
		f[7] = new BasicNumberField("f.siteView.nzmgEast", "NZMG Easting");
		f[8] = new BasicNumberField("f.siteView.nzmgNorth", "NZMG Northing");
		f[9] = new BasicNumberField("f.siteView.latitude", "Latitude");
		f[10] = new BasicNumberField("f.siteView.longitude", "Longitude");
		f[11] = new BasicTextField("f.locality", "Locality");
		f[12] = new BasicTextField("f.coordComments", "Coordinate Comments");
		f[13] = new BasicTextField("f.comments", "Locality Comments");
		add(new TwoLevelField("Locality Fields", f));
		
		f = new Field[9];
		f[0] = new BasicTextField("f.featureName", "Drillhole Name");
		f[1] = new PossibleValueField("f.person", "Operating Company", people);
		f[2] = new BasicDateField("f.startDate", "Spud Date");
		f[3] = new BasicDateField("f.finishDate", "Completion Date");
		f[4] = new BasicTextField("f.licenceArea", "Licence Area");
		f[5] = new PossibleValueField("f.datumType", "Datum Type", getDrillholeDatumTypes());
		f[6] = new BasicNumberField("f.datumElevation", "Datum Elevation (m)");
		f[7] = new MetricDepthField("f.startDepth", "Kick-off Depth (m)", "f.depthUnit");
		f[8] = new MetricDepthField("f.finishDepth", "Termination Depth (m)", "f.depthUnit");
		add(new TwoLevelField("Drillhole Fields", f));
		
		f = new Field[8];
		f[0] = new BasicTextField("f.featureName", "Vertical Section Name");
		f[1] = new PossibleValueField("f.person", "Section Collector", people);
		f[2] = new BasicDateField("f.startDate", "Sampling Start Date");
		f[3] = new BasicDateField("f.finishDate", "Completion Date");
		f[4] = new PossibleValueField("f.datumType", "Datum Type", getVertSectDatumTypes());
		f[5] = new BasicNumberField("f.datumElevation", "Datum Elevation (m)");
		f[6] = new MetricDepthField("f.startDepth", "Top Horizon (m)", "f.depthUnit");
		f[7] = new MetricDepthField("f.finishDepth", "Base Horizon (m)", "f.depthUnit");
		add(new TwoLevelField("Vertical Section Fields", f));
		
		f = new Field[4];
		f[0] = new HqlTableRequiredDateField("sample.collectionDate", "Collection Date", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[1] = new HqlTableRequiredPossibleValueField("sample.inPlace", "Fossils In Place", getInPlace(), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[2] = new HqlTableRequiredTextField("sample.notCollected", "Not Collected", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[3] = new HqlTableRequiredTextField("sample.significance", "Significance/Comments", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		//need to add collectors, sent to
		add(new TwoLevelField("Collection Fields", f));
		
		f = new Field[6];
		f[0] = new HqlTableRequiredTextField("sample.stratUnit", "Stratigraphic Name", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[1] = new HqlTableRequiredTextField("sample.columnMap", "Column/Map", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[2] = new HqlTableRequiredNumberField("sample.dip", "Dip", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[3] = new HqlTableRequiredPossibleValueField("sample.dipDirection", "Dip Direction", getDipDirection(), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[4] = new HqlTableRequiredNumberField("sample.strike", "Strike", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[5] = new HqlTableRequiredPossibleValueField("sample.facing", "Facing", getFacing(), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		//need to add stages, relationships
		add(new TwoLevelField("Stratigraphic Fields", f));
		
		f = new Field[14];
		f[0] = new HqlTableRequiredPossibleValueField("sample.primaryGrainSize", "Primary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[1] = new HqlTableRequiredPossibleValueField("sample.secondaryGrainSize", "Secondary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[2] = new HqlTableRequiredPossibleValueField("sample.comparatorUsed", "Comparator Used", getComparatorUsed(), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[3] = new HqlTableRequiredPossibleValueField("sample.bedThickness", "Bedding Thickness", getValues("FROM BedThickness AS b", BedThickness.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[4] = new HqlTableRequiredPossibleValueField("sample.primaryBedding", "Primary Bedding", getValues("FROM Bedding AS b", Bedding.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[5] = new HqlTableRequiredPossibleValueField("sample.secondaryBedding", "Secondary Bedding", getValues("FROM Bedding AS b", Bedding.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[6] = new HqlTableRequiredPossibleValueField("sample.weathering", "Weathering", getValues("FROM Weathering AS w", Weathering.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[7] = new HqlTableRequiredPossibleValueField("sample.hardness", "Hardness", getValues("FROM Hardness AS h", Hardness.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[8] = new HqlTableRequiredPossibleValueField("sample.carbonate", "Carbonate", getValues("FROM Carbonate AS c", Carbonate.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[9] = new HqlTableRequiredPossibleValueField("sample.colourModifier", "Colour Modifier", getValues("FROM ColourModifier AS c", ColourModifier.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[10] = new HqlTableRequiredPossibleValueField("sample.primaryColour", "Primary Colour", getValues("FROM RockColour AS r", RockColour.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[11] = new HqlTableRequiredPossibleValueField("sample.secondaryColour", "Secondary Colour", getValues("FROM RockColour AS r", RockColour.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));		
		f[12] = new HqlTableRequiredTextField("sample.depositionEnv", "Inferred Enviornment", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[13] = new HqlTableRequiredTextField("sample.rockNature", "Nature of Rock Unit", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		//need to add additional features
		add(new TwoLevelField("Sedimentary Feature Fields", f));
		
		f = new Field[1];
		f[0] = new HqlTableRequiredTextField("sample.correspondence", "Correspondence", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		add(new TwoLevelField("Correspondence Fields", f));
		
		String[] recordTables = new String[] {"Sample", "Record"};
		HqlAliasedJoin[] recordJoins = {new HqlAliasedJoin("f", "samples", "sample"), new HqlAliasedJoin("sample", "records", "record")};
		
		f = new Field[2];
		f[0] = new HqlTableRequiredDateField("record.adoption.adoptionDate", "Adoption Date", recordTables, recordJoins);
		f[1] = new HqlTableRequiredTextField("record.adoption.comments", "Comments", recordTables, recordJoins);
		//need to add adoptors and stages
		add(new TwoLevelField("Adoption Fields", f));
		
		String[] palListTables = new String[] {"Sample", "Record", "PalList"};
		HqlAliasedJoin[] palListJoins = {new HqlAliasedJoin("f", "samples", "sample"), new HqlAliasedJoin("sample", "records", "record"), new HqlAliasedJoin("record", "paleontology.listEntries", "palList")};
		
		f = new Field[10];
		f[0] = new HqlTableRequiredDateField("record.paleontology.identificationDate", "Identification Date", recordTables, recordJoins);
		f[1] = new HqlTableRequiredTextField("record.paleontology.stageComments", "Stage Comments", recordTables, recordJoins);
		f[2] = new HqlTableRequiredPossibleValueField("record.paleontology.labSection", "Laboratory", getValues("FROM LabSection AS ls", LabSection.class), recordTables, recordJoins);
		f[3] = new HqlTableRequiredTextField("record.paleontology.labNumber", "Lab Number", recordTables, recordJoins);
		f[4] = new HqlTableRequiredTextField("record.paleontology.collectionComments", "Collection Comments", recordTables, recordJoins);
		f[5] = new HqlTableRequiredPossibleValueField("palList.taxonomicGroup", "Taxonomic Group", getValues("FROM TaxonomicGroup AS tg", TaxonomicGroup.class), palListTables, palListJoins);
		f[6] = new HqlTableRequiredTextField("palList.taxon.taxonomicName", "Taxonomic Name", palListTables, palListJoins);
		f[7] = new HqlTableRequiredNumberField("palList.specimenCount", "Specimen Count", palListTables, palListJoins);
		f[8] = new HqlTableRequiredTextField("palList.specimenCoords", "Specimen Coordinates", palListTables, palListJoins);
		f[9] = new HqlTableRequiredTextField("palList.comments", "Paleontology List Comments", palListTables, palListJoins);
		//need to add identifiers and stages
		add(new TwoLevelField("Paleontology Fields", f));
		
		
	}

	public String getQuery() throws InvalidOperatorException, InvalidValueException {
		String[] tables = { "feature f" };
		return getQuery(tables, null);
	}

	public String getHQLQuery() throws InvalidOperatorException, InvalidValueException {
		return super.getHQLQuery("SELECT f", "Feature AS f", null, null, null);
	}
	
	private <T extends Comparable<? super T>> List<T> getValues(String query, Class<T> clazz) {
		FeatureDAO featureDAO = HibernateUtil.get().getDAOFactory().getFeatureDAO();
		List<T> values = null;
		try {
			values = featureDAO.getList(query, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	
	private List<KeyValueObject> getSQLValues(String sql) {
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
	
	private List<KeyValueObject> getFeatureTypes() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("Outcrop", "Outcrop"));
		options.add(new KeyValueObject("Drillhole", "Drillhole"));
		options.add(new KeyValueObject("Vertical Section", "Vertical Section"));
		return options;
	}
	
	private List<KeyValueObject> getDrillholeDatumTypes() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("KB", "KB"));
		options.add(new KeyValueObject("RT", "RT"));
		options.add(new KeyValueObject("Seafloor", "Seafloor"));
		return options;		
	}
	
	private List<KeyValueObject> getVertSectDatumTypes() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(2);
		options.add(new KeyValueObject("Top", "Top"));
		options.add(new KeyValueObject("Bottom", "Bottom"));
		return options;		
	}
	
	private List<KeyValueObject> getQMapSheets() {
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
	
	private List<KeyValueObject> getInPlace() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("Yes", "Yes"));
		options.add(new KeyValueObject("No", "No"));
		options.add(new KeyValueObject("Almost", "Almost"));
		options.add(new KeyValueObject("Unknown", "Unknown"));
		return options;
	}
	
	private List<KeyValueObject> getDipDirection() {
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
	
	private List<KeyValueObject> getFacing() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("Normal", "Normal"));
		options.add(new KeyValueObject("Overturned", "Overturned"));
		return options;
	}
	
	private List<KeyValueObject> getComparatorUsed() {
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


