package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface OrgView extends Comparable<OrgView>, NameableAndIdentifiable {
	public void setClientCode(Integer clientCode);
	public Integer getClientCode();
	public void setCompanyName(String companyName);
	public String getCompanyName();
	public void setDeleted(Integer deleted);
	public Integer getDeleted();
	public void setConfidGroups(Set<ConfidentialGroup> confidGroups);
	public Set<ConfidentialGroup> getConfidGroups();
	public void setUserViews(Set<UserView> userViews);
	public Set<UserView> getUserViews();
	public void setFrUserViews(Set<FrUserView> frUserViews);
	public Set<FrUserView> getFrUserViews();
}
