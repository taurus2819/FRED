package nz.cri.gns.fred.model;

import nz.cri.gns.fred.util.FREDUtil;

public class TaxonomicNameAndGroup implements Comparable<TaxonomicNameAndGroup>{

	private String taxonomicName;
	private TaxonomicGroup taxonomicGroup;
	
	public TaxonomicNameAndGroup() {
	}
	
	public TaxonomicNameAndGroup(String taxonomicName, TaxonomicGroup taxonomicGroup) {
		this.setTaxonomicName(taxonomicName);
		this.setTaxonomicGroup(taxonomicGroup);
	}

	public void setTaxonomicName(String taxonomicName) {
		this.taxonomicName = taxonomicName;
	}

	public String getTaxonomicName() {
		return taxonomicName;
	}

	public void setTaxonomicGroup(TaxonomicGroup group) {
		this.taxonomicGroup = group;
	}

	public TaxonomicGroup getTaxonomicGroup() {
		return taxonomicGroup;
	}

	public int compareTo(TaxonomicNameAndGroup arg0) {

		try {
                    if (!taxonomicGroup.equals(arg0.getTaxonomicGroup())) {
                            return taxonomicGroup.compareTo(arg0.getTaxonomicGroup());   
                    }
                    
                    return (taxonomicName.toUpperCase()).compareTo(arg0.getTaxonomicName().toUpperCase());
		} catch (Exception e) {}
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TaxonomicNameAndGroup))
			return false;
		TaxonomicNameAndGroup refTaxaName = (TaxonomicNameAndGroup) o; 
		return FREDUtil.equals(taxonomicName, refTaxaName.getTaxonomicName(), true)
			&& taxonomicGroup.equals(refTaxaName.getTaxonomicGroup());
	}
	
}
