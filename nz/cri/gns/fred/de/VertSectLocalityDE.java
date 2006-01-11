package nz.cri.gns.fred.de;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.website.ContentProvider;

public class VertSectLocalityDE extends DrillholeLocalityDE {

	public VertSectLocalityDE(User user, int folderID, DAOFactory factory, ContentProvider provider) throws StorageAccessException, InsufficientPrivelegesException {
		super(user, folderID, factory, provider, FREDConstants.VERTICAL_SECTION);
	}

	public VertSectLocalityDE(Feature feature, int folderID, User user, DAOFactory factory, ContentProvider provider) throws InsufficientPrivelegesException, StorageAccessException, DataInputException {
		super(feature, folderID, user, factory, provider, FREDConstants.VERTICAL_SECTION);
	}

    @Override
	protected String getContentPrefix() {
		return "vertsect";
	}

    @Override
	public String getHeading() {
		return "Edit vertical section locality";
	}

}
