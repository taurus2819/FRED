package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;

public interface StageDAO {

	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

	public int getMaxAgeId() throws StorageAccessException;
	
}
