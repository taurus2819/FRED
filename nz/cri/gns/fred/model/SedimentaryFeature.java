package nz.cri.gns.fred.model;


/**
 *
 */
public interface SedimentaryFeature {

	public abstract String getAbundant();

	public abstract void setAbundant(String abundant);

	public abstract nz.cri.gns.fred.model.SedimentaryFeatureType getSedimentaryFeatureType();

	public abstract void setSedimentaryFeatureType(
			nz.cri.gns.fred.model.SedimentaryFeatureType sedimentaryFeatureType);
}