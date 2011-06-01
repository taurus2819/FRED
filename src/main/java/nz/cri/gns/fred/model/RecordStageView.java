package nz.cri.gns.fred.model;

public interface RecordStageView extends Comparable<RecordStageView> {
	public void setId(String id);
	public String getId();
	public void setType(String type);
	public String getType();
	public void setBaseAge(Double baseAge);
	public Double getBaseAge();
	public void setTopAge(Double topAge);
	public Double getTopAge();
	public void setRecord(Record record);
	public Record getRecord();
	public void setStage(Stage stage);
	public Stage getStage();
}