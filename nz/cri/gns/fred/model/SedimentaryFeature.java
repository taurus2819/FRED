package nz.cri.gns.fred.model;


/**
 *
 */
public interface SedimentaryFeature {

	public abstract String getAbundant();

	public abstract void setAbundant(String abundant);

	public abstract nz.cri.gns.fred.model.Sample getSample();

	public abstract void setSample(nz.cri.gns.fred.model.Sample sample);

	public abstract nz.cri.gns.fred.model.SedimentaryFeatureType getSedimentaryFeatureType();

	public abstract void setSedimentaryFeatureType(
			nz.cri.gns.fred.model.SedimentaryFeatureType sedimentaryFeatureType);
}