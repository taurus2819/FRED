package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

public class SquirrelAgeView implements Serializable, nz.cri.gns.fred.model.SquirrelAgeView {

    private Double narrowBaseAge;
    private Double narrowTopAge;
    private Double wideBaseAge;
    private Double wideTopAge;

    @Override
    public int compareTo(nz.cri.gns.fred.model.SquirrelAgeView o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    
    
}
