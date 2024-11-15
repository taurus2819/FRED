package nz.cri.gns.fred.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import nz.cri.gns.auth.AuthServiceClient;
import nz.cri.gns.auth.AuthServiceException;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.FrUser;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.UserView;

public class UserUtil extends ModelUtil {

	private final FredDAO fredDAO;
	
	public static Integer FRED_WRITE = 2;
	public static Integer FRED_READ = 4;
        
//        private static final Logger log = Logger.getLogger(UserUtil.class.getName());
	
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
		return fredDAO.getList("FROM FrUserView AS f WHERE f.hasDataEntryRight = 1 AND f.deleted = 0", FrUserView.class);
	}
	
	public List<FrUserView> getFrWriters() throws StorageAccessException {
		return fredDAO.getList("FROM FrUserView AS f WHERE f.hasDataEntryRight = 1", FrUserView.class);
        }
        
	public List<FrUserView> getAllUsers() throws StorageAccessException {
		List<FrUserView> users = fredDAO.getList("FROM FrUserView AS f WHERE f.deleted = 0", FrUserView.class);
                // Gave up trying to get 'order by' to work in hibernate
                Comparator fullNameCompare = (Comparator) (Object o1, Object o2) -> {
                    FrUserView u1 = (FrUserView)o1;
                    FrUserView u2 = (FrUserView)o2;
                    if (hasEmptyName(u1)) {
                        if (hasEmptyName(u2)) {
                            return 0;
                        } else {
                            return 1;
                        }
                    } else if (hasEmptyName(u2)) {
                        return -1;
                    } 
                    return u1.getFullName().compareToIgnoreCase(u2.getFullName());
                };
                Collections.sort(users, fullNameCompare);    
                return users;
        } 
        
        boolean hasEmptyName(FrUserView user) {
            return user.getFullName() == null || user.getFullName().trim().isEmpty();
        }        
	
	public List<FrUserView> getActiveFrWritersWithout(Set<FrUserView> excludeFrUsers) throws StorageAccessException {
		List<FrUserView> frUsers = getActiveFrWriters();
		frUsers.removeAll(excludeFrUsers);
		Collections.sort(frUsers);
		return frUsers;
	}
        

        
        public static void updateUserRight(String action, String right, int userId) throws AuthServiceException {
            AuthServiceClient client = new AuthServiceClient();
            String role;
            switch (right) {
                case "read": role = "FRED_USER"; break;
                case "write": role = "FRED_EDITOR"; break;
                case "admin": role = "FRED_ADMIN"; break;
                default:
                    throw new IllegalArgumentException("Invalid right: '" + right + "', must be read, write or admin");
            }
            switch (action) {
                case "grant": client.grantRole(userId, role); break;
                case "revoke": client.revokeRole(userId, role); break;
                default:
                    throw new IllegalArgumentException("Invalid action: '" + action + "', must be grant or revoke");
            }
        }          
		
	public FrUser saveOrUpdate(FrUser frUser) throws StorageAccessException {
		return fredDAO.saveOrUpdate(frUser);
	}
	
	public FrUser save(FrUser frUser) throws StorageAccessException {
		return fredDAO.save(frUser);
	}
	
}