package nz.cri.gns.fred.export;

import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.AgeView;

public class SimpleAgeRange implements AgeRange {

	protected AgeView lower;
	protected AgeView upper;
	protected boolean lowerCertain;
	protected boolean upperCertain;
	protected String comment;

	public SimpleAgeRange(AgeView lower, boolean lowerCertain, AgeView upper, boolean upperCertain, String comment) {
		this.lower = lower;
		this.upper = upper;
		this.lowerCertain = lowerCertain;
		this.upperCertain = upperCertain;
		this.comment = comment;
	}
	
	protected SimpleAgeRange() {
	}
	
	public AgeView getLower() {
		return lower;
	}

	public boolean isLowerCertain() {
		return lowerCertain;
	}

	public AgeView getUpper() {
		return upper;
	}

	public boolean isUpperCertain() {
		return upperCertain;
	}

	public String getComment() {
		return comment;
	}

}
