package nz.cri.gns.fred;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import nz.cri.gns.auth.Authenticable;
import nz.cri.gns.auth.IPRight;
import nz.cri.gns.auth.IPRightAccess;
import nz.cri.gns.auth.Right;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.jsp.ExtranetTemplate;
import nz.cri.gns.jsp.IPSysJspPage;
import nz.cri.gns.jsp.IconnedLink;
import nz.cri.gns.jsp.PageState;

public abstract class FREDIPSysJspPage extends IPSysJspPage {

	private static Authenticable[] fredRights;

	public String getName(HttpServletRequest request) {
		return "FRED :: The Fossil Record Electronic Database";
	}

	public String getTitle(HttpServletRequest request) {
		return "FRED :: The Fossil Record Electronic Database";
	}

	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		if (fredRights == null) try {
			fredRights = new Authenticable[] {
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
		return fredRights;
	}

	protected ExtranetTemplate getExtranetTemplate() {
		return FREDIPSysJspPage.getFREDTemplate();
	}

	public static ExtranetTemplate getFREDTemplate() {
		ExtranetTemplate et = new ExtranetTemplate();
		et.setDisplayLogin(true);
		et.setShowGnsLogo(true);
		et.setUseNavigationColumn(true);
		et.addStyleSheet("fredde.css");
        et.addScript("showhide.js");
		KeyValueObject links[] = new KeyValueObject[4];
		links[0] = new KeyValueObject("/fred/index.jsp", "FRED Home");
		links[1] = new KeyValueObject("/fred/simple_query.jsp", "Query");
		links[2] = new KeyValueObject("http://maps.gns.cri.nz/website/fred", "Map");
		links[3] = new KeyValueObject("/fred/folder_list.jsp", "Data Entry");
		et.setLinks(links);
		et.setImageBase("/fred/images/fred.gif");
		et.setNewHeaderStyle(true);
		KeyValueObject logos[] = new KeyValueObject[1];
		logos[0] = new KeyValueObject("http://www.gsnz.org.nz", "/fred/images/gsnz_logo_head.gif");
		et.setHeaderLogos(logos);
		return et;
	}

	public ContentProvider getContentProvider(PageState state) {
		ContentProvider cp = (ContentProvider)state.session.getAttribute("fred.content.provider");
		if (cp == null) {
			cp = new ContentProvider(new File(state.context.getRealPath("/content")));
			state.session.setAttribute("fred.content.provider", cp);
		}
		return cp;
	}
	
	protected IconnedLink[] getButtons(HttpServletRequest request) {
		return new IconnedLink[0];
	}
	
	protected void startDETable(PageContext context) throws IOException, ServletException {
        context.include("/content/detablestart.html");
	}
	
	protected void endDETable(PageContext context) throws IOException, ServletException {
        context.include("/content/detableend.html");
	}
}