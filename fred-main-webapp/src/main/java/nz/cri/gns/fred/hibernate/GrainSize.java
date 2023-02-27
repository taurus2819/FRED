package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class GrainSize implements Serializable, nz.cri.gns.fred.model.GrainSize {

    private static final long serialVersionUID = 20050818L;
    
    /** identifier field */
    private Integer grainSizeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** full constructor */
    public GrainSize(Integer grainSizeId, String name, String code) {
        this.grainSizeId = grainSizeId;
        this.name = name;
        this.code = code;
    }

    /** default constructor */
    public GrainSize() {
    }

    public Integer getGrainSizeId() {
        return this.grainSizeId;
    }

    public void setGrainSizeId(Integer grainSizeId) {
        this.grainSizeId = grainSizeId;
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

	public int compareTo(nz.cri.gns.fred.model.GrainSize arg0) {
		return this.code.compareTo((arg0.getCode()));
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.grainSizeId);
	}

	public String getDisplayName() {
		return this.code + ": " + this.name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof GrainSize && ((GrainSize)o).getGrainSizeId().equals(grainSizeId);
        }
	
	@Override
	public int hashCode() {
		return 856 * grainSizeId;
	}
}
