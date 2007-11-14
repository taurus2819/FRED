package nz.cri.gns.fred.model;


/**
 *
 */
public interface FeatureMeta extends Meta {
	public abstract Feature getFeature();
	public abstract void setFeature(Feature feature);
}