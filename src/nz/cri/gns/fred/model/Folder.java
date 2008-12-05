package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Folder extends Comparable<Folder>, NameableAndIdentifiable {
	
	public static final int FOLDER_READ_RIGHT = 1;
	public static final int FOLDER_EDIT_RIGHT = 2;
	public static final int FOLDER_CREATE_RIGHT = 4;
	public static final int FOLDER_DELETE_RIGHT = 8;
	public static final int FOLDER_SUBMIT_RIGHT = 16;
	public static final int FOLDER_ADMIN_RIGHT = 32;
	public static final int FOLDER_APPROVE_RIGHT = 64;
	
	public static final String FOLDER_TYPE_ADMIN = "Admin";
	public static final String FOLDER_TYPE_BACKLOG_ADMIN = "Backlog Admin";
	public static final String FOLDER_TYPE_PERSONAL = "Personal";
	public static final String FOLDER_TYPE_BACKLOG = "Backlog";

	public Integer getFolderId();
	public void setFolderId(Integer folderId);
	public String getName();
	public void setName(String name);
	public FolderType getFolderType();
	public void setFolderType(FolderType folderType);
	public FrUserView getOwner();
    public void setOwner(FrUserView owner);
	public Set<Audit> getAudits();
	public void setAudits(Set<Audit> audits);
	public Set<Feature> getMasterfileFeatures();
	public void setMasterfileFeatures(Set<Feature> masterfileFeatures);
	public Set<Feature> getFeatures();
	public void setFeatures(Set<Feature> features);
	public Set<FolderUser> getFolderUsers();
	public void setFolderUsers(Set<FolderUser> folderUsers);
}