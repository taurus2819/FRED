package nz.cri.gns.fred;


import nz.cri.gns.auth.security.IpGrantedAuthority;

public abstract class FREDAdminIPSysJspPage extends FREDIPSysJspPage{

    private static final long serialVersionUID = 20050818L;
    
	private static IpGrantedAuthority adminRights;

	@Override
	public IpGrantedAuthority getRequiredRights() {
		if (adminRights == null) {
			adminRights = new IpGrantedAuthority(FredGrantedAuthorities.FR_ADMIN);
		} 
        return adminRights;
	}
	
}

