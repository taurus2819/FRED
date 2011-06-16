package nz.cri.gns.fred.export;

import nz.cri.gns.fred.model.Stage;

public class DefaultStageAgeRange extends StageAgeRange {

	private Stage stage;
	private String label;

	public DefaultStageAgeRange(Stage stage, String label) {
		this.stage = stage;
		this.label = label;
	}

	@Override
	protected Stage getStage() {
		return stage;
	}

	public String getComment() {
		return null;
	}

	public String getAgeRangeType() {
		return label;
	}

}
