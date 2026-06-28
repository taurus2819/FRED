package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

public interface Adoption extends RecordDetails {
	public Integer getRecordId();
	public void setRecordId(Integer recordId);
	public Date getAdoptionDate();
	public void setAdoptionDate(Date adoptionDate);
	public String getDateRounding();
	public void setDateRounding(String dateRounding);
	public String getComments();
	public void setComments(String comments);
	public FREDRecord getRecord();
	public void setRecord(FREDRecord record);
	public Stage getStage();
	public void setStage(Stage stage);
	public Set<Person> getAdoptors();
	public void setAdoptors(Set<Person> adoptors);
}