package nz.cri.gns.fred.data;

import java.util.Date;

public class AuditEdit {

	private Integer editedByID;
	private String editedBy;
	private Date editedDate;
	private String comments;

	public AuditEdit() {
	}

	public void setEditedByID(Integer editedByID) {
		this.editedByID = editedByID;
	}

	public Integer getEditedByID() {
		return editedByID;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedDate(Date editedDate) {
		this.editedDate = editedDate;
	}

	public Date getEditedDate() {
		return editedDate;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}
	
}