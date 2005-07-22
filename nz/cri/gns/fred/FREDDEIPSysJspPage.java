package nz.cri.gns.fred;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import nz.cri.gns.auth.Authenticable;
import nz.cri.gns.auth.IPRight;
import nz.cri.gns.auth.IPRightAccess;
import nz.cri.gns.auth.Right;
import nz.cri.gns.jsp.ExtranetTemplate;

public abstract class FREDDEIPSysJspPage extends FREDIPSysJspPage{

	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		try {
			return new Authenticable[] {
				 new IPRightAccess(
					new IPRight(
						"FRED data entry",
						getIPApp(
							request.getSession(),
							getServletConfig().getServletContext())),
					Right.ANY_RIGHT)};
		} catch (Exception e) {
			e.printStackTrace();
			//Database error, so just block them
			return new Authenticable[] {
				 new IPRightAccess(
					IPRight.BLOCKED_IP_RIGHT,
					Right.BLOCKED_RIGHT)};
		}
	}
	
	protected ExtranetTemplate getExtranetTemplate() {
		ExtranetTemplate et = super.getExtranetTemplate();
		et.setUseNavigationColumn(false);
		et.setImageBase("/fred/images/fredde.gif");
		et.addStyleSheet("fredde.css");
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
			out.print(links[i].value);
			if (links[i].key != null)
				out.print("</a>");
			out.print("&nbsp;&nbsp;&nbsp;</td><td><img src=\"images/fredDEheaderDiv.gif\" border=\"0\"></td>");
		}
		out.println("</tr></table></td></tr></table></td>");
	}

	protected IconnedLink[] getButtons(HttpServletRequest request) {
		return new IconnedLink[0];
	}
	
	protected void startDETable(JspWriter out) throws IOException {
		out.print("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
		out.print("<td width=\"11\" height=\"11\" style=\"width: 11px; height: 11px\"><img border=\"0\" src=\"images/frameLT.gif\"></td>");
		out.print("<td height=\"11\" style=\"height: 11px; background: #c9c9c9 url(images/frameT.gif) repeat-x\"></td>");
		out.print("<td width=\"11\" height=\"11\" style=\"width: 11px; height: 11px\"><img border=\"0\" src=\"images/frameRT.gif\"></td>");
		out.print("</tr><tr>");
		out.print("<td width=\"11\" style=\"width: 11px; background: white url(images/frameL.gif) repeat-y; vertical-align: top\" valign=\"top\"><img src=\"images/frameLTi.gif\" border=\"0\"></td>");
		out.println("<td style=\"background: white url(images/frameM.gif) repeat-x\">");
	}
	
	protected void endDETable(JspWriter out) throws IOException {
		out.print("<td width=\"11\" style=\"width: 11px; background: white url(images/frameR.gif) repeat-y; vertical-align: top\" valign=\"top\"><img src=\"images/frameRTi.gif\" border=\"0\"></td>");
		out.print("</tr><tr>");
		out.print("<td width=\"11\" height=\"11\" style=\"width: 11px; height: 11px\"><img border=\"0\" src=\"images/frameLB.gif\"></td>");
		out.print("<td height=\"11\" style=\"height: 11px; background: #c9c9c9 url(images/frameB.gif) repeat-x\"></td>");
		out.print("<td width=\"11\" height=\"11\" style=\"width: 11px; height: 11px\"><img border=\"0\" src=\"images/frameRB.gif\"></td>");
		out.println("</tr></table>");
	}
}
