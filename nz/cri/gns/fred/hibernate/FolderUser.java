package nz.cri.gns.fred.hibernate;

import java.io.Serializable;


/** @author Hibernate CodeGenerator */
public class FolderUser implements Serializable {

    /** identifier field */
    private nz.cri.gns.fred.hibernate.FolderUserPK comp_id;

    /** nullable persistent field */
    private Integer userRights;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.Folder folder;

    /** full constructor */
    public FolderUser(nz.cri.gns.fred.hibernate.FolderUserPK comp_id, Integer userRights, nz.cri.gns.fred.hibernate.Folder folder) {
        this.comp_id = comp_id;
        this.userRights = userRights;
        this.folder = folder;
    }

    /** default constructor */
    public FolderUser() {
    }

    /** minimal constructor */
    public FolderUser(nz.cri.gns.fred.hibernate.FolderUserPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.hibernate.FolderUserPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.FolderUserPK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getUserRights() {
        return this.userRights;
    }

    public void setUserRights(Integer userRights) {
        this.userRights = userRights;
    }

    public nz.cri.gns.fred.hibernate.Folder getFolder() {
        return this.folder;
    }

    public void setFolder(nz.cri.gns.fred.hibernate.Folder folder) {
        this.folder = folder;
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof FolderUser) ) return false;
        FolderUser castOther = (FolderUser) other;
        return castOther.comp_id.equals(comp_id);
    }

	public int hashCode() {
		return comp_id.hashCode();
	}

}
