package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.UserDAO;
import nz.cri.gns.fred.model.FrUser;
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

	public FrUser createNewFrUser() {
		return userDAO.createNewFrUser();
	}
	
	public FrUser getFrUser(Integer userId) throws StorageAccessException {
		return userDAO.getFrUser(userId);
	}
	
	public List<FrUserView> getFrUsersWithout(Set<FrUserView> excludeFrUsers) throws StorageAccessException {
		List<FrUserView> frUsers = userDAO.getList("FROM FrUserView AS f", FrUserView.class);
		frUsers.removeAll(excludeFrUsers);
		Collections.sort(frUsers);
		return frUsers;
	}
		
	public FrUser save(FrUser frUser) throws StorageAccessException {
		return userDAO.save(frUser);
	}
}
