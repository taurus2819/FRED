package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

/**
 *
 */
public interface Adoption {
	public abstract Integer getRecordId();

	public abstract void setRecordId(Integer recordId);

	public abstract Date getAdoptionDate();

	public abstract void setAdoptionDate(Date adoptionDate);

	public abstract String getDateRounding();

	public abstract void setDateRounding(String dateRounding);

	public abstract String getComments();

	public abstract void setComments(String comments);

	public abstract nz.cri.gns.fred.model.Record getRecord();

	public abstract void setRecord(nz.cri.gns.fred.model.Record record);

	public abstract nz.cri.gns.fred.model.Stage getStage();

	public abstract void setStage(nz.cri.gns.fred.model.Stage stage);

	public abstract Set getAdopters();

	public abstract void setAdopters(Set adopters);
}