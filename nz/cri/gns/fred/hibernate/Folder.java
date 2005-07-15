package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class Folder implements Serializable, nz.cri.gns.fred.model.Folder {

    /** identifier field */
    private Integer folderId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.FolderType folderType;

    /** nullable persistent field */
    private Integer ownerId;

    /** persistent field */
    private Set records;

    /** persistent field */
    private Set auditTables;

    /** persistent field */
    private Set masterfileFeatures;

    /** persistent field */
    private Set features;

    /** persistent field */
    private Set folderUsers;

    /** full constructor */
    public Folder(String name, FolderType folderType, Integer ownerId, Set records, Set auditTables, Set masterfileFeatures, Set features, Set folderUsers) {
        this.name = name;
        this.folderType = folderType;
        this.ownerId = ownerId;
        this.records = records;
        this.auditTables = auditTables;
        this.masterfileFeatures = masterfileFeatures;
        this.features = features;
        this.folderUsers = folderUsers;
    }

    /** default constructor */
    public Folder() {
    }

    /** minimal constructor */
    public Folder(String name, Set records, Set auditTables, Set masterfileFeatures, Set features, Set folderUsers) {
        this.name = name;
        this.records = records;
        this.auditTables = auditTables;
        this.masterfileFeatures = masterfileFeatures;
        this.features = features;
        this.folderUsers = folderUsers;
    }

    public Integer getFolderId() {
        return this.folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public nz.cri.gns.fred.model.FolderType getFolderType() {
        return this.folderType;
    }

    public void setFolderType(nz.cri.gns.fred.model.FolderType folderType) {
        this.folderType = folderType;
    }

    public Integer getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Set getRecords() {
        return this.records;
    }

    public void setRecords(Set records) {
        this.records = records;
    }

    public Set getAuditTables() {
        return this.auditTables;
    }

    public void setAuditTables(Set auditTables) {
        this.auditTables = auditTables;
    }

    public Set getMasterfileFeatures() {
        return this.masterfileFeatures;
    }

    public void setMasterfileFeatures(Set masterfileFeatures) {
        this.masterfileFeatures = masterfileFeatures;
    }

    public Set getFeatures() {
        return this.features;
    }

    public void setFeatures(Set features) {
        this.features = features;
    }

    public Set getFolderUsers() {
        return this.folderUsers;
    }

    public void setFolderUsers(Set folderUsers) {
        this.folderUsers = folderUsers;
    }

	public int compareTo(Object arg0) {
		return name.compareTo(((Folder)arg0).name);
	}

}
