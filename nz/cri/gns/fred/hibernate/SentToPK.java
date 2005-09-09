package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class SentToPK implements Serializable {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer fossilGroupId;

    /** identifier field */
    private Integer sampleId;

    /** full constructor */
    public SentToPK(Integer fossilGroupId, Integer sampleId) {
        this.fossilGroupId = fossilGroupId;
        this.sampleId = sampleId;
    }

    /** default constructor */
    public SentToPK() {
    }

    public Integer getFossilGroupId() {
        return this.fossilGroupId;
    }

    public void setFossilGroupId(Integer fossilGroupId) {
        this.fossilGroupId = fossilGroupId;
    }

    public Integer getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }


    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof SentToPK) ) return false;
        SentToPK castOther = (SentToPK) other;
        return castOther.fossilGroupId == fossilGroupId && castOther.sampleId == sampleId;
    }

	public int hashCode() {
		return (fossilGroupId + "_" + sampleId).hashCode();
	}


}
