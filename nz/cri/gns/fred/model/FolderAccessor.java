package nz.cri.gns.fred.model;

public class FolderAccessor implements Comparable<FolderAccessor> {

	private Folder folder;
	private FolderUser folderUser;
	private String user;

	public FolderAccessor(Folder folder, FolderUser folderUser, String userName) {
		this.folderUser = folderUser;
		this.user = userName;
		this.folder = folder;
	}
	
	public String getUserName() {
		return user;
	}

	public Folder getFolder() {
		return folder;
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
