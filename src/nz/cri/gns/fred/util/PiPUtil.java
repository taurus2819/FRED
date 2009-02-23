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
		String palQuery = "FROM PalList AS p WHERE ";
		List<Object> palParams = new Vector<Object>();
		if (country != null) {
			palQuery += "p.paleontology.record.sample.feature.siteView.countryName = ? AND ";
			palParams.add(country);
		}
		if (taxon != null) {
			palQuery += "UPPER(p.taxonomicName) LIKE ? AND ";
			palParams.add(taxon.toUpperCase());
		}
		List<PaleontologyListEntry> palLists = null;
		if (palQuery.length() > 38) {
			palQuery = palQuery.substring(0, palQuery.length() - 4).trim();
			palLists = fredDAO.getList(palQuery, PaleontologyListEntry.class, palParams.toArray());
		}
		
		String stageQuery = "SELECT DISTINCT r.record FROM RecordStageView AS r WHERE ";
		List<Object> stageParams = new Vector<Object>();
		if (maxAge != null) {
			stageQuery += "r.topAge <= ? AND ";
			stageParams.add(maxAge);
		}
		if (minAge != null) {
			stageQuery += "r.baseAge >= ? AND ";
			stageParams.add(minAge);
		}
		List<PaleontologyListEntry> stagePalLists = null;
		if (stageQuery.length() > 57) {
			stagePalLists = new Vector<PaleontologyListEntry>();
			stageQuery = stageQuery.substring(0, stageQuery.length() - 4).trim();
			System.out.println(stageQuery);
			List<Record> records = fredDAO.getList(stageQuery, Record.class, stageParams.toArray());
			for (Record record : records) {
				for (PaleontologyListEntry palList : record.getPaleontology().getListEntries())
					stagePalLists.add(palList);
			}
		}
		
		if (palLists == null)
			palLists = stagePalLists;
		else {
			if (stagePalLists != null)
				palLists.retainAll(stagePalLists);
		}
		
		if (limit == null || palLists == null || palLists.size() <= limit)
			return palLists;
		return palLists.subList(0, limit);
	}
	
}