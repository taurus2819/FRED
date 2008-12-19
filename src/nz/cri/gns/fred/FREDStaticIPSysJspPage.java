package nz.cri.gns.fred;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.Authenticable;
import nz.cri.gns.jsp.IconnedLink;
import nz.cri.gns.jsp.NewExtranetTemplate;

public abstract class FREDStaticIPSysJspPage extends FREDIPSysJspPage{
	
    private static final long serialVersionUID = 20050818L;

	@Override
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		return new Authenticable[0];
	}
	
	@Override
	protected NewExtranetTemplate getExtranetTemplate() {
		NewExtranetTemplate et = FREDIPSysJspPage.getFREDTemplate();
		
		//add nav
		IconnedLink[] il = new IconnedLink[9];
		il[0] = new IconnedLink("quick_start.jsp", "images/book.gif", "Quick Start");
		il[1] = new IconnedLink("about.jsp", "images/book.gif", "About");
		il[2] = new IconnedLink("contacts.jsp", "images/register.gif", "Contacts");
		il[3] = new IconnedLink("faq.jsp", "images/help.gif", "FAQ");
		il[4] = new IconnedLink("whats_new.jsp", "images/book.gif", "Whats New");
		il[5] = new IconnedLink("http://data.gns.cri.nz/register/user_reg.jsp?DBase=FRED", "images/register.gif", "Register for FRED account");
		il[6] = new IconnedLink("conditions.jsp", "images/tc.gif", "Conditions of Use");
		il[7] = new IconnedLink("download.jsp", "images/save.gif", "Downloads");
		il[8] = new IconnedLink("http://data.gns.cri.nz/staff/email.jsp?id=frf@subject=FRF%20Feeedback", "images/register.gif", "Feedback");
		addButtons(et, il);
		
		return et;
	}
	
}

