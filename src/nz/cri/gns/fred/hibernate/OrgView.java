package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.UserView;

public class OrgView implements Serializable, nz.cri.gns.fred.model.OrgView {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer clientCode;

    /** nullable persistent field */
    private String companyName;
    
    /** persistent field */
    private Set<ConfidentialGroup> confidGroups;

    /** persistent field */
    private Set<UserView> userViews;
    
    /** persistent field */
    private Set<FrUserView> frUserViews;
    
    /** full constructor */
    public OrgView(Integer clientCode, String companyName, Set<ConfidentialGroup> confidGroups, Set<UserView> userViews, Set<FrUserView> frUserViews) {
    	this.clientCode = clientCode;
    	this.companyName = companyName;
    	this.confidGroups = confidGroups;
    	this.setUserViews(userViews);
    	this.setFrUserViews(frUserViews);
    }

    /** default constructor */
    public OrgView() {
    }

	public void setClientCode(Integer clientCode) {
		this.clientCode = clientCode;
	}

	public Integer getClientCode() {
		return clientCode;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyName() {
		return companyName;
	}
	
	public void setConfidGroups(Set<ConfidentialGroup> confidGroups) {
		this.confidGroups = confidGroups;
	}

	public Set<ConfidentialGroup> getConfidGroups() {
		return confidGroups;
	}

	public void setUserViews(Set<UserView> userViews) {
		this.userViews = userViews;
	}

	public Set<UserView> getUserViews() {
		return userViews;
	}

	public void setFrUserViews(Set<FrUserView> frUserViews) {
		this.frUserViews = frUserViews;
	}

	public Set<FrUserView> getFrUserViews() {
		return frUserViews;
	}

	public int compareTo(nz.cri.gns.fred.model.OrgView arg0) {
		return companyName.compareTo(arg0.getCompanyName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(clientCode);
	}

	public String getDisplayName() {
		return companyName;
	}

	public boolean equals(Object o) {
		return o instanceof OrgView && ((OrgView)o).clientCode.equals(clientCode);
	}
	
	public int hashCode() {
		return 264 * clientCode;
	}
}
