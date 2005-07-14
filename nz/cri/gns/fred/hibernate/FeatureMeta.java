package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class FeatureMeta implements Serializable {

    /** identifier field */
    private nz.cri.gns.fred.hibernate.FeatureMetaPK comp_id;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.Feature feature;

    /** full constructor */
    public FeatureMeta(nz.cri.gns.fred.hibernate.FeatureMetaPK comp_id, nz.cri.gns.fred.hibernate.Feature feature) {
        this.comp_id = comp_id;
        this.feature = feature;
    }

    /** default constructor */
    public FeatureMeta() {
    }

    /** minimal constructor */
    public FeatureMeta(nz.cri.gns.fred.hibernate.FeatureMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.hibernate.FeatureMetaPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.FeatureMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.hibernate.Feature getFeature() {
        return this.feature;
    }

    public void setFeature(nz.cri.gns.fred.hibernate.Feature feature) {
        this.feature = feature;
    }

    public boolean equals(Object other) {
        return other instanceof FeatureMeta && ((FeatureMeta)other).comp_id.equals(comp_id);
    }
	public int hashCode() {
		return comp_id.hashCode();
	}
}
