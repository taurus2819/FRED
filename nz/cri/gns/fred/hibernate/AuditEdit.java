package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.UserView;

/** @author Hibernate CodeGenerator */
public class AuditEdit implements Serializable, Comparable<nz.cri.gns.fred.model.AuditEdit>, nz.cri.gns.fred.model.AuditEdit {

	private static final long serialVersionUID = 20050818L;
	
	/** identifier field */
    private Integer auditEditId;

    /** nullable persistent field */
    private Integer editedById;

    /** nullable persistent field */
    private Date editedDate;

    /** nullable persistent field */
    private String comments;

    /** persistent field */
    private Audit audit;
    
    /** persistent field */
    private UserView editedBy;

    /** full constructor */
    public AuditEdit(Integer editedById, Date editedDate, String comments, Audit auditTable, UserView editedBy) {
        this.editedById = editedById;
        this.editedDate = editedDate;
        this.comments = comments;
        this.audit = auditTable;
        this.setEditedBy(editedBy);
    }

    /** default constructor */
    public AuditEdit() {
    }

    /** minimal constructor */
    public AuditEdit(AuditTable auditTable) {
        this.audit = auditTable;
    }

    public Integer getAuditEditId() {
        return this.auditEditId;
    }

    public void setAuditEditId(Integer auditEditId) {
        this.auditEditId = auditEditId;
    }

    public Integer getEditedById() {
        return this.editedById;
    }

    public void setEditedById(Integer editedById) {
        this.editedById = editedById;
    }

    public Date getEditedDate() {
        return this.editedDate;
    }

    public void setEditedDate(Date editedDate) {
        this.editedDate = editedDate;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Audit getAudit() {
        return this.audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public void setEditedBy(UserView editedBy) {
		this.editedBy = editedBy;
	}

	public UserView getEditedBy() {
		return editedBy;
	}

	/**
     * Orders in chronological order
     */
	public int compareTo(nz.cri.gns.fred.model.AuditEdit o) {
		if (editedDate == null)
			return o.getEditedDate() == null ? 0 : -1;
		else if (o.getEditedDate() == null)
			return 1;
		else
			return editedDate.compareTo(o.getEditedDate());
	}

}
