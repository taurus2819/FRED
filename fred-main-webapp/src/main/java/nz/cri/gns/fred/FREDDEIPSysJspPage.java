package nz.cri.gns.fred;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public abstract class FREDDEIPSysJspPage extends FREDIPSysJspPage{
	
    private static final long serialVersionUID = 20050818L;

	private static GrantedAuthority deRights;

	@Override
	public GrantedAuthority getRequiredRights() {
		if (deRights == null) {
			deRights = new SimpleGrantedAuthority(FredGrantedAuthorities.FR_DATA_ENTRY); 
		}
		return deRights;
	}
	
}

