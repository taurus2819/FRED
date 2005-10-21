package nz.cri.gns.fred;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import nz.cri.gns.auth.Authenticable;
import nz.cri.gns.auth.IPRight;
import nz.cri.gns.auth.IPRightAccess;
import nz.cri.gns.auth.Right;
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
	
	protected ExtranetTemplate getExtranetTemplate() {
		ExtranetTemplate et = super.getExtranetTemplate();
		et.setUseNavigationColumn(false);
		et.setImageBase("/fred/images/fredde.gif");
		et.addStyleSheet("fredde.css");
        et.addScript("showhide.js");
		return et;
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
	
	protected void startDETable(PageContext context) throws IOException, ServletException {
        context.include("/content/detablestart.html");
	}
	
	protected void endDETable(PageContext context) throws IOException, ServletException {
        context.include("/content/detableend.html");
	}
}
