package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.hibernate.dao.CompositeKeyed;

/** @author Hibernate CodeGenerator */
public class FeatureMeta implements Serializable, nz.cri.gns.fred.model.FeatureMeta, CompositeKeyed {

    /** identifier field */
    private nz.cri.gns.fred.hibernate.FeatureMetaPK comp_id;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Feature feature;

	private boolean unsaved;

    /** full constructor */
    public FeatureMeta(nz.cri.gns.fred.hibernate.FeatureMetaPK comp_id, nz.cri.gns.fred.hibernate.Feature feature) {
        this.comp_id = comp_id;
        this.feature = feature;
        unsaved = true;
    }

    /** default constructor */
    public FeatureMeta(boolean saved) {
    	unsaved = !saved;
    }

    /** minimal constructor */
    public FeatureMeta(nz.cri.gns.fred.hibernate.FeatureMetaPK comp_id) {
        this.comp_id = comp_id;
        unsaved = true;
   }

    public nz.cri.gns.fred.hibernate.FeatureMetaPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.FeatureMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.model.Feature getFeature() {
        return this.feature;
    }

    public void setFeature(nz.cri.gns.fred.model.Feature feature) {
        this.feature = feature;
        if (comp_id != null) {
        	comp_id = new FeatureMetaPK();
        }
        comp_id.setFeatureId(feature.getFeatureId());
   }

    public boolean equals(Object other) {
        return other instanceof FeatureMeta && ((FeatureMeta)other).comp_id.equals(comp_id);
    }
	public int hashCode() {
		return comp_id.hashCode();
	}

	public Long getMetaId() {
		return (comp_id == null) ? null : comp_id.getMetaId();
	}

	public void setMetaId(Long id) {
	    if (comp_id != null) {
        	comp_id = new FeatureMetaPK();
        }
        comp_id.setMetaId(id);
	}

	public boolean isUnsaved() {
		return unsaved;
	}
}
