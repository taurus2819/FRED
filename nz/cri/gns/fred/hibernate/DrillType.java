package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class DrillType implements Serializable {

    /** identifier field */
    private Integer drillTypeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private Set samples;

    /** full constructor */
    public DrillType(Integer drillTypeId, String name, Set samples) {
        this.drillTypeId = drillTypeId;
        this.name = name;
        this.samples = samples;
    }

    /** default constructor */
    public DrillType() {
    }

    public Integer getDrillTypeId() {
        return this.drillTypeId;
    }

    public void setDrillTypeId(Integer drillTypeId) {
        this.drillTypeId = drillTypeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
