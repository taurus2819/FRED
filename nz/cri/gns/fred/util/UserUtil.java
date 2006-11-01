package nz.cri.gns.fred.util;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.UserDAO;
import nz.cri.gns.fred.model.FrUserView;

public class UserUtil extends ModelUtil {

	private UserDAO userDAO;
	
	public UserUtil(DAOFactory factory) {
		super(factory);
		this.userDAO = factory.getUserDAO();
	}
    
	public FrUserView getFrUserView(String userName) throws StorageAccessException {
		return userDAO.getFrUserView(userName);
	}
	
	public FrUserView getFrUserView(Integer userId) throws StorageAccessException {
		return userDAO.getFrUserView(userId);
	}
		
}
