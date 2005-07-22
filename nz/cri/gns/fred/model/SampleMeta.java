package nz.cri.gns.fred.model;

/**
 *
 */
public interface SampleMeta {
	public abstract Sample getSample();
	public abstract void setSample(Sample sample);
	public Long getMetaId();
	public void setMetaId(Long id);
}