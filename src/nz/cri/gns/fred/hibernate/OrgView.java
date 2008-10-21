package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.UserView;

public class OrgView implements Serializable, nz.cri.gns.fred.model.OrgView {

    private static final long serialVersionUID = 20050818L;

    private Integer clientCode;
    private String companyName;
    private Boolean deleted;
    private Set<ConfidentialGroup> confidGroups;
    private Set<UserView> userViews;
    private Set<FrUserView> frUserViews;

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
	
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getDeleted() {
		return deleted;
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