package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class BedThickness implements Serializable, nz.cri.gns.fred.model.BedThickness {

    /** identifier field */
    private Integer thicknessId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set samples;

    /** full constructor */
    public BedThickness(Integer thicknessId, String name, String code, Set samples) {
        this.thicknessId = thicknessId;
        this.name = name;
        this.code = code;
        this.samples = samples;
    }

    /** default constructor */
    public BedThickness() {
    }

    public Integer getThicknessId() {
        return this.thicknessId;
    }

    public void setThicknessId(Integer thicknessId) {
        this.thicknessId = thicknessId;
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
