package nz.cri.gns.fred.export;

import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Age;

public abstract class SimpleAgeRange implements AgeRange {

    protected Age lower;
    protected Age upper;
    protected boolean lowerCertain;
    protected boolean upperCertain;
    protected String comment;

    public SimpleAgeRange(Age lower, boolean lowerCertain, Age upper, boolean upperCertain, String comment) {
        this.lower = lower;
        this.upper = upper;
        this.lowerCertain = lowerCertain;
        this.upperCertain = upperCertain;
        this.comment = comment;
    }

    protected SimpleAgeRange() {
    }

    public Age getLower() {
        return lower;
    }

    public boolean isLowerCertain() {
        return lowerCertain;
    }

    public Age getUpper() {
        return upper;
    }

    public boolean isUpperCertain() {
        return upperCertain;
    }

    public String getComment() {
        return comment;
    }
}
