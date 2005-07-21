package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class Person implements Serializable, nz.cri.gns.fred.model.Person {

    /** identifier field */
    private Integer personId;

    /** nullable persistent field */
    private String givenName;

    /** persistent field */
    private String familyName;

    /** nullable persistent field */
    private Integer stCode;

    /** persistent field */
    private Set adoptions;

    /** persistent field */
    private Set identifiedPaleontologies;

    /** persistent field */
    private Set features;

    /** persistent field */
    private Set sentTos;

    /** persistent field */
    private Set collectedSamples;

    /** full constructor */
    public Person(String givenName, String familyName, Integer stCode, Set adoptions, Set identifiedPaleontologies, Set features, Set sentTos, Set collectedSamples) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.stCode = stCode;
        this.adoptions = adoptions;
        this.identifiedPaleontologies = identifiedPaleontologies;
        this.features = features;
        this.sentTos = sentTos;
        this.collectedSamples = collectedSamples;
    }

    /** default constructor */
    public Person() {
    }

    /** minimal constructor */
    public Person(String familyName, Set adoptions, Set identifiedPaleontologies, Set features, Set sentTos, Set collectedSamples) {
        this.familyName = familyName;
        this.adoptions = adoptions;
        this.identifiedPaleontologies = identifiedPaleontologies;
        this.features = features;
        this.sentTos = sentTos;
        this.collectedSamples = collectedSamples;
    }

    public Integer getPersonId() {
        return this.personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getGivenName() {
        return this.givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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

    public Set getSentTos() {
        return this.sentTos;
    }

    public void setSentTos(Set sentTos) {
        this.sentTos = sentTos;
    }

    public Set getCollectedSamples() {
        return this.collectedSamples;
    }

    public void setCollectedSamples(Set collectedSamples) {
        this.collectedSamples = collectedSamples;
    }

}
