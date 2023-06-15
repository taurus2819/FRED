package nz.cri.gns.fred;


import nz.cri.gns.auth.security.IpGrantedAuthority;

public abstract class FREDAdminIPSysJspPage extends FREDIPSysJspPage{

    private static final long serialVersionUID = 20050818L;
    
    public static final IpGrantedAuthority ADMIN_RIGHTS =
            new IpGrantedAuthority(FredGrantedAuthorities.FR_ADMIN);

    @Override
    public IpGrantedAuthority getRequiredRights() {
        return ADMIN_RIGHTS;
    }
	
}

