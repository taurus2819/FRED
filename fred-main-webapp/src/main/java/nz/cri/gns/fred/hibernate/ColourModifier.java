package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class ColourModifier implements Serializable, nz.cri.gns.fred.model.ColourModifier {

	private static final long serialVersionUID = 20050818L;
	
   /** identifier field */
    private Integer modifierId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** full constructor */
    public ColourModifier(Integer modifierId, String name, String code) {
        this.modifierId = modifierId;
        this.name = name;
        this.code = code;
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

    @Override
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

	@Override
	public boolean equals(Object o) {
		return o instanceof ColourModifier && ((ColourModifier)o).getModifierId().equals(modifierId);
	}
	
	@Override
	public int hashCode() {
		return 745 * modifierId;
	}
}
