package nz.cri.gns.fred.util;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.cri.gns.auth.AuthServiceClient;
import nz.cri.gns.auth.AuthServiceException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.auth.security.IpGrantedAuthority;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.FredGrantedAuthorities;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.FrUser;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.UserView;
import org.springframework.security.core.GrantedAuthority;

public class UserUtil extends ModelUtil {

	private FredDAO fredDAO;
	
	public static Integer FRED_WRITE = 2;
	public static Integer FRED_READ = 4;
        
        private static final Logger log = Logger.getLogger(UserUtil.class.getName());
	
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
        
        public List<User> getUsersWithReadAccess() throws AuthServiceException {
            return getUsers(new AuthServiceClient(), FredGrantedAuthorities.FR_WEBSITE_ACCESS);
        }
        
        public List<User> getUsersWithWriteAccess() throws AuthServiceException {
            return getUsers(new AuthServiceClient(), FredGrantedAuthorities.FR_DATA_ENTRY);
        }   

        public List<User> getUsersWithAdminAccess() throws AuthServiceException {
            return getUsers(new AuthServiceClient(), FredGrantedAuthorities.FR_ADMIN);
        }  
        
        public boolean userHasRight(User user, String right) {
            return user.getAuthorities() != null && user.getAuthorities().contains(new IpGrantedAuthority(right));
        }
        
        public Collection<User> getAllUsers() throws AuthServiceException {
            AuthServiceClient client = new AuthServiceClient();
            Map<Long, User> userMap = new HashMap();
            List<User> allUsers = new LinkedList();
            addUsersWithRight(client, FredGrantedAuthorities.FR_WEBSITE_ACCESS, userMap);
            addUsersWithRight(client, FredGrantedAuthorities.FR_DATA_ENTRY, userMap);
            addUsersWithRight(client, FredGrantedAuthorities.FR_ADMIN, userMap);
            allUsers.addAll(userMap.values());
            long start = System.currentTimeMillis();
            Comparator fullNameCompare = (Comparator) (Object o1, Object o2) -> {
                User u1 = (User)o1;
                User u2 = (User)o2;
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
            Collections.sort(allUsers, fullNameCompare);
            log.log(Level.INFO, "Sorting {0} users took {1}ms", new Object[]{allUsers.size(), System.currentTimeMillis() - start});
            return allUsers;
        }
        
        void addUsersWithRight(AuthServiceClient client, String right, Map<Long, User> userMap) throws AuthServiceException {
            long start = System.currentTimeMillis();
            List<User> retrievedUsers = getUsers(client, right);
            for (User retrievedUser : retrievedUsers) {
                User user = userMap.get(retrievedUser.getId());
                if (user == null) {
                    addRight(retrievedUser, right);
                    userMap.put(retrievedUser.getId(), retrievedUser);
                } else {
                    addRight(user, right);
                }
            }
            log.log(Level.INFO, "Retrieving {0} users took {1}ms", new Object[]{right, System.currentTimeMillis() - start});
        }
        
        boolean hasEmptyName(User user) {
            return user.getFullName() == null || user.getFullName().trim().isEmpty();
        }
            
        void addRight(User user, String right) {
            Collection<? extends GrantedAuthority> rights = user.getAuthorities();
            List<GrantedAuthority> newRights = new ArrayList(3);
            newRights.add(new IpGrantedAuthority(right));
            if (rights != null) {
                newRights.addAll(rights);
            } 
            user.setAuthorities(newRights);
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
        
        private List<User> getUsers(AuthServiceClient client, String right) throws AuthServiceException {   
            return client.queryUsersByRight(right);
        }           
		
	public FrUser saveOrUpdate(FrUser frUser) throws StorageAccessException {
		return fredDAO.saveOrUpdate(frUser);
	}
	
	public FrUser save(FrUser frUser) throws StorageAccessException {
		return fredDAO.save(frUser);
	}
	
}