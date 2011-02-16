package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface UserView extends Comparable<UserView>, NameableAndIdentifiable {
	public void setUserId(Integer userId);
	public Integer getUserId();
	public void setUserName(String userName);
	public String getUserName();
	public void setGivenName(String givenName);
	public String getGivenName();
	public void setFamilyName(String familyName);
	public String getFamilyName();
	public void setFullName(String fullName);
	public String getFullName();
	public void setOrgView(OrgView orgView);
	public OrgView getOrgView();
	public void setAuditsByCreatedById(Set<Audit> auditsByCreatedById);
	public Set<Audit> getAuditsByCreatedById();
	public void setAuditsBySubmittedById(Set<Audit> auditsBySubmittedById);
	public Set<Audit> getAuditsBySubmittedById();
	public void setAuditsByApprovedById(Set<Audit> auditsByApprovedById);
	public Set<Audit> getAuditsByApprovedById();
	public void setAuditEdits(Set<AuditEdit> auditEdits);
	public Set<AuditEdit> getAuditEdits();
	public void setLogs(Set<LogTable> logs);
	public Set<LogTable> getLogs();
	public void setTaxaBySubmittedById(Set<Taxon> taxaBySubmittedById);
	public Set<Taxon> getTaxaBySubmittedById();
	public void setTaxaByApprovedById(Set<Taxon> taxaByApprovedById);
	public Set<Taxon> getTaxaByApprovedById();
	public void setUserRightViews(Set<UserRightView> userRightViews);
	public Set<UserRightView> getUserRightViews();
}
