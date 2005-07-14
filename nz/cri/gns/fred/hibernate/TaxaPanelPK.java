package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class TaxaPanelPK implements Serializable {

    /** identifier field */
    private Integer groupId;

    /** identifier field */
    private Integer panelistId;

    /** full constructor */
    public TaxaPanelPK(Integer groupId, Integer panelistId) {
        this.groupId = groupId;
        this.panelistId = panelistId;
    }

    /** default constructor */
    public TaxaPanelPK() {
    }

    public Integer getGroupId() {
        return this.groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getPanelistId() {
        return this.panelistId;
    }

    public void setPanelistId(Integer panelistId) {
        this.panelistId = panelistId;
    }

 

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TaxaPanelPK) ) return false;
        TaxaPanelPK castOther = (TaxaPanelPK) other;
        return castOther.panelistId == panelistId && castOther.groupId == groupId;
    }

	public int hashCode() {
		return (groupId + "_" + panelistId).hashCode();
	}

}
