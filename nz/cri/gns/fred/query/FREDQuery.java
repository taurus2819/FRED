package nz.cri.gns.fred.query;

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
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Person;

public class FREDQuery extends HqlQuery implements NumberSource {

	private static final long serialVersionUID = 20060120L;
	
	private int lastUsedId = 900000;
	
	public FREDQuery() {
		Field[] f = new Field[12];
		f[0] = new BasicTextField("f.featureName", "Feature Name");
		f[1] = new PossibleValueField("f.featureType", "Feature Type", getFeatureTypes());
		f[2] = new PossibleValueField("f.masterfile", "Masterfile", getValues("FROM Folder AS f WHERE f.folderType.name='Admin'", Folder.class));
		f[3] = new BasicTextField("f.siteView.nzmgSheet", "NZMS260 Sheet");
		f[4] = new PossibleValueField("f.siteView.qmapSheet", "QMap Sheet", getQMapSheets());
		//f[9] = new PossibleValueField("pv.country_name", "Country", getValues("SELECT country_name as quoted_country_name, country_name FROM mis.country ORDER BY UPPER(country_name)", app));
		//f[10] = new PossibleValueField("pv.island", "Island", getValues("SELECT DISTINCT name as quoted_name, name FROM sc.island ORDER BY UPPER(name)", app));
		f[5] = new BasicNumberField("f.siteView.nzmgEast", "NZMG Easting");
		f[6] = new BasicNumberField("f.siteView.nzmgNorth", "NZMG Northing");
		f[7] = new BasicNumberField("f.siteView.latitude", "Latitude");
		f[8] = new BasicNumberField("f.siteView.longitude", "Longitude");
		f[9] = new BasicTextField("f.locality", "Locality");
		f[10] = new BasicTextField("f.coordComments", "Coordinate Comments");
		f[11] = new BasicTextField("f.comments", "Locality Comments");
		add(new TwoLevelField("Locality Fields", f));
		
		f = new Field[9];
		f[0] = new BasicTextField("f.featureName", "Drillhole Name");
		f[1] = new PossibleValueField("f.person", "Operating Company", getValues("FROM Person AS p", Person.class));
		f[2] = new BasicDateField("f.startDate", "Spud Date");
		f[3] = new BasicDateField("f.finishDate", "Completion Date");
		f[4] = new BasicTextField("f.licenceArea", "Licence Area");
		f[5] = new PossibleValueField("f.datumType", "Datum Type", getDrillholeDatumTypes());
		f[6] = new BasicNumberField("f.datumElevation", "Datum Elevation");
		f[7] = new BasicNumberField("f.startDepth", "Kick-off Depth");
		f[8] = new BasicNumberField("f.stopDepth", "Termination Depth");
		add(new TwoLevelField("Drillhole Fields", f));
		
		f = new Field[8];
		f[0] = new BasicTextField("f.featureName", "Vertical Section Name");
		f[1] = new PossibleValueField("f.person", "Section Collector", getValues("FROM Person AS p", Person.class));
		f[2] = new BasicDateField("f.startDate", "Sampling Start Date");
		f[3] = new BasicDateField("f.finishDate", "Completion Date");
		f[4] = new PossibleValueField("f.datumType", "Datum Type", getVertSectDatumTypes());
		f[5] = new BasicNumberField("f.datumElevation", "Datum Elevation");
		f[6] = new BasicNumberField("f.startDepth", "Top Horizon");
		f[7] = new BasicNumberField("f.stopDepth", "Base Horizon");
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
		
		f = new Field[4];
		f[0] = new HqlTableRequiredPossibleValueField("sample.grainSizeByPrimaryGrainsizeId.name", "Primary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[1] = new HqlTableRequiredPossibleValueField("sample.grainSizeBySecondaryGrainsizeId.name", "Secondary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class), "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[2] = new HqlTableRequiredTextField("sample.depositionEnv", "Inferred Enviornment", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		f[3] = new HqlTableRequiredTextField("sample.rockNature", "Nature of Rock Unit", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		//need to add grain size, etc and additional features
		add(new TwoLevelField("Sedimentary Feature Fields", f));
		
		f = new Field[1];
		f[0] = new HqlTableRequiredTextField("sample.correspondence", "Correspondence", "Sample", new HqlAliasedJoin("f", "samples", "sample"));
		add(new TwoLevelField("Correspondence Fields", f));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return values;
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
	
	public int getLastUsedId() {
		return lastUsedId;
	}

	public void incrementLastUsedId() {
		lastUsedId++;
	}
}


