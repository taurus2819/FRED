package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class Bedding implements Serializable {

    /** identifier field */
    private Integer beddingId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set samplesByPrimaryBeddingId;

    /** persistent field */
    private Set samplesBySecondaryBeddingId;

    /** full constructor */
    public Bedding(Integer beddingId, String name, String code, Set samplesByPrimaryBeddingId, Set samplesBySecondaryBeddingId) {
        this.beddingId = beddingId;
        this.name = name;
        this.code = code;
        this.samplesByPrimaryBeddingId = samplesByPrimaryBeddingId;
        this.samplesBySecondaryBeddingId = samplesBySecondaryBeddingId;
    }

    /** default constructor */
    public Bedding() {
    }

    public Integer getBeddingId() {
        return this.beddingId;
    }

    public void setBeddingId(Integer beddingId) {
        this.beddingId = beddingId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set getSamplesByPrimaryBeddingId() {
        return this.samplesByPrimaryBeddingId;
    }

    public void setSamplesByPrimaryBeddingId(Set samplesByPrimaryBeddingId) {
        this.samplesByPrimaryBeddingId = samplesByPrimaryBeddingId;
    }

    public Set getSamplesBySecondaryBeddingId() {
        return this.samplesBySecondaryBeddingId;
    }

    public void setSamplesBySecondaryBeddingId(Set samplesBySecondaryBeddingId) {
        this.samplesBySecondaryBeddingId = samplesBySecondaryBeddingId;
    }

    public String toString() {
        return name;
    }

}
