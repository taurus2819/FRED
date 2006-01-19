package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;

import nz.cri.gns.fred.model.Audit;

/** @author Hibernate CodeGenerator */
public class AuditEdit implements Serializable, Comparable, nz.cri.gns.fred.model.AuditEdit {

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

    /** full constructor */
    public AuditEdit(Integer editedById, Date editedDate, String comments, Audit auditTable) {
        this.editedById = editedById;
        this.editedDate = editedDate;
        this.comments = comments;
        this.audit = auditTable;
    }

    /** default constructor */
    public AuditEdit() {
    }

    /** minimal constructor */
    public AuditEdit(Audit auditTable) {
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

    /**
     * Orders in reverse chronological order
     */
	public int compareTo(Object o) {
		if (editedDate == null)
			return ((AuditEdit)o).editedDate == null ? 0 : -1;
		else if (((AuditEdit)o).editedDate == null)
			return 1;
		else
			return -editedDate.compareTo(((AuditEdit)o).editedDate);
	}

}
