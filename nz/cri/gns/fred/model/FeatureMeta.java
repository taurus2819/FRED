package nz.cri.gns.fred.model;


/**
 *
 */
public interface FeatureMeta {
	public abstract Feature getFeature();
	public abstract void setFeature(Feature feature);
	public Long getMetaId();
	public void setMetaId(Long id);
}