package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;

public class FrUser implements Serializable, nz.cri.gns.fred.model.FrUser {

    private static final long serialVersionUID = 20050818L;

    private Integer userId;
    private Date lastLogin;
    
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public int compareTo(nz.cri.gns.fred.model.FrUser arg0) {
		return (userId).compareTo(arg0.getUserId());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(userId);
	}

	public String getDisplayName() {
		return String.valueOf(userId);
	}

}