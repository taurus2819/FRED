package nz.cri.gns.fred.model;

public interface SedimentaryFeature extends Comparable<SedimentaryFeature> {
	public String getAbundant();
	public void setAbundant(String abundant);
	public SedimentaryFeatureType getSedimentaryFeatureType();
	public void setSedimentaryFeatureType(SedimentaryFeatureType sedimentaryFeatureType);
}