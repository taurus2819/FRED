package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class SedimentaryFeature implements Serializable {

    /** identifier field */
    private nz.cri.gns.fred.hibernate.SedimentaryFeaturePK comp_id;

    /** nullable persistent field */
    private String abundant;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.Sample sample;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.SedimentaryFeatureType sedimentaryFeatureType;

    /** full constructor */
    public SedimentaryFeature(nz.cri.gns.fred.hibernate.SedimentaryFeaturePK comp_id, String abundant, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.SedimentaryFeatureType sedimentaryFeatureType) {
        this.comp_id = comp_id;
        this.abundant = abundant;
        this.sample = sample;
        this.sedimentaryFeatureType = sedimentaryFeatureType;
    }

    /** default constructor */
    public SedimentaryFeature() {
    }

    /** minimal constructor */
    public SedimentaryFeature(nz.cri.gns.fred.hibernate.SedimentaryFeaturePK comp_id) {
        this.comp_id = comp_id;
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

    public nz.cri.gns.fred.hibernate.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.hibernate.Sample sample) {
        this.sample = sample;
    }

    public nz.cri.gns.fred.hibernate.SedimentaryFeatureType getSedimentaryFeatureType() {
        return this.sedimentaryFeatureType;
    }

    public void setSedimentaryFeatureType(nz.cri.gns.fred.hibernate.SedimentaryFeatureType sedimentaryFeatureType) {
        this.sedimentaryFeatureType = sedimentaryFeatureType;
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


}
