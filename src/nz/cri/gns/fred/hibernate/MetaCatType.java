package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.MetaCat;

public class MetaCatType implements Serializable, nz.cri.gns.fred.model.MetaCatType {
	
	private static final long serialVersionUID = 20060725L;

    private Integer typeId;
    private String name;
    private Set<MetaCat> metaCats;

    public Integer getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MetaCat> getMetaCats() {
        return this.metaCats;
    }

    public void setMetaCats(Set<MetaCat> metaCats) {
        this.metaCats = metaCats;
    }

    @Override
	public String toString() {
        return this.name;
    }

	public int compareTo(nz.cri.gns.fred.model.MetaCatType arg0) {
		return name.compareTo(arg0.getName());
	}

}