package nz.cri.gns.fred.model;

public interface SampleStageView extends Comparable<SampleStageView> {
	public void setId(String id);
	public String getId();
	public void setType(String type);
	public String getType();
	public void setBaseAge(Double baseAge);
	public Double getBaseAge();
	public void setTopAge(Double topAge);
	public Double getTopAge();
	public void setSample(Sample sample);
	public Sample getSample();
	public void setStage(Stage stage);
	public Stage getStage();
}