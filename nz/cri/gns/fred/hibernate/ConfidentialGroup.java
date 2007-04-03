package nz.cri.gns.fred.hibernate;

import java.util.Set;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.OrgView;

/** @author Hibernate CodeGenerator */
public class ConfidentialGroup implements nz.cri.gns.fred.model.ConfidentialGroup {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer groupId;

    /** persistent field */
    private String name;

    /** persistent field */
    private OrgView orgView;
    
    /** persistent field */
    private FrUserView owner;
    
    /** persistent field */
    private Set<Audit> audits;

    /** persistent field */
    private Set<FrUserView> users;
    
    /** full constructor */
    public ConfidentialGroup(Integer groupId, String name, OrgView orgView, FrUserView owner, Set<Audit> audits, Set<FrUserView> users) {
        this.groupId = groupId;
        this.name = name;
        this.orgView = orgView;
        this.owner = owner;
        this.audits = audits;
        this.users = users;
    }

    /** default constructor */
    public ConfidentialGroup() {
    }

    public Integer getGroupId() {
        return this.groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrgView getOrgView() {
        return this.orgView;
    }

    public void setOrgView(OrgView orgView) {
        this.orgView = orgView;
    }
    
	public FrUserView getOwner() {
		return owner;
	}

	public void setOwner(FrUserView owner) {
		this.owner = owner;
	}
	
	public Set<Audit> getAudits() {
		return audits;
	}
	
	public void setAudits(Set<Audit> audits) {
		this.audits = audits;
	}

	public Set<FrUserView> getUsers() {
		return users;
	}
	
	public void setUsers(Set<FrUserView> users) {
		this.users = users;
	}

	public int compareTo(nz.cri.gns.fred.model.ConfidentialGroup arg0) {
		return name.compareTo(arg0.getName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(groupId);
	}

	public String getDisplayName() {
		return name;
	}

	public boolean equals(Object o) {
		return o instanceof ConfidentialGroup && ((ConfidentialGroup)o).groupId.equals(groupId);
	}
	
	public int hashCode() {
		return 893 * groupId;
	}
}
