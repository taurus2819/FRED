package nz.cri.gns.fred;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.Authenticable;
import nz.cri.gns.auth.IPRight;
import nz.cri.gns.auth.IPRightAccess;
import nz.cri.gns.auth.Right;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.jsp.ExtranetTemplate;
import nz.cri.gns.jsp.IPSysJspPage;

public abstract class FREDIPSysJspPage extends IPSysJspPage {

	public String getName(HttpServletRequest request) {
		return "Fossil Record Electronic Database";
	}

	public String getTitle(HttpServletRequest request) {
		return "Fossil Record Electronic Database";
	}

	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		try {
			return new Authenticable[] {
				 new IPRightAccess(
					new IPRight(
						"FRED website access",
						getIPApp(
							request.getSession(),
							getServletConfig().getServletContext())),
					Right.ANY_RIGHT)};
		} catch (Exception e) {
			//Database error, so just block them
			return new Authenticable[] {
				 new IPRightAccess(
					IPRight.BLOCKED_IP_RIGHT,
					Right.BLOCKED_RIGHT)};
		}
	}

	protected ExtranetTemplate getExtranetTemplate() {
		ExtranetTemplate et = new ExtranetTemplate();
		et.setDisplayLogin(true);
		et.setShowGnsLogo(true);
		et.setUseNavigationColumn(true);
		KeyValueObject links[] = new KeyValueObject[4];
		links[0] = new KeyValueObject("/fred/index.jsp", "FRED Home");
		links[1] = new KeyValueObject("/fred/simple_query.jsp", "Query");
		links[2] = new KeyValueObject("http://maps.gns.cri.nz/website/fred", "Map");
		links[3] = new KeyValueObject("/fred/folder_list.jsp", "Data Entry");
		et.setLinks(links);
		et.setImageBase("/fred/images/fred.gif");
		return et;
	}

}