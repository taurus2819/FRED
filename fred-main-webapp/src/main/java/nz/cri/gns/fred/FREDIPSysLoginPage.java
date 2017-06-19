package nz.cri.gns.fred;

import java.util.Date;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.FrUser;
import nz.cri.gns.fred.util.UserUtil;
import nz.cri.gns.jsp.IPSysLoginPage;
import org.springframework.security.core.AuthenticationException;

public abstract class FREDIPSysLoginPage extends IPSysLoginPage {

	private static final long serialVersionUID = 20061203L;
	
	@Override
	protected User createUser(String loginName, String loginPass) throws AuthenticationException {
        //delegate
        User user = super.createUser(loginName, loginPass);
        
        //now perform work previously done by FREDUser ctor
        //log login
		try {
			UserUtil userUtil = new UserUtil(FredHibernate.get().getDAOFactory());
			FrUser frUser = userUtil.getFrUser(new Integer(user.getId().intValue()));
			if (frUser == null) {
				frUser = userUtil.createNewFrUser();
				frUser.setUserId(new Integer(user.getId().intValue()));
				userUtil.save(frUser);
			}
			frUser.setLastLogin(new Date());
			userUtil.saveOrUpdate(frUser);
		} catch (Exception e) {
			System.out.println("**** User logging exception : " + new Date() + " ****");
			e.printStackTrace();
		}
        return user;
	}

}
