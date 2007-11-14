package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;
import nz.cri.gns.fred.model.Sample;

/** @author Hibernate CodeGenerator */
public class Hardness implements Serializable, nz.cri.gns.fred.model.Hardness {

	private static final long serialVersionUID = 20050818L;
	
    /** identifier field */
    private Integer hardnessId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set<Sample> samples;

    /** full constructor */
    public Hardness(Integer hardnessId, String name, String code, Set<Sample> samples) {
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

    public Set<Sample> getSamples() {
        return this.samples;
    }

    public void setSamples(Set<Sample> samples) {
        this.samples = samples;
    }
    public String toString() {
        return name;
    }

	public int compareTo(nz.cri.gns.fred.model.Hardness arg0) {
		return this.code.compareTo((arg0.getCode()));
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.hardnessId);
	}

	public String getDisplayName() {
		return this.code + ": " + this.name;
	}

	public boolean equals(Object o) {
		return o instanceof Hardness && ((Hardness)o).hardnessId.equals(hardnessId);
	}
	
	public int hashCode() {
		return 362 * hardnessId;
	}
}
