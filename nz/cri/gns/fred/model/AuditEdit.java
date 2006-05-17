package nz.cri.gns.fred.model;

import java.util.Date;

/**
 *
 */
public interface AuditEdit extends Comparable<AuditEdit>{
	
	public abstract Integer getAuditEditId();

	public abstract void setAuditEditId(Integer auditEditId);

	public abstract Integer getEditedById();

	public abstract void setEditedById(Integer editedById);

	public abstract Date getEditedDate();

	public abstract void setEditedDate(Date editedDate);

	public abstract String getComments();

	public abstract void setComments(String comments);

	public abstract Audit getAudit();

	public abstract void setAudit(Audit audit);
}