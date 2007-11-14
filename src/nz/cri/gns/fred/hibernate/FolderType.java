package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class FolderType implements Serializable, nz.cri.gns.fred.model.FolderType {

    private static final long serialVersionUID = 20050818L;
    
    /** identifier field */
    private Integer folderTypeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private Set folders;

    /** full constructor */
    public FolderType(Integer folderTypeId, String name, Set folders) {
        this.folderTypeId = folderTypeId;
        this.name = name;
        this.folders = folders;
    }

    /** default constructor */
    public FolderType() {
    }

    public Integer getFolderTypeId() {
        return this.folderTypeId;
    }

    public void setFolderTypeId(Integer folderTypeId) {
        this.folderTypeId = folderTypeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set getFolders() {
        return this.folders;
    }

    public void setFolders(Set folders) {
        this.folders = folders;
    }

    public String toString() {
        return name;
    }

	public boolean equals(Object o) {
		return o instanceof FolderType && ((FolderType)o).folderTypeId.equals(folderTypeId);
	}
	
	public int hashCode() {
		return 262 * folderTypeId;
	}
}
