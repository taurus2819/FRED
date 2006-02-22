package nz.cri.gns.fred.model;

import java.io.Serializable;
import java.util.Set;

/**
 * @author iainm
 */
public interface Folder extends Serializable, Comparable<Folder> {
	
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

	public abstract Integer getFolderId();

	public abstract void setFolderId(Integer folderId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract FolderType getFolderType();

	public abstract void setFolderType(FolderType folderType);

	public abstract Integer getOwnerId();

	public abstract void setOwnerId(Integer ownerId);

	public abstract Set getRecords();

	public abstract void setRecords(Set records);

	public abstract Set<Audit> getAudits();

	public abstract void setAudits(Set<Audit> audits);

	public abstract Set<Feature> getMasterfileFeatures();

	public abstract void setMasterfileFeatures(Set<Feature> masterfileFeatures);

	public abstract Set<Feature> getFeatures();

	public abstract void setFeatures(Set<Feature> features);

	public abstract Set<FolderUser> getFolderUsers();

	public abstract void setFolderUsers(Set<FolderUser> folderUsers);
}