package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.OrgView;
import nz.cri.gns.fred.model.TaxonomicGroup;

public class FrUserView implements Serializable, nz.cri.gns.fred.model.FrUserView {

    private static final long serialVersionUID = 20050818L;

    private Integer userId;
    private String givenName;
    private String familyName;
    private String fullName;
    private String username;
    private Boolean deleted;
    private Boolean hasWebAccessRight;
    private Boolean hasDataEntryRight;
    private Boolean hasAdminRight;
    private Date lastLogin;
    private OrgView orgView;
    private Set<TaxonomicGroup> taxonomicGroups;
    private Set<ConfidentialGroup> confidGroupsByOwnerId;
    private Set<ConfidentialGroup> confidGroups;
    private Set<Folder> folders;

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
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
        
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getDeleted() {
		return deleted;
	}

    /**
     * @return the hasWebAccessRight
     */
    @Override
    public Boolean getHasWebAccessRight() {
        return hasWebAccessRight;
    }

    /**
     * @param hasWebAccessRight the hasWebAccessRight to set
     */
    @Override
    public void setHasWebAccessRight(Boolean hasWebAccessRight) {
        this.hasWebAccessRight = hasWebAccessRight;
    }

    /**
     * @return the hasDataEntryRight
     */
    public Boolean getHasDataEntryRight() {
        return hasDataEntryRight;
    }

    /**
     * @param hasDataEntryRight the hasDataEntryRight to set
     */
    @Override
    public void setHasDataEntryRight(Boolean hasDataEntryRight) {
        this.hasDataEntryRight = hasDataEntryRight;
    }

    /**
     * @return the hasAdminRight
     */
    @Override
    public Boolean getHasAdminRight() {
        return hasAdminRight;
    }

    /**
     * @param hasAdminRight the hasAdminRight to set
     */
    @Override
    public void setHasAdminRight(Boolean hasAdminRight) {
        this.hasAdminRight = hasAdminRight;
    }

    @Override
	public void setOrgView(OrgView orgView) {
		this.orgView = orgView;
	}

	public OrgView getOrgView() {
		return orgView;
	}
        
    @Override
       public void setLastLogin(Date lastLogin) {
            this.lastLogin = lastLogin;
        }
        
    @Override
        public Date getLastLogin() {
            return this.lastLogin;
        }

	public void setTaxonomicGroups(Set<TaxonomicGroup> taxonomicGroups) {
		this.taxonomicGroups = taxonomicGroups;
	}

	public Set<TaxonomicGroup> getTaxonomicGroups() {
		return taxonomicGroups;
	}

	public Set<ConfidentialGroup> getConfidGroupsByOwnerId() {
		return confidGroupsByOwnerId;
	}
	
	public void setConfidGroupsByOwnerId(Set<ConfidentialGroup> confidGroupsByOwnerId) {
		this.confidGroupsByOwnerId = confidGroupsByOwnerId;
	}

	public Set<ConfidentialGroup> getConfidGroups() {
		return confidGroups;
	}
	
	public void setConfidGroups(Set<ConfidentialGroup> confidGroups) {
		this.confidGroups = confidGroups;
	}

	public Set<Folder> getFolders() {
		return folders;
	}
	
	public void setFolders(Set<Folder> folders) {
		this.folders = folders;
	}

	public int compareTo(nz.cri.gns.fred.model.FrUserView arg0) {
		return (familyName + " " + givenName).compareTo(arg0.getFamilyName() + " " + arg0.getGivenName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(userId);
	}

	public String getDisplayName() {
		return fullName;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof FrUserView && ((FrUserView)o).userId.equals(userId);
	}
	
	@Override
	public int hashCode() {
		return 261 * userId;
	}

    /**
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

}
