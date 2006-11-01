package nz.cri.gns.fred.dao;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.FrUserView;

public interface UserDAO {

	public FrUserView getFrUserView(String userName) throws StorageAccessException;

	public FrUserView getFrUserView(Integer userId) throws StorageAccessException;
	
}
