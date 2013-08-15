package nz.cri.gns.fred.export;

import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Stage;

public class PaleontologyAge extends StageAgeRange implements AgeRange {

	private Paleontology pal;
    private String label;

	public PaleontologyAge(Paleontology paleontology) {
		this(paleontology,null);
	}
    
    public PaleontologyAge(Paleontology paleontology, String label) {
		this.pal = paleontology;
        this.label=label;
	}


	public String getComment() {
		return pal.getStageComments();
	}


	@Override
	protected Stage getStage() {
		return pal.getStage();
	}


	public String getAgeRangeType() {
		return "List" + label;
	}

}
