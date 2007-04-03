package nz.cri.gns.fred.export;

import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Stage;

public class AdoptionAge extends StageAgeRange implements AgeRange {

	private Adoption adoption;

	public AdoptionAge(Adoption adoption) {
		this.adoption = adoption;
	}

	public String getComment() {
		return adoption.getComments();
	}

	@Override
	protected Stage getStage() {
		return adoption.getStage();
	}

	public String getAgeRangeType() {
		return "Adoption";
	}

}
