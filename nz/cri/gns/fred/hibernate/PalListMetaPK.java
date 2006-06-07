package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.hibernate.dao.CompositeKey;
import nz.cri.gns.fred.util.FREDUtil;

/** @author Hibernate CodeGenerator */
public class PalListMetaPK implements Serializable, CompositeKey {

    /** identifier field */
    private Integer palListId;

    /** identifier field */
    private Long metaId;

    /** full constructor */
    public PalListMetaPK(Integer palListId, Long metaId) {
        this.palListId = palListId;
        this.metaId = metaId;
    }

    /** default constructor */
    public PalListMetaPK() {
    }

    public Integer getPalListId() {
        return this.palListId;
    }

    public void setPalListId(Integer palListId) {
        this.palListId = palListId;
    }

    public Long getMetaId() {
        return this.metaId;
    }

    public void setMetaId(Long metaId) {
        this.metaId = metaId;
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PalListMetaPK) ) return false;
        PalListMetaPK castOther = (PalListMetaPK) other;
        return FREDUtil.equals(castOther.palListId, palListId, false) && FREDUtil.equals(castOther.metaId, metaId, false);
    }
    
	public int hashCode() {
		return (metaId + "_" + palListId).hashCode();
	}

}
