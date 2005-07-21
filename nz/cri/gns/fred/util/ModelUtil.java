package nz.cri.gns.fred.util;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;

/**
 *
 */
public abstract class ModelUtil {

	protected DAOFactory factory;
	
	public ModelUtil(DAOFactory factory) {
		this.factory = factory;
	}
	
	public void closeSession() throws StorageAccessException {
		factory.closeSession();
	}
	
}
