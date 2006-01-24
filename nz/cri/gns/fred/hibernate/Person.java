package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.SentTo;


/** @author Hibernate CodeGenerator */
public class Person implements Serializable, nz.cri.gns.fred.model.Person {

	private static final long serialVersionUID = 20050818L;

	/** identifier field */
    private Integer personId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private Integer stCode;

    /** persistent field */
    private Set adoptions;

    /** persistent field */
    private Set identifiedPaleontologies;

    /** persistent field */
    private Set features;

    /** persistent field */
    private Set collectedSamples;

    /** persistent field */
    private Set<SentTo> sentTos;
    
    /** full constructor */
    public Person(String name, Integer stCode, Set adoptions, Set identifiedPaleontologies, Set features, Set collectedSamples, Set<SentTo> sentTos) {
        this.name = name;
        this.stCode = stCode;
        this.adoptions = adoptions;
        this.identifiedPaleontologies = identifiedPaleontologies;
        this.features = features;
        this.collectedSamples = collectedSamples;
        this.sentTos = sentTos;
    }

    /** default constructor */
    public Person() {
    }

    /** minimal constructor */
    public Person(String name, Set adoptions, Set identifiedPaleontologies, Set features, Set collectedSamples) {
        this.name = name;
        this.adoptions = adoptions;
        this.identifiedPaleontologies = identifiedPaleontologies;
        this.features = features;
        this.collectedSamples = collectedSamples;
    }

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

    public Set getAdoptions() {
        return this.adoptions;
    }

    public void setAdoptions(Set adoptions) {
        this.adoptions = adoptions;
    }

    public Set getIdentifiedPaleontologies() {
        return this.identifiedPaleontologies;
    }

    public void setIdentifiedPaleontologies(Set identifiedPaleontologies) {
        this.identifiedPaleontologies = identifiedPaleontologies;
    }

    public Set getFeatures() {
        return this.features;
    }

    public void setFeatures(Set features) {
        this.features = features;
    }

    public Set getCollectedSamples() {
        return this.collectedSamples;
    }

    public void setCollectedSamples(Set collectedSamples) {
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

	public boolean equals(Object o) {
		return o instanceof Person && personId != null && personId.equals(((Person)o).getPersonId());
	}
    
    public int hashCode() {
        return name.hashCode();
    }
    
    public String toString() {
    	return super.toString() + " {" + personId + ": " + name + "}";
    }
}
