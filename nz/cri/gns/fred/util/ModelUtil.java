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

    protected boolean equalsEmptyEquivNull(String str1, String str2) {
        if ((str1 == null || str1.length() == 0) ^ (str2 == null || str2.length() == 0))
            return false;
        if (str1 == null)
            return true;
        else
            return str1.equals(str2);
    }

	
}
