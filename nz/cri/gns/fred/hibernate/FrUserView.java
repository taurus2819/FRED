package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.OrgView;
import nz.cri.gns.fred.model.TaxonomicGroup;

public class FrUserView implements Serializable, nz.cri.gns.fred.model.FrUserView {

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
    private OrgView orgView;
    
    /** persistent field */
    private Set<TaxonomicGroup> taxonomicGroups;
    
    /** persistent field */
    private Set<ConfidentialGroup> confidGroups;
    
    /** full constructor */
    public FrUserView(Integer userId, String userName, String givenName, String familyName, String fullName, OrgView orgView, Set<TaxonomicGroup> taxonomicGroups, Set<ConfidentialGroup> confidGroups) {
    	this.userId = userId;
    	this.userName = userName;
    	this.givenName = givenName;
    	this.familyName = familyName;
    	this.fullName = fullName;
    	this.orgView = orgView;
    	this.taxonomicGroups = taxonomicGroups;
    	this.setConfidGroups(confidGroups);
    }

    /** default constructor */
    public FrUserView() {
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
	
	public Set<ConfidentialGroup> getConfidGroups() {
		return confidGroups;
	}
	
	public void setConfidGroups(Set<ConfidentialGroup> confidGroups) {
		this.confidGroups = confidGroups;
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
	
}
