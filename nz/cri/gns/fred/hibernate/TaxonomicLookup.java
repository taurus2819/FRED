package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.TaxonomicGroup;


/** @author Hibernate CodeGenerator */
public class TaxonomicLookup implements Serializable, nz.cri.gns.fred.model.Taxon {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer taxaId;

    /** persistent field */
    private String taxonomicName;

    /** nullable persistent field */
    private String author;

    /** persistent field */
    private String status;

    /** nullable persistent field */
    private Integer submittedById;

    /** nullable persistent field */
    private Date submittedDate;

    /** nullable persistent field */
    private Integer approvedById;

    /** nullable persistent field */
    private Date approvedDate;

    /** nullable persistent field */
    private String panelistComments;
    
    /** nullable persistent field */
    private String sendMessage;

    /** persistent field */
    private TaxonomicGroup taxonomicGroup;

    /** persistent field */
    private Set palLists;

    /** full constructor */
    public TaxonomicLookup(String taxonomicName, String author, String status, Integer submittedById, Date submittedDate, Integer approvedById, Date approvedDate, String panelistComments, String sendMessage, nz.cri.gns.fred.hibernate.TaxonomicGroup taxonomicGroup, Set palLists) {
        this.taxonomicName = taxonomicName;
        this.author = author;
        this.status = status;
        this.submittedById = submittedById;
        this.submittedDate = submittedDate;
        this.approvedById = approvedById;
        this.approvedDate = approvedDate;
        this.panelistComments = panelistComments;
        this.sendMessage = sendMessage;
        this.taxonomicGroup = taxonomicGroup;
        this.palLists = palLists;
    }

    /** default constructor */
    public TaxonomicLookup() {
    }

    /** minimal constructor */
    public TaxonomicLookup(String taxonomicName, String status, nz.cri.gns.fred.hibernate.TaxonomicGroup taxonomicGroup, Set palLists) {
        this.taxonomicName = taxonomicName;
        this.status = status;
        this.taxonomicGroup = taxonomicGroup;
        this.palLists = palLists;
    }

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

    public TaxonomicGroup getTaxonomicGroup() {
        return this.taxonomicGroup;
    }

    public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup) {
        this.taxonomicGroup = taxonomicGroup;
    }

    public Set getListEntries() {
        return this.palLists;
    }

    public void setListEntries(Set palLists) {
        this.palLists = palLists;
    }
    
	public int compareTo(nz.cri.gns.fred.model.Taxon arg0) {
		return taxonomicName.compareTo(arg0.getTaxonomicName());
	}
}
