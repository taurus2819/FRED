package nz.cri.gns.fred.util;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.FREDConstants;

/**
 *
 */
public abstract class ModelUtil implements FREDConstants {

	protected DAOFactory factory;
	
	public ModelUtil(DAOFactory factory) {
		this.factory = factory;
	}
	
	public void closeSession() throws StorageAccessException {
		factory.closeSession();
	}
	
}
