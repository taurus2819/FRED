package nz.cri.gns.fred.export;

import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.AgeView;
import nz.cri.gns.fred.model.Stage;

public abstract class StageAgeRange implements AgeRange {

	
	public AgeView getLower() {
		return getStage().getLowerAgeView();
	}

	protected abstract Stage getStage();

	public boolean isLowerCertain() {
		return !"?".equals(getStage().getStageLowerMod());
	}

	public AgeView getUpper() {
		return getStage().getUpperAgeView();
	}

	public boolean isUpperCertain() {
		return !"?".equals(getStage().getStageUpperMod());
	}

}
