package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.UserView;

public class TaxonomicLookup implements Serializable, nz.cri.gns.fred.model.Taxon {

    private static final long serialVersionUID = 20050818L;

    private Integer taxaId;
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
    private Set<PaleontologyListEntry> palLists;

    public Integer getTaxaId() {
        return this.taxaId;
    }

    public void setTaxaId(Integer taxaId) {
        this.taxaId = taxaId;
    }

    public String getTaxonomicName() {
        return this.taxonomicName;
    }

    public void setTaxonomicName(String taxonomicName) {
        taxonomicName
        .replace("`", "\\'")
        .replace("\"", "\\'").trim();
        this.taxonomicName = taxonomicName;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSubmittedById() {
        return this.submittedById;
    }

    public void setSubmittedById(Integer submittedById) {
        this.submittedById = submittedById;
    }

    public Date getSubmittedDate() {
        return this.submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Integer getApprovedById() {
        return this.approvedById;
    }

    public void setApprovedById(Integer approvedById) {
        this.approvedById = approvedById;
    }

    public Date getApprovedDate() {
        return this.approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getPanelistComments() {
        return this.panelistComments;
    }

    public void setPanelistComments(String panelistComments) {
        this.panelistComments = panelistComments;
    }
    
    public String getSendMessage() {
        return this.sendMessage;
    }

    public void setSendMessage(String sendMessage) {
        this.sendMessage = sendMessage;
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
        return this.taxonomicGroup;
    }

    public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup) {
        this.taxonomicGroup = taxonomicGroup;
    }

    public Set<PaleontologyListEntry> getListEntries() {
        return this.palLists;
    }

    public void setListEntries(Set<PaleontologyListEntry> palLists) {
        this.palLists = palLists;
    }
    
	public int compareTo(nz.cri.gns.fred.model.Taxon arg0) {
		if (!taxonomicGroup.equals(arg0.getTaxonomicGroup()))
			return taxonomicGroup.compareTo(arg0.getTaxonomicGroup());
		return (taxonomicName.toUpperCase()).compareTo(arg0.getTaxonomicName().toUpperCase());
	}

	@Override
	public String toString() {
		return taxonomicName;
	}
	
	public String getUniqueIdentifier() {
		return String.valueOf(taxaId);
	}

	public String getDisplayName() {
		return taxonomicName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Taxon))
			return false;
		Taxon taxon = (Taxon)o;
		return taxon.getTaxonomicName().equals(taxonomicName) 
			&& taxon.getTaxonomicGroup().equals(taxonomicGroup);
	}
	
	@Override
	public int hashCode() {
		return taxonomicGroup.hashCode() + 1000 * taxonomicName.hashCode();
	}
}
