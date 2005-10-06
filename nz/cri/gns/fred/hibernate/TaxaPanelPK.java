package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.util.FREDUtil;

/** @author Hibernate CodeGenerator */
public class TaxaPanelPK implements Serializable {

    private static final long serialVersionUID = 20050818L;

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
        return FREDUtil.equals(castOther.groupId, groupId, false) && FREDUtil.equals(castOther.panelistId, panelistId, false);
    }

	public int hashCode() {
		return (groupId + "_" + panelistId).hashCode();
	}

}
