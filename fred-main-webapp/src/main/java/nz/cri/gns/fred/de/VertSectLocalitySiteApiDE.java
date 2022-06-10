package nz.cri.gns.fred.de;

import java.io.IOException;
import nz.cri.gns.auth.domain.exception.InsufficientPrivelegesException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.website.ContentProvider;

public class VertSectLocalitySiteApiDE extends DrillholeLocalitySiteApiDE {

	public VertSectLocalitySiteApiDE(User user, int folderID, DAOFactory factory, ContentProvider provider) throws StorageAccessException, InsufficientPrivelegesException {
		super(user, folderID, factory, provider, FREDConstants.VERTICAL_SECTION);
	}

	public VertSectLocalitySiteApiDE(Feature feature, int folderID, User user, DAOFactory factory, ContentProvider provider) throws InsufficientPrivelegesException, StorageAccessException, DataInputException, IOException {
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
