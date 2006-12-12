package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.FrUser;
import nz.cri.gns.fred.model.FrUserView;

public interface UserDAO {

	public FrUserView getFrUserView(String userName) throws StorageAccessException;

	public FrUserView getFrUserView(Integer userId) throws StorageAccessException;
	
	public FrUser createNewFrUser();
	
	public FrUser getFrUser(Integer userId) throws StorageAccessException;
	
	public FrUser save(FrUser frUser) throws StorageAccessException;
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

	
}
