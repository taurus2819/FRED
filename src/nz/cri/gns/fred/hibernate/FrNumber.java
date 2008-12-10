package nz.cri.gns.fred.hibernate;

import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Sample;
import java.io.Serializable;
import java.util.Set;

public class FrNumber implements Serializable, nz.cri.gns.fred.model.FrNumber {

	private static final long serialVersionUID = 20050818L;
	
    private Integer frId;
    private String mapSheet;
    private Integer serialNumber;
    private String recollectionNumber;
    private String frnumComments;
    private String frNumber;
    private String obsolete;
    private Set<Feature> features;
    private Set<Feature> featuresByYard;
    private Set<Sample> samples;
    private Set<Sample> samplesByYard;

    public Integer getFrId() {
        return this.frId;
    }

    public void setFrId(Integer frId) {
        this.frId = frId;
    }

    public String getMapSheet() {
        return this.mapSheet;
    }

    public void setMapSheet(String mapSheet) {
        this.mapSheet = mapSheet;
    }

    public Integer getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRecollectionNumber() {
        return this.recollectionNumber;
    }

    public void setRecollectionNumber(String recollectionNumber) {
        this.recollectionNumber = recollectionNumber;
    }

    public String getFrnumComments() {
        return this.frnumComments;
    }

    public void setFrnumComments(String frnumComments) {
        this.frnumComments = frnumComments;
    }

    public String getFrNumber() {
        return this.frNumber;
    }

    public void setFrNumber(String frNumber) {
        this.frNumber = frNumber;
    }

    public String getObsolete() {
        return this.obsolete;
    }

    public void setObsolete(String obsolete) {
        this.obsolete = obsolete;
    }
    
    public Set<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    public Set<Feature> getFeaturesByYard() {
        return this.featuresByYard;
    }

    public void setFeaturesByYard(Set<Feature> featuresByYard) {
        this.featuresByYard = featuresByYard;
    }
    
    public Set<Sample> getSamples() {
        return this.samples;
    }

    public void setSamples(Set<Sample> samples) {
        this.samples = samples;
    }
    
    public Set<Sample> getSamplesByYard() {
        return this.samplesByYard;
    }

    public void setSamplesByYard(Set<Sample> samplesByYard) {
        this.samplesByYard = samplesByYard;
    }
    
	public int compareTo(nz.cri.gns.fred.model.FrNumber frNumber) {
		return this.frNumber.compareTo(frNumber.getFrNumber());
	}

	public String toString() {
		return frNumber;
	}
}