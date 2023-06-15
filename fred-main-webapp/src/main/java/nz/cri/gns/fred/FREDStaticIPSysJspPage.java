package nz.cri.gns.fred;

import nz.cri.gns.auth.security.IpGrantedAuthority;

public abstract class FREDStaticIPSysJspPage extends FREDIPSysJspPage{
	
    private static final long serialVersionUID = 20050818L;

	@Override
	public IpGrantedAuthority getRequiredRights() {
		return null;
	}
	
}

