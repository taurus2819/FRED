package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import nz.cri.gns.fred.model.Feature;


/** @author Hibernate CodeGenerator */
public class RegistrationArea implements Serializable, nz.cri.gns.fred.model.RegistrationArea {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer regAreaId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set<Feature> features;

    public Integer getRegAreaId() {
        return this.regAreaId;
    }

    public void setRegAreaId(Integer regAreaId) {
        this.regAreaId = regAreaId;
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

    public Set<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

	public int compareTo(nz.cri.gns.fred.model.RegistrationArea arg0) {
		return name.compareTo(arg0.getName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(regAreaId);
	}

	public String getDisplayName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof RegistrationArea && ((RegistrationArea)o).getRegAreaId().equals(regAreaId);
        }
	
	@Override
	public int hashCode() {
		return 542 * regAreaId;
	}
}
