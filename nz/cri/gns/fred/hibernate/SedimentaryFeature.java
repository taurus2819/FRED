package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.hibernate.dao.CompositeKeyed;
import nz.cri.gns.fred.util.FREDUtil;

/** @author Hibernate CodeGenerator */
public class SedimentaryFeature implements Serializable, nz.cri.gns.fred.model.SedimentaryFeature, Cloneable {

    private static final long serialVersionUID = 20050818L;

    /** nullable persistent field */
    private String abundant;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.SedimentaryFeatureType sedimentaryFeatureType;

   /** full constructor */
    public SedimentaryFeature(String abundant, nz.cri.gns.fred.hibernate.SedimentaryFeatureType sedimentaryFeatureType) {
        this.abundant = abundant;
        this.sedimentaryFeatureType = sedimentaryFeatureType;
    }

    public SedimentaryFeature() {
    }
    
    public String getAbundant() {
        return this.abundant;
    }

    public void setAbundant(String abundant) {
        this.abundant = abundant;
    }

    public nz.cri.gns.fred.model.SedimentaryFeatureType getSedimentaryFeatureType() {
        return this.sedimentaryFeatureType;
    }

    public void setSedimentaryFeatureType(nz.cri.gns.fred.model.SedimentaryFeatureType sedimentaryFeatureType) {
        this.sedimentaryFeatureType = sedimentaryFeatureType;
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof SedimentaryFeature) ) return false;
        SedimentaryFeature castOther = (SedimentaryFeature) other;
        return FREDUtil.equals(abundant, castOther.getAbundant(), true)
        	&& FREDUtil.equals(sedimentaryFeatureType, castOther.getSedimentaryFeatureType(), true)
        	;
    }
	
    public int hashCode() {
		return sedimentaryFeatureType.hashCode();
	}
	
	public Object clone() { 
    	try {
    		SedimentaryFeature sedf = (SedimentaryFeature) super.clone();
    		return sedf;
    	} catch (CloneNotSupportedException e) {
    		//But it is!
    		return null;
    	}
    }
}
