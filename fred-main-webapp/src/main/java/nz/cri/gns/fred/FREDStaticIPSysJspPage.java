package nz.cri.gns.fred;

import nz.cri.gns.auth.security.IpGrantedAuthority;
import nz.cri.gns.jsp.IconnedLink;
import nz.cri.gns.jsp.NewExtranetTemplate;

public abstract class FREDStaticIPSysJspPage extends FREDIPSysJspPage{
	
    private static final long serialVersionUID = 20050818L;

	@Override
	public IpGrantedAuthority getRequiredRights() {
		return null;
	}
	
	@Override
	protected NewExtranetTemplate getExtranetTemplate() {
		NewExtranetTemplate et = FREDIPSysJspPage.getFREDTemplate();
		
		//add nav
		IconnedLink[] il = new IconnedLink[8];
		il[0] = new IconnedLink("user_manual.pdf", "images/book.gif", "FRED User Manual");
		il[1] = new IconnedLink("quick_start.jsp", "images/book.gif", "Quick Start");
		il[2] = new IconnedLink("about.jsp", "images/book.gif", "About");
		il[3] = new IconnedLink("contacts.jsp", "images/register.gif", "Contacts");
		il[4] = new IconnedLink("faq.jsp", "images/help.gif", "FAQ");
		il[5] = new IconnedLink("whats_new.jsp", "images/book.gif", "Whats New");
		il[6] = new IconnedLink("http://data.gns.cri.nz/register/user_reg.jsp?DBase=FRED", "images/register.gif", "Register for FRED account");
		il[7] = new IconnedLink("conditions.jsp", "images/tc.gif", "Conditions of Use");
		//il[7] = new IconnedLink("download.jsp", "images/save.gif", "Downloads");
		il[8] = new IconnedLink("http://data.gns.cri.nz/staff/email.jsp?id=frf&subject=FRF%20Feeedback", "images/register.gif", "Feedback");
		addButtons(et, il);
		
		return et;
	}
	
}

