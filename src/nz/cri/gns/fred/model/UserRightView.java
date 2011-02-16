package nz.cri.gns.fred.model;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface UserRightView extends Comparable<UserRightView>, NameableAndIdentifiable {
    public void setUserId(Integer userId);
	public Integer getUserId();
	public void setGivenName(String givenName);
	public String getGivenName();
	public void setFamilyName(String familyName);
	public String getFamilyName();
	public void setFullName(String fullName);
	public String getFullName();
	public void setIpRightId(Integer ipRightId);
	public Integer getIpRightId();
	public void setIpRightDescription(String ipRightDescription);
	public String getIpRightDescription();
	public void setUserView(UserView userView);
	public UserView getUserView();
}
