package nz.cri.gns.fred.hibernate;

import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Sample;
import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class FrNumber implements Serializable, nz.cri.gns.fred.model.FrNumber {

	private static final long serialVersionUID = 20050818L;
	
    /** identifier field */
    private Integer frId;

    /** persistent field */
    private String mapSheet;

    /** persistent field */
    private Integer serialNumber;

    /** nullable persistent field */
    private String recollectionNumber;

    /** nullable persistent field */
    private String frnumComments;

    /** nullable persistent field */
    private String frNumber;
    
    /** nullable persistent field */
    private String obsolete;
    
    /** persistent field */
    private Set<Feature> features;

    /** persistent field */
    private Set<Feature> featuresByYard;
    
    /** persistent field */
    private Set<Sample> samples;
    
    /** persistent field */
    private Set<Sample> samplesByYard;

    /** full constructor */
    public FrNumber(String mapSheet, Integer serialNumber, String recollectionNumber, String frnumComments, String frNumber, String obsolete, Set<Feature> features, Set<Feature> featuresByYard, Set<Sample> samples, Set<Sample> samplesByYard) {
        this.mapSheet = mapSheet;
        this.serialNumber = serialNumber;
        this.recollectionNumber = recollectionNumber;
        this.frnumComments = frnumComments;
        this.frNumber = frNumber;
        this.obsolete = obsolete;
        this.features = features;
        this.featuresByYard = featuresByYard;
        this.samples = samples;
        this.samplesByYard = samplesByYard;
    }

    /** default constructor */
    public FrNumber() {
    }

    /** minimal constructor */
    public FrNumber(String mapSheet, Integer serialNumber, Set<Feature> features, Set<Feature> featuresByYard, Set<Sample> samples, Set<Sample> samplesByYard) {
        this.mapSheet = mapSheet;
        this.serialNumber = serialNumber;
        this.features = features;
        this.featuresByYard = featuresByYard;
        this.samples = samples;
        this.samplesByYard = samplesByYard;
    }

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

	/*public boolean equals(Object o) {
		return o instanceof FrNumber && ((FrNumber)o).frId.equals(frId);
	}
	
	public int hashCode() {
		return 353 * frId;
	}*/
}
