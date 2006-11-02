package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface FrUserView extends Comparable<FrUserView>, NameableAndIdentifiable {
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
	public void setTaxonomicGroups(Set<TaxonomicGroup> taxonomicGroups);
	public Set<TaxonomicGroup> getTaxonomicGroups();
	public void setAuditsByCreatedById(Set<Audit> auditsByCreatedById);
	public Set<Audit> getAuditsByCreatedById();
	public void setAuditsBySubmittedById(Set<Audit> auditsBySubmittedById);
	public Set<Audit> getAuditsBySubmittedById();
	public void setAuditsByApprovedById(Set<Audit> auditsByApprovedById);
	public Set<Audit> getAuditsByApprovedById();
}
