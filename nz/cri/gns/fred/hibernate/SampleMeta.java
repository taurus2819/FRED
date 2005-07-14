package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class SampleMeta implements Serializable {

    /** identifier field */
    private nz.cri.gns.fred.hibernate.SampleMetaPK comp_id;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.Sample sample;

    /** full constructor */
    public SampleMeta(nz.cri.gns.fred.hibernate.SampleMetaPK comp_id, nz.cri.gns.fred.hibernate.Sample sample) {
        this.comp_id = comp_id;
        this.sample = sample;
    }

    /** default constructor */
    public SampleMeta() {
    }

    /** minimal constructor */
    public SampleMeta(nz.cri.gns.fred.hibernate.SampleMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.hibernate.SampleMetaPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.SampleMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.hibernate.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.hibernate.Sample sample) {
        this.sample = sample;
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

}
