package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.OrgView;
import nz.cri.gns.fred.model.TaxonomicGroup;

public class FrUserView implements Serializable, nz.cri.gns.fred.model.FrUserView {

    private static final long serialVersionUID = 20050818L;

    private Integer userId;
    private String userName;
    private String givenName;
    private String familyName;
    private String fullName;
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

	public boolean equals(Object o) {
		return o instanceof FrUserView && ((FrUserView)o).userId.equals(userId);
	}
	
	public int hashCode() {
		return 261 * userId;
	}
}
