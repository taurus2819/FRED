package nz.cri.gns.fred.model;


public class UserFolder implements Comparable {

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

	public int compareTo(Object arg0) {
		return folder.compareTo(((UserFolder)arg0).folder);
	}    
}
