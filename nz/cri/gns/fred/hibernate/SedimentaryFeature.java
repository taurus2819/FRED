package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.hibernate.dao.CompositeKeyed;

/** @author Hibernate CodeGenerator */
public class SedimentaryFeature implements Serializable, nz.cri.gns.fred.model.SedimentaryFeature, Cloneable, CompositeKeyed {

    /** identifier field */
    private nz.cri.gns.fred.hibernate.SedimentaryFeaturePK comp_id;

    /** nullable persistent field */
    private String abundant;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Sample sample;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.SedimentaryFeatureType sedimentaryFeatureType;

	private boolean unsaved;

    /** full constructor */
    public SedimentaryFeature(nz.cri.gns.fred.hibernate.SedimentaryFeaturePK comp_id, String abundant, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.SedimentaryFeatureType sedimentaryFeatureType) {
        this.comp_id = comp_id;
        this.abundant = abundant;
        this.sample = sample;
        this.sedimentaryFeatureType = sedimentaryFeatureType;
        unsaved = true;
    }

    public SedimentaryFeature() {
    	throw new IllegalArgumentException("Do not use this constructor");
    }
    
    /** default constructor */
    public SedimentaryFeature(boolean saved) {
    	unsaved = !saved;
    }

    /** minimal constructor */
    public SedimentaryFeature(nz.cri.gns.fred.hibernate.SedimentaryFeaturePK comp_id) {
        this.comp_id = comp_id;
        unsaved = true;
    }

    public nz.cri.gns.fred.hibernate.SedimentaryFeaturePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.SedimentaryFeaturePK comp_id) {
        this.comp_id = comp_id;
    }

    public String getAbundant() {
        return this.abundant;
    }

    public void setAbundant(String abundant) {
        this.abundant = abundant;
    }

    public nz.cri.gns.fred.model.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.model.Sample sample) {
        this.sample = sample;
        if (comp_id != null) {
        	comp_id = new SedimentaryFeaturePK();
        }
        comp_id.setSampleId(sample.getSampleId());
   }

    public nz.cri.gns.fred.model.SedimentaryFeatureType getSedimentaryFeatureType() {
        return this.sedimentaryFeatureType;
    }

    public void setSedimentaryFeatureType(nz.cri.gns.fred.model.SedimentaryFeatureType sedimentaryFeatureType) {
        this.sedimentaryFeatureType = sedimentaryFeatureType;
        if (comp_id != null) {
        	comp_id = new SedimentaryFeaturePK();
        }
        comp_id.setSedFeatureId(sedimentaryFeatureType.getSedfeatureTypeId());
    }

 

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof SedimentaryFeature) ) return false;
        SedimentaryFeature castOther = (SedimentaryFeature) other;
        return castOther.comp_id.equals(comp_id);
    }
	public int hashCode() {
		return comp_id.hashCode();
	}
	
	public Object clone() { 
    	try {
    		SedimentaryFeature sedf = (SedimentaryFeature) super.clone();
    		sedf.unsaved = true;
    		return sedf;
    	} catch (CloneNotSupportedException e) {
    		//But it is!
    		return null;
    	}
    }

	public boolean isUnsaved() {
		return unsaved;
	}


}
