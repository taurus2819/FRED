package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.util.FREDUtil;

/** @author Hibernate CodeGenerator */
public class FeatureMetaPK implements Serializable {

    /** identifier field */
    private Integer featureId;

    /** identifier field */
    private Long metaId;

    /** full constructor */
    public FeatureMetaPK(Integer featureId, Long metaId) {
        this.featureId = featureId;
        this.metaId = metaId;
    }

    /** default constructor */
    public FeatureMetaPK() {
    }

    public Integer getFeatureId() {
        return this.featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    public Long getMetaId() {
        return this.metaId;
    }

    public void setMetaId(Long metaId) {
        this.metaId = metaId;
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof FeatureMetaPK) ) return false;
        FeatureMetaPK castOther = (FeatureMetaPK) other;
        return FREDUtil.equals(castOther.featureId, featureId, false) && FREDUtil.equals(castOther.metaId, metaId, false);
    }
	public int hashCode() {
		return (metaId + "_" + featureId).hashCode();
	}

}
