package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class Weathering implements Serializable, nz.cri.gns.fred.model.Weathering {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer weatheringId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** full constructor */
    public Weathering(Integer weatheringId, String name, String code) {
        this.weatheringId = weatheringId;
        this.name = name;
        this.code = code;
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

    @Override
	public String toString() {
        return name;
    }

	public int compareTo(nz.cri.gns.fred.model.Weathering arg0) {
		return this.code.compareTo((arg0.getCode()));
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.weatheringId);
	}

	public String getDisplayName() {
		return this.code + ": " + this.name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Weathering && ((Weathering)o).getWeatheringId().equals(weatheringId);
	}
	
	@Override
	public int hashCode() {
		return 752 * weatheringId;
	}
}
