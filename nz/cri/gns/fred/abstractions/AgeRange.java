package nz.cri.gns.fred.abstractions;

import nz.cri.gns.fred.model.AgeView;

public interface AgeRange {

	public AgeView getLower();
	
	public boolean isLowerCertain();
	
	public AgeView getUpper();
	
	public boolean isUpperCertain();
	
	public String getComment();
}
