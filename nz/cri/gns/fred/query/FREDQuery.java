package nz.cri.gns.fred.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import nz.cri.gns.core.NameableAndIdentifiable;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DatabaseApp2;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.db.querybuilder.BasicDateField;
import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.BasicTextField;
import nz.cri.gns.db.querybuilder.Field;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.advanced.AdvancedQuery;
import nz.cri.gns.db.querybuilder.advanced.FilteredNumberField;
import nz.cri.gns.db.querybuilder.advanced.FilteredPossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.NumberSource;
import nz.cri.gns.db.querybuilder.advanced.PossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.StandardJoin;
import nz.cri.gns.db.querybuilder.advanced.Table;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredDateField;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredPossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredTextField;
import nz.cri.gns.db.querybuilder.advanced.TwoLevelField;
import nz.cri.gns.db.querybuilder.advanced.UniqueSubTableFilteredNumberField;
import nz.cri.gns.db.querybuilder.advanced.UniqueSubTablePossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlQuery;
import nz.cri.gns.fred.dao.FeatureDAO;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.intranet.DBConnection;

public class FREDQuery extends HqlQuery implements NumberSource {

	private static final long serialVersionUID = 20060120L;
	
	private int lastUsedId = 900000;
	
	public FREDQuery() {
		Field[] f = new Field[2];
		f[0] = new PossibleValueField("f.person", "Person", getValues("SELECT p.personId, p.name FROM Person AS p", Person.class));
		f[0] = new BasicTextField("f.featureName", "Feature Name");
		f[1] = new BasicNumberField("f.startDepth", "Start Depth");
		add(new TwoLevelField("Test 1", f));

		f = new Field[2];
		//f[0] = new PossibleValueField("f.person_id", "Person", getValues("SELECT person_id, name FROM person ORDER BY person_id", app));
		f[0] = new BasicTextField("f.featureName", "Feature Name");
		f[1] = new BasicNumberField("f.startDepth", "Start Depth");
		add(new TwoLevelField("Test 2", f));
	}

	public String getQuery() throws InvalidOperatorException, InvalidValueException {
		String[] tables = { "feature f" };
		return getQuery(tables, null);
	}

	public String getHQLQuery() throws InvalidOperatorException, InvalidValueException {
		return super.getHQLQuery("", "Feature AS f", null, null, null);
	}
	
	private <T extends Comparable<? super T>> List<T> getValues(String query, Class<T> clazz) {
		FeatureDAO featureDAO = HibernateUtil.get().getDAOFactory().getFeatureDAO();
		List<T> v = null;
		try {
			v = featureDAO.getList(query, clazz);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;
	}
	
	public int getLastUsedId() {
		return lastUsedId;
	}

	public void incrementLastUsedId() {
		lastUsedId++;
	}
}


