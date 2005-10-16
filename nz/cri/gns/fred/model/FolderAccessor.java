package nz.cri.gns.fred.model;

public class FolderAccessor implements Comparable<FolderAccessor> {

	private FolderUser folderUser;
	private String user;

	public FolderAccessor(FolderUser folderUser, String userName) {
		this.folderUser = folderUser;
		this.user = userName;
	}
	
	public String getUserName() {
		return user;
	}

	public Integer getUserId() {
		return folderUser.getUserId();
	}
	
	public Integer getUserRights() {
		return folderUser.getUserRights();
	}

	public int compareTo(FolderAccessor arg0) {
		return user.compareTo(arg0.user);
	}
}
