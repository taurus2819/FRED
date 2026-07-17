package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

public interface Paleontology extends RecordDetails {
	public Integer getRecordId();
	public void setRecordId(Integer recordId);
	public Date getIdentificationDate();
	public void setIdentificationDate(Date identificationDate);
	public String getDateRounding();
	public void setDateRounding(String dateRounding);
	public String getStageComments();
	public void setStageComments(String stageComments);
	public String getLabNumber();
	public void setLabNumber(String labNumber);
	public String getCollectionComments();
	public void setCollectionComments(String collectionComments);
	public FREDRecord getRecord();
	public void setRecord(FREDRecord record);
	public LabSection getLabSection();
	public void setLabSection(LabSection labSection);
	public Stage getStage();
	public void setStage(Stage stage);
	public Set<PaleontologyListEntry> getListEntries();
	public void setListEntries(Set<PaleontologyListEntry> palLists);
	public Set<Person> getIdentifiers();
	public void setIdentifiers(Set<Person> identifiers);
}