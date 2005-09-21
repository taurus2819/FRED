package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface Record extends Audited {
	public abstract Integer getRecordId();

	public abstract void setRecordId(Integer recordId);

	public abstract String getWorkingComments();

	public abstract void setWorkingComments(String workingComments);

	public abstract nz.cri.gns.fred.model.Paleontology getPaleontology();

	public abstract void setPaleontology(
			nz.cri.gns.fred.model.Paleontology paleontology);

	public abstract nz.cri.gns.fred.model.Adoption getAdoption();

	public abstract void setAdoption(nz.cri.gns.fred.model.Adoption adoption);

	public abstract nz.cri.gns.fred.model.Sample getSample();

	public abstract void setSample(nz.cri.gns.fred.model.Sample sample);

	public abstract nz.cri.gns.fred.model.Folder getFolder();

	public abstract void setFolder(nz.cri.gns.fred.model.Folder folder);

    public abstract Set<RecordMeta> getRecordMetas();

	public abstract void setRecordMetas(Set<RecordMeta> recordMetas);

}