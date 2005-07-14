package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class Hardness implements Serializable {

    /** identifier field */
    private Integer hardnessId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set samples;

    /** full constructor */
    public Hardness(Integer hardnessId, String name, String code, Set samples) {
        this.hardnessId = hardnessId;
        this.name = name;
        this.code = code;
        this.samples = samples;
    }

    /** default constructor */
    public Hardness() {
    }

    public Integer getHardnessId() {
        return this.hardnessId;
    }

    public void setHardnessId(Integer hardnessId) {
        this.hardnessId = hardnessId;
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
