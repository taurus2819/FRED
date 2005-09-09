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

	public abstract Set getAuditTables();

	public abstract void setAuditTables(Set auditTables);

	public abstract Set getMasterfileFeatures();

	public abstract void setMasterfileFeatures(Set masterfileFeatures);

	public abstract Set getFeatures();

	public abstract void setFeatures(Set features);

	public abstract Set getFolderUsers();

	public abstract void setFolderUsers(Set folderUsers);
}