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
			if (!taxon.getTaxonomicGroup().equals(arg0.getTaxon().getTaxonomicGroup()))
				return taxon.getTaxonomicGroup().compareTo(arg0.getTaxon().getTaxonomicGroup());
		try {
			return (taxonomicName.toUpperCase()).compareTo(arg0.getTaxonomicName().toUpperCase());
		} catch (Exception e) {}
		return 0;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ReferencedTaxonomicName))
			return false;
		ReferencedTaxonomicName refTaxaName = (ReferencedTaxonomicName) o; 
		return ((taxonomicName == null && refTaxaName.getTaxonomicName() == null) || taxonomicName.equals(refTaxaName.getTaxonomicName())) && taxon.equals(refTaxaName.getTaxon());
	}
	
}
