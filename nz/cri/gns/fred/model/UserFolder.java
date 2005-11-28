package nz.cri.gns.fred.model;


public class UserFolder implements Comparable<UserFolder> {

	public static final int FOLDER_READ_RIGHT = 1;
	public static final int FOLDER_EDIT_RIGHT = 2;
	public static final int FOLDER_CREATE_RIGHT = 4;
	public static final int FOLDER_DELETE_RIGHT = 8;
	public static final int FOLDER_SUBMIT_RIGHT = 16;
	public static final int FOLDER_ADMIN_RIGHT = 32;
	public static final int FOLDER_APPROVE_RIGHT = 64;

	public static final String FOLDER_TYPE_ADMIN = "Admin";
	public static final String FOLDER_TYPE_PERSONAL = "Personal";
	public static final String FOLDER_TYPE_BACKLOG = "Backlog";
	
	private Folder folder;
    private int rights;

    /**
     * @param folder
     * @param i
     */
    public UserFolder(Folder folder, int rights) {
        this.folder = folder;
        this.rights = rights;
    }

    public static UserFolder getOwnedUserFolder(Folder folder) {
        return new UserFolder(folder, 63);
    }
    
    public static UserFolder getAccessibleUserFolder(Folder folder, int userRights) {
        return new UserFolder(folder, userRights);
    }

	public int getRights() {
		return rights;
	}
	
	public Folder getFolder() {
		return folder;
	}

	public int compareTo(UserFolder arg0) {
		return folder.compareTo(((UserFolder)arg0).folder);
	} 
	
	public boolean isAllowedReadLocalities() {
		return ((rights & FOLDER_READ_RIGHT) != 0);
	}
	
	public boolean isAllowedEditLocalities() {
		return ((rights & FOLDER_EDIT_RIGHT) != 0);
	}

	public boolean isAllowedCreateLocalities() {
		return ((rights & FOLDER_CREATE_RIGHT) != 0);
	}

	public boolean isAllowedDeleteLocalities() {
		return ((rights & FOLDER_DELETE_RIGHT) != 0);
	}

	public boolean isAllowedSubmitLocalities() {
		return ((rights & FOLDER_SUBMIT_RIGHT) != 0);
	}

	public boolean isAllowedAdmin() {
		return ((rights & FOLDER_ADMIN_RIGHT) != 0);
	}

	public boolean isAllowedApproveLocalities() {
		return ((rights & FOLDER_APPROVE_RIGHT) != 0);
	}
	
	public Integer getFolderId() {
		return folder.getFolderId();
	}
	
	public String getFolderName() {
		return folder.getName();
	}
}
