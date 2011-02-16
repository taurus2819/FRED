package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.FrUser;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.UserView;

public class UserUtil extends ModelUtil {

	private FredDAO fredDAO;
	
	public static Integer FRED_WRITE = 2;
	public static Integer FRED_READ = 4;
	
	public UserUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
	}
    
	public FrUserView getFrUserView(Integer userId) throws StorageAccessException {
		return fredDAO.get(userId, nz.cri.gns.fred.hibernate.FrUserView.class);
	}

	public UserView getUserView(Integer userId) throws StorageAccessException {
		return fredDAO.get(userId, nz.cri.gns.fred.hibernate.UserView.class);
	}
	
	public FrUser createNewFrUser() {
		return fredDAO.createNewFrUser();
	}
	
	public FrUser getFrUser(Integer userId) throws StorageAccessException {
		return fredDAO.get(userId, nz.cri.gns.fred.hibernate.FrUser.class);
	}
	
	public List<FrUserView> getActiveFrWriters() throws StorageAccessException {
		return fredDAO.getList("FROM FrUserView AS f WHERE f.irId = ? AND f.deleted = ?", FrUserView.class, FRED_WRITE, false);
	}
	
	public List<FrUserView> getFrWriters() throws StorageAccessException {
		return fredDAO.getList("FROM FrUserView AS f WHERE f.irId = ?", FrUserView.class, FRED_WRITE);
	}
	
	public List<FrUserView> getActiveFrUsers() throws StorageAccessException {
		return fredDAO.getList("FROM FrUserView AS f WHERE f.deleted = ?", FrUserView.class, false);
	}
	
	public List<FrUserView> getActiveFrWritersWithout(Set<FrUserView> excludeFrUsers) throws StorageAccessException {
		List<FrUserView> frUsers = getActiveFrWriters();
		frUsers.removeAll(excludeFrUsers);
		Collections.sort(frUsers);
		return frUsers;
	}
		
	public FrUser saveOrUpdate(FrUser frUser) throws StorageAccessException {
		return fredDAO.saveOrUpdate(frUser);
	}
	
	public FrUser save(FrUser frUser) throws StorageAccessException {
		return fredDAO.save(frUser);
	}
	
}