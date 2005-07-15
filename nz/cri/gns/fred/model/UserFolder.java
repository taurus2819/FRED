package nz.cri.gns.fred.model;


public class UserFolder {

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
    
    
}
