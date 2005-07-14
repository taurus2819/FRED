package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class FolderUserPK implements Serializable {

    /** identifier field */
    private Integer folderId;

    /** identifier field */
    private Integer userId;

    /** full constructor */
    public FolderUserPK(Integer folderId, Integer userId) {
        this.folderId = folderId;
        this.userId = userId;
    }

    /** default constructor */
    public FolderUserPK() {
    }

    public Integer getFolderId() {
        return this.folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

     public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof FolderUserPK) ) return false;
        FolderUserPK castOther = (FolderUserPK) other;
        return folderId == castOther.folderId && userId == castOther.userId;
    }

 	public int hashCode() {
		return (folderId + "_" + userId).hashCode();
	}


}
