
package nz.cri.gns.fred.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.test.TestingPageState;
import nz.cri.gns.test.TestingServletContext;
import junit.framework.TestCase;

public class FREDUtilsTest extends TestCase {

	public void testSecClass() throws SQLException, InvalidCredentialsException, IOException, MalformedURLException, RemoteException, NotBoundException {
		TestingPageState state = new TestingPageState();
		((TestingServletContext)state.getContext()).setInitParameter("dbConnect", "raptor4:1521:dev");
		User user = new User("pseudo_ben", "santor32", FREDUtils.getIPConnection(state));
		System.out.println("Public (21) = " + FREDUtils.getSecurityClass(21, user, state));
		System.out.println("User (22) = " + FREDUtils.getSecurityClass(22, user, state));
		System.out.println("User + P (23) = " + FREDUtils.getSecurityClass(23, user, state));
		System.out.println("Org (24) = " + FREDUtils.getSecurityClass(24, user, state));
		System.out.println("Org + P (25) = " + FREDUtils.getSecurityClass(25, user, state));
	}

	public void testSecType() throws NotBoundException, SQLException, InvalidCredentialsException, IOException {
		TestingPageState state = new TestingPageState();
		User user = new User("pseudo_ben", "santor32", FREDUtils.getIPConnection(state));
		System.out.println("SecClass 4 = " + FREDUtils.getSecurityType(4, user, state));
		System.out.println("SecClass 12 = " + FREDUtils.getSecurityType(12, user, state));
		System.out.println("SecClass 13 = " + FREDUtils.getSecurityType(13, user, state));
		System.out.println("SecClass 3 = " + FREDUtils.getSecurityType(3, user, state));
		System.out.println("SecClass 11 = " + FREDUtils.getSecurityType(11, user, state));
	}

}
