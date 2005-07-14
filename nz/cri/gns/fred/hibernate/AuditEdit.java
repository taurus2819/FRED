package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;


/** @author Hibernate CodeGenerator */
public class AuditEdit implements Serializable {

    /** identifier field */
    private Integer auditEditId;

    /** nullable persistent field */
    private Integer editedById;

    /** nullable persistent field */
    private Date editedDate;

    /** nullable persistent field */
    private String comments;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.AuditTable auditTable;

    /** full constructor */
    public AuditEdit(Integer editedById, Date editedDate, String comments, nz.cri.gns.fred.hibernate.AuditTable auditTable) {
        this.editedById = editedById;
        this.editedDate = editedDate;
        this.comments = comments;
        this.auditTable = auditTable;
    }

    /** default constructor */
    public AuditEdit() {
    }

    /** minimal constructor */
    public AuditEdit(nz.cri.gns.fred.hibernate.AuditTable auditTable) {
        this.auditTable = auditTable;
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

    public nz.cri.gns.fred.hibernate.AuditTable getAuditTable() {
        return this.auditTable;
    }

    public void setAuditTable(nz.cri.gns.fred.hibernate.AuditTable auditTable) {
        this.auditTable = auditTable;
    }

}
