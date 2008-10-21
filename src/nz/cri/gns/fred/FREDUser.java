package nz.cri.gns.fred;

import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.DatabaseApp2;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.FrUser;
import nz.cri.gns.fred.util.UserUtil;

public class FREDUser extends User {

	private static final long serialVersionUID = 20061203L;
	
	/**
	 * override User constructor to log login to FRED table
	 * 
	 */
	public FREDUser (String username, String password, DatabaseApp2 app) throws SQLException, InvalidCredentialsException {
		super(username, password, app);
		
		//log login
		try {
			UserUtil userUtil = new UserUtil(HibernateUtil.get().getDAOFactory());
			FrUser frUser = userUtil.getFrUser(new Integer(getId()));
			if (frUser == null) {
				frUser = userUtil.createNewFrUser();
				frUser.setUserId(new Integer(getId()));
				userUtil.save(frUser);
			}
			frUser.setLastLogin(new Date());
			userUtil.saveOrUpdate(frUser);
		} catch (Exception e) {
			System.out.println("**** User logging exception : " + new Date() + " ****");
			e.printStackTrace();
		}
	}
	
	public FREDUser(String username, String password, DatabaseApp2 app, HttpSession session) throws SQLException, InvalidCredentialsException {
		this(username, password, app);
		//Register the user
		session.setAttribute(USER_ATTRIBUTE, this);
	}

}
