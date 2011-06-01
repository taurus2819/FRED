package nz.cri.gns.fred.model;

public interface FolderUser extends Comparable<FolderUser> {

	public void setFuId(Integer fuId);
	public Integer getFuId();
	public void setUserRights(Integer userRights);
	public Integer getUserRights();
	public void setFolder(Folder folder);
	public Folder getFolder();
	public void setUser(FrUserView user);
	public FrUserView getUser();

}