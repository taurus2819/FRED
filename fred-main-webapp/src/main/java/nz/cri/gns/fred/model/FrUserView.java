package nz.cri.gns.fred.model;

import java.util.Set;
import java.util.Date;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface FrUserView extends Comparable<FrUserView>, NameableAndIdentifiable {
	public void setUserId(Integer userId);
	public Integer getUserId();
	public void setGivenName(String givenName);
	public String getGivenName();
	public void setFamilyName(String familyName);
	public String getFamilyName();
	public void setFullName(String fullName);
	public String getFullName();    
	public void setDeleted(Integer deleted);
	public Integer getDeleted();
	public void setOrgView(OrgView orgView);
	public OrgView getOrgView();
        public void setLastLogin(Date lastLogin);
        public Date getLastLogin();
        public void setUsername(String username);
        public String getUsername();
	public void setTaxonomicGroups(Set<TaxonomicGroup> taxonomicGroups);
	public Set<TaxonomicGroup> getTaxonomicGroups();
	public void setConfidGroupsByOwnerId(Set<ConfidentialGroup> confidGroupsByOwnerId);
	public Set<ConfidentialGroup> getConfidGroupsByOwnerId();
	public void setConfidGroups(Set<ConfidentialGroup> confidGroups);
	public Set<ConfidentialGroup> getConfidGroups();
	public Set<Folder> getFolders();
	public void setFolders(Set<Folder> folders);
        
        public void setHasWebAccessRight(Integer hasWebAccessRight);
        public Integer getHasWebAccessRight();
        public void setHasDataEntryRight(Integer hasDataEntryRight);
        public Integer getHasDataEntryRight();
        public void setHasAdminRight(Integer hasAdminRight);
        public Integer getHasAdminRight();
        
}
