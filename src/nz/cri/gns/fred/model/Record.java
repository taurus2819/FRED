package nz.cri.gns.fred.model;

import java.util.Set;

public interface Record extends Audited, Comparable<Record> {
	public Integer getRecordId();
	public void setRecordId(Integer recordId);
	public Paleontology getPaleontology();
	public void setPaleontology(Paleontology paleontology);
	public Adoption getAdoption();
	public void setAdoption(Adoption adoption);
	public Sample getSample();
	public void setSample(Sample sample);
	public Audit getPalListAudit();
    public void setPalListAudit(Audit palListAudit);
    public Set<RecordMeta> getRecordMetas();
	public void setRecordMetas(Set<RecordMeta> recordMetas);
}