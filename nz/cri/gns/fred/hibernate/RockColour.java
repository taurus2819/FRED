package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class RockColour implements Serializable, nz.cri.gns.fred.model.RockColour {

    /** identifier field */
    private Integer colourId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set samplesBySecondaryColourId;

    /** persistent field */
    private Set samplesByPrimaryColourId;

    /** full constructor */
    public RockColour(Integer colourId, String name, String code, Set samplesBySecondaryColourId, Set samplesByPrimaryColourId) {
        this.colourId = colourId;
        this.name = name;
        this.code = code;
        this.samplesBySecondaryColourId = samplesBySecondaryColourId;
        this.samplesByPrimaryColourId = samplesByPrimaryColourId;
    }

    /** default constructor */
    public RockColour() {
    }

    public Integer getColourId() {
        return this.colourId;
    }

    public void setColourId(Integer colourId) {
        this.colourId = colourId;
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

    public Set getSamplesBySecondaryColourId() {
        return this.samplesBySecondaryColourId;
    }

    public void setSamplesBySecondaryColourId(Set samplesBySecondaryColourId) {
        this.samplesBySecondaryColourId = samplesBySecondaryColourId;
    }

    public Set getSamplesByPrimaryColourId() {
        return this.samplesByPrimaryColourId;
    }

    public void setSamplesByPrimaryColourId(Set samplesByPrimaryColourId) {
        this.samplesByPrimaryColourId = samplesByPrimaryColourId;
    }

    public String toString() {
        return name;
    }


}
