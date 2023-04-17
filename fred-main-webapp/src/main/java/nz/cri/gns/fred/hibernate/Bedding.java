package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;
import nz.cri.gns.fred.model.Sample;

/** @author Hibernate CodeGenerator */
public class Bedding implements Serializable, nz.cri.gns.fred.model.Bedding {

	private static final long serialVersionUID = 20050818L;
	
   /** identifier field */
    private Integer beddingId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set<Sample> samplesByPrimaryBeddingId;

    /** persistent field */
    private Set<Sample> samplesBySecondaryBeddingId;

    /** full constructor */
    public Bedding(Integer beddingId, String name, String code, Set<Sample> samplesByPrimaryBeddingId, Set<Sample> samplesBySecondaryBeddingId) {
        this.beddingId = beddingId;
        this.name = name;
        this.code = code;
        this.samplesByPrimaryBeddingId = samplesByPrimaryBeddingId;
        this.samplesBySecondaryBeddingId = samplesBySecondaryBeddingId;
    }

    /** default constructor */
    public Bedding() {
    }

    public Integer getBeddingId() {
        return this.beddingId;
    }

    public void setBeddingId(Integer beddingId) {
        this.beddingId = beddingId;
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

    public Set<Sample> getSamplesByPrimaryBeddingId() {
        return this.samplesByPrimaryBeddingId;
    }

    public void setSamplesByPrimaryBeddingId(Set<Sample> samplesByPrimaryBeddingId) {
        this.samplesByPrimaryBeddingId = samplesByPrimaryBeddingId;
    }

    public Set<Sample> getSamplesBySecondaryBeddingId() {
        return this.samplesBySecondaryBeddingId;
    }

    public void setSamplesBySecondaryBeddingId(Set<Sample> samplesBySecondaryBeddingId) {
        this.samplesBySecondaryBeddingId = samplesBySecondaryBeddingId;
    }

    @Override
	public String toString() {
        return name;
    }

	public int compareTo(nz.cri.gns.fred.model.Bedding arg0) {
		return this.code.compareTo((arg0.getCode()));
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.beddingId);
	}

	public String getDisplayName() {
		return this.code + ": " + this.name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Bedding && ((Bedding)o).getBeddingId().equals(beddingId);
	}
	
	@Override
	public int hashCode() {
		return 184 * beddingId;
	}
}
