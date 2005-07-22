package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class Weathering implements Serializable, nz.cri.gns.fred.model.Weathering {

    /** identifier field */
    private Integer weatheringId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set samples;

    /** full constructor */
    public Weathering(Integer weatheringId, String name, String code, Set samples) {
        this.weatheringId = weatheringId;
        this.name = name;
        this.code = code;
        this.samples = samples;
    }

    /** default constructor */
    public Weathering() {
    }

    public Integer getWeatheringId() {
        return this.weatheringId;
    }

    public void setWeatheringId(Integer weatheringId) {
        this.weatheringId = weatheringId;
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
