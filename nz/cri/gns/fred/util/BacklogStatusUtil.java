package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.BacklogStatusDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.BacklogStatus;
import nz.cri.gns.fred.model.FREDConstants;

public class BacklogStatusUtil extends ModelUtil {

	private BacklogStatusDAO backlogStatusDAO;
	
	public BacklogStatusUtil(DAOFactory factory) {
		super(factory);
		this.backlogStatusDAO = factory.getBacklogStatusDAO();
	}

	public BacklogStatus getBacklogStatus(String mapNumber) throws StorageAccessException {
		return backlogStatusDAO.getBacklogStatus(mapNumber);
	}

	public List<BacklogStatus> getBacklogStatusInMasterfile(int masterfileId) throws StorageAccessException {
		List<BacklogStatus> bss = backlogStatusDAO.getBacklogStatusInMasterfile(masterfileId);
		Collections.sort(bss);
		return bss;
	}

	public String getStatus() throws StorageAccessException {
		if (getSumLocalityCount() - getSumNewCount() == 0)
			return FREDConstants.BACKLOG_EMPTY;
		if (getSumCompletedCount() > 0) {
			if (getSumProcessingCount() > 0)
				return FREDConstants.BACKLOG_PROCESSING;
		if (getSumCompletedCount() == getSumLocalityCount() - getSumNewCount())
			return FREDConstants.BACKLOG_COMPLETE;
		return FREDConstants.BACKLOG_NOT_STARTED;
	}
	
	public String getStatus(int masterfileId) throws StorageAccessException {
		if (getSumLocalityCount(masterfileId) - getSumNewCount(masterfileId) == 0)
			return FREDConstants.BACKLOG_EMPTY;
		if (getSumProcessingCount(masterfileId) > 0 || getSumCompletedCount(masterfileId) > 0)
			return FREDConstants.BACKLOG_PROCESSING;
		if (getSumCompletedCount(masterfileId) == getSumLocalityCount(masterfileId) - getSumNewCount(masterfileId))
			return FREDConstants.BACKLOG_COMPLETE;
		return FREDConstants.BACKLOG_NOT_STARTED;
	}
	
	public int getSumLocalityCount() throws StorageAccessException {
		return backlogStatusDAO.getSumLocalityCount();
	}
	
	public int getSumLocalityCount(int masterfileId) throws StorageAccessException {
		return backlogStatusDAO.getSumLocalityCount(masterfileId);
	}

	public int getSumProcessingCount() throws StorageAccessException {
		return backlogStatusDAO.getSumProcessingCount();
	}
	
	public int getSumProcessingCount(int masterfileId) throws StorageAccessException {
		return backlogStatusDAO.getSumProcessingCount(masterfileId);
	}
	
	public int getSumCompletedCount() throws StorageAccessException {
		return backlogStatusDAO.getSumCompletedCount();
	}
	
	public int getSumCompletedCount(int masterfileId) throws StorageAccessException {
		return backlogStatusDAO.getSumCompletedCount(masterfileId);
	}
	
	public int getSumNewCount() throws StorageAccessException {
		return backlogStatusDAO.getSumNewCount();
	}
	
	public int getSumNewCount(int masterfileId) throws StorageAccessException {
		return backlogStatusDAO.getSumNewCount(masterfileId);
	}
	
}
