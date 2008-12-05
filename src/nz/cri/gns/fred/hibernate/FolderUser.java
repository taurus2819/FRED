package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FrUserView;

public class FolderUser implements Serializable, nz.cri.gns.fred.model.FolderUser {

	private static final long serialVersionUID = 20050818L;
	
    private Integer fuId;
    private Integer userRights;
    private Folder folder;
    private FrUserView user;
    
	public void setFuId(Integer fuId) {
		this.fuId = fuId;
	}
	
	public Integer getFuId() {
		return fuId;
	}

	public void setUserRights(Integer userRights) {
		this.userRights = userRights;
	}

	public Integer getUserRights() {
		return userRights;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setUser(FrUserView user) {
		this.user = user;
	}

	public FrUserView getUser() {
		return user;
	}

	public int compareTo(nz.cri.gns.fred.model.FolderUser arg0) {
		if (folder.equals(arg0.getFolder()))
			return user.compareTo(arg0.getUser());
		return folder.compareTo(arg0.getFolder());
	}

}