package nz.cri.gns.fred.model;

import java.util.Set;

public interface FrNumber extends Comparable<FrNumber> {
	public Integer getFrId();
	public void setFrId(Integer frId);
	public String getMapSheet();
	public void setMapSheet(String mapSheet);
	public Integer getSerialNumber();
	public void setSerialNumber(Integer serialNumber);
	public String getRecollectionNumber();
	public void setRecollectionNumber(String recollectionNumber);
	public String getFrnumComments();
	public void setFrnumComments(String frnumComments);
	public String getFrNumber();
	public void setFrNumber(String frNumber);
	public String getObsolete();
	public void setObsolete(String obsolete);
	public Set<Feature> getFeatures();
	public void setFeatures(Set<Feature> features);
    public Set<Feature> getFeaturesByYard();
    public void setFeaturesByYard(Set<Feature> featuresByYard);
	public Set<Sample> getSamples();
	public void setSamples(Set<Sample> samples);
    public Set<Sample> getSamplesByYard();
    public void setSamplesByYard(Set<Sample> samplesByYard);
}