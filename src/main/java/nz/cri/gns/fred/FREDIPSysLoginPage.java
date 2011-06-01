package nz.cri.gns.fred;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.jsp.IPSysLoginPage;

public abstract class FREDIPSysLoginPage extends IPSysLoginPage {

	private static final long serialVersionUID = 20061203L;
	
	@Override
	protected UserAccount createUser(String loginName, String loginPass, HttpServletRequest request, HttpSession session) throws SQLException, InvalidCredentialsException, IOException {
		return new FREDUser(loginName, loginPass, getIPApp(session, getServletConfig().getServletContext()), session);
	}

}
