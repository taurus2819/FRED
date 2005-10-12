package nz.cri.gns.fred.hibernate;

import java.io.Serializable;


/** @author Hibernate CodeGenerator */
public class FolderUser implements Serializable, nz.cri.gns.fred.model.FolderUser {

	private static final long serialVersionUID = 20050818L;
	
    /** not nullable persistent field */
    private Integer userRights;

    /** not nullable persistent field */
    private Integer userId;

    /** default constructor */
    public FolderUser() {
    }

    public Integer getUserRights() {
        return this.userRights;
    }

    public void setUserRights(Integer userRights) {
        this.userRights = userRights;
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof FolderUser) ) return false;
        FolderUser castOther = (FolderUser) other;
        return userId == castOther.userId && userRights == castOther.userRights;
    }

	public int hashCode() {
		return ("" + userId + userRights).hashCode();
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
		
	}
}
