package nz.cri.gns.fred.model;

public interface FolderUser {

	public Integer getUserRights();
	
	public Integer getUserId();

	public void setUserRights(Integer rights);
	
	public void setUserId(Integer userId);

}
