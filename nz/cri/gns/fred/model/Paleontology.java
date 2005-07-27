package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

/**
 *
 */
public interface Paleontology extends RecordDetails {
	public abstract Integer getRecordId();

	public abstract void setRecordId(Integer recordId);

	public abstract Integer getPalId();

	public abstract void setPalId(Integer palId);

	public abstract Date getIdentificationDate();

	public abstract void setIdentificationDate(Date identificationDate);

	public abstract String getDateRounding();

	public abstract void setDateRounding(String dateRounding);

	public abstract String getStageComments();

	public abstract void setStageComments(String stageComments);

	public abstract String getLabNumber();

	public abstract void setLabNumber(String labNumber);

	public abstract String getCollectionComments();

	public abstract void setCollectionComments(String collectionComments);

	public abstract nz.cri.gns.fred.model.Record getRecord();

	public abstract void setRecord(nz.cri.gns.fred.model.Record record);

	public abstract nz.cri.gns.fred.model.LabSection getLabSection();

	public abstract void setLabSection(
			nz.cri.gns.fred.model.LabSection labSection);

	public abstract nz.cri.gns.fred.model.Stage getStage();

	public abstract void setStage(nz.cri.gns.fred.model.Stage stage);

	public abstract Set getPalLists();

	public abstract void setPalLists(Set palLists);

	public abstract Set getIdentifiers();

	public abstract void setIdentifiers(Set identifiers);
}