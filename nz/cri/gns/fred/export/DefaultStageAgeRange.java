package nz.cri.gns.fred.export;

import nz.cri.gns.fred.model.Stage;

public class DefaultStageAgeRange extends StageAgeRange {

	private Stage stage;

	public DefaultStageAgeRange(Stage stage) {
		this.stage = stage;
	}

	@Override
	protected Stage getStage() {
		return stage;
	}

	public String getComment() {
		return null;
	}

}
