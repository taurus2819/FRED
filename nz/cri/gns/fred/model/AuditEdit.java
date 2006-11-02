package nz.cri.gns.fred.model;

import java.util.Date;

public interface AuditEdit extends Comparable<AuditEdit>{
	public Integer getAuditEditId();
	public void setAuditEditId(Integer auditEditId);
	public Integer getEditedById();
	public void setEditedById(Integer editedById);
	public Date getEditedDate();
	public void setEditedDate(Date editedDate);
	public String getComments();
	public void setComments(String comments);
	public Audit getAudit();
	public void setAudit(Audit audit);
	public FrUserView getEditedBy();
    public void setEditedBy(FrUserView editedBy);

}