package nz.cri.gns.fred.model;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.hibernate.FolderType;

/**
 * @author iainm
 */
public interface Folder extends Serializable, Comparable {
	
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