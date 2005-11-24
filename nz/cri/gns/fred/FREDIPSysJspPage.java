package nz.cri.gns.fred;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import nz.cri.gns.auth.Authenticable;
import nz.cri.gns.auth.IPRight;
import nz.cri.gns.auth.IPRightAccess;
import nz.cri.gns.auth.Right;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.jsp.ExtranetTemplate;
import nz.cri.gns.jsp.IPSysJspPage;
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

	public ContentProvider getContentProvider(PageState state) {
		ContentProvider cp = (ContentProvider)state.session.getAttribute("fred.content.provider");
		if (cp == null) {
			cp = new ContentProvider(new File(state.context.getRealPath("/content")));
			state.session.setAttribute("fred.content.provider", cp);
		}
		return cp;
	}
	
	protected void drawHeadingTableCell(JspWriter out, ExtranetTemplate et,
			HttpServletRequest request) throws IOException {
		int colspan = ((2*et.getLinkCount())+2);
		out.println("	<td colspan=\""+colspan+"\" style=\"background: url(images/fredDEheaderBG.gif) repeat-x; vertical-align: middle\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td class=\"bigheading\" style=\"vertical-align: middle; height: 46px; color: white\" height=\"46\">"+getName(request)+"</td></tr>");
		out.print("   <td colspan=\"" + colspan + "\" style=\"height: 20px; vertical-align: top\" height=\"20\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td><img src=\"images/fredDEheaderDiv.gif\" border=\"0\"></td>");
		IconnedLink[] links = getButtons(request);
		for (int i=0; i<links.length; i++) {
			out.print("<td style=\"vertical-align: middle\">&nbsp;&nbsp;&nbsp;");
			if (links[i].icon != null) {
				if (links[i].key != null)
					out.print("<a href=\"" + links[i].key + "\">");
				out.print("<img border=\"0\" src=\"" + links[i].icon + "\">");
				if (links[i].key != null)
					out.print("</a>");
				out.print("&nbsp;</td><td style=\"vertical-align: middle\">");
			}
			if (links[i].key != null)
				out.print("<a class=\"buttn\" href=\"" + links[i].key + "\">");
			else
				out.print("<span class=\"buttnsub\">");
			out.print(links[i].value);
			if (links[i].key != null)
				out.print("</a>");
			else 
				out.print("</span>");
			out.print("&nbsp;&nbsp;&nbsp;</td><td><img src=\"images/fredDEheaderDiv.gif\" border=\"0\"></td>");
		}
		out.println("</tr></table></td></tr></table></td>");
	}

	protected IconnedLink[] getButtons(HttpServletRequest request) {
		return new IconnedLink[0];
	}	
}