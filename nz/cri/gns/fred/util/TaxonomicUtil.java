package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;

import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.dao.TaxonomicGroupDAO;
import nz.cri.gns.fred.model.TaxonomicGroup;

/**
 * @author iainm
 */
public class TaxonomicUtil {

	private TaxonomicGroupDAO groupDAO;
	
	public TaxonomicUtil(DAOFactory dao) {
		this.groupDAO = dao.getTaxonomicGroupDAO();
	}
	
	public List getPanelsIsMemberOf(UserAccount user) throws StorageAccessException {
		List panels = groupDAO.getPanelsIsMemberOf(Integer.parseInt(user.getId()));
		Collections.sort(panels);
		return panels;
	}
	
	public int getProvisionalCount(TaxonomicGroup group) throws StorageAccessException {
		return groupDAO.getProvisionalCount(group);
	}
}
