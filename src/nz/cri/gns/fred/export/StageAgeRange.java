package nz.cri.gns.fred.export;

import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Stage;

public abstract class StageAgeRange implements AgeRange {

	
	public Age getLower() {
		return getStage().getLowerAge();
	}

	protected abstract Stage getStage();

	public boolean isLowerCertain() {
		return !"?".equals(getStage().getStageLowerMod());
	}

	public Age getUpper() {
		return getStage().getUpperAge();
	}

	public boolean isUpperCertain() {
		return !"?".equals(getStage().getStageUpperMod());
	}

}
