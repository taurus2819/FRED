package nz.cri.gns.fred.model;

public interface FolderUser {

	public Integer getUserRights();
	
	public Integer getUserId();

	public void setUserRights(Integer rights);
	
	/**
	 * Sets the user id, 
	 *@throws IllegalStateException if the folderuser is saved
	 */
	public void setUserId(Integer userId);

	/**
	 * Sets the folder id, 
	 *@throws IllegalStateException if the folderuser is saved
	 */
	public void setFolder(Folder folder);
	
	public Folder getFolder();

}
