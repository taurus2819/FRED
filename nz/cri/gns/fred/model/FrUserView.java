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
	public void setOrgView(OrgView orgView);
	public OrgView getOrgView();
	public void setTaxonomicGroups(Set<TaxonomicGroup> taxonomicGroups);
	public Set<TaxonomicGroup> getTaxonomicGroups();
	public void setConfidGroupsByOwnerId(Set<ConfidentialGroup> confidGroupsByOwnerId);
	public Set<ConfidentialGroup> getConfidGroupsByOwnerId();
	public void setConfidGroups(Set<ConfidentialGroup> confidGroups);
	public Set<ConfidentialGroup> getConfidGroups();
	public Set<Folder> getFolders();
	public void setFolders(Set<Folder> folders);
}
