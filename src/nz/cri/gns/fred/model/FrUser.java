package nz.cri.gns.fred.model;

import java.util.Date;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface FrUser extends Comparable<FrUser>, NameableAndIdentifiable {
	public void setUserId(Integer userId);
	public Integer getUserId();
	public void setLastLogin(Date lastLogin);
	public Date getLastLogin();
}
