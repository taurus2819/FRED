package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.model.Taxon;

public class NpcPaleoName implements Serializable, nz.cri.gns.fred.model.NpcPaleoName {

    private static final long serialVersionUID = 20050818L;

    private Integer nameId;
    private String name;
    private Taxon taxon;

    public Integer getNameId() {
        return this.nameId;
    }

    public void setNameId(Integer nameId) {
        this.nameId = nameId;
    }

    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public Taxon getTaxon() {
		return taxon;
	}

    public String toString() {
        return name;
    }

	public int compareTo(nz.cri.gns.fred.model.NpcPaleoName arg0) {
		return name.compareTo(arg0.getName());
	}

}