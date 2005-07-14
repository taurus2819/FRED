package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class SampleMetaPK implements Serializable {

    /** identifier field */
    private Integer sampleId;

    /** identifier field */
    private Long metaId;

    /** full constructor */
    public SampleMetaPK(Integer sampleId, Long metaId) {
        this.sampleId = sampleId;
        this.metaId = metaId;
    }

    /** default constructor */
    public SampleMetaPK() {
    }

    public Integer getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Long getMetaId() {
        return this.metaId;
    }

    public void setMetaId(Long metaId) {
        this.metaId = metaId;
    }

 
    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof SampleMetaPK) ) return false;
        SampleMetaPK castOther = (SampleMetaPK) other;
        return castOther.metaId == metaId && castOther.sampleId == sampleId;
    }
    
	public int hashCode() {
		return (metaId + "_" + sampleId).hashCode();
	}

}
