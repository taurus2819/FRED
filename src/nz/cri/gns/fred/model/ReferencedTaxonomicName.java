package nz.cri.gns.fred.model;

public class ReferencedTaxonomicName implements Comparable<ReferencedTaxonomicName>{

	private String taxonomicName;
	private Taxon taxon;
	
	public ReferencedTaxonomicName() {
	}
	
	public ReferencedTaxonomicName(String taxonomicName, Taxon taxon) {
		this.setTaxonomicName(taxonomicName);
		this.setTaxon(taxon);
	}

	public void setTaxonomicName(String taxonomicName) {
		this.taxonomicName = taxonomicName;
	}

	public String getTaxonomicName() {
		return taxonomicName;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public int compareTo(ReferencedTaxonomicName arg0) {
		if (taxon != null && arg0.getTaxon() != null && !taxon.equals(arg0.getTaxon()))
			return taxon.compareTo(arg0.getTaxon());
		try {
			return (taxonomicName.toUpperCase()).compareTo(arg0.getTaxonomicName().toUpperCase());
		} catch (Exception e) {}
		return 0;
	}
	
}
