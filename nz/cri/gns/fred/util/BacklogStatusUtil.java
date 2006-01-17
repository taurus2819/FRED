package nz.cri.gns.fred.util;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.BacklogStatusDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.BacklogStatus;

public class BacklogStatusUtil extends ModelUtil {

	private BacklogStatusDAO backlogStatusDAO;
	
	public BacklogStatusUtil(DAOFactory factory) {
		super(factory);
		this.backlogStatusDAO = factory.getBacklogStatusDAO();
	}

	public BacklogStatus getBacklogStatus(String mapNumber) throws StorageAccessException {
		System.out.println("Getting BS");
		return backlogStatusDAO.getBacklogStatus(mapNumber);
	}

	public List<BacklogStatus> getBacklogStatusInMasterfile(int masterfileId) throws StorageAccessException {
		return backlogStatusDAO.getBacklogStatusInMasterfile(masterfileId);
	}

	
}
