package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;
import nz.cri.gns.fred.model.Sample;

/** @author Hibernate CodeGenerator */
public class ColourModifier implements Serializable, nz.cri.gns.fred.model.ColourModifier {

	private static final long serialVersionUID = 20050818L;
	
   /** identifier field */
    private Integer modifierId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set<Sample> samples;

    /** full constructor */
    public ColourModifier(Integer modifierId, String name, String code, Set<Sample> samples) {
        this.modifierId = modifierId;
        this.name = name;
        this.code = code;
        this.samples = samples;
    }

    /** default constructor */
    public ColourModifier() {
    }

    public Integer getModifierId() {
        return this.modifierId;
    }

    public void setModifierId(Integer modifierId) {
        this.modifierId = modifierId;
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
    
	public int compareTo(nz.cri.gns.fred.model.ColourModifier arg0) {
		return this.code.compareTo((arg0.getCode()));
	}

	public String getUniqueIdentifier() {
		return String.valueOf(this.modifierId);
	}

	public String getDisplayName() {
		return this.code + ": " + this.name;
	}

}
