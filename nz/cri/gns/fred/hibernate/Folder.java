package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FolderType;
import nz.cri.gns.fred.model.FolderUser;
import nz.cri.gns.fred.model.FrUserView;

/** @author Hibernate CodeGenerator */
public class Folder implements Serializable, nz.cri.gns.fred.model.Folder {

    private static final long serialVersionUID = 20050818L;
    
    /** identifier field */
    private Integer folderId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private FolderType folderType;

    /** nullable persistent field */
    private Integer ownerId;

    /** nullable persistent field */
    private FrUserView frUserView;
    
    /** persistent field */
    private Set<Audit> auditTables;

    /** persistent field */
    private Set<Feature> masterfileFeatures;

    /** persistent field */
    private Set<Feature> features;

    /** persistent field */
    private Set<FolderUser> folderUsers;

    /** full constructor */
    public Folder(String name, FolderType folderType, Integer ownerId, FrUserView frUserView, Set<Audit> auditTables, Set<Feature> masterfileFeatures, Set<Feature> features, Set<FolderUser> folderUsers) {
        this.name = name;
        this.folderType = folderType;
        this.ownerId = ownerId;
        this.frUserView = frUserView;
        this.auditTables = auditTables;
        this.masterfileFeatures = masterfileFeatures;
        this.features = features;
        this.folderUsers = folderUsers;
    }

    /** default constructor */
    public Folder() {
    }

    /** minimal constructor */
    public Folder(String name, Set<Audit> auditTables, Set<Feature> masterfileFeatures, Set<Feature> features, Set<FolderUser> folderUsers) {
        this.name = name;
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

    public FolderType getFolderType() {
        return this.folderType;
    }

    public void setFolderType(FolderType folderType) {
        this.folderType = folderType;
    }

    public Integer getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

	public FrUserView getFrUserView() {
		return frUserView;
	}
	
    public void setFrUserView(FrUserView frUserView) {
		this.frUserView = frUserView;
	}

	public Set<Audit> getAudits() {
        return this.auditTables;
    }

    public void setAudits(Set<Audit> auditTables) {
        this.auditTables = auditTables;
    }

    public Set<Feature> getMasterfileFeatures() {
        return this.masterfileFeatures;
    }

    public void setMasterfileFeatures(Set<Feature> masterfileFeatures) {
        this.masterfileFeatures = masterfileFeatures;
    }

    public Set<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    public Set<FolderUser> getFolderUsers() {
        return this.folderUsers;
    }

    public void setFolderUsers(Set<FolderUser> folderUsers) {
        this.folderUsers = folderUsers;
    }

	public int compareTo(nz.cri.gns.fred.model.Folder arg0) {
		return name.compareTo(((Folder)arg0).name);
	}
	
	public boolean equals(Object o) {
		return o instanceof Folder && ((Folder)o).getFolderId().intValue() == getFolderId().intValue();
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.folderId);
	}

	public String getDisplayName() {
		return this.name;
	}
}
