package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.SentTo;

/** @author Hibernate CodeGenerator */
public class FossilGroup implements Serializable, nz.cri.gns.fred.model.FossilGroup {

	private static final long serialVersionUID = 20050818L;

	/** identifier field */
    private Integer groupId;

    /** persistent field */
    private String name;

    /** persistent field */
    private Set<SentTo> sentTos;
    
    /** full constructor */
    public FossilGroup(Integer groupId, String name, Set<SentTo> sentTos) {
        this.groupId = groupId;
        this.name = name;
        this.sentTos = sentTos;
    }

    /** default constructor */
    public FossilGroup() {
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

    public Set<SentTo> getSentTos() {
        return this.sentTos;
    }

    public void setSentTos(Set<SentTo> sentTos) {
        this.sentTos = sentTos;
    }
    
    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
    	return o instanceof FossilGroup && groupId != null && groupId.equals(((FossilGroup)o).groupId);
    }

	public int compareTo(nz.cri.gns.fred.model.FossilGroup arg0) {
		return name.compareTo(arg0.getName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(groupId);
	}

	public String getDisplayName() {
		return name;
	}
}
