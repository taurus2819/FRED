package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class ColourModifier implements Serializable {

    /** identifier field */
    private Integer modifierId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set samples;

    /** full constructor */
    public ColourModifier(Integer modifierId, String name, String code, Set samples) {
        this.modifierId = modifierId;
        this.name = name;
        this.code = code;
        this.samples = samples;
    }

    /** default constructor */
    public ColourModifier() {
    }

    public Integer getModifierId() {
        return this.modifierId;
    }

    public void setModifierId(Integer modifierId) {
        this.modifierId = modifierId;
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

    public Set getSamples() {
        return this.samples;
    }

    public void setSamples(Set samples) {
        this.samples = samples;
    }

    public String toString() {
        return name;
    }

}
