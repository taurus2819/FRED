package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

public class SquirrelAgeView implements Serializable, nz.cri.gns.fred.model.SquirrelAgeView {
    private Integer sampleId;
    private Double narrowBaseAge;
    private Double narrowTopAge;
    private Double wideBaseAge;
    private Double wideTopAge;
    
    public static final Double NON_DETERMINED_BASE_AGE = 999.9;
    public static final Double NON_DETERMINED_TOP_AGE = 0.0;

    @Override
    public int compareTo(nz.cri.gns.fred.model.SquirrelAgeView o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    
    
    public Double getNarrowBaseAge() {
        return narrowBaseAge;
    }

    public void setNarrowBaseAge(Double narrowBaseAge) {
        this.narrowBaseAge = narrowBaseAge;
    }

    public Double getNarrowTopAge() {
        return narrowTopAge;
    }

    public void setNarrowTopAge(Double narrowTopAge) {
        this.narrowTopAge = narrowTopAge;
    }

    public Double getWideBaseAge() {
        return wideBaseAge;
    }

    public void setWideBaseAge(Double wideBaseAge) {
        this.wideBaseAge = wideBaseAge;
    }

    public Double getWideTopAge() {
        return wideTopAge;
    }

    public void setWideTopAge(Double wideTopAge) {
        this.wideTopAge = wideTopAge;
    }

    @Override
    public boolean isDeterminedValue() {
        if(NON_DETERMINED_BASE_AGE.equals(narrowBaseAge) 
            && NON_DETERMINED_BASE_AGE.equals(wideBaseAge)
            && NON_DETERMINED_TOP_AGE.equals(narrowTopAge)
            && NON_DETERMINED_TOP_AGE.equals(wideTopAge)
        )   {
            return false;
        } else  {
            return true;
        }
    }
}
