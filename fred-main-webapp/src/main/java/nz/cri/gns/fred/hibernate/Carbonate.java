package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class Carbonate implements Serializable, nz.cri.gns.fred.model.Carbonate {

	private static final long serialVersionUID = 20050818L;
	
    /** identifier field */
    private Integer carbonateId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** full constructor */
    public Carbonate(Integer carbonateId, String name, String code) {
        this.carbonateId = carbonateId;
        this.name = name;
        this.code = code;
    }

    /** default constructor */
    public Carbonate() {
    }

    public Integer getCarbonateId() {
        return this.carbonateId;
    }

    public void setCarbonateId(Integer carbonateId) {
        this.carbonateId = carbonateId;
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

	public int compareTo(nz.cri.gns.fred.model.Carbonate arg0) {
		return this.code.compareTo((arg0.getCode()));
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.carbonateId);
	}

	public String getDisplayName() {
		return this.code + ": " + this.name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Carbonate && ((Carbonate)o).getCarbonateId().equals(carbonateId);
	}
	
	@Override
	public int hashCode() {
		return 966 * carbonateId;
	}
}
