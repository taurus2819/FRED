package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;
import nz.cri.gns.fred.model.Sample;

/** @author Hibernate CodeGenerator */
public class DrillType implements Serializable, nz.cri.gns.fred.model.DrillType {

	private static final long serialVersionUID = 20050818L;
	
    /** identifier field */
    private Integer drillTypeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private Set<Sample> samples;

    /** full constructor */
    public DrillType(Integer drillTypeId, String name, Set<Sample> samples) {
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

    public Set<Sample> getSamples() {
        return this.samples;
    }

    public void setSamples(Set<Sample> samples) {
        this.samples = samples;
    }

    @Override
	public String toString() {
        return name;
    }
    
	public int compareTo(nz.cri.gns.fred.model.DrillType arg0) {
		return this.name.compareTo((arg0.getName()));
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.drillTypeId);
	}

	public String getDisplayName() {
		return this.name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof DrillType && ((DrillType)o).drillTypeId.equals(drillTypeId);
	}
	
	@Override
	public int hashCode() {
		return 822 * drillTypeId;
	}
}
