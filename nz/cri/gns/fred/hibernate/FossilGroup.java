package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class FossilGroup implements Serializable, nz.cri.gns.fred.model.FossilGroup {

    /**
	 * 
	 */
	private static final long serialVersionUID = 20050818L;

	/** identifier field */
    private Integer groupId;

    /** persistent field */
    private String name;

    /** full constructor */
    public FossilGroup(Integer groupId, String name) {
        this.groupId = groupId;
        this.name = name;
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

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
    	return o instanceof FossilGroup && groupId != null && groupId.equals(((FossilGroup)o).groupId);
    }
}
