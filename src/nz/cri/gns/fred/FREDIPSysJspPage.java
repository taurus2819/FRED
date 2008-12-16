package nz.cri.gns.fred;

import java.io.File;
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
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.jsp.CustomHTMLLink;
import nz.cri.gns.jsp.ExtranetTemplate;
import nz.cri.gns.jsp.IPSysJspPage;
import nz.cri.gns.jsp.IconnedLink;
import nz.cri.gns.jsp.Link;
import nz.cri.gns.jsp.NewExtranetTemplate;
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

	protected NewExtranetTemplate getExtranetTemplate() {
		return FREDIPSysJspPage.getFREDTemplate();
	}

	public static NewExtranetTemplate getFREDTemplate() {
		NewExtranetTemplate et = new NewExtranetTemplate();
		et.setDisplayLogin(true);
		et.setShowGnsLogo(true);
		et.setUseNavigationColumn(true);
		et.addStyleSheet("fred.css");
        et.addScript("showhide.js");
        et.setGoogleAnalyticsCode("UA-3641432-1");
		KeyValueObject links[] = new KeyValueObject[4];
		links[0] = new KeyValueObject("index.jsp", "FRED Home");
		links[1] = new KeyValueObject("simple_query.jsp", "Query");
		links[2] = new KeyValueObject("http://maps.gns.cri.nz/website/fred", "Map");
		links[3] = new KeyValueObject("folder_list.jsp", "Data Entry");
		et.setLinks(links);
		et.setImageBase("images/fred.gif");
		et.setNavListHeading("FRED");
		et.setNewTemplateImage("images/fred_banner.jpg");
		et.setAppHome("http://www.fred.org.nz");
		et.setNewHeaderStyle(true);
		KeyValueObject logos[] = new KeyValueObject[1];
		logos[0] = new KeyValueObject("http://www.gsnz.org.nz", "images/gsnz_header.gif");
		et.setHeaderLogos(logos);
		
		//set FRNumber lik
		String htmlLink = "<img src=\"images\\blank.gif\" height=\"20\" width=\"10\" alt=\"\" /><form method=\"post\" action=\"locality\" name=\"FRNumJumpForm\" style=\"display: inline; margin: 0;\">"
			+ "<input type=\"text\" size=\"10\" name=\"frNum\" style=\"border: 0; font-size: 10pt; color: #666666;\" value=\"Enter FrNum\" />&nbsp;"
			+ "<input type=\"submit\" value=\"Go\" /></form>";
		et.setButtons(new Link[] {
				new CustomHTMLLink(htmlLink)
				, new IconnedLink("index.jsp", "images/home.gif", "FRED Home")
				, new IconnedLink("folder_list.jsp", "images/edit.gif", "Data Entry")
				, new IconnedLink("simple_query.jsp", "images/search.gif", "Simple Query")
				, new IconnedLink("buildframe.jsp", "images/search.gif", "Advanced Query")
				, new IconnedLink("javascript:var x=window.open('http://basilisk.gns.cri.nz/website/fred_nz/o_index.htm', 'fred', 'toolbar=no,menu=no,resizable=yes,address=no');x.focus();", "images/map.gif", "Interactive Map")
				, new IconnedLink("admin.jsp", "images/edit.gif", "FRED Admin")
		});
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
	
	public void addButtons(ExtranetTemplate et, Link[] links) {
		Link[] buttons = et.getButtons();
		Link[] l = new Link[buttons.length + links.length];
		for (int i = 0; i < buttons.length - 1; i++)
			l[i] = buttons[i];
		for (int i = 0; i < links.length; i++)
			l[i + buttons.length - 1] = links[i];
		l[buttons.length + links.length - 1] = buttons[buttons.length -1];
		et.setButtons(l);
	}
	
	//protected Link[] getButtons(HttpServletRequest request) {
	//	return new Link[0];
	//}
	
	protected void startDETable(PageContext context) throws IOException, ServletException {
        context.include("/content/detablestart.html");
	}
	
	protected void endDETable(PageContext context) throws IOException, ServletException {
        context.include("/content/detableend.html");
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			super.service(request, response);
		} finally {
			try {
				FredHibernate.get().getDAOFactory().closeSession();
			} catch (StorageAccessException e) {
			}
		}
	}
}