package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.LogTable;
import nz.cri.gns.fred.model.OrgView;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.UserRightView;

public class UserView implements Serializable, nz.cri.gns.fred.model.UserView {

    private static final long serialVersionUID = 20050818L;

    private Integer userId;
    private String userName;
    private String givenName;
    private String familyName;
    private String fullName;
    private OrgView orgView;
    private Set<Audit> auditsByCreatedById;
    private Set<Audit> auditsBySubmittedById;
    private Set<Audit> auditsByApprovedById;
    private Set<AuditEdit> auditEdits;
    private Set<LogTable> logs;
    private Set<Taxon> taxaBySubmittedById;
    private Set<Taxon> taxaByApprovedById;
    private Set<UserRightView> userRightViews;
    
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

	public void setOrgView(OrgView orgView) {
		this.orgView = orgView;
	}

	public OrgView getOrgView() {
		return orgView;
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

	public void setLogs(Set<LogTable> logs) {
		this.logs = logs;
	}

	public Set<LogTable> getLogs() {
		return logs;
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

	public void setUserRightViews(Set<UserRightView> userRightViews) {
		this.userRightViews = userRightViews;
	}

	public Set<UserRightView> getUserRightViews() {
		return userRightViews;
	}

	public int compareTo(nz.cri.gns.fred.model.UserView arg0) {
		return (familyName + " " + givenName).compareTo(arg0.getFamilyName() + " " + arg0.getGivenName());
	}

	public String toString() {
		return fullName;
	}
	
	public String getUniqueIdentifier() {
		return String.valueOf(userId);
	}

	public String getDisplayName() {
		return fullName;
	}

	public boolean equals(Object o) {
		return o instanceof UserView && ((UserView)o).userId.equals(userId);
	}
	
	public int hashCode() {
		return 453 * userId;
	}
}
