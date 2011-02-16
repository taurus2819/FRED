package nz.cri.gns.fred;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.Authenticable;
import nz.cri.gns.auth.IPRight;
import nz.cri.gns.auth.IPRightAccess;
import nz.cri.gns.auth.Right;

public abstract class FREDDEIPSysJspPage extends FREDIPSysJspPage{
	
    private static final long serialVersionUID = 20050818L;

	private static Authenticable[] deRights;

	@Override
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		if (deRights == null) try {
			deRights = new Authenticable[] { 
				new IPRightAccess(
					new IPRight(
						"FRED data entry",
						getIPApp(request.getSession(), getServletConfig().getServletContext())),
						Right.ANY_RIGHT)
			};
		} catch (Exception e) {
			e.printStackTrace();
			//Database error, so just block them
			return new Authenticable[] {
				 new IPRightAccess(
					IPRight.BLOCKED_IP_RIGHT,
					Right.BLOCKED_RIGHT)};
		}
		return deRights;
	}
	
}

