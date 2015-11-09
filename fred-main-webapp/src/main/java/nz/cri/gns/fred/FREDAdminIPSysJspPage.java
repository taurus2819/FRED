package nz.cri.gns.fred;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public abstract class FREDAdminIPSysJspPage extends FREDIPSysJspPage{

    private static final long serialVersionUID = 20050818L;
    
	private static GrantedAuthority adminRights;

	@Override
	public GrantedAuthority getRequiredRights() {
		if (adminRights == null) {
			adminRights = new SimpleGrantedAuthority(FredGrantedAuthorities.FR_ADMIN);
		} 
        return adminRights;
	}
	
}

