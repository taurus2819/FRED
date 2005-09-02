package nz.cri.gns.fred.model;

/**
 *
 */
public interface SampleMeta extends Meta {
	public abstract Sample getSample();
	public abstract void setSample(Sample sample);
}