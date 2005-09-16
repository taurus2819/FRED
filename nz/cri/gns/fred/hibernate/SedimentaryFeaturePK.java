package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class SedimentaryFeaturePK implements Serializable {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer sedFeatureId;

    /** identifier field */
    private Integer sampleId;

    /** full constructor */
    public SedimentaryFeaturePK(Integer sedFeatureId, Integer sampleId) {
        this.sedFeatureId = sedFeatureId;
        this.sampleId = sampleId;
    }

    /** default constructor */
    public SedimentaryFeaturePK() {
    }

    public Integer getSedFeatureId() {
        return this.sedFeatureId;
    }

    public void setSedFeatureId(Integer sedFeatureId) {
        this.sedFeatureId = sedFeatureId;
    }

    public Integer getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

  

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof SedimentaryFeaturePK) ) return false;
        SedimentaryFeaturePK castOther = (SedimentaryFeaturePK) other;
        return castOther.sampleId == sampleId && castOther.sedFeatureId == sedFeatureId;
    }
	public int hashCode() {
		return (sampleId + "_" + sedFeatureId).hashCode();
	}

}
