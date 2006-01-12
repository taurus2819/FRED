package nz.cri.gns.fred;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import nz.cri.gns.auth.Authenticable;
import nz.cri.gns.auth.IPRight;
import nz.cri.gns.auth.IPRightAccess;
import nz.cri.gns.auth.Right;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.jsp.ExtranetTemplate;

public abstract class FREDDEIPSysJspPage extends FREDIPSysJspPage{

	private static Authenticable[] deRights;

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
	
	public ExtranetTemplate getExtranetTemplate() {
		return FREDDEIPSysJspPage.getFREDTemplate();
	}
	
	public static ExtranetTemplate getFREDTemplate() {
		ExtranetTemplate et = FREDIPSysJspPage.getFREDTemplate();
		et.setUseNavigationColumn(false);
	    return et;
	}	
	
}

