package nz.cri.gns.fred.query;

import java.util.ArrayList;
import java.util.List;

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
import nz.cri.gns.db.querybuilder.advanced.hql.HqlTableRequiredTextField;
import nz.cri.gns.fred.dao.FeatureDAO;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.Person;

public class FREDQuery extends HqlQuery implements NumberSource {

	private static final long serialVersionUID = 20060120L;
	
	private int lastUsedId = 900000;
	
	public FREDQuery() {
		Field[] f = new Field[2];
		f[0] = new BasicTextField("f.featureName", "Feature Name");
		f[1] = new PossibleValueField("f.featureType", "Feature Type", getFeatureType());
		add(new TwoLevelField("Locality Fields", f));
		
		f = new Field[8];
		f[0] = new PossibleValueField("f.person", "Operating Company", getValues("FROM Person AS p", Person.class));
		f[1] = new BasicDateField("f.startDate", "Spud Date");
		f[2] = new BasicDateField("f.finishDate", "Completion Date");
		f[3] = new BasicTextField("f.licenceArea", "Licence Area");
		f[4] = new PossibleValueField("f.datumType", "Datum Type", getDrillholeDatumType());
		f[5] = new BasicNumberField("f.datumElevation", "Datum Elevation");
		f[6] = new BasicNumberField("f.startDepth", "Kick-off Depth");
		f[7] = new BasicNumberField("f.stopDepth", "Termination Depth");
		add(new TwoLevelField("Drillhole Fields", f));
		
		f = new Field[2];
		f[0] = new HqlTableRequiredTextField("s.significance", "Significance/Comments", "Sample AS s", new HqlAliasedJoin("f", "samples", "s"));
		f[1] = new BasicTextField("f.samples.siginificance", "Sig2");
		add(new TwoLevelField("Sample Fields", f));
		              
	}

	public String getQuery() throws InvalidOperatorException, InvalidValueException {
		String[] tables = { "feature f" };
		return getQuery(tables, null);
	}

	public String getHQLQuery() throws InvalidOperatorException, InvalidValueException {
		return super.getHQLQuery("f.feature", "Feature AS f", null, null, null);
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
	
	private List<KeyValueObject> getFeatureType() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("Outcrop", "Outcrop"));
		options.add(new KeyValueObject("Drillhole", "Drillhole"));
		options.add(new KeyValueObject("Vertical Section", "Vertical Section"));
		return options;
	}
	
	private List<KeyValueObject> getDrillholeDatumType() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(3);
		options.add(new KeyValueObject("KB", "KB"));
		options.add(new KeyValueObject("RT", "RT"));
		options.add(new KeyValueObject("Seafloor", "Seafloor"));
		return options;		
	}
	
	private List<KeyValueObject> getVertSectDatumType() {
		ArrayList<KeyValueObject> options = new ArrayList<KeyValueObject>(2);
		options.add(new KeyValueObject("Top", "Top"));
		options.add(new KeyValueObject("Bottom", "Bottom"));
		return options;		
	}
	
	public int getLastUsedId() {
		return lastUsedId;
	}

	public void incrementLastUsedId() {
		lastUsedId++;
	}
}


