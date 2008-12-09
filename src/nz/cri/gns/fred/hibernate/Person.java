package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SentTo;

public class Person implements Serializable, nz.cri.gns.fred.model.Person {

	private static final long serialVersionUID = 20050818L;

    private Integer personId;
    private String name;
    private Integer stCode;
    private Set<Adoption> adoptions;
    private Set<Paleontology> identifiedPaleontologies;
    private Set<Feature> features;
    private Set<Sample> collectedSamples;
    private Set<SentTo> sentTos;
    
    public Integer getPersonId() {
        return this.personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStCode() {
        return this.stCode;
    }

    public void setStCode(Integer stCode) {
        this.stCode = stCode;
    }

    public Set<Adoption> getAdoptions() {
        return this.adoptions;
    }

    public void setAdoptions(Set<Adoption> adoptions) {
        this.adoptions = adoptions;
    }

    public Set<Paleontology> getIdentifiedPaleontologies() {
        return this.identifiedPaleontologies;
    }

    public void setIdentifiedPaleontologies(Set<Paleontology> identifiedPaleontologies) {
        this.identifiedPaleontologies = identifiedPaleontologies;
    }

    public Set<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    public Set<Sample> getCollectedSamples() {
        return this.collectedSamples;
    }

    public void setCollectedSamples(Set<Sample> collectedSamples) {
        this.collectedSamples = collectedSamples;
    }

    public Set<SentTo> getSentTos() {
        return this.sentTos;
    }

    public void setSentTos(Set<SentTo> sentTos) {
        this.sentTos = sentTos;
    }
    
	public String getDisplayName() {
		return getName();
	}

    public String toString() {
    	return super.toString() + " {" + personId + ": " + name + "}";
    }
    
	public int compareTo(nz.cri.gns.fred.model.Person arg0) {
		return this.getName().compareTo(arg0.getName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(personId);
	}
	
}