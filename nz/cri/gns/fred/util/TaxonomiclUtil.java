package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.dao.TaxonomicGroupDAO;

/**
 * @author iainm
 */
public class TaxonomiclUtil {

	private TaxonomicGroupDAO groupDAO;
	
	public TaxonomiclUtil(DAOFactory dao) {
		this.groupDAO = dao.getTaxonomicGroupDAO();
	}
	
	public List getPanelsIsMemberOf(int userId) throws StorageAccessException {
		List panels = groupDAO.getPanelsIsMemberOf(userId);
		Collections.sort(panels);
		return panels;
	}
}
