package nz.cri.gns.fred.util;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.BacklogStatus;
import nz.cri.gns.fred.model.FREDConstants;

public class BacklogStatusUtil extends ModelUtil {

	private FredDAO fredDAO;
	
	public BacklogStatusUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
	}

	public BacklogStatus getBacklogStatus(String mapNumber) throws StorageAccessException {
		return fredDAO.getFirst("FROM BacklogStatus AS bs WHERE bs.mapNumber = ?", BacklogStatus.class, mapNumber);
	}

	public List<BacklogStatus> getBacklogStatusInMasterfile(int masterfileId) throws StorageAccessException {
		return fredDAO.getList("FROM BacklogStatus AS bs WHERE bs.masterfileId = ?", BacklogStatus.class, masterfileId);
	}

	public String getStatus() throws StorageAccessException {
		if (getSumLocalityCount() - getSumNewCount() == 0)
			return FREDConstants.BACKLOG_EMPTY;
		if (getSumProcessingCount() > 0 || getSumCompletedCount() > 0)
			return FREDConstants.BACKLOG_PROCESSING;
		if (getSumCompletedCount() == getSumLocalityCount() - getSumNewCount())
			return FREDConstants.BACKLOG_COMPLETE;
		return FREDConstants.BACKLOG_NOT_STARTED;
	}
	
	public String getStatus(int masterfileId) throws StorageAccessException {
		if (getSumLocalityCount(masterfileId) - getSumNewCount(masterfileId) == 0)
			return FREDConstants.BACKLOG_EMPTY;
		if (getSumCompletedCount(masterfileId) == getSumLocalityCount(masterfileId) - getSumNewCount(masterfileId))
			return FREDConstants.BACKLOG_COMPLETE;
		if (getSumProcessingCount(masterfileId) > 0 || getSumCompletedCount(masterfileId) > 0)
			return FREDConstants.BACKLOG_PROCESSING;
		return FREDConstants.BACKLOG_NOT_STARTED;
	}
	
	public int getSumLocalityCount() throws StorageAccessException {
   		List<Integer> list = fredDAO.getList("SELECT sum(bs.localityCount) FROM BacklogStatus AS bs", Integer.class);
    	return list.get(0);
    }
	
	public int getSumLocalityCount(Integer masterfileId) throws StorageAccessException {
		List<Integer> list = fredDAO.getList("SELECT sum(bs.localityCount) FROM BacklogStatus AS bs WHERE bs.masterfileId = ?", Integer.class, masterfileId);
		if (list.get(0) != null)
			return list.get(0);
		return 0;
	}

	public int getSumProcessingCount() throws StorageAccessException {
   		List<Integer> list = fredDAO.getList("SELECT sum(bs.processingCount) FROM BacklogStatus AS bs", Integer.class);
    	return list.get(0);
	}
	
	public int getSumProcessingCount(int masterfileId) throws StorageAccessException {
		List<Integer> list = fredDAO.getList("SELECT sum(bs.processingCount) FROM BacklogStatus AS bs WHERE bs.masterfileId = ?", Integer.class, masterfileId);
		if (list.get(0) != null)
			return list.get(0);
		return 0;
	}
	
	public int getSumCompletedCount() throws StorageAccessException {
   		List<Integer> list = fredDAO.getList("SELECT sum(bs.completedCount) FROM BacklogStatus AS bs", Integer.class);
    	return list.get(0);
	}
	
	public int getSumCompletedCount(int masterfileId) throws StorageAccessException {
		List<Integer> list = fredDAO.getList("SELECT sum(bs.completedCount) FROM BacklogStatus AS bs WHERE bs.masterfileId = ?", Integer.class, masterfileId);
		if (list.get(0) != null)
			return list.get(0);
		return 0;
	}
	
	public int getSumNewCount() throws StorageAccessException {
   		List<Integer> list = fredDAO.getList("SELECT sum(bs.newCount) FROM BacklogStatus AS bs", Integer.class);
    	return list.get(0);
	}
	
	public int getSumNewCount(int masterfileId) throws StorageAccessException {
		List<Integer> list = fredDAO.getList("SELECT sum(bs.newCount) FROM BacklogStatus AS bs WHERE bs.masterfileId = ?", Integer.class, masterfileId);
		if (list.get(0) != null)
			return list.get(0);
		return 0;
	}

}