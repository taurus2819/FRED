package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.Taxon;

public class UserView implements Serializable, nz.cri.gns.fred.model.UserView {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer userId;

    /** nullable persistent field */
    private String userName;
    
    /** nullable persistent field */
    private String givenName;

    /** nullable persistent field */
    private String familyName;

    /** nullable persistent field */
    private String fullName;
    
    /** persistent field */
    private Set<Audit> auditsByCreatedById;
    
    /** persistent field */
    private Set<Audit> auditsBySubmittedById;
    
    /** persistent field */
    private Set<Audit> auditsByApprovedById;
    
    /** persistent field */
    private Set<AuditEdit> auditEdits;
    
    /** persistent field */
    private Set<Taxon> taxaBySubmittedById;
    
    /** persistent field */
    private Set<Taxon> taxaByApprovedById;
    
    /** full constructor */
    public UserView(Integer userId, String userName, String givenName, String familyName, String fullName, Set<Audit> auditsByCreatedById, Set<Audit> auditsBySubmittedById, Set<Audit> auditsByApprovedById, Set<AuditEdit> auditEdits, Set<Taxon> taxaBySubmittedById, Set<Taxon> taxaByApprovedById) {
    	this.userId = userId;
    	this.userName = userName;
    	this.familyName = familyName;
    	this.fullName = fullName;
    	this.givenName = givenName;
    	this.auditsByCreatedById = auditsByCreatedById;
    	this.auditsBySubmittedById = auditsBySubmittedById;
    	this.auditsByApprovedById = auditsByApprovedById;
    	this.auditEdits = auditEdits;
    	this.taxaBySubmittedById = taxaBySubmittedById;
    	this.taxaByApprovedById = taxaByApprovedById;
    }

    /** default constructor */
    public UserView() {
    }

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
	
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setAuditsByCreatedById(Set<Audit> auditsByCreatedById) {
		this.auditsByCreatedById = auditsByCreatedById;
	}

	public Set<Audit> getAuditsByCreatedById() {
		return auditsByCreatedById;
	}

	public void setAuditsBySubmittedById(Set<Audit> auditsBySubmittedById) {
		this.auditsBySubmittedById = auditsBySubmittedById;
	}

	public Set<Audit> getAuditsBySubmittedById() {
		return auditsBySubmittedById;
	}

	public void setAuditsByApprovedById(Set<Audit> auditsByApprovedById) {
		this.auditsByApprovedById = auditsByApprovedById;
	}

	public Set<Audit> getAuditsByApprovedById() {
		return auditsByApprovedById;
	}

	public void setAuditEdits(Set<AuditEdit> auditEdits) {
		this.auditEdits = auditEdits;
	}

	public Set<AuditEdit> getAuditEdits() {
		return auditEdits;
	}

	public void setTaxaBySubmittedById(Set<Taxon> taxaBySubmittedById) {
		this.taxaBySubmittedById = taxaBySubmittedById;
	}

	public Set<Taxon> getTaxaBySubmittedById() {
		return taxaBySubmittedById;
	}

	public void setTaxaByApprovedById(Set<Taxon> taxaByApprovedById) {
		this.taxaByApprovedById = taxaByApprovedById;
	}

	public Set<Taxon> getTaxaByApprovedById() {
		return taxaByApprovedById;
	}

	public int compareTo(nz.cri.gns.fred.model.UserView arg0) {
		return (familyName + " " + givenName).compareTo(arg0.getFamilyName() + " " + arg0.getGivenName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(userId);
	}

	public String getDisplayName() {
		return fullName;
	}
	
}
