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

public class PiPUtil extends ModelUtil {

	private FredDAO fredDAO;
	
	public PiPUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
	}
	
	public List<PaleontologyListEntry> getPiPSamples(String country, String taxon, Integer maxAge, Integer MinAge, Integer limit) throws StorageAccessException {
		List<Criterion> crit = new Vector<Criterion>();
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
		return fredDAO.getList(PaleontologyListEntry.class, crit, limit);
	}
	
	
}