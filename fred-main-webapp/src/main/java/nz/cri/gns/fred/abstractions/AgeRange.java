package nz.cri.gns.fred.abstractions;

import nz.cri.gns.fred.model.Age;

public interface AgeRange {

    public Age getLower();

    public boolean isLowerCertain();

    public Age getUpper();

    public boolean isUpperCertain();

    public String getComment();

    /**
     * Returns a name for this age range determination method.
     */
    public String getAgeRangeType();
}
