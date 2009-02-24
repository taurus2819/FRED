package nz.cri.gns.fred.util;

import java.util.List;
import java.util.Vector;

import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Disjunction;
import net.sf.hibernate.expression.Expression;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Record;

public class PiPUtil extends ModelUtil {

	private FredDAO fredDAO;
	
	public PiPUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
	}
	
	public List<PaleontologyListEntry> getPiPSamples(String country, String taxon, Integer maxAge, Integer minAge, Integer limit) throws StorageAccessException {
		/*List<Criterion> crit = new Vector<Criterion>();
		if (country != null)
			crit.add(Expression.eq("paleontology.record.sample.feature.siteView.countryName", country));
		if (taxon != null)
			crit.add(Expression.ilike("taxonomicName", taxon));
		if (maxAge != null) {
			Disjunction or = new Disjunction();
			or.add(Expression.le("paleontology.stage.topAge", maxAge));
			or.add(Expression.le("paleontology.record.sample.stageByKnownStageId.topAge", maxAge));
			or.add(Expression.le("paleontology.record.sample.stageByInferredStageId.topAge", maxAge));
			crit.add(or);
		}
		return fredDAO.getList(PaleontologyListEntry.class, crit, limit);*/
		
		boolean doSearch = false;
		
		String query = "SELECT DISTINCT p FROM PalList AS p ";
		if (maxAge != null || minAge != null)
			query += "JOIN p.paleontology.record.recordStageViews AS r ";
		query += "WHERE ";
		
		List<Object> params = new Vector<Object>();
		if (country != null) {
			query += "UPPER(p.paleontology.record.sample.feature.siteView.countryName) = ? AND ";
			params.add(country.toUpperCase());
			doSearch = true;
		}
		if (taxon != null) {
			query += "UPPER(p.taxonomicName) LIKE ? AND ";
			params.add(taxon.toUpperCase());
			doSearch = true;
		}
		if (maxAge != null) {
			query += "r.baseAge <= ? AND ";
			params.add(maxAge);
			doSearch = true;
		}
		if (minAge != null) {
			query += "r.topAge >= ? AND ";
			params.add(minAge);
			doSearch = true;
		}
		
		List<PaleontologyListEntry> palLists = null;
		if (doSearch) {
			query = query.substring(0, query.length() - 4).trim();
			System.out.println(query);
			palLists = fredDAO.getList(query, PaleontologyListEntry.class, params.toArray());
		}
		
		if (limit == null || palLists == null || palLists.size() <= limit)
			return palLists;
		return palLists.subList(0, limit);
	}
	
}