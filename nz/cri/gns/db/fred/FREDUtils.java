/*
 * Created on 12/01/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.cri.gns.db.fred;

import java.io.IOException;
import java.sql.SQLException;

import nz.cri.gns.auth.Right;
import nz.cri.gns.auth.SecurityClass;
import nz.cri.gns.auth.SecurityClassAccess;
import nz.cri.gns.auth.User;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.ExternalUtils;
import nz.cri.gns.jsp.FREDConstants;
import nz.cri.gns.jsp.PageState;

/**
 * @author ben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FREDUtils implements FREDConstants  {

  public static boolean isAllowedToView(User user, int securityClassID, PageState state) throws IOException, SQLException {
	DBConnection conn = ExternalUtils.createDatabaseConnection(state.getSession(), "nz.cri.gns.ip.connection", "ip", state.getContext());
  	SecurityClass sc = new SecurityClass(securityClassID, conn);
  	SecurityClassAccess sca = new SecurityClassAccess(sc, Right.ANY_RIGHT);
  	return sca.isAccessibleTo(user, conn);
  }

}
