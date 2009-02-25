package nz.cri.gns.fred.util;

import java.util.List;
import java.util.Vector;

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
	
	public List<PaleontologyListEntry> getPiPSamples(String country, String taxon, Double maxAge, Double minAge, Integer limit) throws StorageAccessException {
		boolean doSearch = false;
		List<Object> params = new Vector<Object>();
		String query = "SELECT DISTINCT p FROM PalList AS p ";
		if (maxAge != null || minAge != null)
			query += "JOIN p.paleontology.record.recordStageViews AS r ";
		query += "WHERE p.paleontology.record.audit.confidentialFlag = ? AND p.paleontology.record.palListAudit.confidentialFlag = ? AND p.paleontology.record.sample.audit.confidentialFlag = ? AND ";
		query += "p.paleontology.record.audit.status = ? AND p.paleontology.record.sample.audit.status = ? AND p.paleontology.record.sample.feature.audit.status = ? AND ";
		params.add(false);
		params.add(false);
		params.add(false);
		params.add(AuditUtil.APPROVED);
		params.add(AuditUtil.APPROVED);
		params.add(AuditUtil.APPROVED);		
		
		if (country != null) {
			query += "p.paleontology.record.sample.feature.siteView.countryName = ? AND ";
			params.add(country);
			doSearch = true;
		}
		if (taxon != null) {
			query += "UPPER(p.taxonomicName) LIKE ? AND ";
			params.add("%" + taxon.toUpperCase() + "%");
			doSearch = true;
		}
		if (maxAge != null) {
			query += "r.topAge <= ? AND ";
			params.add(maxAge);
			doSearch = true;
		}
		if (minAge != null) {
			query += "r.baseAge >= ? AND ";
			params.add(minAge);
			doSearch = true;
		}
		
		List<PaleontologyListEntry> palLists = null;
		if (doSearch) {
			query = query.substring(0, query.length() - 4).trim();
			System.out.println(query);
			palLists = fredDAO.getList(query, limit, PaleontologyListEntry.class, params.toArray());
			System.out.println("Pal Lists size = " + palLists.size());
		}
		
		return palLists;
	}
	
}