package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.util.FREDUtil;

/** @author Hibernate CodeGenerator */
public class FolderUserPK implements Serializable {

    private static final long serialVersionUID = 20050818L;

	/** identifier field */
    private Integer folderId;

    /** identifier field */
    private Integer userId;

    /** full constructor */
    public FolderUserPK(Integer folderId, Integer userId) {
        this.folderId = folderId;
        this.userId = userId;
    	System.out.println("Created " + this);
    }

    /** default constructor */
    public FolderUserPK() {
    	System.out.println("Created " + this);
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
    	
    	System.err.println(this + "'s user being changed to " + userId);
    	if (false)
			try {
				throw new Exception();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        this.userId = userId;
    }

     public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof FolderUserPK) ) return false;
        FolderUserPK castOther = (FolderUserPK) other;
        return FREDUtil.equals(castOther.userId, userId, false) && FREDUtil.equals(castOther.folderId, folderId, false);
    }

 	public int hashCode() {
		return (folderId + "_" + userId).hashCode();
	}

 	public String toString() {
 		return super.toString() + "{" + folderId + "," + userId + "}";
 	}

}
