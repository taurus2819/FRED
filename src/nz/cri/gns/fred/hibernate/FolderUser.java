package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.hibernate.dao.CompositeKey;
import nz.cri.gns.fred.hibernate.dao.CompositeKeyed;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FrUserView;


/** @author Hibernate CodeGenerator */
public class FolderUser implements Serializable, nz.cri.gns.fred.model.FolderUser, CompositeKeyed {

	private static final long serialVersionUID = 20050818L;
	
    /** identifier field */
    private nz.cri.gns.fred.hibernate.FolderUserPK comp_id;

    /** nullable persistent field */
    private Integer userRights;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Folder folder;
    
	private boolean saved;

    /** default constructor */
    public FolderUser() {
    	throw new IllegalArgumentException("Do not use this constructor");
    }

    public FolderUser(boolean saved) {
    	this.saved = saved;
    }
    
    public nz.cri.gns.fred.hibernate.FolderUserPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.FolderUserPK comp_id) {
        this.comp_id = comp_id;
    }

    /**
     * Thes method is hibernate-only (masked from 'everything else' by not being in the interface)
     */
    public Integer getUserRights_() {
    	return this.userRights;
    }

    public Folder getFolder_() {
    	return this.folder;
    }

    public void setUserRights_(Integer rights) {
    	this.userRights = rights;
    }
    
    public void setFolder_(Folder folder) {
    	this.folder = folder;
    }

    public Integer getUserRights() {
        return this.userRights;
    }
        
    public void setUserRights(Integer userRights) {
        this.userRights = userRights;
        saved = false;
    }

    public Folder getFolder() {
        return this.folder;
    }

    public void setFolder(nz.cri.gns.fred.model.Folder folder) {
    	if (saved)
    		throw new IllegalStateException("Folder cannot be changed once saved");
   		this.folder = folder;
		if (comp_id == null)
        	comp_id = new FolderUserPK();
        comp_id.setFolderId(folder.getFolderId());
        saved = false;
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof FolderUser) ) return false;
        FolderUser castOther = (FolderUser) other;
        return castOther.comp_id.equals(comp_id);
    }

	public int hashCode() {
		return (comp_id == null) ? 0 : comp_id.hashCode();
	}

	public Integer getUserId() {
		return comp_id.getUserId();
	}

	public void setUserId(Integer userId) {
	   	if (saved)
    		throw new IllegalStateException("User cannot be changed once saved");
    	if (comp_id == null)
			comp_id = new FolderUserPK();
		comp_id.setUserId(userId);
		saved = false;
	}

	public boolean isUnsaved() {
		return !saved;
	}

	public void updateKey() {
		//Ensure the folder id is set correctly
		comp_id.setFolderId(folder.getFolderId());
	}

	public void setKey(CompositeKey arg1) {
		comp_id = (FolderUserPK)arg1;
	}
}
