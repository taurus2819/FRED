package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.model.UserView;

public class UserRightView implements Serializable, nz.cri.gns.fred.model.UserRightView {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer userId;

    /** nullable persistent field */
    private String givenName;

    /** nullable persistent field */
    private String familyName;

    /** nullable persistent field */
    private String fullName;

    /** nullable persistent field */
    private String ipRightName;
    
    /** persistent field */
    private UserView userView;
    
    public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}
	
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setIpRightName(String ipRightName) {
		this.ipRightName = ipRightName;
	}

	public String getIpRightName() {
		return ipRightName;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public UserView getUserView() {
		return userView;
	}

	public int compareTo(nz.cri.gns.fred.model.UserRightView arg0) {
		return (familyName + " " + givenName).compareTo(arg0.getFamilyName() + " " + arg0.getGivenName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(userId);
	}

	public String getDisplayName() {
		return fullName;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof UserRightView && ((UserRightView)o).userId.equals(userId);
	}
	
	@Override
	public int hashCode() {
		return 453 * userId;
	}
}
