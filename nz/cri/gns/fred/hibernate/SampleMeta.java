package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.hibernate.dao.CompositeKeyed;

/** @author Hibernate CodeGenerator */
public class SampleMeta implements Serializable, nz.cri.gns.fred.model.SampleMeta, CompositeKeyed {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private nz.cri.gns.fred.hibernate.SampleMetaPK comp_id;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Sample sample;

	private boolean unsaved;

    /** full constructor */
    public SampleMeta(nz.cri.gns.fred.hibernate.SampleMetaPK comp_id, nz.cri.gns.fred.hibernate.Sample sample) {
        this.comp_id = comp_id;
        this.sample = sample;
        unsaved = true;
    }

    public SampleMeta() {
    	throw new IllegalArgumentException("Do not use this constructor");
    }
    
   /** default constructor */
    public SampleMeta(boolean saved) {
    	unsaved = !saved;
    }

    /** minimal constructor */
    public SampleMeta(nz.cri.gns.fred.hibernate.SampleMetaPK comp_id) {
        this.comp_id = comp_id;
        unsaved = true;
    }

    public nz.cri.gns.fred.hibernate.SampleMetaPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.SampleMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.model.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.model.Sample sample) {
        this.sample = sample;
        if (comp_id == null) {
        	comp_id = new SampleMetaPK();
        }
        comp_id.setSampleId(sample.getSampleId());
   }

 
    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof SampleMeta) ) return false;
        SampleMeta castOther = (SampleMeta) other;
        return castOther.comp_id.equals(comp_id);
    }
	public int hashCode() {
		return comp_id.hashCode();
	}

	public Long getMetaId() {
		return (comp_id == null) ? null : comp_id.getMetaId();
	}

	public void setMetaId(Long id) {
	    if (comp_id == null) {
        	comp_id = new SampleMetaPK();
        }
        comp_id.setMetaId(id);
	}

	public boolean isUnsaved() {
		return unsaved;
	}

}
