package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

public class UnsavedTaxon implements Taxon {

	public Integer getTaxaId() {
		return null;
	}

	public void setTaxaId(Integer taxaId) {
		throw new IllegalStateException("Not savable");
	}

	private String taxonomicName;
	private String author;
	private String status;
	private Integer submittedById;
	private Date submittedDate;
	private Integer approvedById;
	private Date approvedDate;
	private String panelistComments;
	private String sendMessage;
    private UserView submittedBy;
    private UserView approvedBy;
	private TaxonomicGroup taxonomicGroup;
	private Set<PaleontologyListEntry> listEntries;
	
	public Integer getApprovedById() {
		return approvedById;
	}

	public void setApprovedById(Integer approvedById) {
		this.approvedById = approvedById;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Set<PaleontologyListEntry> getListEntries() {
		return listEntries;
	}

	public void setListEntries(Set<PaleontologyListEntry> listEntries) {
		this.listEntries = listEntries;
	}

	public String getPanelistComments() {
		return this.panelistComments;
	}

	public void setPanelistComments(String panelistComments) {
		this.panelistComments = panelistComments;
	}
	
	public String getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(String sendMessage) {
		this.sendMessage = sendMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSubmittedById() {
		return submittedById;
	}

	public void setSubmittedById(Integer submittedById) {
		this.submittedById = submittedById;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

    public void setSubmittedBy(UserView submittedBy) {
		this.submittedBy = submittedBy;
	}

	public UserView getSubmittedBy() {
		return submittedBy;
	}

	public void setApprovedBy(UserView approvedBy) {
		this.approvedBy = approvedBy;
	}

	public UserView getApprovedBy() {
		return approvedBy;
	}
	
	public TaxonomicGroup getTaxonomicGroup() {
		return taxonomicGroup;
	}

	public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup) {
		this.taxonomicGroup = taxonomicGroup;
	}

	public String getTaxonomicName() {
		return taxonomicName;
	}

	public void setTaxonomicName(String taxonomicName) {
		this.taxonomicName = taxonomicName;
	}
	
	public int compareTo(nz.cri.gns.fred.model.Taxon arg0) {
		return taxonomicName.compareTo(arg0.getTaxonomicName());
	}

	public String getUniqueIdentifier() {
		return null;
	}

	public String getDisplayName() {
		return taxonomicName;
	}

}