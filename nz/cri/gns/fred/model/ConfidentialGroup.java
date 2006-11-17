package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface ConfidentialGroup extends Comparable<ConfidentialGroup>, NameableAndIdentifiable {
    public Integer getGroupId();
    public void setGroupId(Integer groupId);
    public String getName();
    public void setName(String name);
    public OrgView getOrgView();
    public void setOrgView(OrgView orgView);
	public Set<Audit> getAudits();
	public void setAudits(Set<Audit> audits);
	public Set<FrUserView> getUsers();
	public void setUsers(Set<FrUserView> users);
}
