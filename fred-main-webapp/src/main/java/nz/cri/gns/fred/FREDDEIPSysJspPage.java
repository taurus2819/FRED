package nz.cri.gns.fred;

import nz.cri.gns.auth.security.IpGrantedAuthority;

public abstract class FREDDEIPSysJspPage extends FREDIPSysJspPage{
	
    private static final long serialVersionUID = 20050818L;

	private static IpGrantedAuthority deRights;

	@Override
	public IpGrantedAuthority getRequiredRights() {
		if (deRights == null) {
			deRights = new IpGrantedAuthority(FredGrantedAuthorities.FR_DATA_ENTRY); 
		}
		return deRights;
	}
	
}

