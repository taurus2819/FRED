package nz.cri.gns.fred.export;

import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Stage;

public class PaleontologyAge extends StageAgeRange implements AgeRange {

	private Paleontology pal;

	public PaleontologyAge(Paleontology paleontology) {
		this.pal = paleontology;
	}


	public String getComment() {
		return pal.getStageComments();
	}


	@Override
	protected Stage getStage() {
		return pal.getStage();
	}


	public String getAgeRangeType() {
		return "List";
	}

}
