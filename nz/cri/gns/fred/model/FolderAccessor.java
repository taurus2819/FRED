package nz.cri.gns.fred.model;

public class FolderAccessor implements Comparable {

	private FolderUser folderUser;
	private String user;

	public FolderAccessor(FolderUser folderUser, String userName) {
		this.folderUser = folderUser;
		this.user = userName;
	}
	
	public String getUserName() {
		return user;
	}

	public Folder getFolder() {
		return folderUser.getFolder();
	}
	
	public Integer getUserId() {
		return folderUser.getUserId();
	}
	
	public Integer getUserRights() {
		return folderUser.getUserRights();
	}

	public int compareTo(Object arg0) {
		return user.compareTo(((FolderAccessor)arg0).user);
	}
}
