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
	public Set<MetaCat> getMetaCats();
    public void setMetaCats(Set<MetaCat> metaCats);
	public void setRecordStageViews(Set<RecordStageView> recordStageViews);
	public Set<RecordStageView> getRecordStageViews();
}